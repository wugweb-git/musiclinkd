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
fun SingerDirectoryScreen(
    state: DiscoverUiState,
    profiles: List<DiscoverProfile>,
    onBack: () -> Unit,
    onToggleRange: (String) -> Unit,
    onToggleGender: (String) -> Unit,
    onToggleGenre: (String) -> Unit,
    onToggleView: (Boolean) -> Unit,
    onProfileClick: (DiscoverProfile) -> Unit,
    onToggleSave: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
                Text("Singers Directory", style = Typography.bodyLarge, color = BrandText)
            }
            
            IconButton(onClick = { onToggleView(!state.isSingerGrid) }) {
                Icon(
                    imageVector = if (state.isSingerGrid) Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = "Toggle Grid/List",
                    tint = BrandPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.sp1)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vocal Range:", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Soprano", "Mezzo-Soprano", "Alto", "Tenor", "Baritone", "Bass")) { s ->
                        val isSel = state.vocalRangeFilter == s
                        FilterChipCustom(s, isSel, { onToggleRange(s) })
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Voice Pitch:", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Male", "Female")) { g ->
                        val isSel = state.genderFilter == g
                        FilterChipCustom(g, isSel, { onToggleGender(g) })
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Style / Type:", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Bollywood", "Classical", "Folk", "Indie", "Rap")) { gr ->
                        val isSel = state.selectedSingerGenre == gr
                        FilterChipCustom(gr, isSel, { onToggleGenre(gr) })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.sp2))

        val filtered = profiles.filter { it.category == "Singers" }
        if (state.isSingerGrid) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 44.dp)
            ) {
                items(filtered) { singer ->
                    Box(modifier = Modifier.padding(6.dp)) {
                        HomeFeatureCard(profile = singer, onClick = { onProfileClick(singer) })
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { singer ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        DiscoverySearchResultCard(
                            profile = singer,
                            isSaved = state.savedProfileIds.contains(singer.id),
                            onClick = { onProfileClick(singer) },
                            onToggleSave = { onToggleSave(singer.id) },
                            onHire = { onProfileClick(singer) }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 6: PROFILE PREVIEW SHEET OR CARD
// ==========================================
