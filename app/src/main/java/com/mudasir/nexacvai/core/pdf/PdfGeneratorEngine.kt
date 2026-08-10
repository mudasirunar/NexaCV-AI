package com.mudasir.nexacvai.core.pdf

import android.content.Context
import android.graphics.pdf.PdfDocument
import com.mudasir.nexacvai.core.pdf.renderers.*
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Standard A4 PDF Document Generation Engine.
 * Generates true 595 x 842 pt A4 vector PDF documents using native Android [PdfDocument] & [Canvas].
 * Delegates template rendering to polymorphic [PdfTemplateRenderer] layout strategies.
 */
@Singleton
class PdfGeneratorEngine @Inject constructor(
    private val context: Context
) {
    companion object {
        const val A4_WIDTH_PT = 595
        const val A4_HEIGHT_PT = 842
    }

    suspend fun generateCvPdf(
        template: ResumeTemplate,
        data: TemplateData,
        templateStyle: TemplateStyle,
        outputFileName: String = "generated_cv_preview.pdf"
    ): File = withContext(Dispatchers.IO) {
        val outputFile = File(context.cacheDir, outputFileName)
        val pdfDocument = PdfDocument()

        try {
            val pageManager = PdfPageManager(pdfDocument)
            val renderer = getRendererForTemplate(template)
            renderer.render(pageManager, data, templateStyle)

            FileOutputStream(outputFile).use { out ->
                pdfDocument.writeTo(out)
            }
        } finally {
            pdfDocument.close()
        }

        outputFile
    }

    private fun getRendererForTemplate(template: ResumeTemplate): PdfTemplateRenderer {
        val id = template.metadata.id
        return when {
            id == "template_ats_hybrid" -> AtsHybridTimelineRenderer(context)
            id == "template_healthcare_specialist" -> HealthcareSpecialistRenderer(context)
            id.contains("tech_matrix") -> AtsTechMatrixRenderer(context)
            id.contains("wavy") -> ModernWavyRenderer(context)
            id.contains("cyber") -> CyberSecurityMatrixRenderer(context)
            id.contains("developer") -> DeveloperSlateRenderer(context)
            id == "template_minimal_serif" -> AcademicSerifRenderer(context)
            id == "template_clinical_sidebar" || id == "template_ux_designer" || id == "template_exec_sidebar" || id == "template_modern_tech" || id == "template_tech_lead_grid" -> ModernTechRenderer(context)
            id == "template_clinical_doctor" -> ClinicalDoctorRenderer(context)
            id.contains("exec") || id.contains("banking") || id.contains("director") -> ExecutiveCompetencyRenderer(context)
            id.contains("creative") || id.contains("artisan") -> CreativePortfolioRenderer(context)
            else -> AtsCleanRenderer(context)
        }
    }
}
