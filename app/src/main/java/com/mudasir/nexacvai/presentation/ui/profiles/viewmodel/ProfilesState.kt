package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.compose.runtime.Immutable
import com.mudasir.nexacvai.core.utils.ProfileImportExportHelper
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.model.ProfileSortOrder

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

enum class DuplicateProgressState {
    Idle,
    Duplicating,
    Success
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
    
    // Duplication States
    val duplicateState: DuplicateProgressState = DuplicateProgressState.Idle,
    val duplicatedCount: Int = 0,
    val newlyDuplicatedProfileId: Long? = null,
    val duplicatedProfileName: String = "",

    // Selection States
    val isSelectionMode: Boolean = false,
    val selectedProfileIds: Set<Long> = emptySet(),

    // Sorting State
    val sortOrder: ProfileSortOrder = ProfileSortOrder.NEWEST_FIRST,

    // Search State
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)
