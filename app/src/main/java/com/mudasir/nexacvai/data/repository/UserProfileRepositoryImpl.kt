package com.mudasir.nexacvai.data.repository

import com.mudasir.nexacvai.data.local.dao.UserProfileDao
import com.mudasir.nexacvai.di.ApplicationScope
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.domain.mapper.toDomain
import com.mudasir.nexacvai.domain.mapper.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : UserProfileRepository {

    // Hot in-memory StateFlow driven directly by Room SQLite table invalidations
    private val _profilesStateFlow: StateFlow<List<UserProfile>> = dao.getAllProfilesWithDetails()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override fun getAllProfiles(): Flow<List<UserProfile>> = _profilesStateFlow

    override fun getCachedProfiles(): List<UserProfile> = _profilesStateFlow.value

    override suspend fun getProfileById(id: Long): UserProfile? {
        val inMemory = _profilesStateFlow.value.find { it.id == id }
        if (inMemory != null) return inMemory

        return withContext(Dispatchers.IO) {
            dao.getProfileWithDetailsById(id)?.toDomain()
        }
    }

    override suspend fun getProfileByUuid(uuid: String): UserProfile? {
        val inMemory = _profilesStateFlow.value.find { it.uuid == uuid }
        if (inMemory != null) return inMemory

        return withContext(Dispatchers.IO) {
            dao.getProfileWithDetailsByUuid(uuid)?.toDomain()
        }
    }

    override suspend fun insertProfile(profile: UserProfile): Long = withContext(Dispatchers.IO) {
        val profileId = dao.insertProfile(profile.toEntity())
        saveSubEntities(profileId, profile)
        profileId
    }

    override suspend fun updateProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        dao.updateProfile(profile.toEntity())
        saveSubEntities(profile.id, profile)
    }

    override suspend fun dismissCopyTag(profileId: Long) = withContext(Dispatchers.IO) {
        dao.dismissCopyTag(profileId)
    }

    override suspend fun deleteProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        dao.deleteProfile(profile.toEntity())
        // Cascading deletes will automatically clear child tables in Room database.
    }

    private suspend fun saveSubEntities(profileId: Long, profile: UserProfile) {
        dao.deleteExperiencesForProfile(profileId)
        dao.deleteProjectsForProfile(profileId)
        dao.deleteEducationsForProfile(profileId)
        dao.deleteCertificationsForProfile(profileId)
        dao.deleteReferencesForProfile(profileId)
        dao.deleteSocialLinksForProfile(profileId)
        dao.deleteLanguagesForProfile(profileId)

        dao.insertExperiences(profile.experiences.map { it.toEntity(profileId) })
        dao.insertProjects(profile.projects.map { it.toEntity(profileId) })
        dao.insertEducations(profile.educations.map { it.toEntity(profileId) })
        dao.insertCertifications(profile.certifications.map { it.toEntity(profileId) })
        dao.insertReferences(profile.references.map { it.toEntity(profileId) })
        dao.insertSocialLinks(profile.socialLinks.map { it.toEntity(profileId) })
        dao.insertLanguages(profile.languages.map { it.toEntity(profileId) })
    }
}

