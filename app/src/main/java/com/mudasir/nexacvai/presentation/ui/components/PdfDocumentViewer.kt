package com.mudasir.nexacvai.presentation.ui.components

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/** Double-tap zoom threshold: below this → zoom in to TARGET, above → zoom out to 1x. */
private const val DOUBLE_TAP_THRESHOLD = 1.5f
/** Target zoom level on double-tap zoom-in. */
private const val DOUBLE_TAP_ZOOM_TARGET = 2.5f
/** Animation duration for double-tap zoom (ms). */
private const val DOUBLE_TAP_ANIM_MS = 300

/**
 * Interactive A4 PDF Document Viewer with Google Drive style Bounded Pinch-to-Zoom (1.0x - 4.0x),
 * direct 1:1 Pan with fling momentum, and double-tap to zoom in/out.
 */
@Composable
fun PdfDocumentViewer(
    pdfFile: File?,
    modifier: Modifier = Modifier
) {
    var bitmapState by remember(pdfFile) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(pdfFile) { mutableStateOf(true) }

    // Zoom state — Animatable for smooth double-tap and button-triggered zoom transitions
    val scaleAnimatable = remember { Animatable(1f) }
    val currentScale by remember { derivedStateOf { scaleAnimatable.value } }

    // Pan state — direct tracking with fling support (no animation lag)
    val offsetAnimatable = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    // Render A4 PDF Page 0 asynchronously using Android PdfRenderer
    LaunchedEffect(pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(fileDescriptor)

                if (pdfRenderer.pageCount > 0) {
                    val page = pdfRenderer.openPage(0)
                    // Render page at 2.5x density for sharp vector rendering
                    val bitmap = Bitmap.createBitmap(
                        page.width * 2 + 300,
                        page.height * 2 + 420,
                        Bitmap.Config.ARGB_8888
                    )

                    // Draw White paper background
                    val canvas = android.graphics.Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    bitmapState = bitmap
                }

                pdfRenderer.close()
                fileDescriptor.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        val containerWidth = constraints.maxWidth.toFloat()
        val containerHeight = constraints.maxHeight.toFloat()

        if (isLoading || bitmapState == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val bitmap = bitmapState!!
            val scope = rememberCoroutineScope()

            // Helper to clamp offset within allowed pan boundaries
            fun clampOffset(offset: Offset, currentScale: Float): Offset {
                if (currentScale <= 1.05f) return Offset.Zero
                val maxPanX = (containerWidth * (currentScale - 1f) / 2f).coerceAtLeast(0f)
                val maxPanY = (containerHeight * (currentScale - 1f) / 2f).coerceAtLeast(0f)
                return Offset(
                    offset.x.coerceIn(-maxPanX, maxPanX),
                    offset.y.coerceIn(-maxPanY, maxPanY)
                )
            }

            // Track last single-tap for manual double-tap detection
            var lastTapTime by remember { mutableLongStateOf(0L) }
            var lastTapPosition by remember { mutableStateOf(Offset.Zero) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val velocityTracker = VelocityTracker()
                            val down = awaitFirstDown(requireUnconsumed = false)
                            // Stop any ongoing fling or double-tap animation
                            scope.launch {
                                offsetAnimatable.stop()
                                scaleAnimatable.stop()
                            }

                            var gestureScale = scaleAnimatable.value
                            var previousPointerCount = 1
                            var wasPinching = false
                            var totalDragDistance = Offset.Zero

                            do {
                                val event = awaitPointerEvent()
                                val activePointers = event.changes.count { it.pressed }
                                val zoomChange = event.calculateZoom()
                                val panChange = event.calculatePan()

                                // Detect pointer count transition — skip this frame's pan
                                val pointerCountChanged = activePointers != previousPointerCount
                                previousPointerCount = activePointers

                                val isPinching = activePointers >= 2
                                if (isPinching) wasPinching = true

                                // Track total drag distance to distinguish taps from drags
                                totalDragDistance += panChange

                                // Apply zoom
                                val newScale = (gestureScale * zoomChange).coerceIn(1f, 4f)
                                if (newScale <= 1.05f) {
                                    gestureScale = 1f
                                    scope.launch {
                                        scaleAnimatable.snapTo(1f)
                                        offsetAnimatable.snapTo(Offset.Zero)
                                    }
                                } else {
                                    gestureScale = newScale
                                    scope.launch { scaleAnimatable.snapTo(newScale) }

                                    // Re-clamp offset to new scale boundaries
                                    val clampedCurrent = clampOffset(offsetAnimatable.value, newScale)

                                    if (!pointerCountChanged && !isPinching) {
                                        val newOffset = clampOffset(
                                            clampedCurrent + panChange,
                                            newScale
                                        )
                                        scope.launch { offsetAnimatable.snapTo(newOffset) }
                                    } else if (clampedCurrent != offsetAnimatable.value) {
                                        scope.launch { offsetAnimatable.snapTo(clampedCurrent) }
                                    }
                                }

                                // Track velocity for fling — only single-finger drag
                                if (!isPinching && !pointerCountChanged) {
                                    event.changes.forEach { change ->
                                        if (change.positionChanged()) {
                                            velocityTracker.addPosition(
                                                change.uptimeMillis,
                                                change.position
                                            )
                                        }
                                    }
                                }

                                event.changes.forEach { it.consume() }
                            } while (event.changes.any { it.pressed })

                            // Detect if this was a tap (minimal drag, single finger, no pinch)
                            val wasTap = !wasPinching &&
                                    totalDragDistance.getDistance() < 20f

                            if (wasTap) {
                                val now = System.currentTimeMillis()
                                val tapPos = down.position
                                val timeSinceLastTap = now - lastTapTime
                                val distFromLastTap = (tapPos - lastTapPosition).getDistance()

                                if (timeSinceLastTap < 350L && distFromLastTap < 100f) {
                                    // DOUBLE TAP detected
                                    lastTapTime = 0L // Reset to avoid triple-tap
                                    scope.launch {
                                        val currentZoom = scaleAnimatable.value
                                        if (currentZoom < DOUBLE_TAP_THRESHOLD) {
                                            // Zoom IN centered on tap point
                                            val targetScale = DOUBLE_TAP_ZOOM_TARGET
                                            val centerX = containerWidth / 2f
                                            val centerY = containerHeight / 2f
                                            val tapDeltaX = centerX - tapPos.x
                                            val tapDeltaY = centerY - tapPos.y
                                            val targetOffset = clampOffset(
                                                Offset(tapDeltaX * targetScale, tapDeltaY * targetScale),
                                                targetScale
                                            )
                                            launch { scaleAnimatable.animateTo(targetScale, tween(DOUBLE_TAP_ANIM_MS)) }
                                            launch { offsetAnimatable.animateTo(targetOffset, tween(DOUBLE_TAP_ANIM_MS)) }
                                        } else {
                                            // Zoom OUT to 1x
                                            launch { scaleAnimatable.animateTo(1f, tween(DOUBLE_TAP_ANIM_MS)) }
                                            launch { offsetAnimatable.animateTo(Offset.Zero, tween(DOUBLE_TAP_ANIM_MS)) }
                                        }
                                    }
                                } else {
                                    // Single tap — record for potential double-tap
                                    lastTapTime = now
                                    lastTapPosition = tapPos
                                }
                            } else {
                                // Was a drag/pinch — reset tap tracking
                                lastTapTime = 0L
                            }

                            // Fling on release — only for single-finger pan
                            if (gestureScale > 1.05f && !wasPinching && !wasTap) {
                                val velocity = velocityTracker.calculateVelocity()
                                val maxPanX = (containerWidth * (gestureScale - 1f) / 2f).coerceAtLeast(0f)
                                val maxPanY = (containerHeight * (gestureScale - 1f) / 2f).coerceAtLeast(0f)

                                scope.launch {
                                    offsetAnimatable.updateBounds(
                                        lowerBound = Offset(-maxPanX, -maxPanY),
                                        upperBound = Offset(maxPanX, maxPanY)
                                    )
                                    offsetAnimatable.animateDecay(
                                        initialVelocity = Offset(velocity.x, velocity.y),
                                        animationSpec = exponentialDecay(frictionMultiplier = 1.5f)
                                    )
                                    offsetAnimatable.updateBounds(
                                        lowerBound = Offset(-Float.MAX_VALUE, -Float.MAX_VALUE),
                                        upperBound = Offset(Float.MAX_VALUE, Float.MAX_VALUE)
                                    )
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Real A4 Paper Document Sheet Card
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.92f)
                        .graphicsLayer(
                            scaleX = currentScale,
                            scaleY = currentScale,
                            translationX = offsetAnimatable.value.x,
                            translationY = offsetAnimatable.value.y
                        )
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "A4 PDF Document Preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Floating Interactive Zoom Controls Bar
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            val newScale = (currentScale - 0.5f).coerceIn(1f, 4f)
                            scope.launch {
                                scaleAnimatable.animateTo(newScale, tween(200))
                                if (newScale <= 1f) {
                                    offsetAnimatable.animateTo(Offset.Zero, tween(200))
                                } else {
                                    offsetAnimatable.snapTo(
                                        clampOffset(offsetAnimatable.value, newScale)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom Out",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "${(currentScale * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { scope.launch { scaleAnimatable.animateTo((currentScale + 0.5f).coerceIn(1f, 4f), tween(200)) } },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            scope.launch {
                                launch { scaleAnimatable.animateTo(1f, tween(200)) }
                                launch { offsetAnimatable.animateTo(Offset.Zero, tween(200)) }
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Zoom",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

