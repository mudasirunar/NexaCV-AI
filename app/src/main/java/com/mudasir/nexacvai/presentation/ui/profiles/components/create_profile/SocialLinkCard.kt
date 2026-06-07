package com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.SocialLink
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.core.utils.SUPPORTED_PLATFORMS
import com.mudasir.nexacvai.core.utils.getSocialPlatformInfo
import com.mudasir.nexacvai.core.utils.isStandardPlatform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialLinkCard(
    socialLink: SocialLink,
    index: Int,
    onUpdateSocialLink: (SocialLink) -> Unit,
    onRemoveSocialLink: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val platformInfo = remember(socialLink.label) {
                getSocialPlatformInfo(socialLink.label)
            }
            val platformIconPainter = if (platformInfo.iconResId != null) {
                painterResource(id = platformInfo.iconResId)
            } else {
                androidx.compose.ui.graphics.vector.rememberVectorPainter(image = platformInfo.defaultIcon)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (platformInfo.iconResId != null) {
                        Icon(
                            painter = platformIconPainter,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.Unspecified,
                            modifier = Modifier.size(18.dp).scale(platformInfo.scale)
                        )
                    } else {
                        Icon(
                            painter = platformIconPainter,
                            contentDescription = null,
                            tint = if (isStandardPlatform(socialLink.label)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp).scale(platformInfo.scale)
                        )
                    }
                    Text(
                        text = if (socialLink.label.isNotBlank()) socialLink.label else "Link #${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                val removeLinkInteractionSource = remember { MutableInteractionSource() }
                val removeLinkPressed by removeLinkInteractionSource.collectIsPressedAsState()
                val removeLinkScale by animateFloatAsState(if (removeLinkPressed) 0.98f else 1f, label = "removeLinkScale")

                IconButton(
                    onClick = onRemoveSocialLink,
                    interactionSource = removeLinkInteractionSource,
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer(scaleX = removeLinkScale, scaleY = removeLinkScale)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Link",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            var isExpanded by remember { mutableStateOf(false) }
            var itemWidth by remember { mutableStateOf(0) }
            val density = LocalDensity.current
            val itemWidthDp = with(density) { itemWidth.toDp() }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        itemWidth = coordinates.size.width
                    }
            ) {
                val displayPlatformName = remember(socialLink.label) {
                    if (isStandardPlatform(socialLink.label)) socialLink.label else if (socialLink.label.isBlank()) "" else "Other Platform"
                }

                NexaTextField(
                    value = displayPlatformName,
                    onValueChange = {},
                    label = "Platform / Website*",
                    placeholder = "Select Platform",
                    trailingIcon = {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    enabled = true,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isExpanded = true
                        }
                )

                val dropdownWidthDp = if (itemWidthDp > 0.dp) itemWidthDp.coerceAtMost(400.dp) else 280.dp
                val offsetX = if (itemWidthDp > 400.dp) (itemWidthDp - 400.dp) / 2 else 0.dp

                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    offset = DpOffset(x = offsetX, y = 0.dp),
                    modifier = Modifier
                        .width(dropdownWidthDp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    SUPPORTED_PLATFORMS.forEach { platformName ->
                        val pInfo = getSocialPlatformInfo(platformName)
                        val dIconPainter = if (pInfo.iconResId != null) {
                            painterResource(id = pInfo.iconResId)
                        } else {
                            androidx.compose.ui.graphics.vector.rememberVectorPainter(image = pInfo.defaultIcon)
                        }
                        DropdownMenuItem(
                            leadingIcon = {
                                if (pInfo.iconResId != null) {
                                    Icon(
                                        painter = dIconPainter,
                                        contentDescription = null,
                                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                                        modifier = Modifier.size(18.dp).scale(pInfo.scale)
                                    )
                                } else {
                                    Icon(
                                        painter = dIconPainter,
                                        contentDescription = null,
                                        tint = if (platformName == "Other Platform") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp).scale(pInfo.scale)
                                    )
                                }
                            },
                            text = { Text(platformName, style = MaterialTheme.typography.bodyMedium) },
                            onClick = {
                                isExpanded = false
                                if (platformName == "Other Platform") {
                                    onUpdateSocialLink(socialLink.copy(label = "Other"))
                                } else {
                                    onUpdateSocialLink(socialLink.copy(label = platformName))
                                }
                            }
                        )
                    }
                }
            }

            val isOtherSelected = remember(socialLink.label) {
                socialLink.label.isNotBlank() && !isStandardPlatform(socialLink.label)
            }
            if (isOtherSelected) {
                NexaTextField(
                    value = if (socialLink.label == "Other") "" else socialLink.label,
                    onValueChange = { onUpdateSocialLink(socialLink.copy(label = it)) },
                    label = "Other Platform Name*",
                    placeholder = "e.g. Product Hunt",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            NexaTextField(
                value = socialLink.url,
                onValueChange = { onUpdateSocialLink(socialLink.copy(url = it)) },
                label = "Website URL*",
                placeholder = "e.g. github.com/username",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
