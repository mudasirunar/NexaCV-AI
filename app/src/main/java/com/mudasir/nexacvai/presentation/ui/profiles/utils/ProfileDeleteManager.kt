package com.mudasir.nexacvai.presentation.ui.profiles.utils

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.DeleteProfileUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the temporary deletion state for UserProfiles.
 * Coordinates UI invisibility (undo period) and permanent database execution.
 */
@Singleton
class ProfileDeleteManager @Inject constructor(
    private val deleteProfileUseCase: DeleteProfileUseCase
) {
    var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    constructor(
        deleteProfileUseCase: DeleteProfileUseCase,
        scope: CoroutineScope
    ) : this(deleteProfileUseCase) {
        this.scope = scope
    }

    private val _pendingDeleteProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val pendingDeleteProfiles: StateFlow<List<UserProfile>> = _pendingDeleteProfiles.asStateFlow()

    private val _pendingDeleteProfile = MutableStateFlow<UserProfile?>(null)
    val pendingDeleteProfile: StateFlow<UserProfile?> = _pendingDeleteProfile.asStateFlow()
    
    private val _isFabVisible = MutableStateFlow(true)
    val isFabVisible: StateFlow<Boolean> = _isFabVisible.asStateFlow()

    private val _isSelectionModeActive = MutableStateFlow(false)
    val isSelectionModeActive: StateFlow<Boolean> = _isSelectionModeActive.asStateFlow()

    private val _selectedProfileIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedProfileIds: StateFlow<Set<Long>> = _selectedProfileIds.asStateFlow()

    private val _lastUndoneProfileId = MutableStateFlow<Long?>(null)
    val lastUndoneProfileId: StateFlow<Long?> = _lastUndoneProfileId.asStateFlow()

    private var autoCommitJob: Job? = null

    fun setFabVisible(visible: Boolean) {
        _isFabVisible.value = visible
    }

    fun clearLastUndoneProfileId() {
        _lastUndoneProfileId.value = null
    }

    fun setSelectionModeActive(active: Boolean) {
        _isSelectionModeActive.value = active
        if (!active) {
            _selectedProfileIds.value = emptySet()
        }
    }

    fun toggleProfileSelection(profileId: Long) {
        val currentSet = _selectedProfileIds.value.toMutableSet()
        if (currentSet.contains(profileId)) {
            currentSet.remove(profileId)
        } else {
            currentSet.add(profileId)
        }
        _selectedProfileIds.value = currentSet
        if (currentSet.isEmpty() && _isSelectionModeActive.value) {
            _isSelectionModeActive.value = false
        }
    }

    fun selectAllProfiles(allProfileIds: List<Long>) {
        _selectedProfileIds.value = allProfileIds.toSet()
        _isSelectionModeActive.value = true
    }

    fun clearSelection() {
        _selectedProfileIds.value = emptySet()
        _isSelectionModeActive.value = false
    }

    fun requestDelete(profile: UserProfile) {
        commitPendingDelete()
        _pendingDeleteProfile.value = profile
        _pendingDeleteProfiles.value = listOf(profile)
        scheduleAutoCommit()
    }

    fun requestDeleteMultiple(profiles: List<UserProfile>) {
        if (profiles.isEmpty()) return
        commitPendingDelete()
        _pendingDeleteProfiles.value = profiles
        _pendingDeleteProfile.value = profiles.first()
        clearSelection()
        scheduleAutoCommit()
    }

    fun undoDelete() {
        autoCommitJob?.cancel()
        autoCommitJob = null
        val undoneId = _pendingDeleteProfile.value?.id ?: _pendingDeleteProfiles.value.firstOrNull()?.id
        _lastUndoneProfileId.value = undoneId
        _pendingDeleteProfile.value = null
        _pendingDeleteProfiles.value = emptyList()
        _isFabVisible.value = true
    }

    fun commitPendingDelete() {
        autoCommitJob?.cancel()
        autoCommitJob = null
        val profilesToDelete = _pendingDeleteProfiles.value
        val singleProfile = _pendingDeleteProfile.value

        if (profilesToDelete.isNotEmpty()) {
            _pendingDeleteProfiles.value = emptyList()
            _pendingDeleteProfile.value = null
            _isFabVisible.value = true
            scope.launch {
                profilesToDelete.forEach { profile ->
                    deleteProfileUseCase(profile)
                }
            }
        } else if (singleProfile != null) {
            _pendingDeleteProfile.value = null
            _isFabVisible.value = true
            scope.launch {
                deleteProfileUseCase(singleProfile)
            }
        }
    }

    private fun scheduleAutoCommit() {
        autoCommitJob?.cancel()
        autoCommitJob = scope.launch {
            delay(5000)
            commitPendingDelete()
        }
    }
}
