package com.mudasir.nexacvai.presentation.ui.components

import android.annotation.SuppressLint
import android.graphics.PointF
import android.view.MotionEvent
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
import com.github.barteksc.pdfviewer.util.Constants
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.mudasir.nexacvai.ui.theme.*
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.roundToInt

private const val MIN_ZOOM_LEVEL = 0.5f
private const val MID_ZOOM_LEVEL = 2.5f
private const val MAX_ZOOM_LEVEL = 5.0f

@Composable
fun PdfDocumentViewer(
    pdfFile: File?,
    modifier: Modifier = Modifier,
    isTopBarVisible: Boolean = true,
    onToggleTopBar: (Boolean) -> Unit = {},
    onZoomChange: (Float) -> Unit = {}
) {
    var pdfViewRef by remember { mutableStateOf<PDFView?>(null) }
    var currentPage by remember { mutableIntStateOf(1) }
    var totalPages by remember { mutableIntStateOf(1) }
    var currentZoom by remember { mutableFloatStateOf(1.0f) }

    val isDark = isAppInDarkTheme()
    val canvasBgColor = getPdfCanvasBgColor(isDark)
    val canvasBgHex = getPdfCanvasBgHex(isDark)

    var isControlsVisible by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    fun wakeUpControls() {
        lastInteractionTime = System.currentTimeMillis()
        isControlsVisible = true
    }

    // 3s Inactivity Auto-Hide timer ONLY for floating zoom controls
    LaunchedEffect(lastInteractionTime) {
        isControlsVisible = true
        delay(3000L)
        isControlsVisible = false
    }

    val currentTopBarVisible by rememberUpdatedState(isTopBarVisible)
    val currentToggleTopBar by rememberUpdatedState(onToggleTopBar)
    val currentOnZoomChange by rememberUpdatedState(onZoomChange)

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val widthDp = configuration.screenWidthDp
    val heightDp = configuration.screenHeightDp
    val smallestWidthDp = configuration.smallestScreenWidthDp

    val maxDim = maxOf(widthDp, heightDp).toFloat()
    val minDim = minOf(widthDp, heightDp).toFloat()
    val aspectRatio = if (minDim > 0) maxDim / minDim else 1f

    // Device classification:
    // - Foldables (Unfolded): Large screen (>= 580dp) with almost square aspect ratio (< 1.35)
    // - Tablets: Large screen (>= 580dp) with widescreen ratio (>= 1.35)
    val isFoldable = smallestWidthDp >= 580 && aspectRatio < 1.35
    val isTablet = smallestWidthDp >= 580 && aspectRatio >= 1.35
    val isPhone = smallestWidthDp < 580

    // Initial Zoom Level:
    // - Landscape (Phone & Tablet): 150% (1.5f) for enhanced readability
    // - Phone Portrait & Tablet Portrait: 100% (1.0f)
    // - Foldable Unfolded (Portrait & Landscape): 100% (1.0f)
    val targetInitialZoom = when {
        isLandscape && (isPhone || isTablet) -> 1.5f
        else -> 1.0f
    }

    LaunchedEffect(pdfFile?.absolutePath, pdfViewRef, isLandscape, isFoldable, isTablet) {
        val view = pdfViewRef ?: return@LaunchedEffect
        val file = pdfFile ?: return@LaunchedEffect
        if (!file.exists() || file.length() == 0L) return@LaunchedEffect

        var lastYOffset = 0f
        var hasAppliedInitialZoom = false

        Constants.THUMBNAIL_RATIO = 1f
        Constants.PRELOAD_OFFSET = 100

        view.fromFile(file)
            .defaultPage(0)
            .enableAnnotationRendering(true)
            .swipeHorizontal(false)
            .spacing(16)
            .pageFitPolicy(FitPolicy.BOTH)
            .enableDoubletap(true)
            .enableAntialiasing(true)
            .onPageChange(OnPageChangeListener { page, pageCount ->
                currentPage = page + 1
                totalPages = pageCount
                if (!hasAppliedInitialZoom && targetInitialZoom != 1.0f) {
                    hasAppliedInitialZoom = true
                    val centerX = if (view.width > 0) view.width / 2f else 0f
                    view.zoomCenteredTo(targetInitialZoom, PointF(centerX, 0f))
                }
                currentZoom = view.zoom
                currentOnZoomChange(view.zoom)
                wakeUpControls()
            })
            .onDraw(OnDrawListener { canvas, pageWidth, pageHeight, _ ->
                val z = view.zoom
                if (kotlin.math.abs(z - currentZoom) > 0.01f) {
                    currentZoom = z
                    currentOnZoomChange(z)
                    wakeUpControls()
                }

                // TopBar scroll rules: Scrolling DOWN hides TopBar, reaching TOP shows TopBar
                val yOffset = view.currentYOffset
                if (view.currentPage == 0 && yOffset >= -80f) {
                    if (!currentTopBarVisible) {
                        currentToggleTopBar(true)
                    }
                } else {
                    val deltaY = lastYOffset - yOffset
                    if (deltaY > 15f) {
                        if (currentTopBarVisible) {
                            currentToggleTopBar(false)
                        }
                    }
                }
                lastYOffset = yOffset

                val shadowGradient = android.graphics.LinearGradient(
                    0f, pageHeight,
                    0f, pageHeight + 12f,
                    intArrayOf(
                        android.graphics.Color.parseColor("#28000000"),
                        android.graphics.Color.parseColor("#0F000000"),
                        android.graphics.Color.TRANSPARENT
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
                currentToggleTopBar(!currentTopBarVisible)
                wakeUpControls()
                true
            })
            .load()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(canvasBgColor),
        contentAlignment = Alignment.Center
    ) {
        if (pdfFile == null || !pdfFile.exists()) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            var lastAppliedBgHex by remember { mutableStateOf<String?>(null) }

            AndroidView(
                factory = { context ->
                    TouchInterceptPdfView(context, targetInitialZoom).apply {
                        setBackgroundColor(android.graphics.Color.parseColor(canvasBgHex))
                        lastAppliedBgHex = canvasBgHex
                        setMinZoom(MIN_ZOOM_LEVEL)
                        setMidZoom(MID_ZOOM_LEVEL)
                        setMaxZoom(MAX_ZOOM_LEVEL)
                        useBestQuality(true)
                        enableRenderDuringScale(true)
                        pdfViewRef = this
                    }
                },
                update = { pdfView ->
                    pdfViewRef = pdfView
                    (pdfView as? TouchInterceptPdfView)?.initialZoom = targetInitialZoom
                    if (lastAppliedBgHex != canvasBgHex) {
                        pdfView.setBackgroundColor(android.graphics.Color.parseColor(canvasBgHex))
                        lastAppliedBgHex = canvasBgHex
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 16.dp)
            )

            val isCanZoomOut = currentZoom > (MIN_ZOOM_LEVEL + 0.05f)
            val isCanZoomIn = currentZoom < (MAX_ZOOM_LEVEL - 0.05f)
            val isCanReset = kotlin.math.abs(currentZoom - 1.0f) > 0.05f

            val rawPercent = (currentZoom * 100).roundToInt()
            val displayZoomPercent = when {
                rawPercent in 48..52 -> 50
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

            // Floating Zoom Controls (Independent 3s inactivity auto-hide)
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
                        IconButton(
                            onClick = {
                                wakeUpControls()
                                pdfViewRef?.let { view ->
                                    val target = (view.zoom - 1.0f).coerceAtLeast(MIN_ZOOM_LEVEL)
                                    view.zoomWithAnimation(target)
                                    view.invalidate()
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

                        IconButton(
                            onClick = {
                                wakeUpControls()
                                pdfViewRef?.let { view ->
                                    val target = (view.zoom + 1.0f).coerceAtMost(MAX_ZOOM_LEVEL)
                                    view.zoomWithAnimation(target)
                                    view.invalidate()
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

                        IconButton(
                            onClick = {
                                wakeUpControls()
                                pdfViewRef?.let { view ->
                                    view.zoomWithAnimation(1.0f)
                                    view.invalidate()
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

@SuppressLint("ViewConstructor")
private class TouchInterceptPdfView(
    context: android.content.Context,
    var initialZoom: Float
) : PDFView(context, null) {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (zoom > (initialZoom + 0.05f)) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                } else {
                    parent?.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                // Multi-touch detected (2 or more fingers -> pinch to zoom).
                // Block parent pagers from intercepting this gesture!
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (ev.pointerCount >= 2 || zoom > (initialZoom + 0.05f)) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
