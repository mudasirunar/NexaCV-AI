package com.mudasir.nexacvai

import androidx.lifecycle.SavedStateHandle
import com.mudasir.nexacvai.domain.model.*
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.domain.usecase.GetProfileUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeUserProfileRepository
    private lateinit var getProfileUseCase: GetProfileUseCase
    private lateinit var saveProfileUseCase: SaveProfileUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeUserProfileRepository()
        getProfileUseCase = GetProfileUseCase(fakeRepository)
        saveProfileUseCase = SaveProfileUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(profileId: Long = -1L): CreateProfileViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("profileId" to profileId))
        return CreateProfileViewModel(
            saveProfileUseCase = saveProfileUseCase,
            getProfileUseCase = getProfileUseCase,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun testInitialState_withoutProfileId_createsEmptyState() {
        val viewModel = createViewModel()
        val state = viewModel.state.value

        assertNull(state.profileId)
        assertEquals(0, state.currentStep)
        assertEquals(5, state.totalSteps)
        assertEquals("", state.fullName)
        assertEquals("", state.professionalTitle)
        assertEquals(listOf(""), state.emails)
        assertEquals(listOf(""), state.phones)
        assertFalse(state.isLoading)
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
    }

    @Test
    fun testLoadProfile_withValidProfileId_loadsProfileData() = runTest {
        val existingProfile = UserProfile(
            id = 42L,
            fullName = "John Doe",
            professionalTitle = "Android Developer",
            emails = listOf("john@example.com"),
            phones = listOf("12345678"),
            professionalSummary = "Experienced developer",
            skills = listOf("Kotlin", "Compose")
        )
        fakeRepository.profiles[42L] = existingProfile

        val viewModel = createViewModel(42L)
        
        // Wait for coroutine inside init to finish loadProfile
        testScheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(42L, state.profileId)
        assertEquals("John Doe", state.fullName)
        assertEquals("Android Developer", state.professionalTitle)
        assertEquals(listOf("john@example.com"), state.emails)
        assertEquals(listOf("12345678"), state.phones)
        assertEquals("Experienced developer", state.professionalSummary)
        assertEquals(listOf("Kotlin", "Compose"), state.skills)
        assertFalse(state.isLoading)
    }

    @Test
    fun testLoadProfile_notFound_setsErrorState() = runTest {
        val viewModel = createViewModel(99L)
        testScheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Profile not found", state.error)
    }

    @Test
    fun testStepNavigation_boundsAndTransitions() {
        val viewModel = createViewModel()
        
        // Initial step is 0
        assertEquals(0, viewModel.state.value.currentStep)

        // Previous step at 0 should stay at 0
        viewModel.previousStep()
        assertEquals(0, viewModel.state.value.currentStep)

        // Next step increments step
        viewModel.nextStep()
        assertEquals(1, viewModel.state.value.currentStep)

        // Test setStep within bounds
        viewModel.setStep(3)
        assertEquals(3, viewModel.state.value.currentStep)

        // Test setStep out of bounds (should ignore)
        viewModel.setStep(10)
        assertEquals(3, viewModel.state.value.currentStep)
        viewModel.setStep(-1)
        assertEquals(3, viewModel.state.value.currentStep)

        // Next step up to max steps (totalSteps = 5, max index = 4)
        viewModel.nextStep() // to 4
        assertEquals(4, viewModel.state.value.currentStep)
        viewModel.nextStep() // should stay at 4
        assertEquals(4, viewModel.state.value.currentStep)
    }

    @Test
    fun testStep1_BasicInfoUpdate() {
        val viewModel = createViewModel()

        viewModel.updateBasicInfo(
            fullName = "Alice Smith",
            title = "QA Engineer",
            dateOfBirth = "01/01/1990",
            address = "New York",
            yearsOfExperience = "3",
            profilePictureUri = "content://image"
        )

        val state = viewModel.state.value
        assertEquals("Alice Smith", state.fullName)
        assertEquals("QA Engineer", state.professionalTitle)
        assertEquals("01/01/1990", state.dateOfBirth)
        assertEquals("New York", state.address)
        assertEquals("3", state.yearsOfExperience)
        assertEquals("content://image", state.profilePictureUri)

        // Remove profile picture
        viewModel.removeProfilePicture()
        assertNull(viewModel.state.value.profilePictureUri)
    }

    @Test
    fun testStep1_EmailsListManipulation() {
        val viewModel = createViewModel()
        
        // Initial email field is [""]
        assertEquals(listOf(""), viewModel.state.value.emails)

        // Update first email
        viewModel.updateEmail(0, "alice@example.com")
        assertEquals(listOf("alice@example.com"), viewModel.state.value.emails)

        // Add email field
        viewModel.addEmailField()
        assertEquals(listOf("alice@example.com", ""), viewModel.state.value.emails)

        // Update second email
        viewModel.updateEmail(1, "alice.work@example.com")
        assertEquals(listOf("alice@example.com", "alice.work@example.com"), viewModel.state.value.emails)

        // Add third email field
        viewModel.addEmailField()
        assertEquals(listOf("alice@example.com", "alice.work@example.com", ""), viewModel.state.value.emails)

        // Maximum email limit is 3, trying to add a 4th should be ignored
        viewModel.addEmailField()
        assertEquals(3, viewModel.state.value.emails.size)

        // Remove an email field
        viewModel.removeEmailField(1)
        assertEquals(listOf("alice@example.com", ""), viewModel.state.value.emails)

        // Cannot remove last remaining field if size is 1
        viewModel.removeEmailField(0) // removes index 0, list becomes [""]
        assertEquals(listOf(""), viewModel.state.value.emails)
        viewModel.removeEmailField(0) // should not remove since size is 1
        assertEquals(listOf(""), viewModel.state.value.emails)
    }

    @Test
    fun testStep1_PhonesListManipulation() {
        val viewModel = createViewModel()
        
        // Initial phone field is [""]
        assertEquals(listOf(""), viewModel.state.value.phones)

        // Update first phone
        viewModel.updatePhone(0, "111-222")
        assertEquals(listOf("111-222"), viewModel.state.value.phones)

        // Add phone field
        viewModel.addPhoneField()
        assertEquals(listOf("111-222", ""), viewModel.state.value.phones)

        // Update second phone
        viewModel.updatePhone(1, "333-444")
        assertEquals(listOf("111-222", "333-444"), viewModel.state.value.phones)

        // Add third phone field
        viewModel.addPhoneField()
        assertEquals(listOf("111-222", "333-444", ""), viewModel.state.value.phones)

        // Maximum phone limit is 3, trying to add a 4th should be ignored
        viewModel.addPhoneField()
        assertEquals(3, viewModel.state.value.phones.size)

        // Remove a phone field
        viewModel.removePhoneField(1)
        assertEquals(listOf("111-222", ""), viewModel.state.value.phones)
    }

    @Test
    fun testStep2_ProfessionalSnapshotAndSkills() {
        val viewModel = createViewModel()

        // Update professional summary, should capitalize first letter
        viewModel.updateSummary("hello world summary")
        assertEquals("Hello world summary", viewModel.state.value.professionalSummary)

        // Update skill input field state
        viewModel.updateSkillInput("Kotlin")
        assertEquals("Kotlin", viewModel.state.value.currentSkillInput)

        // Add skill
        viewModel.addSkill("Kotlin")
        assertTrue(viewModel.state.value.skills.contains("Kotlin"))
        assertEquals("", viewModel.state.value.currentSkillInput) // Reset input

        // Add duplicate skill (should be ignored)
        viewModel.addSkill("Kotlin")
        assertEquals(1, viewModel.state.value.skills.size)

        // Add blank skill (should be ignored)
        viewModel.addSkill("   ")
        assertEquals(1, viewModel.state.value.skills.size)

        // Add another skill
        viewModel.addSkill(" Compose ") // should be trimmed
        assertEquals(listOf("Kotlin", "Compose"), viewModel.state.value.skills)

        // Remove skill
        viewModel.removeSkill("Kotlin")
        assertEquals(listOf("Compose"), viewModel.state.value.skills)
    }

    @Test
    fun testStep3_ExperiencesAndProjects() {
        val viewModel = createViewModel()

        val experience = Experience(
            jobTitle = "Software Engineer",
            companyName = "Tech Corp"
        )
        
        // Add Experience
        viewModel.addExperience(experience)
        assertEquals(listOf(experience), viewModel.state.value.experiences)

        // Update Experience
        val updatedExperience = experience.copy(jobTitle = "Senior Software Engineer")
        viewModel.updateExperience(experience.id, updatedExperience)
        assertEquals("Senior Software Engineer", viewModel.state.value.experiences.first().jobTitle)

        // Remove Experience
        viewModel.removeExperience(experience)
        assertTrue(viewModel.state.value.experiences.isEmpty())

        // Add Project
        val project = Project(
            projectName = "NexaCV AI",
            description = "AI Resume Builder"
        )
        viewModel.addProject(project)
        assertEquals(listOf(project), viewModel.state.value.projects)

        // Update Project
        val updatedProject = project.copy(projectName = "NexaCV AI NextGen")
        viewModel.updateProject(project.id, updatedProject)
        assertEquals("NexaCV AI NextGen", viewModel.state.value.projects.first().projectName)

        // Remove Project
        viewModel.removeProject(project)
        assertTrue(viewModel.state.value.projects.isEmpty())
    }

    @Test
    fun testStep4_EducationsAndCertifications() {
        val viewModel = createViewModel()

        val education = Education(
            degree = "B.S. Computer Science",
            instituteName = "State University"
        )
        
        // Add Education
        viewModel.addEducation(education)
        assertEquals(listOf(education), viewModel.state.value.educations)

        // Update Education
        val updatedEducation = education.copy(degree = "M.S. Computer Science")
        viewModel.updateEducation(education.id, updatedEducation)
        assertEquals("M.S. Computer Science", viewModel.state.value.educations.first().degree)

        // Remove Education
        viewModel.removeEducation(education)
        assertTrue(viewModel.state.value.educations.isEmpty())

        // Add Certification
        val certification = Certification(
            certificationName = "Google Associate Android Developer",
            issuingOrganization = "Google"
        )
        viewModel.addCertification(certification)
        assertEquals(listOf(certification), viewModel.state.value.certifications)

        // Update Certification
        val updatedCertification = certification.copy(certificationName = "Google Professional Android Developer")
        viewModel.updateCertification(certification.id, updatedCertification)
        assertEquals("Google Professional Android Developer", viewModel.state.value.certifications.first().certificationName)

        // Remove Certification
        viewModel.removeCertification(certification)
        assertTrue(viewModel.state.value.certifications.isEmpty())
    }

    @Test
    fun testStep5_SocialLinksLanguagesReferencesAndExtras() {
        val viewModel = createViewModel()

        val link = SocialLink(label = "GitHub", url = "github.com")
        viewModel.addSocialLink(link)
        assertEquals(listOf(link), viewModel.state.value.socialLinks)

        val updatedLink = link.copy(url = "github.com/user")
        viewModel.updateSocialLink(link.id, updatedLink)
        assertEquals("github.com/user", viewModel.state.value.socialLinks.first().url)

        viewModel.removeSocialLink(link)
        assertTrue(viewModel.state.value.socialLinks.isEmpty())

        val lang = Language(languageName = "English", proficiency = "Native")
        viewModel.addLanguage(lang)
        assertEquals(listOf(lang), viewModel.state.value.languages)

        val updatedLang = lang.copy(proficiency = "Fluent")
        viewModel.updateLanguage(lang.id, updatedLang)
        assertEquals("Fluent", viewModel.state.value.languages.first().proficiency)

        viewModel.removeLanguage(lang)
        assertTrue(viewModel.state.value.languages.isEmpty())

        val ref = Reference(fullName = "Dr. John", company = "University")
        viewModel.addReference(ref)
        assertEquals(listOf(ref), viewModel.state.value.references)

        val updatedRef = ref.copy(fullName = "Dr. John Smith")
        viewModel.updateReference(ref.id, updatedRef)
        assertEquals("Dr. John Smith", viewModel.state.value.references.first().fullName)

        viewModel.removeReference(ref)
        assertTrue(viewModel.state.value.references.isEmpty())

        // Update Additional Info
        viewModel.updateAdditionalInfo(
            hobbies = "Reading, Coding",
            volunteerWork = "Open Source Contribution",
            awards = "Best Developer Award 2026"
        )
        val state = viewModel.state.value
        assertEquals("Reading, Coding", state.hobbies)
        assertEquals("Open Source Contribution", state.volunteerWork)
        assertEquals("Best Developer Award 2026", state.awards)
    }

    @Test
    fun testUnsavedChangesDetection() {
        val viewModel = createViewModel()
        
        // Initial state matches an empty UserProfile
        assertFalse(viewModel.hasUnsavedChanges())

        // Modify a field
        viewModel.updateBasicInfo(fullName = "New Name")
        assertTrue(viewModel.hasUnsavedChanges())

        // Reset field to match empty profile
        viewModel.updateBasicInfo(fullName = "")
        assertFalse(viewModel.hasUnsavedChanges())
    }

    @Test
    fun testSaveProfile_validationError_whenNameIsBlank() {
        val viewModel = createViewModel()
        
        // Full name is blank initially
        viewModel.saveProfile()

        val state = viewModel.state.value
        assertEquals("Name is required", state.error)
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
    }

    @Test
    fun testSaveProfile_success_insertsProfile() = runTest {
        val viewModel = createViewModel()

        viewModel.updateBasicInfo(fullName = "John Doe", title = "Developer")
        viewModel.saveProfile()

        // Wait for coroutine saving operation to finish
        testScheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isSaving)
        assertTrue(state.isSaved)
        assertNotNull(state.profileId)
        assertEquals(1L, state.profileId)

        // Verify it was written to fake repository
        val profileInDb = fakeRepository.profiles[1L]
        assertNotNull(profileInDb)
        assertEquals("John Doe", profileInDb?.fullName)
        assertEquals("Developer", profileInDb?.professionalTitle)

        // Once saved, hasUnsavedChanges should reset to false
        assertFalse(viewModel.hasUnsavedChanges())
    }

    @Test
    fun testSaveProfile_fails_setsError() = runTest {
        val viewModel = createViewModel()
        viewModel.updateBasicInfo(fullName = "John Doe")

        // Make repository fail on insert
        fakeRepository.shouldThrowError = true

        viewModel.saveProfile()
        testScheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isSaving)
        assertFalse(state.isSaved)
        assertEquals("Database insert failed", state.error)
    }

    class FakeUserProfileRepository : UserProfileRepository {
        val profiles = mutableMapOf<Long, UserProfile>()
        var nextId = 1L
        var shouldThrowError = false
        
        override fun getAllProfiles(): Flow<List<UserProfile>> {
            return flowOf(profiles.values.toList())
        }
        
        override suspend fun getProfileById(id: Long): UserProfile? {
            return profiles[id]
        }
        
        override suspend fun insertProfile(profile: UserProfile): Long {
            if (shouldThrowError) throw Exception("Database insert failed")
            val id = nextId++
            val savedProfile = profile.copy(id = id)
            profiles[id] = savedProfile
            return id
        }
        
        override suspend fun updateProfile(profile: UserProfile) {
            if (shouldThrowError) throw Exception("Database update failed")
            profiles[profile.id] = profile
        }
        
        override suspend fun deleteProfile(profile: UserProfile) {
            profiles.remove(profile.id)
        }
    }
}
