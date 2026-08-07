package com.example.onboarding.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onboarding.audio.AudioPlayerManager
import com.example.onboarding.database.MusicLinkdDatabase
import com.example.onboarding.database.ReviewRepository
import kotlinx.coroutines.launch
import com.example.onboarding.ui.components.*

// Color system aligned with MusicLinkd Design tokens
private val BrandPrimary = Color(0xFFEA4C89)      // Primary Pink
private val BrandAccent = Color(0xFFEA4C89)       // Pink Accent
private val BrandDark = Color(0xFF0D0B26)         // Very deep midnight navy
private val BrandSecondaryText = Color(0xFF7D7D8E)// Muted grey
private val BrandBorder = Color(0xFFECECF1)       // Light border gray
private val BrandBackground = Color(0xFFFCFCFD)   // Clean editorial background
private val BrandSuccess = Color(0xFF10B981)      // M3 success green

enum class ReputationSubScreen {
    DASHBOARD,                  // Screen 1
    REVIEW_LIST,                // Screen 2
    REVIEW_DETAIL,              // Screen 3
    WRITE_REVIEW,               // Screen 4
    RATING_BREAKDOWN,           // Screen 5
    COMPLETED_PROJECT_REVIEW,   // Screen 6
    MARKETPLACE_REVIEW,         // Screen 7
    STUDIO_REVIEW,              // Screen 8
    BUSINESS_RESPONSE,          // Screen 9
    REPORT_REVIEW,              // Screen 10
    PUBLIC_REPUTATION           // Screen 11
}

// Struct for mock data reviews
data class ReputationReviewItem(
    val id: String,
    val reviewerName: String,
    val reviewerRole: String,
    val isVerified: Boolean = true,
    val ratingStars: Int,
    val reviewText: String,
    val projectContext: String,
    val date: String,
    val initialHelpfulCount: Int,
    val hasAudioSample: Boolean = false,
    val audioTitle: String = "",
    val hasImages: Boolean = false,
    val businessReplyText: String = ""
)

@Composable
fun ReputationAndReviewsScreenContainer(
    onBackClick: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(ReputationSubScreen.DASHBOARD) }
    var selectedReviewId by remember { mutableStateOf("rev-1") }
    var activeCategoryFilter by remember { mutableStateOf("All") }
    
    val context = LocalContext.current
    val database = remember { MusicLinkdDatabase.getDatabase(context) }
    val repository = remember { ReviewRepository(database.reviewDao()) }
    val dbReviews by repository.allReviews.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    // Seeding with default data if empty to ensure rich offline experience
    LaunchedEffect(dbReviews) {
        if (dbReviews.isEmpty()) {
            repository.insertReviews(listOf(
                ReputationReviewItem(
                    id = "rev-1",
                    reviewerName = "James Miller",
                    reviewerRole = "A&R Universal Records",
                    isVerified = true,
                    ratingStars = 5,
                    reviewText = "The raw vocal tracks Vedanshu recorded are exceptionally clean. The Kemper profile work was masterfully done. Saved us days of EQ in our final studio mix.",
                    projectContext = "Project: Dark Ambient Synth Mastery EP",
                    date = "June 24, 2026",
                    initialHelpfulCount = 24,
                    hasAudioSample = true,
                    audioTitle = "Ambient_Master_V2_Universal.wav",
                    hasImages = true,
                    businessReplyText = "Thanks James! Your synth stems were absolute quality to master. Looking forward to the next EP!"
                ),
                ReputationReviewItem(
                    id = "rev-2",
                    reviewerName = "Sarah Connor",
                    reviewerRole = "Creative Director at Synergy Games",
                    isVerified = true,
                    ratingStars = 5,
                    reviewText = "Incredible cinematic arrangements delivered on extremely tight deadlines. The contract release and communication were flawless.",
                    projectContext = "Project: Cyberpunk 2088 Game Soundtrack",
                    date = "June 18, 2026",
                    initialHelpfulCount = 14,
                    hasAudioSample = false,
                    hasImages = true
                ),
                ReputationReviewItem(
                    id = "rev-3",
                    reviewerName = "Arjun Mehta",
                    reviewerRole = "Composer / Lead Sitarist",
                    isVerified = true,
                    ratingStars = 5,
                    reviewText = "Absolute state of the art workspace! Karan understood the acoustic dimensions of the sitar perfectly. High-end genelec tracking was mind-blowing.",
                    projectContext = "Recording Session: Monsoon Raga Live",
                    date = "June 12, 2026",
                    initialHelpfulCount = 8,
                    hasAudioSample = true,
                    audioTitle = "Sitar_Resonance_Atmos_48k.wav",
                    businessReplyText = "Thank you Arjun! Sourcing those sitar spatial channels was an absolute pleasure. Come back soon!"
                ),
                ReputationReviewItem(
                    id = "rev-4",
                    reviewerName = "Rohan Das",
                    reviewerRole = "Vintage Synth Collector",
                    isVerified = true,
                    ratingStars = 4,
                    reviewText = "Bought a vintage Roland Juno-106. Packaging was solid with thick bubble wrap and condition matches the photos. Delivery took an extra day but communication was great.",
                    projectContext = "Marketplace purchase: Vintage Roland Juno-106",
                    date = "May 28, 2026",
                    initialHelpfulCount = 5,
                    hasImages = true
                )
            ))
        }
    }

    var showSnackbarMessage by remember { mutableStateOf<String?>(null) }
    
    if (showSnackbarMessage != null) {
        LaunchedEffect(showSnackbarMessage) {
            kotlinx.coroutines.delay(2500)
            showSnackbarMessage = null
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, BrandBorder)
            ) {
                // Main top app bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (currentScreen == ReputationSubScreen.DASHBOARD) {
                                    onBackClick()
                                } else {
                                    currentScreen = ReputationSubScreen.DASHBOARD
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = BrandDark)
                        }
                        Spacer(modifier = Modifier.width(Dimens.sp2))
                        Column {
                            Text(
                                text = "REPUTATION HUB",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = when (currentScreen) {
                                    ReputationSubScreen.DASHBOARD -> "Verified Trust & Scorecard"
                                    ReputationSubScreen.REVIEW_LIST -> "Client Review Catalog"
                                    ReputationSubScreen.REVIEW_DETAIL -> "Verified Review Deep-Dive"
                                    ReputationSubScreen.WRITE_REVIEW -> "Draft Client Feedback"
                                    ReputationSubScreen.RATING_BREAKDOWN -> "Analytical Rating Spread"
                                    ReputationSubScreen.COMPLETED_PROJECT_REVIEW -> "Rate Project Collaborator"
                                    ReputationSubScreen.MARKETPLACE_REVIEW -> "Evaluate Gear Transaction"
                                    ReputationSubScreen.STUDIO_REVIEW -> "Rate Recording Facility"
                                    ReputationSubScreen.BUSINESS_RESPONSE -> "Manage Business Reply"
                                    ReputationSubScreen.REPORT_REVIEW -> "Dispute / Report Review"
                                    ReputationSubScreen.PUBLIC_REPUTATION -> "Public Profile Preview widget"
                                },
                                style = Typography.bodyLarge,
                                color = BrandDark
                            )
                        }
                    }

                    // Direct jump to other sub-pages toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(BrandAccent.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("VERIFIED CONTRACT", color = BrandAccent, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                // SCREEN SELECTOR ACCORDION (Ensures all 11 screens of Flow 17 are directly accessible & indexable)
                Text(
                    text = "Flow 17 Interactive Screens Index (Click to jump immediately):",
                    fontSize = 10.sp,
                    color = BrandSecondaryText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.sp1),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        ReputationSubScreen.DASHBOARD to "1. Dashboard",
                        ReputationSubScreen.REVIEW_LIST to "2. Review Feed",
                        ReputationSubScreen.REVIEW_DETAIL to "3. Detail View",
                        ReputationSubScreen.WRITE_REVIEW to "4. Write Review",
                        ReputationSubScreen.RATING_BREAKDOWN to "5. Analytics",
                        ReputationSubScreen.COMPLETED_PROJECT_REVIEW to "6. Rate Collab",
                        ReputationSubScreen.MARKETPLACE_REVIEW to "7. Marketplace",
                        ReputationSubScreen.STUDIO_REVIEW to "8. Studio",
                        ReputationSubScreen.BUSINESS_RESPONSE to "9. Biz Reply",
                        ReputationSubScreen.REPORT_REVIEW to "10. Dispute",
                        ReputationSubScreen.PUBLIC_REPUTATION to "11. Public Embed"
                    ).forEach { (scr, label) ->
                        val isSelected = currentScreen == scr
                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(if (isSelected) BrandDark else BrandBorder.copy(alpha = 0.5f))
                                    .border(1.dp, if (isSelected) BrandDark else BrandBorder, RoundedCornerShape(99.dp))
                                    .clickable { currentScreen = scr }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else BrandDark,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Toast alert overlay
            AnimatedVisibility(
                visible = showSnackbarMessage != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandDark),
                    shape = RoundedCornerShape(Dimens.r2)
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.sp2),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(showSnackbarMessage ?: "", color = Color.White, style = Typography.bodyLarge)
                        Text("✕", color = BrandAccent, style = Typography.bodyLarge, modifier = Modifier.clickable { showSnackbarMessage = null })
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBackground)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ReputationSubScreen.DASHBOARD -> ReputationDashboardScreen(
                    onNavigateTo = { currentScreen = it },
                    reviewsCount = dbReviews.size
                )
                ReputationSubScreen.REVIEW_LIST -> ReviewListScreen(
                    reviews = dbReviews,
                    activeFilter = activeCategoryFilter,
                    onFilterChange = { activeCategoryFilter = it },
                    onSelectReview = { id ->
                        selectedReviewId = id
                        currentScreen = ReputationSubScreen.REVIEW_DETAIL
                    }
                )
                ReputationSubScreen.REVIEW_DETAIL -> {
                    val reviewItem = dbReviews.firstOrNull { it.id == selectedReviewId } 
                        ?: dbReviews.firstOrNull() 
                        ?: ReputationReviewItem("temp", "James Miller", "Reviewer", true, 5, "Fabulous track masters.", "Project", "Today", 0)
                    ReviewDetailScreen(
                        review = reviewItem,
                        onBack = { currentScreen = ReputationSubScreen.REVIEW_LIST },
                        onNavigateTo = { currentScreen = it },
                        onDispute = { currentScreen = ReputationSubScreen.REPORT_REVIEW }
                    )
                }
                ReputationSubScreen.WRITE_REVIEW -> WriteReviewScreen(
                    onSubmitReview = { stars, comments ->
                        val newItem = ReputationReviewItem(
                            id = "rev-${System.currentTimeMillis()}",
                            reviewerName = "You (Verified Buyer)",
                            reviewerRole = "Solo Creator",
                            isVerified = true,
                            ratingStars = stars,
                            reviewText = comments.ifEmpty { "Exceptional workflow, speedy execution, and clean assets provided through modern contracts." },
                            projectContext = "Project: Live Session Gig Review",
                            date = "Today",
                            initialHelpfulCount = 0
                        )
                        coroutineScope.launch {
                            repository.insertReview(newItem)
                        }
                        showSnackbarMessage = "Verified Review Submitted and indexed successfully!"
                        currentScreen = ReputationSubScreen.DASHBOARD
                    }
                )
                ReputationSubScreen.RATING_BREAKDOWN -> RatingBreakdownScreen()
                ReputationSubScreen.COMPLETED_PROJECT_REVIEW -> CompletedProjectReviewScreen(
                    onSubmit = {
                        showSnackbarMessage = "Project Milestone Evaluation finalized!"
                        currentScreen = ReputationSubScreen.DASHBOARD
                    }
                )
                ReputationSubScreen.MARKETPLACE_REVIEW -> MarketplaceReviewScreen(
                    onSubmit = {
                        showSnackbarMessage = "Seller transaction rating broadcasted!"
                        currentScreen = ReputationSubScreen.DASHBOARD
                    }
                )
                ReputationSubScreen.STUDIO_REVIEW -> StudioReviewScreen(
                    onSubmit = {
                        showSnackbarMessage = "Studio acoustics and workflow review saved!"
                        currentScreen = ReputationSubScreen.DASHBOARD
                    }
                )
                ReputationSubScreen.BUSINESS_RESPONSE -> {
                    val reviewItem = dbReviews.firstOrNull { it.id == selectedReviewId } 
                        ?: dbReviews.firstOrNull() 
                        ?: ReputationReviewItem("temp", "James Miller", "Reviewer", true, 5, "Fabulous track masters.", "Project", "Today", 0)
                    BusinessResponseScreen(
                        review = reviewItem,
                        onSubmitResponse = { response ->
                            val updatedItem = reviewItem.copy(businessReplyText = response)
                            coroutineScope.launch {
                                repository.updateReview(updatedItem)
                            }
                            showSnackbarMessage = "Owner response published on public timeline!"
                            currentScreen = ReputationSubScreen.DASHBOARD
                        }
                    )
                }
                ReputationSubScreen.REPORT_REVIEW -> ReportReviewScreen(
                    onSubmitReport = { reason ->
                        showSnackbarMessage = "Review reported under: $reason. Dispute ticket created."
                        currentScreen = ReputationSubScreen.DASHBOARD
                    }
                )
                ReputationSubScreen.PUBLIC_REPUTATION -> PublicReputationWidget(
                    reviews = dbReviews,
                    onNavigateTo = { currentScreen = it }
                )
            }
        }
    }
}

// ==========================================
// SCREEN 1: REPUTATION DASHBOARD
// ==========================================
@Composable
fun ReputationDashboardScreen(
    onNavigateTo: (ReputationSubScreen) -> Unit,
    reviewsCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        // Core Scorecard Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BrandDark),
            shape = RoundedCornerShape(Dimens.r3)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.sp3),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(BrandAccent.copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, null, tint = BrandAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("VERIFIED ACCOUNT TRUST", color = BrandAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.sp2))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("4.9", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.width(Dimens.sp2))
                    Column {
                        Row {
                            repeat(5) {
                                Icon(Icons.Default.Star, null, tint = BrandAccent, modifier = Modifier.size(22.dp))
                            }
                        }
                        Text("out of 5 stars · $reviewsCount reviews", color = Color.White.copy(alpha = 0.7f), style = Typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.sp2))
                Divider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(Dimens.sp2))

                // Score metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("99.2%", color = Color.White, style = Typography.bodyLarge)
                        Text("Reliability", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("98.7%", color = Color.White, style = Typography.bodyLarge)
                        Text("Recommends", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("100%", color = BrandAccent, style = Typography.bodyLarge)
                        Text("Deals Completed", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                    }
                }
            }
        }

        // Stats Ledger Blocks
        Text("Verified Transaction Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                "💼 Projects" to "48 Done",
                "🎙️ Studio Sessions" to "82 Booked",
                "🎹 Marketplace" to "34 Trades"
            ).forEach { (title, count) ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(Dimens.r2)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(title, fontSize = 11.sp, color = BrandSecondaryText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.height(Dimens.sp1))
                        Text(count, style = Typography.bodyLarge, color = BrandDark)
                    }
                }
            }
        }

        // Direct Core CTAs
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandPrimary.copy(alpha = 0.2f), RoundedCornerShape(Dimens.r2)),
            colors = CardDefaults.cardColors(containerColor = BrandPrimary.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(Dimens.r2)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Need to draft verified client feedback?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandPrimary)
                Text("Only users with verified completed contracts can publish stars on the MusicLinkd network.", fontSize = 11.sp, color = BrandSecondaryText, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
                
                Button(
                    onClick = { onNavigateTo(ReputationSubScreen.WRITE_REVIEW) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(99.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Write a Verified Review Now", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // List of other evaluators
        Text("Evaluate Industry Deliverables", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
            listOf(
                "Completed Project" to "Rate studio collaborators, session musicians and bandmates directly." to ReputationSubScreen.COMPLETED_PROJECT_REVIEW,
                "Marketplace Gear Transaction" to "Rate equipment packaging, value accuracy, and synth/pedal conditions." to ReputationSubScreen.MARKETPLACE_REVIEW,
                "Studio Recording Suite" to "Evaluate facilities, clean spaces, local engineers, and acoustics." to ReputationSubScreen.STUDIO_REVIEW
            ).forEach { (pair, actionScr) ->
                val (title, subtitle) = pair
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.r2))
                        .background(Color.White)
                        .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
                        .clickable { onNavigateTo(actionScr) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandAccent.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⭐", style = Typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.width(Dimens.sp2))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                        Text(subtitle, fontSize = 10.5.sp, color = BrandSecondaryText)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = BrandSecondaryText)
                }
            }
        }

        // Quick View Feed Button
        Button(
            onClick = { onNavigateTo(ReputationSubScreen.REVIEW_LIST) },
            colors = ButtonDefaults.buttonColors(containerColor = BrandDark),
            shape = RoundedCornerShape(99.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Open Verified Reviews Feed", style = Typography.bodyLarge)
        }

        OutlinedButton(
            onClick = { onNavigateTo(ReputationSubScreen.RATING_BREAKDOWN) },
            border = BorderStroke(1.dp, BrandBorder),
            shape = RoundedCornerShape(99.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandDark),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("View Rating Trends & Analytics", style = Typography.bodyLarge)
        }
    }
}

// ==========================================
// SCREEN 2: REVIEW LIST
// ==========================================
@Composable
fun ReviewListScreen(
    reviews: List<ReputationReviewItem>,
    activeFilter: String,
    onFilterChange: (String) -> Unit,
    onSelectReview: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Filters
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
        ) {
            listOf("All", "Projects", "Marketplace", "Studios", "Rentals", "Services").forEach { filter ->
                val isSelected = activeFilter == filter
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(if (isSelected) BrandAccent else BrandBorder.copy(alpha = 0.5f))
                            .clickable { onFilterChange(filter) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else BrandDark,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // List Container
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
        ) {
            val filteredReviews = if (activeFilter == "All") {
                reviews
            } else {
                reviews.filter {
                    it.projectContext.contains(activeFilter.removeSuffix("s"), ignoreCase = true)
                }
            }

            if (filteredReviews.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📂", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(Dimens.sp2))
                        Text("No verified reviews under this category yet.", style = Typography.labelLarge, color = BrandSecondaryText, textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(filteredReviews) { item ->
                    var helpfulCount by remember { mutableIntStateOf(item.initialHelpfulCount) }
                    var hasClickedHelpful by remember { mutableStateOf(false) }

                    UnifiedReviewCard(
                        reviewerName = item.reviewerName,
                        reviewerRole = item.reviewerRole,
                        rating = item.ratingStars.toFloat(),
                        reviewText = item.reviewText,
                        date = item.date,
                        helpfulCount = helpfulCount,
                        hasClickedHelpful = hasClickedHelpful,
                        onHelpfulClick = {
                            if (!hasClickedHelpful) {
                                helpfulCount++
                                hasClickedHelpful = true
                            }
                        },
                        modifier = Modifier.clickable { onSelectReview(item.id) }
                    )
                }
            }
        }
    }
}

// ==========================================
// SCREEN 3: REVIEW DETAIL
// ==========================================
@Composable
fun ReviewDetailScreen(
    review: ReputationReviewItem,
    onBack: () -> Unit,
    onNavigateTo: (ReputationSubScreen) -> Unit,
    onDispute: () -> Unit
) {
    val context = LocalContext.current
    val isEnginePlaying by AudioPlayerManager.isPlayingState.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            AudioPlayerManager.stop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r3),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(BrandDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(review.reviewerName.take(1), color = Color.White, style = Typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.width(Dimens.sp2))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(review.reviewerName, style = Typography.bodyLarge, color = BrandDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Verified, "Verified", tint = BrandAccent, modifier = Modifier.size(16.dp))
                        }
                        Text(review.reviewerRole, fontSize = 11.sp, color = BrandSecondaryText)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row {
                    repeat(review.ratingStars) {
                        Icon(Icons.Default.Star, null, tint = BrandAccent, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(review.reviewText, fontSize = 13.sp, color = BrandDark, lineHeight = 18.sp)
                
                Spacer(modifier = Modifier.height(Dimens.sp2))
                Text(review.projectContext, fontSize = 11.sp, color = BrandPrimary, fontWeight = FontWeight.ExtraBold)
                Text("Verified Secure Transaction · Approved: ${review.date}", fontSize = 10.sp, color = BrandSecondaryText, modifier = Modifier.padding(top = 4.dp))
            }
        }

        // Audio showcase sample
        if (review.hasAudioSample) {
            Text("Approved Audio Delivery", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandDark),
                shape = RoundedCornerShape(Dimens.r2)
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.sp2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (isEnginePlaying) {
                                AudioPlayerManager.pause()
                            } else {
                                val testUrl = if (review.audioTitle.contains("Sitar")) {
                                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
                                } else {
                                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                                }
                                AudioPlayerManager.play(context, testUrl, review.audioTitle)
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BrandAccent)
                    ) {
                        Icon(
                            imageVector = if (isEnginePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(Dimens.sp2))
                    Column {
                        Text(review.audioTitle, color = Color.White, style = Typography.bodyLarge)
                        Text("Project milestone audio release", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                    }
                }
            }
        }

        // Business Reply Screen link/display
        Text("Business Reply History", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)
        if (review.businessReplyText.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(Dimens.r2),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(Dimens.sp2)) {
                    Text("Your Business Reply:", style = Typography.bodyLarge, color = BrandPrimary)
                    Text("\"${review.businessReplyText}\"", style = Typography.labelLarge, color = BrandDark, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onNavigateTo(ReputationSubScreen.BUSINESS_RESPONSE) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandDark),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(99.dp)
            ) {
                Text(if (review.businessReplyText.isEmpty()) "Add Public Response" else "Edit Reply", fontSize = 11.5.sp)
            }

            OutlinedButton(
                onClick = onDispute,
                border = BorderStroke(1.dp, BrandBorder),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(99.dp)
            ) {
                Text("Dispute / Report", color = Color.Red, fontSize = 11.5.sp)
            }
        }
    }
}

// ==========================================
// SCREEN 4: WRITE REVIEW
// ==========================================
@Composable
fun WriteReviewScreen(
    onSubmitReview: (Int, String) -> Unit
) {
    var starRating by remember { mutableIntStateOf(5) }
    var professionalismRating by remember { mutableIntStateOf(5) }
    var communicationRating by remember { mutableIntStateOf(5) }
    var qualityRating by remember { mutableIntStateOf(5) }
    var timelinessRating by remember { mutableIntStateOf(5) }
    var valueRating by remember { mutableIntStateOf(5) }
    var commentsText by remember { mutableStateOf("") }
    var recommendToggle by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        Text("Verified Transaction Scorecard", style = Typography.bodyLarge, color = BrandDark)
        
        // Large Overall Star selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r3),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Overall Rating Score", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                Spacer(modifier = Modifier.height(Dimens.sp1))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= starRating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = BrandAccent,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { starRating = i }
                        )
                    }
                }
            }
        }

        // Subcategory rating arrays
        Text("Detailed Performance Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)

        listOf(
            "Professionalism" to professionalismRating to { v: Int -> professionalismRating = v },
            "Communication" to communicationRating to { v: Int -> communicationRating = v },
            "Acoustic/Deliverable Quality" to qualityRating to { v: Int -> qualityRating = v },
            "Timeliness / Milestones" to timelinessRating to { v: Int -> timelinessRating = v },
            "Overall Financial Value" to valueRating to { v: Int -> valueRating = v }
        ).forEach { (pair, setter) ->
            val (label, curVal) = pair
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(Dimens.r2),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, style = Typography.bodyLarge, color = BrandDark)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= curVal) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = BrandAccent,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { setter(i) }
                            )
                        }
                    }
                }
            }
        }

        // Recommend Toggle Option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.r2))
                .background(Color.White)
                .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Would recommend collaborator?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                Text("This feeds into the overall profile recommendation rate.", fontSize = 10.sp, color = BrandSecondaryText)
            }
            Switch(
                checked = recommendToggle,
                onCheckedChange = { recommendToggle = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandAccent)
            )
        }

        // Written description box
        OutlinedTextField(
            value = commentsText,
            onValueChange = { commentsText = it },
            label = { Text("Written Review Comments") },
            placeholder = { Text("Detail your experience with this collaborator...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(Dimens.r2),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorder
            )
        )

        Button(
            onClick = { onSubmitReview(starRating, commentsText) },
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(99.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Publish Verified Feedback", fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// SCREEN 5: RATING BREAKDOWN
// ==========================================
@Composable
fun RatingBreakdownScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        Text("Acoustic Rating Analytics", style = Typography.bodyLarge, color = BrandDark)

        // Custom drawn Rating Distribution bars
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r3),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Star Spread Statistics", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                Spacer(modifier = Modifier.height(Dimens.sp2))

                listOf(
                    "5 ★" to 0.88f to "88%",
                    "4 ★" to 0.09f to "9%",
                    "3 ★" to 0.02f to "2%",
                    "2 ★" to 0.01f to "1%",
                    "1 ★" to 0.00f to "0%"
                ).forEach { (pair, pctLabel) ->
                    val (label, progress) = pair
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp), color = BrandDark)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(99.dp)),
                            color = BrandAccent,
                            trackColor = BrandBorder
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(pctLabel, fontSize = 11.sp, color = BrandSecondaryText, modifier = Modifier.width(32.dp))
                    }
                }
            }
        }

        // Custom drawn Monthly Review Volume graph
        Text("Monthly Verified Activity Trends (2026)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BrandDark),
            shape = RoundedCornerShape(Dimens.r3)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        "Jan" to 12,
                        "Feb" to 18,
                        "Mar" to 22,
                        "Apr" to 34,
                        "May" to 42,
                        "Jun" to 48
                    ).forEach { (month, count) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Text("$count", color = BrandAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height((count * 1.8).dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(BrandPrimary)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(month, color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 6: COMPLETED PROJECT REVIEW
// ==========================================
@Composable
fun CompletedProjectReviewScreen(
    onSubmit: () -> Unit
) {
    var specRating by remember { mutableIntStateOf(5) }
    var pmComments by remember { mutableStateOf("") }
    var recommendPartner by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        Text("Milestone Evaluation Screen", style = Typography.bodyLarge, color = BrandDark)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r3),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Project: Neon Dreamscape Single", style = Typography.bodyLarge, color = BrandDark)
                Text("Collaborators: Rhea Kapoor (Producer)", fontSize = 11.sp, color = BrandSecondaryText, modifier = Modifier.padding(top = 4.dp))
                Text("Approved Deliverables: 5 HQ WAV Stems, Direct Mix", fontSize = 11.sp, color = BrandSecondaryText)
            }
        }

        Text("Acoustic Architecture Score", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= specRating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = BrandAccent,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { specRating = i }
                )
            }
        }

        OutlinedTextField(
            value = pmComments,
            onValueChange = { pmComments = it },
            label = { Text("Private Evaluation Notes (Admins Only)") },
            placeholder = { Text("These comments will remain confidential...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(Dimens.r2)
        )

        Button(
            onClick = onSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(99.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Finalize Evaluation", fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// SCREEN 7: MARKETPLACE REVIEW
// ==========================================
@Composable
fun MarketplaceReviewScreen(
    onSubmit: () -> Unit
) {
    var conditionRating by remember { mutableIntStateOf(5) }
    var pkgRating by remember { mutableIntStateOf(5) }
    var textReview by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        Text("Marketplace Gear Review", style = Typography.bodyLarge, color = BrandDark)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r2),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🎹", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(Dimens.sp2))
                Column {
                    Text("Vintage Roland Juno-106 Synthesizer", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                    Text("Seller: Siddharth Sen (Verified Gearhead)", fontSize = 10.5.sp, color = BrandSecondaryText)
                }
            }
        }

        // Ratings Sliders
        Text("Condition Accuracy", style = Typography.bodyLarge, color = BrandDark)
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= conditionRating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = BrandAccent,
                    modifier = Modifier.clickable { conditionRating = i }
                )
            }
        }

        Text("Packaging Quality", style = Typography.bodyLarge, color = BrandDark)
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= pkgRating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = BrandAccent,
                    modifier = Modifier.clickable { pkgRating = i }
                )
            }
        }

        OutlinedTextField(
            value = textReview,
            onValueChange = { textReview = it },
            label = { Text("Transaction Details") },
            placeholder = { Text("Describe shipping speed, packing material or minor gear faults...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(Dimens.r2)
        )

        Button(
            onClick = onSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(99.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Publish Seller Review", fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// SCREEN 8: STUDIO REVIEW
// ==========================================
@Composable
fun StudioReviewScreen(
    onSubmit: () -> Unit
) {
    var acousticsRating by remember { mutableIntStateOf(5) }
    var cleanlinessRating by remember { mutableIntStateOf(5) }
    var gearDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        Text("Studio Facility Review", style = Typography.bodyLarge, color = BrandDark)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r2),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🏢", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(Dimens.sp2))
                Column {
                    Text("Abbey Road Dolby Atmos Room", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                    Text("London, UK · Verified Commercial Studio", fontSize = 10.5.sp, color = BrandSecondaryText)
                }
            }
        }

        Text("Acoustics & Isolation Suite", style = Typography.bodyLarge, color = BrandDark)
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= acousticsRating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = BrandAccent,
                    modifier = Modifier.clickable { acousticsRating = i }
                )
            }
        }

        Text("Facility Cleanliness & Amenities", style = Typography.bodyLarge, color = BrandDark)
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= cleanlinessRating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = BrandAccent,
                    modifier = Modifier.clickable { cleanlinessRating = i }
                )
            }
        }

        OutlinedTextField(
            value = gearDescription,
            onValueChange = { gearDescription = it },
            label = { Text("Studio Facilities & Engineering workflow") },
            placeholder = { Text("How were the monitors? Was the local technician helpful?...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(Dimens.r2)
        )

        Button(
            onClick = onSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(99.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Publish Studio Review", fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// SCREEN 9: BUSINESS RESPONSE SCREEN
// ==========================================
@Composable
fun BusinessResponseScreen(
    review: ReputationReviewItem,
    onSubmitResponse: (String) -> Unit
) {
    var responseDraft by remember { mutableStateOf(review.businessReplyText) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        Text("Draft Owner Response", style = Typography.bodyLarge, color = BrandDark)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r2),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(review.reviewerName, fontWeight = FontWeight.Bold, color = BrandDark, style = Typography.labelLarge)
                Text("\"${review.reviewText}\"", fontSize = 11.5.sp, color = BrandSecondaryText, modifier = Modifier.padding(top = 4.dp))
            }
        }

        OutlinedTextField(
            value = responseDraft,
            onValueChange = { responseDraft = it },
            label = { Text("Your Response to ${review.reviewerName}") },
            placeholder = { Text("Thank your client or offer additional details about the project work...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(Dimens.r2)
        )

        Button(
            onClick = { onSubmitResponse(responseDraft) },
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(99.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Publish response on live profile", fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// SCREEN 10: REPORT REVIEW
// ==========================================
@Composable
fun ReportReviewScreen(
    onSubmitReport: (String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("False Information") }
    val reasons = listOf("Spam / Phishing", "Abuse / Harassment", "False Information", "Conflict of Interest", "Copyright Claim")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        Text("Dispute Verification System", style = Typography.bodyLarge, color = BrandDark)
        Text("We investigate disputed reviews through contract milestone verification. Please specify your claim reason:", style = Typography.labelLarge, color = BrandSecondaryText)

        reasons.forEach { reason ->
            val isSelected = selectedReason == reason
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.r2))
                    .background(if (isSelected) BrandPrimary.copy(alpha = 0.08f) else Color.White)
                    .border(1.dp, if (isSelected) BrandPrimary else BrandBorder, RoundedCornerShape(Dimens.r2))
                    .clickable { selectedReason = reason }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(reason, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                RadioButton(
                    selected = isSelected,
                    onClick = { selectedReason = reason },
                    colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                )
            }
        }

        Button(
            onClick = { onSubmitReport(selectedReason) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(99.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("File Dispute Ticket", fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// SCREEN 11: PUBLIC REPUTATION
// ==========================================
@Composable
fun PublicReputationWidget(
    reviews: List<ReputationReviewItem>,
    onNavigateTo: (ReputationSubScreen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        Text("Profile Reputation Display", style = Typography.bodyLarge, color = BrandDark)
        Text("This component renders directly on public portfolios and shared business profiles.", fontSize = 11.sp, color = BrandSecondaryText)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r3),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("4.9", fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, color = BrandDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("★", color = BrandAccent, style = Typography.headlineLarge)
                        }
                        Text("Based on ${reviews.size} verified projects", fontSize = 11.sp, color = BrandSecondaryText)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(BrandSuccess.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("TOP RATED COLLABORATOR", color = BrandSuccess, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = BrandBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Fast Stats Block (Loading/Calculating until populated)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = BrandPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Text(
                        text = "Calculating network response metrics...",
                        fontSize = 11.sp,
                        color = BrandSecondaryText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Text("Recent Testimonial Slide", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BrandDark),
            shape = RoundedCornerShape(Dimens.r2)
        ) {
            Column(modifier = Modifier.padding(Dimens.sp2)) {
                Text(
                    "\"The raw vocals Vedanshu recorded are exceptionally clean. The Kemper profile work was masterfully done.\"",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("James Miller · Universal Records", color = BrandAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = { onNavigateTo(ReputationSubScreen.DASHBOARD) },
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(99.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Open Full Scorecard Dashboard", fontWeight = FontWeight.Bold)
        }
    }
}
