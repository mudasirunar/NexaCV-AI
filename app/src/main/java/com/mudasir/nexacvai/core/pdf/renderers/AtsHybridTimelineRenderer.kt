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
 * 🎓 ATS Hybrid Timeline Renderer
 * Clean single-column ATS structure with right-aligned dates, full-width descriptions,
 * and unified primary accent color scheme (eliminating empty column gaps).
 * Designed for fresh graduates, junior engineers, and career changers.
 */
class AtsHybridTimelineRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()
        pageManager.currentY = 40f

        val hasPhoto = templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()

        // Name Header - Styled in primary accent color for visual unity
        val namePaint = Paint().apply {
            color = primaryColorInt
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Candidate Name" }, 36f, pageManager.currentY, namePaint)
        pageManager.currentY += 20f

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#475569")
                textSize = 11.5f
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

        // Draw Avatar Photo if available
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

        // 1. Professional Summary
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("SUMMARY", "SUMMARY & OBJECTIVE"), primaryColorInt, 36f, width - 36f)
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

        // 2. Education & Academic Honors (Priority #1 for Fresh Graduates)
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("EDUCATION", "EDUCATION & ACADEMIC CREDENTIALS"), primaryColorInt, 36f, width - 36f)

            for (edu in data.educations) {
                pageManager.ensureSpace(35f)

                val degreePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val degreeTitle = if (edu.gradeOrGpa.isNotBlank()) "${edu.degree}  (GPA: ${edu.gradeOrGpa})" else edu.degree
                pageManager.canvas.drawText(degreeTitle, 36f, pageManager.currentY, degreePaint)

                val datePaint = Paint().apply {
                    color = primaryColorInt
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateText = "${edu.startDate} - ${edu.endDate}"
                val dateW = datePaint.measureText(dateText)
                pageManager.canvas.drawText(dateText, width - 36f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 15f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.institution, 36f, pageManager.currentY, instPaint)
                pageManager.currentY += 14f

                if (edu.relevantCoursework.isNotBlank() || edu.description.isNotBlank()) {
                    val courseText = "• ${edu.relevantCoursework.ifBlank { edu.description }}"
                    val courseLines = PdfDrawUtils.wrapText(courseText, instPaint, width - 72f)
                    for (l in courseLines) {
                        pageManager.ensureSpace(14f)
                        pageManager.canvas.drawText(l, 36f, pageManager.currentY, instPaint)
                        pageManager.currentY += 14f
                    }
                }
                pageManager.currentY += 10f
            }
            pageManager.currentY += 6f
        }

        // 3. Key Projects & Capstones
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("PROJECTS", "FEATURED PROJECTS & CAPSTONES"), primaryColorInt, 36f, width - 36f)

            for (proj in data.projects) {
                pageManager.ensureSpace(35f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val titleStr = if (proj.roleInProject.isNotBlank()) "${proj.projectName} — ${proj.roleInProject}" else proj.projectName
                pageManager.canvas.drawText(titleStr, 36f, pageManager.currentY, projNamePaint)

                if (proj.projectLink.isNotBlank()) {
                    val linkPaint = Paint().apply {
                        color = primaryColorInt
                        textSize = 9f
                        isAntiAlias = true
                    }
                    val linkW = linkPaint.measureText(proj.projectLink)
                    pageManager.canvas.drawText(proj.projectLink, width - 36f - linkW, pageManager.currentY, linkPaint)
                }
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

                if (proj.technologiesUsed.isNotEmpty()) {
                    val techStr = "Tech Stack: " + proj.technologiesUsed.joinToString(", ")
                    val techPaint = Paint().apply {
                        color = Color.parseColor("#64748B")
                        textSize = 9f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                        isAntiAlias = true
                    }
                    pageManager.canvas.drawText(techStr, 36f, pageManager.currentY, techPaint)
                    pageManager.currentY += 14f
                }
                pageManager.currentY += 8f
            }
        }

        // 4. Work & Internship Experience
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("EXPERIENCE", "EXPERIENCE & INTERNSHIPS"), primaryColorInt, 36f, width - 36f)

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
                    color = primaryColorInt
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateText = "${exp.startDate} - ${exp.endDate}"
                val dateW = datePaint.measureText(dateText)
                pageManager.canvas.drawText(dateText, width - 36f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 15f

                val companyPaint = Paint().apply {
                    color = Color.parseColor("#475569")
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
                        pageManager.ensureSpace(14f)
                        pageManager.canvas.drawText(line, 36f, pageManager.currentY, descPaint)
                        pageManager.currentY += 14f
                    }
                }
                pageManager.currentY += 10f
            }
            pageManager.currentY += 6f
        }

        // 5. Technical Skills & Tools
        if (data.skills.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("SKILLS", "TECHNICAL SKILLS & TOOLS"), primaryColorInt, 36f, width - 36f)

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

        // 6. Certifications
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("CERTIFICATIONS", "CERTIFICATIONS"), primaryColorInt, 36f, width - 36f)
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

        // 7. Languages & Hobbies
        if (data.languages.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("LANGUAGES", "LANGUAGES"), primaryColorInt, 36f, width - 36f)
            val langPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val langText = data.languages.joinToString("   |   ") { "${it.languageName} (${it.proficiency})" }
            pageManager.canvas.drawText(langText, 36f, pageManager.currentY, langPaint)
            pageManager.currentY += 18f
        }

        if (data.hobbies.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("HOBBIES", "INTERESTS & ACTIVITIES"), primaryColorInt, 36f, width - 36f)
            val hobbyPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val hobbyText = data.hobbies.joinToString("  •  ")
            pageManager.canvas.drawText(hobbyText, 36f, pageManager.currentY, hobbyPaint)
            pageManager.currentY += 18f
        }

        // 8. References
        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("REFERENCES", "REFERENCES"), primaryColorInt, 36f, width - 36f)
            val refPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            for (ref in data.references) {
                pageManager.ensureSpace(14f)
                val contactDetail = listOfNotNull(ref.company.ifBlank { null }, ref.email.ifBlank { null }, ref.phone.ifBlank { null }).joinToString(" • ")
                val refStr = "• ${ref.name} — ${ref.title} (${contactDetail})"
                val lines = PdfDrawUtils.wrapText(refStr, refPaint, width - 72f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 36f, pageManager.currentY, refPaint)
                    pageManager.currentY += 14f
                }
            }
        }

        pageManager.finish()
    }
}
