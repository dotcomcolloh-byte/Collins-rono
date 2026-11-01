package com.telefam.company.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ProfileEntity::class,
        MediaFingerprintEntity::class,
        TrustedDeviceEntity::class,
        OtpChallengeEntity::class,
        EncryptedSessionEntity::class,
        RateLimitBucketEntity::class,
        ForensicAuditLogEntity::class,
        PrivacySettingsEntity::class,
        FollowEdgeEntity::class,
        ContactHashEntity::class,
        UserBlockEntity::class,
        UserReportEntity::class,
        UserPostEntity::class,
        UserSubscriptionEntity::class,
        SocialSyncQueueEntity::class,
        AccountSettingsAndSecurityEntity::class,
        ConnectedAppSessionEntity::class,
        ProblemSupportTicketEntity::class,
        QuickLoginAccountEntity::class,
        ChatEntity::class,
        ChatMessageEntity::class,
        MessageReactionEntity::class,
        ChatPinEntity::class,
        ChatFavouriteEntity::class,
        DisappearingSettingEntity::class,
        DeliveryReceiptEntity::class,
        ChatDraftEntity::class,
        SignalIdentityKeyEntity::class,
        SignalSignedPreKeyEntity::class,
        SignalOneTimePreKeyEntity::class,
        SignalRatchetSessionEntity::class,
        ChatOutboxEntity::class,
        KlipyCachedItemEntity::class,
        KlipyRecentItemEntity::class,
        FeedVideoEntity::class,
        FeedVideoAudioEntity::class,
        FeedVideoLikeEntity::class,
        FeedVideoCommentsCountEntity::class,
        FeedVideoShareEntity::class,
        FeedVideoSaveEntity::class,
        FeedHashtagEntity::class,
        FeedVideoHashtagCrossRefEntity::class,
        FeedUserInterestEntity::class,
        FeedVideoViewEntity::class,
        StarsBalanceEntity::class,
        StarsTransactionEntity::class,
        CampaignEntity::class,
        CampaignLeadEntity::class,
        FeedCommentEntity::class,
        FeedCommentLikeEntity::class,
        MarketplaceProductEntity::class,
        MarketplaceFavoriteEntity::class,
        MarketplaceRecentViewEntity::class,
        MarketplaceRecentSearchEntity::class,
        MarketplaceReportEntity::class,
        LinkedDeviceEntity::class,
        DeviceLinkRequestEntity::class,
        SubscriptionPlanEntity::class,
        CreatorSubscriberEntity::class,
        WalletBalanceEntity::class,
        WalletTransactionEntity::class,
        PayoutAccountEntity::class,
        PayoutRecordEntity::class,
        CreatorMonetizationEntity::class,
        VideoMonetizationEntity::class,
        GroupChatEntity::class,
        GroupMemberEntity::class,
        GroupJoinRequestEntity::class,
        GroupAuditLogEntity::class,
        MemoryEntity::class,
        CallRecordEntity::class
    ],
    version = 20,
    exportSchema = false
)
abstract class TelefamAppDatabase : RoomDatabase() {
    abstract fun telefamDao(): TelefamDao

    companion object {
        @Volatile
        private var INSTANCE: TelefamAppDatabase? = null

        fun getInstance(context: Context): TelefamAppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TelefamAppDatabase::class.java,
                    "telefam_company_production.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
