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
fun RecommendedCollaboratorCard(
    matchResult: CollaboratorMatchResult,
    onProfileClick: (DiscoverProfile) -> Unit,
    onHireClick: (DiscoverProfile) -> Unit,
    onToggleSave: (String) -> Unit,
    isSaved: Boolean,
    modifier: Modifier = Modifier
) {
    val profile = matchResult.profile
    Card(
        modifier = modifier
            .width(280.dp)
            .clickable { onProfileClick(profile) }
            .testTag("recommended_collaborator_card_${profile.id}"),
        shape = RoundedCornerShape(Dimens.r3),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BrandBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Match score header badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulsingGlowBadge(
                    text = "🔥 ${matchResult.matchScore}% MATCH",
                    badgeColor = Color(0xFF16A34A)
                )

                InteractiveLikeButton(
                    isLiked = isSaved,
                    onToggle = { onToggleSave(profile.id) },
                    activeIcon = Icons.Filled.Bookmark,
                    inactiveIcon = Icons.Outlined.BookmarkBorder,
                    activeColor = BrandPrimary,
                    inactiveColor = BrandMuted,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.sp1))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(profile.avatarColorHex).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = profile.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                        contentDescription = profile.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.name,
                            style = Typography.bodyLarge,
                            color = BrandText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (profile.isVerified) {
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(14.dp))
                        }
                    }

                    Text(profile.subRole, style = Typography.labelLarge, color = BrandSecondary, fontWeight = FontWeight.SemiBold)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB800), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${profile.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("• ${profile.location}", fontSize = 11.sp, color = BrandMuted, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Complementary role reason badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.r1))
                    .background(BrandPrimary.copy(alpha = 0.08f))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "⚡ " + matchResult.complementaryReason,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Shared genres tag list
            if (matchResult.sharedGenres.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    matchResult.sharedGenres.forEach { genre ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("🎵 $genre", fontSize = 10.sp, color = BrandText, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            } else {
                Text(
                    text = "Top Genres: " + profile.genres.take(2).joinToString(", "),
                    fontSize = 10.sp,
                    color = BrandMuted
                )
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
            ) {
                Button(
                    onClick = { onHireClick(profile) },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Send, null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Text("Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { onProfileClick(profile) },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BrandBorder),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText)
                }
            }
        }
    }
}
