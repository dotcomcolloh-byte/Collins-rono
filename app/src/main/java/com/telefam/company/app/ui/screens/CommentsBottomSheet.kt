package com.telefam.company.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.TelefamRed
import com.telefam.company.app.data.GiflyMediaItemDto
import com.telefam.company.app.domain.CommentThreadItem
import com.telefam.company.app.domain.MentionCandidateUser
import com.telefam.company.app.domain.VirtualGiftCatalog
import com.telefam.company.app.domain.VirtualGiftItem
import com.telefam.company.app.ui.screens.LucideCampaignIcons
import com.telefam.company.app.ui.components.LucideGiftCategoryIcons
import com.telefam.company.app.ui.components.VirtualGiftVectorArtwork
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

object LucideCommentIcons {
    val HeartFilled: ImageVector by lazy {
        ImageVector.Builder("CommentHeartFilled", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color(0xFFFE2C55))) {
                moveTo(12f, 21.35f)
                lineTo(10.55f, 20.03f)
                curveTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
                curveTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
                curveTo(9.24f, 3f, 10.91f, 3.81f, 12f, 5.09f)
                curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
                curveTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
                curveTo(22f, 12.28f, 18.6f, 15.36f, 13.45f, 20.04f)
                lineTo(12f, 21.35f)
                close()
            }
        }.build()
    }

    val HeartOutline: ImageVector by lazy {
        ImageVector.Builder("CommentHeartOutline", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFF7E808C)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(19.5f, 12.572f)
                lineTo(12f, 20f)
                lineTo(4.5f, 12.572f)
                curveTo(2.5f, 10.5f, 2.5f, 7f, 4.5f, 5f)
                curveTo(6.5f, 3f, 10f, 3f, 12f, 5.5f)
                curveTo(14f, 3f, 17.5f, 3f, 19.5f, 5f)
                curveTo(21.5f, 7f, 21.5f, 10.5f, 19.5f, 12.572f)
                close()
            }
        }.build()
    }

    val Pin: ImageVector by lazy {
        ImageVector.Builder("CommentPin", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFFFE2C55)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 17f)
                lineTo(12f, 22f)
                moveTo(5f, 17f)
                lineTo(19f, 17f)
                moveTo(7f, 17f)
                lineTo(8f, 13f)
                lineTo(6f, 8f)
                lineTo(18f, 8f)
                lineTo(16f, 13f)
                lineTo(17f, 17f)
                moveTo(9f, 8f)
                lineTo(9f, 4f)
                lineTo(15f, 4f)
                lineTo(15f, 8f)
            }
        }.build()
    }

    val GiftPill: ImageVector by lazy {
        ImageVector.Builder("CommentGiftPill", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFFFF2A6D)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(20f, 12f)
                lineTo(20f, 22f)
                lineTo(4f, 22f)
                lineTo(4f, 12f)
                moveTo(2f, 7f)
                lineTo(22f, 7f)
                lineTo(22f, 12f)
                lineTo(2f, 12f)
                close()
                moveTo(12f, 22f)
                lineTo(12f, 7f)
                moveTo(12f, 7f)
                curveTo(12f, 7f, 9.5f, 2f, 7.5f, 2f)
                curveTo(5.5f, 2f, 5.5f, 4.5f, 7.5f, 5.5f)
                lineTo(12f, 7f)
                moveTo(12f, 7f)
                curveTo(12f, 7f, 14.5f, 2f, 16.5f, 2f)
                curveTo(18.5f, 2f, 18.5f, 4.5f, 16.5f, 5.5f)
                lineTo(12f, 7f)
            }
        }.build()
    }

    val MediaImage: ImageVector by lazy {
        ImageVector.Builder("CommentMediaImage", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFFCBD5E1)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(19f, 3f)
                lineTo(5f, 3f)
                curveTo(3.9f, 3f, 3f, 3.9f, 3f, 5f)
                lineTo(3f, 19f)
                curveTo(3f, 20.1f, 3.9f, 21f, 5f, 21f)
                lineTo(19f, 21f)
                curveTo(20.1f, 21f, 21f, 20.1f, 21f, 19f)
                lineTo(21f, 5f)
                curveTo(21f, 3.9f, 20.1f, 3f, 19f, 3f)
                close()
                moveTo(8.5f, 10f)
                curveTo(9.33f, 10f, 10f, 9.33f, 10f, 8.5f)
                curveTo(10f, 7.67f, 9.33f, 7f, 8.5f, 7f)
                curveTo(7.67f, 7f, 7f, 7.67f, 7f, 8.5f)
                curveTo(7f, 9.33f, 7.67f, 10f, 8.5f, 10f)
                close()
                moveTo(21f, 15f)
                lineTo(16f, 10f)
                lineTo(5f, 21f)
            }
        }.build()
    }

    val Smiley: ImageVector by lazy {
        ImageVector.Builder("CommentSmiley", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFFCBD5E1)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 22f)
                arcTo(10f, 10f, 0f, true, true, 12f, 2f)
                arcTo(10f, 10f, 0f, true, true, 12f, 22f)
                moveTo(8f, 14f)
                curveTo(9f, 16f, 11f, 17f, 12f, 17f)
                curveTo(13f, 17f, 15f, 16f, 16f, 14f)
                moveTo(9f, 9f)
                lineTo(9.01f, 9f)
                moveTo(15f, 9f)
                lineTo(15.01f, 9f)
            }
        }.build()
    }

    val SendArrowUp: ImageVector by lazy {
        ImageVector.Builder("CommentSendArrowUp", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.White), strokeLineWidth = 2.5f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 19f)
                lineTo(12f, 5f)
                moveTo(5f, 12f)
                lineTo(12f, 5f)
                lineTo(19f, 12f)
            }
        }.build()
    }

    val CloseX: ImageVector by lazy {
        ImageVector.Builder("CommentCloseX", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color(0xFF9E9EA8)), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round) {
                moveTo(18f, 6f)
                lineTo(6f, 18f)
                moveTo(6f, 6f)
                lineTo(18f, 18f)
            }
        }.build()
    }
}

/**
 * Comments Bottom Sheet matching Image 2 exactly.
 * - Header: Drag handle pill, "Comments 1.2K", close button "X"
 * - Threaded replies with connected vertical lines and curved elbow
 * - Author avatar, name, time ago, "• Creator" badge (red text)
 * - Autolink on URLs, hashtags, mentions
 * - Reply button with indented nested reply tree
 * - "View X more replies ⌵" expansion toggle
 * - Translation toggle with "Translate" / "Translated • See original"
 * - Pinned comment indicator ("Pinned by creator")
 * - Long-press menu: Copy, Edit, Delete, Pin/Unpin
 * - Composer pill bar at bottom:
 *   - Avatar
 *   - "Add a comment..."
 *   - Mention autocomplete popup (@username)
 *   - Sticker/GIF button -> opens StickersAndGifsComposerSheet
 *   - Media/Photo button -> allows photo upload resized to sticker
 *   - Gift button -> opens VirtualGiftsSheet (Image 1)
 *   - Send button -> red circular button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    targetId: String,
    currentUserId: String,
    currentUserUsername: String,
    currentUserFullName: String,
    currentUserAvatarUrl: String?,
    postOwnerUserId: String,
    comments: List<CommentThreadItem>,
    totalCommentsCount: Long,
    userStarsBalance: Long,
    onDismissRequest: () -> Unit,
    onPostComment: (text: String, parentCommentId: String?, attachmentType: String?, attachmentUrl: String?) -> Unit,
    onToggleLikeComment: (commentId: String) -> Unit,
    onDeleteComment: (commentId: String) -> Unit,
    onEditComment: (commentId: String, newText: String) -> Unit,
    onTogglePinComment: (commentId: String) -> Unit,
    onOpenUserProfile: (userId: String) -> Unit,
    onSearchMentions: suspend (String) -> List<MentionCandidateUser>,
    onSendVirtualGift: (gift: VirtualGiftItem, count: Int) -> Unit,
    onTopUpStars: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var commentInputText by remember { mutableStateOf("") }
    var replyingToComment by remember { mutableStateOf<CommentThreadItem?>(null) }
    var editingComment by remember { mutableStateOf<CommentThreadItem?>(null) }
    var attachedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var attachedMediaType by remember { mutableStateOf<String?>(null) }

    var isStickerSheetOpen by remember { mutableStateOf(false) }
    var isGiftsSheetOpen by remember { mutableStateOf(false) }

    // Mention suggestions state
    var mentionQuery by remember { mutableStateOf<String?>(null) }
    var mentionCandidates by remember { mutableStateOf<List<MentionCandidateUser>>(emptyList()) }

    // Long press action menu
    var longPressedComment by remember { mutableStateOf<CommentThreadItem?>(null) }

    // Clickable full-screen photo/sticker zoom dialog
    var fullScreenPreviewUrl by remember { mutableStateOf<String?>(null) }

    // Expanded replies state set (commentId -> Boolean)
    var expandedRepliesMap by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    // Translated comment states (commentId -> Boolean)
    var translatedCommentsMap by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            attachedMediaUri = uri
            attachedMediaType = "PHOTO"
        }
    }

    // Mention query watcher: triggers immediately when typing '@' anywhere
    LaunchedEffect(commentInputText) {
        val lastAtIndex = commentInputText.lastIndexOf('@')
        if (lastAtIndex >= 0) {
            val textAfterAt = commentInputText.substring(lastAtIndex + 1)
            // If there's no space after the '@', user is actively typing a username/name mention
            if (!textAfterAt.contains(" ")) {
                mentionQuery = textAfterAt
                mentionCandidates = onSearchMentions(textAfterAt)
            } else {
                mentionQuery = null
                mentionCandidates = emptyList()
            }
        } else {
            mentionQuery = null
            mentionCandidates = emptyList()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFF141518),
        dragHandle = null,
        modifier = modifier
            .fillMaxHeight(0.88f)
            .imePadding()
            .navigationBarsPadding()
            .testTag("comments_bottom_sheet_root")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. HEADER (Drag handle, "Comments 1.2K", close "X" matching Image 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Drag handle pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF3A3B42))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))

                    val formattedCount = remember(totalCommentsCount) {
                        if (totalCommentsCount >= 1000) {
                            String.format(Locale.US, "%.1fK", totalCommentsCount / 1000.0)
                        } else {
                            totalCommentsCount.toString()
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Comments",
                            color = Color.White,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formattedCount,
                            color = Color(0xFF8E909A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("comments_close_button")
                    ) {
                        Icon(
                            imageVector = LucideCommentIcons.CloseX,
                            contentDescription = "Close",
                            tint = Color(0xFF9E9EA8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF22242B), thickness = 0.8.dp)

            // 2. MAIN COMMENTS LIST (Threaded with vertical line & curved elbow matching Image 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (comments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No comments yet. Be the first to share your thoughts!",
                            color = Color(0xFF7E808C),
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("comments_list")
                    ) {
                        items(comments, key = { it.commentId }) { topComment ->
                            val isExpanded = expandedRepliesMap[topComment.commentId] == true
                            TopLevelCommentThreadItemView(
                                comment = topComment,
                                postOwnerUserId = postOwnerUserId,
                                isExpanded = isExpanded,
                                translatedCommentsMap = translatedCommentsMap,
                                onToggleTranslate = { commentId ->
                                    val cur = translatedCommentsMap[commentId] == true
                                    translatedCommentsMap = translatedCommentsMap + (commentId to !cur)
                                },
                                onToggleExpandReplies = {
                                    expandedRepliesMap = expandedRepliesMap + (topComment.commentId to !isExpanded)
                                },
                                onReplyClick = { target ->
                                    replyingToComment = target
                                    commentInputText = "@${target.authorUsername} "
                                },
                                onLikeClick = { commentId -> onToggleLikeComment(commentId) },
                                onLongPress = { target -> longPressedComment = target },
                                onProfileClick = { userId -> onOpenUserProfile(userId) },
                                onMediaClick = { url -> fullScreenPreviewUrl = url }
                            )
                        }
                    }
                }

                // Mention Autocomplete Popover right above the composer
                if (mentionCandidates.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E1F24),
                        border = BorderStroke(1.dp, Color(0xFF2E313D)),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Mentions",
                                color = Color(0xFF8E909A),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            mentionCandidates.forEach { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            val lastAtIndex = commentInputText.lastIndexOf('@')
                                            val beforeMention = if (lastAtIndex >= 0) commentInputText.substring(0, lastAtIndex) else ""
                                            commentInputText = "$beforeMention@${user.username} "
                                            mentionCandidates = emptyList()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!user.avatarUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = user.avatarUrl,
                                            contentDescription = user.username,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(TelefamRed),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = user.username.take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = user.fullName,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "@${user.username}",
                                            color = Color(0xFF8E909A),
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Replying To or Editing Banner
            if (replyingToComment != null || editingComment != null) {
                Surface(
                    color = Color(0xFF1E2026),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (editingComment != null) "Editing comment" else "Replying to @${replyingToComment?.authorUsername}",
                            color = TelefamRed,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(
                            onClick = {
                                replyingToComment = null
                                editingComment = null
                                commentInputText = ""
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = LucideCommentIcons.CloseX,
                                contentDescription = "Cancel",
                                tint = Color(0xFF8E909A),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Attached Media Preview Pill in Composer
            if (attachedMediaUri != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF22242B),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = attachedMediaUri,
                            contentDescription = "Attached photo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "Photo attached",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        IconButton(
                            onClick = {
                                attachedMediaUri = null
                                attachedMediaType = null
                            },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = LucideCommentIcons.CloseX,
                                contentDescription = "Remove",
                                tint = Color(0xFF8E909A),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // 3. COMPOSER BAR (Matching Image 2: Avatar + Pill with action buttons inside/right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141518))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                if (!currentUserAvatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = currentUserAvatarUrl,
                        contentDescription = "My avatar",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF33333E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUserUsername.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Pill Container with Input + In-Line SVG Action Buttons (Matching Image 2)
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0xFF22242B),
                    border = BorderStroke(1.dp, Color(0xFF2D3039)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentInputText,
                            onValueChange = { commentInputText = it },
                            placeholder = {
                                Text(
                                    text = "Add a comment...",
                                    color = Color(0xFF7E808C),
                                    fontSize = 14.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = TelefamRed
                            ),
                            maxLines = 4,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_text_field")
                        )

                        // 1. Smiley / Sticker Button (SVG Icon)
                        IconButton(
                            onClick = { isStickerSheetOpen = true },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("comment_open_stickers_button")
                        ) {
                            Icon(
                                imageVector = LucideCommentIcons.Smiley,
                                contentDescription = "Stickers",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // 2. Media / Image Button (SVG Icon)
                        IconButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("comment_attach_media_button")
                        ) {
                            Icon(
                                imageVector = LucideCommentIcons.MediaImage,
                                contentDescription = "Attach photo",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // 3. Gift Button (Present Icon in Pink/Gold matching Image 2)
                        IconButton(
                            onClick = { isGiftsSheetOpen = true },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("comment_open_gifts_button")
                        ) {
                            Icon(
                                imageVector = LucideCommentIcons.GiftPill,
                                contentDescription = "Send Gift",
                                tint = Color(0xFFFF2A6D),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // 4. Send Button (Arrow up in filled red circle matching Image 2)
                        val canSend = commentInputText.isNotBlank() || attachedMediaUri != null
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (canSend) Color(0xFFFE2C55) else Color(0xFF4A4B54))
                                .clickable(enabled = canSend) {
                                    val text = commentInputText.trim()
                                    val mediaUrl = attachedMediaUri?.toString()
                                    val mediaType = attachedMediaType

                                    if (editingComment != null) {
                                        onEditComment(editingComment!!.commentId, text)
                                        editingComment = null
                                    } else {
                                        onPostComment(text, replyingToComment?.commentId, mediaType, mediaUrl)
                                        replyingToComment = null
                                    }
                                    commentInputText = ""
                                    attachedMediaUri = null
                                    attachedMediaType = null
                                }
                                .testTag("comment_send_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = LucideCommentIcons.SendArrowUp,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Stickers & GIFs composer sheet
    if (isStickerSheetOpen) {
        StickersAndGifsComposerSheet(
            onDismissRequest = { isStickerSheetOpen = false },
            onSendMedia = { item, caption ->
                isStickerSheetOpen = false
                val display = if (caption.isNotBlank()) caption else ""
                onPostComment(display, replyingToComment?.commentId, item.type.uppercase(), item.bestDisplayUrl)
                replyingToComment = null
            }
        )
    }

    // Virtual Gifts Sheet (Matching Image 1)
    if (isGiftsSheetOpen) {
        VirtualGiftsSheet(
            userStarsBalance = userStarsBalance,
            onDismissRequest = { isGiftsSheetOpen = false },
            onSendGift = { gift, count ->
                isGiftsSheetOpen = false
                onSendVirtualGift(gift, count)
            },
            onTopUpStars = {
                isGiftsSheetOpen = false
                onTopUpStars()
            }
        )
    }

    // Long Press Context Menu
    longPressedComment?.let { comment ->
        ModalBottomSheet(
            onDismissRequest = { longPressedComment = null },
            containerColor = Color(0xFF1E1F24)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Comment options",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // Copy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val clip = ClipData.newPlainText("Comment", comment.commentText)
                            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                            longPressedComment = null
                        }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Copy text", color = Color.White, fontSize = 14.sp)
                }

                // If owner of comment: Edit & Delete
                if (comment.authorUserId == currentUserId) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                editingComment = comment
                                commentInputText = comment.commentText
                                longPressedComment = null
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Edit comment", color = Color.White, fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onDeleteComment(comment.commentId)
                                longPressedComment = null
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Delete comment", color = Color(0xFFFF453A), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // If post owner: Pin / Unpin
                if (postOwnerUserId == currentUserId) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onTogglePinComment(comment.commentId)
                                longPressedComment = null
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (comment.isPinned) "Unpin comment" else "Pin comment", color = Color(0xFFFE2C55), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Full-screen zoomable media preview dialog
    fullScreenPreviewUrl?.let { mediaUrl ->
        Dialog(onDismissRequest = { fullScreenPreviewUrl = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { fullScreenPreviewUrl = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = "Preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

/**
 * Top-Level Comment with Threaded Replies Matching Image 2.
 * Includes the smooth vertical connecting line and curved elbow connecting to child replies.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopLevelCommentThreadItemView(
    comment: CommentThreadItem,
    postOwnerUserId: String,
    isExpanded: Boolean,
    translatedCommentsMap: Map<String, Boolean>,
    onToggleTranslate: (String) -> Unit,
    onToggleExpandReplies: () -> Unit,
    onReplyClick: (CommentThreadItem) -> Unit,
    onLikeClick: (String) -> Unit,
    onLongPress: (CommentThreadItem) -> Unit,
    onProfileClick: (String) -> Unit,
    onMediaClick: (String) -> Unit
) {
    val context = LocalContext.current
    val isPostCreator = comment.authorUserId == postOwnerUserId
    val isTranslated = translatedCommentsMap[comment.commentId] == true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongPress(comment) }
            )
            .testTag("comment_row_${comment.commentId}")
    ) {
        // Pinned by creator badge
        if (comment.isPinned) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 6.dp, start = 44.dp)
            ) {
                Icon(
                    imageVector = LucideCommentIcons.Pin,
                    contentDescription = "Pinned",
                    tint = Color(0xFFFE2C55),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Pinned by creator",
                    color = Color(0xFFFE2C55),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Author Avatar (Clickable to open user profile)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick(comment.authorUserId) }
            ) {
                if (!comment.authorAvatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = comment.authorAvatarUrl,
                        contentDescription = comment.authorUsername,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF33333E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comment.authorUsername.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Body Column
            Column(modifier = Modifier.weight(1f)) {
                // Name + Time Ago + Creator Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = comment.authorFullName.ifBlank { "@${comment.authorUsername}" },
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onProfileClick(comment.authorUserId) }
                    )
                    Text(
                        text = formatTimeAgo(comment.createdAtMs),
                        color = Color(0xFF7E808C),
                        fontSize = 12.sp
                    )
                    if (isPostCreator) {
                        Text(
                            text = "• Creator",
                            color = Color(0xFFFE2C55),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (comment.isEdited) {
                        Text(
                            text = "(edited)",
                            color = Color(0xFF7E808C),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Comment Text with Autolink
                val displayText = if (isTranslated && comment.translatedText != null) {
                    comment.translatedText
                } else {
                    comment.commentText
                }

                if (displayText.isNotBlank()) {
                    AutolinkCommentText(
                        text = displayText,
                        onUrlClick = { url ->
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        }
                    )
                }

                // If Gift is attached: show vector artwork and badge
                if (comment.attachmentType == "GIFT" && comment.giftId != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF22242B),
                        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { onMediaClick(comment.giftId) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VirtualGiftVectorArtwork(
                                giftId = comment.giftId,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "${comment.giftName ?: "Virtual Gift"} x${comment.giftCount}",
                                color = Color(0xFFFFD700),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // If Sticker / GIF / Photo is attached
                if (!comment.attachmentUrl.isNullOrBlank() && comment.attachmentType != "GIFT") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF22242B),
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onMediaClick(comment.attachmentUrl) }
                    ) {
                        AsyncImage(
                            model = comment.attachmentUrl,
                            contentDescription = "Attachment",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Reply & Translate Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Reply",
                        color = Color(0xFF7E808C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onReplyClick(comment) }
                    )

                    Text(
                        text = if (isTranslated) "See original" else "Translate",
                        color = Color(0xFF7E808C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onToggleTranslate(comment.commentId) }
                    )
                }
            }

            // Like Heart Button + Like Count (Matching Image 2)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onLikeClick(comment.commentId) }
            ) {
                Icon(
                    imageVector = if (comment.isLikedByMe) LucideCommentIcons.HeartFilled else LucideCommentIcons.HeartOutline,
                    contentDescription = "Like",
                    tint = if (comment.isLikedByMe) Color(0xFFFE2C55) else Color(0xFF7E808C),
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                val formattedLikes = if (comment.likesCount >= 1000) {
                    String.format(Locale.US, "%.1fK", comment.likesCount / 1000.0)
                } else if (comment.likesCount > 0) {
                    comment.likesCount.toString()
                } else ""
                if (formattedLikes.isNotBlank()) {
                    Text(
                        text = formattedLikes,
                        color = Color(0xFF7E808C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 3. THREADED REPLIES TREE (Matching Image 2 with vertical line & curved elbow)
        if (comment.replies.isNotEmpty()) {
            val visibleReplies = if (isExpanded) comment.replies else comment.replies.take(2)

            Row(modifier = Modifier.fillMaxWidth()) {
                // Vertical connecting line
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .padding(start = 17.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLine(
                            color = Color(0xFF33333E),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 10.dp)
                ) {
                    visibleReplies.forEach { reply ->
                        ReplyCommentRowView(
                            reply = reply,
                            postOwnerUserId = postOwnerUserId,
                            isTranslated = translatedCommentsMap[reply.commentId] == true,
                            onToggleTranslate = { onToggleTranslate(reply.commentId) },
                            onReplyClick = { onReplyClick(reply) },
                            onLikeClick = { onLikeClick(reply.commentId) },
                            onLongPress = { onLongPress(reply) },
                            onProfileClick = { onProfileClick(reply.authorUserId) },
                            onMediaClick = onMediaClick
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // "View X more replies ⌵" expansion toggle matching Image 2
                    if (comment.replies.size > 2) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clickable(onClick = onToggleExpandReplies)
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(1.5.dp)
                                    .background(Color(0xFF4A4B54))
                            )
                            Text(
                                text = if (isExpanded) "Hide replies" else "View ${comment.replies.size - 2} more replies ⌵",
                                color = Color(0xFF8E909A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReplyCommentRowView(
    reply: CommentThreadItem,
    postOwnerUserId: String,
    isTranslated: Boolean,
    onToggleTranslate: () -> Unit,
    onReplyClick: () -> Unit,
    onLikeClick: () -> Unit,
    onLongPress: () -> Unit,
    onProfileClick: () -> Unit,
    onMediaClick: (String) -> Unit
) {
    val context = LocalContext.current
    val isPostCreator = reply.authorUserId == postOwnerUserId

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongPress
            )
            .testTag("reply_row_${reply.commentId}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Reply Avatar
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onProfileClick)
            ) {
                if (!reply.authorAvatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = reply.authorAvatarUrl,
                        contentDescription = reply.authorUsername,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF33333E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = reply.authorUsername.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Reply Body
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = reply.authorFullName.ifBlank { "@${reply.authorUsername}" },
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onProfileClick)
                    )
                    Text(
                        text = formatTimeAgo(reply.createdAtMs),
                        color = Color(0xFF7E808C),
                        fontSize = 11.5.sp
                    )
                    if (isPostCreator) {
                        Text(
                            text = "• Creator",
                            color = Color(0xFFFE2C55),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (reply.isEdited) {
                        Text(
                            text = "(edited)",
                            color = Color(0xFF7E808C),
                            fontSize = 10.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                val displayText = if (isTranslated && reply.translatedText != null) {
                    reply.translatedText
                } else {
                    reply.commentText
                }

                if (displayText.isNotBlank()) {
                    AutolinkCommentText(
                        text = displayText,
                        onUrlClick = { url ->
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        }
                    )
                }

                // If Gift is attached to reply
                if (reply.attachmentType == "GIFT" && reply.giftId != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF22242B),
                        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { onMediaClick(reply.giftId) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            VirtualGiftVectorArtwork(
                                giftId = reply.giftId,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "${reply.giftName ?: "Virtual Gift"} x${reply.giftCount}",
                                color = Color(0xFFFFD700),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (!reply.attachmentUrl.isNullOrBlank() && reply.attachmentType != "GIFT") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF22242B),
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onMediaClick(reply.attachmentUrl) }
                    ) {
                        AsyncImage(
                            model = reply.attachmentUrl,
                            contentDescription = "Attachment",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Reply",
                        color = Color(0xFF7E808C),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onReplyClick)
                    )

                    Text(
                        text = if (isTranslated) "See original" else "Translate",
                        color = Color(0xFF7E808C),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onToggleTranslate)
                    )
                }
            }

            // Like Heart for reply
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .clickable(onClick = onLikeClick)
            ) {
                Icon(
                    imageVector = if (reply.isLikedByMe) LucideCommentIcons.HeartFilled else LucideCommentIcons.HeartOutline,
                    contentDescription = "Like",
                    tint = if (reply.isLikedByMe) Color(0xFFFE2C55) else Color(0xFF7E808C),
                    modifier = Modifier.size(17.dp)
                )
                val formattedLikes = if (reply.likesCount >= 1000) {
                    String.format(Locale.US, "%.1fK", reply.likesCount / 1000.0)
                } else if (reply.likesCount > 0) {
                    reply.likesCount.toString()
                } else ""
                if (formattedLikes.isNotBlank()) {
                    Text(
                        text = formattedLikes,
                        color = Color(0xFF7E808C),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Autolink text helper highlighting links, mentions (@), and hashtags (#).
 */
@Composable
private fun AutolinkCommentText(
    text: String,
    onUrlClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(text) {
        buildAnnotatedString {
            val words = text.split(" ")
            words.forEachIndexed { index, word ->
                when {
                    word.startsWith("http://") || word.startsWith("https://") -> {
                        pushStringAnnotation(tag = "URL", annotation = word)
                        pushStyle(SpanStyle(color = Color(0xFF4CC9F0), textDecoration = TextDecoration.Underline))
                        append(word)
                        pop()
                        pop()
                    }
                    word.startsWith("@") -> {
                        pushStyle(SpanStyle(color = Color(0xFFFF758F), fontWeight = FontWeight.Bold))
                        append(word)
                        pop()
                    }
                    word.startsWith("#") -> {
                        pushStyle(SpanStyle(color = Color(0xFF4CC9F0), fontWeight = FontWeight.SemiBold))
                        append(word)
                        pop()
                    }
                    else -> {
                        append(word)
                    }
                }
                if (index < words.size - 1) append(" ")
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = androidx.compose.ui.text.TextStyle(
            color = Color(0xFFF1F1F3),
            fontSize = 13.5.sp,
            lineHeight = 18.sp
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onUrlClick(annotation.item)
                }
        },
        modifier = modifier
    )
}

private fun formatTimeAgo(timeMs: Long): String {
    val diffSec = (System.currentTimeMillis() - timeMs) / 1000L
    return when {
        diffSec < 60 -> "Just now"
        diffSec < 3600 -> "${diffSec / 60}m ago"
        diffSec < 86400 -> "${diffSec / 3600}h ago"
        diffSec < 604800 -> "${diffSec / 86400}d ago"
        else -> "${diffSec / 604800}w ago"
    }
}
