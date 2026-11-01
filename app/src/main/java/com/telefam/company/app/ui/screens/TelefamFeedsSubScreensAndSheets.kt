package com.telefam.company.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.TelefamRedBright
import com.telefam.company.app.domain.FeedAudioTrackInfo
import com.telefam.company.app.domain.FeedHashtagSummaryItem
import com.telefam.company.app.domain.FeedSearchCombinedResults
import com.telefam.company.app.domain.FeedSearchTab
import com.telefam.company.app.domain.FeedTopTab
import com.telefam.company.app.domain.FeedVideoItem
import com.telefam.company.app.domain.NewCreatorUserItem
import com.telefam.company.app.domain.ReportLocationOption
import com.telefam.company.app.domain.ReportReasonOption
import com.telefam.company.app.domain.ReportUrgencyOption
import com.telefam.company.app.domain.ReusableFeedContextMode
import com.telefam.company.app.ui.components.LucideIconKind
import com.telefam.company.app.ui.components.TelefamLucideIcon
import java.io.File

/**
 * Complete Production TikTok-Style Feeds Screen matching the attached reference image:
 * - Dark theme by default (`#000000` background).
 * - Top Bar: `For You` (default, red underline active), `Friends`, `Following` (with red dot indicator for new posts),
 *   and right Search icon.
 * - Below tabs: `New creators` pill button with icon + `>` arrow -> Opens New Creators list screen.
 * - Full-screen vertical video pager supporting ALL aspect ratios with `ContentScale.Fit`.
 * - Audio Page, Hashtag Page, Search Screen, Share Bottom Sheet (including `Share to Inbox` DM),
 *   and 3-Dots Bottom Sheet (Block, Multi-step Report Quiz, Download, Reshare, Not Interested, Owner Delete & Edit).
 */
@Composable
fun TelefamTikTokFeedsScreen(
    currentUserId: String,
    activeTopTab: FeedTopTab,
    feedVideos: List<FeedVideoItem>,
    hasUnreadFollowingDot: Boolean,
    isFeedTabVisible: Boolean,
    newCreatorsList: List<NewCreatorUserItem>,
    searchResults: FeedSearchCombinedResults,
    activeAudioTrack: FeedAudioTrackInfo?,
    activeAudioVideos: List<FeedVideoItem>,
    activeHashtagTitle: String?,
    activeHashtagVideos: List<FeedVideoItem>,
    focusedVideoFromInbox: FeedVideoItem?,
    statusFeedbackMessage: String?,
    onSelectTopTab: (FeedTopTab) -> Unit,
    onLoadMoreVideos: () -> Unit,
    onRefreshNewCreators: () -> Unit,
    onExecuteSearch: (String) -> Unit,
    onToggleFollowCreatorByUserId: (String) -> Unit,
    onToggleLikeVideo: (FeedVideoItem, Boolean) -> Unit,
    onToggleSaveVideo: (FeedVideoItem) -> Unit,
    onRecordVideoShare: (FeedVideoItem, String, String?) -> Unit,
    onShareVideoToInboxDm: (FeedVideoItem, NewCreatorUserItem) -> Unit,
    onRecordVideoView: (videoId: String, watchDurationMs: Long, completedLoop: Boolean) -> Unit,
    onMarkVideoNotInterested: (FeedVideoItem) -> Unit,
    onBlockCreatorFromFeed: (FeedVideoItem) -> Unit,
    onSubmitVideoReportQuiz: (
        video: FeedVideoItem,
        reason: ReportReasonOption,
        location: ReportLocationOption,
        urgency: ReportUrgencyOption,
        details: String
    ) -> Unit,
    onDownloadVideoToDevice: (FeedVideoItem) -> Unit,
    onDeleteOwnedVideo: (FeedVideoItem) -> Unit,
    onEditOwnedVideoInStudio: (FeedVideoItem) -> Unit,
    onOpenAudioPage: (FeedAudioTrackInfo) -> Unit,
    onCloseAudioPage: () -> Unit,
    onUseAudioInPostStudio: (FeedAudioTrackInfo) -> Unit,
    onOpenHashtagPage: (String) -> Unit,
    onCloseHashtagPage: () -> Unit,
    onOpenCreatorProfile: (String) -> Unit,
    onOpenCreatePostStudio: () -> Unit,
    onClearFocusedInboxVideo: () -> Unit,
    onDismissStatusFeedback: () -> Unit,
    onOpenComments: (FeedVideoItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showNewCreatorsScreen by remember { mutableStateOf(false) }
    var showSearchScreen by remember { mutableStateOf(false) }
    var shareSheetTargetVideo by remember { mutableStateOf<FeedVideoItem?>(null) }
    var moreOptionsTargetVideo by remember { mutableStateOf<FeedVideoItem?>(null) }
    var reportQuizTargetVideo by remember { mutableStateOf<FeedVideoItem?>(null) }

    // Overlay vertical feed when user taps a video tile inside Audio Page, Hashtag Page, or Search Results
    var overlayFeedVideos by remember { mutableStateOf<List<FeedVideoItem>>(emptyList()) }
    var overlayFeedInitialIndex by remember { mutableIntStateOf(0) }
    var overlayFeedTitle by remember { mutableStateOf<String?>(null) }
    var overlayFeedMode by remember { mutableStateOf<ReusableFeedContextMode?>(null) }

    val effectiveVideos = remember(focusedVideoFromInbox, feedVideos) {
        if (focusedVideoFromInbox != null) {
            listOf(focusedVideoFromInbox) + feedVideos.filter { it.videoId != focusedVideoFromInbox.videoId }
        } else {
            feedVideos
        }
    }

    val isMainPagerPlaying = isFeedTabVisible &&
        !showNewCreatorsScreen &&
        !showSearchScreen &&
        activeAudioTrack == null &&
        activeHashtagTitle == null &&
        overlayFeedMode == null

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("tiktok_feeds_root_screen")
    ) {
        // 1. MAIN VERTICAL VIDEO PAGER OR CLEAN REAL EMPTY STATE
        if (effectiveVideos.isNotEmpty()) {
            TelefamReusableVerticalVideoFeed(
                videos = effectiveVideos,
                currentUserId = currentUserId,
                initialVideoIndex = 0,
                isFeedVisibleAndActive = isMainPagerPlaying,
                contextMode = ReusableFeedContextMode.MAIN_FEEDS,
                topReservedPaddingDp = 96,
                bottomReservedPaddingDp = 8,
                onLoadMoreVideos = onLoadMoreVideos,
                onToggleFollowCreator = { video -> onToggleFollowCreatorByUserId(video.ownerUserId) },
                onToggleLikeVideo = onToggleLikeVideo,
                onToggleSaveVideo = onToggleSaveVideo,
                onOpenComments = onOpenComments,
                onOpenShareSheet = { video ->
                    onRefreshNewCreators()
                    shareSheetTargetVideo = video
                },
                onOpenMoreOptionsSheet = { video -> moreOptionsTargetVideo = video },
                onOpenAudioPage = onOpenAudioPage,
                onOpenCreatorProfile = onOpenCreatorProfile,
                onOpenHashtagPage = onOpenHashtagPage,
                onRecordVideoView = onRecordVideoView
            )
        } else {
            RealEmptyFeedsStateView(
                activeTab = activeTopTab,
                onOpenCreatePostStudio = onOpenCreatePostStudio,
                onOpenNewCreators = {
                    onRefreshNewCreators()
                    showNewCreatorsScreen = true
                }
            )
        }

        // 2. TOP BAR OVERLAY (Exact match to attached reference image: For You | Friends | Following + Search + New creators pill)
        FeedsTopBarAndNewCreatorsPill(
            activeTab = activeTopTab,
            hasUnreadFollowingDot = hasUnreadFollowingDot,
            focusedVideoFromInbox = focusedVideoFromInbox,
            onSelectTab = { tab ->
                if (focusedVideoFromInbox != null) onClearFocusedInboxVideo()
                onSelectTopTab(tab)
            },
            onOpenSearch = {
                onExecuteSearch("")
                showSearchScreen = true
            },
            onOpenNewCreators = {
                onRefreshNewCreators()
                showNewCreatorsScreen = true
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Non-blocking feedback pill (e.g. "Link copied", "Sent to Inbox", "Saved")
        AnimatedVisibility(
            visible = !statusFeedbackMessage.isNullOrBlank(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Surface(
                onClick = onDismissStatusFeedback,
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1A1A22).copy(alpha = 0.94f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
                modifier = Modifier.testTag("feed_transient_feedback_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.CIRCLE_CHECK_DELIVERED,
                        tint = Color(0xFFFE2C55),
                        size = 18.dp,
                        filled = true
                    )
                    Text(
                        text = statusFeedbackMessage.orEmpty(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 3. NEW CREATORS FULL SCREEN (Opened from "New creators >" pill button)
        if (showNewCreatorsScreen) {
            NewCreatorsDirectoryScreen(
                creators = newCreatorsList,
                onBack = { showNewCreatorsScreen = false },
                onToggleFollow = onToggleFollowCreatorByUserId,
                onOpenProfile = { userId ->
                    showNewCreatorsScreen = false
                    onOpenCreatorProfile(userId)
                }
            )
        }

        // 4. AUDIO PAGE FULL SCREEN (Opened when tapping bottom-left Original Sound / Audio pill)
        if (activeAudioTrack != null) {
            FeedAudioPageScreen(
                audio = activeAudioTrack,
                videos = activeAudioVideos,
                onBack = onCloseAudioPage,
                onUseThisSound = {
                    val sound = activeAudioTrack
                    onCloseAudioPage()
                    onUseAudioInPostStudio(sound)
                },
                onOpenCreatorProfile = { userId ->
                    onCloseAudioPage()
                    onOpenCreatorProfile(userId)
                },
                onSelectVideoAt = { index ->
                    overlayFeedVideos = activeAudioVideos
                    overlayFeedInitialIndex = index
                    overlayFeedTitle = activeAudioTrack.title
                    overlayFeedMode = ReusableFeedContextMode.AUDIO_PAGE_FEED
                }
            )
        }

        // 5. HASHTAG PAGE FULL SCREEN
        if (!activeHashtagTitle.isNullOrBlank()) {
            FeedHashtagPageScreen(
                hashtag = activeHashtagTitle,
                videos = activeHashtagVideos,
                onBack = onCloseHashtagPage,
                onSelectVideoAt = { index ->
                    overlayFeedVideos = activeHashtagVideos
                    overlayFeedInitialIndex = index
                    overlayFeedTitle = activeHashtagTitle
                    overlayFeedMode = ReusableFeedContextMode.HASHTAG_PAGE_FEED
                }
            )
        }

        // 6. FULL-TEXT SEARCH SCREEN (Videos, Users, Hashtags, Sounds)
        if (showSearchScreen) {
            FeedFullTextSearchScreen(
                results = searchResults,
                onQueryChange = onExecuteSearch,
                onBack = { showSearchScreen = false },
                onSelectVideoAt = { list, index ->
                    overlayFeedVideos = list
                    overlayFeedInitialIndex = index
                    overlayFeedTitle = "Search: ${searchResults.query.ifBlank { "Videos" }}"
                    overlayFeedMode = ReusableFeedContextMode.SEARCH_VIDEO_FEED
                },
                onOpenUserProfile = { userId ->
                    showSearchScreen = false
                    onOpenCreatorProfile(userId)
                },
                onToggleFollowUser = onToggleFollowCreatorByUserId,
                onOpenHashtag = { tag ->
                    showSearchScreen = false
                    onOpenHashtagPage(tag)
                },
                onOpenSound = { audio ->
                    showSearchScreen = false
                    onOpenAudioPage(audio)
                }
            )
        }

        // 7. REUSABLE VERTICAL VIDEO FEED OVERLAY (for Audio Page, Hashtag Page, or Search Results video playback)
        if (overlayFeedMode != null && overlayFeedVideos.isNotEmpty()) {
            BackHandler {
                overlayFeedMode = null
            }
            TelefamReusableVerticalVideoFeed(
                videos = overlayFeedVideos,
                currentUserId = currentUserId,
                initialVideoIndex = overlayFeedInitialIndex,
                isFeedVisibleAndActive = true,
                contextMode = overlayFeedMode ?: ReusableFeedContextMode.SEARCH_VIDEO_FEED,
                topReservedPaddingDp = 68,
                bottomReservedPaddingDp = 12,
                onToggleFollowCreator = { v -> onToggleFollowCreatorByUserId(v.ownerUserId) },
                onToggleLikeVideo = onToggleLikeVideo,
                onToggleSaveVideo = onToggleSaveVideo,
                onOpenComments = onOpenComments,
                onOpenShareSheet = { v ->
                    onRefreshNewCreators()
                    shareSheetTargetVideo = v
                },
                onOpenMoreOptionsSheet = { v -> moreOptionsTargetVideo = v },
                onOpenAudioPage = { audio ->
                    overlayFeedMode = null
                    onOpenAudioPage(audio)
                },
                onOpenCreatorProfile = { uid ->
                    overlayFeedMode = null
                    onOpenCreatorProfile(uid)
                },
                onOpenHashtagPage = { tag ->
                    overlayFeedMode = null
                    onOpenHashtagPage(tag)
                },
                onRecordVideoView = onRecordVideoView,
                onCloseOverlayFeed = { overlayFeedMode = null },
                overlayHeaderTitle = overlayFeedTitle
            )
        }
    }

    // 8. SHARE BOTTOM SHEET (Share to system apps, Copy Link, Share to Inbox DM, Save)
    shareSheetTargetVideo?.let { targetVideo ->
        FeedShareBottomSheet(
            video = targetVideo,
            inboxRecipients = newCreatorsList,
            onDismiss = { shareSheetTargetVideo = null },
            onShareToSystem = {
                onRecordVideoShare(targetVideo, "SYSTEM", null)
                shareSheetTargetVideo = null
            },
            onCopyLink = {
                onRecordVideoShare(targetVideo, "COPY_LINK", null)
                shareSheetTargetVideo = null
            },
            onSendToInboxUser = { recipient ->
                onShareVideoToInboxDm(targetVideo, recipient)
                shareSheetTargetVideo = null
            },
            onSaveVideo = {
                onToggleSaveVideo(targetVideo)
                shareSheetTargetVideo = null
            }
        )
    }

    // 9. 3-DOTS MORE OPTIONS BOTTOM SHEET (Block user, Report quiz, Download, Reshare, Not Interested, Owner Delete/Edit)
    moreOptionsTargetVideo?.let { targetVideo ->
        FeedMoreOptionsBottomSheet(
            video = targetVideo,
            isOwner = targetVideo.ownerUserId == currentUserId,
            onDismiss = { moreOptionsTargetVideo = null },
            onNotInterested = {
                onMarkVideoNotInterested(targetVideo)
                moreOptionsTargetVideo = null
            },
            onReshareRepost = {
                onRecordVideoShare(targetVideo, "RESHARE", null)
                moreOptionsTargetVideo = null
            },
            onDownloadVideo = {
                onDownloadVideoToDevice(targetVideo)
                moreOptionsTargetVideo = null
            },
            onBlockCreator = {
                onBlockCreatorFromFeed(targetVideo)
                moreOptionsTargetVideo = null
            },
            onOpenReportQuiz = {
                moreOptionsTargetVideo = null
                reportQuizTargetVideo = targetVideo
            },
            onDeleteOwnedVideo = {
                onDeleteOwnedVideo(targetVideo)
                moreOptionsTargetVideo = null
            },
            onEditOwnedVideo = {
                val v = targetVideo
                moreOptionsTargetVideo = null
                onEditOwnedVideoInStudio(v)
            }
        )
    }

    // 10. MULTI-STEP REPORT QUIZ BOTTOM SHEET
    reportQuizTargetVideo?.let { targetVideo ->
        FeedMultiStepReportQuizSheet(
            video = targetVideo,
            onDismiss = { reportQuizTargetVideo = null },
            onSubmitQuiz = { reason, location, urgency, details ->
                onSubmitVideoReportQuiz(targetVideo, reason, location, urgency, details)
                reportQuizTargetVideo = null
            }
        )
    }
}

/**
 * Top Bar matching the attached reference image:
 * - 3 centered tabs: `For You` (red underline active), `Friends`, `Following` (with small red dot indicator)
 * - Right side: Search icon
 * - Below tabs: `New creators` pill button with icon + `>` arrow
 */
@Composable
private fun FeedsTopBarAndNewCreatorsPill(
    activeTab: FeedTopTab,
    hasUnreadFollowingDot: Boolean,
    focusedVideoFromInbox: FeedVideoItem?,
    onSelectTab: (FeedTopTab) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenNewCreators: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.88f),
                        Color.Black.copy(alpha = 0.45f),
                        Color.Transparent
                    )
                )
            )
            .statusBarsPadding()
            .padding(top = 4.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left spacer matching the right search button width to keep tabs centered without overlap
            Spacer(modifier = Modifier.size(48.dp))

            // Centered 3 Tabs: For You | Friends | Following
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeedTopTab.entries.forEach { tab ->
                    val isSelected = activeTab == tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectTab(tab) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("feed_top_tab_${tab.wireCode.lowercase()}")
                    ) {
                        Box {
                            Text(
                                text = tab.label,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.62f),
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                            )
                            // Small red dot indicator on Following for new posts (exact match to reference image)
                            if (tab == FeedTopTab.FOLLOWING && hasUnreadFollowingDot) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .align(Alignment.TopEnd)
                                        .padding(start = 2.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFE2C55))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        // Red underline under active tab
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 28.dp else 0.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) Color(0xFFFE2C55) else Color.Transparent)
                        )
                    }
                }
            }

            // Right Search Icon (fixed 48dp touch target, guaranteed zero overlap)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onOpenSearch)
                    .testTag("feed_top_search_button"),
                contentAlignment = Alignment.Center
            ) {
                TelefamLucideIcon(
                    kind = LucideIconKind.SEARCH,
                    tint = Color.White,
                    size = 22.dp,
                    strokeWidthDp = 2.2.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Below tabs: "New creators >" pill button (Exact match to attached reference image)
        Surface(
            onClick = onOpenNewCreators,
            shape = RoundedCornerShape(50),
            color = Color(0xFF23232B).copy(alpha = 0.88f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
            modifier = Modifier.testTag("feed_new_creators_pill_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TelefamLucideIcon(
                    kind = LucideIconKind.SPARKLES_STUDIO,
                    tint = Color(0xFFFFCA28),
                    size = 14.dp
                )
                Text(
                    text = if (focusedVideoFromInbox != null) "Shared from Inbox · New creators" else "New creators",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                TelefamLucideIcon(
                    kind = LucideIconKind.CHEVRON_RIGHT,
                    tint = Color.White.copy(alpha = 0.85f),
                    size = 14.dp
                )
            }
        }
    }
}

@Composable
private fun RealEmptyFeedsStateView(
    activeTab: FeedTopTab,
    onOpenCreatePostStudio: () -> Unit,
    onOpenNewCreators: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF1A1A22),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.CLIP_VIDEO,
                        tint = Color(0xFFFE2C55),
                        size = 32.dp
                    )
                }
            }
            Text(
                text = when (activeTab) {
                    FeedTopTab.FOR_YOU -> "No published videos yet"
                    FeedTopTab.FRIENDS -> "No friend videos yet"
                    FeedTopTab.FOLLOWING -> "No videos from accounts you follow"
                },
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = when (activeTab) {
                    FeedTopTab.FOR_YOU -> "Publish your first video, photo, or text post in the Post Studio or discover new creators."
                    FeedTopTab.FRIENDS -> "Follow creators who follow you back to watch mutual friends' posts here."
                    FeedTopTab.FOLLOWING -> "Follow creators from New creators to see their latest posts in Following."
                },
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onOpenCreatePostStudio,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFE2C55),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("empty_feed_create_post_button")
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.PLUS,
                        tint = Color.White,
                        size = 16.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Post", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onOpenNewCreators,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("empty_feed_new_creators_button")
                ) {
                    Text("New creators", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/**
 * Share Bottom Sheet matching prompt requirements:
 * - `Share to` (opens installed apps via system share sheet)
 * - `Copy Link`
 * - `Share to Inbox` (searchable list of users to send video to via DM with clickable video thumbnail preview)
 * - `Save`
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedShareBottomSheet(
    video: FeedVideoItem,
    inboxRecipients: List<NewCreatorUserItem>,
    onDismiss: () -> Unit,
    onShareToSystem: () -> Unit,
    onCopyLink: () -> Unit,
    onSendToInboxUser: (NewCreatorUserItem) -> Unit,
    onSaveVideo: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var inboxFilterQuery by remember { mutableStateOf("") }

    val deepLinkUrl = "https://telefam.company/video/${video.videoId}"
    val filteredUsers = remember(inboxRecipients, inboxFilterQuery) {
        val q = inboxFilterQuery.trim().lowercase()
        if (q.isBlank()) inboxRecipients
        else inboxRecipients.filter {
            it.fullName.lowercase().contains(q) || it.username.lowercase().contains(q)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF16161D),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Share video",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // 1. SHARE TO INBOX (Searchable list of real users to send video to via DM)
            Text(
                text = "Share to Inbox",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = inboxFilterQuery,
                onValueChange = { inboxFilterQuery = it },
                placeholder = { Text("Search users to send via DM...", color = Color.White.copy(alpha = 0.45f), fontSize = 13.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFE2C55),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.18f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("share_inbox_user_search_input")
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filteredUsers, key = { it.userId }) { user ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSendToInboxUser(user) }
                            .padding(vertical = 6.dp)
                            .testTag("share_to_inbox_user_${user.userId}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFE2C55), Color(0xFF3A101B))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.fullName.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = user.fullName.substringBefore(" "),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Send",
                            color = Color(0xFFFE2C55),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

            // 2. PRIMARY SHARE ACTIONS ROW: Share to, Copy Link, Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShareSheetActionCircleItem(
                    icon = LucideIconKind.EXTERNAL_LINK,
                    label = "Share to",
                    circleColor = Color(0xFF262632),
                    testTag = "share_sheet_system_share_button",
                    onClick = {
                        runCatching {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Watch @${video.ownerUsername}'s video on Telefam: $deepLinkUrl"
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(sendIntent, "Share video to")
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                        onShareToSystem()
                    }
                )

                ShareSheetActionCircleItem(
                    icon = LucideIconKind.LINK_CHAIN,
                    label = "Copy Link",
                    circleColor = Color(0xFF262632),
                    testTag = "share_sheet_copy_link_button",
                    onClick = {
                        runCatching {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            cm?.setPrimaryClip(ClipData.newPlainText("Telefam Video Link", deepLinkUrl))
                        }
                        onCopyLink()
                    }
                )

                ShareSheetActionCircleItem(
                    icon = LucideIconKind.BOOKMARK_SAVE,
                    label = if (video.isSavedByMe) "Saved" else "Save",
                    circleColor = if (video.isSavedByMe) Color(0xFFFE2C55) else Color(0xFF262632),
                    testTag = "share_sheet_save_button",
                    onClick = onSaveVideo
                )
            }
        }
    }
}

@Composable
private fun ShareSheetActionCircleItem(
    icon: LucideIconKind,
    label: String,
    circleColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Surface(
            shape = CircleShape,
            color = circleColor,
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                TelefamLucideIcon(
                    kind = icon,
                    tint = Color.White,
                    size = 22.dp
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 3-Dots Bottom Sheet Menu matching prompt requirements:
 * - `Block user`
 * - `Report` (opens multi-step quiz questions + submit to backend)
 * - `Download` (if privacy setting `allowDownload` is restricted, hide this option!)
 * - `Reshare/Repost`
 * - `Not Interested`
 * - For owner: `Delete`, `Edit` (edit opens post screen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedMoreOptionsBottomSheet(
    video: FeedVideoItem,
    isOwner: Boolean,
    onDismiss: () -> Unit,
    onNotInterested: () -> Unit,
    onReshareRepost: () -> Unit,
    onDownloadVideo: () -> Unit,
    onBlockCreator: () -> Unit,
    onOpenReportQuiz: () -> Unit,
    onDeleteOwnedVideo: () -> Unit,
    onEditOwnedVideo: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF16161D),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Video options",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )

            // 1. Reshare / Repost
            MoreOptionRowItem(
                icon = LucideIconKind.REPEAT_RESHARE,
                title = "Reshare / Repost",
                subtitle = "Repost this video to your followers",
                testTag = "more_sheet_reshare_button",
                onClick = onReshareRepost
            )

            // 2. Download (Hidden automatically if privacy setting `allowDownload` is false)
            if (video.allowDownload) {
                MoreOptionRowItem(
                    icon = LucideIconKind.DOWNLOAD,
                    title = "Download",
                    subtitle = "Save verified video to device storage",
                    testTag = "more_sheet_download_button",
                    onClick = onDownloadVideo
                )
            }

            // 3. Not Interested
            MoreOptionRowItem(
                icon = LucideIconKind.EYE_OFF_NOT_INTERESTED,
                title = "Not Interested",
                subtitle = "Show fewer videos like this in For You",
                testTag = "more_sheet_not_interested_button",
                onClick = onNotInterested
            )

            // 4. Owner-only actions: Edit & Delete
            if (isOwner) {
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                MoreOptionRowItem(
                    icon = LucideIconKind.PENCIL_EDIT,
                    title = "Edit",
                    subtitle = "Open in Post Creation Studio to edit",
                    testTag = "more_sheet_owner_edit_button",
                    onClick = onEditOwnedVideo
                )
                MoreOptionRowItem(
                    icon = LucideIconKind.TRASH,
                    title = "Delete",
                    subtitle = "Permanently remove this post",
                    tint = Color(0xFFFE2C55),
                    testTag = "more_sheet_owner_delete_button",
                    onClick = onDeleteOwnedVideo
                )
            } else {
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                // 5. Report (with multi-step quiz questions + submit to backend)
                MoreOptionRowItem(
                    icon = LucideIconKind.FLAG_REPORT,
                    title = "Report",
                    subtitle = "Complete safety quiz and submit report",
                    tint = Color(0xFFFFB300),
                    testTag = "more_sheet_report_button",
                    onClick = onOpenReportQuiz
                )
                // 6. Block user
                MoreOptionRowItem(
                    icon = LucideIconKind.BAN_BLOCK,
                    title = "Block user (@${video.ownerUsername})",
                    subtitle = "Hide all videos and messages from this user",
                    tint = Color(0xFFFE2C55),
                    testTag = "more_sheet_block_user_button",
                    onClick = onBlockCreator
                )
            }
        }
    }
}

@Composable
private fun MoreOptionRowItem(
    icon: LucideIconKind,
    title: String,
    subtitle: String,
    tint: Color = Color.White,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF20202A),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TelefamLucideIcon(
                kind = icon,
                tint = tint,
                size = 21.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = tint,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.58f),
                    fontSize = 12.sp
                )
            }
            TelefamLucideIcon(
                kind = LucideIconKind.CHEVRON_RIGHT,
                tint = Color.White.copy(alpha = 0.4f),
                size = 16.dp
            )
        }
    }
}

/**
 * Multi-step Report Quiz Bottom Sheet (3 structured steps + backend submission).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedMultiStepReportQuizSheet(
    video: FeedVideoItem,
    onDismiss: () -> Unit,
    onSubmitQuiz: (
        reason: ReportReasonOption,
        location: ReportLocationOption,
        urgency: ReportUrgencyOption,
        details: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var step by remember { mutableIntStateOf(1) }
    var selectedReason by remember { mutableStateOf(ReportReasonOption.SPAM_OR_BOT) }
    var selectedLocation by remember { mutableStateOf(ReportLocationOption.POSTS_OR_MEDIA) }
    var selectedUrgency by remember { mutableStateOf(ReportUrgencyOption.STANDARD_REVIEW) }
    var additionalNotes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF16161D),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Report Video · Step $step of 3",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            when (step) {
                1 -> {
                    Text(
                        text = "1. What is the primary issue with @${video.ownerUsername}'s video?",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    ReportReasonOption.entries.forEach { reason ->
                        val selected = selectedReason == reason
                        Surface(
                            onClick = { selectedReason = reason },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) Color(0xFFFE2C55).copy(alpha = 0.18f) else Color(0xFF22222C),
                            border = BorderStroke(
                                1.dp,
                                if (selected) Color(0xFFFE2C55) else Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(reason.label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(reason.wireCode, color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
                            }
                        }
                    }
                    Button(
                        onClick = { step = 2 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Next Question", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                2 -> {
                    Text(
                        text = "2. Where in the video or caption does the violation appear?",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    ReportLocationOption.entries.forEach { loc ->
                        val selected = selectedLocation == loc
                        Surface(
                            onClick = { selectedLocation = loc },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) Color(0xFFFE2C55).copy(alpha = 0.18f) else Color(0xFF22222C),
                            border = BorderStroke(
                                1.dp,
                                if (selected) Color(0xFFFE2C55) else Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = loc.label,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back", color = Color.White)
                        }
                        Button(
                            onClick = { step = 3 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Next Question", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                else -> {
                    Text(
                        text = "3. How urgent is this safety concern?",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    ReportUrgencyOption.entries.forEach { urg ->
                        val selected = selectedUrgency == urg
                        Surface(
                            onClick = { selectedUrgency = urg },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) Color(0xFFFE2C55).copy(alpha = 0.18f) else Color(0xFF22222C),
                            border = BorderStroke(
                                1.dp,
                                if (selected) Color(0xFFFE2C55) else Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = urg.label,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        placeholder = { Text("Optional context for trust & safety team...", color = Color.White.copy(alpha = 0.45f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFFE2C55),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { step = 2 },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back", color = Color.White)
                        }
                        Button(
                            onClick = {
                                onSubmitQuiz(
                                    selectedReason,
                                    selectedLocation,
                                    selectedUrgency,
                                    additionalNotes.trim()
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("submit_feed_report_quiz_button")
                        ) {
                            Text("Submit Report", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * New Creators Screen opened from the `New creators >` pill button below the top tabs.
 * Shows real registered users ordered by newest creators with Follow / Following button.
 */
@Composable
fun NewCreatorsDirectoryScreen(
    creators: List<NewCreatorUserItem>,
    onBack: () -> Unit,
    onToggleFollow: (String) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E12))
            .testTag("new_creators_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBack,
                shape = CircleShape,
                color = Color(0xFF1E1E26),
                modifier = Modifier
                    .size(42.dp)
                    .testTag("new_creators_back_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.ARROW_LEFT,
                        tint = Color.White,
                        size = 20.dp
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "New creators",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ordered by newest creators on Telefam",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(creators, key = { it.userId }) { user ->
                Surface(
                    onClick = { onOpenProfile(user.userId) },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF181820),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFE2C55), Color(0xFF42101C))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.fullName.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.fullName,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "@${user.username} · ${user.locationLabel}",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (user.bio.isNotBlank()) {
                                Text(
                                    text = user.bio.replace("\n", " "),
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = { onToggleFollow(user.userId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.isFollowing) Color(0xFF2B2B36) else Color(0xFFFE2C55),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("new_creator_follow_btn_${user.userId}")
                        ) {
                            Text(
                                text = when {
                                    user.isFriend -> "Friends"
                                    user.isFollowing -> "Following"
                                    else -> "Follow"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Audio Page Screen opened when tapping the bottom-left `Original sound` / Audio pill.
 * Shows all posts using that same audio and a prominent "Use this sound" button to reuse in Post Studio.
 */
@Composable
fun FeedAudioPageScreen(
    audio: FeedAudioTrackInfo,
    videos: List<FeedVideoItem>,
    onBack: () -> Unit,
    onUseThisSound: () -> Unit,
    onOpenCreatorProfile: (String) -> Unit,
    onSelectVideoAt: (Int) -> Unit
) {
    BackHandler(onBack = onBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E12))
            .testTag("feed_audio_page_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color(0xFF1E1E26),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.ARROW_LEFT,
                            tint = Color.White,
                            size = 20.dp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (audio.isOriginalSound) "Original sound" else "Sound",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Audio Header Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF22222C),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.MUSIC_AUDIO,
                            tint = Color(0xFFFE2C55),
                            size = 32.dp
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = audio.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "@${audio.ownerUsername.ifBlank { audio.artistName }}",
                        color = Color(0xFFFE2C55),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onOpenCreatorProfile(audio.ownerUserId) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${videos.size.toLong().coerceAtLeast(audio.videosCount)} videos",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 3-Column Grid of Posts using this Audio
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 115.dp),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 92.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(videos, key = { _, v -> v.videoId }) { index, video ->
                    VideoGridThumbnailTile(
                        video = video,
                        onClick = { onSelectVideoAt(index) }
                    )
                }
            }
        }

        // Bottom Floating "Use this sound" Button -> Opens Post Studio with this audio track
        Button(
            onClick = onUseThisSound,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFE2C55),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 22.dp)
                .height(50.dp)
                .testTag("audio_page_use_sound_button")
        ) {
            TelefamLucideIcon(
                kind = LucideIconKind.MUSIC_AUDIO,
                tint = Color.White,
                size = 18.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Use this sound",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FeedHashtagPageScreen(
    hashtag: String,
    videos: List<FeedVideoItem>,
    onBack: () -> Unit,
    onSelectVideoAt: (Int) -> Unit
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E12))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBack,
                shape = CircleShape,
                color = Color(0xFF1E1E26),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.ARROW_LEFT,
                        tint = Color.White,
                        size = 20.dp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (hashtag.startsWith("#")) hashtag else "#$hashtag",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${videos.size} posts",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 115.dp),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(videos, key = { _, v -> v.videoId }) { index, video ->
                VideoGridThumbnailTile(
                    video = video,
                    onClick = { onSelectVideoAt(index) }
                )
            }
        }
    }
}

/**
 * Full-Text Search Screen (`Videos`, `Users`, `Hashtags`, `Sounds`).
 */
@Composable
fun FeedFullTextSearchScreen(
    results: FeedSearchCombinedResults,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onSelectVideoAt: (List<FeedVideoItem>, Int) -> Unit,
    onOpenUserProfile: (String) -> Unit,
    onToggleFollowUser: (String) -> Unit,
    onOpenHashtag: (String) -> Unit,
    onOpenSound: (FeedAudioTrackInfo) -> Unit
) {
    BackHandler(onBack = onBack)

    var queryText by remember { mutableStateOf(results.query) }
    var activeTab by remember { mutableStateOf(FeedSearchTab.VIDEOS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E12))
            .testTag("feed_search_screen")
    ) {
        // Search Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                onClick = onBack,
                shape = CircleShape,
                color = Color(0xFF1E1E26),
                modifier = Modifier
                    .size(42.dp)
                    .testTag("feed_search_back_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.ARROW_LEFT,
                        tint = Color.White,
                        size = 20.dp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF1C1C24),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.SEARCH,
                        tint = Color.White.copy(alpha = 0.6f),
                        size = 18.dp
                    )
                    BasicTextField(
                        value = queryText,
                        onValueChange = {
                            queryText = it
                            onQueryChange(it)
                        },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        cursorBrush = SolidColor(Color(0xFFFE2C55)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("feed_search_query_input"),
                        decorationBox = { inner ->
                            if (queryText.isEmpty()) {
                                Text(
                                    text = "Search videos, @users, #hashtags, sounds...",
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            inner()
                        }
                    )
                    if (queryText.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    queryText = ""
                                    onQueryChange("")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            TelefamLucideIcon(
                                kind = LucideIconKind.CLOSE_X,
                                tint = Color.White.copy(alpha = 0.7f),
                                size = 15.dp
                            )
                        }
                    }
                }
            }
        }

        // 4 Search Result Tabs: Videos | Users | Hashtags | Sounds
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FeedSearchTab.entries.forEach { tab ->
                val selected = activeTab == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { activeTab = tab }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("feed_search_tab_${tab.name.lowercase()}")
                ) {
                    Text(
                        text = tab.label,
                        color = if (selected) Color.White else Color.White.copy(alpha = 0.55f),
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(if (selected) 28.dp else 0.dp)
                            .height(2.5.dp)
                            .background(if (selected) Color(0xFFFE2C55) else Color.Transparent)
                    )
                }
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        when (activeTab) {
            FeedSearchTab.VIDEOS -> {
                if (results.videos.isEmpty()) {
                    SearchEmptyTabPlaceholder("No matching videos found")
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(results.videos, key = { _, v -> v.videoId }) { idx, video ->
                            VideoGridThumbnailTile(
                                video = video,
                                onClick = { onSelectVideoAt(results.videos, idx) }
                            )
                        }
                    }
                }
            }

            FeedSearchTab.USERS -> {
                if (results.users.isEmpty()) {
                    SearchEmptyTabPlaceholder("No matching users found")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(results.users, key = { it.userId }) { user ->
                            Surface(
                                onClick = { onOpenUserProfile(user.userId) },
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF181820),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFE2C55)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.fullName.take(1).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(user.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("@${user.username}", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { onToggleFollowUser(user.userId) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (user.isFollowing) Color(0xFF2B2B36) else Color(0xFFFE2C55)
                                        ),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = if (user.isFollowing) "Following" else "Follow",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FeedSearchTab.HASHTAGS -> {
                if (results.hashtags.isEmpty()) {
                    SearchEmptyTabPlaceholder("No hashtags indexed yet")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(results.hashtags, key = { it.hashtag }) { item ->
                            Surface(
                                onClick = { onOpenHashtag(item.hashtag) },
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF181820),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF262632),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            TelefamLucideIcon(
                                                kind = LucideIconKind.HASH_TAG,
                                                tint = Color(0xFFFE2C55),
                                                size = 18.dp
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.hashtag,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${item.videosCount} posts · ${formatFeedMetricCount(item.totalViewsCount)} views",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    }
                                    TelefamLucideIcon(
                                        kind = LucideIconKind.CHEVRON_RIGHT,
                                        tint = Color.White.copy(alpha = 0.45f),
                                        size = 16.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            FeedSearchTab.SOUNDS -> {
                if (results.sounds.isEmpty()) {
                    SearchEmptyTabPlaceholder("No matching sounds found")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(results.sounds, key = { it.audioId }) { sound ->
                            Surface(
                                onClick = { onOpenSound(sound) },
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF181820),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF262632),
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            TelefamLucideIcon(
                                                kind = LucideIconKind.MUSIC_AUDIO,
                                                tint = Color(0xFFFE2C55),
                                                size = 20.dp
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = sound.title,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${sound.artistName} · ${sound.videosCount} videos",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    }
                                    TelefamLucideIcon(
                                        kind = LucideIconKind.CHEVRON_RIGHT,
                                        tint = Color.White.copy(alpha = 0.45f),
                                        size = 16.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchEmptyTabPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun VideoGridThumbnailTile(
    video: FeedVideoItem,
    onClick: () -> Unit
) {
    val cleanThumb = remember(video.thumbnailUrl) {
        video.thumbnailUrl.substringBefore("?q=")
    }
    val bmp = remember(cleanThumb) {
        if (cleanThumb.startsWith("/") && File(cleanThumb).exists()) {
            runCatching { BitmapFactory.decodeFile(cleanThumb) }.getOrNull()
        } else null
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1A1A24),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 14f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = video.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF2B1219), Color(0xFF121218))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.PLAY,
                        tint = Color.White.copy(alpha = 0.7f),
                        size = 28.dp,
                        filled = true
                    )
                }
            }

            // Bottom views & creator overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = video.caption.ifBlank { "@${video.ownerUsername}" },
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.PLAY,
                        tint = Color.White,
                        size = 11.dp
                    )
                    Text(
                        text = formatFeedMetricCount(video.viewsCount),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Reusable Profile Vertical Video Feed (without `For You / Friends / Following` top bar or Search bar).
 * Plays the profile owner's videos while scrolling with the exact same interaction stack,
 * Share to Inbox, 3-dot menu, Report quiz, and Audio/Hashtag navigation.
 */
@Composable
fun TelefamProfileVideoFeedOverlay(
    videos: List<FeedVideoItem>,
    initialVideoIndex: Int,
    currentUserId: String,
    profileHeaderTitle: String,
    inboxRecipients: List<NewCreatorUserItem>,
    statusFeedbackMessage: String?,
    onBack: () -> Unit,
    onRefreshInboxRecipients: () -> Unit,
    onToggleFollowCreatorByUserId: (String) -> Unit,
    onToggleLikeVideo: (FeedVideoItem, Boolean) -> Unit,
    onToggleSaveVideo: (FeedVideoItem) -> Unit,
    onRecordVideoShare: (FeedVideoItem, String, String?) -> Unit,
    onShareVideoToInboxDm: (FeedVideoItem, NewCreatorUserItem) -> Unit,
    onRecordVideoView: (videoId: String, watchDurationMs: Long, completedLoop: Boolean) -> Unit,
    onMarkVideoNotInterested: (FeedVideoItem) -> Unit,
    onBlockCreatorFromFeed: (FeedVideoItem) -> Unit,
    onSubmitVideoReportQuiz: (
        video: FeedVideoItem,
        reason: ReportReasonOption,
        location: ReportLocationOption,
        urgency: ReportUrgencyOption,
        details: String
    ) -> Unit,
    onDownloadVideoToDevice: (FeedVideoItem) -> Unit,
    onDeleteOwnedVideo: (FeedVideoItem) -> Unit,
    onEditOwnedVideoInStudio: (FeedVideoItem) -> Unit,
    onOpenAudioPageInFeeds: (FeedAudioTrackInfo) -> Unit,
    onOpenHashtagPageInFeeds: (String) -> Unit,
    onOpenCreatorProfile: (String) -> Unit,
    onDismissStatusFeedback: () -> Unit,
    onOpenComments: (FeedVideoItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var shareSheetTargetVideo by remember { mutableStateOf<FeedVideoItem?>(null) }
    var moreOptionsTargetVideo by remember { mutableStateOf<FeedVideoItem?>(null) }
    var reportQuizTargetVideo by remember { mutableStateOf<FeedVideoItem?>(null) }

    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("profile_reusable_video_feed_overlay")
    ) {
        TelefamReusableVerticalVideoFeed(
            videos = videos,
            currentUserId = currentUserId,
            initialVideoIndex = initialVideoIndex,
            isFeedVisibleAndActive = true,
            contextMode = ReusableFeedContextMode.USER_PROFILE_FEED,
            topReservedPaddingDp = 64,
            bottomReservedPaddingDp = 16,
            onToggleFollowCreator = { v -> onToggleFollowCreatorByUserId(v.ownerUserId) },
            onToggleLikeVideo = onToggleLikeVideo,
            onToggleSaveVideo = onToggleSaveVideo,
            onOpenComments = onOpenComments,
            onOpenShareSheet = { v ->
                onRefreshInboxRecipients()
                shareSheetTargetVideo = v
            },
            onOpenMoreOptionsSheet = { v -> moreOptionsTargetVideo = v },
            onOpenAudioPage = onOpenAudioPageInFeeds,
            onOpenCreatorProfile = onOpenCreatorProfile,
            onOpenHashtagPage = onOpenHashtagPageInFeeds,
            onRecordVideoView = onRecordVideoView,
            onCloseOverlayFeed = onBack,
            overlayHeaderTitle = profileHeaderTitle
        )

        AnimatedVisibility(
            visible = !statusFeedbackMessage.isNullOrBlank(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                onClick = onDismissStatusFeedback,
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1A1A22).copy(alpha = 0.94f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.CIRCLE_CHECK_DELIVERED,
                        tint = Color(0xFFFE2C55),
                        size = 18.dp,
                        filled = true
                    )
                    Text(
                        text = statusFeedbackMessage.orEmpty(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    shareSheetTargetVideo?.let { targetVideo ->
        FeedShareBottomSheet(
            video = targetVideo,
            inboxRecipients = inboxRecipients,
            onDismiss = { shareSheetTargetVideo = null },
            onShareToSystem = {
                onRecordVideoShare(targetVideo, "SYSTEM", null)
                shareSheetTargetVideo = null
            },
            onCopyLink = {
                onRecordVideoShare(targetVideo, "COPY_LINK", null)
                shareSheetTargetVideo = null
            },
            onSendToInboxUser = { recipient ->
                onShareVideoToInboxDm(targetVideo, recipient)
                shareSheetTargetVideo = null
            },
            onSaveVideo = {
                onToggleSaveVideo(targetVideo)
                shareSheetTargetVideo = null
            }
        )
    }

    moreOptionsTargetVideo?.let { targetVideo ->
        FeedMoreOptionsBottomSheet(
            video = targetVideo,
            isOwner = targetVideo.ownerUserId == currentUserId,
            onDismiss = { moreOptionsTargetVideo = null },
            onNotInterested = {
                onMarkVideoNotInterested(targetVideo)
                moreOptionsTargetVideo = null
            },
            onReshareRepost = {
                onRecordVideoShare(targetVideo, "RESHARE", null)
                moreOptionsTargetVideo = null
            },
            onDownloadVideo = {
                onDownloadVideoToDevice(targetVideo)
                moreOptionsTargetVideo = null
            },
            onBlockCreator = {
                onBlockCreatorFromFeed(targetVideo)
                moreOptionsTargetVideo = null
            },
            onOpenReportQuiz = {
                moreOptionsTargetVideo = null
                reportQuizTargetVideo = targetVideo
            },
            onDeleteOwnedVideo = {
                onDeleteOwnedVideo(targetVideo)
                moreOptionsTargetVideo = null
            },
            onEditOwnedVideo = {
                val v = targetVideo
                moreOptionsTargetVideo = null
                onEditOwnedVideoInStudio(v)
            }
        )
    }

    reportQuizTargetVideo?.let { targetVideo ->
        FeedMultiStepReportQuizSheet(
            video = targetVideo,
            onDismiss = { reportQuizTargetVideo = null },
            onSubmitQuiz = { reason, location, urgency, details ->
                onSubmitVideoReportQuiz(targetVideo, reason, location, urgency, details)
                reportQuizTargetVideo = null
            }
        )
    }
}
