package com.mudasir.nexacvai.domain.model.template

import androidx.compose.runtime.Immutable

/**
 * Categorization for CV templates used in filtering and UI showcase tabs.
 */
@Immutable
enum class TemplateCategory(val displayName: String) {
    ALL("All Templates"),
    FAVORITES("Favorites"),
    CUSTOM("Custom"),
    ATS("ATS-Friendly"),
    MODERN("Modern & Tech"),
    EXECUTIVE("Executive & Corporate"),
    CREATIVE("Creative & Healthcare")
}

