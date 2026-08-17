package com.mudasir.nexacvai.profiletests

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.domain.usecase.DeleteProfileUseCase
import com.mudasir.nexacvai.domain.usecase.DuplicateProfileUseCase
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ProfilesViewModel
import com.mudasir.nexacvai.domain.usecase.ImportProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileExportManager
import com.mudasir.nexacvai.data.local.datastore.AppSettingsManager
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ImportProgressState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var fakeRepository: FakeUserProfileRepository
    private lateinit var getAllProfilesUseCase: GetAllProfilesUseCase
    private lateinit var saveProfileUseCase: SaveProfileUseCase
    private lateinit var deleteProfileUseCase: DeleteProfileUseCase
    private lateinit var deleteManager: ProfileDeleteManager
    private lateinit var importProfileUseCase: ImportProfileUseCase
    private lateinit var duplicateProfileUseCase: DuplicateProfileUseCase
    private lateinit var exportManager: ProfileExportManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeUserProfileRepository()
        getAllProfilesUseCase = GetAllProfilesUseCase(fakeRepository)
        saveProfileUseCase = SaveProfileUseCase(fakeRepository)
        deleteProfileUseCase = DeleteProfileUseCase(fakeRepository)
        deleteManager = ProfileDeleteManager(deleteProfileUseCase, testScope)
        importProfileUseCase = ImportProfileUseCase(fakeRepository)
        duplicateProfileUseCase = DuplicateProfileUseCase(fakeRepository)
        exportManager = ProfileExportManager(testScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(appSettingsManager: AppSettingsManager = FakeAppSettingsManager()): ProfilesViewModel {
        return ProfilesViewModel(
            getAllProfilesUseCase = getAllProfilesUseCase,
            saveProfileUseCase = saveProfileUseCase,
            importProfileUseCase = importProfileUseCase,
            duplicateProfileUseCase = duplicateProfileUseCase,
            userProfileRepository = fakeRepository,
            profileDeleteManager = deleteManager,
            profileExportManager = exportManager,
            appSettingsManager = appSettingsManager
        )
    }

    @Test
    fun testInitialState_loadsProfilesFromRepository() = runTest(testDispatcher) {
        val profileA = createDummyProfile(1L, "Alice")
        val profileB = createDummyProfile(2L, "Bob")
        fakeRepository.emit(listOf(profileA, profileB))

        val viewModel = createViewModel()
        runCurrent()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.profiles)
        assertEquals(2, state.profiles?.size)
        assertTrue(state.profiles?.any { it.fullName == "Alice" } == true)
        assertTrue(state.profiles?.any { it.fullName == "Bob" } == true)
    }

    @Test
    fun testDeleteProfile_registersPendingDeletionAndFiltersIt() = runTest(testDispatcher) {
        val profileA = createDummyProfile(1L, "Alice")
        val profileB = createDummyProfile(2L, "Bob")
        fakeRepository.emit(listOf(profileA, profileB))

        val viewModel = createViewModel()
        runCurrent()

        // Verify initial list has 2 profiles
        assertEquals(2, viewModel.state.value.profiles?.size)

        // Request delete for profile A
        viewModel.deleteProfile(profileA)
        runCurrent() // Let the deletion flow update collector run, without advancing virtual time past the delay

        // Profile A should be filtered out because it is pending deletion
        val currentProfiles = viewModel.state.value.profiles
        assertNotNull(currentProfiles)
        assertEquals(1, currentProfiles?.size)
        assertEquals("Bob", currentProfiles?.first()?.fullName)

        // Deletion shouldn't be executed in DB yet (5000ms delay has not elapsed)
        assertTrue(fakeRepository.deletedProfiles.isEmpty())
    }

    @Test
    fun testUndoDelete_restoresProfileToList() = runTest(testDispatcher) {
        val profileA = createDummyProfile(1L, "Alice")
        val profileB = createDummyProfile(2L, "Bob")
        fakeRepository.emit(listOf(profileA, profileB))

        val viewModel = createViewModel()
        runCurrent()

        // Delete profile A
        viewModel.deleteProfile(profileA)
        runCurrent()
        assertEquals(1, viewModel.state.value.profiles?.size)

        // Undo deletion
        viewModel.profileDeleteManager.undoDelete()
        runCurrent()

        // Profile A should reappear in the list
        val currentProfiles = viewModel.state.value.profiles
        assertEquals(2, currentProfiles?.size)
        assertEquals("Alice", currentProfiles?.get(0)?.fullName)
    }

    @Test
    fun testRemoveProfilePicture_savesProfileWithoutPicture() = runTest(testDispatcher) {
        val profile = createDummyProfile(1L, "Alice").copy(profilePictureUri = "content://path")
        fakeRepository.emit(listOf(profile))

        val viewModel = createViewModel()
        runCurrent()

        viewModel.removeProfilePicture(profile)
        runCurrent()

        // Verify save was called to update DB profile picture uri to null
        val updatedProfile = fakeRepository.savedProfiles[1L]
        assertNotNull(updatedProfile)
        assertNull(updatedProfile?.profilePictureUri)
    }

    @Test
    fun testUpdateProfilePicture_savesProfileWithPicture() = runTest(testDispatcher) {
        val profile = createDummyProfile(1L, "Alice")
        fakeRepository.emit(listOf(profile))

        val viewModel = createViewModel()
        runCurrent()

        viewModel.updateProfilePicture(profile, "content://new_path")
        runCurrent()

        // Verify save was called with the updated image URI
        val updatedProfile = fakeRepository.savedProfiles[1L]
        assertNotNull(updatedProfile)
        assertEquals("content://new_path", updatedProfile?.profilePictureUri)
    }

    private fun createDummyProfile(id: Long, name: String): UserProfile {
        return UserProfile(
            id = id,
            uuid = "uuid-$id",
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
            createdAt = 100000L - id * 1000L,
            updatedAt = 100000L - id * 1000L
        )
    }

    @Test
    fun testExportConfirmationState() = runTest(testDispatcher) {
        val profile = createDummyProfile(1L, "Alice")
        val viewModel = createViewModel()
        
        // Initial state
        assertFalse(viewModel.state.value.showExportConfirm)
        assertNull(viewModel.state.value.exportingProfile)

        // Select for export
        viewModel.selectProfileForExport(profile)
        runCurrent()
        assertTrue(viewModel.state.value.showExportConfirm)
        assertEquals(profile, viewModel.state.value.exportingProfile)

        // Hide dialog (retains profile pointer but hides dialog visibility)
        viewModel.hideExportDialog()
        runCurrent()
        assertFalse(viewModel.state.value.showExportConfirm)
        assertEquals(profile, viewModel.state.value.exportingProfile)

        // Dismiss confirmation completely
        viewModel.dismissExportConfirm()
        runCurrent()
        assertFalse(viewModel.state.value.showExportConfirm)
        assertNull(viewModel.state.value.exportingProfile)
    }

    @Test
    fun testCancelImport_clearsImportStates() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        
        viewModel.cancelImport()
        runCurrent()
        
        assertEquals(ImportProgressState.Idle, viewModel.state.value.importState)
        assertNull(viewModel.state.value.importedProfileData)
        assertNull(viewModel.state.value.newlyImportedProfileId)
    }

    class FakeUserProfileRepository : UserProfileRepository {
        val profilesFlow = MutableStateFlow<List<UserProfile>>(emptyList())
        val deletedProfiles = mutableListOf<UserProfile>()
        val savedProfiles = mutableMapOf<Long, UserProfile>()
        private var nextId = 100L

        fun emit(list: List<UserProfile>) {
            profilesFlow.value = list
            list.forEach { savedProfiles[it.id] = it }
        }

        override fun getAllProfiles(): Flow<List<UserProfile>> {
            return profilesFlow
        }

        override suspend fun getProfileById(id: Long): UserProfile? {
            return savedProfiles[id]
        }

        override suspend fun getProfileByUuid(uuid: String): UserProfile? {
            return savedProfiles.values.firstOrNull { it.uuid == uuid }
        }

        override suspend fun insertProfile(profile: UserProfile): Long {
            val id = if (profile.id == 0L) nextId++ else profile.id
            val saved = profile.copy(id = id)
            savedProfiles[id] = saved
            profilesFlow.value = profilesFlow.value + saved
            return id
        }

        override suspend fun updateProfile(profile: UserProfile) {
            savedProfiles[profile.id] = profile
            val current = profilesFlow.value.toMutableList()
            val index = current.indexOfFirst { it.id == profile.id }
            if (index != -1) {
                current[index] = profile
                profilesFlow.value = current
            }
        }

        override suspend fun dismissCopyTag(profileId: Long) {
            val profile = savedProfiles[profileId] ?: return
            updateProfile(profile.copy(isCopyTagDismissed = true))
        }

        override suspend fun deleteProfile(profile: UserProfile) {
            deletedProfiles.add(profile)
            val updatedList = profilesFlow.value.filter { it.id != profile.id }
            profilesFlow.value = updatedList
            savedProfiles.remove(profile.id)
        }
    }

    class FakeAppSettingsManager : com.mudasir.nexacvai.data.local.datastore.AppSettingsManager(android.content.ContextWrapper(null)) {
        val sortFlow = MutableStateFlow(com.mudasir.nexacvai.domain.model.ProfileSortOrder.NEWEST_FIRST)
        override val profileSortOrderFlow: Flow<com.mudasir.nexacvai.domain.model.ProfileSortOrder> get() = sortFlow
        override suspend fun setProfileSortOrder(sortOrder: com.mudasir.nexacvai.domain.model.ProfileSortOrder) {
            sortFlow.value = sortOrder
        }
    }
}
