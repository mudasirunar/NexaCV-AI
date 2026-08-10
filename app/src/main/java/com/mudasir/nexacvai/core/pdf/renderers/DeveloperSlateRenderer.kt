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
 * Dark slate top header banner with Monospace Terminal prompts (class Developer {, @company),
 * Smart-positioned GitHub badge (no photo overlap), borderless technical skills matrix table,
 * git repo URLs, inline tech stack tags ([Kotlin, Compose, Room, Hilt]), and monospace code headers.
 */
class DeveloperSlateRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        val hasPhoto = templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()

        // Dark Slate Top Banner
        val bannerHeight = 110f
        val bannerPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            style = Paint.Style.FILL
        }
        pageManager.canvas.drawRect(0f, 0f, width, bannerHeight, bannerPaint)

        // Monospace Terminal Accent Line
        val termPaint = Paint().apply {
            color = Color.parseColor("#38BDF8")
            textSize = 10f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }
        pageManager.canvas.drawText("class Developer {", 32f, 26f, termPaint)

        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Developer Candidate" }, 32f, 48f, namePaint)

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#94A3B8")
                textSize = 11f
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }
            pageManager.canvas.drawText("// ${data.professionalTitle}", 32f, 66f, titlePaint)
        }

        val contactPaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            textSize = 8.5f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }
        val contactStr = listOfNotNull(data.email.ifBlank { null }, data.phone.ifBlank { null }, data.location.ifBlank { null }).joinToString(" | ")
        pageManager.canvas.drawText(contactStr, 32f, 82f, contactPaint)

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

        // GitHub / Developer Badge Box - Render ONLY if social link / badge text is present (disappears cleanly if blank)
        val githubLink = data.socialLinks.firstOrNull { it.platform.equals("GitHub", ignoreCase = true) }?.url
            ?: data.socialLinks.firstOrNull()?.url
        val devBadgeRaw = if (!githubLink.isNullOrBlank()) githubLink.replace("https://", "").replace("github.com/", "@") else ""
        
        if (devBadgeRaw.isNotBlank()) {
            val devBadgeText = "[ GITHUB: $devBadgeRaw ]"
            val photoSpaceOffset = if (hasPhoto) 75f else 0f
            val badgePaint = Paint().apply {
                color = Color.parseColor("#1E293B")
                style = Paint.Style.FILL
            }
            val badgeBorderPaint = Paint().apply {
                color = Color.parseColor("#38BDF8")
                strokeWidth = 1f
                style = Paint.Style.STROKE
                isAntiAlias = true
            }
            val badgeTextPaint = Paint().apply {
                color = Color.parseColor("#7DD3FC")
                textSize = 8f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                isAntiAlias = true
            }
            val devBadgeW = badgeTextPaint.measureText(devBadgeText) + 14f
            val badgeRightX = width - 32f - photoSpaceOffset
            val devBadgeRect = RectF(badgeRightX - devBadgeW, 16f, badgeRightX, 36f)
            pageManager.canvas.drawRoundRect(devBadgeRect, 4f, 4f, badgePaint)
            pageManager.canvas.drawRoundRect(devBadgeRect, 4f, 4f, badgeBorderPaint)
            pageManager.canvas.drawText(devBadgeText, badgeRightX - devBadgeW + 7f, 29f, badgeTextPaint)
        }

        pageManager.currentY = bannerHeight + 24f

        // 1. Monospace Overview
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            drawDevSectionHeader(pageManager, "// " + data.getSectionTitle("SUMMARY", "OVERVIEW"), primaryColorInt, width)
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

        // 2. Categorized Technical Skills Matrix Table
        val categoriesToRender = if (data.skillCategoryGroups.isNotEmpty()) {
            data.skillCategoryGroups
        } else if (data.skills.isNotEmpty()) {
            listOf(com.mudasir.nexacvai.domain.model.template.TemplateSkillCategoryGroup("Core Competencies", data.skills))
        } else emptyList()

        if (categoriesToRender.isNotEmpty()) {
            pageManager.ensureSpace(50f)
            drawDevSectionHeader(pageManager, "// " + data.getSectionTitle("SKILLS", "TECHNICAL_SKILLS_MATRIX"), primaryColorInt, width)

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

            val labelColumnW = 145f
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

        // 3. Work Experience & Tech Stack Highlights
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawDevSectionHeader(pageManager, "// " + data.getSectionTitle("EXPERIENCE", "WORK_EXPERIENCE"), primaryColorInt, width)

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

                if (exp.technologies.isNotEmpty()) {
                    val techTagStr = "stack: " + exp.technologies.joinToString(", ")
                    val techPaint = Paint().apply {
                        color = Color.parseColor("#0284C7")
                        textSize = 9f
                        typeface = Typeface.MONOSPACE
                        isAntiAlias = true
                    }
                    pageManager.canvas.drawText(techTagStr, 32f, pageManager.currentY, techPaint)
                    pageManager.currentY += 14f
                }
                pageManager.currentY += 10f
            }
        }

        // 4. Education
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawDevSectionHeader(pageManager, "// " + data.getSectionTitle("EDUCATION", "EDUCATION"), primaryColorInt, width)

            for (edu in data.educations) {
                pageManager.ensureSpace(30f)
                val degreePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val degreeText = if (edu.gradeOrGpa.isNotBlank()) "${edu.degree}  (GPA: ${edu.gradeOrGpa})" else edu.degree
                pageManager.canvas.drawText(degreeText, 32f, pageManager.currentY, degreePaint)

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

        // 5. Featured Projects (With Git Repo Links & Tech Tags)
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawDevSectionHeader(pageManager, "// " + data.getSectionTitle("PROJECTS", "FEATURED_PROJECTS"), primaryColorInt, width)

            for (proj in data.projects) {
                pageManager.ensureSpace(35f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val repoTitle = "git repo: ${proj.projectName}"
                pageManager.canvas.drawText(repoTitle, 32f, pageManager.currentY, projNamePaint)

                if (proj.projectLink.isNotBlank()) {
                    val linkPaint = Paint().apply {
                        color = Color.parseColor("#0284C7")
                        textSize = 9f
                        typeface = Typeface.MONOSPACE
                        isAntiAlias = true
                    }
                    val linkW = linkPaint.measureText(proj.projectLink)
                    pageManager.canvas.drawText(proj.projectLink, width - 32f - linkW, pageManager.currentY, linkPaint)
                }
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

                if (proj.technologiesUsed.isNotEmpty()) {
                    val techTagStr = "tags: [" + proj.technologiesUsed.joinToString(", ") + "]"
                    val techPaint = Paint().apply {
                        color = Color.parseColor("#0284C7")
                        textSize = 9f
                        typeface = Typeface.MONOSPACE
                        isAntiAlias = true
                    }
                    pageManager.canvas.drawText(techTagStr, 32f, pageManager.currentY, techPaint)
                    pageManager.currentY += 14f
                }
                pageManager.currentY += 8f
            }
        }

        // 6. Certifications
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            drawDevSectionHeader(pageManager, "// " + data.getSectionTitle("CERTIFICATIONS", "CERTIFICATIONS"), primaryColorInt, width)
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
            drawDevSectionHeader(pageManager, "// " + data.getSectionTitle("LANGUAGES", "LANGUAGES"), primaryColorInt, width)
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
            drawDevSectionHeader(pageManager, "// " + data.getSectionTitle("REFERENCES", "REFERENCES"), primaryColorInt, width)
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
