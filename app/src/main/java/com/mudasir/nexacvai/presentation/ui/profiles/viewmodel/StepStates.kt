package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.compose.runtime.Immutable
import com.mudasir.nexacvai.domain.model.*

@Immutable
data class BasicInfoStepState(
    val fullName: String,
    val professionalTitle: String,
    val emails: List<String>,
    val phones: List<String>,
    val dateOfBirth: String,
    val address: String,
    val yearsOfExperience: String,
    val profilePictureUri: String?,
    val profileId: Long?,
    val tempSessionId: Long
)

@Immutable
data class SummaryStepState(
    val professionalSummary: String,
    val skills: List<String>,
    val currentSkillInput: String
)

@Immutable
data class ExperienceProjectsStepState(
    val experiences: List<Experience>,
    val projects: List<Project>
)

@Immutable
data class EducationCertsStepState(
    val educations: List<Education>,
    val certifications: List<Certification>
)

@Immutable
data class SocialsExtrasStepState(
    val socialLinks: List<SocialLink>,
    val languages: List<Language>,
    val references: List<Reference>,
    val hobbies: String,
    val volunteerWork: String,
    val awards: String
)
