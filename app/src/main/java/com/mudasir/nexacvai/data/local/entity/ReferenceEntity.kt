package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "profile_references",
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
data class ReferenceEntity(
    @PrimaryKey
    val id: String,
    val profileId: Long,
    val fullName: String,
    val jobTitle: String,
    val company: String,
    val email: String?,
    val phone: String?,
    val linkedInUrl: String?,
    val notes: String?
)
