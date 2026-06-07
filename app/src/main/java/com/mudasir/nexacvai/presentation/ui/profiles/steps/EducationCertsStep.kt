package com.mudasir.nexacvai.presentation.ui.profiles.steps

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Education
import com.mudasir.nexacvai.domain.model.Certification
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.EducationCard
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.CertificationCard
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.EducationCertsStepState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel

@Composable
fun EducationCertsStep(
    state: EducationCertsStepState,
    viewModel: CreateProfileViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Step Title
        item {
            Column {
                Text(
                    text = "Step 4: Education & Certifications",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter your academic credentials and verified industry certifications.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ================= EDUCATION SECTION =================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Education*",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add academic degrees, diplomas, or qualifications. At least 1 is required.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ================= EDUCATION ITEMS =================
        if (state.educations.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No education added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val addFirstEduInteractionSource = remember { MutableInteractionSource() }
                        val addFirstEduPressed by addFirstEduInteractionSource.collectIsPressedAsState()
                        val addFirstEduScale by animateFloatAsState(if (addFirstEduPressed) 0.98f else 1f, label = "addEduScale")

                        Button(
                            onClick = { viewModel.addEducation(Education()) },
                            interactionSource = addFirstEduInteractionSource,
                            modifier = Modifier.graphicsLayer(scaleX = addFirstEduScale, scaleY = addFirstEduScale),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Add Education")
                        }
                    }
                }
            }
        } else {
            itemsIndexed(
                items = state.educations,
                key = { _, edu -> edu.id }
            ) { index, edu ->
                EducationCard(
                    education = edu,
                    index = index,
                    onUpdateEducation = { updatedEdu ->
                        viewModel.updateEducation(edu.id, updatedEdu)
                    },
                    onRemoveEducation = {
                        viewModel.removeEducation(edu)
                    }
                )
            }

            item {
                val addMoreEduInteractionSource = remember { MutableInteractionSource() }
                val addMoreEduPressed by addMoreEduInteractionSource.collectIsPressedAsState()
                val addMoreEduScale by animateFloatAsState(if (addMoreEduPressed) 0.98f else 1f, label = "addMoreEduScale")

                OutlinedButton(
                    onClick = { viewModel.addEducation(Education()) },
                    interactionSource = addMoreEduInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = addMoreEduScale, scaleY = addMoreEduScale),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Add Another Education")
                }
            }
        }

        item {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // ================= CERTIFICATIONS SECTION =================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Certifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Highlight your licenses, credentials, or courses. This section is optional.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ================= CERTIFICATIONS ITEMS =================
        if (state.certifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No certifications added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val addFirstCertInteractionSource = remember { MutableInteractionSource() }
                        val addFirstCertPressed by addFirstCertInteractionSource.collectIsPressedAsState()
                        val addFirstCertScale by animateFloatAsState(if (addFirstCertPressed) 0.98f else 1f, label = "addCertScale")

                        Button(
                            onClick = { viewModel.addCertification(Certification()) },
                            interactionSource = addFirstCertInteractionSource,
                            modifier = Modifier.graphicsLayer(scaleX = addFirstCertScale, scaleY = addFirstCertScale),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Add Certification")
                        }
                    }
                }
            }
        } else {
            itemsIndexed(
                items = state.certifications,
                key = { _, cert -> cert.id }
            ) { index, cert ->
                CertificationCard(
                    certification = cert,
                    index = index,
                    onUpdateCertification = { updatedCert ->
                        viewModel.updateCertification(cert.id, updatedCert)
                    },
                    onRemoveCertification = {
                        viewModel.removeCertification(cert)
                    }
                )
            }

            item {
                val addAnotherCertInteractionSource = remember { MutableInteractionSource() }
                val addAnotherCertPressed by addAnotherCertInteractionSource.collectIsPressedAsState()
                val addAnotherCertScale by animateFloatAsState(if (addAnotherCertPressed) 0.98f else 1f, label = "addMoreCertScale")

                OutlinedButton(
                    onClick = { viewModel.addCertification(Certification()) },
                    interactionSource = addAnotherCertInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = addAnotherCertScale, scaleY = addAnotherCertScale),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Add Another Certification")
                }
            }
        }
    }
}
