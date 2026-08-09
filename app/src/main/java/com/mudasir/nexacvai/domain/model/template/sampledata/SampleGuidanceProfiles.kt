package com.mudasir.nexacvai.domain.model.template.sampledata

import com.mudasir.nexacvai.domain.model.template.*

/**
 * Dedicated Repository of Sample Guidance Profiles across multiple industries
 * (Software Architecture, Executive Leadership, Healthcare/Medicine, Finance, and Product Design).
 * Used for MS Word-style guidance placeholders and 1-tap template previews.
 */
object SampleGuidanceProfiles {

    /** Male Software Architecture Lead */
    val MALE_TECH_ARCHITECT = TemplateData(
        fullName = "Alex Mercer",
        professionalTitle = "Senior Software Architect & Mobile Lead",
        email = "alex.mercer@example.com",
        phone = "+1 (555) 234-5678",
        location = "San Francisco, CA • Open to Remote",
        summary = "Results-driven Software Engineer with 7+ years of experience designing scalable mobile architectures, high-performance offline-first engines, and cross-platform systems. Proven track record leading agile engineering teams and optimizing document processing pipelines.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_male_bluebg",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Lead Mobile Architect",
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
                jobTitle = "Senior Systems Engineer",
                company = "Nexus Cloud Solutions",
                startDate = "06/2018",
                endDate = "12/2020",
                location = "Austin, TX",
                responsibilities = listOf(
                    "Developed high-throughput REST APIs and GraphQL microservices processing over 12M daily document requests.",
                    "Migrated legacy monolithic codebase to modular multi-module Kotlin architecture.",
                    "Optimized database query performance with custom Room indices and Coroutines Flow."
                ),
                technologies = listOf("Kotlin", "Java", "Docker", "AWS", "Ktor")
            ),
            TemplateExperienceData(
                jobTitle = "Android Developer & Core Lead",
                company = "Vanguard Digital Labs",
                startDate = "05/2016",
                endDate = "05/2018",
                location = "Seattle, WA",
                responsibilities = listOf(
                    "Built custom UI design systems with hardware-accelerated canvas rendering.",
                    "Integrated OAuth 2.0 authentication and biometric sign-in security protocols."
                ),
                technologies = listOf("Kotlin", "Android SDK", "RxJava", "Retrofit")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "B.S. in Computer Science & Software Engineering",
                institution = "University of California, Berkeley",
                startDate = "2012",
                endDate = "2016",
                gradeOrGpa = "3.9 / 4.0",
                relevantCoursework = "Distributed Systems, Operating Systems, Algorithm Analysis"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "NexaCV AI Engine",
                roleInProject = "Lead Creator & Architect",
                startDate = "2024",
                endDate = "Present",
                description = "Offline-first intelligent resume builder with pluggable AI engine, multi-profile management, and real-time A4 PDF rendering.",
                technologiesUsed = listOf("Kotlin", "Canvas", "PdfDocument", "KSP", "Hilt")
            ),
            TemplateProjectData(
                projectName = "Enterprise Cloud Storage Vault",
                roleInProject = "Core Infrastructure Lead",
                startDate = "2022",
                endDate = "2023",
                description = "End-to-end encrypted document storage pipeline handling over 5TB of encrypted asset backups.",
                technologiesUsed = listOf("Kotlin", "Ktor", "AWS S3", "SQLCipher")
            ),
            TemplateProjectData(
                projectName = "Vector Search PDF Indexer",
                roleInProject = "Principal Contributor",
                startDate = "2021",
                endDate = "2022",
                description = "High-speed document parsing engine converting PDF files to structured vector embeddings for local semantic search.",
                technologiesUsed = listOf("C++", "Java JNI", "Android NDK")
            )
        ),
        skills = listOf("Kotlin", "Jetpack Compose", "Clean Architecture", "Dagger Hilt", "Room DB", "Coroutines", "System Design", "PDF Generation", "Performance Tuning"),
        certifications = listOf(
            TemplateCertData("AWS Certified Solutions Architect", "Amazon Web Services", "2023"),
            TemplateCertData("Google Associate Android Developer", "Google", "2021")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native / Fluent"),
            TemplateLanguageData("Spanish", "Professional Working")
        ),
        socialLinks = listOf(
            TemplateSocialLinkData("GitHub", "https://github.com/alexmercer"),
            TemplateSocialLinkData("LinkedIn", "https://linkedin.com/in/alexmercer")
        ),
        references = listOf(
            TemplateReferenceData("Sarah Jenkins", "VP of Engineering", "Apex Financial Technologies", "sarah.j@apexfin.com"),
            TemplateReferenceData("Michael Chang", "Principal Systems Architect", "Nexus Cloud", "m.chang@nexuscloud.io")
        )
    )

    /** Female Executive Product Leader */
    val FEMALE_EXECUTIVE_LEADER = TemplateData(
        fullName = "Elena Rostova",
        professionalTitle = "VP of Product Strategy & Growth",
        email = "elena.rostova@example.com",
        phone = "+1 (555) 876-5432",
        location = "New York, NY • Global Executive",
        summary = "Visionary Product Executive with 10+ years driving multi-million dollar SaaS growth, international market expansion, and cross-functional leadership. Expert in scaling enterprise product suites and AI integrations.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_female_bluebg",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Vice President of Product",
                company = "Global Dynamics SaaS",
                startDate = "03/2020",
                endDate = "Present",
                location = "New York, NY",
                responsibilities = listOf(
                    "Scaled annual recurring revenue (ARR) from $14M to $68M in 3 years across European & North American enterprise markets.",
                    "Directed 4 product directors and 35 product managers delivering AI workflow automation.",
                    "Spearheaded strategic acquisition of 2 tech startups."
                ),
                technologies = listOf("SaaS", "AI Strategy", "Enterprise Growth", "M&A")
            ),
            TemplateExperienceData(
                jobTitle = "Director of Product Management",
                company = "Vanguard Digital Media",
                startDate = "01/2016",
                endDate = "02/2020",
                location = "Boston, MA",
                responsibilities = listOf(
                    "Launched subscription platform reaching 4.5M active monthly users.",
                    "Decreased customer churn by 32% through personalized recommendation engines."
                ),
                technologies = listOf("Product Lifecycle", "User Retention", "Data Analytics")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Master of Business Administration (MBA)",
                institution = "Harvard Business School",
                startDate = "2014",
                endDate = "2016",
                gradeOrGpa = "High Distinction"
            ),
            TemplateEducationData(
                degree = "B.A. in Economics & International Business",
                institution = "Columbia University",
                startDate = "2010",
                endDate = "2014"
            )
        ),
        skills = listOf("Executive Leadership", "SaaS Strategy", "Enterprise Sales", "P&L Management", "AI Integration", "Cross-Functional Management"),
        certifications = listOf(
            TemplateCertData("Project Management Professional (PMP®)", "PMI", "2018")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("French", "Fluent")
        )
    )

    /** Female Clinical Doctor & Healthcare Specialist */
    val FEMALE_MEDICAL_SPECIALIST = TemplateData(
        fullName = "Dr. Sophia Lin, MD",
        professionalTitle = "Chief Clinical Director & Cardiology Specialist",
        email = "sophia.lin@medcenter.org",
        phone = "+1 (555) 432-1098",
        location = "Chicago, IL",
        summary = "Board-Certified Physician & Clinical Director with 8+ years specializing in cardiovascular health, clinical research trials, and digital health technology integration. Committed to patient care excellence and clinical innovation.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_female_whitebg_01",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Chief Clinical Director",
                company = "Midwest Cardiology Medical Center",
                startDate = "08/2019",
                endDate = "Present",
                location = "Chicago, IL",
                responsibilities = listOf(
                    "Oversee 45 clinical staff members and manage annual department budget of $18M.",
                    "Pioneered telemedicine patient intake system, reducing emergency room wait times by 28%."
                )
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Doctor of Medicine (M.D.)",
                institution = "Northwestern University Feinberg School of Medicine",
                startDate = "2011",
                endDate = "2015"
            )
        ),
        skills = listOf("Clinical Cardiology", "Healthcare Administration", "Patient Care", "Medical Research", "Telemedicine")
    )

    /** Male Financial Director */
    val MALE_FINANCE_DIRECTOR = TemplateData(
        fullName = "Marcus Vance",
        professionalTitle = "Senior Investment Director & Financial Analyst",
        email = "marcus.vance@capital.com",
        phone = "+1 (555) 901-2345",
        location = "New York, NY",
        summary = "Chartered Financial Analyst (CFA®) with 9+ years managing $450M portfolio investments, corporate restructuring, and risk modeling for institutional clients.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_male_whitebg_01",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Senior Investment Director",
                company = "Vance & Sterling Capital",
                startDate = "02/2018",
                endDate = "Present",
                location = "New York, NY",
                responsibilities = listOf(
                    "Managed portfolio yields generating 18.4% average annual returns.",
                    "Executed multi-currency hedging strategies protecting asset downside."
                )
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "B.S. in Finance & Quantitative Economics",
                institution = "NYU Stern School of Business",
                startDate = "2010",
                endDate = "2014"
            )
        ),
        skills = listOf("Portfolio Management", "CFA® Certified", "Financial Modeling", "Risk Analysis", "Corporate Finance")
    )

    /** List of all default guidance profiles for multi-industry guidance */
    val ALL_GUIDANCE_PROFILES = listOf(
        MALE_TECH_ARCHITECT,
        FEMALE_EXECUTIVE_LEADER,
        FEMALE_MEDICAL_SPECIALIST,
        MALE_FINANCE_DIRECTOR
    )
}
