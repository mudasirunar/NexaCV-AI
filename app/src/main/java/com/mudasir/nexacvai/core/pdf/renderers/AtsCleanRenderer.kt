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
 * 🎯 ATS Clean Standard Renderer
 * Single-column parser-safe ATS template rendering 100% of [TemplateData] fields
 * (Summary, Experience, Education, Projects, Skills, Certifications, Languages, References, Hobbies, Volunteer Work, Awards).
 * Smoothly supports avatar photos when enabled.
 */
class AtsCleanRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()
        pageManager.currentY = 40f

        val hasPhoto = templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()

        // Name & Title Header
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
                color = Color.parseColor("#475569")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
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
        val contactInfo = listOfNotNull(
            data.email.ifBlank { null },
            data.phone.ifBlank { null },
            data.location.ifBlank { null }
        ).joinToString("  •  ")
        pageManager.canvas.drawText(contactInfo, 36f, pageManager.currentY, contactPaint)
        pageManager.currentY += 16f

        // Draw Avatar Photo ONLY IF photo is selected (no empty placeholder)
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
            color = Color.parseColor("#CBD5E1")
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        pageManager.canvas.drawLine(36f, pageManager.currentY, width - 36f, pageManager.currentY, rulePaint)
        pageManager.currentY += 20f

        // 1. Professional Summary
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, "PROFESSIONAL SUMMARY", primaryColorInt, 36f, width - 36f)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, width - 72f)
            for (line in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(line, 36f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 14f
        }

        // 2. Work Experience
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "WORK EXPERIENCE", primaryColorInt, 36f, width - 36f)

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
                    for (line in respLines) {
                        pageManager.ensureSpace(13f)
                        pageManager.canvas.drawText(line, 36f, pageManager.currentY, descPaint)
                        pageManager.currentY += 13f
                    }
                }
                pageManager.currentY += 10f
            }
            pageManager.currentY += 6f
        }

        // 3. Education
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "EDUCATION", primaryColorInt, 36f, width - 36f)

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
                val dateText = "${edu.startDate} - ${edu.endDate}"
                val dateW = datePaint.measureText(dateText)
                pageManager.canvas.drawText(dateText, width - 36f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 14f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.institution, 36f, pageManager.currentY, instPaint)
                pageManager.currentY += 16f
            }
            pageManager.currentY += 6f
        }

        // 4. Key Projects
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "KEY PROJECTS", primaryColorInt, 36f, width - 36f)

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
                    pageManager.ensureSpace(13f)
                    pageManager.canvas.drawText(l, 36f, pageManager.currentY, descPaint)
                    pageManager.currentY += 13f
                }
                pageManager.currentY += 8f
            }
        }

        // 5. Skills & Competencies
        if (data.skills.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "SKILLS & COMPETENCIES", primaryColorInt, 36f, width - 36f)

            val skillPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val skillText = data.skills.joinToString("  •  ")
            val lines = PdfDrawUtils.wrapText(skillText, skillPaint, width - 72f)
            for (line in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(line, 36f, pageManager.currentY, skillPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 10f
        }

        // 6. Certifications & Credentials
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CERTIFICATIONS & LICENSES", primaryColorInt, 36f, width - 36f)
            val certPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            for (cert in data.certifications) {
                pageManager.ensureSpace(14f)
                val text = "• ${cert.name} — ${cert.issuer} (${cert.date})"
                val lines = PdfDrawUtils.wrapText(text, certPaint, width - 72f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 36f, pageManager.currentY, certPaint)
                    pageManager.currentY += 14f
                }
            }
            pageManager.currentY += 10f
        }

        // 7. Languages
        if (data.languages.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "LANGUAGES", primaryColorInt, 36f, width - 36f)
            val langPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val langText = data.languages.joinToString("   |   ") { "${it.languageName} (${it.proficiency})" }
            pageManager.canvas.drawText(langText, 36f, pageManager.currentY, langPaint)
            pageManager.currentY += 18f
        }

        // 8. Volunteer Work & Community Engagement
        if (data.volunteerWork.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "VOLUNTEER & COMMUNITY ENGAGEMENT", primaryColorInt, 36f, width - 36f)
            val volPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            for (vol in data.volunteerWork) {
                pageManager.ensureSpace(14f)
                val lines = PdfDrawUtils.wrapText("• $vol", volPaint, width - 72f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 36f, pageManager.currentY, volPaint)
                    pageManager.currentY += 14f
                }
            }
            pageManager.currentY += 10f
        }

        // 9. Honors & Awards
        if (data.awards.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "HONORS & AWARDS", primaryColorInt, 36f, width - 36f)
            val awardPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            for (award in data.awards) {
                pageManager.ensureSpace(14f)
                val lines = PdfDrawUtils.wrapText("• $award", awardPaint, width - 72f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 36f, pageManager.currentY, awardPaint)
                    pageManager.currentY += 14f
                }
            }
            pageManager.currentY += 10f
        }

        // 10. Hobbies & Interests
        if (data.hobbies.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            PdfDrawUtils.drawSectionHeader(pageManager, "INTERESTS & HOBBIES", primaryColorInt, 36f, width - 36f)
            val hobbyPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val hobbyText = data.hobbies.joinToString("  •  ")
            pageManager.canvas.drawText(hobbyText, 36f, pageManager.currentY, hobbyPaint)
            pageManager.currentY += 18f
        }

        // 11. Professional References
        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, "REFERENCES", primaryColorInt, 36f, width - 36f)
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
