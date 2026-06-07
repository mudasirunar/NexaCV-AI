package com.mudasir.nexacvai.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun NexaDateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    showDay: Boolean,
    modifier: Modifier = Modifier,
    label: String? = null,
    onLeadingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    // Map String value from parent to TextFieldValue internally to keep track of selection
    var internalTfv by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        )
    }

    // Synchronize programmatic updates from the parent safely
    LaunchedEffect(value) {
        if (internalTfv.text != value) {
            internalTfv = internalTfv.copy(
                text = value,
                selection = TextRange(value.length)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusEvent { focusState ->
                if (focusState.isFocused) {
                    coroutineScope.launch {
                        // Delay slightly to let the framework's internal text field scroll finish first,
                        // then scroll our custom region (label + text field + top offset) into view.
                        kotlinx.coroutines.delay(150)
                        val topOffset = with(density) { 24.dp.toPx() }
                        val bottomOffset = with(density) { 90.dp.toPx() }
                        bringIntoViewRequester.bringIntoView(
                            rect = Rect(
                                left = 0f,
                                top = -topOffset,
                                right = 0f,
                                bottom = bottomOffset
                            )
                        )
                    }
                }
            },
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else if (isFocused) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                }
            )
        }

        OutlinedTextField(
            value = internalTfv,
            onValueChange = { newTfv ->
                if (enabled) {
                    // 1. If only cursor/selection changed, accept immediately without formatting
                    val textChanged = newTfv.text != internalTfv.text
                    if (!textChanged) {
                        internalTfv = newTfv
                    } else {
                        // 2. Check if it's a special string like "Present" or "Does not expire"
                        val isSpecial = newTfv.text.equals("Present", ignoreCase = true) || 
                                        newTfv.text.equals("Does not expire", ignoreCase = true)
                        if (isSpecial) {
                            internalTfv = newTfv
                            onValueChange(newTfv.text)
                        } else {
                            // 3. Handle deletion of a slash '/' gracefully by deleting the digit before it
                            val isDeletion = newTfv.text.length < internalTfv.text.length
                            var activeTfv = newTfv
                            if (isDeletion) {
                                val oldCursor = internalTfv.selection.start
                                if (oldCursor > 0 && oldCursor - 1 < internalTfv.text.length && internalTfv.text[oldCursor - 1] == '/') {
                                    val sb = StringBuilder(internalTfv.text)
                                    if (oldCursor - 2 >= 0) {
                                        sb.deleteAt(oldCursor - 2) // Delete the digit before the slash
                                        activeTfv = TextFieldValue(
                                            text = sb.toString(),
                                            selection = TextRange(oldCursor - 2)
                                        )
                                    }
                                }
                            }

                            // 4. Format logic
                            val digits = activeTfv.text.filter { it.isDigit() }
                            val formatted = if (showDay) {
                                val limitedDigits = digits.take(8)
                                val base = when {
                                    limitedDigits.length <= 2 -> limitedDigits
                                    limitedDigits.length <= 4 -> "${limitedDigits.substring(0, 2)}/${limitedDigits.substring(2)}"
                                    else -> "${limitedDigits.substring(0, 2)}/${limitedDigits.substring(2, 4)}/${limitedDigits.substring(4)}"
                                }
                                // Preserve trailing slash if explicitly typed
                                if (activeTfv.text.endsWith("/") && base.count { it == '/' } < 2 && limitedDigits.isNotEmpty() && limitedDigits.length < 8) {
                                    if (base.endsWith("/")) base else "$base/"
                                } else {
                                    base
                                }
                            } else {
                                val limitedDigits = digits.take(6)
                                val base = if (limitedDigits.length <= 2) {
                                    limitedDigits
                                } else {
                                    "${limitedDigits.substring(0, 2)}/${limitedDigits.substring(2)}"
                                }
                                // Preserve trailing slash if explicitly typed
                                if (activeTfv.text.endsWith("/") && base.count { it == '/' } < 1 && limitedDigits.isNotEmpty() && limitedDigits.length < 6) {
                                    if (base.endsWith("/")) base else "$base/"
                                } else {
                                    base
                                }
                            }

                            // 5. Secure cursor selection mapping
                            val digitsBefore = activeTfv.text.substring(0, activeTfv.selection.start).count { it.isDigit() }
                            val slashesBefore = activeTfv.text.substring(0, activeTfv.selection.start).count { it == '/' }
                            
                            var formattedCursor = 0
                            var digitsFound = 0
                            var slashesFound = 0
                            for (i in 0 until formatted.length) {
                                if (digitsFound >= digitsBefore && slashesFound >= slashesBefore) {
                                    break
                                }
                                if (formatted[i].isDigit()) {
                                    digitsFound++
                                } else if (formatted[i] == '/') {
                                    slashesFound++
                                }
                                formattedCursor = i + 1
                            }

                            // Skip slash padding when moving cursor forward (not deleting)
                            if (!isDeletion && activeTfv.selection.start > 0 && activeTfv.text.length > formattedCursor) {
                                if (formattedCursor < formatted.length && formatted[formattedCursor] == '/') {
                                    formattedCursor++
                                }
                            }

                            val resultTfv = TextFieldValue(
                                text = formatted,
                                selection = TextRange(formattedCursor.coerceIn(0, formatted.length))
                            )
                            internalTfv = resultTfv
                            onValueChange(formatted)
                        }
                    }
                }
            },
            placeholder = {
                Text(
                    text = if (showDay) "DD/MM/YYYY" else "MM/YYYY",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Select Date",
                    modifier = Modifier
                        .size(18.dp)
                        .then(
                            if (onLeadingIconClick != null) {
                                Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        focusManager.clearFocus()
                                        onLeadingIconClick()
                                    }
                                )
                            } else Modifier
                        ),
                    tint = if (isError) {
                        MaterialTheme.colorScheme.error
                    } else if (isFocused) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    }
                )
            },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) },
                onDone = { focusManager.clearFocus() }
            ),
            enabled = enabled,
            interactionSource = interactionSource,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
            ),
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(
            visible = isError && !errorMessage.isNullOrBlank(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
