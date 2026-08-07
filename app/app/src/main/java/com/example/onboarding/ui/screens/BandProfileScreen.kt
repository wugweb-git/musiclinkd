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
fun BandProfileScreen(
    band: DiscoverProfile,
    onBack: () -> Unit,
    onBookClick: () -> Unit,
    onMemberClick: (DiscoverProfile) -> Unit,
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
                .height(180.dp)
                .background(Brush.radialGradient(colors = listOf(BrandSecondary, Color(band.avatarColorHex))))
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
                    .background(BrandPrimary, RoundedCornerShape(Dimens.r1))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "⚡ LIVE BAND 🎸",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(modifier = Modifier.padding(Dimens.sp2), verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(band.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BrandText)
                if (band.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(18.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("5-Piece Live Ensemble • Koramangala", style = Typography.labelLarge, color = BrandSecondary, fontWeight = FontWeight.Bold)
                PersonaTypeBadge(band.type)
            }

            Text(band.bio, style = Typography.labelLarge, color = BrandText, lineHeight = 18.sp)

            Text("Band Members Lineup", style = Typography.bodyLarge, color = BrandText)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                items(band.members) { mb ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(80.dp)
                            .clickable {
                                onMemberClick(
                                    DiscoverProfile(
                                        id = "mock_" + mb.name,
                                        type = ProfileType.TALENT,
                                        name = mb.name,
                                        category = "Musicians",
                                        subRole = mb.instrument,
                                        bio = mb.name + " is the performing " + mb.instrument + " in " + band.name + ".",
                                        avatarEmoji = "🧑‍🎤"
                                    )
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(mb.avatarColor), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(mb.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(Dimens.sp1))
                        Text(mb.name.split(" ")[0], fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(mb.instrument, fontSize = 8.sp, color = BrandMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            HorizontalDivider()

            Text("Upcoming Concerts & Tour Dates", style = Typography.bodyLarge, color = BrandText)
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                listOf(
                    Triple("28 Jun 2026", "Indie Rock Fiesta @ Phoenix MarketCity", "Mumbai, MH"),
                    Triple("12 Jul 2026", "Swar Fest Showcase @ Swabhumi", "Kolkata, WB"),
                    Triple("05 Aug 2026", "The BlueBreeze Stage @ Koramangala", "Bengaluru, KA")
                ).forEach { (date, eventName, venueLoc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.r2),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(eventName, style = Typography.bodyLarge, color = BrandText)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("📍 $venueLoc", fontSize = 10.sp, color = BrandMuted, fontWeight = FontWeight.Bold)
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
            }

            HorizontalDivider()

            Text("Technical Rider (Backline Requirements)", style = Typography.bodyLarge, color = BrandText)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.r2),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                    listOf(
                        "Drums: 5-piece kit (Mapex/Pearl preferred) with active gates.",
                        "Bass: Ampeg SVT-CL or Gallien-Krueger bass head with 4x10 cabinet.",
                        "Guitars: Fender Twin Reverb or Marshall DSL40 combo with SM57.",
                        "Keys: Stereo DI channels with balanced 1/4\" jacks.",
                        "Vocals: 3x Boom stand mics (Shure SM58) with clean phantom power."
                    ).forEach { req ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.CheckCircle, "Rider Spec", tint = BrandPrimary, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Text(req, fontSize = 11.sp, color = Color(0xFF4B5563))
                        }
                    }
                }
            }

            HorizontalDivider()

            Text("Interactive Stage Plot & Rig Positions", style = Typography.bodyLarge, color = BrandText)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.r2),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(Dimens.sp2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("STAGE PLOT LAYOUT (PRO)", color = BrandSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("BACKSTAGE / UPSTAGE", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(Dimens.r2))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF334155), RoundedCornerShape(Dimens.r1))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(Dimens.r1))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("🎹 Keyboard Rig (Line L/R)", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF475569), RoundedCornerShape(Dimens.r1))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(Dimens.r1))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("🥁 Drums (Cage + 4 Gates)", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF334155), RoundedCornerShape(Dimens.r1))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(Dimens.r1))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("🎸 Bass Cab (XLR Out)", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF334155), RoundedCornerShape(Dimens.r1))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(Dimens.r1))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("🎸 Lead Amp (SM57 Mic)", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(BrandPrimary, RoundedCornerShape(Dimens.r1))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("🎤 Lead Vocals (Shure Beta 58A)", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔊 Monitors: Front Wedges (x3) + IEM (x2)", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        Text("FRONT / DOWNSTAGE", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp1))

            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("book_band_cta"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(Dimens.r2)
            ) {
                Text("Lock Booking Dates ($750/night)", fontWeight = FontWeight.Bold)
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
                    Text("Live Performance", fontSize = 11.sp, color = BrandMuted, fontWeight = FontWeight.Medium)
                    Text("$750 / night", style = Typography.bodyLarge, color = BrandText)
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
                        Text("Book Band Now", style = Typography.bodyLarge)
                    }
                }
            }
        }
    }
}
