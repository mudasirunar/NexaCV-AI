package com.mudasir.nexacvai.presentation.ui.templates.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudasir.nexacvai.core.pdf.PdfGeneratorEngine
import com.mudasir.nexacvai.core.pdf.TemplateThumbnailGenerator
import com.mudasir.nexacvai.core.result.AppResult
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.model.template.*
import com.mudasir.nexacvai.domain.repository.TemplateRepository
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.presentation.ui.templates.TemplatesState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val templateRepository: TemplateRepository,
    private val profileRepository: UserProfileRepository,
    private val pdfEngine: PdfGeneratorEngine
) : ViewModel() {

    private val _state = MutableStateFlow(TemplatesState(isLoading = true))
    val state: StateFlow<TemplatesState> = _state.asStateFlow()

    init {
        loadData()
        observeProfiles()
    }

    private fun observeProfiles() {
        profileRepository.getAllProfiles()
            .onEach { profiles ->
                _state.value = _state.value.copy(profiles = profiles)
            }
            .launchIn(viewModelScope)
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val templatesResult = templateRepository.getAllTemplates()
            val profiles = profileRepository.getAllProfiles().firstOrNull() ?: emptyList()

            val templates = if (templatesResult is AppResult.Success) templatesResult.data else emptyList()

            _state.value = _state.value.copy(
                isLoading = false,
                templates = templates,
                filteredTemplates = filterTemplates(templates, _state.value.selectedCategory, _state.value.searchQuery),
                profiles = profiles
            )

            // Warm up real PDF thumbnail cache in background
            viewModelScope.launch(Dispatchers.IO) {
                templates.forEach { template ->
                    TemplateThumbnailGenerator.generateThumbnail(context, pdfEngine, template)
                }
            }
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

    fun toggleTemplateFlip(templateId: String) {
        val currentFlipped = _state.value.flippedTemplateIds
        val updated = if (currentFlipped.contains(templateId)) {
            currentFlipped - templateId
        } else {
            currentFlipped + templateId
        }
        _state.value = _state.value.copy(flippedTemplateIds = updated)
    }

    fun resetFlippedTemplates() {
        if (_state.value.flippedTemplateIds.isNotEmpty()) {
            _state.value = _state.value.copy(flippedTemplateIds = emptySet())
        }
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
        val trimmed = query.trim()
        val tokens = if (trimmed.isNotEmpty()) trimmed.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() } else emptyList()

        val categoryFiltered = if (category == TemplateCategory.ALL) {
            templates
        } else {
            templates.filter { it.metadata.category == category }
        }

        if (tokens.isEmpty()) {
            return categoryFiltered
        }

        return categoryFiltered
            .mapNotNull { template ->
                val score = calculateMatchScore(template, trimmed.lowercase(), tokens)
                if (score > 0) template to score else null
            }
            .sortedWith(
                compareByDescending<Pair<ResumeTemplate, Int>> { it.second }
                    .thenBy { it.first.metadata.name }
            )
            .map { it.first }
    }

    private fun calculateMatchScore(
        template: ResumeTemplate,
        fullQuery: String,
        tokens: List<String>
    ): Int {
        val meta = template.metadata
        val nameLower = meta.name.lowercase()
        val descLower = meta.description.lowercase()
        val catNameLower = meta.category.displayName.lowercase()
        val colorKeywords = resolveColorKeywords(meta.previewPrimaryColorHex, meta.previewAccentColorHex)
        val photoKeywords = if (meta.supportsPhoto) listOf("photo", "picture", "avatar", "headshot", "image") else listOf("no photo", "text only", "ats only", "without photo")

        var score = 0

        // Exact name match
        if (nameLower == fullQuery) {
            return 1000
        }
        if (nameLower.startsWith(fullQuery)) {
            score += 300
        } else if (nameLower.contains(fullQuery)) {
            score += 150
        }

        if (catNameLower.contains(fullQuery)) {
            score += 80
        }

        if (descLower.contains(fullQuery)) {
            score += 60
        }

        // Token-level multi-match
        var matchedTokens = 0
        for (token in tokens) {
            var tokenScore = 0
            if (nameLower.contains(token)) {
                tokenScore += 40
            }
            if (descLower.contains(token)) {
                tokenScore += 25
            }
            if (catNameLower.contains(token)) {
                tokenScore += 30
            }
            if (colorKeywords.any { it.contains(token) || token.contains(it) }) {
                tokenScore += 20
            }
            if (photoKeywords.any { it.contains(token) || token.contains(it) }) {
                tokenScore += 20
            }

            if (tokenScore > 0) {
                matchedTokens++
                score += tokenScore
            }
        }

        // All search tokens must match at least one attribute to qualify
        return if (matchedTokens == tokens.size) score else 0
    }

    private fun resolveColorKeywords(primaryHex: String, accentHex: String): List<String> {
        val hexes = listOf(primaryHex.uppercase(), accentHex.uppercase())
        val keywords = mutableListOf<String>()

        for (hex in hexes) {
            when {
                hex.contains("1E293B") || hex.contains("0F172A") || hex.contains("334155") || hex.contains("374151") -> {
                    keywords.addAll(listOf("slate", "dark", "charcoal", "gray", "black", "minimal"))
                }
                hex.contains("1E1B4B") || hex.contains("312E81") || hex.contains("4338CA") -> {
                    keywords.addAll(listOf("navy", "indigo", "dark blue", "midnight", "executive"))
                }
                hex.contains("0284C7") || hex.contains("0369A1") || hex.contains("2563EB") || hex.contains("38BDF8") || hex.contains("0EA5E9") || hex.contains("3B82F6") -> {
                    keywords.addAll(listOf("blue", "sky blue", "cyan", "azure", "ocean", "tech"))
                }
                hex.contains("0D9488") || hex.contains("14B8A6") || hex.contains("2DD4BF") -> {
                    keywords.addAll(listOf("teal", "cyan", "turquoise", "aqua", "cyber"))
                }
                hex.contains("059669") || hex.contains("10B981") -> {
                    keywords.addAll(listOf("green", "emerald", "mint", "healthcare"))
                }
                hex.contains("4F46E5") || hex.contains("6366F1") || hex.contains("9333EA") || hex.contains("A855F7") -> {
                    keywords.addAll(listOf("purple", "violet", "indigo", "lavender", "creative"))
                }
                hex.contains("E11D48") || hex.contains("F43F5E") -> {
                    keywords.addAll(listOf("rose", "red", "crimson", "coral", "pink", "creative"))
                }
                hex.contains("B45309") || hex.contains("D97706") || hex.contains("F59E0B") -> {
                    keywords.addAll(listOf("gold", "amber", "yellow", "orange", "bronze", "warm", "banking"))
                }
            }
        }
        return keywords.distinct()
    }
}
