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
fun AdvancedFiltersBottomSheet(
    state: DiscoverUiState,
    onDismiss: () -> Unit,
    onApply: (String, String, String, String, String, String, Boolean, String, Boolean, Boolean, String) -> Unit,
    onReset: () -> Unit
) {
    var role by remember { mutableStateOf(state.filterRole) }
    var language by remember { mutableStateOf(state.filterLanguage) }
    var genre by remember { mutableStateOf(state.filterGenre) }
    var city by remember { mutableStateOf(state.filterCity) }
    var area by remember { mutableStateOf(state.filterArea) }
    var availability by remember { mutableStateOf(state.filterAvailability) }
    var verifiedOnly by remember { mutableStateOf(state.filterVerification) }
    var experience by remember { mutableStateOf(state.filterExperienceLevel) }
    var remoteAvailable by remember { mutableStateOf(state.filterRemoteAvailable) }
    var sessionAvailable by remember { mutableStateOf(state.filterSessionAvailable) }
    var sortBy by remember { mutableStateOf(state.filterSortBy) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Refine Search", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandText)
                    Row {
                        TextButton(onClick = {
                            onReset()
                            role = ""; language = ""; genre = ""; city = ""; area = ""; availability = ""
                            verifiedOnly = false; experience = ""; remoteAvailable = false; sessionAvailable = false
                            sortBy = "Most Relevant"
                        }) {
                            Text("Reset All", color = BrandSecondary)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "Close")
                        }
                    }
                }

                HorizontalDivider()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterSectionHeader("Sort Results By")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Most Relevant", "Most Followed", "Highest Rated").forEach { match ->
                            val isSel = sortBy == match
                            FilterChipCustom(
                                label = match,
                                isSelected = isSel,
                                onClick = { sortBy = match }
                            )
                        }
                    }

                    FilterSectionHeader("Industry Professional Role")
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        placeholder = { Text("e.g. Session Bassist") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    FilterSectionHeader("Location Scope")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            placeholder = { Text("City, e.g. Mumbai") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = area,
                            onValueChange = { area = it },
                            placeholder = { Text("Area, e.g. Juhu") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    FilterSectionHeader("Language Spoken")
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Hindi", "English", "Punjabi", "Tamil", "Bengali").forEach { lang ->
                            val isSel = language.equals(lang, ignoreCase = true)
                            FilterChipCustom(
                                label = lang,
                                isSelected = isSel,
                                onClick = { language = if (isSel) "" else lang }
                            )
                        }
                    }

                    FilterSectionHeader("Genre Focus")
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Bollywood", "Indie", "Classical", "Folk", "Rock", "Pop").forEach { g ->
                            val isSel = genre.equals(g, ignoreCase = true)
                            FilterChipCustom(
                                label = g,
                                isSelected = isSel,
                                onClick = { genre = if (isSel) "" else g }
                            )
                        }
                    }

                    FilterSectionHeader("Experience Seniority")
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Rising Star (1-2 yrs)", "Established (3-5 yrs)", "Pro (5+ years)", "Legend (10+ yrs)").forEach { xp ->
                            val isSel = experience == xp
                            FilterChipCustom(
                                label = xp,
                                isSelected = isSel,
                                onClick = { experience = if (isSel) "" else xp }
                            )
                        }
                    }

                    FilterSectionHeader("Preferences")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = verifiedOnly, onCheckedChange = { verifiedOnly = it })
                            Text("Verified Professionals Only", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = remoteAvailable, onCheckedChange = { remoteAvailable = it })
                            Text("Remote Projects Allowed", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = sessionAvailable, onCheckedChange = { sessionAvailable = it })
                            Text("Available for Session Work", fontSize = 13.sp)
                        }
                    }
                }

                HorizontalDivider()

                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            onApply(role, language, genre, city, area, availability, verifiedOnly, experience, remoteAvailable, sessionAvailable, sortBy)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("apply_filters_cta"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Selected Filters", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 5: SPECIALIZED SINGER DIRECTORY
// ==========================================
