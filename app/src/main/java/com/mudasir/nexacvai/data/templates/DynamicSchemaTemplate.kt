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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mudasir.nexacvai.domain.model.template.*

/**
 * Dynamic template renderer for user-imported external template schemas.
 */
class DynamicSchemaTemplate(
    override val metadata: TemplateMetadata
) : ResumeTemplate {

    @Composable
    override fun Render(
        data: TemplateData,
        style: TemplateStyle,
        modifier: Modifier
    ) {
        val hasPhoto = metadata.supportsPhoto && style.showPhoto && !data.profilePictureUri.isNullOrBlank()

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(style.backgroundColor)
                .padding(20.dp)
        ) {
            // Adaptive Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(style.primaryColor.copy(alpha = 0.06f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasPhoto) {
                    AsyncImage(
                        model = data.profilePictureUri,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .border(2.dp, style.primaryColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.fullName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = style.primaryColor
                    )
                    if (data.professionalTitle.isNotBlank()) {
                        Text(
                            text = data.professionalTitle,
                            fontSize = 13.sp,
                            color = style.accentColor
                        )
                    }
                    Text(
                        text = listOfNotNull(
                            data.email.ifBlank { null },
                            data.phone.ifBlank { null },
                            data.location.ifBlank { null }
                        ).joinToString(" • "),
                        fontSize = 10.sp,
                        color = style.secondaryTextColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary
            if (data.summary.isNotBlank()) {
                Text(
                    text = "SUMMARY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.primaryColor
                )
                Divider(color = style.primaryColor.copy(alpha = 0.3f), thickness = 1.dp)
                Text(
                    text = data.summary,
                    fontSize = 11.sp,
                    color = style.textColor,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            // Experience
            if (data.experiences.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "EXPERIENCE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.primaryColor
                )
                Divider(color = style.primaryColor.copy(alpha = 0.3f), thickness = 1.dp)
                data.experiences.forEach { exp ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${exp.jobTitle} — ${exp.company}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = style.textColor
                            )
                            Text(
                                text = "${exp.startDate} - ${exp.endDate}",
                                fontSize = 10.sp,
                                color = style.secondaryTextColor
                            )
                        }
                        exp.responsibilities.forEach { resp ->
                            Text(
                                text = "• $resp",
                                fontSize = 11.sp,
                                color = style.textColor,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }
                }
            }

            // Education
            if (data.educations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "EDUCATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.primaryColor
                )
                Divider(color = style.primaryColor.copy(alpha = 0.3f), thickness = 1.dp)
                data.educations.forEach { edu ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${edu.degree}, ${edu.institution}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = style.textColor
                        )
                        Text(
                            text = "${edu.startDate} - ${edu.endDate}",
                            fontSize = 10.sp,
                            color = style.secondaryTextColor
                        )
                    }
                }
            }

            // Skills
            if (data.skills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SKILLS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.primaryColor
                )
                Divider(color = style.primaryColor.copy(alpha = 0.3f), thickness = 1.dp)
                Text(
                    text = data.skills.joinToString(" • "),
                    fontSize = 11.sp,
                    color = style.textColor,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
