package com.mudasir.nexacvai.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val parentRoute: String? = null
) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Profiles : Screen("profiles", "Profiles", Icons.Default.Person)
    object Generate : Screen("generate", "Generate", Icons.Default.Description)
    object History : Screen("history", "History", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
    // Sub-screens (Not in bottom nav)
    object CreateProfile : Screen("create_profile", "Create Profile", Icons.Default.Person, parentRoute = "profiles")
}

val BottomNavScreens = listOf(
    Screen.Home,
    Screen.Profiles,
    Screen.Generate,
    Screen.History,
    Screen.Settings
)

val AllScreens = listOf(
    Screen.Home,
    Screen.Profiles,
    Screen.Generate,
    Screen.History,
    Screen.Settings,
    Screen.CreateProfile
)
