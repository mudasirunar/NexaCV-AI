package com.mudasir.nexacvai.presentation.ui.profiles.steps

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Language
import com.mudasir.nexacvai.domain.model.Reference
import com.mudasir.nexacvai.domain.model.SocialLink
import com.mudasir.nexacvai.presentation.ui.components.NexaButton
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.ReferenceCard
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.LanguageCard
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.SocialLinkCard
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.SocialsExtrasStepState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel

@Composable
fun SocialsExtrasStep(
    state: SocialsExtrasStepState,
    viewModel: CreateProfileViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Step Title
        item {
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
        }

        // ================= REFERENCES SECTION =================
        item {
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

                AnimatedVisibility(
                    visible = state.referencesError != null,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Reference Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = state.referencesError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // ================= REFERENCES ITEMS =================
        if (state.references.isEmpty()) {
            item {
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

                        NexaButton(
                            onClick = { viewModel.addReference(Reference()) },
                            text = "+ Add Reference",
                            hasBorder = true,
                            borderColor = MaterialTheme.colorScheme.primary,
                            fillColor = MaterialTheme.colorScheme.primary,
                            fillOpacity = 0.12f,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        } else {
            itemsIndexed(
                items = state.references,
                key = { _, ref -> ref.id }
            ) { index, ref ->
                ReferenceCard(
                    reference = ref,
                    index = index,
                    onUpdateReference = { updatedRef ->
                        viewModel.updateReference(ref.id, updatedRef)
                    },
                    onRemoveReference = {
                        viewModel.removeReference(ref)
                    }
                )
            }

            item {
                NexaButton(
                    onClick = { viewModel.addReference(Reference()) },
                    text = "+ Add Another Reference",
                    modifier = Modifier.fillMaxWidth(),
                    hasBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    fillColor = MaterialTheme.colorScheme.primary,
                    fillOpacity = 0.08f,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // ================= LANGUAGES SECTION =================
        item {
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

                AnimatedVisibility(
                    visible = state.languagesError != null,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Languages Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = state.languagesError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // ================= LANGUAGES ITEMS =================
        if (state.languages.isEmpty()) {
            item {
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

                        NexaButton(
                            onClick = { viewModel.addLanguage(Language()) },
                            text = "+ Add Language",
                            hasBorder = true,
                            borderColor = MaterialTheme.colorScheme.primary,
                            fillColor = MaterialTheme.colorScheme.primary,
                            fillOpacity = 0.12f,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        } else {
            itemsIndexed(
                items = state.languages,
                key = { _, lang -> lang.id }
            ) { index, lang ->
                LanguageCard(
                    language = lang,
                    index = index,
                    onUpdateLanguage = { updatedLang ->
                        viewModel.updateLanguage(lang.id, updatedLang)
                    },
                    onRemoveLanguage = {
                        viewModel.removeLanguage(lang)
                    }
                )
            }

            item {
                NexaButton(
                    onClick = { viewModel.addLanguage(Language()) },
                    text = "+ Add Another Language",
                    modifier = Modifier.fillMaxWidth(),
                    hasBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    fillColor = MaterialTheme.colorScheme.primary,
                    fillOpacity = 0.08f,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // ================= SOCIAL LINKS SECTION =================
        item {
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

                AnimatedVisibility(
                    visible = state.socialLinksError != null,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Social Links Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = state.socialLinksError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // ================= SOCIAL LINKS ITEMS =================
        if (state.socialLinks.isEmpty()) {
            item {
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

                        NexaButton(
                            onClick = { viewModel.addSocialLink(SocialLink()) },
                            text = "+ Add Social Link",
                            hasBorder = true,
                            borderColor = MaterialTheme.colorScheme.primary,
                            fillColor = MaterialTheme.colorScheme.primary,
                            fillOpacity = 0.12f,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        } else {
            itemsIndexed(
                items = state.socialLinks,
                key = { _, link -> link.id }
            ) { index, link ->
                SocialLinkCard(
                    socialLink = link,
                    index = index,
                    onUpdateSocialLink = { updatedLink ->
                        viewModel.updateSocialLink(link.id, updatedLink)
                    },
                    onRemoveSocialLink = {
                        viewModel.removeSocialLink(link)
                    }
                )
            }

            item {
                NexaButton(
                    onClick = { viewModel.addSocialLink(SocialLink()) },
                    text = "+ Add Another Social Link",
                    modifier = Modifier.fillMaxWidth(),
                    hasBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    fillColor = MaterialTheme.colorScheme.primary,
                    fillOpacity = 0.08f,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // ================= ADDITIONAL INFORMATION SECTION =================
        item {
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
        }

        item {
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
        }

        item {
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
        }

        item {
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
}
