package com.mudasir.nexacvai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Basic Info
    val fullName: String = "",
    val professionalTitle: String = "",
    val emails: List<String> = emptyList(),
    val phones: List<String> = emptyList(),
    val country: String = "",
    val city: String = "",
    val address: String = "",
    val shortBio: String = "",
    val preferredRole: String = "",
    val yearsOfExperience: String = "",
    val profilePictureUri: String? = null,
    
    // Summary
    val careerObjective: String = "",
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

