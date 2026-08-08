package com.mudasir.nexacvai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mudasir.nexacvai.presentation.navigation.AppNavHost
import com.mudasir.nexacvai.presentation.navigation.BottomNavigationBar
import com.mudasir.nexacvai.presentation.navigation.BottomNavScreens
import com.mudasir.nexacvai.presentation.navigation.Screen
import com.mudasir.nexacvai.presentation.ui.components.NexaSnackbar
import com.mudasir.nexacvai.presentation.ui.profiles.components.NexaExportToast
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileExportManager
import com.mudasir.nexacvai.ui.theme.NexaCVAITheme
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.mudasir.nexacvai.data.local.datastore.AppSettingsManager
import com.mudasir.nexacvai.domain.model.AppThemeMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var profileDeleteManager: ProfileDeleteManager

    @Inject
    lateinit var profileExportManager: ProfileExportManager

    @Inject
    lateinit var appSettingsManager: AppSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val initialTheme = remember {
                runBlocking(Dispatchers.IO) {
                    appSettingsManager.themeModeFlow.first()
                }
            }
            val themeMode by appSettingsManager.themeModeFlow.collectAsState(initial = initialTheme)
            val systemInDark = androidx.compose.foundation.isSystemInDarkTheme()

            val useDarkTheme = when (themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> systemInDark
            }

            androidx.compose.runtime.SideEffect {
                androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !useDarkTheme
                    isAppearanceLightNavigationBars = !useDarkTheme
                }
            }

            NexaCVAITheme(darkTheme = useDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                val isSelectionModeActive by profileDeleteManager.isSelectionModeActive.collectAsState()
                
                val showBottomBar = (currentRoute == null || BottomNavScreens.any {
                    currentRoute.startsWith(it.route) == true 
                }) && !isSelectionModeActive

                val pendingDeleteProfiles by profileDeleteManager.pendingDeleteProfiles.collectAsState()

                LaunchedEffect(currentRoute) {
                    if (currentRoute != null) {
                        val isBottomNavRoute = BottomNavScreens.any { currentRoute.startsWith(it.route) }
                        if (!isBottomNavRoute) {
                            profileDeleteManager.commitPendingDelete()
                        }
                    }
                }

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

                val isProfilesScreen = currentRoute?.startsWith(Screen.Profiles.route) == true
                val isFabVisibleState by profileDeleteManager.isFabVisible.collectAsState()
                
                val configuration = LocalConfiguration.current
                val isWideScreen = configuration.screenWidthDp >= 600
                val showFabOnScreen = isProfilesScreen && isFabVisibleState && !isWideScreen

                val snackbarBottomPadding by animateDpAsState(
                    targetValue = if (showBottomBar) {
                        if (showFabOnScreen) {
                            160.dp
                        } else {
                            96.dp
                        }
                    } else {
                        16.dp
                    },
                    animationSpec = tween(durationMillis = 250),
                    label = "snackbarBottomPadding"
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
                ) { scaffoldPadding ->
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)) {
                        AppNavHost(
                            navController = navController
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

                        AnimatedVisibility(
                            visible = pendingDeleteProfiles.isNotEmpty() && showBottomBar,
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
                            if (pendingDeleteProfiles.isNotEmpty()) {
                                val message = if (pendingDeleteProfiles.size == 1) {
                                    val profile = pendingDeleteProfiles.first()
                                    "Profile \"${profile.fullName.ifBlank { "Untitled Profile" }}\" deleted"
                                } else {
                                    "${pendingDeleteProfiles.size} profiles deleted"
                                }
                                NexaSnackbar(
                                    message = message,
                                    actionLabel = "Undo",
                                    onActionClick = {
                                        profileDeleteManager.undoDelete()
                                    }
                                )
                            }
                        }

                        val exportState by profileExportManager.exportState.collectAsState()
                        val exportError by profileExportManager.exportError.collectAsState()

                        val isSnackbarVisible = pendingDeleteProfiles.isNotEmpty() && showBottomBar
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

                        BackHandler(enabled = isSelectionModeActive) {
                            profileDeleteManager.setSelectionModeActive(false)
                        }
                    }
                }
            }
        }
    }
}