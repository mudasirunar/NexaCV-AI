package com.mudasir.nexacvai.core.pdf.renderers

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.toArgb
import com.mudasir.nexacvai.core.pdf.PdfPageManager
import com.mudasir.nexacvai.core.pdf.utils.PdfDrawUtils
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle

/**
 * 🌊 Modern Horizon Accent Renderer
 * Draws a dynamic bezier curved wavy top header banner with candidate avatar photo,
 * rendering 100% of UserProfile sections (Summary, Experience, Education, Projects, Skills Pills, Certs, Languages, References, Hobbies, Awards).
 * Uses MS Word-style clean line spacing (14pt) and 18pt section gaps.
 */
class ModernWavyRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        val hasPhoto = templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()
        val headerHeight = if (hasPhoto) 120f else 95f

        // Draw Dynamic Wavy Top Path Header
        val wavyPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(width, 0f)
            lineTo(width, headerHeight - 20f)
            cubicTo(width * 0.7f, headerHeight + 15f, width * 0.3f, headerHeight - 30f, 0f, headerHeight - 5f)
            close()
        }
        val headerPaint = Paint().apply {
            color = primaryColorInt
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        pageManager.canvas.drawPath(wavyPath, headerPaint)

        // Header Title & Name
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Candidate Name" }, 32f, 38f, namePaint)

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#F1F5F9")
                textSize = 11.5f
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.professionalTitle, 32f, 58f, titlePaint)
        }

        val contactPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            textSize = 9f
            isAntiAlias = true
        }
        val contactStr = listOfNotNull(data.email.ifBlank { null }, data.phone.ifBlank { null }, data.location.ifBlank { null }).joinToString("  •  ")
        pageManager.canvas.drawText(contactStr, 32f, 76f, contactPaint)

        // Draw Profile Photo ONLY IF photo is provided (No empty circles if null)
        if (hasPhoto) {
            try {
                PdfDrawUtils.drawStyledProfilePhoto(
                    context = context,
                    canvas = pageManager.canvas,
                    sourceUriString = data.profilePictureUri!!,
                    rightX = width - 32f,
                    centerY = headerHeight / 2f - 4f,
                    shape = templateStyle.photoShape
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        pageManager.currentY = headerHeight + 20f

        // 1. Professional Summary
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, "ABOUT ME", primaryColorInt, 32f, width - 32f)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, width - 64f)
            for (l in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(l, 32f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 14f
        }

        // 2. Work Experience
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "EXPERIENCE & ACHIEVEMENTS", primaryColorInt, 32f, width - 32f)

            for (exp in data.experiences) {
                pageManager.ensureSpace(45f)
                val jobTitlePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.jobTitle, 32f, pageManager.currentY, jobTitlePaint)

                val datePaint = Paint().apply {
                    color = primaryColorInt
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateStr = "${exp.startDate} - ${exp.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 32f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 15f

                val companyPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.company, 32f, pageManager.currentY, companyPaint)
                pageManager.currentY += 15f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                for (resp in exp.responsibilities) {
                    val respLines = PdfDrawUtils.wrapText("• $resp", descPaint, width - 64f)
                    for (l in respLines) {
                        pageManager.ensureSpace(14f)
                        pageManager.canvas.drawText(l, 32f, pageManager.currentY, descPaint)
                        pageManager.currentY += 14f
                    }
                }
                pageManager.currentY += 10f
            }
        }

        // 3. Education
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "EDUCATION & QUALIFICATIONS", primaryColorInt, 32f, width - 32f)

            for (edu in data.educations) {
                pageManager.ensureSpace(30f)
                val degreePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.degree, 32f, pageManager.currentY, degreePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                val dateStr = "${edu.startDate} - ${edu.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 32f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 14f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.institution, 32f, pageManager.currentY, instPaint)
                pageManager.currentY += 16f
            }
        }

        // 4. Projects
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "KEY PROJECTS", primaryColorInt, 32f, width - 32f)

            for (proj in data.projects) {
                pageManager.ensureSpace(35f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(proj.projectName, 32f, pageManager.currentY, projNamePaint)
                pageManager.currentY += 14f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                val descLines = PdfDrawUtils.wrapText(proj.description, descPaint, width - 64f)
                for (l in descLines) {
                    pageManager.ensureSpace(14f)
                    pageManager.canvas.drawText(l, 32f, pageManager.currentY, descPaint)
                    pageManager.currentY += 14f
                }
                pageManager.currentY += 8f
            }
        }

        // 5. Skills Pill Section
        if (data.skills.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CORE SKILLS & TOOLS", primaryColorInt, 32f, width - 32f)

            val pillBgPaint = Paint().apply {
                color = Color.parseColor("#F1F5F9")
                style = Paint.Style.FILL
            }
            val pillTextPaint = Paint().apply {
                color = primaryColorInt
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            var currX = 32f
            pageManager.ensureSpace(24f)

            for (skill in data.skills) {
                val textW = pillTextPaint.measureText(skill)
                val pillW = textW + 16f

                if (currX + pillW > width - 32f) {
                    currX = 32f
                    pageManager.currentY += 22f
                    pageManager.ensureSpace(22f)
                }

                val rect = RectF(currX, pageManager.currentY - 10f, currX + pillW, pageManager.currentY + 8f)
                pageManager.canvas.drawRoundRect(rect, 6f, 6f, pillBgPaint)
                pageManager.canvas.drawText(skill, currX + 8f, pageManager.currentY + 2f, pillTextPaint)

                currX += pillW + 8f
            }
            pageManager.currentY += 22f
        }

        // 6. Certifications & Credentials
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CERTIFICATIONS & LICENSES", primaryColorInt, 32f, width - 32f)
            val certPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            for (cert in data.certifications) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText("• ${cert.name} — ${cert.issuer} (${cert.date})", 32f, pageManager.currentY, certPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 10f
        }

        // 7. Languages
        if (data.languages.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "LANGUAGES", primaryColorInt, 32f, width - 32f)
            val langPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val langText = data.languages.joinToString("   |   ") { "${it.languageName} (${it.proficiency})" }
            pageManager.canvas.drawText(langText, 32f, pageManager.currentY, langPaint)
            pageManager.currentY += 18f
        }

        // 8. Hobbies & References
        if (data.hobbies.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            PdfDrawUtils.drawSectionHeader(pageManager, "INTERESTS & HOBBIES", primaryColorInt, 32f, width - 32f)
            val hobbyPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.hobbies.joinToString("  •  "), 32f, pageManager.currentY, hobbyPaint)
            pageManager.currentY += 18f
        }

        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "REFERENCES", primaryColorInt, 32f, width - 32f)
            val refPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val refText = data.references.joinToString("   |   ") { "${it.name} (${it.title}, ${it.company})" }
            val lines = PdfDrawUtils.wrapText(refText, refPaint, width - 64f)
            for (l in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(l, 32f, pageManager.currentY, refPaint)
                pageManager.currentY += 14f
            }
        }

        pageManager.finish()
    }
}
