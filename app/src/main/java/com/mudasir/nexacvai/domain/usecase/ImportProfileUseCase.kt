package com.mudasir.nexacvai.domain.usecase

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository

class ImportProfileUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile): Long {
        return repository.insertProfile(profile)
    }
}
