package com.mudasir.nexacvai.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * App Theme Modes supported by NexaCV AI.
 */
enum class AppThemeMode(
    val id: String,
    val displayName: String,
    val icon: ImageVector
) {
    SYSTEM("system", "System", Icons.Default.PhoneAndroid),
    LIGHT("light", "Light", Icons.Default.WbSunny),
    DARK("dark", "Dark", Icons.Default.DarkMode);

    companion object {
        fun fromId(id: String?): AppThemeMode {
            return values().find { it.id == id } ?: SYSTEM
        }
    }
}
