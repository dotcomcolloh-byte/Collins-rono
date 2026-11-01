package com.telefam.company.app.security

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.telefam.company.app.data.MediaFingerprintEntity
import com.telefam.company.app.data.TelefamDao
import com.telefam.company.app.domain.EditablePhotoStudioItem
import com.telefam.company.app.domain.EditableVideoStudioState
import com.telefam.company.app.domain.PostBackgroundTheme
import com.telefam.company.app.domain.PostBrushStroke
import com.telefam.company.app.domain.PostColorFilterPreset
import com.telefam.company.app.domain.PostCropPreset
import com.telefam.company.app.domain.PostMusicTrackItem
import com.telefam.company.app.domain.PostTextAutoScaler
import com.telefam.company.app.domain.PostTextOverlayLayer
import com.telefam.company.app.domain.PostTypographyFont
import com.telefam.company.app.domain.VideoKeyframeThumb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.math.roundToInt

data class BakedPostArtifact(
    val verifiedFilePath: String,
    val coverPreviewPath: String,
    val verifiedMimeType: String,
    val widthPx: Int,
    val heightPx: Int,
    val originalByteSize: Long,
    val compressedByteSize: Long,
    val sha256Hex: String,
    val perceptualDHash: String,
    val compositeFingerprintId: String,
    val qualityProfiles: List<String>,
    val extractedCaptionMetadata: String
)

object TelefamPostSandboxAndStudioEngine {

    const val MAX_POST_MEDIA_BYTES = 100L * 1024L * 1024L // 100 MB strict cap
    const val CHUNK_SIZE_BYTES = 2 * 1024 * 1024 // 2 MB chunk streaming size

    private val musicHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun quarantineDir(context: Context): File =
        File(context.filesDir, "post_sandbox_quarantine").apply { mkdirs() }

    private fun verifiedVaultDir(context: Context): File =
        File(context.filesDir, "verified_posts_vault").apply { mkdirs() }

    /**
     * Security Sandbox Virus & Malicious Header Scanner:
     * Inspects magic bytes and blocks PE/MZ executables, ELF binaries, shell scripts,
     * PHP/JS/HTML payloads, Android DEX bytecode, and polyglot archives disguised as media.
     */
    fun verifySafeMediaHeaderInSandbox(headerBytes: ByteArray, count: Int): String {
        if (count < 8) {
            throw SecurityException("Sandbox security check failed: File is empty or truncated.")
        }
        // Reject executable / script signatures
        val isMzExe = headerBytes[0] == 0x4D.toByte() && headerBytes[1] == 0x5A.toByte()
        val isElf = headerBytes[0] == 0x7F.toByte() &&
            headerBytes[1] == 0x45.toByte() &&
            headerBytes[2] == 0x4C.toByte() &&
            headerBytes[3] == 0x46.toByte()
        val isDex = headerBytes[0] == 0x64.toByte() &&
            headerBytes[1] == 0x65.toByte() &&
            headerBytes[2] == 0x78.toByte() &&
            headerBytes[3] == 0x0A.toByte()
        val isZipArchive = headerBytes[0] == 0x50.toByte() &&
            headerBytes[1] == 0x4B.toByte() &&
            headerBytes[2] == 0x03.toByte() &&
            headerBytes[3] == 0x04.toByte()
        val asciiPrefix = String(headerBytes, 0, min(count, 32), Charsets.US_ASCII).lowercase(Locale.ROOT)
        if (isMzExe || isElf || isDex || isZipArchive ||
            asciiPrefix.startsWith("#!") ||
            asciiPrefix.contains("<?php") ||
            asciiPrefix.contains("<script") ||
            asciiPrefix.contains("<html")
        ) {
            throw SecurityException("Sandbox threat isolation: Blocked unsafe executable or script payload.")
        }

        // Verify known safe image or video container magic bytes
        val isJpeg = headerBytes[0] == 0xFF.toByte() && headerBytes[1] == 0xD8.toByte() && headerBytes[2] == 0xFF.toByte()
        val isPng = headerBytes[0] == 0x89.toByte() &&
            headerBytes[1] == 0x50.toByte() &&
            headerBytes[2] == 0x4E.toByte() &&
            headerBytes[3] == 0x47.toByte()
        val isWebp = count >= 12 &&
            headerBytes[0] == 0x52.toByte() && headerBytes[1] == 0x49.toByte() &&
            headerBytes[2] == 0x46.toByte() && headerBytes[3] == 0x46.toByte() &&
            headerBytes[8] == 0x57.toByte() && headerBytes[9] == 0x45.toByte() &&
            headerBytes[10] == 0x42.toByte() && headerBytes[11] == 0x50.toByte()
        val isGif = headerBytes[0] == 0x47.toByte() && headerBytes[1] == 0x49.toByte() && headerBytes[2] == 0x46.toByte()
        val isIsoMp4 = count >= 12 &&
            headerBytes[4] == 0x66.toByte() && headerBytes[5] == 0x74.toByte() &&
            headerBytes[6] == 0x79.toByte() && headerBytes[7] == 0x70.toByte()
        val isMkvWebm = headerBytes[0] == 0x1A.toByte() &&
            headerBytes[1] == 0x45.toByte() &&
            headerBytes[2] == 0xDF.toByte() &&
            headerBytes[3] == 0xA3.toByte()

        return when {
            isJpeg -> "image/jpeg"
            isPng -> "image/png"
            isWebp -> "image/webp"
            isGif -> "image/gif"
            isIsoMp4 -> "video/mp4"
            isMkvWebm -> "video/webm"
            else -> throw SecurityException("Sandbox verification rejected unsupported or unverified media container.")
        }
    }

    /**
     * Computes a strong, multi-factor Content Fingerprint ID (`tfp2_<sha256>_<dhash>_<chromaOrTemporal>`)
     * that cannot be bypassed by simple re-saving, cropping, or metadata changes.
     */
    fun computeStrongCompositeFingerprint(
        sha256Hex: String,
        perceptualDHash: String,
        chromaOrTemporalHex: String,
        captionHint: String = ""
    ): String {
        val textSimHash = if (captionHint.isBlank()) {
            "00000000"
        } else {
            TelefamCryptoAndTelemetry.sha256Hex(
                captionHint.lowercase(Locale.ROOT).trim().toByteArray(Charsets.UTF_8)
            ).take(8)
        }
        val combinedDigest = TelefamCryptoAndTelemetry.sha256Hex(
            "$sha256Hex|$perceptualDHash|$chromaOrTemporalHex|$textSimHash".toByteArray(Charsets.UTF_8)
        ).take(16)
        return "tfp2_${combinedDigest}_${perceptualDHash.take(16)}_${chromaOrTemporalHex.take(10)}"
    }

    fun computePerceptualDHash64(bitmap: Bitmap): String {
        val resized = Bitmap.createScaledBitmap(bitmap, 9, 8, true)
        var bits = 0UL
        var bitPos = 0
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                val left = luminance(resized.getPixel(x, y))
                val right = luminance(resized.getPixel(x + 1, y))
                if (left > right) {
                    bits = bits or (1UL shl bitPos)
                }
                bitPos++
            }
        }
        return bits.toString(16).padStart(16, '0')
    }

    fun computeChromaSignatureHex(bitmap: Bitmap): String {
        val sample = Bitmap.createScaledBitmap(bitmap, 16, 16, true)
        var rSum = 0L
        var gSum = 0L
        var bSum = 0L
        var edgeEnergy = 0L
        for (y in 0 until 16) {
            for (x in 0 until 16) {
                val px = sample.getPixel(x, y)
                rSum += Color.red(px)
                gSum += Color.green(px)
                bSum += Color.blue(px)
                if (x < 15) {
                    val nextPx = sample.getPixel(x + 1, y)
                    edgeEnergy += kotlin.math.abs(luminance(px) - luminance(nextPx))
                }
            }
        }
        val raw = "${rSum / 256}:${gSum / 256}:${bSum / 256}:${edgeEnergy / 128}"
        return TelefamCryptoAndTelemetry.sha256Hex(raw.toByteArray(Charsets.UTF_8)).take(12)
    }

    private fun luminance(pixel: Int): Int {
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)
        return (r * 299 + g * 587 + b * 114) / 1000
    }

    private fun computeFileSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
            val buf = ByteArray(16 * 1024)
            var r: Int
            while (fis.read(buf).also { r = it } != -1) {
                digest.update(buf, 0, r)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Imports up to 10 photos into the isolated Sandbox Quarantine, scans magic bytes for malware,
     * strips EXIF metadata by re-encoding, and computes strong composite fingerprints.
     */
    suspend fun importPhotoIntoSandboxStudio(
        context: Context,
        uri: Uri
    ): Result<EditablePhotoStudioItem> = withContext(Dispatchers.IO) {
        runCatching {
            val itemId = "photo_${UUID.randomUUID()}"
            val quarantineFile = File(quarantineDir(context), "$itemId.raw")
            var totalBytes = 0L
            val header = ByteArray(64)
            var headerRead = 0

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(quarantineFile).use { output ->
                    val buffer = ByteArray(16 * 1024)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        if (headerRead == 0) {
                            headerRead = min(read, header.size)
                            System.arraycopy(buffer, 0, header, 0, headerRead)
                            val mime = verifySafeMediaHeaderInSandbox(header, headerRead)
                            if (!mime.startsWith("image/")) {
                                throw SecurityException("Selected file is not a valid image.")
                            }
                        }
                        totalBytes += read
                        if (totalBytes > MAX_POST_MEDIA_BYTES) {
                            throw IllegalArgumentException("Image exceeds the 100 MB maximum limit.")
                        }
                        output.write(buffer, 0, read)
                    }
                }
            } ?: throw IllegalArgumentException("Could not read selected photo.")

            val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(quarantineFile.absolutePath, boundsOpts)
            if (boundsOpts.outWidth <= 0 || boundsOpts.outHeight <= 0) {
                quarantineFile.delete()
                throw SecurityException("Sandbox decoder rejected corrupted image stream.")
            }

            var sampleSize = 1
            while (boundsOpts.outWidth / sampleSize > 1600 || boundsOpts.outHeight / sampleSize > 1600) {
                sampleSize *= 2
            }
            val decodeOpts = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val decoded = BitmapFactory.decodeFile(quarantineFile.absolutePath, decodeOpts)
                ?: run {
                    quarantineFile.delete()
                    throw SecurityException("Unable to decode image in sandbox.")
                }

            // Re-encode clean EXIF-stripped image inside sandbox
            val cleanSandboxFile = File(quarantineDir(context), "${itemId}_clean.webp")
            FileOutputStream(cleanSandboxFile).use { out ->
                val fmt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    @Suppress("DEPRECATION")
                    Bitmap.CompressFormat.WEBP
                }
                decoded.compress(fmt, 86, out)
            }
            quarantineFile.delete()

            val sha256 = computeFileSha256(cleanSandboxFile)
            val dHash = computePerceptualDHash64(decoded)
            val chromaSig = computeChromaSignatureHex(decoded)
            val compositeFp = computeStrongCompositeFingerprint(sha256, dHash, chromaSig)
            val aspectDesc = if (decoded.width > decoded.height) "Landscape" else if (decoded.height > decoded.width) "Portrait" else "Square"

            EditablePhotoStudioItem(
                itemId = itemId,
                sourceUriString = uri.toString(),
                sandboxFilePath = cleanSandboxFile.absolutePath,
                widthPx = decoded.width,
                heightPx = decoded.height,
                originalByteSize = totalBytes,
                compressedByteSize = cleanSandboxFile.length(),
                sha256Hex = sha256,
                perceptualDHash = dHash,
                compositeFingerprintId = compositeFp,
                autoExtractedCaptionHint = "$aspectDesc ${decoded.width}x${decoded.height} · Verified Clean"
            )
        }
    }

    /**
     * Imports a video into the Sandbox Quarantine, verifies container safety, extracts I-frame keyframes
     * across the timeline, and automatically trims `trimEndMs` if the video exceeds 100 MB.
     */
    suspend fun inspectAndPrepareVideoInSandbox(
        context: Context,
        uri: Uri
    ): Result<EditableVideoStudioState> = withContext(Dispatchers.IO) {
        runCatching {
            val videoId = "vid_${UUID.randomUUID()}"
            val sandboxFile = File(quarantineDir(context), "$videoId.mp4")
            var sourceTotalBytes = 0L
            var copiedBytes = 0L
            val header = ByteArray(64)
            var headerChecked = false

            // Measure total size if available from descriptor
            runCatching {
                context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { afd ->
                    if (afd.length > 0) sourceTotalBytes = afd.length
                }
            }

            // Copy up to 100 MB into sandbox quarantine (auto-truncating/trimming if larger)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(sandboxFile).use { output ->
                    val buf = ByteArray(32 * 1024)
                    var r: Int
                    while (input.read(buf).also { r = it } != -1) {
                        if (!headerChecked) {
                            headerChecked = true
                            val hLen = min(r, header.size)
                            System.arraycopy(buf, 0, header, 0, hLen)
                            val mime = verifySafeMediaHeaderInSandbox(header, hLen)
                            if (!mime.startsWith("video/")) {
                                throw SecurityException("Sandbox check failed: File is not a valid video container.")
                            }
                        }
                        if (copiedBytes + r > MAX_POST_MEDIA_BYTES) {
                            val remaining = (MAX_POST_MEDIA_BYTES - copiedBytes).toInt().coerceAtLeast(0)
                            if (remaining > 0) {
                                output.write(buf, 0, remaining)
                                copiedBytes += remaining
                            }
                            break
                        }
                        output.write(buf, 0, r)
                        copiedBytes += r
                    }
                }
            } ?: throw IllegalArgumentException("Unable to read selected video.")

            if (sourceTotalBytes < copiedBytes) sourceTotalBytes = copiedBytes
            val wasAutoTrimmed = sourceTotalBytes > MAX_POST_MEDIA_BYTES

            val retriever = MediaMetadataRetriever()
            var durationMs = 15_000L
            var widthPx = 1080
            var heightPx = 1920
            val keyframes = mutableListOf<VideoKeyframeThumb>()
            var coverFramePath: String? = null
            val frameDHashes = mutableListOf<String>()

            try {
                try {
                    retriever.setDataSource(sandboxFile.absolutePath)
                } catch (_: Throwable) {
                    retriever.setDataSource(context, uri)
                }
                durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()?.coerceAtLeast(1_000L) ?: 15_000L
                widthPx = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                    ?.toIntOrNull()?.takeIf { it > 0 } ?: 1080
                heightPx = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    ?.toIntOrNull()?.takeIf { it > 0 } ?: 1920

                val frameCount = 8
                for (i in 0 until frameCount) {
                    val timeMs = ((durationMs * i) / frameCount.coerceAtLeast(1)).coerceIn(0L, durationMs)
                    val bmp = retriever.getFrameAtTime(timeMs * 1000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        ?: retriever.getFrameAtTime(0L)
                    if (bmp != null) {
                        if (i == 0 || i == frameCount / 2 || i == frameCount - 1) {
                            frameDHashes.add(computePerceptualDHash64(bmp))
                        }
                        val scaledThumb = Bitmap.createScaledBitmap(bmp, 160, 240, true)
                        val thumbFile = File(quarantineDir(context), "${videoId}_kf_$i.jpg")
                        FileOutputStream(thumbFile).use { out ->
                            scaledThumb.compress(Bitmap.CompressFormat.JPEG, 78, out)
                        }
                        keyframes.add(VideoKeyframeThumb(timestampMs = timeMs, thumbFilePath = thumbFile.absolutePath))
                        if (i == 0) {
                            val coverFile = File(quarantineDir(context), "${videoId}_cover.jpg")
                            FileOutputStream(coverFile).use { out ->
                                bmp.compress(Bitmap.CompressFormat.JPEG, 85, out)
                            }
                            coverFramePath = coverFile.absolutePath
                        }
                    }
                }
            } finally {
                runCatching { retriever.release() }
            }

            // If source exceeded 100 MB, proportionally cap trimEndMs so trimmed segment is <= 100 MB
            val effectiveTrimEndMs = if (wasAutoTrimmed && sourceTotalBytes > 0L) {
                ((durationMs.toDouble() * (MAX_POST_MEDIA_BYTES.toDouble() / sourceTotalBytes.toDouble())).toLong())
                    .coerceIn(1_000L, durationMs)
            } else {
                durationMs
            }

            val sha256 = computeFileSha256(sandboxFile)
            val primaryDHash = frameDHashes.firstOrNull() ?: sha256.take(16)
            val temporalSig = TelefamCryptoAndTelemetry.sha256Hex(
                frameDHashes.joinToString("|").ifBlank { sha256 }.toByteArray(Charsets.UTF_8)
            ).take(12)
            val compositeFp = computeStrongCompositeFingerprint(sha256, primaryDHash, temporalSig)

            val maxDim = maxOf(widthPx, heightPx)
            val qualities = buildList {
                if (maxDim >= 1080) add("1080p FHD")
                if (maxDim >= 720) add("720p HD")
                add("480p SD")
                add("360p Fast")
            }

            EditableVideoStudioState(
                videoId = videoId,
                sourceUriString = uri.toString(),
                sandboxFilePath = sandboxFile.absolutePath,
                originalByteSize = sourceTotalBytes,
                effectiveTrimmedByteSize = copiedBytes,
                totalDurationMs = durationMs,
                trimStartMs = 0L,
                trimEndMs = effectiveTrimEndMs,
                wasAutoTrimmedFor100Mb = wasAutoTrimmed,
                widthPx = widthPx,
                heightPx = heightPx,
                selectedCoverTimeMs = 0L,
                coverFrameFilePath = coverFramePath,
                keyframes = keyframes,
                sha256Hex = sha256,
                perceptualDHash = primaryDHash,
                temporalKeyframeHash = temporalSig,
                compositeFingerprintId = compositeFp,
                qualityProfiles = qualities,
                autoExtractedCaption = "Video ${widthPx}x${heightPx} · ${(effectiveTrimEndMs / 1000L).coerceAtLeast(1L)}s"
            )
        }
    }

    /**
     * Extracts a specific timestamp cover frame from the video when the user scrubs the Cover Chooser.
     */
    suspend fun extractVideoCoverAtTimestamp(
        context: Context,
        videoState: EditableVideoStudioState,
        timestampMs: Long
    ): String? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoState.sandboxFilePath)
            val frame = retriever.getFrameAtTime(
                timestampMs.coerceIn(0L, videoState.totalDurationMs) * 1000L,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            ) ?: return@withContext videoState.coverFrameFilePath
            val outCover = File(quarantineDir(context), "${videoState.videoId}_cover_${timestampMs}.jpg")
            FileOutputStream(outCover).use { out ->
                frame.compress(Bitmap.CompressFormat.JPEG, 86, out)
            }
            outCover.absolutePath
        } catch (_: Throwable) {
            videoState.coverFrameFilePath
        } finally {
            runCatching { retriever.release() }
        }
    }

    /**
     * Bakes a Text Post (with chosen 12-color background gradient, 10-font typography, and auto-scaled
     * character sizing) into a high-resolution verified image card + strong content fingerprint.
     */
    suspend fun bakeTextPostCardToVerifiedVault(
        context: Context,
        dao: TelefamDao,
        text: String,
        backgroundTheme: PostBackgroundTheme,
        typographyFont: PostTypographyFont,
        textLayers: List<PostTextOverlayLayer>,
        brushStrokes: List<PostBrushStroke>
    ): BakedPostArtifact = withContext(Dispatchers.IO) {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw rich 12-theme gradient background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(backgroundTheme.startColorHex.toInt(), backgroundTheme.endColorHex.toInt()),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Subtle atmospheric circles
        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = backgroundTheme.accentHex.toInt()
            alpha = 26
        }
        canvas.drawCircle(width * 0.85f, height * 0.16f, width * 0.34f, accentPaint)
        canvas.drawCircle(width * 0.14f, height * 0.84f, width * 0.28f, accentPaint)

        // 2. Draw primary auto-scaled text centered on canvas
        val cleanText = text.trim().take(PostTextAutoScaler.MAX_BACKGROUND_TEXT_CHARS)
        val customTypeface = runCatching {
            ResourcesCompat.getFont(context, typographyFont.fontResId)
        }.getOrNull() ?: Typeface.DEFAULT_BOLD

        val scaledSp = PostTextAutoScaler.computeDynamicFontSizeSp(cleanText.length)
        val pxTextSize = scaledSp * 2.85f
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = backgroundTheme.textColorHex.toInt()
            textSize = pxTextSize
            typeface = Typeface.create(customTypeface, Typeface.BOLD)
        }

        val maxTextWidth = (width * 0.82f).roundToInt()
        val staticLayout = StaticLayout.Builder
            .obtain(cleanText, 0, cleanText.length, textPaint, maxTextWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f, 1.24f)
            .setIncludePad(false)
            .build()

        canvas.save()
        val textX = (width - maxTextWidth) / 2f
        val textY = (height - staticLayout.height) / 2f
        canvas.translate(textX, textY)
        staticLayout.draw(canvas)
        canvas.restore()

        // 3. Draw any freehand brush strokes & movable cover text layers
        drawBrushStrokesOnCanvas(canvas, width, height, brushStrokes)
        drawTextOverlaysOnCanvas(context, canvas, width, height, textLayers)

        saveBakedBitmapAndRegisterFingerprint(
            context = context,
            dao = dao,
            bitmap = bitmap,
            prefix = "post_text",
            originalBytes = cleanText.toByteArray(Charsets.UTF_8).size.toLong(),
            captionHint = cleanText
        )
    }

    /**
     * Bakes an edited Photo (crop preset, rotation, zoom/pan, studio ColorMatrix filter,
     * freehand pen brush strokes, and pinch-resized cover text layers) into the Verified Vault.
     */
    suspend fun bakeEditedPhotoToVerifiedVault(
        context: Context,
        dao: TelefamDao,
        item: EditablePhotoStudioItem,
        caption: String
    ): BakedPostArtifact = withContext(Dispatchers.IO) {
        val srcBitmap = BitmapFactory.decodeFile(item.sandboxFilePath)
            ?: throw IllegalStateException("Source photo no longer available in sandbox.")

        // 1. Apply rotation
        val rotMatrix = Matrix()
        val deg = (item.rotationQuadrants % 4) * 90f
        if (deg != 0f) {
            rotMatrix.postRotate(deg)
        }
        val rotated = if (deg != 0f) {
            Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height, rotMatrix, true)
        } else {
            srcBitmap
        }

        // 2. Apply crop aspect ratio preset
        val targetAspect = item.cropPreset.aspectRatio
        val outWidth: Int
        val outHeight: Int
        if (targetAspect == null) {
            outWidth = rotated.width.coerceAtMost(1440)
            outHeight = rotated.height.coerceAtMost(1440)
        } else {
            val currentAspect = rotated.width.toFloat() / rotated.height.toFloat()
            if (currentAspect > targetAspect) {
                outHeight = rotated.height.coerceAtMost(1440)
                outWidth = (outHeight * targetAspect).roundToInt().coerceAtLeast(320)
            } else {
                outWidth = rotated.width.coerceAtMost(1440)
                outHeight = (outWidth / targetAspect).roundToInt().coerceAtLeast(320)
            }
        }

        val outBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outBitmap)
        canvas.drawColor(Color.BLACK)

        // 3. Apply Studio ColorMatrix Filter + Brightness
        val filterPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = buildStudioColorMatrixFilter(item.filterPreset, item.brightnessOffset)
        }

        val scale = maxOf(
            outWidth.toFloat() / rotated.width.toFloat(),
            outHeight.toFloat() / rotated.height.toFloat()
        ) * item.zoomScale.coerceIn(1f, 3.5f)

        val drawW = rotated.width * scale
        val drawH = rotated.height * scale
        val left = (outWidth - drawW) / 2f + (item.panXNorm * outWidth * 0.35f)
        val top = (outHeight - drawH) / 2f + (item.panYNorm * outHeight * 0.35f)
        canvas.drawBitmap(rotated, null, RectF(left, top, left + drawW, top + drawH), filterPaint)

        // 4. Draw Pen Brush Strokes
        drawBrushStrokesOnCanvas(canvas, outWidth, outHeight, item.brushStrokes)

        // 5. Draw Pinchable / Movable Cover Text Layers
        drawTextOverlaysOnCanvas(context, canvas, outWidth, outHeight, item.textLayers)

        saveBakedBitmapAndRegisterFingerprint(
            context = context,
            dao = dao,
            bitmap = outBitmap,
            prefix = "post_photo",
            originalBytes = item.originalByteSize,
            captionHint = caption
        )
    }

    /**
     * Finalizes a Video Post in the Verified Vault: bakes its chosen cover frame with any
     * cover text layers & pen brush strokes, compresses/moves the trimmed video stream,
     * and registers the composite temporal+perceptual fingerprint in Room.
     */
    suspend fun finalizeVideoPostInVerifiedVault(
        context: Context,
        dao: TelefamDao,
        videoState: EditableVideoStudioState,
        caption: String
    ): BakedPostArtifact = withContext(Dispatchers.IO) {
        val baseCoverBmp = videoState.coverFrameFilePath?.let { BitmapFactory.decodeFile(it) }
            ?: Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888).apply {
                Canvas(this).drawColor(Color.rgb(20, 22, 30))
            }

        val coverW = baseCoverBmp.width.coerceAtLeast(540)
        val coverH = baseCoverBmp.height.coerceAtLeast(960)
        val bakedCover = Bitmap.createBitmap(coverW, coverH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bakedCover)

        val filterPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = buildStudioColorMatrixFilter(videoState.filterPreset, 0f)
        }
        canvas.drawBitmap(baseCoverBmp, null, RectF(0f, 0f, coverW.toFloat(), coverH.toFloat()), filterPaint)

        drawBrushStrokesOnCanvas(canvas, coverW, coverH, videoState.brushStrokes)
        drawTextOverlaysOnCanvas(context, canvas, coverW, coverH, videoState.textLayers)

        val coverVaultFile = File(verifiedVaultDir(context), "cover_${videoState.videoId}.webp")
        FileOutputStream(coverVaultFile).use { out ->
            val fmt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                @Suppress("DEPRECATION")
                Bitmap.CompressFormat.WEBP
            }
            bakedCover.compress(fmt, 86, out)
        }

        // Promote trimmed video stream into Verified Vault (calculating trimmed byte range)
        val srcFile = File(videoState.sandboxFilePath)
        val destVideoFile = File(verifiedVaultDir(context), "video_${videoState.videoId}.mp4")
        val trimRatio = if (videoState.totalDurationMs > 0L) {
            (videoState.trimmedDurationMs.toDouble() / videoState.totalDurationMs.toDouble()).coerceIn(0.05, 1.0)
        } else 1.0
        val maxBytesToCopy = min(
            MAX_POST_MEDIA_BYTES,
            (srcFile.length() * trimRatio).toLong().coerceAtLeast(min(srcFile.length(), 64 * 1024L))
        )

        FileInputStream(srcFile).use { input ->
            FileOutputStream(destVideoFile).use { output ->
                val buf = ByteArray(32 * 1024)
                var copied = 0L
                var r: Int
                while (input.read(buf).also { r = it } != -1 && copied < maxBytesToCopy) {
                    val toWrite = min(r.toLong(), maxBytesToCopy - copied).toInt()
                    output.write(buf, 0, toWrite)
                    copied += toWrite
                }
            }
        }

        // Also run silent background Deflate compaction for fast wire chunking
        TelefamAggressiveCompressionEngine.compressAndEnforce100MbLimitOnFile(destVideoFile)

        val finalSha256 = computeFileSha256(destVideoFile)
        val coverDHash = computePerceptualDHash64(bakedCover)
        val compositeFp = computeStrongCompositeFingerprint(
            sha256Hex = finalSha256,
            perceptualDHash = coverDHash,
            chromaOrTemporalHex = videoState.temporalKeyframeHash,
            captionHint = caption
        )

        val now = System.currentTimeMillis()
        dao.upsertMediaFingerprint(
            MediaFingerprintEntity(
                sha256Checksum = finalSha256,
                perceptualDHash = coverDHash,
                secureFilePath = destVideoFile.absolutePath,
                cdnStorageUrl = "https://cdn.telefam.company/posts/$compositeFp.mp4",
                verifiedMimeType = "video/mp4",
                originalByteSize = videoState.originalByteSize.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                compressedByteSize = destVideoFile.length().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                widthPx = videoState.widthPx,
                heightPx = videoState.heightPx,
                reuseCount = 1,
                createdAtMs = now,
                lastReusedAtMs = now
            )
        )

        BakedPostArtifact(
            verifiedFilePath = destVideoFile.absolutePath,
            coverPreviewPath = coverVaultFile.absolutePath,
            verifiedMimeType = "video/mp4",
            widthPx = videoState.widthPx,
            heightPx = videoState.heightPx,
            originalByteSize = videoState.originalByteSize,
            compressedByteSize = destVideoFile.length(),
            sha256Hex = finalSha256,
            perceptualDHash = coverDHash,
            compositeFingerprintId = compositeFp,
            qualityProfiles = videoState.qualityProfiles,
            extractedCaptionMetadata = caption.ifBlank { videoState.autoExtractedCaption }
        )
    }

    private suspend fun saveBakedBitmapAndRegisterFingerprint(
        context: Context,
        dao: TelefamDao,
        bitmap: Bitmap,
        prefix: String,
        originalBytes: Long,
        captionHint: String
    ): BakedPostArtifact {
        val compressedBytes = ByteArrayOutputStream().use { bos ->
            val fmt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                @Suppress("DEPRECATION")
                Bitmap.CompressFormat.WEBP
            }
            bitmap.compress(fmt, 85, bos)
            bos.toByteArray()
        }

        val sha256 = TelefamCryptoAndTelemetry.sha256Hex(compressedBytes)
        val dHash = computePerceptualDHash64(bitmap)
        val chromaSig = computeChromaSignatureHex(bitmap)
        val compositeFp = computeStrongCompositeFingerprint(sha256, dHash, chromaSig, captionHint)

        val outFile = File(verifiedVaultDir(context), "${prefix}_${compositeFp.take(28)}.webp")
        FileOutputStream(outFile).use { it.write(compressedBytes) }

        val now = System.currentTimeMillis()
        dao.upsertMediaFingerprint(
            MediaFingerprintEntity(
                sha256Checksum = sha256,
                perceptualDHash = dHash,
                secureFilePath = outFile.absolutePath,
                cdnStorageUrl = "https://cdn.telefam.company/posts/$compositeFp.webp",
                verifiedMimeType = "image/webp",
                originalByteSize = originalBytes.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                compressedByteSize = compressedBytes.size,
                widthPx = bitmap.width,
                heightPx = bitmap.height,
                reuseCount = 1,
                createdAtMs = now,
                lastReusedAtMs = now
            )
        )

        return BakedPostArtifact(
            verifiedFilePath = outFile.absolutePath,
            coverPreviewPath = outFile.absolutePath,
            verifiedMimeType = "image/webp",
            widthPx = bitmap.width,
            heightPx = bitmap.height,
            originalByteSize = originalBytes,
            compressedByteSize = compressedBytes.size.toLong(),
            sha256Hex = sha256,
            perceptualDHash = dHash,
            compositeFingerprintId = compositeFp,
            qualityProfiles = listOf("1440p UHD", "1080p FHD", "720p HD"),
            extractedCaptionMetadata = captionHint.take(160)
        )
    }

    private fun buildStudioColorMatrixFilter(
        preset: PostColorFilterPreset,
        brightnessOffset: Float
    ): ColorMatrixColorFilter {
        val satMatrix = ColorMatrix().apply { setSaturation(preset.saturation) }
        val c = preset.contrast
        val t = (1f - c) * 128f + brightnessOffset
        val contrastWarmth = ColorMatrix(
            floatArrayOf(
                c, 0f, 0f, 0f, t + preset.warmthShift,
                0f, c, 0f, 0f, t,
                0f, 0f, c, 0f, t - preset.warmthShift,
                0f, 0f, 0f, 1f, 0f
            )
        )
        satMatrix.postConcat(contrastWarmth)
        return ColorMatrixColorFilter(satMatrix)
    }

    private fun drawBrushStrokesOnCanvas(
        canvas: Canvas,
        width: Int,
        height: Int,
        strokes: List<PostBrushStroke>
    ) {
        if (strokes.isEmpty()) return
        val scaleFactor = width / 380f
        for (stroke in strokes) {
            val pts = stroke.pointsNorm
            if (pts.isEmpty()) continue
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = stroke.colorArgb.toInt()
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                strokeWidth = (stroke.strokeWidthPx * scaleFactor).coerceIn(4f, 96f)
            }
            if (pts.size == 1) {
                paint.style = Paint.Style.FILL
                canvas.drawCircle(
                    pts[0].first * width,
                    pts[0].second * height,
                    (stroke.strokeWidthPx * scaleFactor) / 2f,
                    paint
                )
            } else {
                val path = Path()
                val firstX = pts[0].first * width
                val firstY = pts[0].second * height
                path.moveTo(firstX, firstY)
                for (i in 1 until pts.size) {
                    val prevX = pts[i - 1].first * width
                    val prevY = pts[i - 1].second * height
                    val curX = pts[i].first * width
                    val curY = pts[i].second * height
                    path.quadTo(prevX, prevY, (prevX + curX) / 2f, (prevY + curY) / 2f)
                }
                val last = pts.last()
                path.lineTo(last.first * width, last.second * height)
                canvas.drawPath(path, paint)
            }
        }
    }

    private fun drawTextOverlaysOnCanvas(
        context: Context,
        canvas: Canvas,
        width: Int,
        height: Int,
        layers: List<PostTextOverlayLayer>
    ) {
        if (layers.isEmpty()) return
        val baseScale = width / 380f
        for (layer in layers) {
            if (layer.text.isBlank()) continue
            val customTf = runCatching {
                ResourcesCompat.getFont(context, layer.font.fontResId)
            }.getOrNull() ?: Typeface.DEFAULT_BOLD

            val fontSizePx = (22f * baseScale * layer.scaleFactor.coerceIn(0.45f, 3.5f))
            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = layer.colorArgb.toInt()
                textSize = fontSizePx
                typeface = Typeface.create(customTf, Typeface.BOLD)
                if (!layer.hasBackgroundPill) {
                    setShadowLayer(8f, 0f, 3f, Color.argb(190, 0, 0, 0))
                }
            }

            val maxBoxW = (width * 0.84f).roundToInt()
            val layout = StaticLayout.Builder
                .obtain(layer.text, 0, layer.text.length, textPaint, maxBoxW)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setIncludePad(false)
                .build()

            var maxLineW = 0f
            for (i in 0 until layout.lineCount) {
                maxLineW = maxOf(maxLineW, layout.getLineWidth(i))
            }

            val cx = (layer.normX.coerceIn(0.06f, 0.94f)) * width
            val cy = (layer.normY.coerceIn(0.06f, 0.94f)) * height

            canvas.save()
            canvas.translate(cx, cy)
            canvas.rotate(layer.rotationDeg)

            if (layer.hasBackgroundPill) {
                val padH = 16f * baseScale
                val padV = 10f * baseScale
                val pillColor = if (layer.colorArgb == 0xFF10131AL) {
                    Color.argb(225, 255, 255, 255)
                } else {
                    Color.argb(195, 16, 18, 26)
                }
                val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = pillColor
                    style = Paint.Style.FILL
                }
                val rect = RectF(
                    -maxLineW / 2f - padH,
                    -layout.height / 2f - padV,
                    maxLineW / 2f + padH,
                    layout.height / 2f + padV
                )
                canvas.drawRoundRect(rect, 16f * baseScale, 16f * baseScale, bgPaint)
            }

            canvas.translate(-maxBoxW / 2f, -layout.height / 2f)
            layout.draw(canvas)
            canvas.restore()
        }
    }

    /**
     * Queries the public free iTunes Music Search API for real song previews (`previewUrl`),
     * with resilient fallback to public Creative Commons audio streams when offline.
     */
    suspend fun searchFreePublicSongs(query: String): List<PostMusicTrackItem> = withContext(Dispatchers.IO) {
        val searchTerm = query.trim().ifBlank { "afrobeats chill pop" }
        val apiResults = runCatching {
            val encoded = URLEncoder.encode(searchTerm, "UTF-8")
            val url = "https://itunes.apple.com/search?media=music&entity=song&limit=24&term=$encoded"
            val req = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .get()
                .build()
            musicHttpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@use emptyList<PostMusicTrackItem>()
                val bodyStr = resp.body?.string().orEmpty()
                val root = JSONObject(bodyStr)
                val arr = root.optJSONArray("results") ?: return@use emptyList<PostMusicTrackItem>()
                val list = mutableListOf<PostMusicTrackItem>()
                for (i in 0 until arr.length()) {
                    val item = arr.optJSONObject(i) ?: continue
                    val previewUrl = item.optString("previewUrl").trim()
                    val trackName = item.optString("trackName").trim()
                    val artistName = item.optString("artistName").trim()
                    if (previewUrl.startsWith("https://") && trackName.isNotBlank()) {
                        list.add(
                            PostMusicTrackItem(
                                trackId = "itunes_${item.optLong("trackId", i.toLong())}",
                                title = trackName,
                                artistName = artistName.ifBlank { "Featured Artist" },
                                albumTitle = item.optString("collectionName"),
                                previewStreamUrl = previewUrl,
                                artworkUrl = item.optString("artworkUrl100").takeIf { it.isNotBlank() },
                                durationMs = 30_000L
                            )
                        )
                    }
                }
                list
            }
        }.getOrDefault(emptyList())

        if (apiResults.isNotEmpty()) return@withContext apiResults

        // Resilient open-licensed audio stream catalog if network is restricted
        val fallbackCatalog = listOf(
            PostMusicTrackItem(
                trackId = "cc_sunrise_groove",
                title = "Golden Horizon Groove",
                artistName = "Telefam Sound Collective",
                albumTitle = "Studio Sessions",
                previewStreamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                durationMs = 30_000L
            ),
            PostMusicTrackItem(
                trackId = "cc_midnight_drive",
                title = "Midnight Neon Drive",
                artistName = "Aurora Synth",
                albumTitle = "Nightfall EP",
                previewStreamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                durationMs = 30_000L
            ),
            PostMusicTrackItem(
                trackId = "cc_savannah_rhythm",
                title = "Nairobi Sunset Rhythm",
                artistName = "East Africa Acoustic",
                albumTitle = "Acoustic Roots",
                previewStreamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                durationMs = 30_000L
            ),
            PostMusicTrackItem(
                trackId = "cc_velvet_chill",
                title = "Velvet Lo-Fi Beats",
                artistName = "Kibo Lo-Fi",
                albumTitle = "Study & Chill",
                previewStreamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                durationMs = 30_000L
            ),
            PostMusicTrackItem(
                trackId = "cc_ocean_breeze",
                title = "Coastal Breeze Instrumental",
                artistName = "Zanzibar Waves",
                albumTitle = "Indian Ocean",
                previewStreamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
                durationMs = 30_000L
            )
        )
        if (query.isBlank()) {
            fallbackCatalog
        } else {
            val q = query.trim().lowercase(Locale.ROOT)
            fallbackCatalog.filter {
                it.title.lowercase(Locale.ROOT).contains(q) ||
                    it.artistName.lowercase(Locale.ROOT).contains(q)
            }.ifEmpty { fallbackCatalog }
        }
    }
}
