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

    private val initialTemplates = com.mudasir.nexacvai.data.templates.BuiltInTemplatesCatalog.ALL_TEMPLATES
    private val _state = MutableStateFlow(
        TemplatesState(
            isLoading = false,
            templates = initialTemplates,
            filteredTemplates = initialTemplates
        )
    )
    val state: StateFlow<TemplatesState> = _state.asStateFlow()

    init {
        loadData()
        observeProfiles()
        observeFavorites()
    }

    private fun observeProfiles() {
        profileRepository.getAllProfiles()
            .onEach { profiles ->
                _state.value = _state.value.copy(profiles = profiles)
            }
            .launchIn(viewModelScope)
    }

    private fun observeFavorites() {
        templateRepository.getFavoriteTemplateIds()
            .onEach { favorites ->
                val favFlipped = _state.value.flippedTemplateIdsByCategory[TemplateCategory.FAVORITES] ?: emptySet()
                val cleanedFavFlipped = favFlipped.intersect(favorites)
                val updatedFlippedMap = if (favFlipped != cleanedFavFlipped) {
                    _state.value.flippedTemplateIdsByCategory + (TemplateCategory.FAVORITES to cleanedFavFlipped)
                } else {
                    _state.value.flippedTemplateIdsByCategory
                }

                _state.value = _state.value.copy(
                    favoriteTemplateIds = favorites,
                    flippedTemplateIdsByCategory = updatedFlippedMap,
                    filteredTemplates = filterTemplates(
                        templates = _state.value.templates,
                        category = _state.value.selectedCategory,
                        query = _state.value.searchQuery,
                        favorites = favorites
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    fun loadData() {
        viewModelScope.launch {
            val templatesResult = templateRepository.getAllTemplates()
            val profiles = profileRepository.getAllProfiles().firstOrNull() ?: emptyList()

            val templates = if (templatesResult is AppResult.Success) templatesResult.data else _state.value.templates

            _state.value = _state.value.copy(
                isLoading = false,
                templates = templates,
                filteredTemplates = filterTemplates(templates, _state.value.selectedCategory, _state.value.searchQuery, _state.value.favoriteTemplateIds),
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
            filteredTemplates = filterTemplates(_state.value.templates, category, _state.value.searchQuery, _state.value.favoriteTemplateIds)
        )
    }

    fun updateSearchQuery(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredTemplates = filterTemplates(_state.value.templates, _state.value.selectedCategory, query, _state.value.favoriteTemplateIds)
        )
    }

    fun toggleFavorite(templateId: String) {
        val favFlipped = _state.value.flippedTemplateIdsByCategory[TemplateCategory.FAVORITES] ?: emptySet()
        if (favFlipped.contains(templateId)) {
            val updatedFavFlipped = favFlipped - templateId
            _state.value = _state.value.copy(
                flippedTemplateIdsByCategory = _state.value.flippedTemplateIdsByCategory + (TemplateCategory.FAVORITES to updatedFavFlipped)
            )
        }
        viewModelScope.launch {
            templateRepository.toggleFavorite(templateId)
        }
    }

    fun addFavorite(templateId: String) {
        val favFlipped = _state.value.flippedTemplateIdsByCategory[TemplateCategory.FAVORITES] ?: emptySet()
        if (favFlipped.contains(templateId)) {
            val updatedFavFlipped = favFlipped - templateId
            _state.value = _state.value.copy(
                flippedTemplateIdsByCategory = _state.value.flippedTemplateIdsByCategory + (TemplateCategory.FAVORITES to updatedFavFlipped)
            )
        }
        viewModelScope.launch {
            templateRepository.addFavorite(templateId)
        }
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

    fun toggleTemplateFlip(templateId: String, category: TemplateCategory = _state.value.selectedCategory) {
        val currentFlippedForCat = _state.value.flippedTemplateIdsByCategory[category] ?: emptySet()
        val updatedForCat = if (currentFlippedForCat.contains(templateId)) {
            currentFlippedForCat - templateId
        } else {
            currentFlippedForCat + templateId
        }
        _state.value = _state.value.copy(
            flippedTemplateIdsByCategory = _state.value.flippedTemplateIdsByCategory + (category to updatedForCat)
        )
    }

    fun resetFlippedTemplates() {
        if (_state.value.flippedTemplateIdsByCategory.isNotEmpty()) {
            _state.value = _state.value.copy(flippedTemplateIdsByCategory = emptyMap())
        }
    }

    fun openTemplateDetail(template: ResumeTemplate) {
        _state.value = _state.value.copy(selectedTemplateForDetail = template)
    }

    fun closeTemplateDetail() {
        _state.value = _state.value.copy(selectedTemplateForDetail = null)
    }

    fun importTemplate(jsonSchema: String) {
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
        query: String,
        favorites: Set<String> = emptySet()
    ): List<ResumeTemplate> {
        val trimmed = query.trim()
        val tokens = if (trimmed.isNotEmpty()) trimmed.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() } else emptyList()

        val categoryFiltered = when (category) {
            TemplateCategory.ALL -> templates
            TemplateCategory.FAVORITES -> templates.filter { favorites.contains(it.metadata.id) }
            TemplateCategory.CUSTOM -> templates.filter { it.metadata.isImported || it.metadata.category == TemplateCategory.CUSTOM }
            else -> templates.filter { it.metadata.category == category }
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
        val keywords = mutableListOf<String>()
        val combined = "$primaryHex $accentHex".uppercase()

        if (combined.contains("2563EB") || combined.contains("3B82F6") || combined.contains("0284C7") || combined.contains("0369A1")) {
            keywords.addAll(listOf("blue", "ocean", "azure", "navy", "sky", "corporate"))
        }
        if (combined.contains("1E1B4B") || combined.contains("0F172A") || combined.contains("1E293B") || combined.contains("334155") || combined.contains("374151")) {
            keywords.addAll(listOf("dark", "slate", "black", "navy", "midnight", "formal", "executive"))
        }
        if (combined.contains("0D9488") || combined.contains("14B8A6") || combined.contains("2DD4BF") || combined.contains("059669") || combined.contains("10B981")) {
            keywords.addAll(listOf("green", "teal", "emerald", "cyan", "mint", "medical"))
        }
        if (combined.contains("9333EA") || combined.contains("A855F7") || combined.contains("4F46E5") || combined.contains("6366F1")) {
            keywords.addAll(listOf("purple", "violet", "indigo", "creative", "studio"))
        }
        if (combined.contains("E11D48") || combined.contains("F43F5E")) {
            keywords.addAll(listOf("red", "rose", "coral", "pink", "vibrant"))
        }
        if (combined.contains("D97706") || combined.contains("F59E0B") || combined.contains("B45309")) {
            keywords.addAll(listOf("amber", "gold", "orange", "yellow", "artisan", "warm"))
        }

        return keywords
    }
}
