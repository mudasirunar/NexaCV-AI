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
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexaCVAITheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                val showBottomBar = currentRoute == null || com.mudasir.nexacvai.presentation.navigation.BottomNavScreens.any { 
                    currentRoute.startsWith(it.route) == true 
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        AppNavHost(
                            navController = navController,
                            innerPadding = innerPadding
                        )
                        
                        AnimatedVisibility(
                            visible = showBottomBar,
                            modifier = Modifier.align(Alignment.BottomCenter),
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(250)
                            ) + fadeIn(animationSpec = tween(250)),
                            exit = slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(250)
                            ) + fadeOut(animationSpec = tween(250))
                        ) {
                            BottomNavigationBar(navController = navController) 
                        }
                    }
                }
            }
        }
    }
}