package com.mudasir.nexacvai.templatetests

import com.mudasir.nexacvai.core.result.AppResult
import com.mudasir.nexacvai.data.parser.ExternalTemplateParser
import com.mudasir.nexacvai.data.repository.TemplateRepositoryImpl
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.model.template.TemplateCategory
import com.mudasir.nexacvai.domain.model.template.toTemplateData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TemplateRepositoryTest {

    private lateinit var parser: ExternalTemplateParser
    private lateinit var repository: TemplateRepositoryImpl

    @Before
    fun setUp() {
        parser = ExternalTemplateParser()
        repository = TemplateRepositoryImpl(parser)
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
