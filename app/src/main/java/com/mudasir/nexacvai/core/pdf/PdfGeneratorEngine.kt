package com.mudasir.nexacvai.core.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import com.mudasir.nexacvai.domain.model.template.PhotoShape
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
 * Features high-resolution photo center-cropping and template shape rendering (Circle, Rounded Square, Passport).
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

        // Draw Profile Photo Avatar if enabled with high-DPI aspect-ratio preserving center crop
        if (templateStyle.showPhoto && !data.profilePictureUri.isNullOrBlank()) {
            try {
                val avatarBitmap = loadBitmapFromUri(data.profilePictureUri)
                if (avatarBitmap != null) {
                    val avatarRightX = width - 28f
                    val avatarCenterY = headerHeight / 2f
                    drawStyledProfilePhoto(
                        canvas = pageManager.canvas,
                        sourceBitmap = avatarBitmap,
                        rightX = avatarRightX,
                        centerY = avatarCenterY,
                        shape = templateStyle.photoShape
                    )
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
                pageManager.canvas.drawText(exp.company, 28f, pageManager.currentY, companyPaint)
                pageManager.currentY += 16f

                val descPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 10f
                    isAntiAlias = true
                }
                val expText = exp.responsibilities.joinToString(". ")
                val descLines = wrapText(expText, descPaint, width - 56f)
                for (line in descLines) {
                    pageManager.ensureSpace(14f)
                    pageManager.canvas.drawText(line, 28f, pageManager.currentY, descPaint)
                    pageManager.currentY += 14f
                }
                pageManager.currentY += 12f
            }
            pageManager.currentY += 8f
        }

        // 4. Education Section
        if (data.educations.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSectionHeader(pageManager, "EDUCATION", primaryColorInt)

            for (edu in data.educations) {
                pageManager.ensureSpace(35f)

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
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
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
                val instText = if (edu.gradeOrGpa.isNotBlank()) "${edu.institution}  •  Grade: ${edu.gradeOrGpa}" else edu.institution
                pageManager.canvas.drawText(instText, 28f, pageManager.currentY, instPaint)
                pageManager.currentY += 18f
            }
            pageManager.currentY += 8f
        }

        // 5. Technical Skills Section
        if (data.skills.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSectionHeader(pageManager, "TECHNICAL SKILLS", primaryColorInt)

            val skillPillBgPaint = Paint().apply {
                color = Color.parseColor("#F1F5F9")
                style = Paint.Style.FILL
            }
            val skillPillTextPaint = Paint().apply {
                color = primaryColorInt
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            var currentX = 28f
            pageManager.ensureSpace(24f)

            for (skill in data.skills) {
                val skillText = skill
                val textWidth = skillPillTextPaint.measureText(skillText)
                val pillWidth = textWidth + 16f

                if (currentX + pillWidth > width - 28f) {
                    currentX = 28f
                    pageManager.currentY += 24f
                    pageManager.ensureSpace(24f)
                }

                val rectF = RectF(currentX, pageManager.currentY - 12f, currentX + pillWidth, pageManager.currentY + 8f)
                pageManager.canvas.drawRoundRect(rectF, 6f, 6f, skillPillBgPaint)
                pageManager.canvas.drawText(skillText, currentX + 8f, pageManager.currentY + 2f, skillPillTextPaint)

                currentX += pillWidth + 8f
            }
            pageManager.currentY += 24f
        }

        // 6. Projects Section
        if (data.projects.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSectionHeader(pageManager, "FEATURED PROJECTS", primaryColorInt)

            for (proj in data.projects) {
                pageManager.ensureSpace(45f)

                val projTitlePaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(proj.projectName, 28f, pageManager.currentY, projTitlePaint)
                pageManager.currentY += 16f

                val projDescPaint = Paint().apply {
                    color = Color.parseColor("#334155")
                    textSize = 10f
                    isAntiAlias = true
                }
                val projLines = wrapText(proj.description, projDescPaint, width - 56f)
                for (line in projLines) {
                    pageManager.ensureSpace(14f)
                    pageManager.canvas.drawText(line, 28f, pageManager.currentY, projDescPaint)
                    pageManager.currentY += 14f
                }
                pageManager.currentY += 12f
            }
            pageManager.currentY += 8f
        }

        // 7. Certifications Section
        if (data.certifications.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSectionHeader(pageManager, "CERTIFICATIONS", primaryColorInt)

            for (cert in data.certifications) {
                pageManager.ensureSpace(20f)
                val certPaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10f
                    isAntiAlias = true
                }
                val certText = "• ${cert.name} — ${cert.issuer} (${cert.date})"
                pageManager.canvas.drawText(certText, 28f, pageManager.currentY, certPaint)
                pageManager.currentY += 16f
            }
            pageManager.currentY += 8f
        }

        // 8. Languages Section
        if (data.languages.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSectionHeader(pageManager, "LANGUAGES", primaryColorInt)

            val langPaint = Paint().apply {
                color = Color.parseColor("#334155")
                textSize = 10f
                isAntiAlias = true
            }
            val langText = data.languages.joinToString("  •  ") { "${it.languageName} (${it.proficiency})" }
            val langLines = wrapText(langText, langPaint, width - 56f)
            for (line in langLines) {
                pageManager.ensureSpace(14f)
                pageManager.canvas.drawText(line, 28f, pageManager.currentY, langPaint)
                pageManager.currentY += 14f
            }
            pageManager.currentY += 12f
        }

        // 9. References Section
        if (data.references.isNotEmpty()) {
            pageManager.ensureSpace(40f)
            drawSectionHeader(pageManager, "REFERENCES", primaryColorInt)

            for (ref in data.references) {
                pageManager.ensureSpace(30f)
                val refPaint = Paint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = 10f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                pageManager.canvas.drawText("${ref.name} — ${ref.title} at ${ref.company}", 28f, pageManager.currentY, refPaint)
                pageManager.currentY += 14f

                val refContactPaint = Paint().apply {
                    color = Color.parseColor("#64748B")
                    textSize = 9.5f
                    isAntiAlias = true
                }
                pageManager.canvas.drawText(ref.contactInfo, 28f, pageManager.currentY, refContactPaint)
                pageManager.currentY += 18f
            }
        }

        pageManager.finish()
    }

    private fun drawSectionHeader(pageManager: PdfPageManager, title: String, primaryColorInt: Int) {
        val width = A4_WIDTH_PT.toFloat()
        val headerPaint = Paint().apply {
            color = primaryColorInt
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(title, 28f, pageManager.currentY, headerPaint)

        val linePaint = Paint().apply {
            color = primaryColorInt
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        pageManager.canvas.drawLine(28f, pageManager.currentY + 4f, width - 28f, pageManager.currentY + 4f, linePaint)
        pageManager.currentY += 18f
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        if (text.isBlank()) return emptyList()
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            val potentialLine = if (currentLine.isEmpty()) word else "${currentLine} $word"
            if (paint.measureText(potentialLine) <= maxWidth) {
                if (currentLine.isNotEmpty()) currentLine.append(" ")
                currentLine.append(word)
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

    /**
     * Renders high-resolution profile photos with aspect-ratio preserving center crop
     * and template shape frames (Circle, Rounded Square, Passport Rect).
     */
    private fun drawStyledProfilePhoto(
        canvas: Canvas,
        sourceBitmap: Bitmap,
        rightX: Float,
        centerY: Float,
        shape: PhotoShape
    ) {
        when (shape) {
            PhotoShape.CIRCLE -> {
                val radiusPt = 32f
                val hiResDim = (radiusPt * 8).toInt() // 256px High DPI
                val cropped = createCenterCropBitmap(sourceBitmap, hiResDim, hiResDim)

                val output = Bitmap.createBitmap(hiResDim, hiResDim, Bitmap.Config.ARGB_8888)
                val maskCanvas = Canvas(output)
                val paint = Paint().apply {
                    isAntiAlias = true
                    isFilterBitmap = true
                    isDither = true
                }

                maskCanvas.drawCircle(hiResDim / 2f, hiResDim / 2f, hiResDim / 2f, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                maskCanvas.drawBitmap(cropped, 0f, 0f, paint)

                val centerX = rightX - radiusPt
                val rectF = RectF(centerX - radiusPt, centerY - radiusPt, centerX + radiusPt, centerY + radiusPt)
                canvas.drawBitmap(output, null, rectF, Paint().apply { isAntiAlias = true; isFilterBitmap = true })

                val borderPaint = Paint().apply {
                    color = Color.WHITE
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                    isAntiAlias = true
                }
                canvas.drawCircle(centerX, centerY, radiusPt, borderPaint)
            }

            PhotoShape.ROUNDED_SQUARE -> {
                val sizePt = 64f
                val hiResDim = (sizePt * 4).toInt() // 256px High DPI
                val cropped = createCenterCropBitmap(sourceBitmap, hiResDim, hiResDim)

                val output = Bitmap.createBitmap(hiResDim, hiResDim, Bitmap.Config.ARGB_8888)
                val maskCanvas = Canvas(output)
                val paint = Paint().apply {
                    isAntiAlias = true
                    isFilterBitmap = true
                    isDither = true
                }

                val cornerPx = 12f * 4f
                maskCanvas.drawRoundRect(0f, 0f, hiResDim.toFloat(), hiResDim.toFloat(), cornerPx, cornerPx, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                maskCanvas.drawBitmap(cropped, 0f, 0f, paint)

                val leftX = rightX - sizePt
                val rectF = RectF(leftX, centerY - sizePt / 2f, rightX, centerY + sizePt / 2f)
                canvas.drawBitmap(output, null, rectF, Paint().apply { isAntiAlias = true; isFilterBitmap = true })

                val borderPaint = Paint().apply {
                    color = Color.WHITE
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                    isAntiAlias = true
                }
                canvas.drawRoundRect(rectF, 12f, 12f, borderPaint)
            }

            PhotoShape.PASSPORT_RECT -> {
                val widthPt = 54f
                val heightPt = 72f // 3:4 passport ratio
                val hiResW = (widthPt * 4).toInt() // 216px
                val hiResH = (heightPt * 4).toInt() // 288px
                val cropped = createCenterCropBitmap(sourceBitmap, hiResW, hiResH)

                val output = Bitmap.createBitmap(hiResW, hiResH, Bitmap.Config.ARGB_8888)
                val maskCanvas = Canvas(output)
                val paint = Paint().apply {
                    isAntiAlias = true
                    isFilterBitmap = true
                    isDither = true
                }

                val cornerPx = 6f * 4f
                maskCanvas.drawRoundRect(0f, 0f, hiResW.toFloat(), hiResH.toFloat(), cornerPx, cornerPx, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                maskCanvas.drawBitmap(cropped, 0f, 0f, paint)

                val leftX = rightX - widthPt
                val rectF = RectF(leftX, centerY - heightPt / 2f, rightX, centerY + heightPt / 2f)
                canvas.drawBitmap(output, null, rectF, Paint().apply { isAntiAlias = true; isFilterBitmap = true })

                val borderPaint = Paint().apply {
                    color = Color.WHITE
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                    isAntiAlias = true
                }
                canvas.drawRoundRect(rectF, 6f, 6f, borderPaint)
            }
        }
    }

    private fun createCenterCropBitmap(source: Bitmap, targetW: Int, targetH: Int): Bitmap {
        val srcW = source.width
        val srcH = source.height
        val scale = maxOf(targetW.toFloat() / srcW, targetH.toFloat() / srcH)

        val scaledW = srcW * scale
        val scaledH = srcH * scale
        val left = (targetW - scaledW) / 2f
        val top = (targetH - scaledH) / 2f

        val result = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
        }
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postTranslate(left, top)
        }
        canvas.drawBitmap(source, matrix, paint)
        return result
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
