package com.telefam.company.app.domain

import androidx.compose.ui.graphics.Color

enum class CallType {
    AUDIO,
    VIDEO
}

enum class CallDirection {
    INCOMING,
    OUTGOING,
    MISSED,
    DECLINED
}

enum class CallStatus {
    RINGING,
    CONNECTING,
    CONNECTED,
    ENDED,
    RECONNECTING
}

enum class NetworkQuality(val label: String, val colorHex: Long) {
    EXCELLENT("Excellent Connection", 0xFF22C55E),
    GOOD("Good Connection", 0xFF4ADE80),
    FAIR("Fair Connection", 0xFFFACC15),
    POOR("Poor Connection", 0xFFF97316),
    RECONNECTING("Reconnecting...", 0xFFEF4444)
}

enum class CameraFacing {
    FRONT,
    BACK
}

enum class VideoCallFilter(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val overlayAlpha: Float
) {
    NATURAL("Natural", "Clean true-to-life studio balance", Color.Transparent, Color.Transparent, 0.0f),
    WARM_SUNSET("Warm Sunset", "Golden hour evening warmth", Color(0xFFFF9E80), Color(0xFFFF6E40), 0.16f),
    STUDIO_GLOW("Studio Glow", "Soft diffused portrait illumination", Color(0xFFFFE082), Color(0xFFFFB74D), 0.14f),
    CYBERPUNK_NEON("Cyberpunk", "Futuristic blue & magenta contrast", Color(0xFF00E5FF), Color(0xFFFF007F), 0.22f),
    VINTAGE_FILM("Vintage Film", "Classic cinema 35mm tones", Color(0xFFD7CCC8), Color(0xFF8D6E63), 0.18f),
    VIVID_BOOST("Vivid HDR", "High-contrast dynamic colors", Color(0xFF40C4FF), Color(0xFF00B0FF), 0.15f),
    DRAMATIC_BW("Dramatic B&W", "Timeless monochrome contrast", Color(0xFF424242), Color(0xFF000000), 0.28f),
    SOFT_BEAUTY("Soft Beauty", "Flawless radiant skin smoothing", Color(0xFFF8BBD0), Color(0xFFF48FB1), 0.15f),
    SEPIA_RETRO("Sepia Retro", "Nostalgic golden-brown tones", Color(0xFFBCAAA4), Color(0xFF6D4C41), 0.20f),
    COLD_CRISP("Cold Crisp", "Clean arctic blue clarity", Color(0xFF80D8FF), Color(0xFF0091EA), 0.16f)
}

data class ActiveCallSession(
    val callId: String,
    val peerUserId: String,
    val peerFullName: String,
    val peerUsername: String,
    val peerAvatarUrl: String? = null,
    val callType: CallType = CallType.VIDEO,
    val status: CallStatus = CallStatus.RINGING,
    val isOutgoing: Boolean = true,
    val startedAtMs: Long = System.currentTimeMillis(),
    val connectedAtMs: Long? = null,
    val durationSeconds: Long = 0L,
    val isMuted: Boolean = false,
    val isCameraEnabled: Boolean = (callType == CallType.VIDEO),
    val isRemoteCameraEnabled: Boolean = (callType == CallType.VIDEO),
    val isSpeakerOn: Boolean = (callType == CallType.VIDEO),
    val cameraFacing: CameraFacing = CameraFacing.FRONT,
    val activeFilter: VideoCallFilter = VideoCallFilter.NATURAL,
    val networkQuality: NetworkQuality = NetworkQuality.GOOD,
    val latencyMs: Int = 32,
    val bitrateKbps: Int = 1850,
    val packetLossPercent: Float = 0.1f,
    val fps: Int = 30,
    val resolution: String = "720p (Adaptive HD)",
    val iceCandidateType: String = "STUN srflx (P2P Direct)",
    val isMinimized: Boolean = false,
    val isE2eeVerified: Boolean = true
) {
    val isTurnRelayed: Boolean get() = iceCandidateType.contains("relay", ignoreCase = true)

    val formattedDuration: String
        get() {
            val mins = durationSeconds / 60
            val secs = durationSeconds % 60
            val hrs = mins / 60
            return if (hrs > 0) {
                String.format(java.util.Locale.US, "%02d:%02d:%02d", hrs, mins % 60, secs)
            } else {
                String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
            }
        }
}

data class WebRtcSignalMessage(
    val callId: String,
    val senderUserId: String,
    val targetUserId: String,
    val type: String, // "INVITE", "ACCEPT", "CANDIDATE", "HANGUP", "REJECT", "MEDIA_TOGGLE"
    val callType: CallType = CallType.VIDEO,
    val sdp: String? = null,
    val iceCandidate: String? = null,
    val isCameraEnabled: Boolean = true,
    val isMuted: Boolean = false,
    val timestampMs: Long = System.currentTimeMillis()
)
