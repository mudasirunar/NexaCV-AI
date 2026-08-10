package com.mudasir.nexacvai.core.pdf.utils

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.mudasir.nexacvai.core.pdf.PdfPageManager
import com.mudasir.nexacvai.domain.model.template.PhotoShape
import java.io.File

/**
 * Shared Canvas drawing helper utilities for PDF rendering.
 */
object PdfDrawUtils {

    fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
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

    fun drawSectionHeader(
        pageManager: PdfPageManager,
        title: String,
        primaryColorInt: Int,
        leftX: Float = 28f,
        rightX: Float = pageManager.widthPt.toFloat() - 28f
    ) {
        val headerPaint = Paint().apply {
            color = primaryColorInt
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        pageManager.canvas.drawText(title.uppercase(), leftX, pageManager.currentY, headerPaint)

        val linePaint = Paint().apply {
            color = primaryColorInt
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        pageManager.canvas.drawLine(leftX, pageManager.currentY + 4f, rightX, pageManager.currentY + 4f, linePaint)
        pageManager.currentY += 18f
    }

    fun drawStyledProfilePhoto(
        context: Context,
        canvas: Canvas,
        sourceUriString: String,
        rightX: Float,
        centerY: Float,
        shape: PhotoShape
    ) {
        val sourceBitmap = loadBitmapFromUri(context, sourceUriString) ?: return
        when (shape) {
            PhotoShape.CIRCLE -> {
                val radiusPt = 32f
                val hiResDim = (radiusPt * 8).toInt()
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
                val hiResDim = (sizePt * 4).toInt()
                val cropped = createCenterCropBitmap(sourceBitmap, hiResDim, hiResDim)

                val output = Bitmap.createBitmap(hiResDim, hiResDim, Bitmap.Config.ARGB_8888)
                val maskCanvas = Canvas(output)
                val paint = Paint().apply {
                    isAntiAlias = true
                    isFilterBitmap = true
                    isDither = true
                }

                val cornerPx = 2f * 4f
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
                canvas.drawRoundRect(rectF, 2f, 2f, borderPaint)
            }

            PhotoShape.PASSPORT_RECT -> {
                val widthPt = 54f
                val heightPt = 72f
                val hiResW = (widthPt * 4).toInt()
                val hiResH = (heightPt * 4).toInt()
                val cropped = createCenterCropBitmap(sourceBitmap, hiResW, hiResH)

                val output = Bitmap.createBitmap(hiResW, hiResH, Bitmap.Config.ARGB_8888)
                val maskCanvas = Canvas(output)
                val paint = Paint().apply {
                    isAntiAlias = true
                    isFilterBitmap = true
                    isDither = true
                }

                val cornerPx = 2f * 4f
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
                canvas.drawRoundRect(rectF, 2f, 2f, borderPaint)
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

    fun loadBitmapFromUri(context: Context, uriString: String): Bitmap? {
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
