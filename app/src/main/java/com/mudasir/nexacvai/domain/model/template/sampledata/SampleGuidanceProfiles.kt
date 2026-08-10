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
                technologiesUsed = listOf("Kotlin", "Canvas", "PdfDocument", "KSP", "Hilt")
            ),
            TemplateProjectData(
                projectName = "Enterprise Cloud Storage Vault",
                roleInProject = "Core Infrastructure Lead",
                startDate = "2022",
                endDate = "2023",
                description = "End-to-end encrypted document storage pipeline handling over 5TB of encrypted asset backups.",
                technologiesUsed = listOf("Kotlin", "Ktor", "AWS S3", "SQLCipher")
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
            TemplateReferenceData("David Vance", "VP of Engineering", "Apex Financial", "david.vance@apexfin.com"),
            TemplateReferenceData("Sarah Lin", "Director of Product", "Nexus Cloud", "slin@nexuscloud.io")
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
        summary = "Dedicated Physician and Clinical Director with over 9 years of medical experience in internal medicine, hospital administration, and clinical research. Board Certified with a passion for healthcare innovation.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_female_whitebg_01",
        dateOfBirth = "03/11/1988",
        yearsOfExperience = "9+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Chief Clinical Specialist",
                company = "Chicago Memorial Hospital",
                startDate = "04/2019",
                endDate = "Present",
                location = "Chicago, IL",
                responsibilities = listOf(
                    "Oversaw daily medical operations of 120-bed acute care unit, managing 35 physicians and nursing staff.",
                    "Implemented digitized electronic health record (EHR) workflow, reducing patient intake latency by 32%.",
                    "Published 6 peer-reviewed clinical research papers on hospital-acquired infection reduction."
                ),
                technologies = listOf("Clinical Medicine", "Hospital Management", "EHR Systems", "Patient Care")
            ),
            TemplateExperienceData(
                jobTitle = "Attending Physician & Clinical Fellow",
                company = "Northwestern Medicine Medical Center",
                startDate = "07/2015",
                endDate = "03/2019",
                location = "Chicago, IL",
                responsibilities = listOf(
                    "Provided comprehensive diagnostic and therapeutic care for complex internal medicine cases.",
                    "Supervised medical residents and clinical fellows in diagnostic rounds and patient evaluations."
                )
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Doctor of Medicine (M.D.)",
                institution = "Northwestern University Feinberg School of Medicine",
                startDate = "2011",
                endDate = "2015",
                gradeOrGpa = "Magna Cum Laude",
                relevantCoursework = "Internal Medicine, Clinical Diagnostics, Bioethics"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Clinical Infection Prevention Protocol",
                roleInProject = "Principal Medical Investigator",
                startDate = "2020",
                endDate = "2022",
                description = "Standardized hospital-wide disinfection protocols, achieving a zero-infection record across 18 surgical suites."
            )
        ),
        skills = listOf("Clinical Medicine", "Internal Medicine", "Patient Diagnosis", "EHR Systems", "Hospital Administration", "Medical Research"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Medical Expertise", listOf("Internal Medicine", "Diagnostic Care", "Patient Triage", "Emergency Response")),
            TemplateSkillCategoryGroup("Clinical Leadership", listOf("Hospital Administration", "EHR Systems", "Staff Supervision")),
            TemplateSkillCategoryGroup("Research & Ethics", listOf("Peer-Reviewed Publishing", "Bioethics", "Clinical Trials"))
        ),
        certifications = listOf(
            TemplateCertData("Board Certified in Internal Medicine", "American Board of Internal Medicine", "2018"),
            TemplateCertData("Advanced Cardiovascular Life Support (ACLS)", "American Heart Association", "2023")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("Mandarin Chinese", "Fluent")
        ),
        references = listOf(
            TemplateReferenceData("Dr. Robert Sterling, MD", "Chief of Medicine", "Chicago Memorial", "r.sterling@cmh.org"),
            TemplateReferenceData("Dr. Karen White, MD", "Director of Residency", "Northwestern Medicine", "k.white@nm.org")
        ),
        hobbies = listOf("Classical Piano", "Medical History Research", "Yoga & Wellness"),
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
                    "Designed zero-trust IAM architecture securing multi-cloud environment across AWS, Azure, and GCP.",
                    "Led red team penetration tests and vulnerability remediations for enterprise infrastructure.",
                    "Automated SOC threat intelligence pipelines using Python, SIEM, and AWS GuardDuty."
                ),
                technologies = listOf("Zero-Trust", "Kubernetes Security", "SIEM", "Python", "Penetration Testing")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "B.S. in Cybersecurity & Information Assurance",
                institution = "University of Washington",
                startDate = "2013",
                endDate = "2017",
                gradeOrGpa = "3.85 / 4.0"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Zero-Trust Mesh Shield",
                roleInProject = "Lead Security Architect",
                startDate = "2022",
                endDate = "2023",
                description = "Deployed Service Mesh mTLS encryption and micro-segmentation across 2,000+ microservices containers."
            )
        ),
        skills = listOf("Zero-Trust Architecture", "Penetration Testing", "SIEM & SOC", "AWS Security", "Kubernetes Security", "Python"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("Security Architecture", listOf("Zero-Trust Architecture", "IAM", "Cloud Security", "Kubernetes Security")),
            TemplateSkillCategoryGroup("Threat & Response", listOf("Penetration Testing", "SIEM", "SOC Analysis", "Incident Response")),
            TemplateSkillCategoryGroup("Security Engineering", listOf("Python", "Bash", "Terraform", "Docker Security"))
        ),
        certifications = listOf(
            TemplateCertData("Certified Information Systems Security Professional (CISSP®)", "ISC2", "2020"),
            TemplateCertData("Offensive Security Certified Professional (OSCP)", "OffSec", "2022")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("Mandarin", "Conversational")
        ),
        references = listOf(
            TemplateReferenceData("Victor Vance", "CISO", "CloudFort Sec", "v.vance@cloudfort.io")
        ),
        hobbies = listOf("CTF (Capture The Flag) Security Competitions", "Lockpicking Mechanics", "Hardware Hacking"),
        volunteerWork = listOf("EFF (Electronic Frontier Foundation) Supporter & Mentor"),
        awards = listOf("DEF CON CTF Top 10 Team Member (2022)")
    )

    /** 6. Female Creative UX Lead */
    val FEMALE_CREATIVE_UX_LEAD = TemplateData(
        fullName = "Amara Okafor",
        professionalTitle = "Staff Product Designer & UX Lead",
        email = "amara.okafor@example-design.com",
        phone = "+1 (555) 432-1098",
        location = "Los Angeles, CA • Creative Studio",
        summary = "Award-winning Product Designer with 6+ years creating human-centered digital experiences, design systems, and mobile interfaces. Passionate about accessibility, typography, and interactive design prototypes.",
        profilePictureUri = "android.resource://com.mudasir.nexacvai/drawable/profile_female_whitebg_02",
        dateOfBirth = "30/01/1993",
        yearsOfExperience = "6+ Years",
        experiences = listOf(
            TemplateExperienceData(
                jobTitle = "Staff Product Designer",
                company = "Lumina Design Studio",
                startDate = "09/2020",
                endDate = "Present",
                location = "Los Angeles, CA",
                responsibilities = listOf(
                    "Created comprehensive multi-brand Figma design system used by 60+ designers and developers.",
                    "Led UX usability testing sessions across 400+ international users to refine mobile app navigation flows."
                ),
                technologies = listOf("Figma", "Design Systems", "Prototyping", "UX Research", "Usability Testing")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "B.F.A. in Interaction Design & Digital Media",
                institution = "Rhode Island School of Design (RISD)",
                startDate = "2014",
                endDate = "2018",
                gradeOrGpa = "Honors"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Lumina UI Design System",
                roleInProject = "Lead Systems Architect",
                startDate = "2021",
                endDate = "2023",
                description = "Constructed 250+ accessible UI components in Figma with dark mode tokens and WCAG AAA contrast compliance."
            )
        ),
        skills = listOf("Figma", "Design Systems", "UX Research", "Interactive Prototyping", "UI Motion", "WCAG Accessibility"),
        skillCategoryGroups = listOf(
            TemplateSkillCategoryGroup("UX & Interaction", listOf("User Research", "Wireframing", "Interactive Prototyping", "Usability Testing")),
            TemplateSkillCategoryGroup("UI & Systems", listOf("Figma Design Systems", "Component Architecture", "Typography", "Motion UI")),
            TemplateSkillCategoryGroup("Design Standards", listOf("WCAG AAA Accessibility", "Design Tokens", "Design System Documentation"))
        ),
        certifications = listOf(
            TemplateCertData("Nielsen Norman Group UX Master Certification", "NN/g", "2021")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("Igbo", "Fluent")
        ),
        references = listOf(
            TemplateReferenceData("Chloe Bennett", "Design Director", "Lumina Studio", "c.bennett@luminadesign.com")
        ),
        hobbies = listOf("Generative Canvas Art", "Photography", "Ceramics & Pottery"),
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
                company = "Stanford University Lab",
                startDate = "09/2017",
                endDate = "Present",
                location = "Stanford, CA",
                responsibilities = listOf(
                    "Principal Investigator on $2.8M NSF grant researching ethical LLM alignment and bias mitigation.",
                    "Taught graduate courses in Deep Learning and Natural Language Processing to 300+ students annually."
                )
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Ph.D. in Computer Science",
                institution = "Stanford University",
                startDate = "2008",
                endDate = "2013"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Open-Align LLM Research",
                roleInProject = "Principal Investigator",
                startDate = "2021",
                endDate = "Present",
                description = "Open-source research framework evaluating safety benchmarks across open-weight language models."
            )
        ),
        skills = listOf("Machine Learning", "PyTorch", "NLP", "Peer Review", "Grant Writing", "Python", "Teaching"),
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
            TemplateReferenceData("Dr. Alan Turing Jr.", "Department Chair", "Stanford CS", "a.turing@stanford.edu")
        ),
        hobbies = listOf("Mountain Climbing", "Violin", "Classical Philosophy"),
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
                jobTitle = "Principal Architect",
                company = "Rossi & Vanguard Architectural Studio",
                startDate = "03/2019",
                endDate = "Present",
                location = "Chicago, IL",
                responsibilities = listOf(
                    "Lead architect for $42M LEED Gold certified commercial high-rise in downtown Chicago.",
                    "Directed 14 structural engineers and draftsmen using Revit, Rhino 3D, and AutoCAD."
                ),
                technologies = listOf("Revit", "Rhino 3D", "BIM Management", "LEED Gold", "AutoCAD")
            )
        ),
        educations = listOf(
            TemplateEducationData(
                degree = "Master of Architecture (M.Arch)",
                institution = "Illinois Institute of Technology (IIT)",
                startDate = "2010",
                endDate = "2013",
                gradeOrGpa = "Honors"
            )
        ),
        projects = listOf(
            TemplateProjectData(
                projectName = "Chicago Riverfront Sustainable Pavilion",
                roleInProject = "Lead Architect & Designer",
                startDate = "2021",
                endDate = "2023",
                description = "Designed net-zero solar pavilion featuring recycled timber frameworks and rainwater harvesting systems."
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
            TemplateCertData("LEED AP Building Design + Construction", "USGBC", "2018")
        ),
        languages = listOf(
            TemplateLanguageData("English", "Native"),
            TemplateLanguageData("Italian", "Fluent")
        ),
        references = listOf(
            TemplateReferenceData("Helena Vance", "Senior Partner", "Vanguard Studio", "h.vance@vanguardarch.com")
        ),
        hobbies = listOf("Architectural Photography", "Sketching & Drafting", "Woodworking"),
        volunteerWork = listOf("Volunteer for Habitat for Humanity Architectural Planning (2018 - Present)"),
        awards = listOf("Chicago AIA Architectural Excellence Award (2022)")
    )
}
