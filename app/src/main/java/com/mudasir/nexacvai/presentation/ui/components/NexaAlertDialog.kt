package com.mudasir.nexacvai.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mudasir.nexacvai.ui.theme.ErrorRed

@Composable
fun NexaAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String? = null,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String? = "Cancel",
    isDestructive: Boolean = false,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    content: @Composable (() -> Unit)? = null
) {
    val effectiveIcon = icon ?: if (isDestructive) Icons.Outlined.DeleteOutline else null
    val effectiveIconTint = iconTint ?: if (isDestructive) ErrorRed else MaterialTheme.colorScheme.primary
    val iconContainerBg = effectiveIconTint.copy(alpha = 0.12f)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = if (effectiveIcon != null) Alignment.CenterHorizontally else Alignment.Start
            ) {
                // Header Icon Badge if present or if destructive
                if (effectiveIcon != null) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(iconContainerBg)
                            .border(
                                BorderStroke(1.dp, effectiveIconTint.copy(alpha = 0.25f)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = effectiveIcon,
                            contentDescription = null,
                            tint = effectiveIconTint,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = (-0.3).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = if (effectiveIcon != null) TextAlign.Center else TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                // Message Text
                if (message != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        ),
                        textAlign = if (effectiveIcon != null) TextAlign.Center else TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Custom Content Slot
                if (content != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }

                // Action Buttons
                val showButtons = dismissLabel != null || confirmLabel.isNotBlank()
                if (showButtons) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dismissLabel != null) {
                            OutlinedButton(
                                onClick = onDismissRequest,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = dismissLabel,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        if (confirmLabel.isNotBlank()) {
                            val buttonContainerColor = if (isDestructive) {
                                ErrorRed
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                            val buttonContentColor = if (isDestructive) {
                                Color.White
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            }

                            Button(
                                onClick = onConfirm,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = buttonContainerColor,
                                    contentColor = buttonContentColor
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = confirmLabel,
                                    color = buttonContentColor,
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
    }
}

