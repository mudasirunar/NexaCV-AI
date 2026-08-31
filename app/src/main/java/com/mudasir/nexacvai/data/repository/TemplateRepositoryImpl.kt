package com.mudasir.nexacvai.data.repository

import com.mudasir.nexacvai.core.result.AppResult
import com.mudasir.nexacvai.core.result.ErrorType
import com.mudasir.nexacvai.data.local.dao.FavoriteTemplateDao
import com.mudasir.nexacvai.data.local.entity.FavoriteTemplateEntity
import com.mudasir.nexacvai.data.parser.ExternalTemplateParser
import com.mudasir.nexacvai.data.templates.BuiltInTemplatesCatalog
import com.mudasir.nexacvai.di.ApplicationScope
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate
import com.mudasir.nexacvai.domain.model.template.TemplateCategory
import com.mudasir.nexacvai.domain.repository.TemplateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [TemplateRepository].
 * Manages built-in templates, user-imported custom JSON templates, and Room-backed favorite states.
 */
@Singleton
class TemplateRepositoryImpl @Inject constructor(
    private val externalTemplateParser: ExternalTemplateParser,
    private val favoriteTemplateDao: FavoriteTemplateDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : TemplateRepository {

    private val customTemplates = mutableListOf<ResumeTemplate>()

    private val builtInTemplates: List<ResumeTemplate> = BuiltInTemplatesCatalog.ALL_TEMPLATES

    private val _favoriteIdsStateFlow: StateFlow<Set<String>> = favoriteTemplateDao.getFavoriteTemplateIds()
        .map { it.toSet() }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )

    override suspend fun getAllTemplates(): AppResult<List<ResumeTemplate>> {
        return try {
            val all = builtInTemplates + customTemplates
            AppResult.Success(all)
        } catch (e: Exception) {
            AppResult.Error(e, ErrorType.UNKNOWN)
        }
    }

    override suspend fun getTemplatesByCategory(category: TemplateCategory): AppResult<List<ResumeTemplate>> {
        return try {
            val all = builtInTemplates + customTemplates
            val filtered = when (category) {
                TemplateCategory.ALL -> all
                TemplateCategory.FAVORITES -> emptyList()
                TemplateCategory.CUSTOM -> customTemplates + all.filter { it.metadata.isImported || it.metadata.category == TemplateCategory.CUSTOM }
                else -> all.filter { it.metadata.category == category }
            }
            AppResult.Success(filtered)
        } catch (e: Exception) {
            AppResult.Error(e, ErrorType.UNKNOWN)
        }
    }

    override suspend fun getTemplateById(templateId: String): AppResult<ResumeTemplate> {
        return try {
            val all = builtInTemplates + customTemplates
            val found = all.find { it.metadata.id == templateId }
                ?: return AppResult.Error(
                    IllegalArgumentException("Template not found: $templateId"),
                    ErrorType.NOT_FOUND
                )
            AppResult.Success(found)
        } catch (e: Exception) {
            AppResult.Error(e, ErrorType.UNKNOWN)
        }
    }

    override suspend fun importTemplateFromJson(jsonSchema: String): AppResult<ResumeTemplate> {
        return try {
            val parsedTemplate = externalTemplateParser.parseJsonTemplate(jsonSchema)
            customTemplates.add(0, parsedTemplate)
            AppResult.Success(parsedTemplate)
        } catch (e: Exception) {
            AppResult.Error(e, ErrorType.VALIDATION)
        }
    }

    override fun getFavoriteTemplateIds(): Flow<Set<String>> = _favoriteIdsStateFlow

    override suspend fun isFavorite(templateId: String): Boolean {
        return _favoriteIdsStateFlow.value.contains(templateId)
    }

    override suspend fun toggleFavorite(templateId: String): AppResult<Boolean> {
        return try {
            val exists = _favoriteIdsStateFlow.value.contains(templateId)
            if (exists) {
                favoriteTemplateDao.removeFavorite(templateId)
                AppResult.Success(false)
            } else {
                favoriteTemplateDao.addFavorite(FavoriteTemplateEntity(templateId = templateId))
                AppResult.Success(true)
            }
        } catch (e: Exception) {
            AppResult.Error(e, ErrorType.DATABASE)
        }
    }

    override suspend fun addFavorite(templateId: String): AppResult<Unit> {
        return try {
            favoriteTemplateDao.addFavorite(FavoriteTemplateEntity(templateId = templateId))
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e, ErrorType.DATABASE)
        }
    }

    override suspend fun removeFavorite(templateId: String): AppResult<Unit> {
        return try {
            favoriteTemplateDao.removeFavorite(templateId)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e, ErrorType.DATABASE)
        }
    }
}
