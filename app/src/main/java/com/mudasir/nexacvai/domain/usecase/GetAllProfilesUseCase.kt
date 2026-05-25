package com.mudasir.nexacvai.domain.usecase

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow

class GetAllProfilesUseCase(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): Flow<List<UserProfile>> {
        return repository.getAllProfiles()
    }
}
