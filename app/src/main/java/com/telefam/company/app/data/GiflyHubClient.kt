package com.telefam.company.app.data

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GiflyMediaResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "type") val type: String = "stickers",
    @Json(name = "query") val query: String? = null,
    @Json(name = "page") val page: Int = 1,
    @Json(name = "per_page") val perPage: Int = 24,
    @Json(name = "has_next") val hasNext: Boolean = false,
    @Json(name = "count") val count: Int = 0,
    @Json(name = "items") val items: List<GiflyMediaItemDto> = emptyList(),
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class GiflyMediaItemDto(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "slug") val slug: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "type") val type: String = "sticker",
    @Json(name = "preview") val preview: String? = null,
    @Json(name = "preview_webp") val previewWebp: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "url_hd") val urlHd: String? = null,
    @Json(name = "mp4") val mp4: String? = null,
    @Json(name = "width") val width: Int = 240,
    @Json(name = "height") val height: Int = 240
) {
    val bestDisplayUrl: String
        get() = urlHd ?: url ?: previewWebp ?: preview ?: ""

    val bestPreviewUrl: String
        get() = previewWebp ?: preview ?: url ?: ""
}

interface GiflyHubSearchApi {
    @GET("api/public/media")
    suspend fun getMedia(
        @Query("type") type: String, // "stickers", "gifs", "clips", "emojis"
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 24
    ): GiflyMediaResponse
}

object GiflyHubClient {
    private const val BASE_URL = "https://gifly-hub-search.lovable.app/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    val api: GiflyHubSearchApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GiflyHubSearchApi::class.java)
    }

    /**
     * Automatically caches a remote sticker/GIF to device disk storage for instant offline loading and zero jitter.
     */
    suspend fun cacheMediaToDevice(context: Context, id: Long, url: String): File? = withContext(Dispatchers.IO) {
        if (url.isBlank()) return@withContext null
        try {
            val cacheDir = File(context.cacheDir, "gifly_stickers_cache").apply { mkdirs() }
            val ext = when {
                url.contains(".webp", ignoreCase = true) -> "webp"
                url.contains(".mp4", ignoreCase = true) -> "mp4"
                else -> "gif"
            }
            val destination = File(cacheDir, "gifly_${id}.$ext")
            if (destination.exists() && destination.length() > 0L) {
                return@withContext destination
            }

            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.byteStream()?.use { input ->
                        FileOutputStream(destination).use { output ->
                            input.copyTo(output)
                        }
                    }
                    if (destination.length() > 0L) destination else null
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
}
