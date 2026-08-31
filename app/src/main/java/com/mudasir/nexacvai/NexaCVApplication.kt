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

import com.mudasir.nexacvai.core.pdf.PdfGeneratorEngine
import com.mudasir.nexacvai.core.pdf.TemplateThumbnailGenerator
import com.mudasir.nexacvai.core.result.AppResult
import com.mudasir.nexacvai.data.local.datastore.AppSettingsManager
import com.mudasir.nexacvai.di.ApplicationScope
import com.mudasir.nexacvai.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.first

@HiltAndroidApp
class NexaCVApplication : Application() {

    @Inject
    lateinit var database: NexaCVDatabase

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var templateRepository: TemplateRepository

    @Inject
    lateinit var appSettingsManager: AppSettingsManager

    @Inject
    lateinit var pdfGeneratorEngine: PdfGeneratorEngine

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

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

        // Non-blocking asynchronous startup cache pre-warming (Zero Main Thread Contention)
        applicationScope.launch {
            try {
                // 1. Pre-warm SQLite Room Database
                database.query("SELECT 1", null).use { cursor ->
                    cursor.moveToFirst()
                }

                // 2. Pre-warm UserProfileRepository hot in-memory StateFlow
                userProfileRepository.getAllProfiles().first()

                // 3. Pre-warm TemplateRepository hot in-memory catalog & favorites
                templateRepository.getAllTemplates()
                templateRepository.getFavoriteTemplateIds().first()

                // 4. Pre-warm AppSettingsManager DataStore
                appSettingsManager.themeModeFlow.first()
                appSettingsManager.profileSortOrderFlow.first()
                appSettingsManager.hasSeenOnboardingFlow.first()

                // 5. Pre-warm Heavy Singletons and Catalog Data
                com.mudasir.nexacvai.data.templates.BuiltInTemplatesCatalog.ALL_TEMPLATES

                // 6. Pre-load ViewModel & Composable Classes into ART JVM Memory
                val warmClasses = listOf(
                    com.mudasir.nexacvai.presentation.ui.templates.viewmodel.TemplatesViewModel::class.java,
                    com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ProfilesViewModel::class.java,
                    com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.ViewProfileViewModel::class.java,
                    com.mudasir.nexacvai.presentation.ui.profiles.viewmodel.CreateProfileViewModel::class.java,
                    com.mudasir.nexacvai.core.pdf.PdfGeneratorEngine::class.java,
                    com.mudasir.nexacvai.core.pdf.TemplateThumbnailGenerator::class.java
                )
                warmClasses.forEach { clazz ->
                    Class.forName(clazz.name)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
