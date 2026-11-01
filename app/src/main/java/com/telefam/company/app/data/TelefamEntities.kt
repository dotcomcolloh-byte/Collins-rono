package com.telefam.company.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "telefam_users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password_hash") val passwordHash: String,
    @ColumnInfo(name = "password_salt") val passwordSalt: String,
    @ColumnInfo(name = "google_sub_id") val googleSubId: String? = null,
    @ColumnInfo(name = "email_verified") val emailVerified: Boolean = false,
    @ColumnInfo(name = "failed_login_attempts") val failedLoginAttempts: Int = 0,
    @ColumnInfo(name = "login_locked_until_ms") val loginLockedUntilMs: Long? = null,
    @ColumnInfo(name = "failed_otp_attempts") val failedOtpAttempts: Int = 0,
    @ColumnInfo(name = "otp_locked_until_ms") val otpLockedUntilMs: Long? = null,
    @ColumnInfo(name = "last_known_location") val lastKnownLocation: String? = null,
    @ColumnInfo(name = "last_latitude") val lastLatitude: Double? = null,
    @ColumnInfo(name = "last_longitude") val lastLongitude: Double? = null,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_profiles",
    indices = [Index(value = ["username"], unique = true)]
)
data class ProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "phone_country_code") val phoneCountryCode: String,
    @ColumnInfo(name = "phone_iso_code") val phoneIsoCode: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "date_of_birth") val dateOfBirth: String,
    @ColumnInfo(name = "bio") val bio: String,
    @ColumnInfo(name = "avatar_local_path") val avatarLocalPath: String? = null,
    @ColumnInfo(name = "avatar_cdn_url") val avatarCdnUrl: String? = null,
    @ColumnInfo(name = "avatar_sha256") val avatarSha256: String? = null,
    @ColumnInfo(name = "avatar_perceptual_dhash") val avatarPerceptualDHash: String? = null,
    @ColumnInfo(name = "avatar_mime_type") val avatarMimeType: String? = null,
    @ColumnInfo(name = "avatar_byte_size") val avatarByteSize: Int? = null,
    @ColumnInfo(name = "last_full_name_changed_at_ms") val lastFullNameChangedAtMs: Long? = null,
    @ColumnInfo(name = "last_username_changed_at_ms") val lastUsernameChangedAtMs: Long? = null,
    @ColumnInfo(name = "subscribers_count") val subscribersCount: Long = 0L,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = false,
    @ColumnInfo(name = "last_synced_at_ms") val lastSyncedAtMs: Long? = null,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_media_fingerprints",
    indices = [Index(value = ["perceptual_dhash"])]
)
data class MediaFingerprintEntity(
    @PrimaryKey
    @ColumnInfo(name = "sha256_checksum") val sha256Checksum: String,
    @ColumnInfo(name = "perceptual_dhash") val perceptualDHash: String,
    @ColumnInfo(name = "secure_file_path") val secureFilePath: String,
    @ColumnInfo(name = "cdn_storage_url") val cdnStorageUrl: String,
    @ColumnInfo(name = "verified_mime_type") val verifiedMimeType: String,
    @ColumnInfo(name = "original_byte_size") val originalByteSize: Int,
    @ColumnInfo(name = "compressed_byte_size") val compressedByteSize: Int,
    @ColumnInfo(name = "width_px") val widthPx: Int = 512,
    @ColumnInfo(name = "height_px") val heightPx: Int = 512,
    @ColumnInfo(name = "reuse_count") val reuseCount: Int = 1,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_reused_at_ms") val lastReusedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_trusted_devices",
    indices = [Index(value = ["user_id", "server_device_hash"], unique = true)]
)
data class TrustedDeviceEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_record_id") val deviceRecordId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "untrusted_client_fp") val untrustedClientFp: String,
    @ColumnInfo(name = "server_device_hash") val serverDeviceHash: String,
    @ColumnInfo(name = "ip_subnet_fp") val ipSubnetFp: String,
    @ColumnInfo(name = "os_description") val osDescription: String,
    @ColumnInfo(name = "location_label") val locationLabel: String,
    @ColumnInfo(name = "is_trusted") val isTrusted: Boolean = true,
    @ColumnInfo(name = "last_seen_ms") val lastSeenMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_otp_challenges",
    indices = [Index(value = ["email", "purpose", "is_invalidated"])]
)
data class OtpChallengeEntity(
    @PrimaryKey
    @ColumnInfo(name = "challenge_id") val challengeId: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "purpose") val purpose: String,
    @ColumnInfo(name = "otp_hmac_digest") val otpHmacDigest: String,
    @ColumnInfo(name = "ip_subnet_fp") val ipSubnetFp: String,
    @ColumnInfo(name = "server_device_hash") val serverDeviceHash: String,
    @ColumnInfo(name = "verify_attempts") val verifyAttempts: Int = 0,
    @ColumnInfo(name = "is_invalidated") val isInvalidated: Boolean = false,
    @ColumnInfo(name = "resend_available_at_ms") val resendAvailableAtMs: Long,
    @ColumnInfo(name = "expires_at_ms") val expiresAtMs: Long,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "telefam_encrypted_sessions")
data class EncryptedSessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "session_id") val sessionId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "access_token_sha256") val accessTokenSha256: String,
    @ColumnInfo(name = "refresh_token_sha256") val refreshTokenSha256: String = "",
    @ColumnInfo(name = "encrypted_payload_base64") val encryptedPayloadBase64: String,
    @ColumnInfo(name = "iv_base64") val ivBase64: String,
    @ColumnInfo(name = "server_device_hash") val serverDeviceHash: String,
    @ColumnInfo(name = "ip_subnet_fp") val ipSubnetFp: String,
    @ColumnInfo(name = "location_label") val locationLabel: String,
    @ColumnInfo(name = "risk_score") val riskScore: Int,
    @ColumnInfo(name = "is_revoked") val isRevoked: Boolean = false,
    @ColumnInfo(name = "expires_at_ms") val expiresAtMs: Long,
    @ColumnInfo(name = "refresh_expires_at_ms") val refreshExpiresAtMs: Long = expiresAtMs + 30L * 24 * 60 * 60 * 1000L,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "telefam_rate_limits")
data class RateLimitBucketEntity(
    @PrimaryKey
    @ColumnInfo(name = "bucket_key") val bucketKey: String,
    @ColumnInfo(name = "action_type") val actionType: String,
    @ColumnInfo(name = "failed_count") val failedCount: Int = 0,
    @ColumnInfo(name = "cooldown_until_ms") val cooldownUntilMs: Long? = null,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "telefam_forensic_audit_logs")
data class ForensicAuditLogEntity(
    @PrimaryKey
    @ColumnInfo(name = "audit_id") val auditId: String,
    @ColumnInfo(name = "timestamp_ms") val timestampMs: Long,
    @ColumnInfo(name = "event_category") val eventCategory: String,
    @ColumnInfo(name = "event_outcome") val eventOutcome: String,
    @ColumnInfo(name = "target_identifier") val targetIdentifier: String,
    @ColumnInfo(name = "client_ip") val clientIp: String,
    @ColumnInfo(name = "ip_subnet_fp") val ipSubnetFp: String,
    @ColumnInfo(name = "untrusted_client_fp") val untrustedClientFp: String,
    @ColumnInfo(name = "server_device_hash") val serverDeviceHash: String,
    @ColumnInfo(name = "location_label") val locationLabel: String,
    @ColumnInfo(name = "risk_score") val riskScore: Int,
    @ColumnInfo(name = "risk_signals_csv") val riskSignalsCsv: String,
    @ColumnInfo(name = "endpoint_path") val endpointPath: String,
    @ColumnInfo(name = "forensic_detail") val forensicDetail: String
)

@Entity(tableName = "telefam_user_privacy_settings")
data class PrivacySettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "message_requests") val messageRequests: String = "ANYONE",
    @ColumnInfo(name = "who_can_call_me") val whoCanCallMe: String = "ANYONE",
    @ColumnInfo(name = "who_can_screenshot_chats") val whoCanScreenshotChats: String = "ANYONE",
    @ColumnInfo(name = "who_can_share_chats") val whoCanShareChats: String = "ANYONE",
    @ColumnInfo(name = "who_can_copy_messages") val whoCanCopyMessages: String = "ANYONE",
    @ColumnInfo(name = "who_can_download_media") val whoCanDownloadMedia: String = "ANYONE",
    @ColumnInfo(name = "chat_theme_id") val chatThemeId: String = "crimson_silk",
    @ColumnInfo(name = "bubble_color_hex") val bubbleColorHex: String = "#C80815",
    @ColumnInfo(name = "read_receipts") val readReceipts: String = "ANYONE",
    @ColumnInfo(name = "typing_indicators") val typingIndicators: String = "ANYONE",
    @ColumnInfo(name = "online_indicators") val onlineIndicators: String = "ANYONE",
    @ColumnInfo(name = "sync_version") val syncVersion: Long = 1L,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = false,
    @ColumnInfo(name = "sync_retry_count") val syncRetryCount: Int = 0,
    @ColumnInfo(name = "last_synced_at_ms") val lastSyncedAtMs: Long? = null,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_follow_edges",
    primaryKeys = ["follower_user_id", "target_user_id"],
    indices = [
        Index(value = ["follower_user_id"]),
        Index(value = ["target_user_id"]),
        Index(value = ["idempotency_key"], unique = true)
    ]
)
data class FollowEdgeEntity(
    @ColumnInfo(name = "follower_user_id") val followerUserId: String,
    @ColumnInfo(name = "target_user_id") val targetUserId: String,
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String,
    @ColumnInfo(name = "internal_risk_score") val internalRiskScore: Int = 0,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = false,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_contact_hashes",
    primaryKeys = ["owner_user_id", "contact_hash_sha256"],
    indices = [
        Index(value = ["owner_user_id"]),
        Index(value = ["contact_hash_sha256"])
    ]
)
data class ContactHashEntity(
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "contact_hash_sha256") val contactHashSha256: String,
    @ColumnInfo(name = "hash_type") val hashType: String, // PHONE or EMAIL
    @ColumnInfo(name = "matched_user_id") val matchedUserId: String? = null,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = true,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_user_blocks",
    primaryKeys = ["blocker_user_id", "blocked_user_id"],
    indices = [
        Index(value = ["blocker_user_id"]),
        Index(value = ["blocked_user_id"]),
        Index(value = ["idempotency_key"], unique = true)
    ]
)
data class UserBlockEntity(
    @ColumnInfo(name = "blocker_user_id") val blockerUserId: String,
    @ColumnInfo(name = "blocked_user_id") val blockedUserId: String,
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = true,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_user_reports",
    indices = [
        Index(value = ["reporter_user_id"]),
        Index(value = ["reported_user_id"]),
        Index(value = ["idempotency_key"], unique = true)
    ]
)
data class UserReportEntity(
    @PrimaryKey
    @ColumnInfo(name = "report_id") val reportId: String,
    @ColumnInfo(name = "reporter_user_id") val reporterUserId: String,
    @ColumnInfo(name = "reported_user_id") val reportedUserId: String,
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String,
    @ColumnInfo(name = "reason_code") val reasonCode: String,
    @ColumnInfo(name = "location_code") val locationCode: String,
    @ColumnInfo(name = "urgency_code") val urgencyCode: String,
    @ColumnInfo(name = "additional_notes") val additionalNotes: String = "",
    @ColumnInfo(name = "internal_risk_score") val internalRiskScore: Int = 0,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = true,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_user_posts",
    indices = [Index(value = ["user_id", "created_at_ms"])]
)
data class UserPostEntity(
    @PrimaryKey
    @ColumnInfo(name = "post_id") val postId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "media_theme_key") val mediaThemeKey: String,
    @ColumnInfo(name = "caption") val caption: String,
    @ColumnInfo(name = "duration_label") val durationLabel: String = "0:42",
    @ColumnInfo(name = "post_category") val postCategory: String = "POSTS",
    @ColumnInfo(name = "views_count") val viewsCount: Long,
    @ColumnInfo(name = "likes_count") val likesCount: Long,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long
)

@Entity(
    tableName = "telefam_user_subscriptions",
    primaryKeys = ["subscriber_user_id", "creator_user_id"],
    indices = [
        Index(value = ["subscriber_user_id"]),
        Index(value = ["creator_user_id"]),
        Index(value = ["idempotency_key"], unique = true)
    ]
)
data class UserSubscriptionEntity(
    @ColumnInfo(name = "subscriber_user_id") val subscriberUserId: String,
    @ColumnInfo(name = "creator_user_id") val creatorUserId: String,
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = false,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_social_sync_queue",
    indices = [Index(value = ["idempotency_key"], unique = true)]
)
data class SocialSyncQueueEntity(
    @PrimaryKey
    @ColumnInfo(name = "Operation_id") val operationId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "operation_type") val operationType: String, // FOLLOW, UNFOLLOW, UPLOAD_CONTACTS, BLOCK, UNBLOCK, REPORT, SETTINGS_SYNC, ACCOUNT_STATUS, TICKET_SUBMIT, DEVICE_LOGOUT, PASSKEY_UPDATE
    @ColumnInfo(name = "target_user_id") val targetUserId: String = "",
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String,
    @ColumnInfo(name = "payload_json") val payloadJson: String,
    @ColumnInfo(name = "internal_risk_score") val internalRiskScore: Int = 0,
    @ColumnInfo(name = "retry_count") val retryCount: Int = 0,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_account_security_settings",
    indices = [Index(value = ["owner_user_id"], unique = true)]
)
data class AccountSettingsAndSecurityEntity(
    @PrimaryKey
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    // Account Recovery & Status
    @ColumnInfo(name = "recovery_email") val recoveryEmail: String = "",
    @ColumnInfo(name = "recovery_phone") val recoveryPhone: String = "",
    @ColumnInfo(name = "backup_recovery_codes_csv") val backupRecoveryCodesCsv: String = "",
    @ColumnInfo(name = "account_status") val accountStatus: String = "ACTIVE", // ACTIVE, DEACTIVATED, PENDING_DELETION_30D
    @ColumnInfo(name = "deactivated_at_ms") val deactivatedAtMs: Long? = null,
    @ColumnInfo(name = "auto_reenable_at_ms") val autoReenableAtMs: Long? = null,
    @ColumnInfo(name = "deactivation_reason") val deactivationReason: String = "",
    @ColumnInfo(name = "deletion_requested_at_ms") val deletionRequestedAtMs: Long? = null,
    @ColumnInfo(name = "permanent_deletion_at_ms") val permanentDeletionAtMs: Long? = null,
    @ColumnInfo(name = "deletion_reason") val deletionReason: String = "",
    // Two-Step Verification & Passkey (FIDO2 / WebAuthn Protocol)
    @ColumnInfo(name = "two_step_enabled") val twoStepEnabled: Boolean = false,
    @ColumnInfo(name = "two_step_method") val twoStepMethod: String = "EMAIL", // EMAIL or AUTHENTICATOR_TOTP
    @ColumnInfo(name = "authenticator_secret_base32") val authenticatorSecretBase32: String = "",
    @ColumnInfo(name = "passkey_enabled") val passkeyEnabled: Boolean = false,
    @ColumnInfo(name = "passkey_credential_id") val passkeyCredentialId: String = "",
    @ColumnInfo(name = "passkey_public_key_cose_hex") val passkeyPublicKeyCoseHex: String = "",
    @ColumnInfo(name = "passkey_rp_id") val passkeyRpId: String = "telefam.company",
    @ColumnInfo(name = "passkey_device_label") val passkeyDeviceLabel: String = "",
    @ColumnInfo(name = "passkey_registered_at_ms") val passkeyRegisteredAtMs: Long? = null,
    // Account Privacy & Detailed Privacy Controls
    @ColumnInfo(name = "is_private_account") val isPrivateAccount: Boolean = false,
    @ColumnInfo(name = "activity_status_enabled") val activityStatusEnabled: Boolean = true,
    @ColumnInfo(name = "who_can_see_videos") val whoCanSeeVideos: String = "EVERYONE",
    @ColumnInfo(name = "who_can_see_likes") val whoCanSeeLikes: String = "EVERYONE",
    @ColumnInfo(name = "who_can_see_follow_list") val whoCanSeeFollowList: String = "EVERYONE",
    @ColumnInfo(name = "who_can_comment") val whoCanComment: String = "EVERYONE",
    @ColumnInfo(name = "who_can_mention") val whoCanMention: String = "EVERYONE",
    @ColumnInfo(name = "who_can_tag") val whoCanTag: String = "EVERYONE",
    @ColumnInfo(name = "who_can_message") val whoCanMessage: String = "EVERYONE",
    @ColumnInfo(name = "allow_downloads") val allowDownloads: Boolean = true,
    @ColumnInfo(name = "who_can_download") val whoCanDownload: String = "EVERYONE",
    @ColumnInfo(name = "who_can_reuse_remix") val whoCanReuseRemix: String = "EVERYONE",
    // System & App Permissions State
    @ColumnInfo(name = "permission_contacts_allowed") val permissionContactsAllowed: Boolean = false,
    @ColumnInfo(name = "permission_camera_allowed") val permissionCameraAllowed: Boolean = false,
    @ColumnInfo(name = "permission_microphone_allowed") val permissionMicrophoneAllowed: Boolean = false,
    @ColumnInfo(name = "permission_photos_allowed") val permissionPhotosAllowed: Boolean = true,
    // Notifications Controls
    @ColumnInfo(name = "notify_likes") val notifyLikes: Boolean = true,
    @ColumnInfo(name = "notify_comments") val notifyComments: Boolean = true,
    @ColumnInfo(name = "notify_new_followers") val notifyNewFollowers: Boolean = true,
    @ColumnInfo(name = "notify_mentions") val notifyMentions: Boolean = true,
    @ColumnInfo(name = "notify_direct_messages") val notifyDirectMessages: Boolean = true,
    @ColumnInfo(name = "notify_recommended_content") val notifyRecommendedContent: Boolean = false,
    @ColumnInfo(name = "notify_live_notifications") val notifyLiveNotifications: Boolean = true,
    @ColumnInfo(name = "notify_email_notifications") val notifyEmailNotifications: Boolean = true,
    @ColumnInfo(name = "push_master_enabled") val pushMasterEnabled: Boolean = true,
    @ColumnInfo(name = "push_quiet_hours_enabled") val pushQuietHoursEnabled: Boolean = false,
    @ColumnInfo(name = "email_security_alerts") val emailSecurityAlerts: Boolean = true,
    @ColumnInfo(name = "email_product_updates") val emailProductUpdates: Boolean = false,
    @ColumnInfo(name = "sms_security_alerts") val smsSecurityAlerts: Boolean = true,
    // Content Preferences
    @ColumnInfo(name = "content_languages_csv") val contentLanguagesCsv: String = "English,Swahili",
    @ColumnInfo(name = "selected_topics_csv") val selectedTopicsCsv: String = "Technology,Community,Education,Music",
    @ColumnInfo(name = "suggested_prioritize_following") val suggestedPrioritizeFollowing: Boolean = true,
    @ColumnInfo(name = "suggested_local_region_boost") val suggestedLocalRegionBoost: Boolean = true,
    @ColumnInfo(name = "video_quality_mode") val videoQualityMode: String = "AUTO", // AUTO, HIGH_1080P, DATA_SAVER
    @ColumnInfo(name = "autoplay_mode") val autoplayMode: String = "WIFI_AND_MOBILE", // WIFI_AND_MOBILE, WIFI_ONLY, NEVER
    @ColumnInfo(name = "content_filtering_level") val contentFilteringLevel: String = "STANDARD", // LESS_SENSITIVE, STANDARD, MORE_SENSITIVE
    // Appearance, Backend QR & Policy/Help Acknowledgements
    @ColumnInfo(name = "app_theme_mode") val appThemeMode: String = "DARK", // SYSTEM, LIGHT, DARK
    @ColumnInfo(name = "backend_qr_payload_token") val backendQrPayloadToken: String = "",
    @ColumnInfo(name = "accepted_policies_csv") val acceptedPoliciesCsv: String = "",
    @ColumnInfo(name = "helpful_articles_csv") val helpfulArticlesCsv: String = "",
    @ColumnInfo(name = "is_marketplace_suspended") val isMarketplaceSuspended: Boolean = false,
    @ColumnInfo(name = "marketplace_suspension_reason") val marketplaceSuspensionReason: String = "",
    // RLS & Offline Sync Tracking
    @ColumnInfo(name = "sync_version") val syncVersion: Long = 1L,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = false,
    @ColumnInfo(name = "last_synced_at_ms") val lastSyncedAtMs: Long? = null,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_connected_apps",
    indices = [Index(value = ["owner_user_id"])]
)
data class ConnectedAppSessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "connection_id") val connectionId: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "developer_domain") val developerDomain: String,
    @ColumnInfo(name = "scopes_granted_csv") val scopesGrantedCsv: String,
    @ColumnInfo(name = "connected_at_ms") val connectedAtMs: Long,
    @ColumnInfo(name = "last_used_at_ms") val lastUsedAtMs: Long,
    @ColumnInfo(name = "is_revoked") val isRevoked: Boolean = false,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = false
)

@Entity(
    tableName = "telefam_problem_support_tickets",
    indices = [
        Index(value = ["owner_user_id"]),
        Index(value = ["idempotency_key"], unique = true)
    ]
)
data class ProblemSupportTicketEntity(
    @PrimaryKey
    @ColumnInfo(name = "ticket_id") val ticketId: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "ticket_type") val ticketType: String, // PROBLEM_REPORT or SUPPORT_REQUEST
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "subject") val subject: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "screenshot_local_path") val screenshotLocalPath: String? = null,
    @ColumnInfo(name = "status") val status: String = "SUBMITTED",
    @ColumnInfo(name = "support_response_note") val supportResponseNote: String = "Your request has been received by Telefam Support. You can add additional details or mark this request resolved at any time.",
    @ColumnInfo(name = "user_follow_up_reply") val userFollowUpReply: String = "",
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = true,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_quick_login_accounts",
    indices = [Index(value = ["email"], unique = true)]
)
data class QuickLoginAccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "avatar_local_path") val avatarLocalPath: String? = null,
    @ColumnInfo(name = "account_status") val accountStatus: String = "ACTIVE",
    @ColumnInfo(name = "auto_reenable_at_ms") val autoReenableAtMs: Long? = null,
    @ColumnInfo(name = "permanent_deletion_at_ms") val permanentDeletionAtMs: Long? = null,
    @ColumnInfo(name = "last_logged_out_at_ms") val lastLoggedOutAtMs: Long = System.currentTimeMillis()
)

// =============================================================================
// 1:1 E2EE CHATS, MESSAGES, REACTIONS, PINS, FAVOURITES, DISAPPEARING, RECEIPTS,
// SIGNAL X3DH + DOUBLE RATCHET KEYS/SESSIONS, OUTBOX & KLIPY CACHE (OFFLINE-FIRST)
// =============================================================================

@Entity(
    tableName = "telefam_chats",
    indices = [
        Index(value = ["owner_user_id", "peer_user_id"], unique = true),
        Index(value = ["owner_user_id", "last_message_at_ms"])
    ]
)
data class ChatEntity(
    @PrimaryKey
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "peer_user_id") val peerUserId: String,
    @ColumnInfo(name = "peer_full_name") val peerFullName: String,
    @ColumnInfo(name = "peer_username") val peerUsername: String,
    @ColumnInfo(name = "peer_avatar_url") val peerAvatarUrl: String? = null,
    @ColumnInfo(name = "peer_bio") val peerBio: String = "",
    @ColumnInfo(name = "is_peer_online") val peerIsOnline: Boolean = false,
    @ColumnInfo(name = "peer_last_seen_ms") val peerLastSeenMs: Long? = null,
    @ColumnInfo(name = "is_peer_typing") val peerIsTyping: Boolean = false,
    @ColumnInfo(name = "is_peer_account_deleted") val peerIsDeleted: Boolean = false,
    @ColumnInfo(name = "is_blocked_by_me") val isBlockedByMe: Boolean = false,
    @ColumnInfo(name = "is_blocked_by_peer") val isBlockedByPeer: Boolean = false,
    @ColumnInfo(name = "last_message_id") val lastMessageId: String? = null,
    @ColumnInfo(name = "last_message_type") val lastMessageType: String = "TEXT", // TEXT, STICKER, GIF, CLIP, VOICE_NOTE
    @ColumnInfo(name = "last_message_preview") val lastMessagePreview: String = "",
    @ColumnInfo(name = "last_message_sender_id") val lastMessageSenderId: String = "",
    @ColumnInfo(name = "last_message_status") val lastMessageDeliveryStatus: String = "SENT", // SENDING, SENT, DELIVERED, SEEN, REJECTED_BLOCKED, REJECTED_USER_NOT_FOUND
    @ColumnInfo(name = "last_message_media_icon_url") val lastMessageMediaIconUrl: String? = null,
    @ColumnInfo(name = "last_message_at_ms") val lastMessageTimestampMs: Long = 0L,
    @ColumnInfo(name = "unread_count") val unreadCount: Int = 0,
    @ColumnInfo(name = "disappearing_duration_seconds") val disappearingDurationSeconds: Long = 0L, // 0=Off, 86400=24h, 604800=7d, 2592000=1m
    @ColumnInfo(name = "is_e2ee_verified") val e2eeSafetyNumberVerified: Boolean = false,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_chat_messages",
    indices = [
        Index(value = ["chat_id", "created_at_ms"]),
        Index(value = ["owner_user_id", "chat_id"]),
        Index(value = ["reply_to_message_id"])
    ]
)
data class ChatMessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "message_id") val messageId: String,
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "sender_user_id") val senderUserId: String,
    @ColumnInfo(name = "recipient_user_id") val recipientUserId: String,
    @ColumnInfo(name = "message_type") val messageType: String, // TEXT, STICKER, GIF, CLIP, VOICE_NOTE
    // Decrypted local fields in Room (encrypted on wire; server stores ciphertext only)
    @ColumnInfo(name = "decrypted_text") val decryptedText: String = "",
    @ColumnInfo(name = "media_id") val mediaId: String? = null,
    @ColumnInfo(name = "media_url") val mediaUrl: String? = null,
    @ColumnInfo(name = "media_preview_url") val mediaPreviewUrl: String? = null,
    @ColumnInfo(name = "media_local_path") val mediaLocalPath: String? = null,
    @ColumnInfo(name = "media_title") val mediaTitle: String = "",
    @ColumnInfo(name = "media_mime_type") val mediaMimeType: String = "",
    @ColumnInfo(name = "voice_duration_ms") val voiceDurationMs: Long = 0L,
    @ColumnInfo(name = "voice_waveform_csv") val voiceWaveformCsv: String = "",
    @ColumnInfo(name = "is_view_once") val isViewOnce: Boolean = false,
    @ColumnInfo(name = "is_view_once_consumed") val isViewOnceConsumed: Boolean = false,
    // Reply & Forward metadata
    @ColumnInfo(name = "reply_to_message_id") val replyToMessageId: String? = null,
    @ColumnInfo(name = "reply_to_sender_name") val replyToSenderName: String? = null,
    @ColumnInfo(name = "reply_to_preview_text") val replyToPreviewText: String? = null,
    @ColumnInfo(name = "reply_to_message_type") val replyToMessageType: String? = null,
    @ColumnInfo(name = "is_forwarded") val isForwarded: Boolean = false,
    @ColumnInfo(name = "is_edited") val isEdited: Boolean = false,
    @ColumnInfo(name = "edited_at_ms") val editedAtMs: Long? = null,
    // E2EE Signal Double Ratchet Ciphertext Envelope (stored on server)
    @ColumnInfo(name = "ciphertext_base64") val ciphertextBase64: String,
    @ColumnInfo(name = "ratchet_header_json") val ratchetHeaderJson: String,
    // Delivery & Lifecycle state
    @ColumnInfo(name = "delivery_status") val deliveryStatus: String = "SENDING", // SENDING, SENT, DELIVERED, SEEN, REJECTED_BLOCKED, REJECTED_USER_NOT_FOUND
    @ColumnInfo(name = "delivered_at_ms") val deliveredAtMs: Long? = null,
    @ColumnInfo(name = "seen_at_ms") val seenAtMs: Long? = null,
    @ColumnInfo(name = "expires_at_ms") val expiresAtMs: Long? = null,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_message_reactions",
    primaryKeys = ["message_id", "reactor_user_id"],
    indices = [
        Index(value = ["chat_id"]),
        Index(value = ["message_id"])
    ]
)
data class MessageReactionEntity(
    @ColumnInfo(name = "message_id") val messageId: String,
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "reactor_user_id") val reactorUserId: String,
    @ColumnInfo(name = "reaction_key") val reactionKey: String, // HEART, THUMBS_UP, FIRE, LAUGH, SURPRISED, PRAY
    @ColumnInfo(name = "encrypted_reaction_base64") val encryptedReactionBase64: String = "",
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_chat_pins",
    primaryKeys = ["chat_id", "message_id"],
    indices = [Index(value = ["chat_id", "pinned_at_ms"])]
)
data class ChatPinEntity(
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "message_id") val messageId: String,
    @ColumnInfo(name = "pinned_by_user_id") val pinnedByUserId: String,
    @ColumnInfo(name = "pinned_at_ms") val pinnedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_chat_favourites",
    primaryKeys = ["owner_user_id", "message_id"],
    indices = [Index(value = ["owner_user_id", "chat_id"])]
)
data class ChatFavouriteEntity(
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "message_id") val messageId: String,
    @ColumnInfo(name = "favourited_at_ms") val favouritedAtMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "telefam_disappearing_settings")
data class DisappearingSettingEntity(
    @PrimaryKey
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "duration_seconds") val durationSeconds: Long = 0L, // 0, 86400, 604800, 2592000
    @ColumnInfo(name = "updated_by_user_id") val updatedByUserId: String,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_delivery_receipts",
    primaryKeys = ["message_id", "recipient_user_id"],
    indices = [Index(value = ["chat_id"])]
)
data class DeliveryReceiptEntity(
    @ColumnInfo(name = "message_id") val messageId: String,
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "recipient_user_id") val recipientUserId: String,
    @ColumnInfo(name = "receipt_type") val receiptType: String, // DELIVERED or SEEN
    @ColumnInfo(name = "timestamp_ms") val timestampMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "telefam_chat_drafts")
data class ChatDraftEntity(
    @PrimaryKey
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "draft_text") val draftText: String = "",
    @ColumnInfo(name = "reply_to_message_id") val replyToMessageId: String? = null,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "telefam_signal_identity_keys")
data class SignalIdentityKeyEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "registration_id") val registrationId: Int,
    @ColumnInfo(name = "public_key_base64") val publicKeyBase64: String,
    // Private key is wrapped with AndroidKeyStore AES-256-GCM master key; null for remote peers
    @ColumnInfo(name = "keystore_wrapped_private_key_base64") val keystoreWrappedPrivateKeyBase64: String? = null,
    @ColumnInfo(name = "keystore_iv_base64") val keystoreIvBase64: String? = null,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_signal_signed_prekeys",
    primaryKeys = ["user_id", "prekey_id"]
)
data class SignalSignedPreKeyEntity(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "prekey_id") val prekeyId: Int,
    @ColumnInfo(name = "public_key_base64") val publicKeyBase64: String,
    @ColumnInfo(name = "signature_base64") val signatureBase64: String,
    @ColumnInfo(name = "keystore_wrapped_private_key_base64") val keystoreWrappedPrivateKeyBase64: String? = null,
    @ColumnInfo(name = "keystore_iv_base64") val keystoreIvBase64: String? = null,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_signal_one_time_prekeys",
    primaryKeys = ["user_id", "prekey_id"]
)
data class SignalOneTimePreKeyEntity(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "prekey_id") val prekeyId: Int,
    @ColumnInfo(name = "public_key_base64") val publicKeyBase64: String,
    @ColumnInfo(name = "keystore_wrapped_private_key_base64") val keystoreWrappedPrivateKeyBase64: String? = null,
    @ColumnInfo(name = "keystore_iv_base64") val keystoreIvBase64: String? = null,
    @ColumnInfo(name = "is_consumed") val isConsumed: Boolean = false,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_signal_ratchet_sessions",
    primaryKeys = ["owner_user_id", "peer_user_id"]
)
data class SignalRatchetSessionEntity(
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "peer_user_id") val peerUserId: String,
    @ColumnInfo(name = "keystore_wrapped_state_base64") val keystoreWrappedStateBase64: String,
    @ColumnInfo(name = "keystore_iv_base64") val keystoreIvBase64: String,
    @ColumnInfo(name = "local_identity_pub_base64") val localIdentityPubBase64: String,
    @ColumnInfo(name = "peer_identity_pub_base64") val peerIdentityPubBase64: String,
    @ColumnInfo(name = "safety_number_digits") val safetyNumberDigits: String,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_chat_outbox",
    indices = [
        Index(value = ["owner_user_id", "status", "created_at_ms"]),
        Index(value = ["idempotency_key"], unique = true)
    ]
)
data class ChatOutboxEntity(
    @PrimaryKey
    @ColumnInfo(name = "outbox_id") val outboxId: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "chat_id") val chatId: String,
    @ColumnInfo(name = "target_user_id") val targetUserId: String,
    @ColumnInfo(name = "message_id") val messageId: String? = null,
    @ColumnInfo(name = "action_type") val actionType: String, // SEND, EDIT, UNSEND, DELETE, REACT, PIN, FAVOURITE, FORWARD, BLOCK, REPORT, CLEAR_CHAT, DISAPPEARING, DELIVERY_RECEIPT, SEEN_RECEIPT, VIEW_ONCE_CONSUMED, PUBLISH_PREKEYS
    @ColumnInfo(name = "payload_json") val payloadJson: String,
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String,
    @ColumnInfo(name = "status") val status: String = "PENDING", // PENDING, TERMINAL_REJECTED
    @ColumnInfo(name = "terminal_error_code") val terminalErrorCode: String? = null, // BLOCKED, USER_NOT_FOUND
    @ColumnInfo(name = "retry_count") val retryCount: Int = 0,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_klipy_cached_items",
    primaryKeys = ["item_id", "media_tab"],
    indices = [Index(value = ["media_tab", "search_query", "page_Order"])]
)
data class KlipyCachedItemEntity(
    @ColumnInfo(name = "item_id") val itemId: String,
    @ColumnInfo(name = "media_tab") val mediaTab: String, // STICKERS, GIFS, CLIPS
    @ColumnInfo(name = "search_query") val searchQuery: String = "",
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "media_url") val mediaUrl: String,
    @ColumnInfo(name = "preview_url") val previewUrl: String,
    @ColumnInfo(name = "local_cached_path") val localCachedPath: String? = null,
    @ColumnInfo(name = "mime_type") val mimeType: String = "image/gif",
    @ColumnInfo(name = "width_px") val widthPx: Int = 240,
    @ColumnInfo(name = "height_px") val heightPx: Int = 240,
    @ColumnInfo(name = "duration_ms") val durationMs: Long = 2400L,
    @ColumnInfo(name = "accent_hex") val accentHex: String = "#C80815",
    @ColumnInfo(name = "page_Order") val pageOrder: Int = 0,
    @ColumnInfo(name = "cached_at_ms") val cachedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_klipy_recents",
    primaryKeys = ["owner_user_id", "item_id"],
    indices = [Index(value = ["owner_user_id", "media_tab", "last_used_at_ms"])]
)
data class KlipyRecentItemEntity(
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "item_id") val itemId: String,
    @ColumnInfo(name = "media_tab") val mediaTab: String, // STICKERS, GIFS, CLIPS
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "media_url") val mediaUrl: String,
    @ColumnInfo(name = "preview_url") val previewUrl: String,
    @ColumnInfo(name = "local_cached_path") val localCachedPath: String? = null,
    @ColumnInfo(name = "mime_type") val mimeType: String = "image/gif",
    @ColumnInfo(name = "accent_hex") val accentHex: String = "#C80815",
    @ColumnInfo(name = "last_used_at_ms") val lastUsedAtMs: Long = System.currentTimeMillis()
)

// =============================================================================
// TIKTOK-STYLE FEEDS, VIDEOS, AUDIOS, LIKES, COMMENTS_COUNT, SHARES, SAVES,
// HASHTAGS, VIDEO_HASHTAGS, USER_INTERESTS & VIDEO_VIEWS TABLES
// =============================================================================

@Entity(
    tableName = "videos",
    indices = [
        Index(value = ["owner_user_id", "created_at_ms"]),
        Index(value = ["audio_id", "created_at_ms"]),
        Index(value = ["created_at_ms"])
    ]
)
data class FeedVideoEntity(
    @PrimaryKey
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "caption") val caption: String,
    @ColumnInfo(name = "language_tag") val languageTag: String = "English",
    @ColumnInfo(name = "video_url") val videoUrl: String,
    @ColumnInfo(name = "url_360p") val url360p: String,
    @ColumnInfo(name = "url_720p") val url720p: String,
    @ColumnInfo(name = "url_1080p") val url1080p: String,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String,
    @ColumnInfo(name = "width_px") val widthPx: Int = 1080,
    @ColumnInfo(name = "height_px") val heightPx: Int = 1920,
    @ColumnInfo(name = "duration_ms") val durationMs: Long = 15_000L,
    @ColumnInfo(name = "duration_label") val durationLabel: String = "0:15",
    @ColumnInfo(name = "audio_id") val audioId: String,
    @ColumnInfo(name = "visibility_audience") val visibilityAudience: String = "ANYONE",
    @ColumnInfo(name = "allow_download") val allowDownload: Boolean = true,
    @ColumnInfo(name = "allow_comments") val allowComments: Boolean = true,
    @ColumnInfo(name = "allow_duet_stitch") val allowDuetStitch: Boolean = true,
    @ColumnInfo(name = "views_count") val viewsCount: Long = 0L,
    @ColumnInfo(name = "likes_count") val likesCount: Long = 0L,
    @ColumnInfo(name = "comments_count") val commentsCount: Long = 0L,
    @ColumnInfo(name = "shares_count") val sharesCount: Long = 0L,
    @ColumnInfo(name = "saves_count") val savesCount: Long = 0L,
    @ColumnInfo(name = "caption_embedding_csv") val captionEmbeddingCsv: String = "",
    @ColumnInfo(name = "composite_fingerprint_id") val compositeFingerprintId: String = "",
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "video_audios",
    indices = [
        Index(value = ["owner_user_id"]),
        Index(value = ["title"])
    ]
)
data class FeedVideoAudioEntity(
    @PrimaryKey
    @ColumnInfo(name = "audio_id") val audioId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "artist_name") val artistName: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "owner_username") val ownerUsername: String,
    @ColumnInfo(name = "cover_art_url") val coverArtUrl: String? = null,
    @ColumnInfo(name = "stream_preview_url") val streamPreviewUrl: String = "",
    @ColumnInfo(name = "duration_ms") val durationMs: Long = 30_000L,
    @ColumnInfo(name = "is_original_sound") val isOriginalSound: Boolean = true,
    @ColumnInfo(name = "videos_count") val videosCount: Long = 1L,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "likes",
    primaryKeys = ["video_id", "user_id"],
    indices = [
        Index(value = ["video_id"]),
        Index(value = ["user_id", "created_at_ms"])
    ]
)
data class FeedVideoLikeEntity(
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "idempotency_key") val idempotencyKey: String,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments_count")
data class FeedVideoCommentsCountEntity(
    @PrimaryKey
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "comments_count") val commentsCount: Long = 0L,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "shares",
    indices = [
        Index(value = ["video_id"]),
        Index(value = ["user_id", "created_at_ms"])
    ]
)
data class FeedVideoShareEntity(
    @PrimaryKey
    @ColumnInfo(name = "share_id") val shareId: String,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "share_channel") val shareChannel: String, // SYSTEM, COPY_LINK, INBOX_DM, RESHARE
    @ColumnInfo(name = "target_user_id") val targetUserId: String? = null,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "saves",
    primaryKeys = ["video_id", "user_id"],
    indices = [
        Index(value = ["video_id"]),
        Index(value = ["user_id", "created_at_ms"])
    ]
)
data class FeedVideoSaveEntity(
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "hashtags",
    indices = [Index(value = ["videos_count", "views_count"])]
)
data class FeedHashtagEntity(
    @PrimaryKey
    @ColumnInfo(name = "hashtag") val hashtag: String,
    @ColumnInfo(name = "videos_count") val videosCount: Long = 1L,
    @ColumnInfo(name = "views_count") val viewsCount: Long = 0L,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "video_hashtags",
    primaryKeys = ["video_id", "hashtag"],
    indices = [
        Index(value = ["video_id"]),
        Index(value = ["hashtag"])
    ]
)
data class FeedVideoHashtagCrossRefEntity(
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "hashtag") val hashtag: String,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "user_interests",
    primaryKeys = ["user_id", "interest_key", "interest_type"],
    indices = [
        Index(value = ["user_id", "interest_type"]),
        Index(value = ["user_id", "weight_score"])
    ]
)
data class FeedUserInterestEntity(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "interest_key") val interestKey: String,
    @ColumnInfo(name = "interest_type") val interestType: String, // HASHTAG, CAPTION_TOKEN, CREATOR, LANGUAGE, NOT_INTERESTED_VIDEO
    @ColumnInfo(name = "weight_score") val weightScore: Double = 1.0,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "video_views",
    indices = [
        Index(value = ["video_id"]),
        Index(value = ["viewer_user_id", "created_at_ms"])
    ]
)
data class FeedVideoViewEntity(
    @PrimaryKey
    @ColumnInfo(name = "view_id") val viewId: String,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "viewer_user_id") val viewerUserId: String,
    @ColumnInfo(name = "watch_duration_ms") val watchDurationMs: Long = 0L,
    @ColumnInfo(name = "completed_loop") val completedLoop: Boolean = false,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_stars_balances",
    indices = [
        Index(value = ["user_id"], unique = true)
    ]
)
data class StarsBalanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "balance_stars") val balanceStars: Long = 0L,
    @ColumnInfo(name = "lifetime_stars") val lifetimeStars: Long = 0L,
    @ColumnInfo(name = "payout_account_id") val payoutAccountId: String? = null,
    @ColumnInfo(name = "payout_threshold_stars") val payoutThresholdStars: Long = 10_000L,
    @ColumnInfo(name = "last_payout_at_ms") val lastPayoutAtMs: Long? = null,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_stars_transactions",
    indices = [
        Index(value = ["recipient_user_id", "created_at_ms"]),
        Index(value = ["sender_user_id"]),
        Index(value = ["recipient_user_id", "source_type"])
    ]
)
data class StarsTransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "transaction_id") val transactionId: String,
    @ColumnInfo(name = "recipient_user_id") val recipientUserId: String,
    @ColumnInfo(name = "sender_user_id") val senderUserId: String?,
    @ColumnInfo(name = "sender_username") val senderUsername: String = "",
    @ColumnInfo(name = "sender_avatar_url") val senderAvatarUrl: String? = null,
    @ColumnInfo(name = "source_type") val sourceType: String, // LIVE, INBOX, FEED
    @ColumnInfo(name = "reference_id") val referenceId: String? = null,
    @ColumnInfo(name = "stars_amount") val starsAmount: Int,
    @ColumnInfo(name = "usd_value") val usdValue: Double = starsAmount * 0.01,
    @ColumnInfo(name = "status") val status: String = "COMPLETED", // PENDING, COMPLETED, PAID_OUT, REFUNDED
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_campaigns",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["post_id"]),
        Index(value = ["status", "expires_at_ms"])
    ]
)
data class CampaignEntity(
    @PrimaryKey
    @ColumnInfo(name = "campaign_id") val campaignId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "post_id") val postId: String,
    @ColumnInfo(name = "post_thumbnail_url") val postThumbnailUrl: String? = null,
    @ColumnInfo(name = "post_caption") val postCaption: String? = null,
    @ColumnInfo(name = "campaign_goal") val campaignGoal: String, // FOLLOWERS, VIDEO_VIEWS, WEBSITE_STORE, DIRECT_MESSAGES, FORM_APPLICATION
    @ColumnInfo(name = "target_audience_type") val targetAudienceType: String = "AUTOMATIC", // AUTOMATIC, CUSTOM
    @ColumnInfo(name = "target_gender") val targetGender: String = "ALL", // ALL, MEN, WOMEN
    @ColumnInfo(name = "target_age_range") val targetAgeRange: String = "18-54",
    @ColumnInfo(name = "target_locations") val targetLocations: String = "GLOBAL",
    @ColumnInfo(name = "target_interests") val targetInterests: String = "ALL",
    @ColumnInfo(name = "tier_id") val tierId: String, // tier_1 to tier_5
    @ColumnInfo(name = "tier_min_reach") val tierMinReach: Int,
    @ColumnInfo(name = "tier_max_reach") val tierMaxReach: Int,
    @ColumnInfo(name = "daily_budget_minor") val dailyBudgetMinor: Long,
    @ColumnInfo(name = "currency") val currency: String = "KES",
    @ColumnInfo(name = "daily_budget_display") val dailyBudgetDisplay: Double,
    @ColumnInfo(name = "duration_days") val durationDays: Int,
    @ColumnInfo(name = "total_budget_display") val totalBudgetDisplay: Double,
    @ColumnInfo(name = "cta_button_text") val ctaButtonText: String,
    @ColumnInfo(name = "destination_url") val destinationUrl: String? = null,
    @ColumnInfo(name = "whatsapp_or_chat_number") val whatsappOrChatNumber: String? = null,
    @ColumnInfo(name = "lead_form_title") val leadFormTitle: String? = null,
    @ColumnInfo(name = "lead_form_questions") val leadFormQuestions: String? = null,
    @ColumnInfo(name = "payment_gateway") val paymentGateway: String, // PAYSTACK, PAYPAL
    @ColumnInfo(name = "payment_reference") val paymentReference: String,
    @ColumnInfo(name = "payment_verified") val paymentVerified: Boolean = true,
    @ColumnInfo(name = "verified_at_ms") val verifiedAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "starts_at_ms") val startsAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "expires_at_ms") val expiresAtMs: Long,
    @ColumnInfo(name = "status") val status: String = "ACTIVE", // PENDING_PAYMENT, ACTIVE, PAUSED, COMPLETED, EXPIRED
    @ColumnInfo(name = "impressions_count") val impressionsCount: Long = 0L,
    @ColumnInfo(name = "clicks_count") val clicksCount: Long = 0L,
    @ColumnInfo(name = "conversions_count") val conversionsCount: Long = 0L,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_campaign_leads",
    indices = [
        Index(value = ["campaign_id", "created_at_ms"])
    ]
)
data class CampaignLeadEntity(
    @PrimaryKey
    @ColumnInfo(name = "lead_id") val leadId: String,
    @ColumnInfo(name = "campaign_id") val campaignId: String,
    @ColumnInfo(name = "user_id") val userId: String? = null,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "inquiry_note") val inquiryNote: String? = null,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_feed_comments",
    indices = [
        Index(value = ["target_id"]),
        Index(value = ["parent_comment_id"]),
        Index(value = ["author_user_id"]),
        Index(value = ["created_at_ms"])
    ]
)
data class FeedCommentEntity(
    @PrimaryKey
    @ColumnInfo(name = "comment_id") val commentId: String,
    @ColumnInfo(name = "target_id") val targetId: String,
    @ColumnInfo(name = "parent_comment_id") val parentCommentId: String? = null,
    @ColumnInfo(name = "author_user_id") val authorUserId: String,
    @ColumnInfo(name = "author_username") val authorUsername: String,
    @ColumnInfo(name = "author_full_name") val authorFullName: String,
    @ColumnInfo(name = "author_avatar_url") val authorAvatarUrl: String? = null,
    @ColumnInfo(name = "is_creator") val isCreator: Boolean = false,
    @ColumnInfo(name = "comment_text") val commentText: String,
    @ColumnInfo(name = "attachment_type") val attachmentType: String? = null,
    @ColumnInfo(name = "attachment_url") val attachmentUrl: String? = null,
    @ColumnInfo(name = "gift_id") val giftId: String? = null,
    @ColumnInfo(name = "gift_name") val giftName: String? = null,
    @ColumnInfo(name = "gift_coin_cost") val giftCoinCost: Long = 0L,
    @ColumnInfo(name = "gift_count") val giftCount: Int = 1,
    @ColumnInfo(name = "likes_count") val likesCount: Long = 0L,
    @ColumnInfo(name = "is_pinned") val isPinned: Boolean = false,
    @ColumnInfo(name = "is_edited") val isEdited: Boolean = false,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_feed_comment_likes",
    primaryKeys = ["comment_id", "user_id"],
    indices = [
        Index(value = ["comment_id"]),
        Index(value = ["user_id"])
    ]
)
data class FeedCommentLikeEntity(
    @ColumnInfo(name = "comment_id") val commentId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_marketplace_products",
    indices = [
        Index(value = ["seller_user_id"]),
        Index(value = ["category"]),
        Index(value = ["review_status"]),
        Index(value = ["is_sold"]),
        Index(value = ["created_at_ms"])
    ]
)
data class MarketplaceProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id") val productId: String,
    @ColumnInfo(name = "seller_user_id") val sellerUserId: String,
    @ColumnInfo(name = "seller_full_name") val sellerFullName: String,
    @ColumnInfo(name = "seller_username") val sellerUsername: String,
    @ColumnInfo(name = "seller_avatar_url") val sellerAvatarUrl: String? = null,
    @ColumnInfo(name = "seller_country") val sellerCountry: String = "Kenya",
    @ColumnInfo(name = "seller_country_iso") val sellerCountryIso: String = "KE",
    @ColumnInfo(name = "seller_location") val sellerLocation: String = "Nairobi",
    @ColumnInfo(name = "seller_response_rate") val sellerResponseRate: String = "Usually replies within 1 hour",
    @ColumnInfo(name = "seller_verified") val sellerVerified: Boolean = false,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "currency") val currency: String = "KES",
    @ColumnInfo(name = "category") val category: String = "general",
    @ColumnInfo(name = "item_condition") val itemCondition: String = "used_good",
    @ColumnInfo(name = "photo_paths_json") val photoPathsJson: String = "[]",
    @ColumnInfo(name = "specs_json") val specsJson: String = "{}",
    @ColumnInfo(name = "delivery_options_json") val deliveryOptionsJson: String = "[\"pickup\"]",
    @ColumnInfo(name = "show_exact_location") val showExactLocation: Boolean = true,
    @ColumnInfo(name = "review_status") val reviewStatus: String = "ACTIVE",
    @ColumnInfo(name = "risk_score") val riskScore: Double = 0.0,
    @ColumnInfo(name = "is_sold") val isSold: Boolean = false,
    @ColumnInfo(name = "is_expired") val isExpired: Boolean = false,
    @ColumnInfo(name = "expires_at_ms") val expiresAtMs: Long = System.currentTimeMillis() + 30L * 86400000L,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "view_count") val viewCount: Int = 0,
    @ColumnInfo(name = "favorite_count") val favoriteCount: Int = 0,
    @ColumnInfo(name = "is_pending_sync") val isPendingSync: Boolean = false
)

@Entity(
    tableName = "telefam_marketplace_favorites",
    primaryKeys = ["product_id", "user_id"],
    indices = [
        Index(value = ["product_id"]),
        Index(value = ["user_id"])
    ]
)
data class MarketplaceFavoriteEntity(
    @ColumnInfo(name = "product_id") val productId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "saved_at_ms") val savedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_marketplace_recent_views",
    primaryKeys = ["product_id", "user_id"],
    indices = [
        Index(value = ["product_id"]),
        Index(value = ["user_id"]),
        Index(value = ["viewed_at_ms"])
    ]
)
data class MarketplaceRecentViewEntity(
    @ColumnInfo(name = "product_id") val productId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "viewed_at_ms") val viewedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_marketplace_recent_searches",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["searched_at_ms"])
    ]
)
data class MarketplaceRecentSearchEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "search_id") val searchId: Long = 0L,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "query") val query: String,
    @ColumnInfo(name = "searched_at_ms") val searchedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_marketplace_reports",
    indices = [
        Index(value = ["reporter_user_id"]),
        Index(value = ["product_id"]),
        Index(value = ["seller_user_id"])
    ]
)
data class MarketplaceReportEntity(
    @PrimaryKey
    @ColumnInfo(name = "report_id") val reportId: String,
    @ColumnInfo(name = "reporter_user_id") val reporterUserId: String,
    @ColumnInfo(name = "product_id") val productId: String,
    @ColumnInfo(name = "seller_user_id") val sellerUserId: String,
    @ColumnInfo(name = "reason") val reason: String,
    @ColumnInfo(name = "details") val details: String,
    @ColumnInfo(name = "timestamp_ms") val timestampMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_linked_devices",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["session_device_id"], unique = true)
    ]
)
data class LinkedDeviceEntity(
    @PrimaryKey
    @ColumnInfo(name = "session_device_id") val sessionDeviceId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "device_name") val deviceName: String,
    @ColumnInfo(name = "device_type") val deviceType: String,
    @ColumnInfo(name = "os_platform") val osPlatform: String,
    @ColumnInfo(name = "ip_address") val ipAddress: String,
    @ColumnInfo(name = "location_label") val locationLabel: String,
    @ColumnInfo(name = "linked_at_ms") val linkedAtMs: Long,
    @ColumnInfo(name = "last_active_at_ms") val lastActiveAtMs: Long,
    @ColumnInfo(name = "is_current_device") val isCurrentDevice: Boolean = false,
    @ColumnInfo(name = "is_revoked") val isRevoked: Boolean = false
)

@Entity(
    tableName = "telefam_device_link_requests",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["link_token"], unique = true),
        Index(value = ["pairing_session_id"], unique = true)
    ]
)
data class DeviceLinkRequestEntity(
    @PrimaryKey
    @ColumnInfo(name = "pairing_session_id") val pairingSessionId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "link_token") val linkToken: String,
    @ColumnInfo(name = "qr_payload") val qrPayload: String,
    @ColumnInfo(name = "requester_device_name") val requesterDeviceName: String,
    @ColumnInfo(name = "requester_device_type") val requesterDeviceType: String,
    @ColumnInfo(name = "requester_ip") val requesterIp: String,
    @ColumnInfo(name = "requester_location") val requesterLocation: String,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long,
    @ColumnInfo(name = "token_expires_at_ms") val tokenExpiresAtMs: Long,
    @ColumnInfo(name = "approval_expires_at_ms") val approvalExpiresAtMs: Long,
    @ColumnInfo(name = "status") val status: String
)

@Entity(
    tableName = "telefam_subscription_plans",
    indices = [
        Index(value = ["creator_user_id"]),
        Index(value = ["plan_id"], unique = true)
    ]
)
data class SubscriptionPlanEntity(
    @PrimaryKey
    @ColumnInfo(name = "plan_id") val planId: String,
    @ColumnInfo(name = "creator_user_id") val creatorUserId: String,
    @ColumnInfo(name = "plan_name") val planName: String,
    @ColumnInfo(name = "monthly_price") val monthlyPrice: Double,
    @ColumnInfo(name = "currency") val currency: String = "USD",
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "perks_json") val perksJson: String = "[]",
    @ColumnInfo(name = "subscribers_count") val subscribersCount: Int = 0,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_creator_subscribers",
    indices = [
        Index(value = ["creator_user_id"]),
        Index(value = ["subscriber_user_id"]),
        Index(value = ["plan_id"])
    ]
)
data class CreatorSubscriberEntity(
    @PrimaryKey
    @ColumnInfo(name = "subscription_id") val subscriptionId: String,
    @ColumnInfo(name = "creator_user_id") val creatorUserId: String,
    @ColumnInfo(name = "subscriber_user_id") val subscriberUserId: String,
    @ColumnInfo(name = "subscriber_name") val subscriberName: String,
    @ColumnInfo(name = "subscriber_username") val subscriberUsername: String,
    @ColumnInfo(name = "subscriber_avatar_url") val subscriberAvatarUrl: String = "",
    @ColumnInfo(name = "plan_id") val planId: String,
    @ColumnInfo(name = "plan_name") val planName: String,
    @ColumnInfo(name = "price_paid") val pricePaid: Double,
    @ColumnInfo(name = "currency") val currency: String = "USD",
    @ColumnInfo(name = "status") val status: String = "ACTIVE", // ACTIVE, CANCELED, EXPIRED
    @ColumnInfo(name = "started_at_ms") val startedAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "expires_at_ms") val expiresAtMs: Long = System.currentTimeMillis() + 30L * 86_400_000L,
    @ColumnInfo(name = "is_auto_renew") val isAutoRenew: Boolean = true
)

@Entity(
    tableName = "telefam_wallet_balance",
    indices = [Index(value = ["user_id"], unique = true)]
)
data class WalletBalanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "total_balance") val totalBalance: Double = 0.0,
    @ColumnInfo(name = "available_balance") val availableBalance: Double = 0.0,
    @ColumnInfo(name = "pending_balance") val pendingBalance: Double = 0.0,
    @ColumnInfo(name = "currency") val currency: String = "USD",
    @ColumnInfo(name = "is_hidden") val isHidden: Boolean = false,
    @ColumnInfo(name = "last_updated_at_ms") val lastUpdatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_wallet_transactions",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["type"]),
        Index(value = ["created_at_ms"])
    ]
)
data class WalletTransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "transaction_id") val transactionId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "type") val type: String, // ALL, EARNINGS, PAYOUTS, ADJUSTMENTS
    @ColumnInfo(name = "category") val category: String, // STARS, SUBSCRIPTIONS, AD_REVENUE, OTHER, WITHDRAWAL, REFUND
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "currency") val currency: String = "USD",
    @ColumnInfo(name = "status") val status: String = "COMPLETED", // COMPLETED, PENDING, FAILED
    @ColumnInfo(name = "reference_id") val referenceId: String = "",
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_payout_accounts",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["is_default"])
    ]
)
data class PayoutAccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "payout_account_id") val payoutAccountId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "account_type") val accountType: String, // BANK_ACCOUNT, MPESA, PAYPAL
    @ColumnInfo(name = "institution_name") val institutionName: String,
    @ColumnInfo(name = "account_holder_name") val accountHolderName: String,
    @ColumnInfo(name = "account_number_encrypted") val accountNumberEncrypted: String,
    @ColumnInfo(name = "account_number_masked") val accountNumberMasked: String,
    @ColumnInfo(name = "routing_or_swift_encrypted") val routingOrSwiftEncrypted: String = "",
    @ColumnInfo(name = "country_iso") val countryIso: String = "US",
    @ColumnInfo(name = "currency") val currency: String = "USD",
    @ColumnInfo(name = "is_default") val isDefault: Boolean = true,
    @ColumnInfo(name = "is_verified") val isVerified: Boolean = true,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_payout_records",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["status"]),
        Index(value = ["requested_at_ms"])
    ]
)
data class PayoutRecordEntity(
    @PrimaryKey
    @ColumnInfo(name = "payout_id") val payoutId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "payout_account_id") val payoutAccountId: String,
    @ColumnInfo(name = "institution_name") val institutionName: String,
    @ColumnInfo(name = "account_masked") val accountMasked: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "fee") val fee: Double = 0.0,
    @ColumnInfo(name = "net_amount") val netAmount: Double,
    @ColumnInfo(name = "currency") val currency: String = "USD",
    @ColumnInfo(name = "status") val status: String = "PAID", // PAID, PENDING, FAILED
    @ColumnInfo(name = "failure_reason") val failureReason: String = "",
    @ColumnInfo(name = "reference_code") val referenceCode: String,
    @ColumnInfo(name = "requested_at_ms") val requestedAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "completed_at_ms") val completedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_creator_monetization",
    indices = [Index(value = ["status"])]
)
data class CreatorMonetizationEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "status") val status: String = "NOT_APPLIED", // NOT_APPLIED, SUBMITTED, APPROVED, NOT_APPROVED
    @ColumnInfo(name = "submitted_at_ms") val submittedAtMs: Long? = null,
    @ColumnInfo(name = "reviewed_at_ms") val reviewedAtMs: Long? = null,
    @ColumnInfo(name = "primary_category") val primaryCategory: String = "Entertainment",
    @ColumnInfo(name = "statement_note") val statementNote: String = "",
    @ColumnInfo(name = "contact_email") val contactEmail: String = "",
    @ColumnInfo(name = "agreed_terms") val agreedTerms: Boolean = false,
    @ColumnInfo(name = "rejection_reasons_csv") val rejectionReasonsCsv: String = "",
    @ColumnInfo(name = "reapply_allowed_at_ms") val reapplyAllowedAtMs: Long = 0L,
    @ColumnInfo(name = "gifts_enabled") val giftsEnabled: Boolean = true,
    @ColumnInfo(name = "subscriptions_enabled") val subscriptionsEnabled: Boolean = true,
    @ColumnInfo(name = "creator_rewards_enabled") val creatorRewardsEnabled: Boolean = true,
    @ColumnInfo(name = "sponsored_content_enabled") val sponsoredContentEnabled: Boolean = true,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_video_monetization",
    indices = [
        Index(value = ["creator_user_id"]),
        Index(value = ["is_monetized"])
    ]
)
data class VideoMonetizationEntity(
    @PrimaryKey
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "creator_user_id") val creatorUserId: String,
    @ColumnInfo(name = "is_monetized") val isMonetized: Boolean = true,
    @ColumnInfo(name = "instream_ads_enabled") val instreamAdsEnabled: Boolean = true,
    @ColumnInfo(name = "video_gifts_enabled") val videoGiftsEnabled: Boolean = true,
    @ColumnInfo(name = "creator_rewards_eligible") val creatorRewardsEligible: Boolean = true,
    @ColumnInfo(name = "monetized_views") val monetizedViews: Long = 0L,
    @ColumnInfo(name = "ad_impressions") val adImpressions: Long = 0L,
    @ColumnInfo(name = "ad_earnings_usd") val adEarningsUsd: Double = 0.0,
    @ColumnInfo(name = "reward_earnings_usd") val rewardEarningsUsd: Double = 0.0,
    @ColumnInfo(name = "gift_earnings_usd") val giftEarningsUsd: Double = 0.0,
    @ColumnInfo(name = "rpm_usd") val rpmUsd: Double = 2.45,
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

// =============================================================================
// GROUP CHATS, MEMBERS, JOIN REQUESTS & AUDIT LOGS TABLES
// =============================================================================

@Entity(
    tableName = "telefam_groups",
    indices = [
        Index(value = ["owner_user_id"]),
        Index(value = ["is_public"]),
        Index(value = ["created_at_ms"])
    ]
)
data class GroupChatEntity(
    @PrimaryKey
    @ColumnInfo(name = "group_id") val groupId: String,
    @ColumnInfo(name = "owner_user_id") val ownerUserId: String,
    @ColumnInfo(name = "group_name") val groupName: String,
    @ColumnInfo(name = "group_description") val groupDescription: String = "",
    @ColumnInfo(name = "avatar_url") val avatarUrl: String? = null,
    @ColumnInfo(name = "is_public") val isPublic: Boolean = true,
    @ColumnInfo(name = "invite_link") val inviteLink: String = "",
    @ColumnInfo(name = "who_can_send_messages") val whoCanSendMessages: String = "ALL_MEMBERS",
    @ColumnInfo(name = "who_can_add_members") val whoCanAddMembers: String = "ALL_MEMBERS",
    @ColumnInfo(name = "who_can_pin_messages") val whoCanPinMessages: String = "ONLY_ADMINS",
    @ColumnInfo(name = "who_can_share_message") val whoCanShareMessage: String = "ALL_MEMBERS",
    @ColumnInfo(name = "allow_copy_messages") val allowCopyMessages: Boolean = true,
    @ColumnInfo(name = "allow_screenshots") val allowScreenshots: Boolean = true,
    @ColumnInfo(name = "allow_download_media") val allowDownloadMedia: Boolean = true,
    @ColumnInfo(name = "requires_join_approval") val requiresJoinApproval: Boolean = false,
    @ColumnInfo(name = "is_history_visible") val isHistoryVisible: Boolean = true,
    @ColumnInfo(name = "slow_mode_seconds") val slowModeSeconds: Int = 0,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at_ms") val updatedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_group_members",
    indices = [
        Index(value = ["group_id", "user_id"], unique = true),
        Index(value = ["group_id", "role"]),
        Index(value = ["user_id"])
    ]
)
data class GroupMemberEntity(
    @PrimaryKey
    @ColumnInfo(name = "member_id") val memberId: String,
    @ColumnInfo(name = "group_id") val groupId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "user_full_name") val userFullName: String,
    @ColumnInfo(name = "user_username") val userUsername: String,
    @ColumnInfo(name = "user_avatar_url") val userAvatarUrl: String? = null,
    @ColumnInfo(name = "role") val role: String = "MEMBER",
    @ColumnInfo(name = "is_online") val isOnline: Boolean = false,
    @ColumnInfo(name = "last_seen_ms") val lastSeenMs: Long? = null,
    @ColumnInfo(name = "is_muted") val isMuted: Boolean = false,
    @ColumnInfo(name = "muted_until_ms") val mutedUntilMs: Long? = null,
    @ColumnInfo(name = "is_banned") val isBanned: Boolean = false,
    @ColumnInfo(name = "joined_at_ms") val joinedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_group_join_requests",
    indices = [
        Index(value = ["group_id", "user_id"], unique = true),
        Index(value = ["group_id", "status"])
    ]
)
data class GroupJoinRequestEntity(
    @PrimaryKey
    @ColumnInfo(name = "request_id") val requestId: String,
    @ColumnInfo(name = "group_id") val groupId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "user_full_name") val userFullName: String,
    @ColumnInfo(name = "user_username") val userUsername: String,
    @ColumnInfo(name = "user_avatar_url") val userAvatarUrl: String? = null,
    @ColumnInfo(name = "status") val status: String = "PENDING",
    @ColumnInfo(name = "requested_at_ms") val requestedAtMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_group_audit_logs",
    indices = [
        Index(value = ["group_id", "timestamp_ms"])
    ]
)
data class GroupAuditLogEntity(
    @PrimaryKey
    @ColumnInfo(name = "log_id") val logId: String,
    @ColumnInfo(name = "group_id") val groupId: String,
    @ColumnInfo(name = "actor_user_id") val actorUserId: String,
    @ColumnInfo(name = "actor_name") val actorName: String,
    @ColumnInfo(name = "action_text") val actionText: String,
    @ColumnInfo(name = "timestamp_ms") val timestampMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telefam_memories",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["memory_date_ms"]),
        Index(value = ["is_favorite"]),
        Index(value = ["created_at_ms"])
    ]
)
data class MemoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "memory_id") val memoryId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "caption") val caption: String = "",
    @ColumnInfo(name = "media_uri") val mediaUri: String,
    @ColumnInfo(name = "media_type") val mediaType: String = "IMAGE", // IMAGE, VIDEO
    @ColumnInfo(name = "memory_date_ms") val memoryDateMs: Long,
    @ColumnInfo(name = "created_at_ms") val createdAtMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "location_name") val locationName: String = "",
    @ColumnInfo(name = "tag_feeling") val tagFeeling: String = "",
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "privacy") val privacy: String = "ONLY_ME" // ONLY_ME, FRIENDS, PUBLIC
)

@Entity(
    tableName = "telefam_call_history",
    indices = [
        Index(value = ["user_id", "timestamp_ms"]),
        Index(value = ["peer_user_id"])
    ]
)
data class CallRecordEntity(
    @PrimaryKey
    @ColumnInfo(name = "call_id") val callId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "peer_user_id") val peerUserId: String,
    @ColumnInfo(name = "peer_full_name") val peerFullName: String,
    @ColumnInfo(name = "peer_username") val peerUsername: String,
    @ColumnInfo(name = "peer_avatar_url") val peerAvatarUrl: String? = null,
    @ColumnInfo(name = "call_type") val callType: String, // AUDIO, VIDEO
    @ColumnInfo(name = "direction") val direction: String, // INCOMING, OUTGOING, MISSED, DECLINED
    @ColumnInfo(name = "timestamp_ms") val timestampMs: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "duration_seconds") val durationSeconds: Long = 0L,
    @ColumnInfo(name = "network_quality_summary") val networkQualitySummary: String = "Good Connection",
    @ColumnInfo(name = "is_p2p_direct") val isP2pDirect: Boolean = true
)
