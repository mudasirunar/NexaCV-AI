package com.mudasir.nexacvai.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.ui.theme.*
import kotlin.math.roundToInt

const val MIN_ZOOM_LEVEL = 0.5f
const val MID_ZOOM_LEVEL = 2.5f
const val MAX_ZOOM_LEVEL = 5.0f

fun calculateNextZoomIn(baseZoom: Float): Float {
    if (baseZoom < 0.95f) return 1.0f
    val step = (kotlin.math.round(baseZoom * 2f) / 2f)
    return (step + 1.0f).coerceAtMost(MAX_ZOOM_LEVEL)
}

fun calculateNextZoomOut(baseZoom: Float): Float {
    if (baseZoom <= 1.05f) return MIN_ZOOM_LEVEL
    val step = (kotlin.math.round(baseZoom * 2f) / 2f)
    val next = step - 1.0f
    return if (next < 0.95f && baseZoom > 1.2f) 1.0f else next.coerceAtLeast(MIN_ZOOM_LEVEL)
}

@Composable
fun FloatingZoomControls(
    currentZoom: Float,
    currentPage: Int = 1,
    totalPages: Int = 1,
    isVisible: Boolean,
    skipEnterAnimation: Boolean = false,
    hasPrevious: Boolean = false,
    hasNext: Boolean = false,
    onPrevious: (() -> Unit)? = null,
    onNext: (() -> Unit)? = null,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    modifier: Modifier = Modifier
) {
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

    val enterTransition = if (skipEnterAnimation) {
        fadeIn(animationSpec = androidx.compose.animation.core.snap())
    } else {
        fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    }

    val showNavigation = onPrevious != null || onNext != null

    AnimatedVisibility(
        visible = isVisible,
        enter = enterTransition,
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        modifier = modifier
    ) {
        if (showNavigation) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(IntrinsicSize.Min)
            ) {
                // Previous Button: Left semi-circle / half-oval wing
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        bottomStart = 24.dp,
                        topEnd = 4.dp,
                        bottomEnd = 4.dp
                    ),
                    color = PdfControlsContainerBg,
                    border = BorderStroke(1.dp, PdfControlsBorder),
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp)
                    ) {
                        IconButton(
                            onClick = { onPrevious?.invoke() },
                            enabled = hasPrevious,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Template",
                                tint = if (hasPrevious) PdfControlsIconTintEnabled else PdfControlsIconTintDisabled
                            )
                        }
                    }
                }

                // Center Rectangle: no rounded ends
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = PdfControlsContainerBg,
                    border = BorderStroke(1.dp, PdfControlsBorder),
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        IconButton(
                            onClick = onZoomOut,
                            enabled = isCanZoomOut,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Zoom Out",
                                tint = if (isCanZoomOut) PdfControlsIconTintEnabled else PdfControlsIconTintDisabled
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PdfControlsPillBg
                        ) {
                            Text(
                                text = if (totalPages > 1) "Page $currentPage of $totalPages" else "$displayZoomPercent%",
                                style = MaterialTheme.typography.labelMedium,
                                color = PdfControlsIconTintEnabled,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        IconButton(
                            onClick = onZoomIn,
                            enabled = isCanZoomIn,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Zoom In",
                                tint = if (isCanZoomIn) PdfControlsIconTintEnabled else PdfControlsIconTintDisabled
                            )
                        }

                        IconButton(
                            onClick = onResetZoom,
                            enabled = isCanReset,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset Zoom",
                                tint = if (isCanReset) PdfControlsIconTintEnabled else PdfControlsIconTintDisabled
                            )
                        }
                    }
                }

                // Next Button: Right semi-circle / half-oval wing
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        bottomStart = 4.dp,
                        topEnd = 24.dp,
                        bottomEnd = 24.dp
                    ),
                    color = PdfControlsContainerBg,
                    border = BorderStroke(1.dp, PdfControlsBorder),
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp)
                    ) {
                        IconButton(
                            onClick = { onNext?.invoke() },
                            enabled = hasNext,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Template",
                                tint = if (hasNext) PdfControlsIconTintEnabled else PdfControlsIconTintDisabled
                            )
                        }
                    }
                }
            }
        } else {
            // Standalone view without navigation: original pill shape
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
                        onClick = onZoomOut,
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
                        onClick = onZoomIn,
                        enabled = isCanZoomIn
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            tint = if (isCanZoomIn) PdfControlsIconTintEnabled else PdfControlsIconTintDisabled
                        )
                    }

                    IconButton(
                        onClick = onResetZoom,
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
