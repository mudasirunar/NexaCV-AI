package com.mudasir.nexacvai.presentation.ui.templates.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Shimmer Skeleton Animation Modifier for Template Preview & Profile Injection.
 */
fun Modifier.shimmerEffect(
    shimmerColor: Color = Color.LightGray.copy(alpha = 0.4f),
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.15f)
): Modifier = composed {
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
            backgroundColor,
            shimmerColor,
            backgroundColor
        ),
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )

    this.background(brush)
}
