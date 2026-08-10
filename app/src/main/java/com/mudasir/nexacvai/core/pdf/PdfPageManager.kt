package com.mudasir.nexacvai.core.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument

/**
 * Manages A4 page lifecycle, current drawing Y offsets, page margins, and automatic page breaks.
 */
class PdfPageManager(
    private val pdfDocument: PdfDocument,
    val widthPt: Int = PdfGeneratorEngine.A4_WIDTH_PT,
    val heightPt: Int = PdfGeneratorEngine.A4_HEIGHT_PT,
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
