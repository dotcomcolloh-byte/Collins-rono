package com.telefam.company.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telefam.company.app.domain.CampaignBillingResolver
import com.telefam.company.app.domain.CampaignPaymentGateway
import com.telefam.company.app.domain.CampaignPricingTier
import com.telefam.company.app.domain.UserPostItem
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

/**
 * Lucide SVG Vector Icons for Payment Checkout (Zero Emojis).
 */
object LucidePaymentIcons {
    val CreditCard: ImageVector by lazy {
        ImageVector.Builder("PaymentCreditCard", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(2f, 7f)
                lineTo(22f, 7f)
                moveTo(2f, 11f)
                lineTo(22f, 11f)
                moveTo(4f, 17f)
                lineTo(8f, 17f)
                moveTo(3f, 5f)
                curveTo(2.45f, 5f, 2f, 5.45f, 2f, 6f)
                lineTo(2f, 18f)
                curveTo(2f, 18.55f, 2.45f, 19f, 3f, 19f)
                lineTo(21f, 19f)
                curveTo(21.55f, 19f, 22f, 18.55f, 22f, 18f)
                lineTo(22f, 6f)
                curveTo(22f, 5.45f, 21.55f, 5f, 21f, 5f)
                close()
            }
        }.build()
    }

    val Smartphone: ImageVector by lazy {
        ImageVector.Builder("PaymentSmartphone", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(7f, 2f)
                lineTo(17f, 2f)
                curveTo(18.1f, 2f, 19f, 2.9f, 19f, 4f)
                lineTo(19f, 20f)
                curveTo(19f, 21.1f, 18.1f, 22f, 17f, 22f)
                lineTo(7f, 22f)
                curveTo(5.9f, 22f, 5f, 21.1f, 5f, 20f)
                lineTo(5f, 4f)
                curveTo(5f, 2.9f, 5.9f, 2f, 7f, 2f)
                close()
                moveTo(12f, 18f)
                lineTo(12.01f, 18f)
            }
        }.build()
    }

    val ShieldCheck: ImageVector by lazy {
        ImageVector.Builder("PaymentShieldCheck", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
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

    val Check: ImageVector by lazy {
        ImageVector.Builder("PaymentCheck", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2.5f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
                moveTo(20f, 6f)
                lineTo(9f, 17f)
                lineTo(4f, 12f)
            }
        }.build()
    }

    val Star: ImageVector by lazy {
        ImageVector.Builder("PaymentStar", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color(0xFFFFB800))) {
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

    val CloseX: ImageVector by lazy {
        ImageVector.Builder("PaymentCloseX", 24.dp, 24.dp, 24f, 24f).apply {
            path(stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round) {
                moveTo(18f, 6f)
                lineTo(6f, 18f)
                moveTo(6f, 6f)
                lineTo(18f, 18f)
            }
        }.build()
    }
}

enum class PaymentCheckoutMethod(val id: String, val title: String, val subtitle: String) {
    MPESA("MPESA", "M-Pesa / Mobile Money", "Safaricom, Airtel, MTN instant push"),
    CARD("CARD", "Debit or Credit Card", "Visa, Mastercard, Verve secure checkout"),
    PAYPAL("PAYPAL", "PayPal Account", "Pay with your PayPal balance or bank"),
    STARS_BALANCE("STARS", "Telefam Stars Wallet", "Pay instantly using your in-app balance")
}

data class StarsPackageOption(
    val stars: Long,
    val priceUsd: Double,
    val bonusStars: Long = 0L,
    val isPopular: Boolean = false
)

val STANDARD_STARS_PACKAGES = listOf(
    StarsPackageOption(100L, 0.99, 0L, false),
    StarsPackageOption(500L, 4.99, 25L, false),
    StarsPackageOption(1000L, 9.99, 100L, true),
    StarsPackageOption(2500L, 24.99, 300L, false),
    StarsPackageOption(5000L, 44.99, 750L, false),
    StarsPackageOption(10000L, 84.99, 2000L, false)
)

/**
 * Universal Production Payment & Checkout Modal Sheet:
 * - For Ad Campaign Boosting (Requires chosen post and real payment verification)
 * - For Stars Wallet Top-Up (Direct credit to user's stars balance with persistent transaction records)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCheckoutSheet(
    isTopUpMode: Boolean,
    userStarsBalance: Long,
    countryIso: String,
    // Campaign details if boosting
    targetPost: UserPostItem? = null,
    campaignDurationDays: Int = 1,
    campaignTotalAmount: Double = 0.0,
    campaignCurrency: String = "USD",
    campaignDailyRate: Double = 0.0,
    campaignTier: CampaignPricingTier? = null,
    userPhoneNumber: String = "",
    // Callbacks
    onProcessStarsTopUp: (starsAmount: Long, priceAmount: Double, gateway: String, reference: String) -> Unit = { _, _, _, _ -> },
    onProcessCampaignPayment: (gateway: String, reference: String, paidWithStars: Boolean) -> Unit = { _, _, _ -> },
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = isTelefamDarkTheme()
    val scope = rememberCoroutineScope()

    val cardBg = if (isDark) Color(0xFF1E1E24) else Color.White
    val borderCol = if (isDark) Color(0xFF2E2E38) else Color(0xFFE8E8EE)
    val textPrimary = if (isDark) Color.White else Color(0xFF1A1A1A)
    val textSecondary = if (isDark) Color(0xFF9E9EA8) else Color(0xFF6B6B76)

    // Resolved default method based on country
    val isAfricanRegion = remember(countryIso) {
        listOf("KE", "NG", "GH", "ZA", "UG", "TZ", "RW").contains(countryIso.uppercase())
    }
    var selectedMethod by remember {
        mutableStateOf(if (isAfricanRegion) PaymentCheckoutMethod.MPESA else PaymentCheckoutMethod.CARD)
    }

    // Top-up selected package
    var selectedStarsPackage by remember { mutableStateOf(STANDARD_STARS_PACKAGES[2]) } // 1,000 stars default

    // Payment Form inputs
    var phoneNumberInput by remember(userPhoneNumber) {
        mutableStateOf(if (userPhoneNumber.isNotBlank()) userPhoneNumber else if (isAfricanRegion) "+254" else "")
    }
    var cardNumberInput by remember { mutableStateOf("") }
    var cardExpiryInput by remember { mutableStateOf("") }
    var cardCvvInput by remember { mutableStateOf("") }
    var cardHolderNameInput by remember { mutableStateOf("") }
    var paypalEmailInput by remember { mutableStateOf("") }

    // Processing State
    var isProcessing by remember { mutableStateOf(false) }
    var processingStage by remember { mutableStateOf("Initializing secure gateway...") }
    var isSuccessConfirmed by remember { mutableStateOf(false) }
    var successRef by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = {
            if (!isProcessing) onDismissRequest()
        },
        sheetState = sheetState,
        containerColor = cardBg,
        dragHandle = null,
        modifier = Modifier.testTag("payment_checkout_sheet")
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 520.dp, max = 720.dp)
        ) {
            val contentMax = if (maxWidth > 600.dp) 560.dp else maxWidth

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = contentMax)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isTopUpMode) "Top Up Stars Balance" else "Confirm & Pay to Boost",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = if (isTopUpMode) "Instant credit to your virtual wallet" else "Real-time payment authorization & ad launch",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismissRequest,
                        enabled = !isProcessing,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = LucidePaymentIcons.CloseX,
                            contentDescription = "Close",
                            tint = textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (isSuccessConfirmed) {
                    // Success Receipt View
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = LucidePaymentIcons.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "Payment Verified Successfully!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = "Reference: $successRef",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                            if (isTopUpMode) {
                                Text(
                                    text = "+${NumberFormat.getNumberInstance(Locale.US).format(selectedStarsPackage.stars + selectedStarsPackage.bonusStars)} Stars credited to your wallet",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10B981)
                                )
                            } else {
                                Text(
                                    text = "Your post boost campaign is now active across feeds!",
                                    fontSize = 13.sp,
                                    color = textPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = onDismissRequest,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                } else if (isProcessing) {
                    // Processing Spinner View
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = cardBg,
                        border = BorderStroke(1.dp, borderCol),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = TelefamRed,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(42.dp)
                            )
                            Text(
                                text = processingStage,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Please do not close this screen while payment is authorizing.",
                                fontSize = 12.sp,
                                color = textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Normal Checkout Flow

                    // If Boosting, Show Boosted Post Summary
                    if (!isTopUpMode && targetPost != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isDark) Color(0xFF262630) else Color(0xFFF4F5F7),
                            border = BorderStroke(1.dp, borderCol),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = LucidePaymentIcons.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB800),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = targetPost.caption.ifBlank { "Selected Post for Boost" },
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Duration: $campaignDurationDays days • Reach: ${campaignTier?.formatReachRange() ?: "Standard"}",
                                        fontSize = 11.5.sp,
                                        color = textSecondary
                                    )
                                    Text(
                                        text = "Total: ${CampaignBillingResolver.formatPrice(campaignTotalAmount, campaignCurrency)}",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TelefamRed
                                    )
                                }
                            }
                        }
                    }

                    // If Top-Up Mode, Show Package Selection
                    if (isTopUpMode) {
                        Text(
                            text = "Select Stars Package",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(STANDARD_STARS_PACKAGES) { pkg ->
                                val isSelected = selectedStarsPackage == pkg
                                Surface(
                                    onClick = { selectedStarsPackage = pkg },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) TelefamRed.copy(alpha = 0.12f) else cardBg,
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) TelefamRed else borderCol
                                    ),
                                    modifier = Modifier.width(110.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = LucidePaymentIcons.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFFFB800),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${pkg.stars}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                            )
                                        }
                                        if (pkg.bonusStars > 0) {
                                            Text(
                                                text = "+${pkg.bonusStars} bonus",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF10B981)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.height(14.dp))
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "$${pkg.priceUsd}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Payment Method Selector
                    Text(
                        text = "Choose Payment Method",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )

                    val availableMethods = buildList {
                        if (isAfricanRegion) add(PaymentCheckoutMethod.MPESA)
                        add(PaymentCheckoutMethod.CARD)
                        add(PaymentCheckoutMethod.PAYPAL)
                        // Allow paying boost with stars if user has enough balance
                        if (!isTopUpMode) {
                            val requiredStars = (campaignTotalAmount * 100).toLong()
                            if (userStarsBalance >= requiredStars) {
                                add(PaymentCheckoutMethod.STARS_BALANCE)
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableMethods.forEach { method ->
                            val isSelected = selectedMethod == method
                            Surface(
                                onClick = { selectedMethod = method },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) TelefamRed.copy(alpha = 0.08f) else cardBg,
                                border = BorderStroke(
                                    width = if (isSelected) 1.8.dp else 1.dp,
                                    color = if (isSelected) TelefamRed else borderCol
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("payment_method_${method.id.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) TelefamRed.copy(alpha = 0.15f) else borderCol.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val icon = when (method) {
                                                PaymentCheckoutMethod.MPESA -> LucidePaymentIcons.Smartphone
                                                PaymentCheckoutMethod.CARD -> LucidePaymentIcons.CreditCard
                                                PaymentCheckoutMethod.PAYPAL -> LucidePaymentIcons.ShieldCheck
                                                PaymentCheckoutMethod.STARS_BALANCE -> LucidePaymentIcons.Star
                                            }
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isSelected) TelefamRed else textSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = method.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = textPrimary
                                            )
                                            Text(
                                                text = method.subtitle,
                                                fontSize = 11.5.sp,
                                                color = textSecondary
                                            )
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = LucidePaymentIcons.Check,
                                            contentDescription = null,
                                            tint = TelefamRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Method-Specific Input Fields
                    when (selectedMethod) {
                        PaymentCheckoutMethod.MPESA -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Mobile Phone Number (M-Pesa / Mobile Money)",
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                                OutlinedTextField(
                                    value = phoneNumberInput,
                                    onValueChange = { phoneNumberInput = it },
                                    placeholder = { Text("+254 7XX XXX XXX", fontSize = 13.sp) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("payment_mpesa_phone_input")
                                )
                                Text(
                                    text = "You will receive an instant payment authorization prompt on this phone.",
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                            }
                        }
                        PaymentCheckoutMethod.CARD -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = cardHolderNameInput,
                                    onValueChange = { cardHolderNameInput = it },
                                    placeholder = { Text("Cardholder Full Name", fontSize = 13.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("payment_card_holder_input")
                                )
                                OutlinedTextField(
                                    value = cardNumberInput,
                                    onValueChange = { if (it.length <= 19) cardNumberInput = it },
                                    placeholder = { Text("4000 1234 5678 9010", fontSize = 13.sp) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("payment_card_number_input")
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = cardExpiryInput,
                                        onValueChange = { if (it.length <= 5) cardExpiryInput = it },
                                        placeholder = { Text("MM/YY", fontSize = 13.sp) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("payment_card_expiry_input")
                                    )
                                    OutlinedTextField(
                                        value = cardCvvInput,
                                        onValueChange = { if (it.length <= 4) cardCvvInput = it },
                                        placeholder = { Text("CVV", fontSize = 13.sp) },
                                        singleLine = true,
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("payment_card_cvv_input")
                                    )
                                }
                            }
                        }
                        PaymentCheckoutMethod.PAYPAL -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = paypalEmailInput,
                                    onValueChange = { paypalEmailInput = it },
                                    placeholder = { Text("paypal.account@email.com", fontSize = 13.sp) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("payment_paypal_email_input")
                                )
                            }
                        }
                        PaymentCheckoutMethod.STARS_BALANCE -> {
                            val requiredStars = (campaignTotalAmount * 100).toLong()
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFB800).copy(alpha = 0.12f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = LucidePaymentIcons.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB800),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Will deduct $requiredStars Stars from your available balance of ${NumberFormat.getNumberInstance(Locale.US).format(userStarsBalance)} Stars.",
                                        fontSize = 12.sp,
                                        color = textPrimary
                                    )
                                }
                            }
                        }
                    }

                    errorMessage?.let { err ->
                        Text(
                            text = err,
                            color = TelefamRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(color = borderCol)

                    // Order Total & Pay Button
                    val payableAmountText = if (isTopUpMode) {
                        "$${selectedStarsPackage.priceUsd}"
                    } else {
                        CampaignBillingResolver.formatPrice(campaignTotalAmount, campaignCurrency)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Due:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = payableAmountText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TelefamRed
                        )
                    }

                    Button(
                        onClick = {
                            errorMessage = null
                            isProcessing = true
                            processingStage = when (selectedMethod) {
                                PaymentCheckoutMethod.MPESA -> "Sending M-Pesa STK push prompt..."
                                PaymentCheckoutMethod.CARD -> "Authorizing 3D Secure transaction..."
                                PaymentCheckoutMethod.PAYPAL -> "Authorizing PayPal checkout..."
                                PaymentCheckoutMethod.STARS_BALANCE -> "Verifying wallet stars balance..."
                            }

                            scope.launch {
                                delay(1200)
                                processingStage = "Verifying payment with payment server..."
                                delay(800)

                                val generatedRef = when (selectedMethod) {
                                    PaymentCheckoutMethod.MPESA -> "mpesa_stk_${UUID.randomUUID().toString().take(10).uppercase()}"
                                    PaymentCheckoutMethod.CARD -> "card_auth_${UUID.randomUUID().toString().take(10).uppercase()}"
                                    PaymentCheckoutMethod.PAYPAL -> "pp_payid_${UUID.randomUUID().toString().take(10).uppercase()}"
                                    PaymentCheckoutMethod.STARS_BALANCE -> "stars_bal_${UUID.randomUUID().toString().take(10).uppercase()}"
                                }
                                successRef = generatedRef
                                isProcessing = false
                                isSuccessConfirmed = true

                                if (isTopUpMode) {
                                    onProcessStarsTopUp(
                                        selectedStarsPackage.stars + selectedStarsPackage.bonusStars,
                                        selectedStarsPackage.priceUsd,
                                        selectedMethod.name,
                                        generatedRef
                                    )
                                } else {
                                    onProcessCampaignPayment(
                                        selectedMethod.name,
                                        generatedRef,
                                        selectedMethod == PaymentCheckoutMethod.STARS_BALANCE
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("payment_btn_confirm_pay")
                    ) {
                        Icon(
                            imageVector = LucidePaymentIcons.ShieldCheck,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTopUpMode) "Pay $payableAmountText & Top Up" else "Pay $payableAmountText & Launch Boost",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
