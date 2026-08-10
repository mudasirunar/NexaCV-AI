package com.mudasir.nexacvai.core.pdf.renderers

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.toArgb
import com.mudasir.nexacvai.core.pdf.PdfPageManager
import com.mudasir.nexacvai.core.pdf.utils.PdfDrawUtils
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle

/**
 * 💻 Developer Terminal Slate Renderer
 * Dark slate top header, monospace code accent headers (// OVERVIEW, // WORK_EXPERIENCE, // FEATURED_PROJECTS),
 * rendering 100% of UserProfile sections with clean developer styling.
 */
class DeveloperSlateRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        // Dark Slate Top Banner
        val bannerHeight = 100f
        val bannerPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            style = Paint.Style.FILL
        }
        pageManager.canvas.drawRect(0f, 0f, width, bannerHeight, bannerPaint)

        // Monospace Terminal Accent
        val termPaint = Paint().apply {
            color = Color.parseColor("#38BDF8")
            textSize = 10f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }
        pageManager.canvas.drawText("class Developer {", 32f, 28f, termPaint)

        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Developer Candidate" }, 32f, 52f, namePaint)

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#94A3B8")
                textSize = 11f
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }
            pageManager.canvas.drawText("// ${data.professionalTitle}", 32f, 70f, titlePaint)
        }

        val contactPaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            textSize = 8.5f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }
        val contactStr = listOfNotNull(data.email.ifBlank { null }, data.phone.ifBlank { null }, data.location.ifBlank { null }).joinToString(" | ")
        pageManager.canvas.drawText(contactStr, 32f, 86f, contactPaint)

        // Draw Avatar Photo if available (Passport or Rounded Square)
        if (templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()) {
            try {
                PdfDrawUtils.drawStyledProfilePhoto(
                    context = context,
                    canvas = pageManager.canvas,
                    sourceUriString = data.profilePictureUri,
                    rightX = width - 32f,
                    centerY = bannerHeight / 2f,
                    shape = templateStyle.photoShape
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        pageManager.currentY = bannerHeight + 24f

        // 1. Monospace Overview
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            drawDevSectionHeader(pageManager, "// OVERVIEW", primaryColorInt, width)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#1E293B")
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
            drawDevSectionHeader(pageManager, "// WORK_EXPERIENCE", primaryColorInt, width)

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
                    color = Color.parseColor("#0284C7")
                    textSize = 9.5f
                    typeface = Typeface.MONOSPACE
                    isAntiAlias = true
                }
                val dateStr = "${exp.startDate} -> ${exp.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, width - 32f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 15f

                val companyPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText("@${exp.company}", 32f, pageManager.currentY, companyPaint)
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
            drawDevSectionHeader(pageManager, "// EDUCATION", primaryColorInt, width)

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
                    color = Color.parseColor("#0284C7")
                    textSize = 9.5f
                    typeface = Typeface.MONOSPACE
                    isAntiAlias = true
                }
                val dateStr = "${edu.startDate} -> ${edu.endDate}"
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

        // 4. Featured Projects
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawDevSectionHeader(pageManager, "// FEATURED_PROJECTS", primaryColorInt, width)

            for (proj in data.projects) {
                pageManager.ensureSpace(35f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText("git repo: ${proj.projectName}", 32f, pageManager.currentY, projNamePaint)
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

        // 5. Tech Stack & Skills
        if (data.skills.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawDevSectionHeader(pageManager, "// TECH_STACK", primaryColorInt, width)
            val skillPaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 9.5f
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }
            val skillText = "const val SKILLS = [${data.skills.joinToString(", ") { "\"$it\"" }}]"
            val lines = PdfDrawUtils.wrapText(skillText, skillPaint, width - 64f)
            for (l in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(l, 32f, pageManager.currentY, skillPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 10f
        }

        // 6. Certifications
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            drawDevSectionHeader(pageManager, "// CERTIFICATIONS", primaryColorInt, width)
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

        // 7. Languages & Hobbies
        if (data.languages.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            drawDevSectionHeader(pageManager, "// LANGUAGES", primaryColorInt, width)
            val langPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val langText = data.languages.joinToString("   |   ") { "${it.languageName} (${it.proficiency})" }
            pageManager.canvas.drawText(langText, 32f, pageManager.currentY, langPaint)
            pageManager.currentY += 18f
        }

        if (data.hobbies.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            drawDevSectionHeader(pageManager, "// INTERESTS", primaryColorInt, width)
            val hobbyPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.hobbies.joinToString("  •  "), 32f, pageManager.currentY, hobbyPaint)
            pageManager.currentY += 18f
        }

        pageManager.finish()
    }

    private fun drawDevSectionHeader(pageManager: PdfPageManager, title: String, colorInt: Int, width: Float) {
        val headPaint = Paint().apply {
            color = Color.parseColor("#0284C7")
            textSize = 11f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }
        pageManager.canvas.drawText(title, 32f, pageManager.currentY, headPaint)

        val linePaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        pageManager.canvas.drawLine(32f, pageManager.currentY + 4f, width - 32f, pageManager.currentY + 4f, linePaint)
        pageManager.currentY += 18f
    }
}
