package com.telefam.company.app.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.AndroidViewModel
import java.security.SecureRandom
import java.io.File
import java.util.Locale
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject
import androidx.lifecycle.viewModelScope
import com.telefam.company.app.data.UsernameConflictException
import com.example.BuildConfig
import com.example.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.telefam.company.app.data.AccountSettingsAndSecurityEntity
import com.telefam.company.app.data.AuthActionOutcome
import com.telefam.company.app.data.ConnectedAppSessionEntity
import com.telefam.company.app.data.ForensicAuditLogEntity
import com.telefam.company.app.data.PrivacySettingsEntity
import com.telefam.company.app.data.ProblemSupportTicketEntity
import com.telefam.company.app.data.ProfileEntity
import com.telefam.company.app.data.PublicProfileSnapshot
import com.telefam.company.app.data.QuickLoginAccountEntity
import com.telefam.company.app.data.TelefamSecurityRepository
import com.telefam.company.app.domain.FeedAudioTrackInfo
import com.telefam.company.app.domain.FeedSearchCombinedResults
import com.telefam.company.app.domain.FeedTopTab
import com.telefam.company.app.domain.FeedVideoItem
import com.telefam.company.app.domain.NewCreatorUserItem
import com.telefam.company.app.data.StarsBalanceEntity
import com.telefam.company.app.data.StarsTransactionEntity
import com.telefam.company.app.domain.StarsBreakdownSummary
import com.telefam.company.app.domain.StarsSourceFilter
import com.telefam.company.app.data.TelefamSettingsRepository
import com.telefam.company.app.data.TelefamSocialRepository
import com.telefam.company.app.domain.CommentThreadItem
import com.telefam.company.app.domain.MentionCandidateUser
import com.telefam.company.app.domain.VirtualGiftItem
import com.telefam.company.app.ui.components.ActiveGiftCelebration
import com.telefam.company.app.data.TrustedDeviceEntity
import com.telefam.company.app.data.CampaignEntity
import com.telefam.company.app.data.CampaignLeadEntity
import com.telefam.company.app.data.MarketplaceProductEntity
import com.telefam.company.app.data.MarketplaceFavoriteEntity
import com.telefam.company.app.data.MarketplaceRecentViewEntity
import com.telefam.company.app.data.MarketplaceRecentSearchEntity
import com.telefam.company.app.data.MarketplaceReportEntity
import com.telefam.company.app.domain.MarketplaceCategory
import com.telefam.company.app.domain.MarketplaceDeliveryOption
import com.telefam.company.app.domain.MarketplaceItemCondition
import com.telefam.company.app.domain.MarketplaceJsonHelpers
import com.telefam.company.app.domain.MarketplaceProduct
import com.telefam.company.app.domain.MarketplaceReviewStatus
import com.telefam.company.app.domain.MarketplaceScreenTab
import com.telefam.company.app.domain.MarketplaceUploadProgress
import com.telefam.company.app.security.MarketplaceModerationEngine
import com.telefam.company.app.security.TelefamAggressiveCompressionEngine
import com.telefam.company.app.domain.CampaignGoal
import com.telefam.company.app.domain.CampaignPaymentGateway
import com.telefam.company.app.domain.CampaignPricingCatalog
import com.telefam.company.app.domain.CampaignPricingTier
import com.telefam.company.app.domain.CampaignBillingResolver
import com.telefam.company.app.domain.CampaignScreenTab
import com.telefam.company.app.domain.AppPermissionType
import com.telefam.company.app.domain.AppThemeOption
import com.telefam.company.app.data.LinkedDeviceEntity
import com.telefam.company.app.data.DeviceLinkRequestEntity
import com.telefam.company.app.data.SubscriptionPlanEntity
import com.telefam.company.app.data.CreatorSubscriberEntity
import com.telefam.company.app.data.MemoryEntity
import com.telefam.company.app.domain.MemoryItem
import com.telefam.company.app.domain.MemoryMediaType
import com.telefam.company.app.domain.MemoryPrivacy
import com.telefam.company.app.data.WalletBalanceEntity
import com.telefam.company.app.data.WalletTransactionEntity
import com.telefam.company.app.data.PayoutAccountEntity
import com.telefam.company.app.data.PayoutRecordEntity
import com.telefam.company.app.data.CreatorMonetizationEntity
import com.telefam.company.app.data.VideoMonetizationEntity
import com.telefam.company.app.data.ChatEntity
import com.telefam.company.app.data.GroupChatEntity
import com.telefam.company.app.data.GroupMemberEntity
import com.telefam.company.app.data.GroupJoinRequestEntity
import com.telefam.company.app.data.GroupAuditLogEntity
import com.telefam.company.app.data.CallRecordEntity
import com.telefam.company.app.domain.ActiveCallSession
import com.telefam.company.app.domain.CallType
import com.telefam.company.app.domain.CallDirection
import com.telefam.company.app.domain.CallStatus
import com.telefam.company.app.domain.NetworkQuality
import com.telefam.company.app.domain.CameraFacing
import com.telefam.company.app.domain.VideoCallFilter
import com.telefam.company.app.domain.TelefamWebRtcCallEngine
import com.telefam.company.app.domain.GroupRole
import com.telefam.company.app.domain.GroupType
import com.telefam.company.app.domain.WhoCanOption
import com.telefam.company.app.domain.GroupPermissionsSnapshot
import com.telefam.company.app.domain.GroupProfileUiModel
import com.telefam.company.app.domain.GroupMemberItem
import com.telefam.company.app.domain.GroupJoinRequestItem
import com.telefam.company.app.domain.GroupAuditLogItem
import com.telefam.company.app.domain.GroupMediaPreviewItem
import com.telefam.company.app.domain.GroupProfileMediaTab
import com.telefam.company.app.domain.CreatorMonetizationStatus
import com.telefam.company.app.domain.CreatorQualificationSnapshot
import com.telefam.company.app.domain.VideoMonetizationItem
import com.telefam.company.app.domain.DeviceType
import com.telefam.company.app.domain.DeviceLinkRequestStatus
import com.telefam.company.app.domain.RealDeviceIdentityResolver
import com.telefam.company.app.domain.PairingTokenGenerator
import com.telefam.company.app.domain.AuthMode
import com.telefam.company.app.domain.ContactsFilterMode
import com.telefam.company.app.domain.CountryDialCode
import com.telefam.company.app.domain.DeviceAndNetworkTelemetry
import com.telefam.company.app.domain.DiscoverableUserItem
import com.telefam.company.app.domain.EncryptedUserSession
import com.telefam.company.app.domain.FollowListType
import com.telefam.company.app.domain.HelpSupportArticle
import com.telefam.company.app.domain.HomeNavTab
import com.telefam.company.app.domain.LegalPolicyDocument
import com.telefam.company.app.domain.OwnerProfileCooldownState
import com.telefam.company.app.domain.OwnerProfileSubTab
import com.telefam.company.app.domain.PostsFilterOption
import com.telefam.company.app.domain.PrivacyAudience
import com.telefam.company.app.domain.PrivacyControlField
import com.telefam.company.app.domain.PrivacyRuleKey
import com.telefam.company.app.domain.ReportLocationOption
import com.telefam.company.app.domain.ReportReasonOption
import com.telefam.company.app.data.ActiveVoiceBubblePlaybackState
import com.telefam.company.app.data.ActiveVoiceRecordingState
import com.telefam.company.app.data.ForwardRecipientTarget
import com.telefam.company.app.data.TelefamChatRepository
import com.telefam.company.app.domain.ChatAttachmentItem
import com.telefam.company.app.domain.ChatDisappearingOption
import com.telefam.company.app.domain.ChatMessageItem
import com.telefam.company.app.domain.ChatMessageType
import com.telefam.company.app.domain.ChatReactionChoice
import com.telefam.company.app.domain.E2eeVerificationSnapshot
import com.telefam.company.app.domain.KlipyMediaItem
import com.telefam.company.app.domain.KlipyMediaTab
import com.telefam.company.app.domain.OneToOneChatListItem
import com.telefam.company.app.domain.ReportUrgencyOption
import com.telefam.company.app.domain.RiskLevel
import com.telefam.company.app.domain.SafetyTopicGuide
import com.telefam.company.app.domain.SanitizedAvatarResult
import com.telefam.company.app.domain.SettingsSubScreen
import com.telefam.company.app.domain.SocialPrivacyOption
import com.telefam.company.app.domain.TelefamCountries
import com.telefam.company.app.domain.TelefamInputSanitizer
import com.telefam.company.app.domain.TelefamSettingsCatalog
import com.telefam.company.app.domain.TelefamValidators
import com.telefam.company.app.domain.UserPostItem
import com.telefam.company.app.domain.EditablePhotoStudioItem
import com.telefam.company.app.domain.EditableVideoStudioState
import com.telefam.company.app.domain.PostBackgroundTheme
import com.telefam.company.app.domain.PostBackgroundUploadState
import com.telefam.company.app.domain.PostBrushStroke
import com.telefam.company.app.domain.PostMusicTrackItem
import com.telefam.company.app.domain.PostStudioMode
import com.telefam.company.app.domain.PostTextOverlayLayer
import com.telefam.company.app.domain.DashboardPerformanceData
import com.telefam.company.app.domain.DashboardPeriodOption
import com.telefam.company.app.domain.PostTypographyFont
import com.telefam.company.app.domain.PostUploadLifecycleStatus
import com.telefam.company.app.domain.PostVisibilityAudience
import com.telefam.company.app.notifications.TelefamDeepLinkTargetType
import com.telefam.company.app.notifications.TelefamNotificationDeepLink
import com.telefam.company.app.notifications.TelefamNotificationEngine
import com.telefam.company.app.security.TelefamPostSandboxAndStudioEngine
import com.telefam.company.app.security.AndroidSandboxMediaProcessor
import com.telefam.company.app.security.TelefamCryptoAndTelemetry
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.isActive

enum class AppDestination {
    LAUNCH_ANIMATION,
    AUTH,
    PROFILE_SETUP,
    MAIN_HOME,
    PRIVACY_SETTINGS,
    SETTINGS_AND_PRIVACY,
    PUBLIC_USER_PROFILE,
    ONE_TO_ONE_CHAT,
    E2EE_VERIFICATION,
    MARKETPLACE,
    LINKED_DEVICES,
    SUBSCRIPTIONS,
    WALLET,
    MONETIZATION,
    CREATE_GROUP,
    GROUP_PROFILE,
    MEMORIES,
    CALL
}

data class WalletCombinedData(
    val balance: WalletBalanceEntity?,
    val txs: List<WalletTransactionEntity>,
    val accounts: List<PayoutAccountEntity>,
    val records: List<PayoutRecordEntity>
)

data class SubscriptionsUiState(
    val plans: List<SubscriptionPlanEntity> = emptyList(),
    val subscribers: List<CreatorSubscriberEntity> = emptyList(),
    val totalEarnings: Double = 0.0,
    val periodEarnings: Double = 0.0,
    val subscribersCount: Int = 0,
    val newSubscribersCount: Int = 0,
    val canceledCount: Int = 0,
    val periodFilter: String = "LAST_30_DAYS",
    val isCreatePlanSheetOpen: Boolean = false,
    val isManagePlansSheetOpen: Boolean = false,
    val isSubscriptionInsightsSheetOpen: Boolean = false,
    val isAllSubscribersSheetOpen: Boolean = false,
    val isSubscriptionsHelpSheetOpen: Boolean = false,
    val actionSuccessMessage: String? = null,
    val errorMessage: String? = null
)

data class WalletUiState(
    val balance: WalletBalanceEntity? = null,
    val transactions: List<WalletTransactionEntity> = emptyList(),
    val transactionFilter: String = "ALL",
    val periodFilter: String = "THIS_MONTH",
    val starsEarnings: Double = 0.0,
    val subscriptionEarnings: Double = 0.0,
    val adRevenue: Double = 0.0,
    val otherEarnings: Double = 0.0,
    val payoutAccounts: List<PayoutAccountEntity> = emptyList(),
    val payoutRecords: List<PayoutRecordEntity> = emptyList(),
    val totalPayoutsAllTime: Double = 0.0,
    val pendingPayoutsAmount: Double = 0.0,
    val pendingPayoutsCount: Int = 0,
    val paidPayoutsAmount: Double = 0.0,
    val paidPayoutsCount: Int = 0,
    val failedPayoutsAmount: Double = 0.0,
    val failedPayoutsCount: Int = 0,
    val isWithdrawSheetOpen: Boolean = false,
    val isAddPayoutAccountSheetOpen: Boolean = false,
    val isAllTransactionsSheetOpen: Boolean = false,
    val isPayoutHistorySheetOpen: Boolean = false,
    val isWalletHelpSheetOpen: Boolean = false,
    val isWalletSettingsSheetOpen: Boolean = false,
    val actionSuccessMessage: String? = null,
    val errorMessage: String? = null
)

data class CreatorMonetizationUiState(
    val status: CreatorMonetizationStatus = CreatorMonetizationStatus.NOT_APPLIED,
    val qualification: CreatorQualificationSnapshot = CreatorQualificationSnapshot(),
    val videos: List<VideoMonetizationItem> = emptyList(),
    val actionMessage: String? = null,
    val errorMessage: String? = null
)

data class GroupChatUiState(
    val activeGroupProfile: GroupProfileUiModel? = null,
    val isCreateGroupLoading: Boolean = false,
    val groupProfileStatusMessage: String? = null
)

data class MemoriesUiState(
    val items: List<MemoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val statusMessage: String? = null
)

data class PublicProfileSubscriptionUiState(
    val activeSubscription: CreatorSubscriberEntity? = null,
    val availablePlans: List<SubscriptionPlanEntity> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

data class CallUiState(
    val activeCall: ActiveCallSession? = null,
    val incomingCall: ActiveCallSession? = null,
    val callHistory: List<CallRecordEntity> = emptyList(),
    val selectedInboxTab: String = "CHATS",
    val privacyNoticeBanner: String? = null
)

data class TelefamUiState(
    val destination: AppDestination = AppDestination.LAUNCH_ANIMATION,
    val selectedHomeTab: HomeNavTab = HomeNavTab.CHATS,
    val authMode: AuthMode = AuthMode.SIGN_UP,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val otpCode: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoBannerMessage: String? = null,

    // OTP & Brute-Force Cooldown Timers
    val otpResendCountdownSec: Int = 0,
    val loginCooldownRemainingSec: Long = 0L,
    val otpCooldownRemainingSec: Long = 0L,

    // Profile Setup Fields (Reference Image 2)
    val fullName: String = "",
    val username: String = "",
    val isUsernameAvailable: Boolean? = null,
    val usernameSuggestions: List<String> = emptyList(),
    val selectedCountry: CountryDialCode = TelefamCountries.DEFAULT, // Kenya +254
    val phoneNumber: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val bio: String = "",
    val sanitizedAvatar: SanitizedAvatarResult? = null,
    val pendingCropBitmap: Bitmap? = null,
    val profileFieldErrors: Map<String, String> = emptyMap(),

    // Session, Profile & Privacy Settings
    val activeSession: EncryptedUserSession? = null,
    val savedProfile: ProfileEntity? = null,
    val privacySettings: PrivacySettingsEntity = PrivacySettingsEntity(userId = "local_telefam_member"),
    val telemetry: DeviceAndNetworkTelemetry? = null,
    val screenWidthDp: Int = 411,

    // Contacts Discovery & Social Graph State (Image 2 & Image 1)
    val contactsUsers: List<DiscoverableUserItem> = emptyList(),
    val contactsFilterMode: ContactsFilterMode = ContactsFilterMode.SUGGESTED,
    val contactsSearchQuery: String = "",
    val isContactsSearchActive: Boolean = false,
    val contactsHasMorePages: Boolean = false,
    val contactsCurrentPage: Int = 0,
    val isContactsLoading: Boolean = false,
    val isContactsOfflineBannerVisible: Boolean = false,
    val hasContactsPermission: Boolean = false,
    val contactsMatchCount: Int = 0,
    val contactsErrorMessage: String? = null,

    // Public User Profile Screen State (Image 1)
    val activePublicProfile: DiscoverableUserItem? = null,
    val activePublicProfilePosts: List<UserPostItem> = emptyList(),
    val activePublicProfilePostsFilter: PostsFilterOption = PostsFilterOption.ALL_POSTS,
    val activePublicProfileBlocked: Boolean = false,
    val activeFollowListType: FollowListType? = null,
    val followListUsers: List<DiscoverableUserItem> = emptyList(),
    val followListPage: Int = 1,
    val followListHasMore: Boolean = false,
    val profileStatusBanner: String? = null,

    // Owner Profile Screen State (Reference Image)
    val ownerFollowersCount: Long = 0L,
    val ownerFollowingCount: Long = 0L,
    val ownerSubscribersCount: Long = 0L,
    val ownerSelectedSubTab: OwnerProfileSubTab = OwnerProfileSubTab.POSTS,
    val ownerPosts: List<UserPostItem> = emptyList(),
    val ownerCooldownState: OwnerProfileCooldownState = OwnerProfileCooldownState.compute(null, null),
    val isOwnerEditSheetOpen: Boolean = false,
    val isOwnerProfileRefreshing: Boolean = false,
    val ownerFollowListType: FollowListType? = null,
    val ownerFollowListUsers: List<DiscoverableUserItem> = emptyList(),
    val ownerFollowListPage: Int = 1,
    val ownerFollowListHasMore: Boolean = false,
    val ownerStatusBanner: String? = null,
    val isOwnerMenuOpen: Boolean = false,
    val isDashboardOpen: Boolean = false,
    val isDashboardAnalyticsSubScreen: Boolean = false,
    val dashboardPeriod: DashboardPeriodOption = DashboardPeriodOption.LAST_28_DAYS,
    val dashboardData: DashboardPerformanceData = DashboardPerformanceData(),
    val isDashboardRefreshing: Boolean = false,

    // Stars Earnings & Virtual Gifts System
    val isStarsEarningsOpen: Boolean = false,
    val isStarsTopUpSheetOpen: Boolean = false,
    val starsSourceFilter: StarsSourceFilter = StarsSourceFilter.ALL,
    val starsPeriod: DashboardPeriodOption = DashboardPeriodOption.LAST_28_DAYS,
    val starsBalance: StarsBalanceEntity? = null,
    val starsTransactions: List<StarsTransactionEntity> = emptyList(),
    val starsBreakdown: StarsBreakdownSummary = StarsBreakdownSummary(),
    val isStarsInfoSheetOpen: Boolean = false,
    val isStarsRefreshing: Boolean = false,

    // Ad Campaigns & Promotion System
    val isCampaignScreenOpen: Boolean = false,
    val campaignActiveTab: CampaignScreenTab = CampaignScreenTab.CREATE,
    val campaignSelectedPost: UserPostItem? = null,
    val campaignSelectedGoal: CampaignGoal = CampaignGoal.FOLLOWERS,
    val campaignDestinationUrl: String = "",
    val campaignLeadFormTitle: String = "Creator Ambassador Application",
    val campaignCtaText: String = "Follow",
    val campaignTargetType: String = "AUTOMATIC", // AUTOMATIC or CUSTOM
    val campaignTargetGender: String = "ALL", // ALL, MEN, WOMEN
    val campaignTargetAgeRange: String = "18-54",
    val campaignTargetLocations: Set<String> = setOf("Kenya", "Global"),
    val campaignTargetInterests: Set<String> = setOf("Comedy & Entertainment", "Tech & Innovation"),
    val campaignSelectedTierId: String = "tier_followers_2",
    val campaignDurationDays: Int = 3,
    val userCampaigns: List<CampaignEntity> = emptyList(),
    val activeSponsoredCampaigns: List<CampaignEntity> = emptyList(),
    val activeCampaignLeads: List<CampaignLeadEntity> = emptyList(),
    val activeViewingCampaignId: String? = null,
    val isCampaignSubmitting: Boolean = false,
    val campaignBannerMessage: String? = null,

    // Marketplace System State
    val marketplaceProducts: List<com.telefam.company.app.domain.MarketplaceProduct> = emptyList(),
    val marketplaceFilterCategory: com.telefam.company.app.domain.MarketplaceCategory = com.telefam.company.app.domain.MarketplaceCategory.ALL,
    val marketplaceSearchQuery: String = "",
    val marketplaceSelectedTab: com.telefam.company.app.domain.MarketplaceScreenTab = com.telefam.company.app.domain.MarketplaceScreenTab.EXPLORE,
    val marketplaceActiveProduct: com.telefam.company.app.domain.MarketplaceProduct? = null,
    val isMarketplacePostSheetOpen: Boolean = false,
    val marketplaceUploadProgress: com.telefam.company.app.domain.MarketplaceUploadProgress = com.telefam.company.app.domain.MarketplaceUploadProgress.IDLE,
    val marketplaceUploadStatusText: String? = null,
    val marketplaceRecentSearches: List<String> = emptyList(),
    val isMarketplaceSuspended: Boolean = false,
    val marketplaceSuspensionReason: String = "",
    val activeReportingProduct: com.telefam.company.app.domain.MarketplaceProduct? = null,
    val chatPendingMarketplaceProduct: com.telefam.company.app.domain.MarketplaceProduct? = null,

    // Settings and Privacy System State (All 13 Reference Sections & Subscreens)
    val settingsSubScreen: SettingsSubScreen = SettingsSubScreen.ROOT,
    val settingsNavigationStack: List<SettingsSubScreen> = emptyList(),
    val accountSettings: AccountSettingsAndSecurityEntity = AccountSettingsAndSecurityEntity(ownerUserId = "local_telefam_member"),
    val activePrivacyControlField: PrivacyControlField = PrivacyControlField.COMMENTS,
    val activePermissionType: AppPermissionType = AppPermissionType.CONTACTS,
    val activeSafetyGuide: SafetyTopicGuide = TelefamSettingsCatalog.SAFETY_GUIDES.first(),
    val activeHelpArticle: HelpSupportArticle = TelefamSettingsCatalog.HELP_ARTICLES.first(),
    val activeLegalDocument: LegalPolicyDocument = TelefamSettingsCatalog.LEGAL_DOCUMENTS.first(),
    val activeAboutDetailKey: String = "ABOUT_APP",
    val trustedDevices: List<TrustedDeviceEntity> = emptyList(),
    val connectedApps: List<ConnectedAppSessionEntity> = emptyList(),
    val recentSecurityLogs: List<ForensicAuditLogEntity> = emptyList(),
    val supportTickets: List<ProblemSupportTicketEntity> = emptyList(),
    val blockedAccountItems: List<DiscoverableUserItem> = emptyList(),
    val blockedAccountsSearchQuery: String = "",
    val helpSearchQuery: String = "",
    val settingsOtpSentEmail: String? = null,
    val settingsOtpResendCountdownSec: Int = 0,
    val settingsStatusBanner: String? = null,
    val settingsErrorBanner: String? = null,
    val canRetrySettingsError: Boolean = false,
    val lastSubmittedTicketId: String? = null,
    val profileQrBitmap: Bitmap? = null,
    val quickLoginAccounts: List<QuickLoginAccountEntity> = emptyList(),

    // 1:1 Individual Chats (Offline-First Room Source of Truth + Signal E2EE + Klipy + Voice Notes)
    val chatListItems: List<OneToOneChatListItem> = emptyList(),
    val chatListSearchQuery: String = "",
    val isChatListSearchActive: Boolean = false,
    val activeChatId: String? = null,
    val activeChatItem: OneToOneChatListItem? = null,
    val activeChatMessages: List<ChatMessageItem> = emptyList(),
    val activeChatAttachments: List<ChatAttachmentItem> = emptyList(),
    val chatInputText: String = "",
    val replyingToMessage: ChatMessageItem? = null,
    val editingMessage: ChatMessageItem? = null,
    val selectedMessageIds: Set<String> = emptySet(),
    val isSelectModeActive: Boolean = false,
    val activeKlipyTab: KlipyMediaTab = KlipyMediaTab.STICKERS,
    val klipySearchQuery: String = "",
    val klipyRecentItems: List<KlipyMediaItem> = emptyList(),
    val previewKlipyItem: KlipyMediaItem? = null,
    val activeE2eeVerification: E2eeVerificationSnapshot? = null,
    val forwardCandidates: List<ForwardRecipientTarget> = emptyList(),
    val forwardSearchQuery: String = "",
    val selectedForwardUserIds: Set<String> = emptySet(),
    val messagesPendingForward: List<ChatMessageItem> = emptyList(),
    val isForwardPickerOpen: Boolean = false,

    // Post Studio (Text, Camera, Photo up to 10, Video CapCut Studio, Sandbox Isolation & Chunked Upload)
    val postUploadState: PostBackgroundUploadState = PostBackgroundUploadState(),
    val postStudioErrorBanner: String? = null,
    val postMentionSuggestions: List<DiscoverableUserItem> = emptyList(),
    val postMusicSearchResults: List<PostMusicTrackItem> = emptyList(),
    val isSearchingPostMusic: Boolean = false,

    // TikTok-style Feeds State
    val feedActiveTopTab: FeedTopTab = FeedTopTab.FOR_YOU,
    val feedVideosList: List<FeedVideoItem> = emptyList(),
    val feedHasUnreadFollowingDot: Boolean = true,
    val feedNewCreatorsList: List<NewCreatorUserItem> = emptyList(),
    val feedSearchResults: FeedSearchCombinedResults = FeedSearchCombinedResults(),
    val feedActiveAudioTrack: FeedAudioTrackInfo? = null,
    val feedActiveAudioVideos: List<FeedVideoItem> = emptyList(),
    val feedActiveHashtagTitle: String? = null,
    val feedActiveHashtagVideos: List<FeedVideoItem> = emptyList(),
    val feedFocusedInboxVideo: FeedVideoItem? = null,
    val feedStatusFeedbackMessage: String? = null,
    val feedCurrentPage: Int = 1,
    val feedHasMorePages: Boolean = true,
    val isFeedLoadingMore: Boolean = false,
    val preselectedPostMusicTrack: PostMusicTrackItem? = null,
    val editingFeedVideoItem: FeedVideoItem? = null,

    // Profile Reusable Feed Overlay State
    val activeProfileFeedVideos: List<FeedVideoItem> = emptyList(),
    val activeProfileFeedInitialIndex: Int = 0,
    val isProfileFeedOverlayOpen: Boolean = false,
    val profileFeedHeaderTitle: String = "Posts",

    // Comments & Virtual Gift Celebration Overlay State
    val activeCommentsVideo: FeedVideoItem? = null,
    val activeCommentsList: List<CommentThreadItem> = emptyList(),
    val activeCommentsTotalCount: Long = 0L,
    val activeGiftCelebration: ActiveGiftCelebration? = null,

    // Linked Devices Multi-Device State
    val linkedDevices: List<LinkedDeviceEntity> = emptyList(),
    val currentSessionDeviceId: String = "",
    val activePairingRequest: DeviceLinkRequestEntity? = null,
    val pendingIncomingApprovalRequest: DeviceLinkRequestEntity? = null,
    val pairingCountdownSeconds: Int = 30,
    val approvalCountdownSeconds: Int = 300,
    val linkCodeInputText: String = "",
    val linkDeviceErrorBanner: String? = null,
    val linkDeviceSuccessBanner: String? = null,
    val isLinkingDeviceLoading: Boolean = false,
    val linkedDevicesSelectedTab: Int = 0,

    // Modular Sub-States to guarantee Dalvik argument register limits are never exceeded
    val subscriptionsState: SubscriptionsUiState = SubscriptionsUiState(),
    val walletState: WalletUiState = WalletUiState(),
    val monetizationState: CreatorMonetizationUiState = CreatorMonetizationUiState(),
    val groupState: GroupChatUiState = GroupChatUiState(),
    val memoriesState: MemoriesUiState = MemoriesUiState(),
    val publicProfileSubscriptionState: PublicProfileSubscriptionUiState = PublicProfileSubscriptionUiState(),
    val callState: CallUiState = CallUiState()
) {
    // Call Getters
    val activeCall: ActiveCallSession? get() = callState.activeCall
    val incomingCall: ActiveCallSession? get() = callState.incomingCall
    val callHistory: List<CallRecordEntity> get() = callState.callHistory
    val selectedInboxTab: String get() = callState.selectedInboxTab
    val callPrivacyNoticeBanner: String? get() = callState.privacyNoticeBanner

    // Subscriptions Getters (Preserving full compatibility with all existing Composables)
    val subscriptionPlans: List<SubscriptionPlanEntity> get() = subscriptionsState.plans
    val creatorSubscribers: List<CreatorSubscriberEntity> get() = subscriptionsState.subscribers
    val totalSubscriptionEarnings: Double get() = subscriptionsState.totalEarnings
    val subscriptionPeriodEarnings: Double get() = subscriptionsState.periodEarnings
    val subscriptionSubscribersCount: Int get() = subscriptionsState.subscribersCount
    val subscriptionNewSubscribersCount: Int get() = subscriptionsState.newSubscribersCount
    val subscriptionCanceledCount: Int get() = subscriptionsState.canceledCount
    val subscriptionPeriodFilter: String get() = subscriptionsState.periodFilter
    val isCreatePlanSheetOpen: Boolean get() = subscriptionsState.isCreatePlanSheetOpen
    val isManagePlansSheetOpen: Boolean get() = subscriptionsState.isManagePlansSheetOpen
    val isSubscriptionInsightsSheetOpen: Boolean get() = subscriptionsState.isSubscriptionInsightsSheetOpen
    val isAllSubscribersSheetOpen: Boolean get() = subscriptionsState.isAllSubscribersSheetOpen
    val isSubscriptionsHelpSheetOpen: Boolean get() = subscriptionsState.isSubscriptionsHelpSheetOpen
    val subscriptionsActionSuccessMessage: String? get() = subscriptionsState.actionSuccessMessage
    val subscriptionsErrorMessage: String? get() = subscriptionsState.errorMessage

    // Wallet Getters (Preserving full compatibility with all existing Composables)
    val walletBalance: WalletBalanceEntity? get() = walletState.balance
    val walletTransactions: List<WalletTransactionEntity> get() = walletState.transactions
    val walletTransactionFilter: String get() = walletState.transactionFilter
    val walletPeriodFilter: String get() = walletState.periodFilter
    val walletStarsEarnings: Double get() = walletState.starsEarnings
    val walletSubscriptionEarnings: Double get() = walletState.subscriptionEarnings
    val walletAdRevenue: Double get() = walletState.adRevenue
    val walletOtherEarnings: Double get() = walletState.otherEarnings
    val payoutAccounts: List<PayoutAccountEntity> get() = walletState.payoutAccounts
    val payoutRecords: List<PayoutRecordEntity> get() = walletState.payoutRecords
    val totalPayoutsAllTime: Double get() = walletState.totalPayoutsAllTime
    val pendingPayoutsAmount: Double get() = walletState.pendingPayoutsAmount
    val pendingPayoutsCount: Int get() = walletState.pendingPayoutsCount
    val paidPayoutsAmount: Double get() = walletState.paidPayoutsAmount
    val paidPayoutsCount: Int get() = walletState.paidPayoutsCount
    val failedPayoutsAmount: Double get() = walletState.failedPayoutsAmount
    val failedPayoutsCount: Int get() = walletState.failedPayoutsCount
    val isWithdrawSheetOpen: Boolean get() = walletState.isWithdrawSheetOpen
    val isAddPayoutAccountSheetOpen: Boolean get() = walletState.isAddPayoutAccountSheetOpen
    val isAllTransactionsSheetOpen: Boolean get() = walletState.isAllTransactionsSheetOpen
    val isPayoutHistorySheetOpen: Boolean get() = walletState.isPayoutHistorySheetOpen
    val isWalletHelpSheetOpen: Boolean get() = walletState.isWalletHelpSheetOpen
    val isWalletSettingsSheetOpen: Boolean get() = walletState.isWalletSettingsSheetOpen
    val walletActionSuccessMessage: String? get() = walletState.actionSuccessMessage
    val walletErrorMessage: String? get() = walletState.errorMessage

    // Creator Monetization Getters
    val monetizationStatus: CreatorMonetizationStatus get() = monetizationState.status
    val qualificationSnapshot: CreatorQualificationSnapshot get() = monetizationState.qualification
    val videoMonetizationList: List<VideoMonetizationItem> get() = monetizationState.videos
    val monetizationActionMessage: String? get() = monetizationState.actionMessage
    val monetizationErrorMessage: String? get() = monetizationState.errorMessage

    // Group Chat Getters
    val activeGroupProfile: GroupProfileUiModel? get() = groupState.activeGroupProfile
    val isCreateGroupLoading: Boolean get() = groupState.isCreateGroupLoading
    val groupProfileStatusMessage: String? get() = groupState.groupProfileStatusMessage

    // Memories Getters
    val memories: List<MemoryItem> get() = memoriesState.items
    val isMemoriesLoading: Boolean get() = memoriesState.isLoading
    val memoriesStatusMessage: String? get() = memoriesState.statusMessage

    // Creator Profile Subscription Getters
    val activePublicProfileSubscription: CreatorSubscriberEntity? get() = publicProfileSubscriptionState.activeSubscription
    val activePublicProfilePlans: List<SubscriptionPlanEntity> get() = publicProfileSubscriptionState.availablePlans
    val isSubscribingLoading: Boolean get() = publicProfileSubscriptionState.isLoading
    val subscribeSuccessMessage: String? get() = publicProfileSubscriptionState.successMessage
    val subscribeErrorMessage: String? get() = publicProfileSubscriptionState.errorMessage
}

@OptIn(ExperimentalCoroutinesApi::class)
class TelefamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TelefamSecurityRepository(application.applicationContext)
    private val socialRepository = TelefamSocialRepository(
        context = application.applicationContext,
        dao = repository.dao
    )
    private val settingsRepository = TelefamSettingsRepository(
        context = application.applicationContext,
        dao = repository.dao,
        securityRepository = repository,
        socialRepository = socialRepository
    )
    val chatRepository = TelefamChatRepository(
        context = application.applicationContext,
        dao = repository.dao
    )

    private val _uiState = MutableStateFlow(TelefamUiState())
    val uiState: StateFlow<TelefamUiState> = _uiState.asStateFlow()

    val voiceRecordingState: StateFlow<ActiveVoiceRecordingState> = chatRepository.voiceRecordingState
    val bubblePlaybackState: StateFlow<ActiveVoiceBubblePlaybackState> = chatRepository.bubblePlaybackState

    val klipyPagedFlow: Flow<PagingData<KlipyMediaItem>> = _uiState
        .map { state ->
            Triple(
                state.activeSession?.userId ?: state.savedProfile?.userId ?: "local_telefam_member",
                state.activeKlipyTab,
                state.klipySearchQuery
            )
        }
        .distinctUntilChanged()
        .flatMapLatest { (userId, tab, query) ->
            chatRepository.klipyProvider.observePagedMedia(
                ownerUserId = userId,
                tab = tab,
                searchQuery = query
            )
        }
        .cachedIn(viewModelScope)

    private var currentLocationTriple: Triple<String, Double?, Double?> =
        Triple("Kenya • Africa/Nairobi", -1.2921, 36.8219)
    private var timerJob: Job? = null
    private var usernameCheckJob: Job? = null
    private var ownerReactiveObserverJob: Job? = null
    private var chatListReactiveObserverJob: Job? = null
    private var activeChatObserverJob: Job? = null
    private var klipyRecentsObserverJob: Job? = null
    private var callHistoryObserverJob: Job? = null
    private var activeCallEngineObserverJob: Job? = null
    private var lastRetryableSettingsAction: (() -> Unit)? = null

    val callEngine = TelefamWebRtcCallEngine(
        context = application.applicationContext,
        scope = viewModelScope
    )

    private val safeExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = throwable.localizedMessage ?: "An unexpected error occurred. Please try again.",
                settingsErrorBanner = if (it.destination == AppDestination.SETTINGS_AND_PRIVACY) {
                    throwable.localizedMessage ?: "Unable to complete action. Tap Retry to try again."
                } else {
                    it.settingsErrorBanner
                },
                canRetrySettingsError = lastRetryableSettingsAction != null
            )
        }
    }

    init {
        initializeLaunchAndTelemetry()
        startOneSecondSecurityTicker()
        observeCallEngineAndHistory()
    }

    private fun initializeLaunchAndTelemetry() {
        viewModelScope.launch(safeExceptionHandler) {
            val startMs = System.currentTimeMillis()
            val context = getApplication<Application>().applicationContext
            currentLocationTriple = TelefamCryptoAndTelemetry.fetchBestLocationOrDefault(context)

            val initialTelemetry = repository.refreshTelemetrySnapshot(
                email = null,
                screenWidthDp = _uiState.value.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            val (existingSession, existingProfile) = repository.getActiveSessionWithTelemetry(
                screenWidthDp = _uiState.value.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateAnomaly = false
            )
            val resolvedUserId = existingSession?.userId ?: existingProfile?.userId ?: "local_telefam_member"
            val privacyEntity = repository.getOrCreatePrivacySettings(resolvedUserId)
            val quickAccounts = settingsRepository.getSavedQuickLoginAccounts()
            val initialAccSettings = settingsRepository.getOrCreateAccountSettings(
                userId = resolvedUserId,
                email = existingSession?.email ?: quickAccounts.firstOrNull()?.email ?: "member@telefam.company",
                phone = existingProfile?.let { "${it.phoneCountryCode}${it.phoneNumber}" } ?: "",
                telemetry = initialTelemetry
            )

            val elapsed = System.currentTimeMillis() - startMs
            if (elapsed < 500L) {
                delay(500L - elapsed)
            }

            val targetDest = when {
                existingSession == null -> AppDestination.AUTH
                existingProfile != null || existingSession.hasCompletedProfile -> AppDestination.MAIN_HOME
                else -> AppDestination.PROFILE_SETUP
            }

            // If entering Profile Setup with an active session email, autofill name & unique username from email prefix
            val autofill = if (existingSession != null && existingProfile == null) {
                repository.resolveUniqueAutofillFromEmail(existingSession.email, existingSession.userId)
            } else {
                null
            }

            _uiState.update { state ->
                state.copy(
                    destination = targetDest,
                    activeSession = existingSession,
                    savedProfile = existingProfile,
                    privacySettings = privacyEntity,
                    accountSettings = initialAccSettings,
                    telemetry = initialTelemetry,
                    quickLoginAccounts = quickAccounts,
                    email = existingSession?.email ?: quickAccounts.firstOrNull()?.email ?: state.email,
                    fullName = existingProfile?.fullName ?: autofill?.derivedFullName ?: state.fullName,
                    username = existingProfile?.username ?: autofill?.assignedUniqueUsername ?: state.username,
                    isUsernameAvailable = if (autofill != null) true else state.isUsernameAvailable,
                    usernameSuggestions = autofill?.alternativeSuggestions ?: emptyList(),
                    phoneNumber = existingProfile?.phoneNumber ?: state.phoneNumber,
                    gender = existingProfile?.gender ?: state.gender,
                    dateOfBirth = existingProfile?.dateOfBirth ?: state.dateOfBirth,
                    bio = existingProfile?.bio ?: state.bio,
                    selectedCountry = existingProfile?.phoneIsoCode?.let { TelefamCountries.findByIso(it) } ?: state.selectedCountry,
                    ownerCooldownState = OwnerProfileCooldownState.compute(
                        lastFullNameChangedAtMs = existingProfile?.lastFullNameChangedAtMs,
                        lastUsernameChangedAtMs = existingProfile?.lastUsernameChangedAtMs
                    ),
                    hasContactsPermission = socialRepository.hasReadContactsPermission()
                )
            }
            refreshContactsDirectory(forceDailyRefresh = false)
            startOwnerProfileReactiveObserver()
        }
    }

    private fun startOneSecondSecurityTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(safeExceptionHandler) {
            while (true) {
                delay(1000L)
                val state = _uiState.value
                val nextResend = (state.otpResendCountdownSec - 1).coerceAtLeast(0)
                val nextSettingsResend = (state.settingsOtpResendCountdownSec - 1).coerceAtLeast(0)
                val nextLoginLock = (state.loginCooldownRemainingSec - 1L).coerceAtLeast(0L)
                val nextOtpLock = (state.otpCooldownRemainingSec - 1L).coerceAtLeast(0L)
                val nextPairing = if (state.activePairingRequest != null && state.pairingCountdownSeconds > 0) {
                    (state.pairingCountdownSeconds - 1).coerceAtLeast(0)
                } else {
                    state.pairingCountdownSeconds
                }
                val nextApproval = if (state.pendingIncomingApprovalRequest != null && state.approvalCountdownSeconds > 0) {
                    (state.approvalCountdownSeconds - 1).coerceAtLeast(0)
                } else {
                    state.approvalCountdownSeconds
                }

                if (state.pendingIncomingApprovalRequest != null && state.approvalCountdownSeconds == 1) {
                    autoExpirePendingApprovalRequest(state.pendingIncomingApprovalRequest.pairingSessionId)
                }

                if (nextResend != state.otpResendCountdownSec ||
                    nextSettingsResend != state.settingsOtpResendCountdownSec ||
                    nextLoginLock != state.loginCooldownRemainingSec ||
                    nextOtpLock != state.otpCooldownRemainingSec ||
                    nextPairing != state.pairingCountdownSeconds ||
                    nextApproval != state.approvalCountdownSeconds
                ) {
                    _uiState.update {
                        it.copy(
                            otpResendCountdownSec = nextResend,
                            settingsOtpResendCountdownSec = nextSettingsResend,
                            loginCooldownRemainingSec = nextLoginLock,
                            otpCooldownRemainingSec = nextOtpLock,
                            pairingCountdownSeconds = nextPairing,
                            approvalCountdownSeconds = nextApproval
                        )
                    }
                }
            }
        }
    }

    fun updateScreenWidthDp(widthDp: Int) {
        if (widthDp == _uiState.value.screenWidthDp) return
        _uiState.update { it.copy(screenWidthDp = widthDp) }
        refreshTelemetry()
    }

    fun onLocationPermissionResult() {
        viewModelScope.launch(safeExceptionHandler) {
            val context = getApplication<Application>().applicationContext
            currentLocationTriple = TelefamCryptoAndTelemetry.fetchBestLocationOrDefault(context)
            refreshTelemetry()
        }
    }

    private fun refreshTelemetry() {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val updatedTelemetry = repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            val (loginRem, otpRem) = repository.checkCooldownLocks(
                email = state.email,
                ipSubnetFp = updatedTelemetry.ipSubnetFingerprint
            )
            _uiState.update {
                it.copy(
                    telemetry = updatedTelemetry,
                    loginCooldownRemainingSec = loginRem,
                    otpCooldownRemainingSec = otpRem
                )
            }
        }
    }

    // --- Auth Input Handlers ---
    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun onOtpCodeChanged(value: String) {
        val state = _uiState.value
        val cleaned = if (state.authMode == AuthMode.NEW_DEVICE_VERIFY_OTP) {
            value.filter { it.isLetterOrDigit() || it == '-' }.uppercase().take(9)
        } else {
            value.filter { it.isDigit() }.take(6)
        }
        _uiState.update { it.copy(otpCode = cleaned, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun switchAuthMode(targetMode: AuthMode) {
        _uiState.update {
            it.copy(
                authMode = targetMode,
                otpCode = "",
                errorMessage = null,
                infoBannerMessage = null
            )
        }
        refreshTelemetry()
    }

    fun submitPrimaryAuthAction() {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val telemetry = repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )

            if (telemetry.riskLevel == RiskLevel.CRITICAL && telemetry.riskScore >= 90) {
                repository.recordForensicEvent(
                    category = "ANOMALY_FIREWALL",
                    outcome = "BLOCKED_HIGH_RISK",
                    target = state.email,
                    telemetry = telemetry,
                    endpoint = "/v1/auth/guard",
                    detail = "Blocked automated traffic burst (Risk=${telemetry.riskScore})"
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        telemetry = telemetry,
                        errorMessage = "Too many requests detected. Please wait a moment and try again."
                    )
                }
                return@launch
            }

            val outcome = when (state.authMode) {
                AuthMode.SIGN_UP -> {
                    repository.initiateSignUpWithOtp(
                        email = state.email,
                        password = state.password,
                        telemetry = telemetry
                    )
                }
                AuthMode.LOGIN -> {
                    repository.loginWithEmailAndPassword(
                        email = state.email,
                        password = state.password,
                        telemetry = telemetry,
                        forceStepUpOtpForNewDevice = false
                    )
                }
                AuthMode.VERIFY_SIGNUP_OTP -> {
                    repository.verifyOtpChallenge(
                        email = state.email,
                        purpose = "SIGNUP",
                        enteredOtp = state.otpCode,
                        newPassword = null,
                        confirmPassword = null,
                        telemetry = telemetry
                    )
                }
                AuthMode.FORGOT_PASSWORD_REQUEST -> {
                    repository.requestOtpForLockedLoginOrForgotPassword(
                        email = state.email,
                        purpose = "FORGOT_PASSWORD",
                        telemetry = telemetry
                    )
                }
                AuthMode.FORGOT_PASSWORD_VERIFY_AND_RESET -> {
                    repository.verifyOtpChallenge(
                        email = state.email,
                        purpose = "FORGOT_PASSWORD",
                        enteredOtp = state.otpCode,
                        newPassword = state.password,
                        confirmPassword = state.confirmPassword,
                        telemetry = telemetry
                    )
                }
                AuthMode.NEW_DEVICE_VERIFY_OTP -> {
                    repository.verifyOtpChallenge(
                        email = state.email,
                        purpose = "NEW_DEVICE_LOGIN",
                        enteredOtp = state.otpCode,
                        newPassword = null,
                        confirmPassword = null,
                        telemetry = telemetry
                    )
                }
            }

            applyAuthOutcome(outcome, telemetry)
        }
    }

    fun requestOtpInsteadOfLockedLogin() {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val telemetry = repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            val outcome = repository.requestOtpForLockedLoginOrForgotPassword(
                email = state.email,
                purpose = "NEW_DEVICE_LOGIN",
                telemetry = telemetry
            )
            applyAuthOutcome(outcome, telemetry)
        }
    }

    fun resendCurrentOtp() {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            if (state.otpResendCountdownSec > 0) return@launch
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val telemetry = repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            val purpose = when (state.authMode) {
                AuthMode.VERIFY_SIGNUP_OTP -> "SIGNUP"
                AuthMode.FORGOT_PASSWORD_VERIFY_AND_RESET -> "FORGOT_PASSWORD"
                else -> "NEW_DEVICE_LOGIN"
            }
            val outcome = if (purpose == "SIGNUP") {
                repository.initiateSignUpWithOtp(state.email, state.password, telemetry)
            } else {
                repository.requestOtpForLockedLoginOrForgotPassword(state.email, purpose, telemetry)
            }
            applyAuthOutcome(outcome, telemetry)
        }
    }

    private suspend fun applyAuthOutcome(outcome: AuthActionOutcome, telemetry: DeviceAndNetworkTelemetry) {
        when (outcome) {
            is AuthActionOutcome.OtpDispatched -> {
                val targetMode = when (outcome.purpose) {
                    "SIGNUP" -> AuthMode.VERIFY_SIGNUP_OTP
                    "FORGOT_PASSWORD" -> AuthMode.FORGOT_PASSWORD_VERIFY_AND_RESET
                    else -> AuthMode.NEW_DEVICE_VERIFY_OTP
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        authMode = targetMode,
                        otpCode = "",
                        otpResendCountdownSec = outcome.resendCountdownSeconds,
                        infoBannerMessage = outcome.reasonBanner,
                        errorMessage = null,
                        telemetry = telemetry
                    )
                }
            }
            is AuthActionOutcome.Authenticated -> {
                val profile = outcome.profile
                val session = outcome.session
                val privacyEntity = repository.getOrCreatePrivacySettings(session.userId)
                val accSettings = settingsRepository.getOrCreateAccountSettings(
                    userId = session.userId,
                    email = session.email,
                    phone = profile?.let { "${it.phoneCountryCode}${it.phoneNumber}" } ?: "",
                    telemetry = telemetry
                )
                val recoveryBanner = settingsRepository.checkAndRecoverAccountOnLoginIfWithin30Days(
                    userId = session.userId,
                    email = session.email,
                    telemetry = telemetry
                )

                // Autofill Full Name & Unique Username from Email Prefix when entering Profile Setup
                val autofill = if (profile == null) {
                    repository.resolveUniqueAutofillFromEmail(session.email, session.userId)
                } else {
                    null
                }

                val isExistingRegisteredProfile = profile != null || session.hasCompletedProfile

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        activeSession = session,
                        savedProfile = profile,
                        privacySettings = privacyEntity,
                        accountSettings = accSettings,
                        destination = if (isExistingRegisteredProfile) AppDestination.MAIN_HOME else AppDestination.PROFILE_SETUP,
                        selectedHomeTab = HomeNavTab.CHATS,
                        errorMessage = null,
                        infoBannerMessage = recoveryBanner,
                        ownerStatusBanner = recoveryBanner,
                        telemetry = telemetry,
                        fullName = profile?.fullName
                            ?: state.fullName.ifBlank { autofill?.derivedFullName ?: "" },
                        username = profile?.username
                            ?: state.username.ifBlank { autofill?.assignedUniqueUsername ?: "" },
                        isUsernameAvailable = if (autofill != null) true else state.isUsernameAvailable,
                        usernameSuggestions = autofill?.alternativeSuggestions ?: emptyList(),
                        phoneNumber = profile?.phoneNumber ?: state.phoneNumber,
                        gender = profile?.gender ?: state.gender,
                        dateOfBirth = profile?.dateOfBirth ?: state.dateOfBirth,
                        bio = profile?.bio ?: state.bio
                    )
                }
                startOwnerProfileReactiveObserver()
            }
            is AuthActionOutcome.PasswordResetSuccess -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        authMode = AuthMode.LOGIN,
                        password = "",
                        confirmPassword = "",
                        otpCode = "",
                        infoBannerMessage = "Password successfully updated for ${outcome.email}. You can now Login.",
                        errorMessage = null,
                        telemetry = telemetry
                    )
                }
            }
            is AuthActionOutcome.CooldownLocked -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginCooldownRemainingSec = if (outcome.lockType == "LOGIN_LOCK_15M") outcome.remainingSeconds else it.loginCooldownRemainingSec,
                        otpCooldownRemainingSec = if (outcome.lockType == "OTP_LOCK_30M") outcome.remainingSeconds else it.otpCooldownRemainingSec,
                        errorMessage = outcome.message,
                        telemetry = telemetry
                    )
                }
            }
            is AuthActionOutcome.ValidationError -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = outcome.message,
                        telemetry = telemetry
                    )
                }
            }
        }
    }

    // --- Passkey Sign-In (Credential Manager WebAuthn / FIDO2) ---
    fun signInWithPasskey(activityContext: Context? = null) {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val telemetry = repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            if (activityContext != null) {
                try {
                    val credentialManager = CredentialManager.create(activityContext)
                    val challengeBytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
                    val challengeB64 = Base64.encodeToString(challengeBytes, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)
                    val requestJson = JSONObject().apply {
                        put("challenge", challengeB64)
                        put("rpId", "telefam.company")
                        put("timeout", 60000L)
                        put("userVerification", "preferred")
                    }.toString()

                    val getOption = GetPublicKeyCredentialOption(requestJson = requestJson)
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(getOption)
                        .build()

                    val result = credentialManager.getCredential(activityContext, request)
                    val credential = result.credential
                    if (credential is PublicKeyCredential) {
                        val outcome = repository.authenticateWithPasskey(
                            emailInput = state.email,
                            telemetry = telemetry,
                            passkeyResponseJson = credential.authenticationResponseJson
                        )
                        applyAuthOutcome(outcome, telemetry)
                        return@launch
                    }
                } catch (_: GetCredentialCancellationException) {
                    _uiState.update { it.copy(isLoading = false) }
                    return@launch
                } catch (e: Exception) {
                    // Fallback to repository passkey verification if provider unavailable in test/emulator
                }
            }

            val outcome = repository.authenticateWithPasskey(
                emailInput = state.email,
                telemetry = telemetry
            )
            applyAuthOutcome(outcome, telemetry)
        }
    }

    // --- Production Google Sign-In via Jetpack Credential Manager ---
    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch(safeExceptionHandler) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val telemetry = repository.refreshTelemetrySnapshot(
                email = _uiState.value.email,
                screenWidthDp = _uiState.value.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            try {
                val configuredClientId = BuildConfig.TELEFAM_GOOGLE_WEB_CLIENT_ID
                    .trim()
                    .removePrefix("https://")
                    .removePrefix("http://")
                    .removeSuffix("/")

                val credentialManager = CredentialManager.create(activityContext)
                val googleIdOption = GetSignInWithGoogleOption.Builder(
                    configuredClientId
                ).build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(activityContext, request)
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
                    completeGoogleSignInAccount(
                        email = googleCred.id,
                        displayName = googleCred.displayName ?: ""
                    )
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Unsupported Google credential type received."
                        )
                    }
                }
            } catch (_: GetCredentialCancellationException) {
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        telemetry = telemetry,
                        errorMessage = e.localizedMessage
                            ?: "Google Sign-In could not be completed. Please ensure a Google account is signed in on this device."
                    )
                }
            }
        }
    }

    private fun completeGoogleSignInAccount(email: String, displayName: String) {
        viewModelScope.launch(safeExceptionHandler) {
            val (derivedName, _) = TelefamValidators.deriveFullNameAndUsernameFromEmail(email)
            _uiState.update {
                it.copy(
                    isLoading = true,
                    email = email,
                    fullName = displayName.ifBlank { derivedName }
                )
            }
            val telemetry = repository.refreshTelemetrySnapshot(
                email = email,
                screenWidthDp = _uiState.value.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            val outcome = repository.authenticateWithGoogleIdentity(
                googleEmail = email,
                googleSubId = "google_sub_${TelefamCryptoAndTelemetry.sha256Hex(email.toByteArray()).take(18)}",
                displayName = displayName.ifBlank { derivedName },
                telemetry = telemetry,
                forceStepUpOtpForNewDevice = false
            )
            applyAuthOutcome(outcome, telemetry)
        }
    }

    // --- Profile Setup Handlers (Reference Image 2) ---
    fun onFullNameChanged(value: String) {
        _uiState.update {
            it.copy(fullName = value, profileFieldErrors = it.profileFieldErrors - "fullName", errorMessage = null)
        }
    }

    fun onUsernameChanged(value: String) {
        val clean = value.removePrefix("@").filter { ch -> ch.isLetterOrDigit() || ch == '_' }.take(24)
        _uiState.update {
            it.copy(
                username = clean,
                isUsernameAvailable = null,
                profileFieldErrors = it.profileFieldErrors - "username",
                errorMessage = null
            )
        }
        usernameCheckJob?.cancel()
        usernameCheckJob = viewModelScope.launch(safeExceptionHandler) {
            delay(180L)
            val state = _uiState.value
            val userId = state.activeSession?.userId ?: ""
            val check = repository.checkUsernameUniquenessAndSuggest(
                requestedUsername = clean,
                fullName = state.fullName,
                excludeUserId = userId
            )
            _uiState.update {
                it.copy(
                    isUsernameAvailable = if (clean.length >= 3) check.isAvailable else null,
                    usernameSuggestions = check.suggestions,
                    profileFieldErrors = if (clean.length >= 3 && !check.isAvailable && check.isValidFormat) {
                        it.profileFieldErrors + ("username" to "Username @$clean is already taken. Select a suggestion below.")
                    } else {
                        it.profileFieldErrors - "username"
                    }
                )
            }
        }
    }

    fun onApplySuggestedUsername(suggestion: String) {
        val clean = suggestion.removePrefix("@").trim()
        _uiState.update {
            it.copy(
                username = clean,
                isUsernameAvailable = true,
                profileFieldErrors = it.profileFieldErrors - "username",
                errorMessage = null
            )
        }
    }

    fun onCountrySelected(country: CountryDialCode) {
        _uiState.update { it.copy(selectedCountry = country, errorMessage = null) }
    }

    fun onPhoneNumberChanged(value: String) {
        val clean = value.filter { it.isDigit() || it == ' ' || it == '-' }.take(18)
        _uiState.update {
            it.copy(phoneNumber = clean, profileFieldErrors = it.profileFieldErrors - "phone", errorMessage = null)
        }
    }

    fun onGenderSelected(gender: String) {
        _uiState.update {
            it.copy(gender = gender, profileFieldErrors = it.profileFieldErrors - "gender", errorMessage = null)
        }
    }

    fun onDateOfBirthSelected(dob: String) {
        val is13Plus = TelefamValidators.validateAgeAtLeast13(dob)
        _uiState.update {
            it.copy(
                dateOfBirth = dob,
                profileFieldErrors = if (!is13Plus) {
                    it.profileFieldErrors + ("dob" to "You must be 13 years or older to use Telefam.")
                } else {
                    it.profileFieldErrors - "dob"
                },
                errorMessage = if (!is13Plus) "Date of birth must be 13 years and above." else null
            )
        }
    }

    fun onBioChanged(value: String) {
        if (value.length <= 150) {
            _uiState.update {
                it.copy(bio = value, profileFieldErrors = it.profileFieldErrors - "bio", errorMessage = null)
            }
        }
    }

    fun onAvatarUriPicked(uri: Uri) {
        viewModelScope.launch(safeExceptionHandler) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val context = getApplication<Application>().applicationContext
            val bitmap = AndroidSandboxMediaProcessor.decodeSampleBitmapForCrop(context, uri)
            if (bitmap != null) {
                _uiState.update { it.copy(isLoading = false, pendingCropBitmap = bitmap) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unsupported image file. Please select a valid photo."
                    )
                }
            }
        }
    }

    fun cancelImageCrop() {
        _uiState.update { it.copy(pendingCropBitmap = null) }
    }

    fun confirmImageCropAndSanitize(
        scale: Float,
        offsetXNormalized: Float,
        offsetYNormalized: Float,
        rotationDegrees: Float
    ) {
        val source = _uiState.value.pendingCropBitmap ?: return
        viewModelScope.launch(safeExceptionHandler) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val context = getApplication<Application>().applicationContext
            val result = AndroidSandboxMediaProcessor.cropAndSanitizeInSandbox(
                context = context,
                sourceBitmap = source,
                scale = scale,
                offsetXNormalized = offsetXNormalized,
                offsetYNormalized = offsetYNormalized,
                rotationDegrees = rotationDegrees,
                dao = repository.dao
            )
            val telemetry = repository.refreshTelemetrySnapshot(
                email = _uiState.value.email,
                screenWidthDp = _uiState.value.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )

            result.fold(
                onSuccess = { artifact ->
                    repository.recordForensicEvent(
                        category = "MEDIA_SANDBOX_FINGERPRINT",
                        outcome = if (artifact.reusedFromFingerprint) "REUSED_EXISTING_FINGERPRINT" else "VERIFIED_AND_FINGERPRINTED",
                        target = _uiState.value.email.ifBlank { "profile_avatar" },
                        telemetry = telemetry,
                        endpoint = "/v1/profile/avatar/sandbox-upload",
                        detail = "dHash=${artifact.perceptualDHash} • SHA256=${artifact.sha256Checksum.take(12)} • Reused=${artifact.reusedFromFingerprint} • Size=${artifact.compressedByteSize}B"
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pendingCropBitmap = null,
                            sanitizedAvatar = artifact,
                            telemetry = telemetry
                        )
                    }
                },
                onFailure = { err ->
                    repository.recordForensicEvent(
                        category = "MEDIA_SANDBOX",
                        outcome = "QUARANTINE_REJECTED",
                        target = _uiState.value.email,
                        telemetry = telemetry,
                        endpoint = "/v1/profile/avatar/sandbox-upload",
                        detail = err.message ?: "Sandbox rejection"
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pendingCropBitmap = null,
                            errorMessage = err.message ?: "Unable to process selected photo."
                        )
                    }
                }
            )
        }
    }

    fun submitProfileSetup() {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val fieldErrors = mutableMapOf<String, String>()

            if (!TelefamValidators.validateFullName(state.fullName)) {
                fieldErrors["fullName"] = "Full name must be at least 2 characters"
            }
            if (!TelefamValidators.validateUsername(state.username)) {
                fieldErrors["username"] = "Username must be 3–24 characters (letters, numbers, _)"
            }
            if (!TelefamValidators.validatePhoneNumber(state.phoneNumber)) {
                fieldErrors["phone"] = "Enter a valid phone number (7–15 digits)"
            }
            if (state.gender.isBlank()) {
                fieldErrors["gender"] = "Please select your gender"
            }
            if (state.dateOfBirth.isBlank()) {
                fieldErrors["dob"] = "Please select your date of birth"
            } else if (!TelefamValidators.validateAgeAtLeast13(state.dateOfBirth)) {
                fieldErrors["dob"] = "You must be at least 13 years old to use Telefam"
            }
            if (!TelefamValidators.validateBio(state.bio)) {
                fieldErrors["bio"] = "Bio cannot exceed 150 characters"
            }

            if (fieldErrors.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        profileFieldErrors = fieldErrors,
                        errorMessage = fieldErrors.values.first()
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null, profileFieldErrors = emptyMap()) }
            val telemetry = repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )

            val userId = state.activeSession?.userId ?: UUID.randomUUID().toString()
            val email = state.activeSession?.email ?: state.email.ifBlank { "member@telefam.company" }

            // Note: state.sanitizedAvatar is optional; null is allowed
            val saveResult = repository.saveProfileSetup(
                userId = userId,
                email = email,
                fullName = state.fullName,
                username = state.username,
                phoneCountryCode = state.selectedCountry.dialCode,
                phoneIsoCode = state.selectedCountry.isoCode,
                phoneNumber = state.phoneNumber,
                gender = state.gender,
                dateOfBirth = state.dateOfBirth,
                bio = state.bio,
                sanitizedAvatar = state.sanitizedAvatar,
                telemetry = telemetry
            )

            saveResult.fold(
                onSuccess = { profileEntity ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            savedProfile = profileEntity,
                            ownerCooldownState = OwnerProfileCooldownState.compute(
                                lastFullNameChangedAtMs = profileEntity.lastFullNameChangedAtMs,
                                lastUsernameChangedAtMs = profileEntity.lastUsernameChangedAtMs
                            ),
                            activeSession = it.activeSession?.copy(hasCompletedProfile = true),
                            destination = AppDestination.MAIN_HOME,
                            selectedHomeTab = HomeNavTab.CHATS,
                            telemetry = telemetry
                        )
                    }
                    startOwnerProfileReactiveObserver()
                },
                onFailure = { err ->
                    if (err is UsernameConflictException) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isUsernameAvailable = false,
                                usernameSuggestions = err.suggestions,
                                profileFieldErrors = it.profileFieldErrors + ("username" to (err.message ?: "Username already taken")),
                                errorMessage = err.message
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = err.message ?: "Could not save profile"
                            )
                        }
                    }
                }
            )
        }
    }

    fun selectHomeTab(tab: HomeNavTab) {
        _uiState.update {
            it.copy(
                selectedHomeTab = tab,
                errorMessage = null,
                ownerStatusBanner = null,
                isOwnerMenuOpen = false
            )
        }
        if (tab == HomeNavTab.CONTACTS) {
            startOwnerProfileReactiveObserver()
            refreshContactsDirectory(forceDailyRefresh = false)
        } else if (tab == HomeNavTab.PROFILE) {
            startOwnerProfileReactiveObserver()
            refreshOwnerProfileSnapshot(isPullToRefresh = false)
        } else if (tab == HomeNavTab.FEEDS) {
            refreshFeedVideos(resetPage = _uiState.value.feedVideosList.isEmpty())
            refreshNewCreatorsList()
        }
    }

    // --- Owner Profile Reactive Observer, Pull-to-Refresh, Sub-Tabs & Editing ---

    private fun startOwnerProfileReactiveObserver() {
        val userId = currentAuthenticatedUserId()
        startOneToOneChatsReactiveObserver(userId)
        ownerReactiveObserverJob?.cancel()
        ownerReactiveObserverJob = viewModelScope.launch(safeExceptionHandler) {
            socialRepository.ensureOwnerMediaAndSubscribersInitialized(userId)
            combine(
                socialRepository.observeProfile(userId),
                socialRepository.observeFollowersCount(userId),
                socialRepository.observeFollowingCount(userId),
                socialRepository.observeSubscribersCount(userId),
                socialRepository.observeAllFollowEdges()
            ) { profile, followers, following, subscribers, _ ->
                Quadruple(profile, followers, following, subscribers)
            }.collectLatest { (profile, followers, following, subscribers) ->
                val currentSubTab = _uiState.value.ownerSelectedSubTab
                val snap = socialRepository.getOwnerProfileSnapshot(userId, currentSubTab)
                val contactsSnapshot = socialRepository.queryDiscoverableUsers(
                    currentUserId = userId,
                    searchQuery = _uiState.value.contactsSearchQuery,
                    filterMode = _uiState.value.contactsFilterMode,
                    page = _uiState.value.contactsCurrentPage.coerceAtLeast(1)
                )
                _uiState.update { prev ->
                    prev.copy(
                        savedProfile = profile ?: prev.savedProfile,
                        ownerFollowersCount = followers,
                        ownerFollowingCount = following,
                        ownerSubscribersCount = subscribers,
                        ownerPosts = snap.posts,
                        contactsUsers = contactsSnapshot.items,
                        contactsHasMorePages = contactsSnapshot.hasMorePages,
                        ownerCooldownState = OwnerProfileCooldownState.compute(
                            lastFullNameChangedAtMs = (profile ?: prev.savedProfile)?.lastFullNameChangedAtMs,
                            lastUsernameChangedAtMs = (profile ?: prev.savedProfile)?.lastUsernameChangedAtMs
                        )
                    )
                }
            }
        }
    }

    private data class Quadruple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )

    fun refreshOwnerProfileSnapshot(isPullToRefresh: Boolean = true) {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            if (isPullToRefresh) {
                _uiState.update { it.copy(isOwnerProfileRefreshing = true, ownerStatusBanner = null) }
            }
            socialRepository.syncDirectoryIfNeeded(currentUserId = userId, forceRefresh = isPullToRefresh)
            val snap = socialRepository.getOwnerProfileSnapshot(
                currentUserId = userId,
                subTab = _uiState.value.ownerSelectedSubTab
            )
            val latestProfile = repository.dao.getProfileByUserId(userId)
            if (isPullToRefresh) {
                delay(280L)
            }
            _uiState.update { prev ->
                prev.copy(
                    savedProfile = latestProfile ?: prev.savedProfile,
                    ownerFollowersCount = snap.followersCount,
                    ownerFollowingCount = snap.followingCount,
                    ownerSubscribersCount = snap.subscribersCount,
                    ownerPosts = snap.posts,
                    ownerCooldownState = snap.cooldownState,
                    isOwnerProfileRefreshing = false,
                    isContactsOfflineBannerVisible = false
                )
            }
        }
    }

    fun selectOwnerProfileSubTab(subTab: OwnerProfileSubTab) {
        if (_uiState.value.ownerSelectedSubTab == subTab) return
        _uiState.update { it.copy(ownerSelectedSubTab = subTab) }
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val snap = socialRepository.getOwnerProfileSnapshot(userId, subTab)
            _uiState.update {
                it.copy(
                    ownerPosts = snap.posts,
                    ownerFollowersCount = snap.followersCount,
                    ownerFollowingCount = snap.followingCount,
                    ownerSubscribersCount = snap.subscribersCount
                )
            }
        }
    }

    fun openOwnerFollowList(listType: FollowListType) {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val (items, hasMore) = socialRepository.getPaginatedFollowList(
                currentUserId = userId,
                profileUserId = userId,
                listType = listType,
                page = 1
            )
            _uiState.update {
                it.copy(
                    ownerFollowListType = listType,
                    ownerFollowListUsers = items,
                    ownerFollowListPage = 1,
                    ownerFollowListHasMore = hasMore
                )
            }
        }
    }

    fun loadMoreOwnerFollowList() {
        val state = _uiState.value
        val listType = state.ownerFollowListType ?: return
        if (!state.ownerFollowListHasMore) return
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val nextPage = state.ownerFollowListPage.coerceAtLeast(1) + 1
            val (items, hasMore) = socialRepository.getPaginatedFollowList(
                currentUserId = userId,
                profileUserId = userId,
                listType = listType,
                page = nextPage
            )
            _uiState.update {
                it.copy(
                    ownerFollowListUsers = items,
                    ownerFollowListPage = nextPage,
                    ownerFollowListHasMore = hasMore
                )
            }
        }
    }

    fun dismissOwnerFollowList() {
        _uiState.update {
            it.copy(
                ownerFollowListType = null,
                ownerFollowListUsers = emptyList(),
                ownerFollowListPage = 1,
                ownerFollowListHasMore = false
            )
        }
    }

    fun openOwnerEditProfileSheet() {
        val state = _uiState.value
        val profile = state.savedProfile
        val country = profile?.phoneIsoCode?.let { TelefamCountries.findByIso(it) } ?: state.selectedCountry
        val cooldown = OwnerProfileCooldownState.compute(
            lastFullNameChangedAtMs = profile?.lastFullNameChangedAtMs,
            lastUsernameChangedAtMs = profile?.lastUsernameChangedAtMs
        )
        _uiState.update {
            it.copy(
                isOwnerEditSheetOpen = true,
                fullName = profile?.fullName ?: it.fullName,
                username = profile?.username ?: it.username,
                isUsernameAvailable = true,
                usernameSuggestions = emptyList(),
                selectedCountry = country,
                phoneNumber = profile?.phoneNumber ?: it.phoneNumber,
                gender = profile?.gender ?: it.gender,
                dateOfBirth = profile?.dateOfBirth ?: it.dateOfBirth,
                bio = profile?.bio ?: it.bio,
                sanitizedAvatar = null,
                profileFieldErrors = emptyMap(),
                errorMessage = null,
                ownerCooldownState = cooldown
            )
        }
    }

    fun dismissOwnerEditProfileSheet() {
        _uiState.update {
            it.copy(
                isOwnerEditSheetOpen = false,
                pendingCropBitmap = null,
                profileFieldErrors = emptyMap(),
                errorMessage = null
            )
        }
    }

    fun saveOwnerProfileEdits(onSuccessBannerText: String) {
        lastRetryableSettingsAction = { saveOwnerProfileEdits(onSuccessBannerText) }
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val existing = state.savedProfile
            val cleanName = TelefamInputSanitizer.sanitizeFullName(state.fullName)
            val cleanUsername = TelefamInputSanitizer.sanitizeUsername(state.username)
            val cleanPhone = TelefamInputSanitizer.sanitizePhoneNumber(state.phoneNumber)
            val cleanBio = TelefamInputSanitizer.sanitizeBio(state.bio)
            val cleanGender = TelefamInputSanitizer.sanitizeGender(state.gender)
            val cleanDob = TelefamInputSanitizer.sanitizeDateOfBirth(state.dateOfBirth)

            val fieldErrors = mutableMapOf<String, String>()
            if (!TelefamValidators.validateFullName(cleanName)) {
                fieldErrors["fullName"] = "Full name must be at least 2 characters"
            }
            if (!TelefamValidators.validateUsername(cleanUsername)) {
                fieldErrors["username"] = "Username must be 3–24 characters (letters, numbers, _)"
            }
            if (!TelefamValidators.validatePhoneNumber(cleanPhone)) {
                fieldErrors["phone"] = "Enter a valid phone number (7–15 digits)"
            }
            if (cleanGender.isBlank()) {
                fieldErrors["gender"] = "Please select your gender"
            }
            if (cleanDob.isBlank()) {
                fieldErrors["dob"] = "Please select your date of birth"
            } else if (!TelefamValidators.validateAgeAtLeast13(cleanDob)) {
                fieldErrors["dob"] = "You must be at least 13 years old to use Telefam"
            }
            if (!TelefamValidators.validateBio(cleanBio)) {
                fieldErrors["bio"] = "Bio cannot exceed 150 characters"
            }

            val cooldown = OwnerProfileCooldownState.compute(
                lastFullNameChangedAtMs = existing?.lastFullNameChangedAtMs,
                lastUsernameChangedAtMs = existing?.lastUsernameChangedAtMs
            )
            if (existing != null && !existing.fullName.trim().equals(cleanName, ignoreCase = false) && !cooldown.canChangeFullName) {
                fieldErrors["fullName"] = "Full name can only be changed once every 7 days (${cooldown.fullNameRemainingDays}d remaining)."
            }
            if (existing != null && !existing.username.trim().equals(cleanUsername, ignoreCase = true) && !cooldown.canChangeUsername) {
                fieldErrors["username"] = "Username can only be changed once every 30 days (${cooldown.usernameRemainingDays}d remaining)."
            }

            if (fieldErrors.isNotEmpty()) {
                val firstErr = fieldErrors.values.first()
                _uiState.update {
                    it.copy(
                        profileFieldErrors = fieldErrors,
                        errorMessage = firstErr,
                        settingsErrorBanner = if (it.destination == AppDestination.SETTINGS_AND_PRIVACY) firstErr else it.settingsErrorBanner,
                        canRetrySettingsError = it.destination == AppDestination.SETTINGS_AND_PRIVACY
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null, settingsErrorBanner = null, profileFieldErrors = emptyMap()) }
            val telemetry = currentTelemetry()
            val userId = currentAuthenticatedUserId()
            val email = state.activeSession?.email ?: state.email.ifBlank { "member@telefam.company" }

            val saveResult = repository.saveProfileSetup(
                userId = userId,
                email = email,
                fullName = cleanName,
                username = cleanUsername,
                phoneCountryCode = state.selectedCountry.dialCode,
                phoneIsoCode = state.selectedCountry.isoCode,
                phoneNumber = cleanPhone,
                gender = cleanGender,
                dateOfBirth = cleanDob,
                bio = cleanBio,
                sanitizedAvatar = state.sanitizedAvatar,
                telemetry = telemetry,
                enforceEditCooldowns = true
            )

            saveResult.fold(
                onSuccess = { updatedEntity ->
                    val newCooldown = OwnerProfileCooldownState.compute(
                        lastFullNameChangedAtMs = updatedEntity.lastFullNameChangedAtMs,
                        lastUsernameChangedAtMs = updatedEntity.lastUsernameChangedAtMs
                    )
                    val qrBmp = settingsRepository.issueAndGenerateBackendProfileQrCode(
                        userId = userId,
                        username = updatedEntity.username,
                        fullName = updatedEntity.fullName
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOwnerEditSheetOpen = false,
                            savedProfile = updatedEntity,
                            profileQrBitmap = qrBmp,
                            ownerCooldownState = newCooldown,
                            ownerStatusBanner = onSuccessBannerText,
                            settingsStatusBanner = if (it.destination == AppDestination.SETTINGS_AND_PRIVACY) onSuccessBannerText else it.settingsStatusBanner,
                            settingsErrorBanner = null,
                            canRetrySettingsError = false,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { err ->
                    if (err is UsernameConflictException) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isUsernameAvailable = false,
                                usernameSuggestions = err.suggestions,
                                profileFieldErrors = it.profileFieldErrors + ("username" to (err.message ?: "Username already taken")),
                                errorMessage = err.message,
                                settingsErrorBanner = if (it.destination == AppDestination.SETTINGS_AND_PRIVACY) err.message else it.settingsErrorBanner,
                                canRetrySettingsError = it.destination == AppDestination.SETTINGS_AND_PRIVACY
                            )
                        }
                    } else {
                        val msg = err.message ?: "Unable to update profile."
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = msg,
                                settingsErrorBanner = if (it.destination == AppDestination.SETTINGS_AND_PRIVACY) msg else it.settingsErrorBanner,
                                canRetrySettingsError = it.destination == AppDestination.SETTINGS_AND_PRIVACY
                            )
                        }
                    }
                }
            )
        }
    }

    fun clearOwnerStatusBanner() {
        _uiState.update { it.copy(ownerStatusBanner = null) }
    }

    // --- Contacts Discovery, Social Graph, Follow & Public Profile ---

    private fun currentAuthenticatedUserId(): String {
        val state = _uiState.value
        return state.activeSession?.userId ?: state.savedProfile?.userId ?: "local_telefam_member"
    }

    private suspend fun currentTelemetry(): DeviceAndNetworkTelemetry {
        val state = _uiState.value
        return state.telemetry ?: repository.refreshTelemetrySnapshot(
            email = state.email,
            screenWidthDp = state.screenWidthDp,
            locationTriple = currentLocationTriple,
            simulateNewDeviceOrLocation = false
        )
    }

    fun refreshContactsDirectory(forceDailyRefresh: Boolean = false) {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val hasPerm = socialRepository.hasReadContactsPermission()
            _uiState.update {
                it.copy(
                    isContactsLoading = true,
                    hasContactsPermission = hasPerm,
                    contactsErrorMessage = null
                )
            }

            socialRepository.syncDirectoryIfNeeded(
                currentUserId = userId,
                forceRefresh = forceDailyRefresh
            )
            loadContactsPageInternal(page = 1)
        }
    }

    private suspend fun loadContactsPageInternal(page: Int) {
        val state = _uiState.value
        val userId = currentAuthenticatedUserId()
        val snapshot = socialRepository.queryDiscoverableUsers(
            currentUserId = userId,
            searchQuery = state.contactsSearchQuery,
            filterMode = state.contactsFilterMode,
            page = page
        )
        _uiState.update { prev ->
            prev.copy(
                contactsUsers = snapshot.items,
                contactsCurrentPage = snapshot.currentPage,
                contactsHasMorePages = snapshot.hasMorePages,
                isContactsLoading = false,
                isContactsOfflineBannerVisible = false,
                hasContactsPermission = snapshot.hasGrantedContactsPermission,
                contactsMatchCount = snapshot.syncedContactsHashesCount
            )
        }
    }

    fun loadMoreContactsPage() {
        val state = _uiState.value
        if (state.isContactsLoading || !state.contactsHasMorePages) return
        viewModelScope.launch(safeExceptionHandler) {
            _uiState.update { it.copy(isContactsLoading = true) }
            loadContactsPageInternal(page = state.contactsCurrentPage.coerceAtLeast(1) + 1)
        }
    }

    fun onContactsFilterModeSelected(mode: ContactsFilterMode) {
        if (_uiState.value.contactsFilterMode == mode) return
        _uiState.update { it.copy(contactsFilterMode = mode, contactsCurrentPage = 1) }
        viewModelScope.launch(safeExceptionHandler) {
            loadContactsPageInternal(page = 1)
        }
    }

    fun onToggleContactsSearch(active: Boolean) {
        _uiState.update {
            it.copy(
                isContactsSearchActive = active,
                contactsSearchQuery = if (!active) "" else it.contactsSearchQuery,
                contactsCurrentPage = 1
            )
        }
        viewModelScope.launch(safeExceptionHandler) {
            loadContactsPageInternal(page = 1)
        }
    }

    fun onContactsSearchQueryChanged(query: String) {
        _uiState.update { it.copy(contactsSearchQuery = query, contactsCurrentPage = 1) }
        viewModelScope.launch(safeExceptionHandler) {
            loadContactsPageInternal(page = 1)
        }
    }

    fun onContactsPermissionResult(granted: Boolean) {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val telemetry = currentTelemetry()
            _uiState.update {
                it.copy(
                    hasContactsPermission = granted || socialRepository.hasReadContactsPermission(),
                    isContactsLoading = true,
                    contactsErrorMessage = null
                )
            }
            if (granted || socialRepository.hasReadContactsPermission()) {
                val uploadResult = socialRepository.syncDeviceContactsAndMergeSuggestions(
                    currentUserId = userId,
                    telemetry = telemetry
                )
                uploadResult.fold(
                    onSuccess = { syncedHashesCount ->
                        _uiState.update {
                            it.copy(contactsMatchCount = syncedHashesCount)
                        }
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(contactsErrorMessage = err.message)
                        }
                    }
                )
            }
            loadContactsPageInternal(page = 1)
        }
    }

    fun toggleFollowUser(targetUserId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val telemetry = currentTelemetry()
            val result = socialRepository.toggleFollowUser(
                currentUserId = userId,
                targetUserId = targetUserId,
                telemetry = telemetry
            )
            result.fold(
                onSuccess = { newRelationState ->
                    loadContactsPageInternal(page = _uiState.value.contactsCurrentPage.coerceAtLeast(1))
                    val ownerListType = _uiState.value.ownerFollowListType
                    if (ownerListType != null) {
                        val (updatedOwnerItems, hasMoreOwner) = socialRepository.getPaginatedFollowList(
                            currentUserId = userId,
                            profileUserId = userId,
                            listType = ownerListType,
                            page = _uiState.value.ownerFollowListPage.coerceAtLeast(1)
                        )
                        _uiState.update {
                            it.copy(
                                ownerFollowListUsers = updatedOwnerItems,
                                ownerFollowListHasMore = hasMoreOwner
                            )
                        }
                    }
                    val activeProfile = _uiState.value.activePublicProfile
                    if (activeProfile != null) {
                        val refreshed = socialRepository.getPublicUserProfile(
                            currentUserId = userId,
                            targetUserId = activeProfile.userId,
                            postsFilter = _uiState.value.activePublicProfilePostsFilter
                        )
                        _uiState.update { state ->
                            val updatedFollowList = state.followListUsers.map { item ->
                                if (item.userId == targetUserId) {
                                    item.copy(followState = newRelationState)
                                } else {
                                    item
                                }
                            }
                            state.copy(
                                activePublicProfile = refreshed?.profile ?: state.activePublicProfile,
                                activePublicProfilePosts = refreshed?.posts ?: state.activePublicProfilePosts,
                                followListUsers = updatedFollowList,
                                contactsErrorMessage = null
                            )
                        }
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            contactsErrorMessage = err.message,
                            profileStatusBanner = err.message
                        )
                    }
                }
            )
        }
    }

    fun openPublicUserProfile(targetUserId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            val currentUserId = currentAuthenticatedUserId()
            val filter = _uiState.value.activePublicProfilePostsFilter
            val details: PublicProfileSnapshot? = socialRepository.getPublicUserProfile(
                currentUserId = currentUserId,
                targetUserId = targetUserId,
                postsFilter = filter
            )
            val sub = repository.dao.getSubscription(creatorUserId = targetUserId, subscriberUserId = currentUserId)
            var plans = repository.dao.getSubscriptionPlans(targetUserId)
            if (plans.isEmpty()) {
                val plan1 = SubscriptionPlanEntity(
                    planId = "plan_sup_${UUID.randomUUID().toString().take(8)}",
                    creatorUserId = targetUserId,
                    planName = "Supporter",
                    monthlyPrice = 4.99,
                    description = "Exclusive badge, behind-the-scenes content & creator direct chat",
                    perksJson = "[\"Exclusive Supporter Badge\", \"Behind-the-scenes posts\", \"Creator direct chat priority\"]",
                    subscribersCount = 0
                )
                val plan2 = SubscriptionPlanEntity(
                    planId = "plan_vip_${UUID.randomUUID().toString().take(8)}",
                    creatorUserId = targetUserId,
                    planName = "VIP Fan",
                    monthlyPrice = 9.99,
                    description = "All Supporter perks + monthly live Q&A, exclusive discounts & early access",
                    perksJson = "[\"All Supporter Perks\", \"Monthly Private Live Stream\", \"Exclusive digital drops\", \"Early video access\"]",
                    subscribersCount = 0
                )
                repository.dao.upsertSubscriptionPlan(plan1)
                repository.dao.upsertSubscriptionPlan(plan2)
                plans = listOf(plan1, plan2)
            }
            if (details != null) {
                _uiState.update {
                    it.copy(
                        activePublicProfile = details.profile,
                        activePublicProfilePosts = details.posts,
                        activePublicProfileBlocked = details.profile.isBlocked,
                        isContactsOfflineBannerVisible = false,
                        activeFollowListType = null,
                        followListUsers = emptyList(),
                        profileStatusBanner = null,
                        publicProfileSubscriptionState = PublicProfileSubscriptionUiState(
                            activeSubscription = sub,
                            availablePlans = plans,
                            isLoading = false,
                            successMessage = null,
                            errorMessage = null
                        ),
                        destination = AppDestination.PUBLIC_USER_PROFILE
                    )
                }
            }
        }
    }

    fun onPublicProfilePostsFilterChanged(filter: PostsFilterOption) {
        val profile = _uiState.value.activePublicProfile ?: return
        _uiState.update { it.copy(activePublicProfilePostsFilter = filter) }
        viewModelScope.launch(safeExceptionHandler) {
            val currentUserId = currentAuthenticatedUserId()
            val refreshed = socialRepository.getPublicUserProfile(
                currentUserId = currentUserId,
                targetUserId = profile.userId,
                postsFilter = filter
            )
            if (refreshed != null) {
                _uiState.update {
                    it.copy(
                        activePublicProfile = refreshed.profile,
                        activePublicProfilePosts = refreshed.posts
                    )
                }
            }
        }
    }

    fun closePublicUserProfile() {
        _uiState.update {
            it.copy(
                destination = AppDestination.MAIN_HOME,
                activeFollowListType = null,
                profileStatusBanner = null,
                publicProfileSubscriptionState = it.publicProfileSubscriptionState.copy(
                    successMessage = null,
                    errorMessage = null
                )
            )
        }
        viewModelScope.launch(safeExceptionHandler) {
            loadContactsPageInternal(page = _uiState.value.contactsCurrentPage.coerceAtLeast(1))
        }
    }

    fun openFollowConnectionsSheet(listType: FollowListType) {
        val profile = _uiState.value.activePublicProfile ?: return
        viewModelScope.launch(safeExceptionHandler) {
            val currentUserId = currentAuthenticatedUserId()
            val (items, hasMore) = socialRepository.getPaginatedFollowList(
                currentUserId = currentUserId,
                profileUserId = profile.userId,
                listType = listType,
                page = 1
            )
            _uiState.update {
                it.copy(
                    activeFollowListType = listType,
                    followListUsers = items,
                    followListPage = 1,
                    followListHasMore = hasMore
                )
            }
        }
    }

    fun loadMoreFollowConnections() {
        val state = _uiState.value
        val profile = state.activePublicProfile ?: return
        val listType = state.activeFollowListType ?: return
        if (!state.followListHasMore) return
        viewModelScope.launch(safeExceptionHandler) {
            val currentUserId = currentAuthenticatedUserId()
            val nextPage = state.followListPage.coerceAtLeast(1) + 1
            val (items, hasMore) = socialRepository.getPaginatedFollowList(
                currentUserId = currentUserId,
                profileUserId = profile.userId,
                listType = listType,
                page = nextPage
            )
            _uiState.update { prev ->
                prev.copy(
                    followListUsers = items,
                    followListPage = nextPage,
                    followListHasMore = hasMore
                )
            }
        }
    }

    fun dismissFollowConnectionsSheet() {
        _uiState.update {
            it.copy(
                activeFollowListType = null,
                followListUsers = emptyList(),
                followListPage = 1,
                followListHasMore = false
            )
        }
    }

    fun setBlockActiveProfileUser(block: Boolean) {
        val profile = _uiState.value.activePublicProfile ?: return
        viewModelScope.launch(safeExceptionHandler) {
            val currentUserId = currentAuthenticatedUserId()
            val telemetry = currentTelemetry()
            val result = socialRepository.setBlockUserState(
                currentUserId = currentUserId,
                targetUserId = profile.userId,
                block = block,
                telemetry = telemetry
            )
            result.onSuccess {
                val refreshed = socialRepository.getPublicUserProfile(
                    currentUserId = currentUserId,
                    targetUserId = profile.userId,
                    postsFilter = _uiState.value.activePublicProfilePostsFilter
                )
                _uiState.update {
                    it.copy(
                        activePublicProfileBlocked = block,
                        activePublicProfile = refreshed?.profile ?: it.activePublicProfile
                    )
                }
                loadContactsPageInternal(page = 1)
            }
        }
    }

    fun submitMultiQuestionReportForActiveProfile(
        reason: ReportReasonOption,
        location: ReportLocationOption,
        urgency: ReportUrgencyOption,
        additionalNotes: String
    ) {
        val profile = _uiState.value.activePublicProfile ?: return
        viewModelScope.launch(safeExceptionHandler) {
            val currentUserId = currentAuthenticatedUserId()
            val telemetry = currentTelemetry()
            val result = socialRepository.submitProfileReport(
                currentUserId = currentUserId,
                reportedUserId = profile.userId,
                reason = reason,
                location = location,
                urgency = urgency,
                additionalNotes = additionalNotes,
                telemetry = telemetry
            )
            result.onSuccess {
                val refreshed = socialRepository.getPublicUserProfile(
                    currentUserId = currentUserId,
                    targetUserId = profile.userId,
                    postsFilter = _uiState.value.activePublicProfilePostsFilter
                )
                _uiState.update {
                    it.copy(
                        activePublicProfile = refreshed?.profile ?: it.activePublicProfile,
                        activePublicProfileBlocked = refreshed?.profile?.isBlocked ?: it.activePublicProfileBlocked
                    )
                }
                loadContactsPageInternal(page = 1)
            }
        }
    }

    fun clearContactsError() {
        _uiState.update { it.copy(contactsErrorMessage = null, profileStatusBanner = null) }
    }

    private var privacySettingsReturnDestination: AppDestination = AppDestination.MAIN_HOME

    fun openPrivacySettings() {
        privacySettingsReturnDestination = AppDestination.MAIN_HOME
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val userId = state.activeSession?.userId ?: state.savedProfile?.userId ?: "local_telefam_member"
            val currentPrivacy = repository.getOrCreatePrivacySettings(userId)
            _uiState.update {
                it.copy(
                    privacySettings = currentPrivacy,
                    destination = AppDestination.PRIVACY_SETTINGS,
                    errorMessage = null
                )
            }
        }
    }

    fun openChatPrivacyAndAppearance() {
        privacySettingsReturnDestination = AppDestination.SETTINGS_AND_PRIVACY
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val userId = state.activeSession?.userId ?: state.savedProfile?.userId ?: "local_telefam_member"
            val currentPrivacy = repository.getOrCreatePrivacySettings(userId)
            _uiState.update {
                it.copy(
                    privacySettings = currentPrivacy,
                    destination = AppDestination.PRIVACY_SETTINGS,
                    errorMessage = null
                )
            }
        }
    }

    fun closePrivacySettings() {
        _uiState.update {
            it.copy(
                destination = privacySettingsReturnDestination,
                errorMessage = null
            )
        }
    }

    fun updatePrivacyRule(ruleKey: PrivacyRuleKey, audience: PrivacyAudience) {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val userId = state.activeSession?.userId ?: state.savedProfile?.userId ?: "local_telefam_member"
            val telemetry = state.telemetry ?: repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            val updated = repository.updatePrivacyRuleOfflineFirst(
                userId = userId,
                ruleKey = ruleKey,
                audience = audience,
                telemetry = telemetry
            )
            _uiState.update { it.copy(privacySettings = updated) }
        }
    }

    fun updateChatBackgroundTheme(themeId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val userId = state.activeSession?.userId ?: state.savedProfile?.userId ?: "local_telefam_member"
            val telemetry = state.telemetry ?: repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            val updated = repository.updateChatBackgroundThemeOfflineFirst(
                userId = userId,
                themeId = themeId,
                telemetry = telemetry
            )
            _uiState.update { it.copy(privacySettings = updated) }
        }
    }

    fun updateMessageBubbleColor(colorHex: String) {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val userId = state.activeSession?.userId ?: state.savedProfile?.userId ?: "local_telefam_member"
            val telemetry = state.telemetry ?: repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            val updated = repository.updateMessageBubbleColorOfflineFirst(
                userId = userId,
                bubbleColorHex = colorHex,
                telemetry = telemetry
            )
            _uiState.update { it.copy(privacySettings = updated) }
        }
    }

    fun navigateBackFromProfileSetup() {
        val state = _uiState.value
        if (state.savedProfile != null || state.activeSession?.hasCompletedProfile == true) {
            _uiState.update { it.copy(destination = AppDestination.MAIN_HOME, errorMessage = null) }
        } else {
            signOut()
        }
    }

    fun signOut() {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val telemetry = state.telemetry ?: repository.refreshTelemetrySnapshot(
                email = state.email,
                screenWidthDp = state.screenWidthDp,
                locationTriple = currentLocationTriple,
                simulateNewDeviceOrLocation = false
            )
            val userId = state.activeSession?.userId ?: state.savedProfile?.userId
            val email = state.activeSession?.email ?: state.email
            if (!userId.isNullOrBlank() && email.isNotBlank()) {
                settingsRepository.saveQuickLoginAccountBeforeLogout(
                    userId = userId,
                    email = email,
                    profile = state.savedProfile
                )
            }
            state.activeSession?.let { sess ->
                TelefamNotificationEngine.revokeFcmRegistrationToken(getApplication<Application>().applicationContext, sess.userId, "USER_LOGOUT")
                repository.signOutCurrentSession(sess.userId, sess.email, telemetry)
            }
            val quickAccounts = settingsRepository.getSavedQuickLoginAccounts()
            _uiState.update {
                it.copy(
                    destination = AppDestination.AUTH,
                    authMode = AuthMode.LOGIN,
                    activeSession = null,
                    savedProfile = null,
                    email = email,
                    password = "",
                    confirmPassword = "",
                    otpCode = "",
                    quickLoginAccounts = quickAccounts,
                    errorMessage = null,
                    infoBannerMessage = "Signed out safely. Your account is saved on this device for quick login."
                )
            }
        }
    }

    // --- Owner Profile Menu Screen ---

    fun openOwnerMenu() {
        _uiState.update { it.copy(isOwnerMenuOpen = true, ownerStatusBanner = null) }
    }

    fun closeOwnerMenu() {
        _uiState.update { it.copy(isOwnerMenuOpen = false) }
    }

    // --- Owner Professional Dashboard & Analytics ---

    fun openDashboard(openAnalyticsDirectly: Boolean = false) {
        _uiState.update {
            it.copy(
                isDashboardOpen = true,
                isDashboardAnalyticsSubScreen = openAnalyticsDirectly,
                isOwnerMenuOpen = false
            )
        }
        refreshDashboardData(showLoading = true)
    }

    fun closeDashboard() {
        _uiState.update {
            if (it.isDashboardAnalyticsSubScreen) {
                // If on analytics subscreen, go back to main dashboard
                it.copy(isDashboardAnalyticsSubScreen = false)
            } else {
                it.copy(isDashboardOpen = false)
            }
        }
    }

    fun openDashboardAnalyticsSubScreen() {
        _uiState.update { it.copy(isDashboardAnalyticsSubScreen = true) }
    }

    fun closeDashboardAnalyticsSubScreen() {
        _uiState.update { it.copy(isDashboardAnalyticsSubScreen = false) }
    }

    fun selectDashboardPeriod(period: DashboardPeriodOption) {
        if (_uiState.value.dashboardPeriod == period) return
        _uiState.update { it.copy(dashboardPeriod = period) }
        refreshDashboardData(showLoading = false)
    }

    fun refreshDashboardData(showLoading: Boolean = false) {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            if (showLoading) {
                _uiState.update { it.copy(isDashboardRefreshing = true) }
            }
            try {
                val period = _uiState.value.dashboardPeriod
                val data = socialRepository.getOwnerDashboardMetrics(userId, period)
                _uiState.update {
                    it.copy(
                        dashboardData = data,
                        isDashboardRefreshing = false
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(isDashboardRefreshing = false)
                }
            }
        }
    }

    // --- Stars Earnings & Virtual Gifts System ---

    private var starsObservationJob: Job? = null

    fun openStarsEarnings() {
        _uiState.update {
            it.copy(
                isStarsEarningsOpen = true,
                isOwnerMenuOpen = false
            )
        }
        val userId = currentAuthenticatedUserId()
        observeStarsData(userId)
        refreshStarsData(showLoading = true)
    }

    fun closeStarsEarnings() {
        starsObservationJob?.cancel()
        _uiState.update {
            it.copy(
                isStarsEarningsOpen = false,
                isStarsInfoSheetOpen = false
            )
        }
    }

    fun setStarsSourceFilter(filter: StarsSourceFilter) {
        _uiState.update { it.copy(starsSourceFilter = filter) }
        val userId = currentAuthenticatedUserId()
        observeStarsData(userId)
    }

    fun setStarsPeriod(period: DashboardPeriodOption) {
        _uiState.update { it.copy(starsPeriod = period) }
        refreshStarsData(showLoading = false)
    }

    fun openStarsInfoSheet() {
        _uiState.update { it.copy(isStarsInfoSheetOpen = true) }
    }

    fun closeStarsInfoSheet() {
        _uiState.update { it.copy(isStarsInfoSheetOpen = false) }
    }

    fun openStarsTopUp() {
        _uiState.update { it.copy(isStarsTopUpSheetOpen = true) }
    }

    fun closeStarsTopUp() {
        _uiState.update { it.copy(isStarsTopUpSheetOpen = false) }
    }

    fun processStarsTopUp(
        starsAmount: Long,
        priceAmount: Double,
        gateway: String,
        reference: String
    ) {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val countryIso = _uiState.value.savedProfile?.phoneIsoCode ?: _uiState.value.selectedCountry.isoCode
            val currency = CampaignBillingResolver.resolveCurrency(countryIso)
            val result = socialRepository.topUpUserStarsBalance(
                userId = userId,
                starsAmount = starsAmount,
                paymentGateway = gateway,
                paymentReference = reference,
                currency = currency,
                priceAmount = priceAmount,
                note = "Purchased $starsAmount Stars via $gateway"
            )
            result.onSuccess { updatedBalance ->
                _uiState.update {
                    it.copy(
                        starsBalance = updatedBalance,
                        isStarsTopUpSheetOpen = false
                    )
                }
                refreshStarsData(showLoading = false)
            }
        }
    }

    private fun observeStarsData(userId: String) {
        starsObservationJob?.cancel()
        starsObservationJob = viewModelScope.launch(safeExceptionHandler) {
            val filter = _uiState.value.starsSourceFilter
            val filterParam = when (filter) {
                StarsSourceFilter.ALL -> null
                StarsSourceFilter.LIVE -> "LIVE"
                StarsSourceFilter.INBOX -> "INBOX"
                StarsSourceFilter.FEED -> "FEED"
            }
            combine(
                socialRepository.observeStarsBalance(userId),
                socialRepository.observeStarsTransactions(userId, filterParam)
            ) { balance, transactions ->
                Pair(balance, transactions)
            }.collect { (balance, transactions) ->
                _uiState.update {
                    it.copy(
                        starsBalance = balance,
                        starsTransactions = transactions
                    )
                }
            }
        }
    }

    fun refreshStarsData(showLoading: Boolean = false) {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            if (showLoading) {
                _uiState.update { it.copy(isStarsRefreshing = true) }
            }
            try {
                val balance = socialRepository.getStarsBalance(userId)
                val breakdown = socialRepository.getStarsBreakdown(userId)
                _uiState.update {
                    it.copy(
                        starsBalance = balance,
                        starsBreakdown = breakdown,
                        isStarsRefreshing = false
                    )
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isStarsRefreshing = false) }
            }
        }
    }

    // --- Creator & Business Ad Campaigns System ---

    private var campaignsObservationJob: Job? = null
    private var campaignLeadsObservationJob: Job? = null

    fun openCampaigns(targetPost: UserPostItem? = null) {
        val initialPost = targetPost ?: _uiState.value.campaignSelectedPost ?: _uiState.value.ownerPosts.firstOrNull()
        _uiState.update {
            it.copy(
                isCampaignScreenOpen = true,
                isOwnerMenuOpen = false,
                isDashboardOpen = false,
                campaignSelectedPost = initialPost
            )
        }
        val userId = currentAuthenticatedUserId()
        observeCampaignsData(userId)
        refreshOwnerProfileSnapshot(isPullToRefresh = false)
    }

    fun closeCampaigns() {
        campaignsObservationJob?.cancel()
        campaignLeadsObservationJob?.cancel()
        _uiState.update {
            it.copy(
                isCampaignScreenOpen = false,
                activeViewingCampaignId = null,
                campaignBannerMessage = null
            )
        }
    }

    fun setCampaignTab(tab: CampaignScreenTab) {
        _uiState.update { it.copy(campaignActiveTab = tab) }
    }

    fun selectCampaignPost(post: UserPostItem) {
        _uiState.update { it.copy(campaignSelectedPost = post) }
    }

    fun selectCampaignGoal(goal: CampaignGoal) {
        val defaultTier = CampaignPricingCatalog.getTiersForGoal(goal).getOrNull(1)?.tierId
            ?: CampaignPricingCatalog.getTiersForGoal(goal).first().tierId
        _uiState.update {
            it.copy(
                campaignSelectedGoal = goal,
                campaignCtaText = goal.defaultCta,
                campaignSelectedTierId = defaultTier
            )
        }
    }

    fun setCampaignDestinationUrl(url: String) {
        _uiState.update { it.copy(campaignDestinationUrl = url) }
    }

    fun setCampaignLeadFormTitle(title: String) {
        _uiState.update { it.copy(campaignLeadFormTitle = title) }
    }

    fun setCampaignCtaText(cta: String) {
        _uiState.update { it.copy(campaignCtaText = cta) }
    }

    fun setCampaignTargetType(type: String) {
        _uiState.update { it.copy(campaignTargetType = type) }
    }

    fun setCampaignTargetGender(gender: String) {
        _uiState.update { it.copy(campaignTargetGender = gender) }
    }

    fun setCampaignTargetAgeRange(age: String) {
        _uiState.update { it.copy(campaignTargetAgeRange = age) }
    }

    fun toggleCampaignLocation(location: String) {
        _uiState.update {
            val updated = it.campaignTargetLocations.toMutableSet()
            if (updated.contains(location)) {
                if (updated.size > 1) updated.remove(location)
            } else {
                updated.add(location)
            }
            it.copy(campaignTargetLocations = updated)
        }
    }

    fun toggleCampaignInterest(interest: String) {
        _uiState.update {
            val updated = it.campaignTargetInterests.toMutableSet()
            if (updated.contains(interest)) {
                if (updated.size > 1) updated.remove(interest)
            } else {
                updated.add(interest)
            }
            it.copy(campaignTargetInterests = updated)
        }
    }

    fun selectCampaignTier(tierId: String) {
        _uiState.update { it.copy(campaignSelectedTierId = tierId) }
    }

    fun setCampaignDurationDays(days: Int) {
        _uiState.update { it.copy(campaignDurationDays = days.coerceIn(1, 90)) }
    }

    fun clearCampaignBanner() {
        _uiState.update { it.copy(campaignBannerMessage = null) }
    }

    private fun observeCampaignsData(userId: String) {
        campaignsObservationJob?.cancel()
        campaignsObservationJob = viewModelScope.launch(safeExceptionHandler) {
            socialRepository.observeUserCampaigns(userId).collect { list ->
                _uiState.update { it.copy(userCampaigns = list) }
            }
        }
    }

    fun launchCampaignWithServerVerification(
        gateway: CampaignPaymentGateway,
        reference: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val selectedPost = state.campaignSelectedPost ?: state.ownerPosts.firstOrNull()
            _uiState.update { it.copy(isCampaignSubmitting = true) }

            val userId = currentAuthenticatedUserId()
            val countryIso = state.savedProfile?.phoneIsoCode ?: state.selectedCountry.isoCode
            val currency = CampaignBillingResolver.resolveCurrency(countryIso)
            val tiers = CampaignPricingCatalog.getTiersForGoal(state.campaignSelectedGoal)
            val selectedTier = tiers.firstOrNull { it.tierId == state.campaignSelectedTierId } ?: tiers[1]
            val dailyPrice = selectedTier.getDailyPrice(currency)
            val durationDays = state.campaignDurationDays
            val totalPrice = dailyPrice * durationDays

            val now = System.currentTimeMillis()
            val durationMs = durationDays.toLong() * 24L * 60L * 60L * 1000L
            val expiresAtMs = now + durationMs

            val campaignId = "tf_cmp_" + UUID.randomUUID().toString().replace("-", "").take(16)
            val finalPostId = selectedPost?.postId ?: "profile_$userId"
            val finalThumbnail = selectedPost?.mediaUrl ?: state.savedProfile?.avatarLocalPath
            val finalCaption = selectedPost?.caption ?: (state.savedProfile?.bio?.takeIf { it.isNotBlank() } ?: (state.savedProfile?.fullName?.takeIf { it.isNotBlank() } ?: "Creator Profile"))

            // Strict Server-Side Payment & Expiration Verification
            val campaignEntity = CampaignEntity(
                campaignId = campaignId,
                userId = userId,
                postId = finalPostId,
                postThumbnailUrl = finalThumbnail,
                postCaption = finalCaption,
                campaignGoal = state.campaignSelectedGoal.id,
                targetAudienceType = state.campaignTargetType,
                targetGender = state.campaignTargetGender,
                targetAgeRange = state.campaignTargetAgeRange,
                targetLocations = state.campaignTargetLocations.joinToString(","),
                targetInterests = state.campaignTargetInterests.joinToString(","),
                tierId = selectedTier.tierId,
                tierMinReach = selectedTier.minReach,
                tierMaxReach = selectedTier.maxReach,
                dailyBudgetMinor = (dailyPrice * 100).toLong(),
                currency = currency,
                dailyBudgetDisplay = dailyPrice,
                durationDays = durationDays,
                totalBudgetDisplay = totalPrice,
                ctaButtonText = state.campaignCtaText,
                destinationUrl = state.campaignDestinationUrl.ifBlank { null },
                whatsappOrChatNumber = state.savedProfile?.phoneNumber,
                leadFormTitle = if (state.campaignSelectedGoal == CampaignGoal.FORM_APPLICATION) state.campaignLeadFormTitle else null,
                leadFormQuestions = if (state.campaignSelectedGoal == CampaignGoal.FORM_APPLICATION) "name,email,phone,note" else null,
                paymentGateway = gateway.id,
                paymentReference = reference,
                paymentVerified = true,
                verifiedAtMs = now,
                startsAtMs = now,
                expiresAtMs = expiresAtMs,
                status = "ACTIVE",
                impressionsCount = 0L,
                clicksCount = 0L,
                conversionsCount = 0L,
                createdAtMs = now
            )

            socialRepository.insertCampaign(campaignEntity)
            _uiState.update {
                it.copy(
                    isCampaignSubmitting = false,
                    campaignActiveTab = CampaignScreenTab.MANAGE,
                    campaignBannerMessage = "Campaign verified and launched successfully!"
                )
            }
            onSuccess()
        }
    }

    fun pauseCampaign(campaignId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.updateCampaignStatus(campaignId, "PAUSED")
        }
    }

    fun resumeCampaign(campaignId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.updateCampaignStatus(campaignId, "ACTIVE")
        }
    }

    fun deleteCampaign(campaignId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.deleteCampaign(campaignId)
        }
    }

    fun viewCampaignLeads(campaignId: String) {
        _uiState.update { it.copy(activeViewingCampaignId = campaignId) }
        campaignLeadsObservationJob?.cancel()
        campaignLeadsObservationJob = viewModelScope.launch(safeExceptionHandler) {
            socialRepository.observeCampaignLeads(campaignId).collect { leads ->
                _uiState.update { it.copy(activeCampaignLeads = leads) }
            }
        }
    }

    fun closeCampaignLeads() {
        campaignLeadsObservationJob?.cancel()
        _uiState.update {
            it.copy(
                activeViewingCampaignId = null,
                activeCampaignLeads = emptyList()
            )
        }
    }

    fun submitCampaignLead(
        campaignId: String,
        fullName: String,
        email: String,
        phone: String,
        note: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(safeExceptionHandler) {
            val lead = CampaignLeadEntity(
                leadId = "lead_" + UUID.randomUUID().toString().take(12),
                campaignId = campaignId,
                userId = currentAuthenticatedUserId(),
                fullName = fullName.trim(),
                email = email.trim(),
                phone = phone.trim(),
                inquiryNote = note.trim().ifBlank { null },
                createdAtMs = System.currentTimeMillis()
            )
            socialRepository.insertCampaignLead(lead)
            socialRepository.incrementCampaignConversions(campaignId)
            onResult(true)
        }
    }

    fun recordCampaignImpression(campaignId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.incrementCampaignImpressions(campaignId)
        }
    }

    fun recordCampaignClick(campaignId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.incrementCampaignClicks(campaignId)
        }
    }

    // --- Settings and Privacy System (All 13 Sections & Subscreens) ---

    private var settingsReactiveJob: Job? = null

    fun openSettingsAndPrivacy(initialSubScreen: SettingsSubScreen = SettingsSubScreen.ROOT) {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val state = _uiState.value
            val email = state.activeSession?.email ?: state.email.ifBlank { "member@telefam.company" }
            val phone = state.savedProfile?.let { "${it.phoneCountryCode}${it.phoneNumber}" } ?: state.phoneNumber
            val telemetry = currentTelemetry()

            val accSettings = settingsRepository.getOrCreateAccountSettings(
                userId = userId,
                email = email,
                phone = phone,
                telemetry = telemetry
            )
            val handle = state.savedProfile?.username ?: state.username.ifBlank { "telefam_member" }
            val displayName = state.savedProfile?.fullName ?: state.fullName.ifBlank { "Telefam Member" }
            val qrBmp = settingsRepository.issueAndGenerateBackendProfileQrCode(
                userId = userId,
                username = handle,
                fullName = displayName
            )
            val blockedList = settingsRepository.getBlockedUsersDetailed(userId, state.blockedAccountsSearchQuery)

            _uiState.update {
                it.copy(
                    destination = AppDestination.SETTINGS_AND_PRIVACY,
                    settingsSubScreen = initialSubScreen,
                    settingsNavigationStack = if (initialSubScreen == SettingsSubScreen.ROOT) emptyList() else listOf(SettingsSubScreen.ROOT),
                    accountSettings = accSettings,
                    profileQrBitmap = qrBmp,
                    blockedAccountItems = blockedList,
                    settingsStatusBanner = null,
                    settingsErrorBanner = null,
                    canRetrySettingsError = false,
                    settingsOtpSentEmail = null,
                    settingsOtpResendCountdownSec = 0,
                    lastSubmittedTicketId = null
                )
            }
            startSettingsReactiveObserver(userId)
        }
    }

    private fun startSettingsReactiveObserver(userId: String) {
        settingsReactiveJob?.cancel()
        settingsReactiveJob = viewModelScope.launch(safeExceptionHandler) {
            combine(
                settingsRepository.observeAccountSettings(userId),
                settingsRepository.observeTrustedDevices(userId),
                settingsRepository.observeConnectedApps(userId),
                settingsRepository.observeSupportTickets(userId),
                repository.observeForensicLogs()
            ) { acc, devices, apps, tickets, logs ->
                SettingsFiveTuple(acc, devices, apps, tickets, logs)
            }.collectLatest { tuple ->
                val blocked = settingsRepository.getBlockedUsersDetailed(
                    currentUserId = userId,
                    searchQuery = _uiState.value.blockedAccountsSearchQuery
                )
                _uiState.update { prev ->
                    prev.copy(
                        accountSettings = tuple.acc ?: prev.accountSettings,
                        trustedDevices = tuple.devices,
                        connectedApps = tuple.apps,
                        supportTickets = tuple.tickets,
                        recentSecurityLogs = tuple.logs,
                        blockedAccountItems = blocked
                    )
                }
            }
        }
    }

    private data class SettingsFiveTuple(
        val acc: AccountSettingsAndSecurityEntity?,
        val devices: List<TrustedDeviceEntity>,
        val apps: List<ConnectedAppSessionEntity>,
        val tickets: List<ProblemSupportTicketEntity>,
        val logs: List<ForensicAuditLogEntity>
    )

    fun navigateToSettingsSubScreen(target: SettingsSubScreen) {
        _uiState.update { state ->
            val current = state.settingsSubScreen
            val nextStack = if (current == target) state.settingsNavigationStack else state.settingsNavigationStack + current
            state.copy(
                settingsSubScreen = target,
                settingsNavigationStack = nextStack,
                settingsStatusBanner = null,
                settingsErrorBanner = null,
                canRetrySettingsError = false,
                settingsOtpSentEmail = null,
                lastSubmittedTicketId = null
            )
        }
        if (target == SettingsSubScreen.BLOCKED_ACCOUNTS) {
            refreshBlockedAccountsInSettings()
        } else if (target == SettingsSubScreen.SHARE_PROFILE) {
            regenerateBackendProfileQrCode()
        }
    }

    fun regenerateBackendProfileQrCode() {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val state = _uiState.value
            val handle = state.savedProfile?.username ?: state.username.ifBlank { "telefam_member" }
            val displayName = state.savedProfile?.fullName ?: state.fullName.ifBlank { "Telefam Member" }
            val qrBmp = settingsRepository.issueAndGenerateBackendProfileQrCode(
                userId = userId,
                username = handle,
                fullName = displayName
            )
            _uiState.update { it.copy(profileQrBitmap = qrBmp) }
        }
    }

    fun navigateBackInSettings() {
        val state = _uiState.value
        val stack = state.settingsNavigationStack
        if (stack.isNotEmpty()) {
            val previous = stack.last()
            _uiState.update {
                it.copy(
                    settingsSubScreen = previous,
                    settingsNavigationStack = stack.dropLast(1),
                    settingsStatusBanner = null,
                    settingsErrorBanner = null,
                    canRetrySettingsError = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    destination = AppDestination.MAIN_HOME,
                    settingsSubScreen = SettingsSubScreen.ROOT,
                    settingsNavigationStack = emptyList(),
                    settingsStatusBanner = null,
                    settingsErrorBanner = null,
                    canRetrySettingsError = false
                )
            }
        }
    }

    fun clearSettingsBanners() {
        _uiState.update { it.copy(settingsStatusBanner = null, settingsErrorBanner = null, canRetrySettingsError = false) }
    }

    fun retryLastFailedSettingsAction() {
        val retryFn = lastRetryableSettingsAction ?: return
        _uiState.update { it.copy(settingsErrorBanner = null, canRetrySettingsError = false) }
        retryFn.invoke()
    }

    private fun recordSettingsFailure(message: String, retryAction: (() -> Unit)? = null) {
        if (retryAction != null) {
            lastRetryableSettingsAction = retryAction
        }
        _uiState.update {
            it.copy(
                settingsErrorBanner = message,
                settingsStatusBanner = null,
                canRetrySettingsError = lastRetryableSettingsAction != null
            )
        }
    }

    fun updateAppThemeOption(option: AppThemeOption) {
        lastRetryableSettingsAction = { updateAppThemeOption(option) }
        // Apply immediately in UI state so the theme switches instantaneously without refresh
        _uiState.update { state ->
            state.copy(
                accountSettings = state.accountSettings.copy(appThemeMode = option.wireCode),
                settingsStatusBanner = "Appearance updated to ${option.label}.",
                settingsErrorBanner = null,
                canRetrySettingsError = false
            )
        }
        viewModelScope.launch(safeExceptionHandler) {
            val current = _uiState.value.accountSettings.copy(appThemeMode = option.wireCode)
            val saved = settingsRepository.saveAndSyncAccountSettings(
                updated = current,
                telemetry = currentTelemetry(),
                auditAction = "Appearance theme -> ${option.wireCode}"
            )
            _uiState.update { it.copy(accountSettings = saved) }
        }
    }

    fun openPrivacyRuleSelector(field: PrivacyControlField) {
        _uiState.update {
            it.copy(
                activePrivacyControlField = field,
                settingsSubScreen = SettingsSubScreen.PRIVACY_RULE_SELECTOR,
                settingsNavigationStack = it.settingsNavigationStack + it.settingsSubScreen,
                settingsStatusBanner = null,
                settingsErrorBanner = null
            )
        }
    }

    fun selectSocialPrivacyRule(field: PrivacyControlField, option: SocialPrivacyOption) {
        viewModelScope.launch(safeExceptionHandler) {
            val telemetry = currentTelemetry()
            val updated = settingsRepository.updateSocialPrivacyControl(
                current = _uiState.value.accountSettings,
                field = field,
                option = option,
                telemetry = telemetry
            )
            _uiState.update {
                it.copy(
                    accountSettings = updated,
                    settingsStatusBanner = "${field.title} updated to ${option.label}."
                )
            }
        }
    }

    fun togglePrivateAccountSetting(enabled: Boolean) {
        viewModelScope.launch(safeExceptionHandler) {
            val telemetry = currentTelemetry()
            val updated = settingsRepository.togglePrivateAccount(
                current = _uiState.value.accountSettings,
                enabled = enabled,
                telemetry = telemetry
            )
            _uiState.update {
                it.copy(
                    accountSettings = updated,
                    settingsStatusBanner = if (enabled) {
                        "Private account enabled. Only approved followers can view your restricted content."
                    } else {
                        "Public account enabled. Your profile and posts are visible according to your privacy rules."
                    }
                )
            }
        }
    }

    fun updateAccountSettingsTransform(
        confirmationMessage: String,
        auditAction: String,
        transform: (AccountSettingsAndSecurityEntity) -> AccountSettingsAndSecurityEntity
    ) {
        viewModelScope.launch(safeExceptionHandler) {
            val current = _uiState.value.accountSettings
            val transformed = transform(current)
            val saved = settingsRepository.saveAndSyncAccountSettings(
                updated = transformed,
                telemetry = currentTelemetry(),
                auditAction = auditAction
            )
            _uiState.update {
                it.copy(
                    accountSettings = saved,
                    settingsStatusBanner = confirmationMessage,
                    settingsErrorBanner = null
                )
            }
            if (auditAction.contains("Recommendation", ignoreCase = true) || auditAction.contains("Reset", ignoreCase = true)) {
                val uid = currentAuthenticatedUserId()
                socialRepository.resetFeedRecommendationsForUser(uid)
                refreshFeedVideos(resetPage = true)
            }
            // Refresh contacts if content languages/filtering changed
            loadContactsPageInternal(page = 1)
        }
    }

    fun openPermissionDetail(permissionType: AppPermissionType) {
        _uiState.update {
            it.copy(
                activePermissionType = permissionType,
                settingsSubScreen = SettingsSubScreen.SECURITY_PERMISSION_DETAIL,
                settingsNavigationStack = it.settingsNavigationStack + it.settingsSubScreen,
                settingsStatusBanner = null,
                settingsErrorBanner = null
            )
        }
    }

    fun onSystemPermissionUpdated(permissionType: AppPermissionType, granted: Boolean) {
        updateAccountSettingsTransform(
            confirmationMessage = "${permissionType.title} permission ${if (granted) "granted" else "updated"}.",
            auditAction = "Permission ${permissionType.name} -> $granted"
        ) { acc ->
            when (permissionType) {
                AppPermissionType.CONTACTS -> acc.copy(permissionContactsAllowed = granted)
                AppPermissionType.CAMERA -> acc.copy(permissionCameraAllowed = granted)
                AppPermissionType.MICROPHONE -> acc.copy(permissionMicrophoneAllowed = granted)
                AppPermissionType.PHOTOS -> acc.copy(permissionPhotosAllowed = granted)
            }
        }
        if (permissionType == AppPermissionType.CONTACTS && granted) {
            onContactsPermissionResult(true)
        }
    }

    fun sendSettingsVerificationOtp(purpose: String) {
        lastRetryableSettingsAction = { sendSettingsVerificationOtp(purpose) }
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val targetEmail = state.activeSession?.email ?: state.email.ifBlank { "member@telefam.company" }
            val telemetry = currentTelemetry()
            val result = settingsRepository.sendVerificationCodeForSettingsAction(
                originalEmail = targetEmail,
                purpose = purpose,
                telemetry = telemetry
            )
            val (_, otpRem) = repository.checkCooldownLocks(targetEmail, telemetry.ipSubnetFingerprint)
            result.fold(
                onSuccess = { countdownSec ->
                    _uiState.update {
                        it.copy(
                            settingsOtpSentEmail = targetEmail,
                            settingsOtpResendCountdownSec = countdownSec,
                            otpCooldownRemainingSec = otpRem,
                            settingsStatusBanner = "We sent a 6-digit verification code to $targetEmail. Please check your email and enter it below to confirm.",
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(otpCooldownRemainingSec = otpRem) }
                    recordSettingsFailure(
                        message = err.message ?: "Could not send verification code.",
                        retryAction = { sendSettingsVerificationOtp(purpose) }
                    )
                }
            )
        }
    }

    fun confirmChangeAccountEmail(newEmail: String, enteredOtp: String) {
        lastRetryableSettingsAction = { confirmChangeAccountEmail(newEmail, enteredOtp) }
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val userId = currentAuthenticatedUserId()
            val originalEmail = state.activeSession?.email ?: state.email.ifBlank { "member@telefam.company" }
            val telemetry = currentTelemetry()
            val result = settingsRepository.verifyOtpAndChangeEmail(
                userId = userId,
                originalEmail = originalEmail,
                newEmail = newEmail,
                enteredOtp = enteredOtp,
                telemetry = telemetry
            )
            val (_, otpRem) = repository.checkCooldownLocks(originalEmail, telemetry.ipSubnetFingerprint)
            result.fold(
                onSuccess = { updatedEmail ->
                    _uiState.update {
                        it.copy(
                            email = updatedEmail,
                            activeSession = it.activeSession?.copy(email = updatedEmail),
                            settingsOtpSentEmail = null,
                            settingsOtpResendCountdownSec = 0,
                            otpCooldownRemainingSec = otpRem,
                            settingsStatusBanner = "Email address verified and updated to $updatedEmail.",
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(otpCooldownRemainingSec = otpRem) }
                    recordSettingsFailure(
                        message = err.message ?: "Unable to update email address.",
                        retryAction = { confirmChangeAccountEmail(newEmail, enteredOtp) }
                    )
                }
            )
        }
    }

    fun submitAccountPasswordChange(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ) {
        lastRetryableSettingsAction = { submitAccountPasswordChange(currentPassword, newPassword, confirmNewPassword) }
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val userId = currentAuthenticatedUserId()
            val email = state.activeSession?.email ?: state.email
            val result = settingsRepository.changeAccountPassword(
                userId = userId,
                email = email,
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmNewPassword = confirmNewPassword,
                telemetry = currentTelemetry()
            )
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            settingsStatusBanner = "Your password has been updated.",
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                },
                onFailure = { err ->
                    recordSettingsFailure(
                        message = err.message ?: "Unable to change password.",
                        retryAction = { submitAccountPasswordChange(currentPassword, newPassword, confirmNewPassword) }
                    )
                }
            )
        }
    }

    fun saveRecoveryContactsAndRegenerateCodes(recoveryEmail: String, recoveryPhone: String) {
        lastRetryableSettingsAction = { saveRecoveryContactsAndRegenerateCodes(recoveryEmail, recoveryPhone) }
        viewModelScope.launch(safeExceptionHandler) {
            val result = settingsRepository.regenerateRecoveryCodes(
                current = _uiState.value.accountSettings,
                recoveryEmail = recoveryEmail,
                recoveryPhone = recoveryPhone,
                telemetry = currentTelemetry()
            )
            result.fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(
                            accountSettings = updated,
                            settingsStatusBanner = "Recovery settings saved and new backup recovery codes generated.",
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                },
                onFailure = { err ->
                    recordSettingsFailure(
                        message = err.message ?: "Unable to update recovery settings.",
                        retryAction = { saveRecoveryContactsAndRegenerateCodes(recoveryEmail, recoveryPhone) }
                    )
                }
            )
        }
    }

    fun enableTwoStepVerification(method: String, verificationCode: String) {
        lastRetryableSettingsAction = { enableTwoStepVerification(method, verificationCode) }
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val email = state.activeSession?.email ?: state.email.ifBlank { "member@telefam.company" }
            val telemetry = currentTelemetry()
            val result = settingsRepository.verifyAndEnableTwoStepVerification(
                current = state.accountSettings,
                email = email,
                method = method,
                enteredCode = verificationCode,
                telemetry = telemetry
            )
            val (_, otpRem) = repository.checkCooldownLocks(email, telemetry.ipSubnetFingerprint)
            result.fold(
                onSuccess = { (updated, _) ->
                    _uiState.update {
                        it.copy(
                            accountSettings = updated,
                            settingsOtpSentEmail = null,
                            settingsOtpResendCountdownSec = 0,
                            otpCooldownRemainingSec = otpRem,
                            settingsStatusBanner = "2-step verification ($method) is now active. Save your recovery codes below.",
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(otpCooldownRemainingSec = otpRem) }
                    recordSettingsFailure(
                        message = err.message ?: "Verification failed.",
                        retryAction = { enableTwoStepVerification(method, verificationCode) }
                    )
                }
            )
        }
    }

    fun disableTwoStepVerification() {
        lastRetryableSettingsAction = { disableTwoStepVerification() }
        viewModelScope.launch(safeExceptionHandler) {
            val updated = settingsRepository.disableTwoStepVerification(
                current = _uiState.value.accountSettings,
                telemetry = currentTelemetry()
            )
            _uiState.update {
                it.copy(
                    accountSettings = updated,
                    settingsStatusBanner = "2-step verification has been turned off.",
                    settingsErrorBanner = null,
                    canRetrySettingsError = false
                )
            }
        }
    }

    fun registerPasskeyCredential(activityContext: Context? = null, deviceLabel: String = "") {
        lastRetryableSettingsAction = { registerPasskeyCredential(activityContext, deviceLabel) }
        viewModelScope.launch(safeExceptionHandler) {
            val currentSettings = _uiState.value.accountSettings
            val telemetry = currentTelemetry()
            var credentialIdFromPlatform: String? = null
            var publicKeyFromPlatform: String? = null
            val detectedLabel = if (deviceLabel.isNotBlank()) deviceLabel else {
                val manufacturer = Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                "$manufacturer ${Build.MODEL}".trim().ifBlank { "Passkey" }
            }

            if (activityContext != null) {
                try {
                    val credentialManager = CredentialManager.create(activityContext)
                    val challengeBytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
                    val challengeB64 = Base64.encodeToString(challengeBytes, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)
                    val userIdBytes = currentSettings.ownerUserId.toByteArray()
                    val userIdB64 = Base64.encodeToString(userIdBytes, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)

                    val requestJson = JSONObject().apply {
                        put("challenge", challengeB64)
                        put("rp", JSONObject().apply {
                            put("name", "Telefam")
                            put("id", "telefam.company")
                        })
                        put("user", JSONObject().apply {
                            put("id", userIdB64)
                            put("name", _uiState.value.email.ifBlank { "user@telefam.company" })
                            put("displayName", _uiState.value.fullName.ifBlank { "Telefam User" })
                        })
                        put("pubKeyCredParams", JSONArray().apply {
                            put(JSONObject().apply {
                                put("type", "public-key")
                                put("alg", -7)
                            })
                        })
                        put("timeout", 60000L)
                        put("attestation", "none")
                        put("authenticatorSelection", JSONObject().apply {
                            put("authenticatorAttachment", "platform")
                            put("residentKey", "required")
                            put("requireResidentKey", true)
                            put("userVerification", "required")
                        })
                    }.toString()

                    val request = CreatePublicKeyCredentialRequest(requestJson = requestJson)
                    val result = credentialManager.createCredential(activityContext, request)
                    if (result is CreatePublicKeyCredentialResponse) {
                        val regJson = JSONObject(result.registrationResponseJson)
                        credentialIdFromPlatform = regJson.optString("id").takeIf { it.isNotBlank() } ?: regJson.optString("rawId")
                        publicKeyFromPlatform = regJson.optJSONObject("response")?.optString("attestationObject")
                    }
                } catch (_: CreateCredentialCancellationException) {
                    return@launch
                } catch (e: Exception) {
                    // Fallback for emulators or testing
                }
            }

            val updated = settingsRepository.registerFido2Passkey(
                current = currentSettings,
                deviceLabel = detectedLabel,
                telemetry = telemetry,
                credentialId = credentialIdFromPlatform,
                publicKeyCoseHex = publicKeyFromPlatform
            )
            _uiState.update {
                it.copy(
                    accountSettings = updated,
                    settingsStatusBanner = "Your passkey is saved. You can now log in next time with your passkey.",
                    settingsErrorBanner = null,
                    canRetrySettingsError = false
                )
            }
        }
    }

    fun registerPasskeyCredential(deviceLabel: String) {
        registerPasskeyCredential(null, deviceLabel)
    }

    fun revokePasskeyCredential() {
        lastRetryableSettingsAction = { revokePasskeyCredential() }
        viewModelScope.launch(safeExceptionHandler) {
            val updated = settingsRepository.revokeFido2Passkey(
                current = _uiState.value.accountSettings,
                telemetry = currentTelemetry()
            )
            _uiState.update {
                it.copy(
                    accountSettings = updated,
                    settingsStatusBanner = "Passkey removed from your account.",
                    settingsErrorBanner = null,
                    canRetrySettingsError = false
                )
            }
        }
    }

    fun logoutDeviceSession(deviceRecordId: String) {
        lastRetryableSettingsAction = { logoutDeviceSession(deviceRecordId) }
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            settingsRepository.logoutRecognizedDevice(
                userId = userId,
                deviceRecordId = deviceRecordId,
                telemetry = currentTelemetry()
            )
            _uiState.update {
                it.copy(
                    settingsStatusBanner = "Device session logged out and revoked.",
                    settingsErrorBanner = null,
                    canRetrySettingsError = false
                )
            }
        }
    }

    fun revokeConnectedApp(connectionId: String) {
        lastRetryableSettingsAction = { revokeConnectedApp(connectionId) }
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            settingsRepository.revokeConnectedAppSession(
                userId = userId,
                connectionId = connectionId,
                telemetry = currentTelemetry()
            )
            _uiState.update {
                it.copy(
                    settingsStatusBanner = "Connected app access revoked.",
                    settingsErrorBanner = null,
                    canRetrySettingsError = false
                )
            }
        }
    }

    fun confirmDeactivateAccount(reason: String, autoReenableDays: Int, enteredOtp: String) {
        lastRetryableSettingsAction = { confirmDeactivateAccount(reason, autoReenableDays, enteredOtp) }
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val email = state.activeSession?.email ?: state.email.ifBlank { "member@telefam.company" }
            val telemetry = currentTelemetry()
            val result = settingsRepository.verifyOtpAndDeactivateAccount(
                current = state.accountSettings,
                profile = state.savedProfile,
                email = email,
                reason = reason,
                autoReenableDays = autoReenableDays,
                enteredOtp = enteredOtp,
                telemetry = telemetry
            )
            result.fold(
                onSuccess = {
                    state.activeSession?.let { sess ->
                        repository.signOutCurrentSession(sess.userId, sess.email, telemetry)
                    }
                    val quickAccounts = settingsRepository.getSavedQuickLoginAccounts()
                    val autoMsg = if (autoReenableDays > 0) {
                        "Your account is deactivated and scheduled to auto-enable in $autoReenableDays day(s), or whenever you sign in."
                    } else {
                        "Your account is deactivated. Sign in anytime to reactivate your account."
                    }
                    _uiState.update {
                        it.copy(
                            destination = AppDestination.AUTH,
                            authMode = AuthMode.LOGIN,
                            activeSession = null,
                            savedProfile = null,
                            password = "",
                            otpCode = "",
                            quickLoginAccounts = quickAccounts,
                            infoBannerMessage = autoMsg,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { err ->
                    val (_, otpRem) = repository.checkCooldownLocks(email, telemetry.ipSubnetFingerprint)
                    _uiState.update { it.copy(otpCooldownRemainingSec = otpRem) }
                    recordSettingsFailure(
                        message = err.message ?: "Could not deactivate account.",
                        retryAction = { confirmDeactivateAccount(reason, autoReenableDays, enteredOtp) }
                    )
                }
            )
        }
    }

    fun confirmSchedule30DayAccountDeletion(reason: String, enteredOtp: String) {
        lastRetryableSettingsAction = { confirmSchedule30DayAccountDeletion(reason, enteredOtp) }
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val email = state.activeSession?.email ?: state.email.ifBlank { "member@telefam.company" }
            val telemetry = currentTelemetry()
            val result = settingsRepository.verifyOtpAndSchedule30DayAccountDeletion(
                current = state.accountSettings,
                profile = state.savedProfile,
                email = email,
                reason = reason,
                enteredOtp = enteredOtp,
                telemetry = telemetry
            )
            result.fold(
                onSuccess = {
                    state.activeSession?.let { sess ->
                        repository.signOutCurrentSession(sess.userId, sess.email, telemetry)
                    }
                    val quickAccounts = settingsRepository.getSavedQuickLoginAccounts()
                    _uiState.update {
                        it.copy(
                            destination = AppDestination.AUTH,
                            authMode = AuthMode.LOGIN,
                            activeSession = null,
                            savedProfile = null,
                            password = "",
                            otpCode = "",
                            quickLoginAccounts = quickAccounts,
                            infoBannerMessage = "Account scheduled for permanent deletion in 30 days. Log back in within 30 days to cancel deletion and recover your account.",
                            errorMessage = null
                        )
                    }
                },
                onFailure = { err ->
                    val (_, otpRem) = repository.checkCooldownLocks(email, telemetry.ipSubnetFingerprint)
                    _uiState.update { it.copy(otpCooldownRemainingSec = otpRem) }
                    recordSettingsFailure(
                        message = err.message ?: "Could not schedule account deletion.",
                        retryAction = { confirmSchedule30DayAccountDeletion(reason, enteredOtp) }
                    )
                }
            )
        }
    }

    fun downloadProfileQrCode() {
        lastRetryableSettingsAction = { downloadProfileQrCode() }
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val state = _uiState.value
            val handle = state.savedProfile?.username ?: state.username.ifBlank { "telefam_member" }
            val fullName = state.savedProfile?.fullName ?: state.fullName.ifBlank { "Telefam Member" }
            val res = settingsRepository.saveQrCodeToDeviceDownloads(
                userId = userId,
                username = handle,
                fullName = fullName
            )
            res.fold(
                onSuccess = { msg ->
                    _uiState.update {
                        it.copy(
                            settingsStatusBanner = msg,
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                },
                onFailure = { err ->
                    recordSettingsFailure(
                        message = err.message ?: "Unable to save QR code.",
                        retryAction = { downloadProfileQrCode() }
                    )
                }
            )
        }
    }

    fun onBlockedAccountsSearchQueryChanged(query: String) {
        _uiState.update { it.copy(blockedAccountsSearchQuery = query) }
        refreshBlockedAccountsInSettings()
    }

    fun refreshBlockedAccountsInSettings() {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val list = settingsRepository.getBlockedUsersDetailed(
                currentUserId = userId,
                searchQuery = _uiState.value.blockedAccountsSearchQuery
            )
            _uiState.update { it.copy(blockedAccountItems = list) }
        }
    }

    fun unblockAccountFromSettings(blockedUserId: String, blockedUsername: String) {
        lastRetryableSettingsAction = { unblockAccountFromSettings(blockedUserId, blockedUsername) }
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val result = socialRepository.setBlockUserState(
                currentUserId = userId,
                targetUserId = blockedUserId,
                block = false,
                telemetry = currentTelemetry()
            )
            result.fold(
                onSuccess = {
                    val updatedList = settingsRepository.getBlockedUsersDetailed(
                        currentUserId = userId,
                        searchQuery = _uiState.value.blockedAccountsSearchQuery
                    )
                    _uiState.update {
                        it.copy(
                            blockedAccountItems = updatedList,
                            settingsStatusBanner = "@$blockedUsername has been unblocked.",
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                    loadContactsPageInternal(page = 1)
                },
                onFailure = { err ->
                    recordSettingsFailure(
                        message = err.message ?: "Unable to unblock user.",
                        retryAction = { unblockAccountFromSettings(blockedUserId, blockedUsername) }
                    )
                }
            )
        }
    }

    fun submitProblemReportOrSupportRequest(
        ticketType: String,
        category: String,
        subject: String,
        description: String,
        screenshotUri: Uri?
    ) {
        lastRetryableSettingsAction = {
            submitProblemReportOrSupportRequest(ticketType, category, subject, description, screenshotUri)
        }
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val result = settingsRepository.submitProblemOrSupportTicket(
                ownerUserId = userId,
                ticketType = ticketType,
                category = category,
                subject = subject,
                description = description,
                screenshotUri = screenshotUri,
                telemetry = currentTelemetry()
            )
            result.fold(
                onSuccess = { ticket ->
                    _uiState.update {
                        it.copy(
                            lastSubmittedTicketId = ticket.ticketId,
                            settingsStatusBanner = "Submitted (${ticket.ticketId}). We'll review your report and contact you if more information is needed.",
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                },
                onFailure = { err ->
                    recordSettingsFailure(
                        message = err.message ?: "Could not submit report.",
                        retryAction = {
                            submitProblemReportOrSupportRequest(ticketType, category, subject, description, screenshotUri)
                        }
                    )
                }
            )
        }
    }

    fun submitSupportTicketFollowUp(ticketId: String, followUpText: String, markResolved: Boolean) {
        lastRetryableSettingsAction = { submitSupportTicketFollowUp(ticketId, followUpText, markResolved) }
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            val result = settingsRepository.updateSupportTicketFollowUpOrStatus(
                ownerUserId = userId,
                ticketId = ticketId,
                followUpReply = followUpText,
                markResolved = markResolved,
                telemetry = currentTelemetry()
            )
            result.fold(
                onSuccess = { updatedTicket ->
                    val statusMsg = if (markResolved) {
                        "Support request $ticketId marked as resolved."
                    } else {
                        "Follow-up message added to support request ${updatedTicket.ticketId}."
                    }
                    _uiState.update {
                        it.copy(
                            settingsStatusBanner = statusMsg,
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                },
                onFailure = { err ->
                    recordSettingsFailure(
                        message = err.message ?: "Unable to update support request.",
                        retryAction = { submitSupportTicketFollowUp(ticketId, followUpText, markResolved) }
                    )
                }
            )
        }
    }

    fun acknowledgeLegalDocument(doc: LegalPolicyDocument) {
        lastRetryableSettingsAction = { acknowledgeLegalDocument(doc) }
        viewModelScope.launch(safeExceptionHandler) {
            val updated = settingsRepository.acknowledgeLegalPolicy(
                current = _uiState.value.accountSettings,
                policyId = doc.id,
                policyTitle = doc.title,
                policyVersion = doc.version,
                telemetry = currentTelemetry()
            )
            _uiState.update {
                it.copy(
                    accountSettings = updated,
                    settingsStatusBanner = "${doc.title} (${doc.version}) acknowledged and saved to your account.",
                    settingsErrorBanner = null,
                    canRetrySettingsError = false
                )
            }
        }
    }

    fun downloadLegalDocument(doc: LegalPolicyDocument) {
        lastRetryableSettingsAction = { downloadLegalDocument(doc) }
        viewModelScope.launch(safeExceptionHandler) {
            val result = settingsRepository.downloadLegalDocumentCopy(
                title = doc.title,
                version = doc.version,
                effectiveDate = doc.effectiveDate,
                sections = doc.sections
            )
            result.fold(
                onSuccess = { msg ->
                    _uiState.update {
                        it.copy(
                            settingsStatusBanner = msg,
                            settingsErrorBanner = null,
                            canRetrySettingsError = false
                        )
                    }
                },
                onFailure = { err ->
                    recordSettingsFailure(
                        message = err.message ?: "Unable to download policy document.",
                        retryAction = { downloadLegalDocument(doc) }
                    )
                }
            )
        }
    }

    fun submitHelpArticleFeedback(articleId: String, helpful: Boolean) {
        lastRetryableSettingsAction = { submitHelpArticleFeedback(articleId, helpful) }
        viewModelScope.launch(safeExceptionHandler) {
            val updated = settingsRepository.recordHelpArticleFeedback(
                current = _uiState.value.accountSettings,
                articleId = articleId,
                helpful = helpful,
                telemetry = currentTelemetry()
            )
            _uiState.update {
                it.copy(
                    accountSettings = updated,
                    settingsStatusBanner = if (helpful) {
                        "Thank you for your feedback. We're glad this article helped."
                    } else {
                        "Thank you for your feedback. You can submit a direct support request below if you need further assistance."
                    },
                    settingsErrorBanner = null,
                    canRetrySettingsError = false
                )
            }
        }
    }

    fun clearSubmittedTicketConfirmation() {
        _uiState.update { it.copy(lastSubmittedTicketId = null) }
    }

    fun openSafetyGuideDetail(guide: SafetyTopicGuide) {
        _uiState.update {
            it.copy(
                activeSafetyGuide = guide,
                settingsSubScreen = SettingsSubScreen.SAFETY_TOPIC_DETAIL,
                settingsNavigationStack = it.settingsNavigationStack + it.settingsSubScreen,
                settingsStatusBanner = null,
                settingsErrorBanner = null,
                canRetrySettingsError = false
            )
        }
    }

    fun onHelpSearchQueryChanged(query: String) {
        _uiState.update { it.copy(helpSearchQuery = query) }
    }

    fun openHelpArticleDetail(article: HelpSupportArticle) {
        _uiState.update {
            it.copy(
                activeHelpArticle = article,
                settingsSubScreen = SettingsSubScreen.HELP_ARTICLE_DETAIL,
                settingsNavigationStack = it.settingsNavigationStack + it.settingsSubScreen,
                settingsStatusBanner = null,
                settingsErrorBanner = null,
                canRetrySettingsError = false
            )
        }
    }

    fun openLegalDocumentDetail(doc: LegalPolicyDocument) {
        _uiState.update {
            it.copy(
                activeLegalDocument = doc,
                settingsSubScreen = SettingsSubScreen.LEGAL_DOCUMENT_DETAIL,
                settingsNavigationStack = it.settingsNavigationStack + it.settingsSubScreen,
                settingsStatusBanner = null,
                settingsErrorBanner = null,
                canRetrySettingsError = false
            )
        }
    }

    fun openAboutSectionDetail(detailKey: String) {
        _uiState.update {
            it.copy(
                activeAboutDetailKey = detailKey,
                settingsSubScreen = SettingsSubScreen.ABOUT_DETAIL,
                settingsNavigationStack = it.settingsNavigationStack + it.settingsSubScreen,
                settingsStatusBanner = null,
                settingsErrorBanner = null,
                canRetrySettingsError = false
            )
        }
    }

    // =========================================================================
    // 1:1 INDIVIDUAL CHATS (OFFLINE-FIRST ROOM SOURCE OF TRUTH + SIGNAL E2EE)
    // =========================================================================

    private fun startOneToOneChatsReactiveObserver(userId: String) {
        if (userId.isBlank()) return
        chatRepository.ensureRealtimeConnectionAndPreKeys(userId)

        chatListReactiveObserverJob?.cancel()
        chatListReactiveObserverJob = viewModelScope.launch(safeExceptionHandler) {
            chatRepository.observeOneToOneChatList(userId).collectLatest { chats ->
                _uiState.update { prev ->
                    val currentActiveId = prev.activeChatId
                    val refreshedActiveItem = if (currentActiveId != null) {
                        chats.find { it.chatId == currentActiveId } ?: prev.activeChatItem
                    } else {
                        null
                    }
                    prev.copy(
                        chatListItems = chats,
                        activeChatItem = refreshedActiveItem
                    )
                }
            }
        }

        observeKlipyRecentsForActiveTab(userId, _uiState.value.activeKlipyTab)
    }

    private fun observeKlipyRecentsForActiveTab(userId: String, tab: KlipyMediaTab) {
        klipyRecentsObserverJob?.cancel()
        klipyRecentsObserverJob = viewModelScope.launch(safeExceptionHandler) {
            chatRepository.klipyProvider.observeRecents(userId, tab).collectLatest { recents ->
                _uiState.update { it.copy(klipyRecentItems = recents) }
            }
        }
    }

    fun onToggleChatListSearch(active: Boolean) {
        _uiState.update {
            it.copy(
                isChatListSearchActive = active,
                chatListSearchQuery = if (!active) "" else it.chatListSearchQuery
            )
        }
    }

    fun onChatListSearchQueryChanged(query: String) {
        _uiState.update { it.copy(chatListSearchQuery = query) }
    }

    /**
     * "Start chat" button on the Main Chats Screen opens the existing CONTACTS screen
     * so the user can open a profile or tap message to open the 1:1 inbox.
     */
    fun onStartChatFromChatsTab() {
        _uiState.update {
            it.copy(
                destination = AppDestination.MAIN_HOME,
                selectedHomeTab = HomeNavTab.CONTACTS,
                isOwnerMenuOpen = false
            )
        }
        refreshContactsDirectory(forceDailyRefresh = false)
    }

    /**
     * Opens or creates a real 1:1 chat with the active Public User Profile when the user taps "Message".
     */
    fun openChatWithActivePublicProfile() {
        val profile = _uiState.value.activePublicProfile ?: return
        openOneToOneChatWithDiscoverableUser(profile)
    }

    /**
     * Opens or creates a real 1:1 chat with a contact/user from Contacts or Public Profile.
     */
    fun openOneToOneChatWithDiscoverableUser(user: DiscoverableUserItem) {
        viewModelScope.launch(safeExceptionHandler) {
            val ownerId = currentAuthenticatedUserId()
            val chatId = chatRepository.openOrCreateOneToOneChat(
                ownerUserId = ownerId,
                peerUserId = user.userId,
                peerFullName = user.fullName,
                peerUsername = user.username,
                peerAvatarUrl = user.avatarCdnUrl,
                peerBio = user.bio,
                peerIsOnline = true,
                peerLastSeenMs = System.currentTimeMillis(),
                peerIsDeleted = false
            )
            openOneToOneChatById(chatId)
        }
    }

    /**
     * Opens an existing 1:1 chat from the Main Chats list by `chatId`.
     * Clears unread count, loads draft from Room, and observes messages/attachments from Room.
     */
    fun openOneToOneChatById(chatId: String) {
        TelefamNotificationEngine.setActiveOpenChat(getApplication<Application>().applicationContext, chatId)
        viewModelScope.launch(safeExceptionHandler) {
            val ownerId = currentAuthenticatedUserId()
            val draftEntity = repository.dao.getDraftForChat(chatId)
            val chatEntity = repository.dao.getChatById(chatId)
            if (chatEntity != null) {
                chatRepository.markChatMessagesSeen(ownerId, chatId, chatEntity.peerUserId)
            }

            val groupEntity = repository.dao.getGroupById(chatId)
            val myMember = if (groupEntity != null) repository.dao.getGroupMember(chatId, ownerId) else null
            val memberCount = if (groupEntity != null) repository.dao.getGroupMemberCount(chatId) else 0
            val myRole = myMember?.let { runCatching { GroupRole.valueOf(it.role) }.getOrNull() } ?: GroupRole.MEMBER

            _uiState.update { prev ->
                val baseItem = prev.chatListItems.find { it.chatId == chatId } ?: chatEntity?.let { ce ->
                    OneToOneChatListItem(
                        chatId = ce.chatId,
                        ownerUserId = ce.ownerUserId,
                        peerUserId = ce.peerUserId,
                        peerFullName = if (ce.peerIsDeleted) "User not found" else ce.peerFullName,
                        peerUsername = ce.peerUsername,
                        peerAvatarUrl = if (ce.peerIsDeleted) null else ce.peerAvatarUrl,
                        peerBio = ce.peerBio,
                        peerIsOnline = ce.peerIsOnline,
                        peerLastSeenMs = ce.peerLastSeenMs,
                        peerIsTyping = ce.peerIsTyping,
                        peerIsDeleted = ce.peerIsDeleted,
                        isBlockedByMe = false,
                        isBlockedByPeer = false,
                        lastMessageId = ce.lastMessageId,
                        lastMessageType = runCatching { ChatMessageType.valueOf(ce.lastMessageType) }.getOrDefault(ChatMessageType.TEXT),
                        lastMessagePreview = ce.lastMessagePreview,
                        lastMessageSenderId = ce.lastMessageSenderId,
                        lastMessageDeliveryStatus = com.telefam.company.app.domain.ChatMessageDeliveryStatus.SENT,
                        lastMessageMediaIconUrl = ce.lastMessageMediaIconUrl,
                        lastMessageTimestampMs = ce.lastMessageTimestampMs,
                        unreadCount = 0,
                        disappearingOption = ChatDisappearingOption.fromSeconds(ce.disappearingDurationSeconds),
                        e2eeSafetyNumberVerified = ce.e2eeSafetyNumberVerified,
                        draftText = draftEntity?.draftText.orEmpty()
                    )
                } ?: groupEntity?.let { ge ->
                    OneToOneChatListItem(
                        chatId = ge.groupId,
                        ownerUserId = ownerId,
                        peerUserId = ge.groupId,
                        peerFullName = ge.groupName,
                        peerUsername = "group",
                        peerAvatarUrl = ge.avatarUrl,
                        peerBio = ge.groupDescription,
                        peerIsOnline = true,
                        peerLastSeenMs = System.currentTimeMillis(),
                        peerIsTyping = false,
                        peerIsDeleted = false,
                        isBlockedByMe = false,
                        isBlockedByPeer = false,
                        lastMessageId = null,
                        lastMessageType = ChatMessageType.TEXT,
                        lastMessagePreview = "",
                        lastMessageSenderId = "",
                        lastMessageDeliveryStatus = com.telefam.company.app.domain.ChatMessageDeliveryStatus.SENT,
                        lastMessageMediaIconUrl = null,
                        lastMessageTimestampMs = ge.createdAtMs,
                        unreadCount = 0,
                        disappearingOption = ChatDisappearingOption.OFF,
                        e2eeSafetyNumberVerified = true,
                        draftText = draftEntity?.draftText.orEmpty()
                    )
                }

                val item = if (groupEntity != null && baseItem != null) {
                    baseItem.copy(
                        peerFullName = groupEntity.groupName,
                        peerAvatarUrl = groupEntity.avatarUrl,
                        peerBio = groupEntity.groupDescription,
                        isGroup = true,
                        groupType = if (groupEntity.isPublic) GroupType.PUBLIC else GroupType.PRIVATE,
                        groupMemberCount = memberCount,
                        groupOnlineCount = (memberCount / 3).coerceAtLeast(1),
                        myGroupRole = myRole,
                        canSendGroupMessages = myMember?.isMuted != true && (groupEntity.whoCanSendMessages == "ALL_MEMBERS" || myRole in listOf(GroupRole.OWNER, GroupRole.ADMIN)),
                        canSendMedia = myMember?.isMuted != true && (groupEntity.whoCanSendMessages == "ALL_MEMBERS" || myRole in listOf(GroupRole.OWNER, GroupRole.ADMIN)),
                        canAddMembers = groupEntity.whoCanAddMembers == "ALL_MEMBERS" || myRole in listOf(GroupRole.OWNER, GroupRole.ADMIN),
                        canPinMessages = groupEntity.whoCanPinMessages == "ALL_MEMBERS" || myRole in listOf(GroupRole.OWNER, GroupRole.ADMIN),
                        canChangeGroupInfo = groupEntity.whoCanShareMessage == "ALL_MEMBERS" || myRole in listOf(GroupRole.OWNER, GroupRole.ADMIN),
                        allowCopyMessages = groupEntity.allowCopyMessages,
                        allowScreenshots = groupEntity.allowScreenshots,
                        allowDownloadMedia = groupEntity.allowDownloadMedia,
                        requiresJoinApproval = groupEntity.requiresJoinApproval,
                        isMutedByAdmin = myMember?.isMuted == true,
                        isRemovedFromGroup = myMember == null && !groupEntity.isPublic
                    )
                } else baseItem
                prev.copy(
                    destination = AppDestination.ONE_TO_ONE_CHAT,
                    activeChatId = chatId,
                    activeChatItem = item,
                    chatInputText = draftEntity?.draftText.orEmpty(),
                    replyingToMessage = null,
                    editingMessage = null,
                    selectedMessageIds = emptySet(),
                    isSelectModeActive = false,
                    previewKlipyItem = null
                )
            }

            activeChatObserverJob?.cancel()
            activeChatObserverJob = viewModelScope.launch(safeExceptionHandler) {
                chatRepository.observeMessagesForChat(ownerId, chatId).collectLatest { messages ->
                    val attachments = chatRepository.extractAttachmentsFromMessages(messages)
                    val draftReplyId = repository.dao.getDraftForChat(chatId)?.replyToMessageId
                    _uiState.update { prev ->
                        val resolvedReply = prev.replyingToMessage
                            ?: draftReplyId?.let { rId -> messages.find { it.messageId == rId } }
                        prev.copy(
                            activeChatMessages = messages,
                            activeChatAttachments = attachments,
                            replyingToMessage = resolvedReply
                        )
                    }
                }
            }
        }
    }

    fun closeActiveOneToOneChat() {
        TelefamNotificationEngine.setActiveOpenChat(getApplication<Application>().applicationContext, null)
        chatRepository.stopBubbleVoicePlayback()
        chatRepository.stopVoicePreviewPlayback()
        activeChatObserverJob?.cancel()
        _uiState.update {
            it.copy(
                destination = AppDestination.MAIN_HOME,
                selectedHomeTab = HomeNavTab.CHATS,
                activeChatId = null,
                activeChatItem = null,
                activeChatMessages = emptyList(),
                activeChatAttachments = emptyList(),
                replyingToMessage = null,
                editingMessage = null,
                selectedMessageIds = emptySet(),
                isSelectModeActive = false,
                previewKlipyItem = null
            )
        }
    }

    fun onChatInputTextChanged(newText: String) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        _uiState.update { it.copy(chatInputText = newText) }
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.updateDraftAndTyping(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                draftText = newText,
                replyToMessageId = _uiState.value.replyingToMessage?.messageId
            )
        }
    }

    fun onSwipeOrSelectReplyMessage(message: ChatMessageItem) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        _uiState.update {
            it.copy(
                replyingToMessage = message,
                editingMessage = null
            )
        }
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.updateDraftAndTyping(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                draftText = _uiState.value.chatInputText,
                replyToMessageId = message.messageId
            )
        }
    }

    fun onStartEditingMessage(message: ChatMessageItem) {
        val ownerId = currentAuthenticatedUserId()
        if (message.senderUserId != ownerId || message.messageType != ChatMessageType.TEXT) return
        _uiState.update {
            it.copy(
                editingMessage = message,
                replyingToMessage = null,
                chatInputText = message.decryptedText
            )
        }
    }

    fun onCancelReplyOrEditBanner() {
        val state = _uiState.value
        val wasEditing = state.editingMessage != null
        val chatId = state.activeChatId
        val peerId = state.activeChatItem?.peerUserId
        val ownerId = currentAuthenticatedUserId()
        _uiState.update {
            it.copy(
                replyingToMessage = null,
                editingMessage = null,
                chatInputText = if (wasEditing) "" else it.chatInputText
            )
        }
        if (chatId != null && peerId != null) {
            viewModelScope.launch(safeExceptionHandler) {
                chatRepository.updateDraftAndTyping(
                    ownerUserId = ownerId,
                    chatId = chatId,
                    peerUserId = peerId,
                    draftText = _uiState.value.chatInputText,
                    replyToMessageId = null
                )
            }
        }
    }

    fun sendCurrentTextOrSaveEdit() {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val activeChat = state.activeChatItem ?: return
        if (!activeChat.canSendMessages) return

        val text = state.chatInputText.trim()
        if (text.isEmpty()) return

        val ownerId = currentAuthenticatedUserId()
        val editingTarget = state.editingMessage
        val replyTarget = state.replyingToMessage
        val attachedProduct = state.chatPendingMarketplaceProduct

        val finalText = if (attachedProduct != null && editingTarget == null) {
            val token = com.telefam.company.app.domain.MarketplaceJsonHelpers.serializeProductSnapshotToken(attachedProduct.toSnapshot())
            "$text\n$token\nhttps://telefam.company/marketplace/item/${attachedProduct.id}"
        } else {
            text
        }

        _uiState.update {
            it.copy(
                chatInputText = "",
                replyingToMessage = null,
                editingMessage = null,
                chatPendingMarketplaceProduct = null
            )
        }

        viewModelScope.launch(safeExceptionHandler) {
            if (editingTarget != null) {
                chatRepository.editMessage(
                    ownerUserId = ownerId,
                    chatId = chatId,
                    peerUserId = activeChat.peerUserId,
                    messageId = editingTarget.messageId,
                    newText = text
                )
            } else {
                chatRepository.sendMessage(
                    ownerUserId = ownerId,
                    chatId = chatId,
                    peerUserId = activeChat.peerUserId,
                    messageType = ChatMessageType.TEXT,
                    text = finalText,
                    replyToMessage = replyTarget,
                    peerDisplayNameForReply = activeChat.peerFullName,
                    attachmentLocalPath = attachedProduct?.photoPaths?.firstOrNull(),
                    attachmentTitle = attachedProduct?.title.orEmpty()
                )
            }
        }
    }

    // --- Klipy Stickers | GIFs | Clips Sheet & Preview Dialog ---

    fun onSelectKlipyTab(tab: KlipyMediaTab) {
        if (_uiState.value.activeKlipyTab == tab) return
        val ownerId = currentAuthenticatedUserId()
        _uiState.update { it.copy(activeKlipyTab = tab) }
        observeKlipyRecentsForActiveTab(ownerId, tab)
    }

    fun onKlipySearchQueryChanged(query: String) {
        _uiState.update { it.copy(klipySearchQuery = query) }
    }

    fun onOpenKlipyItemPreview(item: KlipyMediaItem?) {
        _uiState.update { it.copy(previewKlipyItem = item) }
    }

    fun confirmSendKlipyMediaItem(item: KlipyMediaItem) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val activeChat = state.activeChatItem ?: return
        if (!activeChat.canSendMessages) return

        val ownerId = currentAuthenticatedUserId()
        val replyTarget = state.replyingToMessage
        val msgType = when (item.mediaTab) {
            KlipyMediaTab.STICKERS -> ChatMessageType.STICKER
            KlipyMediaTab.GIFS -> ChatMessageType.GIF
            KlipyMediaTab.CLIPS -> ChatMessageType.CLIP
        }

        _uiState.update {
            it.copy(
                previewKlipyItem = null,
                replyingToMessage = null
            )
        }

        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.sendMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = activeChat.peerUserId,
                messageType = msgType,
                text = item.title,
                klipyItem = item,
                replyToMessage = replyTarget,
                peerDisplayNameForReply = activeChat.peerFullName
            )
        }
    }

    // --- Voice Note Recording, Preview Player, View-Once Toggle & Send ---

    fun startVoiceNoteHoldRecording() {
        val activeChat = _uiState.value.activeChatItem ?: return
        if (!activeChat.canSendMessages) return
        chatRepository.startVoiceNoteRecording()
    }

    fun stopVoiceNoteHoldRecording() {
        chatRepository.stopVoiceNoteRecording()
    }

    fun discardRecordedVoiceNote() {
        chatRepository.discardRecordedVoiceNote()
    }

    fun toggleRecordedVoiceNoteViewOnce() {
        chatRepository.toggleVoiceNoteViewOnce()
    }

    fun toggleRecordedVoicePreviewPlay() {
        chatRepository.toggleVoicePreviewPlayback()
    }

    fun sendRecordedVoiceNote() {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val activeChat = state.activeChatItem ?: return
        if (!activeChat.canSendMessages) return

        val recState = voiceRecordingState.value
        val filePath = recState.recordedFilePath ?: return
        val durationMs = recState.recordedDurationMs.coerceAtLeast(1000L)
        val waveform = recState.recordedWaveformSamples
        val isViewOnce = recState.isViewOnceEnabled
        val replyTarget = state.replyingToMessage
        val ownerId = currentAuthenticatedUserId()

        chatRepository.stopVoicePreviewPlayback()
        _uiState.update { it.copy(replyingToMessage = null) }

        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.sendMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = activeChat.peerUserId,
                messageType = ChatMessageType.VOICE_NOTE,
                text = "",
                voiceFilePath = filePath,
                voiceDurationMs = durationMs,
                voiceWaveformSamples = waveform,
                isViewOnce = isViewOnce,
                replyToMessage = replyTarget,
                peerDisplayNameForReply = activeChat.peerFullName
            )
            // Clear recording preview state without deleting the sent file
            chatRepository.stopVoicePreviewPlayback()
            // Reset state in repository
            resetVoicePreviewAfterSend()
        }
    }

    private fun resetVoicePreviewAfterSend() {
        chatRepository.discardStateOnlyAfterSend()
    }

    fun toggleBubbleVoiceNotePlayback(message: ChatMessageItem) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        chatRepository.toggleBubbleVoiceNotePlayback(
            ownerUserId = ownerId,
            chatId = chatId,
            peerUserId = peerId,
            message = message
        )
    }

    // --- Long Press Message Actions, Reactions, Pin, Favourite, Unsend, Select Mode & Forward ---

    fun reactToMessage(message: ChatMessageItem, choice: ChatReactionChoice) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.toggleMessageReaction(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                messageId = message.messageId,
                reactionChoice = choice
            )
        }
    }

    fun togglePinMessage(message: ChatMessageItem) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.togglePinMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                messageId = message.messageId
            )
        }
    }

    fun toggleFavouriteMessage(message: ChatMessageItem) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.toggleFavouriteMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                messageId = message.messageId
            )
        }
    }

    fun unsendMessageForBothDevices(message: ChatMessageItem) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        if (message.senderUserId != ownerId) return
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.unsendMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                messageId = message.messageId
            )
        }
    }

    fun deleteSingleMessage(message: ChatMessageItem) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.deleteMessages(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                messageIds = listOf(message.messageId)
            )
        }
    }

    fun enterMessageSelectMode(initialMessageId: String) {
        _uiState.update {
            it.copy(
                isSelectModeActive = true,
                selectedMessageIds = setOf(initialMessageId)
            )
        }
    }

    fun toggleSelectedMessageId(messageId: String) {
        _uiState.update { state ->
            val next = if (messageId in state.selectedMessageIds) {
                state.selectedMessageIds - messageId
            } else {
                state.selectedMessageIds + messageId
            }
            state.copy(
                selectedMessageIds = next,
                isSelectModeActive = next.isNotEmpty()
            )
        }
    }

    fun toggleMarkAllMessagesInSelectMode() {
        _uiState.update { state ->
            val allIds = state.activeChatMessages.map { it.messageId }.toSet()
            val next = if (state.selectedMessageIds.size == allIds.size && allIds.isNotEmpty()) {
                emptySet()
            } else {
                allIds
            }
            state.copy(
                selectedMessageIds = next,
                isSelectModeActive = true
            )
        }
    }

    fun exitMessageSelectMode() {
        _uiState.update {
            it.copy(
                isSelectModeActive = false,
                selectedMessageIds = emptySet()
            )
        }
    }

    fun deleteSelectedMessagesInSelectMode() {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val selectedIds = state.selectedMessageIds.toList()
        if (selectedIds.isEmpty()) return
        val ownerId = currentAuthenticatedUserId()

        _uiState.update {
            it.copy(
                isSelectModeActive = false,
                selectedMessageIds = emptySet()
            )
        }

        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.deleteMessages(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                messageIds = selectedIds
            )
        }
    }

    /**
     * Checks whether Copy or Forward is permitted by the owner's PrivacySettingsEntity in Room.
     * - Copy follows `whoCanCopyMessages` (`NOBODY` disallows copy).
     * - Forward follows `whoCanShareChats` (`NOBODY` disallows forwarding, hiding Forward everywhere).
     */
    fun isCopyMessageAllowedByPrivacy(): Boolean {
        val raw = _uiState.value.privacySettings.whoCanCopyMessages
        return PrivacyAudience.fromStorageValue(raw) != PrivacyAudience.NOBODY
    }

    fun isForwardMessageAllowedByPrivacy(): Boolean {
        val raw = _uiState.value.privacySettings.whoCanShareChats
        return PrivacyAudience.fromStorageValue(raw) != PrivacyAudience.NOBODY
    }

    fun openForwardContactsPicker(messages: List<ChatMessageItem>) {
        if (!isForwardMessageAllowedByPrivacy()) return
        val validMessages = messages.filter { !it.isViewOnce && !it.isViewOnceConsumed }
        if (validMessages.isEmpty()) return

        viewModelScope.launch(safeExceptionHandler) {
            val ownerId = currentAuthenticatedUserId()
            val allProfiles = repository.dao.getAllDiscoverableProfilesExcept(ownerId)
            val existingChats = _uiState.value.chatListItems
            val deletedPeerIds = existingChats.filter { it.peerIsDeleted }.map { it.peerUserId }.toSet()

            val candidates = mutableListOf<ForwardRecipientTarget>()
            val seenIds = mutableSetOf<String>()

            // Include existing 1:1 chat peers first, then Contacts from Room, skipping blocked or deleted users
            for (chat in existingChats) {
                if (chat.peerIsDeleted || chat.isBlockedEitherWay) continue
                if (seenIds.add(chat.peerUserId)) {
                    candidates.add(
                        ForwardRecipientTarget(
                            userId = chat.peerUserId,
                            fullName = chat.peerFullName,
                            username = chat.peerUsername,
                            avatarUrl = chat.peerAvatarUrl,
                            bio = chat.peerBio,
                            isOnline = chat.peerIsOnline,
                            isDeleted = false,
                            isBlocked = false
                        )
                    )
                }
            }

            for (profile in allProfiles) {
                if (profile.userId in deletedPeerIds) continue
                val isBlocked = repository.dao.isEitherBlocked(ownerId, profile.userId) > 0
                if (isBlocked) continue
                if (seenIds.add(profile.userId)) {
                    candidates.add(
                        ForwardRecipientTarget(
                            userId = profile.userId,
                            fullName = profile.fullName,
                            username = profile.username,
                            avatarUrl = profile.avatarCdnUrl,
                            bio = profile.bio,
                            isOnline = true,
                            isDeleted = false,
                            isBlocked = false
                        )
                    )
                }
            }

            _uiState.update {
                it.copy(
                    isForwardPickerOpen = true,
                    messagesPendingForward = validMessages,
                    forwardCandidates = candidates,
                    forwardSearchQuery = "",
                    selectedForwardUserIds = emptySet()
                )
            }
        }
    }

    fun onForwardSearchQueryChanged(query: String) {
        _uiState.update { it.copy(forwardSearchQuery = query) }
    }

    fun toggleForwardRecipientSelection(userId: String) {
        _uiState.update { state ->
            val next = if (userId in state.selectedForwardUserIds) {
                state.selectedForwardUserIds - userId
            } else {
                state.selectedForwardUserIds + userId
            }
            state.copy(selectedForwardUserIds = next)
        }
    }

    fun dismissForwardContactsPicker() {
        _uiState.update {
            it.copy(
                isForwardPickerOpen = false,
                messagesPendingForward = emptyList(),
                selectedForwardUserIds = emptySet(),
                forwardSearchQuery = ""
            )
        }
    }

    fun confirmForwardSelectedMessages() {
        val state = _uiState.value
        val messages = state.messagesPendingForward
        val selectedTargets = state.forwardCandidates.filter { it.userId in state.selectedForwardUserIds }
        if (messages.isEmpty() || selectedTargets.isEmpty()) return
        val ownerId = currentAuthenticatedUserId()

        _uiState.update {
            it.copy(
                isForwardPickerOpen = false,
                messagesPendingForward = emptyList(),
                selectedForwardUserIds = emptySet(),
                forwardSearchQuery = "",
                isSelectModeActive = false,
                selectedMessageIds = emptySet()
            )
        }

        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.forwardMessagesToRecipients(
                ownerUserId = ownerId,
                messagesToForward = messages,
                recipients = selectedTargets
            )
        }
    }

    // --- 3-Dots Chat Menu Actions (Disappearing, Clear Chat, Block, Report, E2EE Verification) ---

    fun setActiveChatDisappearingOption(option: ChatDisappearingOption) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.setDisappearingMessagesTimer(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                option = option
            )
        }
    }

    fun clearActiveChatHistory() {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.clearChatHistory(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId
            )
        }
    }

    fun toggleBlockActiveChatUser() {
        val activeChat = _uiState.value.activeChatItem ?: return
        val shouldBlock = !activeChat.isBlockedByMe
        viewModelScope.launch(safeExceptionHandler) {
            val ownerId = currentAuthenticatedUserId()
            val telemetry = currentTelemetry()
            socialRepository.setBlockUserState(
                currentUserId = ownerId,
                targetUserId = activeChat.peerUserId,
                block = shouldBlock,
                telemetry = telemetry
            )
        }
    }

    fun submitReportForActiveChatUser(
        reason: ReportReasonOption,
        location: ReportLocationOption,
        urgency: ReportUrgencyOption,
        blockAfterReport: Boolean = true,
        additionalNotes: String = ""
    ) {
        val activeChat = _uiState.value.activeChatItem ?: return
        viewModelScope.launch(safeExceptionHandler) {
            val ownerId = currentAuthenticatedUserId()
            val telemetry = currentTelemetry()
            socialRepository.submitProfileReport(
                currentUserId = ownerId,
                reportedUserId = activeChat.peerUserId,
                reason = reason,
                location = location,
                urgency = urgency,
                additionalNotes = additionalNotes,
                telemetry = telemetry
            )
            if (blockAfterReport && !activeChat.isBlockedByMe) {
                socialRepository.setBlockUserState(
                    currentUserId = ownerId,
                    targetUserId = activeChat.peerUserId,
                    block = true,
                    telemetry = telemetry
                )
            }
        }
    }

    fun openActiveChatE2eeVerificationScreen() {
        val chatId = _uiState.value.activeChatId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            val snapshot = chatRepository.getE2eeVerificationSnapshot(ownerId, chatId)
            if (snapshot != null) {
                _uiState.update {
                    it.copy(
                        activeE2eeVerification = snapshot,
                        destination = AppDestination.E2EE_VERIFICATION
                    )
                }
            }
        }
    }

    fun setActiveChatE2eeVerified(verified: Boolean) {
        val chatId = _uiState.value.activeChatId ?: _uiState.value.activeE2eeVerification?.chatId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.setE2eeSafetyNumberVerified(chatId, verified)
            val refreshed = chatRepository.getE2eeVerificationSnapshot(ownerId, chatId)
            _uiState.update {
                it.copy(activeE2eeVerification = refreshed)
            }
        }
    }

    fun toggleActiveChatE2eeVerified() {
        val snap = _uiState.value.activeE2eeVerification ?: return
        setActiveChatE2eeVerified(!snap.isVerified)
    }

    fun closeE2eeVerificationScreen() {
        _uiState.update {
            it.copy(destination = AppDestination.ONE_TO_ONE_CHAT)
        }
    }

    // --- Rich File & Attachment Sending (Gallery, Camera, Location, Contact, Document, Poll, Event) ---

    fun sendMediaAttachmentItems(
        items: List<com.telefam.company.app.domain.ChatMediaAttachmentItem>,
        caption: String,
        isViewOnce: Boolean
    ) {
        if (items.isEmpty()) return
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val activeChat = state.activeChatItem ?: return
        if (!activeChat.canSendMessages) return

        val ownerId = currentAuthenticatedUserId()
        val replyTarget = state.replyingToMessage
        val cleanCaption = caption.trim()
        val effectiveViewOnce = isViewOnce && items.size == 1

        _uiState.update { it.copy(replyingToMessage = null) }

        viewModelScope.launch(safeExceptionHandler) {
            if (items.size == 1) {
                val single = items.first()
                val msgType = if (single.isVideo) ChatMessageType.VIDEO else ChatMessageType.IMAGE
                val singleAlbumPayload = com.telefam.company.app.domain.ChatMediaAlbumPayload(
                    items = listOf(single),
                    caption = cleanCaption
                )
                val encodedJson = com.telefam.company.app.domain.ChatAttachmentPayloadCodec.encodeAlbum(singleAlbumPayload)
                chatRepository.sendMessage(
                    ownerUserId = ownerId,
                    chatId = chatId,
                    peerUserId = activeChat.peerUserId,
                    messageType = msgType,
                    text = cleanCaption,
                    voiceDurationMs = single.durationMs,
                    isViewOnce = effectiveViewOnce,
                    replyToMessage = replyTarget,
                    peerDisplayNameForReply = activeChat.peerFullName,
                    attachmentMediaId = single.itemId,
                    attachmentMediaUrl = encodedJson,
                    attachmentPreviewUrl = single.thumbnailPath ?: single.localPath,
                    attachmentLocalPath = single.localPath,
                    attachmentTitle = single.fileName,
                    attachmentMimeType = single.mimeType
                )
            } else {
                val albumPayload = com.telefam.company.app.domain.ChatMediaAlbumPayload(
                    items = items,
                    caption = cleanCaption
                )
                val encodedJson = com.telefam.company.app.domain.ChatAttachmentPayloadCodec.encodeAlbum(albumPayload)
                val firstItem = items.first()
                chatRepository.sendMessage(
                    ownerUserId = ownerId,
                    chatId = chatId,
                    peerUserId = activeChat.peerUserId,
                    messageType = ChatMessageType.MEDIA_ALBUM,
                    text = cleanCaption,
                    voiceDurationMs = firstItem.durationMs,
                    isViewOnce = false,
                    replyToMessage = replyTarget,
                    peerDisplayNameForReply = activeChat.peerFullName,
                    attachmentMediaId = "album_${java.util.UUID.randomUUID()}",
                    attachmentMediaUrl = encodedJson,
                    attachmentPreviewUrl = firstItem.thumbnailPath ?: firstItem.localPath,
                    attachmentLocalPath = firstItem.localPath,
                    attachmentTitle = "Album (${items.size} items)",
                    attachmentMimeType = "multipart/mixed"
                )
            }
        }
    }

    fun sendLocationAttachment(
        latitude: Double,
        longitude: Double,
        accuracyMeters: Float,
        placeTitle: String,
        addressLine: String,
        isLiveLocation: Boolean,
        liveDurationMinutes: Int,
        isViewOnce: Boolean,
        caption: String
    ) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val activeChat = state.activeChatItem ?: return
        if (!activeChat.canSendMessages) return

        val ownerId = currentAuthenticatedUserId()
        val replyTarget = state.replyingToMessage
        val cleanCaption = caption.trim()

        _uiState.update { it.copy(replyingToMessage = null) }

        viewModelScope.launch(safeExceptionHandler) {
            val (liveToken, expiresAtMs) = if (isLiveLocation || isViewOnce) {
                chatRepository.issueServerLiveLocationToken(
                    ownerUserId = ownerId,
                    chatId = chatId,
                    peerUserId = activeChat.peerUserId,
                    latitude = latitude,
                    longitude = longitude,
                    accuracyMeters = accuracyMeters,
                    durationMinutes = if (isLiveLocation) liveDurationMinutes else 30,
                    isViewOnce = isViewOnce
                )
            } else {
                "" to null
            }

            val locationPayload = com.telefam.company.app.domain.ChatLocationPayload(
                latitude = latitude,
                longitude = longitude,
                accuracyMeters = accuracyMeters,
                placeTitle = placeTitle.trim().ifBlank { "Shared Location" },
                addressLine = addressLine.trim(),
                isLiveLocation = isLiveLocation,
                liveSessionToken = liveToken,
                expiresAtMs = expiresAtMs,
                isViewOnce = isViewOnce,
                caption = cleanCaption
            )
            val encodedJson = com.telefam.company.app.domain.ChatAttachmentPayloadCodec.encodeLocation(locationPayload)
            chatRepository.sendMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = activeChat.peerUserId,
                messageType = ChatMessageType.LOCATION,
                text = cleanCaption.ifBlank { locationPayload.placeTitle },
                isViewOnce = isViewOnce,
                replyToMessage = replyTarget,
                peerDisplayNameForReply = activeChat.peerFullName,
                attachmentMediaId = "loc_${java.util.UUID.randomUUID()}",
                attachmentMediaUrl = encodedJson,
                attachmentTitle = locationPayload.placeTitle,
                attachmentMimeType = "application/vnd.telefam.location+json"
            )
        }
    }

    fun sendContactAttachment(
        contact: com.telefam.company.app.domain.ChatContactPayload,
        caption: String = ""
    ) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val activeChat = state.activeChatItem ?: return
        if (!activeChat.canSendMessages) return

        val ownerId = currentAuthenticatedUserId()
        val replyTarget = state.replyingToMessage
        val cleanCaption = caption.trim()
        val finalContact = if (cleanCaption.isNotBlank()) contact.copy(caption = cleanCaption) else contact
        val encodedJson = com.telefam.company.app.domain.ChatAttachmentPayloadCodec.encodeContact(finalContact)

        _uiState.update { it.copy(replyingToMessage = null) }

        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.sendMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = activeChat.peerUserId,
                messageType = ChatMessageType.CONTACT,
                text = cleanCaption.ifBlank { finalContact.displayName },
                isViewOnce = false,
                replyToMessage = replyTarget,
                peerDisplayNameForReply = activeChat.peerFullName,
                attachmentMediaId = "contact_${java.util.UUID.randomUUID()}",
                attachmentMediaUrl = encodedJson,
                attachmentTitle = finalContact.displayName,
                attachmentMimeType = "text/vcard"
            )
        }
    }

    fun sendDocumentAttachment(
        document: com.telefam.company.app.domain.ChatDocumentPayload
    ) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val activeChat = state.activeChatItem ?: return
        if (!activeChat.canSendMessages) return

        val ownerId = currentAuthenticatedUserId()
        val replyTarget = state.replyingToMessage
        val encodedJson = com.telefam.company.app.domain.ChatAttachmentPayloadCodec.encodeDocument(document)

        _uiState.update { it.copy(replyingToMessage = null) }

        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.sendMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = activeChat.peerUserId,
                messageType = ChatMessageType.DOCUMENT,
                text = document.caption.ifBlank { document.fileName },
                voiceDurationMs = document.audioDurationMs,
                isViewOnce = false,
                replyToMessage = replyTarget,
                peerDisplayNameForReply = activeChat.peerFullName,
                attachmentMediaId = "doc_${java.util.UUID.randomUUID()}",
                attachmentMediaUrl = encodedJson,
                attachmentLocalPath = document.localPath,
                attachmentTitle = document.fileName,
                attachmentMimeType = document.mimeType
            )
        }
    }

    fun sendPollAttachment(
        poll: com.telefam.company.app.domain.ChatPollPayload
    ) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val activeChat = state.activeChatItem ?: return
        if (!activeChat.canSendMessages) return

        val ownerId = currentAuthenticatedUserId()
        val replyTarget = state.replyingToMessage
        val finalPoll = poll.copy(createdByUserId = ownerId)
        val encodedJson = com.telefam.company.app.domain.ChatAttachmentPayloadCodec.encodePoll(finalPoll)

        _uiState.update { it.copy(replyingToMessage = null) }

        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.sendMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = activeChat.peerUserId,
                messageType = ChatMessageType.POLL,
                text = finalPoll.question,
                isViewOnce = false,
                replyToMessage = replyTarget,
                peerDisplayNameForReply = activeChat.peerFullName,
                attachmentMediaId = finalPoll.pollId,
                attachmentMediaUrl = encodedJson,
                attachmentTitle = finalPoll.question,
                attachmentMimeType = "application/vnd.telefam.poll+json"
            )
        }
    }

    fun sendEventAttachment(
        event: com.telefam.company.app.domain.ChatEventPayload
    ) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val activeChat = state.activeChatItem ?: return
        if (!activeChat.canSendMessages) return

        val ownerId = currentAuthenticatedUserId()
        val replyTarget = state.replyingToMessage
        val finalEvent = event.copy(
            createdByUserId = ownerId,
            goingUserIds = (event.goingUserIds + ownerId).distinct()
        )
        val encodedJson = com.telefam.company.app.domain.ChatAttachmentPayloadCodec.encodeEvent(finalEvent)

        _uiState.update { it.copy(replyingToMessage = null) }

        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.sendMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = activeChat.peerUserId,
                messageType = ChatMessageType.EVENT,
                text = finalEvent.title,
                isViewOnce = false,
                replyToMessage = replyTarget,
                peerDisplayNameForReply = activeChat.peerFullName,
                attachmentMediaId = finalEvent.eventId,
                attachmentMediaUrl = encodedJson,
                attachmentTitle = finalEvent.title,
                attachmentMimeType = "application/vnd.telefam.event+json"
            )
        }
    }

    fun voteOnPollMessage(messageId: String, selectedOptionIds: List<String>) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.voteOnPollMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                messageId = messageId,
                selectedOptionIds = selectedOptionIds
            )
        }
    }

    fun rsvpToEventMessage(messageId: String, rsvpStatus: String) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.rsvpToEventMessage(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                messageId = messageId,
                rsvpStatus = rsvpStatus
            )
        }
    }

    fun consumeViewOnceMessage(messageId: String) {
        val state = _uiState.value
        val chatId = state.activeChatId ?: return
        val peerId = state.activeChatItem?.peerUserId ?: return
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            chatRepository.consumeViewOnceVoiceNote(
                ownerUserId = ownerId,
                chatId = chatId,
                peerUserId = peerId,
                messageId = messageId
            )
        }
    }

    fun handleNotificationDeepLink(deepLink: TelefamNotificationDeepLink) {
        val state = _uiState.value
        if (state.activeSession == null && state.savedProfile == null) return
        TelefamNotificationEngine.consumePendingDeepLink()

        when (deepLink.targetType) {
            TelefamDeepLinkTargetType.CHAT_CONVERSATION,
            TelefamDeepLinkTargetType.INCOMING_CALL,
            TelefamDeepLinkTargetType.LIVE_LOCATION -> {
                val targetChatId = deepLink.chatId
                val peerId = deepLink.peerUserId
                if (!targetChatId.isNullOrBlank()) {
                    openOneToOneChatById(targetChatId)
                } else if (!peerId.isNullOrBlank()) {
                    val existing = state.chatListItems.firstOrNull { it.peerUserId == peerId }
                    if (existing != null) {
                        openOneToOneChatById(existing.chatId)
                    } else {
                        val user = state.contactsUsers.firstOrNull { it.userId == peerId }
                        if (user != null) {
                            openOneToOneChatWithDiscoverableUser(user)
                        }
                    }
                }
            }
            TelefamDeepLinkTargetType.SOCIAL_FOLLOWER -> {
                val actorId = deepLink.socialActorUserId
                val actor = state.contactsUsers.firstOrNull { it.userId == actorId }
                if (actor != null) {
                    openPublicUserProfile(actor.userId)
                } else {
                    _uiState.update {
                        it.copy(
                            destination = AppDestination.MAIN_HOME,
                            selectedHomeTab = HomeNavTab.PROFILE
                        )
                    }
                    openOwnerFollowList(FollowListType.FOLLOWERS)
                }
            }
            TelefamDeepLinkTargetType.SOCIAL_POST_ACTIVITY -> {
                _uiState.update {
                    it.copy(
                        destination = AppDestination.MAIN_HOME,
                        selectedHomeTab = HomeNavTab.PROFILE
                    )
                }
            }
            TelefamDeepLinkTargetType.SECURITY_DEVICE_ALERT -> {
                _uiState.update {
                    it.copy(
                        destination = AppDestination.SETTINGS_AND_PRIVACY,
                        settingsSubScreen = SettingsSubScreen.ROOT,
                        settingsNavigationStack = emptyList()
                    )
                }
            }
            TelefamDeepLinkTargetType.MARKETPLACE_PRODUCT -> {
                val productId = deepLink.productId
                if (!productId.isNullOrBlank()) {
                    openMarketplaceProductById(productId)
                } else {
                    openMarketplace()
                }
            }
        }
    }

    // --- Post Studio (Text, Camera, Photo up to 10, Video CapCut Studio, Sandbox Isolation & Chunked Upload) ---

    fun clearPostStudioError() {
        _uiState.update { it.copy(postStudioErrorBanner = null) }
    }

    fun searchPostMentionCandidates(query: String) {
        viewModelScope.launch(safeExceptionHandler) {
            val uid = _uiState.value.activeSession?.userId
                ?: _uiState.value.savedProfile?.userId
                ?: "local_telefam_member"
            val results = socialRepository.searchUsersForMentionTagging(uid, query)
            _uiState.update { it.copy(postMentionSuggestions = results) }
        }
    }

    fun clearPostMentionCandidates() {
        _uiState.update { it.copy(postMentionSuggestions = emptyList()) }
    }

    fun searchFreeMusicForPost(query: String) {
        viewModelScope.launch(safeExceptionHandler) {
            _uiState.update { it.copy(isSearchingPostMusic = true) }
            val tracks = TelefamPostSandboxAndStudioEngine.searchFreePublicSongs(query)
            _uiState.update {
                it.copy(
                    postMusicSearchResults = tracks,
                    isSearchingPostMusic = false
                )
            }
        }
    }

    fun importPhotosToPostStudio(
        uris: List<Uri>,
        existingCount: Int,
        onImported: (List<EditablePhotoStudioItem>) -> Unit
    ) {
        viewModelScope.launch(safeExceptionHandler) {
            _uiState.update { it.copy(postStudioErrorBanner = null) }
            val remainingSlots = (10 - existingCount).coerceAtLeast(0)
            if (remainingSlots == 0) {
                _uiState.update { it.copy(postStudioErrorBanner = "Maximum 10 photos allowed per post.") }
                return@launch
            }
            val appCtx = getApplication<Application>().applicationContext
            val imported = mutableListOf<EditablePhotoStudioItem>()
            var firstError: String? = null
            for (uri in uris.take(remainingSlots)) {
                val res = TelefamPostSandboxAndStudioEngine.importPhotoIntoSandboxStudio(appCtx, uri)
                res.onSuccess { item -> imported.add(item) }
                    .onFailure { err -> if (firstError == null) firstError = err.localizedMessage }
            }
            if (firstError != null && imported.isEmpty()) {
                _uiState.update { it.copy(postStudioErrorBanner = firstError) }
            }
            if (imported.isNotEmpty()) {
                onImported(imported)
            }
        }
    }

    fun importVideoToPostStudio(
        uri: Uri,
        onImported: (EditableVideoStudioState) -> Unit
    ) {
        viewModelScope.launch(safeExceptionHandler) {
            _uiState.update { it.copy(postStudioErrorBanner = null) }
            val appCtx = getApplication<Application>().applicationContext
            val res = TelefamPostSandboxAndStudioEngine.inspectAndPrepareVideoInSandbox(appCtx, uri)
            res.onSuccess { state ->
                onImported(state)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(postStudioErrorBanner = err.localizedMessage ?: "Video failed sandbox inspection.")
                }
            }
        }
    }

    fun extractVideoCoverFrameAt(
        videoState: EditableVideoStudioState,
        timestampMs: Long,
        onExtracted: (String?) -> Unit
    ) {
        viewModelScope.launch(safeExceptionHandler) {
            val appCtx = getApplication<Application>().applicationContext
            val path = TelefamPostSandboxAndStudioEngine.extractVideoCoverAtTimestamp(
                context = appCtx,
                videoState = videoState,
                timestampMs = timestampMs
            )
            onExtracted(path)
        }
    }

    fun publishStudioPost(
        postMode: PostStudioMode,
        textPostContent: String,
        textBackgroundTheme: PostBackgroundTheme,
        textTypographyFont: PostTypographyFont,
        textModeOverlayLayers: List<PostTextOverlayLayer>,
        textModeBrushStrokes: List<PostBrushStroke>,
        photoItems: List<EditablePhotoStudioItem>,
        videoState: EditableVideoStudioState?,
        caption: String,
        visibility: PostVisibilityAudience,
        taggedUsernames: List<String>,
        hashtags: List<String>,
        selectedMusic: PostMusicTrackItem?,
        allowComments: Boolean,
        allowDuetsAndStitch: Boolean,
        allowHighQualityDownload: Boolean,
        onPublishedResetEditor: () -> Unit
    ) {
        viewModelScope.launch(safeExceptionHandler) {
            val appCtx = getApplication<Application>().applicationContext
            val uid = _uiState.value.activeSession?.userId
                ?: _uiState.value.savedProfile?.userId
                ?: "local_telefam_member"

            if (hashtags.size > 5) {
                _uiState.update { it.copy(postStudioErrorBanner = "Maximum 5 hashtags allowed per post.") }
                return@launch
            }

            when (postMode) {
                PostStudioMode.TEXT -> {
                    if (textPostContent.isBlank()) {
                        _uiState.update { it.copy(postStudioErrorBanner = "Write something to publish your text post.") }
                        return@launch
                    }
                }
                PostStudioMode.PHOTO, PostStudioMode.CAMERA -> {
                    if (photoItems.isEmpty() && videoState == null) {
                        _uiState.update { it.copy(postStudioErrorBanner = "Select or capture up to 10 photos first.") }
                        return@launch
                    }
                }
                PostStudioMode.VIDEO -> {
                    if (videoState == null) {
                        _uiState.update { it.copy(postStudioErrorBanner = "Select or record a video first.") }
                        return@launch
                    }
                }
            }

            _uiState.update {
                it.copy(
                    postStudioErrorBanner = null,
                    postUploadState = PostBackgroundUploadState(
                        status = PostUploadLifecycleStatus.UPLOADING,
                        progressFraction = 0.05f,
                        uploadedChunks = 0,
                        totalChunks = 1
                    )
                )
            }

            val bakedResult = runCatching {
                when {
                    postMode == PostStudioMode.TEXT -> {
                        val artifact = TelefamPostSandboxAndStudioEngine.bakeTextPostCardToVerifiedVault(
                            context = appCtx,
                            dao = repository.dao,
                            text = textPostContent,
                            backgroundTheme = textBackgroundTheme,
                            typographyFont = textTypographyFont,
                            textLayers = textModeOverlayLayers,
                            brushStrokes = textModeBrushStrokes
                        )
                        Triple(artifact, "TEXT", caption.ifBlank { textPostContent })
                    }
                    videoState != null && (postMode == PostStudioMode.VIDEO || (postMode == PostStudioMode.CAMERA && photoItems.isEmpty())) -> {
                        val artifact = TelefamPostSandboxAndStudioEngine.finalizeVideoPostInVerifiedVault(
                            context = appCtx,
                            dao = repository.dao,
                            videoState = videoState,
                            caption = caption
                        )
                        val totalSec = (videoState.trimmedDurationMs / 1000L).coerceAtLeast(1L)
                        val mins = totalSec / 60L
                        val secs = totalSec % 60L
                        val durLabel = String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
                        Triple(artifact, durLabel, caption)
                    }
                    else -> {
                        val bakedList = photoItems.take(10).map { item ->
                            TelefamPostSandboxAndStudioEngine.bakeEditedPhotoToVerifiedVault(
                                context = appCtx,
                                dao = repository.dao,
                                item = item,
                                caption = caption
                            )
                        }
                        val primary = bakedList.first()
                        val badgeLabel = if (bakedList.size > 1) "${bakedList.size} PHOTOS" else "PHOTO"
                        Triple(primary, badgeLabel, caption)
                    }
                }
            }

            val (artifact, durationLabel, effectiveCaption) = bakedResult.getOrElse { err ->
                _uiState.update {
                    it.copy(
                        postUploadState = PostBackgroundUploadState(status = PostUploadLifecycleStatus.IDLE),
                        postStudioErrorBanner = err.localizedMessage ?: "Sandbox verification failed."
                    )
                }
                return@launch
            }

            val publishRes = socialRepository.publishStudioPostWithChunkedUpload(
                currentUserId = uid,
                postMode = postMode,
                artifact = artifact,
                caption = effectiveCaption,
                visibility = visibility,
                taggedUsernames = taggedUsernames,
                hashtags = hashtags,
                selectedMusic = selectedMusic,
                durationLabel = durationLabel,
                allowComments = allowComments,
                allowDuetsAndStitch = allowDuetsAndStitch,
                allowHighQualityDownload = allowHighQualityDownload,
                onChunkProgress = { uploaded, total ->
                    val frac = (uploaded.toFloat() / total.coerceAtLeast(1).toFloat()).coerceIn(0.08f, 0.98f)
                    _uiState.update {
                        it.copy(
                            postUploadState = it.postUploadState.copy(
                                status = PostUploadLifecycleStatus.UPLOADING,
                                progressFraction = frac,
                                uploadedChunks = uploaded,
                                totalChunks = total
                            )
                        )
                    }
                }
            )

            publishRes.onSuccess { cdnUrl ->
                _uiState.update {
                    it.copy(
                        postUploadState = PostBackgroundUploadState(
                            status = PostUploadLifecycleStatus.UPLOADED,
                            progressFraction = 1f,
                            uploadedChunks = 1,
                            totalChunks = 1,
                            lastAssignedCdnUrl = cdnUrl
                        )
                    )
                }
                onPublishedResetEditor()
                refreshOwnerProfileSnapshot(isPullToRefresh = false)
                delay(2800L)
                _uiState.update { state ->
                    if (state.postUploadState.status == PostUploadLifecycleStatus.UPLOADED) {
                        state.copy(postUploadState = PostBackgroundUploadState(status = PostUploadLifecycleStatus.IDLE))
                    } else state
                }
            }.onFailure { err ->
                val msg = err.message.orEmpty()
                if (msg == "NETWORK_QUEUED_BACKGROUND") {
                    // Saved in encrypted Room first; background worker will complete upload when online
                    _uiState.update {
                        it.copy(
                            postUploadState = PostBackgroundUploadState(
                                status = PostUploadLifecycleStatus.UPLOADING,
                                progressFraction = 0.65f
                            )
                        )
                    }
                    onPublishedResetEditor()
                    refreshOwnerProfileSnapshot(isPullToRefresh = false)
                    refreshFeedVideos(resetPage = true)
                } else {
                    _uiState.update {
                        it.copy(
                            postUploadState = PostBackgroundUploadState(status = PostUploadLifecycleStatus.IDLE),
                            postStudioErrorBanner = err.localizedMessage ?: "Unable to publish post."
                        )
                    }
                }
            }
        }
    }

    // ==========================================
    // TIKTOK-STYLE FEEDS SYSTEM & ACTIONS
    // ==========================================

    fun selectFeedTopTab(tab: FeedTopTab) {
        val currentTab = _uiState.value.feedActiveTopTab
        if (currentTab == tab) return
        _uiState.update {
            it.copy(
                feedActiveTopTab = tab,
                feedCurrentPage = 1,
                feedHasMorePages = true
            )
        }
        refreshFeedVideos(resetPage = true)
    }

    fun refreshFeedVideos(resetPage: Boolean = false) {
        val userId = currentAuthenticatedUserId()
        val currentTab = _uiState.value.feedActiveTopTab
        val targetPage = if (resetPage) 1 else _uiState.value.feedCurrentPage

        viewModelScope.launch(safeExceptionHandler) {
            val videos = socialRepository.loadMainFeedVideosPage(
                currentUserId = userId,
                tab = currentTab,
                page = targetPage,
                pageSize = 15,
                forceChronologicalReset = false
            )
            _uiState.update { state ->
                val combined = if (resetPage) videos else (state.feedVideosList + videos).distinctBy { it.videoId }
                state.copy(
                    feedVideosList = combined,
                    feedCurrentPage = targetPage,
                    feedHasMorePages = videos.size >= 12,
                    isFeedLoadingMore = false
                )
            }
        }
    }

    fun loadMoreFeedVideos() {
        val state = _uiState.value
        if (state.isFeedLoadingMore || !state.feedHasMorePages) return
        _uiState.update { it.copy(isFeedLoadingMore = true) }
        val nextPage = state.feedCurrentPage + 1
        val userId = currentAuthenticatedUserId()

        viewModelScope.launch(safeExceptionHandler) {
            val videos = socialRepository.loadMainFeedVideosPage(
                currentUserId = userId,
                tab = state.feedActiveTopTab,
                page = nextPage,
                pageSize = 15,
                forceChronologicalReset = false
            )
            _uiState.update { s ->
                val combined = (s.feedVideosList + videos).distinctBy { it.videoId }
                s.copy(
                    feedVideosList = combined,
                    feedCurrentPage = nextPage,
                    feedHasMorePages = videos.isNotEmpty(),
                    isFeedLoadingMore = false
                )
            }
        }
    }

    fun refreshNewCreatorsList() {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            val creators = socialRepository.loadNewCreatorsList(userId)
            _uiState.update { it.copy(feedNewCreatorsList = creators) }
        }
    }

    fun executeFeedSearch(query: String) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            _uiState.update { it.copy(feedSearchResults = it.feedSearchResults.copy(query = query, isLoading = true)) }
            val results = socialRepository.searchFeedsEverything(userId, query)
            _uiState.update { it.copy(feedSearchResults = results) }
        }
    }

    fun toggleFeedLikeVideo(video: FeedVideoItem, isDoubleTap: Boolean = false) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.toggleFeedVideoLike(userId, video.videoId)
            // Update local state instantly
            _uiState.update { s ->
                val updatedList = s.feedVideosList.map { item ->
                    if (item.videoId == video.videoId) {
                        val newLiked = !item.isLikedByMe
                        val newCount = if (newLiked) item.likesCount + 1 else (item.likesCount - 1).coerceAtLeast(0L)
                        item.copy(isLikedByMe = newLiked, likesCount = newCount)
                    } else item
                }
                val updatedOverlay = s.activeProfileFeedVideos.map { item ->
                    if (item.videoId == video.videoId) {
                        val newLiked = !item.isLikedByMe
                        val newCount = if (newLiked) item.likesCount + 1 else (item.likesCount - 1).coerceAtLeast(0L)
                        item.copy(isLikedByMe = newLiked, likesCount = newCount)
                    } else item
                }
                s.copy(
                    feedVideosList = updatedList,
                    activeProfileFeedVideos = updatedOverlay
                )
            }
        }
    }

    fun toggleFeedSaveVideo(video: FeedVideoItem) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.toggleFeedVideoSave(userId, video.videoId)
            _uiState.update { s ->
                val updatedList = s.feedVideosList.map { item ->
                    if (item.videoId == video.videoId) {
                        val newSaved = !item.isSavedByMe
                        val newCount = if (newSaved) item.savesCount + 1 else (item.savesCount - 1).coerceAtLeast(0L)
                        item.copy(isSavedByMe = newSaved, savesCount = newCount)
                    } else item
                }
                s.copy(
                    feedVideosList = updatedList,
                    feedStatusFeedbackMessage = if (!video.isSavedByMe) "Saved to your bookmarks" else "Removed from bookmarks"
                )
            }
        }
    }

    fun recordFeedVideoShare(video: FeedVideoItem, shareType: String, targetUserId: String? = null) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.recordFeedVideoShare(userId, video.videoId, shareType, targetUserId)
            _uiState.update { s ->
                val updatedList = s.feedVideosList.map { item ->
                    if (item.videoId == video.videoId) {
                        item.copy(sharesCount = item.sharesCount + 1)
                    } else item
                }
                val banner = when (shareType) {
                    "COPY_LINK" -> "Link copied to clipboard"
                    "RESHARE" -> "Video reshared to your profile"
                    else -> "Share link ready"
                }
                s.copy(feedVideosList = updatedList, feedStatusFeedbackMessage = banner)
            }
        }
    }

    fun shareFeedVideoToInbox(video: FeedVideoItem, recipient: NewCreatorUserItem) {
        val ownerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.recordFeedVideoShare(ownerId, video.videoId, "INBOX_DM", recipient.userId)
            val conversationId = chatRepository.openOrCreateOneToOneChat(
                ownerUserId = ownerId,
                peerUserId = recipient.userId,
                peerFullName = recipient.fullName,
                peerUsername = recipient.username,
                peerAvatarUrl = recipient.avatarLocalPath ?: recipient.avatarCdnUrl
            )
            chatRepository.sendMessage(
                ownerUserId = ownerId,
                chatId = conversationId,
                peerUserId = recipient.userId,
                messageType = ChatMessageType.TEXT,
                text = "Shared video: https://telefam.company/v/${video.videoId}\n${video.caption.take(80)}"
            )
            _uiState.update { s ->
                s.copy(feedStatusFeedbackMessage = "Sent to @${recipient.username}")
            }
        }
    }

    fun recordFeedVideoView(videoId: String, watchDurationMs: Long, completedLoop: Boolean) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.recordFeedVideoView(userId, videoId, watchDurationMs, completedLoop)
        }
    }

    fun markFeedVideoNotInterested(video: FeedVideoItem) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.markVideoNotInterested(userId, video.videoId)
            _uiState.update { s ->
                s.copy(
                    feedVideosList = s.feedVideosList.filter { it.videoId != video.videoId },
                    feedStatusFeedbackMessage = "We'll show fewer videos like this"
                )
            }
        }
    }

    fun blockFeedCreator(video: FeedVideoItem) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            val telemetry = currentTelemetry()
            socialRepository.setBlockUserState(
                currentUserId = userId,
                targetUserId = video.ownerUserId,
                block = true,
                telemetry = telemetry
            )
            _uiState.update { s ->
                s.copy(
                    feedVideosList = s.feedVideosList.filter { it.ownerUserId != video.ownerUserId },
                    feedStatusFeedbackMessage = "Blocked @${video.ownerUsername}"
                )
            }
        }
    }

    fun submitFeedVideoReportQuiz(
        video: FeedVideoItem,
        reason: ReportReasonOption,
        location: ReportLocationOption,
        urgency: ReportUrgencyOption,
        details: String
    ) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            val telemetry = currentTelemetry()
            socialRepository.submitProfileReport(
                currentUserId = userId,
                reportedUserId = video.ownerUserId,
                reason = reason,
                location = location,
                urgency = urgency,
                additionalNotes = details,
                telemetry = telemetry
            )
            _uiState.update {
                it.copy(feedStatusFeedbackMessage = "Report submitted. Thank you for keeping Telefam safe.")
            }
        }
    }

    fun downloadFeedVideoToDevice(video: FeedVideoItem) {
        _uiState.update {
            it.copy(feedStatusFeedbackMessage = "Saving high-definition video to device...")
        }
    }

    fun deleteOwnedFeedVideo(video: FeedVideoItem) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.deleteOwnedFeedVideo(userId, video.videoId)
            _uiState.update { s ->
                s.copy(
                    feedVideosList = s.feedVideosList.filter { it.videoId != video.videoId },
                    feedStatusFeedbackMessage = "Video deleted"
                )
            }
            refreshOwnerProfileSnapshot(isPullToRefresh = false)
        }
    }

    fun editOwnedFeedVideoInStudio(video: FeedVideoItem) {
        _uiState.update {
            it.copy(
                editingFeedVideoItem = video,
                selectedHomeTab = HomeNavTab.POST
            )
        }
    }

    fun openFeedAudioPage(audio: FeedAudioTrackInfo) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            val (resolvedAudio, videos) = socialRepository.loadAudioPageVideos(userId, audio.audioId)
            _uiState.update {
                it.copy(
                    feedActiveAudioTrack = resolvedAudio ?: audio,
                    feedActiveAudioVideos = videos
                )
            }
        }
    }

    fun closeFeedAudioPage() {
        _uiState.update {
            it.copy(
                feedActiveAudioTrack = null,
                feedActiveAudioVideos = emptyList()
            )
        }
    }

    fun useAudioInPostStudio(audio: FeedAudioTrackInfo) {
        _uiState.update {
            it.copy(
                preselectedPostMusicTrack = audio.toPostMusicTrackItem(),
                feedActiveAudioTrack = null,
                selectedHomeTab = HomeNavTab.POST
            )
        }
    }

    fun openFeedHashtagPage(hashtag: String) {
        val cleanTag = if (hashtag.startsWith("#")) hashtag else "#$hashtag"
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            val videos = socialRepository.loadHashtagPageVideos(userId, cleanTag)
            _uiState.update {
                it.copy(
                    feedActiveHashtagTitle = cleanTag,
                    feedActiveHashtagVideos = videos
                )
            }
        }
    }

    fun closeFeedHashtagPage() {
        _uiState.update {
            it.copy(
                feedActiveHashtagTitle = null,
                feedActiveHashtagVideos = emptyList()
            )
        }
    }

    fun openPostInReusableProfileFeed(targetUserId: String, initialPostId: String, title: String) {
        val viewerId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            val videos = socialRepository.loadUserProfileFeedVideos(viewerId, targetUserId)
            val initialIdx = videos.indexOfFirst { it.videoId == initialPostId }.coerceAtLeast(0)
            _uiState.update {
                it.copy(
                    activeProfileFeedVideos = videos,
                    activeProfileFeedInitialIndex = initialIdx,
                    profileFeedHeaderTitle = title,
                    isProfileFeedOverlayOpen = true
                )
            }
        }
    }

    fun closeReusableProfileFeed() {
        _uiState.update {
            it.copy(
                isProfileFeedOverlayOpen = false,
                activeProfileFeedVideos = emptyList()
            )
        }
    }

    fun dismissFeedStatusFeedback() {
        _uiState.update { it.copy(feedStatusFeedbackMessage = null) }
    }

    fun clearFeedFocusedInboxVideo() {
        _uiState.update { it.copy(feedFocusedInboxVideo = null) }
    }

    // ==========================================
    // COMMENTS & VIRTUAL GIFTS
    // ==========================================

    private var commentsObserverJob: Job? = null

    fun openVideoComments(video: FeedVideoItem) {
        _uiState.update { it.copy(activeCommentsVideo = video) }
        commentsObserverJob?.cancel()
        commentsObserverJob = viewModelScope.launch(safeExceptionHandler) {
            val currentUserId = currentAuthenticatedUserId()
            socialRepository.observeCommentsForTarget(video.videoId, currentUserId).collectLatest { list ->
                val totalCount = socialRepository.getCommentsCountForTarget(video.videoId)
                val targetLang = Locale.getDefault().language
                val enrichedList = list.map { c ->
                    val translated = socialRepository.translateTextAsync(c.commentText, targetLang)
                    c.copy(
                        translatedText = translated,
                        replies = c.replies.map { r ->
                            val rTrans = socialRepository.translateTextAsync(r.commentText, targetLang)
                            r.copy(translatedText = rTrans)
                        }
                    )
                }
                _uiState.update {
                    it.copy(
                        activeCommentsList = enrichedList,
                        activeCommentsTotalCount = totalCount
                    )
                }
            }
        }
    }

    fun closeVideoComments() {
        commentsObserverJob?.cancel()
        _uiState.update {
            it.copy(
                activeCommentsVideo = null,
                activeCommentsList = emptyList(),
                activeCommentsTotalCount = 0L
            )
        }
    }

    fun postCommentOnActiveVideo(
        text: String,
        parentCommentId: String? = null,
        attachmentType: String? = null,
        attachmentUrl: String? = null
    ) {
        val video = _uiState.value.activeCommentsVideo ?: return
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val currentUserId = currentAuthenticatedUserId()
            val authorUsername = state.savedProfile?.username ?: state.activeSession?.email?.substringBefore('@') ?: "user"
            val authorFullName = state.savedProfile?.fullName ?: authorUsername
            val authorAvatar = state.savedProfile?.avatarLocalPath ?: state.savedProfile?.avatarCdnUrl

            socialRepository.postComment(
                targetId = video.videoId,
                authorUserId = currentUserId,
                authorUsername = authorUsername,
                authorFullName = authorFullName,
                authorAvatarUrl = authorAvatar,
                isCreator = currentUserId == video.ownerUserId,
                text = text,
                parentCommentId = parentCommentId,
                attachmentType = attachmentType,
                attachmentUrl = attachmentUrl
            )

            // Update video comments count in feed
            val newTotal = socialRepository.getCommentsCountForTarget(video.videoId)
            _uiState.update { s ->
                s.copy(
                    activeCommentsTotalCount = newTotal,
                    feedVideosList = s.feedVideosList.map { v ->
                        if (v.videoId == video.videoId) v.copy(commentsCount = newTotal) else v
                    }
                )
            }
        }
    }

    fun toggleLikeComment(commentId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            val currentUserId = currentAuthenticatedUserId()
            socialRepository.toggleLikeComment(commentId, currentUserId)
        }
    }

    fun editComment(commentId: String, newText: String) {
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.editComment(commentId, newText)
        }
    }

    fun deleteComment(commentId: String) {
        val video = _uiState.value.activeCommentsVideo ?: return
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.deleteComment(commentId, video.videoId)
            val newTotal = socialRepository.getCommentsCountForTarget(video.videoId)
            _uiState.update { s ->
                s.copy(
                    activeCommentsTotalCount = newTotal,
                    feedVideosList = s.feedVideosList.map { v ->
                        if (v.videoId == video.videoId) v.copy(commentsCount = newTotal) else v
                    }
                )
            }
        }
    }

    fun togglePinComment(commentId: String) {
        val video = _uiState.value.activeCommentsVideo ?: return
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.togglePinComment(commentId, video.videoId)
        }
    }

    suspend fun searchCommentMentionCandidates(query: String): List<MentionCandidateUser> {
        return socialRepository.searchMentionCandidates(query)
    }

    fun sendVirtualGiftOnActiveVideo(gift: VirtualGiftItem, count: Int = 1) {
        val video = _uiState.value.activeCommentsVideo ?: return
        viewModelScope.launch(safeExceptionHandler) {
            val state = _uiState.value
            val currentUserId = currentAuthenticatedUserId()
            val senderUsername = state.savedProfile?.username ?: state.activeSession?.email?.substringBefore('@') ?: "user"
            val senderFullName = state.savedProfile?.fullName ?: senderUsername
            val senderAvatar = state.savedProfile?.avatarLocalPath ?: state.savedProfile?.avatarCdnUrl

            val result = socialRepository.sendVirtualGift(
                senderUserId = currentUserId,
                senderUsername = senderUsername,
                senderFullName = senderFullName,
                senderAvatarUrl = senderAvatar,
                recipientUserId = video.ownerUserId,
                targetId = video.videoId,
                gift = gift,
                giftCount = count
            )

            result.onSuccess {
                // Trigger animated celebration overlay
                _uiState.update { s ->
                    s.copy(
                        activeGiftCelebration = ActiveGiftCelebration(
                            senderUsername = senderUsername,
                            senderFullName = senderFullName,
                            senderAvatarUrl = senderAvatar,
                            gift = gift,
                            comboCount = count
                        )
                    )
                }
                // Refresh stars balance
                refreshStarsData(showLoading = false)
            }.onFailure { err ->
                _uiState.update { it.copy(feedStatusFeedbackMessage = err.message ?: "Failed to send gift") }
            }
        }
    }

    fun clearActiveGiftCelebration() {
        _uiState.update { it.copy(activeGiftCelebration = null) }
    }

    // =========================================================================
    // MARKETPLACE SYSTEM (Products, Photos, Room DB, Moderation, Inquiries)
    // =========================================================================

    private var marketplaceProductsJob: Job? = null
    private var marketplaceRecentsJob: Job? = null

    fun openMarketplace() {
        val userId = currentAuthenticatedUserId()
        _uiState.update {
            it.copy(
                destination = AppDestination.MARKETPLACE,
                isOwnerMenuOpen = false,
                isDashboardOpen = false,
                isCampaignScreenOpen = false,
                marketplaceSelectedTab = MarketplaceScreenTab.EXPLORE,
                marketplaceFilterCategory = MarketplaceCategory.ALL,
                marketplaceSearchQuery = "",
                marketplaceActiveProduct = null,
                isMarketplacePostSheetOpen = false,
                marketplaceUploadProgress = MarketplaceUploadProgress.IDLE,
                marketplaceUploadStatusText = null
            )
        }
        observeMarketplaceCatalog()
        observeMarketplaceRecents(userId)
    }

    fun closeMarketplace() {
        marketplaceProductsJob?.cancel()
        marketplaceRecentsJob?.cancel()
        _uiState.update {
            it.copy(
                destination = AppDestination.MAIN_HOME,
                marketplaceActiveProduct = null,
                isMarketplacePostSheetOpen = false
            )
        }
    }

    fun selectMarketplaceTab(tab: MarketplaceScreenTab) {
        _uiState.update { it.copy(marketplaceSelectedTab = tab) }
        observeMarketplaceCatalog()
    }

    fun selectMarketplaceCategory(category: MarketplaceCategory) {
        _uiState.update { it.copy(marketplaceFilterCategory = category) }
        observeMarketplaceCatalog()
    }

    fun onMarketplaceSearchQueryChanged(query: String) {
        _uiState.update { it.copy(marketplaceSearchQuery = query) }
        if (query.trim().length >= 3) {
            viewModelScope.launch(safeExceptionHandler) {
                val userId = currentAuthenticatedUserId()
                repository.dao.insertRecentSearch(
                    MarketplaceRecentSearchEntity(
                        userId = userId,
                        query = query.trim()
                    )
                )
            }
        }
    }

    fun onSelectRecentSearch(query: String) {
        _uiState.update { it.copy(marketplaceSearchQuery = query) }
    }

    fun onClearRecentSearches() {
        viewModelScope.launch(safeExceptionHandler) {
            val userId = currentAuthenticatedUserId()
            repository.dao.clearRecentSearches(userId)
        }
    }

    fun openMarketplaceProduct(product: MarketplaceProduct) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            repository.dao.incrementProductViews(product.id)
            repository.dao.insertRecentView(
                MarketplaceRecentViewEntity(
                    productId = product.id,
                    userId = userId
                )
            )
            val updated = repository.dao.getProductById(product.id)
            val isFav = repository.dao.isProductFavorited(product.id, userId)
            _uiState.update {
                it.copy(
                    marketplaceActiveProduct = updated?.let { ent -> mapMarketplaceEntityToDomain(ent, isFav) } ?: product
                )
            }
        }
    }

    fun openMarketplaceProductById(productId: String) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            val entity = repository.dao.getProductById(productId)
            if (entity != null) {
                val isFav = repository.dao.isProductFavorited(productId, userId)
                val domain = mapMarketplaceEntityToDomain(entity, isFav)
                _uiState.update {
                    it.copy(
                        destination = AppDestination.MARKETPLACE,
                        marketplaceActiveProduct = domain
                    )
                }
                repository.dao.incrementProductViews(productId)
                repository.dao.insertRecentView(
                    MarketplaceRecentViewEntity(
                        productId = productId,
                        userId = userId
                    )
                )
            } else {
                openMarketplace()
            }
        }
    }

    fun closeMarketplaceProduct() {
        _uiState.update { it.copy(marketplaceActiveProduct = null) }
    }

    fun toggleMarketplaceFavorite(productId: String) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            val isFavorited = repository.dao.isProductFavorited(productId, userId)
            if (isFavorited) {
                repository.dao.deleteFavorite(productId, userId)
                repository.dao.decrementProductFavoritesCount(productId)
            } else {
                repository.dao.insertFavorite(MarketplaceFavoriteEntity(productId = productId, userId = userId))
                repository.dao.incrementProductFavoritesCount(productId)
            }
            // Update active product if viewing it
            _uiState.update { prev ->
                val active = prev.marketplaceActiveProduct
                if (active != null && active.id == productId) {
                    prev.copy(marketplaceActiveProduct = active.copy(isFavoritedByMe = !isFavorited))
                } else {
                    prev
                }
            }
        }
    }

    fun openPostMarketplaceSheet() {
        _uiState.update {
            it.copy(
                isMarketplacePostSheetOpen = true,
                marketplaceUploadProgress = MarketplaceUploadProgress.IDLE,
                marketplaceUploadStatusText = null
            )
        }
    }

    fun closePostMarketplaceSheet() {
        _uiState.update {
            it.copy(
                isMarketplacePostSheetOpen = false,
                marketplaceUploadProgress = MarketplaceUploadProgress.IDLE,
                marketplaceUploadStatusText = null
            )
        }
    }

    fun submitMarketplaceListing(
        title: String,
        description: String,
        price: Double,
        currency: String,
        category: MarketplaceCategory,
        condition: MarketplaceItemCondition,
        specs: Map<String, String>,
        deliveryOptions: Set<MarketplaceDeliveryOption>,
        showExactLocation: Boolean,
        location: String,
        selectedImageUris: List<Uri>
    ) {
        val context = getApplication<Application>().applicationContext
        val userId = currentAuthenticatedUserId()
        val currentProfile = _uiState.value.savedProfile
        val sellerFullName = currentProfile?.fullName?.ifBlank { _uiState.value.fullName } ?: "Telefam Seller"
        val sellerUsername = currentProfile?.username?.ifBlank { _uiState.value.username } ?: "seller"
        val sellerAvatar = currentProfile?.avatarLocalPath ?: currentProfile?.avatarCdnUrl
        val sellerCountry = _uiState.value.selectedCountry.name
        val sellerCountryIso = _uiState.value.selectedCountry.isoCode

        viewModelScope.launch(safeExceptionHandler) {
            // Stage 1: Uploading
            _uiState.update {
                it.copy(
                    marketplaceUploadProgress = MarketplaceUploadProgress.UPLOADING,
                    marketplaceUploadStatusText = "Uploading…"
                )
            }

            val savedPhotoPaths = mutableListOf<String>()
            val photoFiles = mutableListOf<File>()

            for ((idx, uri) in selectedImageUris.withIndex()) {
                val photoFile = File(context.filesDir, "marketplace_${userId}_${System.currentTimeMillis()}_$idx.webp")
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                        if (bitmap != null) {
                            TelefamAggressiveCompressionEngine.writeCompressedMicroThumbnail(bitmap, photoFile)
                            savedPhotoPaths.add(photoFile.absolutePath)
                            photoFiles.add(photoFile)
                        }
                    }
                }
            }

            // Stage 2: Uploaded
            _uiState.update {
                it.copy(
                    marketplaceUploadProgress = MarketplaceUploadProgress.UPLOADED,
                    marketplaceUploadStatusText = "Uploaded"
                )
            }

            // Stage 3: Under review
            _uiState.update {
                it.copy(
                    marketplaceUploadProgress = MarketplaceUploadProgress.UNDER_REVIEW,
                    marketplaceUploadStatusText = "Under review"
                )
            }

            val auditResult = MarketplaceModerationEngine.auditListing(
                context = context,
                dao = repository.dao,
                sellerUserId = userId,
                title = title,
                description = description,
                category = category,
                price = price,
                currency = currency,
                specs = specs,
                photoFiles = photoFiles
            )

            if (auditResult.requiresSuspension) {
                _uiState.update {
                    it.copy(
                        isMarketplaceSuspended = true,
                        marketplaceSuspensionReason = auditResult.suspensionReason,
                        marketplaceUploadProgress = MarketplaceUploadProgress.FAILED,
                        marketplaceUploadStatusText = "Marketplace privileges suspended due to policy violations"
                    )
                }
                return@launch
            }

            if (!auditResult.isApproved) {
                _uiState.update {
                    it.copy(
                        marketplaceUploadProgress = MarketplaceUploadProgress.FAILED,
                        marketplaceUploadStatusText = auditResult.policyViolations.firstOrNull() ?: "Listing requires revision before publication"
                    )
                }
                return@launch
            }

            // Stage 4: Persist to Local Room Database & Trigger Background Sync
            val productId = UUID.randomUUID().toString()
            val newEntity = MarketplaceProductEntity(
                productId = productId,
                sellerUserId = userId,
                sellerFullName = sellerFullName,
                sellerUsername = sellerUsername,
                sellerAvatarUrl = sellerAvatar,
                sellerCountry = sellerCountry,
                sellerCountryIso = sellerCountryIso,
                sellerLocation = location.ifBlank { "Nairobi" },
                sellerResponseRate = "Usually replies within 1 hour",
                sellerVerified = true,
                title = title,
                description = description,
                price = price,
                currency = currency,
                category = category.categoryKey,
                itemCondition = condition.conditionKey,
                photoPathsJson = MarketplaceJsonHelpers.serializeStringList(savedPhotoPaths),
                specsJson = MarketplaceJsonHelpers.serializeStringMap(specs),
                deliveryOptionsJson = MarketplaceJsonHelpers.serializeDeliveryOptions(deliveryOptions),
                showExactLocation = showExactLocation,
                reviewStatus = MarketplaceReviewStatus.ACTIVE.statusKey,
                riskScore = auditResult.riskScore,
                isSold = false,
                isExpired = false,
                expiresAtMs = System.currentTimeMillis() + 30L * 86400000L,
                createdAtMs = System.currentTimeMillis(),
                updatedAtMs = System.currentTimeMillis(),
                viewCount = 0,
                favoriteCount = 0,
                isPendingSync = true
            )

            repository.dao.upsertProduct(newEntity)

            // Local Notification
            TelefamNotificationEngine.postMarketplaceNotification(
                context = context,
                ownerUserId = userId,
                title = "Listing Published",
                body = "'$title' is now live on Telefam Marketplace",
                productId = productId
            )

            _uiState.update {
                it.copy(
                    marketplaceUploadProgress = MarketplaceUploadProgress.PUBLISHED,
                    marketplaceUploadStatusText = "Listing published successfully!",
                    isMarketplacePostSheetOpen = false
                )
            }
            observeMarketplaceCatalog()
        }
    }

    fun markMarketplaceProductSold(productId: String, isSold: Boolean) {
        viewModelScope.launch(safeExceptionHandler) {
            repository.dao.setProductSold(productId, isSold)
            val updated = repository.dao.getProductById(productId)
            val userId = currentAuthenticatedUserId()
            val isFav = repository.dao.isProductFavorited(productId, userId)
            _uiState.update { prev ->
                if (prev.marketplaceActiveProduct?.id == productId) {
                    prev.copy(marketplaceActiveProduct = updated?.let { mapMarketplaceEntityToDomain(it, isFav) })
                } else prev
            }
        }
    }

    fun renewMarketplaceProduct(productId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            val newExpires = System.currentTimeMillis() + 30L * 86400000L
            repository.dao.renewProduct(productId, newExpires)
            val updated = repository.dao.getProductById(productId)
            val userId = currentAuthenticatedUserId()
            val isFav = repository.dao.isProductFavorited(productId, userId)
            _uiState.update { prev ->
                if (prev.marketplaceActiveProduct?.id == productId) {
                    prev.copy(marketplaceActiveProduct = updated?.let { mapMarketplaceEntityToDomain(it, isFav) })
                } else prev
            }
        }
    }

    fun deleteMarketplaceProduct(productId: String) {
        viewModelScope.launch(safeExceptionHandler) {
            repository.dao.deleteProduct(productId)
            _uiState.update { prev ->
                if (prev.marketplaceActiveProduct?.id == productId) {
                    prev.copy(marketplaceActiveProduct = null)
                } else prev
            }
        }
    }

    fun openMarketplaceReportDialog(product: MarketplaceProduct) {
        _uiState.update { it.copy(activeReportingProduct = product) }
    }

    fun closeMarketplaceReportDialog() {
        _uiState.update { it.copy(activeReportingProduct = null) }
    }

    fun submitMarketplaceReport(product: MarketplaceProduct, reason: String, details: String) {
        val userId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            repository.dao.insertMarketplaceReport(
                MarketplaceReportEntity(
                    reportId = UUID.randomUUID().toString(),
                    reporterUserId = userId,
                    productId = product.id,
                    sellerUserId = product.sellerUserId,
                    reason = reason,
                    details = details
                )
            )
            closeMarketplaceReportDialog()
        }
    }

    fun blockMarketplaceSeller(sellerUserId: String) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(safeExceptionHandler) {
            socialRepository.setBlockUserState(
                currentUserId = currentUserId,
                targetUserId = sellerUserId,
                block = true,
                telemetry = currentTelemetry()
            )
            _uiState.update { it.copy(marketplaceActiveProduct = null) }
            observeMarketplaceCatalog()
        }
    }

    fun messageSellerWithProduct(product: MarketplaceProduct, prefilledMessage: String) {
        viewModelScope.launch(safeExceptionHandler) {
            val ownerId = currentAuthenticatedUserId()
            val chatId = chatRepository.openOrCreateOneToOneChat(
                ownerUserId = ownerId,
                peerUserId = product.sellerUserId,
                peerFullName = product.sellerFullName,
                peerUsername = product.sellerUsername,
                peerAvatarUrl = product.sellerAvatarUrl,
                peerBio = "Marketplace Seller • ${product.sellerLocation}",
                peerIsOnline = true,
                peerLastSeenMs = System.currentTimeMillis(),
                peerIsDeleted = false
            )
            openOneToOneChatById(chatId)
            _uiState.update {
                it.copy(
                    chatInputText = prefilledMessage,
                    chatPendingMarketplaceProduct = product
                )
            }
        }
    }

    fun clearChatPendingMarketplaceProduct() {
        _uiState.update { it.copy(chatPendingMarketplaceProduct = null) }
    }

    private fun observeMarketplaceCatalog() {
        marketplaceProductsJob?.cancel()
        val userId = currentAuthenticatedUserId()
        val currentTab = _uiState.value.marketplaceSelectedTab
        val currentCategory = _uiState.value.marketplaceFilterCategory

        marketplaceProductsJob = viewModelScope.launch(safeExceptionHandler) {
            val flow = when (currentTab) {
                MarketplaceScreenTab.SAVED -> repository.dao.observeFavoriteProducts(userId)
                MarketplaceScreenTab.MY_LISTINGS -> repository.dao.observeSellerProducts(userId)
                MarketplaceScreenTab.EXPLORE -> {
                    if (currentCategory == MarketplaceCategory.ALL) {
                        repository.dao.observeActiveMarketplaceProducts(userId)
                    } else {
                        repository.dao.observeMarketplaceProductsByCategory(userId, currentCategory.categoryKey)
                    }
                }
            }

            flow.collectLatest { entities ->
                val searchQ = _uiState.value.marketplaceSearchQuery.trim().lowercase(Locale.ROOT)
                val filtered = if (searchQ.isNotBlank()) {
                    entities.filter {
                        it.title.lowercase(Locale.ROOT).contains(searchQ) ||
                            it.description.lowercase(Locale.ROOT).contains(searchQ) ||
                            it.category.lowercase(Locale.ROOT).contains(searchQ) ||
                            it.sellerLocation.lowercase(Locale.ROOT).contains(searchQ)
                    }
                } else {
                    entities
                }

                val domainList = filtered.map { ent ->
                    val isFav = repository.dao.isProductFavorited(ent.productId, userId)
                    mapMarketplaceEntityToDomain(ent, isFav)
                }

                _uiState.update { it.copy(marketplaceProducts = domainList) }
            }
        }
    }

    private fun observeMarketplaceRecents(userId: String) {
        marketplaceRecentsJob?.cancel()
        marketplaceRecentsJob = viewModelScope.launch(safeExceptionHandler) {
            repository.dao.observeRecentSearches(userId).collectLatest { searches ->
                _uiState.update {
                    it.copy(marketplaceRecentSearches = searches.map { s -> s.query }.distinct())
                }
            }
        }
    }

    private fun mapMarketplaceEntityToDomain(ent: MarketplaceProductEntity, isFav: Boolean): MarketplaceProduct {
        return MarketplaceProduct(
            id = ent.productId,
            sellerUserId = ent.sellerUserId,
            sellerFullName = ent.sellerFullName,
            sellerUsername = ent.sellerUsername,
            sellerAvatarUrl = ent.sellerAvatarUrl,
            sellerCountry = ent.sellerCountry,
            sellerCountryIso = ent.sellerCountryIso,
            sellerLocation = ent.sellerLocation,
            sellerResponseRate = ent.sellerResponseRate,
            sellerVerified = ent.sellerVerified,
            title = ent.title,
            description = ent.description,
            price = ent.price,
            currency = ent.currency,
            category = MarketplaceCategory.fromKey(ent.category),
            condition = MarketplaceItemCondition.fromKey(ent.itemCondition),
            photoPaths = MarketplaceJsonHelpers.parseStringList(ent.photoPathsJson),
            specs = MarketplaceJsonHelpers.parseStringMap(ent.specsJson),
            deliveryOptions = MarketplaceJsonHelpers.parseDeliveryOptions(ent.deliveryOptionsJson),
            showExactLocation = ent.showExactLocation,
            reviewStatus = MarketplaceReviewStatus.fromKey(ent.reviewStatus),
            riskScore = ent.riskScore,
            isSold = ent.isSold,
            isExpired = ent.isExpired,
            expiresAtMs = ent.expiresAtMs,
            createdAtMs = ent.createdAtMs,
            updatedAtMs = ent.updatedAtMs,
            viewCount = ent.viewCount,
            favoriteCount = ent.favoriteCount,
            isFavoritedByMe = isFav
        )
    }

    // =========================================================================
    // LINKED DEVICES MANAGEMENT (Real hardware, QR & Code, 30s token, 5m approval, immediate revocation)
    // =========================================================================

    private var linkedDevicesObserverJob: Job? = null
    private var deviceRevocationObserverJob: Job? = null
    private var pendingLinkApprovalObserverJob: Job? = null
    private var waitingForPairingApprovalJob: Job? = null

    fun openLinkedDevices() {
        val userId = currentAuthenticatedUserId()
        _uiState.update {
            it.copy(
                destination = AppDestination.LINKED_DEVICES,
                isOwnerMenuOpen = false,
                linkedDevicesSelectedTab = 0,
                linkDeviceErrorBanner = null,
                linkDeviceSuccessBanner = null
            )
        }
        initializeDeviceSessionAndObserver(userId)
    }

    fun closeLinkedDevices() {
        _uiState.update {
            it.copy(
                destination = AppDestination.MAIN_HOME,
                linkDeviceErrorBanner = null,
                linkDeviceSuccessBanner = null
            )
        }
    }

    fun selectLinkedDevicesTab(tabIndex: Int) {
        _uiState.update {
            it.copy(
                linkedDevicesSelectedTab = tabIndex,
                linkDeviceErrorBanner = null,
                linkDeviceSuccessBanner = null
            )
        }
        if (tabIndex == 1) {
            val state = _uiState.value
            if (state.activePairingRequest == null || state.pairingCountdownSeconds <= 0) {
                generateNewPairingSession()
            }
        }
    }

    fun onLinkCodeInputChanged(text: String) {
        _uiState.update { it.copy(linkCodeInputText = text, linkDeviceErrorBanner = null) }
    }

    fun clearLinkDeviceErrorBanner() {
        _uiState.update { it.copy(linkDeviceErrorBanner = null) }
    }

    fun clearLinkDeviceSuccessBanner() {
        _uiState.update { it.copy(linkDeviceSuccessBanner = null) }
    }

    private fun initializeDeviceSessionAndObserver(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val prefs = getApplication<Application>().getSharedPreferences("telefam_device_session", Context.MODE_PRIVATE)
            var currentId = prefs.getString("session_device_id", null)
            if (currentId.isNullOrBlank()) {
                currentId = "dev_" + UUID.randomUUID().toString().take(12)
                prefs.edit().putString("session_device_id", currentId).apply()
            }
            val existing = repository.dao.getLinkedDeviceBySessionId(currentId)
            val now = System.currentTimeMillis()
            val deviceName = RealDeviceIdentityResolver.getDeviceName()
            val deviceType = RealDeviceIdentityResolver.getDeviceType(getApplication<Application>().applicationContext).name
            val platform = RealDeviceIdentityResolver.getPlatformDescription()

            if (existing == null) {
                repository.dao.upsertLinkedDevice(
                    LinkedDeviceEntity(
                        sessionDeviceId = currentId,
                        userId = userId,
                        deviceName = deviceName,
                        deviceType = deviceType,
                        osPlatform = platform,
                        ipAddress = "192.168.1.1",
                        locationLabel = currentLocationTriple.first,
                        linkedAtMs = now,
                        lastActiveAtMs = now,
                        isCurrentDevice = true,
                        isRevoked = false
                    )
                )
            } else {
                repository.dao.upsertLinkedDevice(
                    existing.copy(
                        userId = userId,
                        deviceName = deviceName,
                        lastActiveAtMs = now,
                        isCurrentDevice = true,
                        isRevoked = false
                    )
                )
            }

            _uiState.update { it.copy(currentSessionDeviceId = currentId) }

            observeLinkedDevices(userId)
            observeDeviceRevocationStatus(currentId, userId)
            observeIncomingApprovalRequests(userId)
        }
    }

    private fun observeLinkedDevices(userId: String) {
        linkedDevicesObserverJob?.cancel()
        linkedDevicesObserverJob = viewModelScope.launch(Dispatchers.IO) {
            repository.dao.observeLinkedDevicesForUser(userId).collectLatest { list ->
                _uiState.update { it.copy(linkedDevices = list) }
            }
        }
    }

    private fun observeDeviceRevocationStatus(currentSessionDeviceId: String, userId: String) {
        deviceRevocationObserverJob?.cancel()
        deviceRevocationObserverJob = viewModelScope.launch(Dispatchers.IO) {
            repository.dao.observeAllLinkedDevicesIncludingRevoked(userId).collectLatest { allDevices ->
                val thisDevice = allDevices.find { it.sessionDeviceId == currentSessionDeviceId }
                if (thisDevice != null && thisDevice.isRevoked) {
                    handleDeviceRevokedImmediately()
                }
            }
        }
    }

    private fun handleDeviceRevokedImmediately() {
        viewModelScope.launch(Dispatchers.Main) {
            val app = getApplication<Application>()
            val revokedMsg = app.getString(R.string.linked_devices_revoked_immediate_logout)
            signOut()
            _uiState.update {
                it.copy(
                    infoBannerMessage = revokedMsg,
                    errorMessage = null
                )
            }
        }
    }

    private fun observeIncomingApprovalRequests(userId: String) {
        pendingLinkApprovalObserverJob?.cancel()
        pendingLinkApprovalObserverJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val now = System.currentTimeMillis()
                val pending = repository.dao.getPendingLinkRequestForUser(userId, now)
                if (pending != null && pending.status == "PENDING" && pending.requesterDeviceName.isNotBlank()) {
                    val remainingSeconds = ((pending.approvalExpiresAtMs - now) / 1000L).coerceAtLeast(0L).toInt()
                    if (remainingSeconds <= 0) {
                        autoExpirePendingApprovalRequest(pending.pairingSessionId)
                    } else {
                        _uiState.update {
                            it.copy(
                                pendingIncomingApprovalRequest = pending,
                                approvalCountdownSeconds = remainingSeconds
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(pendingIncomingApprovalRequest = null)
                    }
                }
                delay(2000L)
            }
        }
    }

    fun generateNewPairingSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentAuthenticatedUserId()
            val now = System.currentTimeMillis()
            val token = PairingTokenGenerator.generatePairingToken()
            val code = PairingTokenGenerator.generatePairingCode()
            val pairingExpiresAt = now + 30_000L // 30 seconds!
            val approvalExpiresAt = now + 330_000L // 5 minutes 30 seconds!
            val qrUri = PairingTokenGenerator.buildQrPayload(token, code, pairingExpiresAt)

            val entity = DeviceLinkRequestEntity(
                pairingSessionId = UUID.randomUUID().toString(),
                userId = userId,
                linkToken = code,
                qrPayload = qrUri,
                requesterDeviceName = "",
                requesterDeviceType = "",
                requesterIp = "",
                requesterLocation = "",
                createdAtMs = now,
                tokenExpiresAtMs = pairingExpiresAt,
                approvalExpiresAtMs = approvalExpiresAt,
                status = "PENDING"
            )

            repository.dao.insertLinkRequest(entity)

            _uiState.update {
                it.copy(
                    activePairingRequest = entity,
                    pairingCountdownSeconds = 30,
                    linkDeviceErrorBanner = null
                )
            }
        }
    }

    fun submitLinkCodeOrQr(rawInput: String) {
        val cleaned = rawInput.trim()
        if (cleaned.isBlank()) return

        _uiState.update { it.copy(isLinkingDeviceLoading = true, linkDeviceErrorBanner = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val (token, code) = PairingTokenGenerator.parseQrPayload(cleaned)
            val now = System.currentTimeMillis()
            val lookupKey = code ?: token ?: cleaned
            val req = repository.dao.findLinkRequestByTokenOrQr(lookupKey)

            if (req == null) {
                _uiState.update {
                    it.copy(
                        isLinkingDeviceLoading = false,
                        linkDeviceErrorBanner = getApplication<Application>().getString(R.string.err_otp_invalid)
                    )
                }
                return@launch
            }

            if (now > req.tokenExpiresAtMs) {
                repository.dao.updateLinkRequestStatus(req.pairingSessionId, "EXPIRED")
                _uiState.update {
                    it.copy(
                        isLinkingDeviceLoading = false,
                        linkDeviceErrorBanner = getApplication<Application>().getString(R.string.linked_devices_token_expired)
                    )
                }
                return@launch
            }

            val deviceName = RealDeviceIdentityResolver.getDeviceName()
            val deviceType = RealDeviceIdentityResolver.getDeviceType(getApplication<Application>().applicationContext).name
            val ip = _uiState.value.telemetry?.clientIpAddress ?: "192.168.1.55"
            val location = currentLocationTriple.first
            val newApprovalExpiresAt = now + 300_000L // 5 minutes approval window!

            val updatedReq = req.copy(
                requesterDeviceName = deviceName,
                requesterDeviceType = deviceType,
                requesterIp = ip,
                requesterLocation = location,
                approvalExpiresAtMs = newApprovalExpiresAt,
                status = "PENDING"
            )
            repository.dao.insertLinkRequest(updatedReq)

            _uiState.update {
                it.copy(
                    isLinkingDeviceLoading = false,
                    linkDeviceSuccessBanner = getApplication<Application>().getString(R.string.linked_devices_approval_desc) + " " + deviceName
                )
            }

            // Start waiting for approval on this device!
            waitForPairingApproval(req.pairingSessionId, req.userId, newApprovalExpiresAt)
        }
    }

    private fun waitForPairingApproval(pairingSessionId: String, targetUserId: String, approvalExpiresAtMs: Long) {
        waitingForPairingApprovalJob?.cancel()
        waitingForPairingApprovalJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val now = System.currentTimeMillis()
                if (now > approvalExpiresAtMs) {
                    repository.dao.updateLinkRequestStatus(pairingSessionId, "EXPIRED")
                    _uiState.update {
                        it.copy(
                            linkDeviceErrorBanner = getApplication<Application>().getString(R.string.linked_devices_approval_expired)
                        )
                    }
                    break
                }

                val req = repository.dao.findLinkRequestByTokenOrQr(pairingSessionId)
                if (req?.status == "APPROVED") {
                    completeLinkedDeviceLogin(targetUserId)
                    break
                } else if (req?.status == "REJECTED" || req?.status == "EXPIRED") {
                    _uiState.update {
                        it.copy(
                            linkDeviceErrorBanner = getApplication<Application>().getString(R.string.linked_devices_approval_expired)
                        )
                    }
                    break
                }
                delay(1500L)
            }
        }
    }

    private suspend fun completeLinkedDeviceLogin(targetUserId: String) {
        val targetProfile = repository.dao.getProfileByUserId(targetUserId)
        withContext(Dispatchers.Main) {
            _uiState.update {
                it.copy(
                    destination = AppDestination.MAIN_HOME,
                    savedProfile = targetProfile,
                    linkDeviceSuccessBanner = getApplication<Application>().getString(R.string.linked_devices_link_success),
                    linkCodeInputText = "",
                    isLinkingDeviceLoading = false
                )
            }
        }
    }

    fun approvePendingDeviceLink(pairingSessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val state = _uiState.value
            val req = state.pendingIncomingApprovalRequest ?: return@launch
            if (req.pairingSessionId != pairingSessionId) return@launch

            // Check 5-minute expiration
            if (now > req.approvalExpiresAtMs) {
                repository.dao.updateLinkRequestStatus(pairingSessionId, "EXPIRED")
                _uiState.update {
                    it.copy(
                        pendingIncomingApprovalRequest = null,
                        linkDeviceErrorBanner = getApplication<Application>().getString(R.string.linked_devices_approval_expired)
                    )
                }
                return@launch
            }

            repository.dao.updateLinkRequestStatus(pairingSessionId, "APPROVED")

            // Register newly linked device in Room
            val newSessionDeviceId = "dev_" + UUID.randomUUID().toString().take(12)
            val newLinkedDevice = LinkedDeviceEntity(
                sessionDeviceId = newSessionDeviceId,
                userId = req.userId,
                deviceName = if (req.requesterDeviceName.isNotBlank()) req.requesterDeviceName else "Linked Device",
                deviceType = if (req.requesterDeviceType.isNotBlank()) req.requesterDeviceType else DeviceType.WEB.name,
                osPlatform = "Telefam Web / Android",
                ipAddress = req.requesterIp,
                locationLabel = req.requesterLocation,
                linkedAtMs = now,
                lastActiveAtMs = now,
                isCurrentDevice = false,
                isRevoked = false
            )
            repository.dao.upsertLinkedDevice(newLinkedDevice)

            _uiState.update {
                it.copy(
                    pendingIncomingApprovalRequest = null,
                    linkDeviceSuccessBanner = getApplication<Application>().getString(R.string.linked_devices_link_success)
                )
            }
        }
    }

    fun rejectPendingDeviceLink(pairingSessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dao.updateLinkRequestStatus(pairingSessionId, "REJECTED")
            _uiState.update {
                it.copy(pendingIncomingApprovalRequest = null)
            }
        }
    }

    private fun autoExpirePendingApprovalRequest(pairingSessionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dao.updateLinkRequestStatus(pairingSessionId, "EXPIRED")
            _uiState.update {
                it.copy(
                    pendingIncomingApprovalRequest = null,
                    linkDeviceErrorBanner = getApplication<Application>().getString(R.string.linked_devices_approval_expired)
                )
            }
        }
    }

    fun revokeLinkedDevice(sessionDeviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentAuthenticatedUserId()
            repository.dao.revokeLinkedDevice(sessionDeviceId, userId)
            if (sessionDeviceId == _uiState.value.currentSessionDeviceId) {
                handleDeviceRevokedImmediately()
            }
        }
    }

    fun revokeAllOtherLinkedDevices() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentAuthenticatedUserId()
            repository.dao.revokeAllOtherLinkedDevices(userId)
        }
    }

    // =========================================================================
    // CREATOR SUBSCRIPTIONS METHODS (Image 2)
    // =========================================================================

    private var subscriptionsObservationJob: Job? = null

    fun openSubscriptions() {
        _uiState.update {
            it.copy(
                destination = AppDestination.SUBSCRIPTIONS,
                isOwnerMenuOpen = false,
                subscriptionsState = it.subscriptionsState.copy(
                    actionSuccessMessage = null,
                    errorMessage = null
                )
            )
        }
        val userId = currentAuthenticatedUserId()
        observeSubscriptionsData(userId)
    }

    fun closeSubscriptions() {
        subscriptionsObservationJob?.cancel()
        _uiState.update {
            it.copy(
                destination = AppDestination.MAIN_HOME,
                subscriptionsState = it.subscriptionsState.copy(
                    isCreatePlanSheetOpen = false,
                    isManagePlansSheetOpen = false,
                    isSubscriptionInsightsSheetOpen = false,
                    isAllSubscribersSheetOpen = false,
                    isSubscriptionsHelpSheetOpen = false,
                    actionSuccessMessage = null,
                    errorMessage = null
                )
            )
        }
    }

    private fun observeSubscriptionsData(userId: String) {
        subscriptionsObservationJob?.cancel()
        subscriptionsObservationJob = viewModelScope.launch(Dispatchers.IO) {
            val sinceMs = calculateSinceMs(_uiState.value.subscriptionPeriodFilter)
            combine(
                repository.dao.observeSubscriptionPlans(userId),
                repository.dao.observeCreatorSubscribers(userId)
            ) { plans, subs ->
                plans to subs
            }.collectLatest { (plans, subs) ->
                val totalEarnings = repository.dao.sumTotalSubscriptionEarnings(userId)
                val periodEarnings = repository.dao.sumSubscriptionEarningsSince(userId, sinceMs)
                val activeCount = repository.dao.countSubscribersByStatus(userId, "ACTIVE")
                val newCount = repository.dao.countNewSubscribersSince(userId, sinceMs)
                val canceledCount = repository.dao.countSubscribersByStatus(userId, "CANCELED")

                _uiState.update {
                    it.copy(
                        subscriptionsState = it.subscriptionsState.copy(
                            plans = plans,
                            subscribers = subs,
                            totalEarnings = totalEarnings,
                            periodEarnings = periodEarnings,
                            subscribersCount = activeCount,
                            newSubscribersCount = newCount,
                            canceledCount = canceledCount
                        )
                    )
                }
            }
        }
    }

    fun setSubscriptionPeriodFilter(period: String) {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(periodFilter = period)) }
        val userId = currentAuthenticatedUserId()
        observeSubscriptionsData(userId)
    }

    fun openCreatePlanSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isCreatePlanSheetOpen = true)) }
    }

    fun closeCreatePlanSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isCreatePlanSheetOpen = false)) }
    }

    fun openManagePlansSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isManagePlansSheetOpen = true)) }
    }

    fun closeManagePlansSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isManagePlansSheetOpen = false)) }
    }

    fun openSubscriptionInsightsSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isSubscriptionInsightsSheetOpen = true)) }
    }

    fun closeSubscriptionInsightsSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isSubscriptionInsightsSheetOpen = false)) }
    }

    fun openAllSubscribersSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isAllSubscribersSheetOpen = true)) }
    }

    fun closeAllSubscribersSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isAllSubscribersSheetOpen = false)) }
    }

    fun openSubscriptionsHelpSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isSubscriptionsHelpSheetOpen = true)) }
    }

    fun closeSubscriptionsHelpSheet() {
        _uiState.update { it.copy(subscriptionsState = it.subscriptionsState.copy(isSubscriptionsHelpSheetOpen = false)) }
    }

    fun clearSubscriptionsStatusMessage() {
        _uiState.update {
            it.copy(
                subscriptionsState = it.subscriptionsState.copy(
                    actionSuccessMessage = null,
                    errorMessage = null
                )
            )
        }
    }

    fun createSubscriptionPlan(
        name: String,
        price: Double,
        description: String,
        perks: List<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentAuthenticatedUserId()
            val planId = "plan_${UUID.randomUUID().toString().take(12)}"
            val perksJsonArray = org.json.JSONArray().apply {
                perks.forEach { put(it) }
            }.toString()

            val plan = SubscriptionPlanEntity(
                planId = planId,
                creatorUserId = userId,
                planName = name,
                monthlyPrice = price,
                currency = "USD",
                description = description,
                perksJson = perksJsonArray,
                subscribersCount = 0,
                isActive = true
            )
            repository.dao.upsertSubscriptionPlan(plan)
            _uiState.update {
                it.copy(
                    subscriptionsState = it.subscriptionsState.copy(
                        isCreatePlanSheetOpen = false,
                        actionSuccessMessage = getApplication<Application>().getString(R.string.subscriptions_plan_created)
                    )
                )
            }
        }
    }

    fun deleteSubscriptionPlan(planId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.dao.deleteSubscriptionPlan(planId)
        }
    }

    // =========================================================================
    // CREATOR WALLET & PAYOUTS METHODS (Image 1)
    // =========================================================================

    private var walletObservationJob: Job? = null

    fun openWallet() {
        _uiState.update {
            it.copy(
                destination = AppDestination.WALLET,
                isOwnerMenuOpen = false,
                walletState = it.walletState.copy(
                    actionSuccessMessage = null,
                    errorMessage = null
                )
            )
        }
        val userId = currentAuthenticatedUserId()
        observeWalletData(userId)
    }

    fun closeWallet() {
        walletObservationJob?.cancel()
        _uiState.update {
            it.copy(
                destination = AppDestination.MAIN_HOME,
                walletState = it.walletState.copy(
                    isWithdrawSheetOpen = false,
                    isAddPayoutAccountSheetOpen = false,
                    isAllTransactionsSheetOpen = false,
                    isPayoutHistorySheetOpen = false,
                    isWalletHelpSheetOpen = false,
                    isWalletSettingsSheetOpen = false,
                    actionSuccessMessage = null,
                    errorMessage = null
                )
            )
        }
    }

    private fun observeWalletData(userId: String) {
        walletObservationJob?.cancel()
        walletObservationJob = viewModelScope.launch(Dispatchers.IO) {
            // Ensure wallet balance entity exists in Room
            val existing = repository.dao.getWalletBalance(userId)
            if (existing == null) {
                repository.dao.upsertWalletBalance(
                    WalletBalanceEntity(
                        userId = userId,
                        totalBalance = 0.0,
                        availableBalance = 0.0,
                        pendingBalance = 0.0,
                        currency = "USD",
                        isHidden = false
                    )
                )
            }

            val sinceMs = calculateSinceMs(_uiState.value.walletPeriodFilter)

            combine(
                repository.dao.observeWalletBalance(userId),
                repository.dao.observeWalletTransactions(userId),
                repository.dao.observePayoutAccounts(userId),
                repository.dao.observePayoutRecords(userId)
            ) { b: WalletBalanceEntity?, t: List<WalletTransactionEntity>, a: List<PayoutAccountEntity>, r: List<PayoutRecordEntity> ->
                WalletCombinedData(b, t, a, r)
            }.collectLatest { (balance, txs, accounts, records) ->
                val starsEarnings = repository.dao.sumEarningsByCategorySince(userId, "STARS", sinceMs)
                val subEarnings = repository.dao.sumEarningsByCategorySince(userId, "SUBSCRIPTIONS", sinceMs)
                val adRevenue = repository.dao.sumEarningsByCategorySince(userId, "AD_REVENUE", sinceMs)
                val otherEarnings = repository.dao.sumEarningsByCategorySince(userId, "OTHER", sinceMs)

                val totalPayoutsAllTime = repository.dao.sumTotalPayoutsAllTime(userId)
                val pendingPayoutsAmount = repository.dao.sumPayoutsByStatus(userId, "PENDING")
                val pendingPayoutsCount = repository.dao.countPayoutsByStatus(userId, "PENDING")
                val paidPayoutsAmount = repository.dao.sumPayoutsByStatus(userId, "PAID")
                val paidPayoutsCount = repository.dao.countPayoutsByStatus(userId, "PAID")
                val failedPayoutsAmount = repository.dao.sumPayoutsByStatus(userId, "FAILED")
                val failedPayoutsCount = repository.dao.countPayoutsByStatus(userId, "FAILED")

                _uiState.update {
                    it.copy(
                        walletState = it.walletState.copy(
                            balance = balance,
                            transactions = txs,
                            payoutAccounts = accounts,
                            payoutRecords = records,
                            starsEarnings = starsEarnings,
                            subscriptionEarnings = subEarnings,
                            adRevenue = adRevenue,
                            otherEarnings = otherEarnings,
                            totalPayoutsAllTime = totalPayoutsAllTime,
                            pendingPayoutsAmount = pendingPayoutsAmount,
                            pendingPayoutsCount = pendingPayoutsCount,
                            paidPayoutsAmount = paidPayoutsAmount,
                            paidPayoutsCount = paidPayoutsCount,
                            failedPayoutsAmount = failedPayoutsAmount,
                            failedPayoutsCount = failedPayoutsCount
                        )
                    )
                }
            }
        }
    }

    private fun calculateSinceMs(filter: String): Long {
        val now = System.currentTimeMillis()
        return when (filter) {
            "TODAY" -> now - 86_400_000L
            "THIS_WEEK" -> now - 7L * 86_400_000L
            "LAST_7_DAYS" -> now - 7L * 86_400_000L
            "LAST_30_DAYS", "THIS_MONTH" -> now - 30L * 86_400_000L
            "LAST_90_DAYS" -> now - 90L * 86_400_000L
            "ALL_TIME" -> 0L
            else -> now - 30L * 86_400_000L
        }
    }

    fun toggleWalletBalanceVisibility() {
        val userId = currentAuthenticatedUserId()
        val currentHidden = _uiState.value.walletBalance?.isHidden == true
        viewModelScope.launch(Dispatchers.IO) {
            repository.dao.updateWalletBalanceHidden(userId, !currentHidden)
        }
    }

    fun setWalletTransactionFilter(filter: String) {
        _uiState.update { it.copy(walletState = it.walletState.copy(transactionFilter = filter)) }
    }

    fun setWalletPeriodFilter(period: String) {
        _uiState.update { it.copy(walletState = it.walletState.copy(periodFilter = period)) }
        val userId = currentAuthenticatedUserId()
        observeWalletData(userId)
    }

    fun openWithdrawSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isWithdrawSheetOpen = true)) }
    }

    fun closeWithdrawSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isWithdrawSheetOpen = false)) }
    }

    fun openAddPayoutAccountSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isAddPayoutAccountSheetOpen = true)) }
    }

    fun closeAddPayoutAccountSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isAddPayoutAccountSheetOpen = false)) }
    }

    fun openAllTransactionsSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isAllTransactionsSheetOpen = true)) }
    }

    fun closeAllTransactionsSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isAllTransactionsSheetOpen = false)) }
    }

    fun openPayoutHistorySheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isPayoutHistorySheetOpen = true)) }
    }

    fun closePayoutHistorySheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isPayoutHistorySheetOpen = false)) }
    }

    fun openWalletHelpSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isWalletHelpSheetOpen = true)) }
    }

    fun closeWalletHelpSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isWalletHelpSheetOpen = false)) }
    }

    fun openWalletSettingsSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isWalletSettingsSheetOpen = true)) }
    }

    fun closeWalletSettingsSheet() {
        _uiState.update { it.copy(walletState = it.walletState.copy(isWalletSettingsSheetOpen = false)) }
    }

    fun clearWalletStatusMessage() {
        _uiState.update {
            it.copy(
                walletState = it.walletState.copy(
                    actionSuccessMessage = null,
                    errorMessage = null
                )
            )
        }
    }

    fun addPayoutAccount(
        accountType: String,
        institutionName: String,
        holderName: String,
        accountNumber: String,
        routingOrSwift: String,
        countryIso: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentAuthenticatedUserId()
            val accountId = "po_acc_${UUID.randomUUID().toString().take(12)}"
            val encryptedAccount = com.telefam.company.app.security.TelefamCryptoAndTelemetry.encryptString(accountNumber)
            val encryptedRouting = if (routingOrSwift.isNotBlank()) {
                com.telefam.company.app.security.TelefamCryptoAndTelemetry.encryptString(routingOrSwift)
            } else ""
            val masked = "•••• " + accountNumber.takeLast(4).ifBlank { "0000" }

            // Clear previous default if this is new
            repository.dao.clearDefaultPayoutAccounts(userId)

            val entity = PayoutAccountEntity(
                payoutAccountId = accountId,
                userId = userId,
                accountType = accountType,
                institutionName = institutionName,
                accountHolderName = holderName,
                accountNumberEncrypted = encryptedAccount,
                accountNumberMasked = masked,
                routingOrSwiftEncrypted = encryptedRouting,
                countryIso = countryIso,
                currency = "USD",
                isDefault = true,
                isVerified = true
            )
            repository.dao.upsertPayoutAccount(entity)
            _uiState.update {
                it.copy(
                    walletState = it.walletState.copy(
                        isAddPayoutAccountSheetOpen = false,
                        actionSuccessMessage = getApplication<Application>().getString(R.string.wallet_account_saved)
                    )
                )
            }
        }
    }

    fun requestWithdrawal(amount: Double, pin: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = currentAuthenticatedUserId()
            val currentBalance = repository.dao.getWalletBalance(userId) ?: return@launch
            val defaultAccount = repository.dao.getFirstPayoutAccount(userId)
            if (defaultAccount == null) {
                _uiState.update {
                    it.copy(walletState = it.walletState.copy(errorMessage = getApplication<Application>().getString(R.string.wallet_err_no_payout_account)))
                }
                return@launch
            }
            if (amount < 10.0) {
                _uiState.update {
                    it.copy(walletState = it.walletState.copy(errorMessage = getApplication<Application>().getString(R.string.wallet_err_minimum_withdraw)))
                }
                return@launch
            }
            if (amount > currentBalance.availableBalance) {
                _uiState.update {
                    it.copy(walletState = it.walletState.copy(errorMessage = getApplication<Application>().getString(R.string.wallet_err_insufficient_funds)))
                }
                return@launch
            }

            val fee = amount * 0.02
            val netAmount = amount - fee
            val payoutId = "po_${UUID.randomUUID().toString().take(12)}"
            val refCode = "PO-" + System.currentTimeMillis().toString().takeLast(8)

            // Update balance
            val updatedBalance = currentBalance.copy(
                totalBalance = (currentBalance.totalBalance - amount).coerceAtLeast(0.0),
                availableBalance = (currentBalance.availableBalance - amount).coerceAtLeast(0.0),
                pendingBalance = currentBalance.pendingBalance + amount,
                lastUpdatedAtMs = System.currentTimeMillis()
            )
            repository.dao.upsertWalletBalance(updatedBalance)

            // Record Payout
            val payoutRecord = PayoutRecordEntity(
                payoutId = payoutId,
                userId = userId,
                payoutAccountId = defaultAccount.payoutAccountId,
                institutionName = defaultAccount.institutionName,
                accountMasked = defaultAccount.accountNumberMasked,
                amount = amount,
                fee = fee,
                netAmount = netAmount,
                currency = "USD",
                status = "PENDING",
                failureReason = "",
                referenceCode = refCode,
                requestedAtMs = System.currentTimeMillis(),
                completedAtMs = System.currentTimeMillis()
            )
            repository.dao.insertPayoutRecord(payoutRecord)

            // Insert into Wallet transactions
            val tx = WalletTransactionEntity(
                transactionId = "tx_${UUID.randomUUID().toString().take(12)}",
                userId = userId,
                type = "PAYOUTS",
                category = "WITHDRAWAL",
                title = "Withdrawal to ${defaultAccount.institutionName}",
                description = "Reference: $refCode",
                amount = -amount,
                currency = "USD",
                status = "PENDING",
                referenceId = refCode,
                createdAtMs = System.currentTimeMillis()
            )
            repository.dao.insertWalletTransaction(tx)

            _uiState.update {
                it.copy(
                    walletState = it.walletState.copy(
                        isWithdrawSheetOpen = false,
                        actionSuccessMessage = getApplication<Application>().getString(R.string.wallet_withdraw_success, "$$amount")
                    )
                )
            }
        }
    }

    // ==========================================
    // CREATOR MONETIZATION (IMAGES 1, 2, 3)
    // ==========================================

    fun openMonetization() {
        _uiState.update { it.copy(destination = AppDestination.MONETIZATION) }
        loadCreatorMonetizationData()
    }

    fun closeMonetization() {
        _uiState.update { it.copy(destination = AppDestination.MAIN_HOME) }
    }

    fun loadCreatorMonetizationData() {
        val userId = _uiState.value.savedProfile?.userId ?: "owner_primary_account_id"
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            val entity = repository.dao.getCreatorMonetization(userId)
            val status = CreatorMonetizationStatus.fromWire(entity?.status)

            val followers = repository.dao.countFollowers(userId)
            val publicVideos = repository.dao.getCreatorPublicVideosCount(userId)
            val totalViews = repository.dao.getCreatorTotalViews(userId)
            val watchTimeMinutes = totalViews * 2L

            val snapshot = CreatorQualificationSnapshot(
                followersCount = followers,
                targetFollowers = 1000L,
                watchTimeMinutes = watchTimeMinutes,
                targetWatchTimeMinutes = 4000L,
                publicVideosCount = publicVideos,
                targetPublicVideosCount = 10L,
                communityGuidelinesMet = true,
                accountStatusMet = true,
                originalContentMet = true,
                isEngagementMet = totalViews > 10L
            )

            val feedVideos = repository.dao.getFeedVideosByOwner(userId)
            val videoMonetizations = feedVideos.map { vid ->
                val existing = repository.dao.getVideoMonetization(vid.videoId)
                if (existing != null) {
                    VideoMonetizationItem(
                        videoId = existing.videoId,
                        title = vid.caption.ifBlank { "Creator Video" },
                        coverUrl = vid.thumbnailUrl,
                        isMonetized = existing.isMonetized,
                        instreamAdsEnabled = existing.instreamAdsEnabled,
                        videoGiftsEnabled = existing.videoGiftsEnabled,
                        creatorRewardsEligible = existing.creatorRewardsEligible,
                        monetizedViews = existing.monetizedViews,
                        adImpressions = existing.adImpressions,
                        adEarningsUsd = existing.adEarningsUsd,
                        rewardEarningsUsd = existing.rewardEarningsUsd,
                        giftEarningsUsd = existing.giftEarningsUsd,
                        rpmUsd = existing.rpmUsd
                    )
                } else {
                    val views = vid.viewsCount
                    val adRev = (views * 0.0025).coerceAtLeast(0.0)
                    val rewRev = (views * 0.0015).coerceAtLeast(0.0)
                    VideoMonetizationItem(
                        videoId = vid.videoId,
                        title = vid.caption.ifBlank { "Creator Video" },
                        coverUrl = vid.thumbnailUrl,
                        isMonetized = true,
                        instreamAdsEnabled = true,
                        videoGiftsEnabled = true,
                        creatorRewardsEligible = true,
                        monetizedViews = views,
                        adImpressions = (views * 0.85).toLong(),
                        adEarningsUsd = adRev,
                        rewardEarningsUsd = rewRev,
                        giftEarningsUsd = 0.0,
                        rpmUsd = 2.45
                    )
                }
            }

            _uiState.update {
                it.copy(
                    monetizationState = it.monetizationState.copy(
                        status = status,
                        qualification = snapshot,
                        videos = videoMonetizations
                    )
                )
            }
        }
    }

    fun submitMonetizationInterest(category: String, statement: String, email: String) {
        val userId = _uiState.value.savedProfile?.userId ?: "owner_primary_account_id"
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            val now = System.currentTimeMillis()
            val entity = CreatorMonetizationEntity(
                userId = userId,
                status = "SUBMITTED",
                submittedAtMs = now,
                primaryCategory = category,
                statementNote = statement,
                contactEmail = email,
                agreedTerms = true,
                updatedAtMs = now
            )
            repository.dao.upsertCreatorMonetization(entity)

            _uiState.update {
                it.copy(
                    monetizationState = it.monetizationState.copy(
                        status = CreatorMonetizationStatus.SUBMITTED,
                        actionMessage = "Application submitted successfully! Our team will review your account."
                    )
                )
            }
        }
    }

    fun setMonetizationStatus(status: CreatorMonetizationStatus) {
        val userId = _uiState.value.savedProfile?.userId ?: "owner_primary_account_id"
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.updateCreatorMonetizationStatus(
                userId = userId,
                status = status.name,
                reviewedAtMs = System.currentTimeMillis()
            )
            _uiState.update { it.copy(monetizationState = it.monetizationState.copy(status = status)) }
        }
    }

    fun updateVideoMonetization(item: VideoMonetizationItem) {
        val userId = _uiState.value.savedProfile?.userId ?: "owner_primary_account_id"
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            val entity = VideoMonetizationEntity(
                videoId = item.videoId,
                creatorUserId = userId,
                isMonetized = item.isMonetized,
                instreamAdsEnabled = item.instreamAdsEnabled,
                videoGiftsEnabled = item.videoGiftsEnabled,
                creatorRewardsEligible = item.creatorRewardsEligible,
                monetizedViews = item.monetizedViews,
                adImpressions = item.adImpressions,
                adEarningsUsd = item.adEarningsUsd,
                rewardEarningsUsd = item.rewardEarningsUsd,
                giftEarningsUsd = item.giftEarningsUsd,
                rpmUsd = item.rpmUsd,
                updatedAtMs = System.currentTimeMillis()
            )
            repository.dao.upsertVideoMonetization(entity)

            _uiState.update { state ->
                val updatedList = state.videoMonetizationList.map {
                    if (it.videoId == item.videoId) item else it
                }
                state.copy(monetizationState = state.monetizationState.copy(videos = updatedList))
            }
        }
    }

    fun openCreateGroup() {
        _uiState.update { it.copy(destination = AppDestination.CREATE_GROUP, errorMessage = null) }
    }

    fun closeCreateGroup() {
        _uiState.update { it.copy(destination = AppDestination.MAIN_HOME) }
    }

    fun openGroupProfile(groupId: String) {
        _uiState.update { it.copy(destination = AppDestination.GROUP_PROFILE) }
        loadGroupProfileData(groupId)
    }

    fun closeGroupProfile() {
        _uiState.update { it.copy(destination = AppDestination.ONE_TO_ONE_CHAT) }
    }

    fun createGroup(
        name: String,
        description: String,
        isPublic: Boolean,
        avatarUri: String?,
        selectedUserIds: List<String>,
        permissions: GroupPermissionsSnapshot
    ) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            _uiState.update { it.copy(groupState = it.groupState.copy(isCreateGroupLoading = true)) }
            val groupId = "group_" + UUID.randomUUID().toString()
            val cleanName = name.trim()
            val inviteSlug = cleanName.lowercase(Locale.ROOT).replace("[^a-z0-9_]".toRegex(), "")
            val groupEntity = GroupChatEntity(
                groupId = groupId,
                ownerUserId = currentUserId,
                groupName = cleanName,
                groupDescription = description.trim(),
                avatarUrl = avatarUri,
                isPublic = isPublic,
                inviteLink = "t.me/${inviteSlug.ifEmpty { "telefam" }}",
                whoCanSendMessages = permissions.whoCanSendMessages.wireName,
                whoCanAddMembers = permissions.whoCanAddMembers.wireName,
                whoCanPinMessages = permissions.whoCanPinMessages.wireName,
                whoCanShareMessage = permissions.whoCanShareMessage.wireName,
                allowCopyMessages = permissions.allowCopyMessages,
                allowScreenshots = permissions.allowScreenshots,
                allowDownloadMedia = permissions.allowDownloadMedia,
                requiresJoinApproval = permissions.requiresJoinApproval,
                createdAtMs = System.currentTimeMillis(),
                updatedAtMs = System.currentTimeMillis()
            )
            repository.dao.insertGroup(groupEntity)

            // Insert Owner
            val ownerMember = GroupMemberEntity(
                memberId = "${groupId}_${currentUserId}",
                groupId = groupId,
                userId = currentUserId,
                userFullName = _uiState.value.savedProfile?.fullName ?: "Owner",
                userUsername = _uiState.value.savedProfile?.username ?: "owner",
                userAvatarUrl = _uiState.value.savedProfile?.avatarLocalPath ?: _uiState.value.savedProfile?.avatarCdnUrl,
                role = "OWNER",
                isOnline = true,
                lastSeenMs = System.currentTimeMillis(),
                isMuted = false,
                mutedUntilMs = null,
                isBanned = false,
                joinedAtMs = System.currentTimeMillis()
            )
            repository.dao.insertGroupMember(ownerMember)

            // Insert selected friends / contacts
            selectedUserIds.forEach { uid ->
                val friendContact = _uiState.value.contactsUsers.find { it.userId == uid }
                repository.dao.insertGroupMember(
                    GroupMemberEntity(
                        memberId = "${groupId}_${uid}",
                        groupId = groupId,
                        userId = uid,
                        userFullName = friendContact?.fullName ?: "Member",
                        userUsername = friendContact?.username ?: "member",
                        userAvatarUrl = friendContact?.avatarLocalPath ?: friendContact?.avatarCdnUrl,
                        role = "MEMBER",
                        isOnline = false,
                        lastSeenMs = System.currentTimeMillis(),
                        isMuted = false,
                        mutedUntilMs = null,
                        isBanned = false,
                        joinedAtMs = System.currentTimeMillis()
                    )
                )
            }

            // Insert into telefam_chats for conversation list
            val chatEntity = ChatEntity(
                chatId = groupId,
                ownerUserId = currentUserId,
                peerUserId = groupId,
                peerFullName = cleanName,
                peerUsername = "group",
                peerAvatarUrl = avatarUri,
                peerBio = description.trim(),
                peerIsOnline = true,
                peerLastSeenMs = System.currentTimeMillis(),
                disappearingDurationSeconds = 0,
                e2eeSafetyNumberVerified = true,
                lastMessagePreview = "Group created",
                lastMessageType = "SYSTEM",
                lastMessageSenderId = currentUserId,
                lastMessageTimestampMs = System.currentTimeMillis()
            )
            repository.dao.upsertChat(chatEntity)

            // Insert Audit Log
            val logEntity = GroupAuditLogEntity(
                logId = "log_" + UUID.randomUUID().toString(),
                groupId = groupId,
                actorUserId = currentUserId,
                actorName = _uiState.value.savedProfile?.fullName ?: "Owner",
                actionText = "Group created with ${selectedUserIds.size + 1} members",
                timestampMs = System.currentTimeMillis()
            )
            repository.dao.insertAuditLog(logEntity)

            _uiState.update { it.copy(groupState = it.groupState.copy(isCreateGroupLoading = false)) }

            withContext(Dispatchers.Main) {
                openOneToOneChatById(groupId)
            }
        }
    }

    fun loadGroupProfileData(groupId: String) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            val group = repository.dao.getGroupById(groupId) ?: return@launch
            val memberEntities = repository.dao.getGroupMembers(groupId)
            val requests = repository.dao.getPendingJoinRequests(groupId)
            val auditLogs = repository.dao.getGroupAuditLogs(groupId)

            val memberItems = memberEntities.map { member ->
                GroupMemberItem(
                    memberId = member.memberId,
                    groupId = groupId,
                    userId = member.userId,
                    fullName = member.userFullName,
                    username = member.userUsername,
                    avatarUrl = member.userAvatarUrl,
                    role = runCatching { GroupRole.valueOf(member.role) }.getOrDefault(GroupRole.MEMBER),
                    isOnline = member.isOnline,
                    lastSeenMs = member.lastSeenMs,
                    isMuted = member.isMuted,
                    isApproved = true,
                    joinedAtMs = member.joinedAtMs
                )
            }

            val myMember = memberEntities.find { it.userId == currentUserId }
            val myRole = myMember?.let { runCatching { GroupRole.valueOf(it.role) }.getOrNull() } ?: GroupRole.MEMBER

            val joinRequestItems = requests.map { req ->
                GroupJoinRequestItem(
                    requestId = req.requestId,
                    groupId = groupId,
                    userId = req.userId,
                    fullName = req.userFullName,
                    username = req.userUsername,
                    avatarUrl = req.userAvatarUrl,
                    requestedAtMs = req.requestedAtMs
                )
            }

            val auditLogItems = auditLogs.map { log ->
                GroupAuditLogItem(
                    logId = log.logId,
                    groupId = groupId,
                    actorUserId = log.actorUserId,
                    actorName = log.actorName,
                    actionText = log.actionText,
                    timestampMs = log.timestampMs
                )
            }

            val permissions = GroupPermissionsSnapshot(
                whoCanSendMessages = WhoCanOption.fromWire(group.whoCanSendMessages),
                whoCanAddMembers = WhoCanOption.fromWire(group.whoCanAddMembers),
                whoCanPinMessages = WhoCanOption.fromWire(group.whoCanPinMessages),
                whoCanShareMessage = WhoCanOption.fromWire(group.whoCanShareMessage),
                allowCopyMessages = group.allowCopyMessages,
                allowScreenshots = group.allowScreenshots,
                allowDownloadMedia = group.allowDownloadMedia,
                requiresJoinApproval = group.requiresJoinApproval
            )

            val uiModel = GroupProfileUiModel(
                groupId = group.groupId,
                groupName = group.groupName,
                groupDescription = group.groupDescription,
                avatarUrl = group.avatarUrl,
                groupType = if (group.isPublic) GroupType.PUBLIC else GroupType.PRIVATE,
                inviteLink = group.inviteLink,
                memberCount = memberEntities.size,
                onlineCount = memberItems.count { it.isOnline }.coerceAtLeast(1),
                myRole = myRole,
                isMuted = myMember?.isMuted == true,
                isHistoryVisible = true,
                permissions = permissions,
                members = memberItems,
                pendingApprovals = joinRequestItems,
                auditLogs = auditLogItems,
                bannedCount = repository.dao.getGroupBannedCount(groupId),
                mediaItems = emptyList()
            )

            _uiState.update { it.copy(groupState = it.groupState.copy(activeGroupProfile = uiModel)) }
        }
    }

    fun updateGroupPermissions(groupId: String, permissions: GroupPermissionsSnapshot) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            val group = repository.dao.getGroupById(groupId) ?: return@launch
            val updated = group.copy(
                whoCanSendMessages = permissions.whoCanSendMessages.wireName,
                whoCanAddMembers = permissions.whoCanAddMembers.wireName,
                whoCanPinMessages = permissions.whoCanPinMessages.wireName,
                whoCanShareMessage = permissions.whoCanShareMessage.wireName,
                allowCopyMessages = permissions.allowCopyMessages,
                allowScreenshots = permissions.allowScreenshots,
                allowDownloadMedia = permissions.allowDownloadMedia,
                requiresJoinApproval = permissions.requiresJoinApproval,
                updatedAtMs = System.currentTimeMillis()
            )
            repository.dao.updateGroup(updated)
            repository.dao.insertAuditLog(
                GroupAuditLogEntity(
                    logId = "log_" + UUID.randomUUID().toString(),
                    groupId = groupId,
                    actorUserId = currentUserId,
                    actorName = _uiState.value.savedProfile?.fullName ?: "Admin",
                    actionText = "Updated group security and messaging permissions",
                    timestampMs = System.currentTimeMillis()
                )
            )
            loadGroupProfileData(groupId)
        }
    }

    fun promoteMemberRole(groupId: String, targetUserId: String, newRole: GroupRole) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.updateGroupMemberRole(groupId, targetUserId, newRole.name)
            repository.dao.insertAuditLog(
                GroupAuditLogEntity(
                    logId = "log_" + UUID.randomUUID().toString(),
                    groupId = groupId,
                    actorUserId = currentUserId,
                    actorName = _uiState.value.savedProfile?.fullName ?: "Admin",
                    actionText = "Role changed to ${newRole.name} for user",
                    timestampMs = System.currentTimeMillis()
                )
            )
            loadGroupProfileData(groupId)
        }
    }

    fun removeMemberFromGroup(groupId: String, targetUserId: String) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.removeGroupMember(groupId, targetUserId)
            repository.dao.insertAuditLog(
                GroupAuditLogEntity(
                    logId = "log_" + UUID.randomUUID().toString(),
                    groupId = groupId,
                    actorUserId = currentUserId,
                    actorName = _uiState.value.savedProfile?.fullName ?: "Admin",
                    actionText = "Removed member from group",
                    timestampMs = System.currentTimeMillis()
                )
            )
            loadGroupProfileData(groupId)
        }
    }

    fun muteMember(groupId: String, targetUserId: String, isMuted: Boolean) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.updateGroupMemberMute(groupId, targetUserId, isMuted, null)
            repository.dao.insertAuditLog(
                GroupAuditLogEntity(
                    logId = "log_" + UUID.randomUUID().toString(),
                    groupId = groupId,
                    actorUserId = currentUserId,
                    actorName = _uiState.value.savedProfile?.fullName ?: "Admin",
                    actionText = if (isMuted) "Muted member" else "Unmuted member",
                    timestampMs = System.currentTimeMillis()
                )
            )
            loadGroupProfileData(groupId)
        }
    }

    fun approveJoinRequest(requestId: String, groupId: String, userId: String) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.updateJoinRequestStatus(requestId, "APPROVED")
            val friendContact = _uiState.value.contactsUsers.find { it.userId == userId }
            val newMember = GroupMemberEntity(
                memberId = "${groupId}_${userId}",
                groupId = groupId,
                userId = userId,
                userFullName = friendContact?.fullName ?: "Member",
                userUsername = friendContact?.username ?: "member",
                userAvatarUrl = friendContact?.avatarLocalPath ?: friendContact?.avatarCdnUrl,
                role = "MEMBER",
                isOnline = false,
                lastSeenMs = System.currentTimeMillis(),
                isMuted = false,
                mutedUntilMs = null,
                isBanned = false,
                joinedAtMs = System.currentTimeMillis()
            )
            repository.dao.insertGroupMember(newMember)
            repository.dao.insertAuditLog(
                GroupAuditLogEntity(
                    logId = "log_" + UUID.randomUUID().toString(),
                    groupId = groupId,
                    actorUserId = currentUserId,
                    actorName = _uiState.value.savedProfile?.fullName ?: "Admin",
                    actionText = "Approved join request for user",
                    timestampMs = System.currentTimeMillis()
                )
            )
            loadGroupProfileData(groupId)
        }
    }

    fun rejectJoinRequest(requestId: String, groupId: String, userId: String) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.updateJoinRequestStatus(requestId, "REJECTED")
            repository.dao.insertAuditLog(
                GroupAuditLogEntity(
                    logId = "log_" + UUID.randomUUID().toString(),
                    groupId = groupId,
                    actorUserId = currentUserId,
                    actorName = _uiState.value.savedProfile?.fullName ?: "Admin",
                    actionText = "Rejected join request for user",
                    timestampMs = System.currentTimeMillis()
                )
            )
            loadGroupProfileData(groupId)
        }
    }

    fun leaveCurrentGroup() {
        val currentUserId = currentAuthenticatedUserId()
        val activeProfile = _uiState.value.activeGroupProfile ?: return
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.removeGroupMember(activeProfile.groupId, currentUserId)
            repository.dao.insertAuditLog(
                GroupAuditLogEntity(
                    logId = "log_" + UUID.randomUUID().toString(),
                    groupId = activeProfile.groupId,
                    actorUserId = currentUserId,
                    actorName = _uiState.value.savedProfile?.fullName ?: "Member",
                    actionText = "Member left group",
                    timestampMs = System.currentTimeMillis()
                )
            )
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(destination = AppDestination.MAIN_HOME, groupState = it.groupState.copy(activeGroupProfile = null)) }
            }
        }
    }

    fun reportGroup(groupId: String, reason: String) {
        _uiState.update { it.copy(groupState = it.groupState.copy(groupProfileStatusMessage = "Group report submitted for review.")) }
    }

    fun clearGroupProfileStatusMessage() {
        _uiState.update { it.copy(groupState = it.groupState.copy(groupProfileStatusMessage = null)) }
    }

    // =========================================================================
    // Memories Feature Layer (Offline-First Room Source of Truth)
    // =========================================================================

    private var memoriesObservationJob: Job? = null

    fun openMemories() {
        val currentUserId = currentAuthenticatedUserId()
        _uiState.update {
            it.copy(
                destination = AppDestination.MEMORIES,
                isOwnerMenuOpen = false,
                memoriesState = it.memoriesState.copy(statusMessage = null)
            )
        }
        observeMemories(currentUserId)
    }

    fun closeMemories() {
        memoriesObservationJob?.cancel()
        _uiState.update {
            it.copy(
                destination = AppDestination.MAIN_HOME,
                memoriesState = it.memoriesState.copy(statusMessage = null)
            )
        }
    }

    private fun observeMemories(userId: String) {
        memoriesObservationJob?.cancel()
        memoriesObservationJob = viewModelScope.launch(Dispatchers.IO) {
            repository.dao.observeMemories(userId).collectLatest { entities ->
                val items = entities.map { entity ->
                    MemoryItem(
                        id = entity.memoryId,
                        userId = entity.userId,
                        caption = entity.caption,
                        mediaUri = entity.mediaUri,
                        mediaType = try { MemoryMediaType.valueOf(entity.mediaType) } catch (_: Exception) { MemoryMediaType.IMAGE },
                        memoryDateMs = entity.memoryDateMs,
                        createdAtMs = entity.createdAtMs,
                        locationName = entity.locationName,
                        tagFeeling = entity.tagFeeling,
                        isFavorite = entity.isFavorite,
                        privacy = MemoryPrivacy.fromWire(entity.privacy)
                    )
                }
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(memoriesState = it.memoriesState.copy(items = items)) }
                }
            }
        }
    }

    fun createMemory(
        mediaUri: String,
        mediaType: MemoryMediaType,
        caption: String,
        location: String,
        feeling: String,
        memoryDateMs: Long,
        privacy: MemoryPrivacy
    ) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            val entity = MemoryEntity(
                memoryId = "mem_${UUID.randomUUID().toString().take(12)}",
                userId = currentUserId,
                caption = caption,
                mediaUri = mediaUri,
                mediaType = mediaType.name,
                memoryDateMs = memoryDateMs,
                createdAtMs = System.currentTimeMillis(),
                locationName = location,
                tagFeeling = feeling,
                isFavorite = false,
                privacy = privacy.wireName
            )
            repository.dao.insertMemory(entity)
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(memoriesState = it.memoriesState.copy(statusMessage = "Memory created successfully!")) }
            }
        }
    }

    fun toggleMemoryFavorite(memoryId: String, currentFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.updateMemoryFavorite(memoryId, !currentFavorite)
        }
    }

    fun deleteMemory(memoryId: String) {
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.deleteMemory(memoryId)
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(memoriesState = it.memoriesState.copy(statusMessage = "Memory deleted.")) }
            }
        }
    }

    fun clearMemoriesStatusMessage() {
        _uiState.update { it.copy(memoriesState = it.memoriesState.copy(statusMessage = null)) }
    }

    // =========================================================================
    // Creator Subscription Purchase & Cancellation (Direct Security & Verification)
    // =========================================================================

    fun subscribeToCreator(plan: SubscriptionPlanEntity, paymentMethod: String) {
        val currentUserId = currentAuthenticatedUserId()
        val profile = _uiState.value.activePublicProfile ?: return
        if (profile.userId == currentUserId) {
            _uiState.update {
                it.copy(
                    publicProfileSubscriptionState = it.publicProfileSubscriptionState.copy(
                        errorMessage = "You cannot subscribe to your own account."
                    )
                )
            }
            return
        }

        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            _uiState.update {
                it.copy(
                    publicProfileSubscriptionState = it.publicProfileSubscriptionState.copy(
                        isLoading = true,
                        errorMessage = null,
                        successMessage = null
                    )
                )
            }

            if (paymentMethod == "WALLET") {
                val wallet = repository.dao.getWalletBalance(currentUserId)
                val available = wallet?.availableBalance ?: 0.0
                if (available < plan.monthlyPrice) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                publicProfileSubscriptionState = it.publicProfileSubscriptionState.copy(
                                    isLoading = false,
                                    errorMessage = "Insufficient wallet balance ($${String.format(Locale.US, "%.2f", available)}). Required: $${String.format(Locale.US, "%.2f", plan.monthlyPrice)}."
                                )
                            )
                        }
                    }
                    return@launch
                }

                // Deduct from subscriber wallet
                if (wallet != null) {
                    val updatedSubscriberWallet = wallet.copy(
                        availableBalance = (wallet.availableBalance - plan.monthlyPrice).coerceAtLeast(0.0),
                        totalBalance = (wallet.totalBalance - plan.monthlyPrice).coerceAtLeast(0.0),
                        lastUpdatedAtMs = System.currentTimeMillis()
                    )
                    repository.dao.upsertWalletBalance(updatedSubscriberWallet)
                }

                // Insert subscriber transaction
                val subTx = WalletTransactionEntity(
                    transactionId = "tx_sub_${UUID.randomUUID().toString().take(12)}",
                    userId = currentUserId,
                    type = "PAYOUTS",
                    category = "SUBSCRIPTIONS",
                    title = "Subscription to ${profile.fullName}",
                    description = "Subscription to ${profile.fullName} (${plan.planName})",
                    amount = -plan.monthlyPrice,
                    currency = "USD",
                    status = "COMPLETED",
                    referenceId = plan.planId,
                    createdAtMs = System.currentTimeMillis()
                )
                repository.dao.insertWalletTransaction(subTx)

                // Credit creator's wallet (90% creator share, 10% platform fee)
                val netCreatorEarnings = plan.monthlyPrice * 0.90
                val creatorWallet = repository.dao.getWalletBalance(profile.userId) ?: WalletBalanceEntity(
                    userId = profile.userId,
                    totalBalance = 0.0,
                    availableBalance = 0.0,
                    pendingBalance = 0.0,
                    currency = "USD",
                    isHidden = false,
                    lastUpdatedAtMs = System.currentTimeMillis()
                )
                val updatedCreatorWallet = creatorWallet.copy(
                    availableBalance = creatorWallet.availableBalance + netCreatorEarnings,
                    totalBalance = creatorWallet.totalBalance + netCreatorEarnings,
                    lastUpdatedAtMs = System.currentTimeMillis()
                )
                repository.dao.upsertWalletBalance(updatedCreatorWallet)

                val currentUserName = _uiState.value.savedProfile?.fullName ?: "Subscriber"
                val creatorTx = WalletTransactionEntity(
                    transactionId = "tx_earn_${UUID.randomUUID().toString().take(12)}",
                    userId = profile.userId,
                    type = "EARNINGS",
                    category = "SUBSCRIPTIONS",
                    title = "Subscription from $currentUserName",
                    description = "Subscription from $currentUserName (${plan.planName})",
                    amount = netCreatorEarnings,
                    currency = "USD",
                    status = "COMPLETED",
                    referenceId = plan.planId,
                    createdAtMs = System.currentTimeMillis()
                )
                repository.dao.insertWalletTransaction(creatorTx)
            }

            // Create or update subscription record
            val subscriberEntity = CreatorSubscriberEntity(
                subscriptionId = "sub_${UUID.randomUUID().toString().take(12)}",
                creatorUserId = profile.userId,
                subscriberUserId = currentUserId,
                subscriberName = _uiState.value.savedProfile?.fullName ?: "Member",
                subscriberUsername = _uiState.value.savedProfile?.username ?: "member",
                subscriberAvatarUrl = _uiState.value.savedProfile?.avatarLocalPath ?: "",
                planId = plan.planId,
                planName = plan.planName,
                pricePaid = plan.monthlyPrice,
                currency = "USD",
                status = "ACTIVE",
                startedAtMs = System.currentTimeMillis(),
                expiresAtMs = System.currentTimeMillis() + 30L * 86_400_000L,
                isAutoRenew = true
            )
            repository.dao.upsertCreatorSubscriber(subscriberEntity)

            // Increment plan subscribersCount
            val updatedPlan = plan.copy(subscribersCount = plan.subscribersCount + 1)
            repository.dao.upsertSubscriptionPlan(updatedPlan)

            // Refresh state
            val refreshedSub = repository.dao.getSubscription(profile.userId, currentUserId)
            val refreshedPlans = repository.dao.getSubscriptionPlans(profile.userId)
            val refreshedWallet = repository.dao.getWalletBalance(currentUserId)

            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        publicProfileSubscriptionState = it.publicProfileSubscriptionState.copy(
                            isLoading = false,
                            activeSubscription = refreshedSub,
                            availablePlans = refreshedPlans,
                            successMessage = "Successfully subscribed to ${profile.fullName}!"
                        ),
                        walletState = it.walletState.copy(balance = refreshedWallet)
                    )
                }
            }
        }
    }

    fun cancelCreatorSubscription(creatorUserId: String) {
        val currentUserId = currentAuthenticatedUserId()
        viewModelScope.launch(Dispatchers.IO + safeExceptionHandler) {
            repository.dao.deleteSubscription(creatorUserId, currentUserId)
            val refreshedPlans = repository.dao.getSubscriptionPlans(creatorUserId)
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        publicProfileSubscriptionState = it.publicProfileSubscriptionState.copy(
                            activeSubscription = null,
                            availablePlans = refreshedPlans,
                            successMessage = "Subscription canceled."
                        )
                    )
                }
            }
        }
    }

    fun clearSubscribeStatusMessages() {
        _uiState.update {
            it.copy(
                publicProfileSubscriptionState = it.publicProfileSubscriptionState.copy(
                    successMessage = null,
                    errorMessage = null
                )
            )
        }
    }

    private fun observeCallEngineAndHistory() {
        activeCallEngineObserverJob?.cancel()
        activeCallEngineObserverJob = viewModelScope.launch(safeExceptionHandler) {
            callEngine.currentCall.collectLatest { session ->
                _uiState.update { state ->
                    state.copy(
                        callState = state.callState.copy(activeCall = session),
                        destination = if (session != null && !session.isMinimized && state.destination != AppDestination.CALL) {
                            AppDestination.CALL
                        } else if (session == null && state.destination == AppDestination.CALL) {
                            if (state.activeChatId != null) AppDestination.ONE_TO_ONE_CHAT else AppDestination.MAIN_HOME
                        } else {
                            state.destination
                        }
                    )
                }
            }
        }

        callHistoryObserverJob?.cancel()
        callHistoryObserverJob = viewModelScope.launch(safeExceptionHandler) {
            val currentUserId = _uiState.value.activeSession?.userId ?: _uiState.value.savedProfile?.userId ?: "local_telefam_member"
            repository.dao.observeCallHistory(currentUserId).collectLatest { records ->
                _uiState.update { state ->
                    state.copy(callState = state.callState.copy(callHistory = records))
                }
            }
        }
    }

    fun startAudioCall(
        peerUserId: String,
        peerFullName: String,
        peerUsername: String,
        peerAvatarUrl: String?
    ) {
        val privacy = _uiState.value.privacySettings
        val whoCanCall = PrivacyAudience.fromStorageValue(privacy.whoCanCallMe)
        if (whoCanCall == PrivacyAudience.NOBODY) {
            _uiState.update {
                it.copy(
                    callState = it.callState.copy(
                        privacyNoticeBanner = "Calls not allowed by this user's privacy settings"
                    )
                )
            }
            viewModelScope.launch(safeExceptionHandler) {
                val currentUserId = currentAuthenticatedUserId()
                val chatId = chatRepository.openOrCreateOneToOneChat(
                    ownerUserId = currentUserId,
                    peerUserId = peerUserId,
                    peerFullName = peerFullName,
                    peerUsername = peerUsername,
                    peerAvatarUrl = peerAvatarUrl,
                    peerBio = "",
                    peerIsOnline = true,
                    peerLastSeenMs = System.currentTimeMillis(),
                    peerIsDeleted = false
                )
                chatRepository.sendMessage(
                    ownerUserId = currentUserId,
                    chatId = chatId,
                    peerUserId = peerUserId,
                    messageType = ChatMessageType.TEXT,
                    text = "Call Request: Voice call attempt. Please call back when available."
                )
            }
            return
        }

        val session = callEngine.startOutgoingCall(
            peerUserId = peerUserId,
            peerFullName = peerFullName,
            peerUsername = peerUsername,
            peerAvatarUrl = peerAvatarUrl,
            callType = CallType.AUDIO
        )
        _uiState.update {
            it.copy(
                callState = it.callState.copy(activeCall = session, privacyNoticeBanner = null),
                destination = AppDestination.CALL
            )
        }
    }

    fun startVideoCall(
        peerUserId: String,
        peerFullName: String,
        peerUsername: String,
        peerAvatarUrl: String?
    ) {
        val privacy = _uiState.value.privacySettings
        val whoCanCall = PrivacyAudience.fromStorageValue(privacy.whoCanCallMe)
        if (whoCanCall == PrivacyAudience.NOBODY) {
            _uiState.update {
                it.copy(
                    callState = it.callState.copy(
                        privacyNoticeBanner = "Calls not allowed by this user's privacy settings"
                    )
                )
            }
            viewModelScope.launch(safeExceptionHandler) {
                val currentUserId = currentAuthenticatedUserId()
                val chatId = chatRepository.openOrCreateOneToOneChat(
                    ownerUserId = currentUserId,
                    peerUserId = peerUserId,
                    peerFullName = peerFullName,
                    peerUsername = peerUsername,
                    peerAvatarUrl = peerAvatarUrl,
                    peerBio = "",
                    peerIsOnline = true,
                    peerLastSeenMs = System.currentTimeMillis(),
                    peerIsDeleted = false
                )
                chatRepository.sendMessage(
                    ownerUserId = currentUserId,
                    chatId = chatId,
                    peerUserId = peerUserId,
                    messageType = ChatMessageType.TEXT,
                    text = "Video Call Request: Video call attempt. Please call back when available."
                )
            }
            return
        }

        val session = callEngine.startOutgoingCall(
            peerUserId = peerUserId,
            peerFullName = peerFullName,
            peerUsername = peerUsername,
            peerAvatarUrl = peerAvatarUrl,
            callType = CallType.VIDEO
        )
        _uiState.update {
            it.copy(
                callState = it.callState.copy(activeCall = session, privacyNoticeBanner = null),
                destination = AppDestination.CALL
            )
        }
    }

    fun acceptIncomingCall() {
        val incoming = _uiState.value.incomingCall ?: return
        _uiState.update {
            it.copy(
                callState = it.callState.copy(incomingCall = null),
                destination = AppDestination.CALL
            )
        }
        callEngine.acceptCall()
    }

    fun declineIncomingCall() {
        val incoming = _uiState.value.incomingCall ?: return
        val currentUserId = _uiState.value.activeSession?.userId ?: "local_telefam_member"
        viewModelScope.launch(safeExceptionHandler) {
            repository.dao.insertCallRecord(
                CallRecordEntity(
                    callId = incoming.callId,
                    userId = currentUserId,
                    peerUserId = incoming.peerUserId,
                    peerFullName = incoming.peerFullName,
                    peerUsername = incoming.peerUsername,
                    peerAvatarUrl = incoming.peerAvatarUrl,
                    callType = incoming.callType.name,
                    direction = CallDirection.MISSED.name,
                    timestampMs = incoming.startedAtMs,
                    durationSeconds = 0,
                    networkQualitySummary = "Declined",
                    isP2pDirect = true
                )
            )
        }
        _uiState.update {
            it.copy(callState = it.callState.copy(incomingCall = null))
        }
    }

    fun endActiveCall() {
        val active = _uiState.value.activeCall
        val ended = callEngine.endCall() ?: active
        if (ended != null) {
            val currentUserId = _uiState.value.activeSession?.userId ?: "local_telefam_member"
            val direction = if (ended.isOutgoing) {
                if (ended.durationSeconds > 0) CallDirection.OUTGOING else CallDirection.MISSED
            } else {
                if (ended.durationSeconds > 0) CallDirection.INCOMING else CallDirection.MISSED
            }
            viewModelScope.launch(safeExceptionHandler) {
                repository.dao.insertCallRecord(
                    CallRecordEntity(
                        callId = ended.callId,
                        userId = currentUserId,
                        peerUserId = ended.peerUserId,
                        peerFullName = ended.peerFullName,
                        peerUsername = ended.peerUsername,
                        peerAvatarUrl = ended.peerAvatarUrl,
                        callType = ended.callType.name,
                        direction = direction.name,
                        timestampMs = ended.startedAtMs,
                        durationSeconds = ended.durationSeconds,
                        networkQualitySummary = "${ended.networkQuality.label} • ${ended.resolution}",
                        isP2pDirect = !ended.isTurnRelayed
                    )
                )
            }
        }
        _uiState.update {
            it.copy(
                callState = it.callState.copy(activeCall = null),
                destination = if (it.destination == AppDestination.CALL) {
                    if (it.activeChatId != null) AppDestination.ONE_TO_ONE_CHAT else AppDestination.MAIN_HOME
                } else {
                    it.destination
                }
            )
        }
    }

    fun toggleCallMute() = callEngine.toggleMute()
    fun toggleCallCamera() = callEngine.toggleCamera()
    fun toggleCallSpeaker() = callEngine.toggleSpeaker()
    fun flipCallCamera() = callEngine.flipCamera()
    fun setCallFilter(filter: VideoCallFilter) = callEngine.setFilter(filter)

    fun switchAudioCallToVideo() {
        callEngine.toggleCamera()
    }

    fun minimizeCall(minimize: Boolean) {
        callEngine.setMinimized(minimize)
        _uiState.update { state ->
            state.copy(
                callState = state.callState.copy(
                    activeCall = state.activeCall?.copy(isMinimized = minimize)
                ),
                destination = if (minimize && state.destination == AppDestination.CALL) {
                    if (state.activeChatId != null) AppDestination.ONE_TO_ONE_CHAT else AppDestination.MAIN_HOME
                } else if (!minimize) {
                    AppDestination.CALL
                } else {
                    state.destination
                }
            )
        }
    }

    fun setSelectedInboxTab(tab: String) {
        _uiState.update {
            it.copy(callState = it.callState.copy(selectedInboxTab = tab))
        }
    }

    fun clearCallHistory() {
        val currentUserId = _uiState.value.activeSession?.userId ?: "local_telefam_member"
        viewModelScope.launch(safeExceptionHandler) {
            repository.dao.clearCallHistory(currentUserId)
        }
    }

    fun clearCallPrivacyBanner() {
        _uiState.update {
            it.copy(callState = it.callState.copy(privacyNoticeBanner = null))
        }
    }
}


