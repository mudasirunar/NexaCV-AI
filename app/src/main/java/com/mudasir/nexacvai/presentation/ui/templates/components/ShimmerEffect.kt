package com.mudasir.nexacvai.presentation.ui.templates.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.MaterialTheme

/**
 * Shimmer Skeleton Animation Modifier for Template Preview & Profile Injection.
 */
fun Modifier.shimmerEffect(
    shimmerColor: Color? = null,
    backgroundColor: Color? = null
): Modifier = composed {
    val defaultShimmer = shimmerColor ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val defaultBg = backgroundColor ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            defaultBg,
            defaultShimmer,
            defaultBg
        ),
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )

    this.background(brush)
}
