package com.example.onboarding.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class PortfolioScreen {
    PORTFOLIO_DASHBOARD,    // Screen 1
    CHOOSE_PROJECT_TYPE,    // Screen 2
    PROJECT_INFORMATION,    // Screen 3
    ADD_STREAMING_LINKS,    // Screen 4
    SELECT_YOUR_ROLE,      // Screen 5
    ADD_COLLABORATORS,     // Screen 6
    CREDIT_ATTRIBUTION,    // Screen 7
    PROJECT_MEDIA_PREVIEW,  // Screen 8
    PUBLISH_PROJECT,       // Screen 9
    SUCCESS_STATE,         // Screen 10
    PROFILE_PROJECT_VIEW   // Screen 11
}

data class PortfolioProject(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val type: String = "",
    val releaseDate: String = "",
    val status: String = "Released", // Released, Upcoming, In Production, Draft
    val spotifyUrl: String = "",
    val appleMusicUrl: String = "",
    val youtubeUrl: String = "",
    val soundcloudUrl: String = "",
    val mixcloudUrl: String = "",
    val instagramReelUrl: String = "",
    val roles: Set<String> = emptySet(),
    val collaborators: List<ProjectCollaborator> = emptyList(),
    val credits: List<CreditItem> = emptyList(),
    val visibility: String = "Public", // Public, Connections Only, Private Draft
    val artworkUrl: String = "Default",
    val projectViews: Int = 0,
    val likes: Int = 0,
    val shares: Int = 0
)

data class ProjectCollaborator(
    val id: String,
    val name: String,
    val role: String,
    val avatarEmoji: String,
    val isVerified: Boolean = true,
    val imageUrl: String? = null
)

data class CreditItem(
    val collaboratorName: String,
    val role: String,
    val avatarEmoji: String,
    val imageUrl: String? = null
)

data class PortfolioState(
    // Current Active screen in the Add Project navigation sub-flow
    val activeScreen: PortfolioScreen = PortfolioScreen.PORTFOLIO_DASHBOARD,
    
    // Portfolio stats
    val projectsCount: Int = 4,
    val creditsCount: Int = 12,
    val profileViews: Int = 340,
    
    // Existing projects list
    val projectsList: List<PortfolioProject> = getSampleProjects(),
    
    // Form fields for currently adding project
    val addingProjectName: String = "",
    val addingProjectDesc: String = "",
    val addingProjectDate: String = "",
    val addingProjectStatus: String = "Released",
    val addingProjectType: String = "Song",
    
    // Streaming urls
    val spotifyUrl: String = "",
    val appleMusicUrl: String = "",
    val youtubeUrl: String = "",
    val soundcloudUrl: String = "",
    val mixcloudUrl: String = "",
    val instagramReelUrl: String = "",
    
    // Form selections
    val selectedRoles: Set<String> = emptySet(),
    val taggedCollaborators: List<ProjectCollaborator> = emptyList(),
    val credits: List<CreditItem> = emptyList(),
    val visibility: String = "Public",
    
    // Search query for collaborators
    val searchQuery: String = "",
    
    // Last published project preview object
    val lastPublishedProject: PortfolioProject? = null
)

data class ProjectTypeItem(
    val name: String,
    val icon: ImageVector,
    val description: String
)

val availableProjectTypes = listOf(
    ProjectTypeItem("Song", Icons.Default.MusicNote, "Single audio piece, vocal or instrumental track"),
    ProjectTypeItem("Album", Icons.Default.Album, "Full length release or curated song list"),
    ProjectTypeItem("EP", Icons.Default.LibraryMusic, "Extended play release of 3-5 tracks"),
    ProjectTypeItem("Podcast", Icons.Default.Podcasts, "Multi-episode talk, discussion, or story series"),
    ProjectTypeItem("DJ Mix", Icons.Default.GraphicEq, "Continuous mix or electronic set"),
    ProjectTypeItem("Film Score", Icons.Default.Audiotrack, "Original instrumental theme or cue for film"),
    ProjectTypeItem("Commercial Jingle", Icons.Default.VolumeUp, "Advertising audio, intro theme, or branding"),
    ProjectTypeItem("Live Performance", Icons.Default.InterpreterMode, "Concert recording, live session, or broadcast"),
    ProjectTypeItem("Music Video", Icons.Default.VideoLibrary, "Official cinematic video clip or visual release"),
    ProjectTypeItem("Custom Project", Icons.Default.Widgets, "Any audio project with a custom format")
)

val availableProjectRoles = listOf(
    "Singer", "Composer", "Music Director", "Producer", "Lyricist",
    "Session Musician", "DJ", "Mix Engineer", "Mastering Engineer",
    "Sound Designer", "Band Member"
)

val globalCollaboratorDirectory = listOf(
    ProjectCollaborator("collab_1", "Tara Sharma", "Pop Singer & Vocalist", "", true, "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=300&q=80"),
    ProjectCollaborator("collab_2", "Arijit Singh", "Playback Singer", "", true, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=300&q=80"),
    ProjectCollaborator("collab_3", "Amit Trivedi", "Composer / Producer", "", true, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80"),
    ProjectCollaborator("collab_4", "Rahul Sharma", "Mix Engineer", "", true, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=300&q=80"),
    ProjectCollaborator("collab_5", "Karan Malhotra", "Rapper / Lyricist", "", true, "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=300&q=80"),
    ProjectCollaborator("collab_6", "Divine", "Hip-Hop Lyricist", "", true, "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=300&q=80"),
    ProjectCollaborator("collab_7", "Sid Sriram", "Carnatic Playback Singer", "", true, "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=300&q=80"),
    ProjectCollaborator("collab_8", "Sandeep Chowta", "Music Director", "", true, "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?auto=format&fit=crop&w=300&q=80"),
    ProjectCollaborator("collab_9", "Karan Kanchan", "Music Producer", "", true, "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=300&q=80")
)

fun getSampleProjects(): List<PortfolioProject> {
    return listOf(
        PortfolioProject(
            id = "proj_1",
            name = "Tum Se Hi (Reimagined)",
            description = "A soft lo-fi acoustic reimaging of the Bollywood classic, blended with classical Hindustani flute accompaniment and deep sub-bass synth elements.",
            type = "Song",
            releaseDate = "2026-04-12",
            status = "Released",
            spotifyUrl = "https://spotify.com/track/tumsehireimagined",
            youtubeUrl = "https://youtube.com/watch?v=tumsehiacoustic",
            roles = setOf("Composer", "Producer"),
            collaborators = listOf(
                ProjectCollaborator("c_1", "Tara Sharma", "Singer", ""),
                ProjectCollaborator("c_2", "Rahul Sharma", "Mix Engineer", "")
            ),
            credits = listOf(
                CreditItem("Vedanshu Srivastava", "Composer & Producer", ""),
                CreditItem("Tara Sharma", "Singer", ""),
                CreditItem("Rahul Sharma", "Mix Engineer", "")
            ),
            artworkUrl = "Acoustic",
            projectViews = 1205,
            likes = 342,
            shares = 54
        ),
        PortfolioProject(
            id = "proj_2",
            name = "Midnight In Mumbai Studio Mix",
            description = "A late-night progressive techno set recorded live at Bandra studio, showcasing unreleased ambient soundscapes and regional vocal chops.",
            type = "DJ Mix",
            releaseDate = "2026-05-30",
            status = "Released",
            soundcloudUrl = "https://soundcloud.com/midnightmumbai",
            roles = setOf("DJ", "Sound Designer"),
            artworkUrl = "Techno",
            projectViews = 840,
            likes = 189,
            shares = 42
        ),
        PortfolioProject(
            id = "proj_3",
            name = "The Himalayan Echoes",
            description = "An atmospheric acoustic instrumental EP inspired by regional travel, using local folk instruments layered with high fidelity synth arrangements.",
            type = "EP",
            releaseDate = "2026-07-20",
            status = "Upcoming",
            roles = setOf("Composer", "Session Musician"),
            artworkUrl = "Folk",
            projectViews = 0,
            likes = 0,
            shares = 0
        )
    )
}
