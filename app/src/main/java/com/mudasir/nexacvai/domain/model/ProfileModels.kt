package com.mudasir.nexacvai.domain.model

data class Experience(
    val id: String = java.util.UUID.randomUUID().toString(),
    val jobTitle: String = "",
    val companyName: String = "",
    val location: String = "",
    val startDate: String = "", // MM/YYYY or DD/MM/YYYY
    val endDate: String = "",
    val isCurrentlyWorking: Boolean = false,
    val description: String = ""
)

data class Education(
    val id: String = java.util.UUID.randomUUID().toString(),
    val degree: String = "",
    val fieldOfStudy: String = "",
    val instituteName: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val isCurrentlyStudying: Boolean = false,
    val grade: String = "", // CGPA or Percentage
    val description: String = ""
)

data class Project(
    val id: String = java.util.UUID.randomUUID().toString(),
    val projectName: String = "",
    val description: String = "",
    val technologiesUsed: List<String> = emptyList(),
    val projectLink: String = "",
    val roleInProject: String = "",
    val startDate: String = "",
    val endDate: String = ""
)

data class Certification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val certificationName: String = "",
    val issuingOrganization: String = "",
    val issueDate: String = "",
    val expiryDate: String = "",
    val credentialUrl: String = ""
)

data class SocialLink(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String = "", // e.g., LinkedIn, GitHub, Behance
    val url: String = ""
)

data class Language(
    val id: String = java.util.UUID.randomUUID().toString(),
    val languageName: String = "",
    val proficiency: String = ""
)

data class Reference(
    val id: String = java.util.UUID.randomUUID().toString(),
    val fullName: String = "",
    val jobTitle: String = "",
    val company: String = "",
    val email: String? = null,
    val phone: String? = null,
    val linkedInUrl: String? = null,
    val notes: String? = null
)
