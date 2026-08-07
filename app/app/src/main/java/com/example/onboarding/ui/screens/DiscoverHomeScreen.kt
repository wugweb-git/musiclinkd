@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.onboarding.ui.screens

import com.example.onboarding.ai.GeminiRecommendationEngine
import com.example.onboarding.ai.TrendingMusicProject
import com.example.onboarding.util.DynamicLinkManager
import com.example.onboarding.util.ErrorReportingService
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onboarding.models.*
import com.example.onboarding.viewmodel.*
import com.example.onboarding.ui.components.*

// Cohesive Branding Colors aligned to high-fashion neutral and active creator pink

@Composable
fun DiscoverHomeScreen(
    state: DiscoverUiState,
    avatarImageUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
    userRole: String = "Producer",
    userGenres: List<String> = listOf("Bollywood", "Indie", "Pop"),
    onNavigateTo: (DiscoverScreen) -> Unit,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onProfileClick: (DiscoverProfile) -> Unit,
    onHireClick: (DiscoverProfile) -> Unit = {},
    onToggleSave: (String) -> Unit = {},
    onBackToDashboard: () -> Unit
) {
    var searchInput by remember { mutableStateOf("") }
    val featuredTalent = sampleDiscoverProfiles.filter { it.isVerified && it.type == ProfileType.TALENT }
    val trendingBands = sampleDiscoverProfiles.filter { it.type == ProfileType.BAND }
    val nearbyStudios = sampleDiscoverProfiles.filter { it.type == ProfileType.BUSINESS }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToDashboard,
                    modifier = Modifier.testTag("back_to_onboarding_dash")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to dashboard",
                        tint = BrandPrimary
                    )
                }
                Column(modifier = Modifier.padding(start = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_logo),
                            contentDescription = "MusicLinkd Logo",
                            tint = BrandPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Discover",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandText
                        )
                    }
                    Text(
                        text = "Connect with India's Music Grid",
                        fontSize = 12.sp,
                        color = BrandMuted
                    )
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onNavigateTo(DiscoverScreen.RECOMMENDED_MATCHES) }) {
                    Icon(Icons.Default.Whatshot, "Recommendations", tint = Color(0xFFFF5A5F))
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { onNavigateTo(DiscoverScreen.SAVED_PROFILES) }) {
                    BadgedBox(badge = {
                        if (state.savedProfileIds.isNotEmpty()) {
                            Badge(containerColor = BrandPrimary) {
                                Text(state.savedProfileIds.size.toString(), color = Color.White)
                            }
                        }
                    }) {
                        Icon(Icons.Default.Bookmark, "Saved Profiles", tint = BrandPrimary)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                
                // User Avatar Circle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = avatarImageUrl,
                        contentDescription = "User avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(BrandSuccess, CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }

        // Search text field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchInput,
                onValueChange = {
                    searchInput = it
                    onSearchQueryChange(it)
                },
                placeholder = { Text("Search talent, studios, bands...", color = BrandMuted) },
                leadingIcon = { Icon(Icons.Default.Search, "Search", tint = BrandPrimary) },
                trailingIcon = {
                    if (searchInput.isNotEmpty()) {
                        IconButton(onClick = {
                            searchInput = ""
                            onSearchQueryChange("")
                        }) {
                            Icon(Icons.Default.Close, "Clear")
                        }
                    } else {
                        IconButton(onClick = { onNavigateTo(DiscoverScreen.SEARCH_RESULTS) }) {
                            Icon(Icons.Default.ArrowForward, "Search Results", tint = BrandPrimary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("discover_search_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = BrandBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
        }

        com.example.onboarding.ui.components.SearchAutocompleteOverlay(
            query = searchInput,
            onSuggestionSelect = { selected ->
                searchInput = selected
                onSearchQueryChange(selected)
            }
        )

        // Swipe & Match Promo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onNavigateTo(DiscoverScreen.RECOMMENDED_MATCHES) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFEA4C89), Color(0xFFFF8EB6))
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Swipe n' Match",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Discover collaborators game-show style!",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = { onNavigateTo(DiscoverScreen.RECOMMENDED_MATCHES) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Hot-Match", color = BrandText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Trending Music Projects (Dynamic Feed based on user genre interests)
        TrendingMusicProjectsSection(
            userGenres = userGenres
        )

        // Recommended Collaborators Section
        RecommendedCollaboratorsSection(
            initialUserRole = userRole,
            initialUserGenres = userGenres,
            savedProfileIds = state.savedProfileIds,
            onProfileClick = onProfileClick,
            onHireClick = onHireClick,
            onToggleSave = onToggleSave
        )

        // Quick Categories Bar Heading
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Quick Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandText)
            Text(
                text = "See All (${defaultCategories.size})",
                color = BrandPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onNavigateTo(DiscoverScreen.CATEGORY_DIRECTORY) }
            )
        }

        // Horizontal Grid of Quick Categories
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(defaultCategories) { cat ->
                Card(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .clickable { onSelectCategory(cat.name) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(cat.icon, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(cat.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BrandText)
                    }
                }
            }
        }

        HomeSectionHeader("Featured Professionals")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(featuredTalent) { talent ->
                ArtistCard(
                    profile = talent,
                    onProfileClick = { onProfileClick(talent) },
                    onHireClick = { onProfileClick(talent) },
                    modifier = Modifier.width(260.dp)
                )
            }
        }

        HomeSectionHeader("Trending Independent Bands")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trendingBands) { band ->
                ArtistCard(
                    profile = band,
                    onProfileClick = { onProfileClick(band) },
                    onHireClick = { onProfileClick(band) },
                    modifier = Modifier.width(260.dp)
                )
            }
        }

        HomeSectionHeader("Nearby Studios & Venues")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            nearbyStudios.take(3).forEach { studio ->
                StudioVenueListItem(studio = studio, onClick = { onProfileClick(studio) })
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}
