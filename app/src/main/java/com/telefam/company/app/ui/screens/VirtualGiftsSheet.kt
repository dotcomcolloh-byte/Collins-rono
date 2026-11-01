package com.telefam.company.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TelefamRed
import com.telefam.company.app.domain.VirtualGiftCatalog
import com.telefam.company.app.domain.VirtualGiftCategory
import com.telefam.company.app.domain.VirtualGiftItem
import com.telefam.company.app.ui.screens.LucideCampaignIcons
import com.telefam.company.app.ui.components.LucideGiftCategoryIcons
import com.telefam.company.app.ui.components.VirtualGiftVectorArtwork
import java.text.NumberFormat
import java.util.Locale

/**
 * Virtual Gifts Screen & Modal matching Image 1 exactly.
 * - Header: Back button, Title "Gifts", Subtitle "Send love, support and make moments special ❤️",
 *   Red wallet icon + Gold coin icon + balance ("1,250")
 * - Category Tabs: For You, Trending (active red underline), Love, Fun, Luxury, Support
 * - Subtitle Banner: Trending Gifts, "Popular gifts this week. Send and make their day!", View All button
 * - 4-Column Grid of 20 gifts matching Image 1 exactly with zero emojis
 * - Real Wallet Stars balance checking with top-up mode if insufficient
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualGiftsSheet(
    userStarsBalance: Long,
    onDismissRequest: () -> Unit,
    onSendGift: (VirtualGiftItem, Int) -> Unit,
    onTopUpStars: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(VirtualGiftCategory.TRENDING) }
    var showInsufficientBalanceDialog by remember { mutableStateOf<VirtualGiftItem?>(null) }
    var selectedGiftForCombo by remember { mutableStateOf<VirtualGiftItem?>(null) }
    var currentComboCount by remember { mutableStateOf(1) }

    val activeGifts = remember(selectedCategory) {
        VirtualGiftCatalog.getGiftsForCategory(selectedCategory)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null,
        modifier = modifier.testTag("virtual_gifts_bottom_sheet")
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(680.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // 1. TOP BAR (Matching Image 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("gifts_back_button")
                    ) {
                        Icon(
                            imageVector = LucideCampaignIcons.ArrowLeft,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gifts",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111111)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Send love, support and make moments special",
                            fontSize = 11.5.sp,
                            color = Color(0xFF757575),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Top Right Wallet + Coin Balance Pill
                    Surface(
                        onClick = onTopUpStars,
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF5F5F7),
                        border = BorderStroke(1.dp, Color(0xFFE5E5EA)),
                        modifier = Modifier.testTag("gifts_wallet_balance_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = LucideGiftCategoryIcons.Wallet,
                                contentDescription = "Wallet",
                                tint = Color(0xFFE50914),
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = LucideGiftCategoryIcons.Coin,
                                contentDescription = "Coins",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = NumberFormat.getNumberInstance(Locale.US).format(userStarsBalance),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }
                }

                // 2. CATEGORY TABS (Matching Image 1: For You, Trending, Love, Fun, Luxury, Support)
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VirtualGiftCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        val categoryIcon = when (category) {
                            VirtualGiftCategory.FOR_YOU -> LucideGiftCategoryIcons.ForYou
                            VirtualGiftCategory.TRENDING -> LucideGiftCategoryIcons.Trending
                            VirtualGiftCategory.LOVE -> LucideGiftCategoryIcons.Love
                            VirtualGiftCategory.FUN -> LucideGiftCategoryIcons.Fun
                            VirtualGiftCategory.LUXURY -> LucideGiftCategoryIcons.Luxury
                            VirtualGiftCategory.SUPPORT -> LucideGiftCategoryIcons.Support
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("gift_tab_${category.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = category.title,
                                    tint = if (isSelected) Color(0xFFFE2C55) else Color(0xFF666666),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = category.title,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFFFE2C55) else Color(0xFF444444)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Active red underline
                            Box(
                                modifier = Modifier
                                    .width(if (isSelected) 36.dp else 0.dp)
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isSelected) Color(0xFFFE2C55) else Color.Transparent)
                            )
                        }
                    }
                }

                // 3. SUBTITLE BANNER (Matching Image 1: Flame icon, Trending Gifts, description, View All)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF0F2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = LucideGiftCategoryIcons.Flame,
                                contentDescription = "Trending",
                                tint = Color(0xFFFF3B30),
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "${selectedCategory.title} Gifts",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Text(
                                    text = "Popular gifts this week. Send and make their day!",
                                    fontSize = 11.sp,
                                    color = Color(0xFF666666),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFFFCCD3)),
                            modifier = Modifier.clickable { }
                        ) {
                            Text(
                                text = "View All",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFE2C55),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 4. 4-COLUMN GIFTS GRID (Matching Image 1)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("gifts_grid")
                ) {
                    items(activeGifts, key = { it.id }) { gift ->
                        GiftGridCard(
                            gift = gift,
                            onClick = {
                                if (userStarsBalance >= gift.coinCost) {
                                    if (selectedGiftForCombo?.id == gift.id) {
                                        currentComboCount++
                                    } else {
                                        selectedGiftForCombo = gift
                                        currentComboCount = 1
                                    }
                                    onSendGift(gift, currentComboCount)
                                } else {
                                    showInsufficientBalanceDialog = gift
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Insufficient Balance Dialog with direct Top-Up mode
    showInsufficientBalanceDialog?.let { gift ->
        ModalBottomSheet(
            onDismissRequest = { showInsufficientBalanceDialog = null },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = LucideGiftCategoryIcons.Coin,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Insufficient Stars",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You need ${gift.coinCost} Stars to send ${gift.name}. Your current balance is $userStarsBalance Stars.",
                    fontSize = 13.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        showInsufficientBalanceDialog = null
                        onTopUpStars()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Top up Stars", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { showInsufficientBalanceDialog = null },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = Color(0xFF444444))
                }
            }
        }
    }
}

@Composable
private fun GiftGridCard(
    gift: VirtualGiftItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFAFAFC),
        border = BorderStroke(1.dp, Color(0xFFF0F0F3)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("gift_card_${gift.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // High-resolution SVG Artwork
            VirtualGiftVectorArtwork(
                giftId = gift.id,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Gift Name
            Text(
                text = gift.name,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF222222),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Coin Badge + Cost
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = LucideGiftCategoryIcons.Coin,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = NumberFormat.getNumberInstance(Locale.US).format(gift.coinCost),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF444444)
                )
            }
        }
    }
}
