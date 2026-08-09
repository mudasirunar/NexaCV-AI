package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.compose.runtime.Immutable
import com.mudasir.nexacvai.domain.model.*

@Immutable
data class CreateProfileState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val currentStep: Int = 0,
    val totalSteps: Int = 5,
    val error: String? = null,
    
    // Original Profile ID (if editing)
    val profileId: Long? = null,
    
    // Temporary session ID for unsaved profiles to isolate profile pictures
    val tempSessionId: Long = -kotlin.math.abs(java.util.UUID.randomUUID().mostSignificantBits),

    // Step 1: Basic Information
    val fullName: String = "",
    val professionalTitle: String = "",
    val emails: List<String> = listOf(""), 
    val phones: List<String> = listOf(""),
    val dateOfBirth: String = "",
    val address: String = "",
    val yearsOfExperience: String = "",
    val profilePictureUri: String? = null,
    
    // Step 2: Professional Snapshot & Skills
    val professionalSummary: String = "",
    val skills: List<String> = emptyList(),
    val currentSkillInput: String = "",
    val duplicateSkillError: String? = null,
    
    // Step 3: Experience & Projects
    val experiences: List<Experience> = emptyList(),
    val projects: List<Project> = emptyList(),
    
    // Step 4: Education & Certifications
    val educations: List<Education> = emptyList(),
    val certifications: List<Certification> = emptyList(),
    
    // Step 5: Social Links & Extras
    val socialLinks: List<SocialLink> = emptyList(),
    val languages: List<Language> = emptyList(),
    val references: List<Reference> = emptyList(),
    val hobbies: String = "",
    val volunteerWork: String = "",
    val awards: String = ""
)
