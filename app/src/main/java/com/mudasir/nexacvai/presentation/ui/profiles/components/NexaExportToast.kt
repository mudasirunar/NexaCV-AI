package com.mudasir.nexacvai.presentation.ui.profiles.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ExportProgressState
import kotlinx.coroutines.delay

/**
 * A reusable, custom premium Toast overlay styled like bottom connection banners.
 * Features:
 * - Slide up/down entry and exit transitions.
 * - Loading indicator with animating dots on "...".
 * - Simple raw outline checkmark (green) and cross (red) icons with no backgrounds.
 * - Automatic timeout dismissals for completion states.
 */
@Composable
fun NexaExportToast(
    exportState: ExportProgressState,
    errorMessage: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dotCount by remember { mutableStateOf(0) }

    LaunchedEffect(exportState) {
        if (exportState == ExportProgressState.Exporting) {
            while (true) {
                delay(500)
                dotCount = (dotCount + 1) % 4
            }
        }
    }

    LaunchedEffect(exportState) {
        if (exportState == ExportProgressState.Success) {
            delay(3000)
            onDismiss()
        } else if (exportState == ExportProgressState.Error) {
            delay(4000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = exportState != ExportProgressState.Idle,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.inverseSurface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.15f)),
            shadowElevation = 6.dp,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .widthIn(max = 400.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                when (exportState) {
                    ExportProgressState.Exporting -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Exporting${".".repeat(dotCount)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                fontSize = 14.sp
                            )
                        )
                    }
                    ExportProgressState.Success -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50), // Raw green tick
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Export successful",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                fontSize = 14.sp
                            )
                        )
                    }
                    ExportProgressState.Error -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Error",
                            tint = Color(0xFFEF5350), // Raw red cross
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMessage ?: "Export failed",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                fontSize = 14.sp
                            )
                        )
                    }
                    ExportProgressState.Idle -> {
                        // Empty state
                    }
                }
            }
        }
    }
}
