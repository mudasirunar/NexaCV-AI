package com.mudasir.nexacvai.profiletests

import com.mudasir.nexacvai.data.local.dao.UserProfileDao
import com.mudasir.nexacvai.data.local.entity.*
import com.mudasir.nexacvai.data.repository.UserProfileRepositoryImpl
import com.mudasir.nexacvai.domain.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileRepositoryTest {

    private lateinit var fakeDao: FakeUserProfileDao
    private lateinit var repository: UserProfileRepositoryImpl

    private class FakeUserProfileDao : UserProfileDao {
        val profilesFlow = MutableStateFlow<List<UserProfileWithDetails>>(emptyList())
        private var currentId = 1L

        override fun getAllProfilesWithDetails(): Flow<List<UserProfileWithDetails>> = profilesFlow

        override suspend fun getProfileWithDetailsById(id: Long): UserProfileWithDetails? {
            return profilesFlow.value.find { it.profile.id == id }
        }

        override suspend fun getProfileWithDetailsByUuid(uuid: String): UserProfileWithDetails? {
            return profilesFlow.value.find { it.profile.uuid == uuid }
        }

        override suspend fun insertProfile(profile: UserProfileEntity): Long {
            val assignedId = if (profile.id == 0L) currentId++ else profile.id
            val entityWithId = profile.copy(id = assignedId)
            val details = UserProfileWithDetails(
                profile = entityWithId,
                experiences = emptyList(),
                projects = emptyList(),
                educations = emptyList(),
                certifications = emptyList(),
                references = emptyList(),
                socialLinks = emptyList(),
                languages = emptyList()
            )
            val currentList = profilesFlow.value.toMutableList()
            currentList.removeAll { it.profile.id == assignedId }
            currentList.add(details)
            profilesFlow.value = currentList
            return assignedId
        }

        override suspend fun updateProfile(profile: UserProfileEntity) {
            val currentList = profilesFlow.value.toMutableList()
            val index = currentList.indexOfFirst { it.profile.id == profile.id }
            if (index != -1) {
                currentList[index] = currentList[index].copy(profile = profile)
                profilesFlow.value = currentList
            }
        }

        override suspend fun deleteProfile(profile: UserProfileEntity) {
            val currentList = profilesFlow.value.toMutableList()
            currentList.removeAll { it.profile.id == profile.id }
            profilesFlow.value = currentList
        }

        override suspend fun dismissCopyTag(profileId: Long) {
            val currentList = profilesFlow.value.toMutableList()
            val index = currentList.indexOfFirst { it.profile.id == profileId }
            if (index != -1) {
                currentList[index] = currentList[index].copy(
                    profile = currentList[index].profile.copy(isCopyTagDismissed = true)
                )
                profilesFlow.value = currentList
            }
        }

        override suspend fun insertExperiences(experiences: List<ExperienceEntity>) {}
        override suspend fun insertProjects(projects: List<ProjectEntity>) {}
        override suspend fun insertEducations(educations: List<EducationEntity>) {}
        override suspend fun insertCertifications(certifications: List<CertificationEntity>) {}
        override suspend fun insertReferences(references: List<ReferenceEntity>) {}
        override suspend fun insertSocialLinks(socialLinks: List<SocialLinkEntity>) {}
        override suspend fun insertLanguages(languages: List<LanguageEntity>) {}

        override suspend fun deleteExperiencesForProfile(profileId: Long) {}
        override suspend fun deleteProjectsForProfile(profileId: Long) {}
        override suspend fun deleteEducationsForProfile(profileId: Long) {}
        override suspend fun deleteCertificationsForProfile(profileId: Long) {}
        override suspend fun deleteReferencesForProfile(profileId: Long) {}
        override suspend fun deleteSocialLinksForProfile(profileId: Long) {}
        override suspend fun deleteLanguagesForProfile(profileId: Long) {}
    }

    @Before
    fun setUp() {
        fakeDao = FakeUserProfileDao()
        repository = UserProfileRepositoryImpl(
            dao = fakeDao,
            applicationScope = CoroutineScope(UnconfinedTestDispatcher())
        )
    }

    @Test
    fun getAllProfiles_returnsHotStateFlow_withInstantEmission() = runTest {
        val initialProfiles = repository.getAllProfiles().first()
        assertTrue(initialProfiles.isEmpty())

        val testProfile = UserProfile(
            id = 1L,
            uuid = "uuid-123",
            fullName = "John Doe",
            professionalTitle = "Android Engineer",
            emails = listOf("john@example.com")
        )
        repository.insertProfile(testProfile)

        val updatedProfiles = repository.getAllProfiles().first()
        assertEquals(1, updatedProfiles.size)
        assertEquals("Android Engineer", updatedProfiles[0].professionalTitle)
        assertEquals("John Doe", updatedProfiles[0].fullName)
    }

    @Test
    fun getProfileById_resolvesFastFromMemory() = runTest {
        val testProfile = UserProfile(
            id = 2L,
            uuid = "uuid-456",
            fullName = "Jane Smith",
            professionalTitle = "Backend Developer",
            emails = listOf("jane@example.com")
        )
        repository.insertProfile(testProfile)

        val retrieved = repository.getProfileById(2L)
        assertNotNull(retrieved)
        assertEquals("Backend Developer", retrieved?.professionalTitle)
        assertEquals("Jane Smith", retrieved?.fullName)
    }

    @Test
    fun deleteProfile_removesFromHotStateFlow_instantly() = runTest {
        val testProfile = UserProfile(
            id = 3L,
            uuid = "uuid-789",
            fullName = "Alex Taylor",
            professionalTitle = "Product Manager"
        )
        repository.insertProfile(testProfile)
        assertEquals(1, repository.getAllProfiles().first().size)

        repository.deleteProfile(testProfile)
        assertEquals(0, repository.getAllProfiles().first().size)
        assertNull(repository.getProfileById(3L))
    }
}
