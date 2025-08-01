package com.carcrashdetection.app.activities

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.carcrashdetection.app.R
import com.carcrashdetection.app.databinding.ActivityPublisherBinding
import com.carcrashdetection.app.models.CrashIncident
import com.carcrashdetection.app.models.VictimInfo
import com.carcrashdetection.app.services.LocationService
import com.carcrashdetection.app.services.MQTTService
import com.carcrashdetection.app.services.SimulationService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*

class PublisherActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPublisherBinding
    private var mqttService: MQTTService? = null
    private var locationService: LocationService? = null
    private val simulationService = SimulationService()
    
    private var isEmergencyActive = false
    private var currentVictimInfo: VictimInfo? = null
    
    private val mqttConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MQTTService.MQTTBinder
            mqttService = binder.getService()
            updateConnectionStatus()
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            mqttService = null
            updateConnectionStatus()
        }
    }
    
    private val locationConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocationBinder
            locationService = binder.getService()
            locationService?.startLocationUpdates()
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublisherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        bindServices()
        checkPermissions()
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Emergency Publisher"
        
        // Emergency button animation
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation)
        binding.emergencyButton.startAnimation(pulseAnimation)
        
        // Set click listeners
        binding.emergencyButton.setOnClickListener {
            if (!isEmergencyActive) {
                triggerEmergency()
            }
        }
        
        binding.profileButton.setOnClickListener {
            showProfileSetupDialog()
        }
        
        binding.simulationButton.setOnClickListener {
            showSimulationDialog()
        }
        
        binding.locationToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                locationService?.startLocationUpdates()
            } else {
                locationService?.stopLocationUpdates()
            }
        }
    }
    
    private fun bindServices() {
        bindService(Intent(this, MQTTService::class.java), mqttConnection, Context.BIND_AUTO_CREATE)
        bindService(Intent(this, LocationService::class.java), locationConnection, Context.BIND_AUTO_CREATE)
    }
    
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSION_REQUEST_CODE)
        }
    }
    
    private fun updateConnectionStatus() {
        val isConnected = mqttService?.isConnectedToBroker() ?: false
        binding.connectionStatus.text = if (isConnected) "Connected" else "Disconnected"
        binding.connectionStatus.setTextColor(
            ContextCompat.getColor(this, if (isConnected) R.color.success_green else R.color.error_red)
        )
    }
    
    private fun triggerEmergency() {
        if (currentVictimInfo == null) {
            showProfileSetupDialog()
            return
        }
        
        val coordinates = locationService?.getCurrentCoordinates()
        if (coordinates == null) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            return
        }
        
        val crashIncident = CrashIncident(
            latitude = coordinates.first,
            longitude = coordinates.second,
            victimInfo = currentVictimInfo!!,
            vehicleType = "Car" // Default vehicle type
        )
        
        mqttService?.publishCrashAlert(crashIncident)
        
        // Start emergency active activity
        val intent = Intent(this, EmergencyActiveActivity::class.java)
        intent.putExtra("incident_id", crashIncident.incidentId)
        startActivity(intent)
        
        isEmergencyActive = true
    }
    
    private fun showProfileSetupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_profile_setup, null)
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Medical Profile Setup")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Save profile logic would go here
                currentVictimInfo = VictimInfo(
                    bloodGroup = "A+",
                    age = 30,
                    allergies = listOf("None"),
                    medicalConditions = listOf("None"),
                    emergencyContact = "Emergency Contact",
                    emergencyContactPhone = "(555) 123-4567"
                )
                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showSimulationDialog() {
        val vehicleTypes = simulationService.getAvailableVehicleTypes()
        val items = vehicleTypes.toTypedArray()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Simulate Crash")
            .setItems(items) { _, which ->
                val selectedVehicle = vehicleTypes[which]
                simulateCrash(selectedVehicle)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun simulateCrash(vehicleType: String) {
        val coordinates = locationService?.getCurrentCoordinates()
        val location = if (coordinates != null) {
            coordinates
        } else {
            simulationService.generateRandomLocation()
        }
        
        val crashIncident = simulationService.generateCrashIncidentWithCustomLocation(
            latitude = location.first,
            longitude = location.second,
            vehicleType = vehicleType
        )
        
        mqttService?.publishCrashAlert(crashIncident)
        Toast.makeText(this, "Simulated crash sent: ${crashIncident.incidentId}", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unbindService(mqttConnection)
        unbindService(locationConnection)
    }
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
} 