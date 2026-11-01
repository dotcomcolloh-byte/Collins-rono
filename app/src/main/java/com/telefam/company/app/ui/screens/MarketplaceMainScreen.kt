package com.telefam.company.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import com.telefam.company.app.domain.MarketplaceCategory
import com.telefam.company.app.domain.MarketplaceProduct
import com.telefam.company.app.domain.MarketplaceScreenTab
import com.telefam.company.app.ui.components.MarketplaceLucideIcons
import java.io.File
import java.text.NumberFormat
import java.util.Locale

/**
 * Main Marketplace Screen:
 * - Edge-to-edge header with `statusBarsPadding()`
 * - Responsive products grid with seller DP, full name, country and location
 * - Pure Lucide SVG vector icons in buttons (zero emojis)
 * - Search bar with recent search pills and instant category filter chips
 * - Full support for phones, foldables, tablets, and desktop
 */
@Composable
fun MarketplaceMainScreen(
    products: List<MarketplaceProduct>,
    selectedTab: MarketplaceScreenTab,
    selectedCategory: MarketplaceCategory,
    searchQuery: String,
    recentSearches: List<String>,
    isMarketplaceSuspended: Boolean,
    suspensionReason: String,
    onBackClick: () -> Unit,
    onSelectTab: (MarketplaceScreenTab) -> Unit,
    onSelectCategory: (MarketplaceCategory) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSelectRecentSearch: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onOpenProductDetails: (MarketplaceProduct) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenPostSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isTelefamDarkTheme()
    val bgColor = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF7F8FA)
    val cardBg = if (isDark) Color(0xFF1B232E) else Color.White
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE8ECF2)
    val primaryText = if (isDark) Color.White else Color(0xFF111827)
    val secondaryText = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    BackHandler {
        onBackClick()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("marketplace_main_screen_root")
    ) {
        val screenWidth = maxWidth
        val isWideScreen = screenWidth >= 700.dp
        val maxContainerWidth = if (screenWidth >= 1000.dp) 1100.dp else 840.dp
        val gridColumns = when {
            screenWidth >= 1000.dp -> 4
            screenWidth >= 640.dp -> 3
            else -> 2
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Bar with statusBarsPadding()
            Surface(
                color = if (isDark) Color(0xFF141C24) else Color.White,
                shadowElevation = if (isDark) 0.dp else 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .widthIn(max = maxContainerWidth)
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    // Header Row: Back, Title, Sell Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("marketplace_back_button")
                            ) {
                                Icon(
                                    imageVector = MarketplaceLucideIcons.ArrowLeft,
                                    contentDescription = stringResource(R.string.marketplace_back),
                                    tint = primaryText,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.marketplace_title),
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryText
                            )
                        }

                        // "Sell" Button (Red Pill, Lucide Plus, SVG Icon)
                        Button(
                            onClick = onOpenPostSheet,
                            enabled = !isMarketplaceSuspended,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TelefamRed,
                                contentColor = Color.White,
                                disabledContainerColor = TelefamRed.copy(alpha = 0.4f),
                                disabledContentColor = Color.White.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier
                                .height(42.dp)
                                .testTag("marketplace_sell_button")
                        ) {
                            Icon(
                                imageVector = MarketplaceLucideIcons.Plus,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.marketplace_sell),
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Bar
                    Surface(
                        color = if (isDark) Color(0xFF222C38) else Color(0xFFF1F3F7),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = MarketplaceLucideIcons.Search,
                                contentDescription = null,
                                tint = secondaryText,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChanged,
                                textStyle = TextStyle(
                                    color = primaryText,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                cursorBrush = SolidColor(TelefamRed),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("marketplace_search_input"),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.marketplace_search_placeholder),
                                            color = secondaryText,
                                            fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchQueryChanged("") },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = MarketplaceLucideIcons.X,
                                        contentDescription = null,
                                        tint = secondaryText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Recent Searches shelf (if search empty and recents exist)
                    if (searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.marketplace_recent_searches_prefix),
                                fontSize = 12.sp,
                                color = secondaryText,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            recentSearches.take(6).forEach { recentQuery ->
                                Surface(
                                    color = if (isDark) Color(0xFF283442) else Color(0xFFE5E9F0),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .clickable { onSelectRecentSearch(recentQuery) }
                                ) {
                                    Text(
                                        text = recentQuery,
                                        fontSize = 12.sp,
                                        color = primaryText,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = stringResource(R.string.marketplace_clear_history),
                                fontSize = 11.5.sp,
                                color = TelefamRed,
                                modifier = Modifier
                                    .clickable { onClearRecentSearches() }
                                    .padding(start = 6.dp, end = 4.dp)
                            )
                        }
                    }
                }
            }

            // 2. Suspension Notice Banner (Calm, Professional, Non-Noisy)
            if (isMarketplaceSuspended) {
                Surface(
                    color = TelefamRed.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, TelefamRed.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = maxContainerWidth)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("marketplace_suspended_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = MarketplaceLucideIcons.AlertTriangle,
                            contentDescription = null,
                            tint = TelefamRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (suspensionReason.isNotBlank()) {
                                stringResource(R.string.marketplace_suspended_with_reason, suspensionReason)
                            } else {
                                stringResource(R.string.marketplace_suspended_notice)
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. Tab Bar: Explore | Saved | My Listings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = maxContainerWidth)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MarketplaceTabPill(
                    title = stringResource(R.string.marketplace_tab_explore),
                    isSelected = selectedTab == MarketplaceScreenTab.EXPLORE,
                    onClick = { onSelectTab(MarketplaceScreenTab.EXPLORE) },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                MarketplaceTabPill(
                    title = stringResource(R.string.marketplace_tab_saved),
                    isSelected = selectedTab == MarketplaceScreenTab.SAVED,
                    onClick = { onSelectTab(MarketplaceScreenTab.SAVED) },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                MarketplaceTabPill(
                    title = stringResource(R.string.marketplace_tab_my_listings),
                    isSelected = selectedTab == MarketplaceScreenTab.MY_LISTINGS,
                    onClick = { onSelectTab(MarketplaceScreenTab.MY_LISTINGS) },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            // 4. Horizontal Categories Row (Active in Explore Tab)
            if (selectedTab == MarketplaceScreenTab.EXPLORE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MarketplaceCategory.entries.forEach { category ->
                        val isSelected = category == selectedCategory
                        val icon = getCategoryIcon(category)
                        Surface(
                            color = if (isSelected) TelefamRed else if (isDark) Color(0xFF222C38) else Color.White,
                            shape = RoundedCornerShape(20.dp),
                            border = if (isSelected) null else BorderStroke(1.dp, cardBorder),
                            modifier = Modifier
                                .clickable { onSelectCategory(category) }
                                .testTag("marketplace_cat_${category.categoryKey}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else secondaryText,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(category.titleRes),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else primaryText
                                )
                            }
                        }
                    }
                }
            }

            // 5. Products Grid
            if (products.isEmpty()) {
                // Accessible Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.widthIn(max = 400.dp)
                    ) {
                        Surface(
                            color = TelefamRed.copy(alpha = 0.1f),
                            shape = CircleShape,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = MarketplaceLucideIcons.Store,
                                    contentDescription = null,
                                    tint = TelefamRed,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when (selectedTab) {
                                MarketplaceScreenTab.SAVED -> stringResource(R.string.marketplace_empty_saved_title)
                                MarketplaceScreenTab.MY_LISTINGS -> stringResource(R.string.marketplace_empty_my_listings_title)
                                else -> stringResource(R.string.marketplace_empty_products_title)
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (selectedTab) {
                                MarketplaceScreenTab.SAVED -> stringResource(R.string.marketplace_empty_saved_desc)
                                MarketplaceScreenTab.MY_LISTINGS -> stringResource(R.string.marketplace_empty_my_listings_desc)
                                else -> stringResource(R.string.marketplace_empty_products_desc)
                            },
                            fontSize = 13.5.sp,
                            color = secondaryText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        if (selectedTab == MarketplaceScreenTab.MY_LISTINGS && !isMarketplaceSuspended) {
                            Button(
                                onClick = onOpenPostSheet,
                                colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.marketplace_start_selling),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = maxContainerWidth)
                        .testTag("marketplace_products_grid")
                ) {
                    items(products, key = { it.id }) { product ->
                        MarketplaceProductGridCard(
                            product = product,
                            isDark = isDark,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            primaryText = primaryText,
                            secondaryText = secondaryText,
                            onClick = { onOpenProductDetails(product) },
                            onToggleFavorite = { onToggleFavorite(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketplaceTabPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) {
            TelefamRed.copy(alpha = if (isDark) 0.22f else 0.12f)
        } else {
            if (isDark) Color(0xFF1B232E) else Color.White
        },
        border = BorderStroke(
            1.dp,
            if (isSelected) TelefamRed else if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE8ECF2)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .height(38.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TelefamRed else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MarketplaceProductGridCard(
    product: MarketplaceProduct,
    isDark: Boolean,
    cardBg: Color,
    cardBorder: Color,
    primaryText: Color,
    secondaryText: Color,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstPhoto = product.photoPaths.firstOrNull()
    val formattedPrice = formatProductPrice(product.price, product.currency)

    Surface(
        color = cardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, cardBorder),
        shadowElevation = if (isDark) 0.dp else 1.5.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("marketplace_product_card_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Product Image Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(if (isDark) Color(0xFF242F3D) else Color(0xFFE8ECF2))
            ) {
                if (!firstPhoto.isNullOrBlank()) {
                    AsyncImage(
                        model = if (firstPhoto.startsWith("/")) File(firstPhoto) else firstPhoto,
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MarketplaceLucideIcons.Store,
                            contentDescription = null,
                            tint = secondaryText.copy(alpha = 0.4f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Condition or Sold Badge
                if (product.isSold) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.marketplace_status_sold),
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Bookmark / Favorite Icon Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        .testTag("marketplace_fav_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (product.isFavoritedByMe) MarketplaceLucideIcons.HeartFilled else MarketplaceLucideIcons.Heart,
                        contentDescription = stringResource(R.string.marketplace_favorite),
                        tint = if (product.isFavoritedByMe) Color(0xFFE50914) else Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // Card Body: Price, Title, Seller row
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = formattedPrice,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryText,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = primaryText.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Seller Row: Small DP (photo/initial), Name, Country & Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(TelefamRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!product.sellerAvatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = if (product.sellerAvatarUrl.startsWith("/")) File(product.sellerAvatarUrl) else product.sellerAvatarUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                text = product.sellerFullName.trim().take(1).uppercase().ifBlank { "S" },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TelefamRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.sellerFullName,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (product.sellerCountryIso.isNotBlank()) {
                                "${product.sellerLocation}, ${product.sellerCountryIso}"
                            } else {
                                product.sellerLocation
                            },
                            fontSize = 10.5.sp,
                            color = secondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

fun formatProductPrice(price: Double, currency: String): String {
    return runCatching {
        val format = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            maximumFractionDigits = if (price % 1.0 == 0.0) 0 else 2
        }
        "$currency ${format.format(price)}"
    }.getOrDefault("$currency $price")
}

fun getCategoryIcon(category: MarketplaceCategory): ImageVector {
    return when (category) {
        MarketplaceCategory.ALL -> MarketplaceLucideIcons.Store
        MarketplaceCategory.VEHICLES -> MarketplaceLucideIcons.Car
        MarketplaceCategory.REAL_ESTATE -> MarketplaceLucideIcons.Home
        MarketplaceCategory.ELECTRONICS -> MarketplaceLucideIcons.Smartphone
        MarketplaceCategory.FASHION -> MarketplaceLucideIcons.Shirt
        MarketplaceCategory.HOME_GARDEN -> MarketplaceLucideIcons.Armchair
        MarketplaceCategory.BEAUTY_HEALTH -> MarketplaceLucideIcons.Sparkles
        MarketplaceCategory.SPORTS_OUTDOORS -> MarketplaceLucideIcons.Dumbbell
        MarketplaceCategory.JOBS_SERVICES -> MarketplaceLucideIcons.Briefcase
        MarketplaceCategory.GENERAL -> MarketplaceLucideIcons.Layers
    }
}
