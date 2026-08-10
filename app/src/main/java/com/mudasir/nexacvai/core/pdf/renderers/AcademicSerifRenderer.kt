package com.mudasir.nexacvai.core.pdf.renderers

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.toArgb
import com.mudasir.nexacvai.core.pdf.PdfPageManager
import com.mudasir.nexacvai.core.pdf.utils.PdfDrawUtils
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle

/**
 * 🎓 Academic Serif Renderer (Minimalist Serif)
 * High-typography contrast serif pairing with delicate accent double-rules,
 * centered academic header, and research publication highlights.
 * Designed specifically for Professors, Academic Researchers, and Ph.D. Fellows.
 */
class AcademicSerifRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        pageManager.currentY = 40f

        val hasPhoto = templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()

        // 1. Centered Academic Serif Name Header
        val namePaint = Paint().apply {
            color = Color.parseColor("#111827")
            textSize = 24f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        val nameW = namePaint.measureText(data.fullName)
        val nameX = (width - nameW) / 2f
        pageManager.canvas.drawText(data.fullName.ifBlank { "Prof. Academic Name" }, nameX, pageManager.currentY, namePaint)
        pageManager.currentY += 20f

        // Professional Title (Centered Serif Italic)
        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#374151")
                textSize = 11.5f
                typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                isAntiAlias = true
            }
            val titleW = titlePaint.measureText(data.professionalTitle)
            val titleX = (width - titleW) / 2f
            pageManager.canvas.drawText(data.professionalTitle, titleX, pageManager.currentY, titlePaint)
            pageManager.currentY += 18f
        }

        // Contact Line (Centered Serif)
        val contactPaint = Paint().apply {
            color = Color.parseColor("#4B5563")
            textSize = 9f
            typeface = Typeface.SERIF
            isAntiAlias = true
        }
        val contactInfo = listOfNotNull(
            data.email.ifBlank { null },
            data.phone.ifBlank { null },
            data.location.ifBlank { null }
        ).joinToString("  •  ")
        val contactW = contactPaint.measureText(contactInfo)
        val contactX = (width - contactW) / 2f
        pageManager.canvas.drawText(contactInfo, contactX, pageManager.currentY, contactPaint)
        pageManager.currentY += 14f

        // Draw Profile Photo if selected
        if (hasPhoto) {
            try {
                PdfDrawUtils.drawStyledProfilePhoto(
                    context = context,
                    canvas = pageManager.canvas,
                    sourceUriString = data.profilePictureUri!!,
                    rightX = width - 36f,
                    centerY = 48f,
                    shape = templateStyle.photoShape
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Elegant Double Accent Divider Rule
        val rulePaint = Paint().apply {
            color = Color.parseColor("#9CA3AF")
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        pageManager.canvas.drawLine(40f, pageManager.currentY, width - 40f, pageManager.currentY, rulePaint)
        pageManager.canvas.drawLine(40f, pageManager.currentY + 2.5f, width - 40f, pageManager.currentY + 2.5f, rulePaint)
        pageManager.currentY += 22f

        // 2. Academic Summary / Research Vision
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            drawSerifSectionHeader(pageManager, data.getSectionTitle("SUMMARY", "RESEARCH PROFILE & ACADEMIC VISION"), primaryColorInt, width)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#1F2937")
                textSize = 9.5f
                typeface = Typeface.SERIF
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, width - 80f)
            for (line in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(line, 40f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 14f
        }

        // 3. Education & Dissertation (Academic Priority #1)
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSerifSectionHeader(pageManager, data.getSectionTitle("EDUCATION", "EDUCATION & ACADEMIC DEGREES"), primaryColorInt, width)

            for (edu in data.educations) {
                pageManager.ensureSpace(35f)
                val degreePaint = Paint().apply {
                    color = Color.parseColor("#111827")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                    isAntiAlias = true
                }
                val degreeTitle = if (edu.gradeOrGpa.isNotBlank()) "${edu.degree}  (${edu.gradeOrGpa})" else edu.degree
                pageManager.canvas.drawText(degreeTitle, 40f, pageManager.currentY, degreePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#4B5563")
                    textSize = 9.5f
                    typeface = Typeface.SERIF
                    isAntiAlias = true
                }
                val dateText = "${edu.startDate} - ${edu.endDate}"
                val dateW = datePaint.measureText(dateText)
                pageManager.canvas.drawText(dateText, width - 40f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 14f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#4B5563")
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.institution, 40f, pageManager.currentY, instPaint)
                pageManager.currentY += 14f

                if (edu.relevantCoursework.isNotBlank() || edu.description.isNotBlank()) {
                    val thesisPaint = Paint().apply {
                        color = Color.parseColor("#374151")
                        textSize = 9f
                        typeface = Typeface.SERIF
                        isAntiAlias = true
                    }
                    val courseText = "• ${edu.relevantCoursework.ifBlank { edu.description }}"
                    val courseLines = PdfDrawUtils.wrapText(courseText, thesisPaint, width - 80f)
                    for (l in courseLines) {
                        pageManager.ensureSpace(14f)
                        pageManager.canvas.drawText(l, 40f, pageManager.currentY, thesisPaint)
                        pageManager.currentY += 14f
                    }
                }
                pageManager.currentY += 10f
            }
        }

        // 4. Academic & Research Appointments
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSerifSectionHeader(pageManager, data.getSectionTitle("EXPERIENCE", "ACADEMIC & RESEARCH APPOINTMENTS"), primaryColorInt, width)

            for (exp in data.experiences) {
                pageManager.ensureSpace(45f)
                val jobTitlePaint = Paint().apply {
                    color = Color.parseColor("#111827")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.jobTitle, 40f, pageManager.currentY, jobTitlePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#4B5563")
                    textSize = 9.5f
                    typeface = Typeface.SERIF
                    isAntiAlias = true
                }
                val dateText = "${exp.startDate} - ${exp.endDate}"
                val dateW = datePaint.measureText(dateText)
                pageManager.canvas.drawText(dateText, width - 40f - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 15f

                val companyPaint = Paint().apply {
                    color = Color.parseColor("#374151")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.company, 40f, pageManager.currentY, companyPaint)
                pageManager.currentY += 15f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#1F2937")
                    textSize = 9.5f
                    typeface = Typeface.SERIF
                    isAntiAlias = true
                }
                for (resp in exp.responsibilities) {
                    val respLines = PdfDrawUtils.wrapText("• $resp", descPaint, width - 80f)
                    for (l in respLines) {
                        pageManager.ensureSpace(14f)
                        pageManager.canvas.drawText(l, 40f, pageManager.currentY, descPaint)
                        pageManager.currentY += 14f
                    }
                }
                pageManager.currentY += 10f
            }
        }

        // 5. Research Projects & Peer-Reviewed Grants
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSerifSectionHeader(pageManager, data.getSectionTitle("PROJECTS", "RESEARCH PROJECTS & GRANTS"), primaryColorInt, width)

            for (proj in data.projects) {
                pageManager.ensureSpace(35f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#111827")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                    isAntiAlias = true
                }
                val titleStr = if (proj.roleInProject.isNotBlank()) "${proj.projectName} — ${proj.roleInProject}" else proj.projectName
                pageManager.canvas.drawText(titleStr, 40f, pageManager.currentY, projNamePaint)
                pageManager.currentY += 14f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#1F2937")
                    textSize = 9.5f
                    typeface = Typeface.SERIF
                    isAntiAlias = true
                }
                val descLines = PdfDrawUtils.wrapText(proj.description, descPaint, width - 80f)
                for (l in descLines) {
                    pageManager.ensureSpace(14f)
                    pageManager.canvas.drawText(l, 40f, pageManager.currentY, descPaint)
                    pageManager.currentY += 14f
                }
                pageManager.currentY += 8f
            }
        }

        // 6. Academic Skills & Methodology
        if (data.skills.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSerifSectionHeader(pageManager, data.getSectionTitle("SKILLS", "RESEARCH METHODOLOGY & EXPERTISE"), primaryColorInt, width)

            val skillPaint = Paint().apply {
                color = Color.parseColor("#1F2937")
                textSize = 9.5f
                typeface = Typeface.SERIF
                isAntiAlias = true
            }
            val skillText = data.skills.joinToString("  •  ")
            val lines = PdfDrawUtils.wrapText(skillText, skillPaint, width - 80f)
            for (line in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(line, 40f, pageManager.currentY, skillPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 10f
        }

        // 7. Academic Honors & Fellowships
        if (data.awards.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            drawSerifSectionHeader(pageManager, data.getSectionTitle("AWARDS", "ACADEMIC HONORS & FELLOWSHIPS"), primaryColorInt, width)
            val awardPaint = Paint().apply {
                color = Color.parseColor("#1F2937")
                textSize = 9.5f
                typeface = Typeface.SERIF
                isAntiAlias = true
            }
            for (award in data.awards) {
                pageManager.ensureSpace(14f)
                val lines = PdfDrawUtils.wrapText("• $award", awardPaint, width - 80f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 40f, pageManager.currentY, awardPaint)
                    pageManager.currentY += 14f
                }
            }
            pageManager.currentY += 10f
        }

        // 8. References
        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            drawSerifSectionHeader(pageManager, data.getSectionTitle("REFERENCES", "ACADEMIC REFERENCES"), primaryColorInt, width)
            val refPaint = Paint().apply {
                color = Color.parseColor("#1F2937")
                textSize = 9.5f
                typeface = Typeface.SERIF
                isAntiAlias = true
            }
            for (ref in data.references) {
                pageManager.ensureSpace(14f)
                val contactDetail = listOfNotNull(ref.company.ifBlank { null }, ref.email.ifBlank { null }, ref.phone.ifBlank { null }).joinToString(" • ")
                val refStr = "• ${ref.name} — ${ref.title} (${contactDetail})"
                val lines = PdfDrawUtils.wrapText(refStr, refPaint, width - 80f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 40f, pageManager.currentY, refPaint)
                    pageManager.currentY += 14f
                }
            }
        }

        pageManager.finish()
    }

    private fun drawSerifSectionHeader(pageManager: PdfPageManager, title: String, colorInt: Int, width: Float) {
        val headPaint = Paint().apply {
            color = Color.parseColor("#111827")
            textSize = 10.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(title, 40f, pageManager.currentY, headPaint)

        val linePaint = Paint().apply {
            color = Color.parseColor("#D1D5DB")
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        pageManager.canvas.drawLine(40f, pageManager.currentY + 4f, width - 40f, pageManager.currentY + 4f, linePaint)
        pageManager.currentY += 18f
    }
}
