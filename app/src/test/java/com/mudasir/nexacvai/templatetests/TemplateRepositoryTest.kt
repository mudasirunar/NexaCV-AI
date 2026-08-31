package com.mudasir.nexacvai.templatetests

import com.mudasir.nexacvai.core.result.AppResult
import com.mudasir.nexacvai.data.local.dao.FavoriteTemplateDao
import com.mudasir.nexacvai.data.local.entity.FavoriteTemplateEntity
import com.mudasir.nexacvai.data.parser.ExternalTemplateParser
import com.mudasir.nexacvai.data.repository.TemplateRepositoryImpl
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.model.template.TemplateCategory
import com.mudasir.nexacvai.domain.model.template.toTemplateData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TemplateRepositoryTest {

    private lateinit var parser: ExternalTemplateParser
    private lateinit var fakeFavoriteDao: FakeFavoriteTemplateDao
    private lateinit var repository: TemplateRepositoryImpl

    private class FakeFavoriteTemplateDao : FavoriteTemplateDao {
        private val favoritesFlow = MutableStateFlow<List<String>>(emptyList())

        override fun getFavoriteTemplateIds(): Flow<List<String>> = favoritesFlow

        override suspend fun isFavorite(templateId: String): Boolean {
            return favoritesFlow.value.contains(templateId)
        }

        override suspend fun addFavorite(entity: FavoriteTemplateEntity) {
            val current = favoritesFlow.value.toMutableList()
            if (!current.contains(entity.templateId)) {
                current.add(0, entity.templateId)
                favoritesFlow.value = current
            }
        }

        override suspend fun removeFavorite(templateId: String) {
            val current = favoritesFlow.value.toMutableList()
            current.remove(templateId)
            favoritesFlow.value = current
        }

        override suspend fun clearAllFavorites() {
            favoritesFlow.value = emptyList()
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        parser = ExternalTemplateParser()
        fakeFavoriteDao = FakeFavoriteTemplateDao()
        repository = TemplateRepositoryImpl(
            externalTemplateParser = parser,
            favoriteTemplateDao = fakeFavoriteDao,
            applicationScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.test.UnconfinedTestDispatcher())
        )
    }

    @Test
    fun getAllTemplates_returnsBuiltInTemplates() = runTest {
        val result = repository.getAllTemplates()
        assertTrue(result is AppResult.Success)
        val templates = (result as AppResult.Success).data
        assertTrue(templates.size >= 3)
        assertTrue(templates.any { it.metadata.id == "template_modern_tech" })
        assertTrue(templates.any { it.metadata.id == "template_exec_slate" })
        assertTrue(templates.any { it.metadata.id == "template_ats_clean" })
    }

    @Test
    fun getTemplatesByCategory_filtersCorrectly() = runTest {
        val modernResult = repository.getTemplatesByCategory(TemplateCategory.MODERN)
        assertTrue(modernResult is AppResult.Success)
        val modernTemplates = (modernResult as AppResult.Success).data
        assertTrue(modernTemplates.all { it.metadata.category == TemplateCategory.MODERN })
    }

    @Test
    fun getTemplateById_returnsCorrectTemplate() = runTest {
        val result = repository.getTemplateById("template_exec_slate")
        assertTrue(result is AppResult.Success)
        val template = (result as AppResult.Success).data
        assertEquals("Executive Formal Slate", template.metadata.name)
        assertTrue(template.metadata.supportsPhoto)
    }

    @Test
    fun importTemplateFromJson_parsesAndRegistersCustomTemplate() = runTest {
        val customJson = """
            {
                "id": "custom_dark_template",
                "name": "Custom Slate Dark",
                "description": "Custom dark mode resume layout.",
                "category": "CREATIVE",
                "supportsPhoto": true,
                "primaryColorHex": "#0F172A"
            }
        """.trimIndent()

        val importResult = repository.importTemplateFromJson(customJson)
        assertTrue(importResult is AppResult.Success)
        val imported = (importResult as AppResult.Success).data
        assertEquals("custom_dark_template", imported.metadata.id)
        assertEquals("Custom Slate Dark", imported.metadata.name)
        assertTrue(imported.metadata.isImported)

        val allResult = repository.getAllTemplates()
        val allTemplates = (allResult as AppResult.Success).data
        assertTrue(allTemplates.any { it.metadata.id == "custom_dark_template" })
    }

    @Test
    fun favoriteOperations_toggleAndPersistCorrectly() = runTest {
        val templateId = "template_modern_wavy"

        assertFalse(repository.isFavorite(templateId))

        // Toggle ON
        val toggleResult = repository.toggleFavorite(templateId)
        assertTrue(toggleResult is AppResult.Success)
        assertTrue((toggleResult as AppResult.Success).data)
        assertTrue(repository.isFavorite(templateId))

        val favorites = repository.getFavoriteTemplateIds().first()
        assertTrue(favorites.contains(templateId))

        // Toggle OFF
        val toggleOffResult = repository.toggleFavorite(templateId)
        assertTrue(toggleOffResult is AppResult.Success)
        assertFalse((toggleOffResult as AppResult.Success).data)
        assertFalse(repository.isFavorite(templateId))
    }

    @Test
    fun userProfileToTemplateData_mapsAllFieldsCorrectly() {
        val profile = UserProfile(
            id = 100L,
            fullName = "Sarah Connor",
            professionalTitle = "Cybernetics Engineer",
            address = "Los Angeles, CA",
            profilePictureUri = "content://media/pfp.jpg"
        )

        val templateData = profile.toTemplateData()
        assertEquals("Sarah Connor", templateData.fullName)
        assertEquals("Cybernetics Engineer", templateData.professionalTitle)
        assertEquals("Los Angeles, CA", templateData.location)
        assertEquals("content://media/pfp.jpg", templateData.profilePictureUri)
    }
}
