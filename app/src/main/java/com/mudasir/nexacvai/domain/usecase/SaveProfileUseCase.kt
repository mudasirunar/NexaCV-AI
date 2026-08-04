package com.mudasir.nexacvai.domain.usecase

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import javax.inject.Inject

class SaveProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend fun getProfileById(id: Long): UserProfile? {
        return repository.getProfileById(id)
    }

    suspend operator fun invoke(profile: UserProfile): Long {
        return if (profile.id == 0L) {
            repository.insertProfile(profile)
        } else {
            repository.updateProfile(profile)
            profile.id
        }
    }
}
