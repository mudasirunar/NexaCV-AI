package com.mudasir.nexacvai.presentation.templates

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mudasir.nexacvai.domain.model.UserProfile

/**
 * 🧩 TEMPLATE ENGINE
 * Templates are STATIC Compose code, not dynamically generated data.
 */
enum class CVTemplate(val key: String, val displayName: String) {
    ATS("ats", "ATS Professional"),
    MODERN("modern", "Modern Clean"),
    DEVELOPER("developer", "Developer Portfolio"),
    SIDEBAR("sidebar", "Sidebar Layout")
}

@Composable
fun TemplateEngine(
    templateKey: String,
    profileData: UserProfile,
    primaryColorHex: String? = null
) {
    when (templateKey) {
        CVTemplate.ATS.key -> AtsTemplate(profileData)
        CVTemplate.MODERN.key -> ModernTemplate(profileData, primaryColorHex)
        CVTemplate.DEVELOPER.key -> DeveloperTemplate(profileData)
        CVTemplate.SIDEBAR.key -> SidebarTemplate(profileData)
        else -> AtsTemplate(profileData) // Safe fallback
    }
}

// ---------------------------------------------------------------------------
// 🧱 TEMPLATE STUBS (To be fully implemented later)
// ---------------------------------------------------------------------------

@Composable
fun AtsTemplate(profileData: UserProfile) {
    // Single column, black & white, recruiter friendly
    Text(text = "ATS Template Structure (Placeholder for ${profileData.fullName})")
}

@Composable
fun ModernTemplate(profileData: UserProfile, primaryColorHex: String?) {
    // Soft accent colors, structured sections
    Text(text = "Modern Template Structure (Placeholder for ${profileData.fullName})")
}

@Composable
fun DeveloperTemplate(profileData: UserProfile) {
    // Project focused layout, monospaced accents
    Text(text = "Developer Template Structure (Placeholder for ${profileData.fullName})")
}

@Composable
fun SidebarTemplate(profileData: UserProfile) {
    // Left column skills, right column experience
    Text(text = "Sidebar Template Structure (Placeholder for ${profileData.fullName})")
}

