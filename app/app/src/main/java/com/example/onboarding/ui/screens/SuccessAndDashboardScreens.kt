@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.onboarding.ui.screens

import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.onboarding.util.ErrorReportingService
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onboarding.models.*
import com.example.onboarding.ui.components.EmptyState
import com.example.onboarding.ui.components.NativeQRCodeCanvas
import com.example.onboarding.ui.components.scale
import com.example.onboarding.ui.components.BrandIcon
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.onboarding.ui.components.*
import com.example.ui.theme.*

// SCREEN 10: SUCCESS STATE
data class CommunityComment(
    val author: String,
    val text: String,
    val timeAgo: String,
    val emoji: String
)

data class NewsletterPost(
    val title: String,
    val description: String,
    val date: String,
    val reads: String,
    val category: String,
    val hasAudio: Boolean,
    val audioDuration: String,
    val initialPlayCount: Int,
    val content: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuccessOnboardingScreen(
    state: OnboardingState,
    onNavigateDashboard: () -> Unit,
    onShareProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(Dimens.sp4))
        
        // Brand logo
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(BrandPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(Dimens.sp3))
        
        Text(
            text = "Welcome to the Grid.",
            style = Typography.bodyLarge.copy(
                fontWeight = FontWeight.Black,
                color = NavyDark,
                letterSpacing = (-1.5).sp
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimens.sp1))
        
        Text(
            text = "Your digital profile is live on India's premier music professional registry.",
            style = Typography.bodyLarge.copy(
                color = BrandTextSecondary,
                lineHeight = 22.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Premium Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r3)),
            shape = RoundedCornerShape(Dimens.r3),
            colors = CardDefaults.cardColors(containerColor = BrandBackground)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.sp3),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Real profile photograph or beautiful professional placeholder
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(BrandBorder)
                ) {
                    AsyncImage(
                        model = if (state.profilePicturePath != null) state.profilePicturePath else "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                        contentDescription = state.fullName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.height(Dimens.sp2))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.fullName.ifEmpty { "Vedanshu Srivastava" },
                        style = Typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Professional",
                        tint = BrandPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Text(
                    text = state.primaryRole ?: "Music Producer",
                    color = BrandPrimary,
                    style = Typography.bodyLarge,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                Text(
                    text = "musiclinkd.com/${state.displayName.ifEmpty { "vedanshu" }}",
                    color = BrandTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
                
                HorizontalDivider(color = BrandBorder)
                
                Spacer(modifier = Modifier.height(Dimens.sp2))
                
                // Native QR Card
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color.White, RoundedCornerShape(Dimens.r2))
                        .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    NativeQRCodeCanvas(
                        contentUrl = "https://musiclinkd.com/${state.displayName.ifEmpty { "vedanshu" }}",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.height(Dimens.sp2))
                
                Text(
                    text = "Scan to view digital registry record",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandTextSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Dimens.sp4))
        
        // Action buttons
        Button(
            onClick = onNavigateDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("go_to_dashboard_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Launch My Registry Dashboard",
                style = Typography.bodyLarge.copy(style = Typography.bodyLarge)
            )
        }
        
        Spacer(modifier = Modifier.height(Dimens.sp2))
        
        OutlinedButton(
            onClick = onShareProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("share_profile_success_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDark),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(Dimens.sp1))
                Text(
                    text = "Share My Digital Pass",
                    style = Typography.bodyLarge.copy(style = Typography.bodyLarge)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Dimens.sp4))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuccessOnboardingScreen_OLD(
    state: OnboardingState,
    onNavigateDashboard: () -> Unit,
    onShareProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    return
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuccessOnboardingScreen_OLD_UNUSED(
    state: OnboardingState,
    onNavigateDashboard: () -> Unit,
    onShareProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val avatar = availableAvatars.getOrElse(state.selectedAvatarIndex) { availableAvatars[0] }
    
    // Community board tab state
    var activeTab by remember { mutableStateOf("Letters & Audio") }
    
    // Dynamic list of comments (interactive community board)
    val comments = remember {
        mutableStateListOf(
            CommunityComment("Amit Trivedi", "This is pure gold! The acoustic demo has so much soul. Let's record in Swar next week.", "2 hours ago", "🎹"),
            CommunityComment("Jonita Gandhi", "That vocal crossover blend is magical. Your tone is absolutely pristine!", "5 hours ago", "🎤"),
            CommunityComment("Arijit Singh", "Excellent phrasing and control. Subscribed!", "Yesterday", "🎙️")
        )
    }
    var newCommentText by remember { mutableStateOf("") }
    
    // Subscribe state
    var isSubscribed by remember { mutableStateOf(false) }
    var subscriberCount by remember { mutableStateOf(1420) }
    
    // Audio playback simulation state
    var playingPostTitle by remember { mutableStateOf<String?>(null) }
    var audioProgress by remember { mutableStateOf(0f) }
    var audioTimeElapsed by remember { mutableStateOf(0) }
    
    // Auto-advance audio timer when playing
    LaunchedEffect(playingPostTitle) {
        if (playingPostTitle != null) {
            audioProgress = 0f
            audioTimeElapsed = 0
            while (audioProgress < 1f) {
                kotlinx.coroutines.delay(1000)
                audioTimeElapsed += 1
                audioProgress = (audioTimeElapsed / 150f).coerceAtMost(1f) // simulated 2:30 min duration
            }
            playingPostTitle = null
        }
    }

    // Dynamic posts based on the user's selected primary role
    val posts = remember(state.primaryRole) {
        val role = state.primaryRole ?: "Composer"
        val firstTitle = when {
            role.contains("Singer") || role.contains("Vocalist") -> "Refining Indian Vocal Harmonies in Modern Indie Pop"
            role.contains("Lyricist") -> "Phonetic Phrasing in Songwriting: A Lyricist's Field Guide"
            role.contains("Producer") || role.contains("Engineer") -> "Designing Warm Analog Synth Basses without Low-End Mud"
            role.contains("Composer") -> "Bridging Classical Ragas with 80s Synthesizer Waveforms"
            else -> "Unlocking Musical Collaboration in the Independent Scene"
        }
        val firstEx = when {
            role.contains("Singer") || role.contains("Vocalist") -> "In this letter, we explore how classical Hindustani backing vocals are recorded across different room sizes. I've uploaded a raw acoustic vocal track below so you can listen to the raw resonance."
            role.contains("Lyricist") -> "Phonetics dictate whether a lyric feels memorable or clumsy. We map the specific vowel selections that align best with fast-attack dholak and acoustic guitar tempos."
            role.contains("Producer") || role.contains("Engineer") -> "Sub-basses need space. In this tutorial and audio commentary, I demonstrate how to sidechain classical tabla low resonances with analog kick transients."
            role.contains("Composer") -> "How do you map Raga Bhairavi to a cinematic minor synthwave progression? We break down chord substitutions and raw arrangements with live audio demos."
            else -> "A deep-dive into how independent creators can bypass traditional labels by distributing directly through audio-first newsletters and community subscription groups."
        }
        
        listOf(
            NewsletterPost(
                title = firstTitle,
                description = firstEx,
                date = "Jun 27",
                reads = "1.2K",
                category = "Creator Newsletter",
                hasAudio = true,
                audioDuration = "2:30",
                initialPlayCount = 482,
                content = "Every classical crossover starts with a decision. For my upcoming single, I decided to blend raw harmonium notes with an ambient reverb tail that wraps around the vocal stem like a warm blanket. In the audio preview, you can hear the raw vocals before the final mixing process. Feel free to leave a comment with your feedback!"
            ),
            NewsletterPost(
                title = "Acoustic Crossover Sessions: Raw Voice & Harmonium Live",
                description = "Recording raw, unedited sessions straight from the living room in Andheri West. This is as honest as independent music gets.",
                date = "Jun 15",
                reads = "890",
                category = "Raw Demos",
                hasAudio = true,
                audioDuration = "1:45",
                initialPlayCount = 210,
                content = "No autotune. No complex foley. Just an acoustic companion track recorded at 2:00 AM on a vintage harmonium box. I would love for producers in Mumbai to pitch remix styles or ambient stems for this arrangement."
            ),
            NewsletterPost(
                title = "The Independent Model for Indian Musicians: Why We Must Own Our Community",
                description = "Traditional streaming platforms pay fractions of a paisa. Here's why editorial subscriptions are the true future of independent sustainability.",
                date = "Jun 02",
                reads = "2.4K",
                category = "Music Business",
                hasAudio = false,
                audioDuration = "",
                initialPlayCount = 0,
                content = "We depend too much on corporate algorithms to feed our listeners. By building direct, editorial, written relationships with subscribers, we convert passing casual stream listeners into active lifelong patrons who fund our studio gear and live tours."
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCFBF9)) // Creamy Editorial Canvas
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP LOGO BAR (Editorial Inspired)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 1.dp, color = BrandBorder)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Editorial iconic symbol stylized in Pink
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "musiclinkd.com/editorial",
                    style = Typography.bodyLarge,
                    color = BrandTextPrimary,
                    letterSpacing = (-0.2).sp
                )
            }

            // Small indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.r1))
                    .background(Color(0xFFFFECEE))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "EDITORIAL PRO",
                    color = BrandPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // HERO HEADER BLOCK (Publication Identity)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User Avatar representation
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Color(avatar.colorHex))
                    .border(3.dp, Color.White, CircleShape)
                    .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = if (state.profilePicturePath != null) state.profilePicturePath else avatar.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // Editorial Publication Name
            Text(
                text = if (state.fullName.isEmpty()) "Vedanshu Srivastava" else state.fullName,
                style = Typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandTextPrimary,
                    style = Typography.headlineLarge,
                    letterSpacing = (-0.5).sp
                ),
                textAlign = TextAlign.Center
            )

            // Custom publication handle
            Text(
                text = "The ${state.displayName.ifEmpty { "vedanshu_vocals" }} Letter",
                style = Typography.bodyLarge.copy(
                    color = BrandPrimary,
                    style = Typography.bodyLarge
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle / Editorial description
            Text(
                text = if (state.bio.isEmpty()) "Lead playback vocal and classical folk-pop independent songwriter. Specializing in classical Hindustani arrangements." else state.bio,
                style = Typography.bodyLarge.copy(
                    color = BrandTextSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 3,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stats Subtext
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${subscriberCount} subscribers",
                    color = BrandTextPrimary,
                    style = Typography.bodyLarge
                )
                Text(
                    text = " • ",
                    color = BrandTextSecondary,
                    style = Typography.labelLarge
                )
                Text(
                    text = "${posts.size} posts",
                    color = BrandTextSecondary,
                    style = Typography.labelLarge
                )
                Text(
                    text = " • ",
                    color = BrandTextSecondary,
                    style = Typography.labelLarge
                )
                Text(
                    text = "📍 ${state.city.ifEmpty { "Mumbai" }}",
                    color = BrandTextSecondary,
                    style = Typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // MONETIZATION & CORE CTAS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Subscribe Button (Simulated active flow)
                Button(
                    onClick = {
                        isSubscribed = !isSubscribed
                        if (isSubscribed) {
                            subscriberCount += 1
                        } else {
                            subscriberCount -= 1
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp)
                        .testTag("community_subscribe_button"),
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubscribed) Color(0xFF137333) else BrandPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSubscribed) Icons.Default.Check else Icons.Default.Mail,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(Dimens.sp1))
                        Text(
                            text = if (isSubscribed) "Subscribed" else "Subscribe to Updates",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Collaborate / Hire pitch
                OutlinedButton(
                    onClick = onShareProfile,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("community_collaborate_button"),
                    shape = RoundedCornerShape(99.dp),
                    border = BorderStroke(1.dp, BrandBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandTextPrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // NAVIGATION TABS (Segmented Underlines)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 1.dp, color = BrandBorder)
                .padding(horizontal = 16.dp)
        ) {
            val tabs = listOf("Letters & Audio", "About & Gear", "Recommendations")
            tabs.forEach { tabName ->
                val isSelected = activeTab == tabName
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = tabName }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = tabName,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandPrimary else BrandTextSecondary,
                            style = Typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(Dimens.sp1))
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(if (isSelected) 40.dp else 0.dp)
                                .background(BrandPrimary)
                        )
                    }
                }
            }
        }

        // DYNAMIC TAB BODY CONTENTS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            when (activeTab) {
                "Letters & Audio" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        posts.forEach { post ->
                            val isPlayingThis = playingPostTitle == post.title
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(Dimens.r2),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BrandBorder)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    // Header of post
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = post.category.uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = BrandPrimary,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = post.date,
                                            color = BrandTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(Dimens.sp1))

                                    // Post Title
                                    Text(
                                        text = post.title,
                                        style = Typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = BrandTextPrimary,
                                            lineHeight = 20.sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Description Excerpt
                                    Text(
                                        text = post.description,
                                        color = BrandTextSecondary,
                                        style = Typography.labelLarge,
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // FULL EXCITING EXCERPT (progressive disclosure)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(Dimens.r1))
                                            .background(BrandBackground)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = post.content,
                                            color = BrandTextPrimary,
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Audio Podcast / Demo Widget
                                    if (post.hasAudio) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(Dimens.r2))
                                                .background(Color(0xFFFFF7F8))
                                                .border(1.dp, BrandPrimary.copy(alpha = 0.15f), RoundedCornerShape(Dimens.r2))
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                // Circular Play / Pause trigger button
                                                Box(
                                                    modifier = Modifier
                                                        .size(42.dp)
                                                        .clip(CircleShape)
                                                        .background(BrandPrimary)
                                                        .clickable {
                                                            playingPostTitle = if (isPlayingThis) null else post.title
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (isPlayingThis) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(Dimens.sp2))

                                                // Track and simulated progress bar
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = "Listen to Demo Preview",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = BrandTextPrimary
                                                        )
                                                        Text(
                                                            text = if (isPlayingThis) "${audioTimeElapsed / 60}:${String.format("%02d", audioTimeElapsed % 60)} / ${post.audioDuration}" else post.audioDuration,
                                                            fontSize = 10.sp,
                                                            color = BrandTextSecondary
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(6.dp))

                                                    if (isPlayingThis) {
                                                        // Linear Progress bar moving
                                                        LinearProgressIndicator(
                                                            progress = { audioProgress },
                                                            modifier = Modifier.fillMaxWidth().height(4.dp),
                                                            color = BrandPrimary,
                                                            trackColor = BrandBorder
                                                        )
                                                    } else {
                                                        // Static line
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(2.dp)
                                                                .background(BrandBorder)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))
                                    }

                                    // Post footer metrics (Reads, Comments, Likes)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.FavoriteBorder,
                                                    contentDescription = null,
                                                    tint = BrandTextSecondary,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(Dimens.sp1))
                                                Text(
                                                    text = if (post.hasAudio) "${post.initialPlayCount + (if (isPlayingThis) 1 else 0)}" else "12",
                                                    color = BrandTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Comment,
                                                    contentDescription = null,
                                                    tint = BrandTextSecondary,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(Dimens.sp1))
                                                Text(
                                                    text = if (post.hasAudio) "2" else "0",
                                                    color = BrandTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Text(
                                            text = "${post.reads} views",
                                            color = BrandTextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "About & Gear" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                        // Bio & Statement
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Dimens.r2),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Editorial Bio",
                                    style = Typography.bodyLarge,
                                    color = BrandTextPrimary
                                )
                                Spacer(modifier = Modifier.height(Dimens.sp1))
                                Text(
                                    text = if (state.bio.isEmpty()) "Lead vocal playback artist & classical-folk music crossover producer. Based in Mumbai, crafting warm ambient sonic stories." else state.bio,
                                    style = Typography.bodyLarge.copy(
                                        color = BrandTextSecondary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                )
                                
                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(14.dp))

                                // Level of Experience details
                                if (state.roleExperience.yearsOfExperience.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Primary Role Experience",
                                            color = BrandTextSecondary,
                                            style = Typography.bodyLarge
                                        )
                                        Text(
                                            text = "${state.roleExperience.yearsOfExperience} (${state.roleExperience.proficiencyLevel})",
                                            color = BrandPrimary,
                                            style = Typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }

                        // Languages & Genres
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Dimens.r2),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Vocal Languages",
                                    style = Typography.bodyLarge,
                                    color = BrandTextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val langs = if (state.selectedLanguages.isEmpty()) listOf("English", "Hindi (हिंदी)", "Punjabi (ਪੰਜਾਬੀ)") else state.selectedLanguages.toList()
                                    langs.forEach { lang ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(Dimens.r1))
                                                .background(BrandBackground)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = lang, fontSize = 11.sp, color = BrandTextPrimary, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(Dimens.sp2))

                                Text(
                                    text = "Genres & Stylistic Focus",
                                    style = Typography.bodyLarge,
                                    color = BrandTextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val genres = if (state.selectedGenres.isEmpty()) listOf("Bollywood / Filmi", "Indie Pop", "Sufi Devotional") else state.selectedGenres.toList()
                                    genres.forEach { genre ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(Dimens.r1))
                                                .background(BrandPrimary.copy(alpha = 0.05f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = genre, fontSize = 11.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }

                        // Instruments & Gear details
                        if (state.selectedInstruments.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(Dimens.r2),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BrandBorder)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        text = "Instruments & Performance Gear",
                                        style = Typography.bodyLarge,
                                        color = BrandTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    state.selectedInstruments.forEach { instrument ->
                                        val prof = state.instrumentProficiency[instrument]
                                        val levelStr = prof?.let { "${it.yearsOfExperience} (${it.proficiencyLevel})" } ?: "Advanced"
                                        val emoji = availableInstruments.find { it.name == instrument }?.emoji ?: "🎸"
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "$emoji $instrument",
                                                style = Typography.bodyLarge,
                                                color = BrandTextPrimary
                                            )
                                            Text(
                                                text = levelStr,
                                                fontSize = 11.sp,
                                                color = BrandTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // External Platforms Connected
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Dimens.r2),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Connected Channels",
                                    style = Typography.bodyLarge,
                                    color = BrandTextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                val networks = state.networkingLinks.filter { it.value.isNotEmpty() }
                                val displayNets = if (networks.isEmpty()) mapOf("Spotify" to "https://spotify.com/demo", "Instagram" to "https://instagram.com/demo") else networks
                                
                                displayNets.forEach { (platform, url) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            BrandIcon(
                                                platform = platform,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = platform,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BrandTextPrimary
                                            )
                                        }
                                        Text(
                                            text = url.replace("https://", "").take(24) + "...",
                                            fontSize = 11.sp,
                                            color = BrandPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Mobile Digital Subscription Pass (QR Code elegant component)
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Scan to Subscribe via Mobile",
                                        style = Typography.bodyLarge,
                                        color = BrandTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Share this QR code so local musicians can subscribe to your letter.",
                                        fontSize = 10.5.sp,
                                        color = BrandTextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(Dimens.sp2))
                                NativeQRCodeCanvas(
                                    contentUrl = "https://musiclinkd.com/${state.displayName.ifEmpty { "ananya_vocals" }}",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r1))
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }

                "Recommendations" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Publications & Writers I Recommend",
                            style = Typography.bodyLarge,
                            color = BrandTextPrimary
                        )
                        recommendedConnections.forEach { artist ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(Dimens.r2),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BrandBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(BrandBackground),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🎹", style = Typography.headlineLarge)
                                    }
                                    Spacer(modifier = Modifier.width(Dimens.sp2))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = artist.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = BrandTextPrimary
                                        )
                                        Text(
                                            text = "${artist.role} • Match score: ${artist.matchScore}%",
                                            fontSize = 11.sp,
                                            color = BrandTextSecondary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(Dimens.r1))
                                            .background(BrandPrimary.copy(alpha = 0.08f))
                                            .clickable { /* trigger simulation */ }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Recommend",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = BrandPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FULLY INTERACTIVE COMMUNITY DISCUSSION BOARD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(width = 1.dp, color = BrandBorder)
                .padding(24.dp)
        ) {
            Text(
                text = "💬 Community Discussion",
                style = Typography.bodyLarge,
                color = BrandTextPrimary
            )
            Text(
                text = "Pitch a collaboration, ask a question, or leave a message directly on this publication board.",
                style = Typography.bodyLarge.copy(color = BrandTextSecondary),
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            // Dynamic live list of comments
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                comments.forEach { comment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandBackground, RoundedCornerShape(Dimens.r2))
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(comment.emoji, style = Typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = comment.author,
                                    style = Typography.bodyLarge,
                                    color = BrandTextPrimary
                                )
                                Text(
                                    text = comment.timeAgo,
                                    fontSize = 10.sp,
                                    color = BrandTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = comment.text,
                                fontSize = 11.5.sp,
                                color = BrandTextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // Post comments form
            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                label = { Text("Leave a message / comment...", fontSize = 11.sp) },
                placeholder = { Text("e.g. Let's collaborate! I play keys...", fontSize = 11.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("community_comment_input"),
                shape = RoundedCornerShape(10.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrandBackground,
                    unfocusedContainerColor = BrandBackground,
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = BrandBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        comments.add(
                            CommunityComment(
                                author = "You (Producer / Artist)",
                                text = newCommentText,
                                timeAgo = "Just now",
                                emoji = "🌟"
                            )
                        )
                        newCommentText = ""
                    }
                },
                enabled = newCommentText.isNotBlank(),
                modifier = Modifier
                    .align(Alignment.End)
                    .height(36.dp)
                    .testTag("community_post_comment_button"),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Post Comment", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // FOOTER BUTTON - GO TO DASHBOARD
        Spacer(modifier = Modifier.height(36.dp))
        
        Button(
            onClick = onNavigateDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp)
                .testTag("go_to_dashboard_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "Launch My MusicLinkd Profile",
                style = Typography.bodyLarge.copy(style = Typography.bodyLarge)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.sp4))
    }
}


// --------------------------------------------------------------------------------------
// CHIP SHORTCUTS FOR SUPPORTING HUBS
// --------------------------------------------------------------------------------------
@Composable
fun HubShortcutChip(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(Dimens.r2))
            .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = BrandPrimary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandTextPrimary)
    }
}


// --------------------------------------------------------------------------------------
// POST ONBOARDING COMPLETE DASHBOARD
// --------------------------------------------------------------------------------------
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DashboardPreviewScreen(
    state: OnboardingState,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSavePictures: ((String?, String?) -> Unit)? = null
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf("Home") }
    var availabilityStatus by remember { mutableStateOf(true) }
    var connectionRequestsSent by remember { mutableStateOf(mapOf<String, Boolean>()) }
    var selectedOpportunityForDetails by remember { mutableStateOf<OpportunityItem?>(null) }
    var alertEmail by remember { mutableStateOf("vedanshu.srivastava@gmail.com") }
    var optInAlerts by remember { mutableStateOf(true) }

    var showNotificationDialog by remember { mutableStateOf(false) }
    val notifications = remember {
        mutableStateListOf(
            "🎵 Aria Vox sent a collaboration request for single 'Swar Sandhya'.",
            "📸 Blue Room Mixing Suite approved your booking request for July 12.",
            "⭐ Kabir Singh left a 5-star review on your profile!",
            "🔥 New audition matching 'Sound Designer' is trending in Mumbai."
        )
    }

    Scaffold(
        topBar = {
            if (activeTab == "Home") {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { activeTab = "Profile" },
                            modifier = Modifier.padding(start = 4.dp).testTag("action_bar_avatar")
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                AsyncImage(
                                    model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
                                    contentDescription = "User Profile Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, BrandPrimary, CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                        .border(1.5.dp, Color.White, CircleShape)
                                )
                            }
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.clickable { activeTab = "Home" }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_musiclinkd_logo),
                                contentDescription = "MusicLinkd Icon",
                                modifier = Modifier.size(22.dp),
                                tint = BrandPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "MusicLinkd",
                                style = Typography.bodyLarge,
                                color = BrandPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BrandPrimary.copy(alpha = 0.12f))
                                    .border(1.dp, BrandPrimary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("PRO", fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { activeTab = "Search" },
                            modifier = Modifier.testTag("action_bar_search")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search Hub",
                                tint = BrandTextPrimary
                            )
                        }
                        Box {
                            IconButton(
                                onClick = { activeTab = "Notifications" },
                                modifier = Modifier.testTag("action_bar_notifications")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (notifications.isNotEmpty()) {
                                            Badge(
                                                containerColor = BrandPrimary,
                                                contentColor = Color.White
                                            ) {
                                                Text(notifications.size.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = "Notifications",
                                        tint = BrandTextPrimary
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = { activeTab = "Settings" },
                            modifier = Modifier.testTag("action_bar_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                tint = BrandTextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BrandBackground
                    )
                )
            }
        },
        bottomBar = {
            UnifiedBottomNavigation(
                activeTab = activeTab,
                onTabClick = { activeTab = it }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        val portfolioViewModel: com.example.onboarding.viewmodel.PortfolioViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        val portfolioState by portfolioViewModel.state.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            var isOffline by remember { mutableStateOf(false) } // Simulated state
            com.example.onboarding.ui.components.OfflineAlertBanner(isOffline = isOffline)

            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    (fadeIn(animationSpec = androidx.compose.animation.core.tween(220)) + slideInVertically(animationSpec = androidx.compose.animation.core.tween(220), initialOffsetY = { it / 14 })) togetherWith
                            (fadeOut(animationSpec = androidx.compose.animation.core.tween(150)) + slideOutVertically(animationSpec = androidx.compose.animation.core.tween(150), targetOffsetY = { -it / 14 }))
                },
                modifier = Modifier.weight(1f),
                label = "ScreenTabTransition"
            ) { targetTab ->
                if (targetTab == "Profile" || targetTab == "ProfileManagement") {
                    com.example.onboarding.ui.screens.ProfileManagementMainFlowContainer(
                        userName = state.fullName.ifEmpty { "Vedanshu Srivastava" },
                        onBackClick = { activeTab = "Home" },
                        initialProfilePath = state.profilePicturePath,
                        initialCoverPath = state.coverPicturePath,
                        onSavePictures = onSavePictures,
                        onAddPortfolioProject = {
                            portfolioViewModel.resetFlow()
                            activeTab = "Portfolio"
                        }
                    )
                } else if (activeTab == "Portfolio") {
                    com.example.onboarding.ui.screens.PortfolioMainFlowContainer(
                        portfolioState = portfolioState,
                        onBackClick = { activeTab = "Profile" },
                        onNavigatetoScreen = { portfolioViewModel.navigateTo(it) },
                        onNavigateBackInFlow = { portfolioViewModel.navigateBack() },
                        onUpdateProjectType = { portfolioViewModel.updateProjectType(it) },
                        onUpdateProjectDetails = { name, desc, date, status -> portfolioViewModel.updateProjectDetails(name, desc, date, status) },
                        onUpdateStreamingUrls = { spot, apple, yt, sound, mix, insta -> portfolioViewModel.updateStreamingUrls(spot, apple, yt, sound, mix, insta) },
                        onToggleRole = { portfolioViewModel.toggleRole(it) },
                        onUpdateSearchQuery = { portfolioViewModel.updateSearchQuery(it) },
                        onTagCollaborator = { portfolioViewModel.tagCollaborator(it) },
                        onUntagCollaborator = { portfolioViewModel.untagCollaborator(it) },
                        onUpdateCreditRole = { tag, role -> portfolioViewModel.updateCollaboratorCreditRole(tag, role) },
                        onUpdateVisibility = { portfolioViewModel.updateVisibility(it) },
                        onPublish = { 
                            portfolioViewModel.publishProject()
                            Toast.makeText(context, "Track published successfully!", Toast.LENGTH_LONG).show()
                            activeTab = "Profile"
                        },
                        onPreloadQuickFill = { portfolioViewModel.resetFlow() },
                        onAddIncStat = { key, valStr -> },
                        onPreviewPublish = { },
                        userName = state.fullName.ifEmpty { "Vedanshu Srivastava" },
                        userRole = state.primaryRole ?: "Composer / Sound Engineer"
                    )
                } else if (activeTab == "StudioSpaces") {
                    StudioBookingMainFlowContainer(
                        userName = state.fullName.ifEmpty { "Vedanshu Srivastava" },
                        onBackToDashboard = { activeTab = "Home" }
                    )
                } else if (activeTab == "Discover") {
                    val userAvatar = availableAvatars.getOrElse(state.selectedAvatarIndex) { availableAvatars[0] }
                    DiscoverMainFlowContainer(
                        userName = state.fullName.ifEmpty { "Vedanshu Srivastava" },
                        userRole = state.primaryRole ?: "Composer",
                        avatarImageUrl = if (state.profilePicturePath != null) state.profilePicturePath else userAvatar.imageUrl,
                        onBackClick = { activeTab = "Home" }
                    )
                } else if (activeTab == "Marketplace") {
                    MarketplaceBetaScreen(onBackClick = { activeTab = "Home" })
                } else if (activeTab == "Connections") {
                    SupportingScreensHub(
                        initialModule = SupportingModule.CONNECTIONS,
                        onBack = { activeTab = "Home" }
                    )
                } else if (activeTab == "Search") {
                    SupportingScreensHub(
                        initialModule = SupportingModule.UNIVERSAL_SEARCH,
                        onBack = { activeTab = "Home" }
                    )
                } else if (activeTab == "QRShare") {
                    SupportingScreensHub(
                        initialModule = SupportingModule.QR_SHARE,
                        onBack = { activeTab = "Home" }
                    )
                } else if (activeTab == "Saved") {
                    SupportingScreensHub(
                        initialModule = SupportingModule.SAVED_FAVORITES,
                        onBack = { activeTab = "Home" }
                    )
                } else if (activeTab == "Settings") {
                    SupportingScreensHub(
                        initialModule = SupportingModule.SETTINGS,
                        onBack = { activeTab = "Home" },
                        onLogout = onResetClick
                    )
                } else if (activeTab == "Help") {
                    SupportingScreensHub(
                        initialModule = SupportingModule.HELP_SUPPORT,
                        onBack = { activeTab = "Home" }
                    )
                } else if (activeTab == "Reputation") {
                    ReputationAndReviewsScreenContainer(
                        onBackClick = { activeTab = "Home" }
                    )
                } else if (activeTab == "Notifications") {
                    NotificationsCenterScreen(
                        onBackClick = { activeTab = "Home" }
                    )
                } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BrandBackground)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Dash Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val profileType = remember(state.primaryRole) {
                        val role = state.primaryRole ?: ""
                        when {
                            role.contains("Studio") || role.contains("Label") || role.contains("Publisher") -> "Studio/Biz"
                            role.contains("Organizer") || role.contains("Venue") || role.contains("Event") -> "Live Venue"
                            role.contains("Band") || role.contains("Group") -> "Band/Crew"
                            else -> "Solo Talent"
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                        Text(
                            text = "Hello, ${state.fullName.ifEmpty { "Vedanshu" }}!",
                            style = Typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandTextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(BrandPrimary.copy(alpha = 0.08f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = profileType,
                                color = BrandPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "musiclinkd.com/${state.displayName.ifEmpty { "vedanshu" }}",
                        style = Typography.labelLarge,
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.sp1),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Log Out Action Button
                    Button(
                        onClick = onResetClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444).copy(alpha = 0.1f),
                            contentColor = Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(Dimens.r3),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("dashboard_header_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Log Out",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(Dimens.sp1))
                        Text("Log Out", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Interactive Reset Mode (for easy testing loop!)
                    IconButton(
                        onClick = onResetClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, BrandBorder, CircleShape)
                            .testTag("reset_app_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart flow",
                            tint = BrandTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Quick-access Supporting Hub Shortcuts Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    HubShortcutChip(icon = Icons.Default.Add, label = "+ Add Track/EP", onClick = {
                        portfolioViewModel.resetFlow()
                        activeTab = "Portfolio"
                    })
                }
                item {
                    HubShortcutChip(icon = Icons.Default.GroupAdd, label = "Connections", onClick = { activeTab = "Connections" })
                }
                item {
                    HubShortcutChip(icon = Icons.Default.Search, label = "Search Hub", onClick = { activeTab = "Search" })
                }
                item {
                    HubShortcutChip(icon = Icons.Default.Share, label = "QR Card", onClick = { activeTab = "QRShare" })
                }
                item {
                    HubShortcutChip(icon = Icons.Default.Bookmark, label = "Saved Items", onClick = { activeTab = "Saved" })
                }
                item {
                    HubShortcutChip(icon = Icons.Default.Settings, label = "Settings", onClick = { activeTab = "Settings" })
                }
                item {
                    HubShortcutChip(icon = Icons.Default.Star, label = "Reputation Hub", onClick = { activeTab = "Reputation" })
                }
                item {
                    HubShortcutChip(icon = Icons.Default.Info, label = "Help Desk", onClick = { activeTab = "Help" })
                }
            }

            // ============================================================================
            // NEW: BUSINESS DASHBOARD KPI GRID (From HTML Mockup)
            // ============================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Business Dashboard",
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandTextPrimary,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 2x2 Scorecard Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder),
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("₹84.5k", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = BrandTextPrimary)
                                Text("Revenue", fontSize = 11.sp, color = BrandTextSecondary)
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder),
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("42", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = BrandTextPrimary)
                                Text("Bookings", fontSize = 11.sp, color = BrandTextSecondary)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder),
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("1,240", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = BrandTextPrimary)
                                Text("Views", fontSize = 11.sp, color = BrandTextSecondary)
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder),
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("98%", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = BrandTextPrimary)
                                Text("Response", fontSize = 11.sp, color = BrandTextSecondary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Recent Activity Feed Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Recent Activity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BrandTextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
                            Box(modifier = Modifier.size(6.dp).background(BrandPrimary, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("New booking: North Room", fontSize = 12.sp, color = BrandTextSecondary, modifier = Modifier.weight(1f))
                            Text("2h ago", fontSize = 10.sp, color = BrandTextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(BrandSuccess, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Review received: 4.9 ★", fontSize = 12.sp, color = BrandTextSecondary, modifier = Modifier.weight(1f))
                            Text("5h ago", fontSize = 10.sp, color = BrandTextSecondary)
                        }
                    }
                }
            }

            // 1. Profile Completion Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, BrandBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.sp2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💯", style = Typography.headlineLarge)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Professional Profile Active",
                            fontWeight = FontWeight.Bold,
                            color = BrandTextPrimary,
                            style = Typography.labelLarge
                        )
                        Text(
                            text = "Your identity is indexed. You are 100% discoverable to labels.",
                            color = BrandTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                    
                    IconButton(
                        onClick = {},
                        modifier = Modifier.background(BrandPrimary, CircleShape).size(28.dp)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Availability Switcher Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, BrandBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (availabilityStatus) BrandSuccess else BrandTextSecondary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Collab Availability",
                                fontWeight = FontWeight.Bold,
                                color = BrandTextPrimary,
                                style = Typography.labelLarge
                            )
                            Text(
                                text = if (availabilityStatus) "Open to record & perform" else "Focused on active gigs",
                                color = BrandTextSecondary,
                                style = Typography.labelLarge
                            )
                        }
                    }

                    Switch(
                        checked = availabilityStatus,
                        onCheckedChange = { availabilityStatus = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandPrimary,
                            uncheckedThumbColor = BrandTextSecondary,
                            uncheckedTrackColor = BrandBorder
                        ),
                        modifier = Modifier.testTag("availability_switcher")
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // FLOW 7: Studio spaces interactive booking promotion
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { activeTab = "StudioSpaces" }
                    .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
                    .testTag("launch_flow_7_studio_banner"),
                shape = RoundedCornerShape(Dimens.r2),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0B26))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEA4C89))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("FLOW 7 LIVE DEMO", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFFEA4C89), modifier = Modifier.size(16.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = "Premium Studio Spaces",
                        style = Typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                    )
                    Text(
                        text = "Book world-class Indian recording studios & live rehearsal spaces in interactive real-time steps.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // Contacts Friends Sync card
            val syncScope = rememberCoroutineScope()
            var hasSyncedContacts by remember { mutableStateOf(false) }
            var isSyncingContacts by remember { mutableStateOf(false) }
            val contactsPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                isSyncingContacts = true
                if (isGranted) {
                    Toast.makeText(context, "Contacts permission granted! Scanning system contacts...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Permission denied. Displaying system matched email contacts fallback.", Toast.LENGTH_SHORT).show()
                }
                syncScope.launch {
                    kotlinx.coroutines.delay(1800) // Beautiful 1.8 seconds skeleton simulation!
                    hasSyncedContacts = true
                    isSyncingContacts = false
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2)),
                shape = RoundedCornerShape(Dimens.r2),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👥 Friends Already on MusicLinkd",
                            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = BrandTextPrimary)
                        )
                        if (hasSyncedContacts) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(BrandSuccess.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Synced", color = BrandSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.sp1))
                    Text(
                        text = "Scan your device contacts or linked email accounts to instantly find fellow musicians already using the system.",
                        color = BrandTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!hasSyncedContacts) {
                        if (isSyncingContacts) {
                            Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                                repeat(3) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            com.example.onboarding.ui.components.SkeletonCircle(size = 36.dp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                com.example.onboarding.ui.components.SkeletonItem(width = 100.dp, height = 12.dp)
                                                com.example.onboarding.ui.components.SkeletonItem(width = 150.dp, height = 10.dp)
                                            }
                                        }
                                        com.example.onboarding.ui.components.SkeletonItem(width = 60.dp, height = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    contactsPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                            ) {
                                Text("Sync Contacts", style = Typography.bodyLarge, color = Color.White)
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            HorizontalDivider(color = BrandBorder)
                            
                            data class ContactRecord(val name: String, val email: String, val desc: String, val imageUrl: String)
                            val matchedContacts = listOf(
                                ContactRecord("A.R. Rahman", "ar.rahman@gmail.com", "Composer • Chennai", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80"),
                                ContactRecord("Pritam Chakraborty", "pritam.music@gmail.com", "Producer • Mumbai", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=200&auto=format&fit=crop&q=80"),
                                ContactRecord("Shreya Ghoshal", "shreya.vocalist@gmail.com", "Singer • Mumbai", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&auto=format&fit=crop&q=80"),
                                ContactRecord("Anirudh Ravichander", "anirudh.r@gmail.com", "Composer • Chennai", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80")
                            )

                            matchedContacts.forEach { contact ->
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
                                                .background(BrandBorder),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = contact.imageUrl,
                                                contentDescription = contact.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(contact.name, style = Typography.bodyLarge, color = BrandTextPrimary)
                                            Text(contact.desc + " • " + contact.email, fontSize = 10.sp, color = BrandTextSecondary)
                                        }
                                    }
                                    
                                    var connected by remember { mutableStateOf(false) }
                                    OutlinedButton(
                                        onClick = { connected = !connected },
                                        shape = RoundedCornerShape(Dimens.r3),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (connected) BrandSuccess else BrandPrimary
                                        ),
                                        border = BorderStroke(1.dp, if (connected) BrandSuccess else BrandPrimary)
                                    ) {
                                        Text(if (connected) { "Connected" } else { "Connect" }, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp3))

            // 3. Recommended Connections (Quick connect slider)
            Text(
                text = "Recommended Connections",
                style = Typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary,
                    style = Typography.bodyLarge
                ),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)
            ) {
                items(recommendedConnections) { item ->
                    val isRequested = connectionRequestsSent[item.name] ?: false
                    
                    Card(
                        modifier = Modifier
                            .width(170.dp)
                            .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2)),
                        shape = RoundedCornerShape(Dimens.r2),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar image bubble
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(BrandBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                color = BrandTextPrimary,
                                fontSize = 13.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = item.role,
                                color = BrandPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = "${item.mutuals} mutual music links",
                                color = BrandTextSecondary,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.sp2))

                            Button(
                                onClick = {
                                    val currentMap = connectionRequestsSent.toMutableMap()
                                    currentMap[item.name] = !isRequested
                                    connectionRequestsSent = currentMap
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .testTag("dash_connect_${item.name}"),
                                contentPadding = PaddingValues(0.dp),
                                shape = RoundedCornerShape(Dimens.r1),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRequested) Color(0xFFE8F0FE) else BrandPrimary,
                                    contentColor = if (isRequested) BrandPrimary else Color.White
                                )
                            ) {
                                Text(
                                    text = if (isRequested) "Requested ✓" else "Connect",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp3))

            // 4. Trending Opportunities
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trending Opportunities",
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandTextPrimary,
                        style = Typography.bodyLarge
                    )
                )
                Spacer(modifier = Modifier.width(Dimens.sp1))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrandPrimary.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("COMING SOON", color = BrandPrimary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                trendingOpportunities.forEach { opp ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2)),
                        shape = RoundedCornerShape(Dimens.r2),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = opp.title,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandTextPrimary,
                                        fontSize = 13.sp
                                    )
                                    if (opp.matches) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(BrandPrimary.copy(alpha = 0.08f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("Match 🌟", color = BrandPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(
                                    text = "${opp.poster} • ${opp.type}",
                                    color = BrandTextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = opp.pay,
                                    color = BrandSuccess,
                                    style = Typography.bodyLarge,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            
                            Button(
                                onClick = { selectedOpportunityForDetails = opp },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(Dimens.r1),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp3))

            // 5. Nearby Registered Record Studios
            Text(
                text = "Nearby Verified Studios",
                style = Typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary,
                    style = Typography.bodyLarge
                ),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)
            ) {
                items(nearbyStudios) { studio ->
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2)),
                        shape = RoundedCornerShape(Dimens.r2),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(Dimens.sp2)) {
                            AsyncImage(
                                model = studio.imageUrl,
                                contentDescription = studio.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(Dimens.r1))
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            
                            Text(
                                text = studio.name,
                                fontWeight = FontWeight.Bold,
                                color = BrandTextPrimary,
                                style = Typography.labelLarge,
                                maxLines = 1
                            )
                            Text(
                                text = "📍 ${studio.location}",
                                color = BrandTextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = studio.rate,
                                    color = BrandPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = studio.rating,
                                    color = Color(0xFFF57C00),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Opportunities coming soon dialog overlay
            if (selectedOpportunityForDetails != null) {
                val opp = selectedOpportunityForDetails!!
                AlertDialog(
                    onDismissRequest = { selectedOpportunityForDetails = null },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Apply to ${opp.poster}", style = Typography.bodyLarge)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BrandPrimary.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("BETA", color = BrandPrimary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                            Text(
                                text = "Opportunity: ${opp.title}",
                                style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = BrandTextPrimary, fontSize = 13.sp)
                            )
                            Text(
                                text = "Type: ${opp.type} • Reward: ${opp.pay}",
                                fontSize = 11.sp,
                                color = BrandSuccess,
                                fontWeight = FontWeight.Bold
                            )
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            
                            Text(
                                text = "Opportunities are coming soon! We are finalizing direct contracting channels with premier Indian labels.",
                                fontSize = 11.sp,
                                color = BrandTextSecondary,
                                lineHeight = 16.sp
                            )
                            
                            Text(
                                text = "Do you want to toggle automatic gig notifications for your registered role?",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandTextPrimary
                            )
                            
                            OutlinedTextField(
                                value = alertEmail,
                                onValueChange = { alertEmail = it },
                                label = { Text("Alert Email Address") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(Dimens.r1)
                            )
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = optInAlerts,
                                    onCheckedChange = { optInAlerts = it },
                                    colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                                )
                                Text("Notify me of new lead matches instantly", fontSize = 10.sp, color = BrandTextSecondary)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                selectedOpportunityForDetails = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text("Activate Alerts", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedOpportunityForDetails = null }) {
                            Text("Dismiss", color = BrandTextSecondary)
                        }
                    },
                    shape = RoundedCornerShape(Dimens.r2),
                    containerColor = Color.White
                )
            }
        } // End Home Column
    } // End else
    } // End Box weight(1f)
    } // End wrapper Column
    } // End Scaffold content

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = BrandTextPrimary)
                    )
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = { notifications.clear() }) {
                            Text("Clear all", fontSize = 11.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            text = {
                if (notifications.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(Dimens.sp1))
                        Text(
                            text = "You are all caught up!",
                            color = BrandTextSecondary,
                            style = Typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(notifications) { notification ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BrandBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(Dimens.sp2),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(BrandPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = notification,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        color = BrandTextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Close", color = Color.White, style = Typography.labelLarge)
                }
            },
            containerColor = Color.White
        )
    }
} // End fun

data class MarketplaceItem(
    val id: String,
    val name: String,
    val category: String, // "sale" or "rental"
    val price: String,
    val deposit: String? = null,
    val condition: String? = null,
    val location: String,
    val owner: String,
    val ownerType: String, // "professional" or "business"
    val rating: String,
    val audioDemo: Boolean = false,
    val keySpec: String,
    val imageEmoji: String,
    val imageUrl: String? = null
)

@Composable
fun MarketplaceBetaScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val marketplaceItems = listOf(
        MarketplaceItem(
            id = "hs8",
            name = "Yamaha HS8 Active Studio Monitors",
            category = "sale",
            price = "₹42,000",
            condition = "Excellent Condition",
            location = "Mumbai, MH",
            owner = "Arjun Mehta",
            ownerType = "professional",
            rating = "4.9",
            audioDemo = true,
            keySpec = "Bi-amplified studio monitor, 8\" cone woofer, 1\" dome tweeter, 38Hz - 30kHz frequency response.",
            imageEmoji = "🔊",
            imageUrl = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=300&q=80"
        ),
        MarketplaceItem(
            id = "u87",
            name = "Neumann U87 Ai Condenser Microphone",
            category = "sale",
            price = "₹1,85,000",
            condition = "Mint Condition",
            location = "Delhi, NCR",
            owner = "Yash Raj Studios",
            ownerType = "business",
            rating = "5.0",
            audioDemo = false,
            keySpec = "Large-diaphragm studio condenser mic, 3 polar patterns (omni, cardioid, figure-8), low noise floor.",
            imageEmoji = "🎙️",
            imageUrl = "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?auto=format&fit=crop&w=300&q=80"
        ),
        MarketplaceItem(
            id = "focusrite",
            name = "Focusrite Scarlett 2i2 USB Interface",
            category = "sale",
            price = "₹8,500",
            condition = "Like New",
            location = "Bangalore, KA",
            owner = "Rahul Sharma",
            ownerType = "professional",
            rating = "4.8",
            audioDemo = false,
            keySpec = "2-in/2-out USB audio interface with two natural-sounding Scarlett mic preamps, 24-bit/192kHz resolution.",
            imageEmoji = "🎛️",
            imageUrl = "https://images.unsplash.com/photo-1598653222000-6b7b7a552625?auto=format&fit=crop&w=300&q=80"
        ),
        MarketplaceItem(
            id = "pearl_kit",
            name = "Pearl Export 5-Piece Drum Kit",
            category = "rental",
            price = "₹2,500 / day",
            deposit = "₹10,000 security deposit",
            location = "Mumbai, MH",
            owner = "Blue Sitar Venues",
            ownerType = "business",
            rating = "4.7",
            keySpec = "Mahogany/Poplar shells, Sabian SBR cymbals package, complete hardware backline kit.",
            imageEmoji = "🥁",
            imageUrl = "https://images.unsplash.com/photo-1519892300165-cb5542fb47c7?auto=format&fit=crop&w=300&q=80"
        ),
        MarketplaceItem(
            id = "ampeg_svt",
            name = "Ampeg SVT Classic Bass Amplifier Head",
            category = "rental",
            price = "₹1,500 / day",
            deposit = "₹5,000 security deposit",
            location = "Pune, MH",
            owner = "Sound Solutions",
            ownerType = "business",
            rating = "4.9",
            keySpec = "300W all-tube bass head, 12AX7 and 12AU7 pre-amp tubes, legendary rock SVT tone.",
            imageEmoji = "🎸",
            imageUrl = "https://images.unsplash.com/photo-1557431177-36141475c676?auto=format&fit=crop&w=300&q=80"
        ),
        MarketplaceItem(
            id = "dolby_mix_srv",
            name = "Dolby Atmos 7.1.4 Mixing & Stem Mastering",
            category = "mixing",
            price = "₹12,000 / track",
            location = "Mumbai, MH",
            owner = "SoundLab Dolby Certified",
            ownerType = "business",
            rating = "5.0",
            audioDemo = true,
            keySpec = "Full Dolby Atmos ADM BWF export, IPRS registered stem mastering, 3 revisions included.",
            imageEmoji = "🎚️",
            imageUrl = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=300&q=80"
        ),
        MarketplaceItem(
            id = "hiphop_beat_pack",
            name = "Exclusive Trap / Lofi Beat Instrumental License",
            category = "beats",
            price = "₹4,500",
            location = "Kolkata, WB",
            owner = "BeatCraft Studios",
            ownerType = "professional",
            rating = "4.9",
            audioDemo = true,
            keySpec = "Untagged WAV + Stems, Unlimited Commercial Streaming, Royalty-Free Copyright clearance.",
            imageEmoji = "🎹",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=300&q=80"
        ),
        MarketplaceItem(
            id = "classical_vocals",
            name = "Hindustani Classical & Sufi Vocal Recording",
            category = "services",
            price = "₹6,000 / session",
            location = "Delhi, NCR",
            owner = "Sunidhi Raga",
            ownerType = "professional",
            rating = "5.0",
            audioDemo = true,
            keySpec = "Lead & Backing Harmony vocals, Neumann U87 recorded in treated acoustic vocal booth.",
            imageEmoji = "🎤",
            imageUrl = "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=300&q=80"
        )
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("all") } // "all", "sale", "rental", "mixing", "beats", "services"
    var selectedItem by remember { mutableStateOf<MarketplaceItem?>(null) }
    var successBookingMsg by remember { mutableStateOf("") }

    val filteredItems = marketplaceItems.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) || item.keySpec.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            "sale" -> item.category == "sale"
            "rental" -> item.category == "rental"
            "mixing" -> item.category == "mixing"
            "beats" -> item.category == "beats"
            "services" -> item.category == "services"
            else -> true
        }
        matchesSearch && matchesTab
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .statusBarsPadding()
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(Dimens.sp1))
            Column {
                Text(
                    text = "Creator Marketplace",
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandTextPrimary
                    )
                )
                Text(
                    text = "Gear, Mixing, Beats, Session Vocalists & Studio Hire",
                    style = Typography.bodyLarge.copy(color = BrandTextSecondary)
                )
            }
        }

        // Inline Success Notification Banner
        AnimatedVisibility(
            visible = successBookingMsg.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(Dimens.r2),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA))
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.sp2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF137333))
                    Spacer(modifier = Modifier.width(Dimens.sp2))
                    Text(successBookingMsg, color = Color(0xFF137333), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { successBookingMsg = "" }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF137333), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Search Bar & Filters Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
        ) {
            // Outlined Search
            UnifiedSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search gear, mixing, vocals, beats..."
            )

            // Horizontal Scrollable Filter Tabs
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
            ) {
                val tabs = listOf(
                    Triple("all", "All Services & Gear", Icons.Default.AllInclusive),
                    Triple("sale", "Buy / Sell Gear", Icons.Default.ShoppingBag),
                    Triple("rental", "Gear Rentals", Icons.Default.CalendarMonth),
                    Triple("mixing", "Dolby Mixing", Icons.Default.Tune),
                    Triple("beats", "Beats & Stems", Icons.Default.GraphicEq),
                    Triple("services", "Session Vocalists", Icons.Default.Mic)
                )

                items(tabs) { (id, label, icon) ->
                    UnifiedChip(
                        text = label,
                        selected = selectedTab == id,
                        onClick = { selectedTab = id }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.sp2))

        // Grid/List of items
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
        ) {
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        title = "No listings found",
                        description = "Try adjusting your search filters or browse other categories.",
                        emoji = "🔍",
                        actionLabel = "Clear Search",
                        onActionClick = { searchQuery = "" }
                    )
                }
            } else {
                filteredItems.forEach { item ->
                    UnifiedMarketplaceCard(
                        name = item.name,
                        price = item.price,
                        condition = item.condition,
                        deposit = item.deposit,
                        location = item.location,
                        owner = item.owner,
                        ownerType = item.ownerType,
                        imageUrl = item.imageUrl,
                        category = item.category,
                        audioDemo = item.audioDemo,
                        onClick = { selectedItem = item },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp3))
        }
    }

    // Detail Dialog Sheet
    if (selectedItem != null) {
        val item = selectedItem!!
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.imageUrl != null) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(Dimens.r1))
                        )
                    } else {
                        Text(item.imageEmoji, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(Dimens.sp2))
                    Text(
                        text = item.name,
                        style = Typography.bodyLarge,
                        color = BrandTextPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Price", fontSize = 11.sp, color = BrandTextSecondary)
                            Text(item.price, style = Typography.bodyLarge, color = BrandPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Location", fontSize = 11.sp, color = BrandTextSecondary)
                            Text(item.location, style = Typography.bodyLarge)
                        }
                    }

                    HorizontalDivider(color = BrandBorder)

                    Text(
                        text = "Technical Specifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandTextPrimary
                    )
                    Text(
                        text = item.keySpec,
                        style = Typography.labelLarge,
                        color = BrandTextSecondary,
                        lineHeight = 18.sp
                    )

                    HorizontalDivider(color = BrandBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(BrandPrimary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.owner.take(1), fontWeight = FontWeight.Bold, color = BrandPrimary)
                        }
                        Spacer(modifier = Modifier.width(Dimens.sp2))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.owner, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(Dimens.sp1))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = if (item.ownerType == "business") Color(0xFFF59E0B) else BrandPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = if (item.ownerType == "business") "Verified Business Partner" else "Verified Professional",
                                fontSize = 11.sp,
                                color = BrandTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(item.rating, style = Typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        successBookingMsg = if (item.category == "sale") {
                            "🎉 Buy Inquiry Sent! Arjun from MusicLinkd support has flagged this. ${item.owner} has been notified."
                        } else {
                            "🎉 Rental Request Dispatched! Rental deposit checkout of ${item.price.take(6)} is initialized under verification ID #REN-5982."
                        }
                        selectedItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(Dimens.r2)
                ) {
                    Text(
                        text = if (item.category == "sale") "Contact Seller & Secure" else "Rent This Gear",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItem = null }) {
                    Text("Dismiss", color = BrandTextSecondary)
                }
            },
            shape = RoundedCornerShape(Dimens.r3),
            containerColor = Color.White
        )
    }
}

// Conversation & Message Models
data class ConversationItem(
    val id: String,
    val name: String,
    val role: String,
    val initialMsg: String,
    val timestamp: String,
    val unread: Boolean,
    val avatarColor: Color,
    val availability: String,
    val pendingOffer: ContractOffer? = null
)

data class ContractOffer(
    val title: String,
    val description: String,
    val budget: String,
    var status: String // "pending", "accepted", "declined"
)

data class ChatMessage(
    val sender: String,
    val text: String,
    val time: String,
    val isMe: Boolean
)

@Composable
fun MessagesComingSoonScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Flow5MessagingWorkspace(
        onBack = onBackClick,
        modifier = modifier
    )
}

// ============================================================================
// FIRESTORE NOTIFICATIONS CENTER SCREEN & PREFERENCES
// ============================================================================

data class AppNotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timestamp: String,
    val category: String, // "bookings", "messages", "system", "reviews"
    val isRead: Boolean = false,
    val sourceName: String = "MusicLinkd System",
    val iconEmoji: String = "🔔"
)

data class PushNotificationPreferences(
    val masterPushEnabled: Boolean = true,
    val allowMessages: Boolean = true,
    val allowCollaborations: Boolean = true,
    val allowBookings: Boolean = true,
    val allowReviews: Boolean = true,
    val allowSystem: Boolean = true,
    val allowTrending: Boolean = true,
    val soundAndVibration: Boolean = true,
    val appBadge: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsCenterScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("all") } // "all", "bookings", "messages", "system"
    var searchQuery by remember { mutableStateOf("") }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var pushPreferences by remember { mutableStateOf(PushNotificationPreferences()) }

    if (showSettingsScreen) {
        NotificationSettingsScreen(
            onBackClick = { showSettingsScreen = false },
            preferences = pushPreferences,
            onUpdatePreferences = { pushPreferences = it }
        )
        return
    }

    val notificationsList = remember {
        mutableStateListOf(
            AppNotificationItem(
                id = "n1",
                title = "Studio Booking Approved 🎉",
                body = "Blue Room Mixing Suite (Mumbai) confirmed your booking request for July 12, 14:00 - 18:00.",
                timestamp = "10 mins ago",
                category = "bookings",
                isRead = false,
                sourceName = "SoundLab Mumbai",
                iconEmoji = "🎙️"
            ),
            AppNotificationItem(
                id = "n2",
                title = "New Collaboration Request",
                body = "Aria Vox sent an audio track collaboration offer for single 'Swar Sandhya'. Tap to listen.",
                timestamp = "1 hour ago",
                category = "messages",
                isRead = false,
                sourceName = "Aria Vox (Sufi Vocalist)",
                iconEmoji = "🎵"
            ),
            AppNotificationItem(
                id = "n3",
                title = "New 5-Star Review Received ⭐",
                body = "\"Exceptional mix quality and super prompt communication on the Dolby Atmos stems.\" - Kabir Singh",
                timestamp = "3 hours ago",
                category = "reviews",
                isRead = true,
                sourceName = "Kabir Singh (Producer)",
                iconEmoji = "⭐"
            ),
            AppNotificationItem(
                id = "n4",
                title = "Firestore Real-Time Alert",
                body = "Your user verification status was updated to 'Verified Industry Professional' in Firestore.",
                timestamp = "Yesterday",
                category = "system",
                isRead = true,
                sourceName = "Firestore Auth System",
                iconEmoji = "⚡"
            ),
            AppNotificationItem(
                id = "n5",
                title = "Trending Opportunity Alert",
                body = "3 new audition posts matching your primary role 'Sound Designer' were posted in Delhi NCR.",
                timestamp = "2 days ago",
                category = "system",
                isRead = true,
                sourceName = "MusicLinkd Match Engine",
                iconEmoji = "🔥"
            )
        )
    }

    val filteredNotifications = notificationsList.filter { item ->
        val allowedByPrefs = if (!pushPreferences.masterPushEnabled) {
            false
        } else {
            when (item.category) {
                "messages" -> pushPreferences.allowMessages
                "bookings" -> pushPreferences.allowBookings
                "reviews" -> pushPreferences.allowReviews
                "system" -> pushPreferences.allowSystem
                else -> true
            }
        }
        val matchesCategory = when (selectedCategory) {
            "bookings" -> item.category == "bookings"
            "messages" -> item.category == "messages"
            "system" -> item.category == "system" || item.category == "reviews"
            else -> true
        }
        val matchesSearch = searchQuery.isEmpty() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.body.contains(searchQuery, ignoreCase = true) ||
                item.sourceName.contains(searchQuery, ignoreCase = true)
        allowedByPrefs && matchesCategory && matchesSearch
    }

    val unreadCount = notificationsList.count { !it.isRead }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingNotifications by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoadingNotifications = true
        kotlinx.coroutines.delay(600)
        isLoadingNotifications = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Notifications Center",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = BrandTextPrimary
                            )
                            if (unreadCount > 0) {
                                Spacer(modifier = Modifier.width(Dimens.sp1))
                                Surface(
                                    color = BrandPrimary,
                                    shape = RoundedCornerShape(Dimens.r2)
                                ) {
                                    Text(
                                        text = "$unreadCount new",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Real-time updates, booking alerts & messages",
                            fontSize = 11.sp,
                            color = BrandTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrandTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsScreen = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Notification Settings", tint = BrandTextPrimary)
                    }
                    if (notificationsList.isNotEmpty()) {
                        IconButton(onClick = {
                            notificationsList.clear()
                            Toast.makeText(context, "All notifications cleared", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Clear All", tint = Color.Red.copy(alpha = 0.7f))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BrandBackground,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Push Preference Warning Banner if Master Toggle is OFF
            if (!pushPreferences.masterPushEnabled) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCD34D)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.sp2),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Push alerts are currently muted in Settings.",
                                style = Typography.labelLarge,
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        TextButton(onClick = { showSettingsScreen = true }) {
                            Text("Configure", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                    }
                }
            }

            // Firestore Sync Indicator Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.sp2),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Firestore Real-time Sync Active",
                                style = Typography.bodyLarge,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "Listening to users/notifications collection & FCM push gateway",
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    TextButton(onClick = {
                        val newAlert = AppNotificationItem(
                            id = "fcm_${System.currentTimeMillis()}",
                            title = "Incoming Firestore Push Alert",
                            body = "Sound Studio Mumbai sent a booking request confirmation for tomorrow at 11:00 AM.",
                            timestamp = "Just now",
                            category = "bookings",
                            isRead = false,
                            sourceName = "Cloud FCM Service",
                            iconEmoji = "⚡"
                        )
                        notificationsList.add(0, newAlert)
                        Toast.makeText(context, "New Firestore Push Notification synced!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("+ Test Sync Alert", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // Search Bar
            UnifiedSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search notification alerts..."
            )

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // Filter Tabs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.sp1),
                    modifier = Modifier.weight(1f)
                ) {
                    val tabs = listOf(
                        Triple("all", "All Alerts", Icons.Default.AllInclusive),
                        Triple("bookings", "Bookings", Icons.Default.CalendarMonth),
                        Triple("messages", "Messages", Icons.Default.Chat),
                        Triple("system", "System", Icons.Default.Info)
                    )

                    items(tabs) { (id, label, icon) ->
                        UnifiedChip(
                            text = label,
                            selected = selectedCategory == id,
                            onClick = { selectedCategory = id }
                        )
                    }
                }

                if (unreadCount > 0) {
                    TextButton(onClick = {
                        val updated = notificationsList.map { it.copy(isRead = true) }
                        notificationsList.clear()
                        notificationsList.addAll(updated)
                    }) {
                        Text("Mark all read", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // Pull-To-Refresh Container for Notifications List
            androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        kotlinx.coroutines.delay(1200)
                        val refreshedAlert = AppNotificationItem(
                            id = "fcm_ptr_${System.currentTimeMillis()}",
                            title = "Firestore Pull-to-Refresh Sync",
                            body = "Refreshed latest alerts & live studio updates from Firestore database.",
                            timestamp = "Just now",
                            category = "system",
                            isRead = false,
                            sourceName = "Firestore Live Database",
                            iconEmoji = "🔄"
                        )
                        notificationsList.add(0, refreshedAlert)
                        isRefreshing = false
                        Toast.makeText(context, "Notifications refreshed from Firestore!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isLoadingNotifications) {
                    com.example.onboarding.ui.components.NotificationListSkeletonLoader(count = 4)
                } else if (filteredNotifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        com.example.onboarding.ui.components.IllustratedEmptyStateView(
                            badgeText = "Notifications Center",
                            title = if (searchQuery.isNotEmpty()) "No Matching Notifications" else "No Notifications Yet",
                            description = if (!pushPreferences.masterPushEnabled)
                                "Push notifications are currently disabled in settings. Enable them to receive live studio booking updates and chat alerts."
                            else if (searchQuery.isNotEmpty())
                                "No notification alerts match \"$searchQuery\". Try adjusting your search query or switching tabs."
                            else
                                "Your inbox is clean! When you receive studio booking confirmations, direct messages, or review requests, they will show up here.",
                            icon = Icons.Default.NotificationsNone,
                            illustrationEmoji = "🔔",
                            primaryActionLabel = if (searchQuery.isNotEmpty()) "Clear Search Filter" else "Simulate Live Push Alert",
                            onPrimaryAction = {
                                if (searchQuery.isNotEmpty()) {
                                    searchQuery = ""
                                } else {
                                    val newAlert = AppNotificationItem(
                                        id = "fcm_${System.currentTimeMillis()}",
                                        title = "Incoming Firestore Push Alert",
                                        body = "Sound Studio Mumbai sent a booking request confirmation for tomorrow at 11:00 AM.",
                                        timestamp = "Just now",
                                        category = "bookings",
                                        isRead = false,
                                        sourceName = "Cloud FCM Service",
                                        iconEmoji = "⚡"
                                    )
                                    notificationsList.add(0, newAlert)
                                    Toast.makeText(context, "Simulated new push notification alert!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.sp2),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredNotifications, key = { it.id }) { item ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    if (dismissValue == SwipeToDismissBoxValue.StartToEnd || dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                        notificationsList.removeIf { it.id == item.id }
                                        Toast.makeText(context, "Notification dismissed", Toast.LENGTH_SHORT).show()
                                        true
                                    } else false
                                }
                            )

                            androidx.compose.animation.AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                                        expandVertically() +
                                        slideInVertically(initialOffsetY = { -30 }),
                                exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessHigh)) +
                                       shrinkVertically() +
                                       slideOutVertically(targetOffsetY = { -30 }),
                                modifier = Modifier.animateItem()
                            ) {
                                SwipeToDismissBox(
                                    state = dismissState,
                                    backgroundContent = {
                                        val isDismissing = dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd || dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
                                        val bgColor = if (isDismissing) Color(0xFFEF4444) else Color.Transparent

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(Dimens.r2))
                                                .background(bgColor)
                                                .padding(horizontal = 20.dp),
                                            contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                                                Alignment.CenterStart else Alignment.CenterEnd
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Swipe to dismiss notification",
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Dismissed",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (!item.isRead) Color(0xFFF3F4F6) else Color.White
                                        ),
                                        shape = RoundedCornerShape(Dimens.r2),
                                        border = BorderStroke(
                                            width = if (!item.isRead) 1.5.dp else 1.dp,
                                            color = if (!item.isRead) BrandPrimary.copy(alpha = 0.4f) else BrandBorder
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(item.iconEmoji, style = Typography.headlineLarge)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = item.title,
                                                        style = Typography.bodyLarge,
                                                        color = BrandTextPrimary
                                                    )
                                                    Text(
                                                        text = item.sourceName + " • " + item.timestamp,
                                                        fontSize = 10.5.sp,
                                                        color = BrandTextSecondary
                                                    )
                                                }
                                            }

                                            if (!item.isRead) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(BrandPrimary)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(Dimens.sp1))

                                        Text(
                                            text = item.body,
                                            fontSize = 12.5.sp,
                                            lineHeight = 17.sp,
                                            color = BrandTextPrimary
                                        )

                                        Spacer(modifier = Modifier.height(Dimens.sp2))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    val idx = notificationsList.indexOfFirst { it.id == item.id }
                                                    if (idx != -1) {
                                                        notificationsList[idx] = notificationsList[idx].copy(isRead = !notificationsList[idx].isRead)
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = if (item.isRead) "Mark Unread" else "Mark Read",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = BrandTextSecondary
                                                )
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                                                IconButton(
                                                    onClick = {
                                                        notificationsList.removeIf { it.id == item.id }
                                                        Toast.makeText(context, "Notification deleted", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.DeleteOutline, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                                }

                                                Button(
                                                    onClick = {
                                                        Toast.makeText(context, "Opening ${item.title}...", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Text("View Details", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    preferences: PushNotificationPreferences,
    onUpdatePreferences: (PushNotificationPreferences) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentPrefs by remember { mutableStateOf(preferences) }
    var isSaving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Notification Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = BrandTextPrimary
                        )
                        Text(
                            text = "Manage FCM gateway & alert preferences",
                            fontSize = 11.sp,
                            color = BrandTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BrandTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        currentPrefs = PushNotificationPreferences()
                        onUpdatePreferences(currentPrefs)
                        Toast.makeText(context, "Preferences reset to default", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Defaults", tint = BrandTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BrandBackground,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
        ) {
            // Master Switch Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (currentPrefs.masterPushEnabled) Color(0xFFEFF6FF) else Color(0xFFFEF2F2)
                ),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(
                    1.dp,
                    if (currentPrefs.masterPushEnabled) Color(0xFFBFDBFE) else Color(0xFFFECACA)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (currentPrefs.masterPushEnabled) BrandPrimary else Color.Gray,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (currentPrefs.masterPushEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(Dimens.sp2))
                        Column {
                            Text(
                                text = "Master Push Gateway",
                                style = Typography.bodyLarge,
                                color = BrandTextPrimary
                            )
                            Text(
                                text = if (currentPrefs.masterPushEnabled)
                                    "FCM real-time push channel active"
                                else
                                    "All push notifications disabled",
                                fontSize = 11.5.sp,
                                color = BrandTextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = currentPrefs.masterPushEnabled,
                        onCheckedChange = { isChecked ->
                            currentPrefs = currentPrefs.copy(masterPushEnabled = isChecked)
                            onUpdatePreferences(currentPrefs)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandPrimary
                        )
                    )
                }
            }

            // Categories Preference Section
            Text(
                text = "Alert Categories & Channels",
                style = Typography.bodyLarge,
                color = BrandTextPrimary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BrandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    NotificationToggleRow(
                        title = "Direct Messages & Chats",
                        subtitle = "Instant push when artists or sound directors text you",
                        icon = Icons.Default.Chat,
                        checked = currentPrefs.allowMessages && currentPrefs.masterPushEnabled,
                        enabled = currentPrefs.masterPushEnabled,
                        onCheckedChange = {
                            currentPrefs = currentPrefs.copy(allowMessages = it)
                            onUpdatePreferences(currentPrefs)
                        }
                    )
                    HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                    NotificationToggleRow(
                        title = "Collaboration Offers",
                        subtitle = "Alerts for track collaboration requests & stem shares",
                        icon = Icons.Default.Group,
                        checked = currentPrefs.allowCollaborations && currentPrefs.masterPushEnabled,
                        enabled = currentPrefs.masterPushEnabled,
                        onCheckedChange = {
                            currentPrefs = currentPrefs.copy(allowCollaborations = it)
                            onUpdatePreferences(currentPrefs)
                        }
                    )
                    HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                    NotificationToggleRow(
                        title = "Studio & Session Bookings",
                        subtitle = "Approvals, slot changes & booking confirmations",
                        icon = Icons.Default.CalendarMonth,
                        checked = currentPrefs.allowBookings && currentPrefs.masterPushEnabled,
                        enabled = currentPrefs.masterPushEnabled,
                        onCheckedChange = {
                            currentPrefs = currentPrefs.copy(allowBookings = it)
                            onUpdatePreferences(currentPrefs)
                        }
                    )
                    HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                    NotificationToggleRow(
                        title = "Ratings & Client Reviews",
                        subtitle = "Get notified when clients leave feedback or star ratings",
                        icon = Icons.Default.Star,
                        checked = currentPrefs.allowReviews && currentPrefs.masterPushEnabled,
                        enabled = currentPrefs.masterPushEnabled,
                        onCheckedChange = {
                            currentPrefs = currentPrefs.copy(allowReviews = it)
                            onUpdatePreferences(currentPrefs)
                        }
                    )
                    HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                    NotificationToggleRow(
                        title = "System & Security Verification",
                        subtitle = "Account security alerts & Firestore status updates",
                        icon = Icons.Default.Shield,
                        checked = currentPrefs.allowSystem && currentPrefs.masterPushEnabled,
                        enabled = currentPrefs.masterPushEnabled,
                        onCheckedChange = {
                            currentPrefs = currentPrefs.copy(allowSystem = it)
                            onUpdatePreferences(currentPrefs)
                        }
                    )
                }
            }

            // Sound, Vibration & Quiet Hours Section
            Text(
                text = "Behavior & Quiet Hours",
                style = Typography.bodyLarge,
                color = BrandTextPrimary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BrandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    NotificationToggleRow(
                        title = "Sound & Vibration",
                        subtitle = "Play audio chime and haptic feedback on incoming alerts",
                        icon = Icons.Default.VolumeUp,
                        checked = currentPrefs.soundAndVibration && currentPrefs.masterPushEnabled,
                        enabled = currentPrefs.masterPushEnabled,
                        onCheckedChange = {
                            currentPrefs = currentPrefs.copy(soundAndVibration = it)
                            onUpdatePreferences(currentPrefs)
                        }
                    )
                    HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                    NotificationToggleRow(
                        title = "App Badge Counter",
                        subtitle = "Show unread notification count on app launcher icon",
                        icon = Icons.Default.Badge,
                        checked = currentPrefs.appBadge && currentPrefs.masterPushEnabled,
                        enabled = currentPrefs.masterPushEnabled,
                        onCheckedChange = {
                            currentPrefs = currentPrefs.copy(appBadge = it)
                            onUpdatePreferences(currentPrefs)
                        }
                    )
                    HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

                    NotificationToggleRow(
                        title = "Do Not Disturb (Quiet Hours)",
                        subtitle = "Mute push chimes during nighttime (10:00 PM - 07:00 AM)",
                        icon = Icons.Default.DoNotDisturbOn,
                        checked = currentPrefs.quietHoursEnabled && currentPrefs.masterPushEnabled,
                        enabled = currentPrefs.masterPushEnabled,
                        onCheckedChange = {
                            currentPrefs = currentPrefs.copy(quietHoursEnabled = it)
                            onUpdatePreferences(currentPrefs)
                        }
                    )

                    if (currentPrefs.quietHoursEnabled && currentPrefs.masterPushEnabled) {
                        Surface(
                            color = Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(Dimens.r2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(Dimens.sp2),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Active Quiet Schedule:",
                                    style = Typography.bodyLarge,
                                    color = BrandTextSecondary
                                )
                                Surface(
                                    color = BrandPrimary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(Dimens.r1)
                                ) {
                                    Text(
                                        text = "${currentPrefs.quietHoursStart} - ${currentPrefs.quietHoursEnd}",
                                        style = Typography.bodyLarge,
                                        color = BrandPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp1))

            // Save to Firestore Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        isSaving = true
                        delay(600)
                        isSaving = false
                        onUpdatePreferences(currentPrefs)
                        Toast.makeText(context, "Preferences saved to Firestore user profile!", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Text("Syncing to Firestore...", fontWeight = FontWeight.Bold, color = Color.White)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Text("Save Preferences", style = Typography.bodyLarge, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun NotificationToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) BrandPrimary else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(Dimens.sp2))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = if (enabled) BrandTextPrimary else BrandTextSecondary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = BrandTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandPrimary
            )
        )
    }
}

