package com.mudasir.nexacvai.presentation.ui.templates.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudasir.nexacvai.presentation.ui.components.NexaButton

@Composable
fun TemplateEmptyCustomScreen(
    onCreateCustomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        val isCompact = maxHeight < 360.dp
        val badgeSize by animateDpAsState(
            targetValue = if (isCompact) 80.dp else 120.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "emptyCustomBadgeSize"
        )
        val iconSize by animateDpAsState(
            targetValue = if (isCompact) 40.dp else 56.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "emptyCustomIconSize"
        )
        val topSpacer by animateDpAsState(
            targetValue = if (isCompact) 8.dp else 14.dp,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "emptyCustomTopSpacer"
        )
        val bottomSpacer by animateDpAsState(
            targetValue = if (isCompact) 10.dp else 18.dp,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "emptyCustomBottomSpacer"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = "Custom Templates",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize)
                )
            }

            Spacer(modifier = Modifier.height(topSpacer))

            Text(
                text = "No Custom Templates Yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isCompact) 15.sp else 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Design personalized CV layouts with custom color palettes, or let AI generate a tailored style for you.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = if (isCompact) 12.sp else 13.sp,
                    lineHeight = if (isCompact) 16.sp else 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(bottomSpacer))

            NexaButton(
                onClick = onCreateCustomClick,
                text = "Create Custom Template",
                icon = Icons.Default.Add,
                hasBorder = true,
                fillOpacity = 0.12f
            )
        }
    }
}
