package com.carcrashdetection.app.models

import com.google.gson.annotations.SerializedName

data class VictimInfo(
    @SerializedName("bloodGroup")
    val bloodGroup: String,
    
    @SerializedName("age")
    val age: Int,
    
    @SerializedName("allergies")
    val allergies: List<String> = emptyList(),
    
    @SerializedName("medicalConditions")
    val medicalConditions: List<String> = emptyList(),
    
    @SerializedName("emergencyContact")
    val emergencyContact: String,
    
    @SerializedName("emergencyContactPhone")
    val emergencyContactPhone: String,
    
    @SerializedName("name")
    val name: String = "Unknown",
    
    @SerializedName("gender")
    val gender: String = "Unknown"
) 