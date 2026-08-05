package com.mudasir.nexacvai.domain.model

enum class ProfileSortOrder(
    val id: String,
    val displayName: String
) {
    NEWEST_FIRST("newest_first", "Newest First"),
    OLDEST_FIRST("oldest_first", "Oldest First"),
    NAME_ASC("name_asc", "Name (A to Z)"),
    NAME_DESC("name_desc", "Name (Z to A)"),
    LAST_UPDATED("last_updated", "Last Updated"),
    MOST_USED("most_used", "Most Used");

    companion object {
        val DEFAULT = NEWEST_FIRST

        fun fromId(id: String?): ProfileSortOrder {
            return entries.find { it.id == id } ?: DEFAULT
        }
    }
}
