package com.telefam.company.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.telefam.company.app.data.CreatorSubscriberEntity
import com.telefam.company.app.data.SubscriptionPlanEntity
import com.telefam.company.app.ui.TelefamUiState
import com.telefam.company.app.ui.components.MonetizationLucideIcons
import com.example.ui.theme.isTelefamDarkTheme
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Creator Subscriptions Screen matching Image 2 exactly:
 * - Profile header with verified badge and Creator pill
 * - Total Subscription Earnings card with 3D purple crown asset
 * - Overview with time-range selector, 4 metric cards, and "View Subscription Insights"
 * - Subscription Plans section with "Manage Plans", Plan Card, and "+ Create New Plan"
 * - Recent Subscribers section with "See All" and clean empty state
 * - How Subscriptions Work 3-step guide
 * - Purple encouragement banner
 * - All supporting sub-screens: Create Plan, Manage Plans, Insights, All Subscribers, Help
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    uiState: TelefamUiState,
    onBackClick: () -> Unit,
    onSelectPeriodFilter: (String) -> Unit = {},
    onOpenCreatePlan: () -> Unit = {},
    onCloseCreatePlan: () -> Unit = {},
    onOpenManagePlans: () -> Unit = {},
    onCloseManagePlans: () -> Unit = {},
    onOpenInsights: () -> Unit = {},
    onCloseInsights: () -> Unit = {},
    onOpenAllSubscribers: () -> Unit = {},
    onCloseAllSubscribers: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onCloseHelp: () -> Unit = {},
    onCreatePlanSubmit: (name: String, price: Double, description: String, perks: List<String>) -> Unit = { _, _, _, _ -> },
    onDeletePlan: (String) -> Unit = {},
    onClearStatusMessage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isTelefamDarkTheme()
    val screenBackground = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF9FAFD)
    val cardBackground = if (isDark) Color(0xFF1E232E) else Color.White
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFEDEEF3)
    val primaryText = if (isDark) Color.White else Color(0xFF161922)
    val secondaryText = if (isDark) Color(0xFF9AA4B2) else Color(0xFF6B7280)
    val accentRed = Color(0xFFE53935)
    val purpleAccent = Color(0xFF8B5CF6)

    androidx.activity.compose.BackHandler {
        onBackClick()
    }

    // Resolved creator info from real profile (no mock/fake)
    val savedProfile = uiState.savedProfile
    val ownerFullName = savedProfile?.fullName?.takeIf { it.isNotBlank() }
        ?: uiState.fullName.takeIf { it.isNotBlank() }
        ?: uiState.activeSession?.email?.substringBefore('@').orEmpty().ifBlank { "Telefam Creator" }
    val ownerUsername = savedProfile?.username?.takeIf { it.isNotBlank() }
        ?: uiState.username.takeIf { it.isNotBlank() }
        ?: uiState.activeSession?.email?.substringBefore('@').orEmpty().ifBlank { "creator" }
    val avatarPath = savedProfile?.avatarLocalPath

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 2 }
    }

    Scaffold(
        modifier = modifier.testTag("subscriptions_screen_root"),
        containerColor = screenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.subscriptions_title),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText,
                        modifier = Modifier.testTag("subscriptions_screen_title")
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("subscriptions_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.crop_cancel_button),
                            tint = primaryText
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenHelp,
                        modifier = Modifier.testTag("subscriptions_help_button")
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.HelpCircle,
                            contentDescription = stringResource(R.string.subscriptions_help_title),
                            tint = primaryText,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = screenBackground
                )
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val contentWidthModifier = if (maxWidth > 680.dp) {
                Modifier
                    .widthIn(max = 680.dp)
                    .align(Alignment.Center)
            } else {
                Modifier.fillMaxWidth()
            }

            LazyColumn(
                modifier = contentWidthModifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Success / Error Feedback Banner
                if (uiState.subscriptionsActionSuccessMessage != null || uiState.subscriptionsErrorMessage != null) {
                    item {
                        val isSuccess = uiState.subscriptionsActionSuccessMessage != null
                        val message = uiState.subscriptionsActionSuccessMessage ?: uiState.subscriptionsErrorMessage.orEmpty()
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            border = BorderStroke(1.dp, if (isSuccess) Color(0xFF81C784) else Color(0xFFEF9A9A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("subscriptions_feedback_banner")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = message,
                                    fontSize = 13.sp,
                                    color = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = onClearStatusMessage,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 1. Creator Profile Identity Header
                item {
                    CreatorProfileHeaderRow(
                        fullName = ownerFullName,
                        username = ownerUsername,
                        avatarPath = avatarPath,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    )
                }

                // 2. Total Subscription Earnings Card (Matching Image 2 with 3D purple crown asset)
                item {
                    TotalSubscriptionEarningsCard(
                        totalEarnings = uiState.totalSubscriptionEarnings,
                        currencyFormat = currencyFormat,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    )
                }

                // 3. Overview Section
                item {
                    SubscriptionOverviewSection(
                        uiState = uiState,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        currencyFormat = currencyFormat,
                        onSelectPeriodFilter = onSelectPeriodFilter,
                        onOpenInsights = onOpenInsights
                    )
                }

                // 4. Subscription Plans Section
                item {
                    SubscriptionPlansSection(
                        plans = uiState.subscriptionPlans,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        currencyFormat = currencyFormat,
                        accentRed = accentRed,
                        onOpenManagePlans = onOpenManagePlans,
                        onOpenCreatePlan = onOpenCreatePlan
                    )
                }

                // 5. Recent Subscribers Section
                item {
                    RecentSubscribersSection(
                        subscribers = uiState.creatorSubscribers,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        accentRed = accentRed,
                        onOpenAllSubscribers = onOpenAllSubscribers
                    )
                }

                // 6. How Subscriptions Work Section (3 Steps)
                item {
                    HowSubscriptionsWorkSection(
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    )
                }

                // 7. Bottom Purple Encouragement Banner
                item {
                    PurpleEncouragementBanner()
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Modal Subscreens & Sheets
    if (uiState.isCreatePlanSheetOpen) {
        CreateSubscriptionPlanSheet(
            onDismiss = onCloseCreatePlan,
            onSave = { name, price, desc, perks ->
                onCreatePlanSubmit(name, price, desc, perks)
                onCloseCreatePlan()
            }
        )
    }

    if (uiState.isManagePlansSheetOpen) {
        ManageSubscriptionPlansSheet(
            plans = uiState.subscriptionPlans,
            currencyFormat = currencyFormat,
            onDismiss = onCloseManagePlans,
            onDeletePlan = onDeletePlan,
            onOpenCreatePlan = {
                onCloseManagePlans()
                onOpenCreatePlan()
            }
        )
    }

    if (uiState.isSubscriptionInsightsSheetOpen) {
        SubscriptionInsightsSheet(
            uiState = uiState,
            currencyFormat = currencyFormat,
            onDismiss = onCloseInsights
        )
    }

    if (uiState.isAllSubscribersSheetOpen) {
        AllSubscribersSheet(
            subscribers = uiState.creatorSubscribers,
            onDismiss = onCloseAllSubscribers
        )
    }

    if (uiState.isSubscriptionsHelpSheetOpen) {
        SubscriptionsHelpDialog(onDismiss = onCloseHelp)
    }
}

// -----------------------------------------------------------------------------
// UI SECTIONS & COMPONENTS (Matching Image 2)
// -----------------------------------------------------------------------------

@Composable
private fun CreatorProfileHeaderRow(
    fullName: String,
    username: String,
    avatarPath: String?,
    primaryText: Color,
    secondaryText: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("creator_profile_header"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Creator Avatar
        val hasFile = remember(avatarPath) {
            !avatarPath.isNullOrBlank() && File(avatarPath).exists()
        }
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            if (hasFile && avatarPath != null) {
                AsyncImage(
                    model = File(avatarPath),
                    contentDescription = fullName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = fullName.take(1).uppercase().ifBlank { "R" },
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fullName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("creator_full_name")
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Red verified check badge
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verified Creator",
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (username.startsWith("@")) username else "@$username",
                fontSize = 14.sp,
                color = secondaryText,
                modifier = Modifier.testTag("creator_username")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Creator badge pill
            Surface(
                color = Color(0xFFFFECEF),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("creator_pill_badge")
            ) {
                Text(
                    text = stringResource(R.string.wallet_creator_badge),
                    color = Color(0xFFE53935),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun TotalSubscriptionEarningsCard(
    totalEarnings: Double,
    currencyFormat: NumberFormat,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("total_subscription_earnings_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.subscriptions_total_earnings),
                    fontSize = 13.5.sp,
                    color = secondaryText,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currencyFormat.format(totalEarnings),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryText,
                    modifier = Modifier.testTag("total_subscription_earnings_value")
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.subscriptions_all_time),
                    fontSize = 12.sp,
                    color = secondaryText
                )
            }

            // 3D Purple Crown Asset (Matching Image 2)
            Image(
                painter = painterResource(R.drawable.img_3d_purple_crown),
                contentDescription = "Subscription Crown",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("subscriptions_3d_crown_asset")
            )
        }
    }
}

@Composable
private fun SubscriptionOverviewSection(
    uiState: TelefamUiState,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    currencyFormat: NumberFormat,
    onSelectPeriodFilter: (String) -> Unit,
    onOpenInsights: () -> Unit
) {
    var periodDropdownExpanded by remember { mutableStateOf(false) }
    val periodLabel = when (uiState.subscriptionPeriodFilter) {
        "LAST_7_DAYS" -> "Last 7 Days"
        "LAST_90_DAYS" -> "Last 90 Days"
        "ALL_TIME" -> stringResource(R.string.subscriptions_all_time)
        else -> stringResource(R.string.wallet_period_last_30_days)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header with Time-range Dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.subscriptions_overview),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText
            )

            Box {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = cardBackground,
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier
                        .clickable { periodDropdownExpanded = true }
                        .testTag("subscription_period_dropdown_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = periodLabel,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryText
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Period",
                            tint = secondaryText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = periodDropdownExpanded,
                    onDismissRequest = { periodDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Last 7 Days") },
                        onClick = {
                            onSelectPeriodFilter("LAST_7_DAYS")
                            periodDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.wallet_period_last_30_days)) },
                        onClick = {
                            onSelectPeriodFilter("LAST_30_DAYS")
                            periodDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Last 90 Days") },
                        onClick = {
                            onSelectPeriodFilter("LAST_90_DAYS")
                            periodDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.subscriptions_all_time)) },
                        onClick = {
                            onSelectPeriodFilter("ALL_TIME")
                            periodDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // 4 Metric Cards in 2x2 Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Earnings Card (Purple Dollar)
            SubscriptionMetricCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF7C3AED)
                        )
                    }
                },
                value = currencyFormat.format(uiState.subscriptionPeriodEarnings),
                title = stringResource(R.string.subscriptions_earnings),
                deltaText = "— 0%",
                deltaColor = Color(0xFF6B7280),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                modifier = Modifier.weight(1f)
            )

            // Subscribers Card (Green Users)
            SubscriptionMetricCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD1FAE5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Users,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                },
                value = uiState.subscriptionSubscribersCount.toString(),
                title = stringResource(R.string.subscriptions_subscribers),
                deltaText = "— 0%",
                deltaColor = Color(0xFF6B7280),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // New Subscribers Card (Blue UserPlus)
            SubscriptionMetricCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.UserPlus,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                },
                value = uiState.subscriptionNewSubscribersCount.toString(),
                title = stringResource(R.string.subscriptions_new_subscribers),
                deltaText = "— 0%",
                deltaColor = Color(0xFF6B7280),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                modifier = Modifier.weight(1f)
            )

            // Canceled Card (Orange UserX)
            SubscriptionMetricCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEDD5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.UserX,
                            contentDescription = null,
                            tint = Color(0xFFEA580C),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                },
                value = uiState.subscriptionCanceledCount.toString(),
                title = stringResource(R.string.subscriptions_canceled),
                deltaText = "— 0%",
                deltaColor = Color(0xFF6B7280),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                modifier = Modifier.weight(1f)
            )
        }

        // View Subscription Insights Button (Matching Image 2)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = cardBackground,
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenInsights() }
                .testTag("view_subscription_insights_button")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = MonetizationLucideIcons.BarChart3,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.subscriptions_view_insights),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryText
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = secondaryText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SubscriptionMetricCard(
    icon: @Composable () -> Unit,
    value: String,
    title: String,
    deltaText: String,
    deltaColor: Color,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier.testTag("metric_card_$title")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = secondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = deltaText,
                fontSize = 11.5.sp,
                color = deltaColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SubscriptionPlansSection(
    plans: List<SubscriptionPlanEntity>,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    currencyFormat: NumberFormat,
    accentRed: Color,
    onOpenManagePlans: () -> Unit,
    onOpenCreatePlan: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.subscriptions_plans_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText
            )

            Text(
                text = stringResource(R.string.subscriptions_manage_plans),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentRed,
                modifier = Modifier
                    .clickable { onOpenManagePlans() }
                    .testTag("manage_subscription_plans_button")
            )
        }

        // Active plans list or default "Supporter" tier card (Matching Image 2)
        if (plans.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardBackground,
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenManagePlans() }
                    .testTag("default_supporter_plan_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Crown,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.subscriptions_supporter_default),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.subscriptions_per_month, "$4.99"),
                            fontSize = 12.5.sp,
                            color = secondaryText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.subscriptions_perks_default_summary),
                            fontSize = 11.5.sp,
                            color = secondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.subscriptions_subscribers_count, 0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryText
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = secondaryText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        } else {
            plans.forEach { plan ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = cardBackground,
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenManagePlans() }
                        .testTag("plan_card_${plan.planId}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEDE9FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = MonetizationLucideIcons.Crown,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = plan.planName,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.subscriptions_per_month, currencyFormat.format(plan.monthlyPrice)),
                                fontSize = 12.5.sp,
                                color = secondaryText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = plan.description.ifBlank { stringResource(R.string.subscriptions_perks_default_summary) },
                                fontSize = 11.5.sp,
                                color = secondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.subscriptions_subscribers_count, plan.subscribersCount),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryText
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = secondaryText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // "+ Create New Plan" Red Outlined Button (Matching Image 2)
        OutlinedButton(
            onClick = onOpenCreatePlan,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = accentRed
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("create_new_plan_button")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.subscriptions_create_new_plan),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RecentSubscribersSection(
    subscribers: List<CreatorSubscriberEntity>,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    accentRed: Color,
    onOpenAllSubscribers: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.subscriptions_recent_subscribers),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText
            )

            Text(
                text = stringResource(R.string.wallet_see_all),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentRed,
                modifier = Modifier
                    .clickable { onOpenAllSubscribers() }
                    .testTag("subscribers_see_all_button")
            )
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = cardBackground,
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (subscribers.isEmpty()) {
                // Empty state matching Image 2
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Users,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.subscriptions_no_subscribers),
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.subscriptions_no_subscribers_desc),
                        fontSize = 12.sp,
                        color = secondaryText,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subscribers.take(3).forEach { sub ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE2E8F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sub.subscriberName.take(1).uppercase().ifBlank { "U" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sub.subscriberName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp,
                                    color = primaryText
                                )
                                Text(
                                    text = "@${sub.subscriberUsername} • ${sub.planName}",
                                    fontSize = 12.sp,
                                    color = secondaryText
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (sub.status == "ACTIVE") Color(0xFFD1FAE5) else Color(0xFFFFEDD5)
                            ) {
                                Text(
                                    text = sub.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sub.status == "ACTIVE") Color(0xFF059669) else Color(0xFFEA580C),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HowSubscriptionsWorkSection(
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("how_subscriptions_work_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.subscriptions_how_it_works),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText
            )

            // Step 1: Create a Plan (Purple Star badge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MonetizationLucideIcons.Star,
                        contentDescription = null,
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(R.string.subscriptions_step1_title),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.subscriptions_step1_desc),
                        fontSize = 12.sp,
                        color = secondaryText
                    )
                }
            }

            // Step 2: Gain Subscribers (Green Users)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD1FAE5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MonetizationLucideIcons.Users,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(R.string.subscriptions_step2_title),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.subscriptions_step2_desc),
                        fontSize = 12.sp,
                        color = secondaryText
                    )
                }
            }

            // Step 3: Earn Monthly (Orange Wallet / Card)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEDD5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MonetizationLucideIcons.Wallet,
                        contentDescription = null,
                        tint = Color(0xFFEA580C),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(R.string.subscriptions_step3_title),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.subscriptions_step3_desc),
                        fontSize = 12.sp,
                        color = secondaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun PurpleEncouragementBanner() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF3F0FF),
        border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("purple_encouragement_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C3AED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MonetizationLucideIcons.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.subscriptions_encouragement),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5B21B6),
                lineHeight = 18.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------
// MODALS & BOTTOM SHEETS
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateSubscriptionPlanSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, price: Double, desc: String, perks: List<String>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var planName by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("4.99") }
    var description by remember { mutableStateOf("") }
    val availablePerks = remember {
        listOf(
            "Exclusive Subscriber-Only Posts",
            "Special Crown Supporter Badge",
            "Direct Messaging Priority Access",
            "Behind-The-Scenes Content",
            "Live Stream VIP Chat Badge"
        )
    }
    val selectedPerks = remember { mutableStateListOf("Exclusive Subscriber-Only Posts", "Special Crown Supporter Badge") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.subscriptions_create_plan_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            OutlinedTextField(
                value = planName,
                onValueChange = { planName = it },
                label = { Text(stringResource(R.string.subscriptions_plan_name)) },
                placeholder = { Text(stringResource(R.string.subscriptions_plan_name_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_plan_name_input"),
                singleLine = true
            )

            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text(stringResource(R.string.subscriptions_monthly_price)) },
                placeholder = { Text("4.99") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_plan_price_input"),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.subscriptions_plan_desc)) },
                placeholder = { Text(stringResource(R.string.subscriptions_plan_desc_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_plan_description_input"),
                maxLines = 3
            )

            Text(
                text = "Included Perks",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                availablePerks.forEach { perk ->
                    val isChecked = selectedPerks.contains(perk)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) selectedPerks.remove(perk) else selectedPerks.add(perk)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) selectedPerks.add(perk) else selectedPerks.remove(perk)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF7C3AED))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = perk, fontSize = 13.sp)
                    }
                }
            }

            Button(
                onClick = {
                    val cleanName = planName.trim()
                    val priceVal = priceText.toDoubleOrNull()
                    if (cleanName.isBlank()) {
                        errorMessage = "Please enter a plan name"
                        return@Button
                    }
                    if (priceVal == null || priceVal <= 0.0) {
                        errorMessage = "Please enter a valid monthly price"
                        return@Button
                    }
                    onSave(cleanName, priceVal, description.trim(), selectedPerks.toList())
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_plan_submit_button")
            ) {
                Text(
                    text = stringResource(R.string.subscriptions_save_plan),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageSubscriptionPlansSheet(
    plans: List<SubscriptionPlanEntity>,
    currencyFormat: NumberFormat,
    onDismiss: () -> Unit,
    onDeletePlan: (String) -> Unit,
    onOpenCreatePlan: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.subscriptions_manage_plans),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onOpenCreatePlan) {
                    Text("+ New Plan", color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
                }
            }

            if (plans.isEmpty()) {
                Text(
                    text = "No custom subscription plans created yet. Tap '+ New Plan' to add your first tier.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                plans.forEach { plan ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = plan.planName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = "${currencyFormat.format(plan.monthlyPrice)} / month • ${plan.subscribersCount} subscribers",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            IconButton(
                                onClick = { onDeletePlan(plan.planId) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Plan",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscriptionInsightsSheet(
    uiState: TelefamUiState,
    currencyFormat: NumberFormat,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.subscriptions_insights_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // MRR Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFEDE9FE),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.subscriptions_insights_mrr),
                        fontSize = 12.5.sp,
                        color = Color(0xFF6D28D9),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currencyFormat.format(uiState.subscriptionPeriodEarnings),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4C1D95)
                    )
                }
            }

            // Retention Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFD1FAE5),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.subscriptions_insights_retention),
                        fontSize = 12.5.sp,
                        color = Color(0xFF047857),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val retentionRate = if (uiState.subscriptionSubscribersCount + uiState.subscriptionCanceledCount > 0) {
                        (uiState.subscriptionSubscribersCount.toDouble() / (uiState.subscriptionSubscribersCount + uiState.subscriptionCanceledCount) * 100).toInt()
                    } else {
                        100
                    }
                    Text(
                        text = "$retentionRate%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllSubscribersSheet(
    subscribers: List<CreatorSubscriberEntity>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(subscribers, searchQuery) {
        if (searchQuery.isBlank()) subscribers else {
            subscribers.filter {
                it.subscriberName.contains(searchQuery, ignoreCase = true) ||
                        it.subscriberUsername.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.subscriptions_recent_subscribers),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search subscribers…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (filtered.isEmpty()) {
                Text(
                    text = "No subscribers found.",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.subscriptionId }) { sub ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = sub.subscriberName.take(1).uppercase().ifBlank { "S" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = sub.subscriberName, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                    Text(text = "@${sub.subscriberUsername} • ${sub.planName}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(
                                    text = sub.status,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sub.status == "ACTIVE") Color(0xFF059669) else Color(0xFFEA580C)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SubscriptionsHelpDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.subscriptions_help_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.subscriptions_help_desc),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "• Creators receive recurring monthly income from active subscribers.\n" +
                            "• Subscribers get custom badges, exclusive posts, and priority chat access.\n" +
                            "• Subscriptions renew automatically every 30 days unless canceled by the member.",
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Got it", color = Color.White)
                }
            }
        }
    }
}
