package com.telefam.company.app.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.OutlinedFlag
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import com.telefam.company.app.data.AccountSettingsAndSecurityEntity
import com.telefam.company.app.domain.AppPermissionType
import com.telefam.company.app.domain.AppThemeOption
import com.telefam.company.app.domain.CountryDialCode
import com.telefam.company.app.domain.HelpSupportArticle
import com.telefam.company.app.domain.LegalPolicyDocument
import com.telefam.company.app.domain.PrivacyControlField
import com.telefam.company.app.domain.SafetyTopicGuide
import com.telefam.company.app.domain.SettingsSubScreen
import com.telefam.company.app.domain.SocialPrivacyOption
import com.telefam.company.app.ui.TelefamUiState
import com.telefam.company.app.ui.components.ImageCropperDialog

@Composable
fun SettingsAndPrivacyScreen(
    uiState: TelefamUiState,
    onBackClick: () -> Unit,
    onNavigateSubScreen: (SettingsSubScreen) -> Unit,
    onClearBanners: () -> Unit,
    onRetryLastAction: () -> Unit = {},
    onOpenChatPrivacyAndAppearance: () -> Unit,
    // Profile editing callbacks reused in Account
    onFullNameChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onApplySuggestedUsername: (String) -> Unit,
    onCountrySelected: (CountryDialCode) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onAvatarUriPicked: (Uri) -> Unit,
    onCancelCrop: () -> Unit,
    onConfirmCrop: (scale: Float, offsetXNorm: Float, offsetYNorm: Float, rotationDeg: Float) -> Unit,
    onSaveOwnerProfileEdits: (String) -> Unit,
    // Account & Security callbacks
    onSendVerificationOtp: (String) -> Unit,
    onConfirmChangeEmail: (newEmail: String, otpCode: String) -> Unit,
    onSubmitPasswordChange: (currentPass: String, newPass: String, confirmPass: String) -> Unit,
    onSaveRecoverySettings: (recoveryEmail: String, recoveryPhone: String) -> Unit,
    onConfirmDeactivateAccount: (reason: String, autoReenableDays: Int, otpCode: String) -> Unit,
    onConfirmDeleteAccount30Days: (reason: String, otpCode: String) -> Unit,
    onEnableTwoStep: (method: String, code: String) -> Unit,
    onDisableTwoStep: () -> Unit,
    onRegisterPasskey: (deviceLabel: String) -> Unit,
    onRevokePasskey: () -> Unit,
    onLogoutDevice: (deviceRecordId: String) -> Unit,
    onRevokeConnectedApp: (connectionId: String) -> Unit,
    onOpenPermissionDetail: (AppPermissionType) -> Unit,
    onSystemPermissionUpdated: (AppPermissionType, Boolean) -> Unit,
    // Privacy & Content callbacks
    onTogglePrivateAccount: (Boolean) -> Unit,
    onOpenPrivacyRuleSelector: (PrivacyControlField) -> Unit,
    onSelectPrivacyRule: (PrivacyControlField, SocialPrivacyOption) -> Unit,
    onSetAppThemeMode: (AppThemeOption) -> Unit = {},
    onUpdateAccountSettings: (
        confirmationMessage: String,
        auditAction: String,
        transform: (AccountSettingsAndSecurityEntity) -> AccountSettingsAndSecurityEntity
    ) -> Unit,
    // Share Profile & QR callbacks
    onDownloadQrCode: () -> Unit,
    onRegenerateQrCode: () -> Unit = {},
    onOpenPublicUserProfile: (String) -> Unit,
    // Blocked Accounts callbacks
    onBlockedSearchQueryChanged: (String) -> Unit,
    onUnblockAccount: (userId: String, username: String) -> Unit,
    // Report a Problem & Support callbacks
    onSubmitTicket: (
        ticketType: String,
        category: String,
        subject: String,
        description: String,
        screenshotUri: Uri?
    ) -> Unit,
    onClearSubmittedTicket: () -> Unit,
    onUpdateSupportTicket: (ticketId: String, followUpText: String, markResolved: Boolean) -> Unit = { _, _, _ -> },
    onOpenSafetyGuide: (SafetyTopicGuide) -> Unit,
    onHelpSearchQueryChanged: (String) -> Unit,
    onOpenHelpArticle: (HelpSupportArticle) -> Unit,
    onSubmitHelpFeedback: (articleId: String, isHelpful: Boolean) -> Unit = { _, _ -> },
    onOpenLegalDocument: (LegalPolicyDocument) -> Unit,
    onAcknowledgeLegalDocument: (LegalPolicyDocument) -> Unit = {},
    onDownloadLegalDocument: (LegalPolicyDocument) -> Unit = {},
    onOpenAboutDetail: (String) -> Unit,
    onConfirmLogOut: () -> Unit
) {
    var showLogoutConfirmDialog by rememberSaveable { mutableStateOf(false) }

    BackHandler {
        if (uiState.pendingCropBitmap != null) {
            onCancelCrop()
        } else {
            onBackClick()
        }
    }

    val isDark = isTelefamDarkTheme()
    val pageBackground = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF6F7F9)
    val cardBackground = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    val sectionTitleColor = if (isDark) Color(0xFF9BA5B4) else Color(0xFF5B6B7F)

    val topTitle = when (uiState.settingsSubScreen) {
        SettingsSubScreen.ROOT -> stringResource(R.string.settings_and_privacy_title)
        SettingsSubScreen.ACCOUNT -> "Account"
        SettingsSubScreen.ACCOUNT_EDIT_USERNAME -> "Edit username"
        SettingsSubScreen.ACCOUNT_EDIT_NAME -> "Edit name"
        SettingsSubScreen.ACCOUNT_EDIT_EMAIL -> "Email address"
        SettingsSubScreen.ACCOUNT_EDIT_PHONE -> "Phone number"
        SettingsSubScreen.ACCOUNT_CHANGE_PASSWORD -> "Change password"
        SettingsSubScreen.ACCOUNT_RECOVERY -> "Account recovery"
        SettingsSubScreen.ACCOUNT_DEACTIVATE -> "Deactivate account"
        SettingsSubScreen.ACCOUNT_DELETE -> "Delete account"
        SettingsSubScreen.PRIVACY_ACCOUNT -> "Privacy"
        SettingsSubScreen.SECURITY_AND_PERMISSIONS -> "Security & permissions"
        SettingsSubScreen.SECURITY_TWO_STEP -> "2-step verification"
        SettingsSubScreen.SECURITY_PASSKEY -> "Passkey"
        SettingsSubScreen.SECURITY_MANAGE_DEVICES -> "Manage devices"
        SettingsSubScreen.SECURITY_LOGIN_HISTORY -> "Login history"
        SettingsSubScreen.SECURITY_ALERTS -> "Security alerts"
        SettingsSubScreen.SECURITY_PERMISSION_DETAIL -> uiState.activePermissionType.title
        SettingsSubScreen.SECURITY_CONNECTED_APPS -> "Manage connected apps"
        SettingsSubScreen.SHARE_PROFILE -> "Share profile"
        SettingsSubScreen.NOTIFICATIONS -> "Notifications"
        SettingsSubScreen.NOTIFICATIONS_PUSH_DETAIL -> "Push notifications"
        SettingsSubScreen.NOTIFICATIONS_EMAIL_DETAIL -> "Email notifications"
        SettingsSubScreen.CONTENT_PREFERENCES -> "Content preferences"
        SettingsSubScreen.APPEARANCE_THEME -> "Appearance"
        SettingsSubScreen.CONTENT_LANGUAGES -> "Content languages"
        SettingsSubScreen.CONTENT_TOPICS -> "Topics and interests"
        SettingsSubScreen.CONTENT_SUGGESTED -> "Suggested content"
        SettingsSubScreen.CONTENT_VIDEO_QUALITY -> "Video quality"
        SettingsSubScreen.CONTENT_AUTOPLAY -> "Autoplay"
        SettingsSubScreen.CONTENT_FILTERING -> "Content filtering"
        SettingsSubScreen.PRIVACY_CONTROLS -> "Privacy controls"
        SettingsSubScreen.PRIVACY_RULE_SELECTOR -> uiState.activePrivacyControlField.title
        SettingsSubScreen.BLOCKED_ACCOUNTS -> "Blocked accounts"
        SettingsSubScreen.REPORT_PROBLEM -> "Report a problem"
        SettingsSubScreen.SAFETY_CENTER -> "Safety Center"
        SettingsSubScreen.SAFETY_TOPIC_DETAIL -> uiState.activeSafetyGuide.title
        SettingsSubScreen.HELP_AND_SUPPORT -> "Help & support"
        SettingsSubScreen.HELP_ARTICLE_DETAIL -> uiState.activeHelpArticle.categoryTitle
        SettingsSubScreen.HELP_CONTACT_SUPPORT -> "Contact support"
        SettingsSubScreen.HELP_MY_REQUESTS -> "My support requests"
        SettingsSubScreen.TERMS_AND_POLICIES -> "Terms and policies"
        SettingsSubScreen.LEGAL_DOCUMENT_DETAIL -> uiState.activeLegalDocument.title
        SettingsSubScreen.ABOUT -> "About"
        SettingsSubScreen.ABOUT_DETAIL -> when (uiState.activeAboutDetailKey) {
            "WHATS_NEW" -> "What's new"
            "COPYRIGHT" -> "Copyright & open source"
            else -> "About the app"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackground)
            .testTag("settings_and_privacy_root")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Header Bar matching reference image: Deep red bar with rounded bottom corners, white back arrow '<' and centered bold title
            Surface(
                color = TelefamRed,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .widthIn(max = 720.dp)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(48.dp)
                            .testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.back_to_login),
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Text(
                        text = topTitle,
                        fontSize = 18.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 52.dp)
                            .testTag("settings_top_bar_title")
                    )
                }
            }

            // Status / Error Banner with Retry Support
            SettingsFeedbackBanners(
                statusMessage = uiState.settingsStatusBanner ?: uiState.ownerStatusBanner,
                errorMessage = uiState.settingsErrorBanner ?: uiState.errorMessage,
                canRetryError = uiState.canRetrySettingsError,
                onRetry = onRetryLastAction,
                onDismiss = onClearBanners
            )

            // Main Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 720.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                when (uiState.settingsSubScreen) {
                    SettingsSubScreen.ROOT -> {
                        SettingsRootContent(
                            uiState = uiState,
                            cardBackground = cardBackground,
                            sectionTitleColor = sectionTitleColor,
                            isDark = isDark,
                            onNavigateSubScreen = onNavigateSubScreen,
                            onLogOutClick = { showLogoutConfirmDialog = true }
                        )
                    }

                    SettingsSubScreen.ACCOUNT,
                    SettingsSubScreen.ACCOUNT_EDIT_USERNAME,
                    SettingsSubScreen.ACCOUNT_EDIT_NAME,
                    SettingsSubScreen.ACCOUNT_EDIT_EMAIL,
                    SettingsSubScreen.ACCOUNT_EDIT_PHONE,
                    SettingsSubScreen.ACCOUNT_CHANGE_PASSWORD,
                    SettingsSubScreen.ACCOUNT_RECOVERY,
                    SettingsSubScreen.ACCOUNT_DEACTIVATE,
                    SettingsSubScreen.ACCOUNT_DELETE,
                    SettingsSubScreen.PRIVACY_ACCOUNT,
                    SettingsSubScreen.SHARE_PROFILE -> {
                        SettingsAccountSecurityAndShareRouter(
                            uiState = uiState,
                            cardBackground = cardBackground,
                            sectionTitleColor = sectionTitleColor,
                            onNavigateSubScreen = onNavigateSubScreen,
                            onFullNameChanged = onFullNameChanged,
                            onUsernameChanged = onUsernameChanged,
                            onApplySuggestedUsername = onApplySuggestedUsername,
                            onCountrySelected = onCountrySelected,
                            onPhoneNumberChanged = onPhoneNumberChanged,
                            onAvatarUriPicked = onAvatarUriPicked,
                            onSaveOwnerProfileEdits = onSaveOwnerProfileEdits,
                            onSendVerificationOtp = onSendVerificationOtp,
                            onConfirmChangeEmail = onConfirmChangeEmail,
                            onSubmitPasswordChange = onSubmitPasswordChange,
                            onSaveRecoverySettings = onSaveRecoverySettings,
                            onConfirmDeactivateAccount = onConfirmDeactivateAccount,
                            onConfirmDeleteAccount30Days = onConfirmDeleteAccount30Days,
                            onTogglePrivateAccount = onTogglePrivateAccount,
                            onOpenPrivacyRuleSelector = onOpenPrivacyRuleSelector,
                            onUpdateAccountSettings = onUpdateAccountSettings,
                            onDownloadQrCode = onDownloadQrCode,
                            onRegenerateQrCode = onRegenerateQrCode,
                            onOpenPublicUserProfile = onOpenPublicUserProfile
                        )
                    }

                    SettingsSubScreen.NOTIFICATIONS,
                    SettingsSubScreen.NOTIFICATIONS_PUSH_DETAIL,
                    SettingsSubScreen.NOTIFICATIONS_EMAIL_DETAIL,
                    SettingsSubScreen.CONTENT_PREFERENCES,
                    SettingsSubScreen.APPEARANCE_THEME,
                    SettingsSubScreen.CONTENT_LANGUAGES,
                    SettingsSubScreen.CONTENT_TOPICS,
                    SettingsSubScreen.CONTENT_SUGGESTED,
                    SettingsSubScreen.CONTENT_VIDEO_QUALITY,
                    SettingsSubScreen.CONTENT_AUTOPLAY,
                    SettingsSubScreen.CONTENT_FILTERING,
                    SettingsSubScreen.PRIVACY_CONTROLS,
                    SettingsSubScreen.PRIVACY_RULE_SELECTOR,
                    SettingsSubScreen.BLOCKED_ACCOUNTS,
                    SettingsSubScreen.REPORT_PROBLEM,
                    SettingsSubScreen.SAFETY_CENTER,
                    SettingsSubScreen.SAFETY_TOPIC_DETAIL,
                    SettingsSubScreen.HELP_AND_SUPPORT,
                    SettingsSubScreen.HELP_ARTICLE_DETAIL,
                    SettingsSubScreen.HELP_CONTACT_SUPPORT,
                    SettingsSubScreen.HELP_MY_REQUESTS,
                    SettingsSubScreen.TERMS_AND_POLICIES,
                    SettingsSubScreen.LEGAL_DOCUMENT_DETAIL,
                    SettingsSubScreen.ABOUT,
                    SettingsSubScreen.ABOUT_DETAIL -> {
                        SettingsContentSafetyAndSupportRouter(
                            uiState = uiState,
                            cardBackground = cardBackground,
                            sectionTitleColor = sectionTitleColor,
                            onNavigateSubScreen = onNavigateSubScreen,
                            onOpenChatPrivacyAndAppearance = onOpenChatPrivacyAndAppearance,
                            onOpenPrivacyRuleSelector = onOpenPrivacyRuleSelector,
                            onSelectPrivacyRule = onSelectPrivacyRule,
                            onSetAppThemeMode = onSetAppThemeMode,
                            onUpdateAccountSettings = onUpdateAccountSettings,
                            onBlockedSearchQueryChanged = onBlockedSearchQueryChanged,
                            onUnblockAccount = onUnblockAccount,
                            onSubmitTicket = onSubmitTicket,
                            onClearSubmittedTicket = onClearSubmittedTicket,
                            onUpdateSupportTicket = onUpdateSupportTicket,
                            onOpenSafetyGuide = onOpenSafetyGuide,
                            onHelpSearchQueryChanged = onHelpSearchQueryChanged,
                            onOpenHelpArticle = onOpenHelpArticle,
                            onSubmitHelpFeedback = onSubmitHelpFeedback,
                            onOpenLegalDocument = onOpenLegalDocument,
                            onAcknowledgeLegalDocument = onAcknowledgeLegalDocument,
                            onDownloadLegalDocument = onDownloadLegalDocument,
                            onOpenAboutDetail = onOpenAboutDetail
                        )
                    }

                    else -> {
                        SettingsRootContent(
                            uiState = uiState,
                            cardBackground = cardBackground,
                            sectionTitleColor = sectionTitleColor,
                            isDark = isDark,
                            onNavigateSubScreen = onNavigateSubScreen,
                            onLogOutClick = { showLogoutConfirmDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        // Photo Crop Overlay when user changes Profile Photo from Account screen
        if (uiState.pendingCropBitmap != null) {
            ImageCropperDialog(
                bitmap = uiState.pendingCropBitmap,
                onCancel = onCancelCrop,
                onConfirmCrop = { scale, offX, offY, rot ->
                    onConfirmCrop(scale, offX, offY, rot)
                }
            )
        }
    }

    // Log out Confirmation Dialog matching specification:
    // "Log out? Are you sure you want to log out of this account? [Cancel] [Log out]"
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.settings_logout_confirm_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.settings_logout_confirm_message),
                    fontSize = 14.5.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutConfirmDialog = false },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("settings_logout_cancel_button")
                ) {
                    Text(
                        text = stringResource(R.string.crop_cancel_button),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onConfirmLogOut()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TelefamRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("settings_logout_confirm_button")
                ) {
                    Text(
                        text = stringResource(R.string.settings_logout_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

/**
 * Settings Badge Icon types matching reference image `/public/assets/aistudio/settings_compressed_10kb_dfc53255.jpg`
 */
enum class SettingsBadgeIcon {
    ACCOUNT,
    PRIVACY_SHIELD,
    SHARE_PROFILE,
    NOTIFICATIONS,
    CONTENT_HEART,
    PRIVACY_LOCK,
    BLOCKED_ACCOUNTS,
    REPORT_PROBLEM,
    SAFETY_CENTER,
    HELP_SUPPORT,
    TERMS_POLICIES,
    ABOUT
}

/**
 * Solid red circular badge with pure vector SVG icon drawn via Compose Canvas.
 * Exactly matches the circular red badges in the reference image.
 */
@Composable
fun SettingsBadge(
    icon: SettingsBadgeIcon,
    modifier: Modifier = Modifier,
    badgeColor: Color = TelefamRed,
    tint: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(badgeColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(19.dp)) {
            val w = size.width
            val h = size.height
            val sw = 1.9.dp.toPx()
            val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
            fun sx(v: Float) = (v / 24f) * w
            fun sy(v: Float) = (v / 24f) * h

            when (icon) {
                SettingsBadgeIcon.ACCOUNT -> {
                    drawCircle(color = tint, radius = sx(3.8f), center = Offset(sx(12f), sy(7.5f)), style = Fill)
                    val shoulders = Path().apply {
                        moveTo(sx(4.5f), sy(19.5f))
                        cubicTo(sx(5f), sy(14.5f), sx(8.5f), sy(13f), sx(12f), sy(13f))
                        cubicTo(sx(15.5f), sy(13f), sx(19f), sy(14.5f), sx(19.5f), sy(19.5f))
                        close()
                    }
                    drawPath(shoulders, color = tint, style = Fill)
                }

                SettingsBadgeIcon.PRIVACY_SHIELD, SettingsBadgeIcon.SAFETY_CENTER -> {
                    val shield = Path().apply {
                        moveTo(sx(12f), sy(2.5f))
                        lineTo(sx(19f), sy(5.5f))
                        lineTo(sx(19f), sy(11f))
                        cubicTo(sx(19f), sy(16.5f), sx(12f), sy(21.2f), sx(12f), sy(21.2f))
                        cubicTo(sx(12f), sy(21.2f), sx(5f), sy(16.5f), sx(5f), sy(11f))
                        lineTo(sx(5f), sy(5.5f))
                        close()
                    }
                    drawPath(shield, color = tint, style = stroke)
                    val check = Path().apply {
                        moveTo(sx(9f), sy(11.5f))
                        lineTo(sx(11.2f), sy(14f))
                        lineTo(sx(15.2f), sy(9.5f))
                    }
                    drawPath(check, color = tint, style = stroke)
                }

                SettingsBadgeIcon.SHARE_PROFILE -> {
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(sx(3f), sy(5.5f)),
                        size = Size(sx(18f), sy(13f)),
                        cornerRadius = CornerRadius(sx(2.5f), sy(2.5f)),
                        style = stroke
                    )
                    drawLine(color = tint, start = Offset(sx(3f), sy(10f)), end = Offset(sx(21f), sy(10f)), strokeWidth = sw, cap = StrokeCap.Round)
                    drawLine(color = tint, start = Offset(sx(6f), sy(14.5f)), end = Offset(sx(10f), sy(14.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                    drawLine(color = tint, start = Offset(sx(13f), sy(14.5f)), end = Offset(sx(17f), sy(14.5f)), strokeWidth = sw, cap = StrokeCap.Round)
                }

                SettingsBadgeIcon.NOTIFICATIONS -> {
                    val bell = Path().apply {
                        moveTo(sx(12f), sy(2.5f))
                        cubicTo(sx(8.5f), sy(2.5f), sx(6f), sy(5.5f), sx(6f), sy(9f))
                        lineTo(sx(6f), sy(14.5f))
                        lineTo(sx(4f), sy(16.5f))
                        lineTo(sx(20f), sy(16.5f))
                        lineTo(sx(18f), sy(14.5f))
                        lineTo(sx(18f), sy(9f))
                        cubicTo(sx(18f), sy(5.5f), sx(15.5f), sy(2.5f), sx(12f), sy(2.5f))
                        close()
                    }
                    drawPath(bell, color = tint, style = stroke)
                    val clapper = Path().apply {
                        moveTo(sx(10f), sy(17f))
                        cubicTo(sx(10f), sy(19.2f), sx(14f), sy(19.2f), sx(14f), sy(17f))
                    }
                    drawPath(clapper, color = tint, style = stroke)
                }

                SettingsBadgeIcon.CONTENT_HEART -> {
                    val heart = Path().apply {
                        moveTo(sx(12f), sy(20f))
                        cubicTo(sx(4.5f), sy(14f), sx(2.5f), sy(9f), sx(5.5f), sy(5.5f))
                        cubicTo(sx(8f), sy(3.5f), sx(10.5f), sy(4.5f), sx(12f), sy(6.8f))
                        cubicTo(sx(13.5f), sy(4.5f), sx(16f), sy(3.5f), sx(18.5f), sy(5.5f))
                        cubicTo(sx(21.5f), sy(9f), sx(19.5f), sy(14f), sx(12f), sy(20f))
                        close()
                    }
                    drawPath(heart, color = tint, style = stroke)
                }

                SettingsBadgeIcon.PRIVACY_LOCK -> {
                    val shackle = Path().apply {
                        moveTo(sx(7.5f), sy(10.5f))
                        lineTo(sx(7.5f), sy(7f))
                        cubicTo(sx(7.5f), sy(4.2f), sx(16.5f), sy(4.2f), sx(16.5f), sy(7f))
                        lineTo(sx(16.5f), sy(10.5f))
                    }
                    drawPath(shackle, color = tint, style = stroke)
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(sx(5f), sy(10.5f)),
                        size = Size(sx(14f), sy(10.5f)),
                        cornerRadius = CornerRadius(sx(2.5f), sy(2.5f)),
                        style = stroke
                    )
                    drawCircle(color = tint, radius = sx(1.6f), center = Offset(sx(12f), sy(15.2f)), style = Fill)
                }

                SettingsBadgeIcon.BLOCKED_ACCOUNTS -> {
                    drawCircle(color = tint, radius = sx(8.5f), center = Offset(sx(12f), sy(12f)), style = stroke)
                    drawLine(color = tint, start = Offset(sx(6f), sy(6f)), end = Offset(sx(18f), sy(18f)), strokeWidth = sw, cap = StrokeCap.Round)
                }

                SettingsBadgeIcon.REPORT_PROBLEM -> {
                    drawLine(color = tint, start = Offset(sx(5f), sy(3f)), end = Offset(sx(5f), sy(21f)), strokeWidth = sw, cap = StrokeCap.Round)
                    val flag = Path().apply {
                        moveTo(sx(5f), sy(4f))
                        lineTo(sx(19f), sy(9f))
                        lineTo(sx(5f), sy(14f))
                        close()
                    }
                    drawPath(flag, color = tint, style = stroke)
                }

                SettingsBadgeIcon.HELP_SUPPORT -> {
                    drawCircle(color = tint, radius = sx(8.5f), center = Offset(sx(12f), sy(12f)), style = stroke)
                    val q = Path().apply {
                        moveTo(sx(9.5f), sy(8.5f))
                        cubicTo(sx(9.5f), sy(6.5f), sx(14.5f), sy(6.5f), sx(14.5f), sy(9f))
                        cubicTo(sx(14.5f), sy(11f), sx(12f), sy(11.5f), sx(12f), sy(13.2f))
                    }
                    drawPath(q, color = tint, style = stroke)
                    drawCircle(color = tint, radius = sx(1.1f), center = Offset(sx(12f), sy(16f)), style = Fill)
                }

                SettingsBadgeIcon.TERMS_POLICIES -> {
                    val doc = Path().apply {
                        moveTo(sx(5f), sy(3f))
                        lineTo(sx(14f), sy(3f))
                        lineTo(sx(19f), sy(8f))
                        lineTo(sx(19f), sy(21f))
                        lineTo(sx(5f), sy(21f))
                        close()
                    }
                    drawPath(doc, color = tint, style = stroke)
                    drawLine(color = tint, start = Offset(sx(14f), sy(3f)), end = Offset(sx(14f), sy(8f)), strokeWidth = sw, cap = StrokeCap.Round)
                    drawLine(color = tint, start = Offset(sx(14f), sy(8f)), end = Offset(sx(19f), sy(8f)), strokeWidth = sw, cap = StrokeCap.Round)
                    drawLine(color = tint, start = Offset(sx(8.5f), sy(12f)), end = Offset(sx(15.5f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
                    drawLine(color = tint, start = Offset(sx(8.5f), sy(16f)), end = Offset(sx(13.5f), sy(16f)), strokeWidth = sw, cap = StrokeCap.Round)
                }

                SettingsBadgeIcon.ABOUT -> {
                    drawCircle(color = tint, radius = sx(8.5f), center = Offset(sx(12f), sy(12f)), style = stroke)
                    drawCircle(color = tint, radius = sx(1.2f), center = Offset(sx(12f), sy(8f)), style = Fill)
                    drawLine(color = tint, start = Offset(sx(12f), sy(11.5f)), end = Offset(sx(12f), sy(16f)), strokeWidth = sw, cap = StrokeCap.Round)
                }
            }
        }
    }
}

/**
 * Pure SVG vector Logout Icon matching the reference image.
 */
@Composable
fun LogoutIconVector(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val sw = 2.dp.toPx()
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun sx(v: Float) = (v / 24f) * w
        fun sy(v: Float) = (v / 24f) * h

        // Bracket / door outline on left
        val bracket = Path().apply {
            moveTo(sx(9f), sy(4f))
            lineTo(sx(4.5f), sy(4f))
            lineTo(sx(4.5f), sy(20f))
            lineTo(sx(9f), sy(20f))
        }
        drawPath(bracket, color = tint, style = stroke)

        // Arrow pointing right
        drawLine(color = tint, start = Offset(sx(9.5f), sy(12f)), end = Offset(sx(19.5f), sy(12f)), strokeWidth = sw, cap = StrokeCap.Round)
        val arrowHead = Path().apply {
            moveTo(sx(15.5f), sy(8f))
            lineTo(sx(19.5f), sy(12f))
            lineTo(sx(15.5f), sy(16f))
        }
        drawPath(arrowHead, color = tint, style = stroke)
    }
}

/**
 * Exact 1:1 Root Layout matching uploaded reference image `/public/assets/aistudio/settings_compressed_10kb_dfc53255.jpg`:
 * - Section 1: Account (Account, Privacy, Share profile) - Security permanently removed
 * - Section 2: Content & activity (Notifications, Content preferences)
 * - Section 3: Privacy and safety (Privacy, Blocked accounts, Report a problem, Safety Center)
 * - Section 4: Support & about (Help & support, Terms and policies, About)
 * - Bottom Pill: Solid Red [ -> Log out ]
 */
@Composable
private fun SettingsRootContent(
    uiState: TelefamUiState,
    cardBackground: Color,
    sectionTitleColor: Color,
    isDark: Boolean,
    onNavigateSubScreen: (SettingsSubScreen) -> Unit,
    onLogOutClick: () -> Unit
) {
    // Section 1: Account
    SettingsSectionHeader(
        title = "Account",
        color = sectionTitleColor
    )
    SettingsGroupCard(cardBackground = cardBackground) {
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.ACCOUNT,
            title = "Account",
            subtitle = "Profile, account info, password, recovery",
            onClick = { onNavigateSubScreen(SettingsSubScreen.ACCOUNT) },
            showDivider = true,
            testTag = "settings_row_account"
        )
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.PRIVACY_SHIELD,
            title = "Privacy",
            subtitle = "Private account, blocked accounts, activity",
            onClick = { onNavigateSubScreen(SettingsSubScreen.PRIVACY_ACCOUNT) },
            showDivider = true,
            testTag = "settings_row_privacy_account"
        )
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.SHARE_PROFILE,
            title = "Share profile",
            subtitle = "QR code, link, copy profile URL",
            onClick = { onNavigateSubScreen(SettingsSubScreen.SHARE_PROFILE) },
            showDivider = false,
            testTag = "settings_row_share_profile"
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Section 2: Content & activity
    SettingsSectionHeader(
        title = "Content & activity",
        color = sectionTitleColor
    )
    SettingsGroupCard(cardBackground = cardBackground) {
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.NOTIFICATIONS,
            title = "Notifications",
            subtitle = "Push, email, SMS",
            onClick = { onNavigateSubScreen(SettingsSubScreen.NOTIFICATIONS) },
            showDivider = true,
            testTag = "settings_row_notifications"
        )
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.CONTENT_HEART,
            title = "Content preferences",
            subtitle = "Suggested content, language, video quality",
            onClick = { onNavigateSubScreen(SettingsSubScreen.CONTENT_PREFERENCES) },
            showDivider = false,
            testTag = "settings_row_content_preferences"
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Section 3: Privacy and safety
    SettingsSectionHeader(
        title = "Privacy and safety",
        color = sectionTitleColor
    )
    SettingsGroupCard(cardBackground = cardBackground) {
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.PRIVACY_LOCK,
            title = "Privacy",
            subtitle = "Who can see your videos, likes, comments, following",
            onClick = { onNavigateSubScreen(SettingsSubScreen.PRIVACY_CONTROLS) },
            showDivider = true,
            testTag = "settings_row_privacy_controls"
        )
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.BLOCKED_ACCOUNTS,
            title = "Blocked accounts",
            subtitle = "Manage blocked users",
            onClick = { onNavigateSubScreen(SettingsSubScreen.BLOCKED_ACCOUNTS) },
            showDivider = true,
            testTag = "settings_row_blocked_accounts"
        )
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.REPORT_PROBLEM,
            title = "Report a problem",
            subtitle = "Send feedback, report abuse",
            onClick = { onNavigateSubScreen(SettingsSubScreen.REPORT_PROBLEM) },
            showDivider = true,
            testTag = "settings_row_report_problem"
        )
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.SAFETY_CENTER,
            title = "Safety Center",
            subtitle = "Tips, tools, resources",
            onClick = { onNavigateSubScreen(SettingsSubScreen.SAFETY_CENTER) },
            showDivider = false,
            testTag = "settings_row_safety_center"
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Section 4: Support & about
    SettingsSectionHeader(
        title = "Support & about",
        color = sectionTitleColor
    )
    SettingsGroupCard(cardBackground = cardBackground) {
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.HELP_SUPPORT,
            title = "Help & support",
            subtitle = "FAQs, contact us, report issue",
            onClick = { onNavigateSubScreen(SettingsSubScreen.HELP_AND_SUPPORT) },
            showDivider = true,
            testTag = "settings_row_help_support"
        )
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.TERMS_POLICIES,
            title = "Terms and policies",
            subtitle = "Terms of Service, Privacy Policy, Community Guidelines",
            onClick = { onNavigateSubScreen(SettingsSubScreen.TERMS_AND_POLICIES) },
            showDivider = true,
            testTag = "settings_row_terms_policies"
        )
        SettingsNavRow(
            badgeIcon = SettingsBadgeIcon.ABOUT,
            title = "About",
            subtitle = "Version, copyright, open source",
            onClick = { onNavigateSubScreen(SettingsSubScreen.ABOUT) },
            showDivider = false,
            testTag = "settings_row_about"
        )
    }

    Spacer(modifier = Modifier.height(26.dp))

    // Bottom Pill Log out Button matching reference image
    Surface(
        onClick = onLogOutClick,
        shape = RoundedCornerShape(26.dp),
        color = TelefamRed,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("settings_logout_pill_button")
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoutIconVector(tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.settings_logout_button),
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// --- Shared Reusable Settings Components ---

@Composable
fun SettingsSectionHeader(
    title: String,
    color: Color = Color(0xFF6B7280)
) {
    Text(
        text = title,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsExplanationCard(
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = TelefamRed.copy(alpha = 0.06f),
        border = BorderStroke(0.8.dp, TelefamRed.copy(alpha = 0.16f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = TelefamRed,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsGroupCard(
    cardBackground: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isTelefamDarkTheme()
    val borderColor = if (isDark) Color(0xFF282830) else Color(0xFFE8ECF2)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBackground,
        border = BorderStroke(0.6.dp, borderColor),
        tonalElevation = 0.5.dp,
        shadowElevation = 0.5.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

@Composable
fun SettingsNavRow(
    badgeIcon: SettingsBadgeIcon? = null,
    icon: ImageVector? = null,
    title: String,
    subtitle: String? = null,
    valueLabel: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
    showDivider: Boolean = true,
    testTag: String
) {
    val isDark = isTelefamDarkTheme()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 13.dp)
                .testTag(testTag),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (badgeIcon != null) {
                SettingsBadge(icon = badgeIcon)
                Spacer(modifier = Modifier.width(14.dp))
            } else if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(TelefamRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.5.sp,
                        lineHeight = 16.5.sp,
                        color = if (isDark) Color(0xFF9AA4B2) else Color(0xFF6B7280)
                    )
                }
            }

            if (!valueLabel.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = valueLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFF9AA4B2) else Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 140.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TelefamRed,
                modifier = Modifier.size(19.dp)
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = if (badgeIcon != null || icon != null) 68.dp else 16.dp, end = 16.dp),
                thickness = 0.5.dp,
                color = if (isDark) Color(0xFF2B2B33) else Color(0xFFEBEFF5)
            )
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean = true,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag(testTag),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    color = Color(0xFF6B7888)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = TelefamRed
                )
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )
        }
    }
}

@Composable
fun SettingsRadioRow(
    title: String,
    subtitle: String? = null,
    selected: Boolean,
    onClick: () -> Unit,
    showDivider: Boolean = true,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag(testTag),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = TelefamRed)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.8.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF6B7888)
                    )
                }
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )
        }
    }
}

@Composable
private fun SettingsFeedbackBanners(
    statusMessage: String?,
    errorMessage: String?,
    canRetryError: Boolean,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 720.dp)
            .padding(horizontal = 16.dp)
    ) {
        AnimatedVisibility(visible = !errorMessage.isNullOrBlank()) {
            Surface(
                color = Color(0xFFFDECEA),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .testTag("settings_error_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = TelefamRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage.orEmpty(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8A000A),
                        modifier = Modifier.weight(1f)
                    )
                    if (canRetryError) {
                        OutlinedButton(
                            onClick = onRetry,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, TelefamRed),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .height(30.dp)
                                .testTag("settings_error_retry_button")
                        ) {
                            Text(
                                text = "Retry",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TelefamRed
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(26.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.crop_cancel_button),
                            tint = Color(0xFF8A000A),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = !statusMessage.isNullOrBlank() && errorMessage.isNullOrBlank()) {
            Surface(
                color = Color(0xFFEAF7EE),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .testTag("settings_status_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF1B7F3B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusMessage.orEmpty(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF135A29),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(26.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.crop_cancel_button),
                            tint = Color(0xFF135A29),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}
