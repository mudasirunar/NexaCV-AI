package com.mudasir.nexacvai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mudasir.nexacvai.data.local.dao.CVGenerationDao
import com.mudasir.nexacvai.data.local.dao.FavoriteTemplateDao
import com.mudasir.nexacvai.data.local.dao.TemplateDao
import com.mudasir.nexacvai.data.local.dao.UserProfileDao
import com.mudasir.nexacvai.data.local.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        CVGenerationEntity::class,
        TemplateEntity::class,
        FavoriteTemplateEntity::class,
        ExperienceEntity::class,
        ProjectEntity::class,
        EducationEntity::class,
        CertificationEntity::class,
        ReferenceEntity::class,
        SocialLinkEntity::class,
        LanguageEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NexaCVDatabase : RoomDatabase() {
    abstract val userProfileDao: UserProfileDao
    abstract val cvGenerationDao: CVGenerationDao
    abstract val templateDao: TemplateDao
    abstract val favoriteTemplateDao: FavoriteTemplateDao
}
