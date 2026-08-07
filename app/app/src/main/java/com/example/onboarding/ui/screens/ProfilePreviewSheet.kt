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
fun ProfilePreviewSheet(
    profile: DiscoverProfile,
    isSaved: Boolean,
    onDismiss: () -> Unit,
    onViewFullProfile: () -> Unit,
    onToggleSave: () -> Unit,
    onHireClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.70f)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(profile.avatarColorHex), BrandSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = profile.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                        contentDescription = profile.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, "Close", tint = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(Dimens.r2))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = (profile.followers / 1000).toString() + "k Followers",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(profile.name, style = Typography.bodyLarge, color = BrandText)
                                if (profile.isVerified) {
                                    Spacer(modifier = Modifier.width(Dimens.sp1))
                                    Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val inst = getInstrumentEmoji(profile.subRole, profile.category)
                                Text(inst, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(Dimens.sp1))
                                Text(profile.subRole, style = Typography.labelLarge, color = BrandSecondary, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        IconButton(
                            onClick = onToggleSave,
                            modifier = Modifier.background(BrandPrimary.copy(alpha = 0.08f), CircleShape).size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Save",
                                tint = BrandPrimary
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.sp2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, "Location", tint = BrandMuted, modifier = Modifier.size(14.dp))
                            Text(profile.location, style = Typography.labelLarge, color = BrandText)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB74D), modifier = Modifier.size(14.dp))
                            Text(profile.rating.toString(), style = Typography.bodyLarge, color = BrandText)
                        }

                        Box(
                            modifier = Modifier
                                .background(BrandSuccess.copy(alpha = 0.1f), RoundedCornerShape(Dimens.r2))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(profile.availabilityStatus, fontSize = 10.sp, color = BrandSuccess, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PersonaTypeBadge(profile.type)
                        Text(
                            text = "🟢 Booking Active",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    }

                    Text(profile.bio, style = Typography.labelLarge, color = BrandText, lineHeight = 18.sp)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Languages spoken:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandMuted)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            profile.languages.forEach { l ->
                                Box(modifier = Modifier.background(Color(0xFFEEEEEE), RoundedCornerShape(Dimens.r1)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text(l, fontSize = 9.sp, color = BrandText)
                                }
                            }
                        }
                    }

                     if (profile.projects.isNotEmpty()) {
                        val top = profile.projects[0]
                        val brandColor = when (top.platform.trim().lowercase()) {
                            "spotify" -> Color(0xFF1DB954)
                            "youtube" -> Color(0xFFFF0000)
                            "soundcloud" -> Color(0xFFFF5500)
                            "apple music", "apple" -> Color(0xFFFA243C)
                            "instagram" -> Color(0xFFE1306C)
                            else -> BrandPrimary
                        }
                        val iconSymbol = when (top.platform.trim().lowercase()) {
                            "spotify" -> "🎵"
                            "youtube" -> "▶️"
                            "soundcloud" -> "☁️"
                            "apple music", "apple" -> "🍎"
                            "instagram" -> "📸"
                            else -> "🔗"
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = brandColor.copy(alpha = 0.05f)),
                            border = BorderStroke(1.dp, brandColor.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(Dimens.sp2),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(brandColor.copy(alpha = 0.12f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(iconSymbol, style = Typography.labelLarge)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(top.title, style = Typography.bodyLarge, color = BrandText)
                                        Text("Top Streamed - " + top.platform, fontSize = 10.sp, color = brandColor, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(top.playsCount, fontSize = 11.sp, color = brandColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewFullProfile,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(Dimens.r2),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimary),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Text("Full Showcase", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onHireClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(Dimens.r2),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Contact & Book", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 7: PREMIUM FULL TALENT PROFILE
// ==========================================
