package com.telefam.company.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.telefam.company.app.data.DeviceLinkRequestEntity
import com.telefam.company.app.data.LinkedDeviceEntity
import com.telefam.company.app.domain.DeviceType
import com.telefam.company.app.domain.QrMatrixGenerator
import com.telefam.company.app.ui.TelefamUiState
import com.telefam.company.app.ui.components.LinkedDevicesLucideIcons
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Linked Devices Management Screen:
 * - Active devices list with real hardware names and last active timestamps
 * - Dynamic QR Code & 8-character Pairing Code with 30-second live expiration timer and regeneration
 * - Scan / Enter Code verification tab with clipboard paste support
 * - 5-minute Approval Modal with live countdown timer and automatic revocation
 * - Per-device and all-devices logout buttons
 * - Immediate session revocation handling
 * - Zero demo/seeded data, pure SVG Lucide vector icons
 * - Responsive for phones, tablets, and desktop
 */
@Composable
fun LinkedDevicesScreen(
    uiState: TelefamUiState,
    onBackClick: () -> Unit,
    onSelectTab: (Int) -> Unit,
    onGeneratePairingSession: () -> Unit,
    onLinkCodeChanged: (String) -> Unit,
    onSubmitLinkCode: (String) -> Unit,
    onApprovePendingRequest: (String) -> Unit,
    onRejectPendingRequest: (String) -> Unit,
    onRevokeDevice: (String) -> Unit,
    onRevokeAllOtherDevices: () -> Unit,
    onClearErrorBanner: () -> Unit,
    onClearSuccessBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)

    val isDark = isTelefamDarkTheme()
    val clipboardManager = LocalClipboardManager.current

    var deviceToRevoke by remember { mutableStateOf<LinkedDeviceEntity?>(null) }
    var showRevokeAllDialog by remember { mutableStateOf(false) }
    var codeCopiedToast by remember { mutableStateOf(false) }

    LaunchedEffect(codeCopiedToast) {
        if (codeCopiedToast) {
            kotlinx.coroutines.delay(2000L)
            codeCopiedToast = false
        }
    }

    // When tab switches to Link a Device, ensure active pairing session is generated if null or expired
    LaunchedEffect(uiState.linkedDevicesSelectedTab) {
        if (uiState.linkedDevicesSelectedTab == 1 &&
            (uiState.activePairingRequest == null || uiState.pairingCountdownSeconds <= 0)
        ) {
            onGeneratePairingSession()
        }
    }

    val pageBg = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF7F8FA)
    val cardBg = if (isDark) Color(0xFF1E2633) else Color.White
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)
    val textColor = if (isDark) Color.White else Color(0xFF191D24)
    val subTextColor = if (isDark) Color(0xFF9EAAB9) else Color(0xFF6B7280)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(pageBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("linked_devices_screen_root")
    ) {
        val maxWidthDp = maxWidth
        val contentMaxWidth = if (maxWidthDp >= 900.dp) 820.dp else if (maxWidthDp >= 600.dp) 680.dp else maxWidthDp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = contentMaxWidth)
                .align(Alignment.TopCenter)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("linked_devices_back_btn")
                ) {
                    Icon(
                        imageVector = LinkedDevicesLucideIcons.LogOut,
                        contentDescription = stringResource(R.string.marketplace_back),
                        tint = textColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.linked_devices_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = textColor,
                        maxLines = 1
                    )
                    Text(
                        text = stringResource(R.string.privacy_title),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TelefamRed,
                        maxLines = 1
                    )
                }

                // Security Shield Badge
                Surface(
                    color = TelefamRed.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.ShieldCheck,
                            contentDescription = null,
                            tint = TelefamRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "E2EE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            ),
                            color = TelefamRed
                        )
                    }
                }
            }

            // Error Banner
            AnimatedVisibility(visible = uiState.linkDeviceErrorBanner != null) {
                val err = uiState.linkDeviceErrorBanner.orEmpty()
                Surface(
                    color = TelefamRed.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TelefamRed.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.ShieldAlert,
                            contentDescription = null,
                            tint = TelefamRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onClearErrorBanner, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = LinkedDevicesLucideIcons.Check,
                                contentDescription = null,
                                tint = subTextColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Success Banner
            AnimatedVisibility(visible = uiState.linkDeviceSuccessBanner != null) {
                val msg = uiState.linkDeviceSuccessBanner.orEmpty()
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.14f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.ShieldCheck,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onClearSuccessBanner, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = LinkedDevicesLucideIcons.Check,
                                contentDescription = null,
                                tint = subTextColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Tabs Header
            ScrollableTabRow(
                selectedTabIndex = uiState.linkedDevicesSelectedTab,
                containerColor = pageBg,
                contentColor = TelefamRed,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (uiState.linkedDevicesSelectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.linkedDevicesSelectedTab]),
                            color = TelefamRed,
                            height = 3.dp
                        )
                    }
                },
                divider = {
                    HorizontalDivider(color = cardBorder)
                }
            ) {
                Tab(
                    selected = uiState.linkedDevicesSelectedTab == 0,
                    onClick = { onSelectTab(0) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = LinkedDevicesLucideIcons.Laptop,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.linked_devices_active_devices),
                                fontWeight = if (uiState.linkedDevicesSelectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    selectedContentColor = TelefamRed,
                    unselectedContentColor = subTextColor,
                    modifier = Modifier.testTag("linked_devices_tab_active")
                )

                Tab(
                    selected = uiState.linkedDevicesSelectedTab == 1,
                    onClick = { onSelectTab(1) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = LinkedDevicesLucideIcons.QrCode,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.linked_devices_link_a_device),
                                fontWeight = if (uiState.linkedDevicesSelectedTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    selectedContentColor = TelefamRed,
                    unselectedContentColor = subTextColor,
                    modifier = Modifier.testTag("linked_devices_tab_link")
                )

                Tab(
                    selected = uiState.linkedDevicesSelectedTab == 2,
                    onClick = { onSelectTab(2) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = LinkedDevicesLucideIcons.Scan,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.linked_devices_scan_tab),
                                fontWeight = if (uiState.linkedDevicesSelectedTab == 2) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    selectedContentColor = TelefamRed,
                    unselectedContentColor = subTextColor,
                    modifier = Modifier.testTag("linked_devices_tab_scan")
                )
            }

            // Tab Content
            when (uiState.linkedDevicesSelectedTab) {
                0 -> ActiveDevicesTabContent(
                    linkedDevices = uiState.linkedDevices,
                    currentSessionDeviceId = uiState.currentSessionDeviceId,
                    isDark = isDark,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    textColor = textColor,
                    subTextColor = subTextColor,
                    onLinkNewDeviceClick = { onSelectTab(1) },
                    onRevokeDeviceClick = { deviceToRevoke = it },
                    onRevokeAllClick = { showRevokeAllDialog = true }
                )

                1 -> LinkNewDeviceQrTabContent(
                    activeRequest = uiState.activePairingRequest,
                    countdownSeconds = uiState.pairingCountdownSeconds,
                    isDark = isDark,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    textColor = textColor,
                    subTextColor = subTextColor,
                    onRegenerate = onGeneratePairingSession,
                    onCopyCode = { code ->
                        clipboardManager.setText(AnnotatedString(code))
                        codeCopiedToast = true
                    },
                    codeCopied = codeCopiedToast
                )

                2 -> ScanOrEnterCodeTabContent(
                    codeText = uiState.linkCodeInputText,
                    isLoading = uiState.isLinkingDeviceLoading,
                    isDark = isDark,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    textColor = textColor,
                    subTextColor = subTextColor,
                    onCodeChanged = onLinkCodeChanged,
                    onPasteFromClipboard = {
                        val clip = clipboardManager.getText()?.text.orEmpty().trim()
                        if (clip.isNotBlank()) {
                            onLinkCodeChanged(clip)
                        }
                    },
                    onSubmit = { onSubmitLinkCode(uiState.linkCodeInputText) }
                )
            }
        }

        // 5-Minute Link Approval Modal (when another device requested link to this account)
        val pendingApproval = uiState.pendingIncomingApprovalRequest
        if (pendingApproval != null) {
            DeviceLinkApprovalModal(
                request = pendingApproval,
                approvalCountdownSeconds = uiState.approvalCountdownSeconds,
                isDark = isDark,
                cardBg = cardBg,
                textColor = textColor,
                subTextColor = subTextColor,
                onApprove = { onApprovePendingRequest(pendingApproval.pairingSessionId) },
                onDeny = { onRejectPendingRequest(pendingApproval.pairingSessionId) }
            )
        }

        // Dialog: Single Device Logout Confirmation
        val targetDevice = deviceToRevoke
        if (targetDevice != null) {
            AlertDialog(
                onDismissRequest = { deviceToRevoke = null },
                title = {
                    Text(
                        text = stringResource(R.string.linked_devices_confirm_logout_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            R.string.linked_devices_confirm_logout_desc,
                            targetDevice.deviceName
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val id = targetDevice.sessionDeviceId
                            deviceToRevoke = null
                            onRevokeDevice(id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        modifier = Modifier.testTag("confirm_revoke_device_btn")
                    ) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.LogOut,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(R.string.linked_devices_logout_device))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deviceToRevoke = null }) {
                        Text(text = stringResource(R.string.crop_cancel_button))
                    }
                }
            )
        }

        // Dialog: Revoke All Other Devices Confirmation
        if (showRevokeAllDialog) {
            AlertDialog(
                onDismissRequest = { showRevokeAllDialog = false },
                title = {
                    Text(
                        text = stringResource(R.string.linked_devices_logout_all),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(text = stringResource(R.string.linked_devices_confirm_logout_all_desc))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRevokeAllDialog = false
                            onRevokeAllOtherDevices()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        modifier = Modifier.testTag("confirm_revoke_all_btn")
                    ) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.LogOut,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(R.string.linked_devices_logout_all))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRevokeAllDialog = false }) {
                        Text(text = stringResource(R.string.crop_cancel_button))
                    }
                }
            )
        }
    }
}

/**
 * Tab 0: Active Linked Devices List
 */
@Composable
private fun ActiveDevicesTabContent(
    linkedDevices: List<LinkedDeviceEntity>,
    currentSessionDeviceId: String,
    isDark: Boolean,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
    subTextColor: Color,
    onLinkNewDeviceClick: () -> Unit,
    onRevokeDeviceClick: (LinkedDeviceEntity) -> Unit,
    onRevokeAllClick: () -> Unit
) {
    val otherDevices = linkedDevices.filter { it.sessionDeviceId != currentSessionDeviceId }
    val thisDevice = linkedDevices.find { it.sessionDeviceId == currentSessionDeviceId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Explanatory Top Banner
        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(TelefamRed.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.Laptop,
                            contentDescription = null,
                            tint = TelefamRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.linked_devices_subtitle),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            ),
                            color = subTextColor
                        )
                    }
                }
            }
        }

        // Action: Link a Device Primary Button
        item {
            Button(
                onClick = onLinkNewDeviceClick,
                colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("linked_devices_link_action_btn")
            ) {
                Icon(
                    imageVector = LinkedDevicesLucideIcons.QrCode,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.linked_devices_link_a_device),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = Color.White
                )
            }
        }

        // Section: Current Device
        if (thisDevice != null) {
            item {
                Text(
                    text = stringResource(R.string.linked_devices_current_device),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = TelefamRed,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                DeviceItemCard(
                    device = thisDevice,
                    isCurrent = true,
                    isDark = isDark,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    textColor = textColor,
                    subTextColor = subTextColor,
                    onRevokeClick = {}
                )
            }
        }

        // Section: Other Linked Devices
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.linked_devices_active_devices),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = subTextColor
                )

                if (otherDevices.isNotEmpty()) {
                    TextButton(
                        onClick = onRevokeAllClick,
                        modifier = Modifier.testTag("logout_all_devices_btn")
                    ) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.LogOut,
                            contentDescription = null,
                            tint = TelefamRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.linked_devices_logout_all),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TelefamRed
                        )
                    }
                }
            }
        }

        if (otherDevices.isEmpty()) {
            item {
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.linked_devices_no_other_devices),
                            style = MaterialTheme.typography.bodyMedium,
                            color = subTextColor
                        )
                    }
                }
            }
        } else {
            items(otherDevices, key = { it.sessionDeviceId }) { dev ->
                DeviceItemCard(
                    device = dev,
                    isCurrent = false,
                    isDark = isDark,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    textColor = textColor,
                    subTextColor = subTextColor,
                    onRevokeClick = { onRevokeDeviceClick(dev) }
                )
            }
        }
    }
}

/**
 * Single Device Card in list
 */
@Composable
private fun DeviceItemCard(
    device: LinkedDeviceEntity,
    isCurrent: Boolean,
    isDark: Boolean,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
    subTextColor: Color,
    onRevokeClick: () -> Unit
) {
    val icon = resolveDeviceIcon(device.deviceType)
    val timeFormatter = remember { SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault()) }
    val lastActiveFormatted = if (isCurrent) {
        stringResource(R.string.linked_devices_active_now)
    } else {
        stringResource(R.string.linked_devices_last_active, timeFormatter.format(Date(device.lastActiveAtMs)))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (isCurrent) TelefamRed.copy(alpha = 0.35f) else cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("linked_device_card_${device.sessionDeviceId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCurrent) TelefamRed.copy(alpha = 0.12f) else if (isDark) Color(0xFF283446) else Color(0xFFEBF0F7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isCurrent) TelefamRed else textColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = device.deviceName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isCurrent) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = TelefamRed,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.linked_devices_current_device),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = LinkedDevicesLucideIcons.Clock,
                        contentDescription = null,
                        tint = if (isCurrent) Color(0xFF10B981) else subTextColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = lastActiveFormatted,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = if (isCurrent) Color(0xFF10B981) else subTextColor
                    )
                }

                if (device.locationLabel.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.MapPin,
                            contentDescription = null,
                            tint = subTextColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = device.locationLabel,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = subTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (!isCurrent) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onRevokeClick,
                    modifier = Modifier.testTag("logout_device_btn_${device.sessionDeviceId}")
                ) {
                    Icon(
                        imageVector = LinkedDevicesLucideIcons.LogOut,
                        contentDescription = stringResource(R.string.linked_devices_logout_device),
                        tint = TelefamRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Tab 1: QR Code & 8-Character Pairing Code (Expires in 30 seconds!)
 */
@Composable
private fun LinkNewDeviceQrTabContent(
    activeRequest: DeviceLinkRequestEntity?,
    countdownSeconds: Int,
    isDark: Boolean,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
    subTextColor: Color,
    onRegenerate: () -> Unit,
    onCopyCode: (String) -> Unit,
    codeCopied: Boolean
) {
    val isExpired = countdownSeconds <= 0 || activeRequest == null
    val payload = activeRequest?.qrPayload.orEmpty()

    val qrMatrix = remember(payload) {
        if (payload.isNotBlank()) {
            QrMatrixGenerator.generateMatrix(payload)
        } else {
            Array(QrMatrixGenerator.QR_SIZE) { BooleanArray(QrMatrixGenerator.QR_SIZE) { false } }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.linked_devices_qr_instructions),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = subTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Live 30s Countdown Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.Clock,
                            contentDescription = null,
                            tint = if (isExpired) TelefamRed else Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isExpired) {
                                stringResource(R.string.linked_devices_token_expired)
                            } else {
                                stringResource(R.string.linked_devices_token_expires_in, countdownSeconds)
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = if (isExpired) TelefamRed else textColor
                        )
                    }

                    if (isExpired) {
                        TextButton(
                            onClick = onRegenerate,
                            modifier = Modifier.testTag("regenerate_qr_top_btn")
                        ) {
                            Icon(
                                imageVector = LinkedDevicesLucideIcons.RefreshCw,
                                contentDescription = null,
                                tint = TelefamRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.linked_devices_regenerate),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TelefamRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                val progress = (countdownSeconds / 30f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (countdownSeconds <= 10) TelefamRed else Color(0xFF10B981),
                    trackColor = if (isDark) Color(0xFF283446) else Color(0xFFE5E7EB)
                )
            }
        }

        // Dynamic Canvas QR Matrix Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, if (isExpired) TelefamRed.copy(alpha = 0.5f) else TelefamRed.copy(alpha = 0.25f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .size(260.dp)
                    .testTag("linked_devices_qr_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isExpired) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = LinkedDevicesLucideIcons.ShieldAlert,
                                contentDescription = null,
                                tint = TelefamRed,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.linked_devices_token_expired),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = TelefamRed,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onRegenerate,
                                colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("regenerate_qr_center_btn")
                            ) {
                                Icon(
                                    imageVector = LinkedDevicesLucideIcons.RefreshCw,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.linked_devices_regenerate),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    } else {
                        // Crisp Canvas Rendering of 25x25 QR Matrix
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val qrCols = QrMatrixGenerator.QR_SIZE
                            val cellSize = size.width / qrCols
                            val darkColor = Color(0xFF1E232A)

                            for (r in 0 until qrCols) {
                                for (c in 0 until qrCols) {
                                    if (qrMatrix[r][c]) {
                                        drawRect(
                                            color = darkColor,
                                            topLeft = Offset(c * cellSize, r * cellSize),
                                            size = Size(cellSize, cellSize)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Alternative: One-Time Pairing Code
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.linked_devices_code_instructions),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = subTextColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                val pairingCode = activeRequest?.linkToken ?: "TLF-••••"
                Surface(
                    color = cardBg,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = LinkedDevicesLucideIcons.KeyRound,
                                contentDescription = null,
                                tint = TelefamRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (isExpired) "EXPIRED" else pairingCode,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    letterSpacing = 2.sp
                                ),
                                color = if (isExpired) TelefamRed else textColor
                            )
                        }

                        if (!isExpired) {
                            OutlinedButton(
                                onClick = { onCopyCode(pairingCode) },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, TelefamRed.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("copy_pairing_code_btn")
                            ) {
                                Icon(
                                    imageVector = if (codeCopied) LinkedDevicesLucideIcons.Check else LinkedDevicesLucideIcons.Copy,
                                    contentDescription = null,
                                    tint = if (codeCopied) Color(0xFF10B981) else TelefamRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (codeCopied) {
                                        stringResource(R.string.linked_devices_code_copied)
                                    } else {
                                        stringResource(R.string.linked_devices_copy_code)
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (codeCopied) Color(0xFF10B981) else TelefamRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: Scan / Enter Code Tab
 */
@Composable
private fun ScanOrEnterCodeTabContent(
    codeText: String,
    isLoading: Boolean,
    isDark: Boolean,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
    subTextColor: Color,
    onCodeChanged: (String) -> Unit,
    onPasteFromClipboard: () -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = LinkedDevicesLucideIcons.Scan,
                            contentDescription = null,
                            tint = TelefamRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.linked_devices_scan_tab),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.linked_devices_code_instructions),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = subTextColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = codeText,
                        onValueChange = onCodeChanged,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.linked_devices_enter_code_placeholder),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                            )
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = onPasteFromClipboard,
                                modifier = Modifier.testTag("paste_code_btn")
                            ) {
                                Icon(
                                    imageVector = LinkedDevicesLucideIcons.Copy,
                                    contentDescription = null,
                                    tint = TelefamRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.linked_devices_paste_code),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TelefamRed
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TelefamRed,
                            unfocusedBorderColor = cardBorder,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("link_code_input_field")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onSubmit,
                        enabled = codeText.trim().length >= 4 && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TelefamRed,
                            disabledContainerColor = TelefamRed.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_link_code_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = LinkedDevicesLucideIcons.ShieldCheck,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.linked_devices_submit_code),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5-Minute Link Approval Modal / Bottom Sheet
 * Shows on authenticated primary device when a link request arrives.
 * Has strict 5-minute timer countdown, device identity, location, IP, and Approve/Deny buttons.
 */
@Composable
private fun DeviceLinkApprovalModal(
    request: DeviceLinkRequestEntity,
    approvalCountdownSeconds: Int,
    isDark: Boolean,
    cardBg: Color,
    textColor: Color,
    subTextColor: Color,
    onApprove: () -> Unit,
    onDeny: () -> Unit
) {
    val minutes = approvalCountdownSeconds / 60
    val seconds = approvalCountdownSeconds % 60
    val timerFormatted = String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
    val isExpired = approvalCountdownSeconds <= 0

    AlertDialog(
        onDismissRequest = { /* Modal requires explicit decision */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("device_link_approval_modal"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = LinkedDevicesLucideIcons.ShieldAlert,
                    contentDescription = null,
                    tint = TelefamRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.linked_devices_approval_required_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.linked_devices_approval_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )

                // Requester Device Details Box
                Surface(
                    color = if (isDark) Color(0xFF283446) else Color(0xFFF1F4F9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = resolveDeviceIcon(request.requesterDeviceType),
                                contentDescription = null,
                                tint = TelefamRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = request.requesterDeviceName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = textColor
                            )
                        }

                        if (request.requesterLocation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = LinkedDevicesLucideIcons.MapPin,
                                    contentDescription = null,
                                    tint = subTextColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = request.requesterLocation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = subTextColor
                                )
                            }
                        }

                        if (request.requesterIp.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = LinkedDevicesLucideIcons.Globe,
                                    contentDescription = null,
                                    tint = subTextColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "IP: ${request.requesterIp}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = subTextColor
                                )
                            }
                        }
                    }
                }

                // Live 5-minute countdown display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = LinkedDevicesLucideIcons.Clock,
                        contentDescription = null,
                        tint = if (isExpired) TelefamRed else Color(0xFFEAB308),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isExpired) {
                            stringResource(R.string.linked_devices_approval_expired)
                        } else {
                            stringResource(R.string.linked_devices_approval_timer, timerFormatted)
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isExpired) TelefamRed else textColor
                        )
                    )
                }

                val progress = (approvalCountdownSeconds / 300f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (approvalCountdownSeconds <= 60) TelefamRed else Color(0xFFEAB308),
                    trackColor = if (isDark) Color(0xFF1E2633) else Color(0xFFE5E7EB)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onApprove,
                enabled = !isExpired,
                colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                modifier = Modifier.testTag("approve_device_btn")
            ) {
                Icon(
                    imageVector = LinkedDevicesLucideIcons.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.linked_devices_approve_button))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDeny,
                border = BorderStroke(1.dp, TelefamRed),
                modifier = Modifier.testTag("deny_device_btn")
            ) {
                Icon(
                    imageVector = LinkedDevicesLucideIcons.LogOut,
                    contentDescription = null,
                    tint = TelefamRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.linked_devices_deny_button),
                    color = TelefamRed
                )
            }
        }
    )
}

/**
 * Resolves appropriate Lucide vector icon for device type
 */
private fun resolveDeviceIcon(typeStr: String): ImageVector {
    return when (typeStr.uppercase(Locale.ROOT)) {
        DeviceType.PHONE.name, "PHONE" -> LinkedDevicesLucideIcons.Smartphone
        DeviceType.TABLET.name, "TABLET" -> LinkedDevicesLucideIcons.Tablet
        DeviceType.DESKTOP.name, "DESKTOP" -> LinkedDevicesLucideIcons.Monitor
        DeviceType.WEB.name, "WEB" -> LinkedDevicesLucideIcons.Globe
        else -> LinkedDevicesLucideIcons.Laptop
    }
}
