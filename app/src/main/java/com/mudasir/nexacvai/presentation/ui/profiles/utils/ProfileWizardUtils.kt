package com.mudasir.nexacvai.presentation.ui.profiles.utils

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.text.TextStyle
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
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun convertToDayFormat(date: String): String {
    if (date.isBlank() || date.equals("Present", ignoreCase = true)) return date
    val parts = date.split("/").map { it.trim() }.filter { it.isNotBlank() }
    if (parts.size == 3) {
        return date
    }
    if (parts.size == 2) {
        return "01/${parts[0]}/${parts[1]}"
    }
    // Handle digits only cases securely
    val digits = date.filter { it.isDigit() }
    if (digits.length >= 6) {
        return "01/${digits.substring(0, 2)}/${digits.substring(2)}"
    } else if (digits.length >= 4) {
        return "01/${digits.substring(0, 2)}/${digits.substring(2)}"
    }
    return date
}

fun convertToMonthFormat(date: String): String {
    if (date.isBlank() || date.equals("Present", ignoreCase = true)) return date
    val parts = date.split("/").map { it.trim() }.filter { it.isNotBlank() }
    if (parts.size == 3) {
        return "${parts[1]}/${parts[2]}"
    }
    if (parts.size == 2) {
        return date
    }
    // Handle digits only cases securely
    val digits = date.filter { it.isDigit() }
    if (digits.length >= 8) {
        return "${digits.substring(2, 4)}/${digits.substring(4)}"
    } else if (digits.length >= 6) {
        return "${digits.substring(0, 2)}/${digits.substring(2)}"
    }
    return date
}

fun parseMonthAndYear(dateStr: String, defaultCalendar: java.util.Calendar): Pair<Int, Int> {
    var month = defaultCalendar.get(java.util.Calendar.MONTH) + 1
    var year = defaultCalendar.get(java.util.Calendar.YEAR)
    if (dateStr.isNotBlank() && !dateStr.equals("Present", ignoreCase = true)) {
        try {
            val parts = dateStr.split("/")
            if (parts.size == 3) { // DD/MM/YYYY
                month = parts[1].toIntOrNull() ?: month
                year = parts[2].toIntOrNull() ?: year
            } else if (parts.size == 2) { // MM/YYYY
                month = parts[0].toIntOrNull() ?: month
                year = parts[1].toIntOrNull() ?: year
            }
        } catch (e: Exception) {
            // ignore
        }
    }
    return Pair(month, year)
}

@Composable
fun MonthYearPickerDialog(
    initialMonth: Int,
    initialYear: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    var selectedYear by remember { mutableStateOf(initialYear) }
    var isEditingYear by remember { mutableStateOf(false) }
    var yearInputText by remember { mutableStateOf(selectedYear.toString()) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    LaunchedEffect(isEditingYear) {
        if (isEditingYear) {
            kotlinx.coroutines.delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMonth, selectedYear) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text = "Select Month & Year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Year Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { 
                        selectedYear-- 
                        yearInputText = selectedYear.toString()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowLeft, contentDescription = "Previous Year")
                    }
                    
                    if (isEditingYear) {
                        androidx.compose.foundation.text.BasicTextField(
                            value = yearInputText,
                            onValueChange = { newValue ->
                                if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
                                    yearInputText = newValue
                                    val parsedYear = newValue.toIntOrNull()
                                    if (parsedYear != null && parsedYear in 1900..2100) {
                                        selectedYear = parsedYear
                                    }
                                }
                            },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val parsedYear = yearInputText.toIntOrNull()
                                    if (parsedYear != null && parsedYear in 1900..2100) {
                                        selectedYear = parsedYear
                                    }
                                    isEditingYear = false
                                }
                            ),
                            modifier = Modifier
                                .width(80.dp)
                                .focusRequester(focusRequester)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            singleLine = true
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable {
                                isEditingYear = true
                                yearInputText = selectedYear.toString()
                            }
                        ) {
                            Text(
                                text = selectedYear.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Year",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    IconButton(onClick = { 
                        selectedYear++ 
                        yearInputText = selectedYear.toString()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Next Year")
                    }
                }

                // Months Grid (3x4)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (row in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0 until 3) {
                                val monthIndex = row * 3 + col
                                val isSelected = selectedMonth == monthIndex + 1
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                        .clickable { selectedMonth = monthIndex + 1 }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = months[monthIndex],
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun NexaCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Tactile bouncy spring animation
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.08f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "checkboxScale"
    )

    // Smooth color state interpolation
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
        animationSpec = tween(durationMillis = 200),
        label = "checkboxBackground"
    )

    val borderColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        animationSpec = tween(durationMillis = 200),
        label = "checkboxBorder"
    )

    Box(
        modifier = modifier
            .size(22.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = fadeIn(animationSpec = tween(100)) + 
                    scaleIn(initialScale = 0.5f, animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(100)) + 
                   scaleOut(targetScale = 0.5f, animationSpec = tween(150))
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

fun dateStringToMillis(dateStr: String): Long? {
    if (dateStr.isBlank() || dateStr.equals("Present", ignoreCase = true)) return null
    return try {
        val format = if (dateStr.count { it == '/' } == 2) {
            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        } else {
            java.text.SimpleDateFormat("MM/yyyy", java.util.Locale.getDefault())
        }
        format.timeZone = java.util.TimeZone.getTimeZone("UTC")
        format.parse(dateStr)?.time
    } catch (e: Exception) {
        null
    }
}

fun millisToDateString(millis: Long, showDay: Boolean): String {
    val date = java.util.Date(millis)
    val format = if (showDay) {
        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
    } else {
        java.text.SimpleDateFormat("MM/yyyy", java.util.Locale.getDefault())
    }
    format.timeZone = java.util.TimeZone.getTimeZone("UTC")
    return format.format(date)
}

fun formatSmartDateInput(input: String, showDay: Boolean): String {
    if (input.isBlank() || input.equals("Present", ignoreCase = true)) return input
    val digits = input.filter { it.isDigit() }
    return if (showDay) {
        // If the user converts a 6-digit date (MM/YYYY) like "122025", let's pad it to "01122025"
        val raw = if (digits.length == 6) {
            "01$digits"
        } else {
            digits
        }
        val limited = raw.take(8)
        when {
            limited.length <= 2 -> limited
            limited.length <= 4 -> {
                "${limited.substring(0, 2)}/${limited.substring(2)}"
            }
            else -> {
                "${limited.substring(0, 2)}/${limited.substring(2, 4)}/${limited.substring(4)}"
            }
        }
    } else {
        // If the user converts an 8-digit date (DD/MM/YYYY) like "01122025", let's convert to "122025"
        val raw = if (digits.length == 8) {
            digits.substring(2) // skip the first 2 digits (day)
        } else {
            digits
        }
        val limited = raw.take(6)
        if (limited.length <= 2) {
            limited
        } else {
            "${limited.substring(0, 2)}/${limited.substring(2)}"
        }
    }
}

class DateVisualTransformation(private val showDay: Boolean) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        // Only allow digits in the raw string for transformation
        val raw = text.text.filter { it.isDigit() }
        val formatted = if (showDay) {
            val limited = raw.take(8)
            when {
                limited.length <= 2 -> limited
                limited.length <= 4 -> "${limited.substring(0, 2)}/${limited.substring(2)}"
                else -> "${limited.substring(0, 2)}/${limited.substring(2, 4)}/${limited.substring(4)}"
            }
        } else {
            val limited = raw.take(6)
            if (limited.length <= 2) limited else "${limited.substring(0, 2)}/${limited.substring(2)}"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val sanitized = offset.coerceIn(0, text.text.length)
                val transformed = if (showDay) {
                    when {
                        sanitized <= 2 -> sanitized
                        sanitized <= 4 -> sanitized + 1
                        else -> sanitized + 2
                    }
                } else {
                    when {
                        sanitized <= 2 -> sanitized
                        else -> sanitized + 1
                    }
                }
                return transformed.coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val sanitized = offset.coerceIn(0, formatted.length)
                val original = if (showDay) {
                    when {
                        sanitized <= 2 -> sanitized
                        sanitized <= 5 -> sanitized - 1
                        else -> sanitized - 2
                    }
                } else {
                    when {
                        sanitized <= 2 -> sanitized
                        else -> sanitized - 1
                    }
                }
                return original.coerceIn(0, text.text.length)
            }
        }

        return TransformedText(
            text = androidx.compose.ui.text.AnnotatedString(formatted),
            offsetMapping = offsetMapping
        )
    }
}