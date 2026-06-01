package com.mudasir.nexacvai.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.UserProfile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Folder
import com.mudasir.nexacvai.ui.theme.IconColorJob
import com.mudasir.nexacvai.ui.theme.IconColorProject
import com.mudasir.nexacvai.ui.theme.IconColorCert

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserProfileStatPills(
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    val expCount = profile.experiences.size
    val projCount = profile.projects.size
    val certCount = profile.certifications.size

    if (expCount > 0 || projCount > 0 || certCount > 0) {
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (expCount > 0) {
                StatPill(
                    icon = Icons.Default.BusinessCenter,
                    text = "$expCount ${if (expCount == 1) "Job" else "Jobs"}",
                    iconColor = IconColorJob
                )
            }
            if (projCount > 0) {
                StatPill(
                    icon = Icons.Default.Folder,
                    text = "$projCount ${if (projCount == 1) "Project" else "Projects"}",
                    iconColor = IconColorProject
                )
            }
            if (certCount > 0) {
                StatPill(
                    icon = Icons.Default.CardMembership,
                    text = "$certCount ${if (certCount == 1) "Certificate" else "Certificates"}",
                    iconColor = IconColorCert
                )
            }
        }
    }
}

@Composable
private fun StatPill(
    icon: ImageVector,
    text: String,
    iconColor: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
