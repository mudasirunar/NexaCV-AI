package com.mudasir.nexacvai.presentation.ui.templates.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudasir.nexacvai.R
import com.mudasir.nexacvai.presentation.ui.components.NexaButton

@Composable
fun TemplateEmptyFavoritesScreen(
    onExploreAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        val isCompact = maxHeight < 360.dp
        val iconSize by animateDpAsState(
            targetValue = if (isCompact) 80.dp else 120.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "emptyFavIconSize"
        )
        val topSpacer by animateDpAsState(
            targetValue = if (isCompact) 8.dp else 14.dp,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "emptyFavTopSpacer"
        )
        val bottomSpacer by animateDpAsState(
            targetValue = if (isCompact) 10.dp else 18.dp,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "emptyFavBottomSpacer"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_favorites),
                contentDescription = "No Favorites",
                modifier = Modifier.size(iconSize)
            )

            Spacer(modifier = Modifier.height(topSpacer))

            Text(
                text = "No Favorites Yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isCompact) 15.sp else 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Star or bookmark your preferred resume layouts to quickly access them in one place.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = if (isCompact) 12.sp else 13.sp,
                    lineHeight = if (isCompact) 16.sp else 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(bottomSpacer))

            NexaButton(
                onClick = onExploreAllClick,
                text = "Explore All Templates",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                hasBorder = true,
                fillOpacity = 0.12f
            )
        }
    }
}
