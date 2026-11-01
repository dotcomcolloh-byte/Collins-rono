package com.telefam.company.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.telefam.company.app.domain.ActiveCallSession
import com.telefam.company.app.domain.CallStatus
import com.telefam.company.app.ui.components.LucideIconKind
import com.telefam.company.app.ui.components.TelefamLucideIcon

@Composable
fun AudioCallScreen(
    call: ActiveCallSession,
    onMuteToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onSwitchToVideo: () -> Unit,
    onMinimize: () -> Unit,
    onEndCall: () -> Unit,
    onOpenE2eeDetails: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler {
        onMinimize()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )
    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1.15f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1F1117),
                        Color(0xFF140D13),
                        Color(0xFF0D0A0E)
                    )
                )
            )
            .testTag("audio_call_screen")
    ) {
        // Top Bar (Minimize, verified shield)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 20.dp, end = 20.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMinimize,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .testTag("audio_call_minimize_button")
            ) {
                TelefamLucideIcon(
                    kind = LucideIconKind.ARROW_LEFT,
                    tint = Color.White,
                    size = 20.dp
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.4f),
                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.clickable { onOpenE2eeDetails() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.LOCK_E2EE,
                        tint = Color.White.copy(alpha = 0.85f),
                        size = 14.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End-to-end Encrypted",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
            }

            IconButton(
                onClick = onOpenE2eeDetails,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                TelefamLucideIcon(
                    kind = LucideIconKind.SHIELD_VERIFIED,
                    tint = Color.White,
                    size = 20.dp
                )
            }
        }

        // Center Content: Pulsing Avatar, Name, Call Duration
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .size((140 * pulse2).dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935).copy(alpha = 0.08f))
                )
                // Mid ring
                Box(
                    modifier = Modifier
                        .size((140 * pulse1).dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935).copy(alpha = 0.16f))
                )
                // Avatar
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C1E26)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!call.peerAvatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = call.peerAvatarUrl,
                            contentDescription = call.peerFullName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        TelefamLucideIcon(
                            kind = LucideIconKind.USER_CONTACT,
                            tint = Color.White.copy(alpha = 0.8f),
                            size = 60.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = call.peerFullName.ifBlank { "User" },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (call.status) {
                    CallStatus.RINGING -> if (call.isOutgoing) "Ringing..." else "Incoming Audio Call..."
                    CallStatus.CONNECTING -> "Connecting WebRTC..."
                    CallStatus.CONNECTED -> call.formattedDuration
                    CallStatus.RECONNECTING -> "Reconnecting..."
                    CallStatus.ENDED -> "Call Ended"
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Switch to Video Call action
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.clickable { onSwitchToVideo() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.VIDEO_CALL,
                        tint = Color.White,
                        size = 18.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Switch to Video",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 44.dp, start = 28.dp, end = 28.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute
            IconButton(
                onClick = onMuteToggle,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (call.isMuted) Color(0xFFE53935) else Color.White.copy(alpha = 0.18f))
                    .testTag("audio_call_mute_button")
            ) {
                TelefamLucideIcon(
                    kind = if (call.isMuted) LucideIconKind.MIC_OFF else LucideIconKind.MIC,
                    tint = Color.White,
                    size = 24.dp
                )
            }

            // End Call
            IconButton(
                onClick = onEndCall,
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935))
                    .testTag("audio_call_end_button")
            ) {
                TelefamLucideIcon(
                    kind = LucideIconKind.PHONE_OFF,
                    tint = Color.White,
                    size = 32.dp
                )
            }

            // Speaker
            IconButton(
                onClick = onSpeakerToggle,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (call.isSpeakerOn) Color(0xFF1E88E5) else Color.White.copy(alpha = 0.18f))
                    .testTag("audio_call_speaker_button")
            ) {
                TelefamLucideIcon(
                    kind = LucideIconKind.VOLUME_SPEAKER,
                    tint = Color.White,
                    size = 24.dp
                )
            }
        }
    }
}
