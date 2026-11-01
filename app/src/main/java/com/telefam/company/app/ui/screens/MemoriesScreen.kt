package com.telefam.company.app.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.TelefamRed
import com.example.ui.theme.isTelefamDarkTheme
import com.telefam.company.app.domain.MemoryFilterTab
import com.telefam.company.app.domain.MemoryItem
import com.telefam.company.app.domain.MemoryMediaType
import com.telefam.company.app.domain.MemoryPrivacy
import com.telefam.company.app.domain.MemoryViewMode
import com.telefam.company.app.ui.components.LucideIconKind
import com.telefam.company.app.ui.components.TelefamLucideIcon
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen(
    memories: List<MemoryItem>,
    onBackClick: () -> Unit,
    onCreateMemory: (mediaUri: String, mediaType: MemoryMediaType, caption: String, location: String, feeling: String, dateMs: Long, privacy: MemoryPrivacy) -> Unit,
    onToggleFavorite: (memoryId: String, currentFavorite: Boolean) -> Unit,
    onDeleteMemory: (memoryId: String) -> Unit,
    onShareMemory: (MemoryItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)

    val context = LocalContext.current
    val isDark = isTelefamDarkTheme()
    val screenBg = if (isDark) MaterialTheme.colorScheme.background else Color(0xFFF7F8FA)
    val cardBg = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE8EBF0)

    var selectedTab by remember { mutableStateOf(MemoryFilterTab.ALL) }
    var viewMode by remember { mutableStateOf(MemoryViewMode.GRID) }
    var searchQuery by remember { mutableStateOf("") }
    var isCreateSheetOpen by remember { mutableStateOf(false) }
    var viewingMemory by remember { mutableStateOf<MemoryItem?>(null) }
    var memoryToDelete by remember { mutableStateOf<MemoryItem?>(null) }

    // Filter memories based on active tab and search query
    val filteredMemories by remember(memories, selectedTab, searchQuery) {
        derivedStateOf {
            val byTab = when (selectedTab) {
                MemoryFilterTab.ALL -> memories
                MemoryFilterTab.ON_THIS_DAY -> memories.filter { it.isOnThisDay }
                MemoryFilterTab.HIGHLIGHTS -> memories.filter { it.isFavorite || it.isOnThisDay }
                MemoryFilterTab.BOOKMARKED -> memories.filter { it.isFavorite }
            }
            if (searchQuery.isBlank()) {
                byTab
            } else {
                val q = searchQuery.trim().lowercase()
                byTab.filter {
                    it.caption.lowercase().contains(q) ||
                        it.locationName.lowercase().contains(q) ||
                        it.tagFeeling.lowercase().contains(q) ||
                        it.formattedDate.lowercase().contains(q)
                }
            }
        }
    }

    val onThisDayHeroMemory = remember(memories) {
        memories.firstOrNull { it.isOnThisDay }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(screenBg)
            .testTag("memories_screen_root")
    ) {
        val maxWidthVal = maxWidth
        val isTablet = maxWidthVal >= 720.dp
        val maxContentWidth = if (isTablet) 900.dp else 600.dp
        val gridColumns = if (maxWidthVal >= 840.dp) 3 else 2

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Surface(
                    color = cardBg,
                    tonalElevation = 2.dp,
                    shadowElevation = if (isDark) 0.dp else 2.dp,
                    border = BorderStroke(0.5.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                    ) {
                        // Header Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = onBackClick,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("memories_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.memories_title),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // View Mode Toggle Button
                                IconButton(
                                    onClick = {
                                        viewMode = if (viewMode == MemoryViewMode.GRID) MemoryViewMode.TIMELINE else MemoryViewMode.GRID
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .testTag("memories_view_mode_toggle")
                                ) {
                                    TelefamLucideIcon(
                                        kind = if (viewMode == MemoryViewMode.GRID) LucideIconKind.MEDIA_GALLERY else LucideIconKind.FILM_THUMBNAIL,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        size = 20.dp
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // Add Memory Button
                                Button(
                                    onClick = { isCreateSheetOpen = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TelefamRed,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .testTag("memories_add_memory_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.memories_create_action),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Search Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color(0xFFF1F3F6),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 14.sp
                                        ),
                                        cursorBrush = SolidColor(TelefamRed),
                                        decorationBox = { innerTextField ->
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    text = stringResource(R.string.memories_search_hint),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 13.5.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            innerTextField()
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("memories_search_input")
                                    )
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { searchQuery = "" },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Filter Tabs Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MemoryFilterChip(
                                label = stringResource(R.string.memories_filter_all),
                                isSelected = selectedTab == MemoryFilterTab.ALL,
                                onClick = { selectedTab = MemoryFilterTab.ALL },
                                testTag = "memories_tab_all"
                            )
                            MemoryFilterChip(
                                label = stringResource(R.string.memories_filter_on_this_day),
                                isSelected = selectedTab == MemoryFilterTab.ON_THIS_DAY,
                                onClick = { selectedTab = MemoryFilterTab.ON_THIS_DAY },
                                testTag = "memories_tab_on_this_day",
                                badgeCount = memories.count { it.isOnThisDay }
                            )
                            MemoryFilterChip(
                                label = stringResource(R.string.memories_filter_highlights),
                                isSelected = selectedTab == MemoryFilterTab.HIGHLIGHTS,
                                onClick = { selectedTab = MemoryFilterTab.HIGHLIGHTS },
                                testTag = "memories_tab_highlights"
                            )
                            MemoryFilterChip(
                                label = stringResource(R.string.memories_filter_bookmarked),
                                isSelected = selectedTab == MemoryFilterTab.BOOKMARKED,
                                onClick = { selectedTab = MemoryFilterTab.BOOKMARKED },
                                testTag = "memories_tab_bookmarked"
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isCreateSheetOpen = true },
                    containerColor = TelefamRed,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 12.dp)
                        .testTag("memories_fab_create")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.memories_create_action),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = maxContentWidth)
                ) {
                    // On This Day Flashback Banner (When Available and tab is ALL or ON_THIS_DAY)
                    if (onThisDayHeroMemory != null && (selectedTab == MemoryFilterTab.ALL || selectedTab == MemoryFilterTab.ON_THIS_DAY)) {
                        OnThisDayHeroBanner(
                            memory = onThisDayHeroMemory,
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            onCardClick = { viewingMemory = onThisDayHeroMemory },
                            onShareClick = { onShareMemory(onThisDayHeroMemory) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }

                    // Empty State
                    if (filteredMemories.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(TelefamRed.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TelefamLucideIcon(
                                        kind = LucideIconKind.CAMERA,
                                        tint = TelefamRed,
                                        size = 36.dp
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.memories_empty_title),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = stringResource(R.string.memories_empty_desc),
                                    fontSize = 13.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.widthIn(max = 380.dp)
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { isCreateSheetOpen = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TelefamRed,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("memories_empty_create_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.memories_create_action),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    } else if (viewMode == MemoryViewMode.GRID) {
                        // Grid Mode
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(gridColumns),
                            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("memories_grid")
                        ) {
                            items(filteredMemories, key = { it.id }) { item ->
                                MemoryGridCard(
                                    memory = item,
                                    cardBg = cardBg,
                                    cardBorder = cardBorder,
                                    onClick = { viewingMemory = item },
                                    onToggleFavorite = { onToggleFavorite(item.id, item.isFavorite) },
                                    onShare = { onShareMemory(item) },
                                    onDelete = { memoryToDelete = item }
                                )
                            }
                        }
                    } else {
                        // Timeline Mode
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("memories_timeline")
                        ) {
                            items(filteredMemories, key = { it.id }) { item ->
                                MemoryTimelineCard(
                                    memory = item,
                                    cardBg = cardBg,
                                    cardBorder = cardBorder,
                                    onClick = { viewingMemory = item },
                                    onToggleFavorite = { onToggleFavorite(item.id, item.isFavorite) },
                                    onShare = { onShareMemory(item) },
                                    onDelete = { memoryToDelete = item }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Memory Sheet
    if (isCreateSheetOpen) {
        CreateMemoryBottomSheet(
            onDismiss = { isCreateSheetOpen = false },
            onCreate = { uri, type, caption, location, feeling, dateMs, privacy ->
                onCreateMemory(uri, type, caption, location, feeling, dateMs, privacy)
                isCreateSheetOpen = false
                Toast.makeText(context, "Memory saved successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // View Memory Fullscreen Dialog
    viewingMemory?.let { memory ->
        MemoryDetailDialog(
            memory = memory,
            onDismiss = { viewingMemory = null },
            onToggleFavorite = { onToggleFavorite(memory.id, memory.isFavorite) },
            onShare = { onShareMemory(memory) },
            onDelete = {
                viewingMemory = null
                memoryToDelete = memory
            }
        )
    }

    // Delete Confirmation Dialog
    memoryToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { memoryToDelete = null },
            title = {
                Text(
                    text = stringResource(R.string.memories_delete_action),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.memories_delete_confirm),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteMemory(item.id)
                        memoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TelefamRed,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("confirm_delete_memory_button")
                ) {
                    Text(stringResource(R.string.memories_delete_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { memoryToDelete = null }) {
                    Text(stringResource(R.string.crop_cancel_button))
                }
            }
        )
    }
}

@Composable
private fun MemoryFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    badgeCount: Int = 0
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) TelefamRed else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, if (isSelected) TelefamRed else Color.Transparent),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else TelefamRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$badgeCount",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) TelefamRed else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun OnThisDayHeroBanner(
    memory: MemoryItem,
    cardBg: Color,
    cardBorder: Color,
    onCardClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.5.dp, TelefamRed.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("memories_hero_on_this_day")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Flashback Tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFD90817), TelefamRed, Color(0xFFB30612))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TelefamLucideIcon(
                            kind = LucideIconKind.CALENDAR_EVENT,
                            tint = Color.White,
                            size = 16.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (memory.yearsAgo == 1) {
                                stringResource(R.string.memories_one_year_ago)
                            } else {
                                stringResource(R.string.memories_years_ago, memory.yearsAgo.coerceAtLeast(1))
                            }.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Share button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.clickable(onClick = onShareClick)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.memories_share_action),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Image Preview & Caption
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = memory.mediaUri,
                    contentDescription = memory.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    if (memory.caption.isNotBlank()) {
                        Text(
                            text = memory.caption,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = memory.formattedDate,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (memory.locationName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TelefamLucideIcon(
                                kind = LucideIconKind.MAP_PIN,
                                tint = TelefamRed,
                                size = 12.dp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = memory.locationName,
                                fontSize = 11.5.sp,
                                color = TelefamRed,
                                fontWeight = FontWeight.Medium,
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

@Composable
private fun MemoryGridCard(
    memory: MemoryItem,
    cardBg: Color,
    cardBorder: Color,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("memory_grid_item_${memory.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = memory.mediaUri,
                    contentDescription = memory.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (memory.isOnThisDay) {
                        Surface(
                            color = TelefamRed,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${memory.yearsAgo}y ago",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    // Favorite Button
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.45f),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(onClick = onToggleFavorite)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (memory.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (memory.isFavorite) TelefamRed else Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                // Video indicator
                if (memory.mediaType == MemoryMediaType.VIDEO) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VIDEO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Info row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                if (memory.caption.isNotBlank()) {
                    Text(
                        text = memory.caption,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = memory.formattedDate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.memories_share_action)) },
                                onClick = {
                                    menuExpanded = false
                                    onShare()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.memories_favorite_action)) },
                                onClick = {
                                    menuExpanded = false
                                    onToggleFavorite()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.memories_delete_action), color = TelefamRed) },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoryTimelineCard(
    memory: MemoryItem,
    cardBg: Color,
    cardBorder: Color,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("memory_timeline_item_${memory.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Date & Location & Menu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = memory.formattedDate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (memory.isOnThisDay) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = TelefamRed.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${memory.yearsAgo}y ago today",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TelefamRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    if (memory.locationName.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            TelefamLucideIcon(
                                kind = LucideIconKind.MAP_PIN,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                size = 12.dp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = memory.locationName,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.memories_share_action)) },
                            onClick = {
                                menuExpanded = false
                                onShare()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.memories_favorite_action)) },
                            onClick = {
                                menuExpanded = false
                                onToggleFavorite()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.memories_delete_action), color = TelefamRed) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            // Image
            AsyncImage(
                model = memory.mediaUri,
                contentDescription = memory.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(Color.DarkGray)
            )

            // Caption & Action Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                if (memory.caption.isNotBlank()) {
                    Text(
                        text = memory.caption,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (memory.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (memory.isFavorite) TelefamRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (memory.tagFeeling.isNotBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "#${memory.tagFeeling}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateMemoryBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (mediaUri: String, mediaType: MemoryMediaType, caption: String, location: String, feeling: String, dateMs: Long, privacy: MemoryPrivacy) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var feeling by remember { mutableStateOf("Joyful") }
    var privacy by remember { mutableStateOf(MemoryPrivacy.ONLY_ME) }
    var memoryDateOffsetYears by remember { mutableStateOf(0) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedMediaUri = uri
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.memories_create_action),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Media Picker Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    }
                    .testTag("memory_picker_trigger"),
                contentAlignment = Alignment.Center
            ) {
                if (selectedMediaUri != null) {
                    AsyncImage(
                        model = selectedMediaUri,
                        contentDescription = "Selected Media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(TelefamRed.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            TelefamLucideIcon(
                                kind = LucideIconKind.CAMERA,
                                tint = TelefamRed,
                                size = 26.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Select Photo or Video from Gallery",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TelefamRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Caption
            OutlinedTextField(
                value = caption,
                onValueChange = { if (it.length <= 200) caption = it },
                label = { Text(stringResource(R.string.memories_add_caption)) },
                placeholder = { Text(stringResource(R.string.memories_caption_hint)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TelefamRed,
                    focusedLabelColor = TelefamRed
                ),
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("memory_caption_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.memories_location_hint)) },
                leadingIcon = {
                    TelefamLucideIcon(
                        kind = LucideIconKind.MAP_PIN,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        size = 18.dp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TelefamRed,
                    focusedLabelColor = TelefamRed
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("memory_location_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Memory Date Offset (Today, 1 Year Ago, 2 Years Ago, 3 Years Ago)
            Text(
                text = stringResource(R.string.memories_date_label),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0 to "Today",
                    1 to "1 Year Ago",
                    2 to "2 Years Ago",
                    3 to "3 Years Ago"
                ).forEach { (offset, label) ->
                    val isSelected = memoryDateOffsetYears == offset
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) TelefamRed else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable { memoryDateOffsetYears = offset }
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mood / Category Tags
            Text(
                text = stringResource(R.string.memories_feeling_hint),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Joyful", "Nostalgic", "Grateful", "Adventure", "Celebration", "Travel").forEach { tag ->
                    val isSelected = feeling == tag
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) TelefamRed else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.clickable { feeling = tag }
                    ) {
                        Text(
                            text = "#$tag",
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Privacy Selector
            Text(
                text = "Privacy Audience",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MemoryPrivacy.values().forEach { priv ->
                    val isSelected = privacy == priv
                    val label = when (priv) {
                        MemoryPrivacy.ONLY_ME -> stringResource(R.string.memories_privacy_only_me)
                        MemoryPrivacy.FRIENDS -> stringResource(R.string.memories_privacy_friends)
                        MemoryPrivacy.PUBLIC -> stringResource(R.string.memories_privacy_public)
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) TelefamRed else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { privacy = priv }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            val isReady = selectedMediaUri != null
            Button(
                onClick = {
                    val uriStr = selectedMediaUri?.toString().orEmpty()
                    val cal = Calendar.getInstance().apply {
                        add(Calendar.YEAR, -memoryDateOffsetYears)
                    }
                    onCreate(
                        uriStr,
                        MemoryMediaType.IMAGE,
                        caption.trim(),
                        location.trim(),
                        feeling,
                        cal.timeInMillis,
                        privacy
                    )
                },
                enabled = isReady,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TelefamRed,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("memory_save_submit_button")
            ) {
                Text(
                    text = stringResource(R.string.memories_save_button),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MemoryDetailDialog(
    memory: MemoryItem,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Image View
                AsyncImage(
                    model = memory.mediaUri,
                    contentDescription = memory.caption,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = if (memory.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (memory.isFavorite) TelefamRed else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            TelefamLucideIcon(
                                kind = LucideIconKind.TRASH,
                                tint = TelefamRed,
                                size = 20.dp
                            )
                        }
                    }
                }

                // Bottom Info Overlay
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                        .navigationBarsPadding()
                        .padding(18.dp)
                ) {
                    Text(
                        text = memory.formattedDate,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (memory.caption.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = memory.caption,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }

                    if (memory.locationName.isNotBlank() || memory.tagFeeling.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (memory.locationName.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TelefamLucideIcon(
                                        kind = LucideIconKind.MAP_PIN,
                                        tint = TelefamRed,
                                        size = 14.dp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = memory.locationName,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            if (memory.tagFeeling.isNotBlank()) {
                                Text(
                                    text = "#${memory.tagFeeling}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFB3B8),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
