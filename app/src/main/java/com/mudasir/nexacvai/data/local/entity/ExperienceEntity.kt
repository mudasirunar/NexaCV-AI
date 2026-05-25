package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "experiences",
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
data class ExperienceEntity(
    @PrimaryKey
    val id: String,
    val profileId: Long,
    val jobTitle: String,
    val companyName: String,
    val employmentType: String,
    val workMode: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val isCurrentlyWorking: Boolean,
    val description: String,
    val responsibilities: String,
    val achievements: String,
    val technologiesUsed: List<String>
)
