package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.core.utils.ProfileImportExportHelper
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.domain.usecase.ImportProfileUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileExportManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.mudasir.nexacvai.data.local.datastore.AppSettingsManager
import com.mudasir.nexacvai.domain.model.ProfileSortOrder
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.domain.model.matchesSearchQuery
import javax.inject.Inject

enum class DuplicateResolution {
    Overwrite,
    KeepBoth,
    Skip
}

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val importProfileUseCase: ImportProfileUseCase,
    private val duplicateProfileUseCase: com.mudasir.nexacvai.domain.usecase.DuplicateProfileUseCase,
    private val userProfileRepository: UserProfileRepository,
    val profileDeleteManager: ProfileDeleteManager,
    private val profileExportManager: ProfileExportManager,
    private val appSettingsManager: AppSettingsManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfilesState())
    val state: StateFlow<ProfilesState> = _state.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        loadProfiles()
        observeSelectionMode()
    }

    private fun observeSelectionMode() {
        viewModelScope.launch {
            combine(
                profileDeleteManager.isSelectionModeActive,
                profileDeleteManager.selectedProfileIds
            ) { active, selected ->
                Pair(active, selected)
            }.collect { (active, selected) ->
                if (active) {
                    searchQueryFlow.value = ""
                }
                _state.value = _state.value.copy(
                    isSelectionMode = active,
                    selectedProfileIds = if (active) selected else emptySet(),
                    isSearchActive = if (active) false else _state.value.isSearchActive,
                    searchQuery = if (active) "" else _state.value.searchQuery
                )
            }
        }
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            if (_state.value.profiles == null) {
                _state.value = _state.value.copy(isLoading = true, error = null)
            }
            combine(
                getAllProfilesUseCase(),
                profileDeleteManager.pendingDeleteProfiles,
                appSettingsManager.profileSortOrderFlow,
                searchQueryFlow
            ) { allProfiles: List<UserProfile>, pendingDeletes: List<UserProfile>, sortOrder: ProfileSortOrder, query: String ->
                val pendingIds = pendingDeletes.map { it.id }.toSet()
                val unDeleted = allProfiles.filter { it.id !in pendingIds }
                val filtered = if (query.isBlank()) {
                    unDeleted
                } else {
                    unDeleted.filter { it.matchesSearchQuery(query) }
                }
                val sorted = when (sortOrder) {
                    ProfileSortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.createdAt }
                    ProfileSortOrder.OLDEST_FIRST -> filtered.sortedBy { it.createdAt }
                    ProfileSortOrder.NAME_ASC -> filtered.sortedBy { it.fullName.lowercase().ifBlank { "zzz" } }
                    ProfileSortOrder.NAME_DESC -> filtered.sortedByDescending { it.fullName.lowercase().ifBlank { "aaa" } }
                    ProfileSortOrder.LAST_UPDATED -> filtered.sortedByDescending { it.updatedAt }
                    ProfileSortOrder.MOST_USED -> filtered.sortedByDescending { it.updatedAt }
                }
                Pair(sorted, sortOrder)
            }
            .catch { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred while loading profiles."
                )
            }
            .collect { (visibleProfiles, sortOrder) ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    profiles = visibleProfiles,
                    sortOrder = sortOrder
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQueryFlow.value = query
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onSearchActiveChanged(active: Boolean) {
        if (!active) {
            searchQueryFlow.value = ""
            _state.value = _state.value.copy(isSearchActive = false, searchQuery = "")
        } else {
            _state.value = _state.value.copy(isSearchActive = true)
        }
    }

    fun clearSearchQuery() {
        searchQueryFlow.value = ""
        _state.value = _state.value.copy(searchQuery = "")
    }

    fun updateSortOrder(sortOrder: ProfileSortOrder) {
        viewModelScope.launch {
            appSettingsManager.setProfileSortOrder(sortOrder)
        }
    }

    fun deleteProfile(profile: UserProfile) {
        profileDeleteManager.requestDelete(profile)
    }

    fun enterSelectionMode(initialProfileId: Long? = null) {
        val selection = if (initialProfileId != null) setOf(initialProfileId) else emptySet()
        profileDeleteManager.enterSelectionMode(selection)
        _state.value = _state.value.copy(
            isSelectionMode = true,
            selectedProfileIds = selection
        )
    }

    fun exitSelectionMode() {
        profileDeleteManager.clearSelection()
        _state.value = _state.value.copy(
            isSelectionMode = false,
            selectedProfileIds = emptySet()
        )
    }

    fun toggleSelection(profileId: Long) {
        profileDeleteManager.toggleProfileSelection(profileId)
        val currentSelected = profileDeleteManager.selectedProfileIds.value
        _state.value = _state.value.copy(selectedProfileIds = currentSelected)
    }

    fun toggleSelectAll() {
        val allProfiles = _state.value.profiles ?: emptyList()
        val allIds = allProfiles.map { it.id }
        val currentSelected = _state.value.selectedProfileIds
        
        if (currentSelected.size == allIds.size) {
            profileDeleteManager.setSelectedProfiles(emptySet())
            _state.value = _state.value.copy(selectedProfileIds = emptySet())
        } else {
            profileDeleteManager.selectAllProfiles(allIds)
            _state.value = _state.value.copy(selectedProfileIds = allIds.toSet())
        }
    }

    fun deleteSelectedProfiles() {
        val selectedIds = _state.value.selectedProfileIds
        if (selectedIds.isEmpty()) return
        
        val allProfiles = _state.value.profiles ?: emptyList()
        val profilesToDelete = allProfiles.filter { it.id in selectedIds }
        
        exitSelectionMode()
        profileDeleteManager.requestDeleteMultiple(profilesToDelete)
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
                    val importedList = ProfileImportExportHelper.readProfilesFromZip(inputStream)
                    if (importedList.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            onError("Invalid or corrupted profile file.")
                        }
                        return@launch
                    }

                    val existingProfiles = _state.value.profiles ?: emptyList()
                    val existingIds = existingProfiles.map { it.id }.toSet()

                    val duplicates = importedList.filter { it.profile.id in existingIds }

                    withContext(Dispatchers.Main) {
                        if (duplicates.isNotEmpty()) {
                            _state.value = _state.value.copy(
                                importState = ImportProgressState.DuplicateSelection,
                                importedProfileData = importedList.firstOrNull(),
                                importedProfileDataList = importedList
                            )
                        } else {
                            _state.value = _state.value.copy(
                                importedProfileDataList = importedList
                            )
                            executeMultiImport(context, emptyMap())
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
        val singleData = _state.value.importedProfileData
        val list = _state.value.importedProfileDataList.ifEmpty {
            singleData?.let { listOf(it) } ?: emptyList()
        }
        val map = list.associate { it.profile.id to duplicateResolution }
        executeMultiImport(context, map)
    }

    fun executeMultiImport(context: Context, perProfileResolutions: Map<Long, DuplicateResolution>) {
        val list = _state.value.importedProfileDataList.ifEmpty {
            _state.value.importedProfileData?.let { listOf(it) } ?: emptyList()
        }
        if (list.isEmpty()) return

        val itemsToImport = list.filter { data ->
            val res = perProfileResolutions[data.profile.id] ?: DuplicateResolution.Overwrite
            res != DuplicateResolution.Skip
        }

        if (itemsToImport.isEmpty()) {
            _state.value = _state.value.copy(
                importState = ImportProgressState.Success,
                newlyImportedProfileId = null,
                importedCount = 0
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(importState = ImportProgressState.Importing)
            try {
                val startTime = System.currentTimeMillis()
                var lastSavedId: Long? = null
                var successCount = 0

                list.forEach { data ->
                    val resolution = perProfileResolutions[data.profile.id] ?: DuplicateResolution.Overwrite
                    if (resolution != DuplicateResolution.Skip) {
                        lastSavedId = importSingleProfileData(context, data, resolution)
                        successCount++
                    }
                }

                val elapsed = System.currentTimeMillis() - startTime
                val remainingDelay = (1000L - elapsed).coerceAtLeast(0L)
                if (remainingDelay > 0) kotlinx.coroutines.delay(remainingDelay)

                _state.value = _state.value.copy(
                    importState = ImportProgressState.Success,
                    newlyImportedProfileId = lastSavedId,
                    importedCount = successCount
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(
                    importState = ImportProgressState.Idle,
                    importedProfileData = null,
                    importedProfileDataList = emptyList(),
                    importedCount = 0,
                    error = "Failed to import profiles: ${e.message}"
                )
            }
        }
    }

    private suspend fun importSingleProfileData(
        context: Context,
        data: ProfileImportExportHelper.ImportedProfileData,
        duplicateResolution: DuplicateResolution
    ): Long {
        val now = System.currentTimeMillis()
        var profileToSave = data.profile

        val newExperiences = profileToSave.experiences.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
        val newProjects = profileToSave.projects.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
        val newEducations = profileToSave.educations.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
        val newCertifications = profileToSave.certifications.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
        val newReferences = profileToSave.references.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
        val newSocialLinks = profileToSave.socialLinks.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
        val newLanguages = profileToSave.languages.map { it.copy(id = java.util.UUID.randomUUID().toString()) }

        when (duplicateResolution) {
            DuplicateResolution.KeepBoth -> {
                profileToSave = profileToSave.copy(
                    id = 0L,
                    experiences = newExperiences,
                    projects = newProjects,
                    educations = newEducations,
                    certifications = newCertifications,
                    references = newReferences,
                    socialLinks = newSocialLinks,
                    languages = newLanguages,
                    createdAt = now,
                    updatedAt = now
                )
            }
            DuplicateResolution.Overwrite -> {
                val existingProfile = userProfileRepository.getProfileById(profileToSave.id)
                val preservedCreatedAt = existingProfile?.createdAt ?: now

                profileToSave = profileToSave.copy(
                    experiences = newExperiences,
                    projects = newProjects,
                    educations = newEducations,
                    certifications = newCertifications,
                    references = newReferences,
                    socialLinks = newSocialLinks,
                    languages = newLanguages,
                    createdAt = preservedCreatedAt,
                    updatedAt = now
                )
            }
            DuplicateResolution.Skip -> return data.profile.id
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
        return savedId
    }

    fun cancelImport() {
        _state.value = _state.value.copy(
            importState = ImportProgressState.Idle,
            importedProfileData = null,
            importedProfileDataList = emptyList(),
            newlyImportedProfileId = null,
            importedCount = 0
        )
    }

    // ---------------------------------------------------------------------------
    // Export System Logic
    // ---------------------------------------------------------------------------

    fun selectProfileForExport(profile: UserProfile) {
        _state.value = _state.value.copy(
            exportingProfile = profile,
            exportingProfilesList = listOf(profile),
            showExportConfirm = true
        )
    }

    fun selectProfilesForExport(profiles: List<UserProfile>) {
        if (profiles.isEmpty()) return
        _state.value = _state.value.copy(
            exportingProfilesList = profiles,
            exportingProfile = profiles.firstOrNull(),
            showExportConfirm = true
        )
    }

    fun selectAllProfilesForExport() {
        val allProfiles = _state.value.profiles ?: emptyList()
        selectProfilesForExport(allProfiles)
    }

    fun dismissExportConfirm() {
        _state.value = _state.value.copy(
            exportingProfile = null,
            exportingProfilesList = emptyList(),
            showExportConfirm = false
        )
    }

    fun hideExportDialog() {
        _state.value = _state.value.copy(
            showExportConfirm = false
        )
    }

    fun exportProfileToUri(context: Context, uri: Uri) {
        val profilesToExport = if (_state.value.exportingProfilesList.isNotEmpty()) {
            _state.value.exportingProfilesList
        } else if (_state.value.exportingProfile != null) {
            listOf(_state.value.exportingProfile!!)
        } else {
            emptyList()
        }

        if (profilesToExport.isEmpty()) return

        profileExportManager.exportProfilesToUri(context, profilesToExport, uri)
        dismissExportConfirm()
        profileDeleteManager.clearSelection()
    }

    fun duplicateSelectedProfiles(context: Context) {
        val selectedIds = _state.value.selectedProfileIds
        val allProfiles = _state.value.profiles ?: emptyList()
        val targets = allProfiles.filter { it.id in selectedIds }
        if (targets.isEmpty()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(duplicateState = DuplicateProgressState.Duplicating)
            val startTime = System.currentTimeMillis()
            var lastId: Long? = null

            targets.forEach { profile ->
                lastId = duplicateProfileUseCase(context, profile)
            }

            val elapsed = System.currentTimeMillis() - startTime
            val remaining = (800L - elapsed).coerceAtLeast(0L)
            if (remaining > 0) kotlinx.coroutines.delay(remaining)

            _state.value = _state.value.copy(
                duplicateState = DuplicateProgressState.Success,
                duplicatedCount = targets.size,
                newlyDuplicatedProfileId = lastId,
                duplicatedProfileName = targets.lastOrNull()?.fullName ?: ""
            )
            profileDeleteManager.clearSelection()
        }
    }

    fun dismissDuplicateSheet() {
        _state.value = _state.value.copy(
            duplicateState = DuplicateProgressState.Idle,
            duplicatedCount = 0,
            newlyDuplicatedProfileId = null,
            duplicatedProfileName = ""
        )
    }

    fun removeSourceProfileTag(profileId: Long) {
        viewModelScope.launch {
            userProfileRepository.dismissCopyTag(profileId)
        }
    }
}
