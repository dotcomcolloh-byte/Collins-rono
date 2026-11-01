package com.telefam.company.backend.supabase

import com.telefam.company.backend.security.SanitizedMediaArtifact
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.slf4j.LoggerFactory

/**
 * High-performance, zero-leakage client communicating directly with Supabase Storage & PostgREST.
 * Used exclusively by the server-side Ktor sanitizer to:
 * 1. Pull unverified uploads from the private 'quarantine' bucket.
 * 2. Push verified, metadata-stripped, compressed assets to public buckets ('avatars', 'public-media').
 * 3. Delete quarantined artifacts after forensic analysis and cryptographic fingerprinting.
 */
class SupabaseStorageService(
    val supabaseUrl: String = "https://whiviqdxrjmvoyjxvxte.supabase.co",
    val anonKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndoaXZpcWR4cmptdm95anh2eHRlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTIwNjQzNzgsImV4cCI6MjEwNzY0MDM3OH0.1uP7e8gBS__zU6jzy4fbRmZN-mkcl7v7iXQb4k3Uo1Y",
    val serviceRoleKey: String = ""
) {
    private val logger = LoggerFactory.getLogger(SupabaseStorageService::class.java)

    private val effectiveKey: String
        get() = serviceRoleKey.ifBlank { anonKey }

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
            )
        }
    }

    /**
     * Retrieves unverified object bytes from the private 'quarantine' bucket.
     */
    suspend fun downloadFromQuarantine(storageKey: String): ByteArray? {
        val cleanKey = storageKey.trim().removePrefix("/")
        val endpoint = "$supabaseUrl/storage/v1/object/quarantine/$cleanKey"
        return try {
            val response = httpClient.get(endpoint) {
                header("apikey", effectiveKey)
                header(HttpHeaders.Authorization, "Bearer $effectiveKey")
            }
            if (response.status == HttpStatusCode.OK) {
                response.bodyAsBytes()
            } else {
                logger.warn("Failed to download from quarantine: $endpoint, status: ${response.status}")
                null
            }
        } catch (e: Exception) {
            logger.error("Exception downloading quarantined object $cleanKey", e)
            null
        }
    }

    /**
     * Uploads verified, sanitized media bytes to destination public bucket.
     * Returns full public CDN URL on Supabase.
     */
    suspend fun uploadSanitized(
        bucket: String,
        targetKey: String,
        mimeType: String,
        bytes: ByteArray
    ): String {
        val cleanKey = targetKey.trim().removePrefix("/")
        val endpoint = "$supabaseUrl/storage/v1/object/$bucket/$cleanKey"
        val response = httpClient.post(endpoint) {
            header("apikey", effectiveKey)
            header(HttpHeaders.Authorization, "Bearer $effectiveKey")
            header("x-upsert", "true")
            contentType(ContentType.parse(mimeType))
            setBody(bytes)
        }
        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.Created) {
            val body = response.bodyAsText()
            logger.error("Supabase Storage upload failed with status ${response.status}: $body")
            throw IllegalStateException("Failed to push sanitized media to Supabase storage ($bucket): $body")
        }
        return "$supabaseUrl/storage/v1/object/public/$bucket/$cleanKey"
    }

    /**
     * Purges quarantined upload once sanitization and promotion succeeds.
     */
    suspend fun deleteFromQuarantine(storageKey: String): Boolean {
        val cleanKey = storageKey.trim().removePrefix("/")
        val endpoint = "$supabaseUrl/storage/v1/object/quarantine/$cleanKey"
        return try {
            val response = httpClient.delete(endpoint) {
                header("apikey", effectiveKey)
                header(HttpHeaders.Authorization, "Bearer $effectiveKey")
            }
            response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent
        } catch (e: Exception) {
            logger.error("Failed to delete quarantined object $cleanKey", e)
            false
        }
    }

    /**
     * Records forensic fingerprint and perceptual hash into Supabase PostgREST table.
     */
    suspend fun recordFingerprint(artifact: SanitizedMediaArtifact): Boolean {
        val endpoint = "$supabaseUrl/rest/v1/telefam_media_fingerprints"
        return try {
            val jsonPayload = buildJsonObject {
                put("sha256_checksum", artifact.sha256Checksum)
                put("perceptual_dhash", artifact.perceptualDHash)
                put("storage_key", artifact.storageKey)
                put("verified_mime_type", artifact.verifiedMimeType)
                put("compressed_byte_size", artifact.finalByteSize)
                put("width_px", artifact.width)
                put("height_px", artifact.height)
                put("reuse_count", 1)
            }

            val response = httpClient.post(endpoint) {
                header("apikey", effectiveKey)
                header(HttpHeaders.Authorization, "Bearer $effectiveKey")
                header("Prefer", "resolution=merge-duplicates")
                contentType(ContentType.Application.Json)
                setBody(jsonPayload.toString())
            }
            response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            logger.warn("Could not record fingerprint to Supabase: ${e.message}")
            false
        }
    }
}
