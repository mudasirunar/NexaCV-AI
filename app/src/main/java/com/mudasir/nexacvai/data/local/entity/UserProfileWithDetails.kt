package com.mudasir.nexacvai.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserProfileWithDetails(
    @Embedded
    val profile: UserProfileEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "profileId"
    )
    val experiences: List<ExperienceEntity> = emptyList(),
    
    @Relation(
        parentColumn = "id",
        entityColumn = "profileId"
    )
    val projects: List<ProjectEntity> = emptyList(),
    
    @Relation(
        parentColumn = "id",
        entityColumn = "profileId"
    )
    val educations: List<EducationEntity> = emptyList(),
    
    @Relation(
        parentColumn = "id",
        entityColumn = "profileId"
    )
    val certifications: List<CertificationEntity> = emptyList(),
    
    @Relation(
        parentColumn = "id",
        entityColumn = "profileId"
    )
    val references: List<ReferenceEntity> = emptyList(),
    
    @Relation(
        parentColumn = "id",
        entityColumn = "profileId"
    )
    val socialLinks: List<SocialLinkEntity> = emptyList(),
    
    @Relation(
        parentColumn = "id",
        entityColumn = "profileId"
    )
    val languages: List<LanguageEntity> = emptyList()
)
