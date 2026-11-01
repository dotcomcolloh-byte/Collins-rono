package com.telefam.company.app.ui.screens

import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.ui.theme.TelefamRed
import com.telefam.company.app.domain.EditablePhotoStudioItem
import com.telefam.company.app.domain.EditableVideoStudioState
import com.telefam.company.app.domain.PostBackgroundTheme
import com.telefam.company.app.domain.PostBrushStroke
import com.telefam.company.app.domain.PostColorFilterPreset
import com.telefam.company.app.domain.PostMusicTrackItem
import com.telefam.company.app.domain.PostStudioColorPalette
import com.telefam.company.app.domain.PostTextAutoScaler
import com.telefam.company.app.domain.PostTextOverlayLayer
import com.telefam.company.app.domain.PostTypographyFont
import com.telefam.company.app.ui.components.LucideIconKind
import com.telefam.company.app.ui.components.TelefamLucideIcon
import kotlinx.coroutines.delay
import java.io.File
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

/**
 * Interactive Text Post Canvas with:
 * - 12 unique background gradient themes (`PostBackgroundTheme`)
 * - 10 locally bundled Google Fonts (`PostTypographyFont`)
 * - Automatic font size reduction as character count grows up to 360 characters
 * - Optional freehand pen brush strokes & pinchable cover text stickers
 */
@Composable
fun InteractiveTextPostStudioCanvas(
    text: String,
    onTextChange: (String) -> Unit,
    backgroundTheme: PostBackgroundTheme,
    typographyFont: PostTypographyFont,
    modifier: Modifier = Modifier
) {
    val dynamicFontSizeSp = PostTextAutoScaler.computeDynamicFontSizeSp(text.length)
    val dynamicLineHeightSp = PostTextAutoScaler.computeDynamicLineHeightSp(text.length)
    val fontFamily = remember(typographyFont) { typographyFont.toComposeFontFamily() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(backgroundTheme.startColor, backgroundTheme.endColor)
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.16f),
                shape = RoundedCornerShape(20.dp)
            )
            .clipToBounds()
            .testTag("post_text_studio_canvas")
    ) {
        // Subtle decorative studio ambient glow circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(backgroundTheme.accentHex).copy(alpha = 0.11f),
                radius = size.width * 0.34f,
                center = Offset(size.width * 0.85f, size.height * 0.16f)
            )
            drawCircle(
                color = Color(backgroundTheme.accentHex).copy(alpha = 0.09f),
                radius = size.width * 0.28f,
                center = Offset(size.width * 0.14f, size.height * 0.84f)
            )
        }

        // Centered auto-scaling text editor
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = text,
                onValueChange = { updated ->
                    if (updated.length <= PostTextAutoScaler.MAX_BACKGROUND_TEXT_CHARS) {
                        onTextChange(updated)
                    }
                },
                textStyle = TextStyle(
                    color = backgroundTheme.textColor,
                    fontSize = dynamicFontSizeSp.sp,
                    lineHeight = dynamicLineHeightSp.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(backgroundTheme.textColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_text_canvas_input"),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = "What's on your mind?",
                                color = backgroundTheme.textColor.copy(alpha = 0.52f),
                                fontSize = 22.sp,
                                lineHeight = 29.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        // Bottom-right live character counter
        Surface(
            color = Color.Black.copy(alpha = 0.42f),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        ) {
            Text(
                text = "${text.length}/${PostTextAutoScaler.MAX_BACKGROUND_TEXT_CHARS}",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Interactive Photo Studio Canvas supporting:
 * - Crop aspect ratio framing (`Original`, `1:1`, `4:5`, `9:16`, `16:9`)
 * - 90° rotation + two-finger pinch zoom & pan on the photo
 * - Real-time studio ColorMatrix filter + brightness preview
 * - Freehand Pen Brush drawing with customizable thickness & colour
 * - Pinchable, draggable, rotatable Cover Text layers with colour & font selection
 */
@Composable
fun InteractivePhotoStudioCanvas(
    item: EditablePhotoStudioItem,
    onUpdatePhotoTransform: (zoom: Float, panX: Float, panY: Float) -> Unit,
    selectedLayerId: String?,
    onSelectLayer: (String?) -> Unit,
    onUpdateLayer: (PostTextOverlayLayer) -> Unit,
    isDrawingBrush: Boolean,
    activeBrushColor: Long,
    activeBrushWidthPx: Float,
    onAppendBrushStroke: (PostBrushStroke) -> Unit,
    modifier: Modifier = Modifier
) {
    val decodedBitmap = remember(item.sandboxFilePath) {
        runCatching { BitmapFactory.decodeFile(item.sandboxFilePath)?.asImageBitmap() }.getOrNull()
    }
    val colorFilter = remember(item.filterPreset, item.brightnessOffset) {
        buildComposeColorFilter(item.filterPreset, item.brightnessOffset)
    }
    val targetRatio = item.cropPreset.aspectRatio ?: (4f / 5f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp, max = 440.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0B0D13))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .aspectRatio(targetRatio.coerceIn(0.56f, 1.78f))
                .fillMaxHeight()
                .clipToBounds()
                .pointerInput(item.itemId, isDrawingBrush, selectedLayerId) {
                    if (!isDrawingBrush && selectedLayerId == null) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newZoom = (item.zoomScale * zoom).coerceIn(1f, 3.5f)
                            val newPanX = (item.panXNorm + pan.x / size.width.coerceAtLeast(1)).coerceIn(-0.85f, 0.85f)
                            val newPanY = (item.panYNorm + pan.y / size.height.coerceAtLeast(1)).coerceIn(-0.85f, 0.85f)
                            onUpdatePhotoTransform(newZoom, newPanX, newPanY)
                        }
                    }
                }
                .testTag("post_photo_studio_canvas")
        ) {
            val canvasWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
            val canvasHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

            if (decodedBitmap != null) {
                Image(
                    bitmap = decodedBitmap,
                    contentDescription = "Edited photo canvas",
                    contentScale = ContentScale.Crop,
                    colorFilter = colorFilter,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = item.zoomScale
                            scaleY = item.zoomScale
                            translationX = item.panXNorm * canvasWidthPx * 0.35f
                            translationY = item.panYNorm * canvasHeightPx * 0.35f
                            rotationZ = (item.rotationQuadrants % 4) * 90f
                        }
                )
            }

            // Rule-of-thirds subtle crop grid guide
            Canvas(modifier = Modifier.fillMaxSize()) {
                val guideColor = Color.White.copy(alpha = 0.16f)
                drawLine(guideColor, Offset(size.width / 3f, 0f), Offset(size.width / 3f, size.height), strokeWidth = 1f)
                drawLine(guideColor, Offset(size.width * 2f / 3f, 0f), Offset(size.width * 2f / 3f, size.height), strokeWidth = 1f)
                drawLine(guideColor, Offset(0f, size.height / 3f), Offset(size.width, size.height / 3f), strokeWidth = 1f)
                drawLine(guideColor, Offset(0f, size.height * 2f / 3f), Offset(size.width, size.height * 2f / 3f), strokeWidth = 1f)
            }

            // Freehand Pen Brush Strokes + Pinchable Cover Text Layers
            StudioBrushAndPinchTextOverlaySurface(
                canvasWidthPx = canvasWidthPx,
                canvasHeightPx = canvasHeightPx,
                textLayers = item.textLayers,
                selectedLayerId = selectedLayerId,
                onSelectLayer = onSelectLayer,
                onUpdateLayer = onUpdateLayer,
                brushStrokes = item.brushStrokes,
                isDrawingBrush = isDrawingBrush,
                activeBrushColor = activeBrushColor,
                activeBrushWidthPx = activeBrushWidthPx,
                onAppendBrushStroke = onAppendBrushStroke
            )
        }
    }
}

/**
 * Interactive CapCut-Style Video Studio Canvas & I-Frame Timeline Trimmer:
 * - Loops trimmed video segment `[trimStartMs, trimEndMs]` in real time
 * - Overlays movable/pinch-resizable Cover Text layers & Pen Brush strokes
 * - Displays I-Frame keyframe filmstrip with Trim Start/End handles & Cover Frame selector
 */
@Composable
fun InteractiveVideoStudioCanvasAndTimeline(
    videoState: EditableVideoStudioState,
    selectedMusic: PostMusicTrackItem?,
    selectedLayerId: String?,
    onSelectLayer: (String?) -> Unit,
    onUpdateLayer: (PostTextOverlayLayer) -> Unit,
    isDrawingBrush: Boolean,
    activeBrushColor: Long,
    activeBrushWidthPx: Float,
    onAppendBrushStroke: (PostBrushStroke) -> Unit,
    onUpdateTrimRange: (startMs: Long, endMs: Long) -> Unit,
    onSelectCoverTimestamp: (timestampMs: Long) -> Unit,
    onUpdateOriginalVolume: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember(videoState.videoId) { mutableStateOf(true) }
    var currentPlayheadMs by remember(videoState.videoId) { mutableLongStateOf(videoState.trimStartMs) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    // Keep playhead looping cleanly inside [trimStartMs, trimEndMs]
    LaunchedEffect(videoState.videoId, videoState.trimStartMs, videoState.trimEndMs, isPlaying) {
        while (isPlaying) {
            val view = videoViewRef
            if (view != null && runCatching { view.isPlaying }.getOrDefault(false)) {
                val pos = runCatching { view.currentPosition.toLong() }.getOrDefault(videoState.trimStartMs)
                currentPlayheadMs = pos
                if (pos >= videoState.trimEndMs || pos < videoState.trimStartMs - 250L) {
                    runCatching { view.seekTo(videoState.trimStartMs.toInt()) }
                    currentPlayheadMs = videoState.trimStartMs
                }
            }
            delay(120L)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Video Preview Canvas with Overlays
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF090B10))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                .clipToBounds()
                .testTag("post_video_studio_canvas")
        ) {
            val canvasWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
            val canvasHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        videoViewRef = this
                        setVideoPath(videoState.sandboxFilePath)
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            val vol = videoState.originalVideoVolume.coerceIn(0f, 1f)
                            mp.setVolume(vol, vol)
                            seekTo(videoState.trimStartMs.toInt())
                            if (isPlaying) start()
                        }
                    }
                },
                update = { view ->
                    videoViewRef = view
                    if (isPlaying && !view.isPlaying) {
                        runCatching { view.start() }
                    } else if (!isPlaying && view.isPlaying) {
                        runCatching { view.pause() }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Freehand Pen Brush + Pinchable Cover Text Layers on top of Video
            StudioBrushAndPinchTextOverlaySurface(
                canvasWidthPx = canvasWidthPx,
                canvasHeightPx = canvasHeightPx,
                textLayers = videoState.textLayers,
                selectedLayerId = selectedLayerId,
                onSelectLayer = onSelectLayer,
                onUpdateLayer = onUpdateLayer,
                brushStrokes = videoState.brushStrokes,
                isDrawingBrush = isDrawingBrush,
                activeBrushColor = activeBrushColor,
                activeBrushWidthPx = activeBrushWidthPx,
                onAppendBrushStroke = onAppendBrushStroke
            )

            // Play / Pause control pill + live timecode badge
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.62f))
                    .clickable {
                        isPlaying = !isPlaying
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("video_studio_play_pause_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TelefamLucideIcon(
                    kind = if (isPlaying) LucideIconKind.PAUSE else LucideIconKind.PLAY,
                    tint = Color.White,
                    size = 15.dp
                )
                Text(
                    text = "${formatMillisShort(currentPlayheadMs)} / ${formatMillisShort(videoState.trimEndMs)}",
                    color = Color.White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Top-right quality &trimmed duration pill
            Surface(
                color = Color.Black.copy(alpha = 0.62f),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Text(
                    text = "${videoState.qualityProfiles.firstOrNull() ?: "1080p"} · ${formatMillisShort(videoState.trimmedDurationMs)}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // CapCut-Style I-Frame Keyframe Filmstrip & Trim Range Slider
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.SCISSORS_TRIM,
                            tint = TelefamRed,
                            size = 16.dp
                        )
                        Text(
                            text = "Trim & Cover",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${formatMillisShort(videoState.trimStartMs)} – ${formatMillisShort(videoState.trimEndMs)} (${formatMillisShort(videoState.trimmedDurationMs)})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TelefamRed
                    )
                }

                // Extracted I-Frame Keyframe Filmstrip
                if (videoState.keyframes.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        videoState.keyframes.forEach { kf ->
                            val isSelectedCover = kotlin.math.abs(kf.timestampMs - videoState.selectedCoverTimeMs) <
                                (videoState.totalDurationMs / (videoState.keyframes.size.coerceAtLeast(1) * 2))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(
                                        width = if (isSelectedCover) 2.dp else 0.dp,
                                        color = if (isSelectedCover) TelefamRed else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        onSelectCoverTimestamp(kf.timestampMs)
                                        videoViewRef?.seekTo(kf.timestampMs.toInt())
                                    }
                            ) {
                                AsyncImage(
                                    model = File(kf.thumbFilePath),
                                    contentDescription = "Keyframe ${kf.timestampMs}ms",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                val maxDurFloat = videoState.totalDurationMs.coerceAtLeast(1000L).toFloat()
                val startFloat = videoState.trimStartMs.toFloat().coerceIn(0f, maxDurFloat)
                val endFloat = videoState.trimEndMs.toFloat().coerceIn(startFloat + 500f, maxDurFloat)

                RangeSlider(
                    value = startFloat..endFloat,
                    onValueChange = { range ->
                        val s = range.start.toLong().coerceAtLeast(0L)
                        val e = range.endInclusive.toLong().coerceAtLeast(s + 500L)
                        onUpdateTrimRange(s, e)
                    },
                    valueRange = 0f..maxDurFloat,
                    colors = SliderDefaults.colors(
                        thumbColor = TelefamRed,
                        activeTrackColor = TelefamRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("video_trim_range_slider")
                )

                // Cover Thumbnail Preview + Audio Volume Mixer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!videoState.coverFrameFilePath.isNullOrBlank()) {
                        AsyncImage(
                            model = File(videoState.coverFrameFilePath),
                            contentDescription = "Selected video cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 40.dp, height = 54.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.5.dp, TelefamRed, RoundedCornerShape(8.dp))
                        )
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.VOLUME_MIXER,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            size = 16.dp
                        )
                        Text(
                            text = "Volume ${(videoState.originalVideoVolume * 100).roundToInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = videoState.originalVideoVolume,
                            onValueChange = onUpdateOriginalVolume,
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = TelefamRed,
                                activeTrackColor = TelefamRed
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Shared Interactive Overlay Surface for Freehand Pen Brush Drawing and Pinch-to-Resize / Drag-to-Move
 * Cover Text Layers across Text, Photo, and Video Studios.
 */
@Composable
fun StudioBrushAndPinchTextOverlaySurface(
    canvasWidthPx: Float,
    canvasHeightPx: Float,
    textLayers: List<PostTextOverlayLayer>,
    selectedLayerId: String?,
    onSelectLayer: (String?) -> Unit,
    onUpdateLayer: (PostTextOverlayLayer) -> Unit,
    brushStrokes: List<PostBrushStroke>,
    isDrawingBrush: Boolean,
    activeBrushColor: Long,
    activeBrushWidthPx: Float,
    onAppendBrushStroke: (PostBrushStroke) -> Unit
) {
    val activeDragPoints = remember { mutableStateListOf<Pair<Float, Float>>() }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isDrawingBrush) {
                    Modifier.pointerInput(activeBrushColor, activeBrushWidthPx, canvasWidthPx, canvasHeightPx) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                activeDragPoints.clear()
                                activeDragPoints.add(
                                    (offset.x / canvasWidthPx).coerceIn(0f, 1f) to
                                        (offset.y / canvasHeightPx).coerceIn(0f, 1f)
                                )
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val pos = change.position
                                activeDragPoints.add(
                                    (pos.x / canvasWidthPx).coerceIn(0f, 1f) to
                                        (pos.y / canvasHeightPx).coerceIn(0f, 1f)
                                )
                            },
                            onDragEnd = {
                                if (activeDragPoints.isNotEmpty()) {
                                    onAppendBrushStroke(
                                        PostBrushStroke(
                                            strokeId = "stroke_${UUID.randomUUID()}",
                                            pointsNorm = activeDragPoints.toList(),
                                            colorArgb = activeBrushColor,
                                            strokeWidthPx = activeBrushWidthPx
                                        )
                                    )
                                    activeDragPoints.clear()
                                }
                            },
                            onDragCancel = {
                                activeDragPoints.clear()
                            }
                        )
                    }
                } else {
                    Modifier
                }
            )
    ) {
        // Render committed brush strokes + live stroke in progress
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleFactor = size.width / 380f
            fun drawNormStroke(pts: List<Pair<Float, Float>>, colorArgb: Long, widthPx: Float) {
                if (pts.isEmpty()) return
                val strokeColor = Color(colorArgb)
                val sw = (widthPx * scaleFactor).coerceIn(4f, 96f)
                if (pts.size == 1) {
                    drawCircle(
                        color = strokeColor,
                        radius = sw / 2f,
                        center = Offset(pts[0].first * size.width, pts[0].second * size.height)
                    )
                } else {
                    val path = Path().apply {
                        moveTo(pts[0].first * size.width, pts[0].second * size.height)
                        for (i in 1 until pts.size) {
                            val px = pts[i - 1].first * size.width
                            val py = pts[i - 1].second * size.height
                            val cx = pts[i].first * size.width
                            val cy = pts[i].second * size.height
                            quadraticTo(px, py, (px + cx) / 2f, (py + cy) / 2f)
                        }
                        val last = pts.last()
                        lineTo(last.first * size.width, last.second * size.height)
                    }
                    drawPath(
                        path = path,
                        color = strokeColor,
                        style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            for (stroke in brushStrokes) {
                drawNormStroke(stroke.pointsNorm, stroke.colorArgb, stroke.strokeWidthPx)
            }
            if (activeDragPoints.isNotEmpty()) {
                drawNormStroke(activeDragPoints, activeBrushColor, activeBrushWidthPx)
            }
        }

        // Render pinchable, draggable, rotatable Cover Text layers
        textLayers.forEach { layer ->
            val isSelected = layer.layerId == selectedLayerId
            val fontFam = remember(layer.font) { layer.font.toComposeFontFamily() }
            val offsetX = ((layer.normX - 0.5f) * canvasWidthPx).roundToInt()
            val offsetY = ((layer.normY - 0.5f) * canvasHeightPx).roundToInt()

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset { IntOffset(offsetX, offsetY) }
                    .graphicsLayer {
                        scaleX = layer.scaleFactor.coerceIn(0.45f, 3.5f)
                        scaleY = layer.scaleFactor.coerceIn(0.45f, 3.5f)
                        rotationZ = layer.rotationDeg
                    }
                    .pointerInput(layer.layerId, isDrawingBrush) {
                        if (!isDrawingBrush) {
                            detectTransformGestures { _, pan, zoom, rotation ->
                                val nx = (layer.normX + pan.x / canvasWidthPx).coerceIn(0.08f, 0.92f)
                                val ny = (layer.normY + pan.y / canvasHeightPx).coerceIn(0.08f, 0.92f)
                                val ns = (layer.scaleFactor * zoom).coerceIn(0.45f, 3.5f)
                                val nr = (layer.rotationDeg + rotation) % 360f
                                onSelectLayer(layer.layerId)
                                onUpdateLayer(
                                    layer.copy(
                                        normX = nx,
                                        normY = ny,
                                        scaleFactor = ns,
                                        rotationDeg = nr
                                    )
                                )
                            }
                        }
                    }
                    .clickable(enabled = !isDrawingBrush) {
                        onSelectLayer(if (isSelected) null else layer.layerId)
                    }
                    .border(
                        width = if (isSelected) 1.5.dp else 0.dp,
                        color = if (isSelected) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        color = if (layer.hasBackgroundPill) {
                            if (layer.colorArgb == 0xFF10131AL) Color.White.copy(alpha = 0.88f)
                            else Color(0xCC10131A)
                        } else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("cover_text_layer_${layer.layerId}")
            ) {
                Text(
                    text = layer.text,
                    color = Color(layer.colorArgb),
                    fontSize = with(density) { 20.sp },
                    fontFamily = fontFam,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Free Public Music API Search & Live 30-Second Stream Preview Bottom Sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMusicSearchBottomSheet(
    searchResults: List<PostMusicTrackItem>,
    isSearching: Boolean,
    selectedTrack: PostMusicTrackItem?,
    onSearchQueryChange: (String) -> Unit,
    onSelectTrack: (PostMusicTrackItem) -> Unit,
    onRemoveTrack: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }
    var playingTrackId by remember { mutableStateOf<String?>(null) }
    var isBufferingAudio by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
                mediaPlayer.release()
            }
        }
    }

    fun togglePreviewTrack(track: PostMusicTrackItem) {
        if (playingTrackId == track.trackId) {
            runCatching {
                if (mediaPlayer.isPlaying) mediaPlayer.pause()
            }
            playingTrackId = null
            isBufferingAudio = false
        } else {
            playingTrackId = track.trackId
            isBufferingAudio = true
            runCatching {
                mediaPlayer.reset()
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                mediaPlayer.setDataSource(track.previewStreamUrl)
                mediaPlayer.setOnPreparedListener { mp ->
                    isBufferingAudio = false
                    mp.start()
                }
                mediaPlayer.setOnCompletionListener {
                    playingTrackId = null
                    isBufferingAudio = false
                }
                mediaPlayer.setOnErrorListener { _, _, _ ->
                    isBufferingAudio = false
                    playingTrackId = null
                    true
                }
                mediaPlayer.prepareAsync()
            }.onFailure {
                isBufferingAudio = false
                playingTrackId = null
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 420.dp, max = 640.dp)
                .padding(horizontal = 18.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                        kind = LucideIconKind.MUSIC_AUDIO,
                        tint = TelefamRed,
                        size = 20.dp
                    )
                    Text(
                        text = "Add Music",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (selectedTrack != null) {
                    Surface(
                        color = TelefamRed.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.clickable {
                            onRemoveTrack()
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = "Remove Song",
                            color = TelefamRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    onSearchQueryChange(it)
                },
                placeholder = { Text("Search songs, artists, Afrobeats, Lo-Fi...") },
                leadingIcon = {
                    TelefamLucideIcon(
                        kind = LucideIconKind.SEARCH,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        size = 18.dp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_music_search_input")
            )

            // Genre quick chips
            val genres = remember {
                listOf("Afrobeats", "Chill Lo-Fi", "Pop Hits", "Acoustic", "Amapiano", "Synthwave")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genres.forEach { genre ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.clickable {
                            query = genre
                            onSearchQueryChange(genre)
                        }
                    ) {
                        Text(
                            text = genre,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TelefamRed, modifier = Modifier.size(28.dp))
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(searchResults, key = { it.trackId }) { track ->
                    val isPlayingThis = playingTrackId == track.trackId
                    val isChosen = selectedTrack?.trackId == track.trackId
                    Surface(
                        color = if (isChosen) {
                            TelefamRed.copy(alpha = 0.10f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isChosen) TelefamRed else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("music_track_row_${track.trackId}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Play / Pause 30s Audio Preview Button
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlayingThis) TelefamRed else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, TelefamRed.copy(alpha = 0.5f), CircleShape)
                                    .clickable { togglePreviewTrack(track) }
                                    .testTag("music_preview_btn_${track.trackId}"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPlayingThis && isBufferingAudio) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    TelefamLucideIcon(
                                        kind = if (isPlayingThis) LucideIconKind.PAUSE else LucideIconKind.PLAY,
                                        tint = if (isPlayingThis) Color.White else TelefamRed,
                                        size = 18.dp
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = buildString {
                                        append(track.artistName)
                                        if (track.albumTitle.isNotBlank()) {
                                            append(" · ")
                                            append(track.albumTitle)
                                        }
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Button(
                                onClick = {
                                    runCatching {
                                        if (mediaPlayer.isPlaying) mediaPlayer.stop()
                                    }
                                    playingTrackId = null
                                    onSelectTrack(track)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TelefamRed,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = if (isChosen) "Selected" else "Use",
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
}

private fun buildComposeColorFilter(
    preset: PostColorFilterPreset,
    brightnessOffset: Float
): ColorFilter {
    val sat = ColorMatrix().apply { setToSaturation(preset.saturation) }
    val c = preset.contrast
    val t = (1f - c) * 128f + brightnessOffset
    val adjust = ColorMatrix(
        floatArrayOf(
            c, 0f, 0f, 0f, t + preset.warmthShift,
            0f, c, 0f, 0f, t,
            0f, 0f, c, 0f, t - preset.warmthShift,
            0f, 0f, 0f, 1f, 0f
        )
    )
    sat.timesAssign(adjust)
    return ColorFilter.colorMatrix(sat)
}

fun formatMillisShort(ms: Long): String {
    val totalSec = (ms.coerceAtLeast(0L) / 1000L)
    val mins = totalSec / 60L
    val secs = totalSec % 60L
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}
