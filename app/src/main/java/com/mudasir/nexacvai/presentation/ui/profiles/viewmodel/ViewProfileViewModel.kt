package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileExportManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import android.content.Context
import android.net.Uri
import com.mudasir.nexacvai.core.utils.ProfileImportExportHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewProfileViewModel(
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    val profileDeleteManager: ProfileDeleteManager,
    private val profileExportManager: ProfileExportManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun deleteProfile(profile: UserProfile) {
        profileDeleteManager.requestDelete(profile)
    }

    private val profileId = savedStateHandle.get<Long>("profileId")?.takeIf { it != -1L }
    private val _exportFields = MutableStateFlow(Pair<UserProfile?, Boolean>(null, false))
    private val _exportProgress = MutableStateFlow(Pair<ExportProgressState, String?>(ExportProgressState.Idle, null))

    val state: StateFlow<ViewProfileState> = if (profileId != null) {
        combine(
            getAllProfilesUseCase(),
            _exportFields,
            _exportProgress
        ) { profiles, exportFields, exportProgress ->
            val profile = profiles.find { it.id == profileId }
            if (profile != null) {
                ViewProfileState(
                    isLoading = false,
                    profile = profile,
                    exportingProfile = exportFields.first,
                    showExportConfirm = exportFields.second,
                    exportState = exportProgress.first,
                    exportError = exportProgress.second
                )
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

    fun selectProfileForExport(profile: UserProfile) {
        _exportFields.value = Pair(profile, true)
    }

    fun dismissExportConfirm() {
        _exportFields.value = Pair(null, false)
    }

    fun hideExportDialog() {
        val currentProfile = _exportFields.value.first
        _exportFields.value = Pair(currentProfile, false)
    }

    fun dismissExportProgress() {
        profileExportManager.dismissExportProgress()
        _exportFields.value = Pair(null, false)
    }

    fun exportProfileToUri(context: Context, uri: Uri) {
        val profile = _exportFields.value.first ?: return
        profileExportManager.exportProfileToUri(context, profile, uri)
        dismissExportConfirm()
    }
}
