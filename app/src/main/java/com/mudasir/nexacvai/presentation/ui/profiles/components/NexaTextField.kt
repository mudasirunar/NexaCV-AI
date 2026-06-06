package com.mudasir.nexacvai.presentation.ui.profiles.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A highly-optimized, premium Material 3 Text Field component.
 *
 * Refactored to act as a highly compatible, standard Material 3 OutlinedTextField
 * that natively supports optimized soft keyboard (IME) input and clean character filtering.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun NexaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    onLeadingIconClick: (() -> Unit)? = null,
    maxInputLength: Int? = null,
    onlyDigits: Boolean = false,
    onlyDigitsAndPlus: Boolean = false
) {
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    // Local TextFieldValue state to maintain selection and composing state for 100% IME compatibility
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    // Keep local state in sync with external value updates during composition
    // Explicitly places the cursor on the last letter when programmatic focus transitions happen (e.g. keyboard "Next")
    if (textFieldValueState.text != value) {
        textFieldValueState = textFieldValueState.copy(
            text = value,
            selection = TextRange(value.length)
        )
    }

    // Automatically set default ImeAction to Next for single line text fields
    val resolvedKeyboardOptions = remember(keyboardOptions, singleLine, onlyDigits, onlyDigitsAndPlus) {
        var options = if (singleLine && (keyboardOptions.imeAction == ImeAction.Default || keyboardOptions.imeAction == ImeAction.None)) {
            keyboardOptions.copy(imeAction = ImeAction.Next)
        } else {
            keyboardOptions
        }

        // Use appropriate numeric/phone keyboard layout when input restrictions are active
        if (onlyDigits) {
            options = options.copy(keyboardType = KeyboardType.Number)
        } else if (onlyDigitsAndPlus) {
            options = options.copy(keyboardType = KeyboardType.Phone)
        }

        options
    }

    val resolvedKeyboardActions = remember(keyboardActions, focusManager) {
        KeyboardActions(
            onDone = keyboardActions.onDone ?: { focusManager.clearFocus() },
            onGo = keyboardActions.onGo,
            onNext = keyboardActions.onNext ?: { 
                focusManager.moveFocus(FocusDirection.Next)
                Unit
            },
            onPrevious = keyboardActions.onPrevious,
            onSearch = keyboardActions.onSearch,
            onSend = keyboardActions.onSend
        )
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
        // Structured M3 label above the input field
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                }
            )
        }

        // Standard, robust M3 OutlinedTextField with identical premium aesthetics
        OutlinedTextField(
            value = textFieldValueState,
            onValueChange = { newTfv ->
                if (!readOnly) {
                    var filteredText = newTfv.text
                    if (onlyDigits) {
                        filteredText = filteredText.filter { it.isDigit() }
                    } else if (onlyDigitsAndPlus) {
                        filteredText = filteredText.filter { it.isDigit() || it == '+' }
                    }

                    if (maxInputLength != null) {
                        filteredText = filteredText.take(maxInputLength)
                    }

                    val finalSelection = if (filteredText.length != newTfv.text.length) {
                        TextRange(newTfv.selection.end.coerceAtMost(filteredText.length))
                    } else {
                        newTfv.selection
                    }

                    val updatedTfv = newTfv.copy(text = filteredText, selection = finalSelection)
                    textFieldValueState = updatedTfv

                    if (filteredText != value) {
                        onValueChange(filteredText)
                    }
                }
            },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .then(
                                if (onLeadingIconClick != null) {
                                    Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = onLeadingIconClick
                                    )
                                } else Modifier
                            ),
                        tint = if (isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        }
                    )
                }
            },
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = resolvedKeyboardOptions,
            keyboardActions = resolvedKeyboardActions,
            visualTransformation = visualTransformation,
            enabled = enabled,
            readOnly = readOnly,
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
            modifier = Modifier.fillMaxWidth()
        )

        // Animated error message
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
