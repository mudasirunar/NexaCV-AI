package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.compose.runtime.Immutable
import com.mudasir.nexacvai.core.utils.ProfileImportExportHelper
import com.mudasir.nexacvai.domain.model.UserProfile

enum class ImportProgressState {
    Idle,
    Importing,
    DuplicateSelection,
    Success
}

enum class ExportProgressState {
    Idle,
    Exporting,
    Success,
    Error
}

@Immutable
data class ProfilesState(
    val isLoading: Boolean = false,
    val profiles: List<UserProfile>? = null,
    val error: String? = null,
    
    // Import/Export States
    val importState: ImportProgressState = ImportProgressState.Idle,
    val importedProfileData: ProfileImportExportHelper.ImportedProfileData? = null,
    val importedProfileDataList: List<ProfileImportExportHelper.ImportedProfileData> = emptyList(),
    val newlyImportedProfileId: Long? = null,
    val importedCount: Int = 0,
    val exportingProfile: UserProfile? = null,
    val exportingProfilesList: List<UserProfile> = emptyList(),
    val showExportConfirm: Boolean = false,
    val exportState: ExportProgressState = ExportProgressState.Idle,
    val exportError: String? = null,
    
    // Selection States
    val isSelectionMode: Boolean = false,
    val selectedProfileIds: Set<Long> = emptySet()
)
