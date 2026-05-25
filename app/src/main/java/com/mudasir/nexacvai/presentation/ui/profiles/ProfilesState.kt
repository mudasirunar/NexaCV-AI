package com.mudasir.nexacvai.presentation.ui.profiles

import androidx.compose.runtime.Immutable
import com.mudasir.nexacvai.domain.model.UserProfile

@Immutable
data class ProfilesState(
    val isLoading: Boolean = false,
    val profiles: List<UserProfile>? = null,
    val error: String? = null
)
