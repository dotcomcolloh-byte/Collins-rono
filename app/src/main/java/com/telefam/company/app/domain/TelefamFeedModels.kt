package com.telefam.company.app.domain

/**
 * Top navigation tabs on the main TikTok-style Feeds screen.
 */
enum class FeedTopTab(val label: String, val wireCode: String) {
    FOR_YOU("For You", "FOR_YOU"),
    FRIENDS("Friends", "FRIENDS"),
    FOLLOWING("Following", "FOLLOWING")
}

/**
 * Tabs on the Feeds Search screen.
 */
enum class FeedSearchTab(val label: String) {
    VIDEOS("Videos"),
    USERS("Users"),
    HASHTAGS("Hashtags"),
    SOUNDS("Sounds")
}

/**
 * Multi-quality video stream URLs preloaded silently in the background.
 */
data class FeedVideoQualityUrls(
    val url360p: String,
    val url720p: String,
    val url1080p: String
)

/**
 * Audio / Sound metadata attached to a feed video.
 * Both original sounds and catalog tracks are tappable to open the Audio Page
 * and can be reused in the Post Creation Studio ("Use this sound").
 */
data class FeedAudioTrackInfo(
    val audioId: String,
    val title: String,
    val artistName: String,
    val ownerUserId: String,
    val ownerUsername: String,
    val coverArtUrl: String? = null,
    val streamPreviewUrl: String = "",
    val durationMs: Long = 30_000L,
    val isOriginalSound: Boolean = true,
    val videosCount: Long = 1L
) {
    fun toPostMusicTrackItem(): PostMusicTrackItem {
        return PostMusicTrackItem(
            trackId = audioId,
            title = title,
            artistName = artistName.ifBlank { "@$ownerUsername" },
            albumTitle = if (isOriginalSound) "Original sound" else "Telefam Sound",
            artworkUrl = coverArtUrl.orEmpty(),
            previewStreamUrl = streamPreviewUrl,
            durationMs = durationMs.coerceAtLeast(15_000L)
        )
    }
}

/**
 * Complete domain model for a single video/post in the vertical feed pager.
 * Supports all aspect ratios (9:16, 16:9, 4:3, 1:1, 21:9, etc.) via [widthPx] and [heightPx].
 */
data class FeedVideoItem(
    val videoId: String,
    val ownerUserId: String,
    val ownerFullName: String,
    val ownerUsername: String,
    val ownerAvatarLocalPath: String? = null,
    val ownerAvatarCdnUrl: String? = null,
    val isFollowingCreator: Boolean = false,
    val isFriendWithCreator: Boolean = false,
    val caption: String,
    val hashtags: List<String> = emptyList(),
    val languageTag: String = "English",
    val primaryVideoUrl: String,
    val qualityUrls: FeedVideoQualityUrls,
    val thumbnailUrl: String,
    val widthPx: Int = 1080,
    val heightPx: Int = 1920,
    val durationMs: Long = 15_000L,
    val durationLabel: String = "0:15",
    val audio: FeedAudioTrackInfo,
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val sharesCount: Long = 0L,
    val savesCount: Long = 0L,
    val viewsCount: Long = 0L,
    val isLikedByMe: Boolean = false,
    val isSavedByMe: Boolean = false,
    val allowDownload: Boolean = true,
    val allowComments: Boolean = true,
    val allowDuetStitch: Boolean = true,
    val visibilityAudience: String = "ANYONE",
    val recommendationScore: Double = 0.0,
    val showYouMightLikeBadge: Boolean = false,
    val activeCampaign: com.telefam.company.app.data.CampaignEntity? = null,
    val createdAtMs: Long = System.currentTimeMillis()
) {
    val aspectRatio: Float
        get() {
            if (widthPx <= 0 || heightPx <= 0) return 9f / 16f
            return (widthPx.toFloat() / heightPx.toFloat()).coerceIn(0.35f, 2.85f)
        }
}

/**
 * Real user item for the "New creators" screen and Search -> Users tab.
 */
data class NewCreatorUserItem(
    val userId: String,
    val fullName: String,
    val username: String,
    val bio: String,
    val locationLabel: String,
    val avatarLocalPath: String? = null,
    val avatarCdnUrl: String? = null,
    val followersCount: Long = 0L,
    val videosCount: Long = 0L,
    val isFollowing: Boolean = false,
    val isFriend: Boolean = false,
    val createdAtMs: Long = System.currentTimeMillis()
)

/**
 * Hashtag item for Search -> Hashtags tab and Hashtag feed header.
 */
data class FeedHashtagSummaryItem(
    val hashtag: String,
    val videosCount: Long,
    val totalViewsCount: Long
)

/**
 * Combined search results across Videos, Users, Hashtags, and Sounds.
 */
data class FeedSearchCombinedResults(
    val query: String = "",
    val videos: List<FeedVideoItem> = emptyList(),
    val users: List<NewCreatorUserItem> = emptyList(),
    val hashtags: List<FeedHashtagSummaryItem> = emptyList(),
    val sounds: List<FeedAudioTrackInfo> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * Context mode for the reusable vertical video feed component.
 */
enum class ReusableFeedContextMode {
    MAIN_FEEDS,
    USER_PROFILE_FEED,
    AUDIO_PAGE_FEED,
    HASHTAG_PAGE_FEED,
    SEARCH_VIDEO_FEED
}
