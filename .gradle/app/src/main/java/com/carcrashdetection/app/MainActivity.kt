package com.carcrashdetection.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.carcrashdetection.app.activities.PublisherActivity
import com.carcrashdetection.app.activities.SubscriberActivity
import com.carcrashdetection.app.databinding.ActivityMainBinding
import com.carcrashdetection.app.services.MQTTService
import com.carcrashdetection.app.services.LocationService

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        startServices()
    }
    
    private fun setupUI() {
        // Apply animations
        val fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.titleText.startAnimation(fadeInAnimation)
        
        val slideUpAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        binding.publisherCard.startAnimation(slideUpAnimation)
        
        val slideUpAnimation2 = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        slideUpAnimation2.startOffset = 200
        binding.subscriberCard.startAnimation(slideUpAnimation2)
        
        // Set click listeners
        binding.publisherCard.setOnClickListener {
            val intent = Intent(this, PublisherActivity::class.java)
            startActivity(intent)
        }
        
        binding.subscriberCard.setOnClickListener {
            val intent = Intent(this, SubscriberActivity::class.java)
            startActivity(intent)
        }
        
        // Add ripple effects
        binding.publisherCard.isClickable = true
        binding.subscriberCard.isClickable = true
    }
    
    private fun startServices() {
        // Start MQTT Service
        val mqttIntent = Intent(this, MQTTService::class.java)
        startService(mqttIntent)
        
        // Start Location Service
        val locationIntent = Intent(this, LocationService::class.java)
        startService(locationIntent)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Note: Services will continue running in background
        // They should be stopped when the app is completely closed
    }
} 