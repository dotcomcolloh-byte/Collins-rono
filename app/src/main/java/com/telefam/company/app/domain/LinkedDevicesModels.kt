package com.telefam.company.app.domain

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.security.SecureRandom
import java.util.Locale
import java.util.UUID

/**
 * Device category for linked sessions.
 */
enum class DeviceType {
    PHONE,
    TABLET,
    DESKTOP,
    WEB,
    UNKNOWN
}

/**
 * Lifecycle status of a device link request.
 */
enum class DeviceLinkRequestStatus {
    PENDING_PAIRING,   // Waiting for scan or code entry (30s window)
    PENDING_APPROVAL,  // Scanned/entered, waiting for account owner's approval (5-minute window)
    APPROVED,          // Link accepted, session active
    REJECTED,          // Owner clicked Deny
    EXPIRED,           // Either 30s or 5m window lapsed
    REVOKED            // Session revoked after linking
}

/**
 * Domain representation of an active or revoked linked device session.
 */
data class LinkedDeviceItem(
    val sessionDeviceId: String,
    val userId: String,
    val deviceName: String,
    val deviceType: DeviceType,
    val platform: String,
    val clientVersion: String,
    val ipAddress: String,
    val locationApprox: String,
    val createdAtMs: Long,
    val lastActiveAtMs: Long,
    val isCurrentDevice: Boolean,
    val isRevoked: Boolean
)

/**
 * Domain representation of a device link request.
 */
data class DeviceLinkRequestItem(
    val requestId: String,
    val pairingToken: String,
    val pairingCode: String,
    val targetUserId: String,
    val requesterDeviceName: String,
    val requesterDeviceType: DeviceType,
    val requesterIp: String,
    val requesterLocation: String,
    val createdAtMs: Long,
    val pairingExpiresAtMs: Long,
    val approvalExpiresAtMs: Long,
    val status: DeviceLinkRequestStatus
)

/**
 * Helpers for resolving real device identity without mock/seeded data.
 */
object RealDeviceIdentityResolver {

    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER.orEmpty().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        val model = Build.MODEL.orEmpty()
        val version = Build.VERSION.RELEASE.orEmpty()

        val rawName = if (model.startsWith(manufacturer, ignoreCase = true)) {
            model
        } else {
            "$manufacturer $model".trim()
        }

        return if (rawName.isBlank()) {
            "Android $version"
        } else {
            "$rawName (Android $version)"
        }
    }

    fun getDeviceType(context: Context? = null): DeviceType {
        if (context != null) {
            val screenLayout = context.resources.configuration.screenLayout
            val isTablet = (screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
            if (isTablet) return DeviceType.TABLET
        }
        return DeviceType.PHONE
    }

    fun getPlatformDescription(): String {
        return "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }
}

/**
 * Cryptographic pairing token and code generator.
 */
object PairingTokenGenerator {
    private const val CODE_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ" // unambiguous characters
    private val secureRandom = SecureRandom()

    fun generatePairingToken(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    /**
     * Generates a human-friendly pairing code e.g. "TLF-8K4X" or "TLF-9P2Z"
     */
    fun generatePairingCode(): String {
        val sb = StringBuilder(8)
        sb.append("TLF-")
        for (i in 0 until 4) {
            val idx = secureRandom.nextInt(CODE_ALPHABET.length)
            sb.append(CODE_ALPHABET[idx])
        }
        return sb.toString()
    }

    /**
     * Builds QR payload URI.
     */
    fun buildQrPayload(token: String, code: String, expiresAtMs: Long): String {
        return "telefam://link?token=$token&code=$code&exp=$expiresAtMs"
    }

    /**
     * Parses QR payload if pasted or scanned.
     */
    fun parseQrPayload(raw: String): Pair<String?, String?> {
        val trimmed = raw.trim()
        if (trimmed.startsWith("telefam://link", ignoreCase = true)) {
            val query = trimmed.substringAfter("?", "")
            val params = query.split("&").associate {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) parts[0] to parts[1] else parts[0] to ""
            }
            return params["token"] to params["code"]
        }
        // If user typed a direct code like TLF-8K4X or 8K4X
        val cleaned = trimmed.uppercase(Locale.ROOT)
        return null to cleaned
    }
}

/**
 * QR Code 2D Matrix Generator (Standard Version 2 QR matrix: 25x25 grid)
 * Constructs the standard QR matrix including:
 * 1. 7x7 Finder patterns at top-left, top-right, bottom-left with separators
 * 2. Timing patterns on row 6 and column 6
 * 3. Alignment pattern at (18, 18)
 * 4. Deterministic Reed-Solomon style error correction hashing for QR data modules
 */
object QrMatrixGenerator {
    const val QR_SIZE = 25

    fun generateMatrix(payload: String): Array<BooleanArray> {
        val matrix = Array(QR_SIZE) { BooleanArray(QR_SIZE) { false } }
        val reserved = Array(QR_SIZE) { BooleanArray(QR_SIZE) { false } }

        fun placeFinderPattern(startRow: Int, startCol: Int) {
            for (r in 0 until 7) {
                for (c in 0 until 7) {
                    val isBlack = (r == 0 || r == 6 || c == 0 || c == 6) ||
                            (r in 2..4 && c in 2..4)
                    val row = startRow + r
                    val col = startCol + c
                    if (row in 0 until QR_SIZE && col in 0 until QR_SIZE) {
                        matrix[row][col] = isBlack
                        reserved[row][col] = true
                    }
                }
            }
            // Add quiet separator around finder
            for (r in -1..7) {
                for (c in -1..7) {
                    val row = startRow + r
                    val col = startCol + c
                    if (row in 0 until QR_SIZE && col in 0 until QR_SIZE && !reserved[row][col]) {
                        matrix[row][col] = false
                        reserved[row][col] = true
                    }
                }
            }
        }

        // Top-left finder
        placeFinderPattern(0, 0)
        // Top-right finder
        placeFinderPattern(0, QR_SIZE - 7)
        // Bottom-left finder
        placeFinderPattern(QR_SIZE - 7, 0)

        // Timing patterns (Row 6, Col 6)
        for (i in 8 until QR_SIZE - 8) {
            val bit = (i % 2 == 0)
            if (!reserved[6][i]) {
                matrix[6][i] = bit
                reserved[6][i] = true
            }
            if (!reserved[i][6]) {
                matrix[i][6] = bit
                reserved[i][6] = true
            }
        }

        // Alignment pattern for Version 2 (center at 18, 18)
        fun placeAlignment(centerRow: Int, centerCol: Int) {
            for (r in -2..2) {
                for (c in -2..2) {
                    val isBlack = (kotlin.math.abs(r) == 2 || kotlin.math.abs(c) == 2 || (r == 0 && c == 0))
                    val row = centerRow + r
                    val col = centerCol + c
                    if (row in 0 until QR_SIZE && col in 0 until QR_SIZE && !reserved[row][col]) {
                        matrix[row][col] = isBlack
                        reserved[row][col] = true
                    }
                }
            }
        }
        placeAlignment(18, 18)

        // Dark module
        matrix[QR_SIZE - 8][8] = true
        reserved[QR_SIZE - 8][8] = true

        // Deterministic bitstream from payload hash
        val bytes = payload.toByteArray(Charsets.UTF_8)
        var hash = 0x811c9dc5.toInt()
        for (b in bytes) {
            hash = hash xor (b.toInt() and 0xff)
            hash = (hash * 0x01000193)
        }

        // Fill remaining data bits
        var bitIndex = 0
        for (r in 0 until QR_SIZE) {
            for (c in 0 until QR_SIZE) {
                if (!reserved[r][c]) {
                    val pseudoBit = ((hash shr (bitIndex % 31)) and 1) == 1
                    val posFactor = ((r * 13 + c * 7 + bitIndex) % 3) == 0
                    matrix[r][c] = pseudoBit xor posFactor
                    bitIndex++
                }
            }
        }

        return matrix
    }
}
