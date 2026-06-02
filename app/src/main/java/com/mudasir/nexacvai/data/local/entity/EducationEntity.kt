package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "educations",
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
data class EducationEntity(
    @PrimaryKey
    val id: String,
    val profileId: Long,
    val degree: String,
    val fieldOfStudy: String,
    val instituteName: String,
    val grade: String,
    val startDate: String,
    val endDate: String,
    val isCurrentlyStudying: Boolean,
    val description: String
)
