package com.mudasir.nexacvai.domain.model.template

/**
 * Descriptor metadata for a CV template.
 */
data class TemplateMetadata(
    val id: String,
    val name: String,
    val description: String,
    val category: TemplateCategory,
    val supportsPhoto: Boolean = true,
    val defaultPhotoShape: PhotoShape = PhotoShape.CIRCLE,
    val isImported: Boolean = false,
    val previewPrimaryColorHex: String = "#1E3A8A",
    val previewAccentColorHex: String = "#3B82F6"
)
