package com.mudasir.nexacvai.domain.repository

import com.mudasir.nexacvai.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getAllProfiles(): Flow<List<UserProfile>>
    fun getCachedProfiles(): List<UserProfile> = emptyList()
    suspend fun getProfileById(id: Long): UserProfile?
    suspend fun getProfileByUuid(uuid: String): UserProfile?
    suspend fun insertProfile(profile: UserProfile): Long
    suspend fun updateProfile(profile: UserProfile)
    suspend fun dismissCopyTag(profileId: Long)
    suspend fun deleteProfile(profile: UserProfile)
}
