package com.mudasir.nexacvai

import com.mudasir.nexacvai.core.utils.ProfileImportExportHelper
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.domain.usecase.DeleteProfileUseCase
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ProfilesViewModel
import com.mudasir.nexacvai.domain.usecase.ImportProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileExportManager
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ImportProgressState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ExportProgressState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.DuplicateResolution
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
            profileDeleteManager = deleteManager,
            profileExportManager = exportManager
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
        assertEquals("Alice", state.profiles?.get(0)?.fullName)
        assertEquals("Bob", state.profiles?.get(1)?.fullName)
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
        runCurrent()

        val currentProfiles = viewModel.state.value.profiles
        assertNotNull(currentProfiles)
        assertEquals(1, currentProfiles?.size)
        assertEquals("Bob", currentProfiles?.first()?.fullName)

        assertTrue(fakeRepository.deletedProfiles.isEmpty())
    }

    @Test
    fun testUndoDelete_restoresProfileToList() = runTest(testDispatcher) {
        val profileA = createDummyProfile(1L, "Alice")
        val profileB = createDummyProfile(2L, "Bob")
        fakeRepository.emit(listOf(profileA, profileB))

        val viewModel = createViewModel()
        runCurrent()

        viewModel.deleteProfile(profileA)
        runCurrent()
        assertEquals(1, viewModel.state.value.profiles?.size)

        viewModel.profileDeleteManager.undoDelete()
        runCurrent()

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

        val updatedProfile = fakeRepository.savedProfiles[1L]
        assertNotNull(updatedProfile)
        assertEquals("content://new_path", updatedProfile?.profilePictureUri)
    }

    @Test
    fun testExportConfirmationState() = runTest(testDispatcher) {
        val profile = createDummyProfile(1L, "Alice")
        val viewModel = createViewModel()
        
        assertFalse(viewModel.state.value.showExportConfirm)
        assertNull(viewModel.state.value.exportingProfile)

        viewModel.selectProfileForExport(profile)
        runCurrent()
        assertTrue(viewModel.state.value.showExportConfirm)
        assertEquals(profile, viewModel.state.value.exportingProfile)

        viewModel.hideExportDialog()
        runCurrent()
        assertFalse(viewModel.state.value.showExportConfirm)
        assertEquals(profile, viewModel.state.value.exportingProfile)

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
        assertEquals(0, viewModel.state.value.importedCount)
    }

    @Test
    fun testExecuteMultiImport_allSkipped_instantlyTransitionsToSuccessWithZeroCount() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val mockContext = android.content.ContextWrapper(null)

        val p1 = createDummyProfile(10L, "Profile A")
        val p2 = createDummyProfile(20L, "Profile B")

        val importDataList = listOf(
            ProfileImportExportHelper.ImportedProfileData(p1, false, null),
            ProfileImportExportHelper.ImportedProfileData(p2, false, null)
        )

        // Set up duplicate selection state with 2 profiles
        val stateField = ProfilesViewModel::class.java.getDeclaredField("_state")
        stateField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = stateField.get(viewModel) as MutableStateFlow<ProfilesState>
        flow.value = flow.value.copy(
            importState = ImportProgressState.DuplicateSelection,
            importedProfileDataList = importDataList
        )

        // Execute multi import with all profiles set to Skip
        val resolutions = mapOf(
            10L to DuplicateResolution.Skip,
            20L to DuplicateResolution.Skip
        )
        viewModel.executeMultiImport(mockContext, resolutions)
        runCurrent()

        // Should transition immediately to Success with 0 count without staying in Importing state
        assertEquals(ImportProgressState.Success, viewModel.state.value.importState)
        assertEquals(0, viewModel.state.value.importedCount)
        assertNull(viewModel.state.value.newlyImportedProfileId)
    }

    @Test
    fun testExecuteMultiImport_partialSkip_calculatesExactImportedCount() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val mockContext = android.content.ContextWrapper(null)

        val p1 = createDummyProfile(10L, "Profile A")
        val p2 = createDummyProfile(20L, "Profile B")

        val importDataList = listOf(
            ProfileImportExportHelper.ImportedProfileData(p1, false, null),
            ProfileImportExportHelper.ImportedProfileData(p2, false, null)
        )

        val stateField = ProfilesViewModel::class.java.getDeclaredField("_state")
        stateField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = stateField.get(viewModel) as MutableStateFlow<ProfilesState>
        flow.value = flow.value.copy(
            importState = ImportProgressState.DuplicateSelection,
            importedProfileDataList = importDataList
        )

        // Skip p1, overwrite p2
        val resolutions = mapOf(
            10L to DuplicateResolution.Skip,
            20L to DuplicateResolution.Overwrite
        )
        viewModel.executeMultiImport(mockContext, resolutions)
        advanceUntilIdle()

        assertEquals(ImportProgressState.Success, viewModel.state.value.importState)
        assertEquals(1, viewModel.state.value.importedCount)
        assertEquals(20L, viewModel.state.value.newlyImportedProfileId)
    }

    @Test
    fun testExportProfileToUri_clearsSelectionMode() = runTest(testDispatcher) {
        val profile = createDummyProfile(1L, "Alice")
        fakeRepository.emit(listOf(profile))

        val viewModel = createViewModel()
        runCurrent()

        // Activate selection mode
        deleteManager.selectAllProfiles(listOf(1L))
        runCurrent()
        assertTrue(viewModel.state.value.isSelectionMode)

        // Select for export
        viewModel.selectProfileForExport(profile)
        runCurrent()

        // Export to URI
        val mockContext = android.content.ContextWrapper(null)
        val mockUri = android.net.Uri.EMPTY
        viewModel.exportProfileToUri(mockContext, mockUri)
        runCurrent()

        // Selection mode should be cleared automatically
        assertFalse(viewModel.state.value.isSelectionMode)
        assertTrue(viewModel.state.value.selectedProfileIds.isEmpty())
    }

    private fun createDummyProfile(id: Long, name: String): UserProfile {
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

    class FakeUserProfileRepository : UserProfileRepository {
        val profilesFlow = MutableStateFlow<List<UserProfile>>(emptyList())
        val deletedProfiles = mutableListOf<UserProfile>()
        val savedProfiles = mutableMapOf<Long, UserProfile>()

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

        override suspend fun insertProfile(profile: UserProfile): Long {
            savedProfiles[profile.id] = profile
            return profile.id
        }

        override suspend fun updateProfile(profile: UserProfile) {
            savedProfiles[profile.id] = profile
        }

        override suspend fun deleteProfile(profile: UserProfile) {
            deletedProfiles.add(profile)
            val updatedList = profilesFlow.value.filter { it.id != profile.id }
            profilesFlow.value = updatedList
            savedProfiles.remove(profile.id)
        }
    }
}
