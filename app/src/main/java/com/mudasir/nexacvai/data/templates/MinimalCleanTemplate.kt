package com.mudasir.nexacvai.data.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mudasir.nexacvai.domain.model.template.*

/**
 * Template 3: Minimalist Clean CV
 * Ultra-clean high-density layout with adaptive photo frame, subtle divider rules,
 * and elegant typography pairing.
 */
class MinimalCleanTemplate : ResumeTemplate {
    override val metadata: TemplateMetadata = TemplateMetadata(
        id = "template_minimal_clean",
        name = "Minimalist Clean",
        description = "Ultra-clean minimalist layout with subtle accent lines, high typography contrast, and adaptive photo frame.",
        category = TemplateCategory.MINIMAL,
        supportsPhoto = true,
        previewPrimaryColorHex = "#27272A",
        previewAccentColorHex = "#52525B"
    )

    @Composable
    override fun Render(
        data: TemplateData,
        style: TemplateStyle,
        modifier: Modifier
    ) {
        val hasPhoto = style.showPhoto && !data.profilePictureUri.isNullOrBlank()

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(style.backgroundColor)
                .padding(24.dp)
        ) {
            // Adaptive Header Layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.fullName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Serif,
                        color = style.textColor
                    )
                    if (data.professionalTitle.isNotBlank()) {
                        Text(
                            text = data.professionalTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = style.secondaryTextColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                if (hasPhoto) {
                    Spacer(modifier = Modifier.width(16.dp))
                    AsyncImage(
                        model = data.profilePictureUri,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(1.dp, style.secondaryTextColor.copy(alpha = 0.4f), CircleShape)
                    )
                }
            }

            // Contact Info Line
            if (data.email.isNotBlank() || data.phone.isNotBlank() || data.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = listOfNotNull(
                        data.email.ifBlank { null },
                        data.phone.ifBlank { null },
                        data.location.ifBlank { null }
                    ).joinToString("  |  "),
                    fontSize = 10.sp,
                    color = style.secondaryTextColor
                )
            }

            Divider(
                color = Color.LightGray.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 14.dp)
            )

            // Summary
            if (data.summary.isNotBlank()) {
                MinimalSectionTitle("ABOUT")
                Text(
                    text = data.summary,
                    fontSize = 11.sp,
                    color = style.textColor,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Experience
            if (data.experiences.isNotEmpty()) {
                MinimalSectionTitle("EXPERIENCE")
                Spacer(modifier = Modifier.height(6.dp))
                data.experiences.forEach { exp ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = exp.jobTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = style.textColor
                            )
                            Text(
                                text = "${exp.startDate} – ${exp.endDate}",
                                fontSize = 10.sp,
                                color = style.secondaryTextColor
                            )
                        }
                        Text(
                            text = exp.company,
                            fontSize = 11.sp,
                            color = style.secondaryTextColor
                        )
                        exp.responsibilities.forEach { resp ->
                            Text(
                                text = "• $resp",
                                fontSize = 11.sp,
                                color = style.textColor,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Education
            if (data.educations.isNotEmpty()) {
                MinimalSectionTitle("EDUCATION")
                Spacer(modifier = Modifier.height(6.dp))
                data.educations.forEach { edu ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${edu.degree} — ${edu.institution}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = style.textColor
                        )
                        Text(
                            text = "${edu.startDate} – ${edu.endDate}",
                            fontSize = 10.sp,
                            color = style.secondaryTextColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Skills
            if (data.skills.isNotEmpty()) {
                MinimalSectionTitle("SKILLS")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.skills.joinToString("  •  "),
                    fontSize = 11.sp,
                    color = style.textColor,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun MinimalSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = Color(0xFF52525B)
    )
}
