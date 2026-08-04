package com.mudasir.nexacvai.presentation.ui.profiles.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudasir.nexacvai.presentation.ui.components.NexaButton
import com.mudasir.nexacvai.ui.theme.SheetIconSuccessGreen

sealed interface ProfileCopySheetContent {
    object Copying : ProfileCopySheetContent
    data class Success(val count: Int, val profileName: String) : ProfileCopySheetContent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCopySheet(
    content: ProfileCopySheetContent,
    onViewProfile: () -> Unit,
    onDone: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDone,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (content) {
                is ProfileCopySheetContent.Copying -> {
                    Text(
                        text = "Copying...",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                is ProfileCopySheetContent.Success -> {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SheetIconSuccessGreen.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = SheetIconSuccessGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Copy Complete",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (content.count > 1) {
                            "Successfully copied ${content.count} profiles."
                        } else {
                            "Profile \"${content.profileName.ifBlank { "Untitled Profile" }}\" has been copied successfully."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (content.count > 1) {
                        NexaButton(
                            onClick = onDone,
                            text = "Done",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            hasBorder = false,
                            fillColor = MaterialTheme.colorScheme.primary,
                            fillOpacity = 1.0f,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        NexaButton(
                            onClick = onViewProfile,
                            text = "View Profile",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            hasBorder = false,
                            fillColor = MaterialTheme.colorScheme.primary,
                            fillOpacity = 1.0f,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NexaButton(
                            onClick = onDone,
                            text = "Done",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            hasBorder = false,
                            fillColor = Color.Transparent,
                            fillOpacity = 0f,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
