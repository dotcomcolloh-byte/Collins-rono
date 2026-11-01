package com.telefam.company.app.domain

/**
 * Domain model for comments and threaded replies matching TikTok and modern video social platforms.
 */
data class CommentThreadItem(
    val commentId: String,
    val targetId: String,
    val parentCommentId: String? = null,
    val authorUserId: String,
    val authorUsername: String,
    val authorFullName: String,
    val authorAvatarUrl: String? = null,
    val isCreator: Boolean = false,
    val commentText: String,
    val attachmentType: String? = null, // STICKER, GIF, CLIP, PHOTO, GIFT
    val attachmentUrl: String? = null,
    val giftId: String? = null,
    val giftName: String? = null,
    val giftCoinCost: Long = 0L,
    val giftCount: Int = 1,
    val likesCount: Long = 0L,
    val isLikedByMe: Boolean = false,
    val isPinned: Boolean = false,
    val isEdited: Boolean = false,
    val createdAtMs: Long = System.currentTimeMillis(),
    val replies: List<CommentThreadItem> = emptyList(),
    val totalRepliesCount: Int = 0,
    val translatedText: String? = null,
    val isShowingTranslation: Boolean = false
)

/**
 * Mention candidate for @username autocomplete in comment composer.
 */
data class MentionCandidateUser(
    val userId: String,
    val username: String,
    val fullName: String,
    val avatarUrl: String? = null,
    val isVerified: Boolean = false
)
