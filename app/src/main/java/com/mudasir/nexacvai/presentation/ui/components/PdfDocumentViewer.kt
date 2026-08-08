package com.mudasir.nexacvai.presentation.ui.components

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Interactive A4 PDF Document Viewer with Google Drive style Bounded Pinch-to-Zoom (1.0x - 4.0x) & Pan.
 */
@Composable
fun PdfDocumentViewer(
    pdfFile: File?,
    modifier: Modifier = Modifier
) {
    var bitmapState by remember(pdfFile) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(pdfFile) { mutableStateOf(true) }

    // Pinch-to-Zoom & Pan Transform State
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val animatedScale by animateFloatAsState(targetValue = scale, label = "pdfZoomScale")
    val animatedOffsetX by animateFloatAsState(targetValue = offsetX, label = "pdfPanX")
    val animatedOffsetY by animateFloatAsState(targetValue = offsetY, label = "pdfPanY")

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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(1f, 4f)
                            scale = newScale

                            if (newScale <= 1.05f) {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            } else {
                                // Strictly clamp pan boundaries to keep document inside container frame
                                val maxPanX = (containerWidth * (newScale - 1f) / 2f).coerceAtLeast(0f)
                                val maxPanY = (containerHeight * (newScale - 1f) / 2f).coerceAtLeast(0f)

                                offsetX = (offsetX + pan.x).coerceIn(-maxPanX, maxPanX)
                                offsetY = (offsetY + pan.y).coerceIn(-maxPanY, maxPanY)
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
                            scaleX = animatedScale,
                            scaleY = animatedScale,
                            translationX = animatedOffsetX,
                            translationY = animatedOffsetY
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
                            val newScale = (scale - 0.5f).coerceIn(1f, 4f)
                            scale = newScale
                            if (newScale <= 1f) {
                                offsetX = 0f
                                offsetY = 0f
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
                        text = "${(scale * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { scale = (scale + 0.5f).coerceIn(1f, 4f) },
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
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
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
