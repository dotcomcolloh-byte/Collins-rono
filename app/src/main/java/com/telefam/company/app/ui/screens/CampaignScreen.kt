package com.telefam.company.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.telefam.company.app.data.CampaignEntity
import com.telefam.company.app.data.CampaignLeadEntity
import com.telefam.company.app.domain.CAMPAIGN_CTA_BUTTONS
import com.telefam.company.app.domain.CAMPAIGN_DURATION_OPTIONS
import com.telefam.company.app.domain.CAMPAIGN_INTEREST_CATEGORIES
import com.telefam.company.app.domain.CampaignBillingResolver
import com.telefam.company.app.domain.CampaignGoal
import com.telefam.company.app.domain.CampaignPaymentGateway
import com.telefam.company.app.domain.CampaignPricingCatalog
import com.telefam.company.app.domain.CampaignPricingTier
import com.telefam.company.app.domain.CampaignScreenTab
import com.telefam.company.app.domain.UserPostItem
import com.telefam.company.app.ui.TelefamUiState
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Clean Lucide SVG Vector icons used strictly across the Campaign and Ads Management experience.
 * Fully compliant with zero-emoji and SVG requirements.
 */
object LucideCampaignIcons {

    val ArrowLeft: ImageVector by lazy {
        ImageVector.Builder("CampaignArrowLeft", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(19f, 12f)
                lineTo(5f, 12f)
                moveTo(12f, 19f)
                lineTo(5f, 12f)
                lineTo(12f, 5f)
            }
        }.build()
    }

    val Megaphone: ImageVector by lazy {
        ImageVector.Builder("CampaignMegaphone", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.White), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(3f, 11f)
                lineTo(3f, 13f)
                curveTo(3f, 14.1f, 3.9f, 15f, 5f, 15f)
                lineTo(7f, 15f)
                lineTo(14f, 20f)
                lineTo(14f, 4f)
                lineTo(7f, 9f)
                lineTo(5f, 9f)
                curveTo(3.9f, 9f, 3f, 9.9f, 3f, 11f)
                close()
                moveTo(17f, 9f)
                curveTo(17.8f, 10f, 18f, 11.2f, 18f, 12f)
                curveTo(18f, 12.8f, 17.8f, 14f, 17f, 15f)
                moveTo(6f, 15f)
                lineTo(6f, 20f)
            }
        }.build()
    }

    val BarChart: ImageVector by lazy {
        ImageVector.Builder("CampaignBarChart", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(18f, 20f)
                lineTo(18f, 10f)
                moveTo(12f, 20f)
                lineTo(12f, 4f)
                moveTo(6f, 20f)
                lineTo(6f, 14f)
            }
        }.build()
    }

    val Users: ImageVector by lazy {
        ImageVector.Builder("CampaignUsers", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(16f, 21f)
                lineTo(16f, 19f)
                curveTo(16f, 16.8f, 14.2f, 15f, 12f, 15f)
                lineTo(6f, 15f)
                curveTo(3.8f, 15f, 2f, 16.8f, 2f, 19f)
                lineTo(2f, 21f)
                moveTo(9f, 11f)
                arcTo(4f, 4f, 0f, true, true, 9f, 3f)
                arcTo(4f, 4f, 0f, true, true, 9f, 11f)
                moveTo(22f, 21f)
                lineTo(22f, 19f)
                curveTo(22f, 17.4f, 20.9f, 16.1f, 19.5f, 15.4f)
                moveTo(16f, 3.1f)
                curveTo(17.2f, 3.8f, 18f, 5.1f, 18f, 6.5f)
                curveTo(18f, 7.9f, 17.2f, 9.2f, 16f, 9.9f)
            }
        }.build()
    }

    val Play: ImageVector by lazy {
        ImageVector.Builder("CampaignPlay", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.Black), stroke = SolidColor(Color.Black), strokeLineWidth = 1.5f) {
                moveTo(5f, 3f)
                lineTo(19f, 12f)
                lineTo(5f, 21f)
                close()
            }
        }.build()
    }

    val Globe: ImageVector by lazy {
        ImageVector.Builder("CampaignGlobe", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 21f)
                arcTo(9f, 9f, 0f, true, true, 12f, 3f)
                arcTo(9f, 9f, 0f, true, true, 12f, 21f)
                moveTo(3.6f, 9f)
                lineTo(20.4f, 9f)
                moveTo(3.6f, 15f)
                lineTo(20.4f, 15f)
                moveTo(12f, 3f)
                curveTo(14.5f, 7f, 14.5f, 17f, 12f, 21f)
                curveTo(9.5f, 17f, 9.5f, 7f, 12f, 3f)
            }
        }.build()
    }

    val MessageSquare: ImageVector by lazy {
        ImageVector.Builder("CampaignMessageSquare", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
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

    val ClipboardList: ImageVector by lazy {
        ImageVector.Builder("CampaignClipboardList", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(16f, 4f)
                lineTo(18f, 4f)
                curveTo(19.1f, 4f, 20f, 4.9f, 20f, 6f)
                lineTo(20f, 20f)
                curveTo(20f, 21.1f, 19.1f, 22f, 18f, 22f)
                lineTo(6f, 22f)
                curveTo(4.9f, 22f, 4f, 21.1f, 4f, 20f)
                lineTo(4f, 6f)
                curveTo(4f, 4.9f, 4.9f, 4f, 6f, 4f)
                lineTo(8f, 4f)
                moveTo(8f, 4f)
                curveTo(8f, 2.9f, 8.9f, 2f, 10f, 2f)
                lineTo(14f, 2f)
                curveTo(15.1f, 2f, 16f, 2.9f, 16f, 4f)
                close()
                moveTo(9f, 12f)
                lineTo(15f, 12f)
                moveTo(9f, 16f)
                lineTo(15f, 16f)
            }
        }.build()
    }

    val ShieldCheck: ImageVector by lazy {
        ImageVector.Builder("CampaignShieldCheck", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.White), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 22f)
                curveTo(12f, 22f, 20f, 18f, 20f, 12f)
                lineTo(20f, 5f)
                lineTo(12f, 2f)
                lineTo(4f, 5f)
                lineTo(4f, 12f)
                curveTo(4f, 18f, 12f, 22f, 12f, 22f)
                close()
                moveTo(9f, 12f)
                lineTo(11f, 14f)
                lineTo(15f, 10f)
            }
        }.build()
    }

    val CheckCircle: ImageVector by lazy {
        ImageVector.Builder("CampaignCheckCircle", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(22f, 11.08f)
                lineTo(22f, 12f)
                arcTo(10f, 10f, 0f, true, true, 16.17f, 3.42f)
                moveTo(22f, 4f)
                lineTo(12f, 14.01f)
                lineTo(9f, 11.01f)
            }
        }.build()
    }

    val Clock: ImageVector by lazy {
        ImageVector.Builder("CampaignClock", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(12f, 22f)
                arcTo(10f, 10f, 0f, true, true, 12f, 2f)
                arcTo(10f, 10f, 0f, true, true, 12f, 22f)
                moveTo(12f, 6f)
                lineTo(12f, 12f)
                lineTo(16f, 14f)
            }
        }.build()
    }

    val ArrowRight: ImageVector by lazy {
        ImageVector.Builder("CampaignArrowRight", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.White), strokeLineWidth = 2.2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(5f, 12f)
                lineTo(19f, 12f)
                moveTo(12f, 5f)
                lineTo(19f, 12f)
                lineTo(12f, 19f)
            }
        }.build()
    }

    val Pause: ImageVector by lazy {
        ImageVector.Builder("CampaignPause", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2.5f, strokeLineCap = StrokeCap.Round) {
                moveTo(10f, 4f)
                lineTo(10f, 20f)
                moveTo(14f, 4f)
                lineTo(14f, 20f)
            }
        }.build()
    }

    val Trash: ImageVector by lazy {
        ImageVector.Builder("CampaignTrash", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(3f, 6f)
                lineTo(21f, 6f)
                moveTo(19f, 6f)
                lineTo(19f, 20f)
                curveTo(19f, 21.1f, 18.1f, 22f, 17f, 22f)
                lineTo(7f, 22f)
                curveTo(5.9f, 22f, 5f, 21.1f, 5f, 20f)
                lineTo(5f, 6f)
                moveTo(8f, 6f)
                lineTo(8f, 4f)
                curveTo(8f, 2.9f, 8.9f, 2f, 10f, 2f)
                lineTo(14f, 2f)
                curveTo(15.1f, 2f, 16f, 2.9f, 16f, 4f)
                lineTo(16f, 6f)
            }
        }.build()
    }

    val Mail: ImageVector by lazy {
        ImageVector.Builder("CampaignMail", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(4f, 4f)
                lineTo(20f, 4f)
                curveTo(21.1f, 4f, 22f, 4.9f, 22f, 6f)
                lineTo(22f, 18f)
                curveTo(22f, 19.1f, 21.1f, 20f, 20f, 20f)
                lineTo(4f, 20f)
                curveTo(2.9f, 20f, 2f, 19.1f, 2f, 18f)
                lineTo(2f, 6f)
                curveTo(2f, 4.9f, 2.9f, 4f, 4f, 4f)
                close()
                moveTo(22f, 6f)
                lineTo(12f, 13f)
                lineTo(2f, 6f)
            }
        }.build()
    }

    val Phone: ImageVector by lazy {
        ImageVector.Builder("CampaignPhone", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(22f, 16.92f)
                lineTo(22f, 19.92f)
                curveTo(22f, 20.48f, 21.54f, 20.94f, 20.97f, 20.92f)
                curveTo(10.45f, 20.57f, 3.43f, 13.55f, 3.08f, 3.03f)
                curveTo(3.06f, 2.46f, 3.52f, 2f, 4.08f, 2f)
                lineTo(7.08f, 2f)
                curveTo(7.6f, 2f, 8.04f, 2.39f, 8.11f, 2.91f)
                curveTo(8.26f, 4.04f, 8.56f, 5.14f, 9f, 6.18f)
                curveTo(9.14f, 6.5f, 9.06f, 6.87f, 8.81f, 7.12f)
                lineTo(7.54f, 8.39f)
                curveTo(9.26f, 11.41f, 11.59f, 13.74f, 14.61f, 15.46f)
                lineTo(15.88f, 14.19f)
                curveTo(16.13f, 13.94f, 16.5f, 13.86f, 16.82f, 14f)
                curveTo(17.86f, 14.44f, 18.96f, 14.74f, 20.09f, 14.89f)
                curveTo(20.61f, 14.96f, 21f, 15.4f, 21f, 15.92f)
                close()
            }
        }.build()
    }
}

/**
 * Main Campaign & Ads Management Screen.
 * Supports all device form factors (compact phone, medium, tablet, desktop) via [widthIn] and scrollable containers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsScreen(
    uiState: TelefamUiState,
    onBackClick: () -> Unit,
    onSelectTab: (CampaignScreenTab) -> Unit,
    onSelectPost: (UserPostItem) -> Unit,
    onSelectGoal: (CampaignGoal) -> Unit,
    onSetDestinationUrl: (String) -> Unit,
    onSetLeadFormTitle: (String) -> Unit,
    onSetCtaText: (String) -> Unit,
    onSetTargetType: (String) -> Unit,
    onSetTargetGender: (String) -> Unit,
    onSetTargetAgeRange: (String) -> Unit,
    onToggleLocation: (String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onSelectTier: (String) -> Unit,
    onSetDurationDays: (Int) -> Unit,
    onLaunchCampaign: (CampaignPaymentGateway, String) -> Unit,
    onPauseCampaign: (String) -> Unit,
    onResumeCampaign: (String) -> Unit,
    onDeleteCampaign: (String) -> Unit,
    onViewLeads: (String) -> Unit,
    onCloseLeads: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isTelefamDarkTheme()
    val context = LocalContext.current
    val backgroundColor = if (isDark) Color(0xFF121214) else Color(0xFFF8F9FA)
    val cardBackground = if (isDark) Color(0xFF1E1E24) else Color.White
    val cardBorder = if (isDark) Color(0xFF2E2E38) else Color(0xFFE8E8EE)
    val textPrimary = if (isDark) Color.White else Color(0xFF1A1A1A)
    val textSecondary = if (isDark) Color(0xFF9E9EA8) else Color(0xFF6B6B76)

    // User Currency & Gateway Resolution
    val countryIso = uiState.savedProfile?.phoneIsoCode ?: uiState.selectedCountry.isoCode
    val currency = remember(countryIso) { CampaignBillingResolver.resolveCurrency(countryIso) }
    val gateway = remember(countryIso) { CampaignBillingResolver.resolveGateway(countryIso) }

    // Tiers for active goal
    val activeTiers = remember(uiState.campaignSelectedGoal) {
        CampaignPricingCatalog.getTiersForGoal(uiState.campaignSelectedGoal)
    }
    val selectedTier = remember(uiState.campaignSelectedTierId, activeTiers) {
        activeTiers.firstOrNull { it.tierId == uiState.campaignSelectedTierId } ?: activeTiers.getOrNull(1) ?: activeTiers.first()
    }
    val dailyBudget = selectedTier.getDailyPrice(currency)
    val totalBudget = dailyBudget * uiState.campaignDurationDays

    // Production Real Payment Sheet
    var isPaymentSheetOpen by remember { mutableStateOf(false) }

    if (isPaymentSheetOpen) {
        PaymentCheckoutSheet(
            isTopUpMode = false,
            userStarsBalance = uiState.starsBalance?.balanceStars ?: 0L,
            countryIso = countryIso,
            targetPost = uiState.campaignSelectedPost,
            campaignDurationDays = uiState.campaignDurationDays,
            campaignTotalAmount = totalBudget,
            campaignCurrency = currency,
            campaignDailyRate = dailyBudget,
            campaignTier = selectedTier,
            userPhoneNumber = uiState.savedProfile?.phoneNumber.orEmpty(),
            onProcessCampaignPayment = { gatewayName, reference, _ ->
                val resolvedGateway = when (gatewayName.uppercase()) {
                    "MPESA" -> CampaignPaymentGateway.PAYSTACK
                    "CARD" -> if (gateway == CampaignPaymentGateway.PAYSTACK) CampaignPaymentGateway.PAYSTACK else CampaignPaymentGateway.PAYPAL
                    "PAYPAL" -> CampaignPaymentGateway.PAYPAL
                    else -> CampaignPaymentGateway.PAYSTACK
                }
                onLaunchCampaign(resolvedGateway, reference)
                isPaymentSheetOpen = false
            },
            onDismissRequest = { isPaymentSheetOpen = false }
        )
    }

    // Lead Submission Sheet for owner review
    if (uiState.activeViewingCampaignId != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = onCloseLeads,
            sheetState = sheetState,
            containerColor = cardBackground,
            modifier = Modifier.testTag("campaign_leads_bottom_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.campaign_action_view_leads),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TelefamRed.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${uiState.activeCampaignLeads.size} leads",
                            color = TelefamRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (uiState.activeCampaignLeads.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.campaign_leads_empty),
                            color = textSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.activeCampaignLeads) { lead ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isDark) Color(0xFF262630) else Color(0xFFF1F3F5),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = lead.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(lead.createdAtMs)),
                                            fontSize = 11.5.sp,
                                            color = textSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = lead.email,
                                        fontSize = 13.sp,
                                        color = textSecondary
                                    )
                                    Text(
                                        text = lead.phone,
                                        fontSize = 13.sp,
                                        color = textSecondary
                                    )
                                    lead.inquiryNote?.let { note ->
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "\"$note\"",
                                            fontSize = 12.5.sp,
                                            color = textPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                runCatching {
                                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${lead.phone}"))
                                                    context.startActivity(intent)
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = LucideCampaignIcons.Phone,
                                                contentDescription = null,
                                                tint = TelefamRed,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Call", fontSize = 12.sp, color = TelefamRed)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                runCatching {
                                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${lead.email}"))
                                                    context.startActivity(intent)
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = LucideCampaignIcons.Mail,
                                                contentDescription = null,
                                                tint = textSecondary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Email", fontSize = 12.sp, color = textPrimary)
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

    Scaffold(
        containerColor = backgroundColor,
        modifier = modifier.testTag("campaigns_screen_root")
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header: Back Button + Screen Title + Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("campaign_back_button")
                        ) {
                            Icon(
                                imageVector = LucideCampaignIcons.ArrowLeft,
                                contentDescription = "Back",
                                tint = textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.campaign_screen_title),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = stringResource(R.string.owner_menu_create_campaign_desc),
                                fontSize = 11.5.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                // Two Tabs Pill Switcher: Create Campaign vs Ads Manager
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = cardBackground,
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        val isCreateActive = uiState.campaignActiveTab == CampaignScreenTab.CREATE
                        Surface(
                            onClick = { onSelectTab(CampaignScreenTab.CREATE) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCreateActive) TelefamRed else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("campaign_tab_create")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = LucideCampaignIcons.Megaphone,
                                    contentDescription = null,
                                    tint = if (isCreateActive) Color.White else textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.campaign_tab_create),
                                    fontSize = 13.sp,
                                    fontWeight = if (isCreateActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCreateActive) Color.White else textSecondary
                                )
                            }
                        }

                        val isManageActive = uiState.campaignActiveTab == CampaignScreenTab.MANAGE
                        Surface(
                            onClick = { onSelectTab(CampaignScreenTab.MANAGE) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isManageActive) TelefamRed else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("campaign_tab_manage")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = LucideCampaignIcons.BarChart,
                                    contentDescription = null,
                                    tint = if (isManageActive) Color.White else textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.campaign_tab_manage),
                                    fontSize = 13.sp,
                                    fontWeight = if (isManageActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isManageActive) Color.White else textSecondary
                                )
                            }
                        }
                    }
                }

                // Banner alert (if any)
                AnimatedVisibility(visible = uiState.campaignBannerMessage != null) {
                    uiState.campaignBannerMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TelefamRed.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, TelefamRed.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = msg,
                                color = TelefamRed,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                // Content View according to Tab
                if (uiState.campaignActiveTab == CampaignScreenTab.CREATE) {
                    CreateCampaignFlowView(
                        uiState = uiState,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        currency = currency,
                        gateway = gateway,
                        activeTiers = activeTiers,
                        selectedTier = selectedTier,
                        dailyBudget = dailyBudget,
                        totalBudget = totalBudget,
                        isDark = isDark,
                        onSelectPost = onSelectPost,
                        onSelectGoal = onSelectGoal,
                        onSetDestinationUrl = onSetDestinationUrl,
                        onSetLeadFormTitle = onSetLeadFormTitle,
                        onSetCtaText = onSetCtaText,
                        onSetTargetType = onSetTargetType,
                        onSetTargetGender = onSetTargetGender,
                        onSetTargetAgeRange = onSetTargetAgeRange,
                        onToggleLocation = onToggleLocation,
                        onToggleInterest = onToggleInterest,
                        onSelectTier = onSelectTier,
                        onSetDurationDays = onSetDurationDays,
                        onLaunchCampaign = onLaunchCampaign,
                        onOpenPaymentSheet = { isPaymentSheetOpen = true }
                    )
                } else {
                    AdsManagerView(
                        uiState = uiState,
                        cardBackground = cardBackground,
                        cardBorder = cardBorder,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        isDark = isDark,
                        onPauseCampaign = onPauseCampaign,
                        onResumeCampaign = onResumeCampaign,
                        onDeleteCampaign = onDeleteCampaign,
                        onViewLeads = onViewLeads,
                        onCreateNewCampaignClick = { onSelectTab(CampaignScreenTab.CREATE) }
                    )
                }
            }
        }
    }
}

/**
 * Step-by-Step Production Campaign Creation Form & Subscreens.
 */
@Composable
private fun CreateCampaignFlowView(
    uiState: TelefamUiState,
    cardBackground: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    currency: String,
    gateway: CampaignPaymentGateway,
    activeTiers: List<CampaignPricingTier>,
    selectedTier: CampaignPricingTier,
    dailyBudget: Double,
    totalBudget: Double,
    isDark: Boolean,
    onSelectPost: (UserPostItem) -> Unit = {},
    onSelectGoal: (CampaignGoal) -> Unit = {},
    onSetDestinationUrl: (String) -> Unit = {},
    onSetLeadFormTitle: (String) -> Unit = {},
    onSetCtaText: (String) -> Unit = {},
    onSetTargetType: (String) -> Unit = {},
    onSetTargetGender: (String) -> Unit = {},
    onSetTargetAgeRange: (String) -> Unit = {},
    onToggleLocation: (String) -> Unit = {},
    onToggleInterest: (String) -> Unit = {},
    onSelectTier: (String) -> Unit = {},
    onSetDurationDays: (Int) -> Unit = {},
    onLaunchCampaign: (CampaignPaymentGateway, String) -> Unit = { _, _ -> },
    onOpenPaymentSheet: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // STEP 1: Select Post
        SectionHeader(
            stepNumber = "1",
            title = stringResource(R.string.campaign_step_post),
            subtitle = stringResource(R.string.campaign_select_post_prompt),
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        val postsList = uiState.ownerPosts
        if (postsList.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardBackground,
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("campaign_no_posts_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = LucideCampaignIcons.Megaphone,
                        contentDescription = null,
                        tint = TelefamRed,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = stringResource(R.string.campaign_no_posts_yet),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.campaign_no_posts_hint),
                        fontSize = 12.5.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LaunchedEffect(postsList) {
                if (uiState.campaignSelectedPost == null && postsList.isNotEmpty()) {
                    onSelectPost(postsList.first())
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(postsList) { post ->
                    val isSelected = uiState.campaignSelectedPost?.postId == post.postId
                    Surface(
                        onClick = { onSelectPost(post) },
                        shape = RoundedCornerShape(14.dp),
                        color = cardBackground,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) TelefamRed else cardBorder
                        ),
                        modifier = Modifier
                            .width(130.dp)
                            .height(180.dp)
                            .testTag("campaign_post_item_${post.postId}")
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Dark gradient background representation of video
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color(0xFF2C2C34), Color(0xFF141418))
                                        )
                                    )
                            )
                            // Caption & views pill at bottom
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = post.caption.ifBlank { "Post" },
                                    color = Color.White,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = "${post.viewsCount} views",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 9.5.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            // Selected Checkmark Pill
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(TelefamRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = LucideCampaignIcons.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP 2: Campaign Objective / Goal
        SectionHeader(
            stepNumber = "2",
            title = stringResource(R.string.campaign_step_goal),
            subtitle = "Select what you want to achieve with this promotion",
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CampaignGoal.entries.forEach { goal ->
                val isSelected = uiState.campaignSelectedGoal == goal
                val icon = when (goal) {
                    CampaignGoal.FOLLOWERS -> LucideCampaignIcons.Users
                    CampaignGoal.VIDEO_VIEWS -> LucideCampaignIcons.Play
                    CampaignGoal.WEBSITE_STORE -> LucideCampaignIcons.Globe
                    CampaignGoal.DIRECT_MESSAGES -> LucideCampaignIcons.MessageSquare
                    CampaignGoal.FORM_APPLICATION -> LucideCampaignIcons.ClipboardList
                }

                Surface(
                    onClick = { onSelectGoal(goal) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) TelefamRed.copy(alpha = 0.08f) else cardBackground,
                    border = BorderStroke(
                        width = if (isSelected) 1.8.dp else 1.dp,
                        color = if (isSelected) TelefamRed else cardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("campaign_goal_${goal.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) TelefamRed else cardBorder.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = goal.title,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                                Text(
                                    text = goal.shortDesc,
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                            }
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = LucideCampaignIcons.CheckCircle,
                                contentDescription = null,
                                tint = TelefamRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // STEP 3: Action & CTA Details
        SectionHeader(
            stepNumber = "3",
            title = stringResource(R.string.campaign_step_cta),
            subtitle = "Customize the call to action button and destination",
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Goal Specific Form Input
                when (uiState.campaignSelectedGoal) {
                    CampaignGoal.WEBSITE_STORE -> {
                        Column {
                            Text(
                                text = stringResource(R.string.campaign_destination_url_label),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = uiState.campaignDestinationUrl,
                                onValueChange = onSetDestinationUrl,
                                placeholder = { Text(stringResource(R.string.campaign_destination_url_hint), fontSize = 13.sp) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("campaign_input_url")
                            )
                        }
                    }
                    CampaignGoal.FORM_APPLICATION -> {
                        Column {
                            Text(
                                text = stringResource(R.string.campaign_lead_form_title_label),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = uiState.campaignLeadFormTitle,
                                onValueChange = onSetLeadFormTitle,
                                placeholder = { Text(stringResource(R.string.campaign_lead_form_title_hint), fontSize = 13.sp) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("campaign_input_form_title")
                            )
                        }
                    }
                    CampaignGoal.DIRECT_MESSAGES -> {
                        Text(
                            text = "Inquiries will automatically open an in-app direct message with your account.",
                            fontSize = 12.5.sp,
                            color = textSecondary
                        )
                    }
                    else -> {}
                }

                // CTA Button Choice Chips
                Column {
                    Text(
                        text = stringResource(R.string.campaign_cta_button_label),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(CAMPAIGN_CTA_BUTTONS) { cta ->
                            val isCtaSelected = uiState.campaignCtaText.equals(cta, ignoreCase = true)
                            Surface(
                                onClick = { onSetCtaText(cta) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isCtaSelected) TelefamRed else cardBorder.copy(alpha = 0.5f),
                                modifier = Modifier.testTag("campaign_cta_btn_$cta")
                            ) {
                                Text(
                                    text = cta,
                                    fontSize = 12.sp,
                                    fontWeight = if (isCtaSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCtaSelected) Color.White else textPrimary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Feed CTA Preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color(0xFF262630) else Color(0xFFF1F3F5),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TelefamRed.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = stringResource(R.string.campaign_sponsored_badge),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TelefamRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Feed Button Preview:",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = TelefamRed,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.campaignCtaText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = LucideCampaignIcons.ArrowRight,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // STEP 4: Target Audience
        SectionHeader(
            stepNumber = "4",
            title = stringResource(R.string.campaign_step_audience),
            subtitle = "Define who will see your sponsored post across feeds",
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Audience Switcher: Automatic vs Custom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isAuto = uiState.campaignTargetType == "AUTOMATIC"
                    Surface(
                        onClick = { onSetTargetType("AUTOMATIC") },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isAuto) TelefamRed else cardBorder.copy(alpha = 0.5f),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("campaign_target_auto")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.campaign_target_auto),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAuto) Color.White else textPrimary
                            )
                        }
                    }

                    val isCustom = uiState.campaignTargetType == "CUSTOM"
                    Surface(
                        onClick = { onSetTargetType("CUSTOM") },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCustom) TelefamRed else cardBorder.copy(alpha = 0.5f),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("campaign_target_custom")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.campaign_target_custom),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCustom) Color.White else textPrimary
                            )
                        }
                    }
                }

                if (uiState.campaignTargetType == "AUTOMATIC") {
                    Text(
                        text = stringResource(R.string.campaign_target_auto_desc),
                        fontSize = 12.5.sp,
                        color = textSecondary
                    )
                } else {
                    // Custom Targeting Options: Gender, Age, Interests
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Gender
                        Text(
                            text = stringResource(R.string.campaign_audience_gender),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("ALL", "MEN", "WOMEN").forEach { g ->
                                val isGSelected = uiState.campaignTargetGender == g
                                Surface(
                                    onClick = { onSetTargetGender(g) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isGSelected) TelefamRed else cardBorder.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = g,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isGSelected) Color.White else textPrimary,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Age Brackets
                        Text(
                            text = stringResource(R.string.campaign_audience_age),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf("18-24", "25-34", "35-44", "45-54", "55+").forEach { age ->
                                val isAgeSelected = uiState.campaignTargetAgeRange == age
                                Surface(
                                    onClick = { onSetTargetAgeRange(age) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isAgeSelected) TelefamRed else cardBorder.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = age,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isAgeSelected) Color.White else textPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Interests
                        Text(
                            text = stringResource(R.string.campaign_audience_interests),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(CAMPAIGN_INTEREST_CATEGORIES) { interest ->
                                val isInterestSelected = uiState.campaignTargetInterests.contains(interest)
                                Surface(
                                    onClick = { onToggleInterest(interest) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isInterestSelected) TelefamRed else cardBorder.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = interest,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isInterestSelected) Color.White else textPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP 5: Budget & 5 Reach Tiers
        SectionHeader(
            stepNumber = "5",
            title = stringResource(R.string.campaign_step_budget),
            subtitle = "5 reach tiers with favorable per-day pricing",
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        // 5 Tiers List
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            activeTiers.forEachIndexed { index, tier ->
                val isTierSelected = tier.tierId == selectedTier.tierId
                val tierDailyPrice = tier.getDailyPrice(currency)

                Surface(
                    onClick = { onSelectTier(tier.tierId) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isTierSelected) TelefamRed.copy(alpha = 0.08f) else cardBackground,
                    border = BorderStroke(
                        width = if (isTierSelected) 1.8.dp else 1.dp,
                        color = if (isTierSelected) TelefamRed else cardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("campaign_tier_${tier.tierId}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isTierSelected) TelefamRed else cardBorder.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = if (isTierSelected) Color.White else textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tier.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    if (tier.isPopular) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = TelefamRed
                                        ) {
                                            Text(
                                                text = "POPULAR",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Est. reach: ${tier.formatReachRange()}",
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = CampaignBillingResolver.formatPrice(tierDailyPrice, currency),
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TelefamRed
                            )
                            Text(
                                text = stringResource(R.string.campaign_per_day),
                                fontSize = 10.5.sp,
                                color = textSecondary
                            )
                        }
                    }
                }
            }
        }

        // Duration Stepper & Days Row
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.campaign_select_duration),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = "${uiState.campaignDurationDays} days",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TelefamRed
                    )
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(CAMPAIGN_DURATION_OPTIONS) { days ->
                        val isDaySelected = uiState.campaignDurationDays == days
                        Surface(
                            onClick = { onSetDurationDays(days) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDaySelected) TelefamRed else cardBorder.copy(alpha = 0.5f),
                            modifier = Modifier.testTag("campaign_duration_chip_$days")
                        ) {
                            Text(
                                text = "$days d",
                                fontSize = 12.sp,
                                fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isDaySelected) Color.White else textPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // STEP 6: Review & Server-Side Verified Payment
        SectionHeader(
            stepNumber = "6",
            title = stringResource(R.string.campaign_step_payment),
            subtitle = "Verified server-side calculation and timestamp validation",
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = BorderStroke(1.dp, cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Daily Rate", fontSize = 13.sp, color = textSecondary)
                    Text(
                        text = "${CampaignBillingResolver.formatPrice(dailyBudget, currency)} / day",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Duration", fontSize = 13.sp, color = textSecondary)
                    Text(
                        text = "${uiState.campaignDurationDays} days",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Reach", fontSize = 13.sp, color = textSecondary)
                    Text(
                        text = selectedTier.formatReachRange(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                }

                HorizontalDivider(color = cardBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.campaign_total_budget_label),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = CampaignBillingResolver.formatPrice(totalBudget, currency),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TelefamRed
                    )
                }

                // Payment Gateway Indicator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (gateway == CampaignPaymentGateway.PAYSTACK) Color(0xFF00C3F7).copy(alpha = 0.12f) else Color(0xFF003087).copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = LucideCampaignIcons.ShieldCheck,
                            contentDescription = null,
                            tint = if (gateway == CampaignPaymentGateway.PAYSTACK) Color(0xFF0084A8) else Color(0xFF003087),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (gateway == CampaignPaymentGateway.PAYSTACK) {
                                stringResource(R.string.campaign_pay_with_paystack) + " (M-Pesa, Cards, Bank)"
                            } else {
                                stringResource(R.string.campaign_pay_with_paypal) + " (Global Cards & PayPal)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Launch CTA Button
                val isPostSelected = uiState.campaignSelectedPost != null
                Button(
                    onClick = onOpenPaymentSheet,
                    enabled = !uiState.isCampaignSubmitting && isPostSelected,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("campaign_btn_launch")
                ) {
                    if (uiState.isCampaignSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = LucideCampaignIcons.ShieldCheck,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPostSelected) stringResource(R.string.campaign_launch_button) else "Select a post to boost",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

/**
 * Ads Manager Dashboard: Displays Active, Paused, and Expired campaigns with real-time stats and controls.
 */
@Composable
private fun AdsManagerView(
    uiState: TelefamUiState,
    cardBackground: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    isDark: Boolean,
    onPauseCampaign: (String) -> Unit,
    onResumeCampaign: (String) -> Unit,
    onDeleteCampaign: (String) -> Unit,
    onViewLeads: (String) -> Unit,
    onCreateNewCampaignClick: () -> Unit
) {
    val campaigns = uiState.userCampaigns
    val activeCount = campaigns.count { it.status == "ACTIVE" && it.expiresAtMs > System.currentTimeMillis() }
    val totalImpressions = campaigns.sumOf { it.impressionsCount }
    val totalClicks = campaigns.sumOf { it.clicksCount }
    val totalLeads = campaigns.sumOf { it.conversionsCount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High-level Performance Metrics Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricStatCard(
                title = "Active Ads",
                value = "$activeCount",
                icon = LucideCampaignIcons.Megaphone,
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                modifier = Modifier.weight(1f)
            )
            MetricStatCard(
                title = stringResource(R.string.campaign_metric_impressions),
                value = "%,d".format(totalImpressions),
                icon = LucideCampaignIcons.Play,
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricStatCard(
                title = stringResource(R.string.campaign_metric_clicks),
                value = "%,d".format(totalClicks),
                icon = LucideCampaignIcons.ArrowRight,
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                modifier = Modifier.weight(1f)
            )
            MetricStatCard(
                title = stringResource(R.string.campaign_metric_conversions),
                value = "%,d".format(totalLeads),
                icon = LucideCampaignIcons.ClipboardList,
                cardBackground = cardBackground,
                cardBorder = cardBorder,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Campaign Items List
        if (campaigns.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = cardBackground,
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("campaign_empty_manager_card")
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(TelefamRed.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = LucideCampaignIcons.BarChart,
                            contentDescription = null,
                            tint = TelefamRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.campaign_empty_manager_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.campaign_empty_manager_desc),
                        fontSize = 12.5.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onCreateNewCampaignClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        modifier = Modifier.testTag("campaign_btn_create_first")
                    ) {
                        Text(
                            text = stringResource(R.string.campaign_tab_create),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            campaigns.forEach { campaign ->
                val now = System.currentTimeMillis()
                val isExpired = now >= campaign.expiresAtMs
                val effectiveStatus = if (isExpired) "EXPIRED" else campaign.status

                CampaignManagerItemCard(
                    campaign = campaign,
                    effectiveStatus = effectiveStatus,
                    cardBackground = cardBackground,
                    cardBorder = cardBorder,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onPause = { onPauseCampaign(campaign.campaignId) },
                    onResume = { onResumeCampaign(campaign.campaignId) },
                    onDelete = { onDeleteCampaign(campaign.campaignId) },
                    onViewLeads = { onViewLeads(campaign.campaignId) }
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun CampaignManagerItemCard(
    campaign: CampaignEntity,
    effectiveStatus: String,
    cardBackground: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
    onViewLeads: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("campaign_manage_card_${campaign.campaignId}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Row: Goal Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = CampaignGoal.fromId(campaign.campaignGoal).title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    campaign.postCaption?.takeIf { it.isNotBlank() }?.let { caption ->
                        Text(
                            text = caption,
                            fontSize = 12.sp,
                            color = textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                val (statusColor, statusLabel) = when (effectiveStatus) {
                    "ACTIVE" -> Pair(Color(0xFF2E7D32), stringResource(R.string.campaign_status_active))
                    "PAUSED" -> Pair(Color(0xFFE65100), stringResource(R.string.campaign_status_paused))
                    "EXPIRED" -> Pair(Color(0xFF757575), stringResource(R.string.campaign_status_expired))
                    else -> Pair(Color(0xFF1976D2), effectiveStatus)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = statusLabel.uppercase(),
                        color = statusColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Expiry countdown / Time Remaining
            val remainingMs = (campaign.expiresAtMs - System.currentTimeMillis()).coerceAtLeast(0L)
            val remainingHours = (remainingMs / (1000 * 60 * 60))
            val remainingDays = remainingHours / 24
            val timeRemainingText = if (remainingMs <= 0L) {
                stringResource(R.string.campaign_status_expired)
            } else if (remainingDays > 0) {
                "${remainingDays}d ${remainingHours % 24}h remaining"
            } else {
                "${remainingHours}h remaining"
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = LucideCampaignIcons.Clock,
                    contentDescription = null,
                    tint = textSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = timeRemainingText,
                    fontSize = 12.sp,
                    color = textSecondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "CTA: ${campaign.ctaButtonText}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = textPrimary
                )
            }

            // Metrics row: Impressions, Clicks, Budget
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBorder.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Impressions", fontSize = 11.sp, color = textSecondary)
                    Text(text = "%,d".format(campaign.impressionsCount), fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
                Column {
                    Text(text = "Clicks", fontSize = 11.sp, color = textSecondary)
                    Text(text = "%,d".format(campaign.clicksCount), fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
                Column {
                    Text(text = "Leads", fontSize = 11.sp, color = textSecondary)
                    Text(text = "%,d".format(campaign.conversionsCount), fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Budget", fontSize = 11.sp, color = textSecondary)
                    Text(
                        text = CampaignBillingResolver.formatPrice(campaign.totalBudgetDisplay, campaign.currency),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TelefamRed
                    )
                }
            }

            // Action Buttons Row: Pause / Resume / View Leads / Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (campaign.campaignGoal == "FORM_APPLICATION") {
                    OutlinedButton(
                        onClick = onViewLeads,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("campaign_btn_view_leads_${campaign.campaignId}")
                    ) {
                        Icon(
                            imageVector = LucideCampaignIcons.ClipboardList,
                            contentDescription = null,
                            tint = TelefamRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.campaign_action_view_leads),
                            fontSize = 11.5.sp,
                            color = TelefamRed
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (effectiveStatus == "ACTIVE") {
                    OutlinedButton(
                        onClick = onPause,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("campaign_btn_pause_${campaign.campaignId}")
                    ) {
                        Icon(
                            imageVector = LucideCampaignIcons.Pause,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.campaign_action_pause),
                            fontSize = 11.5.sp,
                            color = textPrimary
                        )
                    }
                } else if (effectiveStatus == "PAUSED") {
                    Button(
                        onClick = onResume,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("campaign_btn_resume_${campaign.campaignId}")
                    ) {
                        Icon(
                            imageVector = LucideCampaignIcons.Play,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.campaign_action_resume),
                            fontSize = 11.5.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = LucideCampaignIcons.Trash,
                        contentDescription = "Delete",
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    cardBackground: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = cardBackground,
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = textSecondary)
                Icon(imageVector = icon, contentDescription = null, tint = TelefamRed, modifier = Modifier.size(16.dp))
            }
            Text(text = value, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        }
    }
}

@Composable
private fun SectionHeader(
    stepNumber: String,
    title: String,
    subtitle: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(TelefamRed),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = textSecondary
            )
        }
    }
}
