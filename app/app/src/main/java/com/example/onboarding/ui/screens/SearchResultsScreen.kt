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
fun SearchResultsScreen(
    state: DiscoverUiState,
    profiles: List<DiscoverProfile>,
    onBack: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenFilters: () -> Unit,
    onRoleFilterToggle: (String) -> Unit = {},
    onGenreFilterToggle: (String) -> Unit = {},
    onCategoryFilterToggle: (String?) -> Unit = {},
    onExperienceFilterToggle: (String) -> Unit = {},
    onVerificationToggle: () -> Unit = {},
    onRemoteToggle: () -> Unit = {},
    onSessionToggle: () -> Unit = {},
    onClearAllFilters: () -> Unit = {},
    onProfileClick: (DiscoverProfile) -> Unit,
    onToggleSave: (String) -> Unit,
    onHireClick: (DiscoverProfile) -> Unit
) {
    var searchInput by remember { mutableStateOf(state.searchQuery) }

    LaunchedEffect(state.searchQuery) {
        if (state.searchQuery != searchInput) {
            searchInput = state.searchQuery
        }
    }

    val activeFilterCount = listOf(
        state.selectedCategory != null,
        state.filterRole.isNotEmpty(),
        state.filterGenre.isNotEmpty(),
        state.filterExperienceLevel.isNotEmpty(),
        state.filterVerification,
        state.filterRemoteAvailable,
        state.filterSessionAvailable,
        state.filterCity.isNotEmpty()
    ).count { it }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = {
                        searchInput = it
                        onSearchQueryChange(it)
                    },
                    placeholder = { Text("Search professional network...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, "Search", modifier = Modifier.size(16.dp)) },
                    trailingIcon = {
                        if (searchInput.isNotEmpty()) {
                            IconButton(onClick = {
                                searchInput = ""
                                onSearchQueryChange("")
                            }) {
                                Icon(Icons.Default.Close, "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .testTag("network_search_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
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

            // Interactive Filter Chips Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Role Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Role:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText, modifier = Modifier.width(55.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        val roles = listOf("Singer", "Guitarist", "Drummer", "Pianist", "Producer", "Sound Engineer", "Composer", "DJ", "Studio")
                        items(roles) { r ->
                            val isSel = state.filterRole.equals(r, ignoreCase = true)
                            FilterChipCustom(r, isSel, { onRoleFilterToggle(r) })
                        }
                    }
                }

                // Genre Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Genre:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText, modifier = Modifier.width(55.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        val genres = listOf("Bollywood", "Classical", "Indie", "Pop", "Rock", "Electronic", "Folk", "Sufi", "Hip-Hop", "Jazz")
                        items(genres) { g ->
                            val isSel = state.filterGenre.equals(g, ignoreCase = true)
                            FilterChipCustom(g, isSel, { onGenreFilterToggle(g) })
                        }
                    }
                }

                // Quick Toggles and Experience Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChipCustom("✓ Verified", state.filterVerification, { onVerificationToggle() })
                    }
                    item {
                        FilterChipCustom("🌐 Remote", state.filterRemoteAvailable, { onRemoteToggle() })
                    }
                    item {
                        FilterChipCustom("🎹 Session", state.filterSessionAvailable, { onSessionToggle() })
                    }

                    val xpLevels = listOf("Rising Star (1-2 yrs)", "Established (3-5 yrs)", "Pro (5+ years)")
                    items(xpLevels) { xp ->
                        val isSel = state.filterExperienceLevel == xp
                        FilterChipCustom("⭐ " + xp.take(12), isSel, { onExperienceFilterToggle(xp) })
                    }

                    if (activeFilterCount > 0 || searchInput.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .clickable {
                                        searchInput = ""
                                        onClearAllFilters()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Close, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reset ($activeFilterCount)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${profiles.size} Professional Profiles Matched",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandText
                )
                
                state.selectedCategory?.let {
                    Box(
                        modifier = Modifier
                            .background(BrandPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(it, fontSize = 10.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (profiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        title = "No Profiles Matched",
                        description = "No professional network profiles match your current search filters. Try tapping filter chips to broaden your scope.",
                        emoji = "🧐",
                        actionLabel = "Clear All Filters",
                        onActionClick = {
                            searchInput = ""
                            onClearAllFilters()
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(profiles) { profile ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            DiscoverySearchResultCard(
                                profile = profile,
                                isSaved = state.savedProfileIds.contains(profile.id),
                                onClick = { onProfileClick(profile) },
                                onToggleSave = { onToggleSave(profile.id) },
                                onHire = { onHireClick(profile) }
                            )
                        }
                    }
                }
            }
        }

        // Floating filter button
        ExtendedFloatingActionButton(
            onClick = onOpenFilters,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .testTag("floating_filter_button"),
            containerColor = BrandPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.FilterList, "Filter")
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (activeFilterCount > 0) "Refine Search ($activeFilterCount)" else "Refine Search", fontWeight = FontWeight.Bold)
        }
    }
}
