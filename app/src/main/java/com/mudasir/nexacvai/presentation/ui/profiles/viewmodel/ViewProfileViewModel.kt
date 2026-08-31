package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.DuplicateProfileUseCase
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileExportManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewProfileViewModel @Inject constructor(
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    private val userProfileRepository: com.mudasir.nexacvai.domain.repository.UserProfileRepository,
    private val duplicateProfileUseCase: DuplicateProfileUseCase,
    val profileDeleteManager: ProfileDeleteManager,
    private val profileExportManager: ProfileExportManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun deleteProfile(profile: UserProfile) {
        profileDeleteManager.requestDelete(profile)
    }

    private val profileId = savedStateHandle.get<Long>("profileId")?.takeIf { it != -1L }
    private val initialCachedProfile = profileId?.let { id ->
        userProfileRepository.getCachedProfiles().find { it.id == id }
    }
    private val _exportFields = MutableStateFlow(Pair<UserProfile?, Boolean>(null, false))
    private val _exportProgress = MutableStateFlow(Pair<ExportProgressState, String?>(ExportProgressState.Idle, null))
    private val _duplicateState = MutableStateFlow(
        Triple<DuplicateProgressState, Long?, String>(DuplicateProgressState.Idle, null, "")
    )

    val state: StateFlow<ViewProfileState> = if (profileId != null) {
        combine(
            getAllProfilesUseCase(),
            _exportFields,
            _exportProgress,
            _duplicateState
        ) { profiles, exportFields, exportProgress, dupState ->
            val profile = profiles.find { it.id == profileId }
            if (profile != null) {
                val liveSourceProfile = profile.sourceProfileId?.let { srcId ->
                    profiles.find { it.id == srcId }
                }
                val liveSourceProfileName = liveSourceProfile?.fullName?.ifBlank { "Untitled Profile" }
                    ?: profile.sourceProfileName
                val isSourceProfileAlive = liveSourceProfile != null

                ViewProfileState(
                    isLoading = false,
                    profile = profile,
                    liveSourceProfileName = liveSourceProfileName,
                    isSourceProfileAlive = isSourceProfileAlive,
                    exportingProfile = exportFields.first,
                    showExportConfirm = exportFields.second,
                    exportState = exportProgress.first,
                    exportError = exportProgress.second,
                    duplicateState = dupState.first,
                    newlyDuplicatedProfileId = dupState.second,
                    duplicatedProfileName = dupState.third
                )
            } else {
                ViewProfileState(isLoading = false, error = "Profile not found")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = if (initialCachedProfile != null) {
                val cachedProfiles = userProfileRepository.getCachedProfiles()
                val liveSourceProfile = initialCachedProfile.sourceProfileId?.let { srcId ->
                    cachedProfiles.find { it.id == srcId }
                }
                val cachedSourceProfileName = liveSourceProfile?.fullName?.ifBlank { "Untitled Profile" }
                    ?: initialCachedProfile.sourceProfileName

                ViewProfileState(
                    isLoading = false,
                    profile = initialCachedProfile,
                    liveSourceProfileName = cachedSourceProfileName,
                    isSourceProfileAlive = liveSourceProfile != null
                )
            } else {
                ViewProfileState(isLoading = true)
            }
        )
    } else {
        MutableStateFlow(
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

    fun duplicateCurrentProfile(context: Context) {
        val profile = state.value.profile ?: return
        viewModelScope.launch {
            _duplicateState.value = Triple(DuplicateProgressState.Duplicating, null, "")
            val startTime = System.currentTimeMillis()

            val newId = duplicateProfileUseCase(context, profile)

            val elapsed = System.currentTimeMillis() - startTime
            val remaining = (800L - elapsed).coerceAtLeast(0L)
            if (remaining > 0) delay(remaining)

            _duplicateState.value = Triple(
                DuplicateProgressState.Success,
                newId,
                profile.fullName
            )
        }
    }

    fun dismissDuplicateSheet() {
        _duplicateState.value = Triple(DuplicateProgressState.Idle, null, "")
    }
}
