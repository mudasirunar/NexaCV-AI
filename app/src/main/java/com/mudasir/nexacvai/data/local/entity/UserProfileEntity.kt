package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profiles",
    indices = [Index(value = ["updatedAt"])]
)
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Basic Info
    val fullName: String = "",
    val professionalTitle: String = "",
    val emails: List<String> = emptyList(),
    val phones: List<String> = emptyList(),
    val dateOfBirth: String = "",
    val address: String = "",
    val yearsOfExperience: String = "",
    val profilePictureUri: String? = null,
    
    // Summary
    val professionalSummary: String = "",
    
    // Primitive lists (retained via List<String> converter)
    val skills: List<String> = emptyList(),
    
    // Additional Info
    val hobbies: String = "",
    val volunteerWork: String = "",
    val awards: String = "",
    
    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
