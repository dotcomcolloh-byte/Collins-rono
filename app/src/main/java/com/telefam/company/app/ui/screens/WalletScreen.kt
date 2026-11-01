package com.telefam.company.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.telefam.company.app.data.PayoutAccountEntity
import com.telefam.company.app.data.PayoutRecordEntity
import com.telefam.company.app.data.WalletTransactionEntity
import com.telefam.company.app.ui.TelefamUiState
import com.telefam.company.app.ui.components.MonetizationLucideIcons
import com.example.ui.theme.isTelefamDarkTheme
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Creator Wallet Screen matching Image 1 exactly:
 * - Profile header with verified badge and Creator pill
 * - Total Balance card with eye toggle, 3D purple wallet asset, Available balance, and Withdraw button
 * - Earnings Overview with period dropdown ("This Month ⌄") and 4 cards (Stars, Subscriptions, Ad Revenue, Other)
 * - Transaction History with filter chips (All, Earnings, Payouts, Adjustments) and empty receipt illustration
 * - Payouts section with Payout Account card, "Add Payout Account" button, and 4 metric cards
 * - Payment Methods section with "+ Add New" action and Bank Account card
 * - All supporting sub-screens: WithdrawSheet, AddPayoutAccountSheet, AllTransactionsSheet, AllPayoutsSheet, Help, Settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    uiState: TelefamUiState,
    onBackClick: () -> Unit,
    onToggleBalanceVisibility: () -> Unit = {},
    onSelectPeriodFilter: (String) -> Unit = {},
    onSelectTransactionFilter: (String) -> Unit = {},
    onOpenWithdrawSheet: () -> Unit = {},
    onCloseWithdrawSheet: () -> Unit = {},
    onOpenAddPayoutAccountSheet: () -> Unit = {},
    onCloseAddPayoutAccountSheet: () -> Unit = {},
    onOpenAllTransactionsSheet: () -> Unit = {},
    onCloseAllTransactionsSheet: () -> Unit = {},
    onOpenPayoutHistorySheet: () -> Unit = {},
    onClosePayoutHistorySheet: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onCloseHelp: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onCloseSettings: () -> Unit = {},
    onOpenSubscriptionsScreen: () -> Unit = {},
    onOpenStarsEarningsScreen: () -> Unit = {},
    onWithdrawSubmit: (amount: Double, pin: String) -> Unit = { _, _ -> },
    onAddPayoutAccountSubmit: (accountType: String, institutionName: String, holderName: String, accountNumber: String, routingOrSwift: String, countryIso: String) -> Unit = { _, _, _, _, _, _ -> },
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
    val purpleAccent = Color(0xFF7C3AED)

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

    val isHidden = uiState.walletBalance?.isHidden == true
    val totalBalanceDisplay = if (isHidden) "••••••" else currencyFormat.format(uiState.walletBalance?.totalBalance ?: 0.0)
    val availableBalanceDisplay = if (isHidden) "••••••" else currencyFormat.format(uiState.walletBalance?.availableBalance ?: 0.0)

    Scaffold(
        modifier = modifier.testTag("wallet_screen_root"),
        containerColor = screenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.wallet_title),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText,
                        modifier = Modifier.testTag("wallet_screen_title")
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("wallet_back_button")
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
                        modifier = Modifier.testTag("wallet_help_button")
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.HelpCircle,
                            contentDescription = stringResource(R.string.wallet_help_title),
                            tint = primaryText,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("wallet_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.wallet_settings_title),
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

                // Status banner
                if (uiState.walletActionSuccessMessage != null || uiState.walletErrorMessage != null) {
                    item {
                        val isSuccess = uiState.walletActionSuccessMessage != null
                        val msg = uiState.walletActionSuccessMessage ?: uiState.walletErrorMessage.orEmpty()
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            border = BorderStroke(1.dp, if (isSuccess) Color(0xFF81C784) else Color(0xFFEF9A9A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wallet_feedback_banner")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = msg,
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

                // 1. Creator Profile Identity Row
                item {
                    CreatorProfileHeaderRow(
                        fullName = ownerFullName,
                        username = ownerUsername,
                        avatarPath = avatarPath,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    )
                }

                // 2. Total Balance Card (Matching Image 1)
                item {
                    TotalBalanceCard(
                        totalBalanceDisplay = totalBalanceDisplay,
                        availableBalanceDisplay = availableBalanceDisplay,
                        isHidden = isHidden,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        onToggleVisibility = onToggleBalanceVisibility,
                        onWithdrawClick = onOpenWithdrawSheet
                    )
                }

                // 3. Earnings Overview Section
                item {
                    WalletEarningsOverviewSection(
                        uiState = uiState,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        currencyFormat = currencyFormat,
                        onSelectPeriodFilter = onSelectPeriodFilter,
                        onOpenStars = onOpenStarsEarningsScreen,
                        onOpenSubscriptions = onOpenSubscriptionsScreen
                    )
                }

                // 4. Transaction History Section
                item {
                    WalletTransactionHistorySection(
                        transactions = uiState.walletTransactions,
                        selectedFilter = uiState.walletTransactionFilter,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        accentRed = accentRed,
                        currencyFormat = currencyFormat,
                        onSelectFilter = onSelectTransactionFilter,
                        onSeeAll = onOpenAllTransactionsSheet
                    )
                }

                // 5. Payouts Section
                item {
                    WalletPayoutsSection(
                        uiState = uiState,
                        payoutAccount = uiState.payoutAccounts.firstOrNull(),
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        accentRed = accentRed,
                        currencyFormat = currencyFormat,
                        onAddPayoutAccount = onOpenAddPayoutAccountSheet,
                        onSeeAllPayouts = onOpenPayoutHistorySheet
                    )
                }

                // 6. Payment Methods Section
                item {
                    WalletPaymentMethodsSection(
                        payoutAccounts = uiState.payoutAccounts,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        accentRed = accentRed,
                        onAddNew = onOpenAddPayoutAccountSheet
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }

    // Modal Subscreens & Sheets
    if (uiState.isWithdrawSheetOpen) {
        WithdrawalSheet(
            availableBalance = uiState.walletBalance?.availableBalance ?: 0.0,
            payoutAccounts = uiState.payoutAccounts,
            currencyFormat = currencyFormat,
            onDismiss = onCloseWithdrawSheet,
            onOpenAddAccount = {
                onCloseWithdrawSheet()
                onOpenAddPayoutAccountSheet()
            },
            onSubmit = onWithdrawSubmit
        )
    }

    if (uiState.isAddPayoutAccountSheetOpen) {
        AddPayoutAccountSheet(
            onDismiss = onCloseAddPayoutAccountSheet,
            onSubmit = onAddPayoutAccountSubmit
        )
    }

    if (uiState.isAllTransactionsSheetOpen) {
        AllTransactionsSheet(
            transactions = uiState.walletTransactions,
            selectedFilter = uiState.walletTransactionFilter,
            currencyFormat = currencyFormat,
            onSelectFilter = onSelectTransactionFilter,
            onDismiss = onCloseAllTransactionsSheet
        )
    }

    if (uiState.isPayoutHistorySheetOpen) {
        AllPayoutsSheet(
            payoutRecords = uiState.payoutRecords,
            currencyFormat = currencyFormat,
            onDismiss = onClosePayoutHistorySheet
        )
    }

    if (uiState.isWalletHelpSheetOpen) {
        WalletHelpDialog(onDismiss = onCloseHelp)
    }

    if (uiState.isWalletSettingsSheetOpen) {
        WalletSettingsDialog(onDismiss = onCloseSettings)
    }
}

// -----------------------------------------------------------------------------
// UI SECTIONS & COMPONENTS (Matching Image 1)
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
            .testTag("wallet_creator_profile_header"),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                    overflow = TextOverflow.Ellipsis
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
                color = secondaryText
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Creator badge pill
            Surface(
                color = Color(0xFFFFECEF),
                shape = RoundedCornerShape(12.dp)
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
private fun TotalBalanceCard(
    totalBalanceDisplay: String,
    availableBalanceDisplay: String,
    isHidden: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    onToggleVisibility: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("total_balance_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Main Row: Left side Total Balance, Center 3D purple wallet asset, Right side Available balance + Withdraw button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Total Balance
                Column(modifier = Modifier.weight(1.1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.wallet_total_balance),
                            fontSize = 13.5.sp,
                            color = secondaryText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onToggleVisibility,
                            modifier = Modifier
                                .size(22.dp)
                                .testTag("wallet_balance_eye_toggle")
                        ) {
                            Icon(
                                imageVector = if (isHidden) MonetizationLucideIcons.EyeOff else MonetizationLucideIcons.Eye,
                                contentDescription = "Toggle Balance Visibility",
                                tint = secondaryText,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = totalBalanceDisplay,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText,
                        modifier = Modifier.testTag("total_balance_value")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.wallet_balance_subtitle),
                        fontSize = 11.5.sp,
                        color = secondaryText,
                        lineHeight = 15.sp
                    )
                }

                // Center 3D Purple Wallet Asset with coins (Matching Image 1)
                Image(
                    painter = painterResource(R.drawable.img_3d_purple_wallet),
                    contentDescription = "Wallet 3D",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .testTag("wallet_3d_asset")
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Right Column: Available Balance & Withdraw Button
                Column(
                    modifier = Modifier.weight(0.9f),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.wallet_available_balance),
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = secondaryText.copy(alpha = 0.6f),
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = availableBalanceDisplay,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Purple "Withdraw" Pill Button (Matching Image 1)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF3E8FF),
                        modifier = Modifier
                            .clickable { onWithdrawClick() }
                            .testTag("wallet_withdraw_button")
                    ) {
                        Text(
                            text = stringResource(R.string.wallet_withdraw_button),
                            color = Color(0xFF7C3AED),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletEarningsOverviewSection(
    uiState: TelefamUiState,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    currencyFormat: NumberFormat,
    onSelectPeriodFilter: (String) -> Unit,
    onOpenStars: () -> Unit,
    onOpenSubscriptions: () -> Unit
) {
    var periodDropdownExpanded by remember { mutableStateOf(false) }
    val periodLabel = when (uiState.walletPeriodFilter) {
        "TODAY" -> stringResource(R.string.wallet_period_today)
        "THIS_WEEK" -> stringResource(R.string.wallet_period_this_week)
        "LAST_30_DAYS" -> stringResource(R.string.wallet_period_last_30_days)
        "ALL_TIME" -> stringResource(R.string.wallet_period_all_time)
        else -> stringResource(R.string.wallet_period_this_month)
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
                text = stringResource(R.string.wallet_earnings_overview),
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
                        .testTag("wallet_period_dropdown_button")
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
                        text = { Text(stringResource(R.string.wallet_period_today)) },
                        onClick = {
                            onSelectPeriodFilter("TODAY")
                            periodDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.wallet_period_this_week)) },
                        onClick = {
                            onSelectPeriodFilter("THIS_WEEK")
                            periodDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.wallet_period_this_month)) },
                        onClick = {
                            onSelectPeriodFilter("THIS_MONTH")
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
                        text = { Text(stringResource(R.string.wallet_period_all_time)) },
                        onClick = {
                            onSelectPeriodFilter("ALL_TIME")
                            periodDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // 4 Earnings Cards: Stars, Subscriptions, Ad Revenue, Other Earnings (Matching Image 1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stars Earnings Card (Yellow/Gold star)
            WalletEarningCategoryCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                },
                title = stringResource(R.string.wallet_stars_earnings),
                amount = currencyFormat.format(uiState.walletStarsEarnings),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                onClick = onOpenStars,
                modifier = Modifier.weight(1f)
            )

            // Subscriptions Card (Purple crown)
            WalletEarningCategoryCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Crown,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                },
                title = stringResource(R.string.wallet_subscriptions),
                amount = currencyFormat.format(uiState.walletSubscriptionEarnings),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                onClick = onOpenSubscriptions,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Ad Revenue Card (Green PlaySquare)
            WalletEarningCategoryCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.PlaySquare,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                title = stringResource(R.string.wallet_ad_revenue),
                amount = currencyFormat.format(uiState.walletAdRevenue),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                onClick = {},
                modifier = Modifier.weight(1f)
            )

            // Other Earnings Card (Blue Gift)
            WalletEarningCategoryCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Gift,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                title = stringResource(R.string.wallet_other_earnings),
                amount = currencyFormat.format(uiState.walletOtherEarnings),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WalletEarningCategoryCard(
    icon: @Composable () -> Unit,
    title: String,
    amount: String,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier
            .clickable { onClick() }
            .testTag("earning_card_$title")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            icon()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = secondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
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

@Composable
private fun WalletTransactionHistorySection(
    transactions: List<WalletTransactionEntity>,
    selectedFilter: String,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    accentRed: Color,
    currencyFormat: NumberFormat,
    onSelectFilter: (String) -> Unit,
    onSeeAll: () -> Unit
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
                text = stringResource(R.string.wallet_transaction_history),
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
                    .clickable { onSeeAll() }
                    .testTag("wallet_transactions_see_all_button")
            )
        }

        // Filter chips: All, Earnings, Payouts, Adjustments (Matching Image 1)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                "ALL" to R.string.wallet_filter_all,
                "EARNINGS" to R.string.wallet_filter_earnings,
                "PAYOUTS" to R.string.wallet_filter_payouts,
                "ADJUSTMENTS" to R.string.wallet_filter_adjustments
            )
            items(filters) { (key, labelRes) ->
                val isSelected = selectedFilter == key
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) Color(0xFFFFEBEE) else cardBackground,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFFEF9A9A) else cardBorder
                    ),
                    modifier = Modifier.clickable { onSelectFilter(key) }
                ) {
                    Text(
                        text = stringResource(labelRes),
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFFE53935) else secondaryText,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Transactions Container
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = cardBackground,
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (transactions.isEmpty()) {
                // Empty state matching Image 1: Receipt illustration with clock + "No transactions yet"
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Receipt,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.wallet_no_transactions),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.wallet_no_transactions_desc),
                        fontSize = 12.sp,
                        color = secondaryText
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    transactions.take(4).forEach { tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (tx.amount >= 0) Color(0xFFDCFCE7) else Color(0xFFFFEDD5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (tx.amount >= 0) MonetizationLucideIcons.CheckCircle else MonetizationLucideIcons.Clock,
                                    contentDescription = null,
                                    tint = if (tx.amount >= 0) Color(0xFF16A34A) else Color(0xFFEA580C),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp,
                                    color = primaryText
                                )
                                Text(
                                    text = "${tx.category} • ${dateFormat.format(Date(tx.createdAtMs))}",
                                    fontSize = 11.5.sp,
                                    color = secondaryText
                                )
                            }

                            Text(
                                text = (if (tx.amount >= 0) "+" else "") + currencyFormat.format(tx.amount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (tx.amount >= 0) Color(0xFF16A34A) else primaryText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletPayoutsSection(
    uiState: TelefamUiState,
    payoutAccount: PayoutAccountEntity?,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    accentRed: Color,
    currencyFormat: NumberFormat,
    onAddPayoutAccount: () -> Unit,
    onSeeAllPayouts: () -> Unit
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
                text = stringResource(R.string.wallet_payouts_title),
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
                    .clickable { onSeeAllPayouts() }
                    .testTag("wallet_payouts_see_all_button")
            )
        }

        // Payout Account Card (Matching Image 1)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = cardBackground,
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
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
                        imageVector = MonetizationLucideIcons.Landmark,
                        contentDescription = null,
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.wallet_payout_account),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = payoutAccount?.institutionName ?: stringResource(R.string.wallet_no_payout_account_linked),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = secondaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = payoutAccount?.accountNumberMasked ?: stringResource(R.string.wallet_payout_account_prompt),
                        fontSize = 11.5.sp,
                        color = secondaryText,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Purple pill button: "Add Payout Account" or "Edit"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF3E8FF),
                    modifier = Modifier
                        .clickable { onAddPayoutAccount() }
                        .testTag("add_payout_account_action_button")
                ) {
                    Text(
                        text = if (payoutAccount == null) stringResource(R.string.wallet_add_payout_account) else "Manage",
                        color = Color(0xFF7C3AED),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // 4 Payout Metric Cards in 2x2 Layout (Matching Image 1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Total Payouts (Blue Dollar)
            PayoutMetricCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0284C7)
                        )
                    }
                },
                title = stringResource(R.string.wallet_total_payouts),
                amount = currencyFormat.format(uiState.totalPayoutsAllTime),
                subtitle = stringResource(R.string.wallet_all_time),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                modifier = Modifier.weight(1f)
            )

            // Pending Payouts (Orange Clock)
            PayoutMetricCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEDD5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Clock,
                            contentDescription = null,
                            tint = Color(0xFFEA580C),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                },
                title = stringResource(R.string.wallet_pending_payouts),
                amount = currencyFormat.format(uiState.pendingPayoutsAmount),
                subtitle = stringResource(R.string.wallet_transactions_count, uiState.pendingPayoutsCount),
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
            // Paid Payouts (Green CheckCircle)
            PayoutMetricCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                },
                title = stringResource(R.string.wallet_paid_payouts),
                amount = currencyFormat.format(uiState.paidPayoutsAmount),
                subtitle = stringResource(R.string.wallet_transactions_count, uiState.paidPayoutsCount),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                modifier = Modifier.weight(1f)
            )

            // Failed Payouts (Red XCircle)
            PayoutMetricCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE4E6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.XCircle,
                            contentDescription = null,
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                },
                title = stringResource(R.string.wallet_failed_payouts),
                amount = currencyFormat.format(uiState.failedPayoutsAmount),
                subtitle = stringResource(R.string.wallet_transactions_count, uiState.failedPayoutsCount),
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                primaryText = primaryText,
                secondaryText = secondaryText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PayoutMetricCard(
    icon: @Composable () -> Unit,
    title: String,
    amount: String,
    subtitle: String,
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
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = secondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = secondaryText
            )
        }
    }
}

@Composable
private fun WalletPaymentMethodsSection(
    payoutAccounts: List<PayoutAccountEntity>,
    cardBackground: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    accentRed: Color,
    onAddNew: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header with "+ Add New"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.wallet_payment_methods),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText
            )

            Text(
                text = stringResource(R.string.wallet_add_new),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentRed,
                modifier = Modifier
                    .clickable { onAddNew() }
                    .testTag("wallet_payment_methods_add_new_button")
            )
        }

        // Bank Account Card (Matching Image 1)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = cardBackground,
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddNew() }
                .testTag("wallet_bank_account_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MonetizationLucideIcons.Landmark,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.wallet_bank_account),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = payoutAccounts.firstOrNull()?.let { "${it.institutionName} • ${it.accountNumberMasked}" }
                            ?: stringResource(R.string.wallet_no_account_added),
                        fontSize = 12.sp,
                        color = secondaryText
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

// -----------------------------------------------------------------------------
// MODALS & BOTTOM SHEETS (Withdraw, Add Account, Transactions, Payouts)
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WithdrawalSheet(
    availableBalance: Double,
    payoutAccounts: List<PayoutAccountEntity>,
    currencyFormat: NumberFormat,
    onDismiss: () -> Unit,
    onOpenAddAccount: () -> Unit,
    onSubmit: (amount: Double, pin: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountText by remember { mutableStateOf("") }
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val amountVal = amountText.toDoubleOrNull() ?: 0.0
    val feeVal = (amountVal * 0.02).coerceAtLeast(0.0) // 2% fee
    val netPayoutVal = (amountVal - feeVal).coerceAtLeast(0.0)

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
                text = stringResource(R.string.wallet_withdraw_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Available Balance display
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF3E8FF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Available Balance", fontSize = 13.sp, color = Color(0xFF6B21A8))
                    Text(
                        text = currencyFormat.format(availableBalance),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF581C87)
                    )
                }
            }

            if (payoutAccounts.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "No payout account configured yet.",
                            fontSize = 13.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = onOpenAddAccount,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Link Payout Account Now")
                        }
                    }
                }
            } else {
                Text(
                    text = "Payout Destination: ${payoutAccounts.first().institutionName} (${payoutAccounts.first().accountNumberMasked})",
                    fontSize = 12.5.sp,
                    color = Color.Gray
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = Color.Red,
                    fontSize = 12.5.sp
                )
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount to withdraw ($)") },
                placeholder = { Text("min $10.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("withdraw_amount_input"),
                singleLine = true
            )

            // Fee and Net Payout calculation
            if (amountVal > 0.0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.wallet_fee_breakdown, currencyFormat.format(feeVal)),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = stringResource(R.string.wallet_net_payout, currencyFormat.format(netPayoutVal)),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                }
            }

            OutlinedTextField(
                value = pinText,
                onValueChange = { if (it.length <= 4) pinText = it },
                label = { Text(stringResource(R.string.wallet_security_pin_hint)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("withdraw_pin_input"),
                singleLine = true
            )

            Button(
                onClick = {
                    if (payoutAccounts.isEmpty()) {
                        errorMessage = "Please link a payout account first"
                        return@Button
                    }
                    if (amountVal < 10.0) {
                        errorMessage = "Minimum withdrawal amount is $10.00"
                        return@Button
                    }
                    if (amountVal > availableBalance) {
                        errorMessage = "Insufficient available balance"
                        return@Button
                    }
                    if (pinText.length < 4) {
                        errorMessage = "Please enter 4-digit security PIN"
                        return@Button
                    }
                    onSubmit(amountVal, pinText)
                },
                enabled = payoutAccounts.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_withdraw_submit_button")
            ) {
                Text(
                    text = stringResource(R.string.wallet_confirm_withdraw),
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
private fun AddPayoutAccountSheet(
    onDismiss: () -> Unit,
    onSubmit: (type: String, instName: String, holder: String, accNum: String, routing: String, country: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var accountType by remember { mutableStateOf("BANK_ACCOUNT") }
    var holderName by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var routingSwift by remember { mutableStateOf("") }
    var countryIso by remember { mutableStateOf("US") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.wallet_add_payout_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Account Type Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "BANK_ACCOUNT" to "Bank Account",
                    "MPESA" to "M-Pesa",
                    "PAYPAL" to "PayPal"
                ).forEach { (typeKey, label) ->
                    val isSelected = accountType == typeKey
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFFEDE9FE) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF7C3AED) else Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { accountType = typeKey }
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF7C3AED) else Color(0xFF475569),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            OutlinedTextField(
                value = holderName,
                onValueChange = { holderName = it },
                label = { Text(stringResource(R.string.wallet_account_holder_name)) },
                placeholder = { Text("e.g. John Doe") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("payout_holder_input")
            )

            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text(stringResource(R.string.wallet_bank_name)) },
                placeholder = { Text(if (accountType == "MPESA") "Safaricom M-Pesa" else "e.g. Chase Bank / Barclays") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("payout_bank_input")
            )

            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = { Text(stringResource(R.string.wallet_account_number)) },
                placeholder = { Text(if (accountType == "MPESA") "+254712345678" else "IBAN / Account Number") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("payout_acc_number_input")
            )

            if (accountType == "BANK_ACCOUNT") {
                OutlinedTextField(
                    value = routingSwift,
                    onValueChange = { routingSwift = it },
                    label = { Text(stringResource(R.string.wallet_routing_swift)) },
                    placeholder = { Text("SWIFT / BIC / ABA Routing") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payout_swift_input")
                )
            }

            // AES-256 GCM Security Assurance badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = MonetizationLucideIcons.ShieldCheck,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Your sensitive financial details are encrypted on-device via hardware-backed AES-256-GCM before storage.",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )
                }
            }

            Button(
                onClick = {
                    if (holderName.trim().isBlank()) {
                        errorMessage = "Please enter account holder name"
                        return@Button
                    }
                    if (bankName.trim().isBlank()) {
                        errorMessage = "Please enter bank or provider name"
                        return@Button
                    }
                    if (accountNumber.trim().length < 5) {
                        errorMessage = "Please enter a valid account number / identifier"
                        return@Button
                    }
                    onSubmit(accountType, bankName.trim(), holderName.trim(), accountNumber.trim(), routingSwift.trim(), countryIso)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_payout_account_submit_button")
            ) {
                Text(
                    text = stringResource(R.string.wallet_save_account),
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
private fun AllTransactionsSheet(
    transactions: List<WalletTransactionEntity>,
    selectedFilter: String,
    currencyFormat: NumberFormat,
    onSelectFilter: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val filtered = remember(transactions, selectedFilter) {
        if (selectedFilter == "ALL") transactions else transactions.filter { it.type.equals(selectedFilter, ignoreCase = true) }
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
                text = stringResource(R.string.wallet_transaction_history),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "ALL" to R.string.wallet_filter_all,
                    "EARNINGS" to R.string.wallet_filter_earnings,
                    "PAYOUTS" to R.string.wallet_filter_payouts,
                    "ADJUSTMENTS" to R.string.wallet_filter_adjustments
                ).forEach { (key, labelRes) ->
                    val isSelected = selectedFilter == key
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFFFFEBEE) else Color(0xFFF1F5F9),
                            modifier = Modifier.clickable { onSelectFilter(key) }
                        ) {
                            Text(
                                text = stringResource(labelRes),
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFFE53935) else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                Text(
                    text = "No transactions found in this filter.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                val dateFormat = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault())
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.transactionId }) { tx ->
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = tx.title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                    Text(
                                        text = "${tx.category} • ${dateFormat.format(Date(tx.createdAtMs))}",
                                        fontSize = 11.5.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = (if (tx.amount >= 0) "+" else "") + currencyFormat.format(tx.amount),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (tx.amount >= 0) Color(0xFF16A34A) else Color(0xFFDC2626)
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
private fun AllPayoutsSheet(
    payoutRecords: List<PayoutRecordEntity>,
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "All Payout Records",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (payoutRecords.isEmpty()) {
                Text(
                    text = "No payout records found.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                val dateFormat = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault())
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(payoutRecords, key = { it.payoutId }) { record ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${record.institutionName} (${record.accountMasked})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                    Text(
                                        text = currencyFormat.format(record.netAmount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (record.status == "PAID") Color(0xFF16A34A) else Color(0xFFEA580C)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Ref: ${record.referenceCode} • ${dateFormat.format(Date(record.requestedAtMs))}",
                                        fontSize = 11.5.sp,
                                        color = Color.Gray
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (record.status) {
                                            "PAID" -> Color(0xFFDCFCE7)
                                            "PENDING" -> Color(0xFFFFEDD5)
                                            else -> Color(0xFFFFE4E6)
                                        }
                                    ) {
                                        Text(
                                            text = record.status,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (record.status) {
                                                "PAID" -> Color(0xFF16A34A)
                                                "PENDING" -> Color(0xFFEA580C)
                                                else -> Color(0xFFE11D48)
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
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
private fun WalletHelpDialog(onDismiss: () -> Unit) {
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
                    text = stringResource(R.string.wallet_help_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.wallet_help_desc),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "• Withdrawals have a minimum limit of $10.00.\n" +
                            "• Payouts to linked bank or mobile money accounts are processed securely.\n" +
                            "• A standard 2% processing fee applies to withdrawals to cover payment gateway charges.\n" +
                            "• Stars, subscriptions, and ad revenue are aggregated automatically in your total balance.",
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

@Composable
private fun WalletSettingsDialog(onDismiss: () -> Unit) {
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
                    text = stringResource(R.string.wallet_settings_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Default Payout Currency: USD ($)\n" +
                            "• Security Verification: Biometric & 4-digit PIN required on withdrawals\n" +
                            "• Encryption Standard: Hardware-backed AES-256-GCM\n" +
                            "• Notification alerts: Instant email and in-app alert when payouts are dispatched",
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}
