package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cv_generations")
data class CVGenerationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val templateId: Long, // Links to TemplateEntity (preferences used for this CV)
    val jobDescription: String,
    val generatedCV: String, // Can be JSON or structured text from AI
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
