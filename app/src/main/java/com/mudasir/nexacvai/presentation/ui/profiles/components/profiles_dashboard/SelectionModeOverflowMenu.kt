package com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectionModeOverflowMenu(
    isExportEnabled: Boolean,
    onExportSelectedClick: () -> Unit,
    onCopySelectedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSelectionMenuExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { isSelectionMenuExpanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Selection Options",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = isSelectionMenuExpanded,
            onDismissRequest = { isSelectionMenuExpanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .width(180.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Export",
                        color = if (isExportEnabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        }
                    )
                },
                enabled = isExportEnabled,
                onClick = {
                    if (isExportEnabled) {
                        isSelectionMenuExpanded = false
                        onExportSelectedClick()
                    }
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = "Copy",
                        color = if (isExportEnabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        }
                    )
                },
                enabled = isExportEnabled,
                onClick = {
                    if (isExportEnabled) {
                        isSelectionMenuExpanded = false
                        onCopySelectedClick()
                    }
                }
            )
        }
    }
}
