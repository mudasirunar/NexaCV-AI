package com.mudasir.nexacvai.presentation.ui.profiles.steps

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Reference
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.utils.NexaCheckbox

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReferencesStep(state: CreateProfileState, viewModel: CreateProfileViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Step 7: References", 
            style = MaterialTheme.typography.titleSmall, 
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Add professionals who can verify your skills and experience. References are optional.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.references.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person, 
                        contentDescription = null, 
                        modifier = Modifier.size(48.dp), 
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No references added yet", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val addInteractionSource = remember { MutableInteractionSource() }
                    val addPressed by addInteractionSource.collectIsPressedAsState()
                    val addScale by animateFloatAsState(if (addPressed) 0.98f else 1f, label = "addScale")
                    
                    Button(
                        onClick = { viewModel.addReference(Reference()) },
                        interactionSource = addInteractionSource,
                        modifier = Modifier.graphicsLayer(scaleX = addScale, scaleY = addScale),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Add Reference")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                state.references.forEachIndexed { index, ref ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp, 
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Card Header Row with dynamic limits
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (ref.fullName.isNotBlank()) ref.fullName else "Reference #${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 3,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                                
                                val removeInteractionSource = remember { MutableInteractionSource() }
                                val removePressed by removeInteractionSource.collectIsPressedAsState()
                                val removeScale by animateFloatAsState(if (removePressed) 0.98f else 1f, label = "removeScale")
                                
                                IconButton(
                                    onClick = { viewModel.removeReference(ref) },
                                    interactionSource = removeInteractionSource,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .graphicsLayer(scaleX = removeScale, scaleY = removeScale)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Reference",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                            NexaTextField(
                                value = ref.fullName,
                                onValueChange = { viewModel.updateReference(ref.id, ref.copy(fullName = it)) },
                                label = "Full Name*",
                                placeholder = "e.g. John Doe",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = ref.jobTitle,
                                onValueChange = { viewModel.updateReference(ref.id, ref.copy(jobTitle = it)) },
                                label = "Job Title*",
                                placeholder = "e.g. Product Manager",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = ref.company,
                                onValueChange = { viewModel.updateReference(ref.id, ref.copy(company = it)) },
                                label = "Company / Organization*",
                                placeholder = "e.g. Google",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = ref.email ?: "",
                                onValueChange = { viewModel.updateReference(ref.id, ref.copy(email = it.takeIf { it.isNotBlank() })) },
                                label = "Email Address",
                                placeholder = "e.g. reference@company.com",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = ref.phone ?: "",
                                onValueChange = { viewModel.updateReference(ref.id, ref.copy(phone = it.takeIf { it.isNotBlank() })) },
                                label = "Phone Number",
                                placeholder = "e.g. +1 234 567 890",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = ref.linkedInUrl ?: "",
                                onValueChange = { viewModel.updateReference(ref.id, ref.copy(linkedInUrl = it.takeIf { it.isNotBlank() })) },
                                label = "LinkedIn URL",
                                placeholder = "e.g. linkedin.com/in/username",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    keyboardType = KeyboardType.Uri,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            NexaTextField(
                                value = ref.notes ?: "",
                                onValueChange = { viewModel.updateReference(ref.id, ref.copy(notes = it.takeIf { it.isNotBlank() })) },
                                label = "Notes",
                                placeholder = "Any additional context about this reference...",
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = false,
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val addMoreInteractionSource = remember { MutableInteractionSource() }
                val addMorePressed by addMoreInteractionSource.collectIsPressedAsState()
                val addMoreScale by animateFloatAsState(if (addMorePressed) 0.98f else 1f, label = "addMoreScale")

                OutlinedButton(
                    onClick = { viewModel.addReference(Reference()) },
                    interactionSource = addMoreInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(scaleX = addMoreScale, scaleY = addMoreScale),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Add Another Reference")
                }
            }
        }
    }
}
