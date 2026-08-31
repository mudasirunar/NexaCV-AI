package com.mudasir.nexacvai.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Universal NexaCV AI Modal Bottom Sheet component.
 *
 * Encapsulates standard bottom sheet design rules:
 * - 24dp rounded top corners
 * - Centered pill drag handle with standard opacity
 * - Tablet & landscape width constraint (max 560dp)
 * - Custom window insets passthrough for granular internal padding control
 * - Dynamic slot API for flexible feature content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexaModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { NexaBottomSheetDragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { WindowInsets(0, 0, 0, 0) },
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .widthIn(max = 560.dp)
            .fillMaxWidth(),
        sheetState = sheetState,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        contentWindowInsets = contentWindowInsets,
        properties = properties,
        content = content
    )
}

/**
 * Standard NexaCV AI bottom sheet drag handle pill.
 */
@Composable
fun NexaBottomSheetDragHandle(
    modifier: Modifier = Modifier,
    width: Dp = 36.dp,
    height: Dp = 4.dp,
    topPadding: Dp = 10.dp,
    bottomPadding: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
) {
    Box(
        modifier = modifier
            .padding(top = topPadding, bottom = bottomPadding)
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}
