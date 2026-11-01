package com.telefam.company.app.ui.screens

import com.example.R
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.TelefamRed
import com.telefam.company.app.domain.GroupAuditLogItem
import com.telefam.company.app.domain.GroupJoinRequestItem
import com.telefam.company.app.domain.GroupMemberItem
import com.telefam.company.app.domain.GroupPermissionsSnapshot
import com.telefam.company.app.domain.GroupProfileMediaTab
import com.telefam.company.app.domain.GroupProfileUiModel
import com.telefam.company.app.domain.GroupRole
import com.telefam.company.app.domain.GroupType
import com.telefam.company.app.domain.WhoCanOption
import com.telefam.company.app.ui.components.LucideIconKind
import com.telefam.company.app.ui.components.TelefamLucideIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupProfileScreen(
    group: GroupProfileUiModel,
    onBackClick: () -> Unit,
    onOpenChat: () -> Unit,
    onAddMembersClick: () -> Unit,
    onToggleMute: () -> Unit,
    onSearchChatClick: () -> Unit,
    onShareInviteLink: () -> Unit,
    onUpdatePermissions: (GroupPermissionsSnapshot) -> Unit,
    onPromoteMemberRole: (userId: String, newRole: GroupRole) -> Unit,
    onRemoveMember: (userId: String) -> Unit,
    onMuteMember: (userId: String, isMuted: Boolean) -> Unit,
    onApproveJoinRequest: (requestId: String, userId: String) -> Unit,
    onRejectJoinRequest: (requestId: String) -> Unit,
    onLeaveGroup: () -> Unit,
    onReportGroup: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedMediaTab by remember { mutableStateOf(GroupProfileMediaTab.MEDIA) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showTopMenu by remember { mutableStateOf(false) }

    // Dialogs / Sheets
    var isPermissionsSheetOpen by remember { mutableStateOf(false) }
    var isMembersSheetOpen by remember { mutableStateOf(false) }
    var isApprovalsSheetOpen by remember { mutableStateOf(false) }
    var isAuditLogsSheetOpen by remember { mutableStateOf(false) }
    var selectedMemberForAction by remember { mutableStateOf<GroupMemberItem?>(null) }

    val isPrivileged = group.myRole.isPrivileged
    val isAdminOrOwner = group.myRole.isAdminOrOwner

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Red Top Header Matching Image 2
        Surface(
            color = TelefamRed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(bottom = 16.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("group_profile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Edit pencil (only for Admins/Owner)
                    if (isAdminOrOwner) {
                        IconButton(
                            onClick = { isPermissionsSheetOpen = true },
                            modifier = Modifier.testTag("group_profile_edit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Group",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { showTopMenu = !showTopMenu },
                            modifier = Modifier.testTag("group_profile_more_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showTopMenu,
                            onDismissRequest = { showTopMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.group_quick_share)) },
                                onClick = {
                                    showTopMenu = false
                                    onShareInviteLink()
                                }
                            )
                            if (isPrivileged) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.group_permissions_row)) },
                                    onClick = {
                                        showTopMenu = false
                                        isPermissionsSheetOpen = true
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.group_report_button)) },
                                onClick = {
                                    showTopMenu = false
                                    onReportGroup()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.group_leave_button), color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showTopMenu = false
                                    onLeaveGroup()
                                }
                            )
                        }
                    }
                }

                // Avatar, Title, and Subtitle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(80.dp)) {
                        if (!group.avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = group.avatarUrl,
                                contentDescription = group.groupName,
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Group,
                                    contentDescription = null,
                                    tint = TelefamRed,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        // Camera badge if admin/owner
                        if (isAdminOrOwner) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(TelefamRed)
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Edit photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // Group Name (NO verification badge as requested by user)
                        Text(
                            text = group.groupName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Subtitle: Public Group / Private Group
                        Text(
                            text = if (group.groupType == GroupType.PUBLIC) {
                                stringResource(R.string.public_group_title)
                            } else {
                                stringResource(R.string.private_group_title)
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = Color.White.copy(alpha = 0.88f)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Member Count & Online Count
                        Text(
                            text = stringResource(
                                R.string.members_online_format,
                                group.memberCount,
                                group.onlineCount
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color.White.copy(alpha = 0.78f)
                        )
                    }
                }
            }
        }

        // Scrollable Body
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Quick Action Bar Matching Image 2
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Add Members action
                    QuickActionButton(
                        icon = Icons.Default.PersonAdd,
                        label = stringResource(R.string.group_quick_add_members),
                        onClick = onAddMembersClick
                    )

                    // Mute / Unmute action
                    QuickActionButton(
                        icon = if (group.isMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                        label = if (group.isMuted) stringResource(R.string.group_quick_unmute) else stringResource(R.string.group_quick_mute),
                        onClick = onToggleMute
                    )

                    // Search action
                    QuickActionButton(
                        icon = Icons.Default.Search,
                        label = stringResource(R.string.group_quick_search),
                        onClick = onSearchChatClick
                    )

                    // Share action
                    QuickActionButton(
                        icon = Icons.Default.Share,
                        label = stringResource(R.string.group_quick_share),
                        onClick = onShareInviteLink
                    )

                    // More action
                    QuickActionButton(
                        icon = Icons.Default.MoreHoriz,
                        label = stringResource(R.string.group_quick_more),
                        onClick = { showTopMenu = true }
                    )
                }
            }

            // Description Card Matching Image 2
            if (group.groupDescription.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.group_desc_label).substringBefore(" "),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = TelefamRed
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = group.groupDescription,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (group.groupDescription.length > 70) {
                            Text(
                                text = if (isDescriptionExpanded) "less" else "more",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TelefamRed,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Group Info List Card Matching Image 2
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Group Type
                    GroupInfoRow(
                        icon = Icons.Default.Language,
                        title = stringResource(R.string.group_type_row),
                        value = if (group.groupType == GroupType.PUBLIC) "Public" else "Private",
                        onClick = if (isAdminOrOwner) { { isPermissionsSheetOpen = true } } else null
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 0.8.dp,
                        modifier = Modifier.padding(start = 54.dp)
                    )

                    // Invite Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                clipboardManager.setText(AnnotatedString(group.inviteLink))
                                Toast.makeText(context, "Invite link copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = TelefamRed,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = stringResource(R.string.group_invite_link_row),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = group.inviteLink.ifBlank { "t.me/telefamcommunity" },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy link",
                            tint = TelefamRed,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 0.8.dp,
                        modifier = Modifier.padding(start = 54.dp)
                    )

                    // Notifications
                    GroupInfoRow(
                        icon = Icons.Default.Notifications,
                        title = stringResource(R.string.group_notifications_row),
                        value = if (group.isMuted) "Muted" else "Enabled",
                        onClick = onToggleMute
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 0.8.dp,
                        modifier = Modifier.padding(start = 54.dp)
                    )

                    // Chat History
                    GroupInfoRow(
                        icon = Icons.Default.History,
                        title = stringResource(R.string.group_chat_history_row),
                        value = if (group.isHistoryVisible) {
                            stringResource(R.string.group_history_visible)
                        } else {
                            stringResource(R.string.group_history_hidden)
                        },
                        onClick = if (isAdminOrOwner) { { isPermissionsSheetOpen = true } } else null
                    )
                }
            }

            // Shared Media Tabs Section Matching Image 2
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedMediaTab.ordinal,
                        edgePadding = 12.dp,
                        containerColor = Color.Transparent,
                        contentColor = TelefamRed,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedMediaTab.ordinal]),
                                color = TelefamRed,
                                height = 2.5.dp
                            )
                        },
                        divider = {}
                    ) {
                        GroupProfileMediaTab.entries.forEach { tab ->
                            val label = when (tab) {
                                GroupProfileMediaTab.MEDIA -> stringResource(R.string.group_media_tab)
                                GroupProfileMediaTab.FILES -> stringResource(R.string.group_files_tab)
                                GroupProfileMediaTab.LINKS -> stringResource(R.string.group_links_tab)
                                GroupProfileMediaTab.VOICE -> stringResource(R.string.group_voice_tab)
                                GroupProfileMediaTab.GIFS -> stringResource(R.string.group_gifs_tab)
                                GroupProfileMediaTab.POLLS -> stringResource(R.string.group_polls_tab)
                            }
                            Tab(
                                selected = selectedMediaTab == tab,
                                onClick = { selectedMediaTab = tab },
                                text = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (selectedMediaTab == tab) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (selectedMediaTab == tab) TelefamRed else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    // Tab Content Preview Matching Image 2 Grid
                    when (selectedMediaTab) {
                        GroupProfileMediaTab.MEDIA -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val sampleCards = listOf(
                                    Triple("Community Meetup", 32, Color(0xFF1E293B)),
                                    Triple("Nature Trip", 18, Color(0xFF0F766E)),
                                    Triple("Welcome to Community", 45, TelefamRed),
                                    Triple("Volunteers", 26, Color(0xFF7C2D12)),
                                    Triple("Campus Live", 12, Color(0xFF4338CA))
                                )

                                sampleCards.forEachIndexed { idx, item ->
                                    Box(
                                        modifier = Modifier
                                            .size(width = 86.dp, height = 98.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(item.third)
                                            .padding(6.dp)
                                    ) {
                                        Text(
                                            text = item.first,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color.White,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (idx == 4) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Video",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .align(Alignment.Center)
                                            )
                                        }

                                        // Bottom-Right Count Badge
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color.Black.copy(alpha = 0.65f),
                                            modifier = Modifier.align(Alignment.BottomEnd)
                                        ) {
                                            Text(
                                                text = "${item.second}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No shared items in this category yet",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Members Section Matching Image 2
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${group.memberCount} Members",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "See All",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TelefamRed,
                            modifier = Modifier.clickable { isMembersSheetOpen = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Member Preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        group.members.take(10).forEach { member ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(62.dp)
                                    .clickable { selectedMemberForAction = member }
                            ) {
                                Box(modifier = Modifier.size(52.dp)) {
                                    if (!member.avatarUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = member.avatarUrl,
                                            contentDescription = member.fullName,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .align(Alignment.Center)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .align(Alignment.Center)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE2E8F0)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = member.fullName.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Role or Online Badge
                                    if (member.isOnline) {
                                        Box(
                                            modifier = Modifier
                                                .size(11.dp)
                                                .align(Alignment.BottomEnd)
                                                .clip(CircleShape)
                                                .background(Color(0xFF16A34A))
                                                .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = member.fullName.substringBefore(" "),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = when {
                                        member.role != GroupRole.MEMBER -> member.role.wireName.lowercase().replaceFirstChar { it.uppercase() }
                                        member.isOnline -> "online"
                                        else -> "offline"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (member.isOnline) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Settings Section Matching Image 2 (ROLE-GATED)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.group_settings_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Admins / Owner / Moderator Administrative Controls
                        if (isPrivileged) {
                            // Permissions
                            GroupSettingRow(
                                icon = Icons.Default.Security,
                                title = stringResource(R.string.group_permissions_row),
                                trailingText = "${group.permissions.enabledCount}/8",
                                onClick = { isPermissionsSheetOpen = true }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(start = 54.dp)
                            )

                            // Admins
                            val adminCount = group.members.count { it.role.isAdminOrOwner }
                            GroupSettingRow(
                                icon = Icons.Default.Star,
                                title = stringResource(R.string.group_admins_row),
                                trailingText = "$adminCount",
                                onClick = { isMembersSheetOpen = true }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(start = 54.dp)
                            )

                            // Members
                            GroupSettingRow(
                                icon = Icons.Outlined.Group,
                                title = stringResource(R.string.group_members_row),
                                trailingText = "${group.memberCount}",
                                onClick = { isMembersSheetOpen = true }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(start = 54.dp)
                            )

                            // Pending Join Approvals (if join approval enabled)
                            if (group.permissions.requiresJoinApproval) {
                                GroupSettingRow(
                                    icon = Icons.Default.PersonAdd,
                                    title = stringResource(R.string.group_join_approvals_row),
                                    trailingText = "${group.pendingApprovals.size}",
                                    onClick = { isApprovalsSheetOpen = true }
                                )

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                    thickness = 0.8.dp,
                                    modifier = Modifier.padding(start = 54.dp)
                                )
                            }

                            // Recent Actions (Audit Logs)
                            GroupSettingRow(
                                icon = Icons.Default.History,
                                title = stringResource(R.string.group_recent_actions_row),
                                trailingText = "",
                                onClick = { isAuditLogsSheetOpen = true }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(start = 54.dp)
                            )

                            // Banned Users
                            GroupSettingRow(
                                icon = Icons.Outlined.Block,
                                title = stringResource(R.string.group_banned_users_row),
                                trailingText = "${group.bannedCount}",
                                onClick = { Toast.makeText(context, "${group.bannedCount} banned users", Toast.LENGTH_SHORT).show() }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(start = 54.dp)
                            )
                        } else {
                            // Non-admin / regular member rows
                            if (group.permissions.whoCanAddMembers == WhoCanOption.ALL_MEMBERS) {
                                GroupSettingRow(
                                    icon = Icons.Default.PersonAdd,
                                    title = stringResource(R.string.add_members_title),
                                    trailingText = "",
                                    onClick = onAddMembersClick
                                )

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                    thickness = 0.8.dp,
                                    modifier = Modifier.padding(start = 54.dp)
                                )
                            }

                            GroupSettingRow(
                                icon = Icons.Default.Notifications,
                                title = stringResource(R.string.group_notifications_row),
                                trailingText = if (group.isMuted) "Muted" else "Enabled",
                                onClick = onToggleMute
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(start = 54.dp)
                            )

                            GroupSettingRow(
                                icon = Icons.Default.Flag,
                                title = stringResource(R.string.group_report_button),
                                trailingText = "",
                                onClick = onReportGroup
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(start = 54.dp)
                            )
                        }

                        // Leave Group Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLeaveGroup() }
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEE2E2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null,
                                    tint = TelefamRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = stringResource(R.string.group_leave_button),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TelefamRed
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Sheet: Permissions Editor (for Admins/Owner)
    if (isPermissionsSheetOpen) {
        var currentPerms by remember { mutableStateOf(group.permissions) }

        ModalBottomSheet(
            onDismissRequest = { isPermissionsSheetOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.group_permissions_row),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Who can send messages
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.who_can_send_messages),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            currentPerms = currentPerms.copy(
                                whoCanSendMessages = if (currentPerms.whoCanSendMessages == WhoCanOption.ALL_MEMBERS) {
                                    WhoCanOption.ONLY_ADMINS
                                } else {
                                    WhoCanOption.ALL_MEMBERS
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = if (currentPerms.whoCanSendMessages == WhoCanOption.ALL_MEMBERS) {
                                stringResource(R.string.all_members_option)
                            } else {
                                stringResource(R.string.only_admins_option)
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Who can add members
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.who_can_add_members),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            currentPerms = currentPerms.copy(
                                whoCanAddMembers = if (currentPerms.whoCanAddMembers == WhoCanOption.ALL_MEMBERS) {
                                    WhoCanOption.ONLY_ADMINS
                                } else {
                                    WhoCanOption.ALL_MEMBERS
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = if (currentPerms.whoCanAddMembers == WhoCanOption.ALL_MEMBERS) {
                                stringResource(R.string.all_members_option)
                            } else {
                                stringResource(R.string.only_admins_option)
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Allow copying messages
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.group_copy_messages_setting),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = currentPerms.allowCopyMessages,
                        onCheckedChange = { currentPerms = currentPerms.copy(allowCopyMessages = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = TelefamRed)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Allow screenshots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.group_screenshot_setting),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = currentPerms.allowScreenshots,
                        onCheckedChange = { currentPerms = currentPerms.copy(allowScreenshots = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = TelefamRed)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Allow download media
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.group_download_media_setting),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = currentPerms.allowDownloadMedia,
                        onCheckedChange = { currentPerms = currentPerms.copy(allowDownloadMedia = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = TelefamRed)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Approve new members
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.group_join_approval_setting),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = currentPerms.requiresJoinApproval,
                        onCheckedChange = { currentPerms = currentPerms.copy(requiresJoinApproval = it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = TelefamRed)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onUpdatePermissions(currentPerms)
                        isPermissionsSheetOpen = false
                        Toast.makeText(context, "Group permissions updated", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Save Permissions", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Modal Sheet: Members Management List
    if (isMembersSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isMembersSheetOpen = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "${group.members.size} Members",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    items(group.members, key = { it.memberId }) { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMemberForAction = member }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!member.avatarUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = member.avatarUrl,
                                    contentDescription = member.fullName,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.fullName.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = member.fullName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "@${member.username.removePrefix("@")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Role Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when (member.role) {
                                    GroupRole.OWNER -> TelefamRed.copy(alpha = 0.12f)
                                    GroupRole.ADMIN -> Color(0xFF2563EB).copy(alpha = 0.12f)
                                    GroupRole.MODERATOR -> Color(0xFF16A34A).copy(alpha = 0.12f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = member.role.wireName.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = when (member.role) {
                                        GroupRole.OWNER -> TelefamRed
                                        GroupRole.ADMIN -> Color(0xFF2563EB)
                                        GroupRole.MODERATOR -> Color(0xFF16A34A)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                    }
                }
            }
        }
    }

    // Modal Sheet: Member Action Bottom Sheet (Promote, Demote, Mute, Remove)
    selectedMemberForAction?.let { member ->
        ModalBottomSheet(
            onDismissRequest = { selectedMemberForAction = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = member.fullName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Role: ${member.role.wireName} • @${member.username.removePrefix("@")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                if (isAdminOrOwner && member.role != GroupRole.OWNER) {
                    if (member.role != GroupRole.ADMIN) {
                        Button(
                            onClick = {
                                onPromoteMemberRole(member.userId, GroupRole.ADMIN)
                                selectedMemberForAction = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Promote to Admin")
                        }
                    }

                    if (member.role != GroupRole.MODERATOR) {
                        Button(
                            onClick = {
                                onPromoteMemberRole(member.userId, GroupRole.MODERATOR)
                                selectedMemberForAction = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Promote to Moderator")
                        }
                    }

                    Button(
                        onClick = {
                            onMuteMember(member.userId, !member.isMuted)
                            selectedMemberForAction = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (member.isMuted) "Unmute User" else "Mute User in Group")
                    }

                    Button(
                        onClick = {
                            onRemoveMember(member.userId)
                            selectedMemberForAction = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Remove from Group")
                    }
                } else {
                    Button(
                        onClick = { selectedMemberForAction = null },
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }

    // Modal Sheet: Pending Join Approvals (for Admins)
    if (isApprovalsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isApprovalsSheetOpen = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = stringResource(R.string.group_join_approvals_row),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (group.pendingApprovals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No pending join requests", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        items(group.pendingApprovals, key = { it.requestId }) { req ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = req.fullName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = "@${req.username.removePrefix("@")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onApproveJoinRequest(req.requestId, req.userId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Approve", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = { onRejectJoinRequest(req.requestId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Reject", fontSize = 12.sp)
                                    }
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet: Audit Logs
    if (isAuditLogsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isAuditLogsSheetOpen = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = stringResource(R.string.group_recent_actions_row),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (group.auditLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No recent administrative actions recorded", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        items(group.auditLogs, key = { it.logId }) { log ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = log.actionText,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "By ${log.actorName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun GroupInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEE2E2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TelefamRed,
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (onClick != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
private fun GroupSettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailingText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEE2E2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TelefamRed,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        if (trailingText.isNotBlank()) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(17.dp)
        )
    }
}
