package com.mudasir.nexacvai.presentation.ui.templates

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mudasir.nexacvai.domain.model.template.TemplateCategory
import com.mudasir.nexacvai.presentation.ui.templates.components.*
import com.mudasir.nexacvai.presentation.ui.templates.viewmodel.TemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    onNavigateBack: () -> Unit = {},
    onOpenTemplatePreview: (templateId: String) -> Unit = {},
    viewModel: TemplatesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showImportDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFlippedTemplates()
        }
    }

    // Scroll state & responsive collapsible header controller
    val gridState = rememberLazyGridState()
    var isHeaderVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < -10f && isHeaderVisible) {
                    // Scrolling down -> smoothly slide up floating header
                    isHeaderVisible = false
                } else if (delta > 10f && !isHeaderVisible) {
                    // Scrolling up -> smoothly slide down floating header
                    isHeaderVisible = true
                }
                return Offset.Zero
            }
        }
    }

    // Ensure header is always visible when at the very top of the list or on search/category change
    LaunchedEffect(gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset, state.searchQuery, state.selectedCategory) {
        if (gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset <= 12) {
            isHeaderVisible = true
        }
    }

    // Smoothly scroll back to top when search query or filter category changes
    LaunchedEffect(state.searchQuery, state.selectedCategory) {
        if (state.filteredTemplates.isNotEmpty()) {
            gridState.scrollToItem(0)
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTabletOrFoldableUnfolded = configuration.smallestScreenWidthDp >= 580 || configuration.screenWidthDp >= 580

    // Responsive grid column span matrix:
    // - Phone / Folded Closed (Portrait): 2 cards
    // - Phone / Folded Closed (Landscape): 3 cards
    // - Tablet / Foldable Unfolded (Portrait): 3 cards
    // - Tablet / Foldable Unfolded (Landscape): 4 cards
    val gridColumnCount = when {
        isTabletOrFoldableUnfolded -> if (isLandscape) 4 else 3
        isLandscape -> 3
        else -> 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Text(
                        text = "CV Templates",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { showImportDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Import Custom Template",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Floating Header Overlay Architecture:
        // Grid fills the screen with static top contentPadding so cards NEVER jump or shift when header slides
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Base Layer: Templates Grid View
            if (state.isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumnCount),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 14.dp, top = 116.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(6) {
                        TemplateCardSkeleton()
                    }
                }
            } else if (state.filteredTemplates.isEmpty()) {
                TemplateEmptySearchScreen(
                    query = state.searchQuery,
                    onClearSearchClick = { viewModel.updateSearchQuery("") },
                    modifier = Modifier.padding(top = 110.dp)
                )
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(gridColumnCount),
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 14.dp, top = 116.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredTemplates, key = { it.metadata.id }) { template ->
                        TemplateCard(
                            template = template,
                            isFlipped = state.flippedTemplateIds.contains(template.metadata.id),
                            onToggleFlip = { viewModel.toggleTemplateFlip(template.metadata.id) },
                            onSelectTemplate = { onOpenTemplatePreview(template.metadata.id) }
                        )
                    }
                }
            }

            // 2. Floating Top Layer: Search Bar & Category Filter Chips
            AnimatedVisibility(
                visible = isHeaderVisible,
                modifier = Modifier.align(Alignment.TopCenter),
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                ) + fadeIn(animationSpec = tween(durationMillis = 180)),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                ) + fadeOut(animationSpec = tween(durationMillis = 140))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
                        .padding(top = 10.dp, bottom = 8.dp)
                ) {
                    // Modern Search Bar
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        TemplateSearchBar(
                            query = state.searchQuery,
                            onQueryChange = { viewModel.updateSearchQuery(it) },
                            onClearQuery = { viewModel.updateSearchQuery("") },
                            placeholderText = "Search...",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Pills Row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(TemplateCategory.entries.toTypedArray()) { category ->
                            val isSelected = state.selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectCategory(category) },
                                label = {
                                    Text(
                                        text = category.displayName,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Custom Template JSON Import Dialog
    if (showImportDialog) {
        CustomTemplateImportDialog(
            onDismissRequest = { showImportDialog = false },
            onImportJson = { json -> viewModel.importCustomTemplate(json) }
        )
    }
}
