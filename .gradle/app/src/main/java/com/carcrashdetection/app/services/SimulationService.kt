package com.carcrashdetection.app.services

import com.carcrashdetection.app.models.CrashIncident
import com.carcrashdetection.app.models.VictimInfo
import kotlin.random.Random

class SimulationService {
    
    companion object {
        private val BLOOD_GROUPS = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        private val VEHICLE_TYPES = listOf("Car", "Motorcycle", "Truck", "Bus", "Van", "SUV", "Sedan")
        private val ALLERGIES = listOf(
            "Peanuts", "Shellfish", "Penicillin", "Latex", "Dairy", "Eggs", "Soy", "Wheat",
            "Tree Nuts", "Fish", "Sulfites", "Aspirin", "Ibuprofen"
        )
        private val MEDICAL_CONDITIONS = listOf(
            "Diabetes", "Hypertension", "Asthma", "Heart Disease", "Epilepsy", "Anemia",
            "Arthritis", "Depression", "Anxiety", "None"
        )
        
        // Sample city coordinates (New York City area)
        private const val BASE_LAT = 40.7128
        private const val BASE_LNG = -74.0060
        private const val LAT_RANGE = 0.1 // ~11km radius
        private const val LNG_RANGE = 0.1
    }
    
    fun generateRandomCrashIncident(): CrashIncident {
        val (lat, lng) = generateRandomLocation()
        val victimInfo = generateRandomVictimInfo()
        val vehicleType = VEHICLE_TYPES.random()
        
        return CrashIncident(
            latitude = lat,
            longitude = lng,
            victimInfo = victimInfo,
            vehicleType = vehicleType,
            severity = if (Random.nextBoolean()) "HIGH" else "MEDIUM"
        )
    }
    
    fun generateRandomLocation(): Pair<Double, Double> {
        val lat = BASE_LAT + (Random.nextDouble() - 0.5) * LAT_RANGE
        val lng = BASE_LNG + (Random.nextDouble() - 0.5) * LNG_RANGE
        return Pair(lat, lng)
    }
    
    fun generateRandomVictimInfo(): VictimInfo {
        val age = Random.nextInt(18, 81)
        val bloodGroup = BLOOD_GROUPS.random()
        val allergies = generateRandomAllergies()
        val medicalConditions = generateRandomMedicalConditions()
        
        return VictimInfo(
            bloodGroup = bloodGroup,
            age = age,
            allergies = allergies,
            medicalConditions = medicalConditions,
            emergencyContact = generateRandomName(),
            emergencyContactPhone = generateRandomPhone(),
            name = generateRandomName(),
            gender = if (Random.nextBoolean()) "Male" else "Female"
        )
    }
    
    private fun generateRandomAllergies(): List<String> {
        val numAllergies = Random.nextInt(0, 4) // 0-3 allergies
        return ALLERGIES.shuffled().take(numAllergies)
    }
    
    private fun generateRandomMedicalConditions(): List<String> {
        val numConditions = Random.nextInt(0, 3) // 0-2 conditions
        val conditions = MEDICAL_CONDITIONS.filter { it != "None" }.shuffled().take(numConditions)
        return if (conditions.isEmpty()) listOf("None") else conditions
    }
    
    private fun generateRandomName(): String {
        val firstNames = listOf(
            "John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Jessica",
            "William", "Ashley", "Richard", "Amanda", "Joseph", "Stephanie", "Thomas", "Nicole",
            "Christopher", "Elizabeth", "Charles", "Helen", "Daniel", "Deborah", "Matthew", "Rachel",
            "Anthony", "Carolyn", "Mark", "Janet", "Donald", "Catherine", "Steven", "Maria",
            "Paul", "Heather", "Andrew", "Diane", "Joshua", "Ruth", "Kenneth", "Julie"
        )
        val lastNames = listOf(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
            "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
            "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson",
            "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker",
            "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores"
        )
        
        return "${firstNames.random()} ${lastNames.random()}"
    }
    
    private fun generateRandomPhone(): String {
        val areaCode = Random.nextInt(200, 999)
        val prefix = Random.nextInt(200, 999)
        val lineNumber = Random.nextInt(1000, 9999)
        return "($areaCode) $prefix-$lineNumber"
    }
    
    fun generateCrashIncidentWithCustomLocation(
        latitude: Double,
        longitude: Double,
        vehicleType: String? = null
    ): CrashIncident {
        val victimInfo = generateRandomVictimInfo()
        val selectedVehicleType = vehicleType ?: VEHICLE_TYPES.random()
        
        return CrashIncident(
            latitude = latitude,
            longitude = longitude,
            victimInfo = victimInfo,
            vehicleType = selectedVehicleType,
            severity = if (Random.nextBoolean()) "HIGH" else "MEDIUM"
        )
    }
    
    fun getAvailableVehicleTypes(): List<String> = VEHICLE_TYPES
    
    fun getAvailableBloodGroups(): List<String> = BLOOD_GROUPS
    
    fun getAvailableAllergies(): List<String> = ALLERGIES
    
    fun getAvailableMedicalConditions(): List<String> = MEDICAL_CONDITIONS.filter { it != "None" }
} 