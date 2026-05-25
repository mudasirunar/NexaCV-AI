package com.mudasir.nexacvai.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.mudasir.nexacvai.presentation.ui.generate.GenerateScreen
import com.mudasir.nexacvai.presentation.ui.history.HistoryScreen
import com.mudasir.nexacvai.presentation.ui.home.HomeScreen
import com.mudasir.nexacvai.presentation.ui.profiles.CreateProfileScreen
import com.mudasir.nexacvai.presentation.ui.profiles.ProfilesScreen
import com.mudasir.nexacvai.presentation.ui.settings.SettingsScreen

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
            // For sub-screens that are not in the bottom nav, slide in from the right smoothly
            if (!routeIndices.containsKey(targetState.destination.route?.split("?")?.get(0))) {
                return@NavHost slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(250))
            }
            
            val initialIndex = routeIndices[initialState.destination.route?.split("?")?.get(0)] ?: 0
            val targetIndex = routeIndices[targetState.destination.route?.split("?")?.get(0)] ?: 0
            val direction = if (targetIndex > initialIndex) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }
            slideIntoContainer(
                towards = direction,
                animationSpec = tween(250) // Quick 250ms slide between bottom nav screens
            )
        },
        exitTransition = {
            // When navigating to a sub-screen, slide out to the left smoothly
            if (!routeIndices.containsKey(targetState.destination.route?.split("?")?.get(0))) {
                 return@NavHost slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(250))
            }
            
            val initialIndex = routeIndices[initialState.destination.route?.split("?")?.get(0)] ?: 0
            val targetIndex = routeIndices[targetState.destination.route?.split("?")?.get(0)] ?: 0
            val direction = if (targetIndex > initialIndex) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }
            slideOutOfContainer(
                towards = direction,
                animationSpec = tween(250)
            )
        },
        popEnterTransition = {
            // When popping back from a sub-screen, slide back in from the left smoothly
            if (!routeIndices.containsKey(initialState.destination.route?.split("?")?.get(0))) {
                return@NavHost slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(250))
            }
            
            val initialIndex = routeIndices[initialState.destination.route?.split("?")?.get(0)] ?: 0
            val targetIndex = routeIndices[targetState.destination.route?.split("?")?.get(0)] ?: 0
            val direction = if (targetIndex > initialIndex) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }
            slideIntoContainer(
                towards = direction,
                animationSpec = tween(250)
            )
        },
        popExitTransition = {
            // When popping back from a sub-screen, slide out to the right smoothly
            if (!routeIndices.containsKey(initialState.destination.route?.split("?")?.get(0))) {
                return@NavHost slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(250))
            }
            
            val initialIndex = routeIndices[initialState.destination.route?.split("?")?.get(0)] ?: 0
            val targetIndex = routeIndices[targetState.destination.route?.split("?")?.get(0)] ?: 0
            val direction = if (targetIndex > initialIndex) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }
            slideOutOfContainer(
                towards = direction,
                animationSpec = tween(250)
            )
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
