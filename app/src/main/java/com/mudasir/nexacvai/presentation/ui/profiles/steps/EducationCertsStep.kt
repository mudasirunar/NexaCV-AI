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
import com.mudasir.nexacvai.domain.model.Education
import com.mudasir.nexacvai.domain.model.Certification
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.utils.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EducationCertsStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    val includeDayEduMap = remember { mutableStateMapOf<String, Boolean>() }
    val includeDayCertMap = remember { mutableStateMapOf<String, Boolean>() }

    // HOISTED DATE PICKER DIALOG STATES FOR EDUCATION
    var activeStartCalendarEduId by remember { mutableStateOf<String?>(null) }
    var activeEndCalendarEduId by remember { mutableStateOf<String?>(null) }
    var activeStartMonthYearEduId by remember { mutableStateOf<String?>(null) }
    var activeEndMonthYearEduId by remember { mutableStateOf<String?>(null) }

    // HOISTED DATE PICKER DIALOG STATES FOR CERTIFICATIONS
    var activeStartCalendarCertId by remember { mutableStateOf<String?>(null) }
    var activeEndCalendarCertId by remember { mutableStateOf<String?>(null) }
    var activeStartMonthYearCertId by remember { mutableStateOf<String?>(null) }
    var activeEndMonthYearCertId by remember { mutableStateOf<String?>(null) }

    // ================= EDUCATION DIALOGS =================
    if (activeStartCalendarEduId != null) {
        val edu = state.educations.find { it.id == activeStartCalendarEduId }
        if (edu != null) {
            val showDay = includeDayEduMap[edu.id] ?: (edu.startDate.count { it == '/' } == 2 || edu.endDate.count { it == '/' } == 2)
            com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaDatePicker(
                initialDateMillis = dateStringToMillis(edu.startDate),
                onDismissRequest = { activeStartCalendarEduId = null },
                onDateSelected = { millis ->
                    if (millis != null) {
                        val dateStr = millisToDateString(millis, showDay)
                        viewModel.updateEducation(edu.id, edu.copy(startDate = dateStr))
                    }
                    activeStartCalendarEduId = null
                }
            )
        }
    }

    if (activeEndCalendarEduId != null) {
        val edu = state.educations.find { it.id == activeEndCalendarEduId }
        if (edu != null) {
            val showDay = includeDayEduMap[edu.id] ?: (edu.startDate.count { it == '/' } == 2 || edu.endDate.count { it == '/' } == 2)
            com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaDatePicker(
                initialDateMillis = dateStringToMillis(edu.endDate),
                onDismissRequest = { activeEndCalendarEduId = null },
                onDateSelected = { millis ->
                    if (millis != null) {
                        val dateStr = millisToDateString(millis, showDay)
                        viewModel.updateEducation(edu.id, edu.copy(endDate = dateStr))
                    }
                    activeEndCalendarEduId = null
                }
            )
        }
    }

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

    // ================= CERTIFICATIONS DIALOGS =================
    if (activeStartCalendarCertId != null) {
        val cert = state.certifications.find { it.id == activeStartCalendarCertId }
        if (cert != null) {
            val showDay = includeDayCertMap[cert.id] ?: (cert.issueDate.count { it == '/' } == 2 || cert.expiryDate.count { it == '/' } == 2)
            com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaDatePicker(
                initialDateMillis = dateStringToMillis(cert.issueDate),
                onDismissRequest = { activeStartCalendarCertId = null },
                onDateSelected = { millis ->
                    if (millis != null) {
                        val dateStr = millisToDateString(millis, showDay)
                        viewModel.updateCertification(cert.id, cert.copy(issueDate = dateStr))
                    }
                    activeStartCalendarCertId = null
                }
            )
        }
    }

    if (activeEndCalendarCertId != null) {
        val cert = state.certifications.find { it.id == activeEndCalendarCertId }
        if (cert != null) {
            val showDay = includeDayCertMap[cert.id] ?: (cert.issueDate.count { it == '/' } == 2 || cert.expiryDate.count { it == '/' } == 2)
            com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaDatePicker(
                initialDateMillis = dateStringToMillis(cert.expiryDate),
                onDismissRequest = { activeEndCalendarCertId = null },
                onDateSelected = { millis ->
                    if (millis != null) {
                        val dateStr = millisToDateString(millis, showDay)
                        viewModel.updateCertification(cert.id, cert.copy(expiryDate = dateStr))
                    }
                    activeEndCalendarCertId = null
                }
            )
        }
    }

    if (activeStartMonthYearCertId != null) {
        val cert = state.certifications.find { it.id == activeStartMonthYearCertId }
        if (cert != null) {
            val parsed = remember(cert.issueDate) { parseMonthAndYear(cert.issueDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeStartMonthYearCertId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateCertification(cert.id, cert.copy(issueDate = formatted))
                    activeStartMonthYearCertId = null
                }
            )
        }
    }

    if (activeEndMonthYearCertId != null) {
        val cert = state.certifications.find { it.id == activeEndMonthYearCertId }
        if (cert != null) {
            val parsed = remember(cert.expiryDate) { parseMonthAndYear(cert.expiryDate, calendar) }
            MonthYearPickerDialog(
                initialMonth = parsed.first,
                initialYear = parsed.second,
                onDismissRequest = { activeEndMonthYearCertId = null },
                onConfirm = { m, y ->
                    val formatted = String.format("%02d/%04d", m, y)
                    viewModel.updateCertification(cert.id, cert.copy(expiryDate = formatted))
                    activeEndMonthYearCertId = null
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
                text = "Step 4: Education & Certifications",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Enter your academic credentials and verified industry certifications.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ================= EDUCATION SECTION =================
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    text = "Education*",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add academic degrees, diplomas, or qualifications. At least 1 is required.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.educations.isEmpty()) {
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
                            text = "No education added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val addFirstEduInteractionSource = remember { MutableInteractionSource() }
                        val addFirstEduPressed by addFirstEduInteractionSource.collectIsPressedAsState()
                        val addFirstEduScale by animateFloatAsState(if (addFirstEduPressed) 0.98f else 1f, label = "addEduScale")

                        Button(
                            onClick = { viewModel.addEducation(Education()) },
                            interactionSource = addFirstEduInteractionSource,
                            modifier = Modifier.graphicsLayer(scaleX = addFirstEduScale, scaleY = addFirstEduScale),
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (edu.degree.isNotBlank()) "${edu.degree}${if(edu.fieldOfStudy.isNotBlank()) " in ${edu.fieldOfStudy}" else ""}" else "Education #${index + 1}",
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
                                val showDayEdu = includeDayEduMap[edu.id] ?: (edu.startDate.count { it == '/' } == 2 || edu.endDate.count { it == '/' } == 2)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            focusManager.clearFocus()
                                            val isChecked = !showDayEdu
                                            includeDayEduMap[edu.id] = isChecked
                                            val newStart = if (isChecked) convertToDayFormat(edu.startDate) else convertToMonthFormat(edu.startDate)
                                            val newEnd = if (isChecked) convertToDayFormat(edu.endDate) else convertToMonthFormat(edu.endDate)
                                            viewModel.updateEducation(edu.id, edu.copy(startDate = newStart, endDate = newEnd))
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NexaCheckbox(
                                        checked = showDayEdu,
                                        onCheckedChange = { isChecked ->
                                            focusManager.clearFocus()
                                            includeDayEduMap[edu.id] = isChecked
                                            val newStart = if (isChecked) convertToDayFormat(edu.startDate) else convertToMonthFormat(edu.startDate)
                                            val newEnd = if (isChecked) convertToDayFormat(edu.endDate) else convertToMonthFormat(edu.endDate)
                                            viewModel.updateEducation(edu.id, edu.copy(startDate = newStart, endDate = newEnd))
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
                                    showDay = showDayEdu,
                                    onLeadingIconClick = {
                                        if (showDayEdu) activeStartCalendarEduId = edu.id else activeStartMonthYearEduId = edu.id
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
                                            val nextOngoing = !edu.isCurrentlyStudying
                                            viewModel.updateEducation(
                                                edu.id,
                                                edu.copy(isCurrentlyStudying = nextOngoing, endDate = if (nextOngoing) "Present" else "")
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
                                                edu.copy(isCurrentlyStudying = isChecked, endDate = if (isChecked) "Present" else "")
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
                                        showDay = showDayEdu,
                                        onLeadingIconClick = {
                                            if (showDayEdu) activeEndCalendarEduId = edu.id else activeEndMonthYearEduId = edu.id
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                NexaTextField(
                                    value = edu.description,
                                    onValueChange = { viewModel.updateEducation(edu.id, edu.copy(description = it)) },
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

                    Spacer(modifier = Modifier.height(4.dp))

                    val addMoreEduInteractionSource = remember { MutableInteractionSource() }
                    val addMoreEduPressed by addMoreEduInteractionSource.collectIsPressedAsState()
                    val addMoreEduScale by animateFloatAsState(if (addMoreEduPressed) 0.98f else 1f, label = "addMoreEduScale")

                    OutlinedButton(
                        onClick = { viewModel.addEducation(Education()) },
                        interactionSource = addMoreEduInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(scaleX = addMoreEduScale, scaleY = addMoreEduScale),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Add Another Education")
                    }
                }
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // ================= CERTIFICATIONS SECTION =================
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Certifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Highlight your licenses, credentials, or courses. This section is optional.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (state.certifications.isEmpty()) {
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
                            text = "No certifications added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val addFirstCertInteractionSource = remember { MutableInteractionSource() }
                        val addFirstCertPressed by addFirstCertInteractionSource.collectIsPressedAsState()
                        val addFirstCertScale by animateFloatAsState(if (addFirstCertPressed) 0.98f else 1f, label = "addCertScale")

                        Button(
                            onClick = { viewModel.addCertification(Certification()) },
                            interactionSource = addFirstCertInteractionSource,
                            modifier = Modifier.graphicsLayer(scaleX = addFirstCertScale, scaleY = addFirstCertScale),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Add Certification")
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
                    state.certifications.forEachIndexed { index, cert ->
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
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (cert.certificationName.isNotBlank()) cert.certificationName else "Certification #${index + 1}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    val removeInteractionSource = remember { MutableInteractionSource() }
                                    val removePressed by removeInteractionSource.collectIsPressedAsState()
                                    val removeScale by animateFloatAsState(if (removePressed) 0.98f else 1f, label = "removeCertScale")

                                    IconButton(
                                        onClick = { viewModel.removeCertification(cert) },
                                        interactionSource = removeInteractionSource,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .graphicsLayer(scaleX = removeScale, scaleY = removeScale)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Certification",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                                NexaTextField(
                                    value = cert.certificationName,
                                    onValueChange = { viewModel.updateCertification(cert.id, cert.copy(certificationName = it)) },
                                    label = "Certification Name*",
                                    placeholder = "e.g. Google Android Developer",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                NexaTextField(
                                    value = cert.issuingOrganization,
                                    onValueChange = { viewModel.updateCertification(cert.id, cert.copy(issuingOrganization = it)) },
                                    label = "Issuing Organization*",
                                    placeholder = "e.g. Google, Udemy, Coursera",
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Dates Input Area
                                val showDayCert = includeDayCertMap[cert.id] ?: (cert.issueDate.count { it == '/' } == 2 || cert.expiryDate.count { it == '/' } == 2)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            focusManager.clearFocus()
                                            val isChecked = !showDayCert
                                            includeDayCertMap[cert.id] = isChecked
                                            val newStart = if (isChecked) convertToDayFormat(cert.issueDate) else convertToMonthFormat(cert.issueDate)
                                            val newEnd = if (isChecked) convertToDayFormat(cert.expiryDate) else convertToMonthFormat(cert.expiryDate)
                                            viewModel.updateCertification(cert.id, cert.copy(issueDate = newStart, expiryDate = newEnd))
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NexaCheckbox(
                                        checked = showDayCert,
                                        onCheckedChange = { isChecked ->
                                            focusManager.clearFocus()
                                            includeDayCertMap[cert.id] = isChecked
                                            val newStart = if (isChecked) convertToDayFormat(cert.issueDate) else convertToMonthFormat(cert.issueDate)
                                            val newEnd = if (isChecked) convertToDayFormat(cert.expiryDate) else convertToMonthFormat(cert.expiryDate)
                                            viewModel.updateCertification(cert.id, cert.copy(issueDate = newStart, expiryDate = newEnd))
                                        }
                                    )
                                    Text(
                                        text = "Include day in dates (DD/MM/YYYY)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                NexaDateTextField(
                                    value = cert.issueDate,
                                    onValueChange = { formatted ->
                                        viewModel.updateCertification(cert.id, cert.copy(issueDate = formatted))
                                    },
                                    label = "Issue Date",
                                    showDay = showDayCert,
                                    onLeadingIconClick = {
                                        if (showDayCert) activeStartCalendarCertId = cert.id else activeStartMonthYearCertId = cert.id
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                var isNotExpiring by remember(cert.id, cert.expiryDate) {
                                    mutableStateOf(cert.expiryDate.isBlank() || cert.expiryDate.equals("Does not expire", ignoreCase = true))
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            focusManager.clearFocus()
                                            val nextVal = !isNotExpiring
                                            isNotExpiring = nextVal
                                            viewModel.updateCertification(cert.id, cert.copy(expiryDate = if (nextVal) "Does not expire" else ""))
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NexaCheckbox(
                                        checked = isNotExpiring,
                                        onCheckedChange = { isChecked ->
                                            focusManager.clearFocus()
                                            isNotExpiring = isChecked
                                            viewModel.updateCertification(cert.id, cert.copy(expiryDate = if (isChecked) "Does not expire" else ""))
                                        }
                                    )
                                    Text(
                                        text = "This credential does not expire",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (!isNotExpiring) {
                                    NexaDateTextField(
                                        value = cert.expiryDate,
                                        onValueChange = { formatted ->
                                            viewModel.updateCertification(cert.id, cert.copy(expiryDate = formatted))
                                        },
                                        label = "Expiration Date",
                                        showDay = showDayCert,
                                        onLeadingIconClick = {
                                            if (showDayCert) activeEndCalendarCertId = cert.id else activeEndMonthYearCertId = cert.id
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                NexaTextField(
                                    value = cert.credentialUrl,
                                    onValueChange = { viewModel.updateCertification(cert.id, cert.copy(credentialUrl = it)) },
                                    label = "Credential URL",
                                    placeholder = "e.g. credentials.authority.com/verify/123",
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

                    val addAnotherCertInteractionSource = remember { MutableInteractionSource() }
                    val addAnotherCertPressed by addAnotherCertInteractionSource.collectIsPressedAsState()
                    val addAnotherCertScale by animateFloatAsState(if (addAnotherCertPressed) 0.98f else 1f, label = "addMoreCertScale")

                    OutlinedButton(
                        onClick = { viewModel.addCertification(Certification()) },
                        interactionSource = addAnotherCertInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(scaleX = addAnotherCertScale, scaleY = addAnotherCertScale),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Add Another Certification")
                    }
                }
            }
        }
    }
}
