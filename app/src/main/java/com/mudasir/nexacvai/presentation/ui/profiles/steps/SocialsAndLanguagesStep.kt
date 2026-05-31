package com.mudasir.nexacvai.presentation.ui.profiles.steps

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Language
import com.mudasir.nexacvai.domain.model.SocialLink
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import androidx.compose.ui.res.painterResource
import com.mudasir.nexacvai.R

// Define platform data model containing official brand styling fallback
data class PlatformItem(
    val name: String,
    val iconResId: Int?,
    val defaultIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val scale: Float = 1.0f
)

// Define CEFR language proficiency levels
data class ProficiencyLevel(
    val code: String,
    val label: String,
    val description: String
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SocialLinksStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    // Official Brand mappings using standard icons
    val platformsList = remember {
        listOf(
            PlatformItem("LinkedIn", R.drawable.ic_linkedin, Icons.Default.Share, 1.2f),
            PlatformItem("GitHub", R.drawable.ic_github, Icons.Default.Code, 1.0f),
            PlatformItem("Behance", R.drawable.ic_behance, Icons.Default.Brush, 1.2f),
            PlatformItem("Dribbble", R.drawable.ic_dribble, Icons.Default.Palette, 1.0f),
            PlatformItem("Stack Overflow", R.drawable.ic_stackoverflow, Icons.Default.QuestionAnswer, 0.95f),
            PlatformItem("X", R.drawable.ic_x, Icons.Default.AlternateEmail, 1.7f),
            PlatformItem("Personal Portfolio", null, Icons.Default.Language, 1.0f),
            PlatformItem("Other Platform", null, Icons.Default.Link, 1.0f)
        )
    }

    val proficiencyLevels = remember {
        listOf(
            ProficiencyLevel("A1", "Beginner", "Can understand basic daily phrases"),
            ProficiencyLevel("A2", "Elementary", "Can speak simple sentences on familiar topics"),
            ProficiencyLevel("B1", "Intermediate", "Can communicate in everyday work situations"),
            ProficiencyLevel("B2", "Upper-Intermediate", "Can converse fluently on complex subjects"),
            ProficiencyLevel("C1", "Advanced / Fluent", "Can express ideas spontaneously and flexibly"),
            ProficiencyLevel("C2", "Proficient / Native", "Can speak and write with complete native ease")
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Step Title
        Column {
            Text(
                text = "Step 8: Social Links & Languages",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add your professional portfolios and languages you speak.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ================= SOCIAL LINKS SECTION =================
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Social Links & Portfolios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Links to GitHub, LinkedIn, Behance, or your personal website.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.socialLinks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No social links added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val addLinkInteractionSource = remember { MutableInteractionSource() }
                        val addLinkPressed by addLinkInteractionSource.collectIsPressedAsState()
                        val addLinkScale by animateFloatAsState(if (addLinkPressed) 0.98f else 1f, label = "addLinkScale")

                        Button(
                            onClick = { viewModel.addSocialLink(SocialLink()) },
                            interactionSource = addLinkInteractionSource,
                            modifier = Modifier.graphicsLayer(scaleX = addLinkScale, scaleY = addLinkScale),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Add Social Link")
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.socialLinks.forEachIndexed { index, link ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Card Header containing Brand Icon
                                val platformInfo = remember(link.label) {
                                    platformsList.find { it.name == link.label }
                                        ?: platformsList.last() // Other Platform
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
                                                tint = androidx.compose.ui.graphics.Color.Unspecified, // Preserve SVG colors
                                                modifier = Modifier.size(18.dp).scale(platformInfo.scale)
                                            )
                                        } else {
                                            Icon(
                                                painter = platformIconPainter,
                                                contentDescription = null,
                                                tint = if (platformInfo.name == "Other Platform") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp).scale(platformInfo.scale)
                                            )
                                        }
                                        Text(
                                            text = if (link.label.isNotBlank()) link.label else "Link #${index + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 3,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    val removeLinkInteractionSource = remember { MutableInteractionSource() }
                                    val removeLinkPressed by removeLinkInteractionSource.collectIsPressedAsState()
                                    val removeLinkScale by animateFloatAsState(if (removeLinkPressed) 0.98f else 1f, label = "removeLinkScale")

                                    IconButton(
                                        onClick = { viewModel.removeSocialLink(link) },
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

                                // Platform Dropdown with Brand icons
                                var isExpanded by remember { mutableStateOf(false) }
                                
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    val displayPlatformName = remember(link.label) {
                                        val isPrefilled = platformsList.any { it.name == link.label && it.name != "Other Platform" }
                                        if (isPrefilled) link.label else if (link.label.isBlank()) "" else "Other Platform"
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

                                    DropdownMenu(
                                        expanded = isExpanded,
                                        onDismissRequest = { isExpanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        platformsList.forEach { platformItem ->
                                            val dropdownIconPainter = if (platformItem.iconResId != null) {
                                                painterResource(id = platformItem.iconResId)
                                            } else {
                                                androidx.compose.ui.graphics.vector.rememberVectorPainter(image = platformItem.defaultIcon)
                                            }
                                            DropdownMenuItem(
                                                leadingIcon = {
                                                    if (platformItem.iconResId != null) {
                                                        Icon(
                                                            painter = dropdownIconPainter,
                                                            contentDescription = null,
                                                            tint = androidx.compose.ui.graphics.Color.Unspecified, // Preserve SVG colors
                                                            modifier = Modifier.size(18.dp).scale(platformItem.scale)
                                                        )
                                                    } else {
                                                        Icon(
                                                            painter = dropdownIconPainter,
                                                            contentDescription = null,
                                                            tint = if (platformItem.name == "Other Platform") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(18.dp).scale(platformItem.scale)
                                                        )
                                                    }
                                                },
                                                text = { Text(platformItem.name, style = MaterialTheme.typography.bodyMedium) },
                                                onClick = {
                                                    isExpanded = false
                                                    if (platformItem.name == "Other Platform") {
                                                        viewModel.updateSocialLink(link.id, link.copy(label = "Other"))
                                                    } else {
                                                        viewModel.updateSocialLink(link.id, link.copy(label = platformItem.name))
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                // Secondary Other Platform input if Other selected
                                val isOtherSelected = remember(link.label) {
                                    link.label.isNotBlank() && platformsList.none { it.name == link.label && it.name != "Other Platform" }
                                }
                                if (isOtherSelected) {
                                    NexaTextField(
                                        value = if (link.label == "Other") "" else link.label,
                                        onValueChange = { viewModel.updateSocialLink(link.id, link.copy(label = it)) },
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

                                // Link URL Field
                                NexaTextField(
                                    value = link.url,
                                    onValueChange = { viewModel.updateSocialLink(link.id, link.copy(url = it)) },
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

                    Spacer(modifier = Modifier.height(4.dp))

                    val addMoreLinkInteractionSource = remember { MutableInteractionSource() }
                    val addMoreLinkPressed by addMoreLinkInteractionSource.collectIsPressedAsState()
                    val addMoreLinkScale by animateFloatAsState(if (addMoreLinkPressed) 0.98f else 1f, label = "addMoreLinkScale")

                    OutlinedButton(
                        onClick = { viewModel.addSocialLink(SocialLink()) },
                        interactionSource = addMoreLinkInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(scaleX = addMoreLinkScale, scaleY = addMoreLinkScale),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Add Another Social Link")
                    }
                }
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // ================= LANGUAGES SECTION =================
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Public, // Modern outline web icon
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Languages",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add languages you speak and your proficiency levels.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.languages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No languages added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val addLangInteractionSource = remember { MutableInteractionSource() }
                        val addLangPressed by addLangInteractionSource.collectIsPressedAsState()
                        val addLangScale by animateFloatAsState(if (addLangPressed) 0.98f else 1f, label = "addLangScale")

                        Button(
                            onClick = { viewModel.addLanguage(Language()) },
                            interactionSource = addLangInteractionSource,
                            modifier = Modifier.graphicsLayer(scaleX = addLangScale, scaleY = addLangScale),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Add Language")
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.languages.forEachIndexed { index, lang ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Card Header
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
                                        Icon(
                                            imageVector = Icons.Default.Public, // Modern outline web icon
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (lang.languageName.isNotBlank()) lang.languageName else "Language #${index + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 3,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    val removeLangInteractionSource = remember { MutableInteractionSource() }
                                    val removeLangPressed by removeLangInteractionSource.collectIsPressedAsState()
                                    val removeLangScale by animateFloatAsState(if (removeLangPressed) 0.98f else 1f, label = "removeLangScale")

                                    IconButton(
                                        onClick = { viewModel.removeLanguage(lang) },
                                        interactionSource = removeLangInteractionSource,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .graphicsLayer(scaleX = removeLangScale, scaleY = removeLangScale)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Language",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                                // Language Name input
                                NexaTextField(
                                    value = lang.languageName,
                                    onValueChange = { viewModel.updateLanguage(lang.id, lang.copy(languageName = it)) },
                                    label = "Language Name*",
                                    placeholder = "e.g. English, Urdu, German",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Level-wise CEFR Language Proficiency Dropdown
                                var isProfExpanded by remember { mutableStateOf(false) }

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    NexaTextField(
                                        value = lang.proficiency,
                                        onValueChange = {},
                                        label = "Proficiency*",
                                        placeholder = "Select Proficiency Level",
                                        trailingIcon = {
                                            Icon(
                                                imageVector = if (isProfExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
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
                                                isProfExpanded = true
                                            }
                                    )

                                    DropdownMenu(
                                        expanded = isProfExpanded,
                                        onDismissRequest = { isProfExpanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        proficiencyLevels.forEachIndexed { index, level ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 4.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            Badge(
                                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                            ) {
                                                                Text(
                                                                    text = level.code,
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    fontWeight = FontWeight.Bold,
                                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                            Text(
                                                                text = level.label,
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onSurface
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = level.description,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    isProfExpanded = false
                                                    viewModel.updateLanguage(lang.id, lang.copy(proficiency = "${level.code} - ${level.label}"))
                                                }
                                            )
                                            if (index < proficiencyLevels.lastIndex) {
                                                HorizontalDivider(
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val addMoreLangInteractionSource = remember { MutableInteractionSource() }
                    val addMoreLangPressed by addMoreLangInteractionSource.collectIsPressedAsState()
                    val addMoreLangScale by animateFloatAsState(if (addMoreLangPressed) 0.98f else 1f, label = "addMoreLangScale")

                    OutlinedButton(
                        onClick = { viewModel.addLanguage(Language()) },
                        interactionSource = addMoreLangInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(scaleX = addMoreLangScale, scaleY = addMoreLangScale),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Add Another Language")
                    }
                }
            }
        }
    }
}
