package com.mudasir.nexacvai.domain.usecase

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository

class GetProfileUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(id: Long): UserProfile? {
        return repository.getProfileById(id)
    }
}
