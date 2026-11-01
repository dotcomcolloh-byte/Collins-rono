package com.telefam.company.app.ui.screens

import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.TelefamRedBright
import com.telefam.company.app.domain.FeedAudioTrackInfo
import com.telefam.company.app.domain.FeedVideoItem
import com.telefam.company.app.domain.ReusableFeedContextMode
import com.telefam.company.app.security.TelefamFeedVideoPreloadAndPlayerEngine
import com.telefam.company.app.ui.components.LucideIconKind
import com.telefam.company.app.ui.components.TelefamLucideIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Reusable TikTok-style Vertical Video Feed Component.
 *
 * Used in:
 * 1. Main Feeds Screen (`For You`, `Friends`, `Following`)
 * 2. Owner Profile & Public User Profile (`USER_PROFILE_FEED` - no top tabs/search bar, plays while scrolling)
 * 3. Audio Page Feed (`AUDIO_PAGE_FEED`)
 * 4. Hashtag Page Feed (`HASHTAG_PAGE_FEED`)
 * 5. Search Results Video Feed (`SEARCH_VIDEO_FEED`)
 *
 * Supports ALL video aspect ratios (`9:16`, `16:9`, `4:3`, `1:1`, `21:9`, etc.) with `ContentScale.Fit`
 * and pure black letterboxing so video never overlaps the top bar or right action stack.
 */
@Composable
fun TelefamReusableVerticalVideoFeed(
    videos: List<FeedVideoItem>,
    currentUserId: String,
    initialVideoIndex: Int = 0,
    isFeedVisibleAndActive: Boolean = true,
    contextMode: ReusableFeedContextMode = ReusableFeedContextMode.MAIN_FEEDS,
    topReservedPaddingDp: Int = 92,
    bottomReservedPaddingDp: Int = 12,
    onLoadMoreVideos: () -> Unit = {},
    onToggleFollowCreator: (FeedVideoItem) -> Unit,
    onToggleLikeVideo: (FeedVideoItem, Boolean) -> Unit,
    onToggleSaveVideo: (FeedVideoItem) -> Unit,
    onOpenComments: (FeedVideoItem) -> Unit,
    onOpenShareSheet: (FeedVideoItem) -> Unit,
    onOpenMoreOptionsSheet: (FeedVideoItem) -> Unit,
    onOpenAudioPage: (FeedAudioTrackInfo) -> Unit,
    onOpenCreatorProfile: (String) -> Unit,
    onOpenHashtagPage: (String) -> Unit,
    onRecordVideoView: (videoId: String, watchDurationMs: Long, completedLoop: Boolean) -> Unit,
    onCloseOverlayFeed: (() -> Unit)? = null,
    overlayHeaderTitle: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val safeInitialPage = initialVideoIndex.coerceIn(0, (videos.size - 1).coerceAtLeast(0))
    val pagerState = rememberPagerState(
        initialPage = safeInitialPage,
        pageCount = { videos.size }
    )

    // Preload next 5 videos in chunks & trigger infinite scroll pagination when approaching end
    LaunchedEffect(pagerState.currentPage, videos.size, isFeedVisibleAndActive) {
        if (!isFeedVisibleAndActive || videos.isEmpty()) return@LaunchedEffect
        TelefamFeedVideoPreloadAndPlayerEngine.preloadNextFiveVideosInChunks(
            context = context,
            allFeedItems = videos,
            currentIndex = pagerState.currentPage
        )
        if (pagerState.currentPage >= videos.size - 3) {
            onLoadMoreVideos()
        }
    }

    // Cancel background preloads when leaving feed completely
    DisposableEffect(Unit) {
        onDispose {
            if (contextMode == ReusableFeedContextMode.MAIN_FEEDS) {
                TelefamFeedVideoPreloadAndPlayerEngine.cancelAllPreloads()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reusable_vertical_video_feed_${contextMode.name.lowercase()}")
    ) {
        if (videos.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("vertical_feed_pager"),
                beyondViewportPageCount = 1,
                key = { idx -> videos.getOrNull(idx)?.videoId ?: "page_$idx" }
            ) { pageIndex ->
                val item = videos.getOrNull(pageIndex) ?: return@VerticalPager
                val isCurrentPagePlaying = isFeedVisibleAndActive && (pagerState.currentPage == pageIndex)

                SingleFeedVideoPageContent(
                    item = item,
                    currentUserId = currentUserId,
                    isPageActive = isCurrentPagePlaying,
                    topReservedPaddingDp = topReservedPaddingDp,
                    bottomReservedPaddingDp = bottomReservedPaddingDp,
                    onToggleFollowCreator = { onToggleFollowCreator(item) },
                    onToggleLikeVideo = { forceLike -> onToggleLikeVideo(item, forceLike) },
                    onToggleSaveVideo = { onToggleSaveVideo(item) },
                    onOpenComments = { onOpenComments(item) },
                    onOpenShareSheet = { onOpenShareSheet(item) },
                    onOpenMoreOptionsSheet = { onOpenMoreOptionsSheet(item) },
                    onOpenAudioPage = { onOpenAudioPage(item.audio) },
                    onOpenCreatorProfile = { onOpenCreatorProfile(item.ownerUserId) },
                    onOpenHashtagPage = onOpenHashtagPage,
                    onRecordVideoView = { watchMs, looped ->
                        onRecordVideoView(item.videoId, watchMs, looped)
                    }
                )
            }
        }

        // Optional top back bar when used as an overlay feed (Profile Feed, Audio Feed, Hashtag Feed, Search Feed)
        if (onCloseOverlayFeed != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, start = 14.dp, end = 14.dp)
                    .align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onCloseOverlayFeed,
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("close_overlay_feed_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.ARROW_LEFT,
                            tint = Color.White,
                            size = 20.dp
                        )
                    }
                }
                if (!overlayHeaderTitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.Black.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Text(
                            text = overlayHeaderTitle,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleFeedVideoPageContent(
    item: FeedVideoItem,
    currentUserId: String,
    isPageActive: Boolean,
    topReservedPaddingDp: Int,
    bottomReservedPaddingDp: Int,
    onToggleFollowCreator: () -> Unit,
    onToggleLikeVideo: (forceLikeOnly: Boolean) -> Unit,
    onToggleSaveVideo: () -> Unit,
    onOpenComments: () -> Unit,
    onOpenShareSheet: () -> Unit,
    onOpenMoreOptionsSheet: () -> Unit,
    onOpenAudioPage: () -> Unit,
    onOpenCreatorProfile: () -> Unit,
    onOpenHashtagPage: (String) -> Unit,
    onRecordVideoView: (watchDurationMs: Long, completedLoop: Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var isPausedByUser by remember(item.videoId) { mutableStateOf(false) }
    var isFastForward2x by remember(item.videoId) { mutableStateOf(false) }
    var isLifecycleResumed by remember { mutableStateOf(true) }
    var isRetryingStream by remember(item.videoId) { mutableStateOf(false) }
    var retryAttempt by remember(item.videoId) { mutableIntStateOf(0) }

    // Double-tap floating heart animation state
    var showDoubleTapHeart by remember(item.videoId) { mutableStateOf(false) }
    var doubleTapOffset by remember(item.videoId) { mutableStateOf(Offset.Zero) }

    // Track watch duration for recommendation engine
    var pageEnteredAtMs by remember(item.videoId) { mutableLongStateOf(0L) }
    var completedAtLeastOneLoop by remember(item.videoId) { mutableStateOf(false) }

    val effectiveShouldPlay = isPageActive && isLifecycleResumed && !isPausedByUser

    // Observe lifecycle so video & audio immediately pause/stop when leaving screen or backgrounding app
    DisposableEffect(lifecycleOwner, item.videoId) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> isLifecycleResumed = true
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> {
                    isLifecycleResumed = false
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Record view duration when page becomes active / inactive
    LaunchedEffect(isPageActive, item.videoId) {
        if (isPageActive) {
            isPausedByUser = false
            isFastForward2x = false
            pageEnteredAtMs = System.currentTimeMillis()
            completedAtLeastOneLoop = false
        } else if (pageEnteredAtMs > 0L) {
            val watchedMs = (System.currentTimeMillis() - pageEnteredAtMs).coerceAtLeast(0L)
            pageEnteredAtMs = 0L
            if (watchedMs >= 800L) {
                onRecordVideoView(watchedMs, completedAtLeastOneLoop)
            }
        }
    }

    // Optional companion audio player when post has an external song preview URL
    val companionAudioPlayer = remember(item.videoId, item.audio.streamPreviewUrl) {
        val streamUrl = item.audio.streamPreviewUrl.trim()
        if (streamUrl.startsWith("http://") || streamUrl.startsWith("https://") || streamUrl.startsWith("/")) {
            MediaPlayer().apply {
                runCatching {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(streamUrl)
                    isLooping = true
                    prepareAsync()
                }
            }
        } else {
            null
        }
    }

    LaunchedEffect(effectiveShouldPlay, isFastForward2x, companionAudioPlayer) {
        companionAudioPlayer?.let { mp ->
            runCatching {
                if (effectiveShouldPlay) {
                    if (!mp.isPlaying) mp.start()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mp.isPlaying) {
                        mp.playbackParams = PlaybackParams().setSpeed(if (isFastForward2x) 2.0f else 1.0f)
                    }
                } else {
                    if (mp.isPlaying) mp.pause()
                }
            }
        }
    }

    DisposableEffect(companionAudioPlayer) {
        onDispose {
            runCatching {
                companionAudioPlayer?.stop()
            }
            runCatching {
                companionAudioPlayer?.release()
            }
        }
    }

    // Resolve local bitmap thumbnail / cover frame for instant zero-skeleton display & baked image/text posts
    val cleanThumbPath = remember(item.thumbnailUrl, item.primaryVideoUrl) {
        item.thumbnailUrl.ifBlank { item.primaryVideoUrl }.substringBefore("?q=")
    }
    val localCoverBitmap = remember(cleanThumbPath) {
        if (cleanThumbPath.startsWith("/")) {
            val f = File(cleanThumbPath)
            if (f.exists()) runCatching { BitmapFactory.decodeFile(f.absolutePath) }.getOrNull() else null
        } else null
    }

    val resolvedStreamPath = remember(item.videoId, retryAttempt) {
        TelefamFeedVideoPreloadAndPlayerEngine.resolveSilentAdaptiveStreamUrl(context, item)
            .substringBefore("?q=")
    }
    val isPlayableVideoStream = remember(resolvedStreamPath) {
        val lower = resolvedStreamPath.lowercase()
        lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mkv") ||
            lower.endsWith(".mov") || lower.startsWith("http://") || lower.startsWith("https://")
    }

    // Exponential backoff retry when network stream encounters transient error
    LaunchedEffect(isRetryingStream, retryAttempt) {
        if (isRetryingStream && retryAttempt < 4) {
            val waitMs = 800L * (1 shl retryAttempt.coerceAtMost(3))
            delay(waitMs)
            retryAttempt++
            isRetryingStream = false
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(item.videoId) {
                detectTapGestures(
                    onTap = {
                        isPausedByUser = !isPausedByUser
                    },
                    onDoubleTap = { offset ->
                        doubleTapOffset = offset
                        showDoubleTapHeart = true
                        onToggleLikeVideo(true)
                        scope.launch {
                            delay(750L)
                            showDoubleTapHeart = false
                        }
                    },
                    onLongPress = {
                        isFastForward2x = true
                    },
                    onPress = {
                        tryAwaitRelease()
                        if (isFastForward2x) {
                            isFastForward2x = false
                        }
                    }
                )
            }
    ) {
        val isWideTabletOrDesktop = maxWidth >= 680.dp
        val maxContentWidth = if (isWideTabletOrDesktop) 520.dp else maxWidth

        // 1. ALL-ASPECT-RATIO VIDEO / MEDIA CONTAINER (ContentScale.Fit, Black Letterboxing, Never Overlaps Top Bar)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topReservedPaddingDp.dp,
                    bottom = bottomReservedPaddingDp.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                // Thumbnail / Baked Studio Cover Frame (always rendered underneath so there is never a blank flash)
                if (localCoverBitmap != null) {
                    Image(
                        bitmap = localCoverBitmap.asImageBitmap(),
                        contentDescription = item.caption.ifBlank { "Feed media by @${item.ownerUsername}" },
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Hardware VideoView when source is an MP4/WebM/Stream
                if (isPlayableVideoStream && (!resolvedStreamPath.startsWith("/") || File(resolvedStreamPath).exists())) {
                    var videoViewRef by remember(item.videoId) { mutableStateOf<VideoView?>(null) }

                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                videoViewRef = this
                                setOnPreparedListener { mp ->
                                    isRetryingStream = false
                                    mp.isLooping = true
                                    if (companionAudioPlayer != null) {
                                        // Mute embedded track if user attached a custom studio song
                                        mp.setVolume(0f, 0f)
                                    } else {
                                        mp.setVolume(1f, 1f)
                                    }
                                    if (effectiveShouldPlay) {
                                        start()
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            runCatching {
                                                mp.playbackParams = PlaybackParams().setSpeed(if (isFastForward2x) 2.0f else 1.0f)
                                            }
                                        }
                                    } else {
                                        pause()
                                    }
                                }
                                setOnInfoListener { _, what, _ ->
                                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                        isRetryingStream = false
                                    }
                                    false
                                }
                                setOnCompletionListener {
                                    completedAtLeastOneLoop = true
                                    onRecordVideoView(item.durationMs.coerceAtLeast(5_000L), true)
                                }
                                setOnErrorListener { _, _, _ ->
                                    // Silent exponential backoff retry on network glitch
                                    if (!resolvedStreamPath.startsWith("/")) {
                                        isRetryingStream = true
                                    }
                                    true
                                }
                                runCatching {
                                    if (resolvedStreamPath.startsWith("/")) {
                                        setVideoPath(resolvedStreamPath)
                                    } else {
                                        setVideoURI(Uri.parse(resolvedStreamPath))
                                    }
                                }
                            }
                        },
                        update = { vv ->
                            runCatching {
                                if (effectiveShouldPlay) {
                                    if (!vv.isPlaying) vv.start()
                                } else {
                                    if (vv.isPlaying) vv.pause()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(item.aspectRatio, matchHeightConstraintsFirst = item.aspectRatio < 0.75f)
                    )

                    DisposableEffect(item.videoId) {
                        onDispose {
                            runCatching {
                                videoViewRef?.stopPlayback()
                            }
                            videoViewRef = null
                        }
                    }
                }
            }
        }

        // Subtle Bottom Scrim Gradient so bottom-left text & right icons remain crisp over bright videos
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.82f)
                        )
                    )
                )
        )

        // 2. CENTER PLAY ICON WHEN PAUSED (Exact match to attached reference image)
        AnimatedVisibility(
            visible = isPausedByUser,
            enter = fadeIn(tween(150)) + scaleIn(initialScale = 0.82f),
            exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.85f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            CenterTranslucentPlayButtonOverlay()
        }

        // 3. DOUBLE-TAP HEART BURST ANIMATION
        AnimatedVisibility(
            visible = showDoubleTapHeart,
            enter = scaleIn(animationSpec = spring(dampingRatio = 0.45f, stiffness = 500f)) + fadeIn(),
            exit = scaleOut(targetScale = 1.45f) + fadeOut(),
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (doubleTapOffset.x.roundToInt() - 110).coerceAtLeast(24),
                        y = (doubleTapOffset.y.roundToInt() - 110).coerceAtLeast(100)
                    )
                }
        ) {
            TelefamLucideIcon(
                kind = LucideIconKind.REACTION_HEART,
                tint = Color(0xFFFE2C55),
                size = 86.dp,
                filled = true
            )
        }

        // 4. LONG-PRESS 2X SPEED INDICATOR PILL
        AnimatedVisibility(
            visible = isFastForward2x,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = (topReservedPaddingDp + 12).dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.Black.copy(alpha = 0.68f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.PLAY,
                        tint = TelefamRedBright,
                        size = 14.dp,
                        filled = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "2x Speed",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Small non-blocking retry indicator on transient network hiccup
        AnimatedVisibility(
            visible = isRetryingStream,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = (topReservedPaddingDp + 8).dp, end = 16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                CircularProgressIndicator(
                    color = TelefamRedBright,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                )
            }
        }

        // 5. RIGHT ACTION STACK (Exact match to attached reference image)
        FeedRightActionStack(
            item = item,
            currentUserId = currentUserId,
            onOpenCreatorProfile = onOpenCreatorProfile,
            onToggleFollowCreator = onToggleFollowCreator,
            onToggleLikeVideo = { onToggleLikeVideo(false) },
            onOpenComments = onOpenComments,
            onOpenShareSheet = onOpenShareSheet,
            onToggleSaveVideo = onToggleSaveVideo,
            onOpenMoreOptionsSheet = onOpenMoreOptionsSheet,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = (bottomReservedPaddingDp + 16).dp)
        )

        // 6. BOTTOM-LEFT CREATOR INFO, CAPTION, HASHTAGS & TAPPABLE AUDIO ROW (Exact match to reference image)
        FeedBottomLeftMetadataAndAudioOverlay(
            item = item,
            isPlaying = effectiveShouldPlay,
            onOpenCreatorProfile = onOpenCreatorProfile,
            onOpenHashtagPage = onOpenHashtagPage,
            onOpenAudioPage = onOpenAudioPage,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = (bottomReservedPaddingDp + 16).dp)
        )
    }
}

/**
 * Center translucent Play triangle icon shown when video is paused (exact match to attached image).
 */
@Composable
private fun CenterTranslucentPlayButtonOverlay() {
    Canvas(modifier = Modifier.size(74.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.24f, h * 0.14f)
            lineTo(w * 0.86f, h * 0.50f)
            lineTo(w * 0.24f, h * 0.86f)
            close()
        }
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.52f),
            style = Fill
        )
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.65f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 6.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Right Action Stack matching the attached image from top to bottom:
 * 1. Avatar with small red `+` follow button at bottom right/center of avatar
 * 2. Heart like icon (red when liked), count below
 * 3. Comments icon w `...` inside bubble, count below -> COMING SOON
 * 4. Share icon (paper plane), count below -> Share Bottom Sheet
 * 5. Bookmark/save icon, count below
 * 6. 3 dots `...` -> More Options Bottom Sheet
 */
@Composable
private fun FeedRightActionStack(
    item: FeedVideoItem,
    currentUserId: String,
    onOpenCreatorProfile: () -> Unit,
    onToggleFollowCreator: () -> Unit,
    onToggleLikeVideo: () -> Unit,
    onOpenComments: () -> Unit,
    onOpenShareSheet: () -> Unit,
    onToggleSaveVideo: () -> Unit,
    onOpenMoreOptionsSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val likeScale by animateFloatAsState(
        targetValue = if (item.isLikedByMe) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 500f),
        label = "like_scale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Creator Avatar + Red '+' Follow Badge
        Box(
            modifier = Modifier
                .size(56.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            val avatarBitmap = remember(item.ownerAvatarLocalPath) {
                val p = item.ownerAvatarLocalPath
                if (!p.isNullOrBlank() && File(p).exists()) {
                    runCatching { BitmapFactory.decodeFile(p) }.getOrNull()
                } else null
            }

            Surface(
                onClick = onOpenCreatorProfile,
                shape = CircleShape,
                color = Color(0xFF22222A),
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("feed_creator_avatar_${item.videoId}")
            ) {
                if (avatarBitmap != null) {
                    Image(
                        bitmap = avatarBitmap.asImageBitmap(),
                        contentDescription = "Creator @${item.ownerUsername}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(TelefamRedBright, Color(0xFF4A0E17))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.ownerFullName.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Small red '+' Follow/Following toggle button at bottom-right of avatar
            if (item.ownerUserId != currentUserId) {
                Surface(
                    onClick = onToggleFollowCreator,
                    shape = CircleShape,
                    color = if (item.isFollowingCreator) Color(0xFF2B2B36) else Color(0xFFFE2C55),
                    border = BorderStroke(1.5.dp, Color.White),
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomEnd)
                        .testTag("feed_follow_plus_button_${item.videoId}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        TelefamLucideIcon(
                            kind = if (item.isFollowingCreator) LucideIconKind.CHECK_SINGLE else LucideIconKind.PLUS,
                            tint = Color.White,
                            size = 12.dp,
                            strokeWidthDp = 2.4.dp
                        )
                    }
                }
            }
        }

        // 2. Heart Like Icon + Count
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onToggleLikeVideo)
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .testTag("feed_like_button_${item.videoId}")
        ) {
            TelefamLucideIcon(
                kind = LucideIconKind.REACTION_HEART,
                tint = if (item.isLikedByMe) Color(0xFFFE2C55) else Color.White,
                size = (32.dp * likeScale),
                strokeWidthDp = 2.1.dp,
                filled = item.isLikedByMe
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = formatFeedMetricCount(item.likesCount),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // 3. Comments Icon w/ '...' inside bubble + Count -> Opens Comments Bottom Sheet
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onOpenComments)
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .testTag("feed_comments_button_${item.videoId}")
        ) {
            TelefamLucideIcon(
                kind = LucideIconKind.MESSAGE_CIRCLE_DOTS,
                tint = Color.White,
                size = 31.dp,
                strokeWidthDp = 2.1.dp,
                filled = true
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = formatFeedMetricCount(item.commentsCount),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // 4. Share Icon (Paper Plane) + Count -> On click show Share Bottom Sheet
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onOpenShareSheet)
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .testTag("feed_share_button_${item.videoId}")
        ) {
            TelefamLucideIcon(
                kind = LucideIconKind.SEND,
                tint = Color.White,
                size = 29.dp,
                strokeWidthDp = 2.1.dp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = formatFeedMetricCount(item.sharesCount),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // 5. Bookmark / Save Icon + Count
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onToggleSaveVideo)
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .testTag("feed_save_button_${item.videoId}")
        ) {
            TelefamLucideIcon(
                kind = LucideIconKind.BOOKMARK_SAVE,
                tint = if (item.isSavedByMe) Color(0xFFFFC107) else Color.White,
                size = 29.dp,
                strokeWidthDp = 2.1.dp,
                filled = item.isSavedByMe
            )
            if (item.savesCount > 0L) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatFeedMetricCount(item.savesCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 6. 3 Horizontal Dots '...' -> More Options Bottom Sheet
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onOpenMoreOptionsSheet)
                .testTag("feed_more_dots_button_${item.videoId}"),
            contentAlignment = Alignment.Center
        ) {
            TelefamLucideIcon(
                kind = LucideIconKind.MORE_HORIZONTAL,
                tint = Color.White,
                size = 28.dp
            )
        }
    }
}

/**
 * Bottom-left overlay matching the attached reference image:
 * - Optional "You might like" dark translucent pill
 * - Bold creator username
 * - Tappable caption & `#hashtags`
 * - Rounded bordered Audio Pill (`Original sound` + music note icon + animated waveform)
 */
@Composable
private fun FeedBottomLeftMetadataAndAudioOverlay(
    item: FeedVideoItem,
    isPlaying: Boolean,
    onOpenCreatorProfile: () -> Unit,
    onOpenHashtagPage: (String) -> Unit,
    onOpenAudioPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "You might like" pill badge (exact match to attached image)
        if (item.showYouMightLikeBadge) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2A2A32).copy(alpha = 0.85f)
            ) {
                Text(
                    text = "You might like",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Creator Display Name / Username
        Text(
            text = item.ownerUsername.ifBlank { item.ownerFullName },
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable(onClick = onOpenCreatorProfile)
                .testTag("feed_creator_username_${item.videoId}")
        )

        // Caption + Hashtags
        if (item.caption.isNotBlank() || item.hashtags.isNotEmpty()) {
            val annotatedCaption = remember(item.caption, item.hashtags) {
                buildAnnotatedString {
                    if (item.caption.isNotBlank()) {
                        append(item.caption.trim())
                    }
                    val missingTags = item.hashtags.filter { tag ->
                        !item.caption.contains(tag, ignoreCase = true)
                    }
                    if (missingTags.isNotEmpty()) {
                        if (length > 0) append(" ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append(missingTags.joinToString(" "))
                        }
                    }
                }
            }

            Text(
                text = annotatedCaption,
                color = Color.White.copy(alpha = 0.94f),
                fontSize = 14.sp,
                lineHeight = 19.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Tappable hashtag chips row if video has hashtags
            if (item.hashtags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item.hashtags.take(4).forEach { tag ->
                        Text(
                            text = if (tag.startsWith("#")) tag else "#$tag",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onOpenHashtagPage(tag) }
                                .padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Tappable Audio Row Pill: Music icon + "Original sound" (or track title) + Animated Waveform
        Surface(
            onClick = onOpenAudioPage,
            shape = RoundedCornerShape(50),
            color = Color.Black.copy(alpha = 0.42f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.38f)),
            modifier = Modifier.testTag("feed_audio_pill_${item.videoId}")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelefamLucideIcon(
                    kind = LucideIconKind.MUSIC_AUDIO,
                    tint = Color.White,
                    size = 15.dp
                )
                Text(
                    text = if (item.audio.isOriginalSound) {
                        "Original sound - @${item.audio.ownerUsername.ifBlank { item.ownerUsername }}"
                    } else {
                        "${item.audio.title} · ${item.audio.artistName}"
                    },
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 195.dp)
                )
                AnimatedAudioWaveformBars(isPlaying = isPlaying)
            }
        }
    }
}

@Composable
private fun AnimatedAudioWaveformBars(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_phase"
    )

    Canvas(modifier = modifier.size(width = 18.dp, height = 13.dp)) {
        val barWidth = 2.2.dp.toPx()
        val gap = 2.4.dp.toPx()
        val maxH = size.height
        val ratios = if (isPlaying) {
            listOf(
                0.35f + 0.55f * phase,
                0.85f - 0.50f * phase,
                0.45f + 0.50f * phase,
                0.75f - 0.40f * phase
            )
        } else {
            listOf(0.35f, 0.55f, 0.40f, 0.30f)
        }

        ratios.forEachIndexed { idx, r ->
            val bh = (maxH * r.coerceIn(0.25f, 1f))
            val x = idx * (barWidth + gap) + barWidth / 2f
            val yTop = (maxH - bh) / 2f
            drawLine(
                color = Color.White,
                start = Offset(x, yTop),
                end = Offset(x, yTop + bh),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

fun formatFeedMetricCount(count: Long): String {
    if (count < 1000L) return count.toString()
    if (count < 1_000_000L) {
        val k = count / 1000.0
        return String.format(Locale.US, "%.1fK", k).replace(".0K", "K")
    }
    val m = count / 1_000_000.0
    return String.format(Locale.US, "%.1fM", m).replace(".0M", "M")
}
