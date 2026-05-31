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
import com.mudasir.nexacvai.domain.model.Certification
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.utils.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CertificationsStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    // HOISTED DATE PICKER DIALOG STATES
    var activeStartCalendarCertId by remember { mutableStateOf<String?>(null) }
    var activeEndCalendarCertId by remember { mutableStateOf<String?>(null) }
    var activeStartMonthYearCertId by remember { mutableStateOf<String?>(null) }
    var activeEndMonthYearCertId by remember { mutableStateOf<String?>(null) }

    // START CALENDAR DIALOG
    if (activeStartCalendarCertId != null) {
        val cert = state.certifications.find { it.id == activeStartCalendarCertId }
        if (cert != null) {
            val showDay = cert.issueDate.count { it == '/' } == 2 || cert.expiryDate.count { it == '/' } == 2
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateStringToMillis(cert.issueDate)
            )
            DatePickerDialog(
                onDismissRequest = { activeStartCalendarCertId = null },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateStr = millisToDateString(millis, showDay)
                                viewModel.updateCertification(cert.id, cert.copy(issueDate = dateStr))
                            }
                            activeStartCalendarCertId = null
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { activeStartCalendarCertId = null }) { Text("Cancel") }
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
    if (activeEndCalendarCertId != null) {
        val cert = state.certifications.find { it.id == activeEndCalendarCertId }
        if (cert != null) {
            val showDay = cert.issueDate.count { it == '/' } == 2 || cert.expiryDate.count { it == '/' } == 2
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateStringToMillis(cert.expiryDate)
            )
            DatePickerDialog(
                onDismissRequest = { activeEndCalendarCertId = null },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateStr = millisToDateString(millis, showDay)
                                viewModel.updateCertification(cert.id, cert.copy(expiryDate = dateStr))
                            }
                            activeEndCalendarCertId = null
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { activeEndCalendarCertId = null }) { Text("Cancel") }
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

    // END MONTH YEAR PICKER
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Step 6: Certifications",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Highlight your certified qualifications, credentials, or licenses. This section is optional.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.certifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No certifications added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val addFirstInteractionSource = remember { MutableInteractionSource() }
                    val addFirstPressed by addFirstInteractionSource.collectIsPressedAsState()
                    val addFirstScale by animateFloatAsState(if (addFirstPressed) 0.98f else 1f, label = "addFirstScale")

                    Button(
                        onClick = { viewModel.addCertification(Certification()) },
                        interactionSource = addFirstInteractionSource,
                        modifier = Modifier.graphicsLayer(scaleX = addFirstScale, scaleY = addFirstScale),
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
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
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (cert.certificationName.isNotBlank()) cert.certificationName else "Certification #${index + 1}",
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
                                label = "Issuing Organization / Authority*",
                                placeholder = "e.g. Google, Udemy, Coursera",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Dates Input Area
                            var localIncludeDay by remember(cert.id) { mutableStateOf<Boolean?>(null) }
                            val showDay by remember(localIncludeDay, cert.issueDate, cert.expiryDate) {
                                derivedStateOf {
                                    localIncludeDay ?: (cert.issueDate.count { it == '/' } == 2 || cert.expiryDate.count { it == '/' } == 2)
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
                                            convertToDayFormat(cert.issueDate)
                                        } else {
                                            convertToMonthFormat(cert.issueDate)
                                        }
                                        val newEnd = if (!showDay) {
                                            convertToDayFormat(cert.expiryDate)
                                        } else {
                                            convertToMonthFormat(cert.expiryDate)
                                        }
                                        viewModel.updateCertification(
                                            cert.id,
                                            cert.copy(issueDate = newStart, expiryDate = newEnd)
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
                                            convertToDayFormat(cert.issueDate)
                                        } else {
                                            convertToMonthFormat(cert.issueDate)
                                        }
                                        val newEnd = if (isChecked) {
                                            convertToDayFormat(cert.expiryDate)
                                        } else {
                                            convertToMonthFormat(cert.expiryDate)
                                        }
                                        viewModel.updateCertification(
                                            cert.id,
                                            cert.copy(issueDate = newStart, expiryDate = newEnd)
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
                                value = cert.issueDate,
                                onValueChange = { formatted ->
                                    viewModel.updateCertification(cert.id, cert.copy(issueDate = formatted))
                                },
                                label = "Issue Date",
                                showDay = showDay,
                                onLeadingIconClick = {
                                    if (showDay) {
                                        activeStartCalendarCertId = cert.id
                                    } else {
                                        activeStartMonthYearCertId = cert.id
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Does not expire toggle
                            val doesNotExpire = cert.expiryDate.equals("Present", ignoreCase = true) || cert.expiryDate.isEmpty() && cert.issueDate.isNotEmpty() // custom semantic check or let it be empty
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
                                        viewModel.updateCertification(
                                            cert.id,
                                            cert.copy(expiryDate = if (nextVal) "Does not expire" else "")
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NexaCheckbox(
                                    checked = isNotExpiring,
                                    onCheckedChange = { isChecked ->
                                        focusManager.clearFocus()
                                        isNotExpiring = isChecked
                                        viewModel.updateCertification(
                                            cert.id,
                                            cert.copy(expiryDate = if (isChecked) "Does not expire" else "")
                                        )
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
                                    showDay = showDay,
                                    onLeadingIconClick = {
                                        if (showDay) {
                                            activeEndCalendarCertId = cert.id
                                        } else {
                                            activeEndMonthYearCertId = cert.id
                                        }
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

                Spacer(modifier = Modifier.height(8.dp))

                val addAnotherInteractionSource = remember { MutableInteractionSource() }
                val addAnotherPressed by addAnotherInteractionSource.collectIsPressedAsState()
                val addAnotherScale by animateFloatAsState(if (addAnotherPressed) 0.98f else 1f, label = "addAnotherScale")

                OutlinedButton(
                    onClick = { viewModel.addCertification(Certification()) },
                    interactionSource = addAnotherInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = addAnotherScale, scaleY = addAnotherScale),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Add Another Certification")
                }
            }
        }
    }
}
