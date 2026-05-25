package com.mudasir.nexacvai.presentation.ui.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProfilesViewModel(
    private val getAllProfilesUseCase: GetAllProfilesUseCase
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
}

