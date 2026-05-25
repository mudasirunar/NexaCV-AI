package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "certifications",
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
data class CertificationEntity(
    @PrimaryKey
    val id: String,
    val profileId: Long,
    val certificationName: String,
    val issuingOrganization: String,
    val issueDate: String,
    val expiryDate: String,
    val credentialUrl: String
)
