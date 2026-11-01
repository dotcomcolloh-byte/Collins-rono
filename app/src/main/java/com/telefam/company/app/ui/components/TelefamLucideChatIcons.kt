package com.telefam.company.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.telefam.company.app.domain.ChatReactionChoice

enum class LucideIconKind {
    CLOCK,
    CHECK_SINGLE,
    CIRCLE_CHECK_DELIVERED,
    MIC,
    STICKER,
    GIF,
    CLIP_VIDEO,
    PAPERCLIP,
    SEND,
    PHONE_CALL,
    VIDEO_CALL,
    MORE_VERTICAL,
    ARROW_LEFT,
    REPLY,
    FORWARD,
    COPY,
    PIN,
    STAR,
    SELECT_CHECK,
    TRASH,
    PENCIL_EDIT,
    UNSEND_UNDO,
    LOCK_E2EE,
    SHIELD_VERIFIED,
    PLAY,
    PAUSE,
    VIEW_ONCE_ONE,
    TIMER_DISAPPEARING,
    SEARCH,
    CLOSE_X,
    BAN_BLOCK,
    FLAG_REPORT,
    MEDIA_GALLERY,
    LINK_CHAIN,
    FILE_DOC,
    MESSAGE_PLUS,
    QR_CODE,
    REACTION_HEART,
    REACTION_THUMBS_UP,
    REACTION_FIRE,
    REACTION_LAUGH,
    REACTION_SURPRISED,
    REACTION_PRAY,
    ALERT_TRIANGLE,
    REFRESH_RETRY,
    MAIL_EMAIL,
    USER_PLUS_CONTACT,
    EXTERNAL_LINK,
    GLOBE_WEB,
    KEY_OTP,
    CAMERA,
    MAP_PIN,
    USER_CONTACT,
    BAR_CHART_POLL,
    CALENDAR_EVENT,
    DOWNLOAD,
    PLUS,
    ZOOM_IN,
    PACKAGE_APP,
    MUSIC_AUDIO,
    NAVIGATION_LIVE,
    EYE_VIEW,
    TYPE_TEXT,
    CROP_FRAME,
    BRUSH_PEN,
    PALETTE_COLOR,
    SCISSORS_TRIM,
    SLIDERS_FILTER,
    AT_SIGN,
    HASH_TAG,
    USERS_FRIENDS,
    ROTATE_CW,
    SPARKLES_STUDIO,
    FILM_THUMBNAIL,
    VOLUME_MIXER,
    BOOKMARK_SAVE,
    MESSAGE_CIRCLE_DOTS,
    MORE_HORIZONTAL,
    CHEVRON_RIGHT,
    EYE_OFF_NOT_INTERESTED,
    REPEAT_RESHARE,
    PHONE_OFF,
    MIC_OFF,
    VIDEO_OFF,
    VOLUME_SPEAKER,
    CAMERA_SWITCH,
    CALL_INCOMING,
    CALL_OUTGOING,
    CALL_MISSED,
    SIGNAL_CELLULAR,
    MAGIC_WAND,
    SHIELD_LOCK
}

/**
 * Pure SVG/Canvas Lucide 24x24 vector icon renderer (`stroke-width = 2`, `stroke-linecap = round`, `stroke-linejoin = round`).
 * Strictly no emoji characters are used anywhere as UI icons.
 */
@Composable
fun TelefamLucideIcon(
    kind: LucideIconKind,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    strokeWidthDp: Dp = 1.9.dp,
    filled: Boolean = false
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sw = strokeWidthDp.toPx()
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun sx(v: Float) = (v / 24f) * w
        fun sy(v: Float) = (v / 24f) * h

        when (kind) {
            LucideIconKind.CLOCK -> {
                drawCircle(color = tint, radius = sx(9f), center = Offset(sx(12f), sy(12f)), style = stroke)
                val p = Path().apply {
                    moveTo(sx(12f), sy(7f))
                    lineTo(sx(12f), sy(12f))
                    lineTo(sx(15.5f), sy(14f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.CHECK_SINGLE -> {
                val p = Path().apply {
                    moveTo(sx(20f), sy(6f))
                    lineTo(sx(9f), sy(17f))
                    lineTo(sx(4f), sy(12f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.CIRCLE_CHECK_DELIVERED -> {
                // Small cycle check in the red theme accent = delivered
                if (filled) {
                    drawCircle(color = tint, radius = sx(9.5f), center = Offset(sx(12f), sy(12f)), style = Fill)
                } else {
                    drawCircle(color = tint, radius = sx(9.2f), center = Offset(sx(12f), sy(12f)), style = stroke)
                }
                val checkColor = if (filled) Color.White else tint
                val p = Path().apply {
                    moveTo(sx(16.2f), sy(9.2f))
                    lineTo(sx(10.8f), sy(14.8f))
                    lineTo(sx(7.8f), sy(11.8f))
                }
                drawPath(p, color = checkColor, style = stroke)
            }

            LucideIconKind.MIC -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(9f), sy(2.5f)),
                    size = Size(sx(6f), sy(11f)),
                    cornerRadius = CornerRadius(sx(3f), sy(3f)),
                    style = stroke
                )
                drawArc(
                    color = tint,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(sx(5f), sy(6.5f)),
                    size = Size(sx(14f), sy(11f)),
                    style = stroke
                )
                drawLine(color = tint, start = Offset(sx(12f), sy(17.5f)), end = Offset(sx(12f), sy(21.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(8.5f), sy(21.5f)), end = Offset(sx(15.5f), sy(21.5f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.STICKER -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(3.5f), sy(3.5f)),
                    size = Size(sx(17f), sy(17f)),
                    cornerRadius = CornerRadius(sx(4.5f), sy(4.5f)),
                    style = stroke
                )
                drawCircle(color = tint, radius = sx(1.2f), center = Offset(sx(9f), sy(10f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.2f), center = Offset(sx(15f), sy(10f)), style = Fill)
                drawArc(
                    color = tint,
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(sx(8.2f), sy(11f)),
                    size = Size(sx(7.6f), sy(5.5f)),
                    style = stroke
                )
            }

            LucideIconKind.GIF -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(2.5f), sy(5f)),
                    size = Size(sx(19f), sy(14f)),
                    cornerRadius = CornerRadius(sx(3.2f), sy(3.2f)),
                    style = stroke
                )
                // G
                val gPath = Path().apply {
                    moveTo(sx(9.2f), sy(9.5f))
                    lineTo(sx(6.2f), sy(9.5f))
                    lineTo(sx(6.2f), sy(14.5f))
                    lineTo(sx(9.2f), sy(14.5f))
                    lineTo(sx(9.2f), sy(12.2f))
                    lineTo(sx(7.8f), sy(12.2f))
                }
                drawPath(gPath, color = tint, style = stroke)
                // I
                drawLine(color = tint, start = Offset(sx(12f), sy(9.5f)), end = Offset(sx(12f), sy(14.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                // F
                val fPath = Path().apply {
                    moveTo(sx(17.8f), sy(9.5f))
                    lineTo(sx(14.8f), sy(9.5f))
                    lineTo(sx(14.8f), sy(14.5f))
                    moveTo(sx(14.8f), sy(12f))
                    lineTo(sx(17.2f), sy(12f))
                }
                drawPath(fPath, color = tint, style = stroke)
            }

            LucideIconKind.CLIP_VIDEO -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(3f), sy(4.5f)),
                    size = Size(sx(18f), sy(15f)),
                    cornerRadius = CornerRadius(sx(3f), sy(3f)),
                    style = stroke
                )
                val tri = Path().apply {
                    moveTo(sx(10f), sy(9f))
                    lineTo(sx(15.5f), sy(12f))
                    lineTo(sx(10f), sy(15f))
                    close()
                }
                drawPath(tri, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.PAPERCLIP -> {
                val p = Path().apply {
                    moveTo(sx(21.4f), sy(11.0f))
                    lineTo(sx(12.2f), sy(20.2f))
                    cubicTo(sx(10.0f), sy(22.4f), sx(6.4f), sy(22.4f), sx(4.2f), sy(20.2f))
                    cubicTo(sx(2.0f), sy(18.0f), sx(2.0f), sy(14.4f), sx(4.2f), sy(12.2f))
                    lineTo(sx(13.4f), sy(3.0f))
                    cubicTo(sx(14.9f), sy(1.5f), sx(17.3f), sy(1.5f), sx(18.8f), sy(3.0f))
                    cubicTo(sx(20.3f), sy(4.5f), sx(20.3f), sy(6.9f), sx(18.8f), sy(8.4f))
                    lineTo(sx(9.6f), sy(17.6f))
                    cubicTo(sx(8.8f), sy(18.4f), sx(7.6f), sy(18.4f), sx(6.8f), sy(17.6f))
                    cubicTo(sx(6.0f), sy(16.8f), sx(6.0f), sy(15.6f), sx(6.8f), sy(14.8f))
                    lineTo(sx(15.3f), sy(6.3f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.SEND -> {
                val p = Path().apply {
                    moveTo(sx(22f), sy(2f))
                    lineTo(sx(11f), sy(13f))
                    moveTo(sx(22f), sy(2f))
                    lineTo(sx(15f), sy(22f))
                    lineTo(sx(11f), sy(13f))
                    lineTo(sx(2f), sy(9f))
                    close()
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.PHONE_CALL -> {
                val p = Path().apply {
                    moveTo(sx(22f), sy(16.9f))
                    lineTo(sx(22f), sy(19.9f))
                    cubicTo(sx(22f), sy(21.0f), sx(21.1f), sy(21.9f), sx(20f), sy(21.9f))
                    cubicTo(sx(10.6f), sy(21.4f), sx(2.6f), sy(13.4f), sx(2.1f), sy(4f))
                    cubicTo(sx(2.1f), sy(2.9f), sx(3.0f), sy(2f), sx(4.1f), sy(2f))
                    lineTo(sx(7.1f), sy(2f))
                    lineTo(sx(9.1f), sy(7.2f))
                    lineTo(sx(6.8f), sy(9.2f))
                    cubicTo(sx(8.2f), sy(12.1f), sx(11.9f), sy(15.8f), sx(14.8f), sy(17.2f))
                    lineTo(sx(16.8f), sy(14.9f))
                    close()
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.VIDEO_CALL -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(2.5f), sy(6f)),
                    size = Size(sx(13f), sy(12f)),
                    cornerRadius = CornerRadius(sx(2.5f), sy(2.5f)),
                    style = stroke
                )
                val p = Path().apply {
                    moveTo(sx(15.5f), sy(10f))
                    lineTo(sx(21.5f), sy(6.5f))
                    lineTo(sx(21.5f), sy(17.5f))
                    lineTo(sx(15.5f), sy(14f))
                    close()
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.MORE_VERTICAL -> {
                drawCircle(color = tint, radius = sx(1.4f), center = Offset(sx(12f), sy(5f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.4f), center = Offset(sx(12f), sy(12f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.4f), center = Offset(sx(12f), sy(19f)), style = Fill)
            }

            LucideIconKind.ARROW_LEFT -> {
                drawLine(color = tint, start = Offset(sx(19f), sy(12f)), end = Offset(sx(5f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
                val p = Path().apply {
                    moveTo(sx(12f), sy(19f))
                    lineTo(sx(5f), sy(12f))
                    lineTo(sx(12f), sy(5f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.REPLY -> {
                val p = Path().apply {
                    moveTo(sx(9f), sy(14f))
                    lineTo(sx(4f), sy(9f))
                    lineTo(sx(9f), sy(4f))
                    moveTo(sx(4f), sy(9f))
                    lineTo(sx(15f), sy(9f))
                    cubicTo(sx(18.5f), sy(9f), sx(20f), sy(11.5f), sx(20f), sy(15f))
                    lineTo(sx(20f), sy(19f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.FORWARD -> {
                val p = Path().apply {
                    moveTo(sx(15f), sy(14f))
                    lineTo(sx(20f), sy(9f))
                    lineTo(sx(15f), sy(4f))
                    moveTo(sx(20f), sy(9f))
                    lineTo(sx(9f), sy(9f))
                    cubicTo(sx(5.5f), sy(9f), sx(4f), sy(11.5f), sx(4f), sy(15f))
                    lineTo(sx(4f), sy(19f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.COPY -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(9f), sy(9f)),
                    size = Size(sx(11f), sy(11f)),
                    cornerRadius = CornerRadius(sx(2f), sy(2f)),
                    style = stroke
                )
                val p = Path().apply {
                    moveTo(sx(5f), sy(15f))
                    lineTo(sx(4f), sy(15f))
                    cubicTo(sx(3.4f), sy(15f), sx(3f), sy(14.5f), sx(3f), sy(14f))
                    lineTo(sx(3f), sy(5f))
                    cubicTo(sx(3f), sy(4.4f), sx(3.4f), sy(4f), sx(4f), sy(4f))
                    lineTo(sx(13f), sy(4f))
                    cubicTo(sx(13.6f), sy(4f), sx(14f), sy(4.4f), sx(14f), sy(5f))
                    lineTo(sx(14f), sy(6f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.PIN -> {
                val p = Path().apply {
                    moveTo(sx(12f), sy(17f))
                    lineTo(sx(12f), sy(22f))
                    moveTo(sx(5f), sy(17f))
                    lineTo(sx(19f), sy(17f))
                    lineTo(sx(17f), sy(13f))
                    lineTo(sx(17f), sy(7f))
                    lineTo(sx(18.5f), sy(5f))
                    lineTo(sx(5.5f), sy(5f))
                    lineTo(sx(7f), sy(7f))
                    lineTo(sx(7f), sy(13f))
                    close()
                }
                drawPath(p, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.STAR -> {
                val p = Path().apply {
                    moveTo(sx(12f), sy(2.5f))
                    lineTo(sx(14.9f), sy(8.5f))
                    lineTo(sx(21.5f), sy(9.4f))
                    lineTo(sx(16.7f), sy(14.0f))
                    lineTo(sx(17.9f), sy(20.6f))
                    lineTo(sx(12f), sy(17.5f))
                    lineTo(sx(6.1f), sy(20.6f))
                    lineTo(sx(7.3f), sy(14.0f))
                    lineTo(sx(2.5f), sy(9.4f))
                    lineTo(sx(9.1f), sy(8.5f))
                    close()
                }
                drawPath(p, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.SELECT_CHECK -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(3.5f), sy(3.5f)),
                    size = Size(sx(17f), sy(17f)),
                    cornerRadius = CornerRadius(sx(3f), sy(3f)),
                    style = if (filled) Fill else stroke
                )
                val checkCol = if (filled) Color.White else tint
                val p = Path().apply {
                    moveTo(sx(8.5f), sy(12f))
                    lineTo(sx(11f), sy(14.5f))
                    lineTo(sx(16f), sy(9.5f))
                }
                drawPath(p, color = checkCol, style = stroke)
            }

            LucideIconKind.TRASH -> {
                drawLine(color = tint, start = Offset(sx(3f), sy(6f)), end = Offset(sx(21f), sy(6f)), strokeWidth = sw, cap = StrokeCap.Round)
                val p = Path().apply {
                    moveTo(sx(8f), sy(6f))
                    lineTo(sx(8f), sy(4f))
                    lineTo(sx(16f), sy(4f))
                    lineTo(sx(16f), sy(6f))
                    moveTo(sx(6f), sy(6f))
                    lineTo(sx(7f), sy(20f))
                    lineTo(sx(17f), sy(20f))
                    lineTo(sx(18f), sy(6f))
                    moveTo(sx(10f), sy(11f))
                    lineTo(sx(10f), sy(16f))
                    moveTo(sx(14f), sy(11f))
                    lineTo(sx(14f), sy(16f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.PENCIL_EDIT -> {
                val p = Path().apply {
                    moveTo(sx(17f), sy(3f))
                    lineTo(sx(21f), sy(7f))
                    lineTo(sx(8f), sy(20f))
                    lineTo(sx(3f), sy(21f))
                    lineTo(sx(4f), sy(16f))
                    close()
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.UNSEND_UNDO -> {
                val p = Path().apply {
                    moveTo(sx(3f), sy(7f))
                    lineTo(sx(3f), sy(13f))
                    lineTo(sx(9f), sy(13f))
                }
                drawPath(p, color = tint, style = stroke)
                drawArc(
                    color = tint,
                    startAngle = 200f,
                    sweepAngle = 290f,
                    useCenter = false,
                    topLeft = Offset(sx(4f), sy(5f)),
                    size = Size(sx(16f), sy(15f)),
                    style = stroke
                )
            }

            LucideIconKind.LOCK_E2EE -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(5f), sy(11f)),
                    size = Size(sx(14f), sy(10f)),
                    cornerRadius = CornerRadius(sx(2.5f), sy(2.5f)),
                    style = stroke
                )
                drawArc(
                    color = tint,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(sx(8f), sy(4f)),
                    size = Size(sx(8f), sy(10f)),
                    style = stroke
                )
                drawCircle(color = tint, radius = sx(1.2f), center = Offset(sx(12f), sy(16f)), style = Fill)
            }

            LucideIconKind.SHIELD_VERIFIED -> {
                val p = Path().apply {
                    moveTo(sx(12f), sy(2.5f))
                    lineTo(sx(19.5f), sy(5.5f))
                    lineTo(sx(19.5f), sy(11.5f))
                    cubicTo(sx(19.5f), sy(16.5f), sx(16.2f), sy(20.0f), sx(12f), sy(21.8f))
                    cubicTo(sx(7.8f), sy(20.0f), sx(4.5f), sy(16.5f), sx(4.5f), sy(11.5f))
                    lineTo(sx(4.5f), sy(5.5f))
                    close()
                }
                drawPath(p, color = tint, style = stroke)
                val check = Path().apply {
                    moveTo(sx(9f), sy(12.2f))
                    lineTo(sx(11.2f), sy(14.4f))
                    lineTo(sx(15.4f), sy(10.0f))
                }
                drawPath(check, color = tint, style = stroke)
            }

            LucideIconKind.PLAY -> {
                val p = Path().apply {
                    moveTo(sx(7f), sy(4.5f))
                    lineTo(sx(19f), sy(12f))
                    lineTo(sx(7f), sy(19.5f))
                    close()
                }
                drawPath(p, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.PAUSE -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(6.5f), sy(4.5f)),
                    size = Size(sx(3.8f), sy(15f)),
                    cornerRadius = CornerRadius(sx(1.2f), sy(1.2f)),
                    style = if (filled) Fill else stroke
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(13.7f), sy(4.5f)),
                    size = Size(sx(3.8f), sy(15f)),
                    cornerRadius = CornerRadius(sx(1.2f), sy(1.2f)),
                    style = if (filled) Fill else stroke
                )
            }

            LucideIconKind.VIEW_ONCE_ONE -> {
                drawCircle(color = tint, radius = sx(9f), center = Offset(sx(12f), sy(12f)), style = stroke)
                val p = Path().apply {
                    moveTo(sx(10.5f), sy(9.5f))
                    lineTo(sx(12.5f), sy(8f))
                    lineTo(sx(12.5f), sy(16f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.TIMER_DISAPPEARING -> {
                drawLine(color = tint, start = Offset(sx(10f), sy(2.5f)), end = Offset(sx(14f), sy(2.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawCircle(color = tint, radius = sx(8f), center = Offset(sx(12f), sy(13.5f)), style = stroke)
                drawLine(color = tint, start = Offset(sx(12f), sy(13.5f)), end = Offset(sx(15f), sy(10.5f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.SEARCH -> {
                drawCircle(color = tint, radius = sx(7f), center = Offset(sx(11f), sy(11f)), style = stroke)
                drawLine(color = tint, start = Offset(sx(16.2f), sy(16.2f)), end = Offset(sx(21f), sy(21f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.CLOSE_X -> {
                drawLine(color = tint, start = Offset(sx(18f), sy(6f)), end = Offset(sx(6f), sy(18f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(6f), sy(6f)), end = Offset(sx(18f), sy(18f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.BAN_BLOCK -> {
                drawCircle(color = tint, radius = sx(9f), center = Offset(sx(12f), sy(12f)), style = stroke)
                drawLine(color = tint, start = Offset(sx(5.7f), sy(5.7f)), end = Offset(sx(18.3f), sy(18.3f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.FLAG_REPORT -> {
                val p = Path().apply {
                    moveTo(sx(5f), sy(21f))
                    lineTo(sx(5f), sy(4f))
                    lineTo(sx(19f), sy(4f))
                    lineTo(sx(16f), sy(9f))
                    lineTo(sx(19f), sy(14f))
                    lineTo(sx(5f), sy(14f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.MEDIA_GALLERY -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(3f), sy(3f)),
                    size = Size(sx(18f), sy(18f)),
                    cornerRadius = CornerRadius(sx(3f), sy(3f)),
                    style = stroke
                )
                drawCircle(color = tint, radius = sx(1.8f), center = Offset(sx(8.5f), sy(8.5f)), style = stroke)
                val p = Path().apply {
                    moveTo(sx(21f), sy(15f))
                    lineTo(sx(16f), sy(10f))
                    lineTo(sx(5f), sy(21f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.LINK_CHAIN -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(3f), sy(8f)),
                    size = Size(sx(9f), sy(8f)),
                    cornerRadius = CornerRadius(sx(4f), sy(4f)),
                    style = stroke
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(12f), sy(8f)),
                    size = Size(sx(9f), sy(8f)),
                    cornerRadius = CornerRadius(sx(4f), sy(4f)),
                    style = stroke
                )
                drawLine(color = tint, start = Offset(sx(8f), sy(12f)), end = Offset(sx(16f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.FILE_DOC -> {
                val p = Path().apply {
                    moveTo(sx(14f), sy(2.5f))
                    lineTo(sx(6f), sy(2.5f))
                    lineTo(sx(6f), sy(21.5f))
                    lineTo(sx(18f), sy(21.5f))
                    lineTo(sx(18f), sy(6.5f))
                    close()
                    moveTo(sx(14f), sy(2.5f))
                    lineTo(sx(14f), sy(6.5f))
                    lineTo(sx(18f), sy(6.5f))
                    moveTo(sx(9f), sy(12f))
                    lineTo(sx(15f), sy(12f))
                    moveTo(sx(9f), sy(16f))
                    lineTo(sx(15f), sy(16f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.MESSAGE_PLUS -> {
                val p = Path().apply {
                    moveTo(sx(21f), sy(15f))
                    lineTo(sx(21f), sy(6f))
                    cubicTo(sx(21f), sy(4.3f), sx(19.7f), sy(3f), sx(18f), sy(3f))
                    lineTo(sx(6f), sy(3f))
                    cubicTo(sx(4.3f), sy(3f), sx(3f), sy(4.3f), sx(3f), sy(6f))
                    lineTo(sx(3f), sy(21f))
                    lineTo(sx(7f), sy(17f))
                    lineTo(sx(18f), sy(17f))
                    cubicTo(sx(19.7f), sy(17f), sx(21f), sy(16.1f), sx(21f), sy(15f))
                    close()
                }
                drawPath(p, color = tint, style = stroke)
                drawLine(color = tint, start = Offset(sx(12f), sy(7.5f)), end = Offset(sx(12f), sy(12.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(9.5f), sy(10f)), end = Offset(sx(14.5f), sy(10f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.QR_CODE -> {
                drawRoundRect(color = tint, topLeft = Offset(sx(3f), sy(3f)), size = Size(sx(7f), sy(7f)), cornerRadius = CornerRadius(sx(1.5f)), style = stroke)
                drawRoundRect(color = tint, topLeft = Offset(sx(14f), sy(3f)), size = Size(sx(7f), sy(7f)), cornerRadius = CornerRadius(sx(1.5f)), style = stroke)
                drawRoundRect(color = tint, topLeft = Offset(sx(3f), sy(14f)), size = Size(sx(7f), sy(7f)), cornerRadius = CornerRadius(sx(1.5f)), style = stroke)
                drawRect(color = tint, topLeft = Offset(sx(14f), sy(14f)), size = Size(sx(3f), sy(3f)))
                drawRect(color = tint, topLeft = Offset(sx(18f), sy(18f)), size = Size(sx(3f), sy(3f)))
            }

            LucideIconKind.REACTION_HEART -> {
                val p = Path().apply {
                    moveTo(sx(12f), sy(20.5f))
                    cubicTo(sx(12f), sy(20.5f), sx(3.5f), sy(15f), sx(3.5f), sy(8.8f))
                    cubicTo(sx(3.5f), sy(5.8f), sx(5.9f), sy(3.8f), sx(8.5f), sy(3.8f))
                    cubicTo(sx(10.2f), sy(3.8f), sx(11.4f), sy(4.7f), sx(12f), sy(6.0f))
                    cubicTo(sx(12.6f), sy(4.7f), sx(13.8f), sy(3.8f), sx(15.5f), sy(3.8f))
                    cubicTo(sx(18.1f), sy(3.8f), sx(20.5f), sy(5.8f), sx(20.5f), sy(8.8f))
                    cubicTo(sx(20.5f), sy(15f), sx(12f), sy(20.5f), sx(12f), sy(20.5f))
                    close()
                }
                drawPath(p, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.REACTION_THUMBS_UP -> {
                val p = Path().apply {
                    moveTo(sx(7f), sy(10f))
                    lineTo(sx(11.5f), sy(3f))
                    cubicTo(sx(12.8f), sy(3f), sx(14f), sy(4.2f), sx(13.8f), sy(5.8f))
                    lineTo(sx(13.2f), sy(9.5f))
                    lineTo(sx(19.2f), sy(9.5f))
                    cubicTo(sx(20.4f), sy(9.5f), sx(21.2f), sy(10.6f), sx(20.9f), sy(11.8f))
                    lineTo(sx(19.3f), sy(18.8f))
                    cubicTo(sx(19.0f), sy(19.6f), sx(18.2f), sy(20.2f), sx(17.2f), sy(20.2f))
                    lineTo(sx(7f), sy(20.2f))
                    close()
                    moveTo(sx(3.5f), sy(10f))
                    lineTo(sx(7f), sy(10f))
                    lineTo(sx(7f), sy(20.2f))
                    lineTo(sx(3.5f), sy(20.2f))
                    close()
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.REACTION_FIRE -> {
                val p = Path().apply {
                    moveTo(sx(12f), sy(2.5f))
                    cubicTo(sx(14.5f), sy(6.5f), sx(19f), sy(9f), sx(19f), sy(14.2f))
                    cubicTo(sx(19f), sy(18.2f), sx(15.9f), sy(21.2f), sx(12f), sy(21.2f))
                    cubicTo(sx(8.1f), sy(21.2f), sx(5f), sy(18.2f), sx(5f), sy(14.2f))
                    cubicTo(sx(5f), sy(10.5f), sx(8.2f), sy(7.8f), sx(12f), sy(2.5f))
                    close()
                }
                drawPath(p, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.REACTION_LAUGH -> {
                drawCircle(color = tint, radius = sx(9f), center = Offset(sx(12f), sy(12f)), style = stroke)
                drawCircle(color = tint, radius = sx(1.1f), center = Offset(sx(9f), sy(9.5f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.1f), center = Offset(sx(15f), sy(9.5f)), style = Fill)
                drawArc(
                    color = tint,
                    startAngle = 10f,
                    sweepAngle = 160f,
                    useCenter = true,
                    topLeft = Offset(sx(7.8f), sy(11f)),
                    size = Size(sx(8.4f), sy(6.2f)),
                    style = stroke
                )
            }

            LucideIconKind.REACTION_SURPRISED -> {
                drawCircle(color = tint, radius = sx(9f), center = Offset(sx(12f), sy(12f)), style = stroke)
                drawCircle(color = tint, radius = sx(1.1f), center = Offset(sx(9f), sy(9.5f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.1f), center = Offset(sx(15f), sy(9.5f)), style = Fill)
                drawCircle(color = tint, radius = sx(2.3f), center = Offset(sx(12f), sy(15.2f)), style = stroke)
            }

            LucideIconKind.REACTION_PRAY -> {
                val p = Path().apply {
                    moveTo(sx(12f), sy(3f))
                    lineTo(sx(13.8f), sy(9.2f))
                    lineTo(sx(20f), sy(11f))
                    lineTo(sx(13.8f), sy(12.8f))
                    lineTo(sx(12f), sy(19f))
                    lineTo(sx(10.2f), sy(12.8f))
                    lineTo(sx(4f), sy(11f))
                    lineTo(sx(10.2f), sy(9.2f))
                    close()
                }
                drawPath(p, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.ALERT_TRIANGLE -> {
                val p = Path().apply {
                    moveTo(sx(12f), sy(3.5f))
                    lineTo(sx(21.5f), sy(20f))
                    lineTo(sx(2.5f), sy(20f))
                    close()
                }
                drawPath(p, color = tint, style = stroke)
                drawLine(color = tint, start = Offset(sx(12f), sy(9.5f)), end = Offset(sx(12f), sy(14f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawCircle(color = tint, radius = sx(1f), center = Offset(sx(12f), sy(17.2f)), style = Fill)
            }

            LucideIconKind.REFRESH_RETRY -> {
                drawArc(
                    color = tint,
                    startAngle = 25f,
                    sweepAngle = 295f,
                    useCenter = false,
                    topLeft = Offset(sx(3.8f), sy(3.8f)),
                    size = Size(sx(16.4f), sy(16.4f)),
                    style = stroke
                )
                val arrow = Path().apply {
                    moveTo(sx(20.5f), sy(4f))
                    lineTo(sx(20.5f), sy(9.5f))
                    lineTo(sx(15f), sy(9.5f))
                }
                drawPath(arrow, color = tint, style = stroke)
            }

            LucideIconKind.MAIL_EMAIL -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(2.5f), sy(4.5f)),
                    size = Size(sx(19f), sy(15f)),
                    cornerRadius = CornerRadius(sx(2.5f), sy(2.5f)),
                    style = stroke
                )
                val flap = Path().apply {
                    moveTo(sx(3f), sy(6.5f))
                    lineTo(sx(12f), sy(13f))
                    lineTo(sx(21f), sy(6.5f))
                }
                drawPath(flap, color = tint, style = stroke)
            }

            LucideIconKind.USER_PLUS_CONTACT -> {
                drawCircle(
                    color = tint,
                    radius = sx(3.8f),
                    center = Offset(sx(9f), sy(7.5f)),
                    style = stroke
                )
                val body = Path().apply {
                    moveTo(sx(2.5f), sy(20f))
                    cubicTo(sx(2.5f), sy(15.5f), sx(5.5f), sy(14f), sx(9f), sy(14f))
                    cubicTo(sx(12.5f), sy(14f), sx(15.5f), sy(15.5f), sx(15.5f), sy(20f))
                }
                drawPath(body, color = tint, style = stroke)
                drawLine(
                    color = tint,
                    start = Offset(sx(19f), sy(8f)),
                    end = Offset(sx(19f), sy(14f)),
                    strokeWidth = sw,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = tint,
                    start = Offset(sx(16f), sy(11f)),
                    end = Offset(sx(22f), sy(11f)),
                    strokeWidth = sw,
                    cap = StrokeCap.Round
                )
            }

            LucideIconKind.EXTERNAL_LINK -> {
                val boxPath = Path().apply {
                    moveTo(sx(18f), sy(13f))
                    lineTo(sx(18f), sy(19f))
                    cubicTo(sx(18f), sy(20.1f), sx(17.1f), sy(21f), sx(16f), sy(21f))
                    lineTo(sx(5f), sy(21f))
                    cubicTo(sx(3.9f), sy(21f), sx(3f), sy(20.1f), sx(3f), sy(19f))
                    lineTo(sx(3f), sy(8f))
                    cubicTo(sx(3f), sy(6.9f), sx(3.9f), sy(6f), sx(5f), sy(6f))
                    lineTo(sx(11f), sy(6f))
                }
                drawPath(boxPath, color = tint, style = stroke)
                val arrowHead = Path().apply {
                    moveTo(sx(15f), sy(3f))
                    lineTo(sx(21f), sy(3f))
                    lineTo(sx(21f), sy(9f))
                }
                drawPath(arrowHead, color = tint, style = stroke)
                drawLine(
                    color = tint,
                    start = Offset(sx(10f), sy(14f)),
                    end = Offset(sx(21f), sy(3f)),
                    strokeWidth = sw,
                    cap = StrokeCap.Round
                )
            }

            LucideIconKind.GLOBE_WEB -> {
                drawCircle(
                    color = tint,
                    radius = sx(9.2f),
                    center = Offset(sx(12f), sy(12f)),
                    style = stroke
                )
                drawLine(
                    color = tint,
                    start = Offset(sx(2.8f), sy(12f)),
                    end = Offset(sx(21.2f), sy(12f)),
                    strokeWidth = sw,
                    cap = StrokeCap.Round
                )
                val meridian = Path().apply {
                    moveTo(sx(12f), sy(2.8f))
                    cubicTo(sx(15.5f), sy(6.5f), sx(15.5f), sy(17.5f), sx(12f), sy(21.2f))
                    moveTo(sx(12f), sy(2.8f))
                    cubicTo(sx(8.5f), sy(6.5f), sx(8.5f), sy(17.5f), sx(12f), sy(21.2f))
                }
                drawPath(meridian, color = tint, style = stroke)
            }

            LucideIconKind.KEY_OTP -> {
                drawCircle(
                    color = tint,
                    radius = sx(4.2f),
                    center = Offset(sx(7.5f), sy(15.5f)),
                    style = stroke
                )
                val shaft = Path().apply {
                    moveTo(sx(10.5f), sy(12.5f))
                    lineTo(sx(20.5f), sy(2.5f))
                    moveTo(sx(16.5f), sy(6.5f))
                    lineTo(sx(19.5f), sy(9.5f))
                    moveTo(sx(19f), sy(4f))
                    lineTo(sx(21.5f), sy(6.5f))
                }
                drawPath(shaft, color = tint, style = stroke)
            }

            LucideIconKind.CAMERA -> {
                val body = Path().apply {
                    moveTo(sx(14.5f), sy(4f))
                    lineTo(sx(16.5f), sy(7f))
                    lineTo(sx(20f), sy(7f))
                    cubicTo(sx(21.1f), sy(7f), sx(22f), sy(7.9f), sx(22f), sy(9f))
                    lineTo(sx(22f), sy(18f))
                    cubicTo(sx(22f), sy(19.1f), sx(21.1f), sy(20f), sx(20f), sy(20f))
                    lineTo(sx(4f), sy(20f))
                    cubicTo(sx(2.9f), sy(20f), sx(2f), sy(19.1f), sx(2f), sy(18f))
                    lineTo(sx(2f), sy(9f))
                    cubicTo(sx(2f), sy(7.9f), sx(2.9f), sy(7f), sx(4f), sy(7f))
                    lineTo(sx(7.5f), sy(7f))
                    lineTo(sx(9.5f), sy(4f))
                    close()
                }
                drawPath(body, color = tint, style = stroke)
                drawCircle(
                    color = tint,
                    radius = sx(3.5f),
                    center = Offset(sx(12f), sy(13.2f)),
                    style = stroke
                )
            }

            LucideIconKind.MAP_PIN -> {
                val pin = Path().apply {
                    moveTo(sx(12f), sy(21.5f))
                    cubicTo(sx(12f), sy(21.5f), sx(19f), sy(15.5f), sx(19f), sy(10f))
                    cubicTo(sx(19f), sy(6.1f), sx(15.9f), sy(3f), sx(12f), sy(3f))
                    cubicTo(sx(8.1f), sy(3f), sx(5f), sy(6.1f), sx(5f), sy(10f))
                    cubicTo(sx(5f), sy(15.5f), sx(12f), sy(21.5f), sx(12f), sy(21.5f))
                    close()
                }
                drawPath(pin, color = tint, style = if (filled) Fill else stroke)
                drawCircle(
                    color = if (filled) Color.White else tint,
                    radius = sx(2.6f),
                    center = Offset(sx(12f), sy(10f)),
                    style = if (filled) Fill else stroke
                )
            }

            LucideIconKind.USER_CONTACT -> {
                drawCircle(
                    color = tint,
                    radius = sx(4f),
                    center = Offset(sx(12f), sy(8f)),
                    style = stroke
                )
                val shoulders = Path().apply {
                    moveTo(sx(5f), sy(20f))
                    cubicTo(sx(5f), sy(16f), sx(8f), sy(14f), sx(12f), sy(14f))
                    cubicTo(sx(16f), sy(14f), sx(19f), sy(16f), sx(19f), sy(20f))
                }
                drawPath(shoulders, color = tint, style = stroke)
            }

            LucideIconKind.BAR_CHART_POLL -> {
                drawLine(
                    color = tint,
                    start = Offset(sx(6f), sy(20f)),
                    end = Offset(sx(6f), sy(13f)),
                    strokeWidth = sw * 1.15f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = tint,
                    start = Offset(sx(12f), sy(20f)),
                    end = Offset(sx(12f), sy(5f)),
                    strokeWidth = sw * 1.15f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = tint,
                    start = Offset(sx(18f), sy(20f)),
                    end = Offset(sx(18f), sy(9f)),
                    strokeWidth = sw * 1.15f,
                    cap = StrokeCap.Round
                )
            }

            LucideIconKind.CALENDAR_EVENT -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(3f), sy(4.5f)),
                    size = Size(sx(18f), sy(16.5f)),
                    cornerRadius = CornerRadius(sx(2.5f), sy(2.5f)),
                    style = stroke
                )
                drawLine(color = tint, start = Offset(sx(8f), sy(2.5f)), end = Offset(sx(8f), sy(6.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(16f), sy(2.5f)), end = Offset(sx(16f), sy(6.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(3f), sy(9.5f)), end = Offset(sx(21f), sy(9.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(12f), sy(12.5f)), end = Offset(sx(12f), sy(17.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(9.5f), sy(15f)), end = Offset(sx(14.5f), sy(15f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.DOWNLOAD -> {
                drawLine(
                    color = tint,
                    start = Offset(sx(12f), sy(3.5f)),
                    end = Offset(sx(12f), sy(15f)),
                    strokeWidth = sw,
                    cap = StrokeCap.Round
                )
                val arrow = Path().apply {
                    moveTo(sx(7.5f), sy(10.5f))
                    lineTo(sx(12f), sy(15f))
                    lineTo(sx(16.5f), sy(10.5f))
                }
                drawPath(arrow, color = tint, style = stroke)
                val tray = Path().apply {
                    moveTo(sx(4f), sy(17f))
                    lineTo(sx(4f), sy(19f))
                    cubicTo(sx(4f), sy(20.1f), sx(4.9f), sy(21f), sx(6f), sy(21f))
                    lineTo(sx(18f), sy(21f))
                    cubicTo(sx(19.1f), sy(21f), sx(20f), sy(20.1f), sx(20f), sy(19f))
                    lineTo(sx(20f), sy(17f))
                }
                drawPath(tray, color = tint, style = stroke)
            }

            LucideIconKind.PLUS -> {
                drawLine(color = tint, start = Offset(sx(12f), sy(5f)), end = Offset(sx(12f), sy(19f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(5f), sy(12f)), end = Offset(sx(19f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.ZOOM_IN -> {
                drawCircle(color = tint, radius = sx(7.5f), center = Offset(sx(11f), sy(11f)), style = stroke)
                drawLine(color = tint, start = Offset(sx(16.5f), sy(16.5f)), end = Offset(sx(21f), sy(21f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(11f), sy(8f)), end = Offset(sx(11f), sy(14f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(8f), sy(11f)), end = Offset(sx(14f), sy(11f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.PACKAGE_APP -> {
                val cube = Path().apply {
                    moveTo(sx(12f), sy(2.5f))
                    lineTo(sx(20.5f), sy(7f))
                    lineTo(sx(20.5f), sy(17f))
                    lineTo(sx(12f), sy(21.5f))
                    lineTo(sx(3.5f), sy(17f))
                    lineTo(sx(3.5f), sy(7f))
                    close()
                }
                drawPath(cube, color = tint, style = stroke)
                drawLine(color = tint, start = Offset(sx(3.5f), sy(7f)), end = Offset(sx(12f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(20.5f), sy(7f)), end = Offset(sx(12f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(12f), sy(12f)), end = Offset(sx(12f), sy(21.5f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.MUSIC_AUDIO -> {
                val note = Path().apply {
                    moveTo(sx(9f), sy(18f))
                    lineTo(sx(9f), sy(5f))
                    lineTo(sx(20f), sy(3.5f))
                    lineTo(sx(20f), sy(16.5f))
                }
                drawPath(note, color = tint, style = stroke)
                drawCircle(color = tint, radius = sx(2.8f), center = Offset(sx(6.2f), sy(18f)), style = stroke)
                drawCircle(color = tint, radius = sx(2.8f), center = Offset(sx(17.2f), sy(16.5f)), style = stroke)
            }

            LucideIconKind.NAVIGATION_LIVE -> {
                val nav = Path().apply {
                    moveTo(sx(3f), sy(11f))
                    lineTo(sx(21f), sy(3f))
                    lineTo(sx(13f), sy(21f))
                    lineTo(sx(11f), sy(13f))
                    close()
                }
                drawPath(nav, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.EYE_VIEW -> {
                val eye = Path().apply {
                    moveTo(sx(2f), sy(12f))
                    cubicTo(sx(4.5f), sy(6.5f), sx(8.2f), sy(5f), sx(12f), sy(5f))
                    cubicTo(sx(15.8f), sy(5f), sx(19.5f), sy(6.5f), sx(22f), sy(12f))
                    cubicTo(sx(19.5f), sy(17.5f), sx(15.8f), sy(19f), sx(12f), sy(19f))
                    cubicTo(sx(8.2f), sy(19f), sx(4.5f), sy(17.5f), sx(2f), sy(12f))
                    close()
                }
                drawPath(eye, color = tint, style = stroke)
                drawCircle(color = tint, radius = sx(3f), center = Offset(sx(12f), sy(12f)), style = stroke)
            }

            LucideIconKind.TYPE_TEXT -> {
                val topBar = Path().apply {
                    moveTo(sx(4f), sy(7f))
                    lineTo(sx(4f), sy(4f))
                    lineTo(sx(20f), sy(4f))
                    lineTo(sx(20f), sy(7f))
                }
                drawPath(topBar, color = tint, style = stroke)
                drawLine(color = tint, start = Offset(sx(9f), sy(20f)), end = Offset(sx(15f), sy(20f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(12f), sy(4f)), end = Offset(sx(12f), sy(20f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.CROP_FRAME -> {
                val p1 = Path().apply {
                    moveTo(sx(6f), sy(2f))
                    lineTo(sx(6f), sy(16f))
                    cubicTo(sx(6f), sy(17.1f), sx(6.9f), sy(18f), sx(8f), sy(18f))
                    lineTo(sx(22f), sy(18f))
                }
                val p2 = Path().apply {
                    moveTo(sx(18f), sy(22f))
                    lineTo(sx(18f), sy(8f))
                    cubicTo(sx(18f), sy(6.9f), sx(17.1f), sy(6f), sx(16f), sy(6f))
                    lineTo(sx(2f), sy(6f))
                }
                drawPath(p1, color = tint, style = stroke)
                drawPath(p2, color = tint, style = stroke)
            }

            LucideIconKind.BRUSH_PEN -> {
                val handle = Path().apply {
                    moveTo(sx(18.4f), sy(2.6f))
                    cubicTo(sx(19.2f), sy(1.8f), sx(20.5f), sy(1.8f), sx(21.4f), sy(2.6f))
                    cubicTo(sx(22.2f), sy(3.5f), sx(22.2f), sy(4.8f), sx(21.4f), sy(5.6f))
                    lineTo(sx(14f), sy(13f))
                    lineTo(sx(11f), sy(10f))
                    close()
                }
                drawPath(handle, color = tint, style = stroke)
                val tip = Path().apply {
                    moveTo(sx(11f), sy(10f))
                    cubicTo(sx(8f), sy(10.5f), sx(6f), sy(12.5f), sx(6f), sy(15.5f))
                    cubicTo(sx(6f), sy(18f), sx(3.5f), sy(19.5f), sx(2.5f), sy(20f))
                    cubicTo(sx(4.5f), sy(21.5f), sx(8.5f), sy(21.5f), sx(11f), sy(19f))
                    cubicTo(sx(13f), sy(17f), sx(14f), sy(15f), sx(14f), sy(13f))
                }
                drawPath(tip, color = tint, style = stroke)
            }

            LucideIconKind.PALETTE_COLOR -> {
                drawCircle(color = tint, radius = sx(9.5f), center = Offset(sx(12f), sy(12f)), style = stroke)
                drawCircle(color = tint, radius = sx(1.3f), center = Offset(sx(8f), sy(10f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.3f), center = Offset(sx(11.5f), sy(7.5f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.3f), center = Offset(sx(15.5f), sy(9.5f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.6f), center = Offset(sx(15f), sy(15f)), style = stroke)
            }

            LucideIconKind.SCISSORS_TRIM -> {
                drawCircle(color = tint, radius = sx(3f), center = Offset(sx(6f), sy(6f)), style = stroke)
                drawCircle(color = tint, radius = sx(3f), center = Offset(sx(6f), sy(18f)), style = stroke)
                drawLine(color = tint, start = Offset(sx(20f), sy(4f)), end = Offset(sx(8.12f), sy(15.88f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(14.47f), sy(14.48f)), end = Offset(sx(20f), sy(20f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(8.12f), sy(8.12f)), end = Offset(sx(12f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.SLIDERS_FILTER -> {
                drawLine(color = tint, start = Offset(sx(4f), sy(21f)), end = Offset(sx(4f), sy(14f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(4f), sy(10f)), end = Offset(sx(4f), sy(3f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(12f), sy(21f)), end = Offset(sx(12f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(12f), sy(8f)), end = Offset(sx(12f), sy(3f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(20f), sy(21f)), end = Offset(sx(20f), sy(16f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(20f), sy(12f)), end = Offset(sx(20f), sy(3f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(2f), sy(14f)), end = Offset(sx(6f), sy(14f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(10f), sy(8f)), end = Offset(sx(14f), sy(8f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(18f), sy(16f)), end = Offset(sx(22f), sy(16f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.AT_SIGN -> {
                drawCircle(color = tint, radius = sx(4f), center = Offset(sx(12f), sy(12f)), style = stroke)
                val arc = Path().apply {
                    moveTo(sx(16f), sy(8f))
                    lineTo(sx(16f), sy(13f))
                    cubicTo(sx(16f), sy(14.7f), sx(17.3f), sy(16f), sx(19f), sy(16f))
                    cubicTo(sx(20.7f), sy(16f), sx(21.5f), sy(14.5f), sx(21.5f), sy(12f))
                    cubicTo(sx(21.5f), sy(6.75f), sx(17.25f), sy(2.5f), sx(12f), sy(2.5f))
                    cubicTo(sx(6.75f), sy(2.5f), sx(2.5f), sy(6.75f), sx(2.5f), sy(12f))
                    cubicTo(sx(2.5f), sy(17.25f), sx(6.75f), sy(21.5f), sx(12f), sy(21.5f))
                    cubicTo(sx(14.5f), sy(21.5f), sx(16.8f), sy(20.6f), sx(18.5f), sy(19f))
                }
                drawPath(arc, color = tint, style = stroke)
            }

            LucideIconKind.HASH_TAG -> {
                drawLine(color = tint, start = Offset(sx(4f), sy(9f)), end = Offset(sx(20f), sy(9f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(4f), sy(15f)), end = Offset(sx(20f), sy(15f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(10f), sy(3f)), end = Offset(sx(8f), sy(21f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(16f), sy(3f)), end = Offset(sx(14f), sy(21f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.USERS_FRIENDS -> {
                drawCircle(color = tint, radius = sx(3.5f), center = Offset(sx(9f), sy(7f)), style = stroke)
                val body1 = Path().apply {
                    moveTo(sx(2.5f), sy(20f))
                    cubicTo(sx(2.5f), sy(16.2f), sx(5.4f), sy(14f), sx(9f), sy(14f))
                    cubicTo(sx(12.6f), sy(14f), sx(15.5f), sy(16.2f), sx(15.5f), sy(20f))
                }
                drawPath(body1, color = tint, style = stroke)
                drawCircle(color = tint, radius = sx(2.8f), center = Offset(sx(17f), sy(8f)), style = stroke)
                val body2 = Path().apply {
                    moveTo(sx(17.5f), sy(14.2f))
                    cubicTo(sx(19.8f), sy(14.6f), sx(21.5f), sy(16.5f), sx(21.5f), sy(20f))
                }
                drawPath(body2, color = tint, style = stroke)
            }

            LucideIconKind.ROTATE_CW -> {
                val arc = Path().apply {
                    moveTo(sx(21f), sy(12f))
                    cubicTo(sx(21f), sy(16.97f), sx(16.97f), sy(21f), sx(12f), sy(21f))
                    cubicTo(sx(7.03f), sy(21f), sx(3f), sy(16.97f), sx(3f), sy(12f))
                    cubicTo(sx(3f), sy(7.03f), sx(7.03f), sy(3f), sx(12f), sy(3f))
                    cubicTo(sx(15.3f), sy(3f), sx(18.2f), sy(4.8f), sx(19.8f), sy(7.5f))
                }
                drawPath(arc, color = tint, style = stroke)
                val head = Path().apply {
                    moveTo(sx(21f), sy(3f))
                    lineTo(sx(21f), sy(8f))
                    lineTo(sx(16f), sy(8f))
                }
                drawPath(head, color = tint, style = stroke)
            }

            LucideIconKind.SPARKLES_STUDIO -> {
                val star = Path().apply {
                    moveTo(sx(12f), sy(3f))
                    lineTo(sx(14.2f), sy(9.8f))
                    lineTo(sx(21f), sy(12f))
                    lineTo(sx(14.2f), sy(14.2f))
                    lineTo(sx(12f), sy(21f))
                    lineTo(sx(9.8f), sy(14.2f))
                    lineTo(sx(3f), sy(12f))
                    lineTo(sx(9.8f), sy(9.8f))
                    close()
                }
                drawPath(star, color = tint, style = stroke)
            }

            LucideIconKind.FILM_THUMBNAIL -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(3f), sy(4f)),
                    size = Size(sx(18f), sy(16f)),
                    cornerRadius = CornerRadius(sx(2.5f), sy(2.5f)),
                    style = stroke
                )
                drawLine(color = tint, start = Offset(sx(7f), sy(4f)), end = Offset(sx(7f), sy(20f)), strokeWidth = sw)
                drawLine(color = tint, start = Offset(sx(17f), sy(4f)), end = Offset(sx(17f), sy(20f)), strokeWidth = sw)
                drawLine(color = tint, start = Offset(sx(3f), sy(9f)), end = Offset(sx(7f), sy(9f)), strokeWidth = sw)
                drawLine(color = tint, start = Offset(sx(3f), sy(15f)), end = Offset(sx(7f), sy(15f)), strokeWidth = sw)
                drawLine(color = tint, start = Offset(sx(17f), sy(9f)), end = Offset(sx(21f), sy(9f)), strokeWidth = sw)
                drawLine(color = tint, start = Offset(sx(17f), sy(15f)), end = Offset(sx(21f), sy(15f)), strokeWidth = sw)
            }

            LucideIconKind.VOLUME_MIXER -> {
                val spk = Path().apply {
                    moveTo(sx(11f), sy(5f))
                    lineTo(sx(6f), sy(9f))
                    lineTo(sx(2f), sy(9f))
                    lineTo(sx(2f), sy(15f))
                    lineTo(sx(6f), sy(15f))
                    lineTo(sx(11f), sy(19f))
                    close()
                }
                drawPath(spk, color = tint, style = stroke)
                val w1 = Path().apply {
                    moveTo(sx(15.5f), sy(8.5f))
                    cubicTo(sx(17.5f), sy(10.5f), sx(17.5f), sy(13.5f), sx(15.5f), sy(15.5f))
                }
                val w2 = Path().apply {
                    moveTo(sx(19f), sy(5f))
                    cubicTo(sx(22.5f), sy(8.5f), sx(22.5f), sy(15.5f), sx(19f), sy(19f))
                }
                drawPath(w1, color = tint, style = stroke)
                drawPath(w2, color = tint, style = stroke)
            }

            LucideIconKind.BOOKMARK_SAVE -> {
                val p = Path().apply {
                    moveTo(sx(6f), sy(3.5f))
                    lineTo(sx(18f), sy(3.5f))
                    lineTo(sx(18f), sy(20.8f))
                    lineTo(sx(12f), sy(16.2f))
                    lineTo(sx(6f), sy(20.8f))
                    close()
                }
                if (filled) {
                    drawPath(p, color = tint, style = Fill)
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.MESSAGE_CIRCLE_DOTS -> {
                val bubble = Path().apply {
                    moveTo(sx(21f), sy(11.5f))
                    cubicTo(sx(21f), sy(16.47f), sx(16.97f), sy(20.5f), sx(12f), sy(20.5f))
                    cubicTo(sx(10.4f), sy(20.5f), sx(8.9f), sy(20.1f), sx(7.6f), sy(19.3f))
                    lineTo(sx(3f), sy(20.8f))
                    lineTo(sx(4.5f), sy(16.2f))
                    cubicTo(sx(3.55f), sy(14.8f), sx(3f), sy(13.2f), sx(3f), sy(11.5f))
                    cubicTo(sx(3f), sy(6.53f), sx(7.03f), sy(2.5f), sx(12f), sy(2.5f))
                    cubicTo(sx(16.97f), sy(2.5f), sx(21f), sy(6.53f), sx(21f), sy(11.5f))
                    close()
                }
                if (filled) {
                    drawPath(bubble, color = tint, style = Fill)
                }
                drawPath(bubble, color = tint, style = stroke)
                val dotColor = if (filled) Color(0xFF121216) else tint
                drawCircle(color = dotColor, radius = sx(1.25f), center = Offset(sx(8.5f), sy(11.5f)), style = Fill)
                drawCircle(color = dotColor, radius = sx(1.25f), center = Offset(sx(12f), sy(11.5f)), style = Fill)
                drawCircle(color = dotColor, radius = sx(1.25f), center = Offset(sx(15.5f), sy(11.5f)), style = Fill)
            }

            LucideIconKind.MORE_HORIZONTAL -> {
                drawCircle(color = tint, radius = sx(1.7f), center = Offset(sx(5f), sy(12f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.7f), center = Offset(sx(12f), sy(12f)), style = Fill)
                drawCircle(color = tint, radius = sx(1.7f), center = Offset(sx(19f), sy(12f)), style = Fill)
            }

            LucideIconKind.CHEVRON_RIGHT -> {
                val p = Path().apply {
                    moveTo(sx(9f), sy(6f))
                    lineTo(sx(15f), sy(12f))
                    lineTo(sx(9f), sy(18f))
                }
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.EYE_OFF_NOT_INTERESTED -> {
                val eye = Path().apply {
                    moveTo(sx(2f), sy(12f))
                    cubicTo(sx(5f), sy(6.5f), sx(9f), sy(5f), sx(12f), sy(5f))
                    cubicTo(sx(15f), sy(5f), sx(19f), sy(6.5f), sx(22f), sy(12f))
                    cubicTo(sx(19f), sy(17.5f), sx(15f), sy(19f), sx(12f), sy(19f))
                    cubicTo(sx(9f), sy(19f), sx(5f), sy(17.5f), sx(2f), sy(12f))
                    close()
                }
                drawPath(eye, color = tint, style = stroke)
                drawCircle(color = tint, radius = sx(2.6f), center = Offset(sx(12f), sy(12f)), style = stroke)
                drawLine(color = tint, start = Offset(sx(3f), sy(3f)), end = Offset(sx(21f), sy(21f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.REPEAT_RESHARE -> {
                val topArrow = Path().apply {
                    moveTo(sx(17f), sy(2f))
                    lineTo(sx(21f), sy(6f))
                    lineTo(sx(17f), sy(10f))
                }
                val topLine = Path().apply {
                    moveTo(sx(3f), sy(11f))
                    lineTo(sx(3f), sy(9f))
                    cubicTo(sx(3f), sy(7.3f), sx(4.3f), sy(6f), sx(6f), sy(6f))
                    lineTo(sx(21f), sy(6f))
                }
                val botArrow = Path().apply {
                    moveTo(sx(7f), sy(22f))
                    lineTo(sx(3f), sy(18f))
                    lineTo(sx(7f), sy(14f))
                }
                val botLine = Path().apply {
                    moveTo(sx(21f), sy(13f))
                    lineTo(sx(21f), sy(15f))
                    cubicTo(sx(21f), sy(16.7f), sx(19.7f), sy(18f), sx(18f), sy(18f))
                    lineTo(sx(3f), sy(18f))
                }
                drawPath(topLine, color = tint, style = stroke)
                drawPath(topArrow, color = tint, style = stroke)
                drawPath(botLine, color = tint, style = stroke)
                drawPath(botArrow, color = tint, style = stroke)
            }

            LucideIconKind.PHONE_OFF -> {
                // Hang up / End Call handset (horizontal downward curve)
                val p = Path().apply {
                    moveTo(sx(4f), sy(10f))
                    cubicTo(sx(8f), sy(6f), sx(16f), sy(6f), sx(20f), sy(10f))
                    lineTo(sx(18f), sy(13f))
                    cubicTo(sx(15.5f), sy(11.5f), sx(13.5f), sy(11f), sx(12f), sy(11f))
                    cubicTo(sx(10.5f), sy(11f), sx(8.5f), sy(11.5f), sx(6f), sy(13f))
                    close()
                }
                drawPath(p, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.MIC_OFF -> {
                // Mic outline
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(9f), sy(3f)),
                    size = Size(sx(6f), sy(10f)),
                    cornerRadius = CornerRadius(sx(3f), sy(3f)),
                    style = stroke
                )
                // Cradle arc
                val arc = Path().apply {
                    moveTo(sx(5f), sy(10f))
                    cubicTo(sx(5f), sy(15f), sx(19f), sy(15f), sx(19f), sy(10f))
                }
                drawPath(arc, color = tint, style = stroke)
                drawLine(color = tint, start = Offset(sx(12f), sy(17f)), end = Offset(sx(12f), sy(21f)), strokeWidth = sw, cap = StrokeCap.Round)
                // Diagonal slash
                drawLine(color = tint, start = Offset(sx(3f), sy(3f)), end = Offset(sx(21f), sy(21f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.VIDEO_OFF -> {
                // Camera box
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(2.5f), sy(6f)),
                    size = Size(sx(13f), sy(12f)),
                    cornerRadius = CornerRadius(sx(2.5f), sy(2.5f)),
                    style = stroke
                )
                val p = Path().apply {
                    moveTo(sx(15.5f), sy(10f))
                    lineTo(sx(21.5f), sy(6.5f))
                    lineTo(sx(21.5f), sy(17.5f))
                    lineTo(sx(15.5f), sy(14f))
                    close()
                }
                drawPath(p, color = tint, style = stroke)
                // Slash
                drawLine(color = tint, start = Offset(sx(2f), sy(2f)), end = Offset(sx(22f), sy(22f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.VOLUME_SPEAKER -> {
                // Speaker body
                val spk = Path().apply {
                    moveTo(sx(11f), sy(5f))
                    lineTo(sx(6f), sy(9f))
                    lineTo(sx(2f), sy(9f))
                    lineTo(sx(2f), sy(15f))
                    lineTo(sx(6f), sy(15f))
                    lineTo(sx(11f), sy(19f))
                    close()
                }
                drawPath(spk, color = tint, style = stroke)
                // Sound wave arcs
                val arc1 = Path().apply {
                    moveTo(sx(15.5f), sy(8.5f))
                    cubicTo(sx(17.5f), sy(10.5f), sx(17.5f), sy(13.5f), sx(15.5f), sy(15.5f))
                }
                val arc2 = Path().apply {
                    moveTo(sx(19f), sy(5f))
                    cubicTo(sx(23f), sy(9f), sx(23f), sy(15f), sx(19f), sy(19f))
                }
                drawPath(arc1, color = tint, style = stroke)
                drawPath(arc2, color = tint, style = stroke)
            }

            LucideIconKind.CAMERA_SWITCH -> {
                // Camera outline
                val cam = Path().apply {
                    moveTo(sx(2f), sy(8f))
                    cubicTo(sx(2f), sy(6.9f), sx(2.9f), sy(6f), sx(4f), sy(6f))
                    lineTo(sx(7f), sy(6f))
                    lineTo(sx(8.5f), sy(3.5f))
                    lineTo(sx(15.5f), sy(3.5f))
                    lineTo(sx(17f), sy(6f))
                    lineTo(sx(20f), sy(6f))
                    cubicTo(sx(21.1f), sy(6f), sx(22f), sy(6.9f), sx(22f), sy(8f))
                    lineTo(sx(22f), sy(19f))
                    cubicTo(sx(22f), sy(20.1f), sx(21.1f), sy(21f), sx(20f), sy(21f))
                    lineTo(sx(4f), sy(21f))
                    cubicTo(sx(2.9f), sy(21f), sx(2f), sy(20.1f), sx(2f), sy(19f))
                    close()
                }
                drawPath(cam, color = tint, style = stroke)
                // Rotating arrows inside lens
                val arrowPath = Path().apply {
                    moveTo(sx(15f), sy(11f))
                    lineTo(sx(14f), sy(12.5f))
                    lineTo(sx(12.5f), sy(11f))
                }
                val arrowPath2 = Path().apply {
                    moveTo(sx(9f), sy(16f))
                    lineTo(sx(10f), sy(14.5f))
                    lineTo(sx(11.5f), sy(16f))
                }
                drawCircle(color = tint, radius = sx(3.5f), center = Offset(sx(12f), sy(13.5f)), style = stroke)
                drawPath(arrowPath, color = tint, style = stroke)
                drawPath(arrowPath2, color = tint, style = stroke)
            }

            LucideIconKind.CALL_INCOMING -> {
                val p = Path().apply {
                    moveTo(sx(16f), sy(2f))
                    lineTo(sx(16f), sy(8f))
                    lineTo(sx(10f), sy(8f))
                }
                drawLine(color = tint, start = Offset(sx(16f), sy(2f)), end = Offset(sx(6f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.CALL_OUTGOING -> {
                val p = Path().apply {
                    moveTo(sx(8f), sy(2f))
                    lineTo(sx(16f), sy(2f))
                    lineTo(sx(16f), sy(10f))
                }
                drawLine(color = tint, start = Offset(sx(6f), sy(12f)), end = Offset(sx(16f), sy(2f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawPath(p, color = tint, style = stroke)
            }

            LucideIconKind.CALL_MISSED -> {
                val p = Path().apply {
                    moveTo(sx(16f), sy(2f))
                    lineTo(sx(16f), sy(8f))
                    lineTo(sx(10f), sy(8f))
                }
                drawLine(color = tint, start = Offset(sx(16f), sy(2f)), end = Offset(sx(6f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawPath(p, color = tint, style = stroke)
                // Exclamation dot
                drawCircle(color = tint, radius = sx(1.2f), center = Offset(sx(20f), sy(6f)), style = Fill)
            }

            LucideIconKind.SIGNAL_CELLULAR -> {
                // 4 bars
                drawLine(color = tint, start = Offset(sx(4f), sy(19f)), end = Offset(sx(4f), sy(16f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(9f), sy(19f)), end = Offset(sx(9f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(14f), sy(19f)), end = Offset(sx(14f), sy(8f)), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color = tint, start = Offset(sx(19f), sy(19f)), end = Offset(sx(19f), sy(4f)), strokeWidth = sw, cap = StrokeCap.Round)
            }

            LucideIconKind.MAGIC_WAND -> {
                // Wand diagonal shaft
                drawLine(color = tint, start = Offset(sx(4f), sy(20f)), end = Offset(sx(14f), sy(10f)), strokeWidth = sw, cap = StrokeCap.Round)
                // Sparkle star at top right
                val star = Path().apply {
                    moveTo(sx(18f), sy(2f))
                    lineTo(sx(19f), sy(5f))
                    lineTo(sx(22f), sy(6f))
                    lineTo(sx(19f), sy(7f))
                    lineTo(sx(18f), sy(10f))
                    lineTo(sx(17f), sy(7f))
                    lineTo(sx(14f), sy(6f))
                    lineTo(sx(17f), sy(5f))
                    close()
                }
                drawPath(star, color = tint, style = if (filled) Fill else stroke)
            }

            LucideIconKind.SHIELD_LOCK -> {
                val shield = Path().apply {
                    moveTo(sx(12f), sy(2f))
                    lineTo(sx(20f), sy(5f))
                    cubicTo(sx(20f), sy(12f), sx(16f), sy(18f), sx(12f), sy(22f))
                    cubicTo(sx(8f), sy(18f), sx(4f), sy(12f), sx(4f), sy(5f))
                    close()
                }
                drawPath(shield, color = tint, style = stroke)
                // Inner lock body
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(sx(9.5f), sy(11f)),
                    size = Size(sx(5f), sy(4.5f)),
                    cornerRadius = CornerRadius(sx(1f), sy(1f)),
                    style = stroke
                )
                val shackle = Path().apply {
                    moveTo(sx(10.5f), sy(11f))
                    cubicTo(sx(10.5f), sy(9.5f), sx(13.5f), sy(9.5f), sx(13.5f), sy(11f))
                }
                drawPath(shackle, color = tint, style = stroke)
            }
        }
    }
}

fun ChatReactionChoice.toLucideIconKind(): LucideIconKind {
    return when (this) {
        ChatReactionChoice.HEART -> LucideIconKind.REACTION_HEART
        ChatReactionChoice.THUMBS_UP -> LucideIconKind.REACTION_THUMBS_UP
        ChatReactionChoice.FIRE -> LucideIconKind.REACTION_FIRE
        ChatReactionChoice.LAUGH -> LucideIconKind.REACTION_LAUGH
        ChatReactionChoice.SURPRISED -> LucideIconKind.REACTION_SURPRISED
        ChatReactionChoice.PRAY -> LucideIconKind.REACTION_PRAY
    }
}
