package com.mudasir.nexacvai.core.pdf.renderers

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.toArgb
import com.mudasir.nexacvai.core.pdf.PdfPageManager
import com.mudasir.nexacvai.core.pdf.utils.PdfDrawUtils
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle

/**
 * 🛡️ Cyber Security Code Matrix Renderer
 * High-contrast dark cyber terminal header (root@security-matrix:~# cat profile.sys),
 * Compact Clearance Badge Box ([ TOP SECRET CLEARANCE ]), borderless threat intelligence skills matrix table,
 * and monospace security logs.
 */
class CyberSecurityMatrixRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        val hasPhoto = templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()

        // Dark Cyber Slate Top Banner Header
        val bannerHeight = 115f
        val bannerPaint = Paint().apply {
            color = Color.parseColor("#0D1117")
            style = Paint.Style.FILL
        }
        pageManager.canvas.drawRect(0f, 0f, width, bannerHeight, bannerPaint)

        // Monospace Terminal Command Prompt Line
        val termPaint = Paint().apply {
            color = Color.parseColor("#10B981") // Emerald Cyber Green
            textSize = 9.5f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }
        pageManager.canvas.drawText("root@security-matrix:~# cat profile.sys", 32f, 26f, termPaint)

        // Name & Cyber Security Title
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 21f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Cyber Security Lead" }, 32f, 50f, namePaint)

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#06B6D4") // Cyber Cyan Accent
                textSize = 11f
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }
            pageManager.canvas.drawText("// ${data.professionalTitle}", 32f, 68f, titlePaint)
        }

        // Contact Info Line
        val contactPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 8.5f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }
        val contactStr = listOfNotNull(data.email.ifBlank { null }, data.phone.ifBlank { null }, data.location.ifBlank { null }).joinToString(" | ")
        pageManager.canvas.drawText(contactStr, 32f, 86f, contactPaint)

        // Draw Avatar Photo if available
        if (hasPhoto) {
            try {
                PdfDrawUtils.drawStyledProfilePhoto(
                    context = context,
                    canvas = pageManager.canvas,
                    sourceUriString = data.profilePictureUri!!,
                    rightX = width - 32f,
                    centerY = bannerHeight / 2f,
                    shape = templateStyle.photoShape
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Compact Security Clearance Badge Box (Positioned safely without prompt or photo collision)
        val photoSpaceOffset = if (hasPhoto) 70f else 0f
        val badgePaint = Paint().apply {
            color = Color.parseColor("#064E3B") // Dark Emerald Fill
            style = Paint.Style.FILL
        }
        val badgeBorderPaint = Paint().apply {
            color = Color.parseColor("#10B981") // Emerald Border
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        val badgeTextPaint = Paint().apply {
            color = Color.parseColor("#A7F3D0")
            textSize = 8f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }
        val badgeText = "[ TOP SECRET CLEARANCE ]"
        val badgeW = badgeTextPaint.measureText(badgeText) + 12f
        val badgeRightX = width - 32f - photoSpaceOffset
        val devBadgeRect = RectF(badgeRightX - badgeW, 14f, badgeRightX, 32f)
        pageManager.canvas.drawRoundRect(devBadgeRect, 4f, 4f, badgePaint)
        pageManager.canvas.drawRoundRect(devBadgeRect, 4f, 4f, badgeBorderPaint)
        pageManager.canvas.drawText(badgeText, badgeRightX - badgeW + 6f, 25f, badgeTextPaint)

        pageManager.currentY = bannerHeight + 24f

        // 1. Executive Security Summary
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            drawCyberHeader(pageManager, "// 01. SECURITY_EXECUTIVE_SUMMARY", primaryColorInt, width)
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

        // 2. Threat Intelligence & Categorized Skills Matrix
        val categoriesToRender = if (data.skillCategoryGroups.isNotEmpty()) {
            data.skillCategoryGroups
        } else if (data.skills.isNotEmpty()) {
            listOf(com.mudasir.nexacvai.domain.model.template.TemplateSkillCategoryGroup("SECURITY COMPETENCIES", data.skills))
        } else emptyList()

        if (categoriesToRender.isNotEmpty()) {
            pageManager.ensureSpace(50f)
            drawCyberHeader(pageManager, "// 02. SECURITY_COMPETENCIES_MATRIX", primaryColorInt, width)

            val catNamePaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 9.5f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                isAntiAlias = true
            }
            val skillsTextPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9f
                isAntiAlias = true
            }

            val labelColumnW = 150f
            val valueX = 32f + labelColumnW
            val valueW = width - 32f - valueX

            for (group in categoriesToRender) {
                pageManager.ensureSpace(24f)
                pageManager.canvas.drawText(group.categoryName, 32f, pageManager.currentY, catNamePaint)

                val skillsStr = group.skills.joinToString("  •  ")
                val lines = PdfDrawUtils.wrapText(skillsStr, skillsTextPaint, valueW)

                var lineY = pageManager.currentY
                for (line in lines) {
                    pageManager.canvas.drawText(line, valueX, lineY, skillsTextPaint)
                    lineY += 14f
                }
                pageManager.currentY = Math.max(pageManager.currentY + 16f, lineY + 4f)
            }
            pageManager.currentY += 10f
        }

        // 3. Work Experience & Incident Response History
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawCyberHeader(pageManager, "// 03. INCIDENT_RESPONSE_&_WORK_HISTORY", primaryColorInt, width)

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
                    color = Color.parseColor("#0D9488") // Teal Date
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

        // 4. Education & Academic Credentials
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawCyberHeader(pageManager, "// 04. ACADEMIC_CREDENTIALS", primaryColorInt, width)

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
                    color = Color.parseColor("#0D9488")
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

        // 5. Security Projects & Infrastructure
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawCyberHeader(pageManager, "// 05. SECURITY_PROJECTS_&_INFRASTRUCTURE", primaryColorInt, width)

            for (proj in data.projects) {
                pageManager.ensureSpace(35f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText("security audit: ${proj.projectName}", 32f, pageManager.currentY, projNamePaint)
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

        // 6. Certifications & Clearances
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            drawCyberHeader(pageManager, "// 06. CERTIFICATIONS_&_CLEARANCES", primaryColorInt, width)
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

        // 7. Languages & References
        if (data.languages.isNotEmpty()) {
            pageManager.ensureSpace(30f)
            drawCyberHeader(pageManager, "// 07. LANGUAGES", primaryColorInt, width)
            val langPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val langText = data.languages.joinToString("   |   ") { "${it.languageName} (${it.proficiency})" }
            pageManager.canvas.drawText(langText, 32f, pageManager.currentY, langPaint)
            pageManager.currentY += 18f
        }

        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            drawCyberHeader(pageManager, "// 08. PROFESSIONAL_REFERENCES", primaryColorInt, width)
            val refPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            for (ref in data.references) {
                pageManager.ensureSpace(14f)
                val contactDetail = listOfNotNull(ref.company.ifBlank { null }, ref.email.ifBlank { null }, ref.phone.ifBlank { null }).joinToString(" • ")
                val refStr = "• ${ref.name} — ${ref.title} (${contactDetail})"
                val lines = PdfDrawUtils.wrapText(refStr, refPaint, width - 64f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 32f, pageManager.currentY, refPaint)
                    pageManager.currentY += 14f
                }
            }
        }

        pageManager.finish()
    }

    private fun drawCyberHeader(pageManager: PdfPageManager, title: String, colorInt: Int, width: Float) {
        val headPaint = Paint().apply {
            color = Color.parseColor("#0D9488")
            textSize = 10.5f
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
