package com.mudasir.nexacvai.presentation.ui.profiles

import com.mudasir.nexacvai.domain.model.UserProfile

data class ProfilesState(
    val isLoading: Boolean = false,
    val profiles: List<UserProfile> = emptyList(),
    val error: String? = null
)
