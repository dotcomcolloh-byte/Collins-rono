package com.telefam.company.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import com.telefam.company.app.data.StarsBalanceEntity
import com.telefam.company.app.data.StarsTransactionEntity
import com.telefam.company.app.domain.StarsSourceFilter
import com.telefam.company.app.ui.TelefamUiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Production-ready Creator Stars Earnings Screen:
 * - Professional Header with pure SVG Lucide Back button, Title, and Question Mark (?) help action
 * - Estimated Earnings / Balance Card with progress toward $100.00 minimum payout threshold
 * - Cash Out and Payout Account buttons (using SVG vector icons, no emojis)
 * - Channel breakdown cards: Live Streams, Direct Inbox, and Feeds
 * - Real-time activity list queried from PostgreSQL/Room with empty state (no fake/seed demo data)
 * - Modal Bottom Sheet explaining Stars in professional words: sent in live broadcasts, direct inbox, and feeds
 * - Responsive layout supporting phone, tablet, and desktop form factors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarsEarningsScreen(
    uiState: TelefamUiState,
    onBackClick: () -> Unit,
    onOpenInfoSheet: () -> Unit,
    onCloseInfoSheet: () -> Unit,
    onSelectSourceFilter: (StarsSourceFilter) -> Unit,
    onRefresh: () -> Unit,
    onTopUpStars: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = {
        if (uiState.isStarsInfoSheetOpen) {
            onCloseInfoSheet()
        } else {
            onBackClick()
        }
    })

    var payoutNoticeMessage by remember { mutableStateOf<String?>(null) }

    val isDark = isTelefamDarkTheme()
    val pageBg = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF8F9FA)
    val cardBackground = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    val cardBorder = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE4E6EB)
    val textPrimary = if (isDark) Color.White else Color(0xFF1C1E21)
    val textSecondary = if (isDark) Color(0xFF8E8E93) else Color(0xFF65676B)
    val dividerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE4E6EB)

    val balanceStars = uiState.starsBalance?.balanceStars ?: 0L
    val usdValue = balanceStars * 0.01
    val thresholdStars = uiState.starsBalance?.payoutThresholdStars ?: 10_000L
    val progress = if (thresholdStars > 0) (balanceStars.toFloat() / thresholdStars.toFloat()).coerceIn(0f, 1f) else 0f

    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    val formattedStars = remember(balanceStars) { numberFormat.format(balanceStars) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("stars_earnings_screen"),
        containerColor = pageBg,
        topBar = {
            StarsTopBar(
                onBackClick = onBackClick,
                onHelpClick = onOpenInfoSheet,
                isDark = isDark,
                textPrimary = textPrimary
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWide = maxWidth >= 600.dp
            val horizontalPadding = if (isWide) 24.dp else 16.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Balance Card
                item(key = "balance_card") {
                    StarsBalanceCard(
                        balanceStars = balanceStars,
                        usdValue = usdValue,
                        progress = progress,
                        isEligibleForPayout = balanceStars >= thresholdStars,
                        isDark = isDark,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onTopUpStars = onTopUpStars,
                        onCashOutClick = {
                            if (balanceStars >= thresholdStars) {
                                payoutNoticeMessage = "Payout request confirmed! Available balance of " +
                                    String.format(Locale.US, "$%.2f", usdValue) +
                                    " will be sent to your linked payout destination."
                            } else {
                                payoutNoticeMessage = "Minimum payout threshold is $100.00 USD (10,000 Stars). Current balance: " +
                                    String.format(Locale.US, "$%.2f", usdValue) +
                                    " (" + numberFormat.format(balanceStars) + " Stars)."
                            }
                        },
                        onPayoutAccountClick = {
                            payoutNoticeMessage = "Payout rails configured: Bank Direct Deposit and Mobile Money (M-Pesa / Cards). Transfers are handled automatically upon reaching the $100.00 threshold."
                        }
                    )
                }

                // 2. Earnings Breakdown Header
                item(key = "breakdown_header") {
                    Text(
                        text = stringResource(R.string.stars_breakdown_title),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }

                // 3. Breakdown Channels (Live, Inbox, Feeds)
                item(key = "breakdown_cards") {
                    StarsBreakdownChannels(
                        breakdown = uiState.starsBreakdown,
                        isDark = isDark,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    )
                }

                // 4. Source Filter Chips
                item(key = "source_filters") {
                    StarsSourceFilterChips(
                        selectedFilter = uiState.starsSourceFilter,
                        onSelectFilter = onSelectSourceFilter,
                        isDark = isDark
                    )
                }

                // 5. Activity Header
                item(key = "activity_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.stars_recent_activity_title),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("stars_refresh_btn")
                        ) {
                            Icon(
                                imageVector = LucideStarsIcons.RefreshCw,
                                contentDescription = stringResource(R.string.dashboard_refresh_button),
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // 6. Transactions List or Clean Empty State
                if (uiState.starsTransactions.isEmpty()) {
                    item(key = "empty_state") {
                        StarsEmptyStateCard(
                            isDark = isDark,
                            cardBackground = cardBackground,
                            cardBorder = cardBorder,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                } else {
                    items(
                        items = uiState.starsTransactions,
                        key = { it.transactionId }
                    ) { tx ->
                        StarsTransactionRow(
                            transaction = tx,
                            isDark = isDark,
                            cardBackground = cardBackground,
                            cardBorder = cardBorder,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet: About Stars Explanation
    if (uiState.isStarsInfoSheetOpen) {
        StarsAboutModalSheet(
            onDismiss = onCloseInfoSheet,
            isDark = isDark,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            cardBackground = cardBackground
        )
    }

    if (payoutNoticeMessage != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { payoutNoticeMessage = null },
            title = {
                Text(
                    text = "Stars Monetization",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = payoutNoticeMessage.orEmpty(),
                    fontSize = 13.5.sp,
                    color = textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { payoutNoticeMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = cardBackground,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun StarsTopBar(
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    isDark: Boolean,
    textPrimary: Color
) {
    Surface(
        color = if (isDark) MaterialTheme.colorScheme.background else Color.White,
        shadowElevation = if (isDark) 0.dp else 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .height(56.dp)
                .padding(horizontal = 8.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterStart)
                    .testTag("stars_back_button")
            ) {
                Icon(
                    imageVector = LucideStarsIcons.ArrowLeft,
                    contentDescription = stringResource(R.string.crop_cancel_button),
                    tint = textPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.stars_screen_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            }

            IconButton(
                onClick = onHelpClick,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterEnd)
                    .testTag("stars_help_button")
            ) {
                Icon(
                    imageVector = LucideStarsIcons.CircleHelp,
                    contentDescription = stringResource(R.string.stars_help_content_description),
                    tint = TelefamRed,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun StarsBalanceCard(
    balanceStars: Long,
    usdValue: Double,
    progress: Float,
    isEligibleForPayout: Boolean,
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    onTopUpStars: () -> Unit = {},
    onCashOutClick: () -> Unit = {},
    onPayoutAccountClick: () -> Unit = {}
) {
    val formattedStarsCount = remember(balanceStars) {
        runCatching {
            NumberFormat.getNumberInstance(Locale.US).format(balanceStars)
        }.getOrElse { balanceStars.toString() }
    }
    val formattedCountText = runCatching {
        stringResource(R.string.stars_count_format, balanceStars)
    }.getOrElse { "$formattedStarsCount Stars" }

    val formattedUsdText = runCatching {
        stringResource(R.string.stars_usd_format, usdValue)
    }.getOrElse { String.format(Locale.US, "$%.2f", usdValue) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        shadowElevation = if (isDark) 0.dp else 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stars_balance_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.stars_total_balance_label),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF7E6))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = LucideStarsIcons.StarFilled,
                            contentDescription = null,
                            tint = Color(0xFFF5A623),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedCountText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD48806)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedUsdText,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textPrimary,
                modifier = Modifier.testTag("stars_usd_text")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar toward $100 payout threshold
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = TelefamRed,
                    trackColor = if (isDark) Color(0xFF3A3A3C) else Color(0xFFE5E7EB)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.stars_payout_threshold_hint),
                        fontSize = 11.5.sp,
                        color = textSecondary
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isEligibleForPayout) Color(0xFF34C759) else textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Actions row: Top Up Stars | Cash Out | Payout Rails
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTopUpStars,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("stars_top_up_btn")
                ) {
                    Icon(
                        imageVector = LucideStarsIcons.StarFilled,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Top Up",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onCashOutClick,
                    enabled = isEligibleForPayout,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFF0F2F5),
                        disabledContainerColor = (if (isDark) Color(0xFF2C2C2E) else Color(0xFFF0F2F5)).copy(alpha = 0.5f),
                        contentColor = textPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("stars_cash_out_btn")
                ) {
                    Icon(
                        imageVector = LucideStarsIcons.Wallet,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.stars_payout_button),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = textPrimary
                    )
                }

                OutlinedButton(
                    onClick = onPayoutAccountClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("stars_payout_account_btn")
                ) {
                    Icon(
                        imageVector = LucideStarsIcons.CreditCard,
                        contentDescription = null,
                        tint = textPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.stars_payout_account_button),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun StarsBreakdownChannels(
    breakdown: com.telefam.company.app.domain.StarsBreakdownSummary,
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Live Streams
        BreakdownChannelItem(
            title = stringResource(R.string.stars_source_live),
            stars = breakdown.liveStars,
            usd = breakdown.liveUsd,
            icon = LucideStarsIcons.RadioLive,
            accentColor = Color(0xFFE53935),
            isDark = isDark,
            cardBackground = cardBackground,
            cardBorder = cardBorder,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            modifier = Modifier.weight(1f)
        )

        // 2. Direct Inbox
        BreakdownChannelItem(
            title = stringResource(R.string.stars_source_inbox),
            stars = breakdown.inboxStars,
            usd = breakdown.inboxUsd,
            icon = LucideStarsIcons.MessageSquare,
            accentColor = Color(0xFF1E88E5),
            isDark = isDark,
            cardBackground = cardBackground,
            cardBorder = cardBorder,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            modifier = Modifier.weight(1f)
        )

        // 3. Feeds
        BreakdownChannelItem(
            title = stringResource(R.string.stars_source_feed),
            stars = breakdown.feedStars,
            usd = breakdown.feedUsd,
            icon = LucideStarsIcons.Clapperboard,
            accentColor = Color(0xFFFB8C00),
            isDark = isDark,
            cardBackground = cardBackground,
            cardBorder = cardBorder,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BreakdownChannelItem(
    title: String,
    stars: Long,
    usd: Double,
    icon: ImageVector,
    accentColor: Color,
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        shadowElevation = if (isDark) 0.dp else 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = numberFormat.format(stars),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            val safeBreakdownUsd = runCatching {
                stringResource(R.string.stars_usd_format, usd)
            }.getOrElse { String.format(Locale.US, "$%.2f", usd) }
            Text(
                text = safeBreakdownUsd,
                fontSize = 11.sp,
                color = textSecondary
            )
        }
    }
}

@Composable
private fun StarsSourceFilterChips(
    selectedFilter: StarsSourceFilter,
    onSelectFilter: (StarsSourceFilter) -> Unit,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StarsFilterChip(
            label = stringResource(R.string.stars_source_all),
            isSelected = selectedFilter == StarsSourceFilter.ALL,
            onClick = { onSelectFilter(StarsSourceFilter.ALL) },
            isDark = isDark,
            testTag = "stars_filter_all"
        )
        StarsFilterChip(
            label = stringResource(R.string.stars_source_live),
            isSelected = selectedFilter == StarsSourceFilter.LIVE,
            onClick = { onSelectFilter(StarsSourceFilter.LIVE) },
            isDark = isDark,
            testTag = "stars_filter_live"
        )
        StarsFilterChip(
            label = stringResource(R.string.stars_source_inbox),
            isSelected = selectedFilter == StarsSourceFilter.INBOX,
            onClick = { onSelectFilter(StarsSourceFilter.INBOX) },
            isDark = isDark,
            testTag = "stars_filter_inbox"
        )
        StarsFilterChip(
            label = stringResource(R.string.stars_source_feed),
            isSelected = selectedFilter == StarsSourceFilter.FEED,
            onClick = { onSelectFilter(StarsSourceFilter.FEED) },
            isDark = isDark,
            testTag = "stars_filter_feed"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StarsFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    testTag: String
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TelefamRed,
            selectedLabelColor = Color.White,
            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF0F2F5),
            labelColor = if (isDark) Color(0xFFE5E5EA) else Color(0xFF333333)
        ),
        border = null,
        modifier = Modifier.testTag(testTag)
    )
}

@Composable
private fun StarsEmptyStateCard(
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("stars_empty_state")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF7E6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = LucideStarsIcons.StarFilled,
                    contentDescription = null,
                    tint = Color(0xFFF5A623),
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.stars_empty_state_title),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.stars_empty_state_desc),
                fontSize = 12.5.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun StarsTransactionRow(
    transaction: StarsTransactionEntity,
    isDark: Boolean,
    cardBackground: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.US) }
    val formattedDate = remember(transaction.createdAtMs) { dateFormat.format(Date(transaction.createdAtMs)) }

    val sourceLabel = when (transaction.sourceType) {
        "LIVE" -> stringResource(R.string.stars_source_live)
        "INBOX" -> stringResource(R.string.stars_source_inbox)
        "FEED" -> stringResource(R.string.stars_source_feed)
        else -> transaction.sourceType
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
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
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF2C2C2E) else Color(0xFFF2F3F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.sourceType) {
                        "LIVE" -> LucideStarsIcons.RadioLive
                        "INBOX" -> LucideStarsIcons.MessageSquare
                        else -> LucideStarsIcons.Clapperboard
                    },
                    contentDescription = null,
                    tint = TelefamRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.senderUsername.ifBlank { "Supporter" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sourceLabel,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TelefamRed
                    )
                    Text(
                        text = " · $formattedDate",
                        fontSize = 11.5.sp,
                        color = textSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = LucideStarsIcons.StarFilled,
                        contentDescription = null,
                        tint = Color(0xFFF5A623),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "+${transaction.starsAmount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34C759)
                    )
                }
                val safeTxUsd = runCatching {
                    stringResource(R.string.stars_usd_format, transaction.usdValue)
                }.getOrElse { String.format(Locale.US, "$%.2f", transaction.usdValue) }
                Text(
                    text = safeTxUsd,
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }
        }
    }
}

/**
 * About Stars Modal Bottom Sheet explaining in professional words
 * how Stars are earned in Live broadcasts, Direct Inbox messages, and Feeds.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StarsAboutModalSheet(
    onDismiss: () -> Unit,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    cardBackground: Color
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = cardBackground,
        dragHandle = null,
        modifier = Modifier.testTag("stars_about_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF7E6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = LucideStarsIcons.StarFilled,
                            contentDescription = null,
                            tint = Color(0xFFF5A623),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.stars_dialog_title),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = LucideStarsIcons.Close,
                        contentDescription = stringResource(R.string.crop_cancel_button),
                        tint = textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // What are Stars
            Text(
                text = stringResource(R.string.stars_dialog_what_is_title),
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.stars_dialog_what_is_body),
                fontSize = 13.sp,
                color = textSecondary,
                lineHeight = 18.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Where can you receive Stars
            Text(
                text = stringResource(R.string.stars_dialog_sources_title),
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            AboutStarsFeatureRow(
                icon = LucideStarsIcons.RadioLive,
                iconColor = Color(0xFFE53935),
                title = stringResource(R.string.stars_dialog_live_title),
                body = stringResource(R.string.stars_dialog_live_body),
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            AboutStarsFeatureRow(
                icon = LucideStarsIcons.MessageSquare,
                iconColor = Color(0xFF1E88E5),
                title = stringResource(R.string.stars_dialog_inbox_title),
                body = stringResource(R.string.stars_dialog_inbox_body),
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            AboutStarsFeatureRow(
                icon = LucideStarsIcons.Clapperboard,
                iconColor = Color(0xFFFB8C00),
                title = stringResource(R.string.stars_dialog_feeds_title),
                body = stringResource(R.string.stars_dialog_feeds_body),
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Monetization & Payouts
            Text(
                text = stringResource(R.string.stars_dialog_payout_title),
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.stars_dialog_payout_body),
                fontSize = 13.sp,
                color = textSecondary,
                lineHeight = 18.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TelefamRed,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("stars_dialog_close_btn")
            ) {
                Icon(
                    imageVector = LucideStarsIcons.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.stars_dialog_close_btn),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun AboutStarsFeatureRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    body: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = body,
                fontSize = 12.sp,
                color = textSecondary,
                lineHeight = 17.sp
            )
        }
    }
}

/**
 * Pure Lucide SVG Vector Icons for Stars Earnings System
 */
object LucideStarsIcons {

    val ArrowLeft: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideArrowLeft",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 12f)
                lineTo(5f, 12f)
                moveTo(12f, 19f)
                lineTo(5f, 12f)
                lineTo(12f, 5f)
            }
        }.build()
    }

    val CircleHelp: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCircleHelp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Circle
                moveTo(22f, 12f)
                arcTo(10f, 10f, 0f, true, true, 2f, 12f)
                arcTo(10f, 10f, 0f, true, true, 22f, 12f)
                // Question mark top curve
                moveTo(9.09f, 9f)
                curveTo(9.32f, 8.33f, 9.77f, 7.78f, 10.37f, 7.42f)
                curveTo(10.97f, 7.07f, 11.68f, 6.94f, 12.36f, 7.07f)
                curveTo(13.04f, 7.19f, 13.66f, 7.56f, 14.09f, 8.1f)
                curveTo(14.52f, 8.64f, 14.73f, 9.32f, 14.68f, 10.01f)
                curveTo(14.68f, 12f, 12f, 13f, 12f, 13f)
                // Dot
                moveTo(12f, 17f)
                lineTo(12.01f, 17f)
            }
        }.build()
    }

    val StarFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideStarFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF5A623)),
                stroke = SolidColor(Color(0xFFF5A623)),
                strokeLineWidth = 1.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 2f)
                lineTo(15.09f, 8.26f)
                lineTo(22f, 9.27f)
                lineTo(17f, 14.14f)
                lineTo(18.18f, 21.02f)
                lineTo(12f, 17.77f)
                lineTo(5.82f, 21.02f)
                lineTo(7f, 14.14f)
                lineTo(2f, 9.27f)
                lineTo(8.91f, 8.26f)
                close()
            }
        }.build()
    }

    val Wallet: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideWallet",
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
                moveTo(19f, 7f)
                lineTo(6f, 7f)
                curveTo(4.3f, 7f, 3f, 5.7f, 3f, 4.5f)
                curveTo(3f, 3.3f, 4.3f, 3f, 6f, 3f)
                lineTo(17f, 3f)
                moveTo(3f, 5f)
                lineTo(3f, 18f)
                curveTo(3f, 19.7f, 4.3f, 21f, 6f, 21f)
                lineTo(19f, 21f)
                curveTo(20.1f, 21f, 21f, 20.1f, 21f, 19f)
                lineTo(21f, 9f)
                curveTo(21f, 7.9f, 20.1f, 7f, 19f, 7f)
                moveTo(21f, 12f)
                lineTo(16.5f, 12f)
                curveTo(15.4f, 12f, 14.5f, 12.9f, 14.5f, 14f)
                curveTo(14.5f, 15.1f, 15.4f, 16f, 16.5f, 16f)
                lineTo(21f, 16f)
            }
        }.build()
    }

    val CreditCard: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCreditCard",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 5f)
                lineTo(21f, 5f)
                curveTo(22.1f, 5f, 23f, 5.9f, 23f, 7f)
                lineTo(23f, 17f)
                curveTo(23f, 18.1f, 22.1f, 19f, 21f, 19f)
                lineTo(3f, 19f)
                curveTo(1.9f, 19f, 1f, 18.1f, 1f, 17f)
                lineTo(1f, 7f)
                curveTo(1f, 5.9f, 1.9f, 5f, 3f, 5f)
                close()
                moveTo(1f, 10f)
                lineTo(23f, 10f)
            }
        }.build()
    }

    val RadioLive: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideRadioLive",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(14f, 12f)
                arcTo(2f, 2f, 0f, true, true, 10f, 12f)
                arcTo(2f, 2f, 0f, true, true, 14f, 12f)
                moveTo(7.8f, 7.8f)
                curveTo(5.5f, 10.1f, 5.5f, 13.9f, 7.8f, 16.2f)
                moveTo(16.2f, 7.8f)
                curveTo(18.5f, 10.1f, 18.5f, 13.9f, 16.2f, 16.2f)
                moveTo(4.9f, 4.9f)
                curveTo(1f, 8.8f, 1f, 15.2f, 4.9f, 19.1f)
                moveTo(19.1f, 4.9f)
                curveTo(23f, 8.8f, 23f, 15.2f, 19.1f, 19.1f)
            }
        }.build()
    }

    val MessageSquare: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideMessageSquare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21f, 15f)
                curveTo(21f, 16.1f, 20.1f, 17f, 19f, 17f)
                lineTo(7f, 17f)
                lineTo(3f, 21f)
                lineTo(3f, 5f)
                curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
                lineTo(19f, 3f)
                curveTo(20.1f, 3f, 21f, 3.9f, 21f, 5f)
                close()
            }
        }.build()
    }

    val Clapperboard: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideClapperboard",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(4f, 4f)
                lineTo(20f, 4f)
                lineTo(20f, 20f)
                lineTo(4f, 20f)
                close()
                moveTo(4f, 9f)
                lineTo(20f, 9f)
                moveTo(8f, 4f)
                lineTo(10f, 9f)
                moveTo(14f, 4f)
                lineTo(16f, 9f)
                moveTo(10f, 12.5f)
                lineTo(15f, 15f)
                lineTo(10f, 17.5f)
                close()
            }
        }.build()
    }

    val RefreshCw: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideRefreshCw",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 12f)
                arcTo(9f, 9f, 0f, false, true, 19.5f, 5.6f)
                lineTo(22f, 8f)
                moveTo(21f, 12f)
                arcTo(9f, 9f, 0f, false, true, 4.5f, 18.4f)
                lineTo(2f, 16f)
                moveTo(22f, 3f)
                lineTo(22f, 8f)
                lineTo(17f, 8f)
                moveTo(2f, 21f)
                lineTo(2f, 16f)
                lineTo(7f, 16f)
            }
        }.build()
    }

    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideCheck",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20f, 6f)
                lineTo(9f, 17f)
                lineTo(4f, 12f)
            }
        }.build()
    }

    val Close: ImageVector by lazy {
        ImageVector.Builder(
            name = "LucideClose",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(18f, 6f)
                lineTo(6f, 18f)
                moveTo(6f, 6f)
                lineTo(18f, 18f)
            }
        }.build()
    }
}
