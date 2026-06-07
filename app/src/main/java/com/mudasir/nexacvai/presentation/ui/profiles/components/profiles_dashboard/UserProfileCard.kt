package com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard

import com.mudasir.nexacvai.presentation.ui.profiles.components.UserProfileImageDialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.core.utils.DateTimeUtils
import com.mudasir.nexacvai.presentation.ui.components.NexaAlertDialog
import com.mudasir.nexacvai.core.utils.NameUtils
import com.mudasir.nexacvai.ui.theme.AvatarColorPairs
import com.mudasir.nexacvai.ui.theme.IconColorEmail
import com.mudasir.nexacvai.ui.theme.IconColorPhone

/**
 * A highly-optimized, premium Material 3 User Profile Card component.
 *
 * Refactored to adhere strictly to:
 * - Keep Composable files under 500 lines rule.
 * - RULE 8 (Strict Clean Architecture package modularity).
 * - Zero hardcoded Color(...) structures.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserProfileCard(
    profile: UserProfile,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRemovePhotoClick: () -> Unit,
    onPhotoSelected: (String) -> Unit,
    modifier: Modifier = Modifier
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .pointerInput(profile.id) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                    },
                    onTap = { onCardClick() }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                    onAvatarClick = { showFullScreenImage = true }
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
                        
                        if (profile.socialLinks.isNotEmpty()) {
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

            // SECTION 3: CONTEXT CONTROL LAYER (Bottom Row)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Aligned: Relative Update Time & Completion
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

                // Right Aligned: Expandable Action Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AnimatedVisibility(
                        visible = areControlsVisible,
                        enter = fadeIn(animationSpec = tween(200)) + 
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
                        exit = fadeOut(animationSpec = tween(150)) + 
                                slideOutHorizontally(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    targetOffsetX = { it }
                                ) +
                                scaleOut(
                                    animationSpec = tween(150),
                                    targetScale = 0.6f
                                )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                        }
                    }

                    // Rotating Menu Toggle Button
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
    
    // 1. Decode prefix strings and map them cleanly
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
    
    // 2. Format based on type
    return when (selectedType) {
        "Fresh" -> "📊 Fresh Candidate"
        "Months" -> {
            // Strip any legacy/qualifying text if present, only parse the number
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
                    // Smart Month-to-Year Formatting:
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
            // Custom or fallback
            if (valueTrimmed.isBlank()) "" else "📊 $valueTrimmed Experience"
        }
    }
}
