package com.mudasir.nexacvai.presentation.ui.profiles.steps

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Education
import com.mudasir.nexacvai.domain.model.Certification
import com.mudasir.nexacvai.presentation.ui.components.NexaButton
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.EducationCard
import com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile.CertificationCard
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.EducationCertsStepState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun EducationCertsStep(
    state: EducationCertsStepState,
    viewModel: CreateProfileViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    LaunchedEffect(state.validationTrigger) {
        if (state.validationTrigger > 0L) {
            if (state.educationError != null || state.certificationError != null) {
                focusManager.clearFocus()
                keyboardController?.hide()
                if (state.educationError != null) {
                    // Scroll to Education section header where the error banner is displayed
                    listState.animateScrollToItem(1)
                } else if (state.certificationError != null) {
                    // Scroll to Certification section header where the error banner is displayed
                    val eduCount = if (state.educations.isEmpty()) 1 else state.educations.size + 1
                    val certHeaderIndex = 3 + eduCount
                    listState.animateScrollToItem(certHeaderIndex)
                }
            }
        }
    }

    LazyColumn(
        state = listState,
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

                AnimatedVisibility(
                    visible = state.educationError != null,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Education Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = state.educationError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
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

                        NexaButton(
                            onClick = { viewModel.addEducation(Education()) },
                            text = "+ Add Education",
                            modifier = Modifier.fillMaxWidth(),
                            hasBorder = true,
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            fillColor = MaterialTheme.colorScheme.primary,
                            fillOpacity = 0.08f,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
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
                NexaButton(
                    onClick = { viewModel.addEducation(Education()) },
                    text = "+ Add Another Education",
                    modifier = Modifier.fillMaxWidth(),
                    hasBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    fillColor = MaterialTheme.colorScheme.primary,
                    fillOpacity = 0.08f,
                    contentColor = MaterialTheme.colorScheme.primary
                )
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

                AnimatedVisibility(
                    visible = state.certificationError != null,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Certification Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = state.certificationError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
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

                        NexaButton(
                            onClick = { viewModel.addCertification(Certification()) },
                            text = "+ Add Certification",
                            hasBorder = true,
                            borderColor = MaterialTheme.colorScheme.primary,
                            fillColor = MaterialTheme.colorScheme.primary,
                            fillOpacity = 0.12f,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
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
                NexaButton(
                    onClick = { viewModel.addCertification(Certification()) },
                    text = "+ Add Another Certification",
                    modifier = Modifier.fillMaxWidth(),
                    hasBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    fillColor = MaterialTheme.colorScheme.primary,
                    fillOpacity = 0.08f,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
