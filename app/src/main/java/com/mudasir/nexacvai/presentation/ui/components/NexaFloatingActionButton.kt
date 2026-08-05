package com.mudasir.nexacvai.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable, highly customizable translucent/bordered Floating Action Button (FAB) for NexaCV AI.
 * Fixed clipping and border stroke alignment for clean Light & Dark Mode rendering.
 */
@Composable
fun NexaFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String? = "Add Profile",
    hasBorder: Boolean = true,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderWidth: Dp = 1.dp,
    fillColor: Color = MaterialTheme.colorScheme.primary,
    fillOpacity: Float = 0.16f,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(16.dp),
    scaleOnPress: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (scaleOnPress && isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "NexaFabScale"
    )

    val effectiveModifier = modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clip(shape) // 1. Clip first to force exact rounded corners
        .then(
            if (hasBorder) {
                Modifier.border(
                    border = BorderStroke(borderWidth, borderColor),
                    shape = shape
                )
            } else {
                Modifier
            }
        )

    FloatingActionButton(
        onClick = onClick,
        modifier = effectiveModifier,
        shape = shape,
        containerColor = fillColor.copy(alpha = fillOpacity),
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor
        )
    }
}