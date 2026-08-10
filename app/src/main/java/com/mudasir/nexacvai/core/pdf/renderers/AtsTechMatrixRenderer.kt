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
 * 🎯 ATS Categorized Skills Renderer
 * Parser-safe single-column ATS template featuring a **Borderless Categorized Skills Table**
 * (e.g., Mobile Development: Kotlin, Swift, Flutter | Backend Systems: Java, Python, Ktor).
 * Smoothly supports avatar photos when enabled.
 */
class AtsTechMatrixRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()
        pageManager.currentY = 40f

        val hasPhoto = templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()

        // Name Header (Clean Plain Text)
        val namePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Candidate Name" }, 36f, pageManager.currentY, namePaint)
        pageManager.currentY += 20f

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = primaryColorInt
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.professionalTitle, 36f, pageManager.currentY, titlePaint)
            pageManager.currentY += 18f
        }

        // Contact Info Line
        val contactPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 9.5f
            isAntiAlias = true
        }
        val contactInfo = listOfNotNull(data.email.ifBlank { null }, data.phone.ifBlank { null }, data.location.ifBlank { null }).joinToString("  •  ")
        pageManager.canvas.drawText(contactInfo, 36f, pageManager.currentY, contactPaint)
        pageManager.currentY += 16f

        // Draw Profile Photo ONLY IF photo is selected (no blank placeholder)
        if (hasPhoto) {
            try {
                PdfDrawUtils.drawStyledProfilePhoto(
                    context = context,
                    canvas = pageManager.canvas,
                    sourceUriString = data.profilePictureUri!!,
                    rightX = width - 36f,
                    centerY = 50f,
                    shape = templateStyle.photoShape
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Divider Rule
        val rulePaint = Paint().apply {
            color = primaryColorInt
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        pageManager.canvas.drawLine(36f, pageManager.currentY, width - 36f, pageManager.currentY, rulePaint)
        pageManager.currentY += 20f

        // 1. Categorized Skills Section (Clean Borderless Table Alignment)
        if (data.skillCategoryGroups.isNotEmpty() || data.skills.isNotEmpty()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, "TECHNICAL SKILLS & COMPETENCIES", primaryColorInt, 36f, width - 36f)

            val catLabelPaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val skillsListPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }

            if (data.skillCategoryGroups.isNotEmpty()) {
                val labelColWidth = 130f
                val valueColX = 36f + labelColWidth

                for (group in data.skillCategoryGroups) {
                    pageManager.ensureSpace(16f)
                    pageManager.canvas.drawText(group.categoryName, 36f, pageManager.currentY, catLabelPaint)

                    val skillsStr = group.skills.joinToString(", ")
                    val remainingW = width - valueColX - 36f
                    val lines = PdfDrawUtils.wrapText(skillsStr, skillsListPaint, remainingW)

                    if (lines.isNotEmpty()) {
                        pageManager.canvas.drawText(lines[0], valueColX, pageManager.currentY, skillsListPaint)
                        pageManager.currentY += 15f
                        for (i in 1 until lines.size) {
                            pageManager.ensureSpace(15f)
                            pageManager.canvas.drawText(lines[i], valueColX, pageManager.currentY, skillsListPaint)
                            pageManager.currentY += 15f
                        }
                    } else {
                        pageManager.currentY += 15f
                    }
                }
            } else {
                val skillText = data.skills.joinToString("  •  ")
                val lines = PdfDrawUtils.wrapText(skillText, skillsListPaint, width - 72f)
                for (l in lines) {
                    pageManager.ensureSpace(14f)
                    pageManager.canvas.drawText(l, 36f, pageManager.currentY, skillsListPaint)
                    pageManager.currentY += 14f
                }
            }
            pageManager.currentY += 12f
        }

        // Summary
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, "PROFESSIONAL SUMMARY", primaryColorInt, 36f, width - 36f)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, width - 72f)
            for (l in lines) {
                pageManager.ensureSpace(13f)
                pageManager.canvas.drawText(l, 36f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 13f
            }
            pageManager.currentY += 14f
        }

        // Work Experience
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "PROFESSIONAL EXPERIENCE", primaryColorInt, 36f, width - 36f)

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
                val dateText = "${exp.startDate} - ${exp.endDate}"
                val dateW = datePaint.measureText(dateText)
                pageManager.canvas.drawText(dateText, width - 36f - dateW, pageManager.currentY, datePaint)
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
                        pageManager.ensureSpace(13f)
                        pageManager.canvas.drawText(l, 36f, pageManager.currentY, descPaint)
                        pageManager.currentY += 13f
                    }
                }
                pageManager.currentY += 10f
            }
        }

        pageManager.finish()
    }
}
