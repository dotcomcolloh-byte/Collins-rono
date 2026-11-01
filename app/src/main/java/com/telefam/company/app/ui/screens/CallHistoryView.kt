package com.telefam.company.app.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.telefam.company.app.data.CallRecordEntity
import com.telefam.company.app.domain.CallType
import com.telefam.company.app.ui.components.LucideIconKind
import com.telefam.company.app.ui.components.TelefamLucideIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallHistoryView(
    calls: List<CallRecordEntity>,
    onInitiateCall: (peerUserId: String, peerFullName: String, peerUsername: String, peerAvatarUrl: String?, callType: CallType) -> Unit,
    onClearHistory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL" or "MISSED"

    val filteredCalls = remember(calls, selectedFilter) {
        if (selectedFilter == "MISSED") {
            calls.filter { it.direction == "MISSED" || it.direction == "DECLINED" }
        } else {
            calls
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("call_history_view")
    ) {
        // Filter Pills Row (All / Missed)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedFilter == "ALL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { selectedFilter = "ALL" }
                ) {
                    Text(
                        text = "All Calls (${calls.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selectedFilter == "ALL") FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedFilter == "ALL") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                val missedCount = calls.count { it.direction == "MISSED" || it.direction == "DECLINED" }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedFilter == "MISSED") Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { selectedFilter = "MISSED" }
                ) {
                    Text(
                        text = "Missed ($missedCount)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selectedFilter == "MISSED") FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedFilter == "MISSED") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            if (calls.isNotEmpty()) {
                Text(
                    text = "Clear",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .clickable { onClearHistory() }
                        .padding(4.dp)
                )
            }
        }

        if (filteredCalls.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.PHONE_CALL,
                            tint = MaterialTheme.colorScheme.primary,
                            size = 32.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (selectedFilter == "MISSED") "No missed calls" else "No call history yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Your recent P2P audio and video calls will appear here",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCalls, key = { it.callId }) { call ->
                    CallHistoryItemRow(
                        call = call,
                        onCallBack = {
                            val type = if (call.callType == "VIDEO") CallType.VIDEO else CallType.AUDIO
                            onInitiateCall(
                                call.peerUserId,
                                call.peerFullName,
                                call.peerUsername,
                                call.peerAvatarUrl,
                                type
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CallHistoryItemRow(
    call: CallRecordEntity,
    onCallBack: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("call_history_item_${call.callId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            size = 22.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = call.peerFullName.ifBlank { "User" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = if (call.direction == "MISSED" || call.direction == "DECLINED") {
                                Color(0xFFEF4444)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Direction Icon
                        val directionColor = when (call.direction) {
                            "INCOMING" -> Color(0xFF22C55E)
                            "OUTGOING" -> Color(0xFF3B82F6)
                            "MISSED" -> Color(0xFFEF4444)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        val directionIcon = when (call.direction) {
                            "INCOMING" -> LucideIconKind.CALL_INCOMING
                            "OUTGOING" -> LucideIconKind.CALL_OUTGOING
                            else -> LucideIconKind.CALL_MISSED
                        }

                        TelefamLucideIcon(
                            kind = directionIcon,
                            tint = directionColor,
                            size = 14.dp
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        val durationStr = if (call.durationSeconds > 0) {
                            val mins = call.durationSeconds / 60
                            val secs = call.durationSeconds % 60
                            "${mins}m ${secs}s"
                        } else {
                            when (call.direction) {
                                "MISSED" -> "Missed"
                                "DECLINED" -> "Declined"
                                else -> "Cancelled"
                            }
                        }

                        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                        val timeStr = timeFormat.format(Date(call.timestampMs))

                        Text(
                            text = "$durationStr • $timeStr",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // Quick Call Back Button
            IconButton(
                onClick = onCallBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .testTag("call_back_button_${call.callId}")
            ) {
                TelefamLucideIcon(
                    kind = if (call.callType == "VIDEO") LucideIconKind.VIDEO_CALL else LucideIconKind.PHONE_CALL,
                    tint = MaterialTheme.colorScheme.primary,
                    size = 18.dp
                )
            }
        }
    }
}
