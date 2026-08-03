package com.mudasir.nexacvai

import android.app.Application
import com.mudasir.nexacvai.data.local.NexaCVDatabase
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import com.mudasir.nexacvai.domain.usecase.DeleteProfileUseCase
import com.mudasir.nexacvai.domain.usecase.GetAllProfilesUseCase
import com.mudasir.nexacvai.domain.usecase.GetProfileUseCase
import com.mudasir.nexacvai.domain.usecase.SaveProfileUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class NexaCVApplication : Application() {

    @Inject
    lateinit var database: NexaCVDatabase

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var getAllProfilesUseCase: GetAllProfilesUseCase

    @Inject
    lateinit var getProfileUseCase: GetProfileUseCase

    @Inject
    lateinit var saveProfileUseCase: SaveProfileUseCase

    @Inject
    lateinit var deleteProfileUseCase: DeleteProfileUseCase

    override fun onCreate() {
        super.onCreate()

        // Pre-warm Room database asynchronously to avoid Main thread disk I/O on first screen launch
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.query("SELECT 1", null).use { cursor ->
                    if (cursor.moveToFirst()) {
                        // DB fully initialized
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
