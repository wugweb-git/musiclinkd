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
fun SavedProfilesScreen(
    state: DiscoverUiState,
    allProfiles: List<DiscoverProfile>,
    onBack: () -> Unit,
    onTabChange: (String) -> Unit,
    onProfileClick: (DiscoverProfile) -> Unit,
    onSaveNote: (String, String) -> Unit,
    onAddTag: (String, String) -> Unit,
    onRemoveTag: (String, String) -> Unit,
    onRemove: (String) -> Unit
) {
    val savedLists = allProfiles.filter { state.savedProfileIds.contains(it.id) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            Text("Saved Profiles & Bookmarks", style = Typography.bodyLarge, color = BrandText)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)
        ) {
            listOf("Talent", "Bands", "Studios").forEach { tab ->
                val isSel = state.activeSavedTab == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabChange(tab) }
                        .padding(bottom = 6.dp)
                ) {
                    Text(
                        text = tab,
                        fontSize = 13.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) BrandPrimary else BrandMuted
                    )
                    if (isSel) {
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(30.dp)
                                .background(BrandPrimary)
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        val finalRenderList = savedLists.filter {
            when (state.activeSavedTab) {
                "Talent" -> it.type == ProfileType.TALENT
                "Bands" -> it.type == ProfileType.BAND
                "Studios" -> it.type == ProfileType.BUSINESS || it.type == ProfileType.VENUE_EVENT
                else -> true
            }
        }

        if (finalRenderList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = "No Saved Entries Yet",
                    description = "Tap on card bookmarks on the search page or discover screens to save references and build your personal collaborator roster.",
                    emoji = "📁",
                    actionLabel = "Back to Discover",
                    onActionClick = onBack
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
            ) {
                items(finalRenderList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(Dimens.r2),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(Dimens.r1))
                                        .background(Color(item.avatarColorHex).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = item.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                        contentDescription = item.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, style = Typography.bodyLarge, color = BrandText)
                                    Text(item.subRole, fontSize = 11.sp, color = BrandSecondary)
                                }
                                IconButton(onClick = { onRemove(item.id) }) {
                                    Icon(Icons.Default.Delete, "Remove", tint = Color.Red)
                                }
                            }

                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            val customNote = state.savedNotes[item.id] ?: ""
                            var noteInput by remember { mutableStateOf(customNote) }
                            
                            OutlinedTextField(
                                value = noteInput,
                                onValueChange = {
                                    noteInput = it
                                    onSaveNote(item.id, it)
                                },
                                placeholder = { Text("Add custom note (e.g. Call for Friday demo)", fontSize = 11.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp),
                                textStyle = TextStyle(fontSize = 11.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandSecondary,
                                    focusedContainerColor = Color(0xFFF9FAFB),
                                    unfocusedContainerColor = Color(0xFFF9FAFB)
                                ),
                                shape = RoundedCornerShape(Dimens.r1)
                            )

                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            val tagsList = state.savedTags[item.id] ?: emptyList()
                            var tagInputText by remember { mutableStateOf("") }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = tagInputText,
                                    onValueChange = { tagInputText = it },
                                    placeholder = { Text("Add tag", fontSize = 10.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    textStyle = TextStyle(fontSize = 10.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandSecondary,
                                        focusedContainerColor = Color(0xFFEEEEEE),
                                        unfocusedContainerColor = Color(0xFFEEEEEE)
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        if (tagInputText.isNotEmpty()) {
                                            onAddTag(item.id, tagInputText)
                                            tagInputText = ""
                                        }
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, "Add Tag", tint = BrandPrimary)
                                }
                            }

                            if (tagsList.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                DiscoverFlowRow(horizontalGap = 4.dp, verticalGap = 4.dp) {
                                    tagsList.forEach { tag ->
                                        Row(
                                            modifier = Modifier
                                                .background(BrandPrimary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                                .clickable { onRemoveTag(item.id, tag) }
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(tag, fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Icon(Icons.Default.Close, "Remove tag", tint = BrandPrimary, modifier = Modifier.size(10.dp))
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
}

// ==========================================
// SCREEN 11: CONTACT / HIRE FLOW (MODAL OVERLAY)
// ==========================================
