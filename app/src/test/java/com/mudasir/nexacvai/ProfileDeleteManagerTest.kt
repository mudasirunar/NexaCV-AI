package com.mudasir.nexacvai

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.domain.usecase.DeleteProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * JVM Unit tests verifying the state mutations, scheduling, and automatic timeout logic inside ProfileDeleteManager.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileDeleteManagerTest {

    private lateinit var fakeRepository: FakeUserProfileRepository
    private lateinit var deleteProfileUseCase: DeleteProfileUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeUserProfileRepository()
        deleteProfileUseCase = DeleteProfileUseCase(fakeRepository)
    }

    @Test
    fun testInitialState() = runTest {
        val manager = ProfileDeleteManager(deleteProfileUseCase, this)
        assertNull(manager.pendingDeleteProfile.value)
        assertTrue(manager.isFabVisible.value)
        assertNull(manager.lastUndoneProfileId.value)
    }

    @Test
    fun testRequestDeleteSetsPendingProfile() = runTest {
        val manager = ProfileDeleteManager(deleteProfileUseCase, this)
        val profile = createDummyProfile(1L, "Alice")
        manager.requestDelete(profile)
        
        assertEquals(profile, manager.pendingDeleteProfile.value)
        runCurrent()
        assertTrue(fakeRepository.deletedProfiles.isEmpty()) // Should not be deleted yet (undo period active)
    }

    @Test
    fun testUndoDelete() = runTest {
        val manager = ProfileDeleteManager(deleteProfileUseCase, this)
        val profile = createDummyProfile(1L, "Alice")
        manager.requestDelete(profile)
        
        manager.undoDelete()
        
        assertNull(manager.pendingDeleteProfile.value)
        assertEquals(1L, manager.lastUndoneProfileId.value)
        runCurrent()
        assertTrue(fakeRepository.deletedProfiles.isEmpty()) // Restored, not deleted
    }

    @Test
    fun testCommitPendingDelete() = runTest {
        val manager = ProfileDeleteManager(deleteProfileUseCase, this)
        val profile = createDummyProfile(1L, "Alice")
        manager.requestDelete(profile)
        
        manager.commitPendingDelete()
        
        assertNull(manager.pendingDeleteProfile.value)
        runCurrent()
        assertEquals(1, fakeRepository.deletedProfiles.size)
        assertEquals(profile, fakeRepository.deletedProfiles[0])
    }

    @Test
    fun testSequentialDeletesCommitsPrevious() = runTest {
        val manager = ProfileDeleteManager(deleteProfileUseCase, this)
        val profileA = createDummyProfile(1L, "Alice")
        val profileB = createDummyProfile(2L, "Bob")
        
        manager.requestDelete(profileA)
        manager.requestDelete(profileB) // Requesting delete B must commit deletion of profile A immediately
        
        assertEquals(profileB, manager.pendingDeleteProfile.value)
        runCurrent()
        assertEquals(1, fakeRepository.deletedProfiles.size)
        assertEquals(profileA, fakeRepository.deletedProfiles[0])
    }

    @Test
    fun testClearLastUndoneProfileId() = runTest {
        val manager = ProfileDeleteManager(deleteProfileUseCase, this)
        val profile = createDummyProfile(1L, "Alice")
        manager.requestDelete(profile)
        manager.undoDelete()
        
        manager.clearLastUndoneProfileId()
        assertNull(manager.lastUndoneProfileId.value)
    }

    @Test
    fun testAutomaticTimeoutDeletion() = runTest {
        val manager = ProfileDeleteManager(deleteProfileUseCase, this)
        val profile = createDummyProfile(1L, "Alice")
        var finishedCalled = false
        
        manager.requestDelete(profile, onFinished = { finishedCalled = true })
        
        // Let the coroutine start and reach delay
        runCurrent()
        
        // Assert it is pending and not yet committed (suspended at delay)
        assertEquals(profile, manager.pendingDeleteProfile.value)
        assertTrue(fakeRepository.deletedProfiles.isEmpty())
        assertFalse(finishedCalled)
        
        // Advance time by 4999ms (just before the 5000ms timeout)
        testScheduler.advanceTimeBy(4999)
        assertEquals(profile, manager.pendingDeleteProfile.value)
        assertTrue(fakeRepository.deletedProfiles.isEmpty())
        assertFalse(finishedCalled)
        
        // Advance time to 5000ms (timeout trigger)
        testScheduler.advanceTimeBy(1)
        // runCurrent is automatically called by advanceTimeBy, but we can call it to be sure
        runCurrent()
        
        // Now it should be committed automatically
        assertNull(manager.pendingDeleteProfile.value)
        assertEquals(1, fakeRepository.deletedProfiles.size)
        assertEquals(profile, fakeRepository.deletedProfiles[0])
        assertTrue(finishedCalled)
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
        val deletedProfiles = mutableListOf<UserProfile>()
        
        override fun getAllProfiles(): Flow<List<UserProfile>> = flowOf(emptyList())
        override suspend fun getProfileById(id: Long): UserProfile? = null
        override suspend fun insertProfile(profile: UserProfile): Long = 0
        override suspend fun updateProfile(profile: UserProfile) {}
        
        override suspend fun deleteProfile(profile: UserProfile) {
            deletedProfiles.add(profile)
        }
    }
}
