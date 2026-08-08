package com.mudasir.nexacvai.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mudasir.nexacvai.presentation.ui.generate.GenerateScreen
import com.mudasir.nexacvai.presentation.ui.history.HistoryScreen
import com.mudasir.nexacvai.presentation.ui.home.HomeScreen
import com.mudasir.nexacvai.presentation.ui.profiles.CreateProfileScreen
import com.mudasir.nexacvai.presentation.ui.profiles.ProfilesScreen
import com.mudasir.nexacvai.presentation.ui.profiles.ViewProfileScreen
import com.mudasir.nexacvai.presentation.ui.settings.SettingsScreen

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

// Shared transition durations for consistent, snappy navigation feel
private const val NAV_ANIM_DURATION = 180

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val routeIndices = BottomNavScreens.mapIndexed { index, screen -> screen.route to index }.toMap()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                val targetRoute = targetState.destination.route?.split("?")?.get(0)
                if (!routeIndices.containsKey(targetRoute)) {
                    slideInHorizontally(tween(NAV_ANIM_DURATION, easing = FastOutSlowInEasing)) { width -> width / 3 } +
                            fadeIn(tween(NAV_ANIM_DURATION))
                } else {
                    fadeIn(tween(120))
                }
            },
            exitTransition = {
                val targetRoute = targetState.destination.route?.split("?")?.get(0)
                if (!routeIndices.containsKey(targetRoute)) {
                    slideOutHorizontally(tween(NAV_ANIM_DURATION, easing = FastOutSlowInEasing)) { width -> -width / 3 } +
                            fadeOut(tween(NAV_ANIM_DURATION))
                } else {
                    fadeOut(tween(120))
                }
            },
            popEnterTransition = {
                val initialRoute = initialState.destination.route?.split("?")?.get(0)
                if (!routeIndices.containsKey(initialRoute)) {
                    slideInHorizontally(tween(NAV_ANIM_DURATION, easing = FastOutSlowInEasing)) { width -> -width / 3 } +
                            fadeIn(tween(NAV_ANIM_DURATION))
                } else {
                    fadeIn(tween(120))
                }
            },
            popExitTransition = {
                val initialRoute = initialState.destination.route?.split("?")?.get(0)
                if (!routeIndices.containsKey(initialRoute)) {
                    slideOutHorizontally(tween(NAV_ANIM_DURATION, easing = FastOutSlowInEasing)) { width -> width / 3 } +
                            fadeOut(tween(NAV_ANIM_DURATION))
                } else {
                    fadeOut(tween(120))
                }
            }
        ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Profiles.route) {
            ProfilesScreen(navController = navController)
        }
        composable(Screen.Generate.route) {
            GenerateScreen(navController = navController)
        }
        composable(Screen.History.route) {
            HistoryScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(
            route = "${Screen.CreateProfile.route}?profileId={profileId}",
            arguments = listOf(navArgument("profileId") { 
                type = NavType.LongType
                defaultValue = -1L 
            })
        ) {
            CreateProfileScreen(navController = navController)
        }
        composable(
            route = "${Screen.ViewProfile.route}?profileId={profileId}",
            arguments = listOf(navArgument("profileId") { 
                type = NavType.LongType
                defaultValue = -1L 
            })
        ) {
            ViewProfileScreen(navController = navController)
        }
        composable(Screen.Templates.route) {
            com.mudasir.nexacvai.presentation.ui.templates.TemplatesScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenTemplatePreview = { templateId ->
                    navController.navigate("${Screen.TemplatePreview.route}?templateId=$templateId")
                }
            )
        }
        composable(
            route = "${Screen.TemplatePreview.route}?templateId={templateId}",
            arguments = listOf(navArgument("templateId") { type = NavType.StringType })
        ) { backStackEntry ->
            val templateId = backStackEntry.arguments?.getString("templateId") ?: "template_modern_tech"
            com.mudasir.nexacvai.presentation.ui.templates.TemplatePreviewScreen(
                templateId = templateId,
                onNavigateBack = { navController.popBackStack() },
                onConfirmCreateCv = { tId, pId ->
                    navController.navigate(Screen.Generate.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
}

