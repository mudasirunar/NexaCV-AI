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
import com.mudasir.nexacvai.domain.model.Education
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.utils.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EducationStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    // HOISTED DATE PICKER DIALOG STATES
    var activeStartCalendarEduId by remember { mutableStateOf<String?>(null) }
    var activeEndCalendarEduId by remember { mutableStateOf<String?>(null) }
    var activeStartMonthYearEduId by remember { mutableStateOf<String?>(null) }
    var activeEndMonthYearEduId by remember { mutableStateOf<String?>(null) }

    // START CALENDAR DIALOG
    if (activeStartCalendarEduId != null) {
        val edu = state.educations.find { it.id == activeStartCalendarEduId }
        if (edu != null) {
            val showDay = edu.startDate.count { it == '/' } == 2 || edu.endDate.count { it == '/' } == 2
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateStringToMillis(edu.startDate)
            )
            DatePickerDialog(
                onDismissRequest = { activeStartCalendarEduId = null },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateStr = millisToDateString(millis, showDay)
                                viewModel.updateEducation(edu.id, edu.copy(startDate = dateStr))
                            }
                            activeStartCalendarEduId = null
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { activeStartCalendarEduId = null }) { Text("Cancel") }
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
    if (activeEndCalendarEduId != null) {
        val edu = state.educations.find { it.id == activeEndCalendarEduId }
        if (edu != null) {
            val showDay = edu.startDate.count { it == '/' } == 2 || edu.endDate.count { it == '/' } == 2
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateStringToMillis(edu.endDate)
            )
            DatePickerDialog(
                onDismissRequest = { activeEndCalendarEduId = null },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateStr = millisToDateString(millis, showDay)
                                viewModel.updateEducation(edu.id, edu.copy(endDate = dateStr))
                            }
                            activeEndCalendarEduId = null
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { activeEndCalendarEduId = null }) { Text("Cancel") }
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

    // START MONTH YEAR PICKER
    if (activeStartMonthYearEduId != null) {
        val edu = state.educations.find { it.id == activeStartMonthYearEduId }
        if (edu != null) {
            val parsed = remember(edu.startDate) { parseMonthAndYear(edu.startDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeStartMonthYearEduId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateEducation(edu.id, edu.copy(startDate = formatted))
                    activeStartMonthYearEduId = null
                }
            )
        }
    }

    // END MONTH YEAR PICKER
    if (activeEndMonthYearEduId != null) {
        val edu = state.educations.find { it.id == activeEndMonthYearEduId }
        if (edu != null) {
            val parsed = remember(edu.endDate) { parseMonthAndYear(edu.endDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeEndMonthYearEduId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateEducation(edu.id, edu.copy(endDate = formatted))
                    activeEndMonthYearEduId = null
                }
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Step 5: Education",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tell us about your academic background. List degrees, diplomas, or certificates.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.educations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No education added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val addFirstInteractionSource = remember { MutableInteractionSource() }
                    val addFirstPressed by addFirstInteractionSource.collectIsPressedAsState()
                    val addFirstScale by animateFloatAsState(if (addFirstPressed) 0.98f else 1f, label = "addFirstScale")

                    Button(
                        onClick = { viewModel.addEducation(Education()) },
                        interactionSource = addFirstInteractionSource,
                        modifier = Modifier.graphicsLayer(scaleX = addFirstScale, scaleY = addFirstScale),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Add Education")
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
                state.educations.forEachIndexed { index, edu ->
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
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (edu.degree.isNotBlank()) "${edu.degree}${if(edu.fieldOfStudy.isNotBlank()) " in ${edu.fieldOfStudy}" else ""}" else "Education #${index + 1}",
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
                                    onClick = { viewModel.removeEducation(edu) },
                                    interactionSource = removeInteractionSource,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .graphicsLayer(scaleX = removeScale, scaleY = removeScale)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Education",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                            NexaTextField(
                                value = edu.degree,
                                onValueChange = { viewModel.updateEducation(edu.id, edu.copy(degree = it)) },
                                label = "Degree / Certificate*",
                                placeholder = "e.g. Bachelor of Science",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = edu.fieldOfStudy,
                                onValueChange = { viewModel.updateEducation(edu.id, edu.copy(fieldOfStudy = it)) },
                                label = "Field of Study",
                                placeholder = "e.g. Computer Science",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = edu.instituteName,
                                onValueChange = { viewModel.updateEducation(edu.id, edu.copy(instituteName = it)) },
                                label = "School / Institute*",
                                placeholder = "e.g. Stanford University",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = edu.grade,
                                onValueChange = { viewModel.updateEducation(edu.id, edu.copy(grade = it)) },
                                label = "Grade / GPA / Percentage",
                                placeholder = "e.g. 3.8 / 4.0 or 85%",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Dates Input Area
                            var localIncludeDay by remember(edu.id) { mutableStateOf<Boolean?>(null) }
                            val showDay by remember(localIncludeDay, edu.startDate, edu.endDate) {
                                derivedStateOf {
                                    localIncludeDay ?: (edu.startDate.count { it == '/' } == 2 || edu.endDate.count { it == '/' } == 2)
                                }
                            }

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
                                            convertToDayFormat(edu.startDate)
                                        } else {
                                            convertToMonthFormat(edu.startDate)
                                        }
                                        val newEnd = if (!showDay) {
                                            convertToDayFormat(edu.endDate)
                                        } else {
                                            convertToMonthFormat(edu.endDate)
                                        }
                                        viewModel.updateEducation(
                                            edu.id,
                                            edu.copy(startDate = newStart, endDate = newEnd)
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
                                            convertToDayFormat(edu.startDate)
                                        } else {
                                            convertToMonthFormat(edu.startDate)
                                        }
                                        val newEnd = if (isChecked) {
                                            convertToDayFormat(edu.endDate)
                                        } else {
                                            convertToMonthFormat(edu.endDate)
                                        }
                                        viewModel.updateEducation(
                                            edu.id,
                                            edu.copy(startDate = newStart, endDate = newEnd)
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
                                value = edu.startDate,
                                onValueChange = { formatted ->
                                    viewModel.updateEducation(edu.id, edu.copy(startDate = formatted))
                                },
                                label = "Start Date",
                                showDay = showDay,
                                onLeadingIconClick = {
                                    if (showDay) {
                                        activeStartCalendarEduId = edu.id
                                    } else {
                                        activeStartMonthYearEduId = edu.id
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Currently studying checkmark toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        focusManager.clearFocus()
                                        val nextOngoing = !edu.isCurrentlyStudying
                                        viewModel.updateEducation(
                                            edu.id,
                                            edu.copy(
                                                isCurrentlyStudying = nextOngoing,
                                                endDate = if (nextOngoing) "Present" else ""
                                            )
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NexaCheckbox(
                                    checked = edu.isCurrentlyStudying,
                                    onCheckedChange = { isChecked ->
                                        focusManager.clearFocus()
                                        viewModel.updateEducation(
                                            edu.id,
                                            edu.copy(
                                                isCurrentlyStudying = isChecked,
                                                endDate = if (isChecked) "Present" else ""
                                            )
                                        )
                                    }
                                )
                                Text(
                                    text = "I am currently studying here",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (!edu.isCurrentlyStudying) {
                                NexaDateTextField(
                                    value = edu.endDate,
                                    onValueChange = { formatted ->
                                        viewModel.updateEducation(edu.id, edu.copy(endDate = formatted))
                                    },
                                    label = "End Date / Graduation Date",
                                    showDay = showDay,
                                    onLeadingIconClick = {
                                        if (showDay) {
                                            activeEndCalendarEduId = edu.id
                                        } else {
                                            activeEndMonthYearEduId = edu.id
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            NexaTextField(
                                value = edu.description,
                                onValueChange = { viewModel.updateEducation(edu.id, edu.copy(description = it)) },
                                label = "Description / Key Achievements / Courses",
                                placeholder = "Describe key courses, research work, or honors...",
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

                Spacer(modifier = Modifier.height(8.dp))

                val addAnotherInteractionSource = remember { MutableInteractionSource() }
                val addAnotherPressed by addAnotherInteractionSource.collectIsPressedAsState()
                val addAnotherScale by animateFloatAsState(if (addAnotherPressed) 0.98f else 1f, label = "addAnotherScale")

                OutlinedButton(
                    onClick = { viewModel.addEducation(Education()) },
                    interactionSource = addAnotherInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = addAnotherScale, scaleY = addAnotherScale),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Add Another Education")
                }
            }
        }
    }
}
