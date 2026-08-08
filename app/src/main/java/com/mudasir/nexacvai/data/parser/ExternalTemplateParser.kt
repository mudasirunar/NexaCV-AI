package com.mudasir.nexacvai.data.parser

import com.mudasir.nexacvai.data.templates.DynamicSchemaTemplate
import com.mudasir.nexacvai.domain.model.template.TemplateCategory
import com.mudasir.nexacvai.domain.model.template.TemplateMetadata
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Parser responsible for ingesting external JSON template schema files uploaded by the user
 * and building executable [DynamicSchemaTemplate] renderers.
 */
@Singleton
class ExternalTemplateParser @Inject constructor() {

    fun parseJsonTemplate(jsonContent: String): DynamicSchemaTemplate {
        val id = extractJsonString(jsonContent, "id") ?: ("custom_" + System.currentTimeMillis())
        val name = extractJsonString(jsonContent, "name") ?: "Custom Imported Template"
        val description = extractJsonString(jsonContent, "description") ?: "User-imported external template design."
        val categoryName = extractJsonString(jsonContent, "category") ?: "IMPORTED"
        val supportsPhoto = extractJsonBoolean(jsonContent, "supportsPhoto", true)
        val primaryColorHex = extractJsonString(jsonContent, "primaryColorHex") ?: "#1E3A8A"
        val accentColorHex = extractJsonString(jsonContent, "accentColorHex") ?: "#3B82F6"

        val category = try {
            TemplateCategory.valueOf(categoryName.uppercase())
        } catch (e: Exception) {
            TemplateCategory.IMPORTED
        }

        val metadata = TemplateMetadata(
            id = id,
            name = name,
            description = description,
            category = category,
            supportsPhoto = supportsPhoto,
            isImported = true,
            previewPrimaryColorHex = primaryColorHex,
            previewAccentColorHex = accentColorHex
        )

        return DynamicSchemaTemplate(metadata)
    }

    private fun extractJsonString(json: String, key: String): String? {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        return pattern.find(json)?.groupValues?.get(1)
    }

    private fun extractJsonBoolean(json: String, key: String, defaultValue: Boolean): Boolean {
        val pattern = "\"$key\"\\s*:\\s*(true|false)".toRegex()
        val match = pattern.find(json)?.groupValues?.get(1) ?: return defaultValue
        return match.toBoolean()
    }
}
