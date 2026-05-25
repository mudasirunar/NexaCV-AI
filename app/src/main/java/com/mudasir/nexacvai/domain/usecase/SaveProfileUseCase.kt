package com.mudasir.nexacvai.domain.usecase

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository

class SaveProfileUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile): Long {
        return if (profile.id == 0L) {
            repository.insertProfile(profile)
        } else {
            repository.updateProfile(profile)
            profile.id
        }
    }
}
