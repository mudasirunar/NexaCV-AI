package com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Education
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.profiles.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationCard(
    education: Education,
    index: Int,
    onUpdateEducation: (Education) -> Unit,
    onRemoveEducation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    // Local state for showDay check
    var showDayEdu by rememberSaveable(education.id) {
        mutableStateOf(education.startDate.count { it == '/' } == 2 || education.endDate.count { it == '/' } == 2)
    }

    // Local DatePicker / MonthYear dialog visibility
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartMonthYearPicker by remember { mutableStateOf(false) }
    var showEndMonthYearPicker by remember { mutableStateOf(false) }

    // Dialogs localized inside the card
    if (showStartDatePicker) {
        NexaDatePicker(
            initialDateMillis = dateStringToMillis(education.startDate),
            onDismissRequest = { showStartDatePicker = false },
            onDateSelected = { millis ->
                if (millis != null) {
                    val dateStr = millisToDateString(millis, showDayEdu)
                    onUpdateEducation(education.copy(startDate = dateStr))
                }
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        NexaDatePicker(
            initialDateMillis = dateStringToMillis(education.endDate),
            onDismissRequest = { showEndDatePicker = false },
            onDateSelected = { millis ->
                if (millis != null) {
                    val dateStr = millisToDateString(millis, showDayEdu)
                    onUpdateEducation(education.copy(endDate = dateStr))
                }
                showEndDatePicker = false
            }
        )
    }

    if (showStartMonthYearPicker) {
        val parsed = remember(education.startDate) { parseMonthAndYear(education.startDate, calendar) }
        MonthYearPickerDialog(
            initialMonth = parsed.first,
            initialYear = parsed.second,
            onDismissRequest = { showStartMonthYearPicker = false },
            onConfirm = { m, y ->
                val formatted = String.format("%02d/%04d", m, y)
                onUpdateEducation(education.copy(startDate = formatted))
                showStartMonthYearPicker = false
            }
        )
    }

    if (showEndMonthYearPicker) {
        val parsed = remember(education.endDate) { parseMonthAndYear(education.endDate, calendar) }
        MonthYearPickerDialog(
            initialMonth = parsed.first,
            initialYear = parsed.second,
            onDismissRequest = { showEndMonthYearPicker = false },
            onConfirm = { m, y ->
                val formatted = String.format("%02d/%04d", m, y)
                onUpdateEducation(education.copy(endDate = formatted))
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
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (education.degree.isNotBlank()) {
                            "${education.degree}${if(education.fieldOfStudy.isNotBlank()) " in ${education.fieldOfStudy}" else ""}"
                        } else {
                            "Education #${index + 1}"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                val removeInteractionSource = remember { MutableInteractionSource() }
                val removePressed by removeInteractionSource.collectIsPressedAsState()
                val removeScale by animateFloatAsState(if (removePressed) 0.98f else 1f, label = "removeEduScale")

                IconButton(
                    onClick = onRemoveEducation,
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
                value = education.degree,
                onValueChange = { onUpdateEducation(education.copy(degree = it)) },
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
                value = education.fieldOfStudy,
                onValueChange = { onUpdateEducation(education.copy(fieldOfStudy = it)) },
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
                value = education.instituteName,
                onValueChange = { onUpdateEducation(education.copy(instituteName = it)) },
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
                value = education.grade,
                onValueChange = { onUpdateEducation(education.copy(grade = it)) },
                label = "Grade / GPA / Percentage",
                placeholder = "e.g. 3.8 / 4.0 or 85%",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Include day in dates toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                        val isChecked = !showDayEdu
                        showDayEdu = isChecked
                        val newStart = if (isChecked) convertToDayFormat(education.startDate) else convertToMonthFormat(education.startDate)
                        val newEnd = if (isChecked) convertToDayFormat(education.endDate) else convertToMonthFormat(education.endDate)
                        onUpdateEducation(education.copy(startDate = newStart, endDate = newEnd))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NexaCheckbox(
                    checked = showDayEdu,
                    onCheckedChange = { isChecked ->
                        focusManager.clearFocus()
                        showDayEdu = isChecked
                        val newStart = if (isChecked) convertToDayFormat(education.startDate) else convertToMonthFormat(education.startDate)
                        val newEnd = if (isChecked) convertToDayFormat(education.endDate) else convertToMonthFormat(education.endDate)
                        onUpdateEducation(education.copy(startDate = newStart, endDate = newEnd))
                    }
                )
                Text(
                    text = "Include day in dates (DD/MM/YYYY)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            NexaDateTextField(
                value = education.startDate,
                onValueChange = { formatted ->
                    onUpdateEducation(education.copy(startDate = formatted))
                },
                label = "Start Date",
                showDay = showDayEdu,
                onLeadingIconClick = {
                    if (showDayEdu) showStartDatePicker = true else showStartMonthYearPicker = true
                },
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
                        val nextOngoing = !education.isCurrentlyStudying
                        onUpdateEducation(
                            education.copy(isCurrentlyStudying = nextOngoing, endDate = if (nextOngoing) "Present" else "")
                        )
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NexaCheckbox(
                    checked = education.isCurrentlyStudying,
                    onCheckedChange = { isChecked ->
                        focusManager.clearFocus()
                        onUpdateEducation(
                            education.copy(isCurrentlyStudying = isChecked, endDate = if (isChecked) "Present" else "")
                        )
                    }
                )
                Text(
                    text = "I am currently studying here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (!education.isCurrentlyStudying) {
                NexaDateTextField(
                    value = education.endDate,
                    onValueChange = { formatted ->
                        onUpdateEducation(education.copy(endDate = formatted))
                    },
                    label = "End Date / Graduation Date",
                    showDay = showDayEdu,
                    onLeadingIconClick = {
                        if (showDayEdu) showEndDatePicker = true else showEndMonthYearPicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            NexaTextField(
                value = education.description,
                onValueChange = { onUpdateEducation(education.copy(description = it)) },
                label = "Description / Achievements",
                placeholder = "Describe key courses, research work, honors, or extracurricular achievements...",
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
