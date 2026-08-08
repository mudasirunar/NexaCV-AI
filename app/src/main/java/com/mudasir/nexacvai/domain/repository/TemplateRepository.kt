package com.mudasir.nexacvai.domain.repository

import com.mudasir.nexacvai.core.result.AppResult
import com.mudasir.nexacvai.domain.model.template.TemplateCategory
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate

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
}
