package com.mudasir.nexacvai.domain.model.template

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Domain contract interface implemented by all built-in Jetpack Compose templates
 * and dynamic external template renderers.
 */
interface ResumeTemplate {
    /** Metadata descriptor (id, name, category, supportsPhoto, isImported) */
    val metadata: TemplateMetadata

    /** Default sample guidance data for template placeholder rendering */
    val defaultData: TemplateData
        get() = TemplateData.SAMPLE_FILLER

    /**
     * Renders the CV layout in 60 FPS Jetpack Compose canvas.
     * Handles dynamic photo collapsing header automatically.
     */
    @Composable
    fun Render(
        data: TemplateData,
        style: TemplateStyle,
        modifier: Modifier
    )
}
