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
fun VenueProfileScreen(
    venue: DiscoverProfile,
    onBack: () -> Unit,
    onBookClick: () -> Unit,
    onToggleSave: () -> Unit,
    onShareClick: () -> Unit,
    isSaved: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
                .verticalScroll(rememberScrollState())
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
            
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(BrandSuccess, RoundedCornerShape(Dimens.r1))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "🏛️ VENUE CAPABLE",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(modifier = Modifier.padding(Dimens.sp2), verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(venue.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BrandText)
                if (venue.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(18.dp))
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Concert Hall & Soundstage • ${venue.location}", style = Typography.labelLarge, color = BrandSecondary, fontWeight = FontWeight.Bold)
                PersonaTypeBadge(venue.type)
            }

            Text(venue.bio.ifEmpty { "Premise acoustic hall engineered for premium audio clarity, containing a full line-array PA system, soundstage, backstage greenroom, and dedicated sound desk." }, style = Typography.labelLarge, color = BrandText, lineHeight = 18.sp)

            Text("Venue Specifications & Capability", style = Typography.bodyLarge, color = BrandText)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.r2),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, "Capacity", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Dimens.sp1))
                        Text("Capacity: 400-500 standing (Indoor & Amphitheater)", fontSize = 11.sp, color = BrandText, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, "Location", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Dimens.sp1))
                        Text("HQ Address: ${venue.area}, ${venue.location}", fontSize = 11.sp, color = BrandText)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ListAlt, "Inclusions", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Dimens.sp1))
                        Text("Inclusions: Backline included, Greenroom, FOH Sound Desk", fontSize = 11.sp, color = BrandText)
                    }
                }
            }

            HorizontalDivider()

            Text("Upcoming Concerts & Festival Calendar", style = Typography.bodyLarge, color = BrandText)
            listOf(
                Triple("Indie Wave Festival", "28 June 2026", "Featuring SwarSymphony"),
                Triple("Acoustic Sunset Series", "14 July 2026", "Featuring Neha Deshmukh")
            ).forEach { (eventTitle, date, artistInfo) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.r2),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.sp2),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(eventTitle, style = Typography.bodyLarge, color = BrandText)
                            Text(artistInfo, fontSize = 10.sp, color = BrandMuted)
                        }
                        Box(
                            modifier = Modifier
                                .background(BrandPrimary.copy(alpha = 0.1f), RoundedCornerShape(Dimens.r1))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(date, fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            HorizontalDivider()

            Text("Availability Calendar Preview", style = Typography.bodyLarge, color = BrandText)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.r2),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(Dimens.sp2)) {
                    Text("June 2026", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("18 Thu", "19 Fri", "20 Sat", "21 Sun", "22 Mon").forEachIndexed { index, day ->
                            val isFree = index != 2
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .background(
                                        if (isFree) BrandSuccess.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f),
                                        RoundedCornerShape(Dimens.r1)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(day.split(" ")[0], style = Typography.bodyLarge, color = BrandText)
                                Text(day.split(" ")[1], fontSize = 9.sp, color = BrandMuted)
                                Text(if (isFree) "Free" else "Booked", fontSize = 8.sp, color = if (isFree) BrandSuccess else Color.Red, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("book_venue_cta"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(Dimens.r2)
            ) {
                Text("Reserve Venue (₹75,000/day)", fontWeight = FontWeight.Bold)
            }
        }
        }

        // Floating Sticky Bottom CTA
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shadowElevation = 16.dp,
            color = Color.White,
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Venue Day Rate", fontSize = 11.sp, color = BrandMuted, fontWeight = FontWeight.Medium)
                    Text(venue.pricing.ifEmpty { "₹75,000 / day" }, style = Typography.bodyLarge, color = BrandText)
                }

                Button(
                    onClick = onBookClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(Dimens.r2),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Book Venue Space", style = Typography.bodyLarge)
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 10: SAVE & SHORTLIST SCREEN
// ==========================================
