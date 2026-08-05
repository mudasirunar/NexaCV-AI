package com.mudasir.nexacvai.profiletests

import com.mudasir.nexacvai.domain.model.Certification
import com.mudasir.nexacvai.domain.model.Education
import com.mudasir.nexacvai.domain.model.Experience
import com.mudasir.nexacvai.domain.model.Language
import com.mudasir.nexacvai.domain.model.Project
import com.mudasir.nexacvai.domain.model.Reference
import com.mudasir.nexacvai.domain.model.SocialLink
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.model.matchesSearchQuery
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserProfileSearchMatcherTest {

    private lateinit var fullyPopulatedProfile: UserProfile

    @Before
    fun setUp() {
        fullyPopulatedProfile = UserProfile(
            id = 1001L,
            fullName = "Mudasir Unar",
            professionalTitle = "Senior Staff Android Architect",
            professionalSummary = "Expert in Kotlin Multiplatform and Jetpack Compose",
            emails = listOf("mudasir.unar@nexacv.ai", "mudasir.work@gmail.com"),
            phones = listOf("+92-300-1234567", "+1-415-555-0199"),
            address = "742 Evergreen Terrace, San Francisco, CA",
            dateOfBirth = "1995-08-15",
            yearsOfExperience = "8 Years",
            skills = listOf("Jetpack Compose", "Clean Architecture", "Dagger Hilt", "KSP"),
            hobbies = "Open Source Contributing, Chess, Machine Learning",
            volunteerWork = "Code Mentor for Beginners, Local Tech Meetup Organizer",
            awards = "Google Developer Expert 2024, Top Innovator Award",
            sourceProfileName = "Master Profile Version A",
            experiences = listOf(
                Experience(
                    id = "exp-1",
                    companyName = "Nexa Technologies Inc",
                    jobTitle = "Lead Mobile Software Engineer",
                    location = "San Jose, California",
                    description = "Architected offline-first resume generator using Room and Coroutines"
                )
            ),
            educations = listOf(
                Education(
                    id = "edu-1",
                    instituteName = "Stanford University",
                    degree = "Master of Science",
                    fieldOfStudy = "Computer Science and AI",
                    grade = "3.9 GPA",
                    description = "Focused on Distributed Systems and Machine Learning Algorithms"
                )
            ),
            projects = listOf(
                Project(
                    id = "proj-1",
                    projectName = "NexaCV AI Generator",
                    roleInProject = "Lead Architect & UI Designer",
                    description = "ATS-optimized resume generator app built with Compose",
                    projectLink = "https://github.com/nexacv/nexacv-ai",
                    technologiesUsed = listOf("Kotlin", "Jetpack Compose", "Room", "Hilt")
                )
            ),
            certifications = listOf(
                Certification(
                    id = "cert-1",
                    certificationName = "Associate Android Developer",
                    issuingOrganization = "Google Developers Certification",
                    credentialUrl = "https://developers.google.com/credentials/cert-889922"
                )
            ),
            references = listOf(
                Reference(
                    id = "ref-1",
                    fullName = "Dr. Robert Vance",
                    jobTitle = "VP of Mobile Engineering",
                    company = "TechCorp Global",
                    email = "robert.vance@techcorp.com",
                    phone = "+1-650-555-0144",
                    linkedInUrl = "https://linkedin.com/in/robertvance-vp",
                    notes = "Highly recommended for leadership and architecture skills"
                )
            ),
            socialLinks = listOf(
                SocialLink(
                    id = "social-1",
                    label = "GitHub Portfolio",
                    url = "https://github.com/mudasirunar"
                )
            ),
            languages = listOf(
                Language(
                    id = "lang-1",
                    languageName = "Urdu",
                    proficiency = "Native Speaker"
                ),
                Language(
                    id = "lang-2",
                    languageName = "English",
                    proficiency = "Full Professional Proficiency"
                )
            )
        )
    }

    // --- Core Metadata Tests ---

    @Test
    fun matchesSearchQuery_blankQuery_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery(""))
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("   "))
    }

    @Test
    fun matchesSearchQuery_fullName_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Mudasir"))
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("unar"))
    }

    @Test
    fun matchesSearchQuery_professionalTitle_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Senior Staff Android Architect"))
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("architect"))
    }

    @Test
    fun matchesSearchQuery_professionalSummary_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Kotlin Multiplatform"))
    }

    @Test
    fun matchesSearchQuery_address_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("San Francisco"))
    }

    @Test
    fun matchesSearchQuery_dateOfBirth_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("1995-08-15"))
    }

    @Test
    fun matchesSearchQuery_yearsOfExperience_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("8 Years"))
    }

    @Test
    fun matchesSearchQuery_hobbies_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Chess"))
    }

    @Test
    fun matchesSearchQuery_volunteerWork_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Code Mentor"))
    }

    @Test
    fun matchesSearchQuery_awards_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Google Developer Expert"))
    }

    @Test
    fun matchesSearchQuery_sourceProfileName_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Master Profile Version A"))
    }

    // --- Primitive Lists Tests ---

    @Test
    fun matchesSearchQuery_email_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("mudasir.work@gmail.com"))
    }

    @Test
    fun matchesSearchQuery_phone_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("+92-300-1234567"))
    }

    @Test
    fun matchesSearchQuery_skills_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Dagger Hilt"))
    }

    // --- Experience Tests ---

    @Test
    fun matchesSearchQuery_experienceCompanyName_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Nexa Technologies"))
    }

    @Test
    fun matchesSearchQuery_experienceJobTitle_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Lead Mobile Software"))
    }

    @Test
    fun matchesSearchQuery_experienceLocation_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("San Jose"))
    }

    @Test
    fun matchesSearchQuery_experienceDescription_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("offline-first resume generator"))
    }

    // --- Education Tests ---

    @Test
    fun matchesSearchQuery_educationInstituteName_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Stanford University"))
    }

    @Test
    fun matchesSearchQuery_educationDegree_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Master of Science"))
    }

    @Test
    fun matchesSearchQuery_educationFieldOfStudy_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Computer Science and AI"))
    }

    @Test
    fun matchesSearchQuery_educationGrade_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("3.9 GPA"))
    }

    @Test
    fun matchesSearchQuery_educationDescription_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Distributed Systems"))
    }

    // --- Project Tests ---

    @Test
    fun matchesSearchQuery_projectName_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("NexaCV AI Generator"))
    }

    @Test
    fun matchesSearchQuery_projectRole_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Lead Architect & UI Designer"))
    }

    @Test
    fun matchesSearchQuery_projectDescription_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("ATS-optimized"))
    }

    @Test
    fun matchesSearchQuery_projectLink_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("github.com/nexacv"))
    }

    @Test
    fun matchesSearchQuery_projectTechnology_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Jetpack Compose"))
    }

    // --- Certification Tests ---

    @Test
    fun matchesSearchQuery_certificationName_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Associate Android Developer"))
    }

    @Test
    fun matchesSearchQuery_certificationIssuingOrganization_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Google Developers Certification"))
    }

    @Test
    fun matchesSearchQuery_certificationCredentialUrl_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("cert-889922"))
    }

    // --- Reference Tests ---

    @Test
    fun matchesSearchQuery_referenceFullName_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Robert Vance"))
    }

    @Test
    fun matchesSearchQuery_referenceJobTitle_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("VP of Mobile Engineering"))
    }

    @Test
    fun matchesSearchQuery_referenceCompany_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("TechCorp Global"))
    }

    @Test
    fun matchesSearchQuery_referenceEmail_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("robert.vance@techcorp.com"))
    }

    @Test
    fun matchesSearchQuery_referencePhone_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("+1-650-555-0144"))
    }

    @Test
    fun matchesSearchQuery_referenceLinkedInUrl_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("linkedin.com/in/robertvance"))
    }

    @Test
    fun matchesSearchQuery_referenceNotes_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("architecture skills"))
    }

    // --- Social Links & Languages Tests ---

    @Test
    fun matchesSearchQuery_socialLinkLabel_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("GitHub Portfolio"))
    }

    @Test
    fun matchesSearchQuery_socialLinkUrl_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("github.com/mudasirunar"))
    }

    @Test
    fun matchesSearchQuery_languageName_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Urdu"))
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("English"))
    }

    @Test
    fun matchesSearchQuery_languageProficiency_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("Full Professional Proficiency"))
    }

    // --- Case Insensitivity & No Match Tests ---

    @Test
    fun matchesSearchQuery_caseInsensitiveMatching_returnsTrue() {
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("MUDASIR"))
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("sTanFoRd"))
        assertTrue(fullyPopulatedProfile.matchesSearchQuery("gOoGlE"))
    }

    @Test
    fun matchesSearchQuery_nonExistentQuery_returnsFalse() {
        assertFalse(fullyPopulatedProfile.matchesSearchQuery("NonExistentXYZQuery123"))
        assertFalse(fullyPopulatedProfile.matchesSearchQuery("Quantum Physics"))
    }
}
