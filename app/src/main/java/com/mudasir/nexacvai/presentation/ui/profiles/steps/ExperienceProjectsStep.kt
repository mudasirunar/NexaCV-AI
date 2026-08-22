package com.mudasir.nexacvai.presentation.ui.profiles.steps

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Experience
import com.mudasir.nexacvai.domain.model.Project
import com.mudasir.nexacvai.presentation.ui.components.NexaButton
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.ExperienceCard
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.ProjectCard
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ExperienceProjectsStepState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun ExperienceProjectsStep(
    state: ExperienceProjectsStepState,
    viewModel: CreateProfileViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    LaunchedEffect(state.validationTrigger) {
        if (state.validationTrigger > 0L) {
            if (state.experienceError != null || state.projectError != null) {
                focusManager.clearFocus()
                keyboardController?.hide()
                if (state.experienceError != null) {
                    // Scroll to Experience section header where the error banner is displayed
                    listState.animateScrollToItem(1)
                } else if (state.projectError != null) {
                    // Scroll to Project section header where the error banner is displayed
                    val expCount = if (state.experiences.isEmpty()) 1 else state.experiences.size + 1
                    val projectHeaderIndex = 3 + expCount
                    listState.animateScrollToItem(projectHeaderIndex)
                }
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Step Title
        item {
            Column {
                Text(
                    text = "Step 3: Experience & Projects",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Document your career history and key technical/business accomplishments.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ================= WORK EXPERIENCE HEADER =================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Work Experience",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add details for each relevant professional role.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AnimatedVisibility(
                    visible = state.experienceError != null,
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
                                contentDescription = "Experience Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = state.experienceError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // ================= WORK EXPERIENCE ITEMS =================
        if (state.experiences.isEmpty()) {
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
                            text = "No work experience added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        NexaButton(
                            onClick = { viewModel.addExperience(Experience()) },
                            text = "+ Add Experience",
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
                items = state.experiences,
                key = { _, item -> item.id }
            ) { index, experience ->
                ExperienceCard(
                    experience = experience,
                    index = index,
                    onUpdateExperience = { updated -> viewModel.updateExperience(experience.id, updated) },
                    onRemoveExperience = { viewModel.removeExperience(experience) }
                )
            }

            item {
                NexaButton(
                    onClick = { viewModel.addExperience(Experience()) },
                    text = "+ Add Another Experience",
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

        // ================= PROJECTS HEADER =================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Projects",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Showcase personal or professional engineering projects.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AnimatedVisibility(
                    visible = state.projectError != null,
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
                                contentDescription = "Project Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = state.projectError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // ================= PROJECTS ITEMS =================
        if (state.projects.isEmpty()) {
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
                            text = "No projects added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        NexaButton(
                            onClick = { viewModel.addProject(Project()) },
                            text = "+ Add Project",
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
                items = state.projects,
                key = { _, proj -> proj.id }
            ) { index, proj ->
                ProjectCard(
                    project = proj,
                    index = index,
                    onUpdateProject = { updatedProj ->
                        viewModel.updateProject(proj.id, updatedProj)
                    },
                    onRemoveProject = {
                        viewModel.removeProject(proj)
                    }
                )
            }

            item {
                NexaButton(
                    onClick = { viewModel.addProject(Project()) },
                    text = "+ Add Another Project",
                    modifier = Modifier.fillMaxWidth(),
                    hasBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    fillColor = MaterialTheme.colorScheme.primary,
                    fillOpacity = 0.08f,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
