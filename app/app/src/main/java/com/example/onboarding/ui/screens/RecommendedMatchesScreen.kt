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
fun RecommendedMatchesScreen(
    state: DiscoverUiState,
    onBack: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onProfileClick: (DiscoverProfile) -> Unit
) {
    val item = sampleDiscoverProfiles[state.matchSwipeIndex]
    val matchResult = remember(item.id) {
        CollaboratorRecommendationEngine.calculateMatches("Producer", listOf("Bollywood", "Indie", "Pop"), listOf(item)).first()
    }
    
    // Auto-playing the audio track immediately on mount as requested by 9.17 (Instant Audio Trigger)
    var audioState by remember(item.id) { mutableStateOf(1) } // 0 = idle, 1 = buffering, 2 = playing
    
    LaunchedEffect(audioState) {
        if (audioState == 1) {
            kotlinx.coroutines.delay(800)
            audioState = 2
        }
    }
    val isAudioPlaying = audioState == 2
    
    // Interactive card swipe gesture state
    var dragOffset by remember { mutableStateOf(0f) }
    val animatedDragOffset by animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "swipe_drag"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F14)) // Premium Neutral-950 background
    ) {
        // Upper Action Bar with high-contrast, editorial typography
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF181A22), CircleShape)
                    .border(1.dp, Color(0xFF2C2F3A), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            Text(
                text = "Discover Matcher",
                style = Typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
            )

            // Spot matching glow indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary)
            )
        }

        // Main Swipeable Canvas Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background ambient glow for high fashion editorial staging
            Box(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(item.avatarColorHex).copy(alpha = 0.25f), Color.Transparent),
                            radius = 1000f
                        )
                    )
            )

            // The Swipeable Card Container
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(animatedDragOffset.roundToInt(), 0) }
                    .graphicsLayer {
                        // Subtle card rotation during physical drag
                        rotationZ = (animatedDragOffset / 20f).coerceIn(-15f, 15f)
                    }
                    .pointerInput(item.id) {
                        detectDragGestures(
                            onDragEnd = {
                                if (dragOffset > 150f) {
                                    onSwipeRight()
                                } else if (dragOffset < -150f) {
                                    onSwipeLeft()
                                }
                                dragOffset = 0f
                            },
                            onDragCancel = {
                                dragOffset = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount.x
                            }
                        )
                    }
                    .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = Color.Black)
                    .testTag("swipeable_recommendation_card"),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181A22)), // Dark editorial card surface
                border = BorderStroke(1.dp, Color(0xFF2C2F3A))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    
                    // 1. Full-Screen Canvas with deep high-contrast gradient vignette
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(item.avatarColorHex).copy(alpha = 0.4f),
                                        Color(0xFF0D0F14).copy(alpha = 0.85f),
                                        Color(0xFF0D0F14)
                                    ),
                                    startY = 0f,
                                    endY = 1600f
                                )
                            )
                    ) {
                        // Large artistic visual placeholder represent concert stage
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.65f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(Color(item.avatarColorHex).copy(alpha = 0.6f), Color.Transparent)
                                        )
                                    )
                                    .border(2.dp, Color(item.avatarColorHex).copy(alpha = 0.8f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = item.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(Dimens.sp2))
                            
                            // Live Interactive Audio Waveform overlay right in the center
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 40.dp)
                                    .height(60.dp)
                                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(Dimens.r3))
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(Dimens.r3))
                                    .clickable { audioState = if (audioState == 2) 0 else 1 }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)
                                ) {
                                    if (audioState == 1) {
                                        CircularProgressIndicator(
                                            color = BrandPrimary,
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Playback",
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    
                                    if (isAudioPlaying) {
                                        AnimatedWaveform(modifier = Modifier.weight(1f))
                                    } else if (audioState == 1) {
                                        Text(
                                            text = "Buffering Preview...",
                                            style = Typography.bodyLarge.copy(
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    } else {
                                        Text(
                                            text = "Tap to Stream Preview track",
                                            style = Typography.bodyLarge.copy(
                                                color = Color.White.copy(alpha = 0.6f),
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Drag/Swipe Dynamic Indicators Overlay (purple "Interested" on right, gray "Pass" on left)
                    if (dragOffset > 40f) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(24.dp)
                                .graphicsLayer { rotationZ = -12f }
                                .background(Color(0xFFEA4C89), RoundedCornerShape(Dimens.r2))
                                .border(2.dp, Color.White, RoundedCornerShape(Dimens.r2))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "INTERESTED",
                                color = Color.White,
                                style = Typography.bodyLarge,
                                letterSpacing = 1.sp
                            )
                        }
                    } else if (dragOffset < -40f) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(24.dp)
                                .graphicsLayer { rotationZ = 12f }
                                .background(Color(0xFF676B7B), RoundedCornerShape(Dimens.r2))
                                .border(2.dp, Color.White, RoundedCornerShape(Dimens.r2))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "PASS",
                                color = Color.White,
                                style = Typography.bodyLarge,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // 3. Artist Info Overlay Content Card (Vignette layout)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
                    ) {
                        // Match Badge Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFEA4C89).copy(alpha = 0.15f), RoundedCornerShape(9999.dp))
                                    .border(1.dp, Color(0xFFEA4C89).copy(alpha = 0.4f), RoundedCornerShape(9999.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🔥 ${matchResult.matchScore}% Match Score",
                                    color = Color(0xFFEA4C89),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            
                            PersonaTypeBadge(
                                type = item.type,
                                modifier = Modifier.height(24.dp)
                            )
                        }

                        // Complementary role reason badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimens.r1))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⚡ " + matchResult.complementaryReason,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Display Name & Verification
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = item.name,
                                style = Typography.bodyLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = (-0.5).sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (item.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Artist",
                                    tint = Color(0xFFEA4C89),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // SubRole and local area tags
                        Text(
                            text = "${item.subRole} • ${item.location}",
                            style = Typography.bodyLarge.copy(
                                color = Color(0xFFB8BAC7),
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        // 2-Line Strict Bio Text limitation
                        Text(
                            text = item.bio,
                            style = Typography.bodyLarge.copy(
                                color = Color(0xFF8E91A1),
                                lineHeight = 18.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Match Badges chip row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
                        ) {
                            item.genres.take(3).forEach { genre ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF2C2F3A), RoundedCornerShape(9999.dp))
                                        .border(1.dp, Color(0xFF4A4D5C), RoundedCornerShape(9999.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = genre,
                                        style = Typography.bodyLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Dimens.sp1))

                        // Interaction Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Pass button
                            IconButton(
                                onClick = onSwipeLeft,
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFF2C2F3A), CircleShape)
                                    .border(1.dp, Color(0xFF4A4D5C), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Pass",
                                    tint = Color(0xFF8E91A1),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Center View Details Button
                            Button(
                                onClick = { onProfileClick(item) },
                                modifier = Modifier
                                    .height(50.dp)
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(9999.dp),
                                border = BorderStroke(1.dp, Color(0xFF4A4D5C))
                            ) {
                                Text(
                                    text = "Review Portfolio",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            // Right Match/Like Button
                            IconButton(
                                onClick = onSwipeRight,
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFFEA4C89), CircleShape)
                                    .border(1.dp, Color(0xFFFFC2DA).copy(alpha = 0.4f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Match",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Dimens.sp2))
    }
}
