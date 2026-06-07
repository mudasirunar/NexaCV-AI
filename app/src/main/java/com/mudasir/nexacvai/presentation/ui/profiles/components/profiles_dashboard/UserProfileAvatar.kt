package com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.ui.theme.*

@Composable
fun UserProfileAvatar(
    profile: UserProfile,
    colorPair: AvatarColorPair,
    initials: String,
    completionProgress: Float,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(68.dp),
        contentAlignment = Alignment.Center
    ) {
        val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        val progressColor = remember(completionProgress) {
            when {
                completionProgress < 0.25f -> ProgressRed
                completionProgress < 0.50f -> ProgressOrange
                completionProgress < 0.70f -> ProgressYellow
                completionProgress < 1.00f -> ProgressBlue
                else -> ProgressGreen
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = if (completionProgress >= 1.0f) 3.dp.toPx() else 2.dp.toPx()
            val arcSize = 60.dp.toPx()
            val topLeftOffset = (size.width - arcSize) / 2

            // Draw background track
            drawCircle(
                color = outlineColor,
                radius = arcSize / 2,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Draw progress arc (fully connected solid path, no hardcoded colors)
            if (completionProgress > 0f) {
                val sweepAngle = 360f * completionProgress
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(topLeftOffset, topLeftOffset),
                    size = Size(arcSize, arcSize),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        // PFP (60dp x 60dp) centered inside 68dp box to remove gap spacing
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(colorPair.background)
                .clickable { onAvatarClick() },
            contentAlignment = Alignment.Center
        ) {
            if (profile.profilePictureUri != null) {
                val context = LocalContext.current
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(profile.profilePictureUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorPair.text
                    )
                )
            }
        }
    }
}
