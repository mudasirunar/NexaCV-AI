package com.mudasir.nexacvai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mudasir.nexacvai.presentation.navigation.AppNavHost
import com.mudasir.nexacvai.presentation.navigation.BottomNavigationBar
import com.mudasir.nexacvai.ui.theme.NexaCVAITheme
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.mudasir.nexacvai.presentation.navigation.BottomNavScreens
import com.mudasir.nexacvai.presentation.navigation.Screen
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileExportManager
import com.mudasir.nexacvai.presentation.ui.components.NexaSnackbar
import com.mudasir.nexacvai.presentation.ui.profiles.components.NexaExportToast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.koin.android.ext.android.inject
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset

class MainActivity : ComponentActivity() {
    private val profileDeleteManager: ProfileDeleteManager by inject()
    private val profileExportManager: ProfileExportManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexaCVAITheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                val showBottomBar = currentRoute == null || BottomNavScreens.any {
                    currentRoute.startsWith(it.route) == true 
                }

                val pendingDeleteProfile by profileDeleteManager.pendingDeleteProfile.collectAsState()

                // Commit pending deletion when navigating away from bottom nav screens (e.g. to creation/editing)
                LaunchedEffect(currentRoute) {
                    if (currentRoute != null) {
                        val isBottomNavRoute = BottomNavScreens.any { currentRoute.startsWith(it.route) }
                        if (!isBottomNavRoute) {
                            profileDeleteManager.commitPendingDelete()
                        }
                    }
                }

                // Commit pending deletion when the app goes to background / closes (but not on screen rotation/theme toggle)
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_DESTROY) {
                            if (!this@MainActivity.isChangingConfigurations) {
                                profileDeleteManager.commitPendingDelete()
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                // Dynamically offset the Snackbar to stay above the scrollable FAB on the Profiles screen
                val isProfilesScreen = currentRoute?.startsWith(Screen.Profiles.route) == true
                val isFabVisibleState by profileDeleteManager.isFabVisible.collectAsState()
                
                val configuration = LocalConfiguration.current
                val isWideScreen = configuration.screenWidthDp >= 600
                
                // On wide screens (tablets or landscape phones), the centered snackbar (max 480dp)
                // and the right-aligned FAB do not overlap horizontally. Thus, we only push the snackbar
                // up when the screen is narrow (isWideScreen == false) and the FAB is visible.
                val showFabOnScreen = isProfilesScreen && isFabVisibleState && !isWideScreen

                val snackbarBottomPadding by animateDpAsState(
                    targetValue = if (showBottomBar) {
                        if (showFabOnScreen) {
                            160.dp // Slightly reduced spacing above FAB (top of FAB is 152dp, giving an 8dp gap)
                        } else {
                            96.dp // 16dp spacing above bottom navigation bar (80dp)
                        }
                    } else {
                        16.dp // 16dp spacing above screen bottom
                    },
                    animationSpec = tween(durationMillis = 250),
                    label = "snackbarBottomPadding"
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        AppNavHost(
                            navController = navController,
                            innerPadding = innerPadding
                        )
                        
                        val bottomBarOffset by animateDpAsState(
                            targetValue = if (showBottomBar) 0.dp else 120.dp,
                            animationSpec = tween(durationMillis = 200),
                            label = "bottomBarOffset"
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = bottomBarOffset)
                        ) {
                            BottomNavigationBar(navController = navController)
                        }

                        // Custom animated Snackbar for profile deletion with Undo button
                        AnimatedVisibility(
                            visible = pendingDeleteProfile != null && showBottomBar,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(durationMillis = 250)
                            ) + fadeIn(animationSpec = tween(durationMillis = 250)),
                            exit = slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(durationMillis = 200)
                            ) + fadeOut(animationSpec = tween(durationMillis = 200)),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = -snackbarBottomPadding.roundToPx()
                                    )
                                }
                        ) {
                            pendingDeleteProfile?.let { profile ->
                                NexaSnackbar(
                                    message = "Profile \"${profile.fullName.ifBlank { "Untitled Profile" }}\" deleted",
                                    actionLabel = "Undo",
                                    onActionClick = {
                                        profileDeleteManager.undoDelete()
                                    }
                                )
                            }
                        }

                        // Custom animated global export status toast
                        val exportState by profileExportManager.exportState.collectAsState()
                        val exportError by profileExportManager.exportError.collectAsState()

                        val isSnackbarVisible = pendingDeleteProfile != null && showBottomBar
                        val targetToastPadding = if (isSnackbarVisible) {
                            snackbarBottomPadding + 64.dp
                        } else {
                            if (showBottomBar) 96.dp else 16.dp
                        }

                        val toastBottomPadding by animateDpAsState(
                            targetValue = targetToastPadding,
                            animationSpec = tween(durationMillis = 250),
                            label = "toastBottomPadding"
                        )

                        NexaExportToast(
                            exportState = exportState,
                            errorMessage = exportError,
                            onDismiss = { profileExportManager.dismissExportProgress() },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = -toastBottomPadding.roundToPx()
                                    )
                                }
                        )
                    }
                }
            }
        }
    }
}