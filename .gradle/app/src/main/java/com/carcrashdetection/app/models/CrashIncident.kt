package com.carcrashdetection.app.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class CrashIncident(
    @SerializedName("incidentId")
    val incidentId: String = UUID.randomUUID().toString(),
    
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("timestamp")
    val timestamp: String = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    
    @SerializedName("victimInfo")
    val victimInfo: VictimInfo,
    
    @SerializedName("vehicleType")
    val vehicleType: String,
    
    @SerializedName("severity")
    val severity: String = "HIGH",
    
    @SerializedName("region")
    val region: String = "DEFAULT",
    
    @SerializedName("status")
    val status: String = "ACTIVE"
) 