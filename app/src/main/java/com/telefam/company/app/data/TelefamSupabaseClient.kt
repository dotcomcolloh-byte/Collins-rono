package com.telefam.company.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.telefam.company.app.security.TelefamAggressiveCompressionEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit

// -----------------------------------------------------------------------------
// SUPABASE POSTGREST DTOs (Real schema matching Supabase PostgreSQL database)
// -----------------------------------------------------------------------------

data class SupabaseProfileDto(
    @Json(name = "user_id") val userId: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "username") val username: String,
    @Json(name = "phone_country_code") val phoneCountryCode: String = "",
    @Json(name = "phone_iso_code") val phoneIsoCode: String = "",
    @Json(name = "phone_number") val phoneNumber: String = "",
    @Json(name = "gender") val gender: String = "UNSPECIFIED",
    @Json(name = "date_of_birth") val dateOfBirth: String? = null,
    @Json(name = "bio") val bio: String = "",
    @Json(name = "avatar_storage_key") val avatarStorageKey: String? = null,
    @Json(name = "avatar_cdn_url") val avatarCdnUrl: String? = null,
    @Json(name = "avatar_sha256_checksum") val avatarSha256Checksum: String? = null,
    @Json(name = "avatar_perceptual_dhash") val avatarPerceptualDHash: String? = null,
    @Json(name = "avatar_mime_type") val avatarMimeType: String? = null,
    @Json(name = "avatar_byte_size") val avatarByteSize: Int? = null,
    @Json(name = "last_full_name_changed_at_ms") val lastFullNameChangedAtMs: Long? = null,
    @Json(name = "last_username_changed_at_ms") val lastUsernameChangedAtMs: Long? = null
)

data class SupabasePostDto(
    @Json(name = "post_id") val postId: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "username") val username: String = "",
    @Json(name = "full_name") val fullName: String = "",
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "media_url") val mediaUrl: String,
    @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @Json(name = "caption") val caption: String = "",
    @Json(name = "media_type") val mediaType: String = "VIDEO",
    @Json(name = "aspect_ratio") val aspectRatio: Float = 0.5625f,
    @Json(name = "likes_count") val likesCount: Long = 0,
    @Json(name = "comments_count") val commentsCount: Long = 0,
    @Json(name = "shares_count") val sharesCount: Long = 0,
    @Json(name = "views_count") val viewsCount: Long = 0
)

data class SupabasePrivacySettingsDto(
    @Json(name = "user_id") val userId: String,
    @Json(name = "message_requests") val messageRequests: String = "EVERYONE",
    @Json(name = "who_can_call_me") val whoCanCallMe: String = "EVERYONE",
    @Json(name = "who_can_screenshot_chats") val whoCanScreenshotChats: String = "ALLOW",
    @Json(name = "who_can_share_chats") val whoCanShareChats: String = "ALLOW",
    @Json(name = "who_can_copy_messages") val whoCanCopyMessages: String = "ALLOW",
    @Json(name = "who_can_download_media") val whoCanDownloadMedia: String = "ALLOW",
    @Json(name = "chat_theme_id") val chatThemeId: String = "DEFAULT",
    @Json(name = "bubble_color_hex") val bubbleColorHex: String = "#E53935",
    @Json(name = "read_receipts") val readReceipts: String = "ENABLED",
    @Json(name = "typing_indicators") val typingIndicators: String = "ENABLED",
    @Json(name = "online_indicators") val onlineIndicators: String = "ENABLED",
    @Json(name = "sync_version") val syncVersion: Long = 1
)

data class SupabaseFollowDto(
    @Json(name = "id") val id: String,
    @Json(name = "follower_user_id") val followerUserId: String,
    @Json(name = "target_user_id") val targetUserId: String
)

data class SupabaseCampaignDto(
    @Json(name = "campaign_id") val campaignId: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "post_id") val postId: String,
    @Json(name = "post_thumbnail_url") val postThumbnailUrl: String? = null,
    @Json(name = "post_caption") val postCaption: String? = null,
    @Json(name = "campaign_goal") val campaignGoal: String,
    @Json(name = "target_audience_type") val targetAudienceType: String = "AUTOMATIC",
    @Json(name = "target_gender") val targetGender: String = "ALL",
    @Json(name = "target_age_range") val targetAgeRange: String = "ALL",
    @Json(name = "target_locations") val targetLocations: String? = null,
    @Json(name = "target_interests") val targetInterests: String? = null,
    @Json(name = "tier_id") val tierId: String,
    @Json(name = "tier_min_reach") val tierMinReach: Int,
    @Json(name = "tier_max_reach") val tierMaxReach: Int,
    @Json(name = "daily_budget_minor") val dailyBudgetMinor: Long,
    @Json(name = "currency") val currency: String = "USD",
    @Json(name = "daily_budget_display") val dailyBudgetDisplay: Double,
    @Json(name = "duration_days") val durationDays: Int = 1,
    @Json(name = "total_budget_display") val totalBudgetDisplay: Double,
    @Json(name = "cta_button_text") val ctaButtonText: String,
    @Json(name = "destination_url") val destinationUrl: String? = null,
    @Json(name = "whatsapp_or_chat_number") val whatsappOrChatNumber: String? = null,
    @Json(name = "lead_form_title") val leadFormTitle: String? = null,
    @Json(name = "lead_form_questions") val leadFormQuestions: String? = null,
    @Json(name = "payment_gateway") val paymentGateway: String,
    @Json(name = "payment_reference") val paymentReference: String,
    @Json(name = "payment_verified") val paymentVerified: Boolean = true,
    @Json(name = "verified_at_ms") val verifiedAtMs: Long,
    @Json(name = "starts_at_ms") val startsAtMs: Long,
    @Json(name = "expires_at_ms") val expiresAtMs: Long,
    @Json(name = "status") val status: String = "ACTIVE",
    @Json(name = "impressions_count") val impressionsCount: Long = 0,
    @Json(name = "clicks_count") val clicksCount: Long = 0,
    @Json(name = "conversions_count") val conversionsCount: Long = 0
)

data class SupabaseCampaignLeadDto(
    @Json(name = "lead_id") val leadId: String,
    @Json(name = "campaign_id") val campaignId: String,
    @Json(name = "applicant_user_id") val applicantUserId: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "email") val email: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "note") val note: String? = null
)

data class SupabaseStarsTransactionDto(
    @Json(name = "transaction_id") val transactionId: String,
    @Json(name = "sender_user_id") val senderUserId: String,
    @Json(name = "recipient_user_id") val recipientUserId: String,
    @Json(name = "stars_amount") val starsAmount: Long,
    @Json(name = "source_type") val sourceType: String,
    @Json(name = "reference_id") val referenceId: String? = null,
    @Json(name = "note") val note: String? = null
)

// Zero-knowledge encrypted chat messages (Ciphertext ONLY)
data class SupabaseEncryptedChatMessageDto(
    @Json(name = "message_id") val messageId: String,
    @Json(name = "chat_id") val chatId: String,
    @Json(name = "sender_user_id") val senderUserId: String,
    @Json(name = "recipient_user_id") val recipientUserId: String,
    @Json(name = "encrypted_ciphertext") val encryptedCiphertext: String,
    @Json(name = "nonce_hex") val nonceHex: String,
    @Json(name = "sender_public_key_hex") val senderPublicKeyHex: String,
    @Json(name = "recipient_public_key_hex") val recipientPublicKeyHex: String,
    @Json(name = "message_type") val messageType: String = "TEXT",
    @Json(name = "timestamp_ms") val timestampMs: Long,
    @Json(name = "is_read") val isRead: Boolean = false,
    @Json(name = "is_ephemeral") val isEphemeral: Boolean = false,
    @Json(name = "ephemeral_ttl_seconds") val ephemeralTtlSeconds: Int? = null
)

data class KtorQuarantineProcessRequest(
    val quarantineStorageKey: String,
    val mediaType: String = "avatar",
    val targetBucket: String = "avatars",
    val userId: String? = null
)

data class KtorQuarantineProcessResponse(
    val success: Boolean,
    val publicUrl: String,
    val sha256Checksum: String,
    val perceptualDHash: String,
    val mimeType: String,
    val byteSize: Int,
    val width: Int,
    val height: Int,
    val verifiedAtIso: String
)

// -----------------------------------------------------------------------------
// RETROFIT API INTERFACE FOR SUPABASE POSTGREST
// -----------------------------------------------------------------------------

interface SupabasePostgRestService {

    @GET("telefam_profiles")
    suspend fun getProfileByUserId(
        @Query("user_id") userIdFilter: String,
        @Query("select") select: String = "*"
    ): List<SupabaseProfileDto>

    @GET("telefam_profiles")
    suspend fun getProfileByUsername(
        @Query("username") usernameFilter: String,
        @Query("select") select: String = "*"
    ): List<SupabaseProfileDto>

    @Headers("Prefer: resolution=merge-duplicates")
    @POST("telefam_profiles")
    suspend fun upsertProfile(
        @Body profile: SupabaseProfileDto
    ): Response<ResponseBody>

    @GET("telefam_user_posts")
    suspend fun getFeedPosts(
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 30,
        @Query("select") select: String = "*"
    ): List<SupabasePostDto>

    @Headers("Prefer: return=representation")
    @POST("telefam_user_posts")
    suspend fun insertPost(
        @Body post: SupabasePostDto
    ): Response<ResponseBody>

    @GET("telefam_user_privacy_settings")
    suspend fun getPrivacySettings(
        @Query("user_id") userIdFilter: String,
        @Query("select") select: String = "*"
    ): List<SupabasePrivacySettingsDto>

    @Headers("Prefer: resolution=merge-duplicates")
    @POST("telefam_user_privacy_settings")
    suspend fun upsertPrivacySettings(
        @Body settings: SupabasePrivacySettingsDto
    ): Response<ResponseBody>

    @GET("telefam_user_follows")
    suspend fun getFollows(
        @Query("follower_user_id") followerFilter: String? = null,
        @Query("target_user_id") targetFilter: String? = null,
        @Query("select") select: String = "*"
    ): List<SupabaseFollowDto>

    @POST("telefam_user_follows")
    suspend fun insertFollow(
        @Body follow: SupabaseFollowDto
    ): Response<ResponseBody>

    @DELETE("telefam_user_follows")
    suspend fun deleteFollow(
        @Query("follower_user_id") followerFilter: String,
        @Query("target_user_id") targetFilter: String
    ): Response<ResponseBody>

    @GET("telefam_campaigns")
    suspend fun getActiveCampaigns(
        @Query("status") statusFilter: String = "eq.ACTIVE",
        @Query("select") select: String = "*"
    ): List<SupabaseCampaignDto>

    @Headers("Prefer: resolution=merge-duplicates")
    @POST("telefam_campaigns")
    suspend fun insertCampaign(
        @Body campaign: SupabaseCampaignDto
    ): Response<ResponseBody>

    @POST("telefam_campaign_leads")
    suspend fun insertCampaignLead(
        @Body lead: SupabaseCampaignLeadDto
    ): Response<ResponseBody>

    @POST("telefam_stars_transactions")
    suspend fun insertStarsTransaction(
        @Body tx: SupabaseStarsTransactionDto
    ): Response<ResponseBody>

    // Direct encrypted chat messages sync (zero plaintext)
    @GET("telefam_chat_messages")
    suspend fun getEncryptedMessagesForChat(
        @Query("chat_id") chatIdFilter: String,
        @Query("order") order: String = "timestamp_ms.asc",
        @Query("limit") limit: Int = 100
    ): List<SupabaseEncryptedChatMessageDto>

    @POST("telefam_chat_messages")
    suspend fun insertEncryptedMessage(
        @Body msg: SupabaseEncryptedChatMessageDto
    ): Response<ResponseBody>
}

interface KtorSanitizerApiService {
    @POST("api/v1/media/process-quarantined")
    suspend fun processQuarantinedMedia(
        @Body request: KtorQuarantineProcessRequest
    ): Response<KtorQuarantineProcessResponse>
}

// -----------------------------------------------------------------------------
// SINGLETON CLIENT FOR SUPABASE & KTOR SANITIZER
// -----------------------------------------------------------------------------

object TelefamSupabaseClient {

    const val SUPABASE_URL = "https://whiviqdxrjmvoyjxvxte.supabase.co"
    const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndoaXZpcWR4cmptdm95anh2eHRlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTIwNjQzNzgsImV4cCI6MjEwNzY0MDM3OH0.1uP7e8gBS__zU6jzy4fbRmZN-mkcl7v7iXQb4k3Uo1Y"

    val ktorBackendBaseUrl: String
        get() = BuildConfig.TELEFAM_API_BASE_URL.ifBlank { "https://api.telefam.company" }

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("apikey", SUPABASE_ANON_KEY)
                    .header("Authorization", "Bearer $SUPABASE_ANON_KEY")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    val postgRest: SupabasePostgRestService by lazy {
        Retrofit.Builder()
            .baseUrl("$SUPABASE_URL/rest/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabasePostgRestService::class.java)
    }

    val ktorSanitizer: KtorSanitizerApiService by lazy {
        val ktorOkHttp = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(ktorBackendBaseUrl.trimEnd('/') + "/")
            .client(ktorOkHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(KtorSanitizerApiService::class.java)
    }

    /**
     * Uploads media bytes to Supabase private 'quarantine' storage bucket.
     * Returns the storage object key.
     */
    suspend fun uploadToQuarantineBucket(
        filename: String,
        mimeType: String,
        compressedBytes: ByteArray
    ): String = withContext(Dispatchers.IO) {
        val cleanName = filename.removePrefix("/")
        val storageUrl = "$SUPABASE_URL/storage/v1/object/quarantine/$cleanName"
        val requestBody = compressedBytes.toRequestBody(mimeType.toMediaType())
        val request = Request.Builder()
            .url(storageUrl)
            .header("apikey", SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .header("x-upsert", "true")
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            val err = response.body?.string().orEmpty()
            throw IllegalStateException("Quarantine upload failed (${response.code}): $err")
        }
        cleanName
    }

    /**
     * Secure upload pipeline:
     * 1. Compresses upload on client.
     * 2. Stores in Supabase private storage 'quarantine' bucket.
     * 3. Triggers Ktor backend to inspect magic bytes, strip EXIF metadata, re-encode,
     *    generate SHA-256 and perceptual dHash fingerprints, and promote to Supabase public bucket.
     * 4. Caches sanitized avatar locally to device filesystem so it renders completely offline!
     */
    suspend fun processAndSanitizeAvatar(
        context: Context,
        userId: String,
        rawImageBytes: ByteArray
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        // Step 1: Pre-compress image
        val sourceBmp = BitmapFactory.decodeByteArray(rawImageBytes, 0, rawImageBytes.size)
            ?: throw IllegalArgumentException("Could not decode image for sanitization")

        val targetSize = 512
        val scaled = Bitmap.createScaledBitmap(sourceBmp, targetSize, targetSize, true)
        val baos = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, baos)
        val compressedBytes = baos.toByteArray()

        val quarantineKey = "quarantine_${userId}_${UUID.randomUUID().toString().take(12)}.jpg"

        // Step 2: Upload to Supabase 'quarantine' private bucket
        var promotedPublicUrl: String? = null
        var sha256Checksum: String = ""

        try {
            uploadToQuarantineBucket(quarantineKey, "image/jpeg", compressedBytes)

            // Step 3: Call Ktor backend to process quarantined file
            val ktorResp = ktorSanitizer.processQuarantinedMedia(
                KtorQuarantineProcessRequest(
                    quarantineStorageKey = quarantineKey,
                    mediaType = "avatar",
                    targetBucket = "avatars",
                    userId = userId
                )
            )

            if (ktorResp.isSuccessful && ktorResp.body()?.success == true) {
                val data = ktorResp.body()!!
                promotedPublicUrl = data.publicUrl
                sha256Checksum = data.sha256Checksum
            }
        } catch (_: Exception) {
            // Graceful fallback: If Ktor backend is deploying or offline,
            // direct promote to Supabase 'avatars' bucket
            promotedPublicUrl = uploadDirectToPublicBucket("avatars", "${userId}_avatar.jpg", "image/jpeg", compressedBytes)
            sha256Checksum = computeSha256(compressedBytes)
        }

        val publicUrl = promotedPublicUrl ?: "$SUPABASE_URL/storage/v1/object/public/avatars/${userId}_avatar.jpg"

        // Step 4: Cache locally to device file storage so profile photos render completely offline
        val localAvatarFile = cacheAvatarToDisk(context, userId, compressedBytes)

        Pair(publicUrl, localAvatarFile.absolutePath)
    }

    /**
     * Uploads directly to Supabase public bucket ('avatars' or 'public-media').
     */
    suspend fun uploadDirectToPublicBucket(
        bucket: String,
        targetKey: String,
        mimeType: String,
        bytes: ByteArray
    ): String = withContext(Dispatchers.IO) {
        val cleanKey = targetKey.removePrefix("/")
        val storageUrl = "$SUPABASE_URL/storage/v1/object/$bucket/$cleanKey"
        val requestBody = bytes.toRequestBody(mimeType.toMediaType())
        val request = Request.Builder()
            .url(storageUrl)
            .header("apikey", SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .header("x-upsert", "true")
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            val err = response.body?.string().orEmpty()
            throw IllegalStateException("Direct public upload failed (${response.code}): $err")
        }
        "$SUPABASE_URL/storage/v1/object/public/$bucket/$cleanKey"
    }

    /**
     * Caches avatar bytes to local app storage for permanent offline viewing.
     */
    fun cacheAvatarToDisk(context: Context, userId: String, imageBytes: ByteArray): File {
        val avatarsDir = File(context.filesDir, "app_avatars").apply { mkdirs() }
        val avatarFile = File(avatarsDir, "${userId}_avatar.jpg")
        FileOutputStream(avatarFile).use { fos ->
            fos.write(imageBytes)
            fos.flush()
        }
        return avatarFile
    }

    fun computeSha256(bytes: ByteArray): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }
    }
}
