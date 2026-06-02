package com.mudasir.nexacvai.presentation.ui.profiles.utils

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.DeleteProfileUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the temporary deletion state for UserProfiles.
 * Coordinates UI invisibility (undo period) and permanent database execution.
 */
class ProfileDeleteManager(
    private val deleteProfileUseCase: DeleteProfileUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {
    
    private val _pendingDeleteProfile = MutableStateFlow<UserProfile?>(null)
    val pendingDeleteProfile: StateFlow<UserProfile?> = _pendingDeleteProfile.asStateFlow()
    
    private val _isFabVisible = MutableStateFlow(true)
    val isFabVisible: StateFlow<Boolean> = _isFabVisible.asStateFlow()

    private val _lastUndoneProfileId = MutableStateFlow<Long?>(null)
    val lastUndoneProfileId: StateFlow<Long?> = _lastUndoneProfileId.asStateFlow()

    private var deleteJob: Job? = null

    fun setFabVisible(visible: Boolean) {
        _isFabVisible.value = visible
    }

    fun clearLastUndoneProfileId() {
        _lastUndoneProfileId.value = null
    }

    /**
     * Places a profile in the pending delete state and starts the countdown.
     * Any existing pending deletion is committed immediately.
     */
    fun requestDelete(profile: UserProfile, onFinished: () -> Unit = {}) {
        val previousProfile = _pendingDeleteProfile.value
        if (previousProfile != null) {
            deleteJob?.cancel()
            deleteJob = null
            scope.launch(NonCancellable) {
                deleteProfileUseCase(previousProfile)
            }
        }
        
        _pendingDeleteProfile.value = profile
        
        deleteJob = scope.launch {
            delay(5000L) // 5 seconds undo window
            commitPendingDelete()
            onFinished()
        }
    }

    /**
     * Restores the profile currently pending deletion.
     */
    fun undoDelete() {
        val profile = _pendingDeleteProfile.value
        if (profile != null) {
            _lastUndoneProfileId.value = profile.id
        }
        deleteJob?.cancel()
        deleteJob = null
        _pendingDeleteProfile.value = null
    }

    /**
     * Commits the pending deletion immediately, removing the profile from the database.
     */
    fun commitPendingDelete() {
        val profile = _pendingDeleteProfile.value ?: return
        deleteJob?.cancel()
        deleteJob = null
        _pendingDeleteProfile.value = null
        
        scope.launch(NonCancellable) {
            deleteProfileUseCase(profile)
        }
    }
}
