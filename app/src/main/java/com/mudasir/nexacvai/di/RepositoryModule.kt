package com.mudasir.nexacvai.di

import com.mudasir.nexacvai.data.repository.UserProfileRepositoryImpl
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.mudasir.nexacvai.data.repository.TemplateRepositoryImpl
import com.mudasir.nexacvai.domain.repository.TemplateRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindTemplateRepository(
        impl: TemplateRepositoryImpl
    ): TemplateRepository
}
