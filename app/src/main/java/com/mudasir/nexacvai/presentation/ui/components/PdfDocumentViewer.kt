package com.mudasir.nexacvai.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnDrawListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnTapListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.mudasir.nexacvai.ui.theme.*
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.roundToInt

private const val MIN_ZOOM_LEVEL = 1.0f
private const val MID_ZOOM_LEVEL = 2.5f
private const val MAX_ZOOM_LEVEL = 5.0f

/**
 * Professional Native A4 PDF Viewer powered by [PDFView].
 * Configured with [FitPolicy.WIDTH] and 5.0x max zoom (100%, 250%, 500%),
 * 60 FPS multi-page vertical scrolling, equal top/left/right spacing, and floating auto-hiding controls.
 */
@Composable
fun PdfDocumentViewer(
    pdfFile: File?,
    modifier: Modifier = Modifier
) {
    var pdfViewInstance by remember { mutableStateOf<PDFView?>(null) }
    var currentPage by remember { mutableIntStateOf(1) }
    var totalPages by remember { mutableIntStateOf(1) }
    var currentZoom by remember { mutableFloatStateOf(1.0f) }

    // Inactivity Auto-Hide state for floating controls bar
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isControlsVisible by remember { mutableStateOf(true) }

    LaunchedEffect(lastInteractionTime) {
        isControlsVisible = true
        delay(3500L) // Auto-hide controls after 3.5 seconds of inactivity
        isControlsVisible = false
    }

    fun wakeUpControls() {
        lastInteractionTime = System.currentTimeMillis()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PdfViewerCanvasBg),
        contentAlignment = Alignment.Center
    ) {
        if (pdfFile == null || !pdfFile.exists()) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            AndroidView(
                factory = { context ->
                    PDFView(context, null).apply {
                        setBackgroundColor(android.graphics.Color.parseColor(PdfViewerCanvasBgHex))
                        setMinZoom(MIN_ZOOM_LEVEL)
                        setMidZoom(MID_ZOOM_LEVEL)
                        setMaxZoom(MAX_ZOOM_LEVEL)
                        pdfViewInstance = this
                    }
                },
                update = { pdfView ->
                    pdfViewInstance = pdfView
                    pdfView.setMinZoom(MIN_ZOOM_LEVEL)
                    pdfView.setMidZoom(MID_ZOOM_LEVEL)
                    pdfView.setMaxZoom(MAX_ZOOM_LEVEL)
                    pdfView.fromFile(pdfFile)
                        .defaultPage(0)
                        .enableAnnotationRendering(true)
                        .swipeHorizontal(false) // Vertical multi-page scrolling!
                        .spacing(20) // 20dp page break gap between A4 pages
                        .pageFitPolicy(FitPolicy.WIDTH) // Exact 100% width fit scale
                        .enableDoubletap(true)
                        .enableAntialiasing(true)
                        .onPageChange(OnPageChangeListener { page, pageCount ->
                            currentPage = page + 1
                            totalPages = pageCount
                            currentZoom = pdfView.zoom
                            wakeUpControls()
                        })
                        .onDraw(OnDrawListener { canvas, pageWidth, pageHeight, _ ->
                            val z = pdfView.zoom
                            if (kotlin.math.abs(z - currentZoom) > 0.01f) {
                                currentZoom = z
                                wakeUpControls()
                            }
                            // Ultra-smooth page-bottom paper drop shadow gradient fade
                            val shadowGradient = android.graphics.LinearGradient(
                                0f, pageHeight,
                                0f, pageHeight + 12f,
                                intArrayOf(
                                    android.graphics.Color.parseColor("#28000000"), // 16% alpha soft dark edge
                                    android.graphics.Color.parseColor("#0F000000"), // 6% alpha mid shadow
                                    android.graphics.Color.TRANSPARENT              // Smooth transparent falloff
                                ),
                                null,
                                android.graphics.Shader.TileMode.CLAMP
                            )
                            val shadowPaint = android.graphics.Paint().apply {
                                shader = shadowGradient
                                style = android.graphics.Paint.Style.FILL
                            }
                            canvas.drawRect(0f, pageHeight, pageWidth, pageHeight + 12f, shadowPaint)
                        })
                        .onTap(OnTapListener {
                            wakeUpControls()
                            true
                        })
                        .load()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 56.dp) // Equal 16dp top, left, and right spacing
            )

            // Synchronized Zoom state bounds (1.0x - 5.0x)
            val isCanZoomOut = currentZoom > (MIN_ZOOM_LEVEL + 0.05f)
            val isCanZoomIn = currentZoom < (MAX_ZOOM_LEVEL - 0.05f)
            val isCanReset = currentZoom > (MIN_ZOOM_LEVEL + 0.05f)

            // Clean percentage snapping for 100%, 250%, and 500% display
            val rawPercent = (currentZoom * 100).roundToInt()
            val displayZoomPercent = when {
                rawPercent in 98..102 -> 100
                rawPercent in 148..152 -> 150
                rawPercent in 198..202 -> 200
                rawPercent in 247..253 -> 250
                rawPercent in 298..302 -> 300
                rawPercent in 348..352 -> 350
                rawPercent in 398..402 -> 400
                rawPercent in 448..452 -> 450
                rawPercent in 495..505 -> 500
                else -> rawPercent
            }

            AnimatedVisibility(
                visible = isControlsVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = PdfControlsContainerBg,
                    border = BorderStroke(1.dp, PdfControlsBorder),
                    shadowElevation = 12.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Zoom Out (-)
                        IconButton(
                            onClick = {
                                wakeUpControls()
                                pdfViewInstance?.let { view ->
                                    val target = (view.zoom - 1.0f).coerceAtLeast(MIN_ZOOM_LEVEL)
                                    view.zoomWithAnimation(target)
                                    view.invalidate()
                                    currentZoom = target
                                }
                            },
                            enabled = isCanZoomOut
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Zoom Out",
                                tint = if (isCanZoomOut) PdfControlsIconTintEnabled else PdfControlsIconTintDisabled
                            )
                        }

                        // Page Counter / Zoom Status Pill
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PdfControlsPillBg
                        ) {
                            Text(
                                text = if (totalPages > 1) "Page $currentPage of $totalPages" else "$displayZoomPercent%",
                                style = MaterialTheme.typography.labelMedium,
                                color = PdfControlsIconTintEnabled,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }

                        // Zoom In (+)
                        IconButton(
                            onClick = {
                                wakeUpControls()
                                pdfViewInstance?.let { view ->
                                    val target = (view.zoom + 1.0f).coerceAtMost(MAX_ZOOM_LEVEL)
                                    view.zoomWithAnimation(target)
                                    view.invalidate()
                                    currentZoom = target
                                }
                            },
                            enabled = isCanZoomIn
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Zoom In",
                                tint = if (isCanZoomIn) PdfControlsIconTintEnabled else PdfControlsIconTintDisabled
                            )
                        }

                        // Reset Zoom Button (ONLY resets zoom level, retains current scroll position)
                        IconButton(
                            onClick = {
                                wakeUpControls()
                                pdfViewInstance?.let { view ->
                                    view.resetZoomWithAnimation()
                                    currentZoom = MIN_ZOOM_LEVEL
                                }
                            },
                            enabled = isCanReset
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset Zoom",
                                tint = if (isCanReset) PdfControlsIconTintEnabled else PdfControlsIconTintDisabled
                            )
                        }
                    }
                }
            }
        }
    }
}
