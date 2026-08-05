package com.example.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import com.example.onboarding.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class PortfolioViewModel : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState())
    val state: StateFlow<PortfolioState> = _state.asStateFlow()

    // Screen navigation stack to support back transitions cleanly
    private val screenStack = mutableListOf<PortfolioScreen>()

    fun navigateTo(screen: PortfolioScreen) {
        screenStack.add(_state.value.activeScreen)
        _state.update { it.copy(activeScreen = screen) }
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            val prev = screenStack.removeAt(screenStack.size - 1)
            _state.update { it.copy(activeScreen = prev) }
            return true
        }
        return false
    }

    fun resetFlow() {
        screenStack.clear()
        _state.update {
            PortfolioState(
                projectsList = it.projectsList // Preserve existing projects
            )
        }
    }

    fun updateProjectType(type: String) {
        _state.update { it.copy(addingProjectType = type) }
    }

    fun updateProjectDetails(name: String, desc: String, date: String, status: String) {
        _state.update {
            it.copy(
                addingProjectName = name,
                addingProjectDesc = desc,
                addingProjectDate = date,
                addingProjectStatus = status
            )
        }
    }

    fun updateStreamingUrls(
        spotify: String = _state.value.spotifyUrl,
        apple: String = _state.value.appleMusicUrl,
        youtube: String = _state.value.youtubeUrl,
        soundcloud: String = _state.value.soundcloudUrl,
        mixcloud: String = _state.value.mixcloudUrl,
        instagram: String = _state.value.instagramReelUrl
    ) {
        _state.update {
            it.copy(
                spotifyUrl = spotify,
                appleMusicUrl = apple,
                youtubeUrl = youtube,
                soundcloudUrl = soundcloud,
                mixcloudUrl = mixcloud,
                instagramReelUrl = instagram
            )
        }
    }

    fun toggleRole(role: String) {
        _state.update {
            val updated = it.selectedRoles.toMutableSet()
            if (updated.contains(role)) {
                updated.remove(role)
            } else {
                updated.add(role)
            }
            it.copy(selectedRoles = updated)
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun tagCollaborator(collab: ProjectCollaborator) {
        _state.update {
            val current = it.taggedCollaborators.toMutableList()
            if (current.none { existing -> existing.id == collab.id }) {
                current.add(collab)
            }
            
            // Auto initialize credit mapping for this collaborator
            val currentCredits = it.credits.toMutableList()
            if (currentCredits.none { credit -> credit.collaboratorName == collab.name }) {
                currentCredits.add(CreditItem(collab.name, collab.role.substringBefore(" &").substringBefore(" /"), collab.avatarEmoji))
            }

            it.copy(
                taggedCollaborators = current,
                credits = currentCredits
            )
        }
    }

    fun untagCollaborator(collabId: String) {
        _state.update { state ->
            val collab = state.taggedCollaborators.find { it.id == collabId }
            val current = state.taggedCollaborators.filterNot { it.id == collabId }
            val currentCredits = state.credits.filterNot { it.collaboratorName == collab?.name }
            state.copy(
                taggedCollaborators = current,
                credits = currentCredits
            )
        }
    }

    fun updateCollaboratorCreditRole(collaboratorName: String, assignedRole: String) {
        _state.update { state ->
            val updatedCredits = state.credits.map {
                if (it.collaboratorName == collaboratorName) {
                    it.copy(role = assignedRole)
                } else {
                    it
                }
            }
            state.copy(credits = updatedCredits)
        }
    }

    fun updateVisibility(vis: String) {
        _state.update { it.copy(visibility = vis) }
    }

    // This compiles form fields into a finished project object and adds to profile
    fun publishProject() {
        val s = _state.value
        
        // Define a randomized beautiful category color for the artwork placeholder
        val artworkName = when (s.addingProjectType) {
            "Song", "Single" -> "Acoustic"
            "Album", "EP" -> "Sunset"
            "Podcast" -> "Studio"
            "DJ Mix" -> "Techno"
            "Film Score" -> "Cinematic"
            else -> "Purple"
        }

        // Gather final credits. Add modern self credit too
        val myName = "Vedanshu Srivastava"
        val myPrimaryRole = if (s.selectedRoles.isEmpty()) "Creator" else s.selectedRoles.first()
        val selfCredit = CreditItem(myName, myPrimaryRole, "🎹")
        
        val compiledCredits = mutableListOf<CreditItem>()
        compiledCredits.add(selfCredit)
        compiledCredits.addAll(s.credits)

        val newProject = PortfolioProject(
            id = "proj_" + System.currentTimeMillis() + "_" + Random.nextInt(100, 999),
            name = s.addingProjectName.ifEmpty { "Untitled Soundscapes" },
            description = s.addingProjectDesc.ifEmpty { "No description available for this " + s.addingProjectType.lowercase() + " yet." },
            type = s.addingProjectType,
            releaseDate = s.addingProjectDate.ifEmpty { "June 2026" },
            status = s.addingProjectStatus,
            spotifyUrl = s.spotifyUrl,
            appleMusicUrl = s.appleMusicUrl,
            youtubeUrl = s.youtubeUrl,
            soundcloudUrl = s.soundcloudUrl,
            mixcloudUrl = s.mixcloudUrl,
            instagramReelUrl = s.instagramReelUrl,
            roles = s.selectedRoles.ifEmpty { setOf("Producer") },
            collaborators = s.taggedCollaborators,
            credits = compiledCredits,
            visibility = s.visibility,
            artworkUrl = artworkName,
            projectViews = 0,
            likes = 0,
            shares = 0
        )

        _state.update {
            val updatedList = listOf(newProject) + it.projectsList // Insert at the top of My Projects!
            it.copy(
                projectsList = updatedList,
                projectsCount = updatedList.size,
                lastPublishedProject = newProject,
                activeScreen = PortfolioScreen.SUCCESS_STATE
            )
        }
    }

    fun previewPublishedProject() {
        _state.update {
            it.copy(activeScreen = PortfolioScreen.PROFILE_PROJECT_VIEW)
        }
    }

    fun addIncrementStats(projId: String, statType: String) {
        _state.update { state ->
            val updated = state.projectsList.map {
                if (it.id == projId) {
                    when (statType) {
                        "view" -> it.copy(projectViews = it.projectViews + 1)
                        "like" -> it.copy(likes = it.likes + 1)
                        "share" -> it.copy(shares = it.shares + 1)
                        else -> it
                    }
                } else it
            }
            val lastPub = if (state.lastPublishedProject?.id == projId) {
                updated.find { it.id == projId }
            } else state.lastPublishedProject

            state.copy(
                projectsList = updated,
                lastPublishedProject = lastPub
            )
        }
    }

    fun preloadQuickFill() {
        _state.update {
            it.copy(
                addingProjectName = "Swar Sandhya EP",
                addingProjectDesc = "A premium crossover compilation of acoustic sarangi notes and modern industrial bass elements.",
                addingProjectDate = "2026-06-18",
                addingProjectStatus = "Released",
                addingProjectType = "EP",
                spotifyUrl = "https://spotify.com/album/swarsandhya",
                youtubeUrl = "https://youtube.com/watch?v=swarsandhya",
                soundcloudUrl = "https://soundcloud.com/swarsandhya",
                selectedRoles = setOf("Composer", "Producer", "Sound Designer"),
                taggedCollaborators = listOf(
                    ProjectCollaborator("collab_1", "Tara Sharma", "Pop Singer & Vocalist", "", true),
                    ProjectCollaborator("collab_4", "Rahul Sharma", "Mix Engineer", "", true)
                ),
                credits = listOf(
                    CreditItem("Tara Sharma", "Singer", ""),
                    CreditItem("Rahul Sharma", "Mix Engineer", "")
                )
            )
        }
    }
}
