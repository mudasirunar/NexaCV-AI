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
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.WorkspacePremium
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
import com.mudasir.nexacvai.domain.model.Certification
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.components.NexaDateTextField
import com.mudasir.nexacvai.presentation.ui.profiles.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificationCard(
    certification: Certification,
    index: Int,
    onUpdateCertification: (Certification) -> Unit,
    onRemoveCertification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val calendar = remember { java.util.Calendar.getInstance() }

    // Local state for showDay check
    var showDayCert by rememberSaveable(certification.id) {
        mutableStateOf(certification.issueDate.count { it == '/' } == 2 || certification.expiryDate.count { it == '/' } == 2)
    }

    // Local DatePicker / MonthYear dialog visibility
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartMonthYearPicker by remember { mutableStateOf(false) }
    var showEndMonthYearPicker by remember { mutableStateOf(false) }

    // Dialogs localized inside the card
    if (showStartDatePicker) {
        NexaDatePicker(
            initialDateMillis = dateStringToMillis(certification.issueDate),
            onDismissRequest = { showStartDatePicker = false },
            onDateSelected = { millis ->
                if (millis != null) {
                    val dateStr = millisToDateString(millis, showDayCert)
                    onUpdateCertification(certification.copy(issueDate = dateStr))
                }
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        NexaDatePicker(
            initialDateMillis = dateStringToMillis(certification.expiryDate),
            onDismissRequest = { showEndDatePicker = false },
            onDateSelected = { millis ->
                if (millis != null) {
                    val dateStr = millisToDateString(millis, showDayCert)
                    onUpdateCertification(certification.copy(expiryDate = dateStr))
                }
                showEndDatePicker = false
            }
        )
    }

    if (showStartMonthYearPicker) {
        val parsed = remember(certification.issueDate) { parseMonthAndYear(certification.issueDate, calendar) }
        MonthYearPickerDialog(
            initialMonth = parsed.first,
            initialYear = parsed.second,
            onDismissRequest = { showStartMonthYearPicker = false },
            onConfirm = { m, y ->
                val formatted = String.format("%02d/%04d", m, y)
                onUpdateCertification(certification.copy(issueDate = formatted))
                showStartMonthYearPicker = false
            }
        )
    }

    if (showEndMonthYearPicker) {
        val parsed = remember(certification.expiryDate) { parseMonthAndYear(certification.expiryDate, calendar) }
        MonthYearPickerDialog(
            initialMonth = parsed.first,
            initialYear = parsed.second,
            onDismissRequest = { showEndMonthYearPicker = false },
            onConfirm = { m, y ->
                val formatted = String.format("%02d/%04d", m, y)
                onUpdateCertification(certification.copy(expiryDate = formatted))
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
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (certification.certificationName.isNotBlank()) certification.certificationName else "Certification #${index + 1}",
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
                    onClick = onRemoveCertification,
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
                value = certification.certificationName,
                onValueChange = { onUpdateCertification(certification.copy(certificationName = it)) },
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
                value = certification.issuingOrganization,
                onValueChange = { onUpdateCertification(certification.copy(issuingOrganization = it)) },
                label = "Issuing Organization*",
                placeholder = "e.g. Google, Udemy, Coursera",
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
                        val isChecked = !showDayCert
                        showDayCert = isChecked
                        val newStart = if (isChecked) convertToDayFormat(certification.issueDate) else convertToMonthFormat(certification.issueDate)
                        val newEnd = if (isChecked) convertToDayFormat(certification.expiryDate) else convertToMonthFormat(certification.expiryDate)
                        onUpdateCertification(certification.copy(issueDate = newStart, expiryDate = newEnd))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NexaCheckbox(
                    checked = showDayCert,
                    onCheckedChange = { isChecked ->
                        focusManager.clearFocus()
                        showDayCert = isChecked
                        val newStart = if (isChecked) convertToDayFormat(certification.issueDate) else convertToMonthFormat(certification.issueDate)
                        val newEnd = if (isChecked) convertToDayFormat(certification.expiryDate) else convertToMonthFormat(certification.expiryDate)
                        onUpdateCertification(certification.copy(issueDate = newStart, expiryDate = newEnd))
                    }
                )
                Text(
                    text = "Include day in dates (DD/MM/YYYY)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            NexaDateTextField(
                value = certification.issueDate,
                onValueChange = { formatted ->
                    onUpdateCertification(certification.copy(issueDate = formatted))
                },
                label = "Issue Date",
                showDay = showDayCert,
                onLeadingIconClick = {
                    if (showDayCert) showStartDatePicker = true else showStartMonthYearPicker = true
                },
                modifier = Modifier.fillMaxWidth()
            )

            var isNotExpiring by remember(certification.id, certification.expiryDate) {
                mutableStateOf(certification.expiryDate.isBlank() || certification.expiryDate.equals("Does not expire", ignoreCase = true))
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
                        onUpdateCertification(certification.copy(expiryDate = if (nextVal) "Does not expire" else ""))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NexaCheckbox(
                    checked = isNotExpiring,
                    onCheckedChange = { isChecked ->
                        focusManager.clearFocus()
                        isNotExpiring = isChecked
                        onUpdateCertification(certification.copy(expiryDate = if (isChecked) "Does not expire" else ""))
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
                    value = certification.expiryDate,
                    onValueChange = { formatted ->
                        onUpdateCertification(certification.copy(expiryDate = formatted))
                    },
                    label = "Expiration Date",
                    showDay = showDayCert,
                    onLeadingIconClick = {
                        if (showDayCert) showEndDatePicker = true else showEndMonthYearPicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            NexaTextField(
                value = certification.credentialUrl,
                onValueChange = { onUpdateCertification(certification.copy(credentialUrl = it)) },
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
