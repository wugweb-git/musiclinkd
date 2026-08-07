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
fun FullTalentProfileScreen(
    profile: DiscoverProfile,
    onBack: () -> Unit,
    onHireClick: () -> Unit,
    onToggleSave: () -> Unit,
    onShareClick: () -> Unit,
    isSaved: Boolean
) {
    var activeSubTab by remember { mutableStateOf("All") }
    var isHeroPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableStateOf(0.35f) }
    var selectedCreditDetail by remember { mutableStateOf<Credit?>(null) }

    // Editorial background color based on profile color accent
    val baseAccentColor = Color(profile.avatarColorHex)
    val lightBgColor = baseAccentColor.copy(alpha = 0.03f)

    // Modular Editorial Section Composables defined locally
    @Composable
    fun ProfileShowcaseSection() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Featured Audio Showcase",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandText
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Premium Custom Audio Player
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // simulated album cover
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(baseAccentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎵", fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.projects.firstOrNull()?.title ?: "Unreleased Studio Session",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = BrandText
                            )
                            Text(
                                text = "${profile.name} • Master Demo Showcase",
                                fontSize = 12.sp,
                                color = BrandMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Interactive Play/Pause Button
                        IconButton(
                            onClick = { isHeroPlaying = !isHeroPlaying },
                            modifier = Modifier
                                .size(48.dp)
                                .background(BrandPrimary, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isHeroPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play Preview",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Interactive waveform & seekbar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val listHeight = listOf(
                            8, 12, 18, 24, 14, 8, 10, 16, 22, 30, 26, 12, 8, 14, 20, 28,
                            16, 10, 18, 24, 32, 22, 14, 8, 12, 20, 16, 10, 14, 18, 10, 6,
                            8, 12, 18, 24, 14, 8, 10, 16, 22, 30, 26, 12, 8, 14, 20, 28,
                            16, 10, 18, 24, 32, 22, 14, 8, 12, 20, 16, 10, 14, 18, 10, 6
                        )
                        val totalBars = listHeight.size
                        listHeight.forEachIndexed { i, maxH ->
                            val isPassed = (i.toFloat() / totalBars.toFloat()) <= playbackProgress
                            val barColor = if (isPassed) {
                                val ratio = i.toFloat() / totalBars.toFloat()
                                val baseColor = Color(
                                    red = BrandPrimary.red + (BrandSecondary.red - BrandPrimary.red) * ratio,
                                    green = BrandPrimary.green + (BrandSecondary.green - BrandPrimary.green) * ratio,
                                    blue = BrandPrimary.blue + (BrandSecondary.blue - BrandPrimary.blue) * ratio
                                )
                                if (isHeroPlaying) baseColor else baseColor.copy(alpha = 0.6f)
                            } else {
                                Color(0xFFD1D5DB)
                            }

                            // Custom pulse/scale factor for play animation
                            val pulseFactor = if (isHeroPlaying) {
                                val wavePulse = rememberInfiniteTransition(label = "waveform_pulse")
                                val scale by wavePulse.animateFloat(
                                    initialValue = 0.8f,
                                    targetValue = 1.2f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(durationMillis = 600 + (i * 12), easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "bar_$i"
                                )
                                scale
                            } else 1.0f

                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(((maxH.dp * pulseFactor).coerceIn(4.dp, 40.dp)))
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(barColor)
                                    .clickable {
                                        playbackProgress = (i.toFloat() / totalBars.toFloat())
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Playback duration metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentSeconds = (185 * playbackProgress).toInt()
                        val min = currentSeconds / 60
                        val sec = currentSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", min, sec),
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = BrandMuted,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "03:05",
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = BrandMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // OTHER PORTFOLIO TRACKS
            Text(
                text = "Portfolio Tracks & Uploads",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandText
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (profile.projects.isEmpty()) {
                EmptyState(
                    title = "No other tracks available",
                    description = "This artist hasn't linked secondary track URLs yet. Keep checking in for future releases.",
                    emoji = "🎶"
                )
            } else {
                profile.projects.forEach { pr ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val platformColor = when (pr.platform.trim().lowercase()) {
                                "spotify" -> Color(0xFF1DB954)
                                "youtube" -> Color(0xFFFF0000)
                                "soundcloud" -> Color(0xFFFF5500)
                                else -> BrandPrimary
                            }
                            val iconSym = when (pr.platform.trim().lowercase()) {
                                "spotify" -> "🎵"
                                "youtube" -> "▶️"
                                "soundcloud" -> "☁️"
                                else -> "🔗"
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(platformColor.copy(alpha = 0.08f), CircleShape)
                                    .border(1.dp, platformColor.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(iconSym, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pr.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(pr.platform, color = platformColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("•", color = BrandMuted, fontSize = 10.sp)
                                    Text(pr.duration, fontSize = 11.sp, color = BrandMuted)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(BrandPrimary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(pr.playsCount, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = BrandPrimary)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ProfileServicesSection() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Professional Collaboration Services",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Book custom work from ${profile.name} with secure payments & guaranteed delivery timelines.",
                fontSize = 12.sp,
                color = BrandMuted,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            val services = when (profile.category) {
                "Singers" -> listOf(
                    Triple("Lead Vocal Recording", "₹8,000", "Custom lead vocals for one full song. Includes 3 tracks (main + double), dry HQ WAV stems, 3 minor revisions. Delivery: 5 days."),
                    Triple("Backing Vocals & Arrangement", "₹4,500", "Harmonization, ad-libs, and vocal background padding layers. Formatted to sit perfectly in your mix. Delivery: 3 days."),
                    Triple("Advertising Jingle Spot", "₹12,000", "High-energy commercial voice recording for promotions, product ads, or video intro clips. Delivery: 2 days.")
                )
                "Musicians" -> listOf(
                    Triple("Studio Solo Session", "₹6,000", "A custom signature instrument solo piece recorded using pristine preamps & mics. Handcrafted rhythm + stems. Delivery: 4 days."),
                    Triple("Full Track Accompaniment", "₹9,500", "Comprehensive instrument tracking throughout your composition (e.g. guitar layers, bass arrangement, rhythm piano). Delivery: 5 days."),
                    Triple("Rhythm Loop / Custom Pack", "₹3,000", "Up to 5 custom original loops or backing samples matching your BPM and chord map. Delivery: 2 days.")
                )
                "Engineers" -> listOf(
                    Triple("Master Class Mixing Session", "₹12,500", "Dynamic processing, vocal pitch-correction, leveling, spatial panning, analog hardware sum. Up to 60 tracks. Delivery: 6 days."),
                    Triple("Analog Mastering Polish", "₹3,500", "Stereo widening, peak compression, dynamic EQ leveling, and streaming loudness targets check (LUFS alignment). Delivery: 2 days."),
                    Triple("Vocal Cleaning & Alignment", "₹4,000", "Manual sibilance control, background noise reduction, manual timing alignment of harmonies to leads. Delivery: 3 days.")
                )
                else -> listOf(
                    Triple("Professional Custom Booking", "₹10,000", "A bespoke session service engineered to your precise instructions. Contact for brief/consultation first. Delivery: 5 days."),
                    Triple("Ad-hoc Project Collaboration", "₹5,000", "Consulting, creative input, digital arrangements, and raw stem feedback. Delivery: 3 days.")
                )
            }

            services.forEach { (name, price, desc) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Turnaround",
                                        tint = BrandMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    val estDays = desc.substringAfter("Delivery: ").substringBefore(".")
                                    Text(
                                        text = "Est: $estDays",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandMuted
                                    )
                                }
                            }

                            Text(
                                text = price,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = BrandSuccess
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = desc,
                            fontSize = 13.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Included",
                                    tint = BrandSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Secure Direct Pay",
                                    fontSize = 11.sp,
                                    color = BrandSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onHireClick,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Book Service", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ProfileReviewsSection() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Verified Testimonials",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandText
                    )
                    Text(
                        text = "100% genuine reviews from collaborators.",
                        fontSize = 12.sp,
                        color = BrandMuted
                    )
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = BorderStroke(1.dp, Color(0xFFFEF3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("⭐", fontSize = 12.sp)
                        Text(
                            text = "${profile.rating} / 5.0",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (profile.reviews.isEmpty()) {
                EmptyState(
                    title = "No Reviews Yet",
                    description = "Be the first one to book a collaboration session with ${profile.name} and submit a verified review!",
                    emoji = "⭐"
                )
            } else {
                profile.reviews.forEach { rv ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(rv.avatarColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = rv.author.take(1).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = rv.author,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            color = BrandText
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "Verified booking",
                                                tint = BrandPrimary,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = "Verified Hire • ${rv.date}",
                                                fontSize = 9.sp,
                                                color = BrandMuted,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(5) { starIndex ->
                                        val starRating = rv.rating.toInt()
                                        Icon(
                                            imageVector = if (starIndex < starRating) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Star",
                                            tint = if (starIndex < starRating) Color(0xFFF59E0B) else BrandBorder,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = rv.commentary,
                                fontSize = 13.sp,
                                color = Color(0xFF4B5563),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ProfileBioInfoSection() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Verified Collaborator Credits",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandText
            )
            Spacer(modifier = Modifier.height(8.dp))

            // IMDb of Music Trust Score indicator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandPrimary.copy(alpha = 0.04f)),
                border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(BrandPrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛡️", fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Verified Profile Score: 100%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandText)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(BrandSuccess.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("VERIFIED", fontSize = 7.sp, color = BrandSuccess, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Credits and portfolio releases verified through collaborator consensus on MusicLinkd.",
                            fontSize = 10.sp,
                            color = BrandMuted,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            if (profile.credits.isEmpty()) {
                EmptyState(
                    title = "No Credits Recorded",
                    description = "Verified industry credits have not been recorded or linked to this portfolio ledger yet.",
                    emoji = "📜"
                )
            } else {
                profile.credits.forEach { cr ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedCreditDetail = cr
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(baseAccentColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = cr.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = BrandText
                                    )
                                    Text(
                                        text = "${cr.role} • ${cr.artist} (${cr.year})",
                                        fontSize = 11.sp,
                                        color = BrandMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(BrandPrimary.copy(alpha = 0.08f), RoundedCornerShape(99.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Seal Detail 🔐", fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            val categoryLower = profile.category.lowercase()
            
            Text(
                text = when {
                    categoryLower.contains("singer") -> "Singer specifications"
                    categoryLower.contains("music") || categoryLower.contains("composer") || categoryLower.contains("producer") -> "Musician specifications"
                    categoryLower.contains("dj") -> "DJ Performance Deck"
                    categoryLower.contains("engineer") || categoryLower.contains("editor") -> "Engineering Tech Spec"
                    categoryLower.contains("lyric") -> "Lyricist Writing Ledger"
                    else -> "Professional Specifications"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandText
            )
            Spacer(modifier = Modifier.height(12.dp))

            when {
                categoryLower.contains("singer") -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Text("Vocal Register: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text(profile.vocalRange, fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Preferred Genres: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text(profile.genres.joinToString(", "), fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Languages Spoken: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text(profile.languages.joinToString(", "), fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Microphone Spec: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text("Neumann U87 Ai / Shure SM7B", fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Session Booking: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text("🟢 Remote stems & Live Session", fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                categoryLower.contains("music") || categoryLower.contains("composer") || categoryLower.contains("producer") -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Text("Core Instrument: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text(profile.subRole.split(" ").lastOrNull() ?: "Guitar/Keys", fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Setup & Backline: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text("Kemper Profiler, Fender Twin Reverb, Vintage Analog Pedalboard", fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Session Availability: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text("Touring, Studio Sessions, Remote stems", fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Preferred Genres: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text(profile.genres.joinToString(", "), fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                categoryLower.contains("engineer") || categoryLower.contains("editor") -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Text("DAW Stack: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text("Pro Tools Ultimate, Logic Pro X, Universal Audio LUNA", fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Outboard Gear: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text("SSL G-Comp Bus Compressor, Neve 1073 Preamp, Tube-Tech CL1B", fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🎛️ Interactive A/B Mastering Compare", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = BrandText)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Toggle between raw unmixed stem vs. high-definition analog master.", fontSize = 11.sp, color = BrandMuted)
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                var currentChannel by remember { mutableStateOf("B") }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { currentChannel = "A" },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (currentChannel == "A") Color.DarkGray else Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color.LightGray)
                                    ) {
                                        Text("A - Dry / Raw Vocal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (currentChannel == "A") Color.White else Color.Black)
                                    }
                                    Button(
                                        onClick = { currentChannel = "B" },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (currentChannel == "B") BrandPrimary else Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color.LightGray)
                                    ) {
                                        Text("B - SSL Analog Master", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (currentChannel == "B") Color.White else Color.Black)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Active channel",
                                        tint = if (currentChannel == "B") BrandSuccess else BrandText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (currentChannel == "A") {
                                            "Playing: Raw vocal stem, dry, unprocessed (-6dB headroom)"
                                        } else {
                                            "Playing: 128x Oversampled master, tube leveling, Atmos Spatial expansion active"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (currentChannel == "B") BrandSuccess else BrandText
                                    )
                                }
                            }
                        }
                    }
                }

                categoryLower.contains("dj") -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Text("Live Setup Deck: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text("Pioneer CDJ-3000 (x4) + DJM-V10 Mixer", fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Mixcloud Embed: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text("mixcloud.com/live-sunset-deep-house-remix", fontSize = 12.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                        
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("🎪 Club Residency & Performance Log", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = BrandText)
                                Spacer(modifier = Modifier.height(8.dp))
                                listOf(
                                    "Hilltop Festival Goa (Supporting Live Act) - 2026",
                                    "Kitsune Tour (Bengaluru Chapter Residency) - 2025",
                                    "Soho House Mumbai (Roof-deck sundowner series) - 2025",
                                    "The Leather Bar Chennai (Resident deep house selector) - 2024"
                                ).forEach { eventLog ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("•", color = BrandPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.width(16.dp))
                                        Text(eventLog, fontSize = 11.sp, color = BrandText, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }

                categoryLower.contains("lyric") -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Text("Writing Languages: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text(profile.languages.joinToString(", "), fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("IPR / IPRS Registered: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text("🟢 Yes (Registered Lyricist)", fontSize = 12.sp, color = BrandSuccess, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("📜 Signature Writing Samples Showcase", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = BrandText)
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFCFD)),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "“Kuch adhoore se alfaaz mere,\nKora kaagaz aur jazbaat mere...\nBehti nadi sa thahar gaya thha main,\nChhu ke guzre jab khayaal tere.”",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandText,
                                    lineHeight = 22.sp,
                                    fontStyle = FontStyle.Italic
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Category: Bollywood Romantic / Sufi Pop", fontSize = 10.sp, color = BrandMuted, fontWeight = FontWeight.Bold)
                                    Box(
                                        modifier = Modifier.background(BrandPrimary.copy(alpha = 0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Sample #1", fontSize = 8.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            Text("Specialty Role: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text(profile.subRole, fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Preferred Genres: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text(profile.genres.joinToString(", "), fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                        Row {
                            Text("Languages: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandMuted, modifier = Modifier.width(140.dp))
                            Text(profile.languages.joinToString(", "), fontSize = 12.sp, color = BrandText, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (profile.equipment.isNotEmpty()) {
                Text(
                    text = "Primary Setup & Gear",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandText
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profile.equipment.take(4).forEach { gear ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(99.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(gear, fontSize = 11.sp, color = Color(0xFF374151), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

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
        // TOP HEADER - EDITORIAL IMAGELESS COVER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            baseAccentColor.copy(alpha = 0.12f),
                            Color.White
                        )
                    )
                )
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color.Black.copy(alpha = 0.06f), CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .border(1.dp, Color.Black.copy(alpha = 0.06f), CircleShape)
                                .size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Profile",
                                tint = BrandText,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleSave,
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .border(1.dp, Color.Black.copy(alpha = 0.06f), CircleShape)
                                .size(44.dp)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Save Profile",
                                tint = if (isSaved) BrandPrimary else BrandText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // IDENTITY SECTION
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Large Premium Avatar with Border following 32px shape
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(baseAccentColor.copy(alpha = 0.2f), baseAccentColor.copy(alpha = 0.05f))
                                )
                            )
                            .border(1.5.dp, baseAccentColor.copy(alpha = 0.3f), RoundedCornerShape(32.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = profile.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                            contentDescription = profile.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Availability Badge
                    val isAvailable = profile.availabilityStatus.contains("Avail", ignoreCase = true) || profile.availabilityStatus.contains("Open", ignoreCase = true)
                    val badgeColor = if (isAvailable) Color(0xFF10B981) else Color(0xFFF59E0B)
                    Row(
                        modifier = Modifier
                            .border(1.dp, badgeColor.copy(alpha = 0.25f), RoundedCornerShape(99.dp))
                            .background(badgeColor.copy(alpha = 0.06f), RoundedCornerShape(99.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(badgeColor, CircleShape)
                        )
                        Text(
                            text = profile.availabilityStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Name & Verified Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = profile.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandText,
                        lineHeight = 38.sp
                    )
                    if (profile.isVerified) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Artist",
                            tint = BrandPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Specialty and Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val instEmoji = getInstrumentEmoji(profile.subRole, profile.category)
                    Text(instEmoji, fontSize = 15.sp)
                    Text(
                        text = profile.subRole,
                        fontSize = 15.sp,
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text("•", color = BrandMuted, fontSize = 14.sp)
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = BrandMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${profile.area}, ${profile.location}",
                        fontSize = 14.sp,
                        color = BrandMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bio Summary
                Text(
                    text = profile.bio.ifEmpty { "A talented professional music creator specializing in creating world-class, premium acoustic experiences, live stage performances, and studio records." },
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Dashboard Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        Triple("★ " + profile.rating.toString(), "Rating", Color(0xFFF59E0B)),
                        Triple(profile.experienceLevel.split(" ").firstOrNull() ?: "Pro", "Experience", BrandPrimary),
                        Triple(profile.pricing.ifEmpty { "₹2,500/hr" }, "Est. Rate", BrandSuccess),
                        Triple(if (profile.followers > 1000) "${(profile.followers / 1000.0).toString().take(4)}K" else profile.followers.toString(), "Followers", Color(0xFFEA4C89))
                    ).forEach { (value, label, color) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = value,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = color
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Collaborate Button
                Button(
                    onClick = onHireClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827))
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Engage",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Collaborate / Hire ${profile.name.split(" ").first()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        // SUBTABS NAVIGATION BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            listOf("All", "Showcase", "Services", "Reviews", "Bio & Info").forEach { tab ->
                val isSelected = activeSubTab == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { activeSubTab = tab }
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = tab,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF111827) else BrandMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(if (isSelected) 30.dp else 0.dp)
                            .background(if (isSelected) BrandPrimary else Color.Transparent)
                    )
                }
            }
        }

        HorizontalDivider(color = BrandBorder)

        // TAB DETAILS & CONTENT - EDITORIAL WITH MASSIVE WHITESPACE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            when (activeSubTab) {
                "All" -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(48.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfileShowcaseSection()
                        HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f))
                        ProfileServicesSection()
                        HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f))
                        ProfileReviewsSection()
                        HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f))
                        ProfileBioInfoSection()
                    }
                }
                "Showcase" -> ProfileShowcaseSection()
                "Services" -> ProfileServicesSection()
                "Reviews" -> ProfileReviewsSection()
                "Bio & Info" -> ProfileBioInfoSection()
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Verified Seal Dialog (Credits Trust Engine Section 23)
        selectedCreditDetail?.let { cr ->
            AlertDialog(
                onDismissRequest = { selectedCreditDetail = null },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { selectedCreditDetail = null }) {
                        Text("Dismiss", color = BrandPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🛡️ Verified Collaborator Credit", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandText)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Custom micro animation success checkmark
                        MicroCheckmarkSuccessAnimation(modifier = Modifier.size(48.dp))
                        
                        Text(
                            text = "IPRS / MCI CERTIFICATE SEAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandSuccess,
                            letterSpacing = 1.5.sp
                        )
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Track / Work:", fontSize = 11.sp, color = BrandMuted, fontWeight = FontWeight.Bold)
                                Text(cr.title, fontSize = 11.sp, color = BrandText, fontWeight = FontWeight.ExtraBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Certified Role:", fontSize = 11.sp, color = BrandMuted, fontWeight = FontWeight.Bold)
                                Text(cr.role, fontSize = 11.sp, color = BrandText, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Artist / Label:", fontSize = 11.sp, color = BrandMuted, fontWeight = FontWeight.Bold)
                                Text(cr.artist, fontSize = 11.sp, color = BrandText, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Year of Release:", fontSize = 11.sp, color = BrandMuted, fontWeight = FontWeight.Bold)
                                Text(cr.year, fontSize = 11.sp, color = BrandText, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Registry Status:", fontSize = 11.sp, color = BrandMuted, fontWeight = FontWeight.Bold)
                                Text("🟢 ACTIVE & CERTIFIED", fontSize = 11.sp, color = BrandSuccess, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        
                        Text(
                            text = "This credential was cryptographically signed and published by the certified label representatives with zero discrepancies.",
                            fontSize = 10.sp,
                            color = BrandMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            )
        }
        }

        // Floating Sticky Bottom Booking CTA Bar
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
                    Text(
                        text = "Session Rate",
                        fontSize = 11.sp,
                        color = BrandMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = profile.pricing.ifEmpty { "₹8,000 / session" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = BrandText
                    )
                }

                Button(
                    onClick = onHireClick,
                    modifier = Modifier.testTag("full_profile_hire_cta"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Book / Hire Talent",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 8: BAND PROFILE SCREEN
// ==========================================
