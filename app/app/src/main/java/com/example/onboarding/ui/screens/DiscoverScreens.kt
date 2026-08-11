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
private val BrandPrimary = Color(0xFFEA4C89)
private val BrandSecondary = Color(0xFFD93C77)
private val BrandBackground = Color(0xFFFCFCFD)
private val BrandCard = Color(0xFFFFFFFF)
private val BrandText = Color(0xFF181A22)
private val BrandBorder = Color(0xFFECECF1)
private val BrandSuccess = Color(0xFF16A34A)
private val BrandMuted = Color(0xFF676B7B)

@Composable
fun DiscoverMainFlowContainer(
    userName: String = "Vedanshu",
    userRole: String = "Producer",
    avatarImageUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
    onBackClick: () -> Unit = {}
) {
    val viewModel: DiscoverViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val filteredProfiles = viewModel.getFilteredProfiles()
    
    var showAdvancedFilters by remember { mutableStateOf(false) }
    var showHireModal by remember { mutableStateOf<DiscoverProfile?>(null) }
    var showShareProfile by remember { mutableStateOf<DiscoverProfile?>(null) }

    val openProfileDirectly: (DiscoverProfile) -> Unit = { profile ->
        val targetScreen = when (profile.type) {
            ProfileType.TALENT -> DiscoverScreen.FULL_PROFILE
            ProfileType.BAND -> DiscoverScreen.BAND_PROFILE
            ProfileType.BUSINESS -> DiscoverScreen.BUSINESS_PROFILE
            ProfileType.VENUE_EVENT -> DiscoverScreen.VENUE_PROFILE
        }
        viewModel.navigateTo(targetScreen, profile)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (state.currentScreen) {
                DiscoverScreen.HOME -> DiscoverHomeScreen(
                    state = state,
                    avatarImageUrl = avatarImageUrl,
                    userRole = userRole,
                    onNavigateTo = { viewModel.navigateTo(it) },
                    onSelectCategory = { viewModel.selectCategory(it) },
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onProfileClick = openProfileDirectly,
                    onHireClick = { showHireModal = it },
                    onToggleSave = { viewModel.toggleSaveProfile(it) },
                    onBackToDashboard = onBackClick
                )
                DiscoverScreen.CATEGORY_DIRECTORY -> CategoryDirectoryScreen(
                    onBack = { viewModel.navigateBack() },
                    onSelectCategory = { viewModel.selectCategory(it) }
                )
                DiscoverScreen.SEARCH_RESULTS -> SearchResultsScreen(
                    state = state,
                    profiles = filteredProfiles,
                    onBack = { viewModel.navigateBack() },
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onOpenFilters = { showAdvancedFilters = true },
                    onRoleFilterToggle = { viewModel.setRoleFilter(it) },
                    onGenreFilterToggle = { viewModel.setGenreFilter(it) },
                    onCategoryFilterToggle = { viewModel.setCategoryFilter(it) },
                    onExperienceFilterToggle = { viewModel.setExperienceFilter(it) },
                    onVerificationToggle = { viewModel.toggleVerificationFilter() },
                    onRemoteToggle = { viewModel.toggleRemoteFilter() },
                    onSessionToggle = { viewModel.toggleSessionFilter() },
                    onClearAllFilters = { viewModel.clearAllFilters() },
                    onProfileClick = openProfileDirectly,
                    onToggleSave = { viewModel.toggleSaveProfile(it) },
                    onHireClick = { showHireModal = it }
                )
                DiscoverScreen.SINGER_DIRECTORY -> SingerDirectoryScreen(
                    state = state,
                    profiles = filteredProfiles,
                    onBack = { viewModel.navigateBack() },
                    onToggleRange = { viewModel.toggleSingerVocalRange(it) },
                    onToggleGender = { viewModel.toggleSingerGender(it) },
                    onToggleGenre = { viewModel.toggleSingerGenre(it) },
                    onToggleView = { viewModel.setSingerGridView(it) },
                    onProfileClick = openProfileDirectly,
                    onToggleSave = { viewModel.toggleSaveProfile(it) }
                )
                DiscoverScreen.FULL_PROFILE -> {
                    state.selectedProfile?.let { profile ->
                        FullTalentProfileScreen(
                            profile = profile,
                            onBack = { viewModel.navigateBack() },
                            onHireClick = { showHireModal = profile },
                            onToggleSave = { viewModel.toggleSaveProfile(profile.id) },
                            onShareClick = { showShareProfile = profile },
                            isSaved = state.savedProfileIds.contains(profile.id)
                        )
                    }
                }
                DiscoverScreen.BAND_PROFILE -> {
                    state.selectedProfile?.let { band ->
                        BandProfileScreen(
                            band = band,
                            onBack = { viewModel.navigateBack() },
                            onBookClick = { showHireModal = band },
                            onMemberClick = openProfileDirectly,
                            onToggleSave = { viewModel.toggleSaveProfile(band.id) },
                            onShareClick = { showShareProfile = band },
                            isSaved = state.savedProfileIds.contains(band.id)
                        )
                    }
                }
                DiscoverScreen.BUSINESS_PROFILE -> {
                    state.selectedProfile?.let { studio ->
                        BusinessProfileScreen(
                            studio = studio,
                            onBack = { viewModel.navigateBack() },
                            onBookClick = { showHireModal = studio },
                            onToggleSave = { viewModel.toggleSaveProfile(studio.id) },
                            onShareClick = { showShareProfile = studio },
                            isSaved = state.savedProfileIds.contains(studio.id)
                        )
                    }
                }
                DiscoverScreen.VENUE_PROFILE -> {
                    state.selectedProfile?.let { venue ->
                        VenueProfileScreen(
                            venue = venue,
                            onBack = { viewModel.navigateBack() },
                            onBookClick = { showHireModal = venue },
                            onToggleSave = { viewModel.toggleSaveProfile(venue.id) },
                            onShareClick = { showShareProfile = venue },
                            isSaved = state.savedProfileIds.contains(venue.id)
                        )
                    }
                }
                DiscoverScreen.SAVED_PROFILES -> SavedProfilesScreen(
                    state = state,
                    allProfiles = sampleDiscoverProfiles,
                    onBack = { viewModel.navigateBack() },
                    onTabChange = { viewModel.changeSavedActiveTab(it) },
                    onProfileClick = openProfileDirectly,
                    onSaveNote = { id, text -> viewModel.updateSavedProfileNotes(id, text) },
                    onAddTag = { id, tag -> viewModel.addSavedProfileTag(id, tag) },
                    onRemoveTag = { id, tag -> viewModel.removeSavedProfileTag(id, tag) },
                    onRemove = { viewModel.toggleSaveProfile(it) }
                )
                DiscoverScreen.RECOMMENDED_MATCHES -> RecommendedMatchesScreen(
                    state = state,
                    onBack = { viewModel.navigateBack() },
                    onSwipeLeft = { viewModel.swipeLeft() },
                    onSwipeRight = { viewModel.swipeRight() },
                    onProfileClick = openProfileDirectly
                )
            }
        }

        // Animated Sheets overlays
        if (showAdvancedFilters) {
            AdvancedFiltersBottomSheet(
                state = state,
                onDismiss = { showAdvancedFilters = false },
                onApply = { r, l, g, c, a, av, v, xp, rem, ses, sort ->
                    viewModel.updateAdvancedFilters(r, l, g, c, a, av, v, xp, rem, ses, sort)
                    showAdvancedFilters = false
                    viewModel.navigateTo(DiscoverScreen.SEARCH_RESULTS)
                },
                onReset = {
                    viewModel.resetAdvancedFilters()
                }
            )
        }

        showHireModal?.let { profile ->
            HireContactModal(
                profile = profile,
                onDismiss = { showHireModal = null },
                onSubmit = { proj, budget, timeline, desc ->
                    viewModel.submitHireRequest(proj, budget, timeline, desc)
                },
                successMessage = state.hireSubmittedMessage
            )
        }

        showShareProfile?.let { profile ->
            WhatsAppShareSheet(
                profile = profile,
                onDismiss = { showShareProfile = null }
            )
        }
    }
}

// ==========================================
// COLLABORATOR RECOMMENDATION ENGINE & UI
// ==========================================
data class CollaboratorMatchResult(
    val profile: DiscoverProfile,
    val matchScore: Int,
    val sharedGenres: List<String>,
    val isComplementaryRole: Boolean,
    val matchReason: String,
    val complementaryReason: String
)

object CollaboratorRecommendationEngine {
    fun calculateMatches(
        userRole: String,
        userGenres: List<String>,
        candidateProfiles: List<DiscoverProfile>
    ): List<CollaboratorMatchResult> {
        val userRoleClean = userRole.lowercase().trim()
        val userGenresClean = userGenres.map { it.lowercase().trim() }

        return candidateProfiles.map { candidate ->
            var score = 35

            val shared = candidate.genres.filter { candidateG ->
                val candG = candidateG.lowercase().trim()
                userGenresClean.any { userG -> userG.contains(candG) || candG.contains(userG) }
            }
            score += (shared.size * 18).coerceAtMost(38)

            val candCategory = candidate.category.lowercase()
            val candSubRole = candidate.subRole.lowercase()

            val (isComp, compReason) = checkRoleComplementarity(userRoleClean, candCategory, candSubRole)
            if (isComp) {
                score += 28
            }

            if (candidate.isVerified) score += 5
            if (candidate.rating >= 4.8) score += 4
            if (candidate.remoteWork || candidate.sessionMusician) score += 5

            val finalScore = score.coerceIn(52, 99)

            val genreText = if (shared.isNotEmpty()) {
                "Shared Genres: ${shared.joinToString(", ")}"
            } else {
                "Style: ${candidate.genres.firstOrNull() ?: "General"}"
            }

            val reason = if (isComp) "$compReason • $genreText" else genreText

            CollaboratorMatchResult(
                profile = candidate,
                matchScore = finalScore,
                sharedGenres = shared,
                isComplementaryRole = isComp,
                matchReason = reason,
                complementaryReason = compReason
            )
        }.sortedByDescending { it.matchScore }
    }

    private fun checkRoleComplementarity(userRole: String, candCat: String, candSubRole: String): Pair<Boolean, String> {
        val cand = "$candCat $candSubRole"
        return when {
            userRole.contains("sing") || userRole.contains("vocal") -> {
                when {
                    cand.contains("produc") || cand.contains("beat") -> true to "Vocalist 🤝 Producer"
                    cand.contains("guitar") || cand.contains("pian") || cand.contains("musician") -> true to "Vocalist 🤝 Instrumentalist"
                    cand.contains("engin") || cand.contains("mix") || cand.contains("studio") -> true to "Vocalist 🤝 Recording Studio"
                    cand.contains("compos") -> true to "Vocalist 🤝 Songwriter"
                    cand.contains("band") -> true to "Vocalist 🤝 Band Ensemble"
                    else -> false to "Peer Vocalist"
                }
            }
            userRole.contains("produc") || userRole.contains("beat") -> {
                when {
                    cand.contains("sing") || cand.contains("vocal") -> true to "Producer 🤝 Lead Vocalist"
                    cand.contains("guitar") || cand.contains("drum") || cand.contains("pian") -> true to "Producer 🤝 Session Musician"
                    cand.contains("engin") || cand.contains("master") -> true to "Producer 🤝 Mix Engineer"
                    cand.contains("compos") || cand.contains("lyric") -> true to "Producer 🤝 Songwriter"
                    else -> false to "Co-Producer"
                }
            }
            userRole.contains("guitar") || userRole.contains("musician") || userRole.contains("drum") || userRole.contains("pian") -> {
                when {
                    cand.contains("sing") || cand.contains("vocal") -> true to "Instrumentalist 🤝 Vocalist"
                    cand.contains("produc") -> true to "Session Musician 🤝 Producer"
                    cand.contains("drum") || cand.contains("bass") || cand.contains("pian") -> true to "Rhythm Section Synergy"
                    cand.contains("band") -> true to "Musician 🤝 Band Ensemble"
                    else -> false to "Jam Partner"
                }
            }
            userRole.contains("engin") || userRole.contains("mix") || userRole.contains("sound") -> {
                when {
                    cand.contains("produc") || cand.contains("compos") -> true to "Sound Engineer 🤝 Producer"
                    cand.contains("sing") || cand.contains("band") -> true to "Sound Engineer 🤝 Recording Artist"
                    cand.contains("studio") || cand.contains("venue") -> true to "Engineer 🤝 Studio Partner"
                    else -> false to "Audio Technical Match"
                }
            }
            userRole.contains("compos") || userRole.contains("song") -> {
                when {
                    cand.contains("sing") || cand.contains("vocal") -> true to "Composer 🤝 Playback Singer"
                    cand.contains("produc") -> true to "Composer 🤝 Music Producer"
                    cand.contains("engin") -> true to "Composer 🤝 Mix Engineer"
                    else -> false to "Co-Songwriter"
                }
            }
            else -> {
                if (cand.contains("sing") || cand.contains("produc") || cand.contains("guitar") || cand.contains("studio")) {
                    true to "Complementary Skills"
                } else {
                    false to "Style Synergy"
                }
            }
        }
    }
}

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
        shape = RoundedCornerShape(20.dp),
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

            Spacer(modifier = Modifier.height(8.dp))

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
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BrandText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (profile.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(14.dp))
                        }
                    }

                    Text(profile.subRole, fontSize = 12.sp, color = BrandSecondary, fontWeight = FontWeight.SemiBold)

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
                    .clip(RoundedCornerShape(8.dp))
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    Spacer(modifier = Modifier.width(4.dp))
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

@Composable
fun TrendingMusicProjectsSection(
    userGenres: List<String>
) {
    var projects by remember { mutableStateOf<List<TrendingMusicProject>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var playingProjectState by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(userGenres) {
        isLoading = true
        try {
            val genresString = if (userGenres.isNotEmpty()) userGenres.joinToString(", ") else "Indie Rock, Fusion, Bollywood"
            projects = GeminiRecommendationEngine.fetchTrendingMusicProjects(genresString)
        } catch (e: Throwable) {
            ErrorReportingService.logException(e, "TrendingProjectsSection", "Error fetching trending projects")
            projects = GeminiRecommendationEngine.getFallbackTrendingProjects(userGenres.joinToString())
        } finally {
            isLoading = false
        }
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
                    Text(
                        text = "🔥 Trending Music Projects",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF7C3AED).copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("AI Recommended", fontSize = 9.sp, color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "Matched to your genres: ${if (userGenres.isNotEmpty()) userGenres.take(2).joinToString(", ") else "Indie & Electronic"}",
                    fontSize = 12.sp,
                    color = BrandMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BrandPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Analyzing Indian music grid trends...", fontSize = 13.sp, color = BrandMuted)
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(projects, key = { it.id }) { project ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.StartToEnd || dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                projects = projects.filter { it.id != project.id }
                                android.widget.Toast.makeText(context, "Project card dismissed from feed", android.widget.Toast.LENGTH_SHORT).show()
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
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFEF4444))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Delete, contentDescription = "Dismiss", tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Dismissed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .testTag("trending_project_${project.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                        Column {
                            // Cover Image with Match Badge
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                AsyncImage(
                                    model = project.coverImageUrl,
                                    contentDescription = project.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                            )
                                        )
                                )
                                // Match Score Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BrandPrimary)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("${project.matchScore}% Match", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                                // Title and Listens
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = project.genre,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = project.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Project Details Body
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = BrandMuted)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = project.creatorName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandText
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = BrandPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("${project.listensCount} plays", fontSize = 10.sp, color = BrandMuted)
                                }

                                Text(
                                    text = project.description,
                                    fontSize = 11.sp,
                                    color = BrandMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )

                                // Seeking Roles Chips
                                Text("Open Roles Needed:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandText)
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    items(project.seekingRoles) { role ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(BrandPrimary.copy(alpha = 0.08f))
                                                .border(0.8.dp, BrandPrimary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(role, fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Audio Snippet, Share & Collab Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isPlaying = playingProjectState == project.id
                                    OutlinedButton(
                                        onClick = {
                                            playingProjectState = if (isPlaying) null else project.id
                                            val msg = if (isPlaying) "Paused demo" else "Playing audio preview: ${project.audioSnippetTitle}"
                                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                        border = BorderStroke(1.dp, if (isPlaying) BrandPrimary else BrandBorder)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Demo",
                                            tint = if (isPlaying) BrandPrimary else BrandText,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = if (isPlaying) "Playing" else "Demo",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPlaying) BrandPrimary else BrandText
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            DynamicLinkManager.shareProjectDynamicLink(
                                                context = context,
                                                projectId = project.id,
                                                projectTitle = project.title,
                                                creatorName = project.creatorName
                                            )
                                        },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .border(1.dp, BrandBorder, RoundedCornerShape(10.dp))
                                            .testTag("share_project_${project.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share Dynamic Link",
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            android.widget.Toast.makeText(
                                                context,
                                                "Collaboration request sent to ${project.creatorName}!",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                    ) {
                                        Text("Join Collab", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                            .clip(RoundedCornerShape(8.dp))
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
                                .clip(RoundedCornerShape(16.dp))
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
                                .clip(RoundedCornerShape(16.dp))
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

        Spacer(modifier = Modifier.height(12.dp))

        val context = androidx.compose.ui.platform.LocalContext.current
        var dismissedProfileIds by remember { mutableStateOf(setOf<String>()) }
        val visibleMatches = remember(matches, dismissedProfileIds) {
            matches.filter { !dismissedProfileIds.contains(it.profile.id) }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFEF4444))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Delete, contentDescription = "Dismiss profile", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dismissed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
@Composable
fun DiscoverHomeScreen(
    state: DiscoverUiState,
    avatarImageUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
    userRole: String = "Producer",
    userGenres: List<String> = listOf("Bollywood", "Indie", "Pop"),
    onNavigateTo: (DiscoverScreen) -> Unit,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onProfileClick: (DiscoverProfile) -> Unit,
    onHireClick: (DiscoverProfile) -> Unit = {},
    onToggleSave: (String) -> Unit = {},
    onBackToDashboard: () -> Unit
) {
    var searchInput by remember { mutableStateOf("") }
    val featuredTalent = sampleDiscoverProfiles.filter { it.isVerified && it.type == ProfileType.TALENT }
    val trendingBands = sampleDiscoverProfiles.filter { it.type == ProfileType.BAND }
    val nearbyStudios = sampleDiscoverProfiles.filter { it.type == ProfileType.BUSINESS }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToDashboard,
                    modifier = Modifier.testTag("back_to_onboarding_dash")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to dashboard",
                        tint = BrandPrimary
                    )
                }
                Column(modifier = Modifier.padding(start = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_logo),
                            contentDescription = "MusicLinkd Logo",
                            tint = BrandPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Discover",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandText
                        )
                    }
                    Text(
                        text = "Connect with India's Music Grid",
                        fontSize = 12.sp,
                        color = BrandMuted
                    )
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onNavigateTo(DiscoverScreen.RECOMMENDED_MATCHES) }) {
                    Icon(Icons.Default.Whatshot, "Recommendations", tint = Color(0xFFFF5A5F))
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { onNavigateTo(DiscoverScreen.SAVED_PROFILES) }) {
                    BadgedBox(badge = {
                        if (state.savedProfileIds.isNotEmpty()) {
                            Badge(containerColor = BrandPrimary) {
                                Text(state.savedProfileIds.size.toString(), color = Color.White)
                            }
                        }
                    }) {
                        Icon(Icons.Default.Bookmark, "Saved Profiles", tint = BrandPrimary)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                
                // User Avatar Circle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = avatarImageUrl,
                        contentDescription = "User avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(BrandSuccess, CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }

        // Search text field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchInput,
                onValueChange = {
                    searchInput = it
                    onSearchQueryChange(it)
                },
                placeholder = { Text("Search talent, studios, bands...", color = BrandMuted) },
                leadingIcon = { Icon(Icons.Default.Search, "Search", tint = BrandPrimary) },
                trailingIcon = {
                    if (searchInput.isNotEmpty()) {
                        IconButton(onClick = {
                            searchInput = ""
                            onSearchQueryChange("")
                        }) {
                            Icon(Icons.Default.Close, "Clear")
                        }
                    } else {
                        IconButton(onClick = { onNavigateTo(DiscoverScreen.SEARCH_RESULTS) }) {
                            Icon(Icons.Default.ArrowForward, "Search Results", tint = BrandPrimary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("discover_search_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = BrandBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
        }

        com.example.onboarding.ui.components.SearchAutocompleteOverlay(
            query = searchInput,
            onSuggestionSelect = { selected ->
                searchInput = selected
                onSearchQueryChange(selected)
            }
        )

        // Swipe & Match Promo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onNavigateTo(DiscoverScreen.RECOMMENDED_MATCHES) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFEA4C89), Color(0xFFFF8EB6))
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Swipe n' Match",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Discover collaborators game-show style!",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = { onNavigateTo(DiscoverScreen.RECOMMENDED_MATCHES) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Hot-Match", color = BrandText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        /*
        // Trending Music Projects (Dynamic Feed based on user genre interests)
        TrendingMusicProjectsSection(
            userGenres = userGenres
        )

        // Recommended Collaborators Section
        RecommendedCollaboratorsSection(
            initialUserRole = userRole,
            initialUserGenres = userGenres,
            savedProfileIds = state.savedProfileIds,
            onProfileClick = onProfileClick,
            onHireClick = onHireClick,
            onToggleSave = onToggleSave
        )
        */

        // Quick Categories Bar Heading
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Quick Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandText)
            Text(
                text = "See All (${defaultCategories.size})",
                color = BrandPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onNavigateTo(DiscoverScreen.CATEGORY_DIRECTORY) }
            )
        }

        // Horizontal Grid of Quick Categories
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(defaultCategories) { cat ->
                Card(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .clickable { onSelectCategory(cat.name) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(cat.icon, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(cat.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BrandText)
                    }
                }
            }
        }

        HomeSectionHeader("Featured Professionals")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(featuredTalent) { talent ->
                ArtistCard(
                    profile = talent,
                    onProfileClick = { onProfileClick(talent) },
                    onHireClick = { onProfileClick(talent) },
                    modifier = Modifier.width(260.dp)
                )
            }
        }

        HomeSectionHeader("Trending Independent Bands")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trendingBands) { band ->
                ArtistCard(
                    profile = band,
                    onProfileClick = { onProfileClick(band) },
                    onHireClick = { onProfileClick(band) },
                    modifier = Modifier.width(260.dp)
                )
            }
        }

        HomeSectionHeader("Nearby Studios & Venues")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            nearbyStudios.take(3).forEach { studio ->
                StudioVenueListItem(studio = studio, onClick = { onProfileClick(studio) })
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun HomeSectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = BrandText,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp)
    )
}

@Composable
fun HomeFeatureCard(profile: DiscoverProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(profile.avatarColorHex).copy(alpha = 0.8f),
                                Color(profile.avatarColorHex)
                            )
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
                
                if (profile.isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.White, CircleShape)
                            .padding(4.dp)
                    ) {
                        Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(14.dp))
                    }
                }
                
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB74D), modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(profile.rating.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = profile.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = BrandText
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val inst = getInstrumentEmoji(profile.subRole, profile.category)
                    Text(inst, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = profile.subRole,
                        fontSize = 11.sp,
                        color = BrandSecondary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, "Location", tint = BrandMuted, modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(profile.location, fontSize = 10.sp, color = BrandMuted)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    profile.genres.take(2).forEach { genre ->
                        Box(
                            modifier = Modifier
                                .background(BrandPrimary.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(genre, fontSize = 8.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedWaveform(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "waveform")
        val heights = listOf(10f, 18f, 14f, 22f, 8f)
        heights.forEachIndexed { index, maxVal ->
            val duration = 400 + (index * 150)
            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = 4f,
                targetValue = maxVal,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = duration
                        4f at 0 with FastOutSlowInEasing
                        maxVal at (duration / 2) with FastOutSlowInEasing
                        4f at duration with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .height(animatedHeight.dp)
                    .background(Color(0xFFEA4C89), RoundedCornerShape(1.dp))
            )
        }
    }
}

@Composable
fun ArtistCard(
    profile: DiscoverProfile,
    onProfileClick: () -> Unit,
    onHireClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val springEasing = remember { CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f) }

    val translationY by animateDpAsState(
        targetValue = if (isHovered || isPressed) (-4).dp else 0.dp,
        animationSpec = tween(durationMillis = 200, easing = springEasing),
        label = "translationY"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else if (isHovered) 1.01f else 1.0f,
        animationSpec = tween(durationMillis = 150, easing = springEasing),
        label = "scale"
    )

    var audioState by remember { mutableStateOf(0) } // 0 = Idle, 1 = Buffering, 2 = Playing

    LaunchedEffect(audioState) {
        if (audioState == 1) {
            kotlinx.coroutines.delay(800) // Simulating buffering
            audioState = 2
        }
    }
    val isAudioPlaying = audioState == 2
    val dominantColor = Color(profile.avatarColorHex)

    Card(
        modifier = modifier
            .graphicsLayer {
                this.translationY = translationY.toPx()
                this.scaleX = scale
                this.scaleY = scale
            }
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onProfileClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHovered) Color.White.copy(alpha = 0.96f) else Color.White
        ),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered || isPressed) 6.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isHovered) {
                        Brush.radialGradient(
                            colors = listOf(dominantColor.copy(alpha = 0.04f), Color.Transparent)
                        )
                    } else {
                        Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                    }
                )
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
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(profile.avatarColorHex).copy(alpha = 0.25f),
                                    Color(profile.avatarColorHex).copy(alpha = 0.05f)
                                )
                            )
                        )
                        .border(1.dp, Color(profile.avatarColorHex).copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = profile.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                        contentDescription = profile.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                val isAvailable = profile.availabilityStatus.contains("Avail", ignoreCase = true) || profile.availabilityStatus.contains("Open", ignoreCase = true)
                val tagBgColor = if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                val tagTextColor = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFE65100)
                val dotColor = if (isAvailable) Color(0xFF4CAF50) else Color(0xFFFF9800)

                Row(
                    modifier = Modifier
                        .background(tagBgColor, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    val pulseTransition = rememberInfiniteTransition(label = "pulse")
                    val alpha by pulseTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_alpha"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .graphicsLayer { this.alpha = alpha }
                            .background(dotColor, CircleShape)
                    )
                    Text(
                        text = profile.availabilityStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = profile.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = BrandText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (profile.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFFEA4C89),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val instEmoji = getInstrumentEmoji(profile.subRole, profile.category)
                Text(instEmoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = profile.subRole,
                    fontSize = 13.sp,
                    color = Color(0xFFEA4C89),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = BrandMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${profile.area} • ${profile.location}",
                    fontSize = 12.sp,
                    color = BrandMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                profile.genres.take(2).forEach { genre ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF3F3F6), RoundedCornerShape(999.dp))
                            .border(1.dp, Color(0xFFECECF1), RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = genre,
                            fontSize = 10.sp,
                            color = Color(0xFF4A4D5C),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFFECECF1))

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val audioInteractionSource = remember { MutableInteractionSource() }
                val isAudioPressed by audioInteractionSource.collectIsPressedAsState()
                val audioScale by animateFloatAsState(
                    targetValue = if (isAudioPressed) 0.92f else 1.0f,
                    label = "audio_scale"
                )

                Row(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = audioScale
                            scaleY = audioScale
                        }
                        .background(
                            if (isAudioPlaying) Color(0xFFEA4C89).copy(alpha = 0.1f) else Color(0xFFF3F3F6),
                            RoundedCornerShape(999.dp)
                        )
                        .border(
                            1.dp,
                            if (isAudioPlaying) Color(0xFFEA4C89).copy(alpha = 0.3f) else Color(0xFFECECF1),
                            RoundedCornerShape(999.dp)
                        )
                        .clickable(
                            interactionSource = audioInteractionSource,
                            indication = androidx.compose.foundation.LocalIndication.current,
                            onClick = { 
                                audioState = if (audioState == 2) 0 else 1 
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (audioState == 1) {
                        CircularProgressIndicator(
                            color = Color(0xFFEA4C89),
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Quick Audio Preview",
                            tint = if (isAudioPlaying) Color(0xFFEA4C89) else Color(0xFF4A4D5C),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    if (isAudioPlaying) {
                        AnimatedWaveform(modifier = Modifier.padding(end = 4.dp))
                    } else if (audioState == 1) {
                        Text(
                            text = "Buffering",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEA4C89)
                        )
                    } else {
                        Text(
                            text = "Preview",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A4D5C)
                        )
                    }
                }

                val buttonInteractionSource = remember { MutableInteractionSource() }
                val isBtnPressed by buttonInteractionSource.collectIsPressedAsState()
                val btnScale by animateFloatAsState(
                    targetValue = if (isBtnPressed) 0.95f else 1.0f,
                    label = "btn_scale"
                )

                Button(
                    onClick = onHireClick,
                    interactionSource = buttonInteractionSource,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = btnScale
                            scaleY = btnScale
                        }
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D0B26),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(999.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Collab / Hire",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
}

@Composable
fun StudioVenueListItem(studio: DiscoverProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(studio.avatarColorHex).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = studio.imageUrl ?: "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=300&q=80",
                    contentDescription = studio.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(studio.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
                    if (studio.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(14.dp))
                    }
                    if (studio.hasAtmos) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .background(BrandSecondary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("ATMOS", fontSize = 8.sp, color = BrandSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(studio.subRole, fontSize = 11.sp, color = BrandMuted)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, "Location", tint = BrandMuted, modifier = Modifier.size(12.dp))
                    Text(studio.area + ", " + studio.location, fontSize = 11.sp, color = BrandMuted)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB74D), modifier = Modifier.size(12.dp))
                    Text(studio.rating.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText)
                }
                if (studio.pricing.isNotEmpty()) {
                    Text(studio.pricing, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSuccess)
                }
            }
        }
    }
}

// ==========================================
// SCREEN 2: BROWSE CATEGORIES SCREEN
// ==========================================
@Composable
fun CategoryDirectoryScreen(
    onBack: () -> Unit,
    onSelectCategory: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            Text("Browse Categories", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = BrandText)
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(defaultCategories) { cat ->
                Card(
                    modifier = Modifier
                        .padding(6.dp)
                        .height(130.dp)
                        .clickable { onSelectCategory(cat.name) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BrandPrimary.copy(alpha = 0.08f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cat.icon, fontSize = 20.sp)
                        }

                        Column {
                            Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
                            Text(
                                text = cat.profileCount.toString() + " Listings",
                                fontSize = 11.sp,
                                color = BrandMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 3: SEARCH RESULTS SCREEN
// ==========================================
@Composable
fun SearchResultsScreen(
    state: DiscoverUiState,
    profiles: List<DiscoverProfile>,
    onBack: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenFilters: () -> Unit,
    onRoleFilterToggle: (String) -> Unit = {},
    onGenreFilterToggle: (String) -> Unit = {},
    onCategoryFilterToggle: (String?) -> Unit = {},
    onExperienceFilterToggle: (String) -> Unit = {},
    onVerificationToggle: () -> Unit = {},
    onRemoteToggle: () -> Unit = {},
    onSessionToggle: () -> Unit = {},
    onClearAllFilters: () -> Unit = {},
    onProfileClick: (DiscoverProfile) -> Unit,
    onToggleSave: (String) -> Unit,
    onHireClick: (DiscoverProfile) -> Unit
) {
    var searchInput by remember { mutableStateOf(state.searchQuery) }

    LaunchedEffect(state.searchQuery) {
        if (state.searchQuery != searchInput) {
            searchInput = state.searchQuery
        }
    }

    val activeFilterCount = listOf(
        state.selectedCategory != null,
        state.filterRole.isNotEmpty(),
        state.filterGenre.isNotEmpty(),
        state.filterExperienceLevel.isNotEmpty(),
        state.filterVerification,
        state.filterRemoteAvailable,
        state.filterSessionAvailable,
        state.filterCity.isNotEmpty()
    ).count { it }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = {
                        searchInput = it
                        onSearchQueryChange(it)
                    },
                    placeholder = { Text("Search professional network...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, "Search", modifier = Modifier.size(16.dp)) },
                    trailingIcon = {
                        if (searchInput.isNotEmpty()) {
                            IconButton(onClick = {
                                searchInput = ""
                                onSearchQueryChange("")
                            }) {
                                Icon(Icons.Default.Close, "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .testTag("network_search_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            com.example.onboarding.ui.components.SearchAutocompleteOverlay(
                query = searchInput,
                onSuggestionSelect = { selected ->
                    searchInput = selected
                    onSearchQueryChange(selected)
                }
            )

            // Interactive Filter Chips Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Role Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Role:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText, modifier = Modifier.width(55.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        val roles = listOf("Singer", "Guitarist", "Drummer", "Pianist", "Producer", "Sound Engineer", "Composer", "DJ", "Studio")
                        items(roles) { r ->
                            val isSel = state.filterRole.equals(r, ignoreCase = true)
                            FilterChipCustom(r, isSel, { onRoleFilterToggle(r) })
                        }
                    }
                }

                // Genre Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Genre:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandText, modifier = Modifier.width(55.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        val genres = listOf("Bollywood", "Classical", "Indie", "Pop", "Rock", "Electronic", "Folk", "Sufi", "Hip-Hop", "Jazz")
                        items(genres) { g ->
                            val isSel = state.filterGenre.equals(g, ignoreCase = true)
                            FilterChipCustom(g, isSel, { onGenreFilterToggle(g) })
                        }
                    }
                }

                // Quick Toggles and Experience Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChipCustom("✓ Verified", state.filterVerification, { onVerificationToggle() })
                    }
                    item {
                        FilterChipCustom("🌐 Remote", state.filterRemoteAvailable, { onRemoteToggle() })
                    }
                    item {
                        FilterChipCustom("🎹 Session", state.filterSessionAvailable, { onSessionToggle() })
                    }

                    val xpLevels = listOf("Rising Star (1-2 yrs)", "Established (3-5 yrs)", "Pro (5+ years)")
                    items(xpLevels) { xp ->
                        val isSel = state.filterExperienceLevel == xp
                        FilterChipCustom("⭐ " + xp.take(12), isSel, { onExperienceFilterToggle(xp) })
                    }

                    if (activeFilterCount > 0 || searchInput.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .clickable {
                                        searchInput = ""
                                        onClearAllFilters()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Close, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reset ($activeFilterCount)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${profiles.size} Professional Profiles Matched",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandText
                )
                
                state.selectedCategory?.let {
                    Box(
                        modifier = Modifier
                            .background(BrandPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(it, fontSize = 10.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (profiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        title = "No Profiles Matched",
                        description = "No professional network profiles match your current search filters. Try tapping filter chips to broaden your scope.",
                        emoji = "🧐",
                        actionLabel = "Clear All Filters",
                        onActionClick = {
                            searchInput = ""
                            onClearAllFilters()
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(profiles) { profile ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            DiscoverySearchResultCard(
                                profile = profile,
                                isSaved = state.savedProfileIds.contains(profile.id),
                                onClick = { onProfileClick(profile) },
                                onToggleSave = { onToggleSave(profile.id) },
                                onHire = { onHireClick(profile) }
                            )
                        }
                    }
                }
            }
        }

        // Floating filter button
        ExtendedFloatingActionButton(
            onClick = onOpenFilters,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .testTag("floating_filter_button"),
            containerColor = BrandPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.FilterList, "Filter")
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (activeFilterCount > 0) "Refine Search ($activeFilterCount)" else "Refine Search", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DiscoverySearchResultCard(
    profile: DiscoverProfile,
    isSaved: Boolean,
    onClick: () -> Unit,
    onToggleSave: () -> Unit,
    onHire: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(10.dp))
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
                
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(profile.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandText)
                        if (profile.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(14.dp))
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val inst = getInstrumentEmoji(profile.subRole, profile.category)
                        Text(inst, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(profile.subRole, fontSize = 11.sp, color = BrandSecondary, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, "Location", tint = BrandMuted, modifier = Modifier.size(12.dp))
                        Text(profile.location, fontSize = 11.sp, color = BrandMuted)
                    }
                }

                IconButton(onClick = onToggleSave) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (isSaved) BrandPrimary else BrandMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = profile.bio,
                color = BrandMuted,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    profile.genres.take(2).forEach { genre ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(genre, fontSize = 9.sp, color = BrandText)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .background(BrandSuccess.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = profile.availabilityStatus,
                        fontSize = 9.sp,
                        color = BrandSuccess,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimary),
                    border = BorderStroke(1.dp, BrandBorder)
                ) {
                    Text("View Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onHire,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Hire Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// SCREEN 4: ADVANCED FILTERS CHIPS
// ==========================================
@Composable
fun AdvancedFiltersBottomSheet(
    state: DiscoverUiState,
    onDismiss: () -> Unit,
    onApply: (String, String, String, String, String, String, Boolean, String, Boolean, Boolean, String) -> Unit,
    onReset: () -> Unit
) {
    var role by remember { mutableStateOf(state.filterRole) }
    var language by remember { mutableStateOf(state.filterLanguage) }
    var genre by remember { mutableStateOf(state.filterGenre) }
    var city by remember { mutableStateOf(state.filterCity) }
    var area by remember { mutableStateOf(state.filterArea) }
    var availability by remember { mutableStateOf(state.filterAvailability) }
    var verifiedOnly by remember { mutableStateOf(state.filterVerification) }
    var experience by remember { mutableStateOf(state.filterExperienceLevel) }
    var remoteAvailable by remember { mutableStateOf(state.filterRemoteAvailable) }
    var sessionAvailable by remember { mutableStateOf(state.filterSessionAvailable) }
    var sortBy by remember { mutableStateOf(state.filterSortBy) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Refine Search", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandText)
                    Row {
                        TextButton(onClick = {
                            onReset()
                            role = ""; language = ""; genre = ""; city = ""; area = ""; availability = ""
                            verifiedOnly = false; experience = ""; remoteAvailable = false; sessionAvailable = false
                            sortBy = "Most Relevant"
                        }) {
                            Text("Reset All", color = BrandSecondary)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "Close")
                        }
                    }
                }

                HorizontalDivider()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterSectionHeader("Sort Results By")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Most Relevant", "Most Followed", "Highest Rated").forEach { match ->
                            val isSel = sortBy == match
                            FilterChipCustom(
                                label = match,
                                isSelected = isSel,
                                onClick = { sortBy = match }
                            )
                        }
                    }

                    FilterSectionHeader("Industry Professional Role")
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        placeholder = { Text("e.g. Session Bassist") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    FilterSectionHeader("Location Scope")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            placeholder = { Text("City, e.g. Mumbai") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = area,
                            onValueChange = { area = it },
                            placeholder = { Text("Area, e.g. Juhu") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    FilterSectionHeader("Language Spoken")
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Hindi", "English", "Punjabi", "Tamil", "Bengali").forEach { lang ->
                            val isSel = language.equals(lang, ignoreCase = true)
                            FilterChipCustom(
                                label = lang,
                                isSelected = isSel,
                                onClick = { language = if (isSel) "" else lang }
                            )
                        }
                    }

                    FilterSectionHeader("Genre Focus")
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Bollywood", "Indie", "Classical", "Folk", "Rock", "Pop").forEach { g ->
                            val isSel = genre.equals(g, ignoreCase = true)
                            FilterChipCustom(
                                label = g,
                                isSelected = isSel,
                                onClick = { genre = if (isSel) "" else g }
                            )
                        }
                    }

                    FilterSectionHeader("Experience Seniority")
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Rising Star (1-2 yrs)", "Established (3-5 yrs)", "Pro (5+ years)", "Legend (10+ yrs)").forEach { xp ->
                            val isSel = experience == xp
                            FilterChipCustom(
                                label = xp,
                                isSelected = isSel,
                                onClick = { experience = if (isSel) "" else xp }
                            )
                        }
                    }

                    FilterSectionHeader("Preferences")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = verifiedOnly, onCheckedChange = { verifiedOnly = it })
                            Text("Verified Professionals Only", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = remoteAvailable, onCheckedChange = { remoteAvailable = it })
                            Text("Remote Projects Allowed", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = sessionAvailable, onCheckedChange = { sessionAvailable = it })
                            Text("Available for Session Work", fontSize = 13.sp)
                        }
                    }
                }

                HorizontalDivider()

                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            onApply(role, language, genre, city, area, availability, verifiedOnly, experience, remoteAvailable, sessionAvailable, sortBy)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("apply_filters_cta"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Selected Filters", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 5: SPECIALIZED SINGER DIRECTORY
// ==========================================
@Composable
fun SingerDirectoryScreen(
    state: DiscoverUiState,
    profiles: List<DiscoverProfile>,
    onBack: () -> Unit,
    onToggleRange: (String) -> Unit,
    onToggleGender: (String) -> Unit,
    onToggleGenre: (String) -> Unit,
    onToggleView: (Boolean) -> Unit,
    onProfileClick: (DiscoverProfile) -> Unit,
    onToggleSave: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
                Text("Singers Directory", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BrandText)
            }
            
            IconButton(onClick = { onToggleView(!state.isSingerGrid) }) {
                Icon(
                    imageVector = if (state.isSingerGrid) Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = "Toggle Grid/List",
                    tint = BrandPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vocal Range:", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Soprano", "Mezzo-Soprano", "Alto", "Tenor", "Baritone", "Bass")) { s ->
                        val isSel = state.vocalRangeFilter == s
                        FilterChipCustom(s, isSel, { onToggleRange(s) })
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Voice Pitch:", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Male", "Female")) { g ->
                        val isSel = state.genderFilter == g
                        FilterChipCustom(g, isSel, { onToggleGender(g) })
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Style / Type:", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Bollywood", "Classical", "Folk", "Indie", "Rap")) { gr ->
                        val isSel = state.selectedSingerGenre == gr
                        FilterChipCustom(gr, isSel, { onToggleGenre(gr) })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filtered = profiles.filter { it.category == "Singers" }
        if (state.isSingerGrid) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 44.dp)
            ) {
                items(filtered) { singer ->
                    Box(modifier = Modifier.padding(6.dp)) {
                        HomeFeatureCard(profile = singer, onClick = { onProfileClick(singer) })
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { singer ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        DiscoverySearchResultCard(
                            profile = singer,
                            isSaved = state.savedProfileIds.contains(singer.id),
                            onClick = { onProfileClick(singer) },
                            onToggleSave = { onToggleSave(singer.id) },
                            onHire = { onProfileClick(singer) }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 6: PROFILE PREVIEW SHEET OR CARD
// ==========================================
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
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(profile.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandText)
                                if (profile.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val inst = getInstrumentEmoji(profile.subRole, profile.category)
                                Text(inst, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(profile.subRole, fontSize = 12.sp, color = BrandSecondary, fontWeight = FontWeight.SemiBold)
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
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, "Location", tint = BrandMuted, modifier = Modifier.size(14.dp))
                            Text(profile.location, fontSize = 12.sp, color = BrandText)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB74D), modifier = Modifier.size(14.dp))
                            Text(profile.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandText)
                        }

                        Box(
                            modifier = Modifier
                                .background(BrandSuccess.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
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

                    Text(profile.bio, fontSize = 12.sp, color = BrandText, lineHeight = 18.sp)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Languages spoken:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandMuted)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            profile.languages.forEach { l ->
                                Box(modifier = Modifier.background(Color(0xFFEEEEEE), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
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
                                modifier = Modifier.padding(12.dp),
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
                                        Text(iconSymbol, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(top.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandText)
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
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimary),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Text("Full Showcase", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onHireClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                    .background(BrandPrimary, RoundedCornerShape(8.dp))
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

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                Text("5-Piece Live Ensemble • Koramangala", fontSize = 12.sp, color = BrandSecondary, fontWeight = FontWeight.Bold)
                PersonaTypeBadge(band.type)
            }

            Text(band.bio, fontSize = 12.sp, color = BrandText, lineHeight = 18.sp)

            Text("Band Members Lineup", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(mb.name.split(" ")[0], fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(mb.instrument, fontSize = 8.sp, color = BrandMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            HorizontalDivider()

            Text("Upcoming Concerts & Tour Dates", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("28 Jun 2026", "Indie Rock Fiesta @ Phoenix MarketCity", "Mumbai, MH"),
                    Triple("12 Jul 2026", "Swar Fest Showcase @ Swabhumi", "Kolkata, WB"),
                    Triple("05 Aug 2026", "The BlueBreeze Stage @ Koramangala", "Bengaluru, KA")
                ).forEach { (date, eventName, venueLoc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(eventName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandText)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("📍 $venueLoc", fontSize = 10.sp, color = BrandMuted, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(BrandPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(date, fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Text("Technical Rider (Backline Requirements)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Drums: 5-piece kit (Mapex/Pearl preferred) with active gates.",
                        "Bass: Ampeg SVT-CL or Gallien-Krueger bass head with 4x10 cabinet.",
                        "Guitars: Fender Twin Reverb or Marshall DSL40 combo with SM57.",
                        "Keys: Stereo DI channels with balanced 1/4\" jacks.",
                        "Vocals: 3x Boom stand mics (Shure SM58) with clean phantom power."
                    ).forEach { req ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.CheckCircle, "Rider Spec", tint = BrandPrimary, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(req, fontSize = 11.sp, color = Color(0xFF4B5563))
                        }
                    }
                }
            }

            HorizontalDivider()

            Text("Interactive Stage Plot & Rig Positions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("STAGE PLOT LAYOUT (PRO)", color = BrandSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("BACKSTAGE / UPSTAGE", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("🎹 Keyboard Rig (Line L/R)", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF475569), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
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
                                    .background(Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("🎸 Bass Cab (XLR Out)", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
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
                                    .background(BrandPrimary, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("🎤 Lead Vocals (Shure Beta 58A)", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
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

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("book_band_cta"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(12.dp)
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
                    Text("$750 / night", fontSize = 20.sp, fontWeight = FontWeight.Black, color = BrandText)
                }

                Button(
                    onClick = onBookClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Book Band Now", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                        .background(Color.Black, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("DOLBY ATMOS PREFERENCE CELL 🔈", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(studio.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandText)
                if (studio.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("State-of-The-Art Sound Lab • " + studio.location, fontSize = 12.sp, color = BrandSecondary, fontWeight = FontWeight.Bold)
                PersonaTypeBadge(studio.type)
            }

            Text(studio.bio, fontSize = 12.sp, color = BrandText, lineHeight = 18.sp)

            val businessCategoryLower = studio.category.lowercase()
            
            when {
                businessCategoryLower.contains("studio") || businessCategoryLower.contains("lab") -> {
                    Text("Certified Studio Gear Checklist", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
                    studio.equipment.forEach { gear ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, "Checked Item", tint = BrandSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(gear, fontSize = 11.sp, color = BrandText)
                        }
                    }
                }
                
                businessCategoryLower.contains("label") || businessCategoryLower.contains("production") -> {
                    Text("Artist Roster & Signed Talents", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
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
                                shape = RoundedCornerShape(12.dp),
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
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(BrandPrimary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
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
                    Text("Offered Courses & Syllabus", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
                    listOf(
                        "Hindustani Classical Vocals (6 Months Certification) - ₹12,000/mo",
                        "Digital Music Production & DAW Mixology (3 Months Boot) - ₹18,000/mo",
                        "Modern Drumming & Rhythmic Composition (Ongoing Level 1) - ₹8,000/mo"
                    ).forEach { course ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, "Course Icon", tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(course, fontSize = 11.sp, color = BrandText, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                businessCategoryLower.contains("dealer") || businessCategoryLower.contains("equipment") || businessCategoryLower.contains("rental") || businessCategoryLower.contains("store") -> {
                    Text("Premium Gear Inventory (Sale & Hire)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
                    listOf(
                        "Neumann U87 Ai Large-diaphragm Microphone - In Stock",
                        "Shure SM7B Vocal Dynamic Microphone - 4 Left",
                        "Focusrite Scarlett 2i2 USB Audio Interface - In Stock"
                    ).forEach { gear ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, "In Stock", tint = BrandSuccess, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(gear, fontSize = 11.sp, color = BrandText)
                        }
                    }
                }

                else -> {
                    Text("Services & Roster Overview", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
                    Text(studio.bio, fontSize = 11.sp, color = BrandText, lineHeight = 16.sp)
                }
            }

            HorizontalDivider()

            Text("Availability Calendar Preview", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
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
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(day.split(" ")[0], fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandText)
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
                shape = RoundedCornerShape(12.dp)
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
                    Text(studio.pricing.ifEmpty { "₹2,500 / hr" }, fontSize = 18.sp, fontWeight = FontWeight.Black, color = BrandText)
                }

                Button(
                    onClick = onBookClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Reserve Studio", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                    .background(BrandSuccess, RoundedCornerShape(8.dp))
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

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                Text("Concert Hall & Soundstage • ${venue.location}", fontSize = 12.sp, color = BrandSecondary, fontWeight = FontWeight.Bold)
                PersonaTypeBadge(venue.type)
            }

            Text(venue.bio.ifEmpty { "Premise acoustic hall engineered for premium audio clarity, containing a full line-array PA system, soundstage, backstage greenroom, and dedicated sound desk." }, fontSize = 12.sp, color = BrandText, lineHeight = 18.sp)

            Text("Venue Specifications & Capability", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, "Capacity", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Capacity: 400-500 standing (Indoor & Amphitheater)", fontSize = 11.sp, color = BrandText, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, "Location", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("HQ Address: ${venue.area}, ${venue.location}", fontSize = 11.sp, color = BrandText)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ListAlt, "Inclusions", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Inclusions: Backline included, Greenroom, FOH Sound Desk", fontSize = 11.sp, color = BrandText)
                    }
                }
            }

            HorizontalDivider()

            Text("Upcoming Concerts & Festival Calendar", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
            listOf(
                Triple("Indie Wave Festival", "28 June 2026", "Featuring SwarSymphony"),
                Triple("Acoustic Sunset Series", "14 July 2026", "Featuring Neha Deshmukh")
            ).forEach { (eventTitle, date, artistInfo) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(eventTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandText)
                            Text(artistInfo, fontSize = 10.sp, color = BrandMuted)
                        }
                        Box(
                            modifier = Modifier
                                .background(BrandPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(date, fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            HorizontalDivider()

            Text("Availability Calendar Preview", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
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
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(day.split(" ")[0], fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandText)
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
                shape = RoundedCornerShape(12.dp)
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
                    Text(venue.pricing.ifEmpty { "₹75,000 / day" }, fontSize = 18.sp, fontWeight = FontWeight.Black, color = BrandText)
                }

                Button(
                    onClick = onBookClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Book Venue Space", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 10: SAVE & SHORTLIST SCREEN
// ==========================================
@Composable
fun SavedProfilesScreen(
    state: DiscoverUiState,
    allProfiles: List<DiscoverProfile>,
    onBack: () -> Unit,
    onTabChange: (String) -> Unit,
    onProfileClick: (DiscoverProfile) -> Unit,
    onSaveNote: (String, String) -> Unit,
    onAddTag: (String, String) -> Unit,
    onRemoveTag: (String, String) -> Unit,
    onRemove: (String) -> Unit
) {
    val savedLists = allProfiles.filter { state.savedProfileIds.contains(it.id) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            Text("Saved Profiles & Bookmarks", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = BrandText)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Talent", "Bands", "Studios").forEach { tab ->
                val isSel = state.activeSavedTab == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabChange(tab) }
                        .padding(bottom = 6.dp)
                ) {
                    Text(
                        text = tab,
                        fontSize = 13.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) BrandPrimary else BrandMuted
                    )
                    if (isSel) {
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(30.dp)
                                .background(BrandPrimary)
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        val finalRenderList = savedLists.filter {
            when (state.activeSavedTab) {
                "Talent" -> it.type == ProfileType.TALENT
                "Bands" -> it.type == ProfileType.BAND
                "Studios" -> it.type == ProfileType.BUSINESS || it.type == ProfileType.VENUE_EVENT
                else -> true
            }
        }

        if (finalRenderList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = "No Saved Entries Yet",
                    description = "Tap on card bookmarks on the search page or discover screens to save references and build your personal collaborator roster.",
                    emoji = "📁",
                    actionLabel = "Back to Discover",
                    onActionClick = onBack
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(finalRenderList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(item.avatarColorHex).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = item.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                        contentDescription = item.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandText)
                                    Text(item.subRole, fontSize = 11.sp, color = BrandSecondary)
                                }
                                IconButton(onClick = { onRemove(item.id) }) {
                                    Icon(Icons.Default.Delete, "Remove", tint = Color.Red)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            val customNote = state.savedNotes[item.id] ?: ""
                            var noteInput by remember { mutableStateOf(customNote) }
                            
                            OutlinedTextField(
                                value = noteInput,
                                onValueChange = {
                                    noteInput = it
                                    onSaveNote(item.id, it)
                                },
                                placeholder = { Text("Add custom note (e.g. Call for Friday demo)", fontSize = 11.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp),
                                textStyle = TextStyle(fontSize = 11.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandSecondary,
                                    focusedContainerColor = Color(0xFFF9FAFB),
                                    unfocusedContainerColor = Color(0xFFF9FAFB)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            val tagsList = state.savedTags[item.id] ?: emptyList()
                            var tagInputText by remember { mutableStateOf("") }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = tagInputText,
                                    onValueChange = { tagInputText = it },
                                    placeholder = { Text("Add tag", fontSize = 10.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    textStyle = TextStyle(fontSize = 10.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandSecondary,
                                        focusedContainerColor = Color(0xFFEEEEEE),
                                        unfocusedContainerColor = Color(0xFFEEEEEE)
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        if (tagInputText.isNotEmpty()) {
                                            onAddTag(item.id, tagInputText)
                                            tagInputText = ""
                                        }
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, "Add Tag", tint = BrandPrimary)
                                }
                            }

                            if (tagsList.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                DiscoverFlowRow(horizontalGap = 4.dp, verticalGap = 4.dp) {
                                    tagsList.forEach { tag ->
                                        Row(
                                            modifier = Modifier
                                                .background(BrandPrimary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                                .clickable { onRemoveTag(item.id, tag) }
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(tag, fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Icon(Icons.Default.Close, "Remove tag", tint = BrandPrimary, modifier = Modifier.size(10.dp))
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

// ==========================================
// SCREEN 11: CONTACT / HIRE FLOW (MODAL OVERLAY)
// ==========================================
@Composable
fun HireContactModal(
    profile: DiscoverProfile,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit,
    successMessage: String?
) {
    var specPurpose by remember { mutableStateOf("Hire For Project") }
    var projectName by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var timeline by remember { mutableStateOf("") }
    var noteMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Contact " + profile.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandText)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                if (successMessage != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(BrandSuccess.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, "Success Check", tint = BrandSuccess, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Inquiry Request Submitted!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(successMessage, color = BrandMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Text("Inquiry Purpose", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Send Message",
                            "Request Quote",
                            "Book Session (Coming Soon)",
                            "Hire For Project (Coming Soon)"
                        ).forEach { spec ->
                            val isSel = specPurpose == spec
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) BrandPrimary else Color(0xFFEEEEEE))
                                    .clickable { specPurpose = spec }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(spec, fontSize = 10.sp, color = if (isSel) Color.White else BrandText)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Project Details", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = projectName,
                        onValueChange = { projectName = it },
                        placeholder = { Text("Project / Event Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = budget,
                            onValueChange = { budget = it },
                            placeholder = { Text("Budget (e.g. $500)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = timeline,
                            onValueChange = { timeline = it },
                            placeholder = { Text("Timeline (e.g. 2 Days)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteMessage,
                        onValueChange = { noteMessage = it },
                        placeholder = { Text("Write brief instructions about reference tracks or audio specifications...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (projectName.isNotEmpty() && budget.isNotEmpty()) {
                                onSubmit(projectName, budget, timeline, noteMessage)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_hire_request"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Send Proposal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 12: RECOMMENDATIONS (SWIPEABLE CARD)
// ==========================================
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
                style = MaterialTheme.typography.titleMedium.copy(
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
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Live Interactive Audio Waveform overlay right in the center
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 40.dp)
                                    .height(60.dp)
                                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                                    .clickable { audioState = if (audioState == 2) 0 else 1 }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    } else {
                                        Text(
                                            text = "Tap to Stream Preview track",
                                            style = MaterialTheme.typography.bodySmall.copy(
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
                                .background(Color(0xFFEA4C89), RoundedCornerShape(12.dp))
                                .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "INTERESTED",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    } else if (dragOffset < -40f) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(24.dp)
                                .graphicsLayer { rotationZ = 12f }
                                .background(Color(0xFF676B7B), RoundedCornerShape(12.dp))
                                .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "PASS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                .clip(RoundedCornerShape(8.dp))
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
                                style = MaterialTheme.typography.headlineSmall.copy(
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
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFB8BAC7),
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        // 2-Line Strict Bio Text limitation
                        Text(
                            text = item.bio,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF8E91A1),
                                lineHeight = 18.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Match Badges chip row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

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
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FilterSectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = BrandSecondary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun FilterChipCustom(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) BrandPrimary else BrandBorder.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) Color.White else BrandText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun StagePositionPill(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White.copy(alpha = 0.7f), fontSize = 8.sp)
    }
}

@Composable
fun DiscoverFlowRow(
    horizontalGap: androidx.compose.ui.unit.Dp = 4.dp,
    verticalGap: androidx.compose.ui.unit.Dp = 4.dp,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(horizontalGap),
        content = { content() }
    )
}

fun getInstrumentEmoji(subRole: String, category: String): String {
    val r = subRole.lowercase()
    val c = category.lowercase()
    return when {
        r.contains("guitar") || r.contains("acoustic") -> "🎸"
        r.contains("drum") || r.contains("percussion") -> "🥁"
        r.contains("singer") || r.contains("vocalist") || r.contains("vocal") || c.contains("singers") -> "🎤"
        r.contains("composer") || r.contains("arranger") -> "🎼"
        r.contains("keyboard") || r.contains("piano") || r.contains("synth") -> "🎹"
        r.contains("dj") || r.contains("remix") || r.contains("electronic") -> "🎧"
        r.contains("mix") || r.contains("master") || r.contains("engineer") || c.contains("engineers") -> "🎛️"
        r.contains("flute") -> "🪈"
        r.contains("violin") -> "🎻"
        r.contains("trumpet") || r.contains("brass") || r.contains("sax") -> "🎷"
        else -> "🎵"
    }
}

@Composable
fun SocialIntegrationBadge(platform: String, modifier: Modifier = Modifier) {
    val (iconColor, iconVector) = when (platform.trim().lowercase()) {
        "spotify" -> Pair(Color(0xFF1DB954), Icons.Default.MusicNote)
        "youtube" -> Pair(Color(0xFFFF0000), Icons.Default.PlayArrow)
        "soundcloud" -> Pair(Color(0xFFFF5500), Icons.Default.Cloud)
        "apple", "apple music" -> Pair(Color(0xFFFA243C), Icons.Default.MusicNote)
        "instagram" -> Pair(Color(0xFFE1306C), Icons.Default.CameraAlt)
        "mixcloud" -> Pair(Color(0xFF52AAD4), Icons.Default.Equalizer)
        else -> Pair(Color(0xFFEA4C89), Icons.Default.Link)
    }
    
    Row(
        modifier = modifier
            .background(iconColor.copy(alpha = 0.08f), RoundedCornerShape(999.dp))
            .border(1.dp, iconColor.copy(alpha = 0.25f), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(iconVector, contentDescription = null, tint = iconColor, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(platform, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = iconColor)
    }
}

@Composable
fun PersonaTypeBadge(type: ProfileType, modifier: Modifier = Modifier) {
    val bgColor = when (type) {
        ProfileType.TALENT -> Color(0xFFEA4C89).copy(alpha = 0.08f)
        ProfileType.BAND -> Color(0xFFD93C77).copy(alpha = 0.08f)
        ProfileType.BUSINESS -> Color(0xFF0D0B26).copy(alpha = 0.08f)
        ProfileType.VENUE_EVENT -> Color(0xFFF59E0B).copy(alpha = 0.08f)
    }
    val textColor = when (type) {
        ProfileType.TALENT -> Color(0xFFEA4C89)
        ProfileType.BAND -> Color(0xFFD93C77)
        ProfileType.BUSINESS -> Color(0xFF0D0B26)
        ProfileType.VENUE_EVENT -> Color(0xFFD97706)
    }
    val label = when (type) {
        ProfileType.TALENT -> "Solo Artist Persona"
        ProfileType.BAND -> "Band / Group Persona"
        ProfileType.BUSINESS -> "Studio / Business Persona"
        ProfileType.VENUE_EVENT -> "Venue / Event Persona"
    }
    val vectorIcon = when (type) {
        ProfileType.TALENT -> Icons.Default.Person
        ProfileType.BAND -> Icons.Default.Group
        ProfileType.BUSINESS -> Icons.Default.Business
        ProfileType.VENUE_EVENT -> Icons.Default.Place
    }
    
    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(999.dp))
            .border(1.dp, textColor.copy(alpha = 0.2f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(vectorIcon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// =========================================================================
// SECTION 25: WHATSAPP LANDING PAGES & MICRO CHECKMARK SUCCESS ANIMATION
// =========================================================================

@Composable
fun MicroCheckmarkSuccessAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "micro_checkmark")
    val strokePercent by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stroke_draw"
    )
    Canvas(modifier = modifier.size(16.dp)) {
        val width = size.width
        val height = size.height
        
        // Circular background outline
        drawCircle(
            color = BrandSuccess,
            radius = width / 2f,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
        )
        
        // Animated checkmark path
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(width * 0.28f, height * 0.5f)
            lineTo(width * 0.45f, height * 0.65f)
            lineTo(width * 0.72f, height * 0.35f)
        }
        
        val pathMeasure = androidx.compose.ui.graphics.PathMeasure()
        pathMeasure.setPath(path, false)
        val animatedPath = androidx.compose.ui.graphics.Path()
        pathMeasure.getSegment(0f, pathMeasure.length * strokePercent, animatedPath, true)
        
        drawPath(
            path = animatedPath,
            color = BrandSuccess,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun WhatsAppShareSheet(
    profile: DiscoverProfile,
    onDismiss: () -> Unit
) {
    var isCopied by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .widthIn(max = 480.dp),
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Share Portfolio Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandText
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Promote this certified profile to external clients and partners via WhatsApp. Landing page previews are automatically generated.",
                    fontSize = 12.sp,
                    color = BrandMuted
                )
                
                Text(
                    text = "WHATSAPP CHAT LINK PREVIEW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandSecondary,
                    letterSpacing = 1.sp
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(profile.avatarColorHex).copy(alpha = 0.35f),
                                            Color(profile.avatarColorHex).copy(alpha = 0.08f)
                                        )
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
                            
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Verified status",
                                        tint = BrandSuccess,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("MusicLinkd Pro", fontSize = 8.sp, color = BrandText, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandText
                                )
                                if (profile.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Artist",
                                        tint = BrandPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            
                            Text(
                                text = "${profile.subRole} • ${profile.location}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                            
                            Text(
                                text = profile.bio.ifEmpty { "Certified professional portfolio page with music streams, availability booking calendar, and client reviews." },
                                fontSize = 10.sp,
                                color = BrandMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = "Link",
                                    tint = BrandSuccess,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "musiclinkd.com/${profile.name.lowercase().replace(" ", "")}",
                                    fontSize = 10.sp,
                                    color = BrandSuccess,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            isCopied = true
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Send to WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    
                    OutlinedButton(
                        onClick = {
                            isCopied = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isCopied) {
                                MicroCheckmarkSuccessAnimation()
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Link",
                                    tint = BrandText,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = if (isCopied) "Copied!" else "Copy Link",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandText
                            )
                        }
                    }
                }
            }
        }
    )
}

