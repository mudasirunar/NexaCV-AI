package com.mudasir.nexacvai.presentation.ui.profiles.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudasir.nexacvai.domain.model.SocialLink
import com.mudasir.nexacvai.presentation.ui.profiles.utils.getSocialPlatformInfo

/**
 * Grouped social links display component for profile cards.
 *
 * Groups links by platform label, preserving the order of first appearance.
 * Shows up to 3 platform icons. Additional platforms go into a +X overflow
 * dropdown sorted by first-appearance order, with all links for each platform
 * grouped together under their platform header.
 *
 * Links added for an already-visible platform (one of the first 3) are merged
 * under that platform's icon tooltip — they never appear in +X overflow.
 */
@Composable
fun UserProfileSocialLinks(
    socialLinks: List<SocialLink>,
    modifier: Modifier = Modifier
) {
    if (socialLinks.isEmpty()) return

    val context = LocalContext.current
    val openBrowser = remember(context) {
        { url: String ->
            try {
                val httpUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    "https://$url"
                } else {
                    url
                }
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(httpUrl))
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Group by platform label, preserving first-appearance order
    val groupedPlatforms = remember(socialLinks) {
        val platformOrder = mutableListOf<String>()
        val platformLinks = mutableMapOf<String, MutableList<SocialLink>>()
        socialLinks.forEach { link ->
            val key = link.label.ifBlank { "Other" }
            if (key !in platformLinks) {
                platformOrder.add(key)
                platformLinks[key] = mutableListOf()
            }
            platformLinks[key]!!.add(link)
        }
        platformOrder.map { key -> key to platformLinks[key]!! }
    }

    val primaryPlatforms = remember(groupedPlatforms) { groupedPlatforms.take(3) }
    val overflowPlatforms = remember(groupedPlatforms) { groupedPlatforms.drop(3) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        primaryPlatforms.forEach { (platformLabel, links) ->
            PlatformIcon(
                platformLabel = platformLabel,
                links = links,
                onLinkClick = openBrowser
            )
        }

        if (overflowPlatforms.isNotEmpty()) {
            val totalOverflowCount = remember(overflowPlatforms) {
                overflowPlatforms.size
            }
            var isOverflowExpanded by remember { mutableStateOf(false) }

            Box {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                        .clickable { isOverflowExpanded = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+$totalOverflowCount",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp
                        )
                    )
                }

                DropdownMenu(
                    expanded = isOverflowExpanded,
                    onDismissRequest = { isOverflowExpanded = false },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    overflowPlatforms.forEach { (platformLabel, links) ->
                        val platformInfo = remember(platformLabel) { getSocialPlatformInfo(platformLabel) }

                        // Platform header
                        DropdownMenuItem(
                            enabled = false,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (platformInfo.iconResId != null) {
                                        Icon(
                                            painter = painterResource(id = platformInfo.iconResId),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .scale(platformInfo.scale)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = platformInfo.defaultIcon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .scale(platformInfo.scale)
                                        )
                                    }
                                    Text(
                                        text = platformLabel,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            },
                            onClick = {},
                            modifier = Modifier.height(28.dp)
                        )

                        // Each link under this platform
                        links.forEach { link ->
                            DropdownMenuItem(
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                text = {
                                    Text(
                                        text = link.url,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        textDecoration = TextDecoration.Underline,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(start = 22.dp)
                                    )
                                },
                                onClick = {
                                    isOverflowExpanded = false
                                    openBrowser(link.url)
                                },
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A single platform icon button that, when tapped, shows a dropdown
 * listing all links belonging to that platform.
 */
@Composable
private fun PlatformIcon(
    platformLabel: String,
    links: List<SocialLink>,
    onLinkClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val platformInfo = remember(platformLabel) { getSocialPlatformInfo(platformLabel) }

    Box {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { isExpanded = true },
            contentAlignment = Alignment.Center
        ) {
            if (platformInfo.iconResId != null) {
                Icon(
                    painter = painterResource(id = platformInfo.iconResId),
                    contentDescription = platformLabel,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(16.dp)
                        .scale(platformInfo.scale)
                )
            } else {
                Icon(
                    imageVector = platformInfo.defaultIcon,
                    contentDescription = platformLabel,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                        .scale(platformInfo.scale)
                )
            }
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .widthIn(max = 240.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // Platform header (non-clickable)
            if (links.size > 1) {
                DropdownMenuItem(
                    enabled = false,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    text = {
                        Text(
                            text = "$platformLabel · ${links.size} links",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    onClick = {},
                    modifier = Modifier.height(28.dp)
                )
            }

            links.forEach { link ->
                DropdownMenuItem(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    text = {
                        Column {
                            if (links.size == 1) {
                                Text(
                                    text = platformLabel,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Text(
                                text = link.url,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    onClick = {
                        isExpanded = false
                        onLinkClick(link.url)
                    },
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}
