package com.mudasir.nexacvai.domain.usecase

import android.content.Context
import android.net.Uri
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class DuplicateProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(context: Context, originalProfile: UserProfile): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        val newExperiences = originalProfile.experiences.map { it.copy(id = UUID.randomUUID().toString()) }
        val newProjects = originalProfile.projects.map { it.copy(id = UUID.randomUUID().toString()) }
        val newEducations = originalProfile.educations.map { it.copy(id = UUID.randomUUID().toString()) }
        val newCertifications = originalProfile.certifications.map { it.copy(id = UUID.randomUUID().toString()) }
        val newReferences = originalProfile.references.map { it.copy(id = UUID.randomUUID().toString()) }
        val newSocialLinks = originalProfile.socialLinks.map { it.copy(id = UUID.randomUUID().toString()) }
        val newLanguages = originalProfile.languages.map { it.copy(id = UUID.randomUUID().toString()) }

        val initialCopy = originalProfile.copy(
            id = 0L,
            uuid = UUID.randomUUID().toString(),
            profilePictureUri = null,
            experiences = newExperiences,
            projects = newProjects,
            educations = newEducations,
            certifications = newCertifications,
            references = newReferences,
            socialLinks = newSocialLinks,
            languages = newLanguages,
            sourceProfileId = originalProfile.id,
            sourceProfileName = originalProfile.fullName,
            isCopyTagDismissed = false,
            createdAt = now,
            updatedAt = now
        )

        val newProfileId = repository.insertProfile(initialCopy)

        if (!originalProfile.profilePictureUri.isNullOrBlank()) {
            val copiedPictureUri = copyProfilePictureFile(context, originalProfile.profilePictureUri, newProfileId)
            if (copiedPictureUri != null) {
                val finalCopy = initialCopy.copy(
                    id = newProfileId,
                    profilePictureUri = copiedPictureUri
                )
                repository.updateProfile(finalCopy)
            }
        }

        return@withContext newProfileId
    }

    private fun copyProfilePictureFile(context: Context, sourceUriString: String, newProfileId: Long): String? {
        return try {
            val sourceUri = Uri.parse(sourceUriString)
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val profilePicsDir = File(context.filesDir, "profile_pictures")
            if (!profilePicsDir.exists()) profilePicsDir.mkdirs()
            val destFile = File(profilePicsDir, "profile_${newProfileId}_picture.jpg")
            destFile.outputStream().use { outputStream ->
                inputStream.use { input -> input.copyTo(outputStream) }
            }
            Uri.fromFile(destFile).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
