package com.mudasir.nexacvai.presentation.ui.profiles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mudasir.nexacvai.presentation.navigation.Screen
import com.mudasir.nexacvai.presentation.ui.profiles.components.UserProfileCard
import com.mudasir.nexacvai.presentation.ui.profiles.components.ProfileCardSkeleton
import com.mudasir.nexacvai.presentation.ui.profiles.components.NexaAlertDialog
import org.koin.androidx.compose.koinViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.*
import com.mudasir.nexacvai.domain.model.UserProfile
import androidx.compose.foundation.shape.RoundedCornerShape


/**
 * Derived UI mode for ProfilesScreen.
 * Using this as the Crossfade key instead of the full state object ensures
 * animations only trigger on category-level changes (Loading→Content),
 * not on every data update within the same mode.
 */
private enum class ProfilesScreenMode {
    Loading, Error, Empty, Content
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    navController: NavController,
    viewModel: ProfilesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val bottomSpacing = 80.dp
    var profileToDelete by remember { mutableStateOf<UserProfile?>(null) }
    val gridState = rememberLazyGridState()

    val lastUndoneId by viewModel.profileDeleteManager.lastUndoneProfileId.collectAsState()

    LaunchedEffect(lastUndoneId, state.profiles) {
        val id = lastUndoneId
        val profiles = state.profiles
        if (id != null && profiles != null) {
            val index = profiles.indexOfFirst { it.id == id }
            if (index != -1) {
            kotlinx.coroutines.delay(200) // Wait for UI update and layout
                val layoutInfo = gridState.layoutInfo
                val visibleItems = layoutInfo.visibleItemsInfo
                val itemInfo = visibleItems.find { it.key == id }
                
                val isVisuallyHidden = when {
                    itemInfo == null -> true
                    itemInfo.offset.y < layoutInfo.viewportStartOffset -> true
                    itemInfo.offset.y + (itemInfo.size.height / 2) > layoutInfo.viewportEndOffset -> true
                    else -> false
                }
                
                if (isVisuallyHidden) {
                    gridState.animateScrollToItem(index)
                }
            }
            viewModel.profileDeleteManager.clearLastUndoneProfileId()
        }
    }

    var lastClickTime by remember { mutableStateOf(0L) }
    val safeNavigate: (String) -> Unit = { route ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 800L) { // 800ms debounce
            lastClickTime = currentTime
            navController.navigate(route)
        }
    }

    var isFabVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Hide FAB on scroll down, show on scroll up
                if (available.y < -10) {
                    isFabVisible = false
                } else if (available.y > 10) {
                    isFabVisible = true
                }
                return Offset.Zero
            }
        }
    }

    // Derive a simple enum key for Crossfade — prevents full-state triggering
    val screenMode by remember {
        derivedStateOf {
            val profiles = state.profiles
            when {
                state.error != null -> ProfilesScreenMode.Error
                state.isLoading || profiles == null -> ProfilesScreenMode.Loading
                profiles.isEmpty() -> ProfilesScreenMode.Empty
                else -> ProfilesScreenMode.Content
            }
        }
    }

    val isFabVisuallyShown by remember {
        derivedStateOf {
            isFabVisible && (screenMode == ProfilesScreenMode.Content || screenMode == ProfilesScreenMode.Empty)
        }
    }
    
    // Synchronize FAB visibility state with ProfileDeleteManager
    LaunchedEffect(isFabVisuallyShown) {
        viewModel.profileDeleteManager.setFabVisible(isFabVisuallyShown)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.profileDeleteManager.setFabVisible(true)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Profiles", style = MaterialTheme.typography.titleMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisuallyShown,
                enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn() + scaleIn(initialScale = 0.8f),
                exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                FloatingActionButton(
                    onClick = { safeNavigate(Screen.CreateProfile.route) },
                    modifier = Modifier.padding(bottom = bottomSpacing),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Profile")
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (screenMode) {
                ProfilesScreenMode.Loading -> {
                    val configuration = LocalConfiguration.current
                    val isWideScreen = configuration.screenWidthDp >= 600
                    if (isWideScreen) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    end = 16.dp,
                                    bottom = bottomSpacing + 16.dp
                                ),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            userScrollEnabled = false
                        ) {
                            items(4) {
                                ProfileCardSkeleton()
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    end = 16.dp,
                                    bottom = bottomSpacing + 16.dp
                                ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            repeat(4) {
                                ProfileCardSkeleton()
                            }
                        }
                    }
                }
                ProfilesScreenMode.Error -> {
                    Text(
                        text = state.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                ProfilesScreenMode.Empty -> {
                    // Empty State
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = bottomSpacing),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = "Empty Profiles",
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Profiles Yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Create a profile to start generating CVs.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                ProfilesScreenMode.Content -> {
                    val configuration = LocalConfiguration.current
                    val isWideScreen = configuration.screenWidthDp >= 600
                    val columns = if (isWideScreen) GridCells.Fixed(2) else GridCells.Fixed(1)

                    // Grid/List of Profiles
                    LazyVerticalGrid(
                        columns = columns,
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 168.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.profiles ?: emptyList(), key = { it.id }) { profile ->
                            UserProfileCard(
                                profile = profile,
                                onCardClick = { safeNavigate("${Screen.ViewProfile.route}?profileId=${profile.id}") },
                                onEditClick = { safeNavigate("${Screen.CreateProfile.route}?profileId=${profile.id}") },
                                onDeleteClick = {
                                    profileToDelete = profile
                                },
                                onRemovePhotoClick = {
                                    viewModel.removeProfilePicture(profile)
                                },
                                onPhotoSelected = { uri ->
                                    viewModel.updateProfilePicture(profile, uri)
                                },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }

            if (profileToDelete != null) {
                val pToDelete = profileToDelete!!
                NexaAlertDialog(
                    onDismissRequest = { profileToDelete = null },
                    title = "Delete Profile?",
                    message = "Are you sure you want to permanently delete the profile for \"${pToDelete.fullName.ifBlank { "Untitled Profile" }}\"?",
                    confirmLabel = "Delete",
                    onConfirm = {
                        viewModel.deleteProfile(pToDelete)
                        profileToDelete = null
                    },
                    dismissLabel = "Cancel",
                    isDestructive = true
                )
            }
        }
    }
}
