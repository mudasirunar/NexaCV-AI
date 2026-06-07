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
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            combine(
                getAllProfilesUseCase(),
                profileDeleteManager.pendingDeleteProfile
            ) { profiles, pendingProfile ->
                if (pendingProfile != null) {
                    profiles.filter { it.id != pendingProfile.id }
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

                // 2. Save parent profile and nested structures in database
                val savedId = withContext(Dispatchers.IO) {
                    importProfileUseCase(profileToSave)
                }

                // 3. Port/save profile picture if present
                if (data.hasPicture && data.pictureBytes != null) {
                    val localUri = withContext(Dispatchers.IO) {
                        ProfileImportExportHelper.saveImportedProfilePicture(
                            context,
                            data.pictureBytes,
                            savedId
                        )
                    }
                    if (localUri != null) {
                        // Update profile picture uri in database
                        val updatedProfile = profileToSave.copy(
                            id = savedId,
                            profilePictureUri = localUri
                        )
                        withContext(Dispatchers.IO) {
                            importProfileUseCase(updatedProfile)
                        }
                    }
                }

                // Ensure minimum 1-second progress visibility for a satisfying UX.
                // If import took less than 1s, wait the remaining time. If it took longer, no extra delay.
                val elapsed = System.currentTimeMillis() - startTime
                val remainingDelay = (1000L - elapsed).coerceAtLeast(0L)
                if (remainingDelay > 0) kotlinx.coroutines.delay(remainingDelay)

                // 4. Update state to success
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
