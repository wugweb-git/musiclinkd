package com.example.onboarding.ui.screens

import com.example.onboarding.util.DemoDataProvider
import com.example.onboarding.util.DynamicLinkManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --------------------------------------------------------------------------------------
// COLOR TOKENS (MUSICLINKD SUPPORTING SCREENS DESIGN SYSTEM)
// --------------------------------------------------------------------------------------
private val BrandPrimary = Color(0xFFEA4C89)      // Pink
private val BrandAccent = Color(0xFFEA4C89)       // Pink (CTAs, accents)
private val BrandDark = Color(0xFF0D0B26)         // Navy Headline
private val BrandBackground = Color(0xFFFCFCFD)   // Soft off-white
private val BrandBorder = Color(0xFFECECF1)       // Subtle separation
private val BrandSecondaryText = Color(0xFF7D7D8E)// Muted grey
private val BrandSuccess = Color(0xFF10B981)      // Green Success
private val BrandCardBg = Color(0xFFFFFFFF)
private val LocalLightPurpleBg = Color(0xFFFFF0F5)

// --------------------------------------------------------------------------------------
// HUB NAVIGATION STATES
// --------------------------------------------------------------------------------------
enum class SupportingModule {
    CONNECTIONS,
    NETWORKING_FEED,
    ARTIST_PROFILE,
    UNIVERSAL_SEARCH,
    QR_SHARE,
    SAVED_FAVORITES,
    SETTINGS,
    HELP_SUPPORT
}

enum class SearchScreenState {
    HOME, LIVE, RESULTS, EMPTY
}

enum class SavedTab {
    ARTISTS, BANDS, STUDIOS, MARKETPLACE, EMPTY
}

enum class SettingsSubPage {
    MAIN, ACCOUNT, PRIVACY, NOTIFICATIONS, SECURITY, APPEARANCE
}

enum class HelpSubPage {
    CENTER, FAQ, CONTACT, REPORT_USER, REPORT_LISTING, FEEDBACK
}

// --------------------------------------------------------------------------------------
// MAIN CONTAINER COMPOSABLE
// --------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportingScreensHub(
    initialModule: SupportingModule = SupportingModule.UNIVERSAL_SEARCH,
    onBack: () -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeModule by remember { mutableStateOf(initialModule) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandCardBg)
                    .statusBarsPadding()
            ) {
                // Main Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BrandBackground)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = BrandDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "MusicLinkd Hub",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandAccent.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SUPPORTING FLOWS",
                            color = BrandAccent,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Horizontal Selector for Modules
                ScrollableTabRow(
                    selectedTabIndex = activeModule.ordinal,
                    edgePadding = 16.dp,
                    containerColor = BrandCardBg,
                    contentColor = BrandPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[activeModule.ordinal]),
                            color = BrandPrimary
                        )
                    },
                    divider = { HorizontalDivider(color = BrandBorder) }
                ) {
                    SupportingModule.values().forEach { module ->
                        val selected = activeModule == module
                        Tab(
                            selected = selected,
                            onClick = { activeModule = module },
                            text = {
                                Text(
                                    text = when (module) {
                                        SupportingModule.CONNECTIONS -> "🤝 Connections"
                                        SupportingModule.NETWORKING_FEED -> "🌐 Feed"
                                        SupportingModule.ARTIST_PROFILE -> "👨‍🎤 Artist Profile"
                                        SupportingModule.UNIVERSAL_SEARCH -> "🔍 Search"
                                        SupportingModule.QR_SHARE -> "🎴 QR & Share"
                                        SupportingModule.SAVED_FAVORITES -> "🔖 Saved"
                                        SupportingModule.SETTINGS -> "⚙️ Settings"
                                        SupportingModule.HELP_SUPPORT -> "🙋 Help & Support"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) BrandPrimary else BrandSecondaryText
                                )
                            }
                        )
                    }
                }
            }
        },
        containerColor = BrandBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeModule,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "module_transition"
            ) { targetModule ->
                when (targetModule) {
                    SupportingModule.CONNECTIONS -> {
                        ConnectionsFlow(
                            showSnackbar = { text ->
                                scope.launch { snackbarHostState.showSnackbar(text) }
                            }
                        )
                    }
                    SupportingModule.NETWORKING_FEED -> {
                        com.example.onboarding.ui.components.NetworkingFeed(
                            onOpenProfile = { activeModule = SupportingModule.ARTIST_PROFILE }
                        )
                    }
                    SupportingModule.ARTIST_PROFILE -> {
                        com.example.onboarding.ui.components.ArtistProfileScreen(
                            onBackClick = { activeModule = SupportingModule.NETWORKING_FEED }
                        )
                    }
                    SupportingModule.UNIVERSAL_SEARCH -> {
                        UniversalSearchFlow(
                            showSnackbar = { text ->
                                scope.launch { snackbarHostState.showSnackbar(text) }
                            }
                        )
                    }
                    SupportingModule.QR_SHARE -> {
                        QrAndShareFlow(
                            showSnackbar = { text ->
                                scope.launch { snackbarHostState.showSnackbar(text) }
                            }
                        )
                    }
                    SupportingModule.SAVED_FAVORITES -> {
                        SavedAndFavoritesFlow(
                            showSnackbar = { text ->
                                scope.launch { snackbarHostState.showSnackbar(text) }
                            }
                        )
                    }
                    SupportingModule.SETTINGS -> {
                        SettingsFlow(
                            showSnackbar = { text ->
                                scope.launch { snackbarHostState.showSnackbar(text) }
                            },
                            onLogout = onLogout
                        )
                    }
                    SupportingModule.HELP_SUPPORT -> {
                        HelpAndSupportFlow(
                            showSnackbar = { text ->
                                scope.launch { snackbarHostState.showSnackbar(text) }
                            }
                        )
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// MODULE 1: UNIVERSAL SEARCH
// --------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UniversalSearchFlow(
    showSnackbar: (String) -> Unit
) {
    var screenState by remember { mutableStateOf(SearchScreenState.HOME) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    
    val recentSearches = remember { mutableStateListOf("Atmos Studios", "Sitar player", "Violin sessions", "Lollapalooza 2026") }
    val trendingSearches = listOf("Karan Malhotra", "SSL console Mumbai", "Foley recording Pune", "Acoustic Band", "Synthpop Producers")
    
    val mockAllResults = listOf(
        SearchItem("Rohan Sharma", "Musicians", "Bandra, Mumbai", true, "🎸", "Lead Guitarist & Producer", listOf("Indie Rock", "Gibson", "Ableton")),
        SearchItem("Aditi Sen", "Musicians", "Indiranagar, Bengaluru", true, "", "Jazz Vocalist & Topliner", listOf("Neo-Soul", "Vocal Tuning", "Jazz")),
        SearchItem("Monsoon Echoes", "Bands", "Dharavi, Mumbai", true, "🥁", "Alternative Fusion Band • 4 Members", listOf("Alternative", "Touring", "In Studio")),
        SearchItem("Shadows of Banaras Film Score", "Projects", "Bollywood Post-Production", true, "🎬", "Full Feature Soundtrack • Seeking Flutist", listOf("Film Score", "Classical Fusion", "Open Audition")),
        SearchItem("Bombay Jazz Ensemble", "Bands", "Colaba, Mumbai", true, "🎷", "Modern Ethno-Jazz Quartet • 5 Members", listOf("Ethno-Jazz", "Live Gigs", "Brass")),
        SearchItem("Octave Sound Labs", "Studios", "Bandra, Mumbai", true, "🎙️", "Atmos 7.1.4 Mixing Suite", listOf("Dolby Atmos", "SSL Console", "Acoustics")),
        SearchItem("Vikramaditya Roy", "Musicians", "Hauz Khas, Delhi", true, "🪕", "Sitar Virtuoso & Session Soloist", listOf("Classical Fusion", "Raga", "Live Concert")),
        SearchItem("Retro Synth EP Collaboration", "Projects", "Remote Studio Co-Write", false, "🎹", "5-Track Synthwave EP • Stem Mix Phase", listOf("Synthwave", "Co-Write", "Mastering Needed")),
        SearchItem("Velvet Frequency", "Bands", "Anjuna, Goa", false, "🎸", "Indie Pop Trio • Seeking Keyboardist", listOf("Indie Pop", "Beach Festivals", "Live Tour")),
        SearchItem("Maya Lin", "Musicians", "Koregaon Park, Pune", true, "🎛️", "Electronic Music Producer & Engineer", listOf("EDM", "Logic Pro X", "Mixing"))
    )

    val filteredResults = mockAllResults.filter {
        val matchesQuery = searchQuery.isEmpty() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true) ||
                it.subcategory.contains(searchQuery, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
        val matchesTab = if (selectedFilter == "All") true else it.category.equals(selectedFilter, ignoreCase = true)
        matchesQuery && matchesTab
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            screenState = if (filteredResults.isEmpty()) SearchScreenState.EMPTY else SearchScreenState.LIVE
        } else {
            screenState = SearchScreenState.HOME
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar Block
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, BrandBorder, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = BrandCardBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search artists, studios, instruments...", fontSize = 14.sp, color = BrandSecondaryText) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("universal_search_field"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = BrandSecondaryText)
                    }
                }
            }
        }

        // Animated Screen Body
        AnimatedContent(
            targetState = screenState,
            transitionSpec = {
                slideInVertically(initialOffsetY = { 50 }) + fadeIn() togetherWith
                slideOutVertically(targetOffsetY = { -50 }) + fadeOut()
            },
            label = "search_screen_state"
        ) { targetState ->
            when (targetState) {
                SearchScreenState.HOME -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Suggested Categories Horizontal
                        item {
                            Text("Suggested Categories", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val categories = listOf(
                                    CategoryChip("Artists", "👨‍🎤"),
                                    CategoryChip("Studios", "🎙️"),
                                    CategoryChip("Equipment", "🎛️"),
                                    CategoryChip("Rentals", "🎸"),
                                    CategoryChip("Opportunities", "🌟"),
                                    CategoryChip("Projects", "🎬")
                                )
                                items(categories) { cat ->
                                    Card(
                                        modifier = Modifier
                                            .clickable { searchQuery = cat.title }
                                            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(cat.emoji, fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(cat.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                                        }
                                    }
                                }
                            }
                        }

                        // Recent Searches
                        if (recentSearches.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Recent Searches", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 14.sp)
                                    Text(
                                        "Clear All",
                                        color = BrandAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { recentSearches.clear() }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    recentSearches.forEach { search ->
                                        Row(
                                            modifier = Modifier
                                                .background(BrandBorder.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                                .clickable { searchQuery = search }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(search, fontSize = 12.sp, color = BrandDark)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = BrandSecondaryText,
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clickable { recentSearches.remove(search) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Trending Searches
                        item {
                            Text("Trending Searches", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    trendingSearches.forEachIndexed { idx, trend ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { searchQuery = trend }
                                                .padding(vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "#${idx + 1}",
                                                color = BrandPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.width(30.dp)
                                            )
                                            Text(trend, fontSize = 13.sp, color = BrandDark, fontWeight = FontWeight.Medium)
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(Icons.Default.TrendingUp, null, tint = BrandAccent, modifier = Modifier.size(16.dp))
                                        }
                                        if (idx < trendingSearches.size - 1) {
                                            HorizontalDivider(color = BrandBorder)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                SearchScreenState.LIVE -> {
                    // Suggestions results
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Quick filter pills row
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val filters = listOf("All", "Musicians", "Bands", "Projects", "Studios")
                            items(filters) { filter ->
                                val isSelected = selectedFilter == filter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedFilter = filter },
                                    label = { Text(filter, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrandPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Results List
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text("Global Search Results (${filteredResults.size})", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 13.sp)
                            }

                            items(filteredResults) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showSnackbar("Selected: ${item.name}")
                                            if (!recentSearches.contains(item.name)) {
                                                recentSearches.add(0, item.name)
                                            }
                                        }
                                        .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(CircleShape)
                                                    .background(BrandBorder.copy(alpha = 0.5f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(item.icon, fontSize = 22.sp)
                                            }
                                            Spacer(modifier = Modifier.width(14.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = BrandDark)
                                                    if (item.verified) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(Icons.Default.CheckCircle, "Verified", tint = BrandPrimary, modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                                if (item.subcategory.isNotEmpty()) {
                                                    Text(item.subcategory, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = BrandPrimary)
                                                }
                                                Text("${item.category} • ${item.location}", fontSize = 11.sp, color = BrandSecondaryText)
                                            }
                                            com.example.onboarding.ui.components.UnifiedConnectButton(
                                                targetUserId = "search_${item.name.lowercase().replace(" ", "_")}",
                                                targetUserName = item.name
                                            )
                                        }

                                        if (item.tags.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                                            androidx.compose.foundation.layout.FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                item.tags.forEach { tag ->
                                                    Surface(
                                                        color = BrandBorder.copy(alpha = 0.3f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text(
                                                            text = "#$tag",
                                                            fontSize = 10.sp,
                                                            color = BrandDark,
                                                            fontWeight = FontWeight.Medium,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
                }
                SearchScreenState.EMPTY -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        com.example.onboarding.ui.components.IllustratedEmptyStateView(
                            badgeText = "Global Registry Search",
                            title = "No Matches Found",
                            description = "We couldn't find any musicians, bands, projects, or studios matching \"$searchQuery\". Try searching by genre tag, city, or instrument.",
                            icon = Icons.Default.SearchOff,
                            illustrationEmoji = "🔍",
                            primaryActionLabel = "Clear Search Term",
                            onPrimaryAction = { searchQuery = "" }
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

data class CategoryChip(val title: String, val emoji: String)
data class SearchItem(
    val name: String,
    val category: String,
    val location: String,
    val verified: Boolean,
    val icon: String,
    val subcategory: String = "",
    val tags: List<String> = emptyList()
)

// --------------------------------------------------------------------------------------
// MODULE 2: QR & SHARE
// --------------------------------------------------------------------------------------
@Composable
fun QrAndShareFlow(
    showSnackbar: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var shareScreenState by remember { mutableStateOf("Home") }
    var downloadingPdf by remember { mutableStateOf(false) }
    var pdfProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(downloadingPdf) {
        if (downloadingPdf) {
            pdfProgress = 0f
            while (pdfProgress < 1f) {
                delay(150)
                pdfProgress += 0.1f
            }
            downloadingPdf = false
            showSnackbar("Premium Comp Card downloaded as PDF!")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (shareScreenState == "Home" || shareScreenState == "Preview") {
            // Screen 1: Share Profile
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(24.dp))
                    .shadow(4.rbShadow(), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BrandCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar and profile mini preview
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(colors = listOf(BrandPrimary, BrandAccent))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("VS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Vedanshu Srivastava", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BrandDark)
                    Text("Chief Music Director • Bandra West", fontSize = 12.sp, color = BrandSecondaryText)
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // QR Code visual block (interactive click to expand preview)
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp))
                            .clickable { shareScreenState = "Preview" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // High-contrast custom vector simulator
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(7) { rowIdx ->
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    repeat(7) { colIdx ->
                                        val isActive = (rowIdx + colIdx) % 2 == 0 || (rowIdx == 0 || rowIdx == 6 || colIdx == 0 || colIdx == 6)
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(if (isActive) BrandDark else Color.White)
                                        )
                                    }
                                }
                            }
                        }
                        // Middle identity badge overlay
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ML", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Click QR to preview full-screen", fontSize = 11.sp, color = BrandSecondaryText)

                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Vanity URL
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(BrandBackground)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "musiclinkd.com/vedanshu",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandDark,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { showSnackbar("Link copied to clipboard!") },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action row buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        DynamicLinkManager.shareProfileDynamicLink(
                            context = context,
                            userId = "vedanshu_srivastava",
                            userName = "Vedanshu Srivastava",
                            userRole = "Music Producer & Sound Engineer"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Share, "Share", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Profile Link", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showSnackbar("QR Image saved to your device album!") },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BrandBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandDark),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Download, "Download", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save QR Code", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📡 NFC Ready — Tap other phone to audition", fontSize = 11.sp, color = BrandSecondaryText, fontWeight = FontWeight.Bold)
            }
        } else if (shareScreenState == "Preview") {
            // Screen 2: QR Preview Full-screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(onClick = { shareScreenState = "Home" }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back to preview")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandDark)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("MUSICLINKD PRO", color = BrandAccent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Scan to Audition", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Full size QR simulation
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                repeat(7) { rowIdx ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                        repeat(7) { colIdx ->
                                            val isActive = (rowIdx + colIdx) % 2 == 0 || (rowIdx < 2 || rowIdx > 4 || colIdx < 2 || colIdx > 4)
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (isActive) BrandDark else Color.White)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Vedanshu Srivastava", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Synthesizers & Orchestration", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            }
        } else if (shareScreenState == "Options") {
            // Screen 3: Share Options
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(onClick = { shareScreenState = "Home" }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back")
                    }
                }

                Text("Share Options", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BrandDark)
                Spacer(modifier = Modifier.height(16.dp))

                val socialChannels = listOf(
                    Triple("WhatsApp", Icons.Default.ChatBubble, Color(0xFF25D366)),
                    Triple("Instagram Feed", Icons.Default.CameraAlt, Color(0xFFE1306C)),
                    Triple("LinkedIn", Icons.Default.Work, Color(0xFF0077B5)),
                    Triple("Email Pitch", Icons.Default.Mail, Color(0xFF4285F4)),
                    Triple("SMS Pitch", Icons.Default.Sms, Color(0xFFEA4C89))
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        socialChannels.forEachIndexed { index, channel ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showSnackbar("Shared successfully on ${channel.first}!") }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(channel.third.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(channel.second, null, tint = channel.third, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(channel.first, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ChevronRight, null, tint = BrandSecondaryText, modifier = Modifier.size(16.dp))
                            }
                            if (index < socialChannels.size - 1) {
                                HorizontalDivider(color = BrandBorder)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Premium Comp Card PDF Download option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandPrimary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LocalLightPurpleBg)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Download PDF Comp Card", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 14.sp)
                        Text("Generate a stunning 1-page professional resume & portfolio pitch doc for agency bookings.", fontSize = 11.sp, color = BrandSecondaryText)
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        if (downloadingPdf) {
                            Column {
                                LinearProgressIndicator(
                                    progress = { pdfProgress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                    color = BrandPrimary,
                                    trackColor = BrandBorder
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Compiling portfolio audio data... ${(pdfProgress * 100).toInt()}%", fontSize = 10.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { downloadingPdf = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text("Compile & Download PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Support for shadow radius helper
private fun Int.rbShadow() = this.dp

// --------------------------------------------------------------------------------------
// MODULE 3: SAVED & FAVORITES
// --------------------------------------------------------------------------------------
@Composable
fun SavedAndFavoritesFlow(
    showSnackbar: (String) -> Unit
) {
    var activeSavedTab by remember { mutableStateOf(SavedTab.ARTISTS) }
    var isGridView by remember { mutableStateOf(false) }

    // Mutable items to simulate delete action
    val savedArtists = remember {
        mutableStateListOf(
            SavedArtistItem("Amit Trivedi", "Chief Composer", "Juhu", "5.0", "AT"),
            SavedArtistItem("Pooja Singhal", "Lead Vocalist", "Andheri", "4.9", "PS"),
            SavedArtistItem("Karan Malhotra", "Atmos Engineer", "Bandra", "4.8", "KM")
        )
    }

    val savedStudios = remember {
        mutableStateListOf(
            SavedStudioItem("Octave Sound Labs", "Bandra West", "SSL Console, Neve Pres, Atmos Setup", 4.9),
            SavedStudioItem("Beat Factory Studio", "Andheri East", "ProTools Native, Avalon, Genelec Mics", 4.7)
        )
    }

    val savedMarketplace = remember {
        mutableStateListOf(
            SavedMarketItem("Gibson Les Paul Studio 2024", "₹95,000", "Devraj Sen", "🎸", "https://images.unsplash.com/photo-1516924962500-2b4b3b99ea02?auto=format&fit=crop&w=300&q=80"),
            SavedMarketItem("Neumann TLM 103 Microphone", "₹75,000", "Aria Vox Labs", "🎙️", "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?auto=format&fit=crop&w=300&q=80")
        )
    }

    val isEmpty = (activeSavedTab == SavedTab.ARTISTS && savedArtists.isEmpty()) ||
                  (activeSavedTab == SavedTab.STUDIOS && savedStudios.isEmpty()) ||
                  (activeSavedTab == SavedTab.MARKETPLACE && savedMarketplace.isEmpty())

    Column(modifier = Modifier.fillMaxSize()) {
        // Toggle view type and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "My Bookmarks & Collections",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = BrandDark
            )
            IconButton(onClick = { isGridView = !isGridView }) {
                Icon(
                    imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                    contentDescription = "Toggle Grid/List",
                    tint = BrandPrimary
                )
            }
        }

        // Sub-tabs for Saved Module
        ScrollableTabRow(
            selectedTabIndex = if (isEmpty) 3 else activeSavedTab.ordinal,
            containerColor = BrandBackground,
            edgePadding = 16.dp,
            divider = { HorizontalDivider(color = BrandBorder) }
        ) {
            listOf("Artists", "Studios", "Marketplace").forEachIndexed { idx, tabTitle ->
                val tabType = when (idx) {
                    0 -> SavedTab.ARTISTS
                    1 -> SavedTab.STUDIOS
                    else -> SavedTab.MARKETPLACE
                }
                val selected = activeSavedTab == tabType && !isEmpty
                Tab(
                    selected = selected,
                    onClick = { activeSavedTab = tabType },
                    text = { Text(tabTitle, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isEmpty) {
            // Empty State
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🔖", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Your favorites folder is silent.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BrandDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Browse listings, discover talents, or book premium spaces to start building your professional collection.",
                    fontSize = 12.sp,
                    color = BrandSecondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        // Reset items to showcase flow again
                        savedArtists.addAll(listOf(
                            SavedArtistItem("Amit Trivedi", "Chief Composer", "Juhu", "5.0", "AT"),
                            SavedArtistItem("Pooja Singhal", "Lead Vocalist", "Andheri", "4.9", "PS")
                        ))
                        savedStudios.addAll(listOf(
                            SavedStudioItem("Octave Sound Labs", "Bandra West", "SSL Console, Neve Pres, Atmos Setup", 4.9)
                        ))
                        savedMarketplace.addAll(listOf(
                            SavedMarketItem("Gibson Les Paul Studio 2024", "₹95,000", "Devraj Sen", "🎸", "https://images.unsplash.com/photo-1516924962500-2b4b3b99ea02?auto=format&fit=crop&w=300&q=80")
                        ))
                        showSnackbar("Restored mock favorites list!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Browse Discover Feed / Restore Items", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        } else {
            // Screen content lists
            Box(modifier = Modifier.weight(1f)) {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (activeSavedTab) {
                            SavedTab.ARTISTS -> {
                                items(savedArtists) { item ->
                                    ArtistGridCard(item, onRemove = { savedArtists.remove(item); showSnackbar("Removed ${item.name} from saved list!") })
                                }
                            }
                            SavedTab.STUDIOS -> {
                                items(savedStudios) { item ->
                                    StudioGridCard(item, onRemove = { savedStudios.remove(item); showSnackbar("Removed ${item.name}!") })
                                }
                            }
                            SavedTab.MARKETPLACE -> {
                                items(savedMarketplace) { item ->
                                    MarketGridCard(item, onRemove = { savedMarketplace.remove(item); showSnackbar("Removed ${item.title}!") })
                                }
                            }
                            else -> {}
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (activeSavedTab) {
                            SavedTab.ARTISTS -> {
                                items(savedArtists) { item ->
                                    ArtistRowCard(
                                        item = item,
                                        onRemove = {
                                            savedArtists.remove(item)
                                            showSnackbar("Removed ${item.name} from saved list!")
                                        },
                                        onHire = { showSnackbar("Initiated booking flow for ${item.name}!") }
                                    )
                                }
                            }
                            SavedTab.STUDIOS -> {
                                items(savedStudios) { item ->
                                    StudioRowCard(
                                        item = item,
                                        onRemove = {
                                            savedStudios.remove(item)
                                            showSnackbar("Removed ${item.name}!")
                                        },
                                        onBook = { showSnackbar("Loading booking screen for ${item.name}...") }
                                    )
                                }
                            }
                            SavedTab.MARKETPLACE -> {
                                items(savedMarketplace) { item ->
                                    MarketRowCard(
                                        item = item,
                                        onRemove = {
                                            savedMarketplace.remove(item)
                                            showSnackbar("Removed ${item.title}!")
                                        },
                                        onView = { showSnackbar("Opening detailed page for ${item.title}") }
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

// Data models for Saved Flow
data class SavedArtistItem(val name: String, val role: String, val location: String, val rating: String, val initials: String)
data class SavedStudioItem(val name: String, val location: String, val equipment: String, val rating: Double)
data class SavedMarketItem(val title: String, val price: String, val seller: String, val emoji: String, val imageUrl: String? = null)

// Row and Grid item templates for Saved Flow
@Composable
fun ArtistRowCard(item: SavedArtistItem, onRemove: () -> Unit, onHire: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.initials, fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
                Text("${item.role} • 📍 ${item.location}", fontSize = 11.sp, color = BrandSecondaryText)
                Text("★ ${item.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandAccent)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "Remove", tint = BrandAccent, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onHire,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hire", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ArtistGridCard(item: SavedArtistItem, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Close, null, tint = BrandSecondaryText, modifier = Modifier.size(14.dp))
                }
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.initials, fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandDark, maxLines = 1, textAlign = TextAlign.Center)
            Text(item.role, fontSize = 10.sp, color = BrandSecondaryText, maxLines = 1)
            Text("★ ${item.rating}", fontSize = 10.sp, color = BrandAccent, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StudioRowCard(item: SavedStudioItem, onRemove: () -> Unit, onBook: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
                    Text("📍 ${item.location} • ★ ${item.rating}", fontSize = 11.sp, color = BrandSecondaryText)
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, "Remove", tint = BrandAccent, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Featured Equipment: ${item.equipment}", fontSize = 11.sp, color = BrandSecondaryText, maxLines = 2)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onBook,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(34.dp)
            ) {
                Text("Book Studio Space Again", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StudioGridCard(item: SavedStudioItem, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("★ ${item.rating}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandAccent)
                IconButton(onClick = onRemove, modifier = Modifier.size(18.dp)) {
                    Icon(Icons.Default.Close, null, tint = BrandSecondaryText, modifier = Modifier.size(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandDark, maxLines = 1)
            Text(item.location, fontSize = 10.sp, color = BrandSecondaryText)
        }
    }
}

@Composable
fun MarketRowCard(item: SavedMarketItem, onRemove: () -> Unit, onView: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BrandBackground),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl != null) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(item.emoji, fontSize = 22.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark, maxLines = 1)
                Text("Seller: ${item.seller}", fontSize = 11.sp, color = BrandSecondaryText)
                Text(item.price, fontWeight = FontWeight.Bold, color = BrandSuccess, fontSize = 13.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, null, tint = BrandAccent, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onView,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("View", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MarketGridCard(item: SavedMarketItem, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (item.imageUrl != null) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                } else {
                    Text(item.emoji, fontSize = 18.sp)
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(18.dp)) {
                    Icon(Icons.Default.Close, null, tint = BrandSecondaryText, modifier = Modifier.size(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandDark, maxLines = 1)
            Text(item.price, color = BrandSuccess, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

// --------------------------------------------------------------------------------------
// MODULE 4: SETTINGS
// --------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsFlow(
    showSnackbar: (String) -> Unit,
    onLogout: () -> Unit = {}
) {
    var subPage by remember { mutableStateOf(SettingsSubPage.MAIN) }

    // Account fields state
    var editName by remember { mutableStateOf("Vedanshu Srivastava") }
    var editEmail by remember { mutableStateOf("vedanshu.srivastava@gmail.com") }
    var editPhone by remember { mutableStateOf("+91 98765 43210") }
    var editUsername by remember { mutableStateOf("vedanshu_music") }

    // Musical Roles state
    var userPrimaryRole by remember { mutableStateOf("Music Producer") }
    var selectedGenresSet by remember { mutableStateOf(setOf("Bollywood", "Indie", "Pop", "Electronic")) }

    // Privacy toggles state
    var profileVisible by remember { mutableStateOf(true) }
    var hideContactDetails by remember { mutableStateOf(false) }
    var hideFollowersList by remember { mutableStateOf(false) }

    // Notifications toggles state
    var pushNotify by remember { mutableStateOf(true) }
    var emailNotify by remember { mutableStateOf(true) }
    var smsNotify by remember { mutableStateOf(false) }
    var marketplaceAlerts by remember { mutableStateOf(true) }
    var bookingsAlerts by remember { mutableStateOf(true) }

    // Security state
    var twoFactorAuth by remember { mutableStateOf(false) }
    var twoFactorCode by remember { mutableStateOf("") }
    var showTwoFactorDialog by remember { mutableStateOf(false) }

    // Appearance state
    var themeSelection by remember { mutableStateOf("Dark System Default") }
    var selectedAccent by remember { mutableStateOf("MusicLinkd Pink") }

    // Logout Dialog state
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Log Out", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("Are you sure you want to log out of your MusicLinkd account? Offline profile and settings will be preserved locally in Room Database.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        showSnackbar("Logged out successfully!")
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutConfirmDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    AnimatedContent(
        targetState = subPage,
        transitionSpec = {
            if (targetState == SettingsSubPage.MAIN) {
                slideInHorizontally(initialOffsetX = { -100 }) + fadeIn() togetherWith
                slideOutHorizontally(targetOffsetX = { 100 }) + fadeOut()
            } else {
                slideInHorizontally(initialOffsetX = { 100 }) + fadeIn() togetherWith
                slideOutHorizontally(targetOffsetX = { -100 }) + fadeOut()
            }
        },
        label = "settings_navigation"
    ) { targetPage ->
        when (targetPage) {
            SettingsSubPage.MAIN -> {
                // Main Settings Category Grid & List
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Account Preferences", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 14.sp)
                    
                    // Category Items
                    val categories = listOf(
                        Triple("Personal Account", "Name, email verification, active phone details", SettingsSubPage.ACCOUNT),
                        Triple("Profile & Discovery Privacy", "Hide search status, visibility limits & blocking", SettingsSubPage.PRIVACY),
                        Triple("Interactive Notification Feeds", "Push alerts, booking SMS & email updates", SettingsSubPage.NOTIFICATIONS),
                        Triple("Authentication & Security", "Password adjustments, login history, 2-Factor setup", SettingsSubPage.SECURITY),
                        Triple("App Theme & Typography", "Dark/Light selection, brand secondary accent colors", SettingsSubPage.APPEARANCE)
                    )

                    categories.forEach { (title, subtitle, pageTarget) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { subPage = pageTarget }
                                .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(BrandPrimary.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (pageTarget) {
                                            SettingsSubPage.ACCOUNT -> Icons.Default.Person
                                            SettingsSubPage.PRIVACY -> Icons.Default.Lock
                                            SettingsSubPage.NOTIFICATIONS -> Icons.Default.Notifications
                                            SettingsSubPage.SECURITY -> Icons.Default.Security
                                            else -> Icons.Default.Palette
                                        },
                                        contentDescription = title,
                                        tint = BrandPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
                                    Text(subtitle, fontSize = 11.sp, color = BrandSecondaryText)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = BrandSecondaryText, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Button(
                        onClick = { 
                            showLogoutConfirmDialog = true 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.08f), contentColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("settings_logout_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
            SettingsSubPage.ACCOUNT -> {
                // Screen 2: Account fields & Musical Roles
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { subPage = SettingsSubPage.MAIN }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Personal Account & Musical Roles", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    Text("Contact Details", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 14.sp)

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Primary Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone / WhatsApp Contact") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Public Username Vanity URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    HorizontalDivider(color = BrandBorder, modifier = Modifier.padding(vertical = 4.dp))

                    // Musical Roles & Expertise Section
                    Text("Primary Musical Role", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 14.sp)
                    Text("Select your primary professional role on MusicLinkd", fontSize = 12.sp, color = BrandSecondaryText)

                    val availableRoles = listOf("Music Producer", "Lead Vocalist", "Session Guitarist", "Mixing Engineer", "Songwriter", "Studio Owner")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableRoles.forEach { role ->
                            val isSelected = userPrimaryRole == role
                            FilterChip(
                                selected = isSelected,
                                onClick = { userPrimaryRole = role },
                                label = { Text(role, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandPrimary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Genres & Styles", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 14.sp)
                    Text("Select genres that best match your musical production & performance style", fontSize = 12.sp, color = BrandSecondaryText)

                    val availableGenres = listOf("Bollywood", "Indie", "Pop", "Rock", "Classical", "Electronic", "Sufi", "Hip-Hop", "Lo-Fi", "Jazz", "R&B")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableGenres.forEach { genre ->
                            val isSelected = selectedGenresSet.contains(genre)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedGenresSet = if (isSelected) selectedGenresSet - genre else selectedGenresSet + genre
                                },
                                label = { Text(genre, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF6366F1),
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            showSnackbar("Account & Musical Roles updated to $userPrimaryRole!")
                            subPage = SettingsSubPage.MAIN
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_profile_roles_button")
                    ) {
                        Text("Save Profile & Musical Roles", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showSnackbar("Logged out successfully!")
                            onLogout()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("account_subpage_logout_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    TextButton(
                        onClick = { showSnackbar("Initiating full delete procedures...") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Professional Account Permanently", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            SettingsSubPage.PRIVACY -> {
                // Screen 3: Privacy
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { subPage = SettingsSubPage.MAIN }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Privacy & Limits Config", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    // Toggles
                    SupportPrivacyToggleRow(
                        title = "Index Profile Visibly",
                        description = "Let labels, creators and venue directors find your portfolio in global search recommendations.",
                        checked = profileVisible,
                        onCheckedChange = { profileVisible = it }
                    )

                    SupportPrivacyToggleRow(
                        title = "Hide Personal Contact Details",
                        description = "Requires connections to obtain explicit authorization before unlocking your WhatsApp or phone.",
                        checked = hideContactDetails,
                        onCheckedChange = { hideContactDetails = it }
                    )

                    SupportPrivacyToggleRow(
                        title = "Anonymize Fan Followers List",
                        description = "Omit publicly accessible listings of who is following you or who you are connected with.",
                        checked = hideFollowersList,
                        onCheckedChange = { hideFollowersList = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Your Indexed Data", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 13.sp)
                            Text("MusicLinkd is a sovereign, secure marketplace. Download a JSON package of your complete history anytime.", fontSize = 11.sp, color = BrandSecondaryText)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { showSnackbar("Generating JSON database package download link...") },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, BrandBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandDark),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text("Download Data JSON Package", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            SettingsSubPage.NOTIFICATIONS -> {
                // Screen 4: Notifications
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { subPage = SettingsSubPage.MAIN }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Notification Feeds", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    SupportPrivacyToggleRow("Push Alerts Feed", "Receive immediate native app notifications for chat message receipts.", pushNotify) { pushNotify = it }
                    SupportPrivacyToggleRow("Email Summary Digests", "Receive periodic highlight newsletters of new booking opportunities.", emailNotify) { emailNotify = it }
                    SupportPrivacyToggleRow("SMS Booking Reminders", "Direct SMS reminders for scheduled studio recording session times.", smsNotify) { smsNotify = it }
                    SupportPrivacyToggleRow("Marketplace Updates", "Alerts when a buyer inquires about gear listed in the marketplace.", marketplaceAlerts) { marketplaceAlerts = it }
                    SupportPrivacyToggleRow("Studio & Venue Bookings", "Receive rapid confirmation updates for live space applications.", bookingsAlerts) { bookingsAlerts = it }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            showSnackbar("Notification preferences saved successfully!")
                            subPage = SettingsSubPage.MAIN
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_notifications_button")
                    ) {
                        Text("Save Notification Preferences", fontWeight = FontWeight.Bold)
                    }
                }
            }
            SettingsSubPage.SECURITY -> {
                // Screen 5: Security
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { subPage = SettingsSubPage.MAIN }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Account Security", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Reset Access Password", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showSnackbar("Sent instructions to password reset link email!") },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text("Email Reset Password Link", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    SupportPrivacyToggleRow(
                        title = "Two-Factor Authentication",
                        description = "Strengthen security by requiring a random SMS verification code upon each session login.",
                        checked = twoFactorAuth,
                        onCheckedChange = {
                            twoFactorAuth = it
                            if (it) {
                                showTwoFactorDialog = true
                            } else {
                                showSnackbar("Disabled 2-Factor Authentication.")
                            }
                        }
                    )

                    // Mock Active Devices
                    Text("Active Connected Sessions", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 13.sp)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            DeviceSessionRow("OnePlus 12 • Active Now", "Bandra West, Mumbai • App Client Version 2.26")
                            HorizontalDivider(color = BrandBorder)
                            DeviceSessionRow("MacBook Pro • 2 Hours Ago", "Safari Browser • Session IP: 192.168.1.150")
                        }
                    }

                    if (showTwoFactorDialog) {
                        AlertDialog(
                            onDismissRequest = { showTwoFactorDialog = false; twoFactorAuth = false },
                            title = { Text("Configure Two-Factor Auth") },
                            text = {
                                Column {
                                    Text("Enter the 6-digit verification code sent to your active verified phone number.")
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedTextField(
                                        value = twoFactorCode,
                                        onValueChange = { if (it.length <= 6) twoFactorCode = it },
                                        placeholder = { Text("e.g. 520418") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    if (twoFactorCode.length == 6) {
                                        showTwoFactorDialog = false
                                        showSnackbar("Two-Factor Authentication fully enabled!")
                                    } else {
                                        showSnackbar("Invalid verification code length.")
                                    }
                                }) {
                                    Text("Verify", color = BrandPrimary, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showTwoFactorDialog = false; twoFactorAuth = false }) {
                                    Text("Cancel", color = BrandSecondaryText)
                                }
                            }
                        )
                    }
                }
            }
            SettingsSubPage.APPEARANCE -> {
                // Screen 6: Appearance
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { subPage = SettingsSubPage.MAIN }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Appearance & Styling", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    Text("Active Theme Selection", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 13.sp)
                    val themes = listOf("Light Editorial Mode", "Dark System Default", "Premium Slate Cosmic Theme")
                    themes.forEach { theme ->
                        val selected = themeSelection == theme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { themeSelection = theme; showSnackbar("Switched visual theme to $theme!") }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selected, onClick = { themeSelection = theme; showSnackbar("Switched visual theme to $theme!") })
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(theme, fontSize = 14.sp, color = BrandDark, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Primary Accent Palettes", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 13.sp)
                    val accents = listOf("MusicLinkd Pink", "Classic Indigo Blue", "Vibrant Emerald Green")
                    accents.forEach { acc ->
                        val selected = selectedAccent == acc
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAccent = acc; showSnackbar("Configured accent palette to $acc") }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (acc) {
                                            "MusicLinkd Pink" -> BrandAccent
                                            "Classic Indigo Blue" -> BrandPrimary
                                            else -> BrandSuccess
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(acc, fontSize = 14.sp, color = BrandDark, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            Spacer(modifier = Modifier.weight(1f))
                            if (selected) {
                                Icon(Icons.Default.Check, null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupportPrivacyToggleRow(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
            Text(description, fontSize = 11.sp, color = BrandSecondaryText, lineHeight = 15.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandPrimary,
                uncheckedThumbColor = BrandSecondaryText,
                uncheckedTrackColor = BrandBorder
            )
        )
    }
}

@Composable
fun DeviceSessionRow(device: String, detail: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BrandPrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PhoneAndroid, null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(device, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Text(detail, fontSize = 11.sp, color = BrandSecondaryText)
        }
    }
}

// --------------------------------------------------------------------------------------
// MODULE 5: HELP & SUPPORT
// --------------------------------------------------------------------------------------
@Composable
fun HelpAndSupportFlow(
    showSnackbar: (String) -> Unit
) {
    var helpPage by remember { mutableStateOf(HelpSubPage.CENTER) }
    var searchQuery by remember { mutableStateOf("") }
    
    // FAQ accordion states
    val expandedFaqIndices = remember { mutableStateListOf<Int>() }

    // Support ticket fields
    var ticketSubject by remember { mutableStateOf("") }
    var ticketCategory by remember { mutableStateOf("Billing") }
    var ticketDesc by remember { mutableStateOf("") }
    var ticketFileUploaded by remember { mutableStateOf(false) }

    // Report user fields
    var reportUserReason by remember { mutableStateOf("Spam or Advertising") }
    var reportUserDesc by remember { mutableStateOf("") }

    // Report listing fields
    var reportListReason by remember { mutableStateOf("Wrong Information") }
    var reportListDesc by remember { mutableStateOf("") }

    // Feedback rating selection
    var starRatingSelected by remember { mutableStateOf(0) }
    var feedbackComments by remember { mutableStateOf("") }

    val faqList = listOf(
        FaqItem("How do I request a Dolby Atmos certified mixing session?", "You can visit any business profile verified with the Dolby Atmos badge (like Octave Sound Labs) and apply via their custom Dolby service slot. A certified engineer will reach out immediately."),
        FaqItem("How are booking security deposits handled?", "MusicLinkd Assurance ensures that payments remain completely locked in security reserves until the recording engineer commits final raw stem masters to your project timeline."),
        FaqItem("What is the difference between Pro and Free tiers?", "MusicLinkd Pro gives you 100% payout structures, 11 parallel service listings, unlimited foley storage uploads, and advanced visual audience dashboard statistics."),
        FaqItem("Can I sync my personal Google Calendar with Studio spaces?", "Yes. Navigate to Settings > Professional Profile and complete Google Workspace integration to automatically reflect gig blocks and recording times.")
    )

    val filteredFaq = faqList.filter {
        it.question.contains(searchQuery, ignoreCase = true) || it.answer.contains(searchQuery, ignoreCase = true)
    }

    AnimatedContent(
        targetState = helpPage,
        transitionSpec = {
            if (targetState == HelpSubPage.CENTER) {
                slideInHorizontally(initialOffsetX = { -100 }) + fadeIn() togetherWith
                slideOutHorizontally(targetOffsetX = { 100 }) + fadeOut()
            } else {
                slideInHorizontally(initialOffsetX = { 100 }) + fadeIn() togetherWith
                slideOutHorizontally(targetOffsetX = { -100 }) + fadeOut()
            }
        },
        label = "help_navigation"
    ) { targetPage ->
        when (targetPage) {
            HelpSubPage.CENTER -> {
                // Screen 1: Help Center Home
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Help & Support Center", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BrandDark)
                    Text("Find answers to frequent queries or lodge direct support tickets with our Bandra West admin team.", fontSize = 11.sp, color = BrandSecondaryText)

                    // Search help topic bar
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, null, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search help topics...", fontSize = 13.sp, color = BrandSecondaryText) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (searchQuery.isNotEmpty()) {
                        Text("Search Results for help:", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 12.sp)
                        filteredFaq.forEach { faq ->
                            Card(
                                modifier = Modifier.fillMaxWidth().border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(faq.question, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandPrimary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(faq.answer, fontSize = 11.sp, color = BrandSecondaryText, lineHeight = 15.sp)
                                }
                            }
                        }
                        if (filteredFaq.isEmpty()) {
                            Text("No match found. Use contact support below.", fontSize = 12.sp, color = BrandSecondaryText)
                        }
                    }

                    // Popular Topics Quick Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { helpPage = HelpSubPage.FAQ },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Browse FAQ Accordion", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { helpPage = HelpSubPage.CONTACT },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, BrandBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Contact Support Ticket", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Moderation reports, reviews
                    Text("Report & Submit Incidents", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { helpPage = HelpSubPage.REPORT_USER },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, BrandBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Report Scam User", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { helpPage = HelpSubPage.REPORT_LISTING },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, BrandBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Report Fake Listing", fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { helpPage = HelpSubPage.FEEDBACK },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, BrandBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandAccent),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("Provide Platform Feedback", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            HelpSubPage.FAQ -> {
                // Screen 2: FAQ Accordions
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { helpPage = HelpSubPage.CENTER }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Accordion FAQ Cards", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    faqList.forEachIndexed { index, item ->
                        val isExpanded = expandedFaqIndices.contains(index)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BrandBorder, RoundedCornerShape(16.dp))
                                .clickable {
                                    if (isExpanded) {
                                        expandedFaqIndices.remove(index)
                                    } else {
                                        expandedFaqIndices.add(index)
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.question, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark, modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = BrandPrimary
                                    )
                                }
                                
                                AnimatedVisibility(visible = isExpanded) {
                                    Column {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        HorizontalDivider(color = BrandBorder)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(item.answer, fontSize = 12.sp, color = BrandSecondaryText, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            HelpSubPage.CONTACT -> {
                // Screen 3: Contact Support Ticket
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { helpPage = HelpSubPage.CENTER }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Contact Support Ticket", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    OutlinedTextField(
                        value = ticketSubject,
                        onValueChange = { ticketSubject = it },
                        label = { Text("Ticket Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Simple select category
                    Text("Category", fontWeight = FontWeight.Bold, color = BrandDark, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Billing", "App Bug", "Feature Suggestion").forEach { cat ->
                            val isSelected = ticketCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { ticketCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = ticketDesc,
                        onValueChange = { ticketDesc = it },
                        label = { Text("Details & Description") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )

                    // Upload Attachment
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                ticketFileUploaded = true
                                showSnackbar("Screenshot successfully attached!")
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandCardBg)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(if (ticketFileUploaded) "📎 screenshot_evidence.png" else "📁 Click to upload screenshot/log attachments", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                        }
                    }

                    Button(
                        onClick = {
                            if (ticketSubject.isNotEmpty() && ticketDesc.isNotEmpty()) {
                                showSnackbar("Ticket submitted successfully! ID: #ML-${(1000..9999).random()}")
                                helpPage = HelpSubPage.CENTER
                            } else {
                                showSnackbar("Please complete all fields.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Submit Support Ticket", fontWeight = FontWeight.Bold)
                    }
                }
            }
            HelpSubPage.REPORT_USER -> {
                // Screen 4: Report User
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { helpPage = HelpSubPage.CENTER }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Report Bad Actor / Scam User", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    Text("Reason for report", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    val reportReasons = listOf("Spam or Advertising", "Fraud / Fake Portfolio Tracks", "Harassment in Chat Messages", "Other Violations")
                    reportReasons.forEach { reason ->
                        val selected = reportUserReason == reason
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportUserReason = reason }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selected, onClick = { reportUserReason = reason })
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(reason, fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(
                        value = reportUserDesc,
                        onValueChange = { reportUserDesc = it },
                        label = { Text("Elaborate on details") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            showSnackbar("User report filed with safety admin board.")
                            helpPage = HelpSubPage.CENTER
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("File Security Incident Report", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            HelpSubPage.REPORT_LISTING -> {
                // Screen 5: Report Listing
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { helpPage = HelpSubPage.CENTER }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Report Fake Listing / Copyright", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    Text("Select Violation Type", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    val listingReasons = listOf("Wrong Information / Fake price", "Copyright Violation", "Sold Already / Expired posting", "Scam seller profile")
                    listingReasons.forEach { reason ->
                        val selected = reportListReason == reason
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportListReason = reason }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selected, onClick = { reportListReason = reason })
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(reason, fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(
                        value = reportListDesc,
                        onValueChange = { reportListDesc = it },
                        label = { Text("Details & Evidence link") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            showSnackbar("Listing reported. Safety moderators will review within 24 hours.")
                            helpPage = HelpSubPage.CENTER
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("File Listing Infringement Report", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            HelpSubPage.FEEDBACK -> {
                // Screen 6: Feedback & Ratings
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { helpPage = HelpSubPage.CENTER }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                        Text("Provide Platform Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandDark)
                    }

                    Text("Audition Experience Rating", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    
                    // Star rating row
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(5) { starIdx ->
                            val active = starIdx < starRatingSelected
                            IconButton(onClick = { starRatingSelected = starIdx + 1 }) {
                                Icon(
                                    imageVector = if (active) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Star ${starIdx + 1}",
                                    tint = if (active) BrandAccent else BrandSecondaryText,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = feedbackComments,
                        onValueChange = { feedbackComments = it },
                        label = { Text("Comments / Ideas to improve?") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (starRatingSelected > 0) {
                                showSnackbar("Thank you for your valuable feedback! 🌟")
                                helpPage = HelpSubPage.CENTER
                            } else {
                                showSnackbar("Please tap star rating before submitting.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Submit Platform Review", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class FaqItem(val question: String, val answer: String)
