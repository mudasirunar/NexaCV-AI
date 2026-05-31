package com.mudasir.nexacvai.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    onlyDigits: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    // Automatically set default ImeAction to Next for single line text fields
    val resolvedKeyboardOptions = remember(keyboardOptions, singleLine) {
        if (singleLine && (keyboardOptions.imeAction == ImeAction.Default || keyboardOptions.imeAction == ImeAction.None)) {
            keyboardOptions.copy(imeAction = ImeAction.Next)
        } else {
            keyboardOptions
        }
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

    // Smooth animated color transitions for focus/error states
    // Using animateColorAsState instead of raw remember — provides both proper
    // caching AND smooth visual transitions between states.
    val outlineColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        },
        animationSpec = tween(durationMillis = 150),
        label = "outlineColor"
    )
    val borderThickness by animateDpAsState(
        targetValue = if (isFocused || isError) 1.5.dp else 1.dp,
        animationSpec = tween(durationMillis = 150),
        label = "borderThickness"
    )
    
    // Premium, clean slightly off-surface input background color (Notion/Apple style)
    val inputBackgroundColor by animateColorAsState(
        targetValue = if (isFocused) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        },
        animationSpec = tween(durationMillis = 150),
        label = "inputBackgroundColor"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Premium structured label above the input field
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

        // Clean Input Field Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (singleLine || minLines == 1) 52.dp else 100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(inputBackgroundColor)
                .border(
                    width = borderThickness,
                    color = outlineColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(
                    start = 14.dp,
                    end = if (trailingIcon != null) 4.dp else 14.dp,
                    top = if (singleLine) 0.dp else 12.dp,
                    bottom = if (singleLine) 0.dp else 12.dp
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            // Track TextFieldValue internally to preserve cursor position
            var internalTfv by remember {
                mutableStateOf(
                    androidx.compose.ui.text.input.TextFieldValue(
                        text = value,
                        selection = androidx.compose.ui.text.TextRange(value.length)
                    )
                )
            }

            // Synchronous update during composition avoids 1-frame delay and stale text race conditions
            val textFieldValue = if (internalTfv.text != value) {
                val newSelection = if (!isFocused) {
                    androidx.compose.ui.text.TextRange(value.length)
                } else {
                    androidx.compose.ui.text.TextRange(
                        internalTfv.selection.start.coerceIn(0, value.length),
                        internalTfv.selection.end.coerceIn(0, value.length)
                    )
                }
                val newTfv = internalTfv.copy(text = value, selection = newSelection)
                internalTfv = newTfv
                newTfv
            } else {
                internalTfv
            }

            // Move cursor to end when field gains focus (specifically helpful for Keyboard 'Next' navigation)
            LaunchedEffect(isFocused) {
                if (isFocused && internalTfv.text.isNotEmpty()) {
                    internalTfv = internalTfv.copy(
                        selection = androidx.compose.ui.text.TextRange(internalTfv.text.length)
                    )
                }
            }

            // Raw text field underneath for 100% custom styling
            androidx.compose.foundation.text.BasicTextField(
                value = textFieldValue,
                onValueChange = { newTfv ->
                    if (!readOnly) {
                        // 1. Apply strict filtering immediately to internal state
                        var filteredText = newTfv.text
                        if (onlyDigits) {
                            filteredText = filteredText.filter { it.isDigit() }
                        }
                        if (maxInputLength != null) {
                            filteredText = filteredText.take(maxInputLength)
                        }

                        // 2. Adjust selection if filtering happened to avoid crash/jumps
                        val effectiveTfv = if (filteredText.length != newTfv.text.length) {
                            newTfv.copy(
                                text = filteredText,
                                selection = androidx.compose.ui.text.TextRange(
                                    newTfv.selection.start.coerceIn(0, filteredText.length),
                                    newTfv.selection.end.coerceIn(0, filteredText.length)
                                )
                            )
                        } else {
                            newTfv
                        }

                        // 3. Update internal state
                        internalTfv = effectiveTfv

                        // 4. Notify parent ONLY if the text actually changed compared to current prop value
                        if (effectiveTfv.text != value) {
                            onValueChange(effectiveTfv.text)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (leadingIcon != null) 30.dp else 0.dp,
                        end = if (trailingIcon != null) 44.dp else 0.dp
                    ),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                ),
                singleLine = singleLine,
                minLines = minLines,
                maxLines = maxLines,
                keyboardOptions = resolvedKeyboardOptions,
                keyboardActions = resolvedKeyboardActions,
                visualTransformation = visualTransformation,
                enabled = enabled,
                readOnly = readOnly,
                interactionSource = interactionSource,
                cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterStart)
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
                    } else if (isFocused) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    }
                )
            }

            // Trailing Icon positioning
            if (trailingIcon != null) {
                Box(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    contentAlignment = Alignment.Center
                ) {
                    trailingIcon()
                }
            }
        }

        // Animate Error message below the field with smooth reveal transition
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
