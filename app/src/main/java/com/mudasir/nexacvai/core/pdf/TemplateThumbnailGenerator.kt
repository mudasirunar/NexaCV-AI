package com.mudasir.nexacvai.core.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.LruCache
import androidx.compose.ui.graphics.Color
import com.mudasir.nexacvai.domain.model.template.ResumeTemplate
import com.mudasir.nexacvai.domain.model.template.TemplateStyle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enterprise Thumbnail Engine for generating and caching high-fidelity 
 * Page 0 Bitmaps of real A4 PDF templates.
 */
@Singleton
class TemplateThumbnailGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfGeneratorEngine: PdfGeneratorEngine
) {
    companion object {
        private val memoryCache = LruCache<String, Bitmap>(32)

        suspend fun generateThumbnail(
            context: Context,
            pdfEngine: PdfGeneratorEngine,
            template: ResumeTemplate
        ): Bitmap? = withContext(Dispatchers.IO) {
            val meta = template.metadata
            val cleanPrimaryHex = meta.previewPrimaryColorHex.removePrefix("#")
            val cacheKey = "thumb_${meta.id}_$cleanPrimaryHex"

            // 1. Check in-memory LRU Cache
            memoryCache.get(cacheKey)?.let { return@withContext it }

            // 2. Check on-disk Cache
            val cacheFile = File(context.cacheDir, "$cacheKey.png")
            if (cacheFile.exists() && cacheFile.length() > 0) {
                val cachedBitmap = BitmapFactory.decodeFile(cacheFile.absolutePath)
                if (cachedBitmap != null) {
                    memoryCache.put(cacheKey, cachedBitmap)
                    return@withContext cachedBitmap
                }
            }

            // 3. Generate Real Vector PDF using PdfGeneratorEngine
            try {
                val primaryColor = try {
                    Color(AndroidColor.parseColor(meta.previewPrimaryColorHex))
                } catch (e: Exception) {
                    Color(0xFF1E3A8A)
                }
                val accentColor = try {
                    Color(AndroidColor.parseColor(meta.previewAccentColorHex))
                } catch (e: Exception) {
                    Color(0xFF3B82F6)
                }
                val style = TemplateStyle(
                    primaryColor = primaryColor,
                    accentColor = accentColor,
                    showPhoto = meta.supportsPhoto,
                    photoShape = meta.defaultPhotoShape
                )

                val tempPdf = pdfEngine.generateCvPdf(
                    template = template,
                    data = template.defaultData,
                    templateStyle = style,
                    outputFileName = "thumb_temp_${meta.id}.pdf"
                )

                if (!tempPdf.exists() || tempPdf.length() == 0L) {
                    return@withContext null
                }

                // 4. Render Page 0 to Bitmap via Android PdfRenderer
                val pfd = ParcelFileDescriptor.open(tempPdf, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(pfd)
                val page = pdfRenderer.openPage(0)

                val targetWidth = 595
                val targetHeight = 842
                val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawColor(AndroidColor.WHITE)

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                page.close()
                pdfRenderer.close()
                pfd.close()
                tempPdf.delete()

                // 5. Persist to Disk Cache
                try {
                    FileOutputStream(cacheFile).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
                    }
                } catch (e: Exception) {
                    // Ignore disk write failure
                }

                memoryCache.put(cacheKey, bitmap)
                bitmap
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getTemplateThumbnail(template: ResumeTemplate): Bitmap? {
        return generateThumbnail(context, pdfGeneratorEngine, template)
    }
}
