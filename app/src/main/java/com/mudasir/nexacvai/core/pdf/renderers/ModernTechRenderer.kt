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
 * 🎨 Modern Tech Sidebar Renderer
 * Split 30/70 sidebar layout with distinct sidebar background panel, avatar photo frame,
 * skill pill badges, contact info, languages, hobbies, and structured main column.
 */
class ModernTechRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val height = pageManager.heightPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        val sidebarWidth = 180f
        val mainLeftX = sidebarWidth + 24f
        val mainWidth = width - mainLeftX - 24f

        // Draw Left Sidebar Background Tint
        val sidebarBgPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            style = Paint.Style.FILL
        }
        pageManager.canvas.drawRect(0f, 0f, sidebarWidth, height, sidebarBgPaint)

        // Sidebar Divider Line
        val sidebarBorderPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        pageManager.canvas.drawLine(sidebarWidth, 0f, sidebarWidth, height, sidebarBorderPaint)

        // --- SIDEBAR CONTENT (Left 180pt) ---
        var sidebarY = 36f

        // Profile Photo Avatar
        if (templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()) {
            try {
                PdfDrawUtils.drawStyledProfilePhoto(
                    context = context,
                    canvas = pageManager.canvas,
                    sourceUriString = data.profilePictureUri,
                    rightX = sidebarWidth / 2f + 32f,
                    centerY = sidebarY + 32f,
                    shape = templateStyle.photoShape
                )
                sidebarY += 80f
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 1. Contact Info in Sidebar
        val sideHeaderPaint = Paint().apply {
            color = primaryColorInt
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText("CONTACT", 20f, sidebarY, sideHeaderPaint)
        sidebarY += 14f

        val sideTextPaint = Paint().apply {
            color = Color.parseColor("#475569")
            textSize = 9f
            isAntiAlias = true
        }
        if (data.email.isNotBlank()) {
            val lines = PdfDrawUtils.wrapText(data.email, sideTextPaint, sidebarWidth - 36f)
            for (l in lines) {
                pageManager.canvas.drawText(l, 20f, sidebarY, sideTextPaint)
                sidebarY += 12f
            }
        }
        if (data.phone.isNotBlank()) {
            pageManager.canvas.drawText(data.phone, 20f, sidebarY, sideTextPaint)
            sidebarY += 12f
        }
        if (data.location.isNotBlank()) {
            val lines = PdfDrawUtils.wrapText(data.location, sideTextPaint, sidebarWidth - 36f)
            for (l in lines) {
                pageManager.canvas.drawText(l, 20f, sidebarY, sideTextPaint)
                sidebarY += 12f
            }
        }
        sidebarY += 16f

        // 2. Social Links in Sidebar
        if (data.socialLinks.isNotEmpty()) {
            pageManager.canvas.drawText("LINKS", 20f, sidebarY, sideHeaderPaint)
            sidebarY += 14f
            for (link in data.socialLinks) {
                val text = "${link.platform}: ${link.url}"
                val lines = PdfDrawUtils.wrapText(text, sideTextPaint, sidebarWidth - 36f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 20f, sidebarY, sideTextPaint)
                    sidebarY += 12f
                }
            }
            sidebarY += 16f
        }

        // 3. Skills Pill Badges in Sidebar
        if (data.skills.isNotEmpty()) {
            pageManager.canvas.drawText("SKILLS", 20f, sidebarY, sideHeaderPaint)
            sidebarY += 16f

            val pillBgPaint = Paint().apply {
                color = primaryColorInt
                style = Paint.Style.FILL
            }
            val pillTextPaint = Paint().apply {
                color = Color.WHITE
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            var currPillX = 20f
            for (skill in data.skills) {
                val textW = pillTextPaint.measureText(skill)
                val pillW = textW + 12f

                if (currPillX + pillW > sidebarWidth - 16f) {
                    currPillX = 20f
                    sidebarY += 20f
                }

                val rect = RectF(currPillX, sidebarY - 10f, currPillX + pillW, sidebarY + 6f)
                pageManager.canvas.drawRoundRect(rect, 4f, 4f, pillBgPaint)
                pageManager.canvas.drawText(skill, currPillX + 6f, sidebarY + 2f, pillTextPaint)

                currPillX += pillW + 6f
            }
            sidebarY += 24f
        }

        // 4. Languages in Sidebar
        if (data.languages.isNotEmpty()) {
            pageManager.canvas.drawText("LANGUAGES", 20f, sidebarY, sideHeaderPaint)
            sidebarY += 14f
            for (lang in data.languages) {
                pageManager.canvas.drawText("• ${lang.languageName} (${lang.proficiency})", 20f, sidebarY, sideTextPaint)
                sidebarY += 14f
            }
            sidebarY += 14f
        }

        // 5. Hobbies in Sidebar
        if (data.hobbies.isNotEmpty()) {
            pageManager.canvas.drawText("HOBBIES", 20f, sidebarY, sideHeaderPaint)
            sidebarY += 14f
            for (hobby in data.hobbies) {
                val lines = PdfDrawUtils.wrapText("• $hobby", sideTextPaint, sidebarWidth - 36f)
                for (l in lines) {
                    pageManager.canvas.drawText(l, 20f, sidebarY, sideTextPaint)
                    sidebarY += 12f
                }
            }
        }

        // --- MAIN CONTENT COLUMN (Right side) ---
        pageManager.currentY = 36f

        // Header Name & Title
        val namePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Candidate Name" }, mainLeftX, pageManager.currentY, namePaint)
        pageManager.currentY += 18f

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = primaryColorInt
                textSize = 11.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.professionalTitle, mainLeftX, pageManager.currentY, titlePaint)
            pageManager.currentY += 20f
        }

        // Professional Summary
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("SUMMARY", "SUMMARY"), primaryColorInt, mainLeftX, mainLeftX + mainWidth)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9.5f
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, mainWidth)
            for (l in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(l, mainLeftX, pageManager.currentY, summaryPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 14f
        }

        // Work Experience
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("EXPERIENCE", "WORK EXPERIENCE"), primaryColorInt, mainLeftX, mainLeftX + mainWidth)

            for (exp in data.experiences) {
                pageManager.ensureSpace(45f)
                val jobTitlePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.jobTitle, mainLeftX, pageManager.currentY, jobTitlePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#64748B")
                    textSize = 9f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateStr = "${exp.startDate} - ${exp.endDate}"
                val dateW = datePaint.measureText(dateStr)
                pageManager.canvas.drawText(dateStr, mainLeftX + mainWidth - dateW, pageManager.currentY, datePaint)
                pageManager.currentY += 14f

                val companyPaint = Paint().apply {
                    color = primaryColorInt
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.company, mainLeftX, pageManager.currentY, companyPaint)
                pageManager.currentY += 14f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9f
                    isAntiAlias = true
                }
                for (resp in exp.responsibilities) {
                    val respLines = PdfDrawUtils.wrapText("• $resp", descPaint, mainWidth)
                    for (l in respLines) {
                        pageManager.ensureSpace(13f)
                        pageManager.canvas.drawText(l, mainLeftX, pageManager.currentY, descPaint)
                        pageManager.currentY += 13f
                    }
                }
                pageManager.currentY += 10f
            }
            pageManager.currentY += 6f
        }

        // Education
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("EDUCATION", "EDUCATION"), primaryColorInt, mainLeftX, mainLeftX + mainWidth)

            for (edu in data.educations) {
                pageManager.ensureSpace(30f)
                val degreePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.degree, mainLeftX, pageManager.currentY, degreePaint)
                pageManager.currentY += 14f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#64748B")
                    textSize = 9f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText("${edu.institution}  (${edu.startDate} - ${edu.endDate})", mainLeftX, pageManager.currentY, instPaint)
                pageManager.currentY += 16f
            }
        }

        // Key Projects
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("PROJECTS", "KEY PROJECTS"), primaryColorInt, mainLeftX, mainLeftX + mainWidth)

            for (proj in data.projects) {
                pageManager.ensureSpace(35f)
                val projNamePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(proj.projectName, mainLeftX, pageManager.currentY, projNamePaint)
                pageManager.currentY += 14f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 9f
                    isAntiAlias = true
                }
                val descLines = PdfDrawUtils.wrapText(proj.description, descPaint, mainWidth)
                for (l in descLines) {
                    pageManager.ensureSpace(13f)
                    pageManager.canvas.drawText(l, mainLeftX, pageManager.currentY, descPaint)
                    pageManager.currentY += 13f
                }
                pageManager.currentY += 8f
            }
        }

        // Certifications & Credentials
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("CERTIFICATIONS", "CERTIFICATIONS & LICENSES"), primaryColorInt, mainLeftX, mainLeftX + mainWidth)
            val certPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9f
                isAntiAlias = true
            }
            for (cert in data.certifications) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText("• ${cert.name} — ${cert.issuer} (${cert.date})", mainLeftX, pageManager.currentY, certPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 10f
        }

        // References
        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(35f)
            PdfDrawUtils.drawSectionHeader(pageManager, data.getSectionTitle("REFERENCES", "REFERENCES"), primaryColorInt, mainLeftX, mainLeftX + mainWidth)
            val refPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 9f
                isAntiAlias = true
            }
            val refText = data.references.joinToString("   |   ") { "${it.name} (${it.title}, ${it.company})" }
            val lines = PdfDrawUtils.wrapText(refText, refPaint, mainWidth)
            for (l in lines) {
                pageManager.ensureSpace(13f)
                pageManager.canvas.drawText(l, mainLeftX, pageManager.currentY, refPaint)
                pageManager.currentY += 13f
            }
        }

        pageManager.finish()
    }
}
