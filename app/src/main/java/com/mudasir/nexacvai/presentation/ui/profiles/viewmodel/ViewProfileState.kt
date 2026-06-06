package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.compose.runtime.Immutable
import com.mudasir.nexacvai.domain.model.UserProfile

@Immutable
data class ViewProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null
)
