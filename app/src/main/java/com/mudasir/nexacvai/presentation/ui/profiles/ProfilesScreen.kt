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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import org.koin.androidx.compose.koinViewModel

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
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomSpacing = remember(bottomInset) { 80.dp + bottomInset }

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
                visible = isFabVisible,
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
        Crossfade(
            targetState = screenMode,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            label = "ProfilesScreenModeCrossfade"
        ) { mode ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (mode) {
                    ProfilesScreenMode.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                        // List of Profiles
                        val listState = rememberLazyListState()
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp,
                                bottom = bottomSpacing + 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.profiles ?: emptyList(), key = { it.id }) { profile ->
                                Card(
                                    onClick = { safeNavigate("${Screen.CreateProfile.route}?profileId=${profile.id}") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = profile.fullName.ifBlank { "Untitled Profile" },
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (profile.professionalTitle.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = profile.professionalTitle,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            if (profile.yearsOfExperience.isNotBlank()) {
                                                Text(
                                                    text = "Exp: ${profile.yearsOfExperience}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            if (profile.city.isNotBlank() || profile.country.isNotBlank()) {
                                                Text(
                                                    text = "${profile.city}${if(profile.city.isNotBlank() && profile.country.isNotBlank()) ", " else ""}${profile.country}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

