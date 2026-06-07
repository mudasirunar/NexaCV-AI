package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileExportManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.mudasir.nexacvai.domain.usecase.ImportProfileUseCase
import com.mudasir.nexacvai.core.utils.ProfileImportExportHelper
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class DuplicateResolution {
    Overwrite,
    KeepBoth
}

class ProfilesViewModel(
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val importProfileUseCase: ImportProfileUseCase,
    val profileDeleteManager: ProfileDeleteManager,
    private val profileExportManager: ProfileExportManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfilesState())
    val state: StateFlow<ProfilesState> = _state.asStateFlow()

    init {
        loadProfiles()
        observeSelectionMode()
    }

    private fun observeSelectionMode() {
        viewModelScope.launch {
            profileDeleteManager.isSelectionModeActive.collect { active ->
                if (!active && _state.value.isSelectionMode) {
                    _state.value = _state.value.copy(
                        isSelectionMode = false,
                        selectedProfileIds = emptySet()
                    )
                }
            }
        }
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            combine(
                getAllProfilesUseCase(),
                profileDeleteManager.pendingDeleteProfiles
            ) { profiles, pendingList ->
                val pendingIds = pendingList.map { it.id }.toSet()
                if (pendingIds.isNotEmpty()) {
                    profiles.filter { it.id !in pendingIds }
                } else {
                    profiles
                }
            }
            .catch { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )
            }
            .collect { filteredProfiles ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    profiles = filteredProfiles
                )
            }
        }
    }

    fun deleteProfile(profile: UserProfile) {
        profileDeleteManager.requestDelete(profile)
    }

    fun enterSelectionMode(initialProfileId: Long? = null) {
        profileDeleteManager.setSelectionModeActive(true)
        val selection = if (initialProfileId != null) setOf(initialProfileId) else emptySet()
        _state.value = _state.value.copy(
            isSelectionMode = true,
            selectedProfileIds = selection
        )
    }

    fun exitSelectionMode() {
        profileDeleteManager.setSelectionModeActive(false)
        _state.value = _state.value.copy(
            isSelectionMode = false,
            selectedProfileIds = emptySet()
        )
    }

    fun toggleSelection(profileId: Long) {
        val currentSelected = _state.value.selectedProfileIds.toMutableSet()
        if (currentSelected.contains(profileId)) {
            currentSelected.remove(profileId)
        } else {
            currentSelected.add(profileId)
        }
        
        if (currentSelected.isEmpty()) {
            exitSelectionMode()
        } else {
            _state.value = _state.value.copy(selectedProfileIds = currentSelected)
        }
    }

    fun toggleSelectAll() {
        val allProfiles = _state.value.profiles ?: emptyList()
        val allIds = allProfiles.map { it.id }.toSet()
        val currentSelected = _state.value.selectedProfileIds
        
        if (currentSelected.size == allIds.size) {
            _state.value = _state.value.copy(selectedProfileIds = emptySet())
        } else {
            _state.value = _state.value.copy(selectedProfileIds = allIds)
        }
    }

    fun deleteSelectedProfiles() {
        val selectedIds = _state.value.selectedProfileIds
        if (selectedIds.isEmpty()) return
        
        val allProfiles = _state.value.profiles ?: emptyList()
        val profilesToDelete = allProfiles.filter { it.id in selectedIds }
        
        exitSelectionMode()
        profileDeleteManager.requestDelete(profilesToDelete)
    }

    fun removeProfilePicture(profile: UserProfile) {
        viewModelScope.launch {
            val updated = profile.copy(
                profilePictureUri = null,
                updatedAt = System.currentTimeMillis()
            )
            saveProfileUseCase(updated)
        }
    }

    fun updateProfilePicture(profile: UserProfile, pictureUri: String?) {
        viewModelScope.launch {
            val updated = profile.copy(
                profilePictureUri = pictureUri,
                updatedAt = System.currentTimeMillis()
            )
            saveProfileUseCase(updated)
        }
    }

    // ---------------------------------------------------------------------------
    // Import System Logic
    // ---------------------------------------------------------------------------

    fun startImport(context: Context, uri: Uri, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val importedData = ProfileImportExportHelper.readProfileFromZip(inputStream)
                    if (importedData == null) {
                        withContext(Dispatchers.Main) {
                            onError("Invalid or corrupted profile file.")
                        }
                        return@launch
                    }

                    // Check for duplicate profile ID
                    val existingProfiles = _state.value.profiles ?: emptyList()
                    val isDuplicate = existingProfiles.any { it.id == importedData.profile.id }

                    withContext(Dispatchers.Main) {
                        if (isDuplicate) {
                            _state.value = _state.value.copy(
                                importState = ImportProgressState.DuplicateSelection,
                                importedProfileData = importedData
                            )
                        } else {
                            // Direct import if no duplicate
                            _state.value = _state.value.copy(
                                importedProfileData = importedData
                            )
                            executeImport(context, DuplicateResolution.Overwrite)
                        }
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        onError("Unable to open file.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onError("Failed to read file: ${e.message}")
                }
            }
        }
    }

    fun executeImport(context: Context, duplicateResolution: DuplicateResolution) {
        val data = _state.value.importedProfileData ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(importState = ImportProgressState.Importing)
            try {
                // Track start time so we can ensure minimum 1-second progress visibility
                val startTime = System.currentTimeMillis()

                var profileToSave = data.profile

                if (duplicateResolution == DuplicateResolution.KeepBoth) {
                    // Reset profile ID to 0 to generate a new key and update sub-entities to new unique UUIDs
                    val newExperiences = profileToSave.experiences.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newProjects = profileToSave.projects.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newEducations = profileToSave.educations.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newCertifications = profileToSave.certifications.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newReferences = profileToSave.references.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newSocialLinks = profileToSave.socialLinks.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newLanguages = profileToSave.languages.map { it.copy(id = java.util.UUID.randomUUID().toString()) }

                    profileToSave = profileToSave.copy(
                        id = 0L,
                        experiences = newExperiences,
                        projects = newProjects,
                        educations = newEducations,
                        certifications = newCertifications,
                        references = newReferences,
                        socialLinks = newSocialLinks,
                        languages = newLanguages,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                } else {
                    // Overwrite or regular import: regenerate sub-entity UUIDs to prevent any key collisions with other profiles
                    val newExperiences = profileToSave.experiences.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newProjects = profileToSave.projects.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newEducations = profileToSave.educations.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newCertifications = profileToSave.certifications.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newReferences = profileToSave.references.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newSocialLinks = profileToSave.socialLinks.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
                    val newLanguages = profileToSave.languages.map { it.copy(id = java.util.UUID.randomUUID().toString()) }

                    profileToSave = profileToSave.copy(
                        experiences = newExperiences,
                        projects = newProjects,
                        educations = newEducations,
                        certifications = newCertifications,
                        references = newReferences,
                        socialLinks = newSocialLinks,
                        languages = newLanguages,
                        updatedAt = System.currentTimeMillis()
                    )
                }

                val savedId = withContext(Dispatchers.IO) {
                    importProfileUseCase(profileToSave)
                }

                if (data.hasPicture && data.pictureBytes != null) {
                    val localUri = withContext(Dispatchers.IO) {
                        ProfileImportExportHelper.saveImportedProfilePicture(
                            context,
                            data.pictureBytes,
                            savedId
                        )
                    }
                    if (localUri != null) {
                        val updatedProfile = profileToSave.copy(
                            id = savedId,
                            profilePictureUri = localUri
                        )
                        withContext(Dispatchers.IO) {
                            importProfileUseCase(updatedProfile)
                        }
                    }
                }

                val elapsed = System.currentTimeMillis() - startTime
                val remainingDelay = (1000L - elapsed).coerceAtLeast(0L)
                if (remainingDelay > 0) kotlinx.coroutines.delay(remainingDelay)

                _state.value = _state.value.copy(
                    importState = ImportProgressState.Success,
                    newlyImportedProfileId = savedId
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(
                    importState = ImportProgressState.Idle,
                    importedProfileData = null,
                    error = "Failed to import profile: ${e.message}"
                )
            }
        }
    }

    fun cancelImport() {
        _state.value = _state.value.copy(
            importState = ImportProgressState.Idle,
            importedProfileData = null,
            newlyImportedProfileId = null
        )
    }

    // ---------------------------------------------------------------------------
    // Export System Logic
    // ---------------------------------------------------------------------------

    fun selectProfileForExport(profile: UserProfile) {
        _state.value = _state.value.copy(
            exportingProfile = profile,
            showExportConfirm = true
        )
    }

    fun dismissExportConfirm() {
        _state.value = _state.value.copy(
            exportingProfile = null,
            showExportConfirm = false
        )
    }

    fun hideExportDialog() {
        _state.value = _state.value.copy(
            showExportConfirm = false
        )
    }

    fun exportProfileToUri(context: Context, uri: Uri) {
        val profile = _state.value.exportingProfile ?: return
        profileExportManager.exportProfileToUri(context, profile, uri)
        dismissExportConfirm()
    }
}
