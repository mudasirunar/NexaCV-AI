package com.mudasir.nexacvai.presentation.navigation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
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
import com.mudasir.nexacvai.presentation.ui.settings.SettingsScreen

// Shared transition durations for consistent, snappy navigation feel
private const val NAV_ANIM_DURATION = 200

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    val routeIndices = BottomNavScreens.mapIndexed { index, screen -> screen.route to index }.toMap()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            // For sub-screens that are not in the bottom nav, slide in from the right smoothly (subtle 10% slide + fade)
            if (!routeIndices.containsKey(targetState.destination.route?.split("?")?.get(0))) {
                return@NavHost fadeIn(tween(NAV_ANIM_DURATION, easing = LinearOutSlowInEasing)) +
                        slideInHorizontally(tween(NAV_ANIM_DURATION, easing = LinearOutSlowInEasing)) { it / 8 }
            }
            
            val initialIndex = routeIndices[initialState.destination.route?.split("?")?.get(0)] ?: 0
            val targetIndex = routeIndices[targetState.destination.route?.split("?")?.get(0)] ?: 0
            val offsetMultiplier = if (targetIndex > initialIndex) 1 else -1
            
            fadeIn(tween(NAV_ANIM_DURATION, easing = LinearOutSlowInEasing)) +
                    slideInHorizontally(tween(NAV_ANIM_DURATION, easing = LinearOutSlowInEasing)) { (it / 8) * offsetMultiplier }
        },
        exitTransition = {
            // When navigating to a sub-screen, slide out to the left smoothly
            if (!routeIndices.containsKey(targetState.destination.route?.split("?")?.get(0))) {
                return@NavHost fadeOut(tween(NAV_ANIM_DURATION, easing = FastOutLinearInEasing)) +
                        slideOutHorizontally(tween(NAV_ANIM_DURATION, easing = FastOutLinearInEasing)) { -it / 8 }
            }
            
            val initialIndex = routeIndices[initialState.destination.route?.split("?")?.get(0)] ?: 0
            val targetIndex = routeIndices[targetState.destination.route?.split("?")?.get(0)] ?: 0
            val offsetMultiplier = if (targetIndex > initialIndex) -1 else 1
            
            fadeOut(tween(NAV_ANIM_DURATION, easing = FastOutLinearInEasing)) +
                    slideOutHorizontally(tween(NAV_ANIM_DURATION, easing = FastOutLinearInEasing)) { (it / 8) * offsetMultiplier }
        },
        popEnterTransition = {
            // When popping back from a sub-screen, slide back in from the left smoothly
            if (!routeIndices.containsKey(initialState.destination.route?.split("?")?.get(0))) {
                return@NavHost fadeIn(tween(NAV_ANIM_DURATION, easing = LinearOutSlowInEasing)) +
                        slideInHorizontally(tween(NAV_ANIM_DURATION, easing = LinearOutSlowInEasing)) { -it / 8 }
            }
            
            val initialIndex = routeIndices[initialState.destination.route?.split("?")?.get(0)] ?: 0
            val targetIndex = routeIndices[targetState.destination.route?.split("?")?.get(0)] ?: 0
            val offsetMultiplier = if (targetIndex > initialIndex) 1 else -1
            
            fadeIn(tween(NAV_ANIM_DURATION, easing = LinearOutSlowInEasing)) +
                    slideInHorizontally(tween(NAV_ANIM_DURATION, easing = LinearOutSlowInEasing)) { (it / 8) * offsetMultiplier }
        },
        popExitTransition = {
            // When popping back from a sub-screen, slide out to the right smoothly
            if (!routeIndices.containsKey(initialState.destination.route?.split("?")?.get(0))) {
                return@NavHost fadeOut(tween(NAV_ANIM_DURATION, easing = FastOutLinearInEasing)) +
                        slideOutHorizontally(tween(NAV_ANIM_DURATION, easing = FastOutLinearInEasing)) { it / 8 }
            }
            
            val initialIndex = routeIndices[initialState.destination.route?.split("?")?.get(0)] ?: 0
            val targetIndex = routeIndices[targetState.destination.route?.split("?")?.get(0)] ?: 0
            val offsetMultiplier = if (targetIndex > initialIndex) -1 else 1
            
            fadeOut(tween(NAV_ANIM_DURATION, easing = FastOutLinearInEasing)) +
                    slideOutHorizontally(tween(NAV_ANIM_DURATION, easing = FastOutLinearInEasing)) { (it / 8) * offsetMultiplier }
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
    }
}

