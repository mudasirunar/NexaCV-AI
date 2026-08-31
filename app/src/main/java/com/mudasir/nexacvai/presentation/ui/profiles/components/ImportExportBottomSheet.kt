package com.mudasir.nexacvai.presentation.ui.profiles.components

import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import com.mudasir.nexacvai.R
import com.mudasir.nexacvai.core.utils.NameUtils
import com.mudasir.nexacvai.core.utils.ProfileImportExportHelper
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.presentation.ui.components.NexaButton
import com.mudasir.nexacvai.presentation.ui.components.NexaModalBottomSheet
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.DuplicateResolution
import com.mudasir.nexacvai.ui.theme.AvatarColorPairs
import com.mudasir.nexacvai.ui.theme.SheetIconSuccessGreen
import com.mudasir.nexacvai.ui.theme.SheetIconWarningAmber

/**
 * Sealed interface representing the distinct content modes
 * rendered inside the Import/Export bottom sheet.
 */
sealed interface ImportExportSheetContent {
    data class ExportConfirm(val profiles: List<UserProfile>) : ImportExportSheetContent
    data class DuplicateFound(
        val importedProfile: UserProfile,
        val existingName: String,
        val existingId: Long? = null
    ) : ImportExportSheetContent
    data class MultiDuplicateFound(
        val importedProfilesData: List<ProfileImportExportHelper.ImportedProfileData>,
        val existingProfilesMap: Map<Long, String>,
        val existingProfilesByUuid: Map<String, UserProfile> = emptyMap()
    ) : ImportExportSheetContent

    data object Importing : ImportExportSheetContent
    data class ImportSuccess(val count: Int, val mainProfileName: String) : ImportExportSheetContent
}

/**
 * A unified premium Modal Bottom Sheet for all Import/Export interactions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportBottomSheet(
    content: ImportExportSheetContent,
    onExportConfirm: () -> Unit = {},
    onKeepBoth: () -> Unit = {},
    onOverwrite: () -> Unit = {},
    onExecuteMultiImport: (Map<Long, DuplicateResolution>) -> Unit = {},
    onViewProfile: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { sheetValue ->
            sheetValue != SheetValue.Hidden || content !is ImportExportSheetContent.Importing
        }
    )

    NexaModalBottomSheet(
        onDismissRequest = {
            if (content !is ImportExportSheetContent.Importing) {
                onDismiss()
            }
        },
        sheetState = sheetState
    ) {
        val contentKey = remember(content) {
            when (content) {
                is ImportExportSheetContent.ExportConfirm -> "export"
                is ImportExportSheetContent.DuplicateFound -> "duplicate"
                is ImportExportSheetContent.MultiDuplicateFound -> "multi_duplicate"
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
                            profiles = data.profiles,
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
                            existingId = data.existingId,
                            onKeepBoth = onKeepBoth,
                            onOverwrite = onOverwrite,
                            onCancel = onDismiss
                        )
                    }
                    "multi_duplicate" -> {
                        val data = content as? ImportExportSheetContent.MultiDuplicateFound
                            ?: return@AnimatedContent
                        MultiDuplicateResolutionContent(
                            importedProfilesData = data.importedProfilesData,
                            existingProfilesMap = data.existingProfilesMap,
                            existingProfilesByUuid = data.existingProfilesByUuid,
                            onConfirmMultiImport = onExecuteMultiImport,
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
                            count = data.count,
                            mainProfileName = data.mainProfileName,
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

@Composable
private fun ExportConfirmContent(
    profiles: List<UserProfile>,
    onExport: () -> Unit,
    onCancel: () -> Unit
) {
    val isMulti = profiles.size > 1
    val mainProfile = profiles.firstOrNull()

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
        text = if (isMulti) "Export ${profiles.size} Profiles" else "Export Profile",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = if (isMulti) {
            "Package ${profiles.size} profiles and photos into a single secure .nexacv backup bundle."
        } else {
            "Package profile details and photo into a secure .nexacv backup file."
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    if (mainProfile != null) {
        val initials = remember(mainProfile.fullName) {
            NameUtils.getInitials(mainProfile.fullName)
        }
        val colorPair = remember(mainProfile.id) {
            val index = kotlin.math.abs((mainProfile.id % AvatarColorPairs.size).toInt())
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
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colorPair.background),
                    contentAlignment = Alignment.Center
                ) {
                    if (mainProfile.profilePictureUri != null) {
                        val context = LocalContext.current
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(mainProfile.profilePictureUri)
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

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isMulti) "${mainProfile.fullName} and ${profiles.size - 1} others" else mainProfile.fullName.ifBlank { "Untitled Profile" },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (mainProfile.professionalTitle.isNotBlank()) {
                        Text(
                            text = mainProfile.professionalTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    NexaButton(
        onClick = onExport,
        text = if (isMulti) "Export (${profiles.size} Profiles)" else "Export",
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

@Composable
private fun DuplicateResolutionContent(
    importedProfile: UserProfile,
    existingName: String,
    existingId: Long? = null,
    onKeepBoth: () -> Unit,
    onOverwrite: () -> Unit,
    onCancel: () -> Unit
) {
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
        text = "Duplicate Profile Found",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    val profileIdSuffix = if (existingId != null && existingId > 0L) " (Profile #$existingId)" else ""
    Text(
        text = "A profile for \"$existingName\"$profileIdSuffix already exists in your app. How would you like to resolve this?",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

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

@Composable
private fun MultiDuplicateResolutionContent(
    importedProfilesData: List<ProfileImportExportHelper.ImportedProfileData>,
    existingProfilesMap: Map<Long, String>,
    existingProfilesByUuid: Map<String, UserProfile> = emptyMap(),
    onConfirmMultiImport: (Map<Long, DuplicateResolution>) -> Unit,
    onCancel: () -> Unit
) {
    var resolutions by remember(importedProfilesData) {
        mutableStateOf(
            importedProfilesData.associate { data ->
                val isDuplicate = (data.profile.uuid.isNotBlank() && existingProfilesByUuid.containsKey(data.profile.uuid))
                    || existingProfilesMap.containsKey(data.profile.id)
                data.profile.id to if (isDuplicate) DuplicateResolution.KeepBoth else DuplicateResolution.Overwrite
            }
        )
    }

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

    val duplicateCount = importedProfilesData.count { data ->
        (data.profile.uuid.isNotBlank() && existingProfilesByUuid.containsKey(data.profile.uuid))
            || existingProfilesMap.containsKey(data.profile.id)
    }

    Text(
        text = "Batch Import Conflict",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Found ${importedProfilesData.size} profiles ($duplicateCount duplicate${if (duplicateCount > 1) "s" else ""}). Choose how to resolve conflicts.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Bulk Quick Actions Bar
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        NexaButton(
            onClick = {
                resolutions = importedProfilesData.associate { it.profile.id to DuplicateResolution.KeepBoth }
            },
            text = "Keep All",
            modifier = Modifier.weight(1f).height(36.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
            hasBorder = true,
            borderColor = MaterialTheme.colorScheme.primary,
            fillColor = MaterialTheme.colorScheme.primary,
            fillOpacity = 0.14f,
            contentColor = MaterialTheme.colorScheme.primary
        )

        NexaButton(
            onClick = {
                resolutions = importedProfilesData.associate { it.profile.id to DuplicateResolution.Overwrite }
            },
            text = "Overwrite All",
            modifier = Modifier.weight(1f).height(36.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
            hasBorder = true,
            borderColor = MaterialTheme.colorScheme.error,
            fillColor = MaterialTheme.colorScheme.error,
            fillOpacity = 0.14f,
            contentColor = MaterialTheme.colorScheme.error
        )

        NexaButton(
            onClick = {
                resolutions = importedProfilesData.associate { data ->
                    val isDuplicate = (data.profile.uuid.isNotBlank() && existingProfilesByUuid.containsKey(data.profile.uuid))
                        || existingProfilesMap.containsKey(data.profile.id)
                    data.profile.id to if (isDuplicate) DuplicateResolution.Skip else DuplicateResolution.Overwrite
                }
            },
            text = "Skip All",
            modifier = Modifier.weight(1f).height(36.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
            hasBorder = true,
            borderColor = SheetIconWarningAmber,
            fillColor = SheetIconWarningAmber,
            fillOpacity = 0.14f,
            contentColor = SheetIconWarningAmber
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 280.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        importedProfilesData.forEach { data ->
            val profile = data.profile
            val existingProfile = if (profile.uuid.isNotBlank()) existingProfilesByUuid[profile.uuid] else null
            val existingName = existingProfile?.fullName ?: existingProfilesMap[profile.id]
            val currentRes = resolutions[profile.id] ?: DuplicateResolution.Overwrite

            val profileBitmap = remember(data.pictureBytes) {
                if (data.hasPicture && data.pictureBytes != null) {
                    try {
                        BitmapFactory.decodeByteArray(data.pictureBytes, 0, data.pictureBytes.size)
                    } catch (e: Exception) {
                        null
                    }
                } else null
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
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(colorPair.background),
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileBitmap != null) {
                                Image(
                                    bitmap = profileBitmap.asImageBitmap(),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = NameUtils.getInitials(profile.fullName),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colorPair.text
                                    )
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.fullName.ifBlank { "Untitled Profile" },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (existingProfile != null) {
                                Text(
                                    text = "Conflict with: \"${existingProfile.fullName}\" (Profile #${existingProfile.id})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else if (existingName != null) {
                                Text(
                                    text = "Conflict with: \"$existingName\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    text = "New Profile",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SheetIconSuccessGreen,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    if (existingName != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val isKeep = currentRes == DuplicateResolution.KeepBoth
                            NexaButton(
                                onClick = {
                                    resolutions = resolutions.toMutableMap().apply { put(profile.id, DuplicateResolution.KeepBoth) }
                                },
                                text = "Keep",
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                                hasBorder = isKeep,
                                borderWidth = 1.5.dp,
                                borderColor = MaterialTheme.colorScheme.primary,
                                fillColor = if (isKeep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                fillOpacity = if (isKeep) 0.22f else 0.40f,
                                contentColor = if (isKeep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f)
                            )

                            val isOverwrite = currentRes == DuplicateResolution.Overwrite
                            NexaButton(
                                onClick = {
                                    resolutions = resolutions.toMutableMap().apply { put(profile.id, DuplicateResolution.Overwrite) }
                                },
                                text = "Overwrite",
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                                hasBorder = isOverwrite,
                                borderWidth = 1.5.dp,
                                borderColor = MaterialTheme.colorScheme.error,
                                fillColor = if (isOverwrite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                                fillOpacity = if (isOverwrite) 0.22f else 0.40f,
                                contentColor = if (isOverwrite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f)
                            )

                            val isSkip = currentRes == DuplicateResolution.Skip
                            NexaButton(
                                onClick = {
                                    resolutions = resolutions.toMutableMap().apply { put(profile.id, DuplicateResolution.Skip) }
                                },
                                text = "Skip",
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                                hasBorder = isSkip,
                                borderWidth = 1.5.dp,
                                borderColor = SheetIconWarningAmber,
                                fillColor = if (isSkip) SheetIconWarningAmber else MaterialTheme.colorScheme.surfaceVariant,
                                fillOpacity = if (isSkip) 0.22f else 0.40f,
                                contentColor = if (isSkip) SheetIconWarningAmber else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f)
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    val importCount = resolutions.values.count { it != DuplicateResolution.Skip }

    NexaButton(
        onClick = { onConfirmMultiImport(resolutions) },
        text = if (importCount > 0) "Import ($importCount Profiles)" else "Skip All",
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

@Composable
private fun ImportingContent() {
    Spacer(modifier = Modifier.height(8.dp))

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
        text = "Importing Profiles",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Extracting package files and saving profile data…",
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

@Composable
private fun ImportSuccessContent(
    count: Int,
    mainProfileName: String,
    onViewProfile: () -> Unit,
    onDone: () -> Unit
) {
    val isZeroImported = count == 0
    val iconVector = if (isZeroImported) Icons.Default.Info else Icons.Default.Check
    val iconColor = if (isZeroImported) SheetIconWarningAmber else SheetIconSuccessGreen
    val iconBgColor = if (isZeroImported) SheetIconWarningAmber.copy(alpha = 0.12f) else SheetIconSuccessGreen.copy(alpha = 0.12f)
    val titleText = if (isZeroImported) "No Profiles Imported" else "Import Successful"
    val bodyText = when {
        isZeroImported -> "All profile imports were skipped. No changes were made to your workspace."
        count > 1 -> "Successfully imported $count profiles into your workspace."
        else -> "Profile \"${mainProfileName.ifBlank { "Untitled Profile" }}\" has been imported successfully."
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(iconBgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = titleText,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = titleText,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = bodyText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (count != 1) {
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
