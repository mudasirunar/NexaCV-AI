package com.mudasir.nexacvai.di

import android.content.Context
import androidx.room.Room
import com.mudasir.nexacvai.data.local.NexaCVDatabase
import com.mudasir.nexacvai.data.local.dao.CVGenerationDao
import com.mudasir.nexacvai.data.local.dao.TemplateDao
import com.mudasir.nexacvai.data.local.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): NexaCVDatabase {
        return Room.databaseBuilder(
            context,
            NexaCVDatabase::class.java,
            "nexacv_database"
        ).build()
    }

    @Provides
    fun provideUserProfileDao(db: NexaCVDatabase): UserProfileDao {
        return db.userProfileDao
    }

    @Provides
    fun provideCVGenerationDao(db: NexaCVDatabase): CVGenerationDao {
        return db.cvGenerationDao
    }

    @Provides
    fun provideTemplateDao(db: NexaCVDatabase): TemplateDao {
        return db.templateDao
    }
}
