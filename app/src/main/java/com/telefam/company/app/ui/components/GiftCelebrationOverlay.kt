package com.telefam.company.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.TelefamRed
import com.telefam.company.app.domain.VirtualGiftItem
import kotlinx.coroutines.delay

data class ActiveGiftCelebration(
    val senderUsername: String,
    val senderFullName: String,
    val senderAvatarUrl: String?,
    val gift: VirtualGiftItem,
    val comboCount: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * TikTok-style animated virtual gift celebration overlay.
 * Non-blocking, smooth 60fps animations with:
 * - Glassmorphic slide-in banner showing sender recognition
 * - Pulsing combo counter (x1, x2, x5...)
 * - Particle burst Canvas animation with glowing trails
 * - Centered high-resolution scaling gift artwork
 */
@Composable
fun GiftCelebrationOverlay(
    activeCelebration: ActiveGiftCelebration?,
    onCelebrationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeCelebration == null) return

    val scaleAnim = remember { Animatable(0.2f) }
    val comboPulseAnim = remember { Animatable(1f) }
    val particleProgress = remember { Animatable(0f) }

    LaunchedEffect(activeCelebration.timestamp, activeCelebration.comboCount) {
        scaleAnim.snapTo(0.3f)
        particleProgress.snapTo(0f)
        comboPulseAnim.snapTo(1.6f)

        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
        )
        comboPulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f)
        )
        particleProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
        )
        delay(800)
        onCelebrationFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("gift_celebration_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Dynamic Canvas Particle Burst
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val p = particleProgress.value
            val colors = listOf(Color(0xFFFFD700), Color(0xFFFF3B30), Color(0xFF00F5D4), Color(0xFFB5179E), Color(0xFF4CC9F0))
            if (p in 0.01f..0.99f) {
                for (i in 0 until 18) {
                    val angle = Math.toRadians((i * 20.0))
                    val dist = (p * size.minDimension * 0.45f)
                    val x = center.x + (dist * Math.cos(angle)).toFloat()
                    val y = center.y + (dist * Math.sin(angle)).toFloat()
                    val alpha = (1f - p).coerceIn(0f, 1f)
                    drawCircle(
                        color = colors[i % colors.size].copy(alpha = alpha),
                        radius = (1f - p) * 8f + 2f,
                        center = Offset(x, y)
                    )
                }
            }
        }

        // 2. Center Animated Gift Artwork
        Box(
            modifier = Modifier
                .scale(scaleAnim.value)
                .size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glowing background aura
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFFFD700).copy(alpha = 0.45f),
                                Color(0xFFFF3B30).copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )
            VirtualGiftVectorArtwork(
                giftId = activeCelebration.gift.id,
                modifier = Modifier.size(130.dp)
            )
        }

        // 3. Top Banner with Sender Recognition and Animated Combo Streak
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color.Black.copy(alpha = 0.75f),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(start = 6.dp, top = 6.dp, bottom = 6.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sender avatar
                    if (!activeCelebration.senderAvatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = activeCelebration.senderAvatarUrl,
                            contentDescription = activeCelebration.senderUsername,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TelefamRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = activeCelebration.senderUsername.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = activeCelebration.senderFullName.ifBlank { "@${activeCelebration.senderUsername}" },
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Sent ${activeCelebration.gift.name}",
                            color = Color(0xFFFFD700),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Combo Multiplier Badge: e.g. x1, x2, x5...
                    Box(
                        modifier = Modifier
                            .scale(comboPulseAnim.value)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF3B30), Color(0xFFFF9500))
                                )
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "x${activeCelebration.comboCount}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
