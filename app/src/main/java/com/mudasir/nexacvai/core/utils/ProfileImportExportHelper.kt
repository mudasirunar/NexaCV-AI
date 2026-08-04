package com.mudasir.nexacvai.core.utils

import android.content.Context
import android.net.Uri
import com.mudasir.nexacvai.domain.model.UserProfile
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * A highly robust helper class to package and extract single and multi-profile bundles to/from ZIP format (.nexacv).
 * Encapsulates serialization/deserialization logic using Moshi and binary picture handling.
 */
object ProfileImportExportHelper {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val userProfileAdapter = moshi.adapter(UserProfile::class.java)

    data class BundleManifest(
        val bundleVersion: Int = 1,
        val exportTimestamp: Long = System.currentTimeMillis(),
        val profileCount: Int = 0
    )

    private val bundleManifestAdapter = moshi.adapter(BundleManifest::class.java)

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
                val json = userProfileAdapter.toJson(profile)
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
     * Exports multiple [profiles] into a single multi-profile .nexacv bundle ZIP.
     */
    fun exportProfiles(context: Context, profiles: List<UserProfile>, outputStream: OutputStream): Boolean {
        if (profiles.isEmpty()) return false
        if (profiles.size == 1) {
            return exportProfile(context, profiles.first(), outputStream)
        }
        return try {
            ZipOutputStream(BufferedOutputStream(outputStream)).use { zos ->
                // 1. Write manifest.json
                val manifest = BundleManifest(
                    bundleVersion = 1,
                    exportTimestamp = System.currentTimeMillis(),
                    profileCount = profiles.size
                )
                val manifestJsonBytes = bundleManifestAdapter.toJson(manifest).toByteArray(Charsets.UTF_8)
                zos.putNextEntry(ZipEntry("manifest.json"))
                zos.write(manifestJsonBytes)
                zos.closeEntry()

                // 2. Write each profile entry under profiles/profile_{id}/
                profiles.forEach { profile ->
                    val folder = "profiles/profile_${profile.id}"
                    val json = userProfileAdapter.toJson(profile)
                    val jsonBytes = json.toByteArray(Charsets.UTF_8)
                    zos.putNextEntry(ZipEntry("$folder/profile.json"))
                    zos.write(jsonBytes)
                    zos.closeEntry()

                    val pictureUri = profile.profilePictureUri
                    if (!pictureUri.isNullOrBlank()) {
                        try {
                            context.contentResolver.openInputStream(Uri.parse(pictureUri))?.use { inputStream ->
                                zos.putNextEntry(ZipEntry("$folder/avatar.jpg"))
                                inputStream.copyTo(zos)
                                zos.closeEntry()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
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
     * Reads a single profile package from the ZIP [inputStream].
     */
    fun readProfileFromZip(inputStream: InputStream): ImportedProfileData? {
        return readProfilesFromZip(inputStream).firstOrNull()
    }

    /**
     * Reads single or multi-profile packages from [inputStream].
     * Supports both single-profile ZIP files and multi-profile bundle ZIP files.
     */
    fun readProfilesFromZip(inputStream: InputStream): List<ImportedProfileData> {
        val resultList = mutableListOf<ImportedProfileData>()
        try {
            val profileMap = mutableMapOf<String, ProfileRawData>()
            var legacyProfileJsonBytes: ByteArray? = null
            var legacyAvatarBytes: ByteArray? = null

            ZipInputStream(BufferedInputStream(inputStream)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    val name = entry.name
                    when {
                        name == "profile.json" -> {
                            val baos = ByteArrayOutputStream()
                            zis.copyTo(baos)
                            legacyProfileJsonBytes = baos.toByteArray()
                        }
                        name == "profile_picture.jpg" -> {
                            val baos = ByteArrayOutputStream()
                            zis.copyTo(baos)
                            legacyAvatarBytes = baos.toByteArray()
                        }
                        name.startsWith("profiles/") -> {
                            val parts = name.split("/")
                            if (parts.size >= 3) {
                                val folderKey = parts[1]
                                val fileName = parts[2]
                                val raw = profileMap.getOrPut(folderKey) { ProfileRawData() }
                                val baos = ByteArrayOutputStream()
                                zis.copyTo(baos)
                                when (fileName) {
                                    "profile.json" -> raw.jsonBytes = baos.toByteArray()
                                    "avatar.jpg" -> raw.avatarBytes = baos.toByteArray()
                                }
                            }
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }

            if (legacyProfileJsonBytes != null) {
                val jsonStr = legacyProfileJsonBytes!!.toString(Charsets.UTF_8)
                val profile = userProfileAdapter.fromJson(jsonStr)
                if (profile != null) {
                    resultList.add(
                        ImportedProfileData(
                            profile = profile,
                            hasPicture = legacyAvatarBytes != null,
                            pictureBytes = legacyAvatarBytes
                        )
                    )
                }
            }

            profileMap.values.forEach { raw ->
                val jsonBytes = raw.jsonBytes
                if (jsonBytes != null) {
                    val jsonStr = jsonBytes.toString(Charsets.UTF_8)
                    val profile = userProfileAdapter.fromJson(jsonStr)
                    if (profile != null) {
                        resultList.add(
                            ImportedProfileData(
                                profile = profile,
                                hasPicture = raw.avatarBytes != null,
                                pictureBytes = raw.avatarBytes
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultList
    }

    private class ProfileRawData {
        var jsonBytes: ByteArray? = null
        var avatarBytes: ByteArray? = null
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
