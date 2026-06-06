package com.mudasir.nexacvai.presentation.ui.profiles.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.mudasir.nexacvai.R

data class SocialPlatformInfo(
    val iconResId: Int?,
    val defaultIcon: ImageVector,
    val scale: Float
)

val SUPPORTED_PLATFORMS = listOf(
    "LinkedIn",
    "GitHub",
    "Behance",
    "Dribbble",
    "Stack Overflow",
    "X",
    "Personal Portfolio",
    "Other Platform"
)

fun isStandardPlatform(platformName: String): Boolean {
    return platformName in SUPPORTED_PLATFORMS && platformName != "Other Platform"
}

fun getSocialPlatformInfo(platformName: String): SocialPlatformInfo {
    return when (platformName) {
        "LinkedIn" -> SocialPlatformInfo(R.drawable.ic_linkedin, Icons.Default.Share, 1.2f)
        "GitHub" -> SocialPlatformInfo(R.drawable.ic_github, Icons.Default.Code, 1.0f)
        "Behance" -> SocialPlatformInfo(R.drawable.ic_behance, Icons.Default.Brush, 1.2f)
        "Dribbble" -> SocialPlatformInfo(R.drawable.ic_dribble, Icons.Default.Palette, 1.0f)
        "Stack Overflow" -> SocialPlatformInfo(R.drawable.ic_stackoverflow, Icons.Default.QuestionAnswer, 0.95f)
        "X" -> SocialPlatformInfo(R.drawable.ic_x, Icons.Default.AlternateEmail, 1.7f)
        "Personal Portfolio" -> SocialPlatformInfo(null, Icons.Default.Language, 1.0f)
        else -> SocialPlatformInfo(null, Icons.Default.Link, 1.0f)
    }
}
