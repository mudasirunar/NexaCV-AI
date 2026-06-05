package com.mudasir.nexacvai.presentation.ui.profiles.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageCompressionHelper {

    /**
     * Copies, resizes, and compresses an image from [sourceUri] into the app's internal files directory.
     * Corrects image orientation using EXIF data.
     *
     * @param context Android context
     * @param sourceUri Camera or gallery URI to process
     * @return Path to the local compressed image, or null if processing fails
     */
    suspend fun compressAndSaveProfilePicture(context: Context, sourceUri: Uri, profileId: Long = 0L): String? {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver

                // 1. Get EXIF Orientation rotation
                var rotation = 0
                try {
                    contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                        val exifInterface = ExifInterface(inputStream)
                        val orientation = exifInterface.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                        rotation = when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> 90
                            ExifInterface.ORIENTATION_ROTATE_180 -> 180
                            ExifInterface.ORIENTATION_ROTATE_270 -> 270
                            else -> 0
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // 2. Decode bounds to determine scaling factor (memory-friendly)
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }

                val maxDimension = 1200
                var srcWidth = options.outWidth
                var srcHeight = options.outHeight

                var sampleSize = 1
                while (srcWidth / 2 >= maxDimension && srcHeight / 2 >= maxDimension) {
                    srcWidth /= 2
                    srcHeight /= 2
                    sampleSize *= 2
                }

                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }

                // 3. Decode Bitmap using selected sample size
                var bitmap = contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, decodeOptions)
                } ?: return@withContext null

                // 4. Apply scale and EXIF rotation
                val width = bitmap.width
                val height = bitmap.height
                val scale = maxDimension.toFloat() / kotlin.math.max(width, height)

                val matrix = Matrix()
                if (scale < 1.0f) {
                    matrix.postScale(scale, scale)
                }
                if (rotation != 0) {
                    matrix.postRotate(rotation.toFloat())
                }

                val processedBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, width, height, matrix, true
                )
                if (processedBitmap != bitmap) {
                    bitmap.recycle()
                    bitmap = processedBitmap
                }

                // 5. Save bitmap to app's secure internal persistent folder
                val outputDir = File(context.filesDir, "profile_pictures").apply { mkdirs() }
                
                val outputFile = File(outputDir, "profile_${profileId}_${System.currentTimeMillis()}.jpg")
                FileOutputStream(outputFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                }
                bitmap.recycle()

                // Return persistent file path URI
                Uri.fromFile(outputFile).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
