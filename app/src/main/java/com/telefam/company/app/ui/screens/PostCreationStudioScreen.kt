package com.telefam.company.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ui.theme.TelefamError
import com.example.ui.theme.TelefamRed
import com.telefam.company.app.domain.DiscoverableUserItem
import com.telefam.company.app.domain.EditablePhotoStudioItem
import com.telefam.company.app.domain.EditableVideoStudioState
import com.telefam.company.app.domain.PostBackgroundTheme
import com.telefam.company.app.domain.PostBrushStroke
import com.telefam.company.app.domain.PostColorFilterPreset
import com.telefam.company.app.domain.PostCropPreset
import com.telefam.company.app.domain.PostMusicTrackItem
import com.telefam.company.app.domain.PostStudioColorPalette
import com.telefam.company.app.domain.PostStudioMode
import com.telefam.company.app.domain.PostTextAutoScaler
import com.telefam.company.app.domain.PostTextOverlayLayer
import com.telefam.company.app.domain.PostTypographyFont
import com.telefam.company.app.domain.PostUploadLifecycleStatus
import com.telefam.company.app.domain.PostVisibilityAudience
import com.telefam.company.app.ui.TelefamUiState
import com.telefam.company.app.ui.components.LucideIconKind
import com.telefam.company.app.ui.components.TelefamAttachmentDeviceHelper
import com.telefam.company.app.ui.components.TelefamLucideIcon
import java.io.File
import java.util.UUID
import kotlin.math.roundToInt

private enum class StudioActiveToolTab(val label: String, val icon: LucideIconKind) {
    NONE("Close", LucideIconKind.CLOSE_X),
    CROP_TRANSFORM("Crop & Rotate", LucideIconKind.CROP_FRAME),
    COVER_TEXT("Text", LucideIconKind.TYPE_TEXT),
    PEN_BRUSH("Brush", LucideIconKind.BRUSH_PEN),
    FILTERS("Filters", LucideIconKind.SLIDERS_FILTER)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostCreationStudioScreen(
    uiState: TelefamUiState,
    onImportPhotos: (uris: List<Uri>, existingCount: Int, onImported: (List<EditablePhotoStudioItem>) -> Unit) -> Unit,
    onImportVideo: (uri: Uri, onImported: (EditableVideoStudioState) -> Unit) -> Unit,
    onExtractVideoCoverAt: (videoState: EditableVideoStudioState, timestampMs: Long, onExtracted: (String?) -> Unit) -> Unit,
    onSearchMentions: (String) -> Unit,
    onClearMentions: () -> Unit,
    onSearchMusic: (String) -> Unit,
    onClearStudioError: () -> Unit,
    onPublishPost: (
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
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Primary Studio Mode (Text, Camera, Photo, Video)
    var studioMode by rememberSaveable { mutableStateOf(PostStudioMode.TEXT) }

    // Text Post Studio State (12 background colours, 10 Google fonts, auto-scaled characters)
    var textPostBody by rememberSaveable { mutableStateOf("") }
    var selectedBgTheme by rememberSaveable { mutableStateOf(PostBackgroundTheme.CRIMSON_VELVET) }
    var selectedTextFont by rememberSaveable { mutableStateOf(PostTypographyFont.PLUS_JAKARTA) }
    val textModeOverlayLayers = remember { mutableStateListOf<PostTextOverlayLayer>() }
    val textModeBrushStrokes = remember { mutableStateListOf<PostBrushStroke>() }

    // Photo Studio State (up to 10 photos)
    val photoItems = remember { mutableStateListOf<EditablePhotoStudioItem>() }
    var activePhotoIndex by rememberSaveable { mutableIntStateOf(0) }

    // Video Studio State (CapCut-style I-Frame timeline, trim, cover, audio mixer)
    var videoState by remember { mutableStateOf<EditableVideoStudioState?>(null) }

    // Shared Studio Tool State (Cover Text & Pen Brush)
    var activeToolTab by remember { mutableStateOf(StudioActiveToolTab.NONE) }
    var selectedOverlayLayerId by remember { mutableStateOf<String?>(null) }
    var coverTextDraft by remember { mutableStateOf("") }
    var activeTextColorArgb by remember { mutableLongStateOf(0xFFFFFFFFL) }
    var activeOverlayFont by remember { mutableStateOf(PostTypographyFont.PLUS_JAKARTA) }
    var activeTextPillBg by remember { mutableStateOf(true) }

    var activeBrushColorArgb by remember { mutableLongStateOf(0xFFC80815L) }
    var activeBrushSizePx by remember { mutableFloatStateOf(14f) }

    // Song / Music State
    var selectedMusicTrack by remember { mutableStateOf<PostMusicTrackItem?>(null) }
    var showMusicSheet by remember { mutableStateOf(false) }

    // Caption, @username Tagging & #hashtags (max 5)
    var postCaption by rememberSaveable { mutableStateOf("") }
    val taggedUsernames = remember { mutableStateListOf<String>() }
    val hashtagsList = remember { mutableStateListOf<String>() }
    var hashtagInputDraft by remember { mutableStateOf("") }
    var activeMentionSearchToken by remember { mutableStateOf<String?>(null) }

    // Privacy & Advanced Creator Settings
    var visibilityAudience by rememberSaveable { mutableStateOf(PostVisibilityAudience.ANYONE) }
    var allowComments by rememberSaveable { mutableStateOf(true) }
    var allowDuetsAndStitch by rememberSaveable { mutableStateOf(true) }
    var allowHighQualityDownload by rememberSaveable { mutableStateOf(true) }

    // Camera capture temp URIs
    var pendingCameraPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Pre-populate when user taps "Use this sound" from Audio Page or "Edit" on an owned video in Feeds
    LaunchedEffect(uiState.preselectedPostMusicTrack, uiState.editingFeedVideoItem) {
        uiState.preselectedPostMusicTrack?.let { preselected ->
            selectedMusicTrack = preselected
        }
        uiState.editingFeedVideoItem?.let { editVideo ->
            postCaption = editVideo.caption
            hashtagsList.clear()
            hashtagsList.addAll(editVideo.hashtags.take(5))
            selectedMusicTrack = editVideo.audio.toPostMusicTrackItem()
            visibilityAudience = PostVisibilityAudience.fromWire(editVideo.visibilityAudience)
            allowComments = editVideo.allowComments
            allowDuetsAndStitch = editVideo.allowDuetStitch
            allowHighQualityDownload = editVideo.allowDownload
        }
    }
    var pendingCameraVideoUri by remember { mutableStateOf<Uri?>(null) }

    // Launchers for Photo Picker (up to 10), Video Picker, and Native Camera
    val multiPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onImportPhotos(uris, photoItems.size) { imported ->
                val startIdx = photoItems.size
                photoItems.addAll(imported)
                activePhotoIndex = startIdx.coerceIn(0, (photoItems.size - 1).coerceAtLeast(0))
                studioMode = PostStudioMode.PHOTO
            }
        }
    }

    val singlePhotoPickerFallback = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImportPhotos(listOf(uri), photoItems.size) { imported ->
                val startIdx = photoItems.size
                photoItems.addAll(imported)
                activePhotoIndex = startIdx.coerceIn(0, (photoItems.size - 1).coerceAtLeast(0))
                studioMode = PostStudioMode.PHOTO
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImportVideo(uri) { importedState ->
                videoState = importedState
                studioMode = PostStudioMode.VIDEO
            }
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraPhotoUri
        if (success && uri != null) {
            onImportPhotos(listOf(uri), photoItems.size) { imported ->
                val startIdx = photoItems.size
                photoItems.addAll(imported)
                activePhotoIndex = startIdx.coerceIn(0, (photoItems.size - 1).coerceAtLeast(0))
                studioMode = PostStudioMode.PHOTO
            }
        }
    }

    val captureVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        val uri = pendingCameraVideoUri
        if (success && uri != null) {
            onImportVideo(uri) { importedState ->
                videoState = importedState
                studioMode = PostStudioMode.VIDEO
            }
        }
    }

    var pendingCameraActionAfterPermission by remember { mutableStateOf<String?>(null) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val cameraGranted = perms[Manifest.permission.CAMERA] == true
        if (cameraGranted) {
            when (pendingCameraActionAfterPermission) {
                "PHOTO" -> {
                    val uri = TelefamAttachmentDeviceHelper.createTempCaptureUri(context, "cam_post", ".jpg")
                    pendingCameraPhotoUri = uri
                    if (uri != null) takePictureLauncher.launch(uri)
                }
                "VIDEO" -> {
                    val uri = TelefamAttachmentDeviceHelper.createTempCaptureUri(context, "vid_post", ".mp4")
                    pendingCameraVideoUri = uri
                    if (uri != null) captureVideoLauncher.launch(uri)
                }
            }
        }
        pendingCameraActionAfterPermission = null
    }

    fun launchCameraCapture(isVideo: Boolean) {
        val hasCam = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasCam) {
            if (isVideo) {
                val uri = TelefamAttachmentDeviceHelper.createTempCaptureUri(context, "vid_post", ".mp4")
                pendingCameraVideoUri = uri
                if (uri != null) captureVideoLauncher.launch(uri)
            } else {
                val uri = TelefamAttachmentDeviceHelper.createTempCaptureUri(context, "cam_post", ".jpg")
                pendingCameraPhotoUri = uri
                if (uri != null) takePictureLauncher.launch(uri)
            }
        } else {
            pendingCameraActionAfterPermission = if (isVideo) "VIDEO" else "PHOTO"
            cameraPermissionLauncher.launch(
                if (isVideo) arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                else arrayOf(Manifest.permission.CAMERA)
            )
        }
    }

    // Detect `@username` typing in text post or caption to load matching users from Room
    fun inspectTextForMentionsAndHashtags(input: String) {
        val extractedHashes = PostTextAutoScaler.extractHashtags(input)
        extractedHashes.forEach { tag ->
            if (hashtagsList.size < PostTextAutoScaler.MAX_HASHTAGS && tag !in hashtagsList) {
                hashtagsList.add(tag)
            }
        }
        val lastToken = input.substringAfterLast(' ', input).substringAfterLast('\n')
        if (lastToken.startsWith("@")) {
            val handleQuery = lastToken.removePrefix("@")
            activeMentionSearchToken = lastToken
            onSearchMentions(handleQuery)
        } else {
            if (activeMentionSearchToken != null) {
                activeMentionSearchToken = null
                onClearMentions()
            }
        }
    }

    // Current active overlay layers & brush strokes helpers
    val activePhotoItem = photoItems.getOrNull(activePhotoIndex.coerceIn(0, (photoItems.size - 1).coerceAtLeast(0)))

    fun currentTextLayers(): List<PostTextOverlayLayer> = when (studioMode) {
        PostStudioMode.TEXT -> textModeOverlayLayers
        PostStudioMode.PHOTO, PostStudioMode.CAMERA -> activePhotoItem?.textLayers ?: emptyList()
        PostStudioMode.VIDEO -> videoState?.textLayers ?: emptyList()
    }

    fun updateCurrentTextLayers(transform: (List<PostTextOverlayLayer>) -> List<PostTextOverlayLayer>) {
        when (studioMode) {
            PostStudioMode.TEXT -> {
                val updated = transform(textModeOverlayLayers.toList())
                textModeOverlayLayers.clear()
                textModeOverlayLayers.addAll(updated)
            }
            PostStudioMode.PHOTO, PostStudioMode.CAMERA -> {
                val idx = activePhotoIndex.coerceIn(0, (photoItems.size - 1).coerceAtLeast(0))
                val cur = photoItems.getOrNull(idx) ?: return
                photoItems[idx] = cur.copy(textLayers = transform(cur.textLayers))
            }
            PostStudioMode.VIDEO -> {
                val cur = videoState ?: return
                videoState = cur.copy(textLayers = transform(cur.textLayers))
            }
        }
    }

    fun currentBrushStrokes(): List<PostBrushStroke> = when (studioMode) {
        PostStudioMode.TEXT -> textModeBrushStrokes
        PostStudioMode.PHOTO, PostStudioMode.CAMERA -> activePhotoItem?.brushStrokes ?: emptyList()
        PostStudioMode.VIDEO -> videoState?.brushStrokes ?: emptyList()
    }

    fun updateCurrentBrushStrokes(transform: (List<PostBrushStroke>) -> List<PostBrushStroke>) {
        when (studioMode) {
            PostStudioMode.TEXT -> {
                val updated = transform(textModeBrushStrokes.toList())
                textModeBrushStrokes.clear()
                textModeBrushStrokes.addAll(updated)
            }
            PostStudioMode.PHOTO, PostStudioMode.CAMERA -> {
                val idx = activePhotoIndex.coerceIn(0, (photoItems.size - 1).coerceAtLeast(0))
                val cur = photoItems.getOrNull(idx) ?: return
                photoItems[idx] = cur.copy(brushStrokes = transform(cur.brushStrokes))
            }
            PostStudioMode.VIDEO -> {
                val cur = videoState ?: return
                videoState = cur.copy(brushStrokes = transform(cur.brushStrokes))
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("post_creation_studio_screen")
    ) {
        val isWideStudioLayout = maxWidth >= 760.dp

        val canvasPaneContent: @Composable (Modifier) -> Unit = { paneMod ->
            Column(
                modifier = paneMod,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Top Studio Header + Minimal Background Upload Status Pill ("Uploading" / "Uploaded" ONLY)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.SPARKLES_STUDIO,
                            tint = TelefamRed,
                            size = 20.dp
                        )
                        Text(
                            text = "Create Post",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Minimalist Background Upload Progress Pill ("Uploading" / "Uploaded" ONLY)
                    val uploadStatus = uiState.postUploadState.status
                    if (uploadStatus == PostUploadLifecycleStatus.UPLOADING ||
                        uploadStatus == PostUploadLifecycleStatus.UPLOADED
                    ) {
                        Surface(
                            color = if (uploadStatus == PostUploadLifecycleStatus.UPLOADED) {
                                Color(0xFF1B5E20).copy(alpha = 0.16f)
                            } else {
                                TelefamRed.copy(alpha = 0.12f)
                            },
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (uploadStatus == PostUploadLifecycleStatus.UPLOADED) {
                                    Color(0xFF2E7D32)
                                } else {
                                    TelefamRed.copy(alpha = 0.45f)
                                }
                            ),
                            modifier = Modifier.testTag("post_background_upload_status_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (uploadStatus == PostUploadLifecycleStatus.UPLOADING) {
                                    LinearProgressIndicator(
                                        progress = { uiState.postUploadState.progressFraction.coerceIn(0.05f, 1f) },
                                        color = TelefamRed,
                                        trackColor = TelefamRed.copy(alpha = 0.2f),
                                        modifier = Modifier
                                            .width(48.dp)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(50))
                                    )
                                    Text(
                                        text = "Uploading",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TelefamRed
                                    )
                                } else {
                                    TelefamLucideIcon(
                                        kind = LucideIconKind.CHECK_SINGLE,
                                        tint = Color(0xFF2E7D32),
                                        size = 14.dp
                                    )
                                    Text(
                                        text = "Uploaded",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Four SVG Lucide Mode Switcher Buttons: Text, Camera, Photo, Video
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_studio_mode_bar"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf(
                        PostStudioMode.TEXT to LucideIconKind.TYPE_TEXT,
                        PostStudioMode.CAMERA to LucideIconKind.CAMERA,
                        PostStudioMode.PHOTO to LucideIconKind.MEDIA_GALLERY,
                        PostStudioMode.VIDEO to LucideIconKind.CLIP_VIDEO
                    )
                    modes.forEach { (mode, iconKind) ->
                        val isSelected = studioMode == mode
                        Surface(
                            color = if (isSelected) TelefamRed else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) TelefamRed else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable {
                                    studioMode = mode
                                    activeToolTab = StudioActiveToolTab.NONE
                                    onClearStudioError()
                                }
                                .testTag("post_mode_btn_${mode.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TelefamLucideIcon(
                                    kind = iconKind,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    size = 17.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mode.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Error Banner (if rate limit or validation triggered)
                uiState.postStudioErrorBanner?.let { errText ->
                    Surface(
                        color = TelefamError.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, TelefamError.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_studio_error_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = errText,
                                color = TelefamError,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable { onClearStudioError() },
                                contentAlignment = Alignment.Center
                            ) {
                                TelefamLucideIcon(
                                    kind = LucideIconKind.CLOSE_X,
                                    tint = TelefamError,
                                    size = 15.dp
                                )
                            }
                        }
                    }
                }

                // 3. Mode-Specific Interactive Studio Canvas
                when (studioMode) {
                    PostStudioMode.TEXT -> {
                        InteractiveTextPostStudioCanvas(
                            text = textPostBody,
                            onTextChange = { updated ->
                                textPostBody = updated
                            },
                            backgroundTheme = selectedBgTheme,
                            typographyFont = selectedTextFont
                        )

                        // Background Color Swatch Selector
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TelefamLucideIcon(
                                    kind = LucideIconKind.PALETTE_COLOR,
                                    tint = TelefamRed,
                                    size = 15.dp
                                )
                                Text(
                                    text = "Background",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("text_post_bg_color_row")
                            ) {
                                items(PostBackgroundTheme.entries, key = { it.id }) { theme ->
                                    val isChosen = selectedBgTheme == theme
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(theme.startColor, theme.endColor)
                                                )
                                            )
                                            .border(
                                                width = if (isChosen) 2.5.dp else 1.dp,
                                                color = if (isChosen) TelefamRed else Color.White.copy(alpha = 0.35f),
                                                shape = CircleShape
                                            )
                                            .clickable { selectedBgTheme = theme }
                                            .testTag("bg_color_swatch_${theme.id}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isChosen) {
                                            TelefamLucideIcon(
                                                kind = LucideIconKind.CHECK_SINGLE,
                                                tint = theme.textColor,
                                                size = 16.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Typography Font Selector
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TelefamLucideIcon(
                                    kind = LucideIconKind.TYPE_TEXT,
                                    tint = TelefamRed,
                                    size = 15.dp
                                )
                                Text(
                                    text = "Font",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("text_post_font_row")
                            ) {
                                items(PostTypographyFont.entries, key = { it.id }) { fontItem ->
                                    val isChosen = selectedTextFont == fontItem
                                    val itemFontFamily = remember(fontItem) { fontItem.toComposeFontFamily() }
                                    Surface(
                                        color = if (isChosen) TelefamRed else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isChosen) TelefamRed else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                        ),
                                        modifier = Modifier
                                            .clickable { selectedTextFont = fontItem }
                                            .testTag("font_choice_chip_${fontItem.id}")
                                    ) {
                                        Text(
                                            text = fontItem.displayName,
                                            fontFamily = itemFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    PostStudioMode.CAMERA -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(TelefamRed.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TelefamLucideIcon(
                                        kind = LucideIconKind.CAMERA,
                                        tint = TelefamRed,
                                        size = 30.dp
                                    )
                                }
                                Text(
                                    text = "Camera",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { launchCameraCapture(isVideo = false) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = TelefamRed,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("camera_capture_photo_btn")
                                    ) {
                                        TelefamLucideIcon(
                                            kind = LucideIconKind.CAMERA,
                                            tint = Color.White,
                                            size = 17.dp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Take Photo", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { launchCameraCapture(isVideo = true) },
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.5.dp, TelefamRed),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("camera_record_video_btn")
                                    ) {
                                        TelefamLucideIcon(
                                            kind = LucideIconKind.CLIP_VIDEO,
                                            tint = TelefamRed,
                                            size = 17.dp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Record Video", color = TelefamRed, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    PostStudioMode.PHOTO -> {
                        if (photoItems.isEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        runCatching {
                                            multiPhotoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }.onFailure {
                                            singlePhotoPickerFallback.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                    }
                                    .testTag("photo_studio_empty_picker_card")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(26.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(TelefamRed.copy(alpha = 0.14f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        TelefamLucideIcon(
                                            kind = LucideIconKind.MEDIA_GALLERY,
                                            tint = TelefamRed,
                                            size = 30.dp
                                        )
                                    }
                                    Text(
                                        text = "Select Photos",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Button(
                                        onClick = {
                                            runCatching {
                                                multiPhotoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            }.onFailure {
                                                singlePhotoPickerFallback.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = TelefamRed,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        TelefamLucideIcon(
                                            kind = LucideIconKind.PLUS,
                                            tint = Color.White,
                                            size = 16.dp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Choose Photos", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else if (activePhotoItem != null) {
                            // Multi-photo thumbnail carousel strip (1..10 photos)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Photos (${photoItems.size}/10)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (photoItems.size < 10) {
                                    Surface(
                                        color = TelefamRed.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(50),
                                        modifier = Modifier.clickable {
                                            runCatching {
                                                multiPhotoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            }.onFailure {
                                                singlePhotoPickerFallback.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            TelefamLucideIcon(
                                                kind = LucideIconKind.PLUS,
                                                tint = TelefamRed,
                                                size = 14.dp
                                            )
                                            Text(
                                                text = "Add Photo",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TelefamRed
                                            )
                                        }
                                    }
                                }
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(photoItems, key = { _, it -> it.itemId }) { idx, pItem ->
                                    val isCurrent = idx == activePhotoIndex
                                    Box(
                                        modifier = Modifier
                                            .size(62.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(
                                                width = if (isCurrent) 2.5.dp else 1.dp,
                                                color = if (isCurrent) TelefamRed else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { activePhotoIndex = idx }
                                    ) {
                                        AsyncImage(
                                            model = File(pItem.sandboxFilePath),
                                            contentDescription = "Photo ${idx + 1}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(3.dp)
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.65f))
                                                .clickable {
                                                    photoItems.removeAt(idx)
                                                    activePhotoIndex = activePhotoIndex.coerceIn(
                                                        0,
                                                        (photoItems.size - 1).coerceAtLeast(0)
                                                    )
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            TelefamLucideIcon(
                                                kind = LucideIconKind.CLOSE_X,
                                                tint = Color.White,
                                                size = 11.dp
                                            )
                                        }
                                    }
                                }
                            }

                            InteractivePhotoStudioCanvas(
                                item = activePhotoItem,
                                onUpdatePhotoTransform = { z, px, py ->
                                    val idx = activePhotoIndex.coerceIn(0, photoItems.size - 1)
                                    photoItems[idx] = photoItems[idx].copy(
                                        zoomScale = z,
                                        panXNorm = px,
                                        panYNorm = py
                                    )
                                },
                                selectedLayerId = selectedOverlayLayerId,
                                onSelectLayer = { selectedOverlayLayerId = it },
                                onUpdateLayer = { updatedLayer ->
                                    updateCurrentTextLayers { list ->
                                        list.map { if (it.layerId == updatedLayer.layerId) updatedLayer else it }
                                    }
                                },
                                isDrawingBrush = activeToolTab == StudioActiveToolTab.PEN_BRUSH,
                                activeBrushColor = activeBrushColorArgb,
                                activeBrushWidthPx = activeBrushSizePx,
                                onAppendBrushStroke = { stroke ->
                                    updateCurrentBrushStrokes { it + stroke }
                                }
                            )
                        }
                    }

                    PostStudioMode.VIDEO -> {
                        val currentVideo = videoState
                        if (currentVideo == null) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        videoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    }
                                    .testTag("video_studio_empty_picker_card")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(26.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(TelefamRed.copy(alpha = 0.14f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        TelefamLucideIcon(
                                            kind = LucideIconKind.CLIP_VIDEO,
                                            tint = TelefamRed,
                                            size = 30.dp
                                        )
                                    }
                                    Text(
                                        text = "Select Video",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Button(
                                        onClick = {
                                            videoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = TelefamRed,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        TelefamLucideIcon(
                                            kind = LucideIconKind.PLUS,
                                            tint = Color.White,
                                            size = 16.dp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Choose Video", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentVideo.autoExtractedCaption,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Surface(
                                    color = TelefamRed.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.clickable {
                                        videoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    }
                                ) {
                                    Text(
                                        text = "Replace Video",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TelefamRed,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            InteractiveVideoStudioCanvasAndTimeline(
                                videoState = currentVideo,
                                selectedMusic = selectedMusicTrack,
                                selectedLayerId = selectedOverlayLayerId,
                                onSelectLayer = { selectedOverlayLayerId = it },
                                onUpdateLayer = { updatedLayer ->
                                    updateCurrentTextLayers { list ->
                                        list.map { if (it.layerId == updatedLayer.layerId) updatedLayer else it }
                                    }
                                },
                                isDrawingBrush = activeToolTab == StudioActiveToolTab.PEN_BRUSH,
                                activeBrushColor = activeBrushColorArgb,
                                activeBrushWidthPx = activeBrushSizePx,
                                onAppendBrushStroke = { stroke ->
                                    updateCurrentBrushStrokes { it + stroke }
                                },
                                onUpdateTrimRange = { startMs, endMs ->
                                    videoState = currentVideo.copy(
                                        trimStartMs = startMs,
                                        trimEndMs = endMs
                                    )
                                },
                                onSelectCoverTimestamp = { ts ->
                                    onExtractVideoCoverAt(currentVideo, ts) { newCoverPath ->
                                        videoState = videoState?.copy(
                                            selectedCoverTimeMs = ts,
                                            coverFrameFilePath = newCoverPath ?: currentVideo.coverFrameFilePath
                                        )
                                    }
                                },
                                onUpdateOriginalVolume = { vol ->
                                    videoState = currentVideo.copy(originalVideoVolume = vol)
                                }
                            )
                        }
                    }
                }

                // 4. Unified Studio Editing Toolbar (Crop & Rotate, Cover Text, Pen Brush, Filters, Song)
                val showStudioTools = (studioMode == PostStudioMode.PHOTO && photoItems.isNotEmpty()) ||
                    (studioMode == PostStudioMode.VIDEO && videoState != null)

                if (showStudioTools) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val toolButtons = buildList {
                            if (studioMode == PostStudioMode.PHOTO) {
                                add(StudioActiveToolTab.CROP_TRANSFORM)
                            }
                            add(StudioActiveToolTab.COVER_TEXT)
                            add(StudioActiveToolTab.PEN_BRUSH)
                            if (studioMode == PostStudioMode.PHOTO || studioMode == PostStudioMode.VIDEO) {
                                add(StudioActiveToolTab.FILTERS)
                            }
                        }

                        toolButtons.forEach { tab ->
                            val isSelected = activeToolTab == tab
                            Surface(
                                color = if (isSelected) TelefamRed else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(42.dp)
                                    .clickable {
                                        activeToolTab = if (isSelected) StudioActiveToolTab.NONE else tab
                                    }
                                    .testTag("studio_tool_btn_${tab.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TelefamLucideIcon(
                                        kind = tab.icon,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        size = 16.dp
                                    )
                                    Text(
                                        text = tab.label,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Add Free Song Button
                        Surface(
                            color = if (selectedMusicTrack != null) TelefamRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (selectedMusicTrack != null) TelefamRed else Color.Transparent
                            ),
                            modifier = Modifier
                                .height(42.dp)
                                .clickable {
                                    showMusicSheet = true
                                    if (uiState.postMusicSearchResults.isEmpty()) {
                                        onSearchMusic("")
                                    }
                                }
                                .testTag("studio_add_music_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TelefamLucideIcon(
                                    kind = LucideIconKind.MUSIC_AUDIO,
                                    tint = if (selectedMusicTrack != null) TelefamRed else MaterialTheme.colorScheme.onSurface,
                                    size = 16.dp
                                )
                                Text(
                                    text = selectedMusicTrack?.let { "${it.title} · ${it.artistName}" } ?: "Add Music",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedMusicTrack != null) TelefamRed else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Active Studio Tool Panel (Crop, Cover Text, Pen Brush, or Filters)
                    AnimatedVisibility(visible = activeToolTab != StudioActiveToolTab.NONE) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                when (activeToolTab) {
                                    StudioActiveToolTab.CROP_TRANSFORM -> {
                                        val curPhoto = activePhotoItem
                                        if (curPhoto != null) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Crop & Rotate",
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Surface(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.clickable {
                                                        val idx = activePhotoIndex.coerceIn(0, photoItems.size - 1)
                                                        photoItems[idx] = curPhoto.copy(
                                                            rotationQuadrants = (curPhoto.rotationQuadrants + 1) % 4
                                                        )
                                                    }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        TelefamLucideIcon(
                                                            kind = LucideIconKind.ROTATE_CW,
                                                            tint = TelefamRed,
                                                            size = 15.dp
                                                        )
                                                        Text("Rotate 90°", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TelefamRed)
                                                    }
                                                }
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                PostCropPreset.entries.forEach { preset ->
                                                    val chosen = curPhoto.cropPreset == preset
                                                    Surface(
                                                        color = if (chosen) TelefamRed else MaterialTheme.colorScheme.surface,
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clickable {
                                                                val idx = activePhotoIndex.coerceIn(0, photoItems.size - 1)
                                                                photoItems[idx] = curPhoto.copy(cropPreset = preset)
                                                            }
                                                    ) {
                                                        Text(
                                                            text = preset.label,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (chosen) Color.White else MaterialTheme.colorScheme.onSurface,
                                                            modifier = Modifier.padding(vertical = 8.dp),
                                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    StudioActiveToolTab.COVER_TEXT -> {
                                        Text(
                                            text = "Text",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = coverTextDraft,
                                                onValueChange = { coverTextDraft = it.take(120) },
                                                placeholder = { Text("Add text...") },
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("cover_text_overlay_input")
                                            )
                                            Button(
                                                onClick = {
                                                    val clean = coverTextDraft.trim()
                                                    if (clean.isNotEmpty()) {
                                                        val newLayer = PostTextOverlayLayer(
                                                            layerId = "txt_${UUID.randomUUID()}",
                                                            text = clean,
                                                            colorArgb = activeTextColorArgb,
                                                            font = activeOverlayFont,
                                                            hasBackgroundPill = activeTextPillBg
                                                        )
                                                        updateCurrentTextLayers { it + newLayer }
                                                        selectedOverlayLayerId = newLayer.layerId
                                                        coverTextDraft = ""
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .height(48.dp)
                                                    .testTag("add_cover_text_layer_btn")
                                            ) {
                                                Text("Add", color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // 12 Colours for Cover Text
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(PostStudioColorPalette.COLORS) { colorHex ->
                                                val isChosen = activeTextColorArgb == colorHex
                                                Box(
                                                    modifier = Modifier
                                                        .size(30.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(colorHex))
                                                        .border(
                                                            width = if (isChosen) 2.5.dp else 1.dp,
                                                            color = if (isChosen) TelefamRed else Color.Gray.copy(alpha = 0.5f),
                                                            shape = CircleShape
                                                        )
                                                        .clickable {
                                                            activeTextColorArgb = colorHex
                                                            selectedOverlayLayerId?.let { selId ->
                                                                updateCurrentTextLayers { list ->
                                                                    list.map { if (it.layerId == selId) it.copy(colorArgb = colorHex) else it }
                                                                }
                                                            }
                                                        }
                                                )
                                            }
                                        }

                                        // Selected Cover Text Size Slider + Delete button
                                        val selectedLayer = currentTextLayers().firstOrNull { it.layerId == selectedOverlayLayerId }
                                        if (selectedLayer != null) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text("Size", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                Slider(
                                                    value = selectedLayer.scaleFactor,
                                                    onValueChange = { newScale ->
                                                        updateCurrentTextLayers { list ->
                                                            list.map {
                                                                if (it.layerId == selectedLayer.layerId) {
                                                                    it.copy(scaleFactor = newScale)
                                                                } else it
                                                            }
                                                        }
                                                    },
                                                    valueRange = 0.45f..3.5f,
                                                    colors = SliderDefaults.colors(
                                                        thumbColor = TelefamRed,
                                                        activeTrackColor = TelefamRed
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Surface(
                                                    color = TelefamError.copy(alpha = 0.14f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.clickable {
                                                        updateCurrentTextLayers { list ->
                                                            list.filterNot { it.layerId == selectedLayer.layerId }
                                                        }
                                                        selectedOverlayLayerId = null
                                                    }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        TelefamLucideIcon(
                                                            kind = LucideIconKind.TRASH,
                                                            tint = TelefamError,
                                                            size = 14.dp
                                                        )
                                                        Text("Remove", fontSize = 11.5.sp, color = TelefamError, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    StudioActiveToolTab.PEN_BRUSH -> {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Brush",
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.clickable {
                                                        updateCurrentBrushStrokes { list ->
                                                            if (list.isNotEmpty()) list.dropLast(1) else list
                                                        }
                                                    }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        TelefamLucideIcon(
                                                            kind = LucideIconKind.UNSEND_UNDO,
                                                            tint = MaterialTheme.colorScheme.onSurface,
                                                            size = 14.dp
                                                        )
                                                        Text("Undo", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                                    }
                                                }
                                                Surface(
                                                    color = TelefamError.copy(alpha = 0.14f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.clickable {
                                                        updateCurrentBrushStrokes { emptyList() }
                                                    }
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        TelefamLucideIcon(
                                                            kind = LucideIconKind.TRASH,
                                                            tint = TelefamError,
                                                            size = 14.dp
                                                        )
                                                        Text("Clear", fontSize = 11.5.sp, color = TelefamError, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        // Brush Size Slider
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size((activeBrushSizePx.coerceIn(6f, 36f)).dp)
                                                    .clip(CircleShape)
                                                    .background(Color(activeBrushColorArgb))
                                            )
                                            Slider(
                                                value = activeBrushSizePx,
                                                onValueChange = { activeBrushSizePx = it },
                                                valueRange = 4f..48f,
                                                colors = SliderDefaults.colors(
                                                    thumbColor = TelefamRed,
                                                    activeTrackColor = TelefamRed
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("pen_brush_size_slider")
                                            )
                                        }

                                        // 12 Brush Colours
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(PostStudioColorPalette.COLORS) { colorHex ->
                                                val isChosen = activeBrushColorArgb == colorHex
                                                Box(
                                                    modifier = Modifier
                                                        .size(30.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(colorHex))
                                                        .border(
                                                            width = if (isChosen) 2.5.dp else 1.dp,
                                                            color = if (isChosen) TelefamRed else Color.Gray.copy(alpha = 0.5f),
                                                            shape = CircleShape
                                                        )
                                                        .clickable { activeBrushColorArgb = colorHex }
                                                )
                                            }
                                        }
                                    }

                                    StudioActiveToolTab.FILTERS -> {
                                        Text(
                                            text = "Filters",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(PostColorFilterPreset.entries, key = { it.id }) { preset ->
                                                val currentPreset = if (studioMode == PostStudioMode.VIDEO) {
                                                    videoState?.filterPreset
                                                } else {
                                                    activePhotoItem?.filterPreset
                                                }
                                                val isChosen = currentPreset == preset
                                                Surface(
                                                    color = if (isChosen) TelefamRed else MaterialTheme.colorScheme.surface,
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.clickable {
                                                        if (studioMode == PostStudioMode.VIDEO) {
                                                            videoState = videoState?.copy(filterPreset = preset)
                                                        } else if (activePhotoItem != null) {
                                                            val idx = activePhotoIndex.coerceIn(0, photoItems.size - 1)
                                                            photoItems[idx] = activePhotoItem.copy(filterPreset = preset)
                                                        }
                                                    }
                                                ) {
                                                    Text(
                                                        text = preset.label,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                                    )
                                                }
                                            }
                                        }
                                        if (studioMode == PostStudioMode.PHOTO && activePhotoItem != null) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text("Brightness", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                Slider(
                                                    value = activePhotoItem.brightnessOffset,
                                                    onValueChange = { b ->
                                                        val idx = activePhotoIndex.coerceIn(0, photoItems.size - 1)
                                                        photoItems[idx] = activePhotoItem.copy(brightnessOffset = b)
                                                    },
                                                    valueRange = -40f..40f,
                                                    colors = SliderDefaults.colors(
                                                        thumbColor = TelefamRed,
                                                        activeTrackColor = TelefamRed
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }

                                    StudioActiveToolTab.NONE -> Unit
                                }
                            }
                        }
                    }
                }
            }
        }

        val metadataAndPublishPaneContent: @Composable (Modifier) -> Unit = { paneMod ->
            Column(
                modifier = paneMod,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 5. Caption Input + @username Live User Autocomplete + #Hashtags (Only for Photos & Videos)
                if (studioMode != PostStudioMode.TEXT) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.38f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Caption",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${hashtagsList.size}/${PostTextAutoScaler.MAX_HASHTAGS}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (hashtagsList.size >= PostTextAutoScaler.MAX_HASHTAGS) TelefamRed else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            OutlinedTextField(
                                value = postCaption,
                                onValueChange = { updated ->
                                    if (updated.length <= PostTextAutoScaler.MAX_CAPTION_CHARS) {
                                        postCaption = updated
                                        inspectTextForMentionsAndHashtags(updated)
                                    }
                                },
                                placeholder = {
                                    Text("Write a caption...")
                                },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("post_caption_input")
                            )

                            // Quick @Tag User & #Hashtag Adder Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .clickable {
                                            if (!postCaption.endsWith("@")) {
                                                postCaption = if (postCaption.isBlank()) "@" else "$postCaption @"
                                            }
                                            activeMentionSearchToken = "@"
                                            onSearchMentions("")
                                        }
                                        .testTag("post_quick_mention_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        TelefamLucideIcon(
                                            kind = LucideIconKind.AT_SIGN,
                                            tint = TelefamRed,
                                            size = 15.dp
                                        )
                                        Text("Tag", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                OutlinedTextField(
                                    value = hashtagInputDraft,
                                    onValueChange = { raw ->
                                        hashtagInputDraft = raw.filter { it.isLetterOrDigit() || it == '_' }.take(28)
                                    },
                                    placeholder = { Text("Add #hashtag") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("post_hashtag_draft_input")
                                )

                                Button(
                                    onClick = {
                                        val clean = hashtagInputDraft.trim().removePrefix("#").lowercase()
                                        if (clean.isNotEmpty() && hashtagsList.size < PostTextAutoScaler.MAX_HASHTAGS) {
                                            val tag = "#$clean"
                                            if (tag !in hashtagsList) {
                                                hashtagsList.add(tag)
                                            }
                                            hashtagInputDraft = ""
                                        }
                                    },
                                    enabled = hashtagsList.size < PostTextAutoScaler.MAX_HASHTAGS && hashtagInputDraft.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .testTag("post_add_hashtag_btn")
                                ) {
                                    TelefamLucideIcon(
                                        kind = LucideIconKind.HASH_TAG,
                                        tint = Color.White,
                                        size = 14.dp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Live @username Autocomplete Suggestions from Room
                            AnimatedVisibility(visible = uiState.postMentionSuggestions.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("post_mention_suggestions_row")
                                ) {
                                    items(uiState.postMentionSuggestions, key = { it.userId }) { user ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(50),
                                            border = BorderStroke(1.dp, TelefamRed.copy(alpha = 0.45f)),
                                            modifier = Modifier
                                                .clickable {
                                                    val uname = user.username.lowercase()
                                                    if (uname !in taggedUsernames) {
                                                        taggedUsernames.add(uname)
                                                    }
                                                    val token = activeMentionSearchToken
                                                    if (token != null && postCaption.endsWith(token)) {
                                                        postCaption = postCaption.removeSuffix(token) + "@$uname "
                                                    }
                                                    activeMentionSearchToken = null
                                                    onClearMentions()
                                                }
                                                .testTag("mention_suggestion_${user.username}")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                TelefamLucideIcon(
                                                    kind = LucideIconKind.AT_SIGN,
                                                    tint = TelefamRed,
                                                    size = 13.dp
                                                )
                                                Text(
                                                    text = "${user.username} · ${user.fullName}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Active Tagged Users & Hashtag Chips
                            if (taggedUsernames.isNotEmpty() || hashtagsList.isNotEmpty()) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    taggedUsernames.forEach { uname ->
                                        Surface(
                                            color = TelefamRed.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(50),
                                            modifier = Modifier.clickable { taggedUsernames.remove(uname) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "@$uname",
                                                    color = TelefamRed,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                TelefamLucideIcon(
                                                    kind = LucideIconKind.CLOSE_X,
                                                    tint = TelefamRed,
                                                    size = 11.dp
                                                )
                                            }
                                        }
                                    }
                                    hashtagsList.forEach { htag ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(50),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier.clickable { hashtagsList.remove(htag) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = htag,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                TelefamLucideIcon(
                                                    kind = LucideIconKind.CLOSE_X,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    size = 11.dp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Privacy Audience Settings
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.38f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TelefamLucideIcon(
                                kind = LucideIconKind.LOCK_E2EE,
                                tint = TelefamRed,
                                size = 16.dp
                            )
                            Text(
                                text = "Audience",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("post_privacy_audience_row"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val audienceOptions = listOf(
                                PostVisibilityAudience.ANYONE to LucideIconKind.GLOBE_WEB,
                                PostVisibilityAudience.FRIENDS to LucideIconKind.USERS_FRIENDS,
                                PostVisibilityAudience.SUBSCRIBERS to LucideIconKind.STAR,
                                PostVisibilityAudience.NOBODY to LucideIconKind.LOCK_E2EE
                            )
                            audienceOptions.forEach { (aud, iconKind) ->
                                val isSelected = visibilityAudience == aud
                                val labelText = if (aud == PostVisibilityAudience.NOBODY) "Only Me" else aud.label
                                Surface(
                                    color = if (isSelected) TelefamRed else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) TelefamRed else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .clickable { visibilityAudience = aud }
                                        .testTag("post_privacy_${aud.wireCode.lowercase()}")
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        TelefamLucideIcon(
                                            kind = iconKind,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            size = 15.dp
                                        )
                                        Text(
                                            text = labelText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                        // Advanced Post Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Allow Comments",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Switch(
                                checked = allowComments,
                                onCheckedChange = { allowComments = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = TelefamRed)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Allow Duet & Stitch",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Switch(
                                checked = allowDuetsAndStitch,
                                onCheckedChange = { allowDuetsAndStitch = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = TelefamRed)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Allow Downloads",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Switch(
                                checked = allowHighQualityDownload,
                                onCheckedChange = { allowHighQualityDownload = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = TelefamRed)
                            )
                        }
                    }
                }

                // 7. Primary Publish Post Button
                Button(
                    onClick = {
                        onPublishPost(
                            studioMode,
                            textPostBody,
                            selectedBgTheme,
                            selectedTextFont,
                            textModeOverlayLayers.toList(),
                            textModeBrushStrokes.toList(),
                            photoItems.toList(),
                            videoState,
                            postCaption,
                            visibilityAudience,
                            taggedUsernames.toList(),
                            hashtagsList.toList(),
                            selectedMusicTrack,
                            allowComments,
                            allowDuetsAndStitch,
                            allowHighQualityDownload
                        ) {
                            // Reset studio drafts cleanly after Room persistence & upload completion
                            textPostBody = ""
                            textModeOverlayLayers.clear()
                            textModeBrushStrokes.clear()
                            photoItems.clear()
                            activePhotoIndex = 0
                            videoState = null
                            postCaption = ""
                            taggedUsernames.clear()
                            hashtagsList.clear()
                            selectedMusicTrack = null
                            activeToolTab = StudioActiveToolTab.NONE
                        }
                    },
                    enabled = uiState.postUploadState.status != PostUploadLifecycleStatus.UPLOADING,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TelefamRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("publish_studio_post_button")
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.SEND,
                        tint = Color.White,
                        size = 18.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.postUploadState.status == PostUploadLifecycleStatus.UPLOADING) {
                            "Uploading"
                        } else {
                            "Publish Post"
                        },
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        if (isWideStudioLayout) {
            // Canonical Two-Column Studio Layout for Tablets, Foldables & Desktop
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Top
            ) {
                canvasPaneContent(
                    Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
                metadataAndPublishPaneContent(
                    Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
            }
        } else {
            // Mobile Handheld Vertical Studio Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                canvasPaneContent(Modifier.fillMaxWidth())
                metadataAndPublishPaneContent(Modifier.fillMaxWidth())
            }
        }
    }

    if (showMusicSheet) {
        PostMusicSearchBottomSheet(
            searchResults = uiState.postMusicSearchResults,
            isSearching = uiState.isSearchingPostMusic,
            selectedTrack = selectedMusicTrack,
            onSearchQueryChange = onSearchMusic,
            onSelectTrack = { track -> selectedMusicTrack = track },
            onRemoveTrack = { selectedMusicTrack = null },
            onDismiss = { showMusicSheet = false }
        )
    }
}
