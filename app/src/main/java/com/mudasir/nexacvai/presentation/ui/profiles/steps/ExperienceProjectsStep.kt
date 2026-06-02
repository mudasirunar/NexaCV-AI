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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.mudasir.nexacvai.domain.model.Experience
import com.mudasir.nexacvai.domain.model.Project
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.utils.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExperienceProjectsStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    val includeDayExpMap = remember { mutableStateMapOf<String, Boolean>() }
    val includeDayProjMap = remember { mutableStateMapOf<String, Boolean>() }

    // HOISTED DIALOG STATES FOR EXPERIENCES
    var activeStartCalendarExpId by remember { mutableStateOf<String?>(null) }
    var activeEndCalendarExpId by remember { mutableStateOf<String?>(null) }
    var activeStartMonthYearExpId by remember { mutableStateOf<String?>(null) }
    var activeEndMonthYearExpId by remember { mutableStateOf<String?>(null) }

    // HOISTED DATE PICKER DIALOG STATES FOR PROJECTS
    var activeStartCalendarProjId by remember { mutableStateOf<String?>(null) }
    var activeEndCalendarProjId by remember { mutableStateOf<String?>(null) }
    var activeStartMonthYearProjId by remember { mutableStateOf<String?>(null) }
    var activeEndMonthYearProjId by remember { mutableStateOf<String?>(null) }

    // ================= EXPERIENCES DIALOGS =================
    if (activeStartCalendarExpId != null) {
        val exp = state.experiences.find { it.id == activeStartCalendarExpId }
        if (exp != null) {
            val showDay = includeDayExpMap[exp.id] ?: (exp.startDate.count { it == '/' } == 2 || exp.endDate.count { it == '/' } == 2)
            com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaDatePicker(
                initialDateMillis = dateStringToMillis(exp.startDate),
                onDismissRequest = { activeStartCalendarExpId = null },
                onDateSelected = { millis ->
                    if (millis != null) {
                        val dateStr = millisToDateString(millis, showDay)
                        viewModel.updateExperience(exp.id, exp.copy(startDate = dateStr))
                    }
                    activeStartCalendarExpId = null
                }
            )
        }
    }

    if (activeEndCalendarExpId != null) {
        val exp = state.experiences.find { it.id == activeEndCalendarExpId }
        if (exp != null) {
            val showDay = includeDayExpMap[exp.id] ?: (exp.startDate.count { it == '/' } == 2 || exp.endDate.count { it == '/' } == 2)
            com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaDatePicker(
                initialDateMillis = dateStringToMillis(exp.endDate),
                onDismissRequest = { activeEndCalendarExpId = null },
                onDateSelected = { millis ->
                    if (millis != null) {
                        val dateStr = millisToDateString(millis, showDay)
                        viewModel.updateExperience(exp.id, exp.copy(endDate = dateStr))
                    }
                    activeEndCalendarExpId = null
                }
            )
        }
    }

    if (activeStartMonthYearExpId != null) {
        val exp = state.experiences.find { it.id == activeStartMonthYearExpId }
        if (exp != null) {
            val parsed = remember(exp.startDate) { parseMonthAndYear(exp.startDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeStartMonthYearExpId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateExperience(exp.id, exp.copy(startDate = formatted))
                    activeStartMonthYearExpId = null
                }
            )
        }
    }

    if (activeEndMonthYearExpId != null) {
        val exp = state.experiences.find { it.id == activeEndMonthYearExpId }
        if (exp != null) {
            val parsed = remember(exp.endDate) { parseMonthAndYear(exp.endDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeEndMonthYearExpId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateExperience(exp.id, exp.copy(endDate = formatted))
                    activeEndMonthYearExpId = null
                }
            )
        }
    }

    // ================= PROJECTS DIALOGS =================
    if (activeStartCalendarProjId != null) {
        val proj = state.projects.find { it.id == activeStartCalendarProjId }
        if (proj != null) {
            val showDay = includeDayProjMap[proj.id] ?: (proj.startDate.count { it == '/' } == 2 || proj.endDate.count { it == '/' } == 2)
            com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaDatePicker(
                initialDateMillis = dateStringToMillis(proj.startDate),
                onDismissRequest = { activeStartCalendarProjId = null },
                onDateSelected = { millis ->
                    if (millis != null) {
                        val dateStr = millisToDateString(millis, showDay)
                        viewModel.updateProject(proj.id, proj.copy(startDate = dateStr))
                    }
                    activeStartCalendarProjId = null
                }
            )
        }
    }

    if (activeEndCalendarProjId != null) {
        val proj = state.projects.find { it.id == activeEndCalendarProjId }
        if (proj != null) {
            val showDay = includeDayProjMap[proj.id] ?: (proj.startDate.count { it == '/' } == 2 || proj.endDate.count { it == '/' } == 2)
            com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaDatePicker(
                initialDateMillis = dateStringToMillis(proj.endDate),
                onDismissRequest = { activeEndCalendarProjId = null },
                onDateSelected = { millis ->
                    if (millis != null) {
                        val dateStr = millisToDateString(millis, showDay)
                        viewModel.updateProject(proj.id, proj.copy(endDate = dateStr))
                    }
                    activeEndCalendarProjId = null
                }
            )
        }
    }

    if (activeStartMonthYearProjId != null) {
        val proj = state.projects.find { it.id == activeStartMonthYearProjId }
        if (proj != null) {
            val parsed = remember(proj.startDate) { parseMonthAndYear(proj.startDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeStartMonthYearProjId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateProject(proj.id, proj.copy(startDate = formatted))
                    activeStartMonthYearProjId = null
                }
            )
        }
    }

    if (activeEndMonthYearProjId != null) {
        val proj = state.projects.find { it.id == activeEndMonthYearProjId }
        if (proj != null) {
            val parsed = remember(proj.endDate) { parseMonthAndYear(proj.endDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeEndMonthYearProjId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateProject(proj.id, proj.copy(endDate = formatted))
                    activeEndMonthYearProjId = null
                }
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Step Title
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

        // ================= WORK EXPERIENCE SECTION =================
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
            Spacer(modifier = Modifier.height(12.dp))

            if (state.experiences.isEmpty()) {
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.experiences.forEachIndexed { index, exp ->
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
                                            imageVector = Icons.Default.Work,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (exp.jobTitle.isNotBlank()) exp.jobTitle else "Experience #${index + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    val removeInteractionSource = remember { MutableInteractionSource() }
                                    val removePressed by removeInteractionSource.collectIsPressedAsState()
                                    val removeScale by animateFloatAsState(if (removePressed) 0.98f else 1f, label = "removeExpScale")

                                    IconButton(
                                        onClick = { viewModel.removeExperience(exp) },
                                        interactionSource = removeInteractionSource,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .graphicsLayer(scaleX = removeScale, scaleY = removeScale)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Role",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                                NexaTextField(
                                    value = exp.jobTitle,
                                    onValueChange = { viewModel.updateExperience(exp.id, exp.copy(jobTitle = it)) },
                                    label = "Job Title*",
                                    placeholder = "e.g. Senior Android Engineer",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NexaTextField(
                                    value = exp.companyName,
                                    onValueChange = { viewModel.updateExperience(exp.id, exp.copy(companyName = it)) },
                                    label = "Company Name*",
                                    placeholder = "e.g. Google",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Dates Input Area
                                val showDay = includeDayExpMap[exp.id] ?: (exp.startDate.count { it == '/' } == 2 || exp.endDate.count { it == '/' } == 2)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            focusManager.clearFocus()
                                            val isChecked = !showDay
                                            includeDayExpMap[exp.id] = isChecked
                                            val newStart = if (isChecked) convertToDayFormat(exp.startDate) else convertToMonthFormat(exp.startDate)
                                            val newEnd = if (isChecked) convertToDayFormat(exp.endDate) else convertToMonthFormat(exp.endDate)
                                            viewModel.updateExperience(exp.id, exp.copy(startDate = newStart, endDate = newEnd))
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NexaCheckbox(
                                        checked = showDay,
                                        onCheckedChange = { isChecked ->
                                            focusManager.clearFocus()
                                            includeDayExpMap[exp.id] = isChecked
                                            val newStart = if (isChecked) convertToDayFormat(exp.startDate) else convertToMonthFormat(exp.startDate)
                                            val newEnd = if (isChecked) convertToDayFormat(exp.endDate) else convertToMonthFormat(exp.endDate)
                                            viewModel.updateExperience(exp.id, exp.copy(startDate = newStart, endDate = newEnd))
                                        }
                                    )
                                    Text(
                                        text = "Include day in dates (DD/MM/YYYY)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                NexaDateTextField(
                                    value = exp.startDate,
                                    onValueChange = { formatted ->
                                        viewModel.updateExperience(exp.id, exp.copy(startDate = formatted))
                                    },
                                    label = "Start Date",
                                    showDay = showDay,
                                    onLeadingIconClick = {
                                        if (showDay) activeStartCalendarExpId = exp.id else activeStartMonthYearExpId = exp.id
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                val isOngoing = exp.endDate.equals("Present", ignoreCase = true)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            focusManager.clearFocus()
                                            val nextOngoing = !isOngoing
                                            viewModel.updateExperience(exp.id, exp.copy(endDate = if (nextOngoing) "Present" else ""))
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NexaCheckbox(
                                        checked = isOngoing,
                                        onCheckedChange = { isChecked ->
                                            focusManager.clearFocus()
                                            viewModel.updateExperience(exp.id, exp.copy(endDate = if (isChecked) "Present" else ""))
                                        }
                                    )
                                    Text(
                                        text = "I currently work here",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (!isOngoing) {
                                    NexaDateTextField(
                                        value = exp.endDate,
                                        onValueChange = { formatted ->
                                            viewModel.updateExperience(exp.id, exp.copy(endDate = formatted))
                                        },
                                        label = "End Date",
                                        showDay = showDay,
                                        onLeadingIconClick = {
                                            if (showDay) activeEndCalendarExpId = exp.id else activeEndMonthYearExpId = exp.id
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                NexaTextField(
                                    value = exp.description,
                                    onValueChange = { viewModel.updateExperience(exp.id, exp.copy(description = it)) },
                                    label = "Description / Achievements",
                                    placeholder = "Describe your daily impact, tech stack used, and core accomplishments...",
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = false,
                                    minLines = 3,
                                    maxLines = 4,
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

                    Spacer(modifier = Modifier.height(4.dp))

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
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // ================= PROJECTS SECTION =================
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
            Spacer(modifier = Modifier.height(12.dp))

            if (state.projects.isEmpty()) {
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.projects.forEachIndexed { index, proj ->
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
                                            text = if (proj.projectName.isNotBlank()) proj.projectName else "Project #${index + 1}",
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
                                        onClick = { viewModel.removeProject(proj) },
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
                                    value = proj.projectName,
                                    onValueChange = { viewModel.updateProject(proj.id, proj.copy(projectName = it)) },
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
                                    value = proj.roleInProject,
                                    onValueChange = { viewModel.updateProject(proj.id, proj.copy(roleInProject = it)) },
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
                                    value = proj.description,
                                    onValueChange = { viewModel.updateProject(proj.id, proj.copy(description = it)) },
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

                                var techInput by remember(proj.id) { mutableStateOf(proj.technologiesUsed.joinToString(", ")) }
                                LaunchedEffect(proj.technologiesUsed) {
                                    val currentList = techInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                    if (currentList != proj.technologiesUsed) {
                                        techInput = proj.technologiesUsed.joinToString(", ")
                                    }
                                }

                                NexaTextField(
                                    value = techInput,
                                    onValueChange = {
                                        techInput = it
                                        val techList = it.split(",")
                                            .map { tech -> tech.trim() }
                                            .filter { tech -> tech.isNotBlank() }
                                        viewModel.updateProject(proj.id, proj.copy(technologiesUsed = techList))
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

                                // Dates Input Area
                                val showDayProj = includeDayProjMap[proj.id] ?: (proj.startDate.count { it == '/' } == 2 || proj.endDate.count { it == '/' } == 2)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            focusManager.clearFocus()
                                            val isChecked = !showDayProj
                                            includeDayProjMap[proj.id] = isChecked
                                            val newStart = if (isChecked) convertToDayFormat(proj.startDate) else convertToMonthFormat(proj.startDate)
                                            val newEnd = if (isChecked) convertToDayFormat(proj.endDate) else convertToMonthFormat(proj.endDate)
                                            viewModel.updateProject(proj.id, proj.copy(startDate = newStart, endDate = newEnd))
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NexaCheckbox(
                                        checked = showDayProj,
                                        onCheckedChange = { isChecked ->
                                            focusManager.clearFocus()
                                            includeDayProjMap[proj.id] = isChecked
                                            val newStart = if (isChecked) convertToDayFormat(proj.startDate) else convertToMonthFormat(proj.startDate)
                                            val newEnd = if (isChecked) convertToDayFormat(proj.endDate) else convertToMonthFormat(proj.endDate)
                                            viewModel.updateProject(proj.id, proj.copy(startDate = newStart, endDate = newEnd))
                                        }
                                    )
                                    Text(
                                        text = "Include day in dates (DD/MM/YYYY)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                NexaDateTextField(
                                    value = proj.startDate,
                                    onValueChange = { formatted ->
                                        viewModel.updateProject(proj.id, proj.copy(startDate = formatted))
                                    },
                                    label = "Start Date",
                                    showDay = showDayProj,
                                    onLeadingIconClick = {
                                        if (showDayProj) activeStartCalendarProjId = proj.id else activeStartMonthYearProjId = proj.id
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                val isOngoingProj = proj.endDate.equals("Present", ignoreCase = true)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            focusManager.clearFocus()
                                            val nextOngoing = !isOngoingProj
                                            viewModel.updateProject(proj.id, proj.copy(endDate = if (nextOngoing) "Present" else ""))
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NexaCheckbox(
                                        checked = isOngoingProj,
                                        onCheckedChange = { isChecked ->
                                            focusManager.clearFocus()
                                            viewModel.updateProject(proj.id, proj.copy(endDate = if (isChecked) "Present" else ""))
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
                                        value = proj.endDate,
                                        onValueChange = { formatted ->
                                            viewModel.updateProject(proj.id, proj.copy(endDate = formatted))
                                        },
                                        label = "End Date",
                                        showDay = showDayProj,
                                        onLeadingIconClick = {
                                            if (showDayProj) activeEndCalendarProjId = proj.id else activeEndMonthYearProjId = proj.id
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                NexaTextField(
                                    value = proj.githubLink,
                                    onValueChange = { viewModel.updateProject(proj.id, proj.copy(githubLink = it)) },
                                    label = "GitHub Link",
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

                                NexaTextField(
                                    value = proj.liveDemoLink,
                                    onValueChange = { viewModel.updateProject(proj.id, proj.copy(liveDemoLink = it)) },
                                    label = "Live Demo URL",
                                    placeholder = "e.g. myproject.app",
                                    leadingIcon = Icons.Default.Link,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.None,
                                        keyboardType = KeyboardType.Uri,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NexaTextField(
                                    value = proj.playStoreLink,
                                    onValueChange = { viewModel.updateProject(proj.id, proj.copy(playStoreLink = it)) },
                                    label = "Play Store Link",
                                    placeholder = "e.g. play.google.com/...",
                                    leadingIcon = Icons.Default.Link,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.None,
                                        keyboardType = KeyboardType.Uri,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { focusManager.clearFocus() }
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

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
}
