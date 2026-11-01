package com.telefam.company.backend.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/**
 * Parameterized JDBC + HikariCP Data Access Layer for Telefam Production PostgreSQL.
 * Strictly uses bound PreparedStatements and SQL parameter types to prevent SQL injection.
 */
object TelefamDatabase {
    private var dataSource: HikariDataSource? = null

    fun init(jdbcUrl: String, dbUser: String, dbPassword: String, maxPoolSize: Int) {
        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = dbUser
            this.password = dbPassword
            this.maximumPoolSize = maxPoolSize
            this.driverClassName = "org.postgresql.Driver"
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            addDataSourceProperty("ApplicationName", "TelefamKtorBackend")
            addDataSourceProperty("reWriteBatchedInserts", "true")
        }
        dataSource = HikariDataSource(config)
        ensureSchemaInitialized()
    }

    fun ensureSchemaInitialized(schemaSql: String? = null) {
        val ds = dataSource ?: return
        runCatching {
            ds.connection.use { conn ->
                val sqlContent = schemaSql ?: runCatching {
                    val candidates = listOf(
                        java.io.File("backend/sql/telefam_postgres_schema.sql"),
                        java.io.File("sql/telefam_postgres_schema.sql"),
                        java.io.File("/backend/sql/telefam_postgres_schema.sql")
                    )
                    candidates.firstOrNull { it.exists() }?.readText()
                }.getOrNull()
                if (!sqlContent.isNullOrBlank()) {
                    conn.autoCommit = true
                    val statements = sqlContent.split(";")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    for (stmt in statements) {
                        runCatching {
                            conn.createStatement().use { s -> s.execute(stmt) }
                        }
                    }
                }
            }
        }
    }

    fun <T> withTransaction(block: (Connection) -> T): T {
        val ds = requireNotNull(dataSource) { "Telefam PostgreSQL DataSource not initialized" }
        ds.connection.use { conn ->
            try {
                val result = block(conn)
                conn.commit()
                return result
            } catch (ex: Exception) {
                conn.rollback()
                throw ex
            }
        }
    }

    fun invalidateExpiredOtpsAndSessions(conn: Connection, now: Instant) {
        val nowTs = Timestamp.from(now)
        conn.prepareStatement(
            """
            UPDATE telefam_otp_challenges
            SET is_invalidated = TRUE
            WHERE is_invalidated = FALSE AND expires_at <= ?
            """.trimIndent()
        ).use { ps ->
            ps.setTimestamp(1, nowTs)
            ps.executeUpdate()
        }

        conn.prepareStatement(
            """
            UPDATE telefam_encrypted_sessions
            SET is_revoked = TRUE
            WHERE is_revoked = FALSE AND expires_at <= ?
            """.trimIndent()
        ).use { ps ->
            ps.setTimestamp(1, nowTs)
            ps.executeUpdate()
        }
    }

    fun insertForensicAuditLog(
        conn: Connection,
        category: String,
        outcome: String,
        userId: UUID?,
        targetIdentifier: String?,
        ipSubnetFp: String,
        untrustedClientDeviceFp: String,
        serverVerifiedDeviceHash: String,
        geoLocationLabel: String?,
        geoLat: Double?,
        geoLon: Double?,
        riskScore: Int,
        riskSignalsJson: String,
        requestPath: String,
        httpMethod: String,
        userAgent: String?,
        metadataJson: String
    ) {
        val sql = """
            INSERT INTO telefam_forensic_audit_logs (
                event_category, event_outcome, user_id, target_identifier,
                ip_subnet_fingerprint, untrusted_client_device_fp, server_verified_device_hash,
                geo_location_label, geo_latitude, geo_longitude,
                computed_risk_score, risk_signals, request_path, http_method,
                user_agent, forensic_metadata
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?::jsonb)
        """.trimIndent()
        conn.prepareStatement(sql).use { ps: PreparedStatement ->
            ps.setString(1, category)
            ps.setString(2, outcome)
            ps.setObject(3, userId)
            ps.setString(4, targetIdentifier)
            ps.setString(5, ipSubnetFp)
            ps.setString(6, untrustedClientDeviceFp)
            ps.setString(7, serverVerifiedDeviceHash)
            ps.setString(8, geoLocationLabel)
            if (geoLat != null) ps.setDouble(9, geoLat) else ps.setNull(9, java.sql.Types.DOUBLE)
            if (geoLon != null) ps.setDouble(10, geoLon) else ps.setNull(10, java.sql.Types.DOUBLE)
            ps.setInt(11, riskScore)
            ps.setString(12, riskSignalsJson)
            ps.setString(13, requestPath)
            ps.setString(14, httpMethod)
            ps.setString(15, userAgent)
            ps.setString(16, metadataJson)
            ps.executeUpdate()
        }
    }

    /**
     * Executes a database block inside an explicit RLS-isolated transaction where
     * `app.current_user_id` is bound via parameterized `set_config` so PostgreSQL Row-Level Security
     * policies strictly isolate rows to the authenticated user.
     */
    fun <T> withRlsUserContext(userId: UUID, block: (Connection) -> T): T {
        val ds = checkNotNull(dataSource) { "Database pool is not initialized" }
        ds.connection.use { conn ->
            val oldAutoCommit = conn.autoCommit
            conn.autoCommit = false
            try {
                conn.prepareStatement("SELECT set_config('app.current_user_id', ?, true)").use { ps ->
                    ps.setString(1, userId.toString())
                    ps.executeQuery()
                }
                val result = block(conn)
                conn.commit()
                return result
            } catch (e: Exception) {
                conn.rollback()
                throw e
            } finally {
                conn.autoCommit = oldAutoCommit
            }
        }
    }

    fun upsertPrivacySettingsWithRls(
        userId: UUID,
        messageRequests: String,
        whoCanCallMe: String,
        whoCanScreenshotChats: String,
        whoCanShareChats: String,
        whoCanCopyMessages: String,
        whoCanDownloadMedia: String,
        chatThemeId: String,
        bubbleColorHex: String,
        readReceipts: String,
        typingIndicators: String,
        onlineIndicators: String,
        syncVersion: Long
    ) {
        withRlsUserContext(userId) { conn ->
            val sql = """
                INSERT INTO telefam_user_privacy_settings (
                    user_id, message_requests, who_can_call_me, who_can_screenshot_chats,
                    who_can_share_chats, who_can_copy_messages, who_can_download_media,
                    chat_theme_id, bubble_color_hex, read_receipts, typing_indicators,
                    online_indicators, sync_version, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
                ON CONFLICT (user_id) DO UPDATE SET
                    message_requests = EXCLUDED.message_requests,
                    who_can_call_me = EXCLUDED.who_can_call_me,
                    who_can_screenshot_chats = EXCLUDED.who_can_screenshot_chats,
                    who_can_share_chats = EXCLUDED.who_can_share_chats,
                    who_can_copy_messages = EXCLUDED.who_can_copy_messages,
                    who_can_download_media = EXCLUDED.who_can_download_media,
                    chat_theme_id = EXCLUDED.chat_theme_id,
                    bubble_color_hex = EXCLUDED.bubble_color_hex,
                    read_receipts = EXCLUDED.read_receipts,
                    typing_indicators = EXCLUDED.typing_indicators,
                    online_indicators = EXCLUDED.online_indicators,
                    sync_version = EXCLUDED.sync_version,
                    updated_at = NOW()
            """.trimIndent()
            conn.prepareStatement(sql).use { ps ->
                ps.setObject(1, userId)
                ps.setString(2, messageRequests)
                ps.setString(3, whoCanCallMe)
                ps.setString(4, whoCanScreenshotChats)
                ps.setString(5, whoCanShareChats)
                ps.setString(6, whoCanCopyMessages)
                ps.setString(7, whoCanDownloadMedia)
                ps.setString(8, chatThemeId)
                ps.setString(9, bubbleColorHex)
                ps.setString(10, readReceipts)
                ps.setString(11, typingIndicators)
                ps.setString(12, onlineIndicators)
                ps.setLong(13, syncVersion)
                ps.executeUpdate()
            }
        }
    }

    fun findPrivacySettings(userId: UUID): BackendPrivacyRecord? {
        return runCatching {
            withTransaction { conn ->
                val sql = "SELECT * FROM telefam_user_privacy_settings WHERE user_id = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setObject(1, userId)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) {
                            BackendPrivacyRecord(
                                userId = rs.getObject("user_id") as UUID,
                                messageRequests = rs.getString("message_requests") ?: "ANYONE",
                                whoCanCallMe = rs.getString("who_can_call_me") ?: "ANYONE",
                                whoCanScreenshotChats = rs.getString("who_can_screenshot_chats") ?: "ANYONE",
                                whoCanShareChats = rs.getString("who_can_share_chats") ?: "ANYONE",
                                whoCanCopyMessages = rs.getString("who_can_copy_messages") ?: "ANYONE",
                                whoCanDownloadMedia = rs.getString("who_can_download_media") ?: "ANYONE",
                                chatThemeId = rs.getString("chat_theme_id") ?: "crimson_silk",
                                bubbleColorHex = rs.getString("bubble_color_hex") ?: "#C80815",
                                readReceipts = rs.getString("read_receipts") ?: "ANYONE",
                                typingIndicators = rs.getString("typing_indicators") ?: "ANYONE",
                                onlineIndicators = rs.getString("online_indicators") ?: "ANYONE",
                                syncVersion = rs.getLong("sync_version")
                            )
                        } else null
                    }
                }
            }
        }.getOrNull()
    }

    fun findUserByEmail(email: String): BackendUserRecord? {
        return runCatching {
            withTransaction { conn ->
                val sql = "SELECT * FROM telefam_users WHERE LOWER(email) = LOWER(?)"
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, email.trim())
                    ps.executeQuery().use { rs ->
                        if (rs.next()) mapUserRecord(rs) else null
                    }
                }
            }
        }.getOrNull()
    }

    fun findUserById(userId: UUID): BackendUserRecord? {
        return runCatching {
            withTransaction { conn ->
                val sql = "SELECT * FROM telefam_users WHERE user_id = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setObject(1, userId)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) mapUserRecord(rs) else null
                    }
                }
            }
        }.getOrNull()
    }

    fun findUserByGoogleSub(googleSub: String): BackendUserRecord? {
        return runCatching {
            withTransaction { conn ->
                val sql = "SELECT * FROM telefam_users WHERE google_sub_id = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, googleSub.trim())
                    ps.executeQuery().use { rs ->
                        if (rs.next()) mapUserRecord(rs) else null
                    }
                }
            }
        }.getOrNull()
    }

    fun upsertUser(
        userId: UUID,
        email: String,
        passwordHash: String,
        passwordSalt: String,
        googleSubId: String? = null,
        emailVerified: Boolean = false,
        accountStatus: String = "ACTIVE"
    ): BackendUserRecord {
        return withTransaction { conn ->
            val sql = """
                INSERT INTO telefam_users (
                    user_id, email, password_hash, password_salt, google_sub_id,
                    email_verified, account_status, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
                ON CONFLICT (email) DO UPDATE SET
                    password_hash = CASE WHEN EXCLUDED.password_hash <> '' THEN EXCLUDED.password_hash ELSE telefam_users.password_hash END,
                    password_salt = CASE WHEN EXCLUDED.password_salt <> '' THEN EXCLUDED.password_salt ELSE telefam_users.password_salt END,
                    google_sub_id = COALESCE(EXCLUDED.google_sub_id, telefam_users.google_sub_id),
                    email_verified = telefam_users.email_verified OR EXCLUDED.email_verified,
                    account_status = EXCLUDED.account_status,
                    updated_at = NOW()
                RETURNING *
            """.trimIndent()
            conn.prepareStatement(sql).use { ps ->
                ps.setObject(1, userId)
                ps.setString(2, email.trim().lowercase())
                ps.setString(3, passwordHash)
                ps.setString(4, passwordSalt)
                if (googleSubId != null) ps.setString(5, googleSubId) else ps.setNull(5, java.sql.Types.VARCHAR)
                ps.setBoolean(6, emailVerified)
                ps.setString(7, accountStatus)
                ps.executeQuery().use { rs ->
                    rs.next()
                    mapUserRecord(rs)
                }
            }
        }
    }

    fun markEmailVerified(email: String) {
        runCatching {
            withTransaction { conn ->
                val sql = "UPDATE telefam_users SET email_verified = TRUE, updated_at = NOW() WHERE LOWER(email) = LOWER(?)"
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, email.trim())
                    ps.executeUpdate()
                }
            }
        }
    }

    fun findProfileByUserId(userId: UUID): BackendProfileRecord? {
        return runCatching {
            withTransaction { conn ->
                val sql = "SELECT * FROM telefam_profiles WHERE user_id = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setObject(1, userId)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) mapProfileRecord(rs) else null
                    }
                }
            }
        }.getOrNull()
    }

    fun findProfileByUsername(username: String): BackendProfileRecord? {
        return runCatching {
            withTransaction { conn ->
                val sql = "SELECT * FROM telefam_profiles WHERE LOWER(username) = LOWER(?)"
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, username.trim().removePrefix("@"))
                    ps.executeQuery().use { rs ->
                        if (rs.next()) mapProfileRecord(rs) else null
                    }
                }
            }
        }.getOrNull()
    }

    fun isUsernameTaken(username: String, excludeUserId: UUID? = null): Boolean {
        return runCatching {
            withTransaction { conn ->
                val sql = if (excludeUserId != null) {
                    "SELECT 1 FROM telefam_profiles WHERE LOWER(username) = LOWER(?) AND user_id <> ? LIMIT 1"
                } else {
                    "SELECT 1 FROM telefam_profiles WHERE LOWER(username) = LOWER(?) LIMIT 1"
                }
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, username.trim().removePrefix("@"))
                    if (excludeUserId != null) ps.setObject(2, excludeUserId)
                    ps.executeQuery().use { rs -> rs.next() }
                }
            }
        }.getOrDefault(false)
    }

    fun upsertProfile(
        userId: UUID,
        fullName: String,
        username: String,
        phoneCountryCode: String,
        phoneNumber: String,
        gender: String,
        dateOfBirth: String,
        bio: String,
        avatarStorageKey: String? = null,
        avatarSha256: String? = null,
        avatarDHash: String? = null
    ): BackendProfileRecord {
        return withTransaction { conn ->
            val cleanUsername = username.trim().removePrefix("@").lowercase()
            val dob = LocalDate.parse(dateOfBirth.filter { it.isDigit() || it == '-' }.take(10))
            val sql = """
                INSERT INTO telefam_profiles (
                    user_id, full_name, username, phone_country_code, phone_number,
                    gender, date_of_birth, bio, avatar_storage_key,
                    avatar_sha256_checksum, avatar_perceptual_dhash, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
                ON CONFLICT (user_id) DO UPDATE SET
                    full_name = EXCLUDED.full_name,
                    username = EXCLUDED.username,
                    phone_country_code = EXCLUDED.phone_country_code,
                    phone_number = EXCLUDED.phone_number,
                    gender = EXCLUDED.gender,
                    date_of_birth = EXCLUDED.date_of_birth,
                    bio = EXCLUDED.bio,
                    avatar_storage_key = COALESCE(EXCLUDED.avatar_storage_key, telefam_profiles.avatar_storage_key),
                    avatar_sha256_checksum = COALESCE(EXCLUDED.avatar_sha256_checksum, telefam_profiles.avatar_sha256_checksum),
                    avatar_perceptual_dhash = COALESCE(EXCLUDED.avatar_perceptual_dhash, telefam_profiles.avatar_perceptual_dhash),
                    updated_at = NOW()
                RETURNING *
            """.trimIndent()
            conn.prepareStatement(sql).use { ps ->
                ps.setObject(1, userId)
                ps.setString(2, fullName.trim())
                ps.setString(3, cleanUsername)
                ps.setString(4, phoneCountryCode.trim())
                ps.setString(5, phoneNumber.trim())
                ps.setString(6, gender.trim())
                ps.setDate(7, java.sql.Date.valueOf(dob))
                ps.setString(8, bio.trim())
                ps.setString(9, avatarStorageKey)
                ps.setString(10, avatarSha256)
                ps.setString(11, avatarDHash)
                ps.executeQuery().use { rs ->
                    rs.next()
                    mapProfileRecord(rs)
                }
            }
        }
    }

    fun discoverProfiles(
        query: String = "",
        page: Int = 1,
        pageSize: Int = 20,
        excludeUserId: UUID? = null
    ): List<BackendProfileRecord> {
        return runCatching {
            withTransaction { conn ->
                val offset = ((page - 1).coerceAtLeast(0)) * pageSize
                val hasQuery = query.isNotBlank()
                val sql = buildString {
                    append("SELECT p.* FROM telefam_profiles p ")
                    if (excludeUserId != null) {
                        append("WHERE p.user_id <> ? ")
                        if (hasQuery) append("AND (LOWER(p.username) LIKE ? OR LOWER(p.full_name) LIKE ?) ")
                    } else if (hasQuery) {
                        append("WHERE (LOWER(p.username) LIKE ? OR LOWER(p.full_name) LIKE ?) ")
                    }
                    append("ORDER BY p.updated_at DESC LIMIT ? OFFSET ?")
                }
                conn.prepareStatement(sql).use { ps ->
                    var idx = 1
                    if (excludeUserId != null) ps.setObject(idx++, excludeUserId)
                    if (hasQuery) {
                        val wildcard = "%${query.trim().lowercase()}%"
                        ps.setString(idx++, wildcard)
                        ps.setString(idx++, wildcard)
                    }
                    ps.setInt(idx++, pageSize.coerceIn(1, 50))
                    ps.setInt(idx++, offset)
                    ps.executeQuery().use { rs ->
                        val results = mutableListOf<BackendProfileRecord>()
                        while (rs.next()) {
                            results.add(mapProfileRecord(rs))
                        }
                        results
                    }
                }
            }
        }.getOrDefault(emptyList())
    }

    fun upsertE2eeKeyBundle(
        userId: UUID,
        registrationId: Int,
        identityPublicKeyB64: String,
        signedPreKeyId: Int,
        signedPreKeyPublicB64: String,
        signedPreKeySigB64: String,
        oneTimePreKeysJson: String
    ) {
        runCatching {
            withTransaction { conn ->
                val sql = """
                    INSERT INTO telefam_e2ee_key_bundles (
                        user_id, registration_id, identity_public_key_b64,
                        signed_prekey_id, signed_prekey_public_b64, signed_prekey_signature_b64,
                        one_time_prekeys_json, updated_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
                    ON CONFLICT (user_id) DO UPDATE SET
                        registration_id = EXCLUDED.registration_id,
                        identity_public_key_b64 = EXCLUDED.identity_public_key_b64,
                        signed_prekey_id = EXCLUDED.signed_prekey_id,
                        signed_prekey_public_b64 = EXCLUDED.signed_prekey_public_b64,
                        signed_prekey_signature_b64 = EXCLUDED.signed_prekey_signature_b64,
                        one_time_prekeys_json = EXCLUDED.one_time_prekeys_json,
                        updated_at = NOW()
                """.trimIndent()
                conn.prepareStatement(sql).use { ps ->
                    ps.setObject(1, userId)
                    ps.setInt(2, registrationId)
                    ps.setString(3, identityPublicKeyB64)
                    ps.setInt(4, signedPreKeyId)
                    ps.setString(5, signedPreKeyPublicB64)
                    ps.setString(6, signedPreKeySigB64)
                    ps.setString(7, oneTimePreKeysJson)
                    ps.executeUpdate()
                }
            }
        }
    }

    fun findE2eeKeyBundle(userId: UUID): BackendE2eeBundleRecord? {
        return runCatching {
            withTransaction { conn ->
                val sql = "SELECT * FROM telefam_e2ee_key_bundles WHERE user_id = ?"
                conn.prepareStatement(sql).use { ps ->
                    ps.setObject(1, userId)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) {
                            BackendE2eeBundleRecord(
                                userId = rs.getObject("user_id") as UUID,
                                registrationId = rs.getInt("registration_id"),
                                identityPublicKeyB64 = rs.getString("identity_public_key_b64"),
                                signedPreKeyId = rs.getInt("signed_prekey_id"),
                                signedPreKeyPublicB64 = rs.getString("signed_prekey_public_b64"),
                                signedPreKeySignatureB64 = rs.getString("signed_prekey_signature_b64"),
                                oneTimePreKeysJson = rs.getString("one_time_prekeys_json") ?: "[]"
                            )
                        } else null
                    }
                }
            }
        }.getOrNull()
    }

    private fun mapUserRecord(rs: java.sql.ResultSet): BackendUserRecord {
        return BackendUserRecord(
            userId = rs.getObject("user_id") as UUID,
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            passwordSalt = rs.getString("password_salt"),
            googleSubId = rs.getString("google_sub_id"),
            emailVerified = rs.getBoolean("email_verified"),
            accountStatus = rs.getString("account_status") ?: "ACTIVE",
            failedLoginAttempts = rs.getInt("failed_login_attempts"),
            loginLockedUntil = rs.getTimestamp("login_locked_until"),
            lastKnownCountry = rs.getString("last_known_country"),
            createdAt = rs.getTimestamp("created_at")
        )
    }

    private fun mapProfileRecord(rs: java.sql.ResultSet): BackendProfileRecord {
        return BackendProfileRecord(
            userId = rs.getObject("user_id") as UUID,
            fullName = rs.getString("full_name"),
            username = rs.getString("username"),
            phoneCountryCode = rs.getString("phone_country_code"),
            phoneNumber = rs.getString("phone_number"),
            gender = rs.getString("gender"),
            dateOfBirth = rs.getDate("date_of_birth")?.toString() ?: "2000-01-01",
            bio = rs.getString("bio") ?: "",
            avatarStorageKey = rs.getString("avatar_storage_key"),
            avatarSha256 = rs.getString("avatar_sha256_checksum"),
            avatarDHash = rs.getString("avatar_perceptual_dhash")
        )
    }
}

data class BackendUserRecord(
    val userId: UUID,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val googleSubId: String?,
    val emailVerified: Boolean,
    val accountStatus: String,
    val failedLoginAttempts: Int,
    val loginLockedUntil: Timestamp?,
    val lastKnownCountry: String?,
    val createdAt: Timestamp
)

data class BackendProfileRecord(
    val userId: UUID,
    val fullName: String,
    val username: String,
    val phoneCountryCode: String,
    val phoneNumber: String,
    val gender: String,
    val dateOfBirth: String,
    val bio: String,
    val avatarStorageKey: String?,
    val avatarSha256: String?,
    val avatarDHash: String?
)

data class BackendE2eeBundleRecord(
    val userId: UUID,
    val registrationId: Int,
    val identityPublicKeyB64: String,
    val signedPreKeyId: Int,
    val signedPreKeyPublicB64: String,
    val signedPreKeySignatureB64: String,
    val oneTimePreKeysJson: String
)

data class BackendPrivacyRecord(
    val userId: UUID,
    val messageRequests: String,
    val whoCanCallMe: String,
    val whoCanScreenshotChats: String,
    val whoCanShareChats: String,
    val whoCanCopyMessages: String,
    val whoCanDownloadMedia: String,
    val chatThemeId: String,
    val bubbleColorHex: String,
    val readReceipts: String,
    val typingIndicators: String,
    val onlineIndicators: String,
    val syncVersion: Long
)

