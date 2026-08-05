package com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.ui.theme.SearchMatchBorder
import com.mudasir.nexacvai.ui.theme.SearchMatchContainer
import com.mudasir.nexacvai.ui.theme.SearchNoMatchBorder
import com.mudasir.nexacvai.ui.theme.SearchNoMatchContainer

@Composable
fun ProfileSearchBar(
    query: String,
    hasResults: Boolean,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val isFilled = query.isNotBlank()

    val targetBgColor = when {
        !isFilled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        hasResults -> SearchMatchContainer.copy(alpha = 0.14f)
        else -> SearchNoMatchContainer.copy(alpha = 0.14f)
    }

    val targetBorderColor = when {
        !isFilled -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        hasResults -> SearchMatchBorder.copy(alpha = 0.35f)
        else -> SearchNoMatchBorder.copy(alpha = 0.35f)
    }

    val targetClearButtonBgColor = when {
        !isFilled -> MaterialTheme.colorScheme.surfaceVariant
        hasResults -> SearchMatchBorder
        else -> SearchNoMatchBorder
    }

    val targetClearButtonIconColor = when {
        !isFilled -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> Color.White
    }

    // Smooth color transitions
    val animatedBgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(durationMillis = 300),
        label = "searchBarBgColor"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(durationMillis = 300),
        label = "searchBarBorderColor"
    )

    val clearButtonBgColor by animateColorAsState(
        targetValue = targetClearButtonBgColor,
        animationSpec = tween(durationMillis = 300),
        label = "clearButtonBg"
    )

    val clearButtonIconColor by animateColorAsState(
        targetValue = targetClearButtonIconColor,
        animationSpec = tween(durationMillis = 300),
        label = "clearButtonIconColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        // Smooth background Canvas clipped strictly inside rounded corners
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = animatedBgColor)
        }

        // Foreground UI
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 6.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCloseSearch,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Close Search",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Normal
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                        }
                    )
                )
            }

            val isQueryNotEmpty = query.isNotEmpty()

            val clearIconRotation by animateFloatAsState(
                targetValue = if (isQueryNotEmpty) 0f else 360f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "clearIconSpin"
            )

            AnimatedVisibility(
                visible = isQueryNotEmpty,
                enter = fadeIn(animationSpec = tween(220)) + scaleIn(
                    initialScale = 0.2f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ),
                exit = fadeOut(animationSpec = tween(180)) + scaleOut(
                    targetScale = 0.2f,
                    animationSpec = tween(180)
                )
            ) {
                Surface(
                    onClick = { onQueryChange("") },
                    shape = CircleShape,
                    color = clearButtonBgColor,
                    border = BorderStroke(1.dp, animatedBorderColor),
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Text",
                            tint = clearButtonIconColor,
                            modifier = Modifier
                                .size(14.dp)
                                .graphicsLayer { rotationZ = clearIconRotation }
                        )
                    }
                }
            }
        }
    }
}