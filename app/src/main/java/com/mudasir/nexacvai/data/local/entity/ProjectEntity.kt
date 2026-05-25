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
    val description: String,
    val technologiesUsed: List<String>,
    val githubLink: String,
    val liveDemoLink: String,
    val playStoreLink: String,
    val roleInProject: String,
    val isTeamProject: Boolean,
    val startDate: String,
    val endDate: String,
    val projectImagesUris: List<String>
)
