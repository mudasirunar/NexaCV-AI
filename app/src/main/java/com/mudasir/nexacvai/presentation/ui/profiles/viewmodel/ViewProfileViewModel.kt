package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ViewProfileViewModel(
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    val profileDeleteManager: ProfileDeleteManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun deleteProfile(profile: UserProfile) {
        profileDeleteManager.requestDelete(profile)
    }

    private val profileId = savedStateHandle.get<Long>("profileId")?.takeIf { it != -1L }

    val state: StateFlow<ViewProfileState> = if (profileId != null) {
        getAllProfilesUseCase()
            .map { profiles ->
                val profile = profiles.find { it.id == profileId }
                if (profile != null) {
                    ViewProfileState(isLoading = false, profile = profile)
                } else {
                    ViewProfileState(isLoading = false, error = "Profile not found")
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ViewProfileState(isLoading = true)
            )
    } else {
        kotlinx.coroutines.flow.MutableStateFlow(
            ViewProfileState(error = "No profile ID provided")
        )
    }
}
