package com.mudasir.nexacvai.presentation.ui.profiles.components.view_profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.core.utils.NameUtils
import com.mudasir.nexacvai.ui.theme.AvatarColorPair

@Composable
fun ProfileHeaderSection(
    profile: UserProfile,
    colorPair: AvatarColorPair,
    initials: String,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large circular profile picture
            val isClickable = profile.profilePictureUri != null
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(colorPair.background)
                    .then(
                        if (isClickable) {
                            Modifier.clickable { onAvatarClick() }
                        } else {
                            Modifier
                        }
                    ),
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
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorPair.text
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = profile.fullName.ifBlank { "Untitled Profile" },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                textAlign = TextAlign.Center
            )
            
            if (profile.professionalTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = profile.professionalTitle,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ContactAndQuickInfoSection(
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Contact & Info",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            // Emails
            val nonBlankEmails = remember(profile.emails) { profile.emails.filter { it.isNotBlank() } }
            InfoRowItem(
                icon = Icons.Default.Email,
                label = "Email",
                values = nonBlankEmails,
                fallbackMessage = "Email address not provided",
                isMandatoryMissing = nonBlankEmails.isEmpty()
            )

            // Phones
            val nonBlankPhones = remember(profile.phones) { profile.phones.filter { it.isNotBlank() } }
            InfoRowItem(
                icon = Icons.Default.Phone,
                label = "Phone",
                values = nonBlankPhones,
                fallbackMessage = "Phone number not provided",
                isMandatoryMissing = nonBlankPhones.isEmpty()
            )

            // Date of Birth
            if (profile.dateOfBirth.isNotBlank()) {
                InfoRowItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Date of Birth",
                    value = profile.dateOfBirth
                )
            }

            // Address
            if (profile.address.isNotBlank()) {
                InfoRowItem(
                    icon = Icons.Default.LocationOn,
                    label = "Address",
                    value = profile.address
                )
            }

            // Experience
            val expText = remember(profile.yearsOfExperience) {
                val exp = profile.yearsOfExperience.trim()
                val (selectedType, valueTrimmed) = when {
                    exp.startsWith("FRESH", ignoreCase = true) || exp.equals("fresh", ignoreCase = true) || exp.equals("none", ignoreCase = true) || exp == "0" -> "Fresh" to ""
                    exp.startsWith("MONTHS:", ignoreCase = true) -> "Months" to exp.substringAfter("MONTHS:").trim()
                    exp.startsWith("YEARS:", ignoreCase = true) -> "Years" to exp.substringAfter("YEARS:").trim()
                    exp.startsWith("CUSTOM:", ignoreCase = true) -> "Custom" to exp.substringAfter("CUSTOM:").trim()
                    exp.contains("month", ignoreCase = true) -> "Months" to exp.filter { it.isDigit() || it == '+' }
                    exp.contains("year", ignoreCase = true) -> "Years" to exp.filter { it.isDigit() || it == '+' }
                    else -> "Years" to exp.filter { it.isDigit() || it == '+' }
                }

                when (selectedType) {
                    "Fresh" -> "Fresh Candidate"
                    "Months" -> {
                        if (valueTrimmed.isNotBlank()) {
                            val numberOnly = valueTrimmed.filter { it.isDigit() }
                            val count = numberOnly.toIntOrNull()
                            if (count != null) {
                                val hasPlus = valueTrimmed.contains("+")
                                val unit = if (count == 1 && !hasPlus) "Month" else "Months"
                                val plusStr = if (hasPlus) "+" else ""
                                "$count$plusStr $unit"
                            } else {
                                "$valueTrimmed Months"
                            }
                        } else ""
                    }
                    "Years" -> {
                        if (valueTrimmed.isNotBlank()) {
                            val numberOnly = valueTrimmed.filter { it.isDigit() }
                            val count = numberOnly.toIntOrNull()
                            if (count != null) {
                                val hasPlus = valueTrimmed.contains("+")
                                val unit = if (count > 1 || hasPlus) "Years" else "Year"
                                val plusStr = if (hasPlus) "+" else ""
                                "$count$plusStr $unit"
                            } else {
                                "$valueTrimmed Years"
                            }
                        } else ""
                    }
                    else -> valueTrimmed
                }
            }

            if (expText.isNotBlank()) {
                InfoRowItem(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    label = "Experience",
                    value = expText
                )
            }
        }
    }
}

@Composable
fun InfoRowItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    fallbackMessage: String = "",
    isMandatoryMissing: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isMandatoryMissing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = value.ifBlank { fallbackMessage },
                style = MaterialTheme.typography.bodyMedium,
                color = if (isMandatoryMissing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isMandatoryMissing) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
fun InfoRowItem(
    icon: ImageVector,
    label: String,
    values: List<String>,
    modifier: Modifier = Modifier,
    fallbackMessage: String = "",
    isMandatoryMissing: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isMandatoryMissing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            if (values.isEmpty()) {
                Text(
                    text = fallbackMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMandatoryMissing) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isMandatoryMissing) FontWeight.Medium else FontWeight.Normal
                )
            } else {
                values.forEach { value ->
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
