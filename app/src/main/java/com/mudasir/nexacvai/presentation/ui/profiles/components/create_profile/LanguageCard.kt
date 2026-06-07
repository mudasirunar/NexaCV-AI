package com.mudasir.nexacvai.presentation.ui.profiles.components.create_profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.Language
import com.mudasir.nexacvai.presentation.ui.components.NexaTextField

private data class ProficiencyLevel(
    val code: String,
    val label: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageCard(
    language: Language,
    index: Int,
    onUpdateLanguage: (Language) -> Unit,
    onRemoveLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val proficiencyLevels = remember {
        listOf(
            ProficiencyLevel("A1", "Beginner", "Can understand basic daily phrases"),
            ProficiencyLevel("A2", "Elementary", "Can speak simple sentences on familiar topics"),
            ProficiencyLevel("B1", "Intermediate", "Can communicate in everyday work situations"),
            ProficiencyLevel("B2", "Upper-Intermediate", "Can converse fluently on complex subjects"),
            ProficiencyLevel("C1", "Advanced / Fluent", "Can express ideas spontaneously and flexibly"),
            ProficiencyLevel("C2", "Proficient / Native", "Can speak and write with complete native ease")
        )
    }

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
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (language.languageName.isNotBlank()) language.languageName else "Language #${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                val removeLangInteractionSource = remember { MutableInteractionSource() }
                val removeLangPressed by removeLangInteractionSource.collectIsPressedAsState()
                val removeLangScale by animateFloatAsState(if (removeLangPressed) 0.98f else 1f, label = "removeLangScale")

                IconButton(
                    onClick = onRemoveLanguage,
                    interactionSource = removeLangInteractionSource,
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer(scaleX = removeLangScale, scaleY = removeLangScale)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Language",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            NexaTextField(
                value = language.languageName,
                onValueChange = { onUpdateLanguage(language.copy(languageName = it)) },
                label = "Language Name*",
                placeholder = "e.g. English, Urdu, German",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            var isProfExpanded by remember { mutableStateOf(false) }
            var profItemWidth by remember { mutableStateOf(0) }
            val density = LocalDensity.current
            val profItemWidthDp = with(density) { profItemWidth.toDp() }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        profItemWidth = coordinates.size.width
                    }
            ) {
                NexaTextField(
                    value = language.proficiency,
                    onValueChange = {},
                    label = "Proficiency*",
                    placeholder = "Select Proficiency Level",
                    trailingIcon = {
                        Icon(
                            imageVector = if (isProfExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    enabled = true,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isProfExpanded = true
                        }
                )

                val profDropdownWidthDp = if (profItemWidthDp > 0.dp) profItemWidthDp.coerceAtMost(400.dp) else 280.dp
                val profOffsetX = if (profItemWidthDp > 400.dp) (profItemWidthDp - 400.dp) / 2 else 0.dp

                DropdownMenu(
                    expanded = isProfExpanded,
                    onDismissRequest = { isProfExpanded = false },
                    offset = DpOffset(x = profOffsetX, y = 0.dp),
                    modifier = Modifier
                        .width(profDropdownWidthDp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    proficiencyLevels.forEachIndexed { i, level ->
                        DropdownMenuItem(
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ) {
                                            Text(
                                                text = level.code,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = level.label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = level.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                isProfExpanded = false
                                onUpdateLanguage(language.copy(proficiency = "${level.code} - ${level.label}"))
                            }
                        )
                        if (i < proficiencyLevels.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
