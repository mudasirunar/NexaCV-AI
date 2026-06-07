package com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.R
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.presentation.ui.components.NexaAlertDialog
import com.mudasir.nexacvai.ui.theme.ErrorRed

@Composable
fun ProfileSelectionBottomBar(
    isSelectionModeActive: Boolean,
    selectedProfileIds: Set<Long>,
    allProfiles: List<UserProfile>,
    onToggleSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isSelectionModeActive,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        val isAllSelected = selectedProfileIds.size == allProfiles.size && allProfiles.isNotEmpty()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Intercept clicks inside the card area to prevent pass-through */ }
                    ),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onToggleSelectAll,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isAllSelected) R.drawable.ic_deselect_all else R.drawable.ic_select_all
                            ),
                            contentDescription = if (isAllSelected) "Deselect All" else "Select All"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAllSelected) "Deselect All" else "Select All",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    var showBulkDeleteDialog by remember { mutableStateOf(false) }
                    val isDeleteEnabled = selectedProfileIds.isNotEmpty()

                    Button(
                        onClick = { showBulkDeleteDialog = true },
                        enabled = isDeleteEnabled,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Selected",
                            tint = if (isDeleteEnabled) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDeleteEnabled) "Delete (${selectedProfileIds.size})" else "Delete",
                            color = if (isDeleteEnabled) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    if (showBulkDeleteDialog && isDeleteEnabled) {
                        val count = selectedProfileIds.size
                        val titleText = if (count == 1) "Delete Profile?" else "Delete $count Profiles?"
                        val messageText = if (count == 1) {
                            val singleProfile = allProfiles.find { it.id in selectedProfileIds }
                            "Are you sure you want to permanently delete the profile for \"${singleProfile?.fullName.orEmpty().ifBlank { "Untitled Profile" }}\"?"
                        } else {
                            "Are you sure you want to permanently delete the $count selected profiles?"
                        }

                        NexaAlertDialog(
                            onDismissRequest = { showBulkDeleteDialog = false },
                            title = titleText,
                            message = messageText,
                            confirmLabel = "Delete",
                            onConfirm = {
                                showBulkDeleteDialog = false
                                onDeleteSelected()
                            },
                            dismissLabel = "Cancel",
                            isDestructive = true
                        )
                    }
                }
            }
        }
    }
}
