package com.mudasir.nexacvai.core.pdf.renderers

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.toArgb
import com.mudasir.nexacvai.core.pdf.PdfPageManager
import com.mudasir.nexacvai.core.pdf.utils.PdfDrawUtils
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle

/**
 * 🏥 Clinical Doctor Credentials Renderer
 * Medical blue header banner with top-right Clinical Credentials Box (Board Certified & Medical Licenses),
 * clean 1-page clinical timeline layout without photo collision or box overlap.
 */
class ClinicalDoctorRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        // Top Medical Header Block Banner
        val headerHeight = 100f
        val headerPaint = Paint().apply {
            color = Color.parseColor("#0284C7")
            style = Paint.Style.FILL
        }
        pageManager.canvas.drawRect(0f, 0f, width, headerHeight, headerPaint)

        // Candidate Name & Medical Title (Left Aligned)
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 21f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Dr. Medical Candidate, MD" }, 32f, 38f, namePaint)

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#E0F2FE")
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.professionalTitle, 32f, 56f, titlePaint)
        }

        val contactPaint = Paint().apply {
            color = Color.parseColor("#BAE6FD")
            textSize = 8.5f
            isAntiAlias = true
        }
        val contactStr = listOfNotNull(data.email.ifBlank { null }, data.phone.ifBlank { null }, data.location.ifBlank { null }).joinToString("  •  ")
        pageManager.canvas.drawText(contactStr, 32f, 74f, contactPaint)

        // Top Right Clinical Credentials Box - Positioned safely on top-right without photo collision
        if (data.certifications.isNotEmpty()) {
            val credBgPaint = Paint().apply {
                color = Color.argb(46, 255, 255, 255)
                style = Paint.Style.FILL
            }
            val boxW = 200f
            val boxRight = width - 32f
            val credRect = RectF(boxRight - boxW, 14f, boxRight, 86f)
            pageManager.canvas.drawRoundRect(credRect, 6f, 6f, credBgPaint)

            val credTextPaint = Paint().apply {
                color = Color.WHITE
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            pageManager.canvas.drawText("CLINICAL CREDENTIALS", boxRight - boxW + 10f, 30f, credTextPaint)

            val credSubPaint = Paint().apply {
                color = Color.parseColor("#F0F9FF")
                textSize = 7.5f
                isAntiAlias = true
            }
            var credY = 46f
            for (cert in data.certifications.take(3)) {
                val certText = if (cert.name.length > 34) cert.name.take(32) + "..." else cert.name
                pageManager.canvas.drawText("• $certText", boxRight - boxW + 10f, credY, credSubPaint)
                credY += 12f
            }
        }

        pageManager.currentY = headerHeight + 20f

        // 1. Clinical Profile & Specialties
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CLINICAL PROFILE & SPECIALTIES", primaryColorInt, 32f, width - 32f)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#1E293B")
                textSize = 9f
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, width - 64f)
            for (l in lines) {
                pageManager.ensureSpace(12f)
                pageManager.canvas.drawText(l, 32f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 12f
            }
            pageManager.currentY += 10f
        }

        // 2. Education & Medical Residencies
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "MEDICAL DEGREES & RESIDENCY", primaryColorInt, 32f, width - 32f)

            for (edu in data.educations) {
                pageManager.ensureSpace(30f)
                val degreePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val degreeText = if (edu.gradeOrGpa.isNotBlank()) "${edu.degree}  (${edu.gradeOrGpa})" else edu.degree
                pageManager.canvas.drawText(degreeText, 32f, pageManager.currentY, degreePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#0284C7")
                    textSize = 9f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateStr = "${edu.startDate} - ${edu.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 32f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 13f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.institution, 32f, pageManager.currentY, instPaint)
                pageManager.currentY += 13f

                if (edu.relevantCoursework.isNotBlank() || edu.description.isNotBlank()) {
                    val courseText = "• ${edu.relevantCoursework.ifBlank { edu.description }}"
                    val courseLines = PdfDrawUtils.wrapText(courseText, instPaint, width - 64f)
                    for (l in courseLines) {
                        pageManager.ensureSpace(12f)
                        pageManager.canvas.drawText(l, 32f, pageManager.currentY, instPaint)
                        pageManager.currentY += 12f
                    }
                }
                pageManager.currentY += 6f
            }
            pageManager.currentY += 4f
        }

        // 3. Clinical Appointments & Experience
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CLINICAL APPOINTMENTS & EXPERIENCE", primaryColorInt, 32f, width - 32f)

            for (exp in data.experiences) {
                pageManager.ensureSpace(35f)
                val jobTitlePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.jobTitle, 32f, pageManager.currentY, jobTitlePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#0284C7")
                    textSize = 9f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateStr = "${exp.startDate} - ${exp.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 32f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 13f

                val companyPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.company, 32f, pageManager.currentY, companyPaint)
                pageManager.currentY += 13f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9f
                    isAntiAlias = true
                }
                for (resp in exp.responsibilities) {
                    val respLines = PdfDrawUtils.wrapText("• $resp", descPaint, width - 64f)
                    for (l in respLines) {
                        pageManager.ensureSpace(12f)
                        pageManager.canvas.drawText(l, 32f, pageManager.currentY, descPaint)
                        pageManager.currentY += 12f
                    }
                }
                pageManager.currentY += 6f
            }
            pageManager.currentY += 4f
        }

        // 4. Clinical Research & Quality Projects
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CLINICAL RESEARCH & QUALITY INITIATIVES", primaryColorInt, 32f, width - 32f)

            for (proj in data.projects) {
                pageManager.ensureSpace(30f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val titleStr = if (proj.roleInProject.isNotBlank()) "${proj.projectName} — ${proj.roleInProject}" else proj.projectName
                pageManager.canvas.drawText(titleStr, 32f, pageManager.currentY, projNamePaint)
                pageManager.currentY += 13f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9f
                    isAntiAlias = true
                }
                val descLines = PdfDrawUtils.wrapText(proj.description, descPaint, width - 64f)
                for (l in descLines) {
                    pageManager.ensureSpace(12f)
                    pageManager.canvas.drawText(l, 32f, pageManager.currentY, descPaint)
                    pageManager.currentY += 12f
                }
                pageManager.currentY += 6f
            }
            pageManager.currentY += 4f
        }

        // 5. Board Certifications & Medical Licenses
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            PdfDrawUtils.drawSectionHeader(pageManager, "BOARD CERTIFICATIONS & LICENSES", primaryColorInt, 32f, width - 32f)
            val certPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9f
                isAntiAlias = true
            }
            for (cert in data.certifications) {
                pageManager.ensureSpace(12f)
                pageManager.canvas.drawText("• ${cert.name} — ${cert.issuer} (${cert.date})", 32f, pageManager.currentY, certPaint)
                pageManager.currentY += 12f
            }
            pageManager.currentY += 8f
        }

        // 6. Medical References
        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            PdfDrawUtils.drawSectionHeader(pageManager, "MEDICAL REFERENCES", primaryColorInt, 32f, width - 32f)
            val refPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9f
                isAntiAlias = true
            }
            for (ref in data.references) {
                pageManager.ensureSpace(12f)
                val contactDetail = listOfNotNull(ref.company.ifBlank { null }, ref.email.ifBlank { null }, ref.phone.ifBlank { null }).joinToString(" • ")
                val refStr = "• ${ref.name} — ${ref.title} (${contactDetail})"
                val lines = PdfDrawUtils.wrapText(refStr, refPaint, width - 64f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 32f, pageManager.currentY, refPaint)
                    pageManager.currentY += 12f
                }
            }
        }

        pageManager.finish()
    }
}
