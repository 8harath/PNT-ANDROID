package com.carcrashdetection.app.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.carcrashdetection.app.models.CrashIncident
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.*

class MQTTService : Service() {
    
    companion object {
        private const val TAG = "MQTTService"
        private const val BROKER_URL = "tcp://localhost:1883"
        private const val CLIENT_ID = "AndroidClient_${UUID.randomUUID()}"
        private const val CRASH_ALERTS_TOPIC = "crash/alerts/+"
        private const val CRASH_RESPONSES_TOPIC = "crash/responses/+"
        private const val QOS = 1
    }
    
    private val binder = MQTTBinder()
    private var mqttClient: MqttClient? = null
    private var isConnected = false
    private val gson = Gson()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Callbacks
    private var crashAlertCallback: ((CrashIncident) -> Unit)? = null
    private var connectionStatusCallback: ((Boolean) -> Unit)? = null
    
    inner class MQTTBinder : Binder() {
        fun getService(): MQTTService = this@MQTTService
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onCreate() {
        super.onCreate()
        connectToBroker()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        disconnectFromBroker()
        serviceScope.cancel()
    }
    
    fun setCrashAlertCallback(callback: (CrashIncident) -> Unit) {
        crashAlertCallback = callback
    }
    
    fun setConnectionStatusCallback(callback: (Boolean) -> Unit) {
        connectionStatusCallback = callback
        callback(isConnected)
    }
    
    private fun connectToBroker() {
        serviceScope.launch {
            try {
                mqttClient = MqttClient(BROKER_URL, CLIENT_ID, MemoryPersistence())
                
                val options = MqttConnectOptions().apply {
                    isCleanSession = true
                    connectionTimeout = 30
                    keepAliveInterval = 60
                    isAutomaticReconnect = true
                }
                
                mqttClient?.connect(options)
                isConnected = true
                
                withContext(Dispatchers.Main) {
                    connectionStatusCallback?.invoke(true)
                }
                
                Log.d(TAG, "Connected to MQTT broker")
                
                // Subscribe to crash alerts
                subscribeToCrashAlerts()
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to connect to MQTT broker", e)
                isConnected = false
                withContext(Dispatchers.Main) {
                    connectionStatusCallback?.invoke(false)
                }
                
                // Retry connection after delay
                delay(5000)
                connectToBroker()
            }
        }
    }
    
    private fun disconnectFromBroker() {
        serviceScope.launch {
            try {
                mqttClient?.disconnect()
                mqttClient?.close()
                isConnected = false
                withContext(Dispatchers.Main) {
                    connectionStatusCallback?.invoke(false)
                }
                Log.d(TAG, "Disconnected from MQTT broker")
            } catch (e: Exception) {
                Log.e(TAG, "Error disconnecting from MQTT broker", e)
            }
        }
    }
    
    private fun subscribeToCrashAlerts() {
        try {
            mqttClient?.subscribe(CRASH_ALERTS_TOPIC, QOS) { _, message ->
                handleCrashAlert(message)
            }
            Log.d(TAG, "Subscribed to crash alerts topic")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to subscribe to crash alerts", e)
        }
    }
    
    private fun handleCrashAlert(message: MqttMessage) {
        try {
            val payload = String(message.payload)
            val crashIncident = gson.fromJson(payload, CrashIncident::class.java)
            
            Log.d(TAG, "Received crash alert: ${crashIncident.incidentId}")
            
            serviceScope.launch(Dispatchers.Main) {
                crashAlertCallback?.invoke(crashIncident)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing crash alert message", e)
        }
    }
    
    fun publishCrashAlert(crashIncident: CrashIncident, region: String = "DEFAULT") {
        serviceScope.launch {
            try {
                if (!isConnected) {
                    Log.w(TAG, "Not connected to MQTT broker")
                    return@launch
                }
                
                val topic = "crash/alerts/$region"
                val payload = gson.toJson(crashIncident)
                val message = MqttMessage(payload.toByteArray())
                message.qos = QOS
                
                mqttClient?.publish(topic, message)
                Log.d(TAG, "Published crash alert to topic: $topic")
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to publish crash alert", e)
            }
        }
    }
    
    fun publishResponse(incidentId: String, response: String) {
        serviceScope.launch {
            try {
                if (!isConnected) {
                    Log.w(TAG, "Not connected to MQTT broker")
                    return@launch
                }
                
                val topic = "crash/responses/$incidentId"
                val responseData = mapOf(
                    "incidentId" to incidentId,
                    "response" to response,
                    "timestamp" to java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                )
                val payload = gson.toJson(responseData)
                val message = MqttMessage(payload.toByteArray())
                message.qos = QOS
                
                mqttClient?.publish(topic, message)
                Log.d(TAG, "Published response to topic: $topic")
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to publish response", e)
            }
        }
    }
    
    fun isConnectedToBroker(): Boolean = isConnected
} 