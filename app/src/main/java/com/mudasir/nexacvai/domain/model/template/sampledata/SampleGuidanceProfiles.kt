package com.mudasir.nexacvai.domain.model.template.sampledata

import com.mudasir.nexacvai.domain.model.template.*

/**
 * Dedicated Repository of Sample Guidance Profiles across multiple industries
 * (Software Architecture, Executive Leadership, Healthcare/Medicine, Finance, Cyber Security, Creative UX, Academic Research, Fresh Graduates, DevOps, and Architectural Design).
 * Used for MS Word-style guidance placeholders and 1-tap template previews.
 * Fully populated with 100% UserProfile domain parity (including Hobbies, Volunteer Work, Awards, References, Languages, and Certifications).
 */
object SampleGuidanceProfiles {

    /** 1. Male Software Architecture Lead */
    val MALE_TECH_ARCHITECT = TemplateData(
        fullName = "Alex Mercer",
        professionalTitle = "Senior Software Architect & Mobile Lead",
        email = "alex.mercer@example.com",
        phone = "+1 (555) 234-5678",
        location = "San Francisco, CA • Open to Remote",
        summary = "Results-driven Software Engineer with 7+ years of experience designing scalable mobile architectures, high-performance offline-first engines, and cross-platform systems. Proven track record leading agile engineering teams and optimizing document processing pipelines.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_male_bluebg",
        dateOfBirth = "14/05/1990",
        yearsOfExperience = "7+ Years",
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
                technologiesUsed = listOf("Kotlin", "Canvas", "PdfDocument", "KSP", "Hilt"),
                projectLink = "github.com/alexmercer/nexacv"
            ),
            TemplateProjectData(
                projectName = "Enterprise Cloud Storage Vault",
                roleInProject = "Core Infrastructure Lead",
                startDate = "2022",
                endDate = "2023",
                description = "End-to-end encrypted document storage pipeline handling over 5TB of encrypted asset backups.",
                technologiesUsed = listOf("Kotlin", "Ktor", "AWS S3", "SQLCipher"),
                projectLink = "github.com/alexmercer/cloud-vault"
            )
        ),
        skills = listOf("Kotlin", "Jetpack Compose", "Clean Architecture", "Dagger Hilt", "Room DB", "Coroutines", "System Design", "PDF Generation"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Mobile Development", listOf("Kotlin", "Jetpack Compose", "Swift", "Android SDK", "Coroutines")),
            TemplateSkillCategoryGroup("Backend Systems", listOf("Java", "Ktor", "Clean Architecture", "Dagger Hilt", "Room DB")),
            TemplateSkillCategoryGroup("Cloud & Security", listOf("Docker", "AWS S3", "SQLCipher", "CI/CD Pipelines", "Git"))
        ),
        certifications = listOf(
            TemplateCertData("AWS Certified Solutions Architect", "Amazon Web Services", "2023"),
            TemplateCertData("Google Associate Android Developer", "Google", "2021")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native / Fluent"),
            TemplateLanguageData("Spanish", "Professional Working")
        ),
        references = listOf(
            TemplateReferenceData("David Vance", "VP of Engineering", "Apex Financial", "david.vance@apexfin.com • +1 (555) 987-6543", "david.vance@apexfin.com", "+1 (555) 987-6543", "linkedin.com/in/davidvance"),
            TemplateReferenceData("Sarah Lin", "Director of Product", "Nexus Cloud", "slin@nexuscloud.io • +1 (555) 432-1098", "slin@nexuscloud.io", "+1 (555) 432-1098", "linkedin.com/in/sarahlin")
        ),
        hobbies = listOf("Open Source Engineering", "Algorithmic Chess", "Marathon Running"),
        volunteerWork = listOf("Volunteer Coding Instructor at Code.org (2021 - Present)"),
        awards = listOf("Google Developer Excellence Award (2022)")
    )

    /** 2. Female Executive Product Leader */
    val FEMALE_EXECUTIVE_LEADER = TemplateData(
        fullName = "Elena Rostova",
        professionalTitle = "VP of Product Strategy & Growth",
        email = "elena.rostova@example.com",
        phone = "+1 (555) 876-5432",
        location = "New York, NY • Global Executive",
        summary = "Visionary Product Executive with 10+ years driving multi-million dollar SaaS growth, international market expansion, and cross-functional leadership. Expert in scaling enterprise product suites and AI integrations.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_female_bluebg",
        dateOfBirth = "22/09/1986",
        yearsOfExperience = "10+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "VP of Product Strategy",
                company = "Global SaaS Solutions Corp",
                startDate = "03/2020",
                endDate = "Present",
                location = "New York, NY",
                responsibilities = listOf(
                    "Scaled annual recurring revenue (ARR) from $18M to $65M across North America and European enterprise markets.",
                    "Directed 45+ cross-functional product managers, designers, and data scientists across 3 international hubs.",
                    "Launched enterprise AI copilot integration, increasing monthly active user retention by 38%."
                ),
                technologies = listOf("SaaS Strategy", "P&L Management", "AI Product Integration", "Enterprise Growth")
            ),
            TemplateExperienceData(
                jobTitle = "Director of Product Management",
                company = "Vanguard Digital Media",
                startDate = "08/2016",
                endDate = "02/2020",
                location = "Boston, MA",
                responsibilities = listOf(
                    "Managed $12M product budget and led agile product roadmap for flagship B2B analytics suite.",
                    "Negotiated strategic partnerships with Fortune 500 enterprise clients."
                ),
                technologies = listOf("Product Strategy", "B2B Analytics", "Agile Leadership")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Master of Business Administration (MBA)",
                institution = "Harvard Business School",
                startDate = "2014",
                endDate = "2016",
                gradeOrGpa = "High Distinction",
                relevantCoursework = "Executive Leadership, Strategic Marketing, Financial Engineering"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Enterprise AI Copilot Platform",
                roleInProject = "Executive Sponsor & Product Lead",
                startDate = "2022",
                endDate = "2023",
                description = "Spearheaded $4.5M AI product initiative delivering automated workflow intelligence for enterprise SaaS subscribers."
            )
        ),
        skills = listOf("Executive Strategy", "P&L Ownership", "SaaS Growth", "Product Lifecycle Management", "Team Building", "Enterprise AI"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Strategic Leadership", listOf("P&L Management", "Global Expansion", "Board Governance", "M&A Integration")),
            TemplateSkillCategoryGroup("Product Innovation", listOf("SaaS Product Architecture", "AI Integration", "User Retention Optimization")),
            TemplateSkillCategoryGroup("Corporate Governance", listOf("Investor Relations", "Cross-Functional Management", "Enterprise Sales"))
        ),
        certifications = listOf(
            TemplateCertData("Certified Product Executive (CPE)", "Product Management Institute", "2019")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native / Fluent"),
            TemplateLanguageData("German", "Professional Working"),
            TemplateLanguageData("French", "Conversational")
        ),
        references = listOf(
            TemplateReferenceData("Marcus Thorne", "CEO", "Global SaaS Corp", "m.thorne@globalsaas.com"),
            TemplateReferenceData("Helena Vance", "Board Member", "Vanguard Digital", "h.vance@vanguard.org")
        ),
        hobbies = listOf("Art History & Gallery Curation", "Competitive Sailing", "Angel Investing"),
        volunteerWork = listOf("Board Member at Women in Executive Tech (2020 - Present)"),
        awards = listOf("Forbes 40 Under 40 Product Leader (2023)", "SaaS Growth Executive of the Year (2022)")
    )

    /** 3. Female Doctor / Clinical Specialist */
    val FEMALE_MEDICAL_SPECIALIST = TemplateData(
        fullName = "Dr. Sophia Lin, MD",
        professionalTitle = "Chief Clinical Specialist & Medical Director",
        email = "sophia.lin@example-hospital.org",
        phone = "+1 (555) 987-6543",
        location = "Chicago, IL • Board Certified",
        summary = "Board-Certified Attending Physician and Clinical Director with 10+ years leading inpatient hospital care, electronic health record (EHR) optimization, clinical research trials, and medical residency training.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_female_whitebg_01",
        dateOfBirth = "14/11/1986",
        yearsOfExperience = "10+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Chief of Internal Medicine & Clinical Professor",
                company = "Chicago Memorial Hospital & Health System",
                startDate = "04/2019",
                endDate = "Present",
                location = "Chicago, IL",
                responsibilities = listOf(
                    "Oversaw daily medical operations of 140-bed acute care inpatient unit, supervising 38 attending physicians and medical residents.",
                    "Implemented digitized electronic health record (EHR/Epic) clinical decision workflow, cutting diagnostic turn-around time by 34%.",
                    "Chaired the Hospital Quality & Safety Committee, achieving 100% Joint Commission (JCAHO) accreditation compliance."
                ),
                technologies = listOf("Clinical Medicine", "Inpatient Care", "Epic EHR", "Hospital Administration", "Bioethics", "JCAHO")
            ),
            TemplateExperienceData(
                jobTitle = "Attending Physician & Clinical Fellow",
                company = "Northwestern Medicine Medical Center",
                startDate = "07/2015",
                endDate = "03/2019",
                location = "Chicago, IL",
                responsibilities = listOf(
                    "Provided diagnostic, therapeutic, and preventative care for 1,800+ complex internal medicine and ICU patients.",
                    "Directed daily resident teaching rounds and published 4 peer-reviewed clinical trials on sepsis protocol optimization."
                ),
                technologies = listOf("Internal Medicine", "Intensive Care (ICU)", "Diagnostic Protocols", "Medical Residency Supervision")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Doctor of Medicine (M.D.)",
                fieldOfStudy = "Internal Medicine",
                institution = "Northwestern University Feinberg School of Medicine",
                startDate = "2011",
                endDate = "2015",
                gradeOrGpa = "Magna Cum Laude (Alpha Omega Alpha)",
                relevantCoursework = "Internal Medicine Residency at McGaw Medical Center (2015 - 2018)",
                description = "Honors: Alpha Omega Alpha Medical Honor Society"
            ),
            TemplateEducationData(
                degree = "B.S. in Human Biology & Biochemistry",
                fieldOfStudy = "Pre-Medicine",
                institution = "University of Chicago",
                startDate = "2007",
                endDate = "2011",
                gradeOrGpa = "3.94 / 4.0 (Summa Cum Laude)"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Hospital Sepsis & Infection Prevention Protocol",
                roleInProject = "Principal Medical Investigator",
                startDate = "2020",
                endDate = "2022",
                description = "Standardized hospital-wide disinfection and sepsis protocols, achieving a zero-infection record across 18 surgical suites.",
                technologiesUsed = listOf("Epic EHR", "Sepsis Analytics", "Clinical Protocols"),
                projectLink = "cmh.org/sepsis-protocol"
            ),
            TemplateProjectData(
                projectName = "Telemedicine Inpatient Triage Initiative",
                roleInProject = "Director of Clinical Operations",
                startDate = "2021",
                endDate = "2023",
                description = "Launched remote specialist consultation network across 4 regional community hospitals.",
                technologiesUsed = listOf("EHR Telehealth", "Remote Patient Monitoring"),
                projectLink = "cmh.org/telemedicine"
            )
        ),
        skills = listOf("Clinical Medicine", "Internal Medicine", "Patient Diagnosis", "EHR Systems", "Hospital Administration", "Medical Research"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Medical Expertise", listOf("Internal Medicine", "Diagnostic Care", "Patient Triage", "Emergency Response")),
            TemplateSkillCategoryGroup("Clinical Leadership", listOf("Hospital Administration", "Epic EHR", "JCAHO Accreditation", "Staff Supervision")),
            TemplateSkillCategoryGroup("Research & Bioethics", listOf("Peer-Reviewed Clinical Trials", "Bioethics Committee", "Protocol Standardization"))
        ),
        certifications = listOf(
            TemplateCertData("Board Certified in Internal Medicine", "American Board of Internal Medicine", "2018"),
            TemplateCertData("Licensed Physician & Surgeon", "State of Illinois (License #036-142890)", "2015"),
            TemplateCertData("Advanced Cardiovascular Life Support (ACLS)", "American Heart Association", "2023"),
            TemplateCertData("DEA Medical Registration", "U.S. Drug Enforcement Administration", "2015")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("Mandarin Chinese", "Fluent")
        ),
        references = listOf(
            TemplateReferenceData("Dr. Robert Sterling, MD", "Chief Executive Medical Officer", "Chicago Memorial Hospital", "r.sterling@cmh.org • +1 (555) 312-9900", "r.sterling@cmh.org", "+1 (555) 312-9900"),
            TemplateReferenceData("Dr. Karen White, MD", "Director of Medical Residency", "Northwestern Medicine", "k.white@nm.org • +1 (555) 456-7788", "k.white@nm.org", "+1 (555) 456-7788")
        ),
        hobbies = listOf("Classical Piano Performance", "Medical History Research", "Yoga & Wellness"),
        volunteerWork = listOf("Volunteer Physician at Community Free Health Clinic (2017 - Present)"),
        awards = listOf("Physician Excellence Award (2021)", "Alpha Omega Alpha Medical Honor Society (2015)")
    )

    /** 4. Male Finance & Investment Director */
    val MALE_FINANCE_DIRECTOR = TemplateData(
        fullName = "Marcus Vance",
        professionalTitle = "Senior Investment Director & CFA®",
        email = "marcus.vance@example-capital.com",
        phone = "+1 (555) 345-6789",
        location = "New York, NY • Wall Street",
        summary = "Analytical Corporate Finance Leader and CFA charterholder with 8+ years of experience in portfolio management, M&A advisory, private equity valuation, and enterprise risk management.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_male_whitebg_01",
        dateOfBirth = "19/07/1989",
        yearsOfExperience = "8+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Senior Investment Director",
                company = "Blackstone Capital Management",
                startDate = "02/2020",
                endDate = "Present",
                location = "New York, NY",
                responsibilities = listOf(
                    "Managed $450M institutional asset portfolio yielding an average annual return of 14.8%.",
                    "Led financial due diligence and transaction structuring for 12 technology M&A acquisitions.",
                    "Developed proprietary quantitative risk models using Python and Bloomberg Terminal API."
                ),
                technologies = listOf("Financial Modeling", "Portfolio Management", "Bloomberg Terminal", "Python", "Valuation")
            ),
            TemplateExperienceData(
                jobTitle = "Corporate Finance Associate",
                company = "Goldman Sachs & Co.",
                startDate = "07/2016",
                endDate = "01/2020",
                location = "New York, NY",
                responsibilities = listOf(
                    "Constructed discounted cash flow (DCF) and leveraged buyout (LBO) financial models for Fortune 500 transactions."
                )
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "B.S. in Finance & Economics",
                institution = "Wharton School, University of Pennsylvania",
                startDate = "2012",
                endDate = "2016",
                gradeOrGpa = "Summa Cum Laude",
                relevantCoursework = "Corporate Finance, Asset Pricing, Financial Accounting"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Quantitative Portfolio Risk Model",
                roleInProject = "Lead Financial Architect",
                startDate = "2021",
                endDate = "2022",
                description = "Built algorithmic VaR (Value at Risk) stress-testing framework evaluating portfolio exposure during macroeconomic shifts."
            )
        ),
        skills = listOf("Financial Modeling", "Portfolio Management", "M&A Advisory", "Valuation (DCF/LBO)", "Risk Analysis", "Python"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Investment & Advisory", listOf("Portfolio Management", "M&A Structuring", "Private Equity Valuation")),
            TemplateSkillCategoryGroup("Quantitative Analysis", listOf("Financial Modeling (DCF/LBO)", "Risk Analysis", "Python", "Bloomberg")),
            TemplateSkillCategoryGroup("Compliance & Capital", listOf("Asset Allocation", "Regulatory Compliance", "Capital Raising"))
        ),
        certifications = listOf(
            TemplateCertData("Chartered Financial Analyst (CFA®)", "CFA Institute", "2019"),
            TemplateCertData("Financial Risk Manager (FRM)", "GARP", "2021")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("French", "Fluent")
        ),
        references = listOf(
            TemplateReferenceData("Arthur Pendelton", "Managing Partner", "Blackstone Capital", "a.pendelton@blackstone.com")
        ),
        hobbies = listOf("Financial Blog Writing", "Squash & Tennis", "Chess Strategy"),
        volunteerWork = listOf("Junior Achievement Financial Literacy Mentor (2018 - Present)"),
        awards = listOf("Wall Street Young Analyst of the Year (2018)")
    )

    /** 5. Male Cyber Security Specialist */
    val MALE_CYBER_SECURITY_LEAD = TemplateData(
        fullName = "David Chen",
        professionalTitle = "Principal Security Architect & CISSP®",
        email = "david.chen@example-sec.io",
        phone = "+1 (555) 654-3210",
        location = "Seattle, WA • Top Secret Clearance",
        summary = "Certified Information Systems Security Professional with 8+ years specializing in zero-trust cloud security, penetration testing, automated threat detection, and incident response architecture.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_male_whitebg_02",
        dateOfBirth = "08/04/1991",
        yearsOfExperience = "8+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Principal Security Architect",
                company = "CloudFort Cyber Defense",
                startDate = "05/2021",
                endDate = "Present",
                location = "Seattle, WA",
                responsibilities = listOf(
                    "Architected zero-trust IAM and network micro-segmentation securing multi-cloud environment across AWS, Azure, and GCP.",
                    "Led red team penetration tests and automated vulnerability remediations across 2,500+ production Kubernetes nodes.",
                    "Automated SOC threat intelligence pipelines using Python, SIEM, and AWS GuardDuty."
                ),
                technologies = listOf("Zero-Trust", "Kubernetes Security", "SIEM", "Python", "AWS GuardDuty")
            ),
            TemplateExperienceData(
                jobTitle = "Senior Threat Intelligence Analyst",
                company = "Sentinel Defense Labs",
                startDate = "06/2018",
                endDate = "04/2021",
                location = "Austin, TX",
                responsibilities = listOf(
                    "Engineered real-time SIEM detection rules and malware reverse-engineering protocols for enterprise SOC.",
                    "Conducted threat hunting operations identifying advanced persistent threats (APT) across financial infrastructure."
                ),
                technologies = listOf("Splunk", "CrowdStrike", "Threat Hunting", "Metasploit")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "M.S. in Cybersecurity & Information Assurance",
                fieldOfStudy = "Cloud & Network Security",
                institution = "Carnegie Mellon University",
                startDate = "2015",
                endDate = "2017",
                gradeOrGpa = "3.94 / 4.0",
                relevantCoursework = "Automated Vulnerability Exploitation & Remediation in Kubernetes",
                description = "Master's Thesis: Zero-Trust Micro-segmentation in Distributed Service Meshes"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Zero-Trust Service Mesh Shield",
                roleInProject = "Lead Security Architect",
                startDate = "2022",
                endDate = "2023",
                description = "Deployed Service Mesh mTLS encryption and micro-segmentation across 2,000+ microservices containers with zero downtime.",
                technologiesUsed = listOf("Kubernetes", "Istio", "Go", "Vault"),
                projectLink = "github.com/davidchen/mesh-shield"
            ),
            TemplateProjectData(
                projectName = "Automated SOC SIEM Detection Engine",
                roleInProject = "Principal Security Engineer",
                startDate = "2021",
                endDate = "2022",
                description = "Built automated security event ingestion pipeline processing over 50M log records daily with real-time Slack alerting.",
                technologiesUsed = listOf("Python", "Splunk API", "AWS GuardDuty", "Terraform"),
                projectLink = "github.com/davidchen/siem-engine"
            )
        ),
        skills = listOf("Zero-Trust Architecture", "Penetration Testing", "SIEM & SOC", "AWS Security", "Kubernetes Security", "Python", "ISO 27001"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Security Architecture", listOf("Zero-Trust Architecture", "IAM", "Cloud Security", "Kubernetes Security")),
            TemplateSkillCategoryGroup("Threat & Response", listOf("Penetration Testing", "Splunk SIEM", "CrowdStrike", "Incident Response")),
            TemplateSkillCategoryGroup("Security Engineering", listOf("Python", "Bash", "Terraform", "Docker Security"))
        ),
        socialLinks = listOf(
            TemplateSocialLinkData("GitHub", "github.com/davidchen"),
            TemplateSocialLinkData("LinkedIn", "linkedin.com/in/davidchensec")
        ),
        certifications = listOf(
            TemplateCertData("Certified Information Systems Security Professional (CISSP®)", "ISC2", "2020"),
            TemplateCertData("Offensive Security Certified Professional (OSCP)", "OffSec", "2022"),
            TemplateCertData("AWS Certified Security - Specialty", "Amazon Web Services", "2021")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("Mandarin", "Conversational")
        ),
        references = listOf(
            TemplateReferenceData("Victor Vance", "CISO", "CloudFort Cyber Defense", "v.vance@cloudfort.io • +1 (555) 987-1234", "v.vance@cloudfort.io", "+1 (555) 987-1234"),
            TemplateReferenceData("Marcus Thorne", "VP of Infrastructure", "Sentinel Labs", "m.thorne@sentinel.io • +1 (555) 456-7890", "m.thorne@sentinel.io", "+1 (555) 456-7890")
        ),
        hobbies = listOf("Capture The Flag (CTF) Security Tournaments", "Hardware Lockpicking & Reverse Engineering"),
        volunteerWork = listOf("EFF (Electronic Frontier Foundation) Cybersecurity Mentor"),
        awards = listOf("DEF CON CTF Top 5 Finalist (2022)")
    )

    /** 6. Female Creative UX Lead */
    val FEMALE_CREATIVE_UX_LEAD = TemplateData(
        fullName = "Amara Okafor",
        professionalTitle = "Staff Product Designer & UX Lead",
        email = "amara.okafor@example-design.com",
        phone = "+1 (555) 432-1098",
        location = "Los Angeles, CA • Creative Studio",
        summary = "Award-winning Product Designer with 6+ years creating human-centered digital experiences, multi-brand design systems, and mobile interfaces. Passionate about WCAG accessibility, typography, and interactive design prototypes.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_female_whitebg_02",
        dateOfBirth = "30/01/1993",
        yearsOfExperience = "6+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Staff Product Designer & UX Lead",
                company = "Lumina Design Studio",
                startDate = "09/2020",
                endDate = "Present",
                location = "Los Angeles, CA",
                responsibilities = listOf(
                    "Architected multi-brand Figma design system used by 60+ engineers and product designers.",
                    "Led remote usability testing labs across 400+ international users, boosting onboarding conversion by 38%.",
                    "Spearheaded WCAG AAA accessibility audit and color system tokens across iOS and Android apps."
                ),
                technologies = listOf("Figma", "Design Systems", "Prototyping", "UX Research", "Usability Testing", "Principle")
            ),
            TemplateExperienceData(
                jobTitle = "Senior Interaction Designer",
                company = "Frog Design Agency",
                startDate = "06/2018",
                endDate = "08/2020",
                location = "San Francisco, CA",
                responsibilities = listOf(
                    "Designed end-to-end mobile banking experience for top-tier fintech application serving 1.2M users.",
                    "Created high-fidelity micro-interactions and motion UI prototypes using Framer and After Effects."
                ),
                technologies = listOf("Figma", "Framer", "After Effects", "User Journeys", "Wireframing")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "B.F.A. in Interaction Design & Digital Media",
                fieldOfStudy = "Interaction Design",
                institution = "Rhode Island School of Design (RISD)",
                startDate = "2014",
                endDate = "2018",
                gradeOrGpa = "3.91 / 4.0 (Honors)",
                description = "Thesis: Human-Centered Micro-Interactions in Mobile Interfaces"
            ),
            TemplateEducationData(
                degree = "Diploma in Visual Communication & Typography",
                institution = "ArtCenter College of Design",
                startDate = "2012",
                endDate = "2014"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Lumina Multi-Brand UI Design System",
                roleInProject = "Lead Systems Architect",
                startDate = "2021",
                endDate = "2023",
                description = "Constructed 250+ accessible UI components in Figma with dark mode tokens and WCAG AAA contrast compliance.",
                technologiesUsed = listOf("Figma", "Design Tokens", "WCAG AAA", "Principle"),
                projectLink = "amara-ux.com/lumina"
            ),
            TemplateProjectData(
                projectName = "Fintech Mobile Banking Experience",
                roleInProject = "Senior UX Researcher & Designer",
                startDate = "2019",
                endDate = "2020",
                description = "Redesigned onboarding user journey cutting drop-off rates by 24% across mobile web and native app.",
                technologiesUsed = listOf("Framer", "User Testing", "Motion UI"),
                projectLink = "amara-ux.com/fintech"
            )
        ),
        skills = listOf("Figma", "Design Systems", "UX Research", "Interactive Prototyping", "UI Motion", "WCAG Accessibility"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("UX & Interaction", listOf("User Research", "Wireframing", "Interactive Prototyping", "Usability Testing")),
            TemplateSkillCategoryGroup("UI & Systems", listOf("Figma Design Systems", "Component Architecture", "Typography", "Motion UI")),
            TemplateSkillCategoryGroup("Design Standards", listOf("WCAG AAA Accessibility", "Design Tokens", "Design System Documentation"))
        ),
        socialLinks = listOf(
            TemplateSocialLinkData("Portfolio", "amara-ux.com"),
            TemplateSocialLinkData("Behance", "behance.net/amaraokafor")
        ),
        certifications = listOf(
            TemplateCertData("Nielsen Norman Group UX Master Certification", "NN/g", "2021"),
            TemplateCertData("Certified Professional in Accessibility (CPACC)", "IAAP", "2022"),
            TemplateCertData("Enterprise Design Thinking Practitioner", "IBM", "2020")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("Igbo", "Fluent")
        ),
        references = listOf(
            TemplateReferenceData("Chloe Bennett", "VP of Product Design", "Lumina Studio", "c.bennett@luminadesign.com • +1 (555) 432-8877", "c.bennett@luminadesign.com", "+1 (555) 432-8877"),
            TemplateReferenceData("Julian Vance", "Design Director", "Frog Design Agency", "j.vance@frogdesign.com • +1 (555) 987-6543", "j.vance@frogdesign.com", "+1 (555) 987-6543")
        ),
        hobbies = listOf("Generative Canvas Art", "Street Photography", "Ceramics & Pottery"),
        volunteerWork = listOf("Design Mentor at ADPList (2021 - Present)"),
        awards = listOf("Awwwards Site of the Day (2022)", "Red Dot Design Award (2021)")
    )

    /** 7. Male Academic Researcher & Professor */
    val MALE_ACADEMIC_RESEARCHER = TemplateData(
        fullName = "Prof. James Sterling",
        professionalTitle = "Associate Professor & AI Fellow",
        email = "j.sterling@example-edu.org",
        phone = "+1 (555) 789-0123",
        location = "Stanford, CA • Higher Education",
        summary = "Distinguished Computer Science Researcher specializing in machine learning algorithms, natural language processing, and ethical AI systems. Authored 14+ peer-reviewed papers with over 1,200 academic citations.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_male_whitebg_01",
        dateOfBirth = "05/06/1984",
        yearsOfExperience = "12+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Associate Professor of Computer Science",
                company = "Stanford University AI Lab",
                startDate = "09/2017",
                endDate = "Present",
                location = "Stanford, CA",
                responsibilities = listOf(
                    "Principal Investigator on $2.8M NSF grant researching ethical LLM alignment and bias mitigation.",
                    "Taught graduate courses CS224N (Deep Learning & NLP) to 350+ doctoral and master's students annually.",
                    "Advised 8 Ph.D. candidates and 14 Master's thesis projects in neural architecture safety."
                ),
                technologies = listOf("Deep Learning", "PyTorch", "NLP", "Grant Writing", "Ph.D. Advising")
            ),
            TemplateExperienceData(
                jobTitle = "Postdoctoral Research Fellow",
                company = "MIT CSAIL",
                startDate = "09/2013",
                endDate = "08/2017",
                location = "Cambridge, MA",
                responsibilities = listOf(
                    "Engineered novel transformer attention mechanisms reducing inference latency by 40%.",
                    "Co-authored 6 peer-reviewed papers published in NeurIPS, ICML, and ACL proceedings."
                ),
                technologies = listOf("PyTorch", "Python", "LaTeX", "CUDA", "TensorFlow")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Ph.D. in Computer Science",
                fieldOfStudy = "Artificial Intelligence & Safety",
                institution = "Stanford University",
                startDate = "2008",
                endDate = "2013",
                gradeOrGpa = "3.96 / 4.0 (Distinction)",
                relevantCoursework = "Provable Safety Guarantees & Bias Mitigation in Deep Generative Models",
                description = "Dissertation: Ethical Alignment Benchmarks in Distributed Machine Learning"
            ),
            TemplateEducationData(
                degree = "B.S. in Applied Mathematics & Computer Science",
                fieldOfStudy = "Applied Mathematics",
                institution = "UC Berkeley",
                startDate = "2004",
                endDate = "2008",
                gradeOrGpa = "3.92 / 4.0 (Summa Cum Laude)"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Open-Align LLM Safety Framework",
                roleInProject = "Principal Investigator",
                startDate = "2021",
                endDate = "Present",
                description = "Open-source research framework evaluating safety benchmarks across open-weight language models.",
                technologiesUsed = listOf("PyTorch", "CUDA", "LaTeX", "Python"),
                projectLink = "github.com/jsterling/open-align"
            ),
            TemplateProjectData(
                projectName = "Neural Bias Auditing Benchmark",
                roleInProject = "Lead Researcher",
                startDate = "2019",
                endDate = "2021",
                description = "Benchmark dataset for auditing demographic bias in multimodal foundation models.",
                technologiesUsed = listOf("Python", "HuggingFace", "TensorFlow"),
                projectLink = "github.com/jsterling/bias-bench"
            )
        ),
        skills = listOf("Machine Learning", "PyTorch", "NLP", "Peer Review", "Grant Writing", "Python", "LaTeX"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Academic Research", listOf("Deep Learning", "NLP", "Algorithmic Ethics", "Peer-Reviewed Publishing")),
            TemplateSkillCategoryGroup("Technical Stack", listOf("PyTorch", "Python", "LaTeX", "CUDA", "TensorFlow")),
            TemplateSkillCategoryGroup("Pedagogy & Grants", listOf("Grant Writing", "Curriculum Design", "Ph.D. Advising"))
        ),
        certifications = listOf(
            TemplateCertData("NSF Principal Investigator Qualification", "National Science Foundation", "2018")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("German", "Fluent")
        ),
        references = listOf(
            TemplateReferenceData("Dr. Alan Turing Jr.", "Department Chair", "Stanford CS", "a.turing@stanford.edu • +1 (555) 789-9900", "a.turing@stanford.edu", "+1 (555) 789-9900"),
            TemplateReferenceData("Dr. Eleanor Vance", "Director of AI Safety", "MIT CSAIL", "e.vance@csail.mit.edu • +1 (555) 456-1122", "e.vance@csail.mit.edu", "+1 (555) 456-1122")
        ),
        hobbies = listOf("Mountain Climbing", "Violin Performance", "Classical Philosophy"),
        volunteerWork = listOf("Reviewer for NeurIPS and ICML Conferences (2016 - Present)"),
        awards = listOf("NSF CAREER Award (2019)", "Best Paper Award at NeurIPS (2020)")
    )

    /** 8. Female Federal & Governance Specialist */
    val FEMALE_FEDERAL_SPECIALIST = TemplateData(
        fullName = "Sarah Jenkins",
        professionalTitle = "Senior Federal Operations & Audit Director",
        email = "sarah.jenkins@example-gov.org",
        phone = "+1 (555) 210-9876",
        location = "Washington, DC • Secret Clearance",
        summary = "Accomplished Public Sector Director with 11+ years managing federal compliance, regulatory oversight, and large-scale government modernization programs. Expert in FISMA, NIST guidelines, and public policy.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_female_whitebg_01",
        dateOfBirth = "11/12/1985",
        yearsOfExperience = "11+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Senior Operations Director",
                company = "Federal Technology Management Agency",
                startDate = "01/2018",
                endDate = "Present",
                location = "Washington, DC",
                responsibilities = listOf(
                    "Directed $80M federal IT modernization grant, ensuring 100% compliance with NIST SP 800-53 security controls.",
                    "Supervised audit readiness evaluations for 14 civilian federal agencies, eliminating key compliance findings."
                )
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Master of Public Administration (MPA)",
                institution = "Georgetown University",
                startDate = "2007",
                endDate = "2009"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Federal Audit Readiness Initiative",
                roleInProject = "Program Director",
                startDate = "2019",
                endDate = "2021",
                description = "Modernized compliance tracking system across civilian agencies, reducing audit cycle duration by 40%."
            )
        ),
        skills = listOf("Federal Governance", "NIST Compliance", "FISMA", "Audit Readiness", "Public Policy", "Program Management"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Governance & Compliance", listOf("NIST SP 800-53", "FISMA", "Federal Audit Readiness", "Public Policy")),
            TemplateSkillCategoryGroup("Program Leadership", listOf("Budget Management", "Inter-Agency Coordination", "Regulatory Oversight")),
            TemplateSkillCategoryGroup("Security Clearance", listOf("Secret Clearance", "Public Trust", "Government Procurement"))
        ),
        certifications = listOf(
            TemplateCertData("Certified Government Financial Manager (CGFM)", "AGA", "2015"),
            TemplateCertData("Project Management Professional (PMP)", "PMI", "2017")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native")
        ),
        references = listOf(
            TemplateReferenceData("Hon. Robert Vance", "Deputy Director", "Federal Tech Agency", "r.vance@ftma.gov")
        ),
        hobbies = listOf("Historical Biography Reading", "Equestrian Sports", "Community Gardening"),
        volunteerWork = listOf("President of Washington Public Leadership Association (2021 - Present)"),
        awards = listOf("Federal Executive Leadership Award (2021)")
    )

    /** 9. Fresh Graduate Computer Science Engineer */
    val FRESH_GRADUATE_ENGINEER = TemplateData(
        fullName = "Liam Sterling",
        professionalTitle = "Software Engineering Graduate & Mobile Developer",
        email = "liam.sterling@example-grad.edu",
        phone = "+1 (555) 321-7654",
        location = "Seattle, WA • Entry Level",
        summary = "Motivated Computer Science Honor Graduate with strong foundation in Kotlin, Android SDK, and Data Structures. Seeking an entry-level Software Engineer role to leverage capstone project experience and passion for mobile apps.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_male_bluebg",
        dateOfBirth = "18/02/2002",
        yearsOfExperience = "Fresh Graduate",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Software Engineering Intern",
                company = "TechStart Incubator",
                startDate = "06/2023",
                endDate = "09/2023",
                location = "Seattle, WA",
                responsibilities = listOf(
                    "Developed 4 responsive Jetpack Compose screens for real-time fitness tracking mobile application.",
                    "Collaborated with senior engineers to write unit tests using JUnit5 and Mockito, achieving 88% coverage."
                ),
                technologies = listOf("Kotlin", "Jetpack Compose", "JUnit5", "Git")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "B.S. in Computer Science (Summa Cum Laude)",
                institution = "University of Washington",
                startDate = "2020",
                endDate = "2024",
                gradeOrGpa = "3.92 / 4.0",
                relevantCoursework = "Data Structures & Algorithms, Mobile App Development, Operating Systems, Software Testing"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Campus Navigation & Event Finder App",
                roleInProject = "Lead Developer (Senior Capstone)",
                startDate = "2023",
                endDate = "2024",
                description = "Built native Android application using Clean Architecture and Room DB, serving 3,200 active campus students.",
                technologiesUsed = listOf("Kotlin", "Jetpack Compose", "Room", "Hilt", "Google Maps API")
            ),
            TemplateProjectData(
                projectName = "Algorithmic Code Visualizer",
                roleInProject = "Personal Project",
                startDate = "2023",
                endDate = "2023",
                description = "Interactive web application visualizing sorting and graph traversal algorithms step-by-step.",
                technologiesUsed = listOf("TypeScript", "React", "Canvas API")
            )
        ),
        skills = listOf("Kotlin", "Java", "Jetpack Compose", "Data Structures", "Git", "Clean Architecture", "SQL"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Languages & Frameworks", listOf("Kotlin", "Java", "TypeScript", "Jetpack Compose", "Android SDK")),
            TemplateSkillCategoryGroup("Core Foundations", listOf("Data Structures & Algorithms", "Object-Oriented Design", "Clean Architecture")),
            TemplateSkillCategoryGroup("Tools & Databases", listOf("Git & GitHub", "Room DB", "SQLite", "Android Studio"))
        ),
        certifications = listOf(
            TemplateCertData("Google Associate Android Developer", "Google Developers", "2024")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("Spanish", "Conversational")
        ),
        references = listOf(
            TemplateReferenceData("Prof. Michael Vance", "Chair of Computer Science", "UW", "m.vance@cs.uw.edu")
        ),
        hobbies = listOf("Competitive Hackathons", "Open Source Contributing", "Robotics Club Lead"),
        volunteerWork = listOf("Peer Computer Science Tutor at UW Academic Center (2022 - 2024)"),
        awards = listOf("Dean's High Honors List (8 Consecutive Semesters)", "1st Place Campus Hackathon Winner (2023)")
    )

    /** 10. Architectural & Spatial Designer */
    val ARCHITECTURAL_DESIGNER = TemplateData(
        fullName = "Mateo Rossi",
        professionalTitle = "Principal Architect & Spatial Studio Director",
        email = "mateo.rossi@example-arch.com",
        phone = "+1 (555) 912-3456",
        location = "Chicago, IL • Registered Architect",
        summary = "Innovative Licensed Architect with 9+ years directing sustainable urban design projects, commercial high-rise developments, and BIM workflow integration. Passionate about eco-friendly materials and spatial aesthetics.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_male_whitebg_01",
        dateOfBirth = "12/08/1987",
        yearsOfExperience = "9+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Principal Architect & Studio Director",
                company = "Rossi & Vanguard Architectural Studio",
                startDate = "03/2019",
                endDate = "Present",
                location = "Chicago, IL",
                responsibilities = listOf(
                    "Lead architect for $42M LEED Gold certified commercial high-rise in downtown Chicago.",
                    "Directed 14 structural engineers, interior designers, and BIM draftsmen using Revit, Rhino 3D, and AutoCAD.",
                    "Pioneered net-zero solar facade integration reducing annual building energy consumption by 35%."
                ),
                technologies = listOf("Revit", "Rhino 3D", "BIM Management", "LEED Gold", "AutoCAD", "V-Ray")
            ),
            TemplateExperienceData(
                jobTitle = "Senior Project Architect",
                company = "SOM (Skidmore, Owings & Merrill)",
                startDate = "06/2014",
                endDate = "02/2019",
                location = "Chicago, IL",
                responsibilities = listOf(
                    "Managed structural design phases for 3 municipal civic centers and mixed-use urban complexes.",
                    "Supervised building code compliance, zoning permits, and structural engineering reviews."
                ),
                technologies = listOf("AutoCAD", "Rhino 3D", "Grasshopper", "3ds Max", "Building Codes")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Master of Architecture (M.Arch)",
                fieldOfStudy = "Sustainable Urban Design",
                institution = "Illinois Institute of Technology (IIT)",
                startDate = "2010",
                endDate = "2013",
                gradeOrGpa = "3.92 / 4.0 (Honors)",
                description = "Thesis: Sustainable High-Density Urban Living & Biomimetic Facades"
            ),
            TemplateEducationData(
                degree = "B.S. in Architectural Studies",
                fieldOfStudy = "Architectural Engineering",
                institution = "University of Illinois Urbana-Champaign",
                startDate = "2006",
                endDate = "2010",
                gradeOrGpa = "3.86 / 4.0 (Magna Cum Laude)"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Chicago Riverfront Sustainable Pavilion",
                roleInProject = "Lead Architect & Designer",
                startDate = "2021",
                endDate = "2023",
                description = "Designed net-zero solar pavilion featuring recycled timber frameworks and rainwater harvesting systems.",
                technologiesUsed = listOf("Revit", "Grasshopper", "V-Ray", "Timber Construction"),
                projectLink = "rossi-arch.com/riverfront"
            ),
            TemplateProjectData(
                projectName = "High-Density Eco Living Complex",
                roleInProject = "Principal Spatial Designer",
                startDate = "2019",
                endDate = "2021",
                description = "120-unit residential eco-complex featuring passive solar heating and modular vertical gardens.",
                technologiesUsed = listOf("Rhino 3D", "Solar Simulation", "AutoCAD"),
                projectLink = "rossi-arch.com/eco-living"
            )
        ),
        skills = listOf("Architectural Design", "Revit BIM", "Rhino 3D", "Urban Planning", "LEED AP", "Construction Management"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Architectural Software", listOf("Autodesk Revit", "Rhino 3D + Grasshopper", "AutoCAD", "V-Ray Rendering")),
            TemplateSkillCategoryGroup("Design Specialties", listOf("Sustainable Urban Planning", "LEED Gold Certification", "Structural Drafting")),
            TemplateSkillCategoryGroup("Project Management", listOf("Building Code Compliance", "Contractor Supervision", "Cost Estimating"))
        ),
        certifications = listOf(
            TemplateCertData("NCARB Certified Architect", "NCARB", "2016"),
            TemplateCertData("LEED AP Building Design + Construction", "USGBC", "2018"),
            TemplateCertData("Licensed Structural Architect", "State of Illinois", "2017")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("Italian", "Fluent")
        ),
        references = listOf(
            TemplateReferenceData("Helena Vance", "Senior Partner", "Vanguard Studio", "h.vance@vanguardarch.com • +1 (555) 912-8800", "h.vance@vanguardarch.com", "+1 (555) 912-8800"),
            TemplateReferenceData("Marcus Sterling", "VP of Urban Design", "SOM Chicago", "m.sterling@som.com • +1 (555) 345-6789", "m.sterling@som.com", "+1 (555) 345-6789")
        ),
        hobbies = listOf("Architectural Photography", "Sketching & Drafting", "Woodworking Mechanics"),
        volunteerWork = listOf("Volunteer for Habitat for Humanity Architectural Planning (2018 - Present)"),
        awards = listOf("Chicago AIA Architectural Excellence Award (2022)")
    )
}
