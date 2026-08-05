package com.example.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import com.example.onboarding.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DiscoverUiState(
    val currentScreen: DiscoverScreen = DiscoverScreen.HOME,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val vocalRangeFilter: String? = null, // Specialized for Singers
    val genderFilter: String? = null,    // Specialized for Singers
    val selectedSingerGenre: String? = null, // specialized chip
    val isSingerGrid: Boolean = false,
    
    // Advanced Filters BottomSheet
    val filterRole: String = "",
    val filterLanguage: String = "",
    val filterGenre: String = "",
    val filterCity: String = "",
    val filterArea: String = "",
    val filterAvailability: String = "",
    val filterVerification: Boolean = false,
    val filterExperienceLevel: String = "",
    val filterRemoteAvailable: Boolean = false,
    val filterSessionAvailable: Boolean = false,
    val filterSortBy: String = "Most Relevant",
    
    // Currently Active Profile Details
    val selectedProfile: DiscoverProfile? = null,
    
    // Saved / Shortlisted profiles
    val savedProfileIds: Set<String> = emptySet(),
    val savedNotes: Map<String, String> = emptyMap(), // profileId -> custom text notes
    val savedTags: Map<String, List<String>> = emptyMap(),  // profileId -> category tags
    val activeSavedTab: String = "Talent", // Talent, Bands, Studios, Businesses
    
    // Hire Modal States
    val activeHireProfile: DiscoverProfile? = null,
    val hireSubmittedMessage: String? = null,
    
    // Swipe list (Match recommendations)
    val tinderSwipeIndex: Int = 0, // Aliased field for compatibility
    val matchSwipeIndex: Int = 0,
    val swipeInteractionLog: List<String> = emptyList() // logging swiped actions
)

class DiscoverViewModel : ViewModel() {
    private val _state = MutableStateFlow(DiscoverUiState())
    val state: StateFlow<DiscoverUiState> = _state.asStateFlow()

    // Navigation Stack for internal flow navigation back-actions
    private val backStack = mutableListOf<DiscoverScreen>()

    init {
        // Initial setup can go here
    }

    // Navigation logic
    fun navigateTo(screen: DiscoverScreen, profile: DiscoverProfile? = null) {
        backStack.add(_state.value.currentScreen)
        _state.update {
            it.copy(
                currentScreen = screen,
                selectedProfile = profile ?: it.selectedProfile
            )
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            val destination = backStack.removeAt(backStack.size - 1)
            _state.update { it.copy(currentScreen = destination) }
            return true
        }
        return false // Stack empty, propagate back action or close flow
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: String?) {
        _state.update { it.copy(selectedCategory = category) }
        if (category == "Singers") {
            navigateTo(DiscoverScreen.SINGER_DIRECTORY)
        } else {
            navigateTo(DiscoverScreen.SEARCH_RESULTS)
        }
    }

    fun toggleSingerVocalRange(vocalRange: String?) {
        _state.update {
            val currentVal = if (it.vocalRangeFilter == vocalRange) null else vocalRange
            it.copy(vocalRangeFilter = currentVal)
        }
    }

    fun toggleSingerGender(gender: String?) {
        _state.update {
            val currentVal = if (it.genderFilter == gender) null else gender
            it.copy(genderFilter = currentVal)
        }
    }

    fun toggleSingerGenre(genre: String?) {
        _state.update {
            val currentVal = if (it.selectedSingerGenre == genre) null else genre
            it.copy(selectedSingerGenre = currentVal)
        }
    }

    fun setSingerGridView(isGrid: Boolean) {
        _state.update { it.copy(isSingerGrid = isGrid) }
    }

    // Toggle Saves
    fun toggleSaveProfile(profileId: String) {
        _state.update { current ->
            val updatedIds = if (current.savedProfileIds.contains(profileId)) {
                current.savedProfileIds - profileId
            } else {
                current.savedProfileIds + profileId
            }
            
            // Auto initialize default tags for a saved profile if not already present
            val updatedTags = current.savedTags.toMutableMap()
            if (!current.savedProfileIds.contains(profileId) && !updatedTags.containsKey(profileId)) {
                val profile = sampleDiscoverProfiles.find { it.id == profileId }
                profile?.let {
                    updatedTags[profileId] = listOf(it.category, "Shortlist")
                }
            }
            
            current.copy(
                savedProfileIds = updatedIds,
                savedTags = updatedTags
            )
        }
    }

    fun updateSavedProfileNotes(profileId: String, note: String) {
        _state.update { current ->
            val updatedNotes = current.savedNotes + (profileId to note)
            current.copy(savedNotes = updatedNotes)
        }
    }

    fun addSavedProfileTag(profileId: String, newTag: String) {
        if (newTag.trim().isEmpty()) return
        _state.update { current ->
            val currentTags = current.savedTags[profileId] ?: emptyList()
            if (!currentTags.contains(newTag)) {
                val updatedTags = current.savedTags + (profileId to (currentTags + newTag.trim()))
                current.copy(savedTags = updatedTags)
            } else {
                current
            }
        }
    }

    fun removeSavedProfileTag(profileId: String, tagToRemove: String) {
        _state.update { current ->
            val currentTags = current.savedTags[profileId] ?: emptyList()
            val updatedTags = current.savedTags + (profileId to currentTags.filter { it != tagToRemove })
            current.copy(savedTags = updatedTags)
        }
    }

    fun changeSavedActiveTab(tab: String) {
        _state.update { it.copy(activeSavedTab = tab) }
    }

    // Update Advanced Filters
    fun updateAdvancedFilters(
        role: String,
        language: String,
        genre: String,
        city: String,
        area: String,
        availability: String,
        isVerified: Boolean,
        xp: String,
        remote: Boolean,
        session: Boolean,
        sortBy: String
    ) {
        _state.update {
            it.copy(
                filterRole = role,
                filterLanguage = language,
                filterGenre = genre,
                filterCity = city,
                filterArea = area,
                filterAvailability = availability,
                filterVerification = isVerified,
                filterExperienceLevel = xp,
                filterRemoteAvailable = remote,
                filterSessionAvailable = session,
                filterSortBy = sortBy
            )
        }
    }

    fun setRoleFilter(role: String) {
        _state.update { 
            val newRole = if (it.filterRole == role) "" else role
            it.copy(filterRole = newRole) 
        }
    }

    fun setGenreFilter(genre: String) {
        _state.update { 
            val newGenre = if (it.filterGenre == genre) "" else genre
            it.copy(filterGenre = newGenre) 
        }
    }

    fun setCategoryFilter(category: String?) {
        _state.update { 
            val newCat = if (it.selectedCategory == category) null else category
            it.copy(selectedCategory = newCat) 
        }
    }

    fun setExperienceFilter(xp: String) {
        _state.update { 
            val newXp = if (it.filterExperienceLevel == xp) "" else xp
            it.copy(filterExperienceLevel = newXp) 
        }
    }

    fun toggleVerificationFilter() {
        _state.update { it.copy(filterVerification = !it.filterVerification) }
    }

    fun toggleRemoteFilter() {
        _state.update { it.copy(filterRemoteAvailable = !it.filterRemoteAvailable) }
    }

    fun toggleSessionFilter() {
        _state.update { it.copy(filterSessionAvailable = !it.filterSessionAvailable) }
    }

    fun clearAllFilters() {
        _state.update {
            it.copy(
                searchQuery = "",
                selectedCategory = null,
                filterRole = "",
                filterLanguage = "",
                filterGenre = "",
                filterCity = "",
                filterArea = "",
                filterAvailability = "",
                filterVerification = false,
                filterExperienceLevel = "",
                filterRemoteAvailable = false,
                filterSessionAvailable = false,
                vocalRangeFilter = null,
                genderFilter = null,
                selectedSingerGenre = null
            )
        }
    }

    fun resetAdvancedFilters() {
        _state.update {
            it.copy(
                filterRole = "",
                filterLanguage = "",
                filterGenre = "",
                filterCity = "",
                filterArea = "",
                filterAvailability = "",
                filterVerification = false,
                filterExperienceLevel = "",
                filterRemoteAvailable = false,
                filterSessionAvailable = false,
                filterSortBy = "Most Relevant"
            )
        }
    }

    // Match Swiping Interaction
    fun swipeLeft() {
        _state.update { current ->
            val nextIndex = (current.matchSwipeIndex + 1) % sampleDiscoverProfiles.size
            val updatedLog = current.swipeInteractionLog + "Skipped ${sampleDiscoverProfiles[current.matchSwipeIndex].name}"
            current.copy(
                tinderSwipeIndex = nextIndex,
                matchSwipeIndex = nextIndex,
                swipeInteractionLog = updatedLog
            )
        }
    }

    fun swipeRight() {
        val currentProfile = sampleDiscoverProfiles[_state.value.matchSwipeIndex]
        toggleSaveProfile(currentProfile.id)
        _state.update { current ->
            val nextIndex = (current.matchSwipeIndex + 1) % sampleDiscoverProfiles.size
            val updatedLog = current.swipeInteractionLog + "Saved & Starred ${currentProfile.name}"
            current.copy(
                tinderSwipeIndex = nextIndex,
                matchSwipeIndex = nextIndex,
                swipeInteractionLog = updatedLog
            )
        }
    }

    // Contact Hire Flow
    fun openHireModal(profile: DiscoverProfile) {
        _state.update {
            it.copy(
                activeHireProfile = profile,
                hireSubmittedMessage = null
            )
        }
    }

    fun closeHireModal() {
        _state.update { it.copy(activeHireProfile = null, hireSubmittedMessage = null) }
    }

    fun submitHireRequest(projectName: String, budget: String, timeline: String, message: String) {
        val profileName = _state.value.activeHireProfile?.name ?: "Professional"
        val successConfirmMsg = "Request submitted successfully to $profileName! They will respond via Messages. Budget: $budget, Timeline: $timeline."
        _state.update {
            it.copy(hireSubmittedMessage = successConfirmMsg)
        }
    }

    // Get profiles matching current search input + selected category + advanced filters
    fun getFilteredProfiles(): List<DiscoverProfile> {
        val s = _state.value
        return sampleDiscoverProfiles.filter { profile ->
            // 1. Search Query Filters
            if (s.searchQuery.isNotEmpty()) {
                val matchesQuery = profile.name.contains(s.searchQuery, ignoreCase = true) ||
                        profile.subRole.contains(s.searchQuery, ignoreCase = true) ||
                        profile.bio.contains(s.searchQuery, ignoreCase = true) ||
                        profile.genres.any { it.contains(s.searchQuery, ignoreCase = true) } ||
                        profile.location.contains(s.searchQuery, ignoreCase = true)
                if (!matchesQuery) return@filter false
            }

            // 2. Selective Category Filter
            if (s.selectedCategory != null) {
                if (profile.category != s.selectedCategory) return@filter false
            }

            // 3. Specialized Singer Filters
            if (s.selectedCategory == "Singers" || s.currentScreen == DiscoverScreen.SINGER_DIRECTORY) {
                if (s.vocalRangeFilter != null && profile.vocalRange != s.vocalRangeFilter) return@filter false
                if (s.genderFilter != null && profile.gender != s.genderFilter) return@filter false
                if (s.selectedSingerGenre != null && !profile.genres.contains(s.selectedSingerGenre)) return@filter false
            }

            // 4. Advanced Filters (from bottom sheet)
            if (s.filterRole.isNotEmpty() && !profile.subRole.contains(s.filterRole, ignoreCase = true)) return@filter false
            if (s.filterLanguage.isNotEmpty() && !profile.languages.any { it.contains(s.filterLanguage, ignoreCase = true) }) return@filter false
            if (s.filterGenre.isNotEmpty() && !profile.genres.any { it.contains(s.filterGenre, ignoreCase = true) }) return@filter false
            if (s.filterCity.isNotEmpty() && !profile.location.contains(s.filterCity, ignoreCase = true)) return@filter false
            if (s.filterArea.isNotEmpty() && !profile.area.contains(s.filterArea, ignoreCase = true)) return@filter false
            if (s.filterAvailability.isNotEmpty() && profile.availabilityStatus != s.filterAvailability) return@filter false
            if (s.filterVerification && !profile.isVerified) return@filter false
            if (s.filterExperienceLevel.isNotEmpty() && profile.experienceLevel != s.filterExperienceLevel) return@filter false
            if (s.filterRemoteAvailable && !profile.remoteWork) return@filter false
            if (s.filterSessionAvailable && !profile.sessionMusician) return@filter false

            true
        }.let { list ->
            // Sort list based on chosen Sort Rule
            when (s.filterSortBy) {
                "Most Followed" -> list.sortedByDescending { it.followers }
                "Highest Rated" -> list.sortedByDescending { it.rating }
                else -> list // "Most Relevant" default unsorted or distance
            }
        }
    }
}
