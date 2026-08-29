package com.mudasir.nexacvai.presentation.ui.templates

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    onNavigateBack: () -> Unit = {},
    onOpenTemplatePreview: (templateId: String) -> Unit = {},
    viewModel: TemplatesViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetFlippedTemplates()
        }
    }

    // Independent persistent scroll state allocated per category & responsive collapsible header controller
    val categoryGridStates = rememberSaveable(
        saver = Saver(
            save = { map ->
                map.map { (category, state) ->
                    "${category.name}:${state.firstVisibleItemIndex}:${state.firstVisibleItemScrollOffset}"
                }
            },
            restore = { list ->
                val restored = mutableMapOf<TemplateCategory, LazyGridState>()
                for (category in TemplateCategory.entries) {
                    val entry = list.find { it.startsWith("${category.name}:") }
                    if (entry != null) {
                        val parts = entry.split(":")
                        val index = parts.getOrNull(1)?.toIntOrNull() ?: 0
                        val offset = parts.getOrNull(2)?.toIntOrNull() ?: 0
                        restored[category] = LazyGridState(index, offset)
                    } else {
                        restored[category] = LazyGridState()
                    }
                }
                restored
            }
        )
    ) {
        TemplateCategory.entries.associateWith { LazyGridState() }.toMutableMap()
    }

    val gridState = categoryGridStates[state.selectedCategory] ?: rememberLazyGridState()
    val filterChipRowState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    var isHeaderVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = remember(gridState) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = consumed.y
                // Only hide if the list ACTUALLY moved down and has remaining content to scroll
                if (delta < -8f && isHeaderVisible && gridState.canScrollForward) {
                    isHeaderVisible = false
                } else if (delta > 8f && !isHeaderVisible) {
                    isHeaderVisible = true
                }
                return Offset.Zero
            }
        }
    }

    // Ensure header is always visible when at the very top of the list or on search/category change
    LaunchedEffect(gridState, gridState.canScrollBackward, gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset) {
        if (!gridState.canScrollBackward || (gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset <= 12)) {
            isHeaderVisible = true
        }
    }

    // Reset scroll to top ONLY when user searches, keeping filter chips and favorites purely persistent
    var previousQuery by rememberSaveable { mutableStateOf(state.searchQuery) }

    LaunchedEffect(state.searchQuery) {
        if (state.searchQuery != previousQuery) {
            previousQuery = state.searchQuery
            if (state.searchQuery.isNotBlank() && state.filteredTemplates.isNotEmpty()) {
                gridState.scrollToItem(0)
            }
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTabletOrFoldableUnfolded = configuration.smallestScreenWidthDp >= 580

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
                        text = "Templates",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
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
                if (state.searchQuery.isNotBlank()) {
                    TemplateEmptySearchScreen(
                        query = state.searchQuery,
                        onClearSearchClick = { viewModel.updateSearchQuery("") },
                        modifier = Modifier.padding(top = 126.dp)
                    )
                } else if (state.selectedCategory == TemplateCategory.FAVORITES) {
                    TemplateEmptyFavoritesScreen(
                        onExploreAllClick = { viewModel.selectCategory(TemplateCategory.ALL) },
                        modifier = Modifier.padding(top = 126.dp)
                    )
                } else if (state.selectedCategory == TemplateCategory.CUSTOM) {
                    TemplateEmptyCustomScreen(
                        onCreateCustomClick = {
                            android.widget.Toast.makeText(
                                context,
                                "Custom Template Studio is coming soon!",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.padding(top = 126.dp)
                    )
                } else {
                    TemplateEmptySearchScreen(
                        query = state.searchQuery,
                        onClearSearchClick = { viewModel.updateSearchQuery("") },
                        modifier = Modifier.padding(top = 126.dp)
                    )
                }
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(gridColumnCount),
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 14.dp, top = 126.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredTemplates, key = { "${state.selectedCategory.name}_${it.metadata.id}" }) { template ->
                        val templateId = template.metadata.id
                        val isFav = state.favoriteTemplateIds.contains(templateId)
                        TemplateCard(
                            template = template,
                            isFlipped = state.flippedTemplateIds.contains(templateId),
                            isFavorite = isFav,
                            onToggleFlip = { viewModel.toggleTemplateFlip(templateId, state.selectedCategory) },
                            onToggleFavorite = { viewModel.toggleFavorite(templateId) },
                            onAddFavorite = { viewModel.addFavorite(templateId) },
                            onSelectTemplate = { onOpenTemplatePreview(templateId) },
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
                                fadeOutSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                                placementSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                            )
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
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {} // Consume clicks in empty spaces of header to prevent pass-through
                        )
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
                        state = filterChipRowState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(TemplateCategory.entries.toTypedArray(), key = { it.name }) { category ->
                            val isSelected = state.selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        coroutineScope.launch {
                                            isHeaderVisible = true
                                            gridState.animateScrollToItem(0)
                                        }
                                    } else {
                                        viewModel.selectCategory(category)
                                    }
                                },
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
}
