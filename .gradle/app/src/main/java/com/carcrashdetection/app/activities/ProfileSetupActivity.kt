package com.carcrashdetection.app.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.carcrashdetection.app.R
import com.carcrashdetection.app.databinding.ActivityProfileSetupBinding
import com.carcrashdetection.app.models.VictimInfo
import com.carcrashdetection.app.services.SimulationService
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class ProfileSetupActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileSetupBinding
    private val simulationService = SimulationService()
    private val selectedAllergies = mutableSetOf<String>()
    private val selectedMedicalConditions = mutableSetOf<String>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupFormValidation()
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Medical Profile"
        
        // Setup blood group spinner
        val bloodGroups = simulationService.getAvailableBloodGroups()
        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bloodGroupSpinner.adapter = bloodGroupAdapter
        
        // Setup allergies chip group
        setupAllergiesChipGroup()
        
        // Setup medical conditions chip group
        setupMedicalConditionsChipGroup()
        
        // Setup save button
        binding.saveButton.setOnClickListener {
            if (validateForm()) {
                saveProfile()
            }
        }
        
        // Setup clear button
        binding.clearButton.setOnClickListener {
            clearForm()
        }
    }
    
    private fun setupAllergiesChipGroup() {
        val allergies = simulationService.getAvailableAllergies()
        
        binding.addAllergyButton.setOnClickListener {
            val allergyInput = binding.allergyInput.text.toString().trim()
            if (allergyInput.isNotEmpty() && !selectedAllergies.contains(allergyInput)) {
                addAllergyChip(allergyInput)
                binding.allergyInput.text?.clear()
            }
        }
        
        // Add predefined allergies
        allergies.forEach { allergy ->
            addAllergyChip(allergy)
        }
    }
    
    private fun setupMedicalConditionsChipGroup() {
        val conditions = simulationService.getAvailableMedicalConditions()
        
        binding.addConditionButton.setOnClickListener {
            val conditionInput = binding.conditionInput.text.toString().trim()
            if (conditionInput.isNotEmpty() && !selectedMedicalConditions.contains(conditionInput)) {
                addMedicalConditionChip(conditionInput)
                binding.conditionInput.text?.clear()
            }
        }
        
        // Add predefined conditions
        conditions.forEach { condition ->
            addMedicalConditionChip(condition)
        }
    }
    
    private fun addAllergyChip(allergy: String) {
        val chip = Chip(this).apply {
            text = allergy
            isCheckable = true
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedAllergies.add(allergy)
                } else {
                    selectedAllergies.remove(allergy)
                }
            }
        }
        binding.allergiesChipGroup.addView(chip)
        selectedAllergies.add(allergy)
    }
    
    private fun addMedicalConditionChip(condition: String) {
        val chip = Chip(this).apply {
            text = condition
            isCheckable = true
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedMedicalConditions.add(condition)
                } else {
                    selectedMedicalConditions.remove(condition)
                }
            }
        }
        binding.medicalConditionsChipGroup.addView(chip)
        selectedMedicalConditions.add(condition)
    }
    
    private fun setupFormValidation() {
        // Real-time validation
        binding.nameInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                validateNameField()
            }
        })
        
        binding.ageInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                validateAgeField()
            }
        })
        
        binding.emergencyContactInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                validateEmergencyContactField()
            }
        })
    }
    
    private fun validateNameField(): Boolean {
        val name = binding.nameInput.text.toString().trim()
        return if (name.length >= 2) {
            binding.nameInputLayout.error = null
            true
        } else {
            binding.nameInputLayout.error = "Name must be at least 2 characters"
            false
        }
    }
    
    private fun validateAgeField(): Boolean {
        val ageText = binding.ageInput.text.toString().trim()
        return if (ageText.isEmpty()) {
            binding.ageInputLayout.error = null
            true
        } else {
            val age = ageText.toIntOrNull()
            if (age != null && age in 1..120) {
                binding.ageInputLayout.error = null
                true
            } else {
                binding.ageInputLayout.error = "Age must be between 1 and 120"
                false
            }
        }
    }
    
    private fun validateEmergencyContactField(): Boolean {
        val contact = binding.emergencyContactInput.text.toString().trim()
        return if (contact.length >= 3) {
            binding.emergencyContactInputLayout.error = null
            true
        } else {
            binding.emergencyContactInputLayout.error = "Emergency contact must be at least 3 characters"
            false
        }
    }
    
    private fun validateForm(): Boolean {
        val isNameValid = validateNameField()
        val isAgeValid = validateAgeField()
        val isContactValid = validateEmergencyContactField()
        
        return isNameValid && isAgeValid && isContactValid
    }
    
    private fun saveProfile() {
        val name = binding.nameInput.text.toString().trim()
        val age = binding.ageInput.text.toString().trim().toIntOrNull() ?: 30
        val bloodGroup = binding.bloodGroupSpinner.selectedItem.toString()
        val emergencyContact = binding.emergencyContactInput.text.toString().trim()
        val emergencyContactPhone = binding.emergencyContactPhoneInput.text.toString().trim()
        val gender = when (binding.genderRadioGroup.checkedRadioButtonId) {
            R.id.maleRadio -> "Male"
            R.id.femaleRadio -> "Female"
            else -> "Unknown"
        }
        
        val victimInfo = VictimInfo(
            name = name,
            age = age,
            bloodGroup = bloodGroup,
            allergies = selectedAllergies.toList(),
            medicalConditions = selectedMedicalConditions.toList(),
            emergencyContact = emergencyContact,
            emergencyContactPhone = emergencyContactPhone,
            gender = gender
        )
        
        // Save to SharedPreferences or database
        saveVictimInfoToStorage(victimInfo)
        
        Snackbar.make(binding.root, "Profile saved successfully", Snackbar.LENGTH_LONG).show()
        
        // Return to previous activity
        finish()
    }
    
    private fun saveVictimInfoToStorage(victimInfo: VictimInfo) {
        // This would save to SharedPreferences or database
        // For now, just show a success message
    }
    
    private fun clearForm() {
        binding.nameInput.text?.clear()
        binding.ageInput.text?.clear()
        binding.emergencyContactInput.text?.clear()
        binding.emergencyContactPhoneInput.text?.clear()
        binding.genderRadioGroup.clearCheck()
        binding.bloodGroupSpinner.setSelection(0)
        
        // Clear chip groups
        binding.allergiesChipGroup.removeAllViews()
        binding.medicalConditionsChipGroup.removeAllViews()
        selectedAllergies.clear()
        selectedMedicalConditions.clear()
        
        // Re-add predefined options
        setupAllergiesChipGroup()
        setupMedicalConditionsChipGroup()
        
        Snackbar.make(binding.root, "Form cleared", Snackbar.LENGTH_SHORT).show()
    }
} 