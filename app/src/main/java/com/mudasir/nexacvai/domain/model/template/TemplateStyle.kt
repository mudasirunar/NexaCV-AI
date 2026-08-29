package com.mudasir.nexacvai.domain.model.template

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Visual shape options for profile photo frames across different resume templates.
 */
@Immutable
enum class PhotoShape {
    CIRCLE,          // Executive & classic round avatar
    ROUNDED_SQUARE,  // Modern tech rounded rectangle
    PASSPORT_RECT    // Corporate passport size 3:4 aspect ratio
}

/**
 * Visual styling configuration for a rendered template instance.
 */
@Immutable
data class TemplateStyle(
    val primaryColor: Color = Color(0xFF1E3A8A),      // Deep Navy
    val accentColor: Color = Color(0xFF3B82F6),       // Vibrant Blue
    val backgroundColor: Color = Color(0xFFFFFFFF),   // Clean Pure White A4 Page
    val textColor: Color = Color(0xFF1F2937),         // High-Contrast Slate Dark Body
    val secondaryTextColor: Color = Color(0xFF6B7280),// Subtle Gray Secondary Text
    val fontFamilyName: String = "Inter",
    val showPhoto: Boolean = true,
    val photoShape: PhotoShape = PhotoShape.CIRCLE,
    val fontSizeScale: Float = 1.0f
)
