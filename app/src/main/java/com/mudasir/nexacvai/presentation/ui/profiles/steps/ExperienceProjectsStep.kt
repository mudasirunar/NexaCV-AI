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

                        val addFirstInteractionSource = remember { MutableInteractionSource() }
                        val addFirstPressed by addFirstInteractionSource.collectIsPressedAsState()
                        val addFirstScale by animateFloatAsState(if (addFirstPressed) 0.98f else 1f, label = "addExpScale")

                        Button(
                            onClick = { viewModel.addExperience(Experience()) },
                            interactionSource = addFirstInteractionSource,
                            modifier = Modifier.graphicsLayer(scaleX = addFirstScale, scaleY = addFirstScale),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Add Experience")
                        }
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
                val addMoreInteractionSource = remember { MutableInteractionSource() }
                val addMorePressed by addMoreInteractionSource.collectIsPressedAsState()
                val addMoreScale by animateFloatAsState(if (addMorePressed) 0.98f else 1f, label = "addMoreExpScale")

                OutlinedButton(
                    onClick = { viewModel.addExperience(Experience()) },
                    interactionSource = addMoreInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = addMoreScale, scaleY = addMoreScale),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Add Another Experience")
                }
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

                        val addFirstProjInteractionSource = remember { MutableInteractionSource() }
                        val addFirstProjPressed by addFirstProjInteractionSource.collectIsPressedAsState()
                        val addFirstProjScale by animateFloatAsState(if (addFirstProjPressed) 0.98f else 1f, label = "addProjScale")

                        Button(
                            onClick = { viewModel.addProject(Project()) },
                            interactionSource = addFirstProjInteractionSource,
                            modifier = Modifier.graphicsLayer(scaleX = addFirstProjScale, scaleY = addFirstProjScale),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Add Project")
                        }
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
                val addAnotherProjInteractionSource = remember { MutableInteractionSource() }
                val addAnotherProjPressed by addAnotherProjInteractionSource.collectIsPressedAsState()
                val addAnotherProjScale by animateFloatAsState(if (addAnotherProjPressed) 0.98f else 1f, label = "addMoreProjScale")

                OutlinedButton(
                    onClick = { viewModel.addProject(Project()) },
                    interactionSource = addAnotherProjInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = addAnotherProjScale, scaleY = addAnotherProjScale),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Add Another Project")
                }
            }
        }
    }
}
