package com.mudasir.nexacvai.data.repository

import com.mudasir.nexacvai.data.local.dao.UserProfileDao
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.domain.mapper.toDomain
import com.mudasir.nexacvai.domain.mapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao
) : UserProfileRepository {

    override fun getAllProfiles(): Flow<List<UserProfile>> {
        return dao.getAllProfilesWithDetails()
            .map { list ->
                list.map { it.toDomain() }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getProfileById(id: Long): UserProfile? = withContext(Dispatchers.IO) {
        dao.getProfileWithDetailsById(id)?.toDomain()
    }

    override suspend fun getProfileByUuid(uuid: String): UserProfile? = withContext(Dispatchers.IO) {
        dao.getProfileWithDetailsByUuid(uuid)?.toDomain()
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

