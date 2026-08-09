package com.mudasir.nexacvai.data.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
 * Template 1: Modern Tech CV
 * Features a sleek 2-column sidebar layout, primary accent header,
 * circular profile avatar frame (dynamically collapsing if no photo), skill pills, and experience timeline.
 */
class ModernTechTemplate : ResumeTemplate {
    override val metadata: TemplateMetadata = TemplateMetadata(
        id = "template_modern_tech",
        name = "Modern Tech",
        description = "Sleek 2-column layout with primary accent sidebar, skill chips, and adaptive photo header.",
        category = TemplateCategory.MODERN,
        supportsPhoto = true,
        previewPrimaryColorHex = "#1E3A8A",
        previewAccentColorHex = "#3B82F6"
    )

    override val defaultData: TemplateData =
        com.mudasir.nexacvai.domain.model.template.sampledata.SampleGuidanceProfiles.MALE_TECH_ARCHITECT

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
                .padding(20.dp)
        ) {
            // Header Banner Section with Adaptive Photo Layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(style.primaryColor.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasPhoto) {
                    AsyncImage(
                        model = data.profilePictureUri,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, style.primaryColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = style.accentColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    if (data.email.isNotBlank() || data.phone.isNotBlank() || data.location.isNotBlank()) {
                        Text(
                            text = listOfNotNull(
                                data.email.ifBlank { null },
                                data.phone.ifBlank { null },
                                data.location.ifBlank { null }
                            ).joinToString("  •  "),
                            fontSize = 11.sp,
                            color = style.secondaryTextColor,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Professional Summary Section
            if (data.summary.isNotBlank()) {
                SectionHeader("PROFESSIONAL SUMMARY", style.primaryColor)
                Text(
                    text = data.summary,
                    fontSize = 12.sp,
                    color = style.textColor,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Work Experience Section
            if (data.experiences.isNotEmpty()) {
                SectionHeader("WORK EXPERIENCE", style.primaryColor)
                Spacer(modifier = Modifier.height(6.dp))
                data.experiences.forEach { exp ->
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = exp.jobTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = style.textColor
                            )
                            Text(
                                text = "${exp.startDate} - ${exp.endDate}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = style.accentColor
                            )
                        }
                        Text(
                            text = exp.company + if (exp.location.isNotBlank()) " • ${exp.location}" else "",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
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
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Projects Section
            if (data.projects.isNotEmpty()) {
                SectionHeader("KEY PROJECTS", style.primaryColor)
                Spacer(modifier = Modifier.height(6.dp))
                data.projects.forEach { proj ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = proj.projectName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = style.textColor
                            )
                            if (proj.startDate.isNotBlank()) {
                                Text(
                                    text = proj.startDate,
                                    fontSize = 11.sp,
                                    color = style.secondaryTextColor
                                )
                            }
                        }
                        if (proj.roleInProject.isNotBlank()) {
                            Text(
                                text = "Role: ${proj.roleInProject}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = style.accentColor
                            )
                        }
                        Text(
                            text = proj.description,
                            fontSize = 11.sp,
                            color = style.textColor,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Education Section
            if (data.educations.isNotEmpty()) {
                SectionHeader("EDUCATION", style.primaryColor)
                Spacer(modifier = Modifier.height(6.dp))
                data.educations.forEach { edu ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = edu.degree,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = style.textColor
                            )
                            Text(
                                text = edu.institution,
                                fontSize = 11.sp,
                                color = style.secondaryTextColor
                            )
                        }
                        Text(
                            text = "${edu.startDate} - ${edu.endDate}",
                            fontSize = 11.sp,
                            color = style.accentColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Skills Section
            if (data.skills.isNotEmpty()) {
                SectionHeader("SKILLS & TECHNOLOGIES", style.primaryColor)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = data.skills.joinToString("  •  "),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = style.textColor,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 1.sp
        )
        HorizontalDivider(
            color = color.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
