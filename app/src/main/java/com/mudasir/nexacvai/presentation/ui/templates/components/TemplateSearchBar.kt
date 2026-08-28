package com.mudasir.nexacvai.presentation.ui.templates.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TemplateSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "Search..."
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val closeIconRotation = remember { Animatable(0f) }
    var clearSpinTrigger by remember { mutableIntStateOf(0) }
    var closeSpinTrigger by remember { mutableIntStateOf(0) }

    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = query,
                selection = TextRange(query.length)
            )
        )
    }

    LaunchedEffect(query) {
        if (textFieldValueState.text != query) {
            textFieldValueState = TextFieldValue(
                text = query,
                selection = TextRange(query.length)
            )
        }
    }

    // Animation 1: On Clear -> Spin left to -90° and immediately spin back to 0° (unbroken go-and-come-back movement)
    LaunchedEffect(clearSpinTrigger) {
        if (clearSpinTrigger > 0) {
            closeIconRotation.animateTo(
                targetValue = -90f,
                animationSpec = tween(durationMillis = 110, easing = FastOutSlowInEasing)
            )
            closeIconRotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 130, easing = FastOutSlowInEasing)
            )
        }
    }

    // Animation 2: On Close -> Quick single left spin to -90° as search bar closes
    LaunchedEffect(closeSpinTrigger) {
        if (closeSpinTrigger > 0) {
            closeIconRotation.animateTo(
                targetValue = -90f,
                animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
            )
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
            isFocused = false
            closeIconRotation.snapTo(0f)
        }
    }

    val isQueryNotEmpty = query.isNotEmpty()
    val isSearchActive = isQueryNotEmpty || isFocused

    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon:
            // - Inactive: Search icon
            // - Active: Simple X button with distinct left-spin-and-back or single left-spin animation
            if (isSearchActive) {
                IconButton(
                    onClick = {
                        if (isQueryNotEmpty) {
                            // Clear text -> Trigger left spin and back
                            clearSpinTrigger++
                            onClearQuery()
                            textFieldValueState = TextFieldValue("")
                        } else {
                            // Close search -> Trigger single left spin exit
                            closeSpinTrigger++
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = if (isQueryNotEmpty) "Clear Text" else "Close Search",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { rotationZ = closeIconRotation.value }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Input Field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholderText,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                        fontWeight = FontWeight.Normal
                    )
                }

                BasicTextField(
                    value = textFieldValueState,
                    onValueChange = { newValue ->
                        textFieldValueState = newValue
                        if (newValue.text != query) {
                            onQueryChange(newValue.text)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                )
            }
        }
    }
}
