package com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Project
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.profiles.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCard(
    project: Project,
    index: Int,
    onUpdateProject: (Project) -> Unit,
    onRemoveProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    // Local state for showDay check
    var showDayProj by rememberSaveable(project.id) {
        mutableStateOf(project.startDate.count { it == '/' } == 2 || project.endDate.count { it == '/' } == 2)
    }

    // Local DatePicker / MonthYear dialog visibility
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartMonthYearPicker by remember { mutableStateOf(false) }
    var showEndMonthYearPicker by remember { mutableStateOf(false) }

    // Dialogs localized inside the card
    if (showStartDatePicker) {
        NexaDatePicker(
            initialDateMillis = dateStringToMillis(project.startDate),
            onDismissRequest = { showStartDatePicker = false },
            onDateSelected = { millis ->
                if (millis != null) {
                    val dateStr = millisToDateString(millis, showDayProj)
                    onUpdateProject(project.copy(startDate = dateStr))
                }
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        NexaDatePicker(
            initialDateMillis = dateStringToMillis(project.endDate),
            onDismissRequest = { showEndDatePicker = false },
            onDateSelected = { millis ->
                if (millis != null) {
                    val dateStr = millisToDateString(millis, showDayProj)
                    onUpdateProject(project.copy(endDate = dateStr))
                }
                showEndDatePicker = false
            }
        )
    }

    if (showStartMonthYearPicker) {
        val parsed = remember(project.startDate) { parseMonthAndYear(project.startDate, calendar) }
        MonthYearPickerDialog(
            initialMonth = parsed.first,
            initialYear = parsed.second,
            onDismissRequest = { showStartMonthYearPicker = false },
            onConfirm = { m, y ->
                val formatted = String.format("%02d/%04d", m, y)
                onUpdateProject(project.copy(startDate = formatted))
                showStartMonthYearPicker = false
            }
        )
    }

    if (showEndMonthYearPicker) {
        val parsed = remember(project.endDate) { parseMonthAndYear(project.endDate, calendar) }
        MonthYearPickerDialog(
            initialMonth = parsed.first,
            initialYear = parsed.second,
            onDismissRequest = { showEndMonthYearPicker = false },
            onConfirm = { m, y ->
                val formatted = String.format("%02d/%04d", m, y)
                onUpdateProject(project.copy(endDate = formatted))
                showEndMonthYearPicker = false
            }
        )
    }

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
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (project.projectName.isNotBlank()) project.projectName else "Project #${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                val removeInteractionSource = remember { MutableInteractionSource() }
                val removePressed by removeInteractionSource.collectIsPressedAsState()
                val removeScale by animateFloatAsState(if (removePressed) 0.98f else 1f, label = "removeProjScale")

                IconButton(
                    onClick = onRemoveProject,
                    interactionSource = removeInteractionSource,
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer(scaleX = removeScale, scaleY = removeScale)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Project",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            NexaTextField(
                value = project.projectName,
                onValueChange = { onUpdateProject(project.copy(projectName = it)) },
                label = "Project Name*",
                placeholder = "e.g. Smart Finance Tracker",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            NexaTextField(
                value = project.roleInProject,
                onValueChange = { onUpdateProject(project.copy(roleInProject = it)) },
                label = "Your Role in Project",
                placeholder = "e.g. Lead Android Developer",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            NexaTextField(
                value = project.description,
                onValueChange = { onUpdateProject(project.copy(description = it)) },
                label = "Project Description",
                placeholder = "Describe project goals, outcomes, and your main contributions...",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3,
                maxLines = 4,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )

            var techInput by remember(project.id) { mutableStateOf(project.technologiesUsed.joinToString(", ")) }
            LaunchedEffect(project.technologiesUsed) {
                val currentList = techInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                if (currentList != project.technologiesUsed) {
                    techInput = project.technologiesUsed.joinToString(", ")
                }
            }

            NexaTextField(
                value = techInput,
                onValueChange = {
                    techInput = it
                    val techList = it.split(",")
                        .map { tech -> tech.trim() }
                        .filter { tech -> tech.isNotBlank() }
                    onUpdateProject(project.copy(technologiesUsed = techList))
                },
                label = "Technologies Used",
                placeholder = "e.g. Kotlin, Compose, Hilt, Room (comma separated)",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 2,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )

            NexaTextField(
                value = project.projectLink,
                onValueChange = { onUpdateProject(project.copy(projectLink = it)) },
                label = "Project Link",
                placeholder = "e.g. github.com/username/project",
                leadingIcon = Icons.Default.Link,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Dates Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                        val isChecked = !showDayProj
                        showDayProj = isChecked
                        val newStart = if (isChecked) convertToDayFormat(project.startDate) else convertToMonthFormat(project.startDate)
                        val newEnd = if (isChecked) convertToDayFormat(project.endDate) else convertToMonthFormat(project.endDate)
                        onUpdateProject(project.copy(startDate = newStart, endDate = newEnd))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NexaCheckbox(
                    checked = showDayProj,
                    onCheckedChange = { isChecked ->
                        focusManager.clearFocus()
                        showDayProj = isChecked
                        val newStart = if (isChecked) convertToDayFormat(project.startDate) else convertToMonthFormat(project.startDate)
                        val newEnd = if (isChecked) convertToDayFormat(project.endDate) else convertToMonthFormat(project.endDate)
                        onUpdateProject(project.copy(startDate = newStart, endDate = newEnd))
                    }
                )
                Text(
                    text = "Include day in dates (DD/MM/YYYY)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val isOngoingProj = project.endDate.equals("Present", ignoreCase = true)

            NexaDateTextField(
                value = project.startDate,
                onValueChange = { formatted ->
                    onUpdateProject(project.copy(startDate = formatted))
                },
                label = "Start Date",
                showDay = showDayProj,
                onLeadingIconClick = {
                    if (showDayProj) showStartDatePicker = true else showStartMonthYearPicker = true
                },
                imeAction = if (isOngoingProj) ImeAction.Done else ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                        val nextOngoing = !isOngoingProj
                        onUpdateProject(project.copy(endDate = if (nextOngoing) "Present" else ""))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NexaCheckbox(
                    checked = isOngoingProj,
                    onCheckedChange = { isChecked ->
                        focusManager.clearFocus()
                        onUpdateProject(project.copy(endDate = if (isChecked) "Present" else ""))
                    }
                )
                Text(
                    text = "This is an ongoing project",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (!isOngoingProj) {
                NexaDateTextField(
                    value = project.endDate,
                    onValueChange = { formatted ->
                        onUpdateProject(project.copy(endDate = formatted))
                    },
                    label = "End Date",
                    showDay = showDayProj,
                    onLeadingIconClick = {
                        if (showDayProj) showEndDatePicker = true else showEndMonthYearPicker = true
                    },
                    imeAction = ImeAction.Done,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
