package com.telefam.company.app.domain

import androidx.annotation.FontRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

enum class PostStudioMode(val label: String) {
    TEXT("Text"),
    CAMERA("Camera"),
    PHOTO("Photo"),
    VIDEO("Video")
}

/**
 * 12 Unique Background Colours & Gradients for Text Posts.
 */
enum class PostBackgroundTheme(
    val id: String,
    val label: String,
    val startColorHex: Long,
    val endColorHex: Long,
    val textColorHex: Long,
    val accentHex: Long
) {
    CRIMSON_VELVET("crimson_velvet", "Crimson Velvet", 0xFFC80815, 0xFF4A0308, 0xFFFFFFFF, 0xFFFFCDD2),
    OBSIDIAN_NOIR("obsidian_noir", "Obsidian Noir", 0xFF10131A, 0xFF283040, 0xFFFFFFFF, 0xFF90CAF9),
    ROYAL_SAPPHIRE("royal_sapphire", "Royal Sapphire", 0xFF0F3460, 0xFF16213E, 0xFFFFFFFF, 0xFF82B1FF),
    EMERALD_CANOPY("emerald_canopy", "Emerald Canopy", 0xFF0B5345, 0xFF148F77, 0xFFFFFFFF, 0xFFA3E4D7),
    SUNSET_AMBER("sunset_amber", "Sunset Amber", 0xFFD35400, 0xFF8E24AA, 0xFFFFFFFF, 0xFFFFE082),
    CYBER_MAGENTA("cyber_magenta", "Cyber Magenta", 0xFFB80D57, 0xFF3B0944, 0xFFFFFFFF, 0xFFFF80AB),
    SAHARA_GOLD("sahara_gold", "Sahara Gold", 0xFF9A7D0A, 0xFF5D4037, 0xFFFFFFFF, 0xFFFFF59D),
    OCEAN_TEAL("ocean_teal", "Ocean Teal", 0xFF006266, 0xFF1B1464, 0xFFFFFFFF, 0xFF80CBC4),
    AURORA_VIOLET("aurora_violet", "Aurora Violet", 0xFF4834D4, 0xFF130F40, 0xFFFFFFFF, 0xFFB388FF),
    ROSE_QUARTZ("rose_quartz", "Rose Quartz", 0xFFFAD0C4, 0xFFFFD1FF, 0xFF1E141A, 0xFF880E4F),
    ARCTIC_MINT("arctic_mint", "Arctic Mint", 0xFFD4FC79, 0xFF96E6A1, 0xFF0E2616, 0xFF1B5E20),
    SLATE_TITANIUM("slate_titanium", "Slate Titanium", 0xFF2C3E50, 0xFF4CA1AF, 0xFFFFFFFF, 0xFFCFD8DC);

    val startColor: Color get() = Color(startColorHex)
    val endColor: Color get() = Color(endColorHex)
    val textColor: Color get() = Color(textColorHex)
}

/**
 * 10 Locally Bundled Google Fonts in `res/font/` for Text Posts and Photo/Video Cover Text Overlays.
 */
enum class PostTypographyFont(
    val id: String,
    val displayName: String,
    val sampleBadge: String,
    @FontRes val fontResId: Int
) {
    INTER("inter", "Inter Clean", "Aa", R.font.inter),
    PLUS_JAKARTA("plus_jakarta", "Jakarta Bold", "Aa", R.font.plus_jakarta_sans),
    PLAYFAIR("playfair", "Playfair Serif", " Serif", R.font.playfair_display),
    PACIFICO("pacifico", "Pacifico Script", "Script", R.font.pacifico),
    ROBOTO_MONO("roboto_mono", "Roboto Mono", "Mono", R.font.roboto_mono),
    BEBAS_NEUE("bebas_neue", "Bebas Poster", "POSTER", R.font.bebas_neue),
    OSWALD("oswald", "Oswald Condensed", "TALL", R.font.oswald),
    DANCING_SCRIPT("dancing_script", "Dancing Cursive", "Cursive", R.font.dancing_script),
    SPACE_GROTESK("space_grotesk", "Space Grotesk", "Tech", R.font.space_grotesk),
    MERRIWEATHER("merriweather", "Merriweather", "Book", R.font.merriweather);

    fun toComposeFontFamily(): FontFamily = FontFamily(
        Font(fontResId, FontWeight.Normal),
        Font(fontResId, FontWeight.Bold)
    )
}

/**
 * Privacy / Audience selector for who can see the post.
 */
enum class PostVisibilityAudience(
    val wireCode: String,
    val label: String,
    val subtitle: String
) {
    ANYONE("ANYONE", "Anyone", "Anyone on Telefam can view"),
    FRIENDS("FRIENDS", "Friends", "Only mutual followers"),
    SUBSCRIBERS("SUBSCRIBERS", "Subscribers", "Exclusive to your subscribers"),
    NOBODY("NOBODY", "Nobody", "Private vault · only you");

    companion object {
        fun fromWire(code: String): PostVisibilityAudience =
            entries.firstOrNull { it.wireCode.equals(code, ignoreCase = true) } ?: ANYONE
    }
}

/**
 * Free Music Track from Public Music API (iTunes Search API + Jamendo/CC fallback) with 30s stream preview.
 */
data class PostMusicTrackItem(
    val trackId: String,
    val title: String,
    val artistName: String,
    val albumTitle: String = "",
    val previewStreamUrl: String,
    val artworkUrl: String? = null,
    val durationMs: Long = 30_000L,
    val startOffsetMs: Long = 0L,
    val musicVolume: Float = 0.85f
)

enum class PostCropPreset(
    val id: String,
    val label: String,
    val aspectRatio: Float? // null = Free
) {
    FREE("FREE", "Original", null),
    SQUARE_1_1("1_1", "1:1", 1f),
    PORTRAIT_4_5("4_5", "4:5", 0.8f),
    STORY_9_16("9_16", "9:16", 9f / 16f),
    LANDSCAPE_16_9("16_9", "16:9", 16f / 9f)
}

enum class PostColorFilterPreset(
    val id: String,
    val label: String,
    val contrast: Float,
    val saturation: Float,
    val warmthShift: Float
) {
    ORIGINAL("ORIGINAL", "Original", 1.0f, 1.0f, 0f),
    VIVID_CINEMA("VIVID_CINEMA", "Cinema", 1.18f, 1.28f, 8f),
    WARM_GOLD("WARM_GOLD", "Golden Hour", 1.08f, 1.15f, 22f),
    COOL_CYBER("COOL_CYBER", "Arctic", 1.14f, 1.05f, -20f),
    SOFT_FILM("SOFT_FILM", "Kodak Film", 0.94f, 0.86f, 12f),
    MONO_NOIR("MONO_NOIR", "Noir B&W", 1.25f, 0.0f, 0f)
}

/**
 * Pinchable, draggable, rotatable cover text layer with color & font selection.
 */
data class PostTextOverlayLayer(
    val layerId: String,
    val text: String,
    val colorArgb: Long = 0xFFFFFFFF,
    val font: PostTypographyFont = PostTypographyFont.PLUS_JAKARTA,
    val hasBackgroundPill: Boolean = true,
    val normX: Float = 0.5f,
    val normY: Float = 0.5f,
    val scaleFactor: Float = 1.0f,
    val rotationDeg: Float = 0f
)

/**
 * Freehand pen brush stroke with customizable thickness and color.
 */
data class PostBrushStroke(
    val strokeId: String,
    val pointsNorm: List<Pair<Float, Float>>,
    val colorArgb: Long = 0xFFC80815,
    val strokeWidthPx: Float = 14f
)

/**
 * Studio Color Palette for Cover Text & Pen Brush.
 */
object PostStudioColorPalette {
    val COLORS: List<Long> = listOf(
        0xFFFFFFFF, // Pure White
        0xFF10131A, // Obsidian Black
        0xFFC80815, // Telefam Crimson
        0xFFFFD600, // Electric Yellow
        0xFF00E676, // Neon Emerald
        0xFF00B0FF, // Sky Cyan
        0xFFFF4081, // Vibrant Pink
        0xFFFF6D00, // Tangerine Orange
        0xFFAA00FF, // Vivid Purple
        0xFF64FFDA, // Aqua Mint
        0xFFFFAB91, // Coral Peach
        0xFFCFD8DC  // Silver Mist
    )
}

/**
 * Per-photo editable studio item (supports up to 10 photos in a single post).
 */
data class EditablePhotoStudioItem(
    val itemId: String,
    val sourceUriString: String,
    val sandboxFilePath: String,
    val widthPx: Int,
    val heightPx: Int,
    val originalByteSize: Long,
    val compressedByteSize: Long,
    val sha256Hex: String,
    val perceptualDHash: String,
    val compositeFingerprintId: String,
    val cropPreset: PostCropPreset = PostCropPreset.FREE,
    val zoomScale: Float = 1f,
    val panXNorm: Float = 0f,
    val panYNorm: Float = 0f,
    val rotationQuadrants: Int = 0, // 0, 1 (90°), 2 (180°), 3 (270°)
    val filterPreset: PostColorFilterPreset = PostColorFilterPreset.ORIGINAL,
    val brightnessOffset: Float = 0f, // -40f..40f
    val textLayers: List<PostTextOverlayLayer> = emptyList(),
    val brushStrokes: List<PostBrushStroke> = emptyList(),
    val autoExtractedCaptionHint: String = ""
)

data class VideoKeyframeThumb(
    val timestampMs: Long,
    val thumbFilePath: String
)

/**
 * CapCut-style Video Studio state with I-Frame timeline trimming, 100 MB auto-trim,
 * cover frame extraction, pinchable text layers, pen brush drawing, and dual audio mixer.
 */
data class EditableVideoStudioState(
    val videoId: String,
    val sourceUriString: String,
    val sandboxFilePath: String,
    val originalByteSize: Long,
    val effectiveTrimmedByteSize: Long,
    val totalDurationMs: Long,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long,
    val wasAutoTrimmedFor100Mb: Boolean = false,
    val widthPx: Int = 1080,
    val heightPx: Int = 1920,
    val selectedCoverTimeMs: Long = 0L,
    val coverFrameFilePath: String? = null,
    val keyframes: List<VideoKeyframeThumb> = emptyList(),
    val filterPreset: PostColorFilterPreset = PostColorFilterPreset.ORIGINAL,
    val textLayers: List<PostTextOverlayLayer> = emptyList(),
    val brushStrokes: List<PostBrushStroke> = emptyList(),
    val originalVideoVolume: Float = 1.0f,
    val sha256Hex: String = "",
    val perceptualDHash: String = "",
    val temporalKeyframeHash: String = "",
    val compositeFingerprintId: String = "",
    val qualityProfiles: List<String> = listOf("1080p FHD", "720p HD", "480p SD"),
    val autoExtractedCaption: String = ""
) {
    val trimmedDurationMs: Long
        get() = (trimEndMs - trimStartMs).coerceAtLeast(500L)
}

enum class PostUploadLifecycleStatus {
    IDLE,
    UPLOADING,
    UPLOADED,
    QUEUED_RETRY
}

data class PostBackgroundUploadState(
    val status: PostUploadLifecycleStatus = PostUploadLifecycleStatus.IDLE,
    val progressFraction: Float = 0f,
    val uploadedChunks: Int = 0,
    val totalChunks: Int = 1,
    val lastPostId: String? = null,
    val lastAssignedCdnUrl: String? = null
)

object PostTextAutoScaler {
    const val MAX_BACKGROUND_TEXT_CHARS = 360
    const val MAX_CAPTION_CHARS = 2200
    const val MAX_HASHTAGS = 5

    /**
     * Dynamically scales font size down as character count increases up to `MAX_BACKGROUND_TEXT_CHARS`.
     */
    fun computeDynamicFontSizeSp(charCount: Int): Float {
        return when {
            charCount <= 40 -> 30f
            charCount <= 85 -> 25f
            charCount <= 140 -> 21f
            charCount <= 220 -> 18f
            charCount <= 300 -> 15.5f
            else -> 14f
        }
    }

    fun computeDynamicLineHeightSp(charCount: Int): Float {
        return computeDynamicFontSizeSp(charCount) * 1.28f
    }

    /**
     * Extracts unique hashtags (up to 5) from text/caption.
     */
    fun extractHashtags(text: String): List<String> {
        val regex = Regex("#([A-Za-z0-9_]{1,32})")
        return regex.findAll(text)
            .map { "#${it.groupValues[1].lowercase()}" }
            .distinct()
            .take(MAX_HASHTAGS)
            .toList()
    }

    /**
     * Extracts mentioned `@username` handles from text/caption.
     */
    fun extractMentionedUsernames(text: String): List<String> {
        val regex = Regex("@([A-Za-z0-9_]{1,24})")
        return regex.findAll(text)
            .map { it.groupValues[1].lowercase() }
            .distinct()
            .toList()
    }
}
