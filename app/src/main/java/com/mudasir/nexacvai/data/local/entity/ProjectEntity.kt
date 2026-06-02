package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "projects",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profileId"])]
)
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val profileId: Long,
    val projectName: String,
    val roleInProject: String,
    val description: String,
    val technologiesUsed: List<String>,
    val projectLink: String,
    val startDate: String,
    val endDate: String
)
