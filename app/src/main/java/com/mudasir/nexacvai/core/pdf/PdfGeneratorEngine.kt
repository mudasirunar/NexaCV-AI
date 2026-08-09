package com.mudasir.nexacvai.core.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate
import com.mudasir.nexacvai.domain.model.template.TemplateData
import com.mudasir.nexacvai.domain.model.template.TemplateProjectData
import com.mudasir.nexacvai.domain.model.template.TemplateReferenceData
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
 * Features dynamic MS Word-style multi-page flow using [PdfPageManager].
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
            renderTemplateDynamicMultiPage(pdfDocument, template, data, templateStyle)

            FileOutputStream(outputFile).use { out ->
                pdfDocument.writeTo(out)
            }
        } finally {
            pdfDocument.close()
        }

        outputFile
    }

    /**
     * Dynamic MS Word-style multi-page canvas manager.
     * Automatically monitors currentY position and creates new A4 pages on demand.
     */
    private class PdfPageManager(
        private val pdfDocument: PdfDocument,
        val widthPt: Int = A4_WIDTH_PT,
        val heightPt: Int = A4_HEIGHT_PT,
        val topMargin: Float = 36f,
        val bottomMargin: Float = 36f
    ) {
        var pageNumber = 0
            private set
        private var currentPage: PdfDocument.Page? = null
        lateinit var canvas: Canvas
            private set

        var currentY: Float = 0f

        val maxContentY: Float
            get() = heightPt - bottomMargin

        init {
            startNewPage()
        }

        fun startNewPage() {
            currentPage?.let { pdfDocument.finishPage(it) }
            pageNumber++
            val pageInfo = PdfDocument.PageInfo.Builder(widthPt, heightPt, pageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            currentPage = page
            canvas = page.canvas

            // Draw White A4 paper background
            val bgPaint = Paint().apply {
                color = Color.WHITE
                style = Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, widthPt.toFloat(), heightPt.toFloat(), bgPaint)

            currentY = if (pageNumber == 1) 0f else topMargin
        }

        fun ensureSpace(neededHeight: Float) {
            if (currentY + neededHeight > maxContentY) {
                startNewPage()
            }
        }

        fun finish() {
            currentPage?.let { pdfDocument.finishPage(it) }
            currentPage = null
        }
    }

    private fun renderTemplateDynamicMultiPage(
        pdfDocument: PdfDocument,
        template: ResumeTemplate,
        data: TemplateData,
        templateStyle: TemplateStyle
    ) {
        val pageManager = PdfPageManager(pdfDocument)
        val width = A4_WIDTH_PT.toFloat()
        val primaryColorInt = templateStyle.primaryColor.toArgb()

        // 1. Top Header (Page 1)
        val headerHeight = if (templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()) 120f else 100f
        val headerPaint = Paint().apply {
            color = primaryColorInt
            style = Paint.Style.FILL
        }
        pageManager.canvas.drawRect(0f, 0f, width, headerHeight, headerPaint)

        // Name & Professional Title
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.fullName.ifBlank { "Alex Mercer" }, 28f, 40f, namePaint)

        val titlePaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(data.professionalTitle.ifBlank { "Senior Software Architect" }, 28f, 60f, titlePaint)

        val contactPaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            textSize = 10f
            isAntiAlias = true
        }
        val contactText = "${data.email}  •  ${data.phone}  •  ${data.location}"
        pageManager.canvas.drawText(contactText, 28f, 80f, contactPaint)

        // Draw Profile Photo Avatar if enabled
        if (templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()) {
            try {
                val avatarBitmap = loadBitmapFromUri(data.profilePictureUri)
                if (avatarBitmap != null) {
                    val avatarRadius = 32f
                    val avatarX = width - 28f - avatarRadius
                    val avatarY = headerHeight / 2f
                    drawCircularBitmap(pageManager.canvas, avatarBitmap, avatarX, avatarY, avatarRadius)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        pageManager.currentY = headerHeight + 24f

        // 2. Summary Section
        if (data.summary.isNotBlank()) {
            pageManager.ensureSpace(60f)
            drawSectionHeader(pageManager, "PROFESSIONAL SUMMARY", primaryColorInt)
            val summaryPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 10.5f
                isAntiAlias = true
            }
            val lines = wrapText(data.summary, summaryPaint, width - 56f)
            for (line in lines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(line, 28f, pageManager.currentY, summaryPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 16f
        }

        // 3. Work Experience Section
        if (data.experiences.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSectionHeader(pageManager, "WORK EXPERIENCE", primaryColorInt)

            for (exp in data.experiences) {
                pageManager.ensureSpace(50f)

                val jobTitlePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 11.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(exp.jobTitle, 28f, pageManager.currentY, jobTitlePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#64748B")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val dateText = "${exp.startDate} - ${exp.endDate}"
                val dateWidth = datePaint.measureText(dateText)
                pageManager.canvas.drawText(dateText, width - 28f - dateWidth, pageManager.currentY, datePaint)

                pageManager.currentY += 16f

                val companyPaint = Paint().apply {
                    color = primaryColorInt
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val companyLoc = if (exp.location.isNotBlank()) "${exp.company}  •  ${exp.location}" else exp.company
                pageManager.canvas.drawText(companyLoc, 28f, pageManager.currentY, companyPaint)
                pageManager.currentY += 16f

                val bulletPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 10f
                    isAntiAlias = true
                }
                val dotPaint = Paint().apply {
                    color = primaryColorInt
                    isAntiAlias = true
                }

                for (resp in exp.responsibilities) {
                    val respLines = wrapText(resp, bulletPaint, width - 70f)
                    for ((lineIdx, line) in respLines.withIndex()) {
                        pageManager.ensureSpace(14f)
                        if (lineIdx == 0) {
                            pageManager.canvas.drawCircle(36f, pageManager.currentY - 3f, 2.5f, dotPaint)
                        }
                        pageManager.canvas.drawText(line, 46f, pageManager.currentY, bulletPaint)
                        pageManager.currentY += 14f
                    }
                }
                pageManager.currentY += 12f
            }
            pageManager.currentY += 8f
        }

        // 4. Education Section
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSectionHeader(pageManager, "EDUCATION & CREDENTIALS", primaryColorInt)

            for (edu in data.educations) {
                pageManager.ensureSpace(40f)

                val degreePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.degree, 28f, pageManager.currentY, degreePaint)

                val datePaint = Paint().apply {
                    color = Color.parseColor("#64748B")
                    textSize = 10f
                    isAntiAlias = true
                }
                val dateText = "${edu.startDate} - ${edu.endDate}"
                val dateWidth = datePaint.measureText(dateText)
                pageManager.canvas.drawText(dateText, width - 28f - dateWidth, pageManager.currentY, datePaint)

                pageManager.currentY += 16f

                val instPaint = Paint().apply {
                    color = Color.parseColor("#475569")
                    textSize = 10f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(edu.institution, 28f, pageManager.currentY, instPaint)
                pageManager.currentY += 20f
            }
            pageManager.currentY += 8f
        }

        // 5. Skills Section
        if (data.skills.isNotEmpty()) {
            pageManager.ensureSpace(50f)
            drawSectionHeader(pageManager, "TECHNICAL SKILLS", primaryColorInt)

            val skillTextPaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val skillBgPaint = Paint().apply {
                color = Color.parseColor("#F1F5F9")
                style = Paint.Style.FILL
            }

            var startX = 28f
            for (skill in data.skills) {
                val textWidth = skillTextPaint.measureText(skill)
                val chipWidth = textWidth + 14f

                if (startX + chipWidth > width - 28f) {
                    startX = 28f
                    pageManager.currentY += 20f
                    pageManager.ensureSpace(20f)
                }

                val chipRect = RectF(startX, pageManager.currentY - 10f, startX + chipWidth, pageManager.currentY + 6f)
                pageManager.canvas.drawRoundRect(chipRect, 4f, 4f, skillBgPaint)
                pageManager.canvas.drawText(skill, startX + 7f, pageManager.currentY + 2f, skillTextPaint)

                startX += chipWidth + 8f
            }
            pageManager.currentY += 24f
        }

        // 6. Projects & Portfolio Section (Flows dynamically onto Page 2 if needed)
        val projects = if (data.projects.isNotEmpty()) data.projects else listOf(
            TemplateProjectData("NexaCV AI Engine", "Lead Developer", "2024", "Present", "Offline-first resume engine with pluggable AI providers and real-time A4 PDF preview."),
            TemplateProjectData("Cloud Enterprise Portal", "Architect", "2023", "2024", "High-throughput cloud portal processing over 10M daily transactions.")
        )

        pageManager.ensureSpace(40f)
        drawSectionHeader(pageManager, "PROJECTS & PORTFOLIO", primaryColorInt)

        val projTitlePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val projDescPaint = Paint().apply {
            color = Color.parseColor("#475569")
            textSize = 10f
            isAntiAlias = true
        }

        for (proj in projects) {
            pageManager.ensureSpace(40f)
            pageManager.canvas.drawText(proj.projectName, 28f, pageManager.currentY, projTitlePaint)
            val dateText = "${proj.startDate} - ${proj.endDate}"
            val dateWidth = projDescPaint.measureText(dateText)
            pageManager.canvas.drawText(dateText, width - 28f - dateWidth, pageManager.currentY, projDescPaint)

            pageManager.currentY += 16f
            val descLines = wrapText("${proj.roleInProject}: ${proj.description}", projDescPaint, width - 56f)
            for (line in descLines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(line, 28f, pageManager.currentY, projDescPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 16f
        }

        // 7. References Section
        val references = if (data.references.isNotEmpty()) data.references else listOf(
            TemplateReferenceData("Sarah Jenkins", "VP of Engineering", "Apex Financial Technologies", "sarah.jenkins@apexfin.com"),
            TemplateReferenceData("Michael Chang", "Principal Systems Architect", "Nexus Cloud", "m.chang@nexuscloud.io")
        )

        pageManager.ensureSpace(40f)
        drawSectionHeader(pageManager, "PROFESSIONAL REFERENCES", primaryColorInt)

        val refTitlePaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val refDescPaint = Paint().apply {
            color = Color.parseColor("#475569")
            textSize = 10f
            isAntiAlias = true
        }

        for (ref in references) {
            pageManager.ensureSpace(40f)
            pageManager.canvas.drawText(ref.name, 28f, pageManager.currentY, refTitlePaint)
            pageManager.currentY += 16f
            pageManager.canvas.drawText("${ref.title}  •  ${ref.company}", 28f, pageManager.currentY, refDescPaint)
            pageManager.currentY += 14f
            pageManager.canvas.drawText("Contact: ${ref.contactInfo}", 28f, pageManager.currentY, refDescPaint)
        }

        pageManager.finish()
    }

    private fun drawSectionHeader(pageManager: PdfPageManager, title: String, colorInt: Int) {
        pageManager.ensureSpace(30f)
        val titlePaint = Paint().apply {
            color = colorInt
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(title, 28f, pageManager.currentY, titlePaint)
        pageManager.currentY += 6f

        val linePaint = Paint().apply {
            color = colorInt
            strokeWidth = 1.5f
        }
        pageManager.canvas.drawLine(28f, pageManager.currentY, A4_WIDTH_PT - 28f, pageManager.currentY, linePaint)
        pageManager.currentY += 16f
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "${currentLine} $word"
            val textWidth = paint.measureText(testLine)

            if (textWidth <= maxWidth) {
                currentLine.append(if (currentLine.isEmpty()) word else " $word")
            } else {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString())
                }
                currentLine = StringBuilder(word)
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }
        return lines
    }

    private fun drawCircularBitmap(canvas: Canvas, bitmap: Bitmap, centerX: Float, centerY: Float, radius: Float) {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, (radius * 2).toInt(), (radius * 2).toInt(), true)
        val output = Bitmap.createBitmap(scaledBitmap.width, scaledBitmap.height, Bitmap.Config.ARGB_8888)
        val outputCanvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.RED
        }

        outputCanvas.drawARGB(0, 0, 0, 0)
        outputCanvas.drawCircle(radius, radius, radius, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        outputCanvas.drawBitmap(scaledBitmap, 0f, 0f, paint)

        canvas.drawBitmap(output, centerX - radius, centerY - radius, null)

        val borderPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawCircle(centerX, centerY, radius, borderPaint)
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
