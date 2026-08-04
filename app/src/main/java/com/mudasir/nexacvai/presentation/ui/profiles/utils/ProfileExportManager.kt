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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the global export progress state for UserProfiles.
 * Exposes a shared state flow observed by the application root to show the custom toast overlay.
 */
@Singleton
class ProfileExportManager @Inject constructor() {
    var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    constructor(scope: CoroutineScope) : this() {
        this.scope = scope
    }

    private val _exportState = MutableStateFlow(ExportProgressState.Idle)
    val exportState: StateFlow<ExportProgressState> = _exportState.asStateFlow()

    private val _exportError = MutableStateFlow<String?>(null)
    val exportError: StateFlow<String?> = _exportError.asStateFlow()

    fun dismissExportProgress() {
        _exportState.value = ExportProgressState.Idle
        _exportError.value = null
    }

    fun exportProfileToUri(context: Context, profile: UserProfile, uri: Uri) {
        exportProfilesToUri(context, listOf(profile), uri)
    }

    fun exportProfilesToUri(context: Context, profiles: List<UserProfile>, uri: Uri) {
        scope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _exportState.value = ExportProgressState.Exporting
                _exportError.value = null
            }
            try {
                var result = false
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    result = ProfileImportExportHelper.exportProfiles(context, profiles, outputStream)
                } ?: run {
                    withContext(Dispatchers.Main) {
                        _exportState.value = ExportProgressState.Error
                        _exportError.value = "Unable to create or open file destination."
                    }
                    return@launch
                }

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
