package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.compose.runtime.Immutable
import com.mudasir.nexacvai.domain.model.*

@Immutable
data class CreateProfileState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val currentStep: Int = 0,
    val totalSteps: Int = 9,
    val error: String? = null,
    
    // Original Profile ID (if editing)
    val profileId: Long? = null,

    // Step 1: Basic Info
    val fullName: String = "",
    val professionalTitle: String = "",
    val emails: List<String> = listOf(""), 
    val phones: List<String> = listOf(""),
    val dateOfBirth: String = "",
    val address: String = "",
    val preferredRole: String = "",
    val yearsOfExperience: String = "",
    val profilePictureUri: String? = null,
    
    // Step 2: Summary and Skills
    val professionalSummary: String = "",
    val skills: List<String> = emptyList(),
    val currentSkillInput: String = "",
    
    // Step 3: Experiences
    val experiences: List<Experience> = emptyList(),
    
    // Step 4: Projects
    val projects: List<Project> = emptyList(),
    
    // Step 5: Educations
    val educations: List<Education> = emptyList(),
    
    // Step 6: Certifications
    val certifications: List<Certification> = emptyList(),
    
    // Step 7: References
    val references: List<Reference> = emptyList(),
    
    // Step 8: Social Links and Languages
    val socialLinks: List<SocialLink> = emptyList(),
    val languages: List<Language> = emptyList(),
    
    // Step 9: Additional Info
    val hobbies: String = "",
    val volunteerWork: String = "",
    val awards: String = ""
)
