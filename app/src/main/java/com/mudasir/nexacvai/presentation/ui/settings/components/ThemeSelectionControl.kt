package com.mudasir.nexacvai.presentation.ui.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudasir.nexacvai.domain.model.AppThemeMode

/**
 * Liquid Sliding Segmented Theme Selector Control.
 * Features customizable pill colors per theme mode (Yellowish for Light, Bluish for Dark, Primary Blue for System),
 * preloaded instant startup positioning (zero sliding glitch), and pop-out icon scale effects.
 */
@Composable
fun ThemeSelectionControl(
    selectedTheme: AppThemeMode,
    onThemeSelected: (AppThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = AppThemeMode.values()
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "App Theme",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Text(
            text = "Choose your preferred interface appearance",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(4.dp)
        ) {
            val totalWidth = maxWidth
            val segmentWidth = totalWidth / modes.size
            val selectedIndex = modes.indexOf(selectedTheme)

            // Prevent startup slide glitch by using snap() until initial composition is attached
            var isInitialized by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                isInitialized = true
            }

            // Target Pill Colors per Theme Mode
            val (targetPillBg, targetPillBorder) = when (selectedTheme) {
                AppThemeMode.LIGHT -> Pair(
                    Color(0xFFF59E0B).copy(alpha = 0.16f), // Yellowish / Amber Fill
                    Color(0xFFF59E0B).copy(alpha = 0.42f)  // Yellowish / Amber Border
                )
                AppThemeMode.DARK -> Pair(
                    Color(0xFF38BDF8).copy(alpha = 0.16f), // Bluish Fill
                    Color(0xFF38BDF8).copy(alpha = 0.42f)  // Bluish Border
                )
                AppThemeMode.SYSTEM -> Pair(
                    primaryColor.copy(alpha = 0.16f),      // App Primary Blue Fill
                    primaryColor.copy(alpha = 0.42f)       // App Primary Blue Border
                )
            }

            val animatedPillBg by animateColorAsState(
                targetValue = targetPillBg,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "PillBgColorAnim"
            )

            val animatedPillBorder by animateColorAsState(
                targetValue = targetPillBorder,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "PillBorderColorAnim"
            )

            // Animated Liquid Sliding Pill Offset
            val animatedPillOffset by animateDpAsState(
                targetValue = segmentWidth * selectedIndex,
                animationSpec = if (!isInitialized) snap() else spring(
                    dampingRatio = 0.72f,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "LiquidPillOffsetAnimation"
            )

            // Sliding Liquid Pill Indicator
            Box(
                modifier = Modifier
                    .offset(x = animatedPillOffset)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(14.dp))
                    .background(animatedPillBg)
                    .border(
                        width = 1.dp,
                        color = animatedPillBorder,
                        shape = RoundedCornerShape(14.dp)
                    )
            )

            // 3 Segment Options Row
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                modes.forEach { mode ->
                    val isSelected = mode == selectedTheme

                    // Pop-out Scale Animation for Selected Icon & Text
                    val popScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.14f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = 0.6f,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "IconPopScaleAnimation"
                    )

                    // Distinct Pop Colors for Selected Mode Icons & Text
                    val activeColor = when (mode) {
                        AppThemeMode.LIGHT -> Color(0xFFD97706) // Yellowish / Amber Gold Sun
                        AppThemeMode.DARK -> Color(0xFF38BDF8)  // Vibrant Bluish Sky Moon
                        AppThemeMode.SYSTEM -> primaryColor      // App Primary Blue
                    }

                    Box(
                        modifier = Modifier
                            .width(segmentWidth)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (mode != selectedTheme) {
                                    onThemeSelected(mode)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.scale(popScale)
                        ) {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = mode.displayName,
                                tint = if (isSelected) {
                                    activeColor
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                },
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = mode.displayName,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (isSelected) {
                                        activeColor
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
