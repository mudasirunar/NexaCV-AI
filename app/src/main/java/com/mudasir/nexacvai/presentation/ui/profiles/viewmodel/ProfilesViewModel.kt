package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.DeleteProfileUseCase
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProfilesViewModel(
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfilesState())
    val state: StateFlow<ProfilesState> = _state.asStateFlow()

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            getAllProfilesUseCase()
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred"
                    )
                }
                .collect { profiles ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        profiles = profiles
                    )
                }
        }
    }

    fun deleteProfile(profile: UserProfile) {
        viewModelScope.launch {
            deleteProfileUseCase(profile)
        }
    }

    fun removeProfilePicture(profile: UserProfile) {
        viewModelScope.launch {
            val updated = profile.copy(
                profilePictureUri = null,
                updatedAt = System.currentTimeMillis()
            )
            saveProfileUseCase(updated)
        }
    }

    fun updateProfilePicture(profile: UserProfile, pictureUri: String?) {
        viewModelScope.launch {
            val updated = profile.copy(
                profilePictureUri = pictureUri,
                updatedAt = System.currentTimeMillis()
            )
            saveProfileUseCase(updated)
        }
    }
}
