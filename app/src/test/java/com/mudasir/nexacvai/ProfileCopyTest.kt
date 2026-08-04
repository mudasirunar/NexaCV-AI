package com.mudasir.nexacvai

import android.content.ContextWrapper
import com.mudasir.nexacvai.domain.model.*
import com.mudasir.nexacvai.domain.usecase.DuplicateProfileUseCase
import com.mudasir.nexacvai.domain.usecase.DeleteProfileUseCase
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.domain.usecase.ImportProfileUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileExportManager
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.DuplicateProgressState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ProfilesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive test suite for the Profile Copy (Duplication) feature.
 * Tests cover:
 * - DuplicateProfileUseCase: deep copy logic, fresh IDs, timestamps, nested entity UUID regeneration
 * - ProfilesViewModel: single & batch duplication state transitions, selection mode auto-exit, dismiss
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileCopyTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    private lateinit var fakeRepository: ProfilesViewModelTest.FakeUserProfileRepository
    private lateinit var duplicateProfileUseCase: DuplicateProfileUseCase
    private lateinit var getAllProfilesUseCase: GetAllProfilesUseCase
    private lateinit var saveProfileUseCase: SaveProfileUseCase
    private lateinit var deleteProfileUseCase: DeleteProfileUseCase
    private lateinit var importProfileUseCase: ImportProfileUseCase
    private lateinit var deleteManager: ProfileDeleteManager
    private lateinit var exportManager: ProfileExportManager

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)
        fakeRepository = ProfilesViewModelTest.FakeUserProfileRepository()
        duplicateProfileUseCase = DuplicateProfileUseCase(fakeRepository)
        getAllProfilesUseCase = GetAllProfilesUseCase(fakeRepository)
        saveProfileUseCase = SaveProfileUseCase(fakeRepository)
        deleteProfileUseCase = DeleteProfileUseCase(fakeRepository)
        deleteManager = ProfileDeleteManager(deleteProfileUseCase, testScope)
        importProfileUseCase = ImportProfileUseCase(fakeRepository)
        exportManager = ProfileExportManager(testScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ProfilesViewModel {
        return ProfilesViewModel(
            getAllProfilesUseCase = getAllProfilesUseCase,
            saveProfileUseCase = saveProfileUseCase,
            importProfileUseCase = importProfileUseCase,
            duplicateProfileUseCase = duplicateProfileUseCase,
            profileDeleteManager = deleteManager,
            profileExportManager = exportManager
        )
    }

    // ──────────────────────────────────────────────
    // DuplicateProfileUseCase — Unit Tests
    // ──────────────────────────────────────────────

    @Test
    fun testDuplicate_assignsFreshAutoIncrementId() = runTest(testDispatcher) {
        val original = createRichProfile(id = 1L, name = "Alice")
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)

        assertNotEquals(0L, newId)
        assertNotEquals(original.id, newId)
        val saved = fakeRepository.savedProfiles[newId]
        assertNotNull(saved)
        assertEquals(newId, saved!!.id)
    }

    @Test
    fun testDuplicate_preservesFullNameExact() = runTest(testDispatcher) {
        val original = createRichProfile(id = 2L, name = "Bob Johnson")
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertEquals("Bob Johnson", copy.fullName)
    }

    @Test
    fun testDuplicate_assignsFreshTimestamps() = runTest(testDispatcher) {
        val originalCreated = 1000000000L
        val originalUpdated = 1000000100L
        val original = createRichProfile(id = 3L, name = "Charlie").copy(
            createdAt = originalCreated,
            updatedAt = originalUpdated
        )
        val mockContext = ContextWrapper(null)

        val beforeCopy = System.currentTimeMillis()
        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertNotEquals(originalCreated, copy.createdAt)
        assertNotEquals(originalUpdated, copy.updatedAt)
        assertTrue(copy.createdAt >= beforeCopy)
        assertTrue(copy.updatedAt >= beforeCopy)
        assertEquals(copy.createdAt, copy.updatedAt)
    }

    @Test
    fun testDuplicate_regeneratesExperienceUUIDs() = runTest(testDispatcher) {
        val exp = Experience(id = "exp-original-1", jobTitle = "SWE", companyName = "Google")
        val original = createRichProfile(id = 4L, name = "Diana").copy(experiences = listOf(exp))
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertEquals(1, copy.experiences.size)
        assertNotEquals("exp-original-1", copy.experiences[0].id)
        assertEquals("SWE", copy.experiences[0].jobTitle)
        assertEquals("Google", copy.experiences[0].companyName)
    }

    @Test
    fun testDuplicate_regeneratesProjectUUIDs() = runTest(testDispatcher) {
        val proj = Project(id = "proj-original-1", projectName = "NexaCV", description = "Resume builder")
        val original = createRichProfile(id = 5L, name = "Eve").copy(projects = listOf(proj))
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertEquals(1, copy.projects.size)
        assertNotEquals("proj-original-1", copy.projects[0].id)
        assertEquals("NexaCV", copy.projects[0].projectName)
        assertEquals("Resume builder", copy.projects[0].description)
    }

    @Test
    fun testDuplicate_regeneratesEducationUUIDs() = runTest(testDispatcher) {
        val edu = Education(id = "edu-original-1", degree = "BS CS", instituteName = "MIT")
        val original = createRichProfile(id = 6L, name = "Frank").copy(educations = listOf(edu))
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertEquals(1, copy.educations.size)
        assertNotEquals("edu-original-1", copy.educations[0].id)
        assertEquals("BS CS", copy.educations[0].degree)
        assertEquals("MIT", copy.educations[0].instituteName)
    }

    @Test
    fun testDuplicate_regeneratesCertificationUUIDs() = runTest(testDispatcher) {
        val cert = Certification(id = "cert-original-1", certificationName = "AWS Solutions Architect")
        val original = createRichProfile(id = 7L, name = "Grace").copy(certifications = listOf(cert))
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertEquals(1, copy.certifications.size)
        assertNotEquals("cert-original-1", copy.certifications[0].id)
        assertEquals("AWS Solutions Architect", copy.certifications[0].certificationName)
    }

    @Test
    fun testDuplicate_regeneratesReferenceUUIDs() = runTest(testDispatcher) {
        val ref = Reference(id = "ref-original-1", fullName = "John Manager", company = "Acme Inc")
        val original = createRichProfile(id = 8L, name = "Hank").copy(references = listOf(ref))
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertEquals(1, copy.references.size)
        assertNotEquals("ref-original-1", copy.references[0].id)
        assertEquals("John Manager", copy.references[0].fullName)
        assertEquals("Acme Inc", copy.references[0].company)
    }

    @Test
    fun testDuplicate_regeneratesSocialLinkUUIDs() = runTest(testDispatcher) {
        val link = SocialLink(id = "social-original-1", label = "LinkedIn", url = "https://linkedin.com/in/test")
        val original = createRichProfile(id = 9L, name = "Ivy").copy(socialLinks = listOf(link))
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertEquals(1, copy.socialLinks.size)
        assertNotEquals("social-original-1", copy.socialLinks[0].id)
        assertEquals("LinkedIn", copy.socialLinks[0].label)
        assertEquals("https://linkedin.com/in/test", copy.socialLinks[0].url)
    }

    @Test
    fun testDuplicate_regeneratesLanguageUUIDs() = runTest(testDispatcher) {
        val lang = Language(id = "lang-original-1", languageName = "English", proficiency = "Native")
        val original = createRichProfile(id = 10L, name = "Jack").copy(languages = listOf(lang))
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertEquals(1, copy.languages.size)
        assertNotEquals("lang-original-1", copy.languages[0].id)
        assertEquals("English", copy.languages[0].languageName)
        assertEquals("Native", copy.languages[0].proficiency)
    }

    @Test
    fun testDuplicate_copiesScalarFieldsExactly() = runTest(testDispatcher) {
        val original = createRichProfile(id = 11L, name = "Karen").copy(
            professionalTitle = "Senior Engineer",
            address = "123 Main St",
            yearsOfExperience = "8",
            professionalSummary = "Experienced developer",
            skills = listOf("Kotlin", "Android", "Compose"),
            emails = listOf("karen@example.com"),
            phones = listOf("+1234567890"),
            hobbies = "Photography",
            volunteerWork = "Code for Good",
            awards = "Best Contributor 2024"
        )
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertEquals("Karen", copy.fullName)
        assertEquals("Senior Engineer", copy.professionalTitle)
        assertEquals("123 Main St", copy.address)
        assertEquals("8", copy.yearsOfExperience)
        assertEquals("Experienced developer", copy.professionalSummary)
        assertEquals(listOf("Kotlin", "Android", "Compose"), copy.skills)
        assertEquals(listOf("karen@example.com"), copy.emails)
        assertEquals(listOf("+1234567890"), copy.phones)
        assertEquals("Photography", copy.hobbies)
        assertEquals("Code for Good", copy.volunteerWork)
        assertEquals("Best Contributor 2024", copy.awards)
    }

    @Test
    fun testDuplicate_clearsProfilePictureUri_whenOriginalHasNoUri() = runTest(testDispatcher) {
        val original = createRichProfile(id = 12L, name = "Leo").copy(profilePictureUri = null)
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        assertNull(copy.profilePictureUri)
    }

    @Test
    fun testDuplicate_multipleNestedEntities_allGetFreshUUIDs() = runTest(testDispatcher) {
        val exp1 = Experience(id = "exp-1", jobTitle = "SWE 1")
        val exp2 = Experience(id = "exp-2", jobTitle = "SWE 2")
        val proj1 = Project(id = "proj-1", projectName = "Alpha")
        val proj2 = Project(id = "proj-2", projectName = "Beta")
        val edu1 = Education(id = "edu-1", degree = "BS")
        val edu2 = Education(id = "edu-2", degree = "MS")

        val original = createRichProfile(id = 13L, name = "Mia").copy(
            experiences = listOf(exp1, exp2),
            projects = listOf(proj1, proj2),
            educations = listOf(edu1, edu2)
        )
        val mockContext = ContextWrapper(null)

        val newId = duplicateProfileUseCase(mockContext, original)
        val copy = fakeRepository.savedProfiles[newId]!!

        // Verify counts preserved
        assertEquals(2, copy.experiences.size)
        assertEquals(2, copy.projects.size)
        assertEquals(2, copy.educations.size)

        // Verify all IDs are fresh
        val originalExpIds = setOf("exp-1", "exp-2")
        copy.experiences.forEach { assertFalse(it.id in originalExpIds) }

        val originalProjIds = setOf("proj-1", "proj-2")
        copy.projects.forEach { assertFalse(it.id in originalProjIds) }

        val originalEduIds = setOf("edu-1", "edu-2")
        copy.educations.forEach { assertFalse(it.id in originalEduIds) }

        // Verify all copy IDs are unique among themselves
        val allCopyIds = copy.experiences.map { it.id } + copy.projects.map { it.id } + copy.educations.map { it.id }
        assertEquals(allCopyIds.size, allCopyIds.toSet().size)

        // Verify data content preserved
        assertEquals("SWE 1", copy.experiences[0].jobTitle)
        assertEquals("SWE 2", copy.experiences[1].jobTitle)
        assertEquals("Alpha", copy.projects[0].projectName)
        assertEquals("Beta", copy.projects[1].projectName)
        assertEquals("BS", copy.educations[0].degree)
        assertEquals("MS", copy.educations[1].degree)
    }

    // ──────────────────────────────────────────────
    // ProfilesViewModel — Duplication State Tests
    // ──────────────────────────────────────────────

    @Test
    fun testDuplicateSelectedProfiles_singleProfile_transitionsToSuccess() = runTest(testDispatcher) {
        val profile = createRichProfile(1L, "Alice")
        fakeRepository.emit(listOf(profile))
        val viewModel = createViewModel()
        runCurrent()

        // Enter selection mode and select profile
        viewModel.enterSelectionMode(profile.id)
        runCurrent()
        assertTrue(viewModel.state.value.isSelectionMode)
        assertEquals(setOf(1L), viewModel.state.value.selectedProfileIds)

        // Trigger duplication
        val mockContext = ContextWrapper(null)
        viewModel.duplicateSelectedProfiles(mockContext)
        waitForDuplicationSuccess(viewModel)

        val finalState = viewModel.state.value
        assertEquals(DuplicateProgressState.Success, finalState.duplicateState)
        assertEquals(1, finalState.duplicatedCount)
        assertNotNull(finalState.newlyDuplicatedProfileId)
        assertEquals("Alice", finalState.duplicatedProfileName)

        // Selection mode should auto-exit
        assertFalse(finalState.isSelectionMode)
        assertTrue(finalState.selectedProfileIds.isEmpty())
    }

    @Test
    fun testDuplicateSelectedProfiles_multipleProfiles_countsCorrectly() = runTest(testDispatcher) {
        val p1 = createRichProfile(1L, "Alice")
        val p2 = createRichProfile(2L, "Bob")
        val p3 = createRichProfile(3L, "Charlie")
        fakeRepository.emit(listOf(p1, p2, p3))
        val viewModel = createViewModel()
        runCurrent()

        // Select 2 of 3 profiles
        viewModel.enterSelectionMode(p1.id)
        viewModel.toggleSelection(p2.id)
        runCurrent()
        assertEquals(setOf(1L, 2L), viewModel.state.value.selectedProfileIds)

        val mockContext = ContextWrapper(null)
        viewModel.duplicateSelectedProfiles(mockContext)
        waitForDuplicationSuccess(viewModel)

        val state = viewModel.state.value
        assertEquals(DuplicateProgressState.Success, state.duplicateState)
        assertEquals(2, state.duplicatedCount)
        assertNotNull(state.newlyDuplicatedProfileId)

        // Selection mode auto-exited
        assertFalse(state.isSelectionMode)
    }

    @Test
    fun testDuplicateSelectedProfiles_noProfilesSelected_doesNothing() = runTest(testDispatcher) {
        val profile = createRichProfile(1L, "Alice")
        fakeRepository.emit(listOf(profile))
        val viewModel = createViewModel()
        runCurrent()

        // Enter selection mode but don't select anything
        viewModel.enterSelectionMode()
        runCurrent()

        val mockContext = ContextWrapper(null)
        viewModel.duplicateSelectedProfiles(mockContext)
        advanceUntilIdle()

        // Should stay Idle
        assertEquals(DuplicateProgressState.Idle, viewModel.state.value.duplicateState)
        assertEquals(0, viewModel.state.value.duplicatedCount)
    }

    @Test
    fun testDismissDuplicateSheet_resetsAllDuplicateState() = runTest(testDispatcher) {
        val profile = createRichProfile(1L, "Alice")
        fakeRepository.emit(listOf(profile))
        val viewModel = createViewModel()
        runCurrent()

        // Simulate duplication completion
        viewModel.enterSelectionMode(profile.id)
        runCurrent()
        viewModel.duplicateSelectedProfiles(ContextWrapper(null))
        waitForDuplicationSuccess(viewModel)
        assertEquals(DuplicateProgressState.Success, viewModel.state.value.duplicateState)

        // Dismiss
        viewModel.dismissDuplicateSheet()
        runCurrent()

        val state = viewModel.state.value
        assertEquals(DuplicateProgressState.Idle, state.duplicateState)
        assertEquals(0, state.duplicatedCount)
        assertNull(state.newlyDuplicatedProfileId)
        assertEquals("", state.duplicatedProfileName)
    }

    @Test
    fun testDuplicateSelectedProfiles_copiedProfileAppearsInRepository() = runTest(testDispatcher) {
        val original = createRichProfile(1L, "Alice")
        fakeRepository.emit(listOf(original))
        val viewModel = createViewModel()
        runCurrent()

        val initialCount = fakeRepository.savedProfiles.size

        viewModel.enterSelectionMode(original.id)
        runCurrent()
        viewModel.duplicateSelectedProfiles(ContextWrapper(null))
        waitForDuplicationSuccess(viewModel)

        // Repository should now have one extra profile
        assertEquals(initialCount + 1, fakeRepository.savedProfiles.size)

        // The new profile should have a different ID
        val newId = viewModel.state.value.newlyDuplicatedProfileId!!
        assertNotEquals(original.id, newId)
        val copy = fakeRepository.savedProfiles[newId]!!
        assertEquals("Alice", copy.fullName)
    }

    private fun TestScope.waitForDuplicationSuccess(viewModel: ProfilesViewModel) {
        var attempts = 0
        while (viewModel.state.value.duplicateState != DuplicateProgressState.Success && attempts < 40) {
            Thread.sleep(25)
            advanceUntilIdle()
            attempts++
        }
    }

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────

    private fun createRichProfile(id: Long, name: String): UserProfile {
        return UserProfile(
            id = id,
            fullName = name,
            profilePictureUri = null,
            professionalTitle = "Developer",
            dateOfBirth = "",
            emails = emptyList(),
            phones = emptyList(),
            address = "",
            yearsOfExperience = "0",
            skills = emptyList(),
            experiences = emptyList(),
            projects = emptyList(),
            educations = emptyList(),
            certifications = emptyList(),
            references = emptyList(),
            socialLinks = emptyList(),
            languages = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}
