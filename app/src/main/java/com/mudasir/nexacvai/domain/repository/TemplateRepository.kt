package com.mudasir.nexacvai.domain.repository

import com.mudasir.nexacvai.core.result.AppResult
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate
import com.mudasir.nexacvai.domain.model.template.TemplateCategory
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing built-in and user-imported custom CV templates.
 */
interface TemplateRepository {
    /** Fetch all available templates */
    suspend fun getAllTemplates(): AppResult<List<ResumeTemplate>>

    /** Fetch templates filtered by category */
    suspend fun getTemplatesByCategory(category: TemplateCategory): AppResult<List<ResumeTemplate>>

    /** Fetch single template by ID */
    suspend fun getTemplateById(templateId: String): AppResult<ResumeTemplate>

    /** Import external JSON template schema string */
    suspend fun importTemplateFromJson(jsonSchema: String): AppResult<ResumeTemplate>

    /** Continuous reactive stream of all favorited template IDs from Room */
    fun getFavoriteTemplateIds(): Flow<Set<String>>

    /** Check if a template is favorited */
    suspend fun isFavorite(templateId: String): Boolean

    /** Toggle favorite state (returns new favorite state) */
    suspend fun toggleFavorite(templateId: String): AppResult<Boolean>

    /** Add a template to favorites */
    suspend fun addFavorite(templateId: String): AppResult<Unit>

    /** Remove a template from favorites */
    suspend fun removeFavorite(templateId: String): AppResult<Unit>
}
