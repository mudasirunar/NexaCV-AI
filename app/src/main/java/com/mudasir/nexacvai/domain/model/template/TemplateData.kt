package com.mudasir.nexacvai.domain.model.template

import com.mudasir.nexacvai.domain.model.UserProfile

/**
 * Unified data container for rendering CV templates.
 * Contains MS Word-style placeholder guidance text when no profile is selected,
 * and maps cleanly from [UserProfile] for 1-tap profile auto-fill.
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
         * MS Word-Style Sample Guidance Data.
         * Renders informative placeholder guidance text explaining what to write in each section.
         */
        val SAMPLE_FILLER = TemplateData(
            fullName = "Alex Mercer",
            professionalTitle = "Senior Software Architect & Mobile Lead",
            email = "alex.mercer@example.com",
            phone = "+1 (555) 234-5678",
            location = "San Francisco, CA • Open to Remote",
            summary = "Results-driven Software Engineer with 6+ years of experience designing scalable mobile architectures and high-performance cross-platform systems. Proven track record leading agile engineering teams and optimizing document processing pipelines.",
            profilePictureUri = null,
            experiences = listOf(
                TemplateExperienceData(
                    jobTitle = "Lead Android Architect",
                    company = "Apex Financial Technologies",
                    startDate = "01/2021",
                    endDate = "Present",
                    location = "San Francisco, CA",
                    responsibilities = listOf(
                        "Architected offline-first mobile engine using Clean Architecture and Jetpack Compose, reducing app crash rate by 99.4%.",
                        "Led a team of 8 mobile developers to deliver bi-weekly production releases, reducing app cold startup time by 45%.",
                        "Implemented secure local PII encryption at rest using SQLCipher and Android Keystore."
                    ),
                    technologies = listOf("Kotlin", "Jetpack Compose", "Hilt", "Room", "Coroutines")
                ),
                TemplateExperienceData(
                    jobTitle = "Senior Software Engineer",
                    company = "Nexus Cloud Solutions",
                    startDate = "06/2018",
                    endDate = "12/2020",
                    location = "Austin, TX",
                    responsibilities = listOf(
                        "Developed real-time document rendering pipeline handling 50,000+ daily export requests.",
                        "Integrated REST & GraphQL backend services with offline caching and background synchronization."
                    ),
                    technologies = listOf("Kotlin", "Java", "RxJava", "Retrofit", "REST API")
                )
            ),
            educations = listOf(
                TemplateEducationData(
                    degree = "B.S. in Computer Science & Engineering",
                    institution = "University of California, Berkeley",
                    startDate = "2014",
                    endDate = "2018",
                    gradeOrGpa = "3.85 GPA • Honors Graduate",
                    relevantCoursework = "Distributed Systems, Operating Systems, Algorithm Analysis"
                )
            ),
            projects = listOf(
                TemplateProjectData(
                    projectName = "Smart Document AI Engine",
                    roleInProject = "Creator & Lead Developer",
                    startDate = "2023",
                    endDate = "Present",
                    description = "An offline-first document rendering engine for Android capable of generating vector PDFs in under 300ms.",
                    technologiesUsed = listOf("Kotlin", "Canvas", "PdfDocument", "KSP"),
                    projectLink = "github.com/alexmercer/document-engine"
                )
            ),
            skills = listOf(
                "Kotlin", "Android SDK", "Jetpack Compose", "Clean Architecture",
                "Dagger Hilt", "Coroutines & Flow", "Room Database", "Unit Testing",
                "Git & CI/CD", "RESTful APIs", "System Design", "Agile Leadership"
            ),
            socialLinks = listOf(
                TemplateSocialLinkData("LinkedIn", "linkedin.com/in/alexmercer"),
                TemplateSocialLinkData("GitHub", "github.com/alexmercer"),
                TemplateSocialLinkData("Portfolio", "alexmercer.dev")
            ),
            certifications = listOf(
                TemplateCertData("Google Associate Android Developer", "Google", "2022")
            ),
            languages = listOf(
                TemplateLanguageData("English", "Native / Full Professional"),
                TemplateLanguageData("Spanish", "Intermediate Working")
            ),
            references = emptyList()
        )
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
