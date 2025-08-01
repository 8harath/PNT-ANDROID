package com.carcrashdetection.app.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carcrashdetection.app.R
import com.carcrashdetection.app.databinding.ActivitySubscriberBinding
import com.carcrashdetection.app.models.CrashIncident
import com.carcrashdetection.app.services.MQTTService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubscriberActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var binding: ActivitySubscriberBinding
    private var mqttService: MQTTService? = null
    private var googleMap: GoogleMap? = null
    
    private val incidents = MutableStateFlow<List<CrashIncident>>(emptyList())
    private val markers = mutableMapOf<String, Marker>()
    
    private val mqttConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MQTTService.MQTTBinder
            mqttService = binder.getService()
            setupMQTTCallbacks()
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            mqttService = null
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupMap()
        bindServices()
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Emergency Response"
        
        // Setup bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_map -> {
                    binding.mapContainer.visibility = android.view.View.VISIBLE
                    binding.incidentListContainer.visibility = android.view.View.GONE
                    true
                }
                R.id.nav_list -> {
                    binding.mapContainer.visibility = android.view.View.GONE
                    binding.incidentListContainer.visibility = android.view.View.VISIBLE
                    true
                }
                else -> false
            }
        }
        
        // Setup incident list
        setupIncidentList()
    }
    
    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    private fun setupIncidentList() {
        lifecycleScope.launch {
            incidents.collect { incidentList ->
                updateIncidentList(incidentList)
            }
        }
    }
    
    private fun bindServices() {
        bindService(Intent(this, MQTTService::class.java), mqttConnection, Context.BIND_AUTO_CREATE)
    }
    
    private fun setupMQTTCallbacks() {
        mqttService?.setCrashAlertCallback { crashIncident ->
            handleNewCrashIncident(crashIncident)
        }
        
        mqttService?.setConnectionStatusCallback { isConnected ->
            updateConnectionStatus(isConnected)
        }
    }
    
    private fun handleNewCrashIncident(crashIncident: CrashIncident) {
        lifecycleScope.launch {
            val currentList = incidents.value.toMutableList()
            currentList.add(0, crashIncident) // Add to beginning
            incidents.value = currentList
            
            // Add marker to map
            addMarkerToMap(crashIncident)
            
            // Show notification
            showIncidentNotification(crashIncident)
        }
    }
    
    private fun addMarkerToMap(crashIncident: CrashIncident) {
        googleMap?.let { map ->
            val position = LatLng(crashIncident.latitude, crashIncident.longitude)
            
            val markerOptions = MarkerOptions()
                .position(position)
                .title("Crash Incident")
                .snippet("${crashIncident.victimInfo.name} - ${crashIncident.vehicleType}")
                .icon(BitmapDescriptorFactory.defaultMarker(
                    if (crashIncident.severity == "HIGH") BitmapDescriptorFactory.HUE_RED
                    else BitmapDescriptorFactory.HUE_ORANGE
                ))
            
            val marker = map.addMarker(markerOptions)
            marker?.let { markers[crashIncident.incidentId] = it }
            
            // Animate camera to new incident if it's the first one
            if (incidents.value.size == 1) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
            }
        }
    }
    
    private fun updateIncidentList(incidentList: List<CrashIncident>) {
        // This would update the RecyclerView adapter
        // For now, just update the count
        binding.incidentCount.text = "${incidentList.size} Active Incidents"
    }
    
    private fun updateConnectionStatus(isConnected: Boolean) {
        binding.connectionStatus.text = if (isConnected) "Connected" else "Disconnected"
        binding.connectionStatus.setTextColor(
            getColor(if (isConnected) R.color.success_green else R.color.error_red)
        )
    }
    
    private fun showIncidentNotification(crashIncident: CrashIncident) {
        // Create a custom notification view
        val notificationView = layoutInflater.inflate(R.layout.layout_incident_notification, null)
        
        // Set incident details
        notificationView.findViewById<android.widget.TextView>(R.id.incidentTitle).text = 
            "New Crash Alert"
        notificationView.findViewById<android.widget.TextView>(R.id.incidentDetails).text = 
            "${crashIncident.victimInfo.name} - ${crashIncident.vehicleType}"
        
        // Show as a snackbar or custom overlay
        binding.notificationContainer.addView(notificationView)
        
        // Animate in
        val slideInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        notificationView.startAnimation(slideInAnimation)
        
        // Auto-remove after 5 seconds
        notificationView.postDelayed({
            val slideOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
            notificationView.startAnimation(slideOutAnimation)
            notificationView.postDelayed({
                binding.notificationContainer.removeView(notificationView)
            }, 300)
        }, 5000)
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Set map properties
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
        
        // Set default location (New York City)
        val defaultLocation = LatLng(40.7128, -74.0060)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        
        // Set marker click listener
        map.setOnMarkerClickListener { marker ->
            val incidentId = markers.entries.find { it.value == marker }?.key
            incidentId?.let { id ->
                val incident = incidents.value.find { it.incidentId == id }
                incident?.let { showIncidentDetails(it) }
            }
            true
        }
    }
    
    private fun showIncidentDetails(crashIncident: CrashIncident) {
        // Create and show incident details dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_incident_details, null)
        
        // Populate dialog with incident details
        dialogView.findViewById<android.widget.TextView>(R.id.victimName).text = crashIncident.victimInfo.name
        dialogView.findViewById<android.widget.TextView>(R.id.vehicleType).text = crashIncident.vehicleType
        dialogView.findViewById<android.widget.TextView>(R.id.bloodGroup).text = crashIncident.victimInfo.bloodGroup
        dialogView.findViewById<android.widget.TextView>(R.id.age).text = crashIncident.victimInfo.age.toString()
        dialogView.findViewById<android.widget.TextView>(R.id.timestamp).text = crashIncident.timestamp
        
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Incident Details")
            .setView(dialogView)
            .setPositiveButton("Navigate") { _, _ ->
                navigateToIncident(crashIncident)
            }
            .setNegativeButton("Close", null)
            .show()
    }
    
    private fun navigateToIncident(crashIncident: CrashIncident) {
        val uri = "google.navigation:q=${crashIncident.latitude},${crashIncident.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Fallback to web browser
            val webUri = "https://www.google.com/maps/dir/?api=1&destination=${crashIncident.latitude},${crashIncident.longitude}"
            val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(webUri))
            startActivity(webIntent)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unbindService(mqttConnection)
    }
} 