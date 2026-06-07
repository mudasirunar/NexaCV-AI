package com.mudasir.nexacvai.presentation.ui.profiles.utils

import android.content.Context
import android.net.Uri
import com.mudasir.nexacvai.core.utils.ProfileImportExportHelper
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ExportProgressState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the global export progress state for UserProfiles.
 * Exposes a shared state flow observed by the application root to show the custom toast overlay.
 */
class ProfileExportManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    private val _exportState = MutableStateFlow(ExportProgressState.Idle)
    val exportState: StateFlow<ExportProgressState> = _exportState.asStateFlow()

    private val _exportError = MutableStateFlow<String?>(null)
    val exportError: StateFlow<String?> = _exportError.asStateFlow()

    fun dismissExportProgress() {
        _exportState.value = ExportProgressState.Idle
        _exportError.value = null
    }

    fun exportProfileToUri(context: Context, profile: UserProfile, uri: Uri) {
        scope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _exportState.value = ExportProgressState.Exporting
                _exportError.value = null
            }
            try {
                // 1. Perform actual export immediately so the file is saved instantly without waiting
                var result = false
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    result = ProfileImportExportHelper.exportProfile(context, profile, outputStream)
                } ?: run {
                    withContext(Dispatchers.Main) {
                        _exportState.value = ExportProgressState.Error
                        _exportError.value = "Unable to create or open file destination."
                    }
                    return@launch
                }

                // 2. Add visual delay so the user can see the progress spinner and dot animation
                delay(1000L)

                withContext(Dispatchers.Main) {
                    if (result) {
                        _exportState.value = ExportProgressState.Success
                    } else {
                        _exportState.value = ExportProgressState.Error
                        _exportError.value = "Could not package profile data into ZIP."
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _exportState.value = ExportProgressState.Error
                    _exportError.value = e.message ?: "An unknown error occurred during export."
                }
            }
        }
    }
}
