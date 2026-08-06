package com.mudasir.nexacvai.domain.model

import androidx.annotation.Keep

@Keep
data class UserProfile(
    val id: Long = 0,
    val fullName: String = "",
    val professionalTitle: String = "",
    val emails: List<String> = emptyList(),
    val phones: List<String> = emptyList(),
    val dateOfBirth: String = "",
    val address: String = "",
    val yearsOfExperience: String = "",
    val profilePictureUri: String? = null,
    val professionalSummary: String = "",
    val skills: List<String> = emptyList(),
    val experiences: List<Experience> = emptyList(),
    val projects: List<Project> = emptyList(),
    val educations: List<Education> = emptyList(),
    val certifications: List<Certification> = emptyList(),
    val references: List<Reference> = emptyList(),
    val socialLinks: List<SocialLink> = emptyList(),
    val languages: List<Language> = emptyList(),
    val hobbies: String = "",
    val volunteerWork: String = "",
    val awards: String = "",
    val sourceProfileId: Long? = null,
    val sourceProfileName: String? = null,
    val isCopyTagDismissed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
