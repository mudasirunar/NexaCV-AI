package com.mudasir.nexacvai.data.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudasir.nexacvai.domain.model.template.*

/**
 * Template 2: Executive Slate CV
 * Single-column Corporate ATS-focused text-only layout with bold header banner,
 * executive summary section, and high-density structured experience blocks.
 */
class ExecutiveSlateTemplate : ResumeTemplate {
    override val metadata: TemplateMetadata = TemplateMetadata(
        id = "template_executive_slate",
        name = "Executive Slate",
        description = "Corporate single-column ATS text-only layout with high-contrast header banner and executive typography.",
        category = TemplateCategory.EXECUTIVE,
        supportsPhoto = false,
        previewPrimaryColorHex = "#0F172A",
        previewAccentColorHex = "#475569"
    )

    @Composable
    override fun Render(
        data: TemplateData,
        style: TemplateStyle,
        modifier: Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(style.backgroundColor)
                .padding(24.dp)
        ) {
            // Header Banner
            Text(
                text = data.fullName.uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = style.primaryColor,
                letterSpacing = 1.5.sp
            )
            if (data.professionalTitle.isNotBlank()) {
                Text(
                    text = data.professionalTitle.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.accentColor,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            if (data.email.isNotBlank() || data.phone.isNotBlank() || data.location.isNotBlank()) {
                Text(
                    text = listOfNotNull(
                        data.email.ifBlank { null },
                        data.phone.ifBlank { null },
                        data.location.ifBlank { null }
                    ).joinToString("   |   "),
                    fontSize = 11.sp,
                    color = style.secondaryTextColor,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Divider(
                color = style.primaryColor,
                thickness = 2.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Executive Summary
            if (data.summary.isNotBlank()) {
                Text(
                    text = "EXECUTIVE SUMMARY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.primaryColor,
                    letterSpacing = 1.sp
                )
                Text(
                    text = data.summary,
                    fontSize = 11.sp,
                    color = style.textColor,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
            }

            // Work Experience
            if (data.experiences.isNotEmpty()) {
                Text(
                    text = "PROFESSIONAL EXPERIENCE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.primaryColor,
                    letterSpacing = 1.sp
                )
                Divider(
                    color = style.primaryColor.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                data.experiences.forEach { exp ->
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
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
                                text = "${exp.startDate} – ${exp.endDate}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = style.secondaryTextColor
                            )
                        }
                        exp.responsibilities.forEach { bullet ->
                            Text(
                                text = "•  $bullet",
                                fontSize = 11.sp,
                                color = style.textColor,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(start = 6.dp, top = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Education
            if (data.educations.isNotEmpty()) {
                Text(
                    text = "EDUCATION & CREDENTIALS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.primaryColor,
                    letterSpacing = 1.sp
                )
                Divider(
                    color = style.primaryColor.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
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
                            text = "${edu.startDate} – ${edu.endDate}",
                            fontSize = 11.sp,
                            color = style.secondaryTextColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Core Competencies / Skills
            if (data.skills.isNotEmpty()) {
                Text(
                    text = "CORE COMPETENCIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.primaryColor,
                    letterSpacing = 1.sp
                )
                Divider(
                    color = style.primaryColor.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = data.skills.joinToString("   •   "),
                    fontSize = 11.sp,
                    color = style.textColor,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
