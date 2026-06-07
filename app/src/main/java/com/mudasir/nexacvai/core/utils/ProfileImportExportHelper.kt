package com.mudasir.nexacvai.core.utils

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.mudasir.nexacvai.domain.model.UserProfile
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * A highly robust helper class to package and extract profiles to/from ZIP format.
 * Encapsulates serialization/deserialization logic using Gson and binary picture handling.
 */
object ProfileImportExportHelper {
    private val gson = Gson()

    data class ImportedProfileData(
        val profile: UserProfile,
        val hasPicture: Boolean,
        val pictureBytes: ByteArray? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ImportedProfileData

            if (profile != other.profile) return false
            if (hasPicture != other.hasPicture) return false
            if (pictureBytes != null) {
                if (other.pictureBytes == null) return false
                if (!pictureBytes.contentEquals(other.pictureBytes)) return false
            } else if (other.pictureBytes != null) return false

            return true
        }

        override fun hashCode(): Int {
            var result = profile.hashCode()
            result = 31 * result + hasPicture.hashCode()
            result = 31 * result + (pictureBytes?.contentHashCode() ?: 0)
            return result
        }
    }

    /**
     * Exports [profile] textual data as JSON and bundles its profile picture (if exists) into [outputStream] as a ZIP archive.
     */
    fun exportProfile(context: Context, profile: UserProfile, outputStream: OutputStream): Boolean {
        return try {
            ZipOutputStream(BufferedOutputStream(outputStream)).use { zos ->
                // 1. Write profile.json
                val json = gson.toJson(profile)
                val jsonBytes = json.toByteArray(Charsets.UTF_8)
                zos.putNextEntry(ZipEntry("profile.json"))
                zos.write(jsonBytes)
                zos.closeEntry()

                // 2. Write profile picture if present
                val pictureUri = profile.profilePictureUri
                if (!pictureUri.isNullOrBlank()) {
                    try {
                        context.contentResolver.openInputStream(Uri.parse(pictureUri))?.use { inputStream ->
                            zos.putNextEntry(ZipEntry("profile_picture.jpg"))
                            inputStream.copyTo(zos)
                            zos.closeEntry()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Fail gracefully on picture copy; text content is still saved
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Reads a profile package from the ZIP [inputStream].
     * Avoids using reader wrapper classes directly on the ZipInputStream to prevent buffer overshoot.
     */
    fun readProfileFromZip(inputStream: InputStream): ImportedProfileData? {
        return try {
            var profile: UserProfile? = null
            var hasPicture = false
            var pictureBytes: ByteArray? = null

            ZipInputStream(BufferedInputStream(inputStream)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    when (entry.name) {
                        "profile.json" -> {
                            val baos = ByteArrayOutputStream()
                            zis.copyTo(baos)
                            val json = baos.toString("UTF-8")
                            profile = gson.fromJson(json, UserProfile::class.java)
                        }
                        "profile_picture.jpg" -> {
                            hasPicture = true
                            val baos = ByteArrayOutputStream()
                            zis.copyTo(baos)
                            pictureBytes = baos.toByteArray()
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }

            if (profile != null) {
                ImportedProfileData(profile, hasPicture, pictureBytes)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves the extracted profile picture bytes to the app's internal persistent directory.
     * Generates a unique filename using profileId and current epoch millis.
     */
    fun saveImportedProfilePicture(
        context: Context,
        pictureBytes: ByteArray,
        profileId: Long
    ): String? {
        return try {
            val outputDir = File(context.filesDir, "profile_pictures").apply { mkdirs() }
            val outputFile = File(outputDir, "profile_${profileId}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(outputFile).use { fos ->
                fos.write(pictureBytes)
            }
            Uri.fromFile(outputFile).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
