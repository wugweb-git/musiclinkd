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
fun BusinessProfileScreen(
    studio: DiscoverProfile,
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
                .height(180.dp)
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF1E2937), Color(0xFF111827))))
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

            if (studio.hasAtmos) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(Color.Black, RoundedCornerShape(Dimens.r2))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("DOLBY ATMOS PREFERENCE CELL 🔈", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(modifier = Modifier.padding(Dimens.sp2), verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(studio.name, style = Typography.bodyLarge, color = BrandText)
                if (studio.isVerified) {
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("State-of-The-Art Sound Lab • " + studio.location, style = Typography.labelLarge, color = BrandSecondary, fontWeight = FontWeight.Bold)
                PersonaTypeBadge(studio.type)
            }

            Text(studio.bio, style = Typography.labelLarge, color = BrandText, lineHeight = 18.sp)

            val businessCategoryLower = studio.category.lowercase()
            
            when {
                businessCategoryLower.contains("studio") || businessCategoryLower.contains("lab") -> {
                    Text("Certified Studio Gear Checklist", style = Typography.bodyLarge, color = BrandText)
                    studio.equipment.forEach { gear ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, "Checked Item", tint = BrandSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Text(gear, fontSize = 11.sp, color = BrandText)
                        }
                    }
                }
                
                businessCategoryLower.contains("label") || businessCategoryLower.contains("production") -> {
                    Text("Artist Roster & Signed Talents", style = Typography.bodyLarge, color = BrandText)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            Triple("Vedanshu Srivastava", "Vocalist / Playback Singer", 0xFFEA4C89),
                            Triple("Karan Saxena", "Indie / Rock Keyboardist", 0xFF10B981),
                            Triple("Aarav Mehta", "Sufi / Classical Guitarist", 0xFFF59E0B)
                        ).forEach { (name, subRole, colorHex) ->
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(Dimens.r2),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                border = BorderStroke(1.dp, BrandBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier.size(36.dp).background(Color(colorHex), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(subRole, fontSize = 8.sp, color = BrandMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Spacer(modifier = Modifier.height(Dimens.sp1))
                                    Box(
                                        modifier = Modifier
                                            .background(BrandPrimary.copy(alpha = 0.1f), RoundedCornerShape(Dimens.r1))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Signed", fontSize = 8.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                businessCategoryLower.contains("school") || businessCategoryLower.contains("academy") || businessCategoryLower.contains("class") || businessCategoryLower.contains("edu") -> {
                    Text("Offered Courses & Syllabus", style = Typography.bodyLarge, color = BrandText)
                    listOf(
                        "Hindustani Classical Vocals (6 Months Certification) - ₹12,000/mo",
                        "Digital Music Production & DAW Mixology (3 Months Boot) - ₹18,000/mo",
                        "Modern Drumming & Rhythmic Composition (Ongoing Level 1) - ₹8,000/mo"
                    ).forEach { course ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Dimens.r2),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                            Row(modifier = Modifier.padding(Dimens.sp2), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, "Course Icon", tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(course, fontSize = 11.sp, color = BrandText, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                businessCategoryLower.contains("dealer") || businessCategoryLower.contains("equipment") || businessCategoryLower.contains("rental") || businessCategoryLower.contains("store") -> {
                    Text("Premium Gear Inventory (Sale & Hire)", style = Typography.bodyLarge, color = BrandText)
                    listOf(
                        "Neumann U87 Ai Large-diaphragm Microphone - In Stock",
                        "Shure SM7B Vocal Dynamic Microphone - 4 Left",
                        "Focusrite Scarlett 2i2 USB Audio Interface - In Stock"
                    ).forEach { gear ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, "In Stock", tint = BrandSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Text(gear, fontSize = 11.sp, color = BrandText)
                        }
                    }
                }

                else -> {
                    Text("Services & Roster Overview", style = Typography.bodyLarge, color = BrandText)
                    Text(studio.bio, fontSize = 11.sp, color = BrandText, lineHeight = 16.sp)
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
                            val isFree = index != 2 && index != 3
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

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("book_studio_cta"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(Dimens.r2)
            ) {
                Text("Reserve Studio Slot (" + studio.pricing + ")", fontWeight = FontWeight.Bold)
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
                    Text("Studio Hourly Rate", fontSize = 11.sp, color = BrandMuted, fontWeight = FontWeight.Medium)
                    Text(studio.pricing.ifEmpty { "₹2,500 / hr" }, style = Typography.bodyLarge, color = BrandText)
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
                        Text("Reserve Studio", style = Typography.bodyLarge)
                    }
                }
            }
        }
    }
}
