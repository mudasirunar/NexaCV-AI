package com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.R
import com.mudasir.nexacvai.core.utils.DateTimeUtils
import com.mudasir.nexacvai.core.utils.NameUtils
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.presentation.ui.components.NexaAlertDialog
import com.mudasir.nexacvai.presentation.ui.profiles.components.UserProfileImageDialog
import com.mudasir.nexacvai.ui.theme.AvatarColorPairs
import com.mudasir.nexacvai.ui.theme.IconColorEmail
import com.mudasir.nexacvai.ui.theme.IconColorPhone

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserProfileCard(
    profile: UserProfile,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onExportClick: () -> Unit,
    onRemovePhotoClick: () -> Unit,
    onPhotoSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isInSelectionMode: Boolean = false,
    onCardLongClick: () -> Unit = {},
    isHighlighted: Boolean = false,
    onSourceProfileClick: (Long) -> Unit = {},
    onRemoveCopyTagClick: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    var showFullScreenImage by remember { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var areControlsVisible by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "UserProfileCardScale"
    )

    val trimmedName = remember(profile.fullName) { profile.fullName.trim() }
    val initials = remember(trimmedName) { NameUtils.getInitials(trimmedName) }
    val displayTitle = remember(profile.professionalTitle) { profile.professionalTitle.trim() }
    val experienceText = remember(profile.yearsOfExperience) {
        formatExperienceText(profile.yearsOfExperience)
    }
    val relativeTimeText = remember(profile.createdAt, profile.updatedAt) {
        val timeSpan = DateTimeUtils.getRelativeTimeSpanString(profile.updatedAt)
        val isJustCreated = kotlin.math.abs(profile.updatedAt - profile.createdAt) < 1000L
        val prefix = if (isJustCreated) "Created " else "Updated "
        "$prefix$timeSpan"
    }
    val displaySkills = remember(profile.skills) {
        profile.skills.take(4)
    }
    val remainingSkillsCount = remember(profile.skills) {
        (profile.skills.size - 4).coerceAtLeast(0)
    }
    val colorPair = remember(profile.id) {
        val index = kotlin.math.abs((profile.id % AvatarColorPairs.size).toInt())
        AvatarColorPairs[index]
    }

    val primaryEmail = remember(profile.emails) { profile.emails.firstOrNull() ?: "" }
    val primaryPhone = remember(profile.phones) { profile.phones.firstOrNull() ?: "" }

    val completionDetails = remember(profile) {
        val hasBasicInfo = profile.fullName.isNotBlank() &&
                profile.professionalTitle.isNotBlank() &&
                profile.emails.isNotEmpty() &&
                profile.phones.isNotEmpty() &&
                profile.address.isNotBlank()
        
        val pillars = listOf(
            "Basic Info" to hasBasicInfo,
            "Skills" to profile.skills.isNotEmpty(),
            "Experience" to profile.experiences.isNotEmpty(),
            "Projects" to profile.projects.isNotEmpty(),
            "Education" to profile.educations.isNotEmpty()
        )
        val completed = pillars.filter { it.second }.map { it.first }
        val missing = pillars.filter { !it.second }.map { it.first }
        Pair(completed, missing)
    }

    val completionProgress = remember(completionDetails) {
        completionDetails.first.size / 5f
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SpotlightHighlight")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isHighlighted) 1.025f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isHighlighted) 1.0f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlowAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                val finalScale = scale * if (isHighlighted) pulseScale else 1.0f
                scaleX = finalScale
                scaleY = finalScale
            }
            .pointerInput(profile.id, isInSelectionMode) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    },
                    onLongPress = {
                        onCardLongClick()
                    },
                    onTap = {
                        onCardClick()
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                    .compositeOver(MaterialTheme.colorScheme.surface)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isHighlighted) 2.5.dp else if (isSelected) 2.dp else 1.dp,
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.primary.copy(alpha = pulseGlowAlpha)
            } else if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // SECTION 1: HEADLINE IDENTITY LAYER (Top Row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Left Element: Circular Avatar with progress ring
                UserProfileAvatar(
                    profile = profile,
                    colorPair = colorPair,
                    initials = initials,
                    completionProgress = completionProgress,
                    onAvatarClick = { showFullScreenImage = true },
                    isInSelectionMode = isInSelectionMode
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Center-Right Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Row 1: Full Name and Social Links
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = trimmedName.ifBlank { "Untitled Profile" },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (isInSelectionMode) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (isSelected) {
                                    Icons.Default.CheckCircle
                                } else {
                                    Icons.Outlined.RadioButtonUnchecked
                                },
                                contentDescription = if (isSelected) "Selected" else "Not Selected",
                                tint = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        } else if (profile.socialLinks.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            UserProfileSocialLinks(
                                socialLinks = profile.socialLinks
                            )
                        }
                    }
                
                    // Row 2: Professional Title
                    if (displayTitle.isNotBlank()) {
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Row 3: Primary Contact Details layer
                    if (primaryEmail.isNotBlank() || primaryPhone.isNotBlank()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (primaryEmail.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Primary Email",
                                        tint = IconColorEmail,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = primaryEmail,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            if (primaryPhone.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Primary Phone",
                                        tint = IconColorPhone,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = primaryPhone,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                
                    // Row 4: Subtle Statistics Pills Row (Extracted)
                    UserProfileStatPills(
                        profile = profile,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // SECTION 2: CONTENT INVENTORY badge pills (Middle Layout)
            val hasMiddleContent = experienceText.isNotBlank() || displaySkills.isNotEmpty()
            if (hasMiddleContent) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (experienceText.isNotBlank()) {
                        Text(
                            text = experienceText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    
                    if (displaySkills.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            displaySkills.forEach { skill ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = skill,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            if (remainingSkillsCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+$remainingSkillsCount more",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !isInSelectionMode,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(150)),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeOut(animationSpec = tween(150))
            ) {
                Column {
                    // SECTION 3: CONTEXT CONTROL LAYER (Bottom Row)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // 1. Left Aligned Info
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = relativeTimeText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { showCompletionDialog = true }
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Completion Info",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${(completionProgress * 100).toInt()}% Complete",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        // 2. Right Aligned Controls & Chip Layer
                        Row(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val hasCopyTag = (profile.sourceProfileName != null || profile.sourceProfileId != null) && !profile.isCopyTagDismissed
                            val displayCopyName = profile.sourceProfileName ?: "Profile"

                            // Copied Chip with pure Fade + Scale (Zero width measurement impact)
                            AnimatedVisibility(
                                visible = hasCopyTag && !areControlsVisible,
                                enter = fadeIn(animationSpec = tween(150)) + scaleIn(animationSpec = tween(150), initialScale = 0.85f),
                                exit = fadeOut(animationSpec = tween(120)) + scaleOut(animationSpec = tween(120), targetScale = 0.85f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.40f)),
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.clickable {
                                                profile.sourceProfileId?.let { onSourceProfileClick(it) }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copied Profile",
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "Copy: $displayCopyName",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.tertiary
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f),
                                            modifier = Modifier
                                                .clickable { onRemoveCopyTagClick() }
                                                .padding(1.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Dismiss Copy Badge",
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .padding(1.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Action Buttons Row (Edit, Delete, Export)
                            this@Row.AnimatedVisibility(
                                visible = areControlsVisible,
                                enter = fadeIn(animationSpec = tween(150)) +
                                        slideInHorizontally(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            ),
                                            initialOffsetX = { it }
                                        ) +
                                        scaleIn(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            ),
                                            initialScale = 0.6f
                                        ),
                                exit = fadeOut(animationSpec = tween(120)) +
                                        slideOutHorizontally(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            ),
                                            targetOffsetX = { it }
                                        ) +
                                        scaleOut(
                                            animationSpec = tween(120),
                                            targetScale = 0.6f
                                        )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    IconButton(
                                        onClick = onDeleteClick,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Profile",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = onEditClick,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Profile",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = onExportClick,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_export),
                                            contentDescription = "Export Profile",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            val rotationAngle by animateFloatAsState(
                                targetValue = if (areControlsVisible) 90f else 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "toggleButtonRotation"
                            )

                            IconButton(
                                onClick = { areControlsVisible = !areControlsVisible },
                                modifier = Modifier
                                    .size(36.dp)
                                    .graphicsLayer { rotationZ = rotationAngle }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Toggle Actions",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Fullscreen interactive circular PFP Viewer Dialog
    UserProfileImageDialog(
        showFullScreenImage = showFullScreenImage,
        onDismissFullScreen = { showFullScreenImage = false },
        profile = profile,
        colorPair = colorPair,
        onPhotoSelected = onPhotoSelected,
        onRemovePhotoClick = onRemovePhotoClick
    )

    if (showCompletionDialog) {
        NexaAlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            title = "Profile Completion",
            confirmLabel = "Got it",
            onConfirm = { showCompletionDialog = false },
            dismissLabel = null,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = if (completionProgress >= 1.0f) {
                            "Your profile is 100% complete! You have successfully provided all core information to build a strong, ATS-optimized CV."
                        } else {
                            "Your profile is ${(completionProgress * 100).toInt()}% complete. Complete the missing sections below to build a stronger, ATS-optimized CV."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    
                    if (completionDetails.first.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            completionDetails.first.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = com.mudasir.nexacvai.ui.theme.IconColorCert,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (completionDetails.second.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Missing for 100%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            )
                            completionDetails.second.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

/**
 * Dynamically formats the years/months of experience to support singular ("Year"),
 * plural ("Years"), month durations, and "Fresh Candidate" states beautifully.
 */
private fun formatExperienceText(experience: String): String {
    val trimmed = experience.trim()
    if (trimmed.isBlank()) return ""
    
    // Decode prefix strings and map them cleanly
    val (selectedType, rawVal) = when {
        trimmed.startsWith("FRESH", ignoreCase = true) -> "Fresh" to ""
        trimmed.startsWith("MONTHS:", ignoreCase = true) -> "Months" to trimmed.substringAfter("MONTHS:")
        trimmed.startsWith("YEARS:", ignoreCase = true) -> "Years" to trimmed.substringAfter("YEARS:")
        trimmed.startsWith("CUSTOM:", ignoreCase = true) -> "Custom" to trimmed.substringAfter("CUSTOM:")
        // Legacy support mapping
        trimmed.equals("none", ignoreCase = true) || trimmed.equals("fresh", ignoreCase = true) || trimmed == "0" -> "Fresh" to ""
        trimmed.contains("month", ignoreCase = true) -> "Months" to trimmed
        trimmed.contains("year", ignoreCase = true) || trimmed.replace("+", "").trim().toIntOrNull() != null -> "Years" to trimmed
        else -> "Custom" to trimmed
    }
    
    val valueTrimmed = rawVal.trim()
    
    // Format based on type
    return when (selectedType) {
        "Fresh" -> "📊 Fresh Candidate"
        "Months" -> {
            val numberOnly = valueTrimmed.filter { it.isDigit() }
            val count = numberOnly.toIntOrNull()
            if (count == null || count <= 0) {
                if (valueTrimmed.isNotBlank()) "📊 $valueTrimmed Experience" else ""
            } else {
                val hasPlus = valueTrimmed.contains("+")
                if (count < 12) {
                    val unit = if (count == 1 && !hasPlus) "Month" else "Months"
                    val plusStr = if (hasPlus) "+" else ""
                    "📊 $count$plusStr $unit Experience"
                } else {
                    val years = count / 12
                    val remainder = count % 12
                    val isPlural = years > 1 || remainder > 0 || hasPlus
                    val unit = if (isPlural) "Years" else "Year"
                    val suffix = if (remainder > 0 || hasPlus) "+" else ""
                    "📊 $years$suffix $unit Experience"
                }
            }
        }
        "Years" -> {
            val numberOnly = valueTrimmed.filter { it.isDigit() }
            val count = numberOnly.toIntOrNull()
            if (count == null || count < 0) {
                if (valueTrimmed.isNotBlank()) "📊 $valueTrimmed Experience" else ""
            } else {
                val hasPlus = valueTrimmed.contains("+")
                val isPlural = count > 1 || hasPlus
                val unit = if (isPlural) "Years" else "Year"
                val plusStr = if (hasPlus) "+" else ""
                "📊 $count$plusStr $unit Experience"
            }
        }
        else -> {
            if (valueTrimmed.isBlank()) "" else "📊 $valueTrimmed Experience"
        }
    }
}
