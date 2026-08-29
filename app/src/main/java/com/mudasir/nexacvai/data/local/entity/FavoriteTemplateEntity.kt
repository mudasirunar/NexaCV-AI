package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a user's favorited / bookmarked resume template in Room.
 * Normalized and indexed for instant local queries and future Firebase Firestore synchronization.
 */
@Entity(
    tableName = "favorite_templates",
    indices = [
        Index(value = ["templateId"], unique = true),
        Index(value = ["createdAt"]),
        Index(value = ["updatedAt"])
    ]
)
data class FavoriteTemplateEntity(
    @PrimaryKey
    val templateId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
