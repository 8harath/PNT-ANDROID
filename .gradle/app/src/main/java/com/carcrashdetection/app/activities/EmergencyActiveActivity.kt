package com.carcrashdetection.app.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.carcrashdetection.app.R
import com.carcrashdetection.app.databinding.ActivityEmergencyActiveBinding

class EmergencyActiveActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEmergencyActiveBinding
    private var countDownTimer: CountDownTimer? = null
    private var incidentId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyActiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        incidentId = intent.getStringExtra("incident_id")
        
        setupUI()
        startEmergencySequence()
    }
    
    private fun setupUI() {
        // Hide system UI for full-screen experience
        window.decorView.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
            android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
        
        // Setup emergency animations
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.emergency_pulse)
        binding.emergencyIcon.startAnimation(pulseAnimation)
        
        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.emergency_shake)
        binding.emergencyText.startAnimation(shakeAnimation)
        
        // Setup cancel button
        binding.cancelButton.setOnClickListener {
            finish()
        }
        
        // Setup location sharing toggle
        binding.locationSharingToggle.setOnCheckedChangeListener { _, isChecked ->
            updateLocationSharingStatus(isChecked)
        }
    }
    
    private fun startEmergencySequence() {
        // Start countdown timer (30 seconds)
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.countdownText.text = "$secondsRemaining"
                
                // Change color based on time remaining
                if (secondsRemaining <= 10) {
                    binding.countdownText.setTextColor(getColor(R.color.error_red))
                } else if (secondsRemaining <= 20) {
                    binding.countdownText.setTextColor(getColor(R.color.warning_amber))
                } else {
                    binding.countdownText.setTextColor(getColor(R.color.primary_blue))
                }
            }
            
            override fun onFinish() {
                binding.countdownText.text = "0"
                binding.countdownText.setTextColor(getColor(R.color.error_red))
                
                // Auto-finish after countdown
                finish()
            }
        }.start()
        
        // Show emergency message
        binding.emergencyMessage.text = "Emergency alert sent! Help is on the way."
        
        // Start location broadcasting indicator
        startLocationBroadcasting()
    }
    
    private fun updateLocationSharingStatus(isEnabled: Boolean) {
        if (isEnabled) {
            binding.locationStatus.text = "Location Sharing: ON"
            binding.locationStatus.setTextColor(getColor(R.color.success_green))
            startLocationBroadcasting()
        } else {
            binding.locationStatus.text = "Location Sharing: OFF"
            binding.locationStatus.setTextColor(getColor(R.color.error_red))
            stopLocationBroadcasting()
        }
    }
    
    private fun startLocationBroadcasting() {
        // Animate location indicator
        val locationAnimation = AnimationUtils.loadAnimation(this, R.anim.location_pulse)
        binding.locationIndicator.startAnimation(locationAnimation)
        
        binding.locationStatus.text = "Location Sharing: ON"
        binding.locationStatus.setTextColor(getColor(R.color.success_green))
    }
    
    private fun stopLocationBroadcasting() {
        binding.locationIndicator.clearAnimation()
        binding.locationStatus.text = "Location Sharing: OFF"
        binding.locationStatus.setTextColor(getColor(R.color.error_red))
    }
    
    override fun onBackPressed() {
        // Prevent back button from closing emergency screen
        // User must use cancel button
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        
        // Re-enable system UI
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_VISIBLE
    }
} 