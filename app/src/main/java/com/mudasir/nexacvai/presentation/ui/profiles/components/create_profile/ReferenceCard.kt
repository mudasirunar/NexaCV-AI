package com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

@Composable
fun ReferenceCard(
    reference: Reference,
    index: Int,
    onUpdateReference: (Reference) -> Unit,
    onRemoveReference: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (reference.fullName.isNotBlank()) reference.fullName else "Reference #${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                val removeInteractionSource = remember { MutableInteractionSource() }
                val removePressed by removeInteractionSource.collectIsPressedAsState()
                val removeScale by animateFloatAsState(if (removePressed) 0.98f else 1f, label = "removeRefScale")

                IconButton(
                    onClick = onRemoveReference,
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
                value = reference.fullName,
                onValueChange = { onUpdateReference(reference.copy(fullName = it)) },
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
                value = reference.jobTitle,
                onValueChange = { onUpdateReference(reference.copy(jobTitle = it)) },
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
                value = reference.company,
                onValueChange = { onUpdateReference(reference.copy(company = it)) },
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
                value = reference.email ?: "",
                onValueChange = { onUpdateReference(reference.copy(email = it.takeIf { it.isNotBlank() })) },
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
                value = reference.phone ?: "",
                onValueChange = { onUpdateReference(reference.copy(phone = it.takeIf { it.isNotBlank() })) },
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
                value = reference.linkedInUrl ?: "",
                onValueChange = { onUpdateReference(reference.copy(linkedInUrl = it.takeIf { it.isNotBlank() })) },
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
                value = reference.notes ?: "",
                onValueChange = { onUpdateReference(reference.copy(notes = it.takeIf { it.isNotBlank() })) },
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
