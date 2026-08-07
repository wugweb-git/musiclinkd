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
fun RecommendedCollaboratorsSection(
    initialUserRole: String = "Producer",
    initialUserGenres: List<String> = listOf("Bollywood", "Indie", "Pop"),
    savedProfileIds: Set<String> = emptySet(),
    onProfileClick: (DiscoverProfile) -> Unit,
    onHireClick: (DiscoverProfile) -> Unit,
    onToggleSave: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf(initialUserRole) }
    var selectedGenres by remember { mutableStateOf(initialUserGenres.toSet()) }

    val availableRoles = listOf("Producer", "Singer", "Guitarist", "Sound Engineer", "Composer", "Drummer")
    val availableGenres = listOf("Bollywood", "Indie", "Pop", "Rock", "Classical", "Electronic", "Sufi", "Hip-Hop")

    val matches = remember(selectedRole, selectedGenres) {
        CollaboratorRecommendationEngine.calculateMatches(
            userRole = selectedRole,
            userGenres = selectedGenres.toList(),
            candidateProfiles = sampleDiscoverProfiles
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Suggested Collaborators", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = BrandText)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.r1))
                            .background(BrandPrimary.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Smart Match", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = BrandPrimary)
                    }
                }
                Text("Matched by shared genres & complementary musical roles", fontSize = 11.sp, color = BrandMuted)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("My Role:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText, modifier = Modifier.width(62.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(availableRoles) { role ->
                        val isSel = selectedRole.equals(role, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimens.r2))
                                .background(if (isSel) BrandPrimary else Color(0xFFF3F4F6))
                                .clickable { selectedRole = role }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            val emoji = when {
                                role.contains("sing", true) -> "🎤 "
                                role.contains("produc", true) -> "🎛️ "
                                role.contains("guitar", true) -> "🎸 "
                                role.contains("engin", true) -> "🎧 "
                                role.contains("compos", true) -> "🎼 "
                                role.contains("drum", true) -> "🥁 "
                                else -> "🎵 "
                            }
                            Text(emoji + role, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else BrandText)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("My Genres:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText, modifier = Modifier.width(62.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(availableGenres) { genre ->
                        val isSel = selectedGenres.contains(genre)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimens.r2))
                                .background(if (isSel) BrandSecondary else Color(0xFFF3F4F6))
                                .clickable {
                                    selectedGenres = if (isSel) selectedGenres - genre else selectedGenres + genre
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = (if (isSel) "✓ " else "") + genre,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else BrandText
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.sp2))

        val context = androidx.compose.ui.platform.LocalContext.current
        var dismissedProfileIds by remember { mutableStateOf(setOf<String>()) }
        val visibleMatches = remember(matches, dismissedProfileIds) {
            matches.filter { !dismissedProfileIds.contains(it.profile.id) }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)
        ) {
            items(visibleMatches.take(8), key = { it.profile.id }) { matchResult ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        if (dismissValue == SwipeToDismissBoxValue.StartToEnd || dismissValue == SwipeToDismissBoxValue.EndToStart) {
                            dismissedProfileIds = dismissedProfileIds + matchResult.profile.id
                            android.widget.Toast.makeText(context, "${matchResult.profile.name} dismissed", android.widget.Toast.LENGTH_SHORT).show()
                            true
                        } else false
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(Dimens.r3))
                                .background(Color(0xFFEF4444))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Delete, contentDescription = "Dismiss profile", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dismissed", color = Color.White, style = Typography.bodyLarge)
                            }
                        }
                    }
                ) {
                    RecommendedCollaboratorCard(
                        matchResult = matchResult,
                        onProfileClick = onProfileClick,
                        onHireClick = onHireClick,
                        onToggleSave = onToggleSave,
                        isSaved = savedProfileIds.contains(matchResult.profile.id)
                    )
                }
            }
        }
    }
}

// ==========================================
// SCREEN 1: DISCOVER HOME SCREEN
// ==========================================
