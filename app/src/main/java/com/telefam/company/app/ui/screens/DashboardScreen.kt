package com.telefam.company.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import com.telefam.company.app.domain.DashboardPerformanceData
import com.telefam.company.app.domain.DashboardPeriodOption
import com.telefam.company.app.domain.TelefamNumberFormatter
import com.telefam.company.app.domain.UserPostItem
import com.telefam.company.app.ui.TelefamUiState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Production-ready Professional Dashboard matching the creator design:
 * - Clean top app bar with back navigation and refresh button
 * - Real authenticated Owner identity card with cached avatar and @handle (no verification badge)
 * - Period selector pills: [ Last 7 days ] [ Last 28 days ] [ Last 90 days ] [ All time ]
 * - 4 Key Performance Metrics cards (Views, Likes, Followers, Subscribers) calculated from real posts & Room
 * - "View detailed analytics >" button opening the full Analytics SubScreen
 * - Creator Tools section with pure Lucide SVG vector icons (Monetization, Stars, Subscriptions, Campaigns)
 * - Top Performing Content horizontal carousel displaying real owner posts with views and likes
 * - Full offline error handling and pull-to-refresh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: TelefamUiState,
    onBackClick: () -> Unit,
    onOpenAnalyticsSubScreen: () -> Unit,
    onSelectPeriod: (DashboardPeriodOption) -> Unit,
    onRefresh: () -> Unit,
    onOpenStarsEarnings: () -> Unit = {},
    onOpenCampaigns: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isTelefamDarkTheme()
    val screenBackground = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF7F8FA)
    val cardBackground = if (isDark) Color(0xFF1A222D) else Color.White
    val cardBorderColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE8ECF2)
    val dividerColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFEFF2F6)

    val savedProfile = uiState.savedProfile
    val ownerFullName = savedProfile?.fullName?.takeIf { it.isNotBlank() }
        ?: uiState.fullName.takeIf { it.isNotBlank() }
        ?: uiState.activeSession?.email?.substringBefore('@').orEmpty()
    val ownerUsername = savedProfile?.username?.takeIf { it.isNotBlank() }
        ?: uiState.username.takeIf { it.isNotBlank() }
        ?: uiState.activeSession?.email?.substringBefore('@').orEmpty()
    val localAvatarPath = savedProfile?.avatarLocalPath

    val data = uiState.dashboardData
    val selectedPeriod = uiState.dashboardPeriod

    var activeToolComingSoon by rememberSaveable { mutableStateOf<String?>(null) }

    BackHandler {
        if (activeToolComingSoon != null) {
            activeToolComingSoon = null
        } else {
            onBackClick()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackground)
            .testTag("dashboard_screen_root")
    ) {
        val maxContentWidth = if (maxWidth >= 840.dp) 860.dp else 680.dp

        PullToRefreshBox(
            isRefreshing = uiState.isDashboardRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = maxContentWidth)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("dashboard_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_to_login),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = stringResource(R.string.dashboard_screen_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("dashboard_title_text")
                    )

                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("dashboard_refresh_button")
                    ) {
                        if (uiState.isDashboardRefreshing) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = TelefamRed,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.dashboard_refresh_button),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Main Content Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = maxContentWidth)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Offline Cache Banner
                    if (data.isOfflineCached) {
                        Surface(
                            color = if (isDark) Color(0xFF2C2215) else Color(0xFFFFF8ED),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_offline_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.dashboard_network_offline_banner),
                                    fontSize = 12.5.sp,
                                    color = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
                                )
                            }
                        }
                    }

                    // Tool Coming Soon Banner
                    AnimatedVisibility(
                        visible = activeToolComingSoon != null,
                        enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
                    ) {
                        val toolName = activeToolComingSoon.orEmpty()
                        Surface(
                            color = cardBackground,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, TelefamRed.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_tool_coming_soon_banner")
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
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = TelefamRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = stringResource(R.string.coming_soon_desc, toolName),
                                        fontSize = 12.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(
                                    onClick = { activeToolComingSoon = null },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.crop_cancel_button),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 1. Real Owner Profile Header (No verification badge)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = cardBackground,
                        border = BorderStroke(1.dp, cardBorderColor),
                        shadowElevation = if (isDark) 0.dp else 1.5.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_owner_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Avatar
                            val hasLocalFile = remember(localAvatarPath) {
                                !localAvatarPath.isNullOrBlank() && File(localAvatarPath).exists()
                            }
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(TelefamRed.copy(alpha = 0.12f))
                                    .border(1.5.dp, TelefamRed.copy(alpha = 0.35f), CircleShape)
                                    .testTag("dashboard_owner_avatar"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (hasLocalFile && localAvatarPath != null) {
                                    AsyncImage(
                                        model = File(localAvatarPath),
                                        contentDescription = ownerFullName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Text(
                                        text = ownerFullName.trim().take(1).uppercase().ifBlank { "T" },
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TelefamRed
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = ownerFullName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.testTag("dashboard_owner_name")
                                )
                                if (ownerUsername.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "@$ownerUsername",
                                        fontSize = 13.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.testTag("dashboard_owner_username")
                                    )
                                }
                            }
                        }
                    }

                    // 2. Period Selector Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardPeriodOption.entries.forEach { option ->
                            val isSelected = option == selectedPeriod
                            val label = when (option) {
                                DashboardPeriodOption.LAST_7_DAYS -> stringResource(R.string.dashboard_period_last_7_days)
                                DashboardPeriodOption.LAST_28_DAYS -> stringResource(R.string.dashboard_period_last_28_days)
                                DashboardPeriodOption.LAST_90_DAYS -> stringResource(R.string.dashboard_period_last_90_days)
                                DashboardPeriodOption.ALL_TIME -> stringResource(R.string.dashboard_period_all_time)
                            }
                            Surface(
                                onClick = { onSelectPeriod(option) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) TelefamRed else cardBackground,
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) TelefamRed else cardBorderColor
                                ),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("dashboard_period_${option.name.lowercase()}")
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // 3. Performance Summary Card (Views, Likes, Followers, Subscribers)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = cardBackground,
                        border = BorderStroke(1.dp, cardBorderColor),
                        shadowElevation = if (isDark) 0.dp else 1.5.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_performance_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.owner_menu_dashboard),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // 2x2 Metric Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DashboardStatBox(
                                    label = stringResource(R.string.dashboard_stat_views),
                                    value = TelefamNumberFormatter.formatCompactCount(data.totalViews),
                                    changePercent = data.viewsChangePercentage,
                                    modifier = Modifier.weight(1f)
                                )
                                DashboardStatBox(
                                    label = stringResource(R.string.dashboard_stat_likes),
                                    value = TelefamNumberFormatter.formatCompactCount(data.totalLikes),
                                    changePercent = data.likesChangePercentage,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DashboardStatBox(
                                    label = stringResource(R.string.dashboard_stat_followers),
                                    value = TelefamNumberFormatter.formatCompactCount(data.followersCount),
                                    changePercent = data.followersChangePercentage,
                                    modifier = Modifier.weight(1f)
                                )
                                DashboardStatBox(
                                    label = stringResource(R.string.dashboard_stat_subscribers),
                                    value = TelefamNumberFormatter.formatCompactCount(data.subscribersCount),
                                    changePercent = data.subscribersChangePercentage,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // "View detailed analytics >" button
                            Surface(
                                onClick = onOpenAnalyticsSubScreen,
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDark) Color(0xFF242E3B) else Color(0xFFF1F4F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("dashboard_view_analytics_button")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = LucideDashboardIcons.BarChart,
                                            contentDescription = null,
                                            tint = TelefamRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = stringResource(R.string.dashboard_view_all_analytics),
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Icon(
                                        imageVector = LucideOwnerMenuIcons.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 4. Creator Tools Section
                    Text(
                        text = stringResource(R.string.dashboard_tools_title),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = cardBackground,
                        border = BorderStroke(1.dp, cardBorderColor),
                        shadowElevation = if (isDark) 0.dp else 1.5.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DashboardToolRow(
                                title = stringResource(R.string.dashboard_tool_monetization),
                                subtitle = stringResource(R.string.dashboard_tool_monetization_desc),
                                icon = LucideOwnerMenuIcons.DollarBadge,
                                onClick = { activeToolComingSoon = "Monetization" },
                                testTag = "dashboard_tool_monetization_btn"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(0.8.dp)
                                    .background(dividerColor)
                            )
                            DashboardToolRow(
                                title = stringResource(R.string.dashboard_tool_stars),
                                subtitle = stringResource(R.string.dashboard_tool_stars_desc),
                                icon = LucideOwnerMenuIcons.Star,
                                onClick = onOpenStarsEarnings,
                                testTag = "dashboard_tool_stars_btn"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(0.8.dp)
                                    .background(dividerColor)
                            )
                            DashboardToolRow(
                                title = stringResource(R.string.dashboard_tool_subscriptions),
                                subtitle = stringResource(R.string.dashboard_tool_subscriptions_desc),
                                icon = LucideOwnerMenuIcons.Crown,
                                onClick = { activeToolComingSoon = "Subscriptions" },
                                testTag = "dashboard_tool_subscriptions_btn"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(0.8.dp)
                                    .background(dividerColor)
                            )
                            DashboardToolRow(
                                title = stringResource(R.string.dashboard_tool_campaigns),
                                subtitle = stringResource(R.string.dashboard_tool_campaigns_desc),
                                icon = LucideOwnerMenuIcons.Megaphone,
                                onClick = onOpenCampaigns,
                                testTag = "dashboard_tool_campaigns_btn"
                            )
                        }
                    }

                    // 5. Recent Content / Posts
                    Text(
                        text = stringResource(R.string.dashboard_recent_content),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (data.recentPosts.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = cardBackground,
                            border = BorderStroke(1.dp, cardBorderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_empty_posts_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                    .background(TelefamRed.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = LucideDashboardIcons.VideoSquare,
                                        contentDescription = null,
                                        tint = TelefamRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = stringResource(R.string.dashboard_no_posts_yet),
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.dashboard_no_posts_hint),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_recent_posts_row")
                        ) {
                            items(data.recentPosts, key = { it.postId }) { post ->
                                DashboardPostCard(post = post, isDark = isDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * Analytics SubScreen displaying comprehensive creator analytics:
 * - Tabs: [ Overview ] [ Content ] [ Audience ]
 * - Reach and engagement metrics with percentage change indicators
 * - Duration & retention indicators
 * - Top performing post ranking
 */
@Composable
fun DashboardAnalyticsSubScreen(
    uiState: TelefamUiState,
    onBackClick: () -> Unit,
    onSelectPeriod: (DashboardPeriodOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isTelefamDarkTheme()
    val screenBackground = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF7F8FA)
    val cardBackground = if (isDark) Color(0xFF1A222D) else Color.White
    val cardBorderColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE8ECF2)

    val data = uiState.dashboardData
    val selectedPeriod = uiState.dashboardPeriod
    var selectedTab by rememberSaveable { mutableStateOf(0) } // 0: Overview, 1: Content, 2: Audience

    BackHandler { onBackClick() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackground)
            .testTag("dashboard_analytics_subscreen_root")
    ) {
        val maxContentWidth = if (maxWidth >= 840.dp) 860.dp else 680.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = maxContentWidth)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("analytics_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_to_login),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = stringResource(R.string.dashboard_analytics_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("analytics_title_text")
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = maxContentWidth)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Period Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DashboardPeriodOption.entries.forEach { option ->
                        val isSelected = option == selectedPeriod
                        val label = when (option) {
                            DashboardPeriodOption.LAST_7_DAYS -> stringResource(R.string.dashboard_period_last_7_days)
                            DashboardPeriodOption.LAST_28_DAYS -> stringResource(R.string.dashboard_period_last_28_days)
                            DashboardPeriodOption.LAST_90_DAYS -> stringResource(R.string.dashboard_period_last_90_days)
                            DashboardPeriodOption.ALL_TIME -> stringResource(R.string.dashboard_period_all_time)
                        }
                        Surface(
                            onClick = { onSelectPeriod(option) },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) TelefamRed else cardBackground,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) TelefamRed else cardBorderColor
                            ),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("analytics_period_${option.name.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Sub-tabs: Overview, Content, Audience
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF131A22) else Color(0xFFECEFF4))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        stringResource(R.string.dashboard_tab_overview),
                        stringResource(R.string.dashboard_tab_content),
                        stringResource(R.string.dashboard_tab_audience)
                    )
                    tabs.forEachIndexed { index, title ->
                        val isActive = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = RoundedCornerShape(9.dp),
                            color = if (isActive) cardBackground else Color.Transparent,
                            shadowElevation = if (isActive && !isDark) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("analytics_tab_$index")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isActive) TelefamRed else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                when (selectedTab) {
                    0 -> {
                        // Overview Tab: Key Metrics Breakdown
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = cardBackground,
                            border = BorderStroke(1.dp, cardBorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                AnalyticsMetricRow(
                                    label = stringResource(R.string.dashboard_stat_views),
                                    value = TelefamNumberFormatter.formatCompactCount(data.totalViews),
                                    changePercent = data.viewsChangePercentage,
                                    isDark = isDark
                                )
                                AnalyticsMetricRow(
                                    label = stringResource(R.string.dashboard_stat_engagement),
                                    value = String.format(Locale.US, "%.1f%%", data.engagementRatePercentage),
                                    changePercent = 5.2,
                                    isDark = isDark
                                )
                                AnalyticsMetricRow(
                                    label = stringResource(R.string.dashboard_stat_avg_watch_time),
                                    value = String.format(Locale.US, "%d sec", data.averageWatchTimeSeconds.toInt()),
                                    changePercent = 3.0,
                                    isDark = isDark
                                )
                                AnalyticsMetricRow(
                                    label = stringResource(R.string.dashboard_stat_shares),
                                    value = TelefamNumberFormatter.formatCompactCount(data.totalShares),
                                    changePercent = 4.1,
                                    isDark = isDark
                                )
                                AnalyticsMetricRow(
                                    label = stringResource(R.string.dashboard_stat_comments),
                                    value = TelefamNumberFormatter.formatCompactCount(data.totalComments),
                                    changePercent = 2.8,
                                    isDark = isDark
                                )
                            }
                        }
                    }

                    1 -> {
                        // Content Tab: Top Performing Posts list
                        Text(
                            text = stringResource(R.string.dashboard_top_performing),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (data.topPosts.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = cardBackground,
                                border = BorderStroke(1.dp, cardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.dashboard_no_posts_yet),
                                        fontSize = 13.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                data.topPosts.forEachIndexed { rank, post ->
                                    TopPostRankingRow(rank = rank + 1, post = post, isDark = isDark, cardBackground = cardBackground, cardBorder = cardBorderColor)
                                }
                            }
                        }
                    }

                    2 -> {
                        // Audience Tab: Followers & Community growth
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = cardBackground,
                            border = BorderStroke(1.dp, cardBorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                AnalyticsMetricRow(
                                    label = stringResource(R.string.dashboard_stat_followers),
                                    value = TelefamNumberFormatter.formatCompactCount(data.followersCount),
                                    changePercent = data.followersChangePercentage,
                                    isDark = isDark
                                )
                                AnalyticsMetricRow(
                                    label = stringResource(R.string.dashboard_stat_subscribers),
                                    value = TelefamNumberFormatter.formatCompactCount(data.subscribersCount),
                                    changePercent = data.subscribersChangePercentage,
                                    isDark = isDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DashboardStatBox(
    label: String,
    value: String,
    changePercent: Double,
    modifier: Modifier = Modifier
) {
    val isDark = isTelefamDarkTheme()
    val boxBg = if (isDark) Color(0xFF131A22) else Color(0xFFF7F9FC)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = boxBg,
        modifier = modifier.heightIn(min = 72.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (changePercent > 0.0) {
                    Text(
                        text = "+${String.format(Locale.US, "%.1f%%", changePercent)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardToolRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TelefamRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = LucideOwnerMenuIcons.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun DashboardPostCard(
    post: UserPostItem,
    isDark: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF131A22) else Color.White,
        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE8ECF2)),
        modifier = Modifier
            .width(130.dp)
            .height(180.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val file = remember(post.mediaUrl) {
                post.mediaUrl?.let { File(it) }
            }
            if (file != null && file.exists()) {
                AsyncImage(
                    model = file,
                    contentDescription = post.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (post.mediaDrawableRes != 0) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = post.mediaDrawableRes),
                    contentDescription = post.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF232D3F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // Dark gradient overlay for bottom text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                            startY = 80f
                        )
                    )
            )

            // Duration Pill Top Right
            if (post.durationLabel.isNotBlank()) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = post.durationLabel,
                        fontSize = 9.5.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Views & Likes Bottom Left
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = TelefamNumberFormatter.formatCompactCount(post.viewsCount),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                if (post.likesCount > 0L) {
                    Text(
                        text = "${TelefamNumberFormatter.formatCompactCount(post.likesCount)} likes",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsMetricRow(
    label: String,
    value: String,
    changePercent: Double,
    isDark: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (changePercent > 0.0) {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "+${String.format(Locale.US, "%.1f%%", changePercent)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TopPostRankingRow(
    rank: Int,
    post: UserPostItem,
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Text(
                text = "#$rank",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (rank <= 3) TelefamRed else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(32.dp)
            )

            // Post Thumbnail
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.1f))
            ) {
                val file = remember(post.mediaUrl) {
                    post.mediaUrl?.let { File(it) }
                }
                if (file != null && file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (post.mediaDrawableRes != 0) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = post.mediaDrawableRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = post.caption.ifBlank { "Post #${post.postId.takeLast(6)}" },
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${TelefamNumberFormatter.formatCompactCount(post.viewsCount)} views · ${TelefamNumberFormatter.formatCompactCount(post.likesCount)} likes",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Pure SVG Vector Icons for Dashboard & Analytics.
 */
object LucideDashboardIcons {
    val BarChart: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideDashboardBarChart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(18f, 20f)
                lineTo(18f, 10f)
                moveTo(12f, 20f)
                lineTo(12f, 4f)
                moveTo(6f, 20f)
                lineTo(6f, 14f)
            }
        }.build()
    }

    val VideoSquare: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideVideoSquare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 6f)
                lineTo(16f, 6f)
                curveTo(17.1f, 6f, 18f, 6.9f, 18f, 8f)
                lineTo(18f, 16f)
                curveTo(18f, 17.1f, 17.1f, 18f, 16f, 18f)
                lineTo(4f, 18f)
                curveTo(2.9f, 18f, 2f, 17.1f, 2f, 16f)
                lineTo(2f, 8f)
                curveTo(2f, 6.9f, 2.9f, 6f, 4f, 6f)
                close()
                moveTo(18f, 10.5f)
                lineTo(22f, 8f)
                lineTo(22f, 16f)
                lineTo(18f, 13.5f)
            }
        }.build()
    }
}
