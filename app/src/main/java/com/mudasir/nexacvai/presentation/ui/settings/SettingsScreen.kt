package com.mudasir.nexacvai.presentation.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mudasir.nexacvai.presentation.navigation.Screen

@Composable
fun SettingsScreen(navController: NavController) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val scrollState = rememberScrollState()

    val currentBackStackEntry = navController.currentBackStackEntry
    val scrollToTopTrigger by currentBackStackEntry?.savedStateHandle
        ?.getStateFlow("scrollToTop_${Screen.Settings.route}", 0L)
        ?.collectAsState() ?: remember { mutableStateOf(0L) }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0L) {
            scrollState.animateScrollTo(0)
            currentBackStackEntry?.savedStateHandle?.remove<Long>("scrollToTop_${Screen.Settings.route}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp + bottomInset),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Settings Screen", style = MaterialTheme.typography.titleLarge)
    }
}
