package com.mudasir.nexacvai.core.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Standard A4 PDF Document Generation Engine.
 * Generates true 595 x 842 pt A4 vector PDF documents using native Android [PdfDocument] & [Canvas].
 */
@Singleton
class PdfGeneratorEngine @Inject constructor(
    private val context: Context
) {
    companion object {
        const val A4_WIDTH_PT = 595
        const val A4_HEIGHT_PT = 842
    }

    suspend fun generateCvPdf(
        template: ResumeTemplate,
        data: TemplateData,
        templateStyle: TemplateStyle,
        outputFileName: String = "generated_cv_preview.pdf"
    ): File = withContext(Dispatchers.IO) {
        val outputFile = File(context.cacheDir, outputFileName)
        val pdfDocument = PdfDocument()

        try {
            val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH_PT, A4_HEIGHT_PT, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            renderTemplateToCanvas(canvas, template, data, templateStyle)

            pdfDocument.finishPage(page)

            FileOutputStream(outputFile).use { out ->
                pdfDocument.writeTo(out)
            }
        } finally {
            pdfDocument.close()
        }

        outputFile
    }

    private fun renderTemplateToCanvas(
        canvas: Canvas,
        template: ResumeTemplate,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val width = A4_WIDTH_PT.toFloat()
        val height = A4_HEIGHT_PT.toFloat()

        // Background White Paper Sheet
        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width, height, bgPaint)

        val primaryColorInt = templateStyle.primaryColor.toArgb()
        val accentColorInt = templateStyle.accentColor.toArgb()

        var currentY = 0f

        // Top Accent Header Band
        val headerHeight = if (templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()) 120f else 100f
        val headerPaint = Paint().apply {
            color = primaryColorInt
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width, headerHeight, headerPaint)

        // Candidate Full Name
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(data.fullName.ifBlank { "Alex Mercer" }, 28f, 40f, namePaint)

        // Candidate Title
        val titlePaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText(data.professionalTitle.ifBlank { "Senior Software Architect" }, 28f, 60f, titlePaint)

        // Contact Info Line
        val contactPaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            textSize = 10f
            isAntiAlias = true
        }
        val contactText = "${data.email}  •  ${data.phone}  •  ${data.location}"
        canvas.drawText(contactText, 28f, 80f, contactPaint)

        // Draw Profile Photo Avatar if enabled
        if (templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()) {
            try {
                val avatarBitmap = loadBitmapFromUri(data.profilePictureUri)
                if (avatarBitmap != null) {
                    val avatarRadius = 32f
                    val avatarX = width - 60f
                    val avatarY = 50f

                    val borderPaint = Paint().apply {
                        color = Color.WHITE
                        style = Paint.Style.STROKE
                        strokeWidth = 3f
                        isAntiAlias = true
                    }
                    canvas.drawCircle(avatarX, avatarY, avatarRadius, borderPaint)

                    val scaledAvatar = Bitmap.createScaledBitmap(avatarBitmap, 64, 64, true)
                    val shader = BitmapShader(scaledAvatar, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    val avatarPaint = Paint().apply {
                        isAntiAlias = true
                        this.shader = shader
                    }
                    val matrix = Matrix()
                    matrix.setTranslate(avatarX - 32f, avatarY - 32f)
                    shader.setLocalMatrix(matrix)
                    canvas.drawCircle(avatarX, avatarY, avatarRadius - 1.5f, avatarPaint)
                }
            } catch (e: Exception) {
                // Ignore avatar decoding errors gracefully
            }
        }

        currentY = headerHeight + 24f

        // Section Title Helper
        fun drawSectionHeader(title: String, y: Float): Float {
            val textPaint = Paint().apply {
                color = primaryColorInt
                textSize = 13f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            canvas.drawText(title.uppercase(), 28f, y, textPaint)

            val linePaint = Paint().apply {
                color = accentColorInt
                strokeWidth = 1.5f
                isAntiAlias = true
            }
            canvas.drawLine(28f, y + 6f, width - 28f, y + 6f, linePaint)
            return y + 20f
        }

        // Summary Section
        if (data.summary.isNotBlank()) {
            currentY = drawSectionHeader("Professional Summary", currentY)

            val summaryPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 10f
                isAntiAlias = true
            }

            val words = data.summary.split(" ")
            var line = ""
            val maxWidth = width - 56f

            for (word in words) {
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (summaryPaint.measureText(testLine) > maxWidth) {
                    canvas.drawText(line, 28f, currentY, summaryPaint)
                    currentY += 14f
                    line = word
                } else {
                    line = testLine
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, 28f, currentY, summaryPaint)
                currentY += 20f
            }
        }

        // Work Experience Section
        if (data.experiences.isNotEmpty()) {
            currentY = drawSectionHeader("Work Experience", currentY)

            for (exp in data.experiences) {
                val expTitlePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                canvas.drawText(exp.jobTitle, 28f, currentY, expTitlePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#64748B")
                    textSize = 9f
                    isAntiAlias = true
                }
                val dateText = "${exp.company}  |  ${exp.startDate} - ${exp.endDate}"
                canvas.drawText(dateText, 28f, currentY + 12f, datePaint)

                currentY += 26f

                val respPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                for (resp in exp.responsibilities) {
                    canvas.drawText("•  $resp", 38f, currentY, respPaint)
                    currentY += 13f
                }
                currentY += 8f
            }
        }

        // Education Section
        if (data.educations.isNotEmpty()) {
            currentY = drawSectionHeader("Education & Credentials", currentY)

            for (edu in data.educations) {
                val eduPaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                canvas.drawText(edu.degree, 28f, currentY, eduPaint)

                val instPaint = Paint().apply {
                    color = Color.parseColor("#64748B")
                    textSize = 9f
                    isAntiAlias = true
                }
                canvas.drawText("${edu.institution}  |  ${edu.startDate} - ${edu.endDate}", 28f, currentY + 12f, instPaint)

                currentY += 24f
            }
        }

        // Skills Section
        if (data.skills.isNotEmpty()) {
            currentY = drawSectionHeader("Skills & Competencies", currentY)

            val skillBgPaint = Paint().apply {
                color = Color.parseColor("#F1F5F9")
                style = Paint.Style.FILL
            }
            val skillTextPaint = Paint().apply {
                color = primaryColorInt
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            var startX = 28f
            for (skill in data.skills) {
                val textWidth = skillTextPaint.measureText(skill)
                val chipWidth = textWidth + 14f

                if (startX + chipWidth > width - 28f) {
                    startX = 28f
                    currentY += 20f
                }

                val chipRect = RectF(startX, currentY - 10f, startX + chipWidth, currentY + 6f)
                canvas.drawRoundRect(chipRect, 4f, 4f, skillBgPaint)
                canvas.drawText(skill, startX + 7f, currentY + 2f, skillTextPaint)

                startX += chipWidth + 8f
            }
            currentY += 24f
        }

        // Footer Page Mark
        val footerPaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 8f
            isAntiAlias = true
        }
        canvas.drawText("Generated by NexaCV AI  •  Official A4 Document", 28f, height - 16f, footerPaint)
    }

    private fun loadBitmapFromUri(uriString: String): Bitmap? {
        return try {
            val uri = Uri.parse(uriString)
            if (uriString.startsWith("android.resource://")) {
                val resName = uri.lastPathSegment ?: return null
                val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
                if (resId != 0) {
                    BitmapFactory.decodeResource(context.resources, resId)
                } else {
                    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
                }
            } else if (uri.scheme == "content" || uri.scheme == "file") {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            } else {
                val file = File(uriString)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)
                } else {
                    val resId = context.resources.getIdentifier(uriString, "drawable", context.packageName)
                    if (resId != 0) BitmapFactory.decodeResource(context.resources, resId) else null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
