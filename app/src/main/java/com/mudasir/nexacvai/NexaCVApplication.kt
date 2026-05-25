package com.mudasir.nexacvai

import android.app.Application
import com.mudasir.nexacvai.data.local.NexaCVDatabase
import com.mudasir.nexacvai.di.appModule
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import com.mudasir.nexacvai.domain.usecase.DeleteProfileUseCase
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.domain.usecase.GetProfileUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase

class NexaCVApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@NexaCVApplication)
            modules(appModule)
        }

        // Pre-warm Room database asynchronously to avoid Main thread disk I/O on first screen launch
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = get<NexaCVDatabase>()
                db.query("SELECT 1", null).use { cursor ->
                    if (cursor.moveToFirst()) {
                        // DB fully initialized
                    }
                }
                
                // Warm up Koin use cases & repositories to eliminate DI instantiation delay on navigation
                get<UserProfileRepository>()
                get<GetAllProfilesUseCase>()
                get<GetProfileUseCase>()
                get<SaveProfileUseCase>()
                get<DeleteProfileUseCase>()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
