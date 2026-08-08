package com.mudasir.nexacvai.presentation.ui.templates

import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.model.template.*

/**
 * UI state for the Templates Showcase & Selection Screen.
 */
data class TemplatesState(
    val isLoading: Boolean = false,
    val templates: List<ResumeTemplate> = emptyList(),
    val filteredTemplates: List<ResumeTemplate> = emptyList(),
    val selectedCategory: TemplateCategory = TemplateCategory.ALL,
    val searchQuery: String = "",
    val profiles: List<UserProfile> = emptyList(),
    val selectedProfile: UserProfile? = null,
    val injectedTemplateData: TemplateData? = null,
    val selectedTemplateForDetail: ResumeTemplate? = null,
    val showPhotoInTemplate: Boolean = true,
    val isInjectingProfile: Boolean = false,
    val errorMessage: String? = null
)
