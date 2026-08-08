package com.mudasir.nexacvai.data.repository

import com.mudasir.nexacvai.core.result.AppResult
import com.mudasir.nexacvai.data.parser.ExternalTemplateParser
import com.mudasir.nexacvai.data.templates.ExecutiveSlateTemplate
import com.mudasir.nexacvai.data.templates.MinimalCleanTemplate
import com.mudasir.nexacvai.data.templates.ModernTechTemplate
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate
import com.mudasir.nexacvai.domain.model.template.TemplateCategory
import com.mudasir.nexacvai.domain.repository.TemplateRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [TemplateRepository].
 * Manages built-in templates and user-imported custom JSON templates.
 */
@Singleton
class TemplateRepositoryImpl @Inject constructor(
    private val externalTemplateParser: ExternalTemplateParser
) : TemplateRepository {

    private val customTemplates = mutableListOf<ResumeTemplate>()

    private val builtInTemplates: List<ResumeTemplate> by lazy {
        listOf(
            ModernTechTemplate(),
            ExecutiveSlateTemplate(),
            MinimalCleanTemplate()
        )
    }

    override suspend fun getAllTemplates(): AppResult<List<ResumeTemplate>> {
        return try {
            val all = builtInTemplates + customTemplates
            AppResult.Success(all)
        } catch (e: Exception) {
            AppResult.Error(e, com.mudasir.nexacvai.core.result.ErrorType.UNKNOWN)
        }
    }

    override suspend fun getTemplatesByCategory(category: TemplateCategory): AppResult<List<ResumeTemplate>> {
        return try {
            val all = builtInTemplates + customTemplates
            val filtered = if (category == TemplateCategory.ALL) {
                all
            } else {
                all.filter { it.metadata.category == category }
            }
            AppResult.Success(filtered)
        } catch (e: Exception) {
            AppResult.Error(e, com.mudasir.nexacvai.core.result.ErrorType.UNKNOWN)
        }
    }

    override suspend fun getTemplateById(templateId: String): AppResult<ResumeTemplate> {
        return try {
            val all = builtInTemplates + customTemplates
            val found = all.find { it.metadata.id == templateId }
                ?: return AppResult.Error(
                    IllegalArgumentException("Template not found: $templateId"),
                    com.mudasir.nexacvai.core.result.ErrorType.NOT_FOUND
                )
            AppResult.Success(found)
        } catch (e: Exception) {
            AppResult.Error(e, com.mudasir.nexacvai.core.result.ErrorType.UNKNOWN)
        }
    }

    override suspend fun importTemplateFromJson(jsonSchema: String): AppResult<ResumeTemplate> {
        return try {
            val parsedTemplate = externalTemplateParser.parseJsonTemplate(jsonSchema)
            customTemplates.add(0, parsedTemplate)
            AppResult.Success(parsedTemplate)
        } catch (e: Exception) {
            AppResult.Error(e, com.mudasir.nexacvai.core.result.ErrorType.VALIDATION)
        }
    }
}
