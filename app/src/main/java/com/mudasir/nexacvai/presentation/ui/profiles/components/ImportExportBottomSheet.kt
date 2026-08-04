package com.mudasir.nexacvai.presentation.ui.profiles.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.graphics.Color
import com.mudasir.nexacvai.R
import com.mudasir.nexacvai.core.utils.NameUtils
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.presentation.ui.components.NexaButton
import com.mudasir.nexacvai.ui.theme.AvatarColorPairs
import com.mudasir.nexacvai.ui.theme.SheetIconSuccessGreen
import com.mudasir.nexacvai.ui.theme.SheetIconWarningAmber

/**
 * Sealed interface representing the distinct content modes
 * rendered inside the Import/Export bottom sheet.
 */
sealed interface ImportExportSheetContent {
    data class ExportConfirm(val profile: UserProfile) : ImportExportSheetContent
    data class DuplicateFound(
        val importedProfile: UserProfile,
        val existingName: String
    ) : ImportExportSheetContent

    data object Importing : ImportExportSheetContent
    data class ImportSuccess(val profileName: String) : ImportExportSheetContent
}

/**
 * A unified premium Modal Bottom Sheet for all Import/Export interactions.
 *
 * Replaces the 4 separate NexaAlertDialog popups with a single cohesive sheet
 * that transitions between steps using AnimatedContent. Features:
 * - Themed icon circles at the top of each step
 * - Full-width action buttons with clear visual hierarchy
 * - Swipe-to-dismiss support for non-blocking states
 * - Consistent padding, typography, and spacing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportBottomSheet(
    content: ImportExportSheetContent,
    onExportConfirm: () -> Unit = {},
    onKeepBoth: () -> Unit = {},
    onOverwrite: () -> Unit = {},
    onViewProfile: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { sheetValue ->
            // Prevent dismissal by swipe/tap while importing
            sheetValue != SheetValue.Hidden || content !is ImportExportSheetContent.Importing
        }
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (content !is ImportExportSheetContent.Importing) {
                onDismiss()
            }
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            // Subtle centered drag handle pill
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            )
        },
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        modifier = Modifier.widthIn(max = 480.dp)
    ) {
        // Derive a simple key for AnimatedContent to prevent unnecessary re-compositions
        val contentKey = remember(content) {
            when (content) {
                is ImportExportSheetContent.ExportConfirm -> "export"
                is ImportExportSheetContent.DuplicateFound -> "duplicate"
                is ImportExportSheetContent.Importing -> "importing"
                is ImportExportSheetContent.ImportSuccess -> "success"
            }
        }

        AnimatedContent(
            targetState = contentKey,
            transitionSpec = {
                (fadeIn(tween(300)) + slideInVertically(
                    tween(300),
                    initialOffsetY = { it / 6 }
                )).togetherWith(
                    fadeOut(tween(200))
                ) using SizeTransform(clip = true)
            },
            label = "sheetContentTransition"
        ) { key ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 12.dp, bottom = 8.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (key) {
                    "export" -> {
                        val data = content as? ImportExportSheetContent.ExportConfirm
                            ?: return@AnimatedContent
                        ExportConfirmContent(
                            profile = data.profile,
                            onExport = onExportConfirm,
                            onCancel = onDismiss
                        )
                    }
                    "duplicate" -> {
                        val data = content as? ImportExportSheetContent.DuplicateFound
                            ?: return@AnimatedContent
                        DuplicateResolutionContent(
                            importedProfile = data.importedProfile,
                            existingName = data.existingName,
                            onKeepBoth = onKeepBoth,
                            onOverwrite = onOverwrite,
                            onCancel = onDismiss
                        )
                    }
                    "importing" -> {
                        ImportingContent()
                    }
                    "success" -> {
                        val data = content as? ImportExportSheetContent.ImportSuccess
                            ?: return@AnimatedContent
                        ImportSuccessContent(
                            profileName = data.profileName,
                            onViewProfile = onViewProfile,
                            onDone = onDismiss
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Step Contents
// ---------------------------------------------------------------------------

/**
 * Export confirmation step — shows profile info and Export/Cancel actions.
 */
@Composable
private fun ExportConfirmContent(
    profile: UserProfile,
    onExport: () -> Unit,
    onCancel: () -> Unit
) {
    // Icon circle
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_export),
            contentDescription = "Export",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Export Profile",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Package profile details and photo into a secure .nexacv backup file.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Profile info card with avatar
    val initials = remember(profile.fullName) {
        NameUtils.getInitials(profile.fullName)
    }
    val colorPair = remember(profile.id) {
        val index = kotlin.math.abs((profile.id % AvatarColorPairs.size).toInt())
        AvatarColorPairs[index]
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Circular profile picture (40dp)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colorPair.background),
                contentAlignment = Alignment.Center
            ) {
                if (profile.profilePictureUri != null) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(profile.profilePictureUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorPair.text
                        )
                    )
                }
            }

            // Name and title
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = profile.fullName.ifBlank { "Untitled Profile" },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (profile.professionalTitle.isNotBlank()) {
                    Text(
                        text = profile.professionalTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Primary action
    NexaButton(
        onClick = onExport,
        text = "Export",
        icon = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        hasBorder = false,
        fillColor = MaterialTheme.colorScheme.primary,
        fillOpacity = 1.0f,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Cancel action
    NexaButton(
        onClick = onCancel,
        text = "Cancel",
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        hasBorder = false,
        fillColor = Color.Transparent,
        fillOpacity = 0f,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * Duplicate resolution step — shows conflict warning and 3 action choices.
 */
@Composable
private fun DuplicateResolutionContent(
    importedProfile: UserProfile,
    existingName: String,
    onKeepBoth: () -> Unit,
    onOverwrite: () -> Unit,
    onCancel: () -> Unit
) {
    // Warning icon circle
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(SheetIconWarningAmber.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Duplicate Warning",
            tint = SheetIconWarningAmber,
            modifier = Modifier.size(28.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Duplicate Found",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "A profile with ID ${importedProfile.id} (\"$existingName\") already exists. How would you like to resolve this?",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Keep Both — Primary action
    NexaButton(
        onClick = onKeepBoth,
        text = "Keep Both (Import as Copy)",
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        hasBorder = false,
        fillColor = MaterialTheme.colorScheme.primary,
        fillOpacity = 1.0f,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Overwrite — Destructive translucent fill with border
    NexaButton(
        onClick = onOverwrite,
        text = "Overwrite / Replace Existing",
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        hasBorder = true,
        borderColor = MaterialTheme.colorScheme.error,
        fillColor = MaterialTheme.colorScheme.error,
        fillOpacity = 0.16f,
        contentColor = MaterialTheme.colorScheme.error
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Cancel
    NexaButton(
        onClick = onCancel,
        text = "Cancel",
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        hasBorder = false,
        fillColor = Color.Transparent,
        fillOpacity = 0f,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * Import progress step — centered spinner with status text. Non-dismissible.
 */
@Composable
private fun ImportingContent() {
    Spacer(modifier = Modifier.height(8.dp))

    // Progress icon circle
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Importing Profile",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Extracting files and saving profile data…",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    )

    Spacer(modifier = Modifier.height(8.dp))
}

/**
 * Import success step — green check, success message, View Profile + Done.
 */
@Composable
private fun ImportSuccessContent(
    profileName: String,
    onViewProfile: () -> Unit,
    onDone: () -> Unit
) {
    // Success icon circle
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
        text = "Import Successful",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Profile \"${profileName.ifBlank { "Untitled Profile" }}\" has been imported successfully.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    // View Profile — Primary action
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

    // Done
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
