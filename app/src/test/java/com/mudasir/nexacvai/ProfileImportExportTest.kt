package com.mudasir.nexacvai

import com.mudasir.nexacvai.core.utils.ProfileImportExportHelper
import com.mudasir.nexacvai.domain.model.UserProfile
import com.mudasir.nexacvai.domain.usecase.ImportProfileUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Unit test suite targeting Profile Import/Export Use Cases and Helpers.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileImportExportTest {

    @Test
    fun testImportProfileUseCase_savesProfileToRepository() = runTest {
        val fakeRepository = ProfilesViewModelTest.FakeUserProfileRepository()
        val useCase = ImportProfileUseCase(fakeRepository)
        val profile = createDummyProfile(5L, "David")
        
        val resultId = useCase(profile)
        
        assertEquals(5L, resultId)
        assertEquals(profile, fakeRepository.savedProfiles[5L])
    }

    @Test
    fun testExportAndReadProfileZip_withoutPicture() = runTest {
        val originalProfile = createDummyProfile(1L, "Alice Developer")
        val outputStream = ByteArrayOutputStream()
        val mockContext = android.content.ContextWrapper(null)

        // Export using helper
        val exportResult = ProfileImportExportHelper.exportProfile(mockContext, originalProfile, outputStream)
        assertTrue(exportResult)

        // Read back using helper
        val zipBytes = outputStream.toByteArray()
        val inputStream = ByteArrayInputStream(zipBytes)
        val importedData = ProfileImportExportHelper.readProfileFromZip(inputStream)

        assertNotNull(importedData)
        assertEquals(originalProfile, importedData?.profile)
        assertFalse(importedData!!.hasPicture)
        assertNull(importedData.pictureBytes)
    }

    @Test
    fun testReadProfileZip_withPicture() = runTest {
        val originalProfile = createDummyProfile(2L, "Bob Designer")
        val outputStream = ByteArrayOutputStream()
        
        // Construct ZIP archive with Moshi profile data and dummy picture entry manually
        val moshi = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(UserProfile::class.java)
        java.util.zip.ZipOutputStream(outputStream).use { zos ->
            // Write profile.json
            val json = adapter.toJson(originalProfile)
            zos.putNextEntry(java.util.zip.ZipEntry("profile.json"))
            zos.write(json.toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            // Write dummy profile picture
            val dummyImgBytes = byteArrayOf(1, 2, 3, 4, 5)
            zos.putNextEntry(java.util.zip.ZipEntry("profile_picture.jpg"))
            zos.write(dummyImgBytes)
            zos.closeEntry()
        }

        // Parse package using helper
        val zipBytes = outputStream.toByteArray()
        val inputStream = ByteArrayInputStream(zipBytes)
        val importedData = ProfileImportExportHelper.readProfileFromZip(inputStream)

        assertNotNull(importedData)
        assertEquals(originalProfile, importedData?.profile)
        assertTrue(importedData!!.hasPicture)
        assertNotNull(importedData.pictureBytes)
        assertTrue(byteArrayOf(1, 2, 3, 4, 5).contentEquals(importedData.pictureBytes!!))
    }

    @Test
    fun testReadProfileFromZip_withCorruptData_returnsNull() = runTest {
        val corruptBytes = byteArrayOf(0, 1, 2, 3, 4)
        val inputStream = ByteArrayInputStream(corruptBytes)
        val importedData = ProfileImportExportHelper.readProfileFromZip(inputStream)
        
        assertNull(importedData)
    }

    @Test
    fun testExportAndReadProfilesZip_multiProfile() = runTest {
        val p1 = createDummyProfile(101L, "Alice Lead")
        val p2 = createDummyProfile(102L, "Bob Engineer")
        val outputStream = ByteArrayOutputStream()
        val mockContext = android.content.ContextWrapper(null)

        val exportResult = ProfileImportExportHelper.exportProfiles(mockContext, listOf(p1, p2), outputStream)
        assertTrue(exportResult)

        val zipBytes = outputStream.toByteArray()
        val inputStream = ByteArrayInputStream(zipBytes)
        val importedList = ProfileImportExportHelper.readProfilesFromZip(inputStream)

        assertEquals(2, importedList.size)
        assertEquals(p1, importedList[0].profile)
        assertEquals(p2, importedList[1].profile)
    }

    private fun createDummyProfile(id: Long, name: String): UserProfile {
        return UserProfile(
            id = id,
            fullName = name,
            profilePictureUri = null,
            professionalTitle = "Developer",
            dateOfBirth = "",
            emails = emptyList(),
            phones = emptyList(),
            address = "",
            yearsOfExperience = "0",
            skills = emptyList(),
            experiences = emptyList(),
            projects = emptyList(),
            educations = emptyList(),
            certifications = emptyList(),
            references = emptyList(),
            socialLinks = emptyList(),
            languages = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}
