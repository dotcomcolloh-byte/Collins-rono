package com.telefam.company.app.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.telefam.company.app.domain.ActiveCallSession
import com.telefam.company.app.domain.CallStatus
import com.telefam.company.app.domain.CallType
import com.telefam.company.app.domain.NetworkQuality
import com.telefam.company.app.domain.VideoCallFilter
import com.telefam.company.app.ui.components.LucideIconKind
import com.telefam.company.app.ui.components.TelefamLucideIcon
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCallScreen(
    call: ActiveCallSession,
    localAvatarPath: String?,
    onMuteToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onCameraFlip: () -> Unit,
    onFilterSelect: (VideoCallFilter) -> Unit,
    onMinimize: () -> Unit,
    onEndCall: () -> Unit,
    onOpenE2eeDetails: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler {
        onMinimize()
    }

    var isFilterSheetOpen by remember { mutableStateOf(false) }
    var isDiagnosticsSheetOpen by remember { mutableStateOf(false) }

    // Draggable PiP offset
    var pipOffsetX by remember { mutableFloatStateOf(0f) }
    var pipOffsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F12))
            .testTag("video_call_screen")
    ) {
        // -------------------------------------------------------------
        // 1. REMOTE VIEW (Full Screen Video or Avatar Fallback)
        // -------------------------------------------------------------
        if (call.isRemoteCameraEnabled && call.callType == CallType.VIDEO && call.status == CallStatus.CONNECTED) {
            // Simulated live remote WebRTC video surface with applied active filter
            Box(modifier = Modifier.fillMaxSize()) {
                if (!call.peerAvatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = call.peerAvatarUrl,
                        contentDescription = "Remote Video Stream",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Modern gradient background representing video frame
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF2A1620),
                                        Color(0xFF1B1B22),
                                        Color(0xFF0F0F12)
                                    )
                                )
                            )
                    )
                }

                // Filter Overlay Shader tint
                if (call.activeFilter.overlayAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        call.activeFilter.primaryColor.copy(alpha = call.activeFilter.overlayAlpha),
                                        call.activeFilter.secondaryColor.copy(alpha = call.activeFilter.overlayAlpha * 0.7f)
                                    )
                                )
                            )
                    )
                }

                // Subtle top and bottom dark gradient for high contrast UI elements
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )
            }
        } else {
            // "if someone close video call screen turn video to avator"
            // High-quality avatar fallback representation
            RemoteAvatarFallbackView(
                call = call,
                modifier = Modifier.fillMaxSize()
            )
        }

        // -------------------------------------------------------------
        // 2. TOP HEADER (Minimize, Peer Name, Timer, E2EE, Security)
        // Reference image exact layout
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 44.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Minimize Button (Chevron down inside subtle circle)
                IconButton(
                    onClick = onMinimize,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .testTag("call_minimize_button")
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.ARROW_LEFT,
                        tint = Color.White,
                        size = 20.dp
                    )
                }

                // Center Column: Name and Timer
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = call.peerFullName.ifBlank { "User" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = when (call.status) {
                            CallStatus.RINGING -> if (call.isOutgoing) "Ringing..." else "Incoming call..."
                            CallStatus.CONNECTING -> "Connecting WebRTC..."
                            CallStatus.CONNECTED -> call.formattedDuration
                            CallStatus.RECONNECTING -> "Reconnecting..."
                            CallStatus.ENDED -> "Call Ended"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Security Shield Icon Button
                IconButton(
                    onClick = onOpenE2eeDetails,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .testTag("call_e2ee_button")
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.SHIELD_VERIFIED,
                        tint = Color.White,
                        size = 20.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // End-to-end Encrypted Pill Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.5f),
                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.18f)),
                modifier = Modifier.clickable { onOpenE2eeDetails() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.LOCK_E2EE,
                        tint = Color.White.copy(alpha = 0.9f),
                        size = 14.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End-to-end Encrypted",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 3. FLOATING PIP LOCAL PREVIEW (Top Right, Draggable)
        // Reference image exact layout with Flip & Filter Buttons
        // -------------------------------------------------------------
        if (call.callType == CallType.VIDEO) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset { IntOffset(pipOffsetX.roundToInt(), pipOffsetY.roundToInt()) }
                    .padding(top = 105.dp, end = 16.dp)
                    .size(width = 135.dp, height = 200.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                    .background(Color(0xFF1E1E24))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            pipOffsetX += dragAmount.x
                            pipOffsetY += dragAmount.y
                        }
                    }
                    .testTag("call_pip_window")
            ) {
                if (call.isCameraEnabled) {
                    // Local camera stream preview
                    if (!localAvatarPath.isNullOrBlank()) {
                        AsyncImage(
                            model = localAvatarPath,
                            contentDescription = "Local Camera Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF37474F), Color(0xFF263238))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            TelefamLucideIcon(
                                kind = LucideIconKind.CAMERA,
                                tint = Color.White.copy(alpha = 0.7f),
                                size = 32.dp
                            )
                        }
                    }

                    // Active filter tint preview
                    if (call.activeFilter.overlayAlpha > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            call.activeFilter.primaryColor.copy(alpha = call.activeFilter.overlayAlpha * 0.9f),
                                            call.activeFilter.secondaryColor.copy(alpha = call.activeFilter.overlayAlpha * 0.6f)
                                        )
                                    )
                                )
                        )
                    }
                } else {
                    // Turn video to avatar when camera is toggled off
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF1B1B20)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF33333E)),
                                contentAlignment = Alignment.Center
                            ) {
                                TelefamLucideIcon(
                                    kind = LucideIconKind.USER_CONTACT,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    size = 28.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Camera Off",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // PiP Bottom Overlay Buttons (Flip Camera & Filters Wand)
                // Exactly as in reference image!
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 10.dp, start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Camera Flip Button
                    IconButton(
                        onClick = onCameraFlip,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .testTag("pip_flip_camera_button")
                    ) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.CAMERA_SWITCH,
                            tint = Color.White,
                            size = 18.dp
                        )
                    }

                    // Magic Wand / Filters Button
                    IconButton(
                        onClick = { isFilterSheetOpen = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .testTag("pip_filters_button")
                    ) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.MAGIC_WAND,
                            tint = Color.White,
                            size = 18.dp,
                            filled = true
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 4. NETWORK ADAPTATION INDICATOR PILL
        // Exact match from image: "[green bars] Good Connection >"
        // -------------------------------------------------------------
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 142.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Black.copy(alpha = 0.65f),
                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier
                    .clickable { isDiagnosticsSheetOpen = true }
                    .testTag("call_network_quality_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelefamLucideIcon(
                        kind = LucideIconKind.SIGNAL_CELLULAR,
                        tint = Color(call.networkQuality.colorHex),
                        size = 16.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = call.networkQuality.label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TelefamLucideIcon(
                        kind = LucideIconKind.CHEVRON_RIGHT,
                        tint = Color.White.copy(alpha = 0.65f),
                        size = 14.dp
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 5. BOTTOM CALL CONTROLS (Mute, Camera, End Call, Speaker)
        // Reference image exact layout
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 36.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute Button
            CallControlButton(
                icon = if (call.isMuted) LucideIconKind.MIC_OFF else LucideIconKind.MIC,
                label = "Mute",
                isActive = call.isMuted,
                activeColor = Color.White,
                activeBackground = Color(0xFFE53935),
                inactiveBackground = Color.White.copy(alpha = 0.18f),
                onClick = onMuteToggle,
                testTag = "call_mute_toggle_button"
            )

            // Camera Toggle Button
            CallControlButton(
                icon = if (call.isCameraEnabled) LucideIconKind.CLIP_VIDEO else LucideIconKind.VIDEO_OFF,
                label = "Camera",
                isActive = !call.isCameraEnabled,
                activeColor = Color.White,
                activeBackground = Color.White.copy(alpha = 0.35f),
                inactiveBackground = Color.White.copy(alpha = 0.18f),
                onClick = onCameraToggle,
                testTag = "call_camera_toggle_button"
            )

            // End Call Button (Large Red circle)
            CallControlButton(
                icon = LucideIconKind.PHONE_OFF,
                label = "End Call",
                size = 72.dp,
                iconSize = 28.dp,
                isActive = true,
                activeColor = Color.White,
                activeBackground = Color(0xFFE53935),
                inactiveBackground = Color(0xFFE53935),
                onClick = onEndCall,
                testTag = "call_end_button"
            )

            // Speaker Button
            CallControlButton(
                icon = LucideIconKind.VOLUME_SPEAKER,
                label = "Speaker",
                isActive = call.isSpeakerOn,
                activeColor = Color.White,
                activeBackground = Color(0xFF1E88E5),
                inactiveBackground = Color.White.copy(alpha = 0.18f),
                onClick = onSpeakerToggle,
                testTag = "call_speaker_toggle_button"
            )
        }
    }

    // -------------------------------------------------------------
    // 6. VIDEO FILTERS DRAWER / BOTTOM SHEET
    // "filters icon is for fillers changing add more filters please"
    // -------------------------------------------------------------
    if (isFilterSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isFilterSheetOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFF18181D)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .testTag("video_filters_sheet")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Real-Time Video Filters",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "${VideoCallFilter.entries.size} Filters",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(VideoCallFilter.entries.toTypedArray()) { filter ->
                        val isSelected = (call.activeFilter == filter)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(78.dp)
                                .clickable {
                                    onFilterSelect(filter)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .then(
                                        if (filter == VideoCallFilter.NATURAL) {
                                            Modifier.background(Color(0xFF2A2A32))
                                        } else {
                                            Modifier.background(
                                                Brush.linearGradient(
                                                    listOf(filter.primaryColor, filter.secondaryColor)
                                                )
                                            )
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFE53935) else Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                TelefamLucideIcon(
                                    kind = if (filter == VideoCallFilter.NATURAL) LucideIconKind.SLIDERS_FILTER else LucideIconKind.MAGIC_WAND,
                                    tint = if (filter == VideoCallFilter.NATURAL) Color.White else Color.Black.copy(alpha = 0.8f),
                                    size = 24.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = filter.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // -------------------------------------------------------------
    // 7. WEBRTC & ADAPTIVE NETWORK DIAGNOSTICS SHEET
    // -------------------------------------------------------------
    if (isDiagnosticsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isDiagnosticsSheetOpen = false },
            containerColor = Color(0xFF18181D)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .testTag("webrtc_diagnostics_sheet")
            ) {
                Text(
                    text = "WebRTC Network Adaptation",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Real-time P2P WebRTC streaming telemetry automatically adapting to bandwidth",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 12.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                DiagnosticRow(label = "Connection Health", value = call.networkQuality.label, valueColor = Color(call.networkQuality.colorHex))
                DiagnosticRow(label = "P2P Architecture", value = call.iceCandidateType)
                DiagnosticRow(label = "Latency (RTT)", value = "${call.latencyMs} ms")
                DiagnosticRow(label = "Adaptive Bitrate", value = "${call.bitrateKbps} kbps")
                DiagnosticRow(label = "Packet Loss", value = "${call.packetLossPercent}%")
                DiagnosticRow(label = "Resolution & FPS", value = "${call.resolution} @ ${call.fps}fps")
                DiagnosticRow(label = "Encryption", value = "DTLS-SRTP 256-bit AES (P2P E2EE)", valueColor = Color(0xFF4ADE80))

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun DiagnosticRow(
    label: String,
    value: String,
    valueColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.5.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = valueColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp
            )
        )
    }
}

@Composable
private fun CallControlButton(
    icon: LucideIconKind,
    label: String,
    size: androidx.compose.ui.unit.Dp = 58.dp,
    iconSize: androidx.compose.ui.unit.Dp = 24.dp,
    isActive: Boolean = false,
    activeColor: Color = Color.White,
    activeBackground: Color = Color.White.copy(alpha = 0.35f),
    inactiveBackground: Color = Color.White.copy(alpha = 0.18f),
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(if (isActive) activeBackground else inactiveBackground)
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            TelefamLucideIcon(
                kind = icon,
                tint = activeColor,
                size = iconSize
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun RemoteAvatarFallbackView(
    call: ActiveCallSession,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatarPulse"
    )

    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E1528),
                        Color(0xFF15101E),
                        Color(0xFF0C0912)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Concentric ambient halo ring
            Box(
                modifier = Modifier
                    .size((140 * pulseScale).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2234)),
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
                            size = 56.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = call.peerFullName.ifBlank { "User" },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (call.status == CallStatus.CONNECTED) {
                    if (call.callType == CallType.AUDIO) "Audio Call Connected" else "Camera is turned off"
                } else {
                    "Establishing P2P WebRTC..."
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
            )
        }
    }
}
