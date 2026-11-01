package com.telefam.company.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TelefamDao {

    // --- Users ---
    @Query("SELECT * FROM telefam_users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM telefam_users WHERE user_id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    // --- Profiles & Username Uniqueness ---
    @Query("SELECT * FROM telefam_profiles WHERE user_id = :userId LIMIT 1")
    suspend fun getProfileByUserId(userId: String): ProfileEntity?

    @Query("SELECT * FROM telefam_profiles WHERE user_id = :userId LIMIT 1")
    fun observeProfileByUserId(userId: String): Flow<ProfileEntity?>

    @Query("SELECT * FROM telefam_profiles WHERE is_pending_sync = 1 ORDER BY updated_at_ms ASC")
    suspend fun getPendingSyncProfiles(): List<ProfileEntity>

    @Query("UPDATE telefam_profiles SET is_pending_sync = 0, last_synced_at_ms = :syncedAtMs WHERE user_id = :userId")
    suspend fun markProfileSynced(userId: String, syncedAtMs: Long)

    @Query("SELECT * FROM telefam_profiles WHERE LOWER(username) = LOWER(:username) AND user_id != :excludeUserId LIMIT 1")
    suspend fun findOtherProfileWithUsername(username: String, excludeUserId: String): ProfileEntity?

    @Query("SELECT LOWER(username) FROM telefam_profiles WHERE user_id != :excludeUserId")
    suspend fun getAllTakenUsernamesExcept(excludeUserId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: ProfileEntity)

    // --- Media Fingerprints Vault (SHA-256 & 64-bit Perceptual dHash Reuse) ---
    @Query("SELECT * FROM telefam_media_fingerprints WHERE sha256_checksum = :sha256 OR perceptual_dhash = :dHash LIMIT 1")
    suspend fun findMediaByFingerprint(sha256: String, dHash: String): MediaFingerprintEntity?

    @Query("SELECT * FROM telefam_media_fingerprints ORDER BY last_reused_at_ms DESC LIMIT 12")
    suspend fun getRecentFingerprintedMedia(): List<MediaFingerprintEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMediaFingerprint(entity: MediaFingerprintEntity)

    // --- Trusted Devices ---
    @Query("SELECT * FROM telefam_trusted_devices WHERE user_id = :userId AND is_trusted = 1 ORDER BY last_seen_ms DESC")
    suspend fun getTrustedDevicesForUser(userId: String): List<TrustedDeviceEntity>

    @Query("SELECT * FROM telefam_trusted_devices WHERE user_id = :userId AND is_trusted = 1 ORDER BY last_seen_ms DESC")
    fun observeTrustedDevicesForUser(userId: String): Flow<List<TrustedDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTrustedDevice(device: TrustedDeviceEntity)

    @Query("DELETE FROM telefam_trusted_devices WHERE device_record_id = :deviceRecordId AND user_id = :userId")
    suspend fun deleteTrustedDevice(deviceRecordId: String, userId: String)

    // --- OTP Challenges & Expiration Invalidation ---
    @Query("UPDATE telefam_otp_challenges SET is_invalidated = 1 WHERE is_invalidated = 0 AND expires_at_ms <= :nowMs")
    suspend fun invalidateExpiredOtps(nowMs: Long)

    @Query("UPDATE telefam_otp_challenges SET is_invalidated = 1 WHERE LOWER(email) = LOWER(:email) AND is_invalidated = 0")
    suspend fun invalidateActiveOtpsForEmail(email: String)

    @Query("SELECT * FROM telefam_otp_challenges WHERE LOWER(email) = LOWER(:email) AND purpose = :purpose AND is_invalidated = 0 ORDER BY created_at_ms DESC LIMIT 1")
    suspend fun getLatestActiveOtp(email: String, purpose: String): OtpChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtpChallenge(challenge: OtpChallengeEntity)

    // --- Encrypted Sessions ---
    @Query("UPDATE telefam_encrypted_sessions SET is_revoked = 1 WHERE is_revoked = 0 AND refresh_expires_at_ms <= :nowMs")
    suspend fun invalidateExpiredSessions(nowMs: Long)

    @Query("UPDATE telefam_encrypted_sessions SET is_revoked = 1 WHERE user_id = :userId")
    suspend fun revokeSessionsForUser(userId: String)

    @Query("SELECT * FROM telefam_encrypted_sessions WHERE is_revoked = 0 AND refresh_expires_at_ms > :nowMs ORDER BY created_at_ms DESC LIMIT 1")
    suspend fun getLatestActiveSession(nowMs: Long = 0L): EncryptedSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncryptedSession(session: EncryptedSessionEntity)

    // --- IP & User Rate Limits ---
    @Query("SELECT * FROM telefam_rate_limits WHERE bucket_key = :bucketKey LIMIT 1")
    suspend fun getRateLimitBucket(bucketKey: String): RateLimitBucketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRateLimitBucket(bucket: RateLimitBucketEntity)

    // --- Forensic Audit Logs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForensicAuditLog(log: ForensicAuditLogEntity)

    @Query("SELECT * FROM telefam_forensic_audit_logs ORDER BY timestamp_ms DESC LIMIT 60")
    fun observeRecentForensicLogs(): Flow<List<ForensicAuditLogEntity>>

    @Query("SELECT COUNT(*) FROM telefam_forensic_audit_logs")
    suspend fun countForensicLogs(): Int

    // --- User Privacy, Chat Background Theme & Message Bubble Colour (Offline-First Room + Sync Queue) ---
    @Query("SELECT * FROM telefam_user_privacy_settings WHERE user_id = :userId LIMIT 1")
    fun observePrivacySettings(userId: String): Flow<PrivacySettingsEntity?>

    @Query("SELECT * FROM telefam_user_privacy_settings WHERE user_id = :userId LIMIT 1")
    suspend fun getPrivacySettings(userId: String): PrivacySettingsEntity?

    @Query("SELECT * FROM telefam_user_privacy_settings WHERE is_pending_sync = 1 ORDER BY updated_at_ms ASC")
    suspend fun getPendingSyncPrivacySettings(): List<PrivacySettingsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrivacySettings(settings: PrivacySettingsEntity)

    @Query("UPDATE telefam_user_privacy_settings SET is_pending_sync = 0, sync_retry_count = 0, last_synced_at_ms = :syncedAtMs WHERE user_id = :userId")
    suspend fun markPrivacySettingsSynced(userId: String, syncedAtMs: Long)

    // --- Registered Users & Profiles Discovery ---
    @Query("SELECT * FROM telefam_users WHERE user_id != :currentUserId ORDER BY created_at_ms DESC")
    suspend fun getAllOtherUsers(currentUserId: String): List<UserEntity>

    @Query("SELECT * FROM telefam_profiles WHERE user_id != :currentUserId ORDER BY updated_at_ms DESC")
    suspend fun getAllOtherProfiles(currentUserId: String): List<ProfileEntity>

    @Query("SELECT * FROM telefam_profiles ORDER BY updated_at_ms DESC")
    suspend fun getAllProfiles(): List<ProfileEntity>

    // --- Follow Graph & Idempotency ---
    @Query("SELECT * FROM telefam_follow_edges WHERE follower_user_id = :followerUserId")
    suspend fun getFollowingEdgesForUser(followerUserId: String): List<FollowEdgeEntity>

    @Query("SELECT * FROM telefam_follow_edges WHERE target_user_id = :targetUserId")
    suspend fun getFollowerEdgesForUser(targetUserId: String): List<FollowEdgeEntity>

    @Query("SELECT * FROM telefam_follow_edges")
    suspend fun getAllFollowEdges(): List<FollowEdgeEntity>

    @Query("SELECT * FROM telefam_follow_edges")
    fun observeAllFollowEdges(): Flow<List<FollowEdgeEntity>>

    @Query("SELECT * FROM telefam_follow_edges WHERE follower_user_id = :followerUserId AND target_user_id = :targetUserId LIMIT 1")
    suspend fun getFollowEdge(followerUserId: String, targetUserId: String): FollowEdgeEntity?

    @Query("SELECT * FROM telefam_follow_edges WHERE idempotency_key = :idempotencyKey LIMIT 1")
    suspend fun getFollowEdgeByIdempotencyKey(idempotencyKey: String): FollowEdgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFollowEdge(edge: FollowEdgeEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFollowEdgesIgnore(edges: List<FollowEdgeEntity>)

    @Query("DELETE FROM telefam_follow_edges WHERE follower_user_id = :followerUserId AND target_user_id = :targetUserId")
    suspend fun deleteFollowEdge(followerUserId: String, targetUserId: String)

    @Query("DELETE FROM telefam_follow_edges WHERE (follower_user_id = :userA AND target_user_id = :userB) OR (follower_user_id = :userB AND target_user_id = :userA)")
    suspend fun deleteAllFollowEdgesBetween(userA: String, userB: String)

    @Query("SELECT COUNT(*) FROM telefam_follow_edges WHERE target_user_id = :userId")
    suspend fun countFollowers(userId: String): Long

    @Query("SELECT COUNT(*) FROM telefam_follow_edges WHERE target_user_id = :userId")
    fun observeFollowersCount(userId: String): Flow<Long>

    @Query("SELECT COUNT(*) FROM telefam_follow_edges WHERE follower_user_id = :userId")
    suspend fun countFollowing(userId: String): Long

    @Query("SELECT COUNT(*) FROM telefam_follow_edges WHERE follower_user_id = :userId")
    fun observeFollowingCount(userId: String): Flow<Long>

    // --- User Subscriptions (Future Subscribers Support) ---
    @Query("SELECT * FROM telefam_user_subscriptions WHERE creator_user_id = :creatorUserId ORDER BY created_at_ms DESC")
    suspend fun getSubscribersForCreator(creatorUserId: String): List<UserSubscriptionEntity>

    @Query("SELECT COUNT(*) FROM telefam_user_subscriptions WHERE creator_user_id = :creatorUserId")
    suspend fun countSubscribers(creatorUserId: String): Long

    @Query("SELECT COUNT(*) FROM telefam_user_subscriptions WHERE creator_user_id = :creatorUserId")
    fun observeSubscribersCount(creatorUserId: String): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubscriptionsIgnore(subscriptions: List<UserSubscriptionEntity>)

    @Query("DELETE FROM telefam_user_subscriptions WHERE idempotency_key LIKE 'seed_sub_%'")
    suspend fun deleteAllSeededSubscriptions()

    // --- Contact Hashes (Phone & Email SHA-256 Discovery) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContactHashes(hashes: List<ContactHashEntity>)

    @Query("SELECT * FROM telefam_contact_hashes WHERE owner_user_id = :ownerUserId")
    suspend fun getContactHashesForOwner(ownerUserId: String): List<ContactHashEntity>

    @Query("UPDATE telefam_contact_hashes SET is_pending_sync = 0 WHERE owner_user_id = :ownerUserId")
    suspend fun markContactHashesSynced(ownerUserId: String)

    // --- User Blocks ---
    @Query("SELECT * FROM telefam_user_blocks WHERE blocker_user_id = :blockerUserId ORDER BY created_at_ms DESC")
    suspend fun getBlockedUsers(blockerUserId: String): List<UserBlockEntity>

    @Query("SELECT * FROM telefam_user_blocks WHERE blocker_user_id = :blockerUserId ORDER BY created_at_ms DESC")
    fun observeBlockedUsers(blockerUserId: String): Flow<List<UserBlockEntity>>

    @Query("SELECT * FROM telefam_user_blocks WHERE blocker_user_id = :blockerUserId ORDER BY created_at_ms DESC")
    fun observeBlocksByUser(blockerUserId: String): Flow<List<UserBlockEntity>>

    @Query("SELECT * FROM telefam_user_blocks WHERE blocked_user_id = :blockedUserId ORDER BY created_at_ms DESC")
    fun observeBlocksAgainstUser(blockedUserId: String): Flow<List<UserBlockEntity>>

    @Query("SELECT COUNT(*) FROM telefam_user_blocks WHERE (blocker_user_id = :userA AND blocked_user_id = :userB) OR (blocker_user_id = :userB AND blocked_user_id = :userA)")
    suspend fun isEitherBlocked(userA: String, userB: String): Int

    @Query("SELECT * FROM telefam_profiles WHERE user_id != :excludeUserId ORDER BY updated_at_ms DESC")
    suspend fun getAllDiscoverableProfilesExcept(excludeUserId: String): List<ProfileEntity>

    @Query("SELECT * FROM telefam_user_blocks WHERE blocker_user_id = :blockerUserId AND blocked_user_id = :blockedUserId LIMIT 1")
    suspend fun getBlockRecord(blockerUserId: String, blockedUserId: String): UserBlockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserBlock(block: UserBlockEntity)

    @Query("DELETE FROM telefam_user_blocks WHERE blocker_user_id = :blockerUserId AND blocked_user_id = :blockedUserId")
    suspend fun deleteUserBlock(blockerUserId: String, blockedUserId: String)

    // --- Multi-Question User Reports ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserReport(report: UserReportEntity)

    @Query("SELECT * FROM telefam_user_reports WHERE idempotency_key = :idempotencyKey LIMIT 1")
    suspend fun getReportByIdempotencyKey(idempotencyKey: String): UserReportEntity?

    // --- User Posts (Views & Likes) ---
    @Query("SELECT * FROM telefam_user_posts WHERE user_id = :userId ORDER BY created_at_ms DESC")
    suspend fun getPostsForUser(userId: String): List<UserPostEntity>

    @Query("SELECT * FROM telefam_user_posts WHERE user_id = :userId AND post_category = :category ORDER BY created_at_ms DESC")
    suspend fun getUserPostsByCategory(userId: String, category: String): List<UserPostEntity>

    @Query("SELECT * FROM telefam_user_posts WHERE user_id = :userId ORDER BY created_at_ms DESC")
    fun observePostsForUser(userId: String): Flow<List<UserPostEntity>>

    @Query("SELECT COALESCE(SUM(likes_count), 0) FROM telefam_user_posts WHERE user_id = :userId")
    suspend fun getTotalLikesForUser(userId: String): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserPosts(posts: List<UserPostEntity>)

    @Query("DELETE FROM telefam_user_posts WHERE post_id LIKE 'post_%' OR post_id LIKE 'owner_%'")
    suspend fun deleteAllSeededPosts()

    // --- Social Sync Queue (Silent Background Worker Outbox) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueSocialSyncOperation(item: SocialSyncQueueEntity)

    @Query("SELECT * FROM telefam_social_sync_queue ORDER BY created_at_ms ASC")
    suspend fun getPendingSocialSyncOperations(): List<SocialSyncQueueEntity>

    @Query("DELETE FROM telefam_social_sync_queue WHERE Operation_id = :operationId")
    suspend fun deleteSocialSyncOperation(operationId: String)

    @Query("UPDATE telefam_social_sync_queue SET retry_count = retry_count + 1 WHERE Operation_id = :operationId")
    suspend fun incrementSocialSyncRetryCount(operationId: String)

    // --- Account Settings, Security, 2FA, Passkey, Notifications & Content Preferences (RLS) ---
    @Query("SELECT * FROM telefam_account_security_settings WHERE owner_user_id = :ownerUserId LIMIT 1")
    suspend fun getAccountSettingsAndSecurity(ownerUserId: String): AccountSettingsAndSecurityEntity?

    @Query("SELECT * FROM telefam_account_security_settings WHERE owner_user_id = :ownerUserId LIMIT 1")
    fun observeAccountSettingsAndSecurity(ownerUserId: String): Flow<AccountSettingsAndSecurityEntity?>

    @Query("SELECT * FROM telefam_account_security_settings WHERE is_pending_sync = 1 ORDER BY updated_at_ms ASC")
    suspend fun getPendingSyncAccountSettings(): List<AccountSettingsAndSecurityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAccountSettingsAndSecurity(entity: AccountSettingsAndSecurityEntity)

    @Query("UPDATE telefam_account_security_settings SET is_pending_sync = 0, last_synced_at_ms = :syncedAtMs WHERE owner_user_id = :ownerUserId")
    suspend fun markAccountSettingsSynced(ownerUserId: String, syncedAtMs: Long)

    @Query("SELECT * FROM telefam_account_security_settings WHERE passkey_enabled = 1 ORDER BY updated_at_ms DESC LIMIT 1")
    suspend fun getLatestPasskeyEnabledSettings(): AccountSettingsAndSecurityEntity?

    @Query("DELETE FROM telefam_trusted_devices WHERE user_id = :userId AND (device_record_id LIKE '%_secondary_%' OR os_description LIKE '%iPhone%')")
    suspend fun deleteLegacyFakeDevicesForUser(userId: String)

    // --- Connected Apps (RLS) ---
    @Query("SELECT * FROM telefam_connected_apps WHERE owner_user_id = :ownerUserId AND is_revoked = 0 ORDER BY last_used_at_ms DESC")
    suspend fun getConnectedAppsForUser(ownerUserId: String): List<ConnectedAppSessionEntity>

    @Query("SELECT * FROM telefam_connected_apps WHERE owner_user_id = :ownerUserId AND is_revoked = 0 ORDER BY last_used_at_ms DESC")
    fun observeConnectedAppsForUser(ownerUserId: String): Flow<List<ConnectedAppSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConnectedApps(apps: List<ConnectedAppSessionEntity>)

    @Query("UPDATE telefam_connected_apps SET is_revoked = 1, is_pending_sync = 1 WHERE connection_id = :connectionId AND owner_user_id = :ownerUserId")
    suspend fun revokeConnectedApp(connectionId: String, ownerUserId: String)

    // --- Problem Reports & Support Tickets (RLS) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProblemSupportTicket(ticket: ProblemSupportTicketEntity)

    @Query("SELECT * FROM telefam_problem_support_tickets WHERE owner_user_id = :ownerUserId ORDER BY created_at_ms DESC")
    suspend fun getProblemSupportTicketsForUser(ownerUserId: String): List<ProblemSupportTicketEntity>

    @Query("SELECT * FROM telefam_problem_support_tickets WHERE owner_user_id = :ownerUserId ORDER BY created_at_ms DESC")
    fun observeProblemSupportTicketsForUser(ownerUserId: String): Flow<List<ProblemSupportTicketEntity>>

    @Query("UPDATE telefam_problem_support_tickets SET is_pending_sync = 0 WHERE ticket_id = :ticketId")
    suspend fun markProblemSupportTicketSynced(ticketId: String)

    @Query("SELECT * FROM telefam_problem_support_tickets WHERE ticket_id = :ticketId AND owner_user_id = :ownerUserId LIMIT 1")
    suspend fun getProblemSupportTicketById(ticketId: String, ownerUserId: String): ProblemSupportTicketEntity?

    @Query("UPDATE telefam_problem_support_tickets SET status = :status, user_follow_up_reply = :followUpReply, support_response_note = :supportNote, is_pending_sync = 1, updated_at_ms = :updatedAtMs WHERE ticket_id = :ticketId AND owner_user_id = :ownerUserId")
    suspend fun updateSupportTicketFollowUp(
        ticketId: String,
        ownerUserId: String,
        status: String,
        followUpReply: String,
        supportNote: String,
        updatedAtMs: Long
    )

    // --- Quick Login Accounts ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuickLoginAccount(account: QuickLoginAccountEntity)

    @Query("SELECT * FROM telefam_quick_login_accounts ORDER BY last_logged_out_at_ms DESC")
    suspend fun getQuickLoginAccounts(): List<QuickLoginAccountEntity>

    @Query("DELETE FROM telefam_quick_login_accounts WHERE user_id = :userId")
    suspend fun deleteQuickLoginAccount(userId: String)

    // =========================================================================
    // 1:1 CHATS, MESSAGES, REACTIONS, PINS, FAVOURITES, DISAPPEARING, RECEIPTS,
    // DRAFTS, SIGNAL KEYS/SESSIONS, OUTBOX & KLIPY CACHE
    // =========================================================================

    @Query("SELECT * FROM telefam_chats WHERE owner_user_id = :ownerUserId ORDER BY last_message_at_ms DESC, updated_at_ms DESC")
    fun observeChatsForOwner(ownerUserId: String): Flow<List<ChatEntity>>

    @Query("SELECT * FROM telefam_chats WHERE owner_user_id = :ownerUserId ORDER BY last_message_at_ms DESC, updated_at_ms DESC")
    suspend fun getChatsForOwner(ownerUserId: String): List<ChatEntity>

    @Query("SELECT * FROM telefam_chats WHERE chat_id = :chatId LIMIT 1")
    fun observeChatById(chatId: String): Flow<ChatEntity?>

    @Query("SELECT * FROM telefam_chats WHERE chat_id = :chatId LIMIT 1")
    suspend fun getChatById(chatId: String): ChatEntity?

    @Query("SELECT * FROM telefam_chats WHERE owner_user_id = :ownerUserId AND peer_user_id = :peerUserId LIMIT 1")
    suspend fun getChatByOwnerAndPeer(ownerUserId: String, peerUserId: String): ChatEntity?

    @Query("SELECT * FROM telefam_chats WHERE owner_user_id = :ownerUserId AND peer_user_id = :peerUserId LIMIT 1")
    suspend fun getChatByPeer(ownerUserId: String, peerUserId: String): ChatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChat(chat: ChatEntity)

    @Query("UPDATE telefam_chats SET unread_count = 0 WHERE chat_id = :chatId")
    suspend fun clearChatUnreadCount(chatId: String)

    @Query("UPDATE telefam_chats SET is_peer_typing = :isTyping WHERE chat_id = :chatId")
    suspend fun updateChatPeerTyping(chatId: String, isTyping: Boolean)

    @Query("UPDATE telefam_chats SET is_peer_typing = :isTyping WHERE chat_id = :chatId")
    suspend fun updatePeerTypingState(chatId: String, isTyping: Boolean)

    @Query("UPDATE telefam_chats SET is_peer_online = :isOnline, peer_last_seen_ms = :lastSeenMs WHERE chat_id = :chatId")
    suspend fun updateChatPeerPresence(chatId: String, isOnline: Boolean, lastSeenMs: Long?)

    @Query("UPDATE telefam_chats SET is_peer_online = :isOnline, peer_last_seen_ms = COALESCE(:lastSeenMs, peer_last_seen_ms) WHERE chat_id = :chatId")
    suspend fun updatePeerPresenceState(chatId: String, isOnline: Boolean, lastSeenMs: Long?)

    @Query("UPDATE telefam_chats SET is_peer_account_deleted = :isDeleted WHERE chat_id = :chatId")
    suspend fun updateChatPeerAccountDeleted(chatId: String, isDeleted: Boolean)

    @Query("UPDATE telefam_chats SET is_e2ee_verified = :verified WHERE chat_id = :chatId")
    suspend fun updateChatE2eeVerified(chatId: String, verified: Boolean)

    // --- Messages ---
    @Query("SELECT * FROM telefam_chat_messages WHERE chat_id = :chatId AND (expires_at_ms IS NULL OR expires_at_ms > :nowMs) ORDER BY created_at_ms ASC")
    fun observeMessagesForChat(chatId: String, nowMs: Long = 0L): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM telefam_chat_messages WHERE chat_id = :chatId ORDER BY created_at_ms ASC")
    suspend fun getMessagesForChat(chatId: String): List<ChatMessageEntity>

    @Query("SELECT * FROM telefam_chat_messages WHERE message_id = :messageId LIMIT 1")
    suspend fun getMessageById(messageId: String): ChatMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChatMessage(message: ChatMessageEntity)

    @Query("UPDATE telefam_chat_messages SET delivery_status = :status, delivered_at_ms = COALESCE(:deliveredAtMs, delivered_at_ms), seen_at_ms = COALESCE(:seenAtMs, seen_at_ms) WHERE message_id = :messageId")
    suspend fun updateMessageDeliveryStatus(messageId: String, status: String, deliveredAtMs: Long? = null, seenAtMs: Long? = null)

    @Query("UPDATE telefam_chat_messages SET delivery_status = 'SEEN', seen_at_ms = :seenAtMs WHERE chat_id = :chatId AND sender_user_id != :ownerUserId AND delivery_status != 'SEEN'")
    suspend fun markIncomingMessagesSeen(chatId: String, ownerUserId: String, seenAtMs: Long)

    @Query("UPDATE telefam_chat_messages SET decrypted_text = :newText, ciphertext_base64 = :newCiphertext, ratchet_header_json = :newHeader, is_edited = 1, edited_at_ms = :editedAtMs WHERE message_id = :messageId")
    suspend fun updateEditedMessage(messageId: String, newText: String, newCiphertext: String, newHeader: String, editedAtMs: Long)

    @Query("UPDATE telefam_chat_messages SET decrypted_text = :newText, ciphertext_base64 = :newCiphertext, ratchet_header_json = :newHeader, is_edited = 1, edited_at_ms = :editedAtMs WHERE message_id = :messageId")
    suspend fun updateMessageEdited(messageId: String, newText: String, newCiphertext: String, newHeader: String, editedAtMs: Long)

    @Query("UPDATE telefam_chat_messages SET media_url = :newMediaUrl, decrypted_text = :newDecryptedText, ciphertext_base64 = :newCiphertext, ratchet_header_json = :newHeader, edited_at_ms = :editedAtMs WHERE message_id = :messageId")
    suspend fun updateMessageStructuredMetadata(
        messageId: String,
        newMediaUrl: String?,
        newDecryptedText: String,
        newCiphertext: String,
        newHeader: String,
        editedAtMs: Long
    )

    @Query("UPDATE telefam_chat_messages SET is_view_once_consumed = 1, media_local_path = NULL, media_url = NULL, ciphertext_base64 = '' WHERE message_id = :messageId")
    suspend fun markViewOnceVoiceNoteConsumed(messageId: String)

    @Query("UPDATE telefam_chat_messages SET is_view_once_consumed = 1, media_local_path = NULL, media_url = NULL, ciphertext_base64 = '' WHERE message_id = :messageId")
    suspend fun markViewOnceConsumed(messageId: String)

    @Query("DELETE FROM telefam_chat_messages WHERE message_id = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("DELETE FROM telefam_chat_messages WHERE message_id = :messageId")
    suspend fun deleteMessageHard(messageId: String)

    @Query("DELETE FROM telefam_chat_messages WHERE message_id IN (:messageIds)")
    suspend fun deleteMessagesByIds(messageIds: List<String>)

    @Query("DELETE FROM telefam_chat_messages WHERE chat_id = :chatId")
    suspend fun deleteAllMessagesInChat(chatId: String)

    @Query("DELETE FROM telefam_chat_messages WHERE chat_id = :chatId")
    suspend fun deleteAllMessagesForChat(chatId: String)

    @Query("DELETE FROM telefam_chat_messages WHERE expires_at_ms IS NOT NULL AND expires_at_ms <= :nowMs")
    suspend fun purgeExpiredDisappearingMessages(nowMs: Long)

    @Query("DELETE FROM telefam_chat_messages WHERE expires_at_ms IS NOT NULL AND expires_at_ms <= :nowMs")
    suspend fun deleteExpiredMessages(nowMs: Long)

    @Query("SELECT * FROM telefam_chat_messages WHERE chat_id = :chatId ORDER BY created_at_ms DESC LIMIT 1")
    suspend fun getLatestMessageForChat(chatId: String): ChatMessageEntity?

    // --- Reactions ---
    @Query("SELECT * FROM telefam_message_reactions WHERE chat_id = :chatId ORDER BY created_at_ms ASC")
    fun observeReactionsForChat(chatId: String): Flow<List<MessageReactionEntity>>

    @Query("SELECT * FROM telefam_message_reactions WHERE message_id = :messageId AND reactor_user_id = :reactorUserId LIMIT 1")
    suspend fun getReaction(messageId: String, reactorUserId: String): MessageReactionEntity?

    @Query("SELECT * FROM telefam_message_reactions WHERE message_id = :messageId AND reactor_user_id = :reactorUserId LIMIT 1")
    suspend fun getReactionByUser(messageId: String, reactorUserId: String): MessageReactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReaction(reaction: MessageReactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessageReaction(reaction: MessageReactionEntity)

    @Query("DELETE FROM telefam_message_reactions WHERE message_id = :messageId AND reactor_user_id = :reactorUserId")
    suspend fun deleteReaction(messageId: String, reactorUserId: String)

    @Query("DELETE FROM telefam_message_reactions WHERE message_id = :messageId AND reactor_user_id = :reactorUserId")
    suspend fun deleteReactionByUser(messageId: String, reactorUserId: String)

    @Query("DELETE FROM telefam_message_reactions WHERE message_id = :messageId")
    suspend fun deleteAllReactionsForMessage(messageId: String)

    @Query("DELETE FROM telefam_message_reactions WHERE message_id = :messageId")
    suspend fun deleteReactionsForMessage(messageId: String)

    @Query("DELETE FROM telefam_message_reactions WHERE chat_id = :chatId")
    suspend fun deleteAllReactionsForChat(chatId: String)

    // --- Pins ---
    @Query("SELECT * FROM telefam_chat_pins WHERE chat_id = :chatId ORDER BY pinned_at_ms DESC")
    fun observePinsForChat(chatId: String): Flow<List<ChatPinEntity>>

    @Query("SELECT p.* FROM telefam_chat_pins p INNER JOIN telefam_chats c ON p.chat_id = c.chat_id WHERE c.owner_user_id = :ownerUserId")
    fun observeAllPinsForOwner(ownerUserId: String): Flow<List<ChatPinEntity>>

    @Query("SELECT * FROM telefam_chat_pins WHERE chat_id = :chatId AND message_id = :messageId LIMIT 1")
    suspend fun getPin(chatId: String, messageId: String): ChatPinEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPin(pin: ChatPinEntity)

    @Query("DELETE FROM telefam_chat_pins WHERE chat_id = :chatId AND message_id = :messageId")
    suspend fun deletePin(chatId: String, messageId: String)

    @Query("DELETE FROM telefam_chat_pins WHERE message_id = :messageId")
    suspend fun deletePinsForMessage(messageId: String)

    @Query("DELETE FROM telefam_chat_pins WHERE chat_id = :chatId")
    suspend fun deleteAllPinsForChat(chatId: String)

    // --- Favourites ---
    @Query("SELECT * FROM telefam_chat_favourites WHERE owner_user_id = :ownerUserId AND chat_id = :chatId ORDER BY favourited_at_ms DESC")
    fun observeFavouritesForChat(ownerUserId: String, chatId: String): Flow<List<ChatFavouriteEntity>>

    @Query("SELECT * FROM telefam_chat_favourites WHERE owner_user_id = :ownerUserId AND message_id = :messageId LIMIT 1")
    suspend fun getFavourite(ownerUserId: String, messageId: String): ChatFavouriteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavourite(favourite: ChatFavouriteEntity)

    @Query("DELETE FROM telefam_chat_favourites WHERE owner_user_id = :ownerUserId AND message_id = :messageId")
    suspend fun deleteFavourite(ownerUserId: String, messageId: String)

    @Query("DELETE FROM telefam_chat_favourites WHERE message_id = :messageId")
    suspend fun deleteFavouritesForMessage(messageId: String)

    @Query("DELETE FROM telefam_chat_favourites WHERE chat_id = :chatId")
    suspend fun deleteAllFavouritesForChat(chatId: String)

    // --- Disappearing Settings ---
    @Query("SELECT * FROM telefam_disappearing_settings WHERE chat_id = :chatId LIMIT 1")
    fun observeDisappearingSetting(chatId: String): Flow<DisappearingSettingEntity?>

    @Query("SELECT * FROM telefam_disappearing_settings WHERE chat_id = :chatId LIMIT 1")
    suspend fun getDisappearingSetting(chatId: String): DisappearingSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDisappearingSetting(setting: DisappearingSettingEntity)

    // --- Delivery Receipts ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDeliveryReceipt(receipt: DeliveryReceiptEntity)

    @Query("SELECT * FROM telefam_delivery_receipts WHERE chat_id = :chatId")
    fun observeReceiptsForChat(chatId: String): Flow<List<DeliveryReceiptEntity>>

    @Query("DELETE FROM telefam_delivery_receipts WHERE chat_id = :chatId")
    suspend fun deleteAllReceiptsForChat(chatId: String)

    // --- Drafts ---
    @Query("SELECT * FROM telefam_chat_drafts WHERE owner_user_id = :ownerUserId")
    fun observeAllDraftsForOwner(ownerUserId: String): Flow<List<ChatDraftEntity>>

    @Query("SELECT * FROM telefam_chat_drafts WHERE chat_id = :chatId LIMIT 1")
    fun observeDraftForChat(chatId: String): Flow<ChatDraftEntity?>

    @Query("SELECT * FROM telefam_chat_drafts WHERE chat_id = :chatId LIMIT 1")
    suspend fun getDraftForChat(chatId: String): ChatDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChatDraft(draft: ChatDraftEntity)

    @Query("DELETE FROM telefam_chat_drafts WHERE chat_id = :chatId")
    suspend fun deleteChatDraft(chatId: String)

    // --- Signal Keys & Double Ratchet Sessions ---
    @Query("SELECT * FROM telefam_signal_identity_keys WHERE user_id = :userId LIMIT 1")
    suspend fun getSignalIdentityKey(userId: String): SignalIdentityKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSignalIdentityKey(entity: SignalIdentityKeyEntity)

    @Query("SELECT * FROM telefam_signal_signed_prekeys WHERE user_id = :userId ORDER BY created_at_ms DESC LIMIT 1")
    suspend fun getLatestSignalSignedPreKey(userId: String): SignalSignedPreKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSignalSignedPreKey(entity: SignalSignedPreKeyEntity)

    @Query("SELECT * FROM telefam_signal_one_time_prekeys WHERE user_id = :userId AND is_consumed = 0 ORDER BY prekey_id ASC")
    suspend fun getAvailableOneTimePreKeys(userId: String): List<SignalOneTimePreKeyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSignalOneTimePreKeys(entities: List<SignalOneTimePreKeyEntity>)

    @Query("UPDATE telefam_signal_one_time_prekeys SET is_consumed = 1 WHERE user_id = :userId AND prekey_id = :prekeyId")
    suspend fun markOneTimePreKeyConsumed(userId: String, prekeyId: Int)

    @Query("SELECT * FROM telefam_signal_ratchet_sessions WHERE owner_user_id = :ownerUserId AND peer_user_id = :peerUserId LIMIT 1")
    suspend fun getSignalRatchetSession(ownerUserId: String, peerUserId: String): SignalRatchetSessionEntity?

    @Query("SELECT * FROM telefam_signal_ratchet_sessions WHERE owner_user_id = :ownerUserId AND peer_user_id = :peerUserId LIMIT 1")
    fun observeSignalRatchetSession(ownerUserId: String, peerUserId: String): Flow<SignalRatchetSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSignalRatchetSession(session: SignalRatchetSessionEntity)

    // --- Chat Outbox (WorkManager + WebSocket Reliable Queue) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueChatOutbox(item: ChatOutboxEntity)

    @Query("SELECT * FROM telefam_chat_outbox WHERE status = 'PENDING' ORDER BY created_at_ms ASC")
    suspend fun getPendingChatOutboxItems(): List<ChatOutboxEntity>

    @Query("DELETE FROM telefam_chat_outbox WHERE outbox_id = :outboxId")
    suspend fun deleteChatOutboxItem(outboxId: String)

    @Query("UPDATE telefam_chat_outbox SET status = 'TERMINAL_REJECTED', terminal_error_code = :errorCode WHERE outbox_id = :outboxId")
    suspend fun markChatOutboxTerminalRejected(outboxId: String, errorCode: String)

    @Query("UPDATE telefam_chat_outbox SET retry_count = retry_count + 1 WHERE outbox_id = :outboxId")
    suspend fun incrementChatOutboxRetryCount(outboxId: String)

    // --- Klipy Cached Items & Recents (Offline-First) ---
    @Query("SELECT * FROM telefam_klipy_cached_items WHERE media_tab = :mediaTab AND LOWER(search_query) = LOWER(:query) ORDER BY page_Order ASC")
    suspend fun getCachedKlipyItems(mediaTab: String, query: String): List<KlipyCachedItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedKlipyItems(items: List<KlipyCachedItemEntity>)

    @Query("SELECT * FROM telefam_klipy_recents WHERE owner_user_id = :ownerUserId AND media_tab = :mediaTab ORDER BY last_used_at_ms DESC LIMIT 24")
    fun observeKlipyRecents(ownerUserId: String, mediaTab: String): Flow<List<KlipyRecentItemEntity>>

    @Query("SELECT * FROM telefam_klipy_recents WHERE owner_user_id = :ownerUserId AND media_tab = :mediaTab ORDER BY last_used_at_ms DESC LIMIT 24")
    suspend fun getKlipyRecents(ownerUserId: String, mediaTab: String): List<KlipyRecentItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertKlipyRecent(item: KlipyRecentItemEntity)

    // =========================================================================
    // TIKTOK-STYLE FEEDS, VIDEOS, AUDIOS, LIKES, SAVES, SHARES, HASHTAGS,
    // USER INTERESTS, VIDEO VIEWS & SEARCH QUERIES
    // =========================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFeedVideo(video: FeedVideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFeedVideos(videos: List<FeedVideoEntity>)

    @Query("SELECT * FROM videos ORDER BY created_at_ms DESC")
    suspend fun getAllFeedVideosChronological(): List<FeedVideoEntity>

    @Query("SELECT * FROM videos ORDER BY created_at_ms DESC")
    fun observeAllFeedVideos(): Flow<List<FeedVideoEntity>>

    @Query("SELECT * FROM videos WHERE video_id = :videoId LIMIT 1")
    suspend fun getFeedVideoById(videoId: String): FeedVideoEntity?

    @Query("SELECT * FROM videos WHERE owner_user_id = :ownerUserId ORDER BY created_at_ms DESC")
    suspend fun getFeedVideosByOwner(ownerUserId: String): List<FeedVideoEntity>

    @Query("SELECT * FROM videos WHERE audio_id = :audioId ORDER BY created_at_ms DESC")
    suspend fun getFeedVideosByAudioId(audioId: String): List<FeedVideoEntity>

    @Query("SELECT v.* FROM videos v INNER JOIN video_hashtags vh ON v.video_id = vh.video_id WHERE LOWER(vh.hashtag) = LOWER(:hashtag) ORDER BY v.created_at_ms DESC")
    suspend fun getFeedVideosByHashtag(hashtag: String): List<FeedVideoEntity>

    @Query("DELETE FROM videos WHERE video_id = :videoId AND owner_user_id = :ownerUserId")
    suspend fun deleteFeedVideoByOwner(videoId: String, ownerUserId: String)

    @Query("DELETE FROM telefam_user_posts WHERE post_id = :postId AND user_id = :ownerUserId")
    suspend fun deleteUserPostByOwner(postId: String, ownerUserId: String)

    @Query("UPDATE videos SET caption = :caption, visibility_audience = :visibility, allow_download = :allowDownload, allow_comments = :allowComments WHERE video_id = :videoId AND owner_user_id = :ownerUserId")
    suspend fun updateFeedVideoMetadata(
        videoId: String,
        ownerUserId: String,
        caption: String,
        visibility: String,
        allowDownload: Boolean,
        allowComments: Boolean
    )

    // --- Video Audios ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFeedVideoAudio(audio: FeedVideoAudioEntity)

    @Query("SELECT * FROM video_audios WHERE audio_id = :audioId LIMIT 1")
    suspend fun getFeedVideoAudioById(audioId: String): FeedVideoAudioEntity?

    @Query("SELECT * FROM video_audios ORDER BY videos_count DESC, created_at_ms DESC")
    suspend fun getAllFeedVideoAudios(): List<FeedVideoAudioEntity>

    @Query("UPDATE video_audios SET videos_count = (SELECT COUNT(*) FROM videos WHERE audio_id = :audioId) WHERE audio_id = :audioId")
    suspend fun refreshAudioVideosCount(audioId: String)

    // --- Video Likes ---
    @Query("SELECT * FROM likes WHERE video_id = :videoId AND user_id = :userId LIMIT 1")
    suspend fun getFeedVideoLike(videoId: String, userId: String): FeedVideoLikeEntity?

    @Query("SELECT video_id FROM likes WHERE user_id = :userId")
    suspend fun getLikedVideoIdsByUser(userId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedVideoLike(like: FeedVideoLikeEntity)

    @Query("DELETE FROM likes WHERE video_id = :videoId AND user_id = :userId")
    suspend fun deleteFeedVideoLike(videoId: String, userId: String)

    @Query("UPDATE videos SET likes_count = MAX(0, likes_count + :delta) WHERE video_id = :videoId")
    suspend fun adjustFeedVideoLikesCount(videoId: String, delta: Long)

    @Query("UPDATE telefam_user_posts SET likes_count = MAX(0, likes_count + :delta) WHERE post_id = :postId")
    suspend fun adjustUserPostLikesCount(postId: String, delta: Long)

    // --- Video Saves / Bookmarks ---
    @Query("SELECT * FROM saves WHERE video_id = :videoId AND user_id = :userId LIMIT 1")
    suspend fun getFeedVideoSave(videoId: String, userId: String): FeedVideoSaveEntity?

    @Query("SELECT video_id FROM saves WHERE user_id = :userId")
    suspend fun getSavedVideoIdsByUser(userId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedVideoSave(save: FeedVideoSaveEntity)

    @Query("DELETE FROM saves WHERE video_id = :videoId AND user_id = :userId")
    suspend fun deleteFeedVideoSave(videoId: String, userId: String)

    @Query("UPDATE videos SET saves_count = MAX(0, saves_count + :delta) WHERE video_id = :videoId")
    suspend fun adjustFeedVideoSavesCount(videoId: String, delta: Long)

    // --- Video Shares & Comments Count ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedVideoShare(share: FeedVideoShareEntity)

    @Query("UPDATE videos SET shares_count = MAX(0, shares_count + 1) WHERE video_id = :videoId")
    suspend fun incrementFeedVideoSharesCount(videoId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFeedVideoCommentsCount(entity: FeedVideoCommentsCountEntity)

    @Query("SELECT comments_count FROM comments_count WHERE video_id = :videoId LIMIT 1")
    suspend fun getFeedVideoCommentsCount(videoId: String): Long?

    // --- Hashtags & Video Hashtag CrossRefs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFeedHashtag(entity: FeedHashtagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFeedVideoHashtagCrossRefs(refs: List<FeedVideoHashtagCrossRefEntity>)

    @Query("SELECT hashtag FROM video_hashtags WHERE video_id = :videoId")
    suspend fun getHashtagsForVideo(videoId: String): List<String>

    @Query("SELECT * FROM hashtags ORDER BY videos_count DESC, views_count DESC")
    suspend fun getAllFeedHashtags(): List<FeedHashtagEntity>

    // --- Video Views & User Interests (Recommendation Engine) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedVideoView(view: FeedVideoViewEntity)

    @Query("SELECT * FROM video_views WHERE viewer_user_id = :userId ORDER BY created_at_ms DESC LIMIT 200")
    suspend fun getRecentVideoViewsForUser(userId: String): List<FeedVideoViewEntity>

    @Query("UPDATE videos SET views_count = views_count + 1 WHERE video_id = :videoId")
    suspend fun incrementFeedVideoViewsCount(videoId: String)

    @Query("UPDATE telefam_user_posts SET views_count = views_count + 1 WHERE post_id = :postId")
    suspend fun incrementUserPostViewsCount(postId: String)

    @Query("SELECT * FROM user_interests WHERE user_id = :userId")
    suspend fun getUserInterests(userId: String): List<FeedUserInterestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserInterest(interest: FeedUserInterestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserInterests(interests: List<FeedUserInterestEntity>)

    @Query("DELETE FROM user_interests WHERE user_id = :userId")
    suspend fun clearUserInterests(userId: String)

    @Query("DELETE FROM video_views WHERE viewer_user_id = :userId")
    suspend fun clearUserVideoViews(userId: String)

    // --- Full-Text Search (Videos, Users, Hashtags, Sounds) ---
    @Query("SELECT * FROM videos WHERE LOWER(caption) LIKE '%' || LOWER(:query) || '%' ORDER BY views_count DESC, created_at_ms DESC LIMIT 50")
    suspend fun searchFeedVideosByQuery(query: String): List<FeedVideoEntity>

    @Query("SELECT * FROM hashtags WHERE LOWER(hashtag) LIKE '%' || LOWER(:query) || '%' ORDER BY videos_count DESC, views_count DESC LIMIT 30")
    suspend fun searchFeedHashtagsByQuery(query: String): List<FeedHashtagEntity>

    @Query("SELECT * FROM video_audios WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' OR LOWER(artist_name) LIKE '%' || LOWER(:query) || '%' OR LOWER(owner_username) LIKE '%' || LOWER(:query) || '%' ORDER BY videos_count DESC LIMIT 30")
    suspend fun searchFeedAudiosByQuery(query: String): List<FeedVideoAudioEntity>

    // --- Stars Balances & Transactions ---
    @Query("SELECT * FROM telefam_stars_balances WHERE user_id = :userId LIMIT 1")
    fun observeStarsBalance(userId: String): Flow<StarsBalanceEntity?>

    @Query("SELECT * FROM telefam_stars_balances WHERE user_id = :userId LIMIT 1")
    suspend fun getStarsBalance(userId: String): StarsBalanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStarsBalance(balance: StarsBalanceEntity)

    @Query("SELECT * FROM telefam_stars_transactions WHERE recipient_user_id = :recipientUserId ORDER BY created_at_ms DESC")
    fun observeStarsTransactions(recipientUserId: String): Flow<List<StarsTransactionEntity>>

    @Query("SELECT * FROM telefam_stars_transactions WHERE recipient_user_id = :recipientUserId AND source_type = :sourceType ORDER BY created_at_ms DESC")
    fun observeStarsTransactionsBySource(recipientUserId: String, sourceType: String): Flow<List<StarsTransactionEntity>>

    @Query("SELECT * FROM telefam_stars_transactions WHERE recipient_user_id = :recipientUserId ORDER BY created_at_ms DESC")
    suspend fun getStarsTransactions(recipientUserId: String): List<StarsTransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStarsTransaction(transaction: StarsTransactionEntity)

    @Query("SELECT COALESCE(SUM(stars_amount), 0) FROM telefam_stars_transactions WHERE recipient_user_id = :recipientUserId AND source_type = :sourceType")
    suspend fun getTotalStarsCountBySource(recipientUserId: String, sourceType: String): Long

    // Campaigns & Ads Management
    @Query("SELECT * FROM telefam_campaigns WHERE user_id = :userId ORDER BY created_at_ms DESC")
    fun observeUserCampaigns(userId: String): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM telefam_campaigns WHERE status = 'ACTIVE' AND expires_at_ms > :nowMs ORDER BY created_at_ms DESC")
    fun observeActiveSponsoredCampaigns(nowMs: Long): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM telefam_campaigns WHERE post_id = :postId AND status = 'ACTIVE' AND expires_at_ms > :nowMs LIMIT 1")
    suspend fun getActiveCampaignForPost(postId: String, nowMs: Long): CampaignEntity?

    @Query("SELECT * FROM telefam_campaigns WHERE post_id = :postId AND status = 'ACTIVE' AND expires_at_ms > :nowMs LIMIT 1")
    fun observeActiveCampaignForPost(postId: String, nowMs: Long): Flow<CampaignEntity?>

    @Query("SELECT * FROM telefam_campaigns WHERE user_id = :userId ORDER BY created_at_ms DESC")
    suspend fun getUserCampaigns(userId: String): List<CampaignEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: CampaignEntity)

    @Query("UPDATE telefam_campaigns SET status = :status WHERE campaign_id = :campaignId")
    suspend fun updateCampaignStatus(campaignId: String, status: String)

    @Query("UPDATE telefam_campaigns SET impressions_count = impressions_count + 1 WHERE campaign_id = :campaignId")
    suspend fun incrementCampaignImpressions(campaignId: String)

    @Query("UPDATE telefam_campaigns SET clicks_count = clicks_count + 1 WHERE campaign_id = :campaignId")
    suspend fun incrementCampaignClicks(campaignId: String)

    @Query("UPDATE telefam_campaigns SET conversions_count = conversions_count + 1 WHERE campaign_id = :campaignId")
    suspend fun incrementCampaignConversions(campaignId: String)

    @Query("DELETE FROM telefam_campaigns WHERE campaign_id = :campaignId")
    suspend fun deleteCampaign(campaignId: String)

    // Campaign Leads
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaignLead(lead: CampaignLeadEntity)

    @Query("SELECT * FROM telefam_campaign_leads WHERE campaign_id = :campaignId ORDER BY created_at_ms DESC")
    fun observeCampaignLeads(campaignId: String): Flow<List<CampaignLeadEntity>>

    @Query("SELECT * FROM telefam_campaign_leads WHERE campaign_id = :campaignId ORDER BY created_at_ms DESC")
    suspend fun getCampaignLeads(campaignId: String): List<CampaignLeadEntity>

    // --- Comments & Threaded Replies System ---
    @Query("SELECT * FROM telefam_feed_comments WHERE target_id = :targetId AND parent_comment_id IS NULL ORDER BY is_pinned DESC, created_at_ms DESC")
    fun observeTopLevelComments(targetId: String): Flow<List<FeedCommentEntity>>

    @Query("SELECT * FROM telefam_feed_comments WHERE target_id = :targetId AND parent_comment_id IS NULL ORDER BY is_pinned DESC, created_at_ms DESC")
    suspend fun getTopLevelComments(targetId: String): List<FeedCommentEntity>

    @Query("SELECT * FROM telefam_feed_comments WHERE parent_comment_id = :parentCommentId ORDER BY created_at_ms ASC")
    fun observeRepliesForComment(parentCommentId: String): Flow<List<FeedCommentEntity>>

    @Query("SELECT * FROM telefam_feed_comments WHERE parent_comment_id = :parentCommentId ORDER BY created_at_ms ASC")
    suspend fun getRepliesForComment(parentCommentId: String): List<FeedCommentEntity>

    @Query("SELECT COUNT(*) FROM telefam_feed_comments WHERE target_id = :targetId")
    suspend fun getTotalCommentsCount(targetId: String): Long

    @Query("SELECT COUNT(*) FROM telefam_feed_comments WHERE target_id = :targetId")
    fun observeTotalCommentsCount(targetId: String): Flow<Long>

    @Query("SELECT * FROM telefam_feed_comments WHERE comment_id = :commentId LIMIT 1")
    suspend fun getCommentById(commentId: String): FeedCommentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: FeedCommentEntity)

    @Update
    suspend fun updateComment(comment: FeedCommentEntity)

    @Query("DELETE FROM telefam_feed_comments WHERE comment_id = :commentId OR parent_comment_id = :commentId")
    suspend fun deleteCommentAndReplies(commentId: String)

    @Query("UPDATE telefam_feed_comments SET is_pinned = 0 WHERE target_id = :targetId")
    suspend fun unpinAllCommentsForTarget(targetId: String)

    @Query("UPDATE telefam_feed_comments SET is_pinned = :isPinned WHERE comment_id = :commentId")
    suspend fun setCommentPinned(commentId: String, isPinned: Boolean)

    @Query("UPDATE telefam_feed_comments SET comment_text = :newText, is_edited = 1, updated_at_ms = :now WHERE comment_id = :commentId")
    suspend fun editCommentText(commentId: String, newText: String, now: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentLike(like: FeedCommentLikeEntity)

    @Query("DELETE FROM telefam_feed_comment_likes WHERE comment_id = :commentId AND user_id = :userId")
    suspend fun deleteCommentLike(commentId: String, userId: String)

    @Query("SELECT COUNT(*) > 0 FROM telefam_feed_comment_likes WHERE comment_id = :commentId AND user_id = :userId")
    suspend fun isCommentLikedByUser(commentId: String, userId: String): Boolean

    @Query("UPDATE telefam_feed_comments SET likes_count = likes_count + 1 WHERE comment_id = :commentId")
    suspend fun incrementCommentLikesCount(commentId: String)

    @Query("UPDATE telefam_feed_comments SET likes_count = CASE WHEN likes_count > 0 THEN likes_count - 1 ELSE 0 END WHERE comment_id = :commentId")
    suspend fun decrementCommentLikesCount(commentId: String)

    // =========================================================================
    // MARKETPLACE PRODUCTS, FAVORITES, RECENTS, SEARCHES & REPORTS
    // =========================================================================

    @Query("""
        SELECT * FROM telefam_marketplace_products 
        WHERE review_status = 'ACTIVE' 
          AND seller_user_id NOT IN (SELECT blocked_user_id FROM telefam_user_blocks WHERE blocker_user_id = :currentUserId)
        ORDER BY created_at_ms DESC
    """)
    fun observeActiveMarketplaceProducts(currentUserId: String): Flow<List<MarketplaceProductEntity>>

    @Query("""
        SELECT * FROM telefam_marketplace_products 
        WHERE review_status = 'ACTIVE' 
          AND (:category = 'all' OR category = :category)
          AND seller_user_id NOT IN (SELECT blocked_user_id FROM telefam_user_blocks WHERE blocker_user_id = :currentUserId)
        ORDER BY created_at_ms DESC
    """)
    fun observeMarketplaceProductsByCategory(currentUserId: String, category: String): Flow<List<MarketplaceProductEntity>>

    @Query("SELECT * FROM telefam_marketplace_products WHERE seller_user_id = :sellerUserId ORDER BY created_at_ms DESC")
    fun observeSellerProducts(sellerUserId: String): Flow<List<MarketplaceProductEntity>>

    @Query("""
        SELECT p.* FROM telefam_marketplace_products p
        INNER JOIN telefam_marketplace_favorites f ON p.product_id = f.product_id
        WHERE f.user_id = :userId
        ORDER BY f.saved_at_ms DESC
    """)
    fun observeFavoriteProducts(userId: String): Flow<List<MarketplaceProductEntity>>

    @Query("""
        SELECT p.* FROM telefam_marketplace_products p
        INNER JOIN telefam_marketplace_recent_views v ON p.product_id = v.product_id
        WHERE v.user_id = :userId
        ORDER BY v.viewed_at_ms DESC
        LIMIT 20
    """)
    fun observeRecentlyViewedProducts(userId: String): Flow<List<MarketplaceProductEntity>>

    @Query("SELECT * FROM telefam_marketplace_products WHERE product_id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): MarketplaceProductEntity?

    @Query("SELECT * FROM telefam_marketplace_products WHERE product_id = :productId LIMIT 1")
    fun observeProductById(productId: String): Flow<MarketplaceProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduct(product: MarketplaceProductEntity)

    @Query("DELETE FROM telefam_marketplace_products WHERE product_id = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("UPDATE telefam_marketplace_products SET is_sold = :isSold, review_status = CASE WHEN :isSold = 1 THEN 'SOLD' ELSE 'ACTIVE' END, updated_at_ms = :now WHERE product_id = :productId")
    suspend fun setProductSold(productId: String, isSold: Boolean, now: Long = System.currentTimeMillis())

    @Query("UPDATE telefam_marketplace_products SET expires_at_ms = :newExpiresAtMs, is_expired = 0, review_status = 'ACTIVE', updated_at_ms = :now WHERE product_id = :productId")
    suspend fun renewProduct(productId: String, newExpiresAtMs: Long, now: Long = System.currentTimeMillis())

    @Query("UPDATE telefam_marketplace_products SET view_count = view_count + 1 WHERE product_id = :productId")
    suspend fun incrementProductViews(productId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: MarketplaceFavoriteEntity)

    @Query("DELETE FROM telefam_marketplace_favorites WHERE product_id = :productId AND user_id = :userId")
    suspend fun deleteFavorite(productId: String, userId: String)

    @Query("SELECT COUNT(*) > 0 FROM telefam_marketplace_favorites WHERE product_id = :productId AND user_id = :userId")
    suspend fun isProductFavorited(productId: String, userId: String): Boolean

    @Query("UPDATE telefam_marketplace_products SET favorite_count = favorite_count + 1 WHERE product_id = :productId")
    suspend fun incrementProductFavoritesCount(productId: String)

    @Query("UPDATE telefam_marketplace_products SET favorite_count = CASE WHEN favorite_count > 0 THEN favorite_count - 1 ELSE 0 END WHERE product_id = :productId")
    suspend fun decrementProductFavoritesCount(productId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentView(view: MarketplaceRecentViewEntity)

    @Query("SELECT * FROM telefam_marketplace_recent_searches WHERE user_id = :userId ORDER BY searched_at_ms DESC LIMIT 15")
    fun observeRecentSearches(userId: String): Flow<List<MarketplaceRecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(search: MarketplaceRecentSearchEntity)

    @Query("DELETE FROM telefam_marketplace_recent_searches WHERE user_id = :userId")
    suspend fun clearRecentSearches(userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplaceReport(report: MarketplaceReportEntity)

    @Query("SELECT * FROM telefam_marketplace_products WHERE seller_user_id = :sellerUserId AND LOWER(title) = LOWER(:title) LIMIT 5")
    suspend fun findSimilarListings(sellerUserId: String, title: String): List<MarketplaceProductEntity>

    @Query("UPDATE telefam_account_security_settings SET is_marketplace_suspended = :isSuspended, marketplace_suspension_reason = :reason, updated_at_ms = :now WHERE owner_user_id = :ownerUserId")
    suspend fun setMarketplaceSuspension(ownerUserId: String, isSuspended: Boolean, reason: String, now: Long = System.currentTimeMillis())

    // --- Linked Devices & Device Link Pairing Requests ---
    @Query("SELECT * FROM telefam_linked_devices WHERE user_id = :userId AND is_revoked = 0 ORDER BY is_current_device DESC, last_active_at_ms DESC")
    fun observeLinkedDevicesForUser(userId: String): Flow<List<LinkedDeviceEntity>>

    @Query("SELECT * FROM telefam_linked_devices WHERE user_id = :userId AND is_revoked = 0 ORDER BY is_current_device DESC, last_active_at_ms DESC")
    suspend fun getLinkedDevicesForUser(userId: String): List<LinkedDeviceEntity>

    @Query("SELECT * FROM telefam_linked_devices WHERE user_id = :userId ORDER BY is_current_device DESC, last_active_at_ms DESC")
    fun observeAllLinkedDevicesIncludingRevoked(userId: String): Flow<List<LinkedDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLinkedDevice(device: LinkedDeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLinkedDevices(devices: List<LinkedDeviceEntity>)

    @Query("UPDATE telefam_linked_devices SET is_revoked = 1 WHERE session_device_id = :sessionDeviceId AND user_id = :userId")
    suspend fun revokeLinkedDevice(sessionDeviceId: String, userId: String)

    @Query("UPDATE telefam_linked_devices SET is_revoked = 1 WHERE user_id = :userId AND is_current_device = 0")
    suspend fun revokeAllOtherLinkedDevices(userId: String)

    @Query("SELECT * FROM telefam_linked_devices WHERE session_device_id = :sessionDeviceId LIMIT 1")
    suspend fun getLinkedDeviceBySessionId(sessionDeviceId: String): LinkedDeviceEntity?

    @Query("SELECT * FROM telefam_device_link_requests WHERE user_id = :userId AND status = 'PENDING' AND approval_expires_at_ms > :nowMs ORDER BY created_at_ms DESC LIMIT 1")
    fun observePendingLinkRequestForUser(userId: String, nowMs: Long): Flow<DeviceLinkRequestEntity?>

    @Query("SELECT * FROM telefam_device_link_requests WHERE user_id = :userId AND status = 'PENDING' AND approval_expires_at_ms > :nowMs ORDER BY created_at_ms DESC LIMIT 1")
    suspend fun getPendingLinkRequestForUser(userId: String, nowMs: Long): DeviceLinkRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinkRequest(request: DeviceLinkRequestEntity)

    @Query("SELECT * FROM telefam_device_link_requests WHERE (link_token = :tokenOrPayload OR qr_payload = :tokenOrPayload OR pairing_session_id = :tokenOrPayload) LIMIT 1")
    suspend fun findLinkRequestByTokenOrQr(tokenOrPayload: String): DeviceLinkRequestEntity?

    @Query("UPDATE telefam_device_link_requests SET status = :status WHERE pairing_session_id = :pairingSessionId")
    suspend fun updateLinkRequestStatus(pairingSessionId: String, status: String)

    @Query("UPDATE telefam_device_link_requests SET status = 'EXPIRED' WHERE status = 'PENDING' AND approval_expires_at_ms <= :nowMs")
    suspend fun expireOldLinkRequests(nowMs: Long)

    // =========================================================================
    // CREATOR SUBSCRIPTIONS DAO
    // =========================================================================

    @Query("SELECT * FROM telefam_subscription_plans WHERE creator_user_id = :creatorUserId ORDER BY monthly_price ASC")
    fun observeSubscriptionPlans(creatorUserId: String): Flow<List<SubscriptionPlanEntity>>

    @Query("SELECT * FROM telefam_subscription_plans WHERE creator_user_id = :creatorUserId ORDER BY monthly_price ASC")
    suspend fun getSubscriptionPlans(creatorUserId: String): List<SubscriptionPlanEntity>

    @Query("SELECT * FROM telefam_subscription_plans WHERE plan_id = :planId LIMIT 1")
    suspend fun getSubscriptionPlanById(planId: String): SubscriptionPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSubscriptionPlan(plan: SubscriptionPlanEntity)

    @Query("DELETE FROM telefam_subscription_plans WHERE plan_id = :planId")
    suspend fun deleteSubscriptionPlan(planId: String)

    @Query("SELECT * FROM telefam_creator_subscribers WHERE creator_user_id = :creatorUserId ORDER BY started_at_ms DESC")
    fun observeCreatorSubscribers(creatorUserId: String): Flow<List<CreatorSubscriberEntity>>

    @Query("SELECT * FROM telefam_creator_subscribers WHERE creator_user_id = :creatorUserId ORDER BY started_at_ms DESC")
    suspend fun getCreatorSubscribers(creatorUserId: String): List<CreatorSubscriberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCreatorSubscriber(subscriber: CreatorSubscriberEntity)

    @Query("SELECT COUNT(*) FROM telefam_creator_subscribers WHERE creator_user_id = :creatorUserId AND status = :status")
    suspend fun countSubscribersByStatus(creatorUserId: String, status: String): Int

    @Query("SELECT COUNT(*) FROM telefam_creator_subscribers WHERE creator_user_id = :creatorUserId AND started_at_ms >= :sinceMs")
    suspend fun countNewSubscribersSince(creatorUserId: String, sinceMs: Long): Int

    @Query("SELECT COALESCE(SUM(price_paid), 0.0) FROM telefam_creator_subscribers WHERE creator_user_id = :creatorUserId AND started_at_ms >= :sinceMs")
    suspend fun sumSubscriptionEarningsSince(creatorUserId: String, sinceMs: Long): Double

    @Query("SELECT COALESCE(SUM(price_paid), 0.0) FROM telefam_creator_subscribers WHERE creator_user_id = :creatorUserId")
    suspend fun sumTotalSubscriptionEarnings(creatorUserId: String): Double

    // =========================================================================
    // CREATOR WALLET & PAYOUTS DAO
    // =========================================================================

    @Query("SELECT * FROM telefam_wallet_balance WHERE user_id = :userId LIMIT 1")
    fun observeWalletBalance(userId: String): Flow<WalletBalanceEntity?>

    @Query("SELECT * FROM telefam_wallet_balance WHERE user_id = :userId LIMIT 1")
    suspend fun getWalletBalance(userId: String): WalletBalanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWalletBalance(balance: WalletBalanceEntity)

    @Query("UPDATE telefam_wallet_balance SET is_hidden = :isHidden WHERE user_id = :userId")
    suspend fun updateWalletBalanceHidden(userId: String, isHidden: Boolean)

    @Query("SELECT * FROM telefam_wallet_transactions WHERE user_id = :userId ORDER BY created_at_ms DESC")
    fun observeWalletTransactions(userId: String): Flow<List<WalletTransactionEntity>>

    @Query("SELECT * FROM telefam_wallet_transactions WHERE user_id = :userId AND (:filterType = 'ALL' OR type = :filterType) ORDER BY created_at_ms DESC")
    fun observeWalletTransactionsFiltered(userId: String, filterType: String): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletTransaction(tx: WalletTransactionEntity)

    @Query("SELECT * FROM telefam_payout_accounts WHERE user_id = :userId ORDER BY is_default DESC, created_at_ms DESC")
    fun observePayoutAccounts(userId: String): Flow<List<PayoutAccountEntity>>

    @Query("SELECT * FROM telefam_payout_accounts WHERE user_id = :userId AND is_default = 1 LIMIT 1")
    suspend fun getDefaultPayoutAccount(userId: String): PayoutAccountEntity?

    @Query("SELECT * FROM telefam_payout_accounts WHERE user_id = :userId ORDER BY is_default DESC, created_at_ms DESC LIMIT 1")
    suspend fun getFirstPayoutAccount(userId: String): PayoutAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPayoutAccount(account: PayoutAccountEntity)

    @Query("UPDATE telefam_payout_accounts SET is_default = 0 WHERE user_id = :userId")
    suspend fun clearDefaultPayoutAccounts(userId: String)

    @Query("UPDATE telefam_payout_accounts SET is_default = 1 WHERE payout_account_id = :payoutAccountId AND user_id = :userId")
    suspend fun setDefaultPayoutAccount(userId: String, payoutAccountId: String)

    @Query("DELETE FROM telefam_payout_accounts WHERE payout_account_id = :payoutAccountId AND user_id = :userId")
    suspend fun deletePayoutAccount(payoutAccountId: String, userId: String)

    @Query("SELECT * FROM telefam_payout_records WHERE user_id = :userId ORDER BY requested_at_ms DESC")
    fun observePayoutRecords(userId: String): Flow<List<PayoutRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayoutRecord(record: PayoutRecordEntity)

    @Query("UPDATE telefam_payout_records SET status = :status WHERE payout_id = :payoutId")
    suspend fun updatePayoutRecordStatus(payoutId: String, status: String)

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM telefam_wallet_transactions WHERE user_id = :userId AND category = :category AND created_at_ms >= :sinceMs")
    suspend fun sumEarningsByCategorySince(userId: String, category: String, sinceMs: Long): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM telefam_payout_records WHERE user_id = :userId")
    suspend fun sumTotalPayoutsAllTime(userId: String): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM telefam_payout_records WHERE user_id = :userId AND status = :status")
    suspend fun sumPayoutsByStatus(userId: String, status: String): Double

    @Query("SELECT COUNT(*) FROM telefam_payout_records WHERE user_id = :userId AND status = :status")
    suspend fun countPayoutsByStatus(userId: String, status: String): Int

    // ==========================================
    // CREATOR MONETIZATION & CONTENT EARNINGS
    // ==========================================

    @Query("SELECT * FROM telefam_creator_monetization WHERE user_id = :userId LIMIT 1")
    suspend fun getCreatorMonetization(userId: String): CreatorMonetizationEntity?

    @Query("SELECT * FROM telefam_creator_monetization WHERE user_id = :userId LIMIT 1")
    fun observeCreatorMonetization(userId: String): Flow<CreatorMonetizationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCreatorMonetization(entity: CreatorMonetizationEntity)

    @Query("UPDATE telefam_creator_monetization SET status = :status, reviewed_at_ms = :reviewedAtMs WHERE user_id = :userId")
    suspend fun updateCreatorMonetizationStatus(userId: String, status: String, reviewedAtMs: Long = System.currentTimeMillis())

    @Query("SELECT * FROM telefam_video_monetization WHERE creator_user_id = :creatorUserId ORDER BY updated_at_ms DESC")
    fun observeVideoMonetizationsForCreator(creatorUserId: String): Flow<List<VideoMonetizationEntity>>

    @Query("SELECT * FROM telefam_video_monetization WHERE video_id = :videoId LIMIT 1")
    suspend fun getVideoMonetization(videoId: String): VideoMonetizationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVideoMonetization(entity: VideoMonetizationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVideoMonetizations(entities: List<VideoMonetizationEntity>)

    @Query("SELECT COUNT(*) FROM videos WHERE owner_user_id = :creatorUserId")
    suspend fun getCreatorPublicVideosCount(creatorUserId: String): Long

    @Query("SELECT COALESCE(SUM(views_count), 0) FROM videos WHERE owner_user_id = :creatorUserId")
    suspend fun getCreatorTotalViews(creatorUserId: String): Long

    // ==========================================
    // GROUP CHATS, MEMBERS, REQUESTS & AUDIT LOGS
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupChatEntity)

    @Update
    suspend fun updateGroup(group: GroupChatEntity)

    @Query("SELECT * FROM telefam_groups WHERE group_id = :groupId LIMIT 1")
    suspend fun getGroupById(groupId: String): GroupChatEntity?

    @Query("SELECT * FROM telefam_groups WHERE group_id = :groupId LIMIT 1")
    fun observeGroup(groupId: String): Flow<GroupChatEntity?>

    @Query("SELECT * FROM telefam_groups WHERE is_public = 1 ORDER BY created_at_ms DESC")
    suspend fun getAllPublicGroups(): List<GroupChatEntity>

    @Query("DELETE FROM telefam_groups WHERE group_id = :groupId")
    suspend fun deleteGroup(groupId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMembers(members: List<GroupMemberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMember(member: GroupMemberEntity)

    @Query("SELECT * FROM telefam_group_members WHERE group_id = :groupId AND is_banned = 0 ORDER BY CASE role WHEN 'OWNER' THEN 1 WHEN 'ADMIN' THEN 2 WHEN 'MODERATOR' THEN 3 ELSE 4 END, joined_at_ms ASC")
    suspend fun getGroupMembers(groupId: String): List<GroupMemberEntity>

    @Query("SELECT * FROM telefam_group_members WHERE group_id = :groupId AND is_banned = 0 ORDER BY CASE role WHEN 'OWNER' THEN 1 WHEN 'ADMIN' THEN 2 WHEN 'MODERATOR' THEN 3 ELSE 4 END, joined_at_ms ASC")
    fun observeGroupMembers(groupId: String): Flow<List<GroupMemberEntity>>

    @Query("SELECT * FROM telefam_group_members WHERE group_id = :groupId AND user_id = :userId LIMIT 1")
    suspend fun getGroupMember(groupId: String, userId: String): GroupMemberEntity?

    @Query("UPDATE telefam_group_members SET role = :newRole WHERE group_id = :groupId AND user_id = :userId")
    suspend fun updateGroupMemberRole(groupId: String, userId: String, newRole: String)

    @Query("UPDATE telefam_group_members SET is_muted = :isMuted, muted_until_ms = :mutedUntilMs WHERE group_id = :groupId AND user_id = :userId")
    suspend fun updateGroupMemberMute(groupId: String, userId: String, isMuted: Boolean, mutedUntilMs: Long?)

    @Query("DELETE FROM telefam_group_members WHERE group_id = :groupId AND user_id = :userId")
    suspend fun removeGroupMember(groupId: String, userId: String)

    @Query("SELECT COUNT(*) FROM telefam_group_members WHERE group_id = :groupId AND is_banned = 0")
    suspend fun getGroupMemberCount(groupId: String): Int

    @Query("SELECT COUNT(*) FROM telefam_group_members WHERE group_id = :groupId AND is_banned = 1")
    suspend fun getGroupBannedCount(groupId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJoinRequest(request: GroupJoinRequestEntity)

    @Query("SELECT * FROM telefam_group_join_requests WHERE group_id = :groupId AND status = 'PENDING' ORDER BY requested_at_ms DESC")
    suspend fun getPendingJoinRequests(groupId: String): List<GroupJoinRequestEntity>

    @Query("SELECT * FROM telefam_group_join_requests WHERE group_id = :groupId AND status = 'PENDING' ORDER BY requested_at_ms DESC")
    fun observePendingJoinRequests(groupId: String): Flow<List<GroupJoinRequestEntity>>

    @Query("UPDATE telefam_group_join_requests SET status = :status WHERE request_id = :requestId")
    suspend fun updateJoinRequestStatus(requestId: String, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: GroupAuditLogEntity)

    @Query("SELECT * FROM telefam_group_audit_logs WHERE group_id = :groupId ORDER BY timestamp_ms DESC LIMIT :limit")
    suspend fun getGroupAuditLogs(groupId: String, limit: Int = 50): List<GroupAuditLogEntity>

    // ==========================================
    // MEMORIES DAO
    // ==========================================

    @Query("SELECT * FROM telefam_memories WHERE user_id = :userId ORDER BY memory_date_ms DESC")
    fun observeMemories(userId: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM telefam_memories WHERE user_id = :userId ORDER BY memory_date_ms DESC")
    suspend fun getMemories(userId: String): List<MemoryEntity>

    @Query("SELECT * FROM telefam_memories WHERE memory_id = :memoryId LIMIT 1")
    suspend fun getMemoryById(memoryId: String): MemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Query("UPDATE telefam_memories SET is_favorite = :isFavorite WHERE memory_id = :memoryId")
    suspend fun updateMemoryFavorite(memoryId: String, isFavorite: Boolean)

    @Query("DELETE FROM telefam_memories WHERE memory_id = :memoryId")
    suspend fun deleteMemory(memoryId: String)

    // ==========================================
    // CREATOR SUBSCRIBER LOOKUP & CANCEL
    // ==========================================

    @Query("SELECT * FROM telefam_creator_subscribers WHERE creator_user_id = :creatorUserId AND subscriber_user_id = :subscriberUserId LIMIT 1")
    fun observeSubscription(creatorUserId: String, subscriberUserId: String): Flow<CreatorSubscriberEntity?>

    @Query("SELECT * FROM telefam_creator_subscribers WHERE creator_user_id = :creatorUserId AND subscriber_user_id = :subscriberUserId LIMIT 1")
    suspend fun getSubscription(creatorUserId: String, subscriberUserId: String): CreatorSubscriberEntity?

    @Query("DELETE FROM telefam_creator_subscribers WHERE creator_user_id = :creatorUserId AND subscriber_user_id = :subscriberUserId")
    suspend fun deleteSubscription(creatorUserId: String, subscriberUserId: String)

    // ==========================================
    // CALL HISTORY DAO (History Only - No Media)
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallRecord(record: CallRecordEntity)

    @Query("SELECT * FROM telefam_call_history WHERE user_id = :userId ORDER BY timestamp_ms DESC")
    fun observeCallHistory(userId: String): Flow<List<CallRecordEntity>>

    @Query("SELECT * FROM telefam_call_history WHERE user_id = :userId ORDER BY timestamp_ms DESC LIMIT :limit")
    suspend fun getRecentCalls(userId: String, limit: Int = 100): List<CallRecordEntity>

    @Query("DELETE FROM telefam_call_history WHERE call_id = :callId")
    suspend fun deleteCallRecord(callId: String)

    @Query("DELETE FROM telefam_call_history WHERE user_id = :userId")
    suspend fun clearCallHistory(userId: String)
}

