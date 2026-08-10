package com.mudasir.nexacvai.core.pdf.renderers

import com.mudasir.nexacvai.core.pdf.PdfPageManager
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle

/**
 * Polymorphic Canvas Layout Renderer Strategy.
 * Each implementation provides custom rendering logic for a specific template layout style.
 */
interface PdfTemplateRenderer {
    fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    )
}
