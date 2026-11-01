package com.telefam.company.app.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.telefam.company.app.domain.CountryDialCode
import com.telefam.company.app.domain.MarketplaceCategory
import com.telefam.company.app.domain.MarketplaceCurrencies
import com.telefam.company.app.domain.MarketplaceCurrency
import com.telefam.company.app.domain.MarketplaceDeliveryOption
import com.telefam.company.app.domain.MarketplaceItemCondition
import com.telefam.company.app.domain.MarketplaceUploadProgress
import com.telefam.company.app.ui.components.MarketplaceLucideIcons

/**
 * Post Product Listing Studio Sheet:
 * - Multi-photo picker up to 10 photos with preview thumbnails
 * - Dynamic category-specific attribute fields (Cars, Properties, Electronics, Fashion)
 * - Automatic default currency matching user's profile country
 * - Privacy control for location precision
 * - Compression and layered review progress states
 */
@Composable
fun MarketplacePostStudioSheet(
    defaultCountry: CountryDialCode,
    defaultLocation: String,
    uploadProgress: MarketplaceUploadProgress,
    statusMessage: String?,
    onDismiss: () -> Unit,
    onSubmitListing: (
        title: String,
        description: String,
        price: Double,
        currency: String,
        category: MarketplaceCategory,
        condition: MarketplaceItemCondition,
        specs: Map<String, String>,
        deliveryOptions: Set<MarketplaceDeliveryOption>,
        showExactLocation: Boolean,
        location: String,
        selectedImageUris: List<Uri>
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isTelefamDarkTheme()
    val bgColor = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF7F8FA)
    val cardBg = if (isDark) Color(0xFF1B232E) else Color.White
    val primaryText = if (isDark) Color.White else Color(0xFF111827)
    val secondaryText = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    val defaultCurrency = remember(defaultCountry.isoCode) {
        com.telefam.company.app.domain.MarketplaceCurrencies.getDefaultCurrencyForCountry(defaultCountry.isoCode)
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(defaultCurrency) }
    var isCurrencyPickerOpen by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(MarketplaceCategory.GENERAL) }
    var selectedCondition by remember { mutableStateOf(MarketplaceItemCondition.USED_GOOD) }
    var locationText by remember { mutableStateOf(defaultLocation.ifBlank { "Nairobi" }) }
    var showExactLocation by remember { mutableStateOf(true) }

    val selectedDeliveryOptions = remember {
        mutableStateMapOf(
            MarketplaceDeliveryOption.LOCAL_PICKUP to true,
            MarketplaceDeliveryOption.DOOR_DELIVERY to false,
            MarketplaceDeliveryOption.COURIER_SHIPPING to false
        )
    }

    // Dynamic Specs
    val specsMap = remember { mutableStateMapOf<String, String>() }

    // Photos
    var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val combined = (photoUris + uris).distinct().take(10)
            photoUris = combined
        }
    }

    val isSubmitting = uploadProgress != MarketplaceUploadProgress.IDLE &&
        uploadProgress != MarketplaceUploadProgress.FAILED &&
        uploadProgress != MarketplaceUploadProgress.PUBLISHED

    BackHandler(enabled = !isSubmitting) {
        onDismiss()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("marketplace_post_studio_sheet")
    ) {
        val maxContainerWidth = if (maxWidth >= 1000.dp) 780.dp else 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Header with statusBarsPadding()
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
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss,
                            enabled = !isSubmitting,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = MarketplaceLucideIcons.X,
                                contentDescription = "Close",
                                tint = primaryText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.marketplace_post_title),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryText
                        )
                    }

                    // Top Action: Publish
                    Button(
                        onClick = {
                            val priceDouble = priceText.toDoubleOrNull()
                            if (photoUris.isEmpty()) {
                                validationError = "Please upload at least one photo of your item"
                                return@Button
                            }
                            if (title.trim().length < 3) {
                                validationError = "Title must be at least 3 characters"
                                return@Button
                            }
                            if (priceDouble == null || priceDouble <= 0.0) {
                                validationError = "Please enter a valid price greater than zero"
                                return@Button
                            }
                            if (description.trim().length < 10) {
                                validationError = "Description must be at least 10 characters"
                                return@Button
                            }
                            validationError = null
                            val activeDelivery = selectedDeliveryOptions.filter { it.value }.keys
                            onSubmitListing(
                                title.trim(),
                                description.trim(),
                                priceDouble,
                                selectedCurrency,
                                selectedCategory,
                                selectedCondition,
                                specsMap.toMap(),
                                activeDelivery,
                                showExactLocation,
                                locationText.trim(),
                                photoUris
                            )
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("marketplace_publish_btn")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.marketplace_publish_action),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Scrollable Form Fields
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Upload Progress Status Banner
                    if (isSubmitting) {
                        Surface(
                            color = TelefamRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, TelefamRed.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = TelefamRed,
                                    strokeWidth = 2.5.dp,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = statusMessage ?: when (uploadProgress) {
                                        MarketplaceUploadProgress.UPLOADING -> stringResource(R.string.marketplace_progress_uploading)
                                        MarketplaceUploadProgress.UPLOADED -> stringResource(R.string.marketplace_progress_uploaded)
                                        MarketplaceUploadProgress.UNDER_REVIEW -> stringResource(R.string.marketplace_progress_under_review)
                                        MarketplaceUploadProgress.PUBLISHED -> stringResource(R.string.marketplace_progress_published)
                                        else -> stringResource(R.string.marketplace_progress_processing)
                                    },
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = primaryText
                                )
                            }
                        }
                    }

                    // Validation Error Banner
                    if (validationError != null) {
                        Surface(
                            color = TelefamRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, TelefamRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = validationError.orEmpty(),
                                color = TelefamRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // 1. Photos Section (Up to 10 photos)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.marketplace_photos_section_title),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryText
                            )
                            Text(
                                text = "${photoUris.size}/10",
                                fontSize = 13.sp,
                                color = secondaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Add Photo Button
                            if (photoUris.size < 10) {
                                Surface(
                                    color = cardBg,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.5.dp, if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFD1D5DB)),
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        .testTag("marketplace_add_photo_btn")
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = MarketplaceLucideIcons.Camera,
                                            contentDescription = null,
                                            tint = TelefamRed,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = stringResource(R.string.marketplace_add_photos),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = primaryText
                                        )
                                    }
                                }
                            }

                            // Selected Photo Thumbnails
                            photoUris.forEachIndexed { index, uri ->
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Gray.copy(alpha = 0.2f))
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Photo $index",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    if (index == 0) {
                                        Surface(
                                            color = TelefamRed,
                                            shape = RoundedCornerShape(bottomEnd = 6.dp),
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.marketplace_photo_cover),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { photoUris = photoUris.filterIndexed { i, _ -> i != index } },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.TopEnd)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            .padding(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = MarketplaceLucideIcons.X,
                                            contentDescription = "Remove photo",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Title Field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { if (it.length <= 100) title = it },
                        label = { Text(stringResource(R.string.marketplace_field_title)) },
                        placeholder = { Text(stringResource(R.string.marketplace_field_title_hint)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("marketplace_input_title"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TelefamRed,
                            cursorColor = TelefamRed
                        )
                    )

                    // 3. Category Selector
                    Column {
                        Text(
                            text = stringResource(R.string.marketplace_category_label),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MarketplaceCategory.entries.filter { it != MarketplaceCategory.ALL }.forEach { cat ->
                                val isSelected = cat == selectedCategory
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedCategory = cat
                                        specsMap.clear()
                                    },
                                    label = { Text(stringResource(cat.titleRes), fontSize = 12.5.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TelefamRed,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }

                    // 4. Dynamic Category Requirements (e.g. Cars, Real Estate, Electronics)
                    when (selectedCategory) {
                        MarketplaceCategory.VEHICLES -> {
                            Surface(
                                color = cardBg,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE5E9F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = stringResource(R.string.marketplace_specs_vehicles),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryText
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = specsMap["Make"] ?: "",
                                            onValueChange = { specsMap["Make"] = it },
                                            label = { Text("Make (e.g. Toyota)") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        OutlinedTextField(
                                            value = specsMap["Model"] ?: "",
                                            onValueChange = { specsMap["Model"] = it },
                                            label = { Text("Model (e.g. Prado)") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = specsMap["Year"] ?: "",
                                            onValueChange = { specsMap["Year"] = it },
                                            label = { Text("Year") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        OutlinedTextField(
                                            value = specsMap["Mileage"] ?: "",
                                            onValueChange = { specsMap["Mileage"] = it },
                                            label = { Text("Mileage (km)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                    OutlinedTextField(
                                        value = specsMap["Transmission"] ?: "",
                                        onValueChange = { specsMap["Transmission"] = it },
                                        label = { Text("Transmission (Automatic / Manual)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                        MarketplaceCategory.REAL_ESTATE -> {
                            Surface(
                                color = cardBg,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE5E9F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = stringResource(R.string.marketplace_specs_real_estate),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryText
                                    )
                                    OutlinedTextField(
                                        value = specsMap["Property Type"] ?: "",
                                        onValueChange = { specsMap["Property Type"] = it },
                                        label = { Text("Type (Apartment, House, Land, Commercial)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = specsMap["Bedrooms"] ?: "",
                                            onValueChange = { specsMap["Bedrooms"] = it },
                                            label = { Text("Bedrooms") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        OutlinedTextField(
                                            value = specsMap["Bathrooms"] ?: "",
                                            onValueChange = { specsMap["Bathrooms"] = it },
                                            label = { Text("Bathrooms") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                    OutlinedTextField(
                                        value = specsMap["Furnished"] ?: "",
                                        onValueChange = { specsMap["Furnished"] = it },
                                        label = { Text("Furnished (Fully / Semi / Unfurnished)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                        MarketplaceCategory.ELECTRONICS -> {
                            Surface(
                                color = cardBg,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE5E9F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = stringResource(R.string.marketplace_specs_electronics),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryText
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = specsMap["Brand"] ?: "",
                                            onValueChange = { specsMap["Brand"] = it },
                                            label = { Text("Brand (Apple, Samsung)") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        OutlinedTextField(
                                            value = specsMap["Model"] ?: "",
                                            onValueChange = { specsMap["Model"] = it },
                                            label = { Text("Model") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = specsMap["Storage"] ?: "",
                                            onValueChange = { specsMap["Storage"] = it },
                                            label = { Text("Storage (128GB, 256GB)") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        OutlinedTextField(
                                            value = specsMap["RAM"] ?: "",
                                            onValueChange = { specsMap["RAM"] = it },
                                            label = { Text("RAM") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                        MarketplaceCategory.FASHION -> {
                            Surface(
                                color = cardBg,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE5E9F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = stringResource(R.string.marketplace_specs_fashion),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryText
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = specsMap["Size"] ?: "",
                                            onValueChange = { specsMap["Size"] = it },
                                            label = { Text("Size (S, M, L, XL, 42)") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        OutlinedTextField(
                                            value = specsMap["Color"] ?: "",
                                            onValueChange = { specsMap["Color"] = it },
                                            label = { Text("Color") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                    OutlinedTextField(
                                        value = specsMap["Gender"] ?: "",
                                        onValueChange = { specsMap["Gender"] = it },
                                        label = { Text("Gender / Age (Men, Women, Unisex, Kids)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                        else -> Unit
                    }

                    // 5. Price & Currency Selector (Default Currency from User Profile Country)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text(stringResource(R.string.marketplace_price_label)) },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("marketplace_input_price"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TelefamRed,
                                cursorColor = TelefamRed
                            )
                        )

                        // Currency Selector - full ISO 4217 world currencies
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isCurrencyPickerOpen = true }
                        ) {
                            OutlinedTextField(
                                value = selectedCurrency,
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                label = { Text(stringResource(R.string.marketplace_currency_label)) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = MarketplaceLucideIcons.Tag,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("marketplace_currency_selector_trigger"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = primaryText,
                                    disabledBorderColor = if (isDark) Color(0xFF2E3A4B) else Color(0xFFD1D5DB),
                                    disabledLabelColor = secondaryText,
                                    disabledTrailingIconColor = primaryText
                                )
                            )
                        }
                    }

                    // 6. Condition Selector
                    Column {
                        Text(
                            text = stringResource(R.string.marketplace_condition_label),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MarketplaceItemCondition.entries.forEach { cond ->
                                FilterChip(
                                    selected = cond == selectedCondition,
                                    onClick = { selectedCondition = cond },
                                    label = { Text(stringResource(cond.labelRes), fontSize = 12.5.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TelefamRed,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }

                    // 7. Location & Privacy Control
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBg, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        OutlinedTextField(
                            value = locationText,
                            onValueChange = { locationText = it },
                            label = { Text(stringResource(R.string.marketplace_location_label)) },
                            placeholder = { Text("e.g. Westlands, Nairobi") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.marketplace_location_privacy_title),
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = primaryText
                                )
                                Text(
                                    text = if (showExactLocation) {
                                        stringResource(R.string.marketplace_location_exact)
                                    } else {
                                        stringResource(R.string.marketplace_location_approx)
                                    },
                                    fontSize = 11.5.sp,
                                    color = secondaryText
                                )
                            }
                            Switch(
                                checked = showExactLocation,
                                onCheckedChange = { showExactLocation = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = TelefamRed, checkedTrackColor = TelefamRed.copy(alpha = 0.3f))
                            )
                        }
                    }

                    // 8. Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= 3000) description = it },
                        label = { Text(stringResource(R.string.marketplace_field_description)) },
                        placeholder = { Text(stringResource(R.string.marketplace_field_description_hint)) },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("marketplace_input_description"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TelefamRed,
                            cursorColor = TelefamRed
                        )
                    )

                    // 9. Delivery & Pickup Arrangements
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBg, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.marketplace_delivery_options_title),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MarketplaceDeliveryOption.entries.forEach { opt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedDeliveryOptions[opt] = !(selectedDeliveryOptions[opt] ?: false)
                                    }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(opt.labelRes),
                                    fontSize = 13.5.sp,
                                    color = primaryText
                                )
                                Switch(
                                    checked = selectedDeliveryOptions[opt] ?: false,
                                    onCheckedChange = { selectedDeliveryOptions[opt] = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = TelefamRed, checkedTrackColor = TelefamRed.copy(alpha = 0.3f))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (isCurrencyPickerOpen) {
            MarketplaceWorldCurrencyPickerModal(
                selectedCurrencyCode = selectedCurrency,
                onSelectCurrency = { curr ->
                    selectedCurrency = curr.code
                    isCurrencyPickerOpen = false
                },
                onDismiss = { isCurrencyPickerOpen = false }
            )
        }
    }
}

@Composable
private fun MarketplaceWorldCurrencyPickerModal(
    selectedCurrencyCode: String,
    onSelectCurrency: (MarketplaceCurrency) -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = isTelefamDarkTheme()
    val bgColor = if (isDark) Color(0xFF141C24) else Color.White
    val cardBg = if (isDark) Color(0xFF1B232E) else Color(0xFFF9FAFB)
    val primaryText = if (isDark) Color.White else Color(0xFF111827)
    val secondaryText = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    var searchQuery by remember { mutableStateOf("") }

    val filteredCurrencies = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            MarketplaceCurrencies.ALL_CURRENCIES
        } else {
            val q = searchQuery.trim().lowercase()
            MarketplaceCurrencies.ALL_CURRENCIES.filter {
                it.code.lowercase().contains(q) ||
                    it.name.lowercase().contains(q) ||
                    it.symbol.lowercase().contains(q)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = bgColor,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 520.dp)
                .fillMaxHeight(0.85f)
                .testTag("marketplace_currency_picker_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TelefamRed.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = MarketplaceLucideIcons.Tag,
                                    contentDescription = null,
                                    tint = TelefamRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.marketplace_select_currency),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = primaryText
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("marketplace_currency_picker_close")
                    ) {
                        Icon(
                            imageVector = MarketplaceLucideIcons.X,
                            contentDescription = "Close",
                            tint = secondaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.marketplace_search_currency),
                            color = secondaryText,
                            fontSize = 13.5.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = MarketplaceLucideIcons.Search,
                            contentDescription = null,
                            tint = secondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = MarketplaceLucideIcons.X,
                                    contentDescription = "Clear",
                                    tint = secondaryText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TelefamRed,
                        cursorColor = TelefamRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("marketplace_currency_search_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // World currency count badge
                Text(
                    text = "${filteredCurrencies.size} currencies available",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = secondaryText,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // List of currencies
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("marketplace_currency_list"),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = filteredCurrencies,
                        key = { it.code }
                    ) { currency ->
                        val isSelected = currency.code.equals(selectedCurrencyCode, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) TelefamRed.copy(alpha = 0.12f) else cardBg,
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) TelefamRed else (if (isDark) Color(0xFF263238) else Color(0xFFE5E7EB))
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectCurrency(currency) }
                                .testTag("marketplace_currency_option_${currency.code}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Currency code pill
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) TelefamRed else (if (isDark) Color(0xFF24303F) else Color(0xFFE5E7EB)),
                                        modifier = Modifier.widthIn(min = 46.dp)
                                    ) {
                                        Text(
                                            text = currency.code,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else primaryText
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = currency.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = primaryText
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Symbol: ${currency.symbol}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = secondaryText),
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = MarketplaceLucideIcons.Check,
                                        contentDescription = "Selected",
                                        tint = TelefamRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
