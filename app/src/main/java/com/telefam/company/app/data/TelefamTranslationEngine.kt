package com.telefam.company.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Production-ready internal translation engine for user comments and messages.
 * Queries real HTTP translation endpoints with automatic in-memory caching.
 * No mock canned strings, no "Traducido" prefixes, and no examples.
 */
object TelefamTranslationEngine {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .build()
    }

    private val cache = ConcurrentHashMap<String, String>()

    fun getCached(cacheKey: String): String? = cache[cacheKey]

    /**
     * Translates arbitrary text into the target language.
     * Returns genuine translated text or original if translation cannot be performed.
     */
    suspend fun translateText(text: String, targetLanguageCode: String): String = withContext(Dispatchers.IO) {
        val clean = text.trim()
        if (clean.isBlank()) return@withContext text

        val lang = targetLanguageCode.take(2).lowercase()
        val cacheKey = "${lang}_$clean"
        cache[cacheKey]?.let { return@withContext it }

        // Primary: Google GTX public translate endpoint
        val gtxResult = fetchGoogleGtxTranslation(clean, lang)
        if (!gtxResult.isNullOrBlank() && !gtxResult.equals(clean, ignoreCase = true)) {
            cache[cacheKey] = gtxResult
            return@withContext gtxResult
        }

        // Secondary: MyMemory Translation API
        val myMemoryResult = fetchMyMemoryTranslation(clean, lang)
        if (!myMemoryResult.isNullOrBlank() && !myMemoryResult.equals(clean, ignoreCase = true)) {
            cache[cacheKey] = myMemoryResult
            return@withContext myMemoryResult
        }

        // If both network routes fail (e.g. offline), return clean original text
        clean
    }

    private fun fetchGoogleGtxTranslation(text: String, targetLang: String): String? {
        return try {
            val encoded = URLEncoder.encode(text, "UTF-8")
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=$targetLang&dt=t&q=$encoded"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return null
            val body = response.body?.string().orEmpty()
            if (body.isBlank()) return null

            val rootArray = JSONArray(body)
            val sentences = rootArray.optJSONArray(0) ?: return null
            val sb = StringBuilder()
            for (i in 0 until sentences.length()) {
                val sentence = sentences.optJSONArray(i)
                val chunk = sentence?.optString(0).orEmpty()
                if (chunk.isNotBlank()) {
                    sb.append(chunk)
                }
            }
            sb.toString().trim().takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            null
        }
    }

    private fun fetchMyMemoryTranslation(text: String, targetLang: String): String? {
        return try {
            val encoded = URLEncoder.encode(text, "UTF-8")
            val url = "https://api.mymemory.translated.net/get?q=$encoded&langpair=autodetect|$targetLang"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14)")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return null
            val body = response.body?.string().orEmpty()
            if (body.isBlank()) return null

            val jsonObj = JSONObject(body)
            val responseData = jsonObj.optJSONObject("responseData") ?: return null
            val translated = responseData.optString("translatedText")
            if (translated.isNotBlank() && !translated.contains("NO QUERY SPECIFIED", ignoreCase = true) && !translated.contains("MYMEMORY WARNING", ignoreCase = true)) {
                translated.trim()
            } else {
                null
            }
        } catch (_: Throwable) {
            null
        }
    }
}
