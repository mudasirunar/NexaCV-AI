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
import androidx.compose.material.icons.filled.Work
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
import com.mudasir.nexacvai.domain.model.Experience
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.profiles.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceCard(
    experience: Experience,
    index: Int,
    onUpdateExperience: (Experience) -> Unit,
    onRemoveExperience: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    // Local state for showDay check
    var showDay by rememberSaveable(experience.id) {
        mutableStateOf(experience.startDate.count { it == '/' } == 2 || experience.endDate.count { it == '/' } == 2)
    }

    // Local DatePicker / MonthYear dialog visibility
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartMonthYearPicker by remember { mutableStateOf(false) }
    var showEndMonthYearPicker by remember { mutableStateOf(false) }

    // Dialogs localized inside the card
    if (showStartDatePicker) {
        NexaDatePicker(
            initialDateMillis = dateStringToMillis(experience.startDate),
            onDismissRequest = { showStartDatePicker = false },
            onDateSelected = { millis ->
                if (millis != null) {
                    val dateStr = millisToDateString(millis, showDay)
                    onUpdateExperience(experience.copy(startDate = dateStr))
                }
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        NexaDatePicker(
            initialDateMillis = dateStringToMillis(experience.endDate),
            onDismissRequest = { showEndDatePicker = false },
            onDateSelected = { millis ->
                if (millis != null) {
                    val dateStr = millisToDateString(millis, showDay)
                    onUpdateExperience(experience.copy(endDate = dateStr))
                }
                showEndDatePicker = false
            }
        )
    }

    if (showStartMonthYearPicker) {
        val parsed = remember(experience.startDate) { parseMonthAndYear(experience.startDate, calendar) }
        MonthYearPickerDialog(
            initialMonth = parsed.first,
            initialYear = parsed.second,
            onDismissRequest = { showStartMonthYearPicker = false },
            onConfirm = { m, y ->
                val formatted = String.format("%02d/%04d", m, y)
                onUpdateExperience(experience.copy(startDate = formatted))
                showStartMonthYearPicker = false
            }
        )
    }

    if (showEndMonthYearPicker) {
        val parsed = remember(experience.endDate) { parseMonthAndYear(experience.endDate, calendar) }
        MonthYearPickerDialog(
            initialMonth = parsed.first,
            initialYear = parsed.second,
            onDismissRequest = { showEndMonthYearPicker = false },
            onConfirm = { m, y ->
                val formatted = String.format("%02d/%04d", m, y)
                onUpdateExperience(experience.copy(endDate = formatted))
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
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (experience.jobTitle.isNotBlank()) experience.jobTitle else "Experience #${index + 1}",
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
                    onClick = onRemoveExperience,
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
                value = experience.jobTitle,
                onValueChange = { onUpdateExperience(experience.copy(jobTitle = it)) },
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
                value = experience.companyName,
                onValueChange = { onUpdateExperience(experience.copy(companyName = it)) },
                label = "Company Name*",
                placeholder = "e.g. Google",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            NexaTextField(
                value = experience.location,
                onValueChange = { onUpdateExperience(experience.copy(location = it)) },
                label = "Location",
                placeholder = "e.g. San Francisco, CA (or Remote)",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
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
                        val isChecked = !showDay
                        showDay = isChecked
                        val newStart = if (isChecked) convertToDayFormat(experience.startDate) else convertToMonthFormat(experience.startDate)
                        val newEnd = if (isChecked) convertToDayFormat(experience.endDate) else convertToMonthFormat(experience.endDate)
                        onUpdateExperience(experience.copy(startDate = newStart, endDate = newEnd))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NexaCheckbox(
                    checked = showDay,
                    onCheckedChange = { isChecked ->
                        focusManager.clearFocus()
                        showDay = isChecked
                        val newStart = if (isChecked) convertToDayFormat(experience.startDate) else convertToMonthFormat(experience.startDate)
                        val newEnd = if (isChecked) convertToDayFormat(experience.endDate) else convertToMonthFormat(experience.endDate)
                        onUpdateExperience(experience.copy(startDate = newStart, endDate = newEnd))
                    }
                )
                Text(
                    text = "Include day in dates (DD/MM/YYYY)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            NexaDateTextField(
                value = experience.startDate,
                onValueChange = { formatted ->
                    onUpdateExperience(experience.copy(startDate = formatted))
                },
                label = "Start Date",
                showDay = showDay,
                onLeadingIconClick = {
                    if (showDay) showStartDatePicker = true else showStartMonthYearPicker = true
                },
                modifier = Modifier.fillMaxWidth()
            )

            val isOngoing = experience.endDate.equals("Present", ignoreCase = true)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                        val nextOngoing = !isOngoing
                        onUpdateExperience(experience.copy(endDate = if (nextOngoing) "Present" else ""))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NexaCheckbox(
                    checked = isOngoing,
                    onCheckedChange = { isChecked ->
                        focusManager.clearFocus()
                        onUpdateExperience(experience.copy(endDate = if (isChecked) "Present" else ""))
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
                    value = experience.endDate,
                    onValueChange = { formatted ->
                        onUpdateExperience(experience.copy(endDate = formatted))
                    },
                    label = "End Date",
                    showDay = showDay,
                    onLeadingIconClick = {
                        if (showDay) showEndDatePicker = true else showEndMonthYearPicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            NexaTextField(
                value = experience.description,
                onValueChange = { onUpdateExperience(experience.copy(description = it)) },
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
