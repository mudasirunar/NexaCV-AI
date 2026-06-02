package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfilesViewModel(
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    val profileDeleteManager: ProfileDeleteManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfilesState())
    val state: StateFlow<ProfilesState> = _state.asStateFlow()

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            combine(
                getAllProfilesUseCase(),
                profileDeleteManager.pendingDeleteProfile
            ) { profiles, pendingProfile ->
                if (pendingProfile != null) {
                    profiles.filter { it.id != pendingProfile.id }
                } else {
                    profiles
                }
            }
            .catch { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )
            }
            .collect { filteredProfiles ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    profiles = filteredProfiles
                )
            }
        }
    }

    fun deleteProfile(profile: UserProfile) {
        profileDeleteManager.requestDelete(profile)
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
