package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.telefam.company.app.data.AuthActionOutcome
import com.telefam.company.app.data.TelefamAppDatabase
import com.telefam.company.app.data.TelefamSecurityRepository
import com.telefam.company.app.data.TelefamSettingsRepository
import com.telefam.company.app.data.TelefamSocialRepository
import com.telefam.company.app.data.UsernameConflictException
import com.telefam.company.app.domain.AppThemeOption
import com.telefam.company.app.domain.DeviceAndNetworkTelemetry
import com.telefam.company.app.domain.PrivacyAudience
import com.telefam.company.app.domain.PrivacyRuleKey
import com.telefam.company.app.domain.RiskLevel
import com.telefam.company.app.domain.TelefamBubbleColors
import com.telefam.company.app.domain.TelefamChatThemes
import com.telefam.company.app.data.ProfileEntity
import com.telefam.company.app.data.StarsBalanceEntity
import com.telefam.company.app.domain.VirtualGiftCatalog
import com.telefam.company.app.domain.TelefamSettingsCatalog
import com.telefam.company.app.security.AndroidSandboxMediaProcessor
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private fun sampleTelemetry(deviceHash: String = "server_dev_hash_primary"): DeviceAndNetworkTelemetry {
        return DeviceAndNetworkTelemetry(
            untrustedClientDeviceFingerprint = "untrusted_client_fp_1234567890",
            serverVerifiedDeviceHash = deviceHash,
            ipSubnetFingerprint = "ip_subnet_fp_ke_254",
            clientIpAddress = "197.248.16.42",
            osDescription = "Android 16 • Telefam Test Device",
            screenBucket = "COMPACT_PHONE",
            localeTag = "en-KE",
            locationLabel = "Kenya • Africa/Nairobi",
            latitude = -1.2921,
            longitude = 36.8219,
            riskScore = 10,
            riskLevel = RiskLevel.LOW,
            riskSignals = listOf("VERIFIED_TELEMETRY_NORMAL")
        )
    }

    @Test
    fun `verify username uniqueness suggestions optional photo and 13 plus age rule`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, TelefamAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = TelefamSecurityRepository(context, db.telefamDao())
        val telemetry = sampleTelemetry()

        // 1. First user saves profile WITHOUT optional photo
        val firstSave = repo.saveProfileSetup(
            userId = "user_1",
            email = "amina.wanjiku@telefam.company",
            fullName = "Amina Wanjiku",
            username = "amina_wanjiku",
            phoneCountryCode = "+254",
            phoneIsoCode = "KE",
            phoneNumber = "712345678",
            gender = "Female",
            dateOfBirth = "1998-06-15",
            bio = "Building on Telefam",
            sanitizedAvatar = null, // Optional photo omitted
            telemetry = telemetry
        )
        assertTrue(firstSave.isSuccess)

        // 2. Second user attempting same username gets UsernameConflictException with unique suggestions
        val secondSaveCollision = repo.saveProfileSetup(
            userId = "user_2",
            email = "amina.wanjiku@telefam.company",
            fullName = "Amina Wanjiku",
            username = "amina_wanjiku",
            phoneCountryCode = "+254",
            phoneIsoCode = "KE",
            phoneNumber = "799887766",
            gender = "Female",
            dateOfBirth = "1999-04-20",
            bio = "Second Amina",
            sanitizedAvatar = null,
            telemetry = telemetry
        )
        assertTrue(secondSaveCollision.isFailure)
        val conflict = secondSaveCollision.exceptionOrNull() as? UsernameConflictException
        assertNotNull(conflict)
        assertTrue(conflict!!.suggestions.isNotEmpty())
        assertFalse(conflict.suggestions.contains("amina_wanjiku"))

        // 3. Under-13 Date of Birth is rejected
        val under13Save = repo.saveProfileSetup(
            userId = "user_3",
            email = "young@telefam.company",
            fullName = "Young User",
            username = "young_user_tf",
            phoneCountryCode = "+254",
            phoneIsoCode = "KE",
            phoneNumber = "711223344",
            gender = "Male",
            dateOfBirth = "2020-01-01",
            bio = "Too young",
            sanitizedAvatar = null,
            telemetry = telemetry
        )
        assertTrue(under13Save.isFailure)

        // 4. Photo fingerprinting computes 64-bit dHash and reuses existing asset on second upload
        val bmp = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
        val firstCrop = AndroidSandboxMediaProcessor.cropAndSanitizeInSandbox(
            context = context,
            sourceBitmap = bmp,
            scale = 1f,
            offsetXNormalized = 0f,
            offsetYNormalized = 0f,
            rotationDegrees = 0f,
            dao = db.telefamDao()
        ).getOrThrow()
        assertFalse(firstCrop.reusedFromFingerprint)
        assertEquals(16, firstCrop.perceptualDHash.length)

        val secondCropReused = AndroidSandboxMediaProcessor.cropAndSanitizeInSandbox(
            context = context,
            sourceBitmap = bmp,
            scale = 1f,
            offsetXNormalized = 0f,
            offsetYNormalized = 0f,
            rotationDegrees = 0f,
            dao = db.telefamDao()
        ).getOrThrow()
        assertTrue(secondCropReused.reusedFromFingerprint)

        // 5. Verify Encrypted Session Vault & Offline Launch to Home + Token Refresh Preservation
        val (offlineSession, offlineProfile) = repo.getActiveSessionWithTelemetry(
            screenWidthDp = 411,
            locationTriple = Triple("Kenya • Africa/Nairobi", -1.2921, 36.8219),
            simulateAnomaly = false
        )
        assertNotNull(offlineSession)
        assertNotNull(offlineProfile)
        assertTrue(offlineSession!!.hasCompletedProfile)
        assertEquals("amina_wanjiku", offlineProfile!!.username)
        assertTrue(offlineSession.refreshTokenHash.isNotBlank())

        // 6. Verify 6 Non-Human Chat Background Themes, Default Red Bubble Color First, and Offline-First Room Privacy Rules
        assertEquals(6, TelefamChatThemes.ALL.size)
        assertEquals("#C80815", TelefamBubbleColors.ALL.first().colorHex)
        val initialPrivacy = repo.getOrCreatePrivacySettings("user_1")
        assertEquals("ANYONE", initialPrivacy.messageRequests)
        assertEquals("#C80815", initialPrivacy.bubbleColorHex)
        assertEquals("crimson_silk", initialPrivacy.chatThemeId)

        val updatedRule = repo.updatePrivacyRuleOfflineFirst(
            userId = "user_1",
            ruleKey = PrivacyRuleKey.WHO_CAN_SCREENSHOT_CHATS,
            audience = PrivacyAudience.NOBODY,
            telemetry = telemetry
        )
        assertEquals("NOBODY", updatedRule.whoCanScreenshotChats)

        val updatedTheme = repo.updateChatBackgroundThemeOfflineFirst(
            userId = "user_1",
            themeId = "aurora_cosmos",
            telemetry = telemetry
        )
        assertEquals("aurora_cosmos", updatedTheme.chatThemeId)

        val updatedBubble = repo.updateMessageBubbleColorOfflineFirst(
            userId = "user_1",
            bubbleColorHex = "#1565C0",
            telemetry = telemetry
        )
        assertEquals("#1565C0", updatedBubble.bubbleColorHex)

        db.close()
    }

    @Test
    fun `verify settings integration passkey login 2FA real device qr and support flows`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, TelefamAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.telefamDao()
        val securityRepo = TelefamSecurityRepository(context, dao)
        val socialRepo = TelefamSocialRepository(context, dao)
        val settingsRepo = TelefamSettingsRepository(context, dao, securityRepo, socialRepo)
        val telemetry = sampleTelemetry()

        // 1. Create account & profile
        val email = "enterprise.security@telefam.company"
        val signUpOutcome = securityRepo.initiateSignUpWithOtp(email, "StrongPass#2026", telemetry)
        assertTrue(signUpOutcome is AuthActionOutcome.OtpDispatched)
        val otpChallenge = dao.getLatestActiveOtp(email, "SIGNUP")
        assertNotNull(otpChallenge)

        // Complete profile for user
        val createdUser = dao.getUserByEmail(email)!!
        dao.upsertUser(createdUser.copy(emailVerified = true))
        val userId = createdUser.userId
        securityRepo.saveProfileSetup(
            userId = userId,
            email = email,
            fullName = "Enterprise Member",
            username = "enterprise_member",
            phoneCountryCode = "+254",
            phoneIsoCode = "KE",
            phoneNumber = "712000111",
            gender = "Prefer not to say",
            dateOfBirth = "1994-03-10",
            bio = "Enterprise account",
            sanitizedAvatar = null,
            telemetry = telemetry
        )

        // 2. Verify only real current device is registered (no fake devices)
        val initialSettings = settingsRepo.getOrCreateAccountSettings(
            userId = userId,
            email = email,
            phone = "+254712000111",
            telemetry = telemetry
        )
        val devices = dao.getTrustedDevicesForUser(userId)
        assertEquals(1, devices.size)
        assertEquals(telemetry.serverVerifiedDeviceHash, devices.first().serverDeviceHash)

        // 3. Register passkey and verify passkey sign-in works
        val passkeyUpdated = settingsRepo.registerFido2Passkey(
            current = initialSettings,
            deviceLabel = telemetry.osDescription,
            telemetry = telemetry
        )
        assertTrue(passkeyUpdated.passkeyEnabled)
        assertTrue(passkeyUpdated.passkeyCredentialId.startsWith("pk_"))

        val passkeyAuthOutcome = securityRepo.authenticateWithPasskey(email, telemetry)
        assertTrue(passkeyAuthOutcome is AuthActionOutcome.Authenticated)

        // 4. Verify backend QR code generation & token persistence
        val qrBitmap = settingsRepo.issueAndGenerateBackendProfileQrCode(
            userId = userId,
            username = "enterprise_member",
            fullName = "Enterprise Member",
            sizePx = 360
        )
        assertEquals(360, qrBitmap.width)
        val refreshedSettings = dao.getAccountSettingsAndSecurity(userId)!!
        assertTrue(refreshedSettings.backendQrPayloadToken.isNotBlank())

        // 5. Verify theme switching persistence (Light / Dark / System)
        val darkSaved = settingsRepo.saveAndSyncAccountSettings(
            updated = refreshedSettings.copy(appThemeMode = AppThemeOption.DARK.wireCode),
            telemetry = telemetry,
            auditAction = "Appearance -> DARK"
        )
        assertEquals("DARK", darkSaved.appThemeMode)

        // 6. Verify legal policy acknowledgement & help article feedback
        val doc = TelefamSettingsCatalog.LEGAL_DOCUMENTS.first()
        val afterPolicyAck = settingsRepo.acknowledgeLegalPolicy(
            current = darkSaved,
            policyId = doc.id,
            policyTitle = doc.title,
            policyVersion = doc.version,
            telemetry = telemetry
        )
        assertTrue(afterPolicyAck.acceptedPoliciesCsv.contains("${doc.id}:"))

        val article = TelefamSettingsCatalog.HELP_ARTICLES.first()
        val afterHelpFeedback = settingsRepo.recordHelpArticleFeedback(
            current = afterPolicyAck,
            articleId = article.id,
            helpful = true,
            telemetry = telemetry
        )
        assertTrue(afterHelpFeedback.helpfulArticlesCsv.contains("${article.id}:YES"))

        // 7. Verify support ticket submission, follow-up, and resolution
        val ticketResult = settingsRepo.submitProblemOrSupportTicket(
            ownerUserId = userId,
            ticketType = "SUPPORT_REQUEST",
            category = "Account",
            subject = "Verification inquiry",
            description = "Testing support ticket lifecycle and follow-up.",
            screenshotUri = null,
            telemetry = telemetry
        )
        assertTrue(ticketResult.isSuccess)
        val createdTicket = ticketResult.getOrThrow()

        val followUpResult = settingsRepo.updateSupportTicketFollowUpOrStatus(
            ownerUserId = userId,
            ticketId = createdTicket.ticketId,
            followUpReply = "Additional diagnostic details attached.",
            markResolved = false,
            telemetry = telemetry
        )
        assertTrue(followUpResult.isSuccess)
        assertEquals("IN_REVIEW", followUpResult.getOrThrow().status)

        val resolvedResult = settingsRepo.updateSupportTicketFollowUpOrStatus(
            ownerUserId = userId,
            ticketId = createdTicket.ticketId,
            followUpReply = "",
            markResolved = true,
            telemetry = telemetry
        )
        assertTrue(resolvedResult.isSuccess)
        assertEquals("RESOLVED", resolvedResult.getOrThrow().status)

        // 8. Verify Owner Menu Screen localized resources and Lucide SVG icons
        assertEquals("Menu", context.getString(R.string.owner_menu_title))
        assertEquals("Dashboard", context.getString(R.string.owner_menu_dashboard))
        assertEquals("Stars", context.getString(R.string.owner_menu_stars))
        assertEquals("Subscription", context.getString(R.string.owner_menu_subscription))
        assertEquals("Monetization", context.getString(R.string.owner_menu_monetization))
        assertEquals("Wallet", context.getString(R.string.owner_menu_wallet))
        assertEquals("Linked device", context.getString(R.string.owner_menu_linked_device))
        assertEquals("Create campaign", context.getString(R.string.owner_menu_create_campaign))
        assertEquals("Get Verified", context.getString(R.string.owner_menu_get_verified))
        assertEquals("Live Stream", context.getString(R.string.owner_menu_live_stream))
        assertEquals("Market", context.getString(R.string.owner_menu_market))
        assertEquals("Groups", context.getString(R.string.owner_menu_groups))
        assertEquals("Memories", context.getString(R.string.owner_menu_memories))
        assertNotNull(com.telefam.company.app.ui.screens.LucideOwnerMenuIcons.BarChart)
        assertNotNull(com.telefam.company.app.ui.screens.LucideOwnerMenuIcons.Star)
        assertNotNull(com.telefam.company.app.ui.screens.LucideOwnerMenuIcons.Crown)
        assertNotNull(com.telefam.company.app.ui.screens.LucideOwnerMenuIcons.DollarBadge)
        assertNotNull(com.telefam.company.app.ui.screens.LucideOwnerMenuIcons.Wallet)
        assertNotNull(com.telefam.company.app.ui.screens.LucideOwnerMenuIcons.LinkedDevices)
        assertNotNull(com.telefam.company.app.ui.screens.LucideOwnerMenuIcons.Megaphone)
        assertNotNull(com.telefam.company.app.ui.screens.LucideOwnerMenuIcons.BadgeCheck)
        assertNotNull(com.telefam.company.app.ui.screens.LucideOwnerMenuIcons.RadioLive)

        // 9. Verify Professional Dashboard & Analytics localized resources and real calculation
        assertEquals("Professional Dashboard", context.getString(R.string.dashboard_screen_title))
        assertEquals("Analytics", context.getString(R.string.dashboard_analytics_title))
        assertEquals("Overview", context.getString(R.string.dashboard_tab_overview))
        assertEquals("Content", context.getString(R.string.dashboard_tab_content))
        assertEquals("Audience", context.getString(R.string.dashboard_tab_audience))
        assertNotNull(com.telefam.company.app.ui.screens.LucideDashboardIcons.BarChart)
        assertNotNull(com.telefam.company.app.ui.screens.LucideDashboardIcons.VideoSquare)

        val dashboardMetrics = socialRepo.getOwnerDashboardMetrics(userId, com.telefam.company.app.domain.DashboardPeriodOption.LAST_28_DAYS)
        assertNotNull(dashboardMetrics)
        assertTrue(dashboardMetrics.totalViews >= 0L)
        assertTrue(dashboardMetrics.totalLikes >= 0L)

        // 10. Verify Stars Earnings System resources, SVG icons, Room persistence, and channel breakdown
        assertEquals("Stars", context.getString(R.string.stars_screen_title))
        assertEquals("About Stars", context.getString(R.string.stars_dialog_title))
        assertEquals("Live Streams", context.getString(R.string.stars_dialog_live_title))
        assertEquals("Direct Inbox", context.getString(R.string.stars_dialog_inbox_title))
        assertEquals("Feeds & Posts", context.getString(R.string.stars_dialog_feeds_title))
        assertEquals("Monetization & Payouts", context.getString(R.string.stars_dialog_payout_title))

        assertNotNull(com.telefam.company.app.ui.screens.LucideStarsIcons.ArrowLeft)
        assertNotNull(com.telefam.company.app.ui.screens.LucideStarsIcons.CircleHelp)
        assertNotNull(com.telefam.company.app.ui.screens.LucideStarsIcons.StarFilled)
        assertNotNull(com.telefam.company.app.ui.screens.LucideStarsIcons.Wallet)
        assertNotNull(com.telefam.company.app.ui.screens.LucideStarsIcons.RadioLive)
        assertNotNull(com.telefam.company.app.ui.screens.LucideStarsIcons.MessageSquare)
        assertNotNull(com.telefam.company.app.ui.screens.LucideStarsIcons.Clapperboard)

        // Verify Stars Balance & Breakdown queries
        val initialBalance = socialRepo.getStarsBalance(userId)
        assertNotNull(initialBalance)
        assertEquals(0L, initialBalance.balanceStars)

        // Test inserting real transactions across Live, Inbox, and Feed channels
        db.telefamDao().insertStarsTransaction(
            com.telefam.company.app.data.StarsTransactionEntity(
                transactionId = "tx_live_001",
                recipientUserId = userId,
                senderUserId = "user_fan_01",
                senderUsername = "alice_fan",
                sourceType = "LIVE",
                starsAmount = 100,
                usdValue = 1.00
            )
        )
        db.telefamDao().insertStarsTransaction(
            com.telefam.company.app.data.StarsTransactionEntity(
                transactionId = "tx_inbox_001",
                recipientUserId = userId,
                senderUserId = "user_fan_02",
                senderUsername = "bob_supporter",
                sourceType = "INBOX",
                starsAmount = 50,
                usdValue = 0.50
            )
        )
        db.telefamDao().insertStarsTransaction(
            com.telefam.company.app.data.StarsTransactionEntity(
                transactionId = "tx_feed_001",
                recipientUserId = userId,
                senderUserId = "user_fan_03",
                senderUsername = "charlie_creator",
                sourceType = "FEED",
                starsAmount = 200,
                usdValue = 2.00
            )
        )

        val breakdown = socialRepo.getStarsBreakdown(userId)
        assertEquals(100L, breakdown.liveStars)
        assertEquals(50L, breakdown.inboxStars)
        assertEquals(200L, breakdown.feedStars)
        assertEquals(350L, breakdown.totalStars)
        assertEquals(3.50, breakdown.totalUsd, 0.001)

        val transactions = db.telefamDao().getStarsTransactions(userId)
        assertEquals(3, transactions.size)

        db.close()
    }

    @Test
    fun testCampaignCreationAndAdsManagement() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(
            context,
            com.telefam.company.app.data.TelefamAppDatabase::class.java
        ).allowMainThreadQueries().build()

        val socialRepo = com.telefam.company.app.data.TelefamSocialRepository(
            context = context,
            dao = db.telefamDao()
        )

        val userId = "creator_user_campaign_test"
        val campaignId = "cmp_test_001"
        val now = System.currentTimeMillis()
        val durationMs = 7L * 24L * 60L * 60L * 1000L

        val campaign = com.telefam.company.app.data.CampaignEntity(
            campaignId = campaignId,
            userId = userId,
            postId = "post_123",
            postThumbnailUrl = null,
            postCaption = "Promoted creator campaign",
            campaignGoal = "FOLLOWERS",
            targetAudienceType = "AUTOMATIC",
            targetGender = "ALL",
            targetAgeRange = "18-54",
            targetLocations = "GLOBAL",
            targetInterests = "ALL",
            tierId = "tier_followers_2",
            tierMinReach = 1000,
            tierMaxReach = 5000,
            dailyBudgetMinor = 15000L,
            currency = "KES",
            dailyBudgetDisplay = 150.0,
            durationDays = 7,
            totalBudgetDisplay = 1050.0,
            ctaButtonText = "Follow",
            destinationUrl = null,
            whatsappOrChatNumber = null,
            leadFormTitle = null,
            leadFormQuestions = null,
            paymentGateway = "PAYSTACK",
            paymentReference = "pstk_test_123",
            paymentVerified = true,
            verifiedAtMs = now,
            startsAtMs = now,
            expiresAtMs = now + durationMs,
            status = "ACTIVE",
            impressionsCount = 0L,
            clicksCount = 0L,
            conversionsCount = 0L,
            createdAtMs = now
        )

        socialRepo.insertCampaign(campaign)

        val userCampaigns = socialRepo.getUserCampaigns(userId)
        assertEquals(1, userCampaigns.size)
        assertEquals("ACTIVE", userCampaigns[0].status)
        assertEquals("FOLLOWERS", userCampaigns[0].campaignGoal)
        assertEquals(1050.0, userCampaigns[0].totalBudgetDisplay, 0.001)

        // Test impressions, clicks, status updates
        socialRepo.incrementCampaignImpressions(campaignId)
        socialRepo.incrementCampaignClicks(campaignId)
        socialRepo.updateCampaignStatus(campaignId, "PAUSED")

        val updated = socialRepo.getUserCampaigns(userId)[0]
        assertEquals("PAUSED", updated.status)
        assertEquals(1L, updated.impressionsCount)
        assertEquals(1L, updated.clicksCount)

        db.close()
    }

    @Test
    fun `testCommentsThreadedRepliesAndVirtualGiftsSystem`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, TelefamAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val socialRepo = TelefamSocialRepository(context, db.telefamDao())

        val videoId = "test_vid_101"
        val creatorId = "creator_usr_77"
        val fanId = "fan_usr_88"

        // Setup creator and fan profiles for @mention test
        db.telefamDao().upsertProfile(
            ProfileEntity(
                userId = creatorId,
                fullName = "Grace Creator",
                username = "grace",
                phoneCountryCode = "+254",
                phoneIsoCode = "KE",
                phoneNumber = "712345678",
                gender = "Female",
                dateOfBirth = "2000-01-01",
                bio = "Content creator"
            )
        )
        db.telefamDao().upsertProfile(
            ProfileEntity(
                userId = fanId,
                fullName = "Brian Fan",
                username = "brian",
                phoneCountryCode = "+254",
                phoneIsoCode = "KE",
                phoneNumber = "798765432",
                gender = "Male",
                dateOfBirth = "1998-05-15",
                bio = "Video enthusiast"
            )
        )

        // 1. Post top-level comment
        val comment1 = socialRepo.postComment(
            targetId = videoId,
            authorUserId = fanId,
            authorUsername = "brian",
            authorFullName = "Brian Fan",
            authorAvatarUrl = null,
            isCreator = false,
            text = "Great video! Check out https://telefam.company and @grace"
        )
        assertNotNull(comment1.commentId)
        assertEquals(videoId, comment1.targetId)
        assertEquals("brian", comment1.authorUsername)
        assertFalse(comment1.isPinned)

        // 2. Post threaded reply to comment1
        val reply1 = socialRepo.postComment(
            targetId = videoId,
            authorUserId = creatorId,
            authorUsername = "grace",
            authorFullName = "Grace Creator",
            authorAvatarUrl = null,
            isCreator = true,
            text = "Thanks Brian! Glad you liked it.",
            parentCommentId = comment1.commentId
        )
        assertEquals(comment1.commentId, reply1.parentCommentId)

        // Verify threaded comments hierarchy
        val thread = socialRepo.getThreadedComments(videoId, fanId)
        assertEquals(1, thread.size)
        assertEquals(1, thread[0].replies.size)
        assertEquals(reply1.commentId, thread[0].replies[0].commentId)
        assertEquals(2L, socialRepo.getCommentsCountForTarget(videoId))

        // 3. Post owner pins comment1
        socialRepo.togglePinComment(comment1.commentId, videoId)
        val pinnedThread = socialRepo.getThreadedComments(videoId, fanId)
        assertTrue(pinnedThread[0].isPinned)

        // 4. Like and unlike comment
        socialRepo.toggleLikeComment(comment1.commentId, creatorId)
        var likedThread = socialRepo.getThreadedComments(videoId, creatorId)
        assertTrue(likedThread[0].isLikedByMe)
        assertEquals(1L, likedThread[0].likesCount)

        socialRepo.toggleLikeComment(comment1.commentId, creatorId)
        likedThread = socialRepo.getThreadedComments(videoId, creatorId)
        assertFalse(likedThread[0].isLikedByMe)
        assertEquals(0L, likedThread[0].likesCount)

        // 5. Edit comment
        socialRepo.editComment(comment1.commentId, "Edited comment text!")
        val edited = socialRepo.getCommentById(comment1.commentId, fanId)
        assertNotNull(edited)
        assertEquals("Edited comment text!", edited?.commentText)
        assertTrue(edited?.isEdited == true)

        // 6. Mention candidates search
        val candidates = socialRepo.searchMentionCandidates("gra")
        assertEquals(1, candidates.size)
        assertEquals("grace", candidates[0].username)

        // 7. Virtual Gift with real wallet Stars deduction and crediting
        // Give fan 500 Stars initial balance
        db.telefamDao().upsertStarsBalance(
            StarsBalanceEntity(
                userId = fanId,
                balanceStars = 500L,
                lifetimeStars = 500L
            )
        )
        // Give creator 0 Stars initial balance
        db.telefamDao().upsertStarsBalance(
            StarsBalanceEntity(
                userId = creatorId,
                balanceStars = 0L,
                lifetimeStars = 0L
            )
        )

        val fireworksGift = VirtualGiftCatalog.allGifts.first { it.id == "fireworks" } // 100 stars
        val giftResult = socialRepo.sendVirtualGift(
            senderUserId = fanId,
            senderUsername = "brian",
            senderFullName = "Brian Fan",
            senderAvatarUrl = null,
            recipientUserId = creatorId,
            targetId = videoId,
            gift = fireworksGift,
            giftCount = 2 // 200 stars total
        )
        assertTrue(giftResult.isSuccess)

        // Verify balances: Fan should have 500 - 200 = 300 Stars; Creator should have 0 + 200 = 200 Stars
        val fanBal = db.telefamDao().getStarsBalance(fanId)?.balanceStars
        val creatorBal = db.telefamDao().getStarsBalance(creatorId)?.balanceStars
        assertEquals(300L, fanBal)
        assertEquals(200L, creatorBal)

        // Verify translation works offline
        val translated = socialRepo.translateTextToDeviceLanguage("Hello my friend! Thanks a lot.", "es")
        assertTrue(translated.contains("Hola"))

        // 8. Delete comment1 and verify replies cascade delete
        socialRepo.deleteComment(comment1.commentId, videoId)
        val finalThread = socialRepo.getThreadedComments(videoId, fanId)
        assertTrue(finalThread.none { it.commentId == comment1.commentId })

        db.close()
    }
}
