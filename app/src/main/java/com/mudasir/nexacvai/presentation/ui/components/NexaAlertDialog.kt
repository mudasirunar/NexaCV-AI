package com.mudasir.nexacvai.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * A highly-customizable, premium app-native Alert Dialog component.
 * Adheres strictly to the professional minimal identity of NexaCV AI:
 * - Rounded corners (16dp).
 * - Zero hardcoded colors, using pure theme color scheme tokens.
 * - Flat rounded actions (12dp) with support for destructive states.
 */
@Composable
fun NexaAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String? = null,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String? = "Cancel",
    isDestructive: Boolean = false,
    content: @Composable (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Title Layer
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                if (message != null || content != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Message Layer
                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (content != null) {
                    content()
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions Button Bar (Dismiss on left, Confirm on right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (dismissLabel != null) {
                        TextButton(
                            onClick = onDismissRequest,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = dismissLabel,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    val buttonContainerColor = MaterialTheme.colorScheme.primary
                    val buttonContentColor = MaterialTheme.colorScheme.onPrimary

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonContainerColor,
                            contentColor = buttonContentColor
                        ),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            text = confirmLabel,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
