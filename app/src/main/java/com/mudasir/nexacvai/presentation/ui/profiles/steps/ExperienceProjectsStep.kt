package com.mudasir.nexacvai.presentation.ui.profiles.steps

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Work
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

@Composable
fun ExperienceProjectsStep(
    state: ExperienceProjectsStepState,
    viewModel: CreateProfileViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
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
                key = { _, exp -> exp.id }
            ) { index, exp ->
                ExperienceCard(
                    experience = exp,
                    index = index,
                    onUpdateExperience = { updatedExp ->
                        viewModel.updateExperience(exp.id, updatedExp)
                    },
                    onRemoveExperience = {
                        viewModel.removeExperience(exp)
                    }
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
