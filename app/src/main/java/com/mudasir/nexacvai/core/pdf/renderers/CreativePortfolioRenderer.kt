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
 * 🎨 Creative Portfolio Renderer
 * Coral/Vibrant accent header, floating card containers, portfolio project highlight cards,
 * rendering 100% of UserProfile domain fields with clean MS Word-style spacing (14pt line height).
 */
class CreativePortfolioRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        pageManager.currentY = 32f

        // Top Accent Bar
        val accentBarPaint = Paint().apply {
            color = primaryColorInt
            style = Paint.Style.FILL
        }
        pageManager.canvas.drawRect(28f, pageManager.currentY, width - 28f, pageManager.currentY + 6f, accentBarPaint)
        pageManager.currentY += 24f

        // Name & Creative Title
        val namePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Creative Designer" }, 28f, pageManager.currentY, namePaint)
        pageManager.currentY += 18f

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = primaryColorInt
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.professionalTitle, 28f, pageManager.currentY, titlePaint)
            pageManager.currentY += 18f
        }

        // Contact info line
        val contactPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 9.5f
            isAntiAlias = true
        }
        val contactInfo = listOfNotNull(data.email.ifBlank { null }, data.phone.ifBlank { null }, data.location.ifBlank { null }).joinToString("  |  ")
        pageManager.canvas.drawText(contactInfo, 28f, pageManager.currentY, contactPaint)
        pageManager.currentY += 24f

        // 1. Creative Summary Card
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CREATIVE SUMMARY & VISION", primaryColorInt, 28f, width - 28f)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, width - 56f)
            for (l in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(l, 28f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 14f
        }

        // 2. Featured Projects Highlight Block (Floating Cards)
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, "FEATURED PORTFOLIO PROJECTS", primaryColorInt, 28f, width - 28f)

            for (proj in data.projects) {
                pageManager.ensureSpace(45f)
                val cardBgPaint = Paint().apply {
                    color = Color.parseColor("#F8FAFC")
                    style = Paint.Style.FILL
                }
                val cardRect = RectF(28f, pageManager.currentY - 4f, width - 28f, pageManager.currentY + 38f)
                pageManager.canvas.drawRoundRect(cardRect, 6f, 6f, cardBgPaint)

                val projTitlePaint = Paint().apply {
                    color = primaryColorInt
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(proj.projectName, 36f, pageManager.currentY + 12f, projTitlePaint)

                val projDescPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(proj.description, 36f, pageManager.currentY + 26f, projDescPaint)

                pageManager.currentY += 48f
            }
            pageManager.currentY += 8f
        }

        // 3. Work Experience
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "WORK EXPERIENCE", primaryColorInt, 28f, width - 28f)

            for (exp in data.experiences) {
                pageManager.ensureSpace(45f)
                val jobTitlePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.jobTitle, 28f, pageManager.currentY, jobTitlePaint)
                pageManager.currentY += 15f

                val companyPaint = Paint().apply {
                    color = primaryColorInt
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText("${exp.company}  (${exp.startDate} - ${exp.endDate})", 28f, pageManager.currentY, companyPaint)
                pageManager.currentY += 15f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                for (resp in exp.responsibilities) {
                    val respLines = PdfDrawUtils.wrapText("• $resp", descPaint, width - 56f)
                    for (l in respLines) {
                        pageManager.ensureSpace(14f)
                        pageManager.canvas.drawText(l, 28f, pageManager.currentY, descPaint)
                        pageManager.currentY += 14f
                    }
                }
                pageManager.currentY += 10f
            }
        }

        // 4. Education
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "EDUCATION & TRAINING", primaryColorInt, 28f, width - 28f)

            for (edu in data.educations) {
                pageManager.ensureSpace(30f)
                val degreePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.degree, 28f, pageManager.currentY, degreePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                val dateStr = "${edu.startDate} - ${edu.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 28f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 14f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.institution, 28f, pageManager.currentY, instPaint)
                pageManager.currentY += 16f
            }
        }

        // 5. Creative Skills
        if (data.skills.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CREATIVE SKILLS & SOFTWARE", primaryColorInt, 28f, width - 28f)
            val skillPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val skillText = data.skills.joinToString("  •  ")
            val lines = PdfDrawUtils.wrapText(skillText, skillPaint, width - 56f)
            for (l in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(l, 28f, pageManager.currentY, skillPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 10f
        }

        // 6. Certifications & Honors
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CERTIFICATIONS & CREDENTIALS", primaryColorInt, 28f, width - 28f)
            val certPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            for (cert in data.certifications) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText("• ${cert.name} — ${cert.issuer} (${cert.date})", 28f, pageManager.currentY, certPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 10f
        }

        // 7. Hobbies & References
        if (data.hobbies.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CREATIVE INTERESTS & HOBBIES", primaryColorInt, 28f, width - 28f)
            val hobbyPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.hobbies.joinToString("  •  "), 28f, pageManager.currentY, hobbyPaint)
            pageManager.currentY += 18f
        }

        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "PORTFOLIO REFERENCES", primaryColorInt, 28f, width - 28f)
            val refPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val refText = data.references.joinToString("   |   ") { "${it.name} (${it.title}, ${it.company})" }
            val lines = PdfDrawUtils.wrapText(refText, refPaint, width - 56f)
            for (l in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(l, 28f, pageManager.currentY, refPaint)
                pageManager.currentY += 14f
            }
        }

        pageManager.finish()
    }
}
