package com.mudasir.nexacvai.presentation.ui.profiles.viewmodel

import androidx.compose.runtime.Immutable
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
    val importedProfileData: com.mudasir.nexacvai.core.utils.ProfileImportExportHelper.ImportedProfileData? = null,
    val newlyImportedProfileId: Long? = null,
    val exportingProfile: UserProfile? = null,
    val showExportConfirm: Boolean = false,
    val exportState: ExportProgressState = ExportProgressState.Idle,
    val exportError: String? = null,
    
    // Selection States
    val isSelectionMode: Boolean = false,
    val selectedProfileIds: Set<Long> = emptySet()
)
