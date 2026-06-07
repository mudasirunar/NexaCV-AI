package com.mudasir.nexacvai.presentation.ui.profiles.steps

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Language
import com.mudasir.nexacvai.domain.model.Reference
import com.mudasir.nexacvai.domain.model.SocialLink
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.core.utils.SUPPORTED_PLATFORMS
import com.mudasir.nexacvai.core.utils.getSocialPlatformInfo
import com.mudasir.nexacvai.core.utils.isStandardPlatform
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel

// CEFR proficiency helper
data class ProficiencyLevel(
    val code: String,
    val label: String,
    val description: String
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SocialsExtrasStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    val focusManager = LocalFocusManager.current



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
                text = "Step 5: Social Links & Extras",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add optional details to finish your professional CV profile.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ================= REFERENCES SECTION =================
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "References",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add professionals who can endorse your credentials. This section is optional.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.references.isEmpty()) {
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
                            text = "No references added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val addRefInteractionSource = remember { MutableInteractionSource() }
                        val addRefPressed by addRefInteractionSource.collectIsPressedAsState()
                        val addRefScale by animateFloatAsState(if (addRefPressed) 0.98f else 1f, label = "addRefScale")

                        Button(
                            onClick = { viewModel.addReference(Reference()) },
                            interactionSource = addRefInteractionSource,
                            modifier = Modifier.graphicsLayer(scaleX = addRefScale, scaleY = addRefScale),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Add Reference")
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
                    state.references.forEachIndexed { index, ref ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
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
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (ref.fullName.isNotBlank()) ref.fullName else "Reference #${index + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    val removeInteractionSource = remember { MutableInteractionSource() }
                                    val removePressed by removeInteractionSource.collectIsPressedAsState()
                                    val removeScale by animateFloatAsState(if (removePressed) 0.98f else 1f, label = "removeRefScale")

                                    IconButton(
                                        onClick = { viewModel.removeReference(ref) },
                                        interactionSource = removeInteractionSource,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .graphicsLayer(scaleX = removeScale, scaleY = removeScale)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Reference",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                                NexaTextField(
                                    value = ref.fullName,
                                    onValueChange = { viewModel.updateReference(ref.id, ref.copy(fullName = it)) },
                                    label = "Full Name*",
                                    placeholder = "e.g. John Doe",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NexaTextField(
                                    value = ref.jobTitle,
                                    onValueChange = { viewModel.updateReference(ref.id, ref.copy(jobTitle = it)) },
                                    label = "Job Title*",
                                    placeholder = "e.g. Product Manager",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NexaTextField(
                                    value = ref.company,
                                    onValueChange = { viewModel.updateReference(ref.id, ref.copy(company = it)) },
                                    label = "Company / Organization*",
                                    placeholder = "e.g. Google",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NexaTextField(
                                    value = ref.email ?: "",
                                    onValueChange = { viewModel.updateReference(ref.id, ref.copy(email = it.takeIf { it.isNotBlank() })) },
                                    label = "Email Address",
                                    placeholder = "e.g. reference@company.com",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.None,
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NexaTextField(
                                    value = ref.phone ?: "",
                                    onValueChange = { viewModel.updateReference(ref.id, ref.copy(phone = it.takeIf { it.isNotBlank() })) },
                                    label = "Phone Number",
                                    placeholder = "e.g. +1 234 567 890",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.None,
                                        keyboardType = KeyboardType.Phone,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NexaTextField(
                                    value = ref.linkedInUrl ?: "",
                                    onValueChange = { viewModel.updateReference(ref.id, ref.copy(linkedInUrl = it.takeIf { it.isNotBlank() })) },
                                    label = "LinkedIn URL",
                                    placeholder = "e.g. linkedin.com/in/username",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.None,
                                        keyboardType = KeyboardType.Uri,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NexaTextField(
                                    value = ref.notes ?: "",
                                    onValueChange = { viewModel.updateReference(ref.id, ref.copy(notes = it.takeIf { it.isNotBlank() })) },
                                    label = "Notes",
                                    placeholder = "Any additional context about this reference...",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        imeAction = ImeAction.Done
                                    ),
                                    singleLine = false,
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val addMoreRefInteractionSource = remember { MutableInteractionSource() }
                    val addMoreRefPressed by addMoreRefInteractionSource.collectIsPressedAsState()
                    val addMoreRefScale by animateFloatAsState(if (addMoreRefPressed) 0.98f else 1f, label = "addMoreRefScale")

                    OutlinedButton(
                        onClick = { viewModel.addReference(Reference()) },
                        interactionSource = addMoreRefInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(scaleX = addMoreRefScale, scaleY = addMoreRefScale),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Add Another Reference")
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
                    imageVector = Icons.Default.Public,
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
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
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
                                            imageVector = Icons.Default.Public,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (lang.languageName.isNotBlank()) lang.languageName else "Language #${index + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
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

                                var isProfExpanded by remember { mutableStateOf(false) }
                                var profItemWidth by remember { mutableStateOf(0) }
                                val density = LocalDensity.current
                                val profItemWidthDp = with(density) { profItemWidth.toDp() }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            profItemWidth = coordinates.size.width
                                        }
                                ) {
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

                                    val profDropdownWidthDp = if (profItemWidthDp > 0.dp) profItemWidthDp.coerceAtMost(400.dp) else 280.dp
                                    val profOffsetX = if (profItemWidthDp > 400.dp) (profItemWidthDp - 400.dp) / 2 else 0.dp

                                    DropdownMenu(
                                        expanded = isProfExpanded,
                                        onDismissRequest = { isProfExpanded = false },
                                        offset = DpOffset(x = profOffsetX, y = 0.dp),
                                        modifier = Modifier
                                            .width(profDropdownWidthDp)
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        proficiencyLevels.forEachIndexed { i, level ->
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
                                            if (i < proficiencyLevels.lastIndex) {
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

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 8.dp)
        )

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
                text = "Add portfolio links (GitHub, LinkedIn, Portfolio, Behance, etc.).",
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
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                val platformInfo = remember(link.label) {
                                    getSocialPlatformInfo(link.label)
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
                                                tint = if (isStandardPlatform(link.label)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp).scale(platformInfo.scale)
                                            )
                                        }
                                        Text(
                                            text = if (link.label.isNotBlank()) link.label else "Link #${index + 1}",
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
                                    val displayPlatformName = remember(link.label) {
                                        if (isStandardPlatform(link.label)) link.label else if (link.label.isBlank()) "" else "Other Platform"
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
                                                        viewModel.updateSocialLink(link.id, link.copy(label = "Other"))
                                                    } else {
                                                        viewModel.updateSocialLink(link.id, link.copy(label = platformName))
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                val isOtherSelected = remember(link.label) {
                                    link.label.isNotBlank() && !isStandardPlatform(link.label)
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

        // ================= ADDITIONAL INFORMATION SECTION =================
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Extras & Interests",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Card 1: Hobbies & Interests
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Hobbies & Interests",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                NexaTextField(
                    value = state.hobbies,
                    onValueChange = { viewModel.updateAdditionalInfo(hobbies = it) },
                    label = "Hobbies & Interests",
                    placeholder = "e.g. Photography, Reading Technical Blogs, Playing Football...",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }

        // Card 2: Volunteer Work
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Volunteer Work",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                NexaTextField(
                    value = state.volunteerWork,
                    onValueChange = { viewModel.updateAdditionalInfo(volunteerWork = it) },
                    label = "Volunteer Work & Contributions",
                    placeholder = "Describe any volunteering or open-source community contributions...",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }

        // Card 3: Honors & Awards
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Honors & Awards",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                NexaTextField(
                    value = state.awards,
                    onValueChange = { viewModel.updateAdditionalInfo(awards = it) },
                    label = "Honors & Awards",
                    placeholder = "List key professional milestones, hackathon wins, or academic honors...",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )
            }
        }
    }
}
