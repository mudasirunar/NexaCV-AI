package com.mudasir.nexacvai.presentation.ui.profiles

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard.EmptySearchResultScreen
import com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard.ProfileSearchBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import android.widget.Toast
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import com.mudasir.nexacvai.domain.model.ProfileSortOrder
import com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard.ProfilesOverflowMenu
import com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard.SelectionModeOverflowMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.presentation.navigation.Screen
import com.mudasir.nexacvai.presentation.ui.components.NexaAlertDialog
import com.mudasir.nexacvai.presentation.ui.components.NexaButton
import com.mudasir.nexacvai.presentation.ui.components.NexaFloatingActionButton
import com.mudasir.nexacvai.presentation.ui.profiles.components.ImportExportBottomSheet
import com.mudasir.nexacvai.presentation.ui.profiles.components.ImportExportSheetContent
import com.mudasir.nexacvai.presentation.ui.profiles.components.ProfileCopySheet
import com.mudasir.nexacvai.presentation.ui.profiles.components.ProfileCopySheetContent
import com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard.ProfileCardSkeleton
import com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard.ProfileSelectionBottomBar
import com.mudasir.nexacvai.presentation.ui.profiles.components.profiles_dashboard.UserProfileCard
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.DuplicateProgressState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.DuplicateResolution
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ImportProgressState
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ProfilesViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

private enum class ProfilesScreenMode {
    Loading, Error, Empty, Content
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    navController: NavController,
    viewModel: ProfilesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val bottomSpacing = 80.dp
    var profileToDelete by remember { mutableStateOf<UserProfile?>(null) }
    var highlightedProfileId by remember { mutableStateOf<Long?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri ->
            uri?.let {
                viewModel.exportProfileToUri(
                    context = context,
                    uri = it
                )
            } ?: run {
                viewModel.dismissExportConfirm()
            }
        }
    )

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.startImport(
                    context = context,
                    uri = it,
                    onError = { errorMessage ->
                        android.widget.Toast.makeText(context, errorMessage, android.widget.Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    )

    val lastUndoneId by viewModel.profileDeleteManager.lastUndoneProfileId.collectAsState()

    LaunchedEffect(lastUndoneId, state.profiles) {
        val id = lastUndoneId
        val profiles = state.profiles
        if (id != null && profiles != null) {
            val index = profiles.indexOfFirst { it.id == id }
            if (index != -1) {
            kotlinx.coroutines.delay(200)
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

    LaunchedEffect(state.importState) {
        if (state.importState == ImportProgressState.Success) {
            gridState.scrollToItem(0)
        }
    }

    LaunchedEffect(state.duplicateState) {
        if (state.duplicateState == DuplicateProgressState.Success) {
            if (gridState.firstVisibleItemIndex <= 2) {
                gridState.animateScrollToItem(0)
            }
        }
    }

    LaunchedEffect(state.profiles) {
        if (state.profiles?.isEmpty() == true) {
            gridState.scrollToItem(0)
        }
    }

    var previousSortOrder by remember { mutableStateOf<ProfileSortOrder?>(null) }

    LaunchedEffect(state.sortOrder) {
        val currentSort = state.sortOrder
        if (previousSortOrder != null && previousSortOrder != currentSort) {
            if (state.profiles?.isNotEmpty() == true) {
                if (gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0) {
                    gridState.animateScrollToItem(0)
                }
            }
        }
        previousSortOrder = currentSort
    }

    var lastClickTime by remember { mutableStateOf(0L) }
    val safeNavigate: (String) -> Unit = { route ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 800L) {
            lastClickTime = currentTime
            navController.navigate(route)
        }
    }

    var isFabVisible by remember { mutableStateOf(true) }

    val isGridScrollable by remember {
        derivedStateOf {
            gridState.canScrollBackward || gridState.canScrollForward
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isGridScrollable && source == NestedScrollSource.UserInput) {
                    if (available.y < -12) {
                        isFabVisible = false
                    } else if (available.y > 12) {
                        isFabVisible = true
                    }
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(isGridScrollable) {
        if (!isGridScrollable) {
            isFabVisible = true
        }
    }

    LaunchedEffect(gridState.canScrollBackward) {
        if (!gridState.canScrollBackward) {
            isFabVisible = true
        }
    }

    val screenMode by remember {
        derivedStateOf {
            val profiles = state.profiles
            when {
                state.error != null -> ProfilesScreenMode.Error
                state.isLoading || profiles == null -> ProfilesScreenMode.Loading
                state.isTotalProfilesEmpty -> ProfilesScreenMode.Empty
                else -> ProfilesScreenMode.Content
            }
        }
    }

    val isFabVisuallyShown by remember {
        derivedStateOf {
            isFabVisible && screenMode == ProfilesScreenMode.Content && !state.isSelectionMode
        }
    }
    
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
            if (state.isSelectionMode) {
                TopAppBar(
                    title = {
                        Text(
                            text = "${state.selectedProfileIds.size} Selected",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.exitSelectionMode() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Selection Mode",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        SelectionModeOverflowMenu(
                            isExportEnabled = state.selectedProfileIds.isNotEmpty(),
                            onExportSelectedClick = {
                                val all = state.profiles ?: emptyList()
                                val selected = all.filter { it.id in state.selectedProfileIds }
                                viewModel.selectProfilesForExport(selected)
                            },
                            onCopySelectedClick = {
                                viewModel.duplicateSelectedProfiles(context)
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            } else {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clipToBounds()
                        ) {
                            AnimatedContent(
                                targetState = state.isSearchActive,
                                transitionSpec = {
                                    if (targetState) {
                                        // Opening Search: Title slides out to left (-width), Search bar slides in from right (+width)
                                        (slideInHorizontally(animationSpec = tween(280, easing = LinearOutSlowInEasing)) { width -> width } + fadeIn(animationSpec = tween(200))) togetherWith
                                        (slideOutHorizontally(animationSpec = tween(240, easing = FastOutLinearInEasing)) { width -> -width } + fadeOut(animationSpec = tween(180)))
                                    } else {
                                        // Closing Search: Search bar slides out to right (+width), Title slides in from left (-width)
                                        (slideInHorizontally(animationSpec = tween(280, easing = LinearOutSlowInEasing)) { width -> -width } + fadeIn(animationSpec = tween(200))) togetherWith
                                        (slideOutHorizontally(animationSpec = tween(240, easing = FastOutLinearInEasing)) { width -> width } + fadeOut(animationSpec = tween(180)))
                                    }
                                },
                                label = "titleSearchSlide"
                            ) { isSearch ->
                                if (isSearch) {
                                    ProfileSearchBar(
                                        query = state.searchQuery,
                                        hasResults = state.profiles?.isNotEmpty() == true,
                                        onQueryChange = { viewModel.onSearchQueryChanged(it) },
                                        onCloseSearch = { viewModel.onSearchActiveChanged(false) }
                                    )
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Profiles", style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.weight(1f))
                                        if (screenMode != ProfilesScreenMode.Empty) {
                                            IconButton(onClick = { viewModel.onSearchActiveChanged(true) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = "Search Profiles",
                                                    tint = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    actions = {
                        ProfilesOverflowMenu(
                            isProfilesEmpty = screenMode == ProfilesScreenMode.Empty,
                            currentSortOrder = state.sortOrder,
                            onImportClick = {
                                importLauncher.launch(arrayOf("*/*"))
                            },
                            onExportAllClick = {
                                viewModel.selectAllProfilesForExport()
                            },
                            onSelectProfilesClick = {
                                viewModel.enterSelectionMode()
                            },
                            onSortOrderSelected = { sortOption ->
                                viewModel.updateSortOrder(sortOption)
                                Toast.makeText(
                                    context,
                                    "Sorted by: ${sortOption.displayName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisuallyShown,
                enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn() + scaleIn(initialScale = 0.8f),
                exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                NexaFloatingActionButton(
                    onClick = { safeNavigate(Screen.CreateProfile.route) },
                    modifier = Modifier.padding(bottom = bottomSpacing),
                    icon = Icons.Default.Add,
                    contentDescription = "Add Profile",
                    hasBorder = true,
                    borderColor = MaterialTheme.colorScheme.primary,
                    fillColor = MaterialTheme.colorScheme.primary,
                    fillOpacity = 0.16f,
                    contentColor = MaterialTheme.colorScheme.primary
                )
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
                        Spacer(modifier = Modifier.height(24.dp))
                        NexaButton(
                            onClick = { safeNavigate(Screen.CreateProfile.route) },
                            text = "Create Profile",
                            icon = Icons.Default.Add,
                            hasBorder = true,
                            borderColor = MaterialTheme.colorScheme.primary,
                            fillColor = MaterialTheme.colorScheme.primary,
                            fillOpacity = 0.12f,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                ProfilesScreenMode.Content -> {
                    if (state.isSearchActive && state.searchQuery.isNotBlank() && state.profiles.isNullOrEmpty()) {
                        EmptySearchResultScreen(
                            query = state.searchQuery,
                            onClearSearchClick = { viewModel.clearSearchQuery() }
                        )
                    } else {
                        val configuration = LocalConfiguration.current
                        val isWideScreen = configuration.screenWidthDp >= 600
                        val columns = if (isWideScreen) GridCells.Fixed(2) else GridCells.Fixed(1)

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
                                onCardClick = {
                                    if (state.isSelectionMode) {
                                        viewModel.toggleSelection(profile.id)
                                    } else {
                                        safeNavigate("${Screen.ViewProfile.route}?profileId=${profile.id}")
                                    }
                                },
                                onEditClick = { safeNavigate("${Screen.CreateProfile.route}?profileId=${profile.id}") },
                                onDeleteClick = {
                                    profileToDelete = profile
                                },
                                onExportClick = {
                                    viewModel.selectProfileForExport(profile)
                                },
                                onRemovePhotoClick = {
                                    viewModel.removeProfilePicture(profile)
                                },
                                onPhotoSelected = { uri ->
                                    viewModel.updateProfilePicture(profile, uri)
                                },
                                isSelected = state.selectedProfileIds.contains(profile.id),
                                isInSelectionMode = state.isSelectionMode,
                                onCardLongClick = {
                                    if (!state.isSelectionMode) {
                                        viewModel.enterSelectionMode(profile.id)
                                    }
                                },
                                isHighlighted = highlightedProfileId == profile.id,
                                onSourceProfileClick = { sourceId ->
                                    val sourceIndex = state.profiles?.indexOfFirst { it.id == sourceId } ?: -1
                                    if (sourceIndex != -1) {
                                        coroutineScope.launch {
                                            gridState.animateScrollToItem(sourceIndex)
                                            highlightedProfileId = sourceId
                                            kotlinx.coroutines.delay(2000)
                                            if (highlightedProfileId == sourceId) {
                                                highlightedProfileId = null
                                            }
                                        }
                                    }
                                },
                                onRemoveCopyTagClick = {
                                    viewModel.removeSourceProfileTag(profile.id)
                                },
                                modifier = Modifier.animateItem(
                                    fadeInSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    ),
                                    fadeOutSpec = tween(durationMillis = 200),
                                    placementSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = 320f
                                    )
                                )
                            )
                        }
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

            if (state.showExportConfirm && state.exportingProfilesList.isNotEmpty()) {
                val profilesToExport = state.exportingProfilesList
                ImportExportBottomSheet(
                    content = ImportExportSheetContent.ExportConfirm(profilesToExport),
                    onExportConfirm = {
                        viewModel.hideExportDialog()
                        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd_HHmm", java.util.Locale.getDefault())
                        val formattedDate = dateFormat.format(java.util.Date())
                        val fileName = if (profilesToExport.size > 1) {
                            "NexaCV_Profiles_Backup_$formattedDate.nexacv"
                        } else {
                            val cleanName = profilesToExport.first().fullName.trim().replace("\\s+".toRegex(), "_").ifBlank { "Profile" }
                            "${cleanName}_profile.nexacv"
                        }
                        exportLauncher.launch(fileName)
                    },
                    onDismiss = { viewModel.dismissExportConfirm() }
                )
            }

            if (state.importState != ImportProgressState.Idle) {
                val sheetContent = when (state.importState) {
                    ImportProgressState.DuplicateSelection -> {
                        val list = state.importedProfileDataList.ifEmpty {
                            state.importedProfileData?.let { listOf(it) } ?: emptyList()
                        }
                        if (list.size > 1) {
                            val existingProfiles = state.profiles ?: emptyList()
                            val existingMap = existingProfiles.associate { it.id to it.fullName }
                            ImportExportSheetContent.MultiDuplicateFound(
                                importedProfilesData = list,
                                existingProfilesMap = existingMap
                            )
                        } else {
                            val duplicateProfile = state.importedProfileData?.profile
                            val existingProfile = state.profiles?.find { it.id == duplicateProfile?.id }
                            if (duplicateProfile != null) {
                                ImportExportSheetContent.DuplicateFound(
                                    importedProfile = duplicateProfile,
                                    existingName = existingProfile?.fullName ?: "Existing Profile"
                                )
                            } else null
                        }
                    }
                    ImportProgressState.Importing -> ImportExportSheetContent.Importing
                    ImportProgressState.Success -> {
                        val list = state.importedProfileDataList
                        val importedSingleProfile = if (state.newlyImportedProfileId != null) {
                            list.find { it.profile.id == state.newlyImportedProfileId }?.profile
                        } else null
                        val mainName = state.importedProfileData?.profile?.fullName
                            ?: importedSingleProfile?.fullName
                            ?: list.firstOrNull()?.profile?.fullName
                            ?: ""
                        ImportExportSheetContent.ImportSuccess(
                            count = state.importedCount,
                            mainProfileName = mainName
                        )
                    }
                    ImportProgressState.Idle -> null
                }

                if (sheetContent != null) {
                    ImportExportBottomSheet(
                        content = sheetContent,
                        onKeepBoth = { viewModel.executeImport(context, DuplicateResolution.KeepBoth) },
                        onOverwrite = { viewModel.executeImport(context, DuplicateResolution.Overwrite) },
                        onExecuteMultiImport = { map -> viewModel.executeMultiImport(context, map) },
                        onViewProfile = {
                            val importedId = state.newlyImportedProfileId
                            viewModel.cancelImport()
                            if (importedId != null) {
                                safeNavigate("${Screen.ViewProfile.route}?profileId=$importedId")
                            }
                        },
                        onDismiss = { viewModel.cancelImport() }
                    )
                }
            }

            // Profile Copy Progress / Success Sheet
            if (state.duplicateState != DuplicateProgressState.Idle) {
                val sheetContent = when (state.duplicateState) {
                    DuplicateProgressState.Duplicating -> ProfileCopySheetContent.Copying
                    DuplicateProgressState.Success -> ProfileCopySheetContent.Success(
                        count = state.duplicatedCount,
                        profileName = state.duplicatedProfileName
                    )
                    else -> null
                }
                if (sheetContent != null) {
                    ProfileCopySheet(
                        content = sheetContent,
                        onViewProfile = {
                            val newId = state.newlyDuplicatedProfileId
                            viewModel.dismissDuplicateSheet()
                            if (newId != null) {
                                safeNavigate("${Screen.ViewProfile.route}?profileId=$newId")
                            }
                        },
                        onDone = { viewModel.dismissDuplicateSheet() }
                    )
                }
            }

            ProfileSelectionBottomBar(
                isSelectionModeActive = state.isSelectionMode,
                selectedProfileIds = state.selectedProfileIds,
                allProfiles = state.profiles ?: emptyList(),
                onToggleSelectAll = { viewModel.toggleSelectAll() },
                onDeleteSelected = { viewModel.deleteSelectedProfiles() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
