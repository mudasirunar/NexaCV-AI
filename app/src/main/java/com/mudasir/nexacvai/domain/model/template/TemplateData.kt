package com.mudasir.nexacvai.domain.model.template

import com.mudasir.nexacvai.domain.model.UserProfile

/**
 * Unified data container for rendering CV templates.
 * Contains sample placeholder guidance text when no profile is selected,
 * showing users where and how each section will be populated.
 */
data class TemplateData(
    val fullName: String,
    val professionalTitle: String,
    val email: String,
    val phone: String,
    val location: String,
    val summary: String,
    val profilePictureUri: String? = null,
    val experiences: List<TemplateExperienceData> = emptyList(),
    val educations: List<TemplateEducationData> = emptyList(),
    val projects: List<TemplateProjectData> = emptyList(),
    val skills: List<String> = emptyList(),
    val socialLinks: List<TemplateSocialLinkData> = emptyList(),
    val certifications: List<TemplateCertData> = emptyList(),
    val languages: List<TemplateLanguageData> = emptyList(),
    val references: List<TemplateReferenceData> = emptyList()
) {
    companion object {
        /**
         * Default Sample Placeholder Guidance Data.
         */
        val SAMPLE_FILLER: TemplateData
            get() = com.mudasir.nexacvai.domain.model.template.sampledata.SampleGuidanceProfiles.MALE_TECH_ARCHITECT
    }
}

data class TemplateExperienceData(
    val jobTitle: String,
    val company: String,
    val startDate: String,
    val endDate: String,
    val location: String = "",
    val responsibilities: List<String> = emptyList(),
    val technologies: List<String> = emptyList()
)

data class TemplateEducationData(
    val degree: String,
    val institution: String,
    val startDate: String,
    val endDate: String,
    val gradeOrGpa: String = "",
    val relevantCoursework: String = ""
)

data class TemplateProjectData(
    val projectName: String,
    val roleInProject: String,
    val startDate: String,
    val endDate: String,
    val description: String,
    val technologiesUsed: List<String> = emptyList(),
    val projectLink: String = ""
)

data class TemplateSocialLinkData(
    val platform: String,
    val url: String
)

data class TemplateCertData(
    val name: String,
    val issuer: String,
    val date: String
)

data class TemplateLanguageData(
    val languageName: String,
    val proficiency: String
)

data class TemplateReferenceData(
    val name: String,
    val title: String,
    val company: String,
    val contactInfo: String
)

/**
 * Mapper extension converting a pure [UserProfile] domain model to [TemplateData] for 1-tap profile auto-fill.
 */
fun UserProfile.toTemplateData(): TemplateData {
    return TemplateData(
        fullName = this.fullName.ifBlank { "Your Full Name" },
        professionalTitle = this.professionalTitle.ifBlank { "Professional Title" },
        email = this.emails.firstOrNull().orEmpty(),
        phone = this.phones.firstOrNull().orEmpty(),
        location = this.address.ifBlank { "" },
        summary = this.professionalSummary.ifBlank { this.professionalTitle },
        profilePictureUri = this.profilePictureUri,
        experiences = this.experiences.map { exp ->
            TemplateExperienceData(
                jobTitle = exp.jobTitle,
                company = exp.companyName,
                startDate = exp.startDate,
                endDate = if (exp.isCurrentlyWorking) "Present" else exp.endDate,
                location = exp.location,
                responsibilities = if (exp.description.isNotBlank()) listOf(exp.description) else emptyList(),
                technologies = emptyList()
            )
        },
        educations = this.educations.map { edu ->
            TemplateEducationData(
                degree = edu.degree,
                institution = edu.instituteName,
                startDate = edu.startDate,
                endDate = if (edu.isCurrentlyStudying) "Present" else edu.endDate,
                gradeOrGpa = edu.grade,
                relevantCoursework = edu.description
            )
        },
        projects = this.projects.map { proj ->
            TemplateProjectData(
                projectName = proj.projectName,
                roleInProject = proj.roleInProject,
                startDate = proj.startDate,
                endDate = proj.endDate,
                description = proj.description,
                technologiesUsed = proj.technologiesUsed,
                projectLink = proj.projectLink
            )
        },
        skills = this.skills,
        socialLinks = this.socialLinks.map { TemplateSocialLinkData(it.label, it.url) },
        certifications = this.certifications.map { TemplateCertData(it.certificationName, it.issuingOrganization, it.issueDate) },
        languages = this.languages.map { TemplateLanguageData(it.languageName, it.proficiency) },
        references = this.references.map { TemplateReferenceData(it.fullName, it.jobTitle, it.company, it.email.orEmpty()) }
    )
}
