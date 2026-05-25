package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a saved template configuration for a user.
 * 
 * IMPORTANT: Templates are NOT stored as UI data. 
 * This table ONLY stores the user's selected template preferences (e.g. which Compose layout to use, colors).
 */
@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateKey: String, // Maps directly to static Compose layouts: "ats", "modern", "developer"
    val primaryColorHex: String?, // Optional color override
    val fontStyle: String?, // Optional font override
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
