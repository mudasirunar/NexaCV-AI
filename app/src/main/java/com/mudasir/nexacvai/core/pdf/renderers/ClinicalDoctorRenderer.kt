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
 * 🏥 Clinical Doctor Renderer
 * Medical blue accent header, board credentials & clinical trials top highlight box,
 * clean clinical timeline structure.
 */
class ClinicalDoctorRenderer(private val context: Context) : PdfTemplateRenderer {

    override fun render(
        pageManager: PdfPageManager,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = pageManager.widthPt.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        // Top Medical Header Block
        val headerHeight = 105f
        val headerPaint = Paint().apply {
            color = Color.parseColor("#0284C7")
            style = Paint.Style.FILL
        }
        pageManager.canvas.drawRect(0f, 0f, width, headerHeight, headerPaint)

        // Medical Cross / Title Accent
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Dr. Medical Candidate, MD" }, 32f, 40f, namePaint)

        if (data.professionalTitle.isNotBlank()) {
            val titlePaint = Paint().apply {
                color = Color.parseColor("#E0F2FE")
                textSize = 11.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            pageManager.canvas.drawText(data.professionalTitle, 32f, 60f, titlePaint)
        }

        val contactPaint = Paint().apply {
            color = Color.parseColor("#BAE6FD")
            textSize = 9f
            isAntiAlias = true
        }
        val contactStr = listOfNotNull(data.email.ifBlank { null }, data.phone.ifBlank { null }, data.location.ifBlank { null }).joinToString("  •  ")
        pageManager.canvas.drawText(contactStr, 32f, 80f, contactPaint)

        // Top Right Credentials Highlight Box (e.g. Board Certified / M.D. Credentials)
        if (data.certifications.isNotEmpty()) {
            val credBgPaint = Paint().apply {
                color = Color.argb(46, 255, 255, 255)
                style = Paint.Style.FILL
            }
            val credRect = RectF(width - 210f, 18f, width - 24f, 88f)
            pageManager.canvas.drawRoundRect(credRect, 6f, 6f, credBgPaint)

            val credTextPaint = Paint().apply {
                color = Color.WHITE
                textSize = 8.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            pageManager.canvas.drawText("CLINICAL CREDENTIALS", width - 200f, 36f, credTextPaint)

            val credSubPaint = Paint().apply {
                color = Color.parseColor("#F0F9FF")
                textSize = 8f
                isAntiAlias = true
            }
            var credY = 52f
            for (cert in data.certifications.take(2)) {
                pageManager.canvas.drawText("• ${cert.name}", width - 200f, credY, credSubPaint)
                credY += 14f
            }
        }

        pageManager.currentY = headerHeight + 24f

        // Clinical Summary
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(50f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CLINICAL PROFILE & SPECIALTIES", primaryColorInt, 32f, width - 32f)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#1E293B")
                textSize = 9.5f
                isAntiAlias = true
            }
            val lines = PdfDrawUtils.wrapText(data.summary, summaryPaint, width - 64f)
            for (l in lines) {
                pageManager.ensureSpace(13f)
                pageManager.canvas.drawText(l, 32f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 13f
            }
            pageManager.currentY += 14f
        }

        // Clinical Experience
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            PdfDrawUtils.drawSectionHeader(pageManager, "CLINICAL APPOINTMENTS & EXPERIENCE", primaryColorInt, 32f, width - 32f)

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
                        pageManager.ensureSpace(13f)
                        pageManager.canvas.drawText(l, 32f, pageManager.currentY, descPaint)
                        pageManager.currentY += 13f
                    }
                }
                pageManager.currentY += 10f
            }
        }

        pageManager.finish()
    }
}
