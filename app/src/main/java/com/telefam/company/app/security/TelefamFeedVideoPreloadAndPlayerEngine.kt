package com.telefam.company.app.security

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.telefam.company.app.domain.FeedVideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Production Video Preloading, LRU Disk Chunk Cache, and Silent Bandwidth-Adaptive
 * Quality Selection Engine (360p / 720p / 1080p) for the TikTok-style Feeds Pager.
 *
 * - Preloads the next 5 upcoming videos in 256 KB chunks into `telefam_feed_video_cache`.
 * - Silently selects 360p, 720p, or 1080p based on real-time link bandwidth and chunk transfer speed
 *   without ever showing a quality UI.
 * - Retries transient network errors with exponential backoff (800ms -> 1.6s -> 3.2s -> 6.4s).
 */
object TelefamFeedVideoPreloadAndPlayerEngine {

    private const val PRELOAD_CHUNK_BYTES = 256 * 1024 // 256 KB fast-start chunk
    private const val MAX_PRELOAD_UPCOMING_COUNT = 5
    private const val MAX_DISK_CACHE_BYTES = 180L * 1024L * 1024L // 180 MB LRU cache ceiling

    private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val activePreloadJobs = ConcurrentHashMap<String, Job>()
    private val preloadedLocalFileMap = ConcurrentHashMap<String, String>()

    @Volatile
    private var measuredThroughputKbps: Int = 6500

    private val preloadHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun getCacheDir(context: Context): File {
        return File(context.applicationContext.filesDir, "telefam_feed_video_cache").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Silently resolves the optimal quality URL (`360p`, `720p`, or `1080p`) based on current
     * network capabilities and measured chunk throughput. No UI is ever shown for quality changes.
     */
    fun resolveSilentAdaptiveStreamUrl(context: Context, item: FeedVideoItem): String {
        // Check if already cached locally on disk
        val cachedPath = preloadedLocalFileMap[item.videoId]
        if (!cachedPath.isNullOrBlank() && File(cachedPath).exists()) {
            return cachedPath
        }
        val cleanPrimary = item.primaryVideoUrl.substringBefore("?q=")
        if (cleanPrimary.startsWith("/") && File(cleanPrimary).exists()) {
            return cleanPrimary
        }

        val effectiveKbps = estimateDownstreamBandwidthKbps(context)
        val rawSelected = when {
            effectiveKbps >= 8500 -> item.qualityUrls.url1080p
            effectiveKbps >= 2600 -> item.qualityUrls.url720p
            else -> item.qualityUrls.url360p
        }.ifBlank { item.primaryVideoUrl }

        val localCandidate = rawSelected.substringBefore("?q=")
        return if (localCandidate.startsWith("/") && File(localCandidate).exists()) {
            localCandidate
        } else {
            rawSelected
        }
    }

    private fun estimateDownstreamBandwidthKbps(context: Context): Int {
        return runCatching {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return measuredThroughputKbps
            val net = cm.activeNetwork ?: return 1200
            val caps = cm.getNetworkCapabilities(net) ?: return measuredThroughputKbps
            val systemKbps = caps.linkDownstreamBandwidthKbps
            val isWifiOrEthernet = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            when {
                isWifiOrEthernet && measuredThroughputKbps > 3000 -> maxOf(measuredThroughputKbps, 9500)
                systemKbps in 500..100_000 -> (systemKbps + measuredThroughputKbps) / 2
                else -> measuredThroughputKbps
            }
        }.getOrDefault(measuredThroughputKbps)
    }

    /**
     * Preloads the next 5 videos in chunks in the background so swiping to the next video starts instantaneously.
     */
    fun preloadNextFiveVideosInChunks(
        context: Context,
        allFeedItems: List<FeedVideoItem>,
        currentIndex: Int
    ) {
        if (allFeedItems.isEmpty()) return
        val startIdx = (currentIndex + 1).coerceAtLeast(0)
        val upcoming = allFeedItems.drop(startIdx).take(MAX_PRELOAD_UPCOMING_COUNT)

        for (item in upcoming) {
            if (activePreloadJobs.containsKey(item.videoId)) continue
            val job = engineScope.launch {
                try {
                    preloadSingleVideoChunksWithBackoff(context, item)
                } finally {
                    activePreloadJobs.remove(item.videoId)
                }
            }
            activePreloadJobs[item.videoId] = job
        }

        // Also trigger LRU cache trim in background
        engineScope.launch {
            trimCacheDirToQuota(getCacheDir(context))
        }
    }

    private suspend fun preloadSingleVideoChunksWithBackoff(
        context: Context,
        item: FeedVideoItem
    ) = withContext(Dispatchers.IO) {
        val rawUrl = resolveSilentAdaptiveStreamUrl(context, item)
        val cleanLocalPath = rawUrl.substringBefore("?q=")

        // If local sandbox verified file, warm up first 256 KB into OS page cache and register
        if (cleanLocalPath.startsWith("/")) {
            val localFile = File(cleanLocalPath)
            if (localFile.exists() && localFile.length() > 0L) {
                runCatching {
                    RandomAccessFile(localFile, "r").use { raf ->
                        val buf = ByteArray(minOf(PRELOAD_CHUNK_BYTES.toLong(), localFile.length()).toInt())
                        raf.readFully(buf)
                    }
                    preloadedLocalFileMap[item.videoId] = localFile.absolutePath
                }
            }
            return@withContext
        }

        if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            return@withContext
        }

        val cacheDir = getCacheDir(context)
        val safeKey = TelefamCryptoAndTelemetry.sha256Hex(item.videoId.toByteArray(Charsets.UTF_8)).take(20)
        val targetCacheFile = File(cacheDir, "preload_${safeKey}.mp4")
        if (targetCacheFile.exists() && targetCacheFile.length() >= PRELOAD_CHUNK_BYTES) {
            preloadedLocalFileMap[item.videoId] = targetCacheFile.absolutePath
            return@withContext
        }

        // Retry with exponential backoff on transient network errors
        var attempt = 0
        var backoffMs = 800L
        while (attempt < 3) {
            val success = runCatching {
                val startNs = System.nanoTime()
                val req = Request.Builder()
                    .url(rawUrl)
                    .header("Range", "bytes=0-${PRELOAD_CHUNK_BYTES * 2 - 1}")
                    .build()
                preloadHttpClient.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful && resp.code != 206) return@use false
                    val body = resp.body ?: return@use false
                    var totalBytes = 0L
                    FileOutputStream(targetCacheFile).use { fos ->
                        body.byteStream().use { input ->
                            val buffer = ByteArray(32 * 1024)
                            var read: Int
                            while (input.read(buffer).also { read = it } != -1) {
                                fos.write(buffer, 0, read)
                                totalBytes += read
                                if (totalBytes >= PRELOAD_CHUNK_BYTES * 2) break
                            }
                            fos.flush()
                        }
                    }
                    val elapsedMs = ((System.nanoTime() - startNs) / 1_000_000L).coerceAtLeast(1L)
                    if (totalBytes > 16_384L) {
                        val kbps = ((totalBytes * 8L) / elapsedMs).toInt().coerceIn(500, 50_000)
                        measuredThroughputKbps = (measuredThroughputKbps + kbps) / 2
                        preloadedLocalFileMap[item.videoId] = targetCacheFile.absolutePath
                        true
                    } else {
                        false
                    }
                }
            }.getOrDefault(false)

            if (success) break
            attempt++
            if (attempt < 3) {
                delay(backoffMs)
                backoffMs *= 2L
            }
        }
    }

    private fun trimCacheDirToQuota(cacheDir: File) {
        runCatching {
            val files = cacheDir.listFiles()?.toList().orEmpty()
            var total = files.sumOf { it.length() }
            if (total <= MAX_DISK_CACHE_BYTES) return
            val sortedOldest = files.sortedBy { it.lastModified() }
            for (f in sortedOldest) {
                if (total <= MAX_DISK_CACHE_BYTES * 8 / 10) break
                val len = f.length()
                if (f.delete()) {
                    total -= len
                }
            }
        }
    }

    fun cancelAllPreloads() {
        activePreloadJobs.values.forEach { it.cancel() }
        activePreloadJobs.clear()
    }
}
