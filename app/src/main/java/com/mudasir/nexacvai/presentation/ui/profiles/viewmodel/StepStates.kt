package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.compose.runtime.Immutable
import com.mudasir.nexacvai.domain.model.*

@Immutable
data class BasicInfoStepState(
    val fullName: String,
    val professionalTitle: String,
    val fullNameError: String? = null,
    val professionalTitleError: String? = null,
    val emails: List<String>,
    val phones: List<String>,
    val dateOfBirth: String,
    val address: String,
    val yearsOfExperience: String,
    val profilePictureUri: String?,
    val profileId: Long?,
    val tempSessionId: Long,
    val validationTrigger: Long = 0L
)

@Immutable
data class SummaryStepState(
    val professionalSummary: String,
    val skills: List<String>,
    val skillsError: String? = null,
    val currentSkillInput: String,
    val duplicateSkillError: String? = null,
    val validationTrigger: Long = 0L
)

@Immutable
data class ExperienceProjectsStepState(
    val experiences: List<Experience>,
    val projects: List<Project>,
    val experienceError: String? = null,
    val projectError: String? = null,
    val validationTrigger: Long = 0L
)

@Immutable
data class EducationCertsStepState(
    val educations: List<Education>,
    val certifications: List<Certification>,
    val educationError: String? = null,
    val certificationError: String? = null,
    val validationTrigger: Long = 0L
)

@Immutable
data class SocialsExtrasStepState(
    val socialLinks: List<SocialLink>,
    val languages: List<Language>,
    val references: List<Reference>,
    val socialLinksError: String? = null,
    val languagesError: String? = null,
    val referencesError: String? = null,
    val hobbies: String,
    val volunteerWork: String,
    val awards: String,
    val validationTrigger: Long = 0L
)
