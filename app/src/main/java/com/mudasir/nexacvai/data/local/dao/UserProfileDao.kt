package com.mudasir.nexacvai.data.local.dao

import androidx.room.*
import com.mudasir.nexacvai.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Transaction
    @Query("SELECT * FROM user_profiles ORDER BY updatedAt DESC")
    fun getAllProfilesWithDetails(): Flow<List<UserProfileWithDetails>>

    @Transaction
    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileWithDetailsById(id: Long): UserProfileWithDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET isCopyTagDismissed = 1 WHERE id = :profileId")
    suspend fun dismissCopyTag(profileId: Long)

    @Delete
    suspend fun deleteProfile(profile: UserProfileEntity)

    // Sub-tables Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiences(experiences: List<ExperienceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEducations(educations: List<EducationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertifications(certifications: List<CertificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferences(references: List<ReferenceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialLinks(links: List<SocialLinkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguages(languages: List<LanguageEntity>)

    // Sub-tables Delete
    @Query("DELETE FROM experiences WHERE profileId = :profileId")
    suspend fun deleteExperiencesForProfile(profileId: Long)

    @Query("DELETE FROM projects WHERE profileId = :profileId")
    suspend fun deleteProjectsForProfile(profileId: Long)

    @Query("DELETE FROM educations WHERE profileId = :profileId")
    suspend fun deleteEducationsForProfile(profileId: Long)

    @Query("DELETE FROM certifications WHERE profileId = :profileId")
    suspend fun deleteCertificationsForProfile(profileId: Long)

    @Query("DELETE FROM profile_references WHERE profileId = :profileId")
    suspend fun deleteReferencesForProfile(profileId: Long)

    @Query("DELETE FROM social_links WHERE profileId = :profileId")
    suspend fun deleteSocialLinksForProfile(profileId: Long)

    @Query("DELETE FROM languages WHERE profileId = :profileId")
    suspend fun deleteLanguagesForProfile(profileId: Long)
}

