package com.mudasir.nexacvai

import android.app.Application
import com.mudasir.nexacvai.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NexaCVApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@NexaCVApplication)
            modules(appModule)
        }
    }
}
