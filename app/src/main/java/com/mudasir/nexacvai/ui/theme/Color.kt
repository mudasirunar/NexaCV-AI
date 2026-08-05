package com.mudasir.nexacvai.ui.theme

import androidx.compose.ui.graphics.Color

// 🟦 Primary Theme (Professional Blue)
val PrimaryBlue = Color(0xFF2563EB)
val SecondaryBlue = Color(0xFF1E40AF)
val AccentBlue = Color(0xFF38BDF8)

// 🌕 Light Mode Colors
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFE5E7EB)
val LightTextPrimary = Color(0xFF111827)
val LightTextSecondary = Color(0xFF9CA3AF)

// 🌑 Dark Mode Colors
val DarkBackground = Color(0xFF0B1220)
val DarkSurface = Color(0xFF111827)
val DarkBorder = Color(0xFF1F2937)
val DarkTextPrimary = Color(0xFFE5E7EB)
val DarkTextSecondary = Color(0xFF9CA3AF)

// Status / Accent Colors
val ErrorRed = Color(0xFFEF4444)

// 👤 WhatsApp Style Avatar Colors
val AvatarBg1 = Color(0xFFDBEAFE)
val AvatarText1 = Color(0xFF1E40AF)

val AvatarBg2 = Color(0xFFD1FAE5)
val AvatarText2 = Color(0xFF065F46)

val AvatarBg3 = Color(0xFFFCE7F3)
val AvatarText3 = Color(0xFF9D174D)

val AvatarBg4 = Color(0xFFFEF3C7)
val AvatarText4 = Color(0xFF92400E)

val AvatarBg5 = Color(0xFFEDE9FE)
val AvatarText5 = Color(0xFF5B21B6)

val AvatarBg6 = Color(0xFFFFEDD5)
val AvatarText6 = Color(0xFFC2410C)

val AvatarBg7 = Color(0xFFF3E8FF)
val AvatarText7 = Color(0xFF7E22CE)

val AvatarBg8 = Color(0xFFE0F2FE)
val AvatarText8 = Color(0xFF0369A1)

val AvatarBg9 = Color(0xFFFEE2E2)
val AvatarText9 = Color(0xFFB91C1C)

val AvatarBg10 = Color(0xFFECFDF5)
val AvatarText10 = Color(0xFF0F766E)

val AvatarBg11 = Color(0xFFFEF9C3)
val AvatarText11 = Color(0xFFA16207)

val AvatarBg12 = Color(0xFFE0F2F1)
val AvatarText12 = Color(0xFF004D40)

val AvatarBg13 = Color(0xFFECEFF1)
val AvatarText13 = Color(0xFF37474F)

val AvatarBg14 = Color(0xFFE8EAF6)
val AvatarText14 = Color(0xFF283593)

val AvatarBg15 = Color(0xFFF1F8E9)
val AvatarText15 = Color(0xFF33691E)

val AvatarBg16 = Color(0xFFF5F5F5)
val AvatarText16 = Color(0xFF424242)

val AvatarBg17 = Color(0xFFEFEBE9)
val AvatarText17 = Color(0xFF4E342E)

val AvatarBg18 = Color(0xFFF0F4C3)
val AvatarText18 = Color(0xFF827717)

val AvatarBg19 = Color(0xFFFFF9C4)
val AvatarText19 = Color(0xFFF57F17)

val AvatarBg20 = Color(0xFFE1BEE7)
val AvatarText20 = Color(0xFF4A148C)

data class AvatarColorPair(val background: Color, val text: Color)

val AvatarColorPairs = listOf(
    AvatarColorPair(AvatarBg1, AvatarText1),
    AvatarColorPair(AvatarBg2, AvatarText2),
    AvatarColorPair(AvatarBg3, AvatarText3),
    AvatarColorPair(AvatarBg4, AvatarText4),
    AvatarColorPair(AvatarBg5, AvatarText5),
    AvatarColorPair(AvatarBg6, AvatarText6),
    AvatarColorPair(AvatarBg7, AvatarText7),
    AvatarColorPair(AvatarBg8, AvatarText8),
    AvatarColorPair(AvatarBg9, AvatarText9),
    AvatarColorPair(AvatarBg10, AvatarText10),
    AvatarColorPair(AvatarBg11, AvatarText11),
    AvatarColorPair(AvatarBg12, AvatarText12),
    AvatarColorPair(AvatarBg13, AvatarText13),
    AvatarColorPair(AvatarBg14, AvatarText14),
    AvatarColorPair(AvatarBg15, AvatarText15),
    AvatarColorPair(AvatarBg16, AvatarText16),
    AvatarColorPair(AvatarBg17, AvatarText17),
    AvatarColorPair(AvatarBg18, AvatarText18),
    AvatarColorPair(AvatarBg19, AvatarText19),
    AvatarColorPair(AvatarBg20, AvatarText20)
)

// 📊 Progress Ring Dynamic Colors
val ProgressRed = Color(0xFFEF4444)
val ProgressOrange = Color(0xFFF97316)
val ProgressYellow = Color(0xFFEAB308)
val ProgressBlue = Color(0xFF2563EB)
val ProgressAccent = Color(0xFF38BDF8)
val ProgressGreen = Color(0xFF22C55E)

// 🥛 Transparent Overlay Background
val GlassOverlayBg = Color(0x73000000)     // Color.Black.copy(alpha = 0.45f)

// 🎨 Realistic Statistics Icon Colors 
val IconColorJob = Color(0xFF854D0E)       // Rich leather bronze/brown for BusinessCenter/Briefcase
val IconColorProject = Color(0xFFCA8A04)   // Golden manila yellow/amber for Folder
val IconColorCert = Color(0xFF059669)      // Vibrant emerald green for Verified badge
val IconColorEmail = Color(0xFFEA4335)     // Gmail envelope red for mail icon
val IconColorPhone = Color(0xFF16A34A)     // Vibrant green dialer for phone icon

// 🎨 Custom Snackbar Colors
val SnackbarBgDark = Color(0xFF1E293B)     // Premium dark slate/charcoal for dark mode snackbars
val SnackbarTextDark = Color(0xFFF8FAFC)   // Warm white/off-white for dark mode text readability
val SnackbarBgLight = Color(0xFFF1F5F9)    // Soft light slate/gray for light mode snackbars
val SnackbarTextLight = Color(0xFF1E293B)  // Dark slate for light mode text readability

// 📦 Import/Export Bottom Sheet Icon Tints
val SheetIconSuccessGreen = Color(0xFF22C55E)  // Green check for import success state
val SheetIconWarningAmber = Color(0xFFF59E0B)  // Amber warning for duplicate conflicts

// 🔍 Search Bar Result Colors
val SearchMatchContainer = Color(0xFF22C55E)     // green container tint
val SearchMatchBorder = Color(0xFF16A34A)        // green border tint
val SearchNoMatchContainer = Color(0xFFEF4444)   // red container tint
val SearchNoMatchBorder = Color(0xFFDC2626)      // red border tint