package com.mudasir.nexacvai.core.pdf.renderers

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.graphics.toArgb
import com.mudasir.nexacvai.core.pdf.PdfPageManager
import com.mudasir.nexacvai.core.pdf.utils.PdfDrawUtils
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle

/**
 * 🩺 Healthcare Specialist Renderer
 * Clean emerald medical accent layout (#059669) rendering complete clinical appointments,
 * medical degrees, research projects, board licenses, and references on 1 single A4 page.
 */
class HealthcareSpecialistRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()
        val medicalGreen = Color.parseColor("#059669")

        pageManager.currentY = 38f

        val hasPhoto = templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()

        // Candidate Name & Title Header
        val namePaint = Paint().apply {
            color = medicalGreen
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Dr. Healthcare Specialist" }, 36f, pageManager.currentY, namePaint)
        pageManager.currentY += 18f

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#374151")
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.professionalTitle, 36f, pageManager.currentY, titlePaint)
            pageManager.currentY += 16f
        }

        // Contact Info Line
        val contactPaint = Paint().apply {
            color = Color.parseColor("#4B5563")
            textSize = 8.5f
            isAntiAlias = true
        }
        val contactInfo = listOfNotNull(
            data.email.ifBlank { null },
            data.phone.ifBlank { null },
            data.location.ifBlank { null }
        ).joinToString("  •  ")
        pageManager.canvas.drawText(contactInfo, 36f, pageManager.currentY, contactPaint)
        pageManager.currentY += 14f

        // Draw Avatar Photo if available
        if (hasPhoto) {
            try {
                PdfDrawUtils.drawStyledProfilePhoto(
                    context = context,
                    canvas = pageManager.canvas,
                    sourceUriString = data.profilePictureUri!!,
                    rightX = width - 36f,
                    centerY = 46f,
                    shape = templateStyle.photoShape
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Medical Accent Rule
        val rulePaint = Paint().apply {
            color = medicalGreen
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        pageManager.canvas.drawLine(36f, pageManager.currentY, width - 36f, pageManager.currentY, rulePaint)
        pageManager.currentY += 18f

        // 1. Clinical Profile Summary
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("SUMMARY", "CLINICAL PROFILE & SPECIALTIES"), medicalGreen, 36f, width - 36f)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#1F2937")
                textSize = 9f
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, width - 72f)
            for (l in lines) {
                pageManager.ensureSpace(12f)
                pageManager.canvas.drawText(l, 36f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 12f
            }
            pageManager.currentY += 8f
        }

        // 2. Education & Medical Residencies
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("EDUCATION", "MEDICAL DEGREES & RESIDENCY"), medicalGreen, 36f, width - 36f)

            for (edu in data.educations) {
                pageManager.ensureSpace(28f)
                val degreePaint = Paint().apply {
                    color = Color.parseColor("#111827")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val degreeText = if (edu.gradeOrGpa.isNotBlank()) "${edu.degree}  (${edu.gradeOrGpa})" else edu.degree
                pageManager.canvas.drawText(degreeText, 36f, pageManager.currentY, degreePaint)

                val datePaint = Paint().apply {
                    color = medicalGreen
                    textSize = 9f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateStr = "${edu.startDate} - ${edu.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 36f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 12f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#4B5563")
                    textSize = 9f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.institution, 36f, pageManager.currentY, instPaint)
                pageManager.currentY += 12f

                if (edu.relevantCoursework.isNotBlank() || edu.description.isNotBlank()) {
                    val courseText = "• ${edu.relevantCoursework.ifBlank { edu.description }}"
                    val courseLines = PdfDrawUtils.wrapText(courseText, instPaint, width - 72f)
                    for (l in courseLines) {
                        pageManager.ensureSpace(12f)
                        pageManager.canvas.drawText(l, 36f, pageManager.currentY, instPaint)
                        pageManager.currentY += 12f
                    }
                }
                pageManager.currentY += 5f
            }
            pageManager.currentY += 4f
        }

        // 3. Clinical Appointments & Experience
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("EXPERIENCE", "CLINICAL APPOINTMENTS & EXPERIENCE"), medicalGreen, 36f, width - 36f)

            for (exp in data.experiences) {
                pageManager.ensureSpace(32f)
                val jobTitlePaint = Paint().apply {
                    color = Color.parseColor("#111827")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.jobTitle, 36f, pageManager.currentY, jobTitlePaint)

                val datePaint = Paint().apply {
                    color = medicalGreen
                    textSize = 9f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateStr = "${exp.startDate} - ${exp.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 36f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 12f

                val companyPaint = Paint().apply {
                    color = Color.parseColor("#374151")
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.company, 36f, pageManager.currentY, companyPaint)
                pageManager.currentY += 12f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#1F2937")
                    textSize = 9f
                    isAntiAlias = true
                }
                for (resp in exp.responsibilities) {
                    val respLines = PdfDrawUtils.wrapText("• $resp", descPaint, width - 72f)
                    for (l in respLines) {
                        pageManager.ensureSpace(12f)
                        pageManager.canvas.drawText(l, 36f, pageManager.currentY, descPaint)
                        pageManager.currentY += 12f
                    }
                }
                pageManager.currentY += 5f
            }
            pageManager.currentY += 4f
        }

        // 4. Clinical Research & Quality Projects
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(32f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("PROJECTS", "CLINICAL RESEARCH & INITIATIVES"), medicalGreen, 36f, width - 36f)

            for (proj in data.projects) {
                pageManager.ensureSpace(28f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#111827")
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val titleStr = if (proj.roleInProject.isNotBlank()) "${proj.projectName} — ${proj.roleInProject}" else proj.projectName
                pageManager.canvas.drawText(titleStr, 36f, pageManager.currentY, projNamePaint)
                pageManager.currentY += 12f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#1F2937")
                    textSize = 9f
                    isAntiAlias = true
                }
                val descLines = PdfDrawUtils.wrapText(proj.description, descPaint, width - 72f)
                for (l in descLines) {
                    pageManager.ensureSpace(12f)
                    pageManager.canvas.drawText(l, 36f, pageManager.currentY, descPaint)
                    pageManager.currentY += 12f
                }
                pageManager.currentY += 5f
            }
            pageManager.currentY += 4f
        }

        // 5. Board Certifications & Medical Licenses
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(28f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("CERTIFICATIONS", "BOARD CERTIFICATIONS & LICENSES"), medicalGreen, 36f, width - 36f)
            val certPaint = Paint().apply {
                color = Color.parseColor("#1F2937")
                textSize = 9f
                isAntiAlias = true
            }
            for (cert in data.certifications) {
                pageManager.ensureSpace(11.5f)
                pageManager.canvas.drawText("• ${cert.name} — ${cert.issuer} (${cert.date})", 36f, pageManager.currentY, certPaint)
                pageManager.currentY += 11.5f
            }
            pageManager.currentY += 6f
        }

        // 6. Medical References
        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(28f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("REFERENCES", "MEDICAL REFERENCES"), medicalGreen, 36f, width - 36f)
            val refPaint = Paint().apply {
                color = Color.parseColor("#1F2937")
                textSize = 9f
                isAntiAlias = true
            }
            for (ref in data.references) {
                pageManager.ensureSpace(11.5f)
                val contactDetail = listOfNotNull(ref.company.ifBlank { null }, ref.email.ifBlank { null }, ref.phone.ifBlank { null }).joinToString(" • ")
                val refStr = "• ${ref.name} — ${ref.title} (${contactDetail})"
                val lines = PdfDrawUtils.wrapText(refStr, refPaint, width - 72f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 36f, pageManager.currentY, refPaint)
                    pageManager.currentY += 11.5f
                }
            }
        }

        pageManager.finish()
    }
}
