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
    
    private val _pendingDeleteProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val pendingDeleteProfiles: StateFlow<List<UserProfile>> = _pendingDeleteProfiles.asStateFlow()

    private val _pendingDeleteProfile = MutableStateFlow<UserProfile?>(null)
    val pendingDeleteProfile: StateFlow<UserProfile?> = _pendingDeleteProfile.asStateFlow()
    
    private val _isFabVisible = MutableStateFlow(true)
    val isFabVisible: StateFlow<Boolean> = _isFabVisible.asStateFlow()

    private val _isSelectionModeActive = MutableStateFlow(false)
    val isSelectionModeActive: StateFlow<Boolean> = _isSelectionModeActive.asStateFlow()

    private val _lastUndoneProfileId = MutableStateFlow<Long?>(null)
    val lastUndoneProfileId: StateFlow<Long?> = _lastUndoneProfileId.asStateFlow()

    private var deleteJob: Job? = null

    fun setFabVisible(visible: Boolean) {
        _isFabVisible.value = visible
    }

    fun setSelectionModeActive(active: Boolean) {
        _isSelectionModeActive.value = active
    }

    fun clearLastUndoneProfileId() {
        _lastUndoneProfileId.value = null
    }

    /**
     * Places a list of profiles in the pending delete state and starts the countdown.
     * Any existing pending deletion is committed immediately.
     */
    fun requestDelete(profiles: List<UserProfile>, onFinished: () -> Unit = {}) {
        val previousProfiles = _pendingDeleteProfiles.value
        if (previousProfiles.isNotEmpty()) {
            deleteJob?.cancel()
            deleteJob = null
            scope.launch(NonCancellable) {
                previousProfiles.forEach { deleteProfileUseCase(it) }
            }
        }
        
        _pendingDeleteProfiles.value = profiles
        _pendingDeleteProfile.value = profiles.firstOrNull()
        
        deleteJob = scope.launch {
            delay(5000L) // 5 seconds undo window
            commitPendingDelete()
            onFinished()
        }
    }

    /**
     * Places a profile in the pending delete state and starts the countdown.
     * Any existing pending deletion is committed immediately.
     */
    fun requestDelete(profile: UserProfile, onFinished: () -> Unit = {}) {
        requestDelete(listOf(profile), onFinished)
    }

    /**
     * Restores the profiles currently pending deletion.
     */
    fun undoDelete() {
        val profiles = _pendingDeleteProfiles.value
        if (profiles.isNotEmpty()) {
            _lastUndoneProfileId.value = profiles.firstOrNull()?.id
        }
        deleteJob?.cancel()
        deleteJob = null
        _pendingDeleteProfiles.value = emptyList()
        _pendingDeleteProfile.value = null
    }

    /**
     * Commits the pending deletion immediately, removing the profiles from the database.
     */
    fun commitPendingDelete() {
        val profilesToCommit = _pendingDeleteProfiles.value
        if (profilesToCommit.isEmpty()) return
        deleteJob?.cancel()
        deleteJob = null
        
        scope.launch(NonCancellable) {
            profilesToCommit.forEach { deleteProfileUseCase(it) }
            // Allow Room Flow updates to propagate to the UI before clearing the filter
            delay(500L)
            val currentPending = _pendingDeleteProfiles.value
            _pendingDeleteProfiles.value = currentPending.filter { it !in profilesToCommit }
            if (_pendingDeleteProfile.value in profilesToCommit) {
                _pendingDeleteProfile.value = null
            }
        }
    }
}
