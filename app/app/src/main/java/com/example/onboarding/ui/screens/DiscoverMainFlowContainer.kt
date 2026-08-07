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
