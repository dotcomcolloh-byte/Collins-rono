package com.telefam.company.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.resolveTelefamDarkTheme
import com.telefam.company.app.notifications.TelefamNotificationEngine
import com.telefam.company.app.ui.screens.AuthScreen
import com.telefam.company.app.ui.screens.E2eeVerificationScreen
import com.telefam.company.app.ui.screens.MainHomeScreen
import com.telefam.company.app.ui.screens.MarketplaceMainScreen
import com.telefam.company.app.ui.screens.MarketplacePostStudioSheet
import com.telefam.company.app.ui.screens.MarketplaceProductDetailsScreen
import com.telefam.company.app.ui.screens.MarketplaceReportDialog
import com.telefam.company.app.ui.screens.OneToOneChatScreen
import com.telefam.company.app.ui.screens.PaymentCheckoutSheet
import com.telefam.company.app.ui.screens.PrivacySettingsScreen
import com.telefam.company.app.ui.screens.ProfileSetupScreen
import com.telefam.company.app.ui.screens.PublicUserProfileScreen
import com.telefam.company.app.ui.screens.SettingsAndPrivacyScreen
import com.telefam.company.app.ui.screens.TelefamLaunchScreen
import com.telefam.company.app.ui.screens.LinkedDevicesScreen
import com.telefam.company.app.ui.screens.SubscriptionsScreen
import com.telefam.company.app.ui.screens.WalletScreen
import com.telefam.company.app.ui.screens.CreatorMonetizationHubScreen
import com.telefam.company.app.ui.screens.CreateGroupScreen
import com.telefam.company.app.ui.screens.GroupProfileScreen
import com.telefam.company.app.ui.screens.MemoriesScreen
import com.telefam.company.app.ui.screens.VideoCallScreen
import com.telefam.company.app.ui.screens.AudioCallScreen
import com.telefam.company.app.ui.screens.IncomingCallOverlay
import com.telefam.company.app.ui.screens.MinimizedCallBanner
import com.telefam.company.app.domain.CallType
import com.telefam.company.app.domain.ContactsDiscoverableUser

@Composable
fun TelefamRootApp(
    viewModel: TelefamViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val voiceRecordingState by viewModel.voiceRecordingState.collectAsStateWithLifecycle()
    val bubblePlaybackState by viewModel.bubblePlaybackState.collectAsStateWithLifecycle()
    val pendingDeepLink by TelefamNotificationEngine.pendingDeepLink.collectAsStateWithLifecycle()
    val klipyPagingItems = viewModel.klipyPagedFlow.collectAsLazyPagingItems()
    val context = LocalContext.current
    val resolvedDarkTheme = resolveTelefamDarkTheme(uiState.accountSettings.appThemeMode)

    val startupPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.onLocationPermissionResult()
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = buildList {
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
        startupPermissionsLauncher.launch(permissionsToRequest)
    }

    LaunchedEffect(pendingDeepLink, uiState.activeSession, uiState.destination) {
        val link = pendingDeepLink
        if (link != null && uiState.destination != AppDestination.LAUNCH_ANIMATION && uiState.destination != AppDestination.AUTH) {
            viewModel.handleNotificationDeepLink(link)
        }
    }

    MyApplicationTheme(darkTheme = resolvedDarkTheme) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val currentWidthDp = maxWidth.value.toInt()
            LaunchedEffect(currentWidthDp) {
                viewModel.updateScreenWidthDp(currentWidthDp)
            }

            Crossfade(
                targetState = uiState.destination,
                animationSpec = tween(durationMillis = 260),
                label = "telefam_root_navigation"
            ) { destination ->
                when (destination) {
                    AppDestination.LAUNCH_ANIMATION -> {
                        TelefamLaunchScreen()
                    }
                    AppDestination.AUTH -> {
                        AuthScreen(
                            uiState = uiState,
                            onEmailChanged = viewModel::onEmailChanged,
                            onPasswordChanged = viewModel::onPasswordChanged,
                            onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
                            onOtpChanged = viewModel::onOtpCodeChanged,
                            onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                            onToggleConfirmPasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
                            onPrimaryAction = viewModel::submitPrimaryAuthAction,
                            onSwitchMode = viewModel::switchAuthMode,
                            onContinueWithGoogle = { viewModel.signInWithGoogle(context) },
                            onSignInWithPasskey = { viewModel.signInWithPasskey(context) },
                            onResendOtp = viewModel::resendCurrentOtp,
                            onRequestLoginOtpInstead = viewModel::requestOtpInsteadOfLockedLogin
                        )
                    }
                    AppDestination.PROFILE_SETUP -> {
                        ProfileSetupScreen(
                            uiState = uiState,
                            onFullNameChanged = viewModel::onFullNameChanged,
                            onUsernameChanged = viewModel::onUsernameChanged,
                            onApplySuggestedUsername = viewModel::onApplySuggestedUsername,
                            onCountrySelected = viewModel::onCountrySelected,
                            onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
                            onGenderSelected = viewModel::onGenderSelected,
                            onDateOfBirthSelected = viewModel::onDateOfBirthSelected,
                            onBioChanged = viewModel::onBioChanged,
                            onAvatarUriPicked = viewModel::onAvatarUriPicked,
                            onCancelCrop = viewModel::cancelImageCrop,
                            onConfirmCrop = viewModel::confirmImageCropAndSanitize,
                            onSubmitProfile = viewModel::submitProfileSetup,
                            onBackClick = viewModel::navigateBackFromProfileSetup
                        )
                    }
                    AppDestination.MAIN_HOME -> {
                        MainHomeScreenRoute(viewModel = viewModel, uiState = uiState)
                    }
                    AppDestination.PRIVACY_SETTINGS -> {
                        PrivacySettingsScreen(
                            privacySettings = uiState.privacySettings,
                            onBackClick = viewModel::closePrivacySettings,
                            onUpdatePrivacyRule = viewModel::updatePrivacyRule,
                            onUpdateChatTheme = viewModel::updateChatBackgroundTheme,
                            onUpdateBubbleColor = viewModel::updateMessageBubbleColor
                        )
                    }
                    AppDestination.SETTINGS_AND_PRIVACY -> {
                        SettingsAndPrivacyScreenRoute(
                            viewModel = viewModel,
                            uiState = uiState,
                            context = context
                        )
                    }
                    AppDestination.PUBLIC_USER_PROFILE -> {
                        val activeProfile = uiState.activePublicProfile
                        if (activeProfile != null) {
                            PublicUserProfileScreen(
                                profile = activeProfile,
                                posts = uiState.activePublicProfilePosts,
                                postsFilter = uiState.activePublicProfilePostsFilter,
                                isOffline = false,
                                activeFollowListType = uiState.activeFollowListType,
                                followListUsers = uiState.followListUsers,
                                followListHasMore = uiState.followListHasMore,
                                onBackClick = viewModel::closePublicUserProfile,
                                onToggleFollowProfileUser = { viewModel.toggleFollowUser(activeProfile.userId) },
                                onToggleFollowAnyUser = viewModel::toggleFollowUser,
                                onOpenOtherUserProfile = viewModel::openPublicUserProfile,
                                onMessageComingSoonClick = { viewModel.openOneToOneChatWithDiscoverableUser(activeProfile) },
                                onSubscribeComingSoonClick = {},
                                subscription = uiState.activePublicProfileSubscription,
                                plans = uiState.activePublicProfilePlans,
                                walletBalance = uiState.walletBalance,
                                isSubscribingLoading = uiState.isSubscribingLoading,
                                onSubscribeToCreator = viewModel::subscribeToCreator,
                                onCancelSubscription = viewModel::cancelCreatorSubscription,
                                subscribeSuccessMessage = uiState.subscribeSuccessMessage,
                                subscribeErrorMessage = uiState.subscribeErrorMessage,
                                onClearSubscribeBanners = viewModel::clearSubscribeStatusMessages,
                                onPostComingSoonClick = {},
                                onPostsFilterChange = viewModel::onPublicProfilePostsFilterChanged,
                                onOpenFollowList = viewModel::openFollowConnectionsSheet,
                                onDismissFollowList = viewModel::dismissFollowConnectionsSheet,
                                onLoadMoreFollowList = viewModel::loadMoreFollowConnections,
                                onCopyProfileUrlConfirmed = {},
                                onToggleBlockUser = viewModel::setBlockActiveProfileUser,
                                onSubmitReport = viewModel::submitMultiQuestionReportForActiveProfile
                            )
                        } else {
                            MainHomeScreenRoute(
                                viewModel = viewModel,
                                uiState = uiState
                            )
                        }
                    }
                    AppDestination.ONE_TO_ONE_CHAT -> {
                        OneToOneChatRoute(
                            viewModel = viewModel,
                            uiState = uiState,
                            klipyPagingItems = klipyPagingItems,
                            voiceRecordingState = voiceRecordingState,
                            bubblePlaybackState = bubblePlaybackState
                        )
                    }
                    AppDestination.E2EE_VERIFICATION -> {
                        E2eeVerificationScreen(
                            snapshot = uiState.activeE2eeVerification,
                            onBackClick = viewModel::closeE2eeVerificationScreen,
                            onToggleVerified = viewModel::setActiveChatE2eeVerified
                        )
                    }
                    AppDestination.MARKETPLACE -> {
                        MarketplaceRoute(
                            viewModel = viewModel,
                            uiState = uiState
                        )
                    }
                    AppDestination.LINKED_DEVICES -> {
                        LinkedDevicesRoute(
                            viewModel = viewModel,
                            uiState = uiState
                        )
                    }
                    AppDestination.SUBSCRIPTIONS -> {
                        SubscriptionsScreen(
                            uiState = uiState,
                            onBackClick = viewModel::closeSubscriptions,
                            onSelectPeriodFilter = viewModel::setSubscriptionPeriodFilter,
                            onOpenCreatePlan = viewModel::openCreatePlanSheet,
                            onCloseCreatePlan = viewModel::closeCreatePlanSheet,
                            onOpenManagePlans = viewModel::openManagePlansSheet,
                            onCloseManagePlans = viewModel::closeManagePlansSheet,
                            onOpenInsights = viewModel::openSubscriptionInsightsSheet,
                            onCloseInsights = viewModel::closeSubscriptionInsightsSheet,
                            onOpenAllSubscribers = viewModel::openAllSubscribersSheet,
                            onCloseAllSubscribers = viewModel::closeAllSubscribersSheet,
                            onOpenHelp = viewModel::openSubscriptionsHelpSheet,
                            onCloseHelp = viewModel::closeSubscriptionsHelpSheet,
                            onCreatePlanSubmit = viewModel::createSubscriptionPlan,
                            onDeletePlan = viewModel::deleteSubscriptionPlan,
                            onClearStatusMessage = viewModel::clearSubscriptionsStatusMessage
                        )
                    }
                    AppDestination.WALLET -> {
                        WalletScreen(
                            uiState = uiState,
                            onBackClick = viewModel::closeWallet,
                            onToggleBalanceVisibility = viewModel::toggleWalletBalanceVisibility,
                            onSelectPeriodFilter = viewModel::setWalletPeriodFilter,
                            onSelectTransactionFilter = viewModel::setWalletTransactionFilter,
                            onOpenWithdrawSheet = viewModel::openWithdrawSheet,
                            onCloseWithdrawSheet = viewModel::closeWithdrawSheet,
                            onOpenAddPayoutAccountSheet = viewModel::openAddPayoutAccountSheet,
                            onCloseAddPayoutAccountSheet = viewModel::closeAddPayoutAccountSheet,
                            onOpenAllTransactionsSheet = viewModel::openAllTransactionsSheet,
                            onCloseAllTransactionsSheet = viewModel::closeAllTransactionsSheet,
                            onOpenPayoutHistorySheet = viewModel::openPayoutHistorySheet,
                            onClosePayoutHistorySheet = viewModel::closePayoutHistorySheet,
                            onOpenHelp = viewModel::openWalletHelpSheet,
                            onCloseHelp = viewModel::closeWalletHelpSheet,
                            onOpenSettings = viewModel::openWalletSettingsSheet,
                            onCloseSettings = viewModel::closeWalletSettingsSheet,
                            onOpenSubscriptionsScreen = viewModel::openSubscriptions,
                            onOpenStarsEarningsScreen = viewModel::openStarsEarnings,
                            onWithdrawSubmit = viewModel::requestWithdrawal,
                            onAddPayoutAccountSubmit = viewModel::addPayoutAccount,
                            onClearStatusMessage = viewModel::clearWalletStatusMessage
                        )
                    }
                    AppDestination.MONETIZATION -> {
                        CreatorMonetizationHubScreen(
                            uiState = uiState,
                            onBackClick = viewModel::closeMonetization,
                            onSubmitInterest = viewModel::submitMonetizationInterest,
                            onOpenWallet = viewModel::openWallet,
                            onOpenSubscriptions = viewModel::openSubscriptions,
                            onUpdateVideoMonetization = viewModel::updateVideoMonetization,
                            onAdminChangeStatus = viewModel::setMonetizationStatus
                        )
                    }
                    AppDestination.CREATE_GROUP -> {
                        val contacts = uiState.contactsUsers.map { u ->
                            ContactsDiscoverableUser(
                                userId = u.userId,
                                displayName = u.fullName,
                                username = u.username,
                                avatarUrl = u.avatarLocalPath ?: u.avatarCdnUrl
                            )
                        }
                        CreateGroupScreen(
                            contacts = contacts,
                            onBackClick = viewModel::closeCreateGroup,
                            onCreateGroupSubmit = viewModel::createGroup
                        )
                    }
                    AppDestination.GROUP_PROFILE -> {
                        val activeGroup = uiState.activeGroupProfile
                        if (activeGroup != null) {
                            GroupProfileScreen(
                                group = activeGroup,
                                onBackClick = viewModel::closeGroupProfile,
                                onOpenChat = viewModel::closeGroupProfile,
                                onAddMembersClick = {},
                                onToggleMute = {
                                    viewModel.muteMember(activeGroup.groupId, activeGroup.groupId, !activeGroup.isMuted)
                                },
                                onSearchChatClick = {},
                                onShareInviteLink = {},
                                onUpdatePermissions = { newPerms ->
                                    viewModel.updateGroupPermissions(activeGroup.groupId, newPerms)
                                },
                                onPromoteMemberRole = { uid, role ->
                                    viewModel.promoteMemberRole(activeGroup.groupId, uid, role)
                                },
                                onRemoveMember = { uid ->
                                    viewModel.removeMemberFromGroup(activeGroup.groupId, uid)
                                },
                                onMuteMember = { uid, muted ->
                                    viewModel.muteMember(activeGroup.groupId, uid, muted)
                                },
                                onApproveJoinRequest = { reqId, uid ->
                                    viewModel.approveJoinRequest(reqId, activeGroup.groupId, uid)
                                },
                                onRejectJoinRequest = { reqId ->
                                    val req = activeGroup.pendingApprovals.find { it.requestId == reqId }
                                    if (req != null) {
                                        viewModel.rejectJoinRequest(reqId, activeGroup.groupId, req.userId)
                                    }
                                },
                                onLeaveGroup = viewModel::leaveCurrentGroup,
                                onReportGroup = { viewModel.reportGroup(activeGroup.groupId, "Inappropriate content") }
                            )
                        } else {
                            MainHomeScreenRoute(viewModel = viewModel, uiState = uiState)
                        }
                    }
                    AppDestination.MEMORIES -> {
                        MemoriesScreen(
                            memories = uiState.memories,
                            onBackClick = viewModel::closeMemories,
                            onCreateMemory = viewModel::createMemory,
                            onToggleFavorite = viewModel::toggleMemoryFavorite,
                            onDeleteMemory = viewModel::deleteMemory
                        )
                    }
                    AppDestination.CALL -> {
                        val activeCall = uiState.activeCall
                        if (activeCall != null) {
                            if (activeCall.callType == CallType.VIDEO) {
                                VideoCallScreen(
                                    call = activeCall,
                                    localAvatarPath = uiState.savedProfile?.avatarLocalPath,
                                    onMuteToggle = viewModel::toggleCallMute,
                                    onCameraToggle = viewModel::toggleCallCamera,
                                    onSpeakerToggle = viewModel::toggleCallSpeaker,
                                    onCameraFlip = viewModel::flipCallCamera,
                                    onFilterSelect = viewModel::setCallFilter,
                                    onMinimize = { viewModel.minimizeCall(true) },
                                    onEndCall = viewModel::endActiveCall
                                )
                            } else {
                                AudioCallScreen(
                                    call = activeCall,
                                    onMuteToggle = viewModel::toggleCallMute,
                                    onSpeakerToggle = viewModel::toggleCallSpeaker,
                                    onSwitchToVideo = viewModel::switchAudioCallToVideo,
                                    onMinimize = { viewModel.minimizeCall(true) },
                                    onEndCall = viewModel::endActiveCall
                                )
                            }
                        } else {
                            MainHomeScreenRoute(viewModel = viewModel, uiState = uiState)
                        }
                    }
                }

                if (uiState.isStarsTopUpSheetOpen) {
                    val countryIso = uiState.savedProfile?.phoneIsoCode ?: uiState.selectedCountry.isoCode
                    PaymentCheckoutSheet(
                        isTopUpMode = true,
                        userStarsBalance = uiState.starsBalance?.balanceStars ?: 0L,
                        countryIso = countryIso,
                        userPhoneNumber = uiState.savedProfile?.phoneNumber.orEmpty(),
                        onProcessStarsTopUp = { starsAmount, priceAmount, gateway, ref ->
                            viewModel.processStarsTopUp(starsAmount, priceAmount, gateway, ref)
                        },
                        onDismissRequest = { viewModel.closeStarsTopUp() }
                    )
                }
            }

            // Incoming Call Ringing Overlay across entire app
            val incomingCall = uiState.incomingCall
            if (incomingCall != null) {
                IncomingCallOverlay(
                    call = incomingCall,
                    onAccept = viewModel::acceptIncomingCall,
                    onDecline = viewModel::declineIncomingCall
                )
            }

            // Minimized Floating Call Banner
            val currentActiveCall = uiState.activeCall
            if (currentActiveCall != null && currentActiveCall.isMinimized) {
                MinimizedCallBanner(
                    call = currentActiveCall,
                    onExpand = { viewModel.minimizeCall(false) },
                    onEndCall = viewModel::endActiveCall
                )
            }
        }
    }
}

@Composable
private fun MainHomeScreenRoute(
    viewModel: TelefamViewModel,
    uiState: TelefamUiState
) {
    MainHomeScreen(
        uiState = uiState,
        selectedTab = uiState.selectedHomeTab,
        onSelectTab = viewModel::selectHomeTab,
        onOpenPrivacySettings = viewModel::openPrivacySettings,
        onContactsFilterSelected = viewModel::onContactsFilterModeSelected,
        onToggleContactsSearch = viewModel::onToggleContactsSearch,
        onContactsSearchQueryChanged = viewModel::onContactsSearchQueryChanged,
        onContactsPermissionResult = viewModel::onContactsPermissionResult,
        onToggleFollowUser = viewModel::toggleFollowUser,
        onOpenUserProfile = viewModel::openPublicUserProfile,
        onLoadMoreContacts = viewModel::loadMoreContactsPage,
        onRefreshContacts = { viewModel.refreshContactsDirectory(forceDailyRefresh = true) },
        onDismissContactsError = viewModel::clearContactsError,
        onOwnerPullToRefresh = { viewModel.refreshOwnerProfileSnapshot(isPullToRefresh = true) },
        onOwnerSelectSubTab = viewModel::selectOwnerProfileSubTab,
        onOwnerOpenFollowList = viewModel::openOwnerFollowList,
        onOwnerLoadMoreFollowList = viewModel::loadMoreOwnerFollowList,
        onOwnerDismissFollowList = viewModel::dismissOwnerFollowList,
        onOwnerOpenEditProfile = viewModel::openOwnerEditProfileSheet,
        onOwnerDismissEditProfile = viewModel::dismissOwnerEditProfileSheet,
        onOwnerFullNameChanged = viewModel::onFullNameChanged,
        onOwnerUsernameChanged = viewModel::onUsernameChanged,
        onOwnerApplySuggestedUsername = viewModel::onApplySuggestedUsername,
        onOwnerCountrySelected = viewModel::onCountrySelected,
        onOwnerPhoneNumberChanged = viewModel::onPhoneNumberChanged,
        onOwnerGenderSelected = viewModel::onGenderSelected,
        onOwnerDateOfBirthSelected = viewModel::onDateOfBirthSelected,
        onOwnerBioChanged = viewModel::onBioChanged,
        onOwnerAvatarUriPicked = viewModel::onAvatarUriPicked,
        onOwnerCancelCrop = viewModel::cancelImageCrop,
        onOwnerConfirmCrop = viewModel::confirmImageCropAndSanitize,
        onOwnerSaveProfileEdits = viewModel::saveOwnerProfileEdits,
        onOwnerClearStatusBanner = viewModel::clearOwnerStatusBanner,
        onOpenSettingsAndPrivacy = { viewModel.openSettingsAndPrivacy() },
        onOpenOwnerMenu = viewModel::openOwnerMenu,
        onCloseOwnerMenu = viewModel::closeOwnerMenu,
        onOpenDashboard = { viewModel.openDashboard() },
        onCloseDashboard = viewModel::closeDashboard,
        onOpenDashboardAnalytics = viewModel::openDashboardAnalyticsSubScreen,
        onCloseDashboardAnalytics = viewModel::closeDashboardAnalyticsSubScreen,
        onSelectDashboardPeriod = viewModel::selectDashboardPeriod,
        onRefreshDashboard = { viewModel.refreshDashboardData(showLoading = true) },
        onOpenOneToOneChat = viewModel::openOneToOneChatById,
        onChatSearchQueryChanged = viewModel::onChatListSearchQueryChanged,
        onOpenStarsEarnings = viewModel::openStarsEarnings,
        onCloseStarsEarnings = viewModel::closeStarsEarnings,
        onOpenStarsInfoSheet = viewModel::openStarsInfoSheet,
        onCloseStarsInfoSheet = viewModel::closeStarsInfoSheet,
        onSelectStarsSourceFilter = viewModel::setStarsSourceFilter,
        onRefreshStars = { viewModel.refreshStarsData(showLoading = true) },
        onOpenCampaigns = { viewModel.openCampaigns() },
        onCloseCampaigns = viewModel::closeCampaigns,
        onSelectCampaignTab = viewModel::setCampaignTab,
        onSelectCampaignPost = viewModel::selectCampaignPost,
        onSelectCampaignGoal = viewModel::selectCampaignGoal,
        onSetCampaignDestinationUrl = viewModel::setCampaignDestinationUrl,
        onSetCampaignLeadFormTitle = viewModel::setCampaignLeadFormTitle,
        onSetCampaignCtaText = viewModel::setCampaignCtaText,
        onSetCampaignTargetType = viewModel::setCampaignTargetType,
        onSetCampaignTargetGender = viewModel::setCampaignTargetGender,
        onSetCampaignTargetAgeRange = viewModel::setCampaignTargetAgeRange,
        onToggleCampaignLocation = viewModel::toggleCampaignLocation,
        onToggleCampaignInterest = viewModel::toggleCampaignInterest,
        onSelectCampaignTier = viewModel::selectCampaignTier,
        onSetCampaignDurationDays = viewModel::setCampaignDurationDays,
        onLaunchCampaign = { gateway, ref -> viewModel.launchCampaignWithServerVerification(gateway, ref) },
        onPauseCampaign = viewModel::pauseCampaign,
        onResumeCampaign = viewModel::resumeCampaign,
        onDeleteCampaign = viewModel::deleteCampaign,
        onViewCampaignLeads = viewModel::viewCampaignLeads,
        onCloseCampaignLeads = viewModel::closeCampaignLeads,
        onImportPhotosToStudio = viewModel::importPhotosToPostStudio,
        onImportVideoToStudio = viewModel::importVideoToPostStudio,
        onExtractVideoCoverAt = viewModel::extractVideoCoverFrameAt,
        onSearchPostMentions = viewModel::searchPostMentionCandidates,
        onClearPostMentions = viewModel::clearPostMentionCandidates,
        onSearchPostMusic = viewModel::searchFreeMusicForPost,
        onClearPostStudioError = viewModel::clearPostStudioError,
        onPublishStudioPost = viewModel::publishStudioPost,
        onOpenMarketplace = viewModel::openMarketplace,
        onOpenLinkedDevices = viewModel::openLinkedDevices,
        onOpenSubscriptions = viewModel::openSubscriptions,
        onOpenWallet = viewModel::openWallet,
        onOpenMonetization = viewModel::openMonetization,
        onOpenMemories = viewModel::openMemories,
        onOpenCreateGroup = viewModel::openCreateGroup,
        onSelectInboxTab = viewModel::setSelectedInboxTab,
        onInitiateCall = { uid, name, user, avatar, type ->
            if (type == CallType.VIDEO) {
                viewModel.startVideoCall(uid, name, user, avatar)
            } else {
                viewModel.startAudioCall(uid, name, user, avatar)
            }
        },
        onClearCallHistory = viewModel::clearCallHistory
    )
}

@Composable
private fun SettingsAndPrivacyScreenRoute(
    viewModel: TelefamViewModel,
    uiState: TelefamUiState,
    context: android.content.Context
) {
    SettingsAndPrivacyScreen(
        uiState = uiState,
        onBackClick = viewModel::navigateBackInSettings,
        onNavigateSubScreen = viewModel::navigateToSettingsSubScreen,
        onClearBanners = viewModel::clearSettingsBanners,
        onRetryLastAction = viewModel::retryLastFailedSettingsAction,
        onOpenChatPrivacyAndAppearance = viewModel::openChatPrivacyAndAppearance,
        onFullNameChanged = viewModel::onFullNameChanged,
        onUsernameChanged = viewModel::onUsernameChanged,
        onApplySuggestedUsername = viewModel::onApplySuggestedUsername,
        onCountrySelected = viewModel::onCountrySelected,
        onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
        onAvatarUriPicked = viewModel::onAvatarUriPicked,
        onCancelCrop = viewModel::cancelImageCrop,
        onConfirmCrop = viewModel::confirmImageCropAndSanitize,
        onSaveOwnerProfileEdits = viewModel::saveOwnerProfileEdits,
        onSendVerificationOtp = viewModel::sendSettingsVerificationOtp,
        onConfirmChangeEmail = viewModel::confirmChangeAccountEmail,
        onSubmitPasswordChange = viewModel::submitAccountPasswordChange,
        onSaveRecoverySettings = viewModel::saveRecoveryContactsAndRegenerateCodes,
        onConfirmDeactivateAccount = viewModel::confirmDeactivateAccount,
        onConfirmDeleteAccount30Days = viewModel::confirmSchedule30DayAccountDeletion,
        onEnableTwoStep = viewModel::enableTwoStepVerification,
        onDisableTwoStep = viewModel::disableTwoStepVerification,
        onRegisterPasskey = { label -> viewModel.registerPasskeyCredential(context, label) },
        onRevokePasskey = viewModel::revokePasskeyCredential,
        onLogoutDevice = viewModel::logoutDeviceSession,
        onRevokeConnectedApp = viewModel::revokeConnectedApp,
        onOpenPermissionDetail = viewModel::openPermissionDetail,
        onSystemPermissionUpdated = viewModel::onSystemPermissionUpdated,
        onTogglePrivateAccount = viewModel::togglePrivateAccountSetting,
        onOpenPrivacyRuleSelector = viewModel::openPrivacyRuleSelector,
        onSelectPrivacyRule = viewModel::selectSocialPrivacyRule,
        onSetAppThemeMode = viewModel::updateAppThemeOption,
        onUpdateAccountSettings = viewModel::updateAccountSettingsTransform,
        onDownloadQrCode = viewModel::downloadProfileQrCode,
        onRegenerateQrCode = viewModel::regenerateBackendProfileQrCode,
        onOpenPublicUserProfile = viewModel::openPublicUserProfile,
        onBlockedSearchQueryChanged = viewModel::onBlockedAccountsSearchQueryChanged,
        onUnblockAccount = viewModel::unblockAccountFromSettings,
        onSubmitTicket = viewModel::submitProblemReportOrSupportRequest,
        onClearSubmittedTicket = viewModel::clearSubmittedTicketConfirmation,
        onUpdateSupportTicket = viewModel::submitSupportTicketFollowUp,
        onOpenSafetyGuide = viewModel::openSafetyGuideDetail,
        onHelpSearchQueryChanged = viewModel::onHelpSearchQueryChanged,
        onOpenHelpArticle = viewModel::openHelpArticleDetail,
        onSubmitHelpFeedback = viewModel::submitHelpArticleFeedback,
        onOpenLegalDocument = viewModel::openLegalDocumentDetail,
        onAcknowledgeLegalDocument = viewModel::acknowledgeLegalDocument,
        onDownloadLegalDocument = viewModel::downloadLegalDocument,
        onOpenAboutDetail = viewModel::openAboutSectionDetail,
        onConfirmLogOut = viewModel::signOut
    )
}

@Composable
private fun MarketplaceRoute(
    viewModel: TelefamViewModel,
    uiState: TelefamUiState
) {
    val activeProduct = uiState.marketplaceActiveProduct
    if (activeProduct != null) {
        val related = uiState.marketplaceProducts.filter {
            it.id != activeProduct.id && (it.category == activeProduct.category || it.sellerCountryIso == activeProduct.sellerCountryIso)
        }.take(6)
        MarketplaceProductDetailsScreen(
            product = activeProduct,
            relatedProducts = related,
            currentUserId = uiState.activeSession?.userId ?: uiState.savedProfile?.userId ?: "local_telefam_member",
            onBackClick = viewModel::closeMarketplaceProduct,
            onToggleFavorite = viewModel::toggleMarketplaceFavorite,
            onMessageSeller = { prod, prefilled ->
                viewModel.messageSellerWithProduct(prod, prefilled)
            },
            onOpenOtherProduct = viewModel::openMarketplaceProduct,
            onViewSellerProfile = viewModel::openPublicUserProfile,
            onMarkAsSold = viewModel::markMarketplaceProductSold,
            onRenewListing = viewModel::renewMarketplaceProduct,
            onDeleteListing = viewModel::deleteMarketplaceProduct,
            onOpenReportDialog = viewModel::openMarketplaceReportDialog,
            onBlockSeller = viewModel::blockMarketplaceSeller
        )
    } else {
        MarketplaceMainScreen(
            products = uiState.marketplaceProducts,
            selectedTab = uiState.marketplaceSelectedTab,
            selectedCategory = uiState.marketplaceFilterCategory,
            searchQuery = uiState.marketplaceSearchQuery,
            recentSearches = uiState.marketplaceRecentSearches,
            isMarketplaceSuspended = uiState.isMarketplaceSuspended,
            suspensionReason = uiState.marketplaceSuspensionReason,
            onBackClick = viewModel::closeMarketplace,
            onSelectTab = viewModel::selectMarketplaceTab,
            onSelectCategory = viewModel::selectMarketplaceCategory,
            onSearchQueryChanged = viewModel::onMarketplaceSearchQueryChanged,
            onSelectRecentSearch = viewModel::onSelectRecentSearch,
            onClearRecentSearches = viewModel::onClearRecentSearches,
            onOpenProductDetails = viewModel::openMarketplaceProduct,
            onToggleFavorite = viewModel::toggleMarketplaceFavorite,
            onOpenPostSheet = viewModel::openPostMarketplaceSheet
        )
    }

    if (uiState.isMarketplacePostSheetOpen) {
        MarketplacePostStudioSheet(
            defaultCountry = uiState.selectedCountry,
            defaultLocation = uiState.savedProfile?.phoneIsoCode ?: "Nairobi",
            uploadProgress = uiState.marketplaceUploadProgress,
            statusMessage = uiState.marketplaceUploadStatusText,
            onDismiss = viewModel::closePostMarketplaceSheet,
            onSubmitListing = { title, desc, price, curr, cat, cond, specs, del, showLoc, loc, uris ->
                viewModel.submitMarketplaceListing(title, desc, price, curr, cat, cond, specs, del, showLoc, loc, uris)
            }
        )
    }

    val reportingProduct = uiState.activeReportingProduct
    if (reportingProduct != null) {
        MarketplaceReportDialog(
            product = reportingProduct,
            onDismiss = viewModel::closeMarketplaceReportDialog,
            onSubmitReport = { reason, details ->
                viewModel.submitMarketplaceReport(reportingProduct, reason, details)
            }
        )
    }
}

@Composable
private fun OneToOneChatRoute(
    viewModel: TelefamViewModel,
    uiState: TelefamUiState,
    klipyPagingItems: androidx.paging.compose.LazyPagingItems<com.telefam.company.app.domain.KlipyMediaItem>,
    voiceRecordingState: com.telefam.company.app.data.ActiveVoiceRecordingState,
    bubblePlaybackState: com.telefam.company.app.data.ActiveVoiceBubblePlaybackState
) {
    val activeChat = uiState.activeChatItem
        ?: uiState.chatListItems.firstOrNull { it.chatId == uiState.activeChatId }
    val ownerUserId = uiState.activeSession?.userId
        ?: uiState.savedProfile?.userId
        ?: "local_telefam_member"
    if (activeChat != null) {
        OneToOneChatScreen(
            ownerUserId = ownerUserId,
            chatItem = activeChat,
            messages = uiState.activeChatMessages,
            attachments = uiState.activeChatAttachments,
            privacySettings = uiState.privacySettings,
            inputText = uiState.chatInputText,
            replyingToMessage = uiState.replyingToMessage,
            editingMessage = uiState.editingMessage,
            selectedMessageIds = uiState.selectedMessageIds,
            isSelectModeActive = uiState.isSelectModeActive,
            voiceRecordingState = voiceRecordingState,
            bubblePlaybackState = bubblePlaybackState,
            activeKlipyTab = uiState.activeKlipyTab,
            klipySearchQuery = uiState.klipySearchQuery,
            klipyPagingItems = klipyPagingItems,
            klipyRecentItems = uiState.klipyRecentItems,
            previewKlipyItem = uiState.previewKlipyItem,
            e2eeSnapshot = uiState.activeE2eeVerification,
            forwardCandidates = uiState.forwardCandidates,
            forwardSearchQuery = uiState.forwardSearchQuery,
            selectedForwardUserIds = uiState.selectedForwardUserIds,
            isForwardPickerOpen = uiState.isForwardPickerOpen,
            onBackClick = viewModel::closeActiveOneToOneChat,
            onOpenPeerProfile = viewModel::openPublicUserProfile,
            onInputTextChanged = viewModel::onChatInputTextChanged,
            onSendTextOrEdit = viewModel::sendCurrentTextOrSaveEdit,
            onSendKlipyMedia = viewModel::confirmSendKlipyMediaItem,
            onSelectKlipyTab = viewModel::onSelectKlipyTab,
            onKlipySearchQueryChanged = viewModel::onKlipySearchQueryChanged,
            onPreviewKlipyItem = viewModel::onOpenKlipyItemPreview,
            onStartVoiceRecording = viewModel::startVoiceNoteHoldRecording,
            onStopVoiceRecordingToPreview = viewModel::stopVoiceNoteHoldRecording,
            onToggleVoicePreviewPlayback = viewModel::toggleRecordedVoicePreviewPlay,
            onToggleVoiceViewOnce = viewModel::toggleRecordedVoiceNoteViewOnce,
            onCancelVoiceRecording = viewModel::discardRecordedVoiceNote,
            onSendPreviewedVoiceNote = viewModel::sendRecordedVoiceNote,
            onToggleBubbleVoicePlayback = viewModel::toggleBubbleVoiceNotePlayback,
            onStartReply = viewModel::onSwipeOrSelectReplyMessage,
            onCancelReplyOrEdit = viewModel::onCancelReplyOrEditBanner,
            onStartEdit = viewModel::onStartEditingMessage,
            onToggleReaction = { msgId, choice ->
                uiState.activeChatMessages.find { it.messageId == msgId }?.let { msg ->
                    viewModel.reactToMessage(msg, choice)
                }
            },
            onTogglePin = { msgId ->
                uiState.activeChatMessages.find { it.messageId == msgId }?.let { msg ->
                    viewModel.togglePinMessage(msg)
                }
            },
            onToggleFavourite = { msgId ->
                uiState.activeChatMessages.find { it.messageId == msgId }?.let { msg ->
                    viewModel.toggleFavouriteMessage(msg)
                }
            },
            onUnsendMessage = { msgId ->
                uiState.activeChatMessages.find { it.messageId == msgId }?.let { msg ->
                    viewModel.unsendMessageForBothDevices(msg)
                }
            },
            onDeleteSingleMessageForMe = { msgId ->
                uiState.activeChatMessages.find { it.messageId == msgId }?.let { msg ->
                    viewModel.deleteSingleMessage(msg)
                }
            },
            onEnterSelectMode = viewModel::enterMessageSelectMode,
            onToggleSelectMessage = viewModel::toggleSelectedMessageId,
            onSelectAllMessages = viewModel::toggleMarkAllMessagesInSelectMode,
            onExitSelectMode = viewModel::exitMessageSelectMode,
            onDeleteSelectedMessages = viewModel::deleteSelectedMessagesInSelectMode,
            onOpenForwardForSingleMessage = { msg ->
                viewModel.openForwardContactsPicker(listOf(msg))
            },
            onOpenForwardForSelectedMessages = {
                val selected = uiState.activeChatMessages.filter { it.messageId in uiState.selectedMessageIds }
                viewModel.openForwardContactsPicker(selected)
            },
            onForwardSearchQueryChanged = viewModel::onForwardSearchQueryChanged,
            onToggleForwardRecipient = viewModel::toggleForwardRecipientSelection,
            onConfirmForwardSelection = viewModel::confirmForwardSelectedMessages,
            onDismissForwardPicker = viewModel::dismissForwardContactsPicker,
            onSelectDisappearingOption = viewModel::setActiveChatDisappearingOption,
            onClearChat = viewModel::clearActiveChatHistory,
            onToggleBlockPeer = viewModel::toggleBlockActiveChatUser,
            onSubmitReportPeer = viewModel::submitReportForActiveChatUser,
            onOpenE2eeVerificationScreen = viewModel::openActiveChatE2eeVerificationScreen,
            onSetE2eeVerified = viewModel::setActiveChatE2eeVerified,
            onSendMediaAttachments = viewModel::sendMediaAttachmentItems,
            onSendLocationAttachment = viewModel::sendLocationAttachment,
            onSendContactAttachment = viewModel::sendContactAttachment,
            onSendDocumentAttachment = viewModel::sendDocumentAttachment,
            onSendPollAttachment = viewModel::sendPollAttachment,
            onSendEventAttachment = viewModel::sendEventAttachment,
            onVotePollMessage = viewModel::voteOnPollMessage,
            onRsvpEventMessage = viewModel::rsvpToEventMessage,
            onConsumeViewOnceMessage = viewModel::consumeViewOnceMessage,
            pendingMarketplaceProduct = uiState.chatPendingMarketplaceProduct,
            onDismissPendingMarketplaceProduct = viewModel::clearChatPendingMarketplaceProduct,
            onOpenMarketplaceProduct = viewModel::openMarketplaceProductById,
            onOpenGroupProfile = viewModel::openGroupProfile,
            onVoiceCallClick = {
                viewModel.startAudioCall(
                    peerUserId = activeChat.peerUserId,
                    peerFullName = activeChat.peerFullName,
                    peerUsername = activeChat.peerUsername,
                    peerAvatarUrl = activeChat.peerAvatarUrl
                )
            },
            onVideoCallClick = {
                viewModel.startVideoCall(
                    peerUserId = activeChat.peerUserId,
                    peerFullName = activeChat.peerFullName,
                    peerUsername = activeChat.peerUsername,
                    peerAvatarUrl = activeChat.peerAvatarUrl
                )
            },
            callPrivacyNoticeBanner = uiState.callPrivacyNoticeBanner,
            onDismissCallPrivacyBanner = viewModel::clearCallPrivacyBanner
        )
    } else {
        MainHomeScreenRoute(
            viewModel = viewModel,
            uiState = uiState
        )
    }
}

@Composable
private fun LinkedDevicesRoute(
    viewModel: TelefamViewModel,
    uiState: TelefamUiState
) {
    LinkedDevicesScreen(
        uiState = uiState,
        onBackClick = viewModel::closeLinkedDevices,
        onSelectTab = viewModel::selectLinkedDevicesTab,
        onGeneratePairingSession = viewModel::generateNewPairingSession,
        onLinkCodeChanged = viewModel::onLinkCodeInputChanged,
        onSubmitLinkCode = viewModel::submitLinkCodeOrQr,
        onApprovePendingRequest = viewModel::approvePendingDeviceLink,
        onRejectPendingRequest = viewModel::rejectPendingDeviceLink,
        onRevokeDevice = viewModel::revokeLinkedDevice,
        onRevokeAllOtherDevices = viewModel::revokeAllOtherLinkedDevices,
        onClearErrorBanner = viewModel::clearLinkDeviceErrorBanner,
        onClearSuccessBanner = viewModel::clearLinkDeviceSuccessBanner
    )
}
