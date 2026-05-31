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
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.utils.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExperiencesStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    // HOISTED DIALOG STATE
    var activeStartCalendarExpId by remember { mutableStateOf<String?>(null) }
    var activeEndCalendarExpId by remember { mutableStateOf<String?>(null) }
    var activeStartMonthYearExpId by remember { mutableStateOf<String?>(null) }
    var activeEndMonthYearExpId by remember { mutableStateOf<String?>(null) }

    // START CALENDAR DIALOG
    if (activeStartCalendarExpId != null) {
        val exp = state.experiences.find { it.id == activeStartCalendarExpId }
        if (exp != null) {
            val showDay = exp.startDate.count { it == '/' } == 2 || exp.endDate.count { it == '/' } == 2
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateStringToMillis(exp.startDate)
            )
            DatePickerDialog(
                onDismissRequest = { activeStartCalendarExpId = null },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateStr = millisToDateString(millis, showDay)
                                viewModel.updateExperience(exp.id, exp.copy(startDate = dateStr))
                            }
                            activeStartCalendarExpId = null
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { activeStartCalendarExpId = null }) { Text("Cancel") }
                },
                shape = RoundedCornerShape(24.dp)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        headlineContentColor = MaterialTheme.colorScheme.primary,
                        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                        todayContentColor = MaterialTheme.colorScheme.primary,
                        todayDateBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }

    // END CALENDAR DIALOG
    if (activeEndCalendarExpId != null) {
        val exp = state.experiences.find { it.id == activeEndCalendarExpId }
        if (exp != null) {
            val showDay = exp.startDate.count { it == '/' } == 2 || exp.endDate.count { it == '/' } == 2
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateStringToMillis(exp.endDate)
            )
            DatePickerDialog(
                onDismissRequest = { activeEndCalendarExpId = null },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateStr = millisToDateString(millis, showDay)
                                viewModel.updateExperience(exp.id, exp.copy(endDate = dateStr))
                            }
                            activeEndCalendarExpId = null
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { activeEndCalendarExpId = null }) { Text("Cancel") }
                },
                shape = RoundedCornerShape(24.dp)
            ) {
                DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface, headlineContentColor = MaterialTheme.colorScheme.primary, selectedDayContainerColor = MaterialTheme.colorScheme.primary, selectedDayContentColor = MaterialTheme.colorScheme.onPrimary, todayContentColor = MaterialTheme.colorScheme.primary, todayDateBorderColor = MaterialTheme.colorScheme.primary))
            }
        }
    }

    // START MONTH YEAR PICKER
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

    // END MONTH YEAR PICKER
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Step 3: Work Experience",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Highlight your career history. Add details for each relevant role directly on the screen.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.experiences.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No work experience added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val addFirstInteractionSource = remember { MutableInteractionSource() }
                    val addFirstPressed by addFirstInteractionSource.collectIsPressedAsState()
                    val addFirstScale by animateFloatAsState(if (addFirstPressed) 0.98f else 1f, label = "addFirstScale")
                    
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
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
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (exp.jobTitle.isNotBlank()) exp.jobTitle else "Experience #${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 3,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                                
                                val removeInteractionSource = remember { MutableInteractionSource() }
                                val removePressed by removeInteractionSource.collectIsPressedAsState()
                                val removeScale by animateFloatAsState(if (removePressed) 0.98f else 1f, label = "removeScale")
                                
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

                            // Employment Type Dropdown
                            var isEmpTypeExpanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                NexaTextField(
                                    value = exp.employmentType,
                                    onValueChange = {},
                                    label = "Employment Type",
                                    placeholder = "Select Employment Type",
                                    trailingIcon = {
                                        Icon(
                                            imageVector = if (isEmpTypeExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
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
                                            isEmpTypeExpanded = true
                                        }
                                )

                                DropdownMenu(
                                    expanded = isEmpTypeExpanded,
                                    onDismissRequest = { isEmpTypeExpanded = false },
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
                                    val empTypes = listOf(
                                        "Full-time" to Icons.Default.Work,
                                        "Part-time" to Icons.Default.AccessTime,
                                        "Internship" to Icons.Default.School,
                                        "Freelance" to Icons.Default.Person
                                    )
                                    empTypes.forEach { (type, icon) ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Text(
                                                        text = type, 
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = FontWeight.Medium
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            },
                                            onClick = {
                                                viewModel.updateExperience(exp.id, exp.copy(employmentType = type))
                                                isEmpTypeExpanded = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                                        )
                                    }
                                }
                            }

                            // Work Mode Dropdown
                            var isWorkModeExpanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                NexaTextField(
                                    value = exp.workMode,
                                    onValueChange = {},
                                    label = "Work Mode",
                                    placeholder = "Select Work Mode",
                                    trailingIcon = {
                                        Icon(
                                            imageVector = if (isWorkModeExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
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
                                            isWorkModeExpanded = true
                                        }
                                )

                                DropdownMenu(
                                    expanded = isWorkModeExpanded,
                                    onDismissRequest = { isWorkModeExpanded = false },
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
                                    val modes = listOf(
                                        "Onsite" to Icons.Default.Business,
                                        "Remote" to Icons.Default.Home,
                                        "Hybrid" to Icons.Default.Domain
                                    )
                                    modes.forEach { (mode, icon) ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Text(
                                                        text = mode, 
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = FontWeight.Medium
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            },
                                            onClick = {
                                                viewModel.updateExperience(exp.id, exp.copy(workMode = mode))
                                                isWorkModeExpanded = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                                        )
                                    }
                                }
                            }

                            NexaTextField(
                                value = exp.location,
                                onValueChange = { viewModel.updateExperience(exp.id, exp.copy(location = it)) },
                                label = "Location",
                                placeholder = "e.g. Mountain View, CA",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            var localIncludeDay by remember(exp.id) { mutableStateOf<Boolean?>(null) }
                            val showDay by remember(localIncludeDay, exp.startDate, exp.endDate) {
                                derivedStateOf {
                                    localIncludeDay ?: (exp.startDate.count { it == '/' } == 2 || exp.endDate.count { it == '/' } == 2)
                                }
                            }

                            // Date format selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        focusManager.clearFocus()
                                        localIncludeDay = !showDay
                                        val newStart = if (!showDay) {
                                            convertToDayFormat(exp.startDate)
                                        } else {
                                            convertToMonthFormat(exp.startDate)
                                        }
                                        val newEnd = if (!showDay) {
                                            convertToDayFormat(exp.endDate)
                                        } else {
                                            convertToMonthFormat(exp.endDate)
                                        }
                                        viewModel.updateExperience(
                                            exp.id,
                                            exp.copy(startDate = newStart, endDate = newEnd)
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NexaCheckbox(
                                    checked = showDay,
                                    onCheckedChange = { isChecked ->
                                        focusManager.clearFocus()
                                        localIncludeDay = isChecked
                                        val newStart = if (isChecked) {
                                            convertToDayFormat(exp.startDate)
                                        } else {
                                            convertToMonthFormat(exp.startDate)
                                        }
                                        val newEnd = if (isChecked) {
                                            convertToDayFormat(exp.endDate)
                                        } else {
                                            convertToMonthFormat(exp.endDate)
                                        }
                                        viewModel.updateExperience(
                                            exp.id,
                                            exp.copy(startDate = newStart, endDate = newEnd)
                                        )
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
                                label = "Start Date*",
                                showDay = showDay,
                                onLeadingIconClick = {
                                    if (showDay) {
                                        activeStartCalendarExpId = exp.id
                                    } else {
                                        activeStartMonthYearExpId = exp.id
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Currently working toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        focusManager.clearFocus()
                                        val isChecked = !exp.isCurrentlyWorking
                                        viewModel.updateExperience(
                                            exp.id, 
                                            exp.copy(
                                                isCurrentlyWorking = isChecked,
                                                endDate = if (isChecked) "Present" else ""
                                            )
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NexaCheckbox(
                                    checked = exp.isCurrentlyWorking,
                                    onCheckedChange = { isChecked ->
                                        focusManager.clearFocus()
                                        viewModel.updateExperience(
                                            exp.id, 
                                            exp.copy(
                                                isCurrentlyWorking = isChecked,
                                                endDate = if (isChecked) "Present" else ""
                                            )
                                        )
                                    }
                                )
                                Text(
                                    text = "I am currently working in this role",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (!exp.isCurrentlyWorking) {
                                NexaDateTextField(
                                    value = exp.endDate,
                                    onValueChange = { formatted ->
                                        viewModel.updateExperience(exp.id, exp.copy(endDate = formatted))
                                    },
                                    label = "End Date",
                                    showDay = showDay,
                                    onLeadingIconClick = {
                                        if (showDay) {
                                            activeEndCalendarExpId = exp.id
                                        } else {
                                            activeEndMonthYearExpId = exp.id
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            NexaTextField(
                                value = exp.description,
                                onValueChange = { viewModel.updateExperience(exp.id, exp.copy(description = it)) },
                                label = "Role Description & Responsibilities",
                                placeholder = "e.g., Developed scalable Android apps, migrated to Compose, and led the team.",
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                minLines = 3,
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Next
                                )
                            )

                            NexaTextField(
                                value = exp.achievements,
                                onValueChange = { viewModel.updateExperience(exp.id, exp.copy(achievements = it)) },
                                label = "Key Achievements",
                                placeholder = "Describe major highlights (e.g. improved app launch speed by 40%)...",
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                minLines = 3,
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Next
                                )
                            )

                            var techInput by remember(exp.id) { mutableStateOf(exp.technologiesUsed.joinToString(", ")) }
                            LaunchedEffect(exp.technologiesUsed) {
                                val currentList = techInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                if (currentList != exp.technologiesUsed) {
                                    techInput = exp.technologiesUsed.joinToString(", ")
                                }
                            }

                            NexaTextField(
                                value = techInput,
                                onValueChange = { 
                                    techInput = it
                                    val techList = it.split(",")
                                        .map { tech -> tech.trim() }
                                        .filter { tech -> tech.isNotBlank() }
                                    viewModel.updateExperience(exp.id, exp.copy(technologiesUsed = techList))
                                },
                                label = "Technologies Used",
                                placeholder = "e.g. Kotlin, Jetpack Compose, Room (comma separated)",
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                minLines = 2,
                                maxLines = 3,
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

                Spacer(modifier = Modifier.height(8.dp))
                
                val addAnotherInteractionSource = remember { MutableInteractionSource() }
                val addAnotherPressed by addAnotherInteractionSource.collectIsPressedAsState()
                val addAnotherScale by animateFloatAsState(if (addAnotherPressed) 0.98f else 1f, label = "addAnotherScale")
                
                OutlinedButton(
                    onClick = { viewModel.addExperience(Experience()) },
                    interactionSource = addAnotherInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = addAnotherScale, scaleY = addAnotherScale),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Add Another Experience")
                }
            }
        }
    }
}
