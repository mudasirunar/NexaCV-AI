package com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.domain.model.ProfileSortOrder

@Composable
fun ProfilesOverflowMenu(
    isProfilesEmpty: Boolean,
    currentSortOrder: ProfileSortOrder,
    onImportClick: () -> Unit,
    onExportAllClick: () -> Unit,
    onSelectProfilesClick: () -> Unit,
    onSortOrderSelected: (ProfileSortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isSortSubMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(isMenuExpanded) {
        if (!isMenuExpanded) {
            isSortSubMenuExpanded = false
        }
    }

    Box(modifier = modifier) {
        IconButton(onClick = { isMenuExpanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = {
                isMenuExpanded = false
                isSortSubMenuExpanded = false
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .width(220.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            DropdownMenuItem(
                text = { Text("Import Profile(s)") },
                onClick = {
                    isMenuExpanded = false
                    isSortSubMenuExpanded = false
                    onImportClick()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = "Export All Profiles",
                        color = if (isProfilesEmpty) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                },
                enabled = !isProfilesEmpty,
                onClick = {
                    if (!isProfilesEmpty) {
                        isMenuExpanded = false
                        isSortSubMenuExpanded = false
                        onExportAllClick()
                    }
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = "Select Profile(s)",
                        color = if (isProfilesEmpty) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                },
                enabled = !isProfilesEmpty,
                onClick = {
                    if (!isProfilesEmpty) {
                        isMenuExpanded = false
                        isSortSubMenuExpanded = false
                        onSelectProfilesClick()
                    }
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Sorting menu item with springy animated trailing chevron arrow
            val chevronRotation by animateFloatAsState(
                targetValue = if (isSortSubMenuExpanded) 180f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "sortChevronRotation"
            )

            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Sort Icon",
                        tint = if (isProfilesEmpty) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = {
                    Text(
                        text = "Sort Profiles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isProfilesEmpty) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = FontWeight.Medium
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand Sorting Options",
                        modifier = Modifier.graphicsLayer { rotationZ = chevronRotation }
                    )
                },
                enabled = !isProfilesEmpty,
                onClick = {
                    if (!isProfilesEmpty) {
                        isSortSubMenuExpanded = !isSortSubMenuExpanded
                    }
                }
            )

            // Smooth springy expandable container using single-pass layout animateContentSize
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
            ) {
                if (isSortSubMenuExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        ProfileSortOrder.entries.forEach { sortOption ->
                            val isSelected = currentSortOrder == sortOption
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = sortOption.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                trailingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected Sort Option",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                onClick = {
                                    isMenuExpanded = false
                                    isSortSubMenuExpanded = false
                                    onSortOrderSelected(sortOption)
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
