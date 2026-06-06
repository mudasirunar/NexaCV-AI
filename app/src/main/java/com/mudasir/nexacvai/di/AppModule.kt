package com.mudasir.nexacvai.di

import androidx.room.Room
import com.mudasir.nexacvai.data.local.NexaCVDatabase
import com.mudasir.nexacvai.data.local.datastore.AppSettingsManager
import com.mudasir.nexacvai.data.repository.UserProfileRepositoryImpl
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.domain.usecase.*
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ProfilesViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ViewProfileViewModel
import com.mudasir.nexacvai.presentation.ui.profiles.utils.ProfileDeleteManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    
    // ProfileDeleteManager
    single { ProfileDeleteManager(get()) }
    
    // DataStore
    single { AppSettingsManager(androidContext()) }
    
    // Room Database
    single { 
        Room.databaseBuilder(
            androidContext(),
            NexaCVDatabase::class.java,
            "nexacv_database"
        ).build()
    }
    
    // DAOs
    single { get<NexaCVDatabase>().userProfileDao }
    single { get<NexaCVDatabase>().cvGenerationDao }
    single { get<NexaCVDatabase>().templateDao }
    
    // Repositories
    single<UserProfileRepository> { UserProfileRepositoryImpl(get()) }

    // UseCases
    single { SaveProfileUseCase(get()) }
    single { GetProfileUseCase(get()) }
    single { DeleteProfileUseCase(get()) }
    single { GetAllProfilesUseCase(get()) }

    // ViewModels
    viewModel { ProfilesViewModel(get(), get(), get()) }
    viewModel { CreateProfileViewModel(get(), get(), get()) }
    viewModel { ViewProfileViewModel(get(), get(), get()) }
}

