package com.telefam.company.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.TelefamRed
import com.telefam.company.app.data.GiflyHubClient
import com.telefam.company.app.data.GiflyMediaItemDto
import com.telefam.company.app.ui.screens.LucideCampaignIcons
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class GiflySubTab(val typeCode: String, val label: String) {
    STICKERS("stickers", "Stickers"),
    GIFS("gifs", "GIFs"),
    CLIPS("clips", "Clips"),
    EMOJIS("emojis", "Emojis")
}

/**
 * Native internal media and sticker composer bottom sheet.
 * - Subtitles: Stickers, GIFs, Clips, Emojis
 * - Grid with autoplay preview
 * - Tap opens preview modal with caption option and direct send
 * - Automatically caches media to device disk
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickersAndGifsComposerSheet(
    onDismissRequest: () -> Unit,
    onSendMedia: (item: GiflyMediaItemDto, caption: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(GiflySubTab.STICKERS) }
    var searchQuery by remember { mutableStateOf("") }
    var mediaItems by remember { mutableStateOf<List<GiflyMediaItemDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var previewTargetItem by remember { mutableStateOf<GiflyMediaItemDto?>(null) }
    var previewCaption by remember { mutableStateOf("") }

    var searchJob by remember { mutableStateOf<Job?>(null) }

    fun loadMedia(query: String, tab: GiflySubTab) {
        searchJob?.cancel()
        searchJob = scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = GiflyHubClient.api.getMedia(
                    type = tab.typeCode,
                    query = query.ifBlank { null },
                    page = 1,
                    perPage = 30
                )
                if (response.success) {
                    mediaItems = response.items
                    if (response.items.isEmpty()) {
                        errorMessage = "No ${tab.label.lowercase()} found. Try searching for something else."
                    }
                } else {
                    errorMessage = response.error ?: "Unable to fetch ${tab.label.lowercase()}."
                }
            } catch (e: Exception) {
                errorMessage = "Network connection issue. Please check your internet connection."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedTab) {
        loadMedia(searchQuery, selectedTab)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFF16171B),
        dragHandle = null,
        modifier = modifier.testTag("stickers_composer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header with Drag Pill and Close button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF3E4048))
                )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = LucideCommentIcons.CloseX,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { text ->
                    searchQuery = text
                    searchJob?.cancel()
                    searchJob = scope.launch {
                        delay(400)
                        loadMedia(text, selectedTab)
                    }
                },
                placeholder = {
                    Text(
                        text = "Search ${selectedTab.label.lowercase()}...",
                        color = Color(0xFF8E909A),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF22242B),
                    unfocusedContainerColor = Color(0xFF22242B),
                    focusedBorderColor = TelefamRed,
                    unfocusedBorderColor = Color(0xFF2F323D),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sticker_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Tabs: Stickers | GIFs | Clips | Emojis
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = TelefamRed,
                        height = 2.5.dp
                    )
                },
                divider = {}
            ) {
                GiflySubTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = {
                            selectedTab = tab
                        },
                        text = {
                            Text(
                                text = tab.label,
                                fontSize = 13.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF8E909A)
                            )
                        },
                        modifier = Modifier.testTag("sticker_subtab_${tab.typeCode}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Media Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = TelefamRed,
                        modifier = Modifier.size(36.dp)
                    )
                } else if (!errorMessage.isNullOrBlank()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "Unable to load media",
                            color = Color(0xFF8E909A),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { loadMedia(searchQuery, selectedTab) },
                            colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Retry", fontSize = 12.sp, color = Color.White)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(mediaItems, key = { it.id }) { item ->
                            val displayUrl = item.bestPreviewUrl
                            Surface(
                                onClick = {
                                    previewTargetItem = item
                                    previewCaption = ""
                                    // Proactively cache to device in background
                                    scope.launch {
                                        GiflyHubClient.cacheMediaToDevice(context, item.id, item.bestDisplayUrl)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF22242B),
                                border = BorderStroke(1.dp, Color(0xFF2E313D)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .testTag("sticker_grid_item_${item.id}")
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(displayUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Preview and Caption Modal before Sending
    previewTargetItem?.let { item ->
        ModalBottomSheet(
            onDismissRequest = { previewTargetItem = null },
            containerColor = Color(0xFF1E1F24)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.title.ifBlank { "Preview ${selectedTab.label}" },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF121316))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(item.bestDisplayUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = previewCaption,
                    onValueChange = { previewCaption = it },
                    placeholder = { Text("Add a caption (optional)...", color = Color(0xFF8E909A), fontSize = 13.5.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF262830),
                        unfocusedContainerColor = Color(0xFF262830),
                        focusedBorderColor = TelefamRed,
                        unfocusedBorderColor = Color(0xFF333742),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val target = item
                            previewTargetItem = null
                            onDismissRequest()
                            onSendMedia(target, previewCaption.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TelefamRed),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sticker_confirm_send_button")
                    ) {
                        Text("Send", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
