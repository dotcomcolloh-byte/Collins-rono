package com.telefam.company.app.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import com.telefam.company.app.domain.MarketplaceProduct
import com.telefam.company.app.ui.components.MarketplaceLucideIcons
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Product Details Screen:
 * - Edge-to-edge top bar with statusBarsPadding()
 * - Photo Gallery Carousel / Pager with zoomable preview
 * - Category-specific attributes and dynamic specs
 * - Seller DP, full name, country and location
 * - "Message Seller" button opening 1:1 chat inbox with prefilled message
 * - Share, Favorite, Mark as Sold, Renewal, Report and Block features
 * - Related products recommendations
 */
@Composable
fun MarketplaceProductDetailsScreen(
    product: MarketplaceProduct,
    relatedProducts: List<MarketplaceProduct>,
    currentUserId: String,
    onBackClick: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onMessageSeller: (product: MarketplaceProduct, prefilledText: String) -> Unit,
    onOpenOtherProduct: (MarketplaceProduct) -> Unit,
    onViewSellerProfile: (String) -> Unit,
    onMarkAsSold: (String, Boolean) -> Unit,
    onRenewListing: (String) -> Unit,
    onDeleteListing: (String) -> Unit,
    onOpenReportDialog: (MarketplaceProduct) -> Unit,
    onBlockSeller: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isTelefamDarkTheme()
    val isOwner = product.sellerUserId == currentUserId

    val bgColor = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF7F8FA)
    val cardBg = if (isDark) Color(0xFF1B232E) else Color.White
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE8ECF2)
    val primaryText = if (isDark) Color.White else Color(0xFF111827)
    val secondaryText = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    var showMenu by remember { mutableStateOf(false) }
    var previewPhotoPath by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    BackHandler {
        onBackClick()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("marketplace_product_details_screen")
    ) {
        val maxContainerWidth = if (maxWidth >= 1000.dp) 880.dp else 680.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar with statusBarsPadding()
            Surface(
                color = if (isDark) Color(0xFF141C24) else Color.White,
                shadowElevation = if (isDark) 0.dp else 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .widthIn(max = maxContainerWidth)
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("marketplace_details_back_btn")
                    ) {
                        Icon(
                            imageVector = MarketplaceLucideIcons.ArrowLeft,
                            contentDescription = stringResource(R.string.marketplace_back),
                            tint = primaryText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Share Button
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${product.title} - ${formatProductPrice(product.price, product.currency)} on Telefam Marketplace: https://telefam.company/marketplace/item/${product.id}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Listing"))
                            },
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = MarketplaceLucideIcons.Share2,
                                contentDescription = stringResource(R.string.marketplace_share),
                                tint = primaryText,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Favorite Button
                        IconButton(
                            onClick = { onToggleFavorite(product.id) },
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = if (product.isFavoritedByMe) MarketplaceLucideIcons.HeartFilled else MarketplaceLucideIcons.Heart,
                                contentDescription = stringResource(R.string.marketplace_favorite),
                                tint = if (product.isFavoritedByMe) Color(0xFFE50914) else primaryText,
                                modifier = Modifier.size(21.dp)
                            )
                        }

                        // 3-Dots More Options Menu
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(42.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.marketplace_more_options),
                                    tint = primaryText
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                if (isOwner) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = if (product.isSold) stringResource(R.string.marketplace_mark_available) else stringResource(R.string.marketplace_mark_sold),
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            onMarkAsSold(product.id, !product.isSold)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = stringResource(R.string.marketplace_renew_listing),
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            onRenewListing(product.id)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = stringResource(R.string.marketplace_delete_listing),
                                                color = TelefamRed,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            showDeleteConfirm = true
                                        }
                                    )
                                } else {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = stringResource(R.string.marketplace_report_listing),
                                                color = TelefamRed,
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            onOpenReportDialog(product)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = stringResource(R.string.marketplace_block_seller),
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            onBlockSeller(product.sellerUserId)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Scrollable Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = maxContainerWidth)
                        .padding(bottom = 24.dp)
                ) {
                    // Photo Carousel Pager
                    if (product.photoPaths.isNotEmpty()) {
                        val pagerState = rememberPagerState(pageCount = { product.photoPaths.size })
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.2f)
                                .background(if (isDark) Color(0xFF141C24) else Color(0xFFE5E9F0))
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val photoPath = product.photoPaths[page]
                                AsyncImage(
                                    model = if (photoPath.startsWith("/")) File(photoPath) else photoPath,
                                    contentDescription = "${product.title} - Photo ${page + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { previewPhotoPath = photoPath }
                                )
                            }

                            // Page Counter Pill
                            if (product.photoPaths.size > 1) {
                                Surface(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "${pagerState.currentPage + 1}/${product.photoPaths.size}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Main Info Card
                    Surface(
                        color = cardBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Price & Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = formatProductPrice(product.price, product.currency),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryText
                                )

                                if (product.isSold) {
                                    Surface(
                                        color = TelefamRed.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, TelefamRed)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.marketplace_status_sold),
                                            color = TelefamRed,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        color = if (isDark) Color(0xFF283442) else Color(0xFFF1F3F7),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = stringResource(product.condition.labelRes),
                                            color = primaryText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Product Title
                            Text(
                                text = product.title,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryText
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Location & Date Row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = MarketplaceLucideIcons.MapPin,
                                    contentDescription = null,
                                    tint = TelefamRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${product.sellerLocation}, ${product.sellerCountry}",
                                    fontSize = 13.5.sp,
                                    color = secondaryText
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "•  ${formatDateRelative(product.createdAtMs)}",
                                    fontSize = 13.sp,
                                    color = secondaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Specifications (Dynamic requirements e.g. Cars, Properties, Electronics)
                    if (product.specs.isNotEmpty()) {
                        Surface(
                            color = cardBg,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, cardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = stringResource(R.string.marketplace_specifications),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryText
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                product.specs.forEach { (key, value) ->
                                    if (value.isNotBlank()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = key,
                                                fontSize = 13.5.sp,
                                                color = secondaryText
                                            )
                                            Text(
                                                text = value,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = primaryText
                                            )
                                        }
                                        HorizontalDivider(
                                            color = cardBorder.copy(alpha = 0.5f),
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Description Card
                    Surface(
                        color = cardBg,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = stringResource(R.string.marketplace_description),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryText
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.description,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = primaryText.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Delivery & Collection Options
                    if (product.deliveryOptions.isNotEmpty()) {
                        Surface(
                            color = cardBg,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, cardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = stringResource(R.string.marketplace_delivery_options),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    product.deliveryOptions.forEach { option ->
                                        Surface(
                                            color = if (isDark) Color(0xFF283442) else Color(0xFFEDF0F5),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = when (option) {
                                                        com.telefam.company.app.domain.MarketplaceDeliveryOption.LOCAL_PICKUP -> MarketplaceLucideIcons.Package
                                                        com.telefam.company.app.domain.MarketplaceDeliveryOption.DOOR_DELIVERY -> MarketplaceLucideIcons.Truck
                                                        com.telefam.company.app.domain.MarketplaceDeliveryOption.COURIER_SHIPPING -> MarketplaceLucideIcons.Truck
                                                    },
                                                    contentDescription = null,
                                                    tint = TelefamRed,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = stringResource(option.labelRes),
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = primaryText
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Seller Information Card
                    Surface(
                        color = cardBg,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = stringResource(R.string.marketplace_seller_info),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryText
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onViewSellerProfile(product.sellerUserId) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(TelefamRed.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!product.sellerAvatarUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = if (product.sellerAvatarUrl.startsWith("/")) File(product.sellerAvatarUrl) else product.sellerAvatarUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Text(
                                            text = product.sellerFullName.trim().take(1).uppercase().ifBlank { "S" },
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TelefamRed
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = product.sellerFullName,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryText
                                        )
                                        if (product.sellerVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = MarketplaceLucideIcons.ShieldCheck,
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "@${product.sellerUsername} • ${product.sellerLocation}",
                                        fontSize = 12.5.sp,
                                        color = secondaryText
                                    )
                                    Text(
                                        text = product.sellerResponseRate,
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.marketplace_view_profile),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TelefamRed
                                )
                            }
                        }
                    }

                    // Related Products Shelf
                    if (relatedProducts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.marketplace_related_products),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryText,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            relatedProducts.forEach { item ->
                                Surface(
                                    color = cardBg,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, cardBorder),
                                    modifier = Modifier
                                        .width(150.dp)
                                        .clickable { onOpenOtherProduct(item) }
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        val photo = item.photoPaths.firstOrNull()
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp)
                                                .background(if (isDark) Color(0xFF283442) else Color(0xFFE5E9F0))
                                        ) {
                                            if (!photo.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = if (photo.startsWith("/")) File(photo) else photo,
                                                    contentDescription = item.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = formatProductPrice(item.price, item.currency),
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryText
                                            )
                                            Text(
                                                text = item.title,
                                                fontSize = 12.sp,
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
                }
            }

            // 3. Bottom Sticky Action Bar with navigationBarsPadding()
            Surface(
                color = if (isDark) Color(0xFF141C24) else Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .widthIn(max = maxContainerWidth)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isOwner) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onMarkAsSold(product.id, !product.isSold) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (product.isSold) Color(0xFF10B981) else Color(0xFF374151)
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = MarketplaceLucideIcons.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (product.isSold) stringResource(R.string.marketplace_mark_available) else stringResource(R.string.marketplace_mark_sold),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (product.isExpiredOrNear) {
                                Button(
                                    onClick = { onRenewListing(product.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Icon(
                                        imageVector = MarketplaceLucideIcons.RotateCw,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.marketplace_renew_listing),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        // "Message Seller" Button (Opens Inbox with Product Attached & Prefilled text)
                        Button(
                            onClick = {
                                val prefill = "Hi, is this available? Regarding: ${product.title} (${formatProductPrice(product.price, product.currency)})"
                                onMessageSeller(product, prefill)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                            shape = RoundedCornerShape(26.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("marketplace_message_seller_btn")
                        ) {
                            Icon(
                                imageVector = MarketplaceLucideIcons.MessageCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.marketplace_message_seller),
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Full Screen Photo Preview Dialog
        if (previewPhotoPath != null) {
            Dialog(
                onDismissRequest = { previewPhotoPath = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    IconButton(
                        onClick = { previewPhotoPath = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(16.dp)
                            .size(44.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = MarketplaceLucideIcons.X,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    AsyncImage(
                        model = if (previewPhotoPath!!.startsWith("/")) File(previewPhotoPath!!) else previewPhotoPath!!,
                        contentDescription = "Preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text(text = stringResource(R.string.marketplace_delete_confirm_title), fontWeight = FontWeight.Bold) },
                text = { Text(text = stringResource(R.string.marketplace_delete_confirm_desc)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirm = false
                            onDeleteListing(product.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(text = stringResource(R.string.marketplace_delete_action), color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text(text = stringResource(R.string.crop_cancel_button))
                    }
                }
            )
        }
    }
}

private fun formatDateRelative(timestampMs: Long): String {
    val diff = System.currentTimeMillis() - timestampMs
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24
    return when {
        minutes < 5 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestampMs))
    }
}
