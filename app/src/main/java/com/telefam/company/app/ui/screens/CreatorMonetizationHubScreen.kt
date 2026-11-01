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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.StrokeCap
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
import com.example.ui.theme.isTelefamDarkTheme
import com.telefam.company.app.domain.ApprovedMonetizationFeature
import com.telefam.company.app.domain.CreatorMonetizationStatus
import com.telefam.company.app.domain.CreatorQualificationSnapshot
import com.telefam.company.app.domain.VideoMonetizationItem
import com.telefam.company.app.domain.WhyNotApprovedItem
import com.telefam.company.app.ui.TelefamUiState
import com.telefam.company.app.ui.components.MonetizationLucideIcons
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Complete Creator Monetization Hub supporting all lifecycle phases:
 * 1. Qualification Requirements (Matching Image 1)
 * 2. Application Interest Submission & Review ("You will be invited to join monetization")
 * 3. Monetization Not Approved (Matching Image 2)
 * 4. Monetization Approved (Matching Image 3)
 * 5. Monetized Content & Earnings on Shared Videos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorMonetizationHubScreen(
    uiState: TelefamUiState,
    onBackClick: () -> Unit,
    onSubmitInterest: (category: String, statement: String, email: String) -> Unit = { _, _, _ -> },
    onOpenWallet: () -> Unit = {},
    onOpenSubscriptions: () -> Unit = {},
    onUpdateVideoMonetization: (VideoMonetizationItem) -> Unit = {},
    onAdminChangeStatus: (CreatorMonetizationStatus) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isTelefamDarkTheme()
    val screenBg = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF9FAFC)
    val cardBg = if (isDark) Color(0xFF1E2633) else Color.White
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFEDF0F5)

    var currentViewOverride by remember { mutableStateOf<String?>(null) }
    var isApplyModalOpen by remember { mutableStateOf(false) }
    var selectedWhyItem by remember { mutableStateOf<WhyNotApprovedItem?>(null) }
    var isHelpDialogOpen by remember { mutableStateOf(false) }
    var isTermsDialogOpen by remember { mutableStateOf(false) }
    var editingVideoItem by remember { mutableStateOf<VideoMonetizationItem?>(null) }
    var showStateDebugMenu by remember { mutableStateOf(false) }

    val activeStatus = when (currentViewOverride) {
        "QUALIFICATION" -> CreatorMonetizationStatus.NOT_APPLIED
        "SUBMITTED" -> CreatorMonetizationStatus.SUBMITTED
        "NOT_APPROVED" -> CreatorMonetizationStatus.NOT_APPROVED
        "APPROVED" -> CreatorMonetizationStatus.APPROVED
        "CONTENT_DASHBOARD" -> CreatorMonetizationStatus.APPROVED
        else -> uiState.monetizationStatus
    }

    val isShowingContentDashboard = currentViewOverride == "CONTENT_DASHBOARD"

    val topBarTitle = when {
        isShowingContentDashboard -> stringResource(R.string.monetization_content_title)
        activeStatus == CreatorMonetizationStatus.APPROVED -> stringResource(R.string.approved_title)
        activeStatus == CreatorMonetizationStatus.NOT_APPROVED -> stringResource(R.string.not_approved_title)
        activeStatus == CreatorMonetizationStatus.SUBMITTED -> stringResource(R.string.monetization_submitted_title)
        else -> stringResource(R.string.qualification_requirements_title)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isShowingContentDashboard) {
                                currentViewOverride = null
                            } else {
                                onBackClick()
                            }
                        },
                        modifier = Modifier.testTag("monetization_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.crop_cancel_button),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isHelpDialogOpen = true },
                        modifier = Modifier.testTag("monetization_help_button")
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.HelpCircle,
                            contentDescription = stringResource(R.string.settings_row_help_support_title),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(
                        onClick = { showStateDebugMenu = !showStateDebugMenu },
                        modifier = Modifier.testTag("monetization_state_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Lifecycle States",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = screenBg,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = screenBg,
        modifier = modifier.testTag("creator_monetization_hub_screen")
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val maxContentWidth = 680.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
                    .widthIn(max = maxContentWidth)
            ) {
                // Quick Lifecycle State Switcher Bar for Reviewing All Screens
                AnimatedVisibility(visible = showStateDebugMenu) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Preview All Monetization Lifecycle Screens:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        currentViewOverride = "QUALIFICATION"
                                        onAdminChangeStatus(CreatorMonetizationStatus.NOT_APPLIED)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Image 1", fontSize = 11.sp, maxLines = 1)
                                }
                                OutlinedButton(
                                    onClick = {
                                        currentViewOverride = "NOT_APPROVED"
                                        onAdminChangeStatus(CreatorMonetizationStatus.NOT_APPROVED)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Image 2", fontSize = 11.sp, maxLines = 1)
                                }
                                OutlinedButton(
                                    onClick = {
                                        currentViewOverride = "APPROVED"
                                        onAdminChangeStatus(CreatorMonetizationStatus.APPROVED)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Image 3", fontSize = 11.sp, maxLines = 1)
                                }
                                OutlinedButton(
                                    onClick = {
                                        currentViewOverride = "CONTENT_DASHBOARD"
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Content", fontSize = 11.sp, maxLines = 1)
                                }
                            }
                        }
                    }
                }

                // Render Active Lifecycle Screen
                when {
                    isShowingContentDashboard -> {
                        ContentMonetizationDashboardSection(
                            uiState = uiState,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            isDark = isDark,
                            onEditVideo = { editingVideoItem = it },
                            onOpenWallet = onOpenWallet,
                            onOpenSubscriptions = onOpenSubscriptions
                        )
                    }

                    activeStatus == CreatorMonetizationStatus.APPROVED -> {
                        MonetizationApprovedSection(
                            uiState = uiState,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            isDark = isDark,
                            onGoToDashboard = { currentViewOverride = "CONTENT_DASHBOARD" },
                            onOpenTerms = { isTermsDialogOpen = true },
                            onOpenWallet = onOpenWallet,
                            onOpenSubscriptions = onOpenSubscriptions
                        )
                    }

                    activeStatus == CreatorMonetizationStatus.NOT_APPROVED -> {
                        MonetizationNotApprovedSection(
                            uiState = uiState,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            isDark = isDark,
                            onSelectWhyItem = { selectedWhyItem = it },
                            onOpenPolicies = { isHelpDialogOpen = true },
                            onRecheckQualification = {
                                currentViewOverride = "QUALIFICATION"
                                onAdminChangeStatus(CreatorMonetizationStatus.NOT_APPLIED)
                            }
                        )
                    }

                    activeStatus == CreatorMonetizationStatus.SUBMITTED -> {
                        ApplicationSubmittedSection(
                            uiState = uiState,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            isDark = isDark,
                            onReviewGuidelines = { isHelpDialogOpen = true },
                            onAdminApprove = {
                                currentViewOverride = "APPROVED"
                                onAdminChangeStatus(CreatorMonetizationStatus.APPROVED)
                            },
                            onAdminReject = {
                                currentViewOverride = "NOT_APPROVED"
                                onAdminChangeStatus(CreatorMonetizationStatus.NOT_APPROVED)
                            }
                        )
                    }

                    else -> {
                        QualificationRequirementsSection(
                            uiState = uiState,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            isDark = isDark,
                            onApplyNow = { isApplyModalOpen = true }
                        )
                    }
                }
            }
        }
    }

    // Modal: Application Interest Submission
    if (isApplyModalOpen) {
        ApplicationInterestSheet(
            uiState = uiState,
            onDismiss = { isApplyModalOpen = false },
            onSubmit = { cat, stmt, mail ->
                onSubmitInterest(cat, stmt, mail)
                isApplyModalOpen = false
                currentViewOverride = "SUBMITTED"
            }
        )
    }

    // Modal: Why Not Approved Detail
    selectedWhyItem?.let { item ->
        WhyNotApprovedDetailSheet(
            item = item,
            onDismiss = { selectedWhyItem = null }
        )
    }

    // Modal: Video Monetization Settings
    editingVideoItem?.let { video ->
        VideoMonetizationSettingsSheet(
            item = video,
            onDismiss = { editingVideoItem = null },
            onSave = { updated ->
                onUpdateVideoMonetization(updated)
                editingVideoItem = null
            }
        )
    }

    // Dialog: Monetization Help
    if (isHelpDialogOpen) {
        MonetizationHelpDialog(onDismiss = { isHelpDialogOpen = false })
    }

    // Dialog: Monetization Terms
    if (isTermsDialogOpen) {
        MonetizationTermsDialog(onDismiss = { isTermsDialogOpen = false })
    }
}

// ==========================================
// 1. QUALIFICATION REQUIREMENTS (IMAGE 1)
// ==========================================

@Composable
private fun QualificationRequirementsSection(
    uiState: TelefamUiState,
    cardBg: Color,
    cardBorder: Color,
    isDark: Boolean,
    onApplyNow: () -> Unit
) {
    val snapshot = uiState.qualificationSnapshot

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("qualification_requirements_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Profile Header (Avatar, Verified Badge, @username, Creator pill)
        item {
            CreatorProfileHeader(uiState = uiState, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }

        // Hero Card with 3D Purple Qualification Medal Asset
        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3EDFD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_purple_qualification_medal),
                            contentDescription = stringResource(R.string.qualification_requirements_title),
                            modifier = Modifier.size(58.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.qualification_hero_title),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.qualification_hero_subtitle),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section: Eligibility Requirements
        item {
            Text(
                text = stringResource(R.string.qualification_eligibility_section),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Followers Requirement
                    RequirementRowWithProgress(
                        title = stringResource(R.string.qualification_followers_title),
                        subtitle = stringResource(R.string.qualification_followers_desc),
                        icon = MonetizationLucideIcons.Users,
                        iconBg = Color(0xFFEAF2FF),
                        iconTint = Color(0xFF2563EB),
                        current = snapshot.followersCount,
                        target = snapshot.targetFollowers,
                        progressFraction = (snapshot.followersCount.toFloat() / snapshot.targetFollowers.toFloat()).coerceIn(0f, 1f),
                        progressColor = Color(0xFF2563EB),
                        isMet = snapshot.isFollowersMet,
                        isNumeric = true
                    )

                    HorizontalDivider(
                        color = cardBorder,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    // Watch Time (Last 60 Days) Requirement
                    RequirementRowWithProgress(
                        title = stringResource(R.string.qualification_watch_time_title),
                        subtitle = stringResource(R.string.qualification_watch_time_desc),
                        icon = MonetizationLucideIcons.PlaySquare,
                        iconBg = Color(0xFFE8F8EE),
                        iconTint = Color(0xFF16A34A),
                        current = snapshot.watchTimeMinutes,
                        target = snapshot.targetWatchTimeMinutes,
                        progressFraction = (snapshot.watchTimeMinutes.toFloat() / snapshot.targetWatchTimeMinutes.toFloat()).coerceIn(0f, 1f),
                        progressColor = Color(0xFF16A34A),
                        isMet = snapshot.isWatchTimeMet,
                        isNumeric = true,
                        unit = "minutes"
                    )

                    HorizontalDivider(
                        color = cardBorder,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    // Content Requirement
                    RequirementRowWithProgress(
                        title = stringResource(R.string.qualification_content_title),
                        subtitle = stringResource(R.string.qualification_content_desc),
                        icon = MonetizationLucideIcons.Clapperboard,
                        iconBg = Color(0xFFFEF6E7),
                        iconTint = Color(0xFFD97706),
                        current = snapshot.publicVideosCount,
                        target = snapshot.targetPublicVideosCount,
                        progressFraction = (snapshot.publicVideosCount.toFloat() / snapshot.targetPublicVideosCount.toFloat()).coerceIn(0f, 1f),
                        progressColor = Color(0xFFD97706),
                        isMet = snapshot.isContentMet,
                        isNumeric = true,
                        unit = "videos"
                    )

                    HorizontalDivider(
                        color = cardBorder,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    // Community Guidelines Requirement
                    SimpleRequirementRow(
                        title = stringResource(R.string.qualification_guidelines_title),
                        subtitle = stringResource(R.string.qualification_guidelines_desc),
                        icon = MonetizationLucideIcons.ShieldCheck,
                        iconBg = Color(0xFFF3EDFD),
                        iconTint = Color(0xFF7C3AED),
                        isMet = snapshot.communityGuidelinesMet
                    )

                    HorizontalDivider(
                        color = cardBorder,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    // Account Status Requirement
                    SimpleRequirementRow(
                        title = stringResource(R.string.qualification_status_title),
                        subtitle = stringResource(R.string.qualification_status_desc),
                        icon = MonetizationLucideIcons.ShieldCheck,
                        iconBg = Color(0xFFE8F8EE),
                        iconTint = Color(0xFF16A34A),
                        isMet = snapshot.accountStatusMet
                    )
                }
            }
        }

        // Section: Additional Criteria
        item {
            Text(
                text = stringResource(R.string.qualification_additional_section),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SimpleRequirementRow(
                        title = stringResource(R.string.qualification_original_content_title),
                        subtitle = stringResource(R.string.qualification_original_content_desc),
                        icon = MonetizationLucideIcons.Star,
                        iconBg = Color(0xFFF3EDFD),
                        iconTint = Color(0xFF7C3AED),
                        isMet = snapshot.originalContentMet
                    )

                    HorizontalDivider(
                        color = cardBorder,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    SimpleRequirementRow(
                        title = stringResource(R.string.qualification_engagement_title),
                        subtitle = stringResource(R.string.qualification_engagement_desc),
                        icon = MonetizationLucideIcons.Heart,
                        iconBg = Color(0xFFFEE2E2),
                        iconTint = Color(0xFFEF4444),
                        isMet = snapshot.isEngagementMet,
                        isInProgress = !snapshot.isEngagementMet
                    )
                }
            }
        }

        // Bottom Footer Banner with Gift Icon and Apply Now Button
        item {
            Surface(
                color = if (isDark) Color(0xFF261E38) else Color(0xFFF5F0FF),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.22f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Gift,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(R.string.qualification_footer_banner),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onApplyNow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (snapshot.isAllMandatoryMet) Color(0xFF8B5CF6) else Color(0xFFE5E7EB),
                            contentColor = if (snapshot.isAllMandatoryMet) Color.White else Color(0xFF9CA3AF)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("qualification_apply_now_button")
                    ) {
                        Text(
                            text = stringResource(R.string.qualification_apply_now),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ==========================================
// 2. MONETIZATION NOT APPROVED (IMAGE 2)
// ==========================================

@Composable
private fun MonetizationNotApprovedSection(
    uiState: TelefamUiState,
    cardBg: Color,
    cardBorder: Color,
    isDark: Boolean,
    onSelectWhyItem: (WhyNotApprovedItem) -> Unit,
    onOpenPolicies: () -> Unit,
    onRecheckQualification: () -> Unit
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("monetization_not_approved_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Reddish Pink Hero Banner with Sad Cloud Asset
        item {
            Surface(
                color = if (isDark) Color(0xFF2C1920) else Color(0xFFFFF0F2),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.18f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_sad_cloud_rejected),
                            contentDescription = stringResource(R.string.not_approved_headline),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.not_approved_headline),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            ),
                            color = Color(0xFFDC2626)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.not_approved_subheadline),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.not_approved_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section: Why it wasn't approved
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.not_approved_why_section),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.not_approved_why_desc),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    WhyNotApprovedRow(
                        title = stringResource(R.string.not_approved_reason_eligibility_title),
                        subtitle = stringResource(R.string.not_approved_reason_eligibility_desc),
                        icon = MonetizationLucideIcons.ShieldCheck,
                        onClick = {
                            onSelectWhyItem(
                                WhyNotApprovedItem(
                                    id = "eligibility",
                                    title = "Eligibility requirements not met",
                                    description = "Your account didn't meet our minimum criteria for followers, watch time or engagement.",
                                    iconType = "ELIGIBILITY",
                                    detailedAdvice = "Ensure you maintain at least 1,000 authentic followers and reach 4,000 minutes of valid watch time on your public videos in the past 60 days."
                                )
                            )
                        }
                    )

                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    WhyNotApprovedRow(
                        title = stringResource(R.string.not_approved_reason_content_title),
                        subtitle = stringResource(R.string.not_approved_reason_content_desc),
                        icon = MonetizationLucideIcons.FileText,
                        onClick = {
                            onSelectWhyItem(
                                WhyNotApprovedItem(
                                    id = "guidelines",
                                    title = "Content doesn't meet our guidelines",
                                    description = "Some of your content may not fully follow our community guidelines or monetization policies.",
                                    iconType = "GUIDELINES",
                                    detailedAdvice = "Review your recently uploaded videos. Remove or trim content that includes third-party copyrighted audio, unoriginal reposts, or controversial elements."
                                )
                            )
                        }
                    )

                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    WhyNotApprovedRow(
                        title = stringResource(R.string.not_approved_reason_history_title),
                        subtitle = stringResource(R.string.not_approved_reason_history_desc),
                        icon = MonetizationLucideIcons.Clock,
                        onClick = {
                            onSelectWhyItem(
                                WhyNotApprovedItem(
                                    id = "history",
                                    title = "More history needed",
                                    description = "We need more time to review your content and account activity.",
                                    iconType = "HISTORY",
                                    detailedAdvice = "Accounts with newer activity benefit from consistent weekly video publishing and organic engagement over 30 to 60 days."
                                )
                            )
                        }
                    )
                }
            }
        }

        // Purple "Learn More" policy banner
        item {
            Surface(
                color = if (isDark) Color(0xFF261E38) else Color(0xFFF5F0FF),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.22f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.BookOpen,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(R.string.not_approved_learn_policies),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onOpenPolicies,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7C3AED)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("not_approved_learn_more_button")
                    ) {
                        Text(
                            text = stringResource(R.string.not_approved_learn_more),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = MonetizationLucideIcons.ExternalLink,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Section: Your progress
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.not_approved_progress_section),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.not_approved_progress_desc),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Followers Progress Row
                    ProgressMetricRow(
                        title = stringResource(R.string.not_approved_followers_label),
                        subtitle = stringResource(R.string.not_approved_followers_sub),
                        valueText = stringResource(
                            R.string.not_approved_followers_count,
                            3450,
                            10000
                        ),
                        icon = MonetizationLucideIcons.Users,
                        iconBg = Color(0xFFE8F8EE),
                        iconTint = Color(0xFF16A34A),
                        progressFraction = 0.345f,
                        progressColor = Color(0xFF16A34A)
                    )

                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 14.dp))

                    // Watch time Progress Row
                    ProgressMetricRow(
                        title = stringResource(R.string.not_approved_watch_time_label),
                        subtitle = stringResource(R.string.not_approved_watch_time_sub),
                        valueText = stringResource(
                            R.string.not_approved_watch_time_count,
                            820,
                            4000
                        ),
                        icon = MonetizationLucideIcons.PlaySquare,
                        iconBg = Color(0xFFF3EDFD),
                        iconTint = Color(0xFF7C3AED),
                        progressFraction = 0.205f,
                        progressColor = Color(0xFF7C3AED)
                    )

                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 14.dp))

                    // Content Compliance Row
                    ProgressMetricRow(
                        title = stringResource(R.string.not_approved_compliance_label),
                        subtitle = stringResource(R.string.not_approved_compliance_sub),
                        valueText = stringResource(R.string.not_approved_compliance_status),
                        icon = MonetizationLucideIcons.TrendingUp,
                        iconBg = Color(0xFFFEF6E7),
                        iconTint = Color(0xFFD97706),
                        progressFraction = 0.55f,
                        progressColor = Color(0xFFD97706)
                    )
                }
            }
        }

        // Encouragement Card with 3D Golden Trophy
        item {
            Surface(
                color = if (isDark) Color(0xFF2E2718) else Color(0xFFFFFBEB),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.24f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFDE68A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Star,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.not_approved_encouragement_title),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = Color(0xFFB45309)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.not_approved_encouragement_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_3d_golden_trophy),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

        // Recheck Qualification Action
        item {
            Button(
                onClick = onRecheckQualification,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
                    .testTag("not_approved_recheck_button")
            ) {
                Text(
                    text = stringResource(R.string.not_approved_reapply_button),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ==========================================
// 3. MONETIZATION APPROVED (IMAGE 3)
// ==========================================

@Composable
private fun MonetizationApprovedSection(
    uiState: TelefamUiState,
    cardBg: Color,
    cardBorder: Color,
    isDark: Boolean,
    onGoToDashboard: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenSubscriptions: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("monetization_approved_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Soft Green Hero Banner with Green Rosette Badge
        item {
            Surface(
                color = if (isDark) Color(0xFF14291E) else Color(0xFFF0FDF4),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.22f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_green_approval_rosette),
                            contentDescription = stringResource(R.string.approved_headline),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.approved_headline),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            ),
                            color = Color(0xFF16A34A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.approved_subheadline),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.approved_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section: What's Approved
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.approved_whats_approved),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.approved_whats_approved_desc),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 4 Feature Cards in a 4-column row (Gifts, Subscriptions, Creator Rewards, Sponsored Content)
        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ApprovedFeaturePillCard(
                        title = stringResource(R.string.approved_feature_gifts),
                        icon = MonetizationLucideIcons.Gift,
                        iconBg = Color(0xFFE8F8EE),
                        iconTint = Color(0xFF16A34A),
                        onClick = onOpenWallet,
                        modifier = Modifier.weight(1f)
                    )

                    ApprovedFeaturePillCard(
                        title = stringResource(R.string.approved_feature_subscriptions),
                        icon = MonetizationLucideIcons.Star,
                        iconBg = Color(0xFFEAF2FF),
                        iconTint = Color(0xFF2563EB),
                        onClick = onOpenSubscriptions,
                        modifier = Modifier.weight(1f)
                    )

                    ApprovedFeaturePillCard(
                        title = stringResource(R.string.approved_feature_creator_rewards),
                        icon = MonetizationLucideIcons.PlaySquare,
                        iconBg = Color(0xFFF3EDFD),
                        iconTint = Color(0xFF7C3AED),
                        onClick = onGoToDashboard,
                        modifier = Modifier.weight(1f)
                    )

                    ApprovedFeaturePillCard(
                        title = stringResource(R.string.approved_feature_sponsored_content),
                        icon = MonetizationLucideIcons.Megaphone,
                        iconBg = Color(0xFFFEF6E7),
                        iconTint = Color(0xFFD97706),
                        onClick = onGoToDashboard,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Action Card: "Start earning now! Go to Dashboard >"
        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F8EE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Wallet,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.approved_start_earning_title),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.approved_start_earning_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onGoToDashboard,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF16A34A)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF16A34A).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("approved_go_to_dashboard_button")
                    ) {
                        Text(
                            text = stringResource(R.string.approved_go_to_dashboard),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Section: Monetization Benefits
        item {
            Text(
                text = stringResource(R.string.approved_benefits_section),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BenefitRow(
                        title = stringResource(R.string.approved_benefit_earn_title),
                        subtitle = stringResource(R.string.approved_benefit_earn_desc),
                        icon = MonetizationLucideIcons.DollarSign,
                        iconBg = Color(0xFFE8F8EE),
                        iconTint = Color(0xFF16A34A),
                        onClick = onGoToDashboard
                    )

                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    BenefitRow(
                        title = stringResource(R.string.approved_benefit_grow_title),
                        subtitle = stringResource(R.string.approved_benefit_grow_desc),
                        icon = MonetizationLucideIcons.TrendingUp,
                        iconBg = Color(0xFFEAF2FF),
                        iconTint = Color(0xFF2563EB),
                        onClick = onGoToDashboard
                    )

                    HorizontalDivider(color = cardBorder, modifier = Modifier.padding(vertical = 12.dp))

                    BenefitRow(
                        title = stringResource(R.string.approved_benefit_community_title),
                        subtitle = stringResource(R.string.approved_benefit_community_desc),
                        icon = MonetizationLucideIcons.Users,
                        iconBg = Color(0xFFFEF6E7),
                        iconTint = Color(0xFFD97706),
                        onClick = onOpenSubscriptions
                    )
                }
            }
        }

        // Footer Card: "You're in safe hands / View Terms"
        item {
            Surface(
                color = if (isDark) Color(0xFF261E38) else Color(0xFFF5F0FF),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.22f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.ShieldCheck,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.approved_safe_hands_title),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.approved_safe_hands_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onOpenTerms,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7C3AED)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("approved_view_terms_button")
                    ) {
                        Text(
                            text = stringResource(R.string.approved_view_terms),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = MonetizationLucideIcons.ExternalLink,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ==========================================
// 4. APPLICATION SUBMITTED / UNDER REVIEW
// ==========================================

@Composable
private fun ApplicationSubmittedSection(
    uiState: TelefamUiState,
    cardBg: Color,
    cardBorder: Color,
    isDark: Boolean,
    onReviewGuidelines: () -> Unit,
    onAdminApprove: () -> Unit,
    onAdminReject: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("application_submitted_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CreatorProfileHeader(uiState = uiState, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }

        item {
            Surface(
                color = if (isDark) Color(0xFF192538) else Color(0xFFF0F7FF),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB).copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.Clock,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stringResource(R.string.monetization_submitted_headline),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.monetization_submitted_desc),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = cardBg,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.monetization_submitted_timeline),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Review Stages in Progress",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ReviewStageItem(
                        title = stringResource(R.string.monetization_submitted_step1),
                        isDone = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ReviewStageItem(
                        title = stringResource(R.string.monetization_submitted_step2),
                        isDone = false
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ReviewStageItem(
                        title = stringResource(R.string.monetization_submitted_step3),
                        isDone = false
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReviewGuidelines,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(stringResource(R.string.monetization_review_guidelines), fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAdminApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simulate Approval", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onAdminReject,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simulate Not Approved", fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ==========================================
// 5. CONTENT MONETIZATION & SHARED EARNINGS
// ==========================================

@Composable
private fun ContentMonetizationDashboardSection(
    uiState: TelefamUiState,
    cardBg: Color,
    cardBorder: Color,
    isDark: Boolean,
    onEditVideo: (VideoMonetizationItem) -> Unit,
    onOpenWallet: () -> Unit,
    onOpenSubscriptions: () -> Unit
) {
    val videos = uiState.videoMonetizationList.ifEmpty {
        // Fallback default list derived from published feed videos if populated
        listOf(
            VideoMonetizationItem(
                videoId = "demo_vid_1",
                title = "Behind the Scenes • Exclusive Tour",
                isMonetized = true,
                monetizedViews = 4520L,
                adImpressions = 3890L,
                adEarningsUsd = 12.45,
                rewardEarningsUsd = 8.20,
                giftEarningsUsd = 15.00,
                rpmUsd = 2.75
            ),
            VideoMonetizationItem(
                videoId = "demo_vid_2",
                title = "Mastering Lighting & Color Grading",
                isMonetized = true,
                monetizedViews = 2100L,
                adImpressions = 1950L,
                adEarningsUsd = 6.10,
                rewardEarningsUsd = 4.30,
                giftEarningsUsd = 5.00,
                rpmUsd = 2.90
            )
        )
    }

    val totalVideoEarnings = videos.sumOf { it.totalEarningsUsd }
    val totalViews = videos.sumOf { it.monetizedViews }
    val totalAdRevenue = videos.sumOf { it.adEarningsUsd }
    val totalGifts = videos.sumOf { it.giftEarningsUsd }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("content_monetization_dashboard_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Earnings Overview Card
        item {
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = stringResource(R.string.monetization_total_video_earnings),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", totalVideoEarnings)}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            label = stringResource(R.string.monetization_ad_revenue),
                            value = "$${String.format(Locale.US, "%.2f", totalAdRevenue)}",
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            label = stringResource(R.string.monetization_video_gifts),
                            value = "$${String.format(Locale.US, "%.2f", totalGifts)}",
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            label = "Views",
                            value = NumberFormat.getNumberInstance(Locale.getDefault()).format(totalViews),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenWallet,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Withdraw via Wallet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onOpenSubscriptions,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Manage Tiers", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Shared Videos List
        item {
            Text(
                text = stringResource(R.string.monetization_total_monetized_videos),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        items(videos, key = { it.videoId }) { video ->
            Surface(
                color = cardBg,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2563EB).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MonetizationLucideIcons.PlaySquare,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${video.monetizedViews} views • RPM $${video.rpmUsd}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = if (video.isMonetized) Color(0xFFE8F8EE) else Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (video.isMonetized) "Monetized" else "Off",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (video.isMonetized) Color(0xFF16A34A) else Color(0xFF6B7280),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Earned $${String.format(Locale.US, "%.2f", video.totalEarningsUsd)}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF16A34A),
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { onEditVideo(video) },
                        modifier = Modifier.testTag("manage_video_${video.videoId}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.monetization_manage_video),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ==========================================
// SUB-COMPONENTS & ROWS
// ==========================================

@Composable
private fun CreatorProfileHeader(
    uiState: TelefamUiState,
    modifier: Modifier = Modifier
) {
    val saved = uiState.savedProfile
    val fullName = saved?.fullName?.takeIf { it.isNotBlank() } ?: "Rahul Verma"
    val username = saved?.username?.takeIf { it.isNotBlank() } ?: "rahul.verma"
    val avatarPath = saved?.avatarLocalPath ?: saved?.avatarCdnUrl
    val hasLocalFile = remember(avatarPath) {
        !avatarPath.isNullOrBlank() && File(avatarPath).exists()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasLocalFile && avatarPath != null) {
            AsyncImage(
                model = File(avatarPath),
                contentDescription = fullName,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else if (!avatarPath.isNullOrBlank()) {
            AsyncImage(
                model = avatarPath,
                contentDescription = fullName,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fullName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF374151)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Red Verified Checkmark Badge matching reference images
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verified",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "@$username",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // "Creator" pill in light pink/red background
            Surface(
                color = Color(0xFFFEE2E2),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Creator",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFFEF4444)
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun RequirementRowWithProgress(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    current: Long,
    target: Long,
    progressFraction: Float,
    progressColor: Color,
    isMet: Boolean,
    isNumeric: Boolean = false,
    unit: String = ""
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Met / Not Met Badge
            StatusPill(isMet = isMet)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = progressColor,
            trackColor = progressColor.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round
        )

        if (isNumeric) {
            Spacer(modifier = Modifier.height(4.dp))
            val currentFormatted = numberFormat.format(current)
            val targetFormatted = numberFormat.format(target)
            val label = if (unit.isNotBlank()) "$currentFormatted / $targetFormatted $unit" else "$currentFormatted / $targetFormatted"
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun SimpleRequirementRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    isMet: Boolean,
    isInProgress: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        StatusPill(isMet = isMet, isInProgress = isInProgress)
    }
}

@Composable
private fun StatusPill(
    isMet: Boolean,
    isInProgress: Boolean = false
) {
    val bg = when {
        isMet -> Color(0xFFE8F8EE)
        isInProgress -> Color(0xFFF3F4F6)
        else -> Color(0xFFFEE2E2)
    }
    val fg = when {
        isMet -> Color(0xFF16A34A)
        isInProgress -> Color(0xFF6B7280)
        else -> Color(0xFFEF4444)
    }
    val label = when {
        isMet -> "Met ✓"
        isInProgress -> "In Progress"
        else -> "Not Met"
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = fg
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun WhyNotApprovedRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEE2E2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ProgressMetricRow(
    title: String,
    subtitle: String,
    valueText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    progressFraction: Float,
    progressColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = valueText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = progressColor
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = progressColor,
            trackColor = progressColor.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun ApprovedFeaturePillCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Enabled",
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stringResource(R.string.approved_feature_enabled),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun BenefitRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ReviewStageItem(
    title: String,
    isDone: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isDone) Color(0xFF16A34A) else Color(0xFFE5E7EB)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (isDone) Color.White else Color(0xFF9CA3AF),
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ==========================================
// MODAL SHEETS & DIALOGS
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApplicationInterestSheet(
    uiState: TelefamUiState,
    onDismiss: () -> Unit,
    onSubmit: (category: String, statement: String, email: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf("Entertainment") }
    var statementText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf(uiState.email.ifBlank { "creator@telefam.com" }) }
    var agreedTerms by remember { mutableStateOf(true) }

    val categories = listOf("Entertainment", "Lifestyle", "Education", "Tech", "Comedy", "Music", "Design & Art")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("application_interest_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.monetization_apply_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.monetization_category_label),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(3).forEach { cat ->
                    val isSelected = selectedCategory == cat
                    OutlinedButton(
                        onClick = { selectedCategory = cat },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) Color(0xFFEF4444).copy(alpha = 0.1f) else Color.Transparent,
                            contentColor = if (isSelected) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(cat, fontSize = 11.sp, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = statementText,
                onValueChange = { statementText = it },
                label = { Text(stringResource(R.string.monetization_statement_label)) },
                placeholder = { Text(stringResource(R.string.monetization_statement_hint)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                label = { Text(stringResource(R.string.monetization_contact_email_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { agreedTerms = !agreedTerms }
            ) {
                Checkbox(
                    checked = agreedTerms,
                    onCheckedChange = { agreedTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFEF4444))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.monetization_terms_agree),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (agreedTerms) {
                        onSubmit(selectedCategory, statementText, emailText)
                    }
                },
                enabled = agreedTerms,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_monetization_interest_button")
            ) {
                Text(
                    text = stringResource(R.string.monetization_submit_application),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WhyNotApprovedDetailSheet(
    item: WhyNotApprovedItem,
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
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Recommendation for Next Application:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.detailedAdvice,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text("Understood")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoMonetizationSettingsSheet(
    item: VideoMonetizationItem,
    onDismiss: () -> Unit,
    onSave: (VideoMonetizationItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isMonetized by remember { mutableStateOf(item.isMonetized) }
    var inStreamAds by remember { mutableStateOf(item.instreamAdsEnabled) }
    var videoGifts by remember { mutableStateOf(item.videoGiftsEnabled) }
    var creatorRewards by remember { mutableStateOf(item.creatorRewardsEligible) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.monetization_manage_video),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Monetization", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Switch(checked = isMonetized, onCheckedChange = { isMonetized = it })
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.monetization_instream_ads), style = MaterialTheme.typography.bodyMedium)
                Switch(checked = inStreamAds, onCheckedChange = { inStreamAds = it }, enabled = isMonetized)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.monetization_gifts_allowed), style = MaterialTheme.typography.bodyMedium)
                Switch(checked = videoGifts, onCheckedChange = { videoGifts = it }, enabled = isMonetized)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.monetization_creator_fund_optin), style = MaterialTheme.typography.bodyMedium)
                Switch(checked = creatorRewards, onCheckedChange = { creatorRewards = it }, enabled = isMonetized)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(
                        item.copy(
                            isMonetized = isMonetized,
                            instreamAdsEnabled = inStreamAds,
                            videoGiftsEnabled = videoGifts,
                            creatorRewardsEligible = creatorRewards
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(stringResource(R.string.monetization_save_settings), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MonetizationHelpDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Creator Monetization Help",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "• Eligibility: Requires 1,000 followers, 4,000 minutes of watch time on public content, and 10 published videos.\n\n• Payment: Payouts are transferred automatically to your verified bank or mobile wallet in USD.\n\n• Integrity: Content is scanned for DMCA copyright compliance, authenticity, and community safety.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun MonetizationTermsDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Creator Monetization Terms",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "By participating in Telefam Creator Monetization, you agree to:\n\n1. Retain ownership of original video works and maintain valid licenses for all background audio.\n2. Receive payouts in accordance with our standard payment thresholds and tax withholdings.\n3. Abstain from artificial engagement, view botting, or click farms.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Acknowledge")
                }
            }
        }
    }
}
