package com.mudasir.nexacvai.presentation.ui.templates.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.core.result.AppResult
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.model.template.*
import com.mudasir.nexacvai.domain.repository.TemplateRepository
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.presentation.ui.templates.TemplatesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val templateRepository: TemplateRepository,
    private val profileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TemplatesState(isLoading = true))
    val state: StateFlow<TemplatesState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val templatesResult = templateRepository.getAllTemplates()
            val profiles = profileRepository.getAllProfiles().first()

            val templates = if (templatesResult is AppResult.Success) templatesResult.data else emptyList()

            _state.value = _state.value.copy(
                isLoading = false,
                templates = templates,
                filteredTemplates = filterTemplates(templates, _state.value.selectedCategory, _state.value.searchQuery),
                profiles = profiles,
                selectedProfile = null,
                injectedTemplateData = null
            )
        }
    }

    fun selectCategory(category: TemplateCategory) {
        _state.value = _state.value.copy(
            selectedCategory = category,
            filteredTemplates = filterTemplates(_state.value.templates, category, _state.value.searchQuery)
        )
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredTemplates = filterTemplates(_state.value.templates, _state.value.selectedCategory, query)
        )
    }

    fun selectProfileForInjection(profile: UserProfile?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isInjectingProfile = true)
            delay(250) // Smooth shimmer skeleton transition
            _state.value = _state.value.copy(
                selectedProfile = profile,
                injectedTemplateData = profile?.toTemplateData(),
                isInjectingProfile = false
            )
        }
    }

    fun togglePhotoInTemplate(showPhoto: Boolean) {
        _state.value = _state.value.copy(showPhotoInTemplate = showPhoto)
    }

    fun openTemplateDetail(template: ResumeTemplate) {
        _state.value = _state.value.copy(selectedTemplateForDetail = template)
    }

    fun closeTemplateDetail() {
        _state.value = _state.value.copy(selectedTemplateForDetail = null)
    }

    fun importCustomTemplate(jsonSchema: String) {
        viewModelScope.launch {
            when (val result = templateRepository.importTemplateFromJson(jsonSchema)) {
                is AppResult.Success -> {
                    loadData() // Refresh list
                }
                is AppResult.Error -> {
                    _state.value = _state.value.copy(errorMessage = "Failed to import template: ${result.exception.message}")
                }
                else -> {}
            }
        }
    }

    private fun filterTemplates(
        templates: List<ResumeTemplate>,
        category: TemplateCategory,
        query: String
    ): List<ResumeTemplate> {
        return templates.filter { template ->
            val matchesCategory = (category == TemplateCategory.ALL) || (template.metadata.category == category)
            val matchesQuery = query.isBlank() ||
                    template.metadata.name.contains(query, ignoreCase = true) ||
                    template.metadata.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }
}
