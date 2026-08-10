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
 * 💼 Executive Competency Renderer
 * Full width dark navy top banner header, centered executive profile, 3-column competency grid,
 * rendering 100% of UserProfile domain fields with clean MS Word-style spacing (14pt line height).
 */
class ExecutiveCompetencyRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        // Full Width Banner Top Header
        val bannerHeight = 110f
        val bannerPaint = Paint().apply {
            color = primaryColorInt
            style = Paint.Style.FILL
        }
        pageManager.canvas.drawRect(0f, 0f, width, bannerHeight, bannerPaint)

        // Banner Text (Centered / White)
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Executive Candidate" }, 36f, 42f, namePaint)

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#E2E8F0")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.professionalTitle, 36f, 64f, titlePaint)
        }

        val contactPaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            textSize = 9.5f
            isAntiAlias = true
        }
        val contactStr = listOfNotNull(
            data.email.ifBlank { null },
            data.phone.ifBlank { null },
            data.location.ifBlank { null }
        ).joinToString("  •  ")
        pageManager.canvas.drawText(contactStr, 36f, 86f, contactPaint)

        // Avatar Photo in Banner Top Right
        if (templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()) {
            try {
                PdfDrawUtils.drawStyledProfilePhoto(
                    context = context,
                    canvas = pageManager.canvas,
                    sourceUriString = data.profilePictureUri,
                    rightX = width - 36f,
                    centerY = bannerHeight / 2f,
                    shape = templateStyle.photoShape
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        pageManager.currentY = bannerHeight + 24f

        // 1. Executive Summary
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, "EXECUTIVE PROFILE", primaryColorInt, 36f, width - 36f)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#1E293B")
                textSize = 9.5f
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, width - 72f)
            for (l in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(l, 36f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 14f
        }

        // 2. Core Leadership Competencies (Grid Matrix)
        if (data.skills.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CORE LEADERSHIP COMPETENCIES", primaryColorInt, 36f, width - 36f)

            val compPaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val colW = (width - 72f) / 3f
            var col = 0
            var startRowY = pageManager.currentY

            for (skill in data.skills) {
                val posX = 36f + col * colW
                pageManager.canvas.drawText("▪  $skill", posX, startRowY, compPaint)
                col++
                if (col >= 3) {
                    col = 0
                    startRowY += 16f
                    pageManager.ensureSpace(16f)
                }
            }
            pageManager.currentY = if (col > 0) startRowY + 16f else startRowY + 8f
            pageManager.currentY += 10f
        }

        // 3. Professional Experience & P&L
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "EXECUTIVE EXPERIENCE & CAREER HISTORY", primaryColorInt, 36f, width - 36f)

            for (exp in data.experiences) {
                pageManager.ensureSpace(45f)
                val jobTitlePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.jobTitle, 36f, pageManager.currentY, jobTitlePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateStr = "${exp.startDate} - ${exp.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 36f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 15f

                val companyPaint = Paint().apply {
                    color = primaryColorInt
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.company, 36f, pageManager.currentY, companyPaint)
                pageManager.currentY += 15f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                for (resp in exp.responsibilities) {
                    val respLines = PdfDrawUtils.wrapText("• $resp", descPaint, width - 72f)
                    for (l in respLines) {
                        pageManager.ensureSpace(14f)
                        pageManager.canvas.drawText(l, 36f, pageManager.currentY, descPaint)
                        pageManager.currentY += 14f
                    }
                }
                pageManager.currentY += 10f
            }
        }

        // 4. Education & Executive Credentials
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "EDUCATION & EXECUTIVE CREDENTIALS", primaryColorInt, 36f, width - 36f)

            for (edu in data.educations) {
                pageManager.ensureSpace(30f)
                val degreePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.degree, 36f, pageManager.currentY, degreePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                val dateStr = "${edu.startDate} - ${edu.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 36f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 14f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.institution, 36f, pageManager.currentY, instPaint)
                pageManager.currentY += 16f
            }
        }

        // 5. Strategic Projects
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "STRATEGIC CORPORATE INITIATIVES", primaryColorInt, 36f, width - 36f)

            for (proj in data.projects) {
                pageManager.ensureSpace(35f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(proj.projectName, 36f, pageManager.currentY, projNamePaint)
                pageManager.currentY += 14f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                val descLines = PdfDrawUtils.wrapText(proj.description, descPaint, width - 72f)
                for (l in descLines) {
                    pageManager.ensureSpace(14f)
                    pageManager.canvas.drawText(l, 36f, pageManager.currentY, descPaint)
                    pageManager.currentY += 14f
                }
                pageManager.currentY += 8f
            }
        }

        // 6. Certifications & Board Governance
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "BOARD CERTIFICATIONS & LICENSES", primaryColorInt, 36f, width - 36f)
            val certPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            for (cert in data.certifications) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText("• ${cert.name} — ${cert.issuer} (${cert.date})", 36f, pageManager.currentY, certPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 10f
        }

        // 7. Languages & References
        if (data.languages.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            PdfDrawUtils.drawSectionHeader(pageManager, "LANGUAGES & INTERNATIONAL MOBILITY", primaryColorInt, 36f, width - 36f)
            val langPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val langText = data.languages.joinToString("   |   ") { "${it.languageName} (${it.proficiency})" }
            pageManager.canvas.drawText(langText, 36f, pageManager.currentY, langPaint)
            pageManager.currentY += 18f
        }

        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "BOARD REFERENCES & ENDORSEMENTS", primaryColorInt, 36f, width - 36f)
            val refPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val refText = data.references.joinToString("   |   ") { "${it.name} (${it.title}, ${it.company})" }
            val lines = PdfDrawUtils.wrapText(refText, refPaint, width - 72f)
            for (l in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(l, 36f, pageManager.currentY, refPaint)
                pageManager.currentY += 14f
            }
        }

        pageManager.finish()
    }
}
