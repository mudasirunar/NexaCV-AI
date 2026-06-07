package com.mudasir.nexacvai.core.utils

object NameUtils {
    /**
     * Extracts initials from a full name string:
     * - If 1 name: Extract the first letter (e.g., "Mudasir" -> "M").
     * - If 2 names: Extract first letter of each (e.g., "Mudasir Ali" -> "MA").
     * - If 3+ names: Extract first letters of the first two tokens only (e.g., "Mudasir Ali Khan" -> "MA").
     */
    fun getInitials(fullName: String): String {
        val trimmed = fullName.trim()
        if (trimmed.isEmpty()) return "?"
        
        val tokens = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
        return when (tokens.size) {
            0 -> "?"
            1 -> {
                tokens[0].take(1).uppercase()
            }
            else -> {
                val first = tokens[0].take(1).uppercase()
                val second = tokens[1].take(1).uppercase()
                "$first$second"
            }
        }
    }
}
