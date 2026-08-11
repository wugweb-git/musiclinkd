package com.example.onboarding.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

enum class OnboardingScreen {
    SPLASH,
    WELCOME,
    MOBILE_AUTH,
    OTP_VERIFY,
    EMAIL_AUTH, // New Screen for Firebase Email/Password Auth
    ROLE_PRIMARY,
    ROLE_SECONDARY,
    LANGUAGES,
    GENRES,
    INSTRUMENTS, // New Screen for Instrument & Proficiency selection
    LOCATION,
    PROFILE_CREATION,
    SUCCESS,
    DASHBOARD,
    METADATA_SYNC
}

data class SkillProficiency(
    val yearsOfExperience: String = "", // e.g. "1-2 Years", "3-5 Years", "5+ Years"
    val proficiencyLevel: String = "", // e.g. "Beginner", "Intermediate", "Advanced", "Expert"
    val portfolioProofUrl: String = "" // e.g. Spotify, YouTube, SoundCloud reference links
)

data class OnboardingState(
    val currentScreen: OnboardingScreen = OnboardingScreen.SPLASH,
    val mobileNumber: String = "",
    val otp: String = "",
    val primaryRole: String? = null,
    val secondaryRole: String? = null,
    val selectedLanguages: Set<String> = emptySet(),
    val selectedGenres: Set<String> = emptySet(),
    
    // New states for experience and instrument plays mapping
    val selectedInstruments: Set<String> = emptySet(),
    val instrumentProficiency: Map<String, SkillProficiency> = emptyMap(),
    val roleExperience: SkillProficiency = SkillProficiency(),
    val networkingLinks: Map<String, String> = emptyMap(), // Map platform -> link URL
    
    // Location state
    val country: String = "India",
    val state: String = "",
    val city: String = "",
    val area: String = "",
    
    // Profile state
    val fullName: String = "",
    val displayName: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val bio: String = "",
    val selectedAvatarIndex: Int = 0,
    val profilePicturePath: String? = null,
    val coverPicturePath: String? = null,
    
    // Validation flags
    val isOtpVerified: Boolean = false,
    val otpError: String? = null,
    val mobileError: String? = null,
    val nameError: String? = null,
    val usernameError: String? = null,
    val bioError: String? = null,
    val stateError: String? = null,
    val cityError: String? = null,
    val proofError: String? = null,
    val linksErrors: Map<String, String> = emptyMap(),
    val languagesError: String? = null,
    val genresError: String? = null,
    
    // Firebase auth fields
    val firebaseEmailInput: String = "",
    val firebasePasswordInput: String = "",
    val isFirebaseAuthLoginMode: Boolean = false,
    val firebaseAuthError: String? = null,
    val isFirebaseAuthLoading: Boolean = false,
    
    // Loading indicator flags
    val isOtpSending: Boolean = false,
    val isOtpVerifying: Boolean = false,
    val isProfileSaving: Boolean = false,
    val activeDashboardTab: String = "Home"
)

data class RoleItem(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val section: String // Categorized role grouping section
)

// Categorized roles inside the professional Indian music ecosystem
val availableRoles = listOf(
    // Performers & Vocalists
    RoleItem("singer", "Singer / Vocalist", "playback singers, lead vocalists, backing vocals", Icons.Default.Mic, "Performers & Vocalists"),
    RoleItem("rapper", "Rapper / MC", "hip-hop vocalists, lyrical speed spitters", Icons.Default.Mic, "Performers & Vocalists"),
    RoleItem("folk_vocalist", "Folk Vocalist", "traditional regional storytellers, classical singers", Icons.Default.Mic, "Performers & Vocalists"),
    
    // Instrumentalists
    RoleItem("guitarist", "Guitarist", "acoustic, electric, slide, bass players", Icons.Default.MusicNote, "Instrumentalists"),
    RoleItem("keyboardist", "Keyboardist / Pianist", "synthesizer, classical piano, organ players", Icons.Default.Piano, "Instrumentalists"),
    RoleItem("drummer", "Drummer / Percussionist", "acoustic drums, tabla, dholak, octapad players", Icons.Default.MusicNote, "Instrumentalists"),
    RoleItem("sitarist", "Sitarist / Flutist", "traditional Indian instruments virtuosos", Icons.Default.MusicNote, "Instrumentalists"),
    
    // Creative & Writing
    RoleItem("composer", "Composer", "songwriters, background score directors, arrangers", Icons.Default.InterpreterMode, "Creative & Writing"),
    RoleItem("lyricist", "Lyricist", "poets, songwriters, script writers", Icons.Default.Create, "Creative & Writing"),
    RoleItem("arranger", "Vocal Arranger", "harmonies, choir coordinators, backing patterns", Icons.Default.InterpreterMode, "Creative & Writing"),

    // Production & Engineering
    RoleItem("producer", "Music Producer", "beatmakers, mixers, arrangers", Icons.Default.Speaker, "Production & Engineering"),
    RoleItem("sound_engineer", "Sound Engineer", "mixing, mastering, recording engineers", Icons.Default.Tune, "Production & Engineering"),
    RoleItem("sound_designer", "Sound Designer", "foley artists, movie sound designers, FX specialists", Icons.Default.GraphicEq, "Production & Engineering"),

    // Business & Management
    RoleItem("artist_manager", "Artist Manager", "talent representatives, agents, booking reps", Icons.Default.Business, "Business & Management"),
    RoleItem("music_label", "Music Label", "record companies, distributors, publishers", Icons.Default.Album, "Business & Management"),
    RoleItem("music_publisher", "Music Publisher", "sync agents, copyright administrators", Icons.Default.Business, "Business & Management"),

    // Spaces & Events
    RoleItem("studio", "Recording Studio", "recording, rehearsal, and writing spaces", Icons.Default.Hearing, "Spaces & Events"),
    RoleItem("event_organizer", "Event Organizer", "promoters, festival bookers, gig organizers", Icons.Default.Event, "Spaces & Events")
)

// Map primary identity to recommended secondary identity choices
val primaryToSecondaryRolesMap = mapOf(
    "Singer / Vocalist" to listOf("Lyricist", "Folk Vocalist", "Vocal Arranger", "Composer"),
    "Rapper / MC" to listOf("Lyricist", "Composer", "Music Producer"),
    "Folk Vocalist" to listOf("Singer / Vocalist", "Lyricist", "Sitarist / Flutist"),
    "Guitarist" to listOf("Drummer / Percussionist", "Keyboardist / Pianist", "Composer", "Music Producer"),
    "Keyboardist / Pianist" to listOf("Composer", "Music Producer", "Sound Engineer"),
    "Drummer / Percussionist" to listOf("Guitarist", "Music Producer", "Sound Engineer"),
    "Sitarist / Flutist" to listOf("Folk Vocalist", "Composer", "Singer / Vocalist"),
    "Composer" to listOf("Music Producer", "Sound Engineer", "Lyricist", "Vocal Arranger"),
    "Lyricist" to listOf("Composer", "Vocal Arranger"),
    "Vocal Arranger" to listOf("Singer / Vocalist", "Composer", "Lyricist"),
    "Music Producer" to listOf("Sound Engineer", "Composer", "Keyboardist / Pianist", "Sound Designer"),
    "Sound Engineer" to listOf("Music Producer", "Sound Designer", "Recording Studio"),
    "Sound Designer" to listOf("Sound Engineer", "Music Producer"),
    "Artist Manager" to listOf("Music Label", "Event Organizer", "Music Publisher"),
    "Music Label" to listOf("Music Publisher", "Artist Manager", "Event Organizer"),
    "Music Publisher" to listOf("Music Label", "Artist Manager"),
    "Recording Studio" to listOf("Sound Engineer", "Event Organizer"),
    "Event Organizer" to listOf("Recording Studio", "Artist Manager", "Music Label")
)

// Indian regional & global languages in native formats
val availableLanguages = listOf(
    "English",
    "Hindi (हिंदी)",
    "Punjabi (ਪੰਜਾਬੀ)",
    "Tamil (தமிழ்)",
    "Telugu (తెలుగు)",
    "Marathi (मराठी)",
    "Gujarati (ગુજરાતી)",
    "Kannada (ਕನ್ನಡ)",
    "Malayalam (മലയാളം)",
    "Bengali (বাংলা)",
    "Assamese (অসমੀয়া)",
    "Odia (ଓଡ଼ਿଆ)",
    "Urdu (اردو)",
    "Sanskrit (संस्कृत)",
    "Spanish (Español)",
    "French (Français)",
    "German (Deutsch)",
    "Japanese (日本語)",
    "Korean (한국어)"
)

// Detailed musical genres
val availableGenres = listOf(
    "Bollywood / Filmi", "Indie Pop", "Hindustani Classical", "Carnatic Classical",
    "Punjabi Folk / Bhangra", "Rajasthani Folk", "Sufi Devotional", "Ghazal",
    "Qawwali", "Rock / Metal", "Hip Hop / Trap", "Lofi / Chill Beats",
    "EDM / Synthwave", "Acoustic / Unplugged", "R&B / Soul", "Jazz / Blues", "Reggae / Dub"
)

// Instrument options with visual representations
data class InstrumentItem(
    val name: String,
    val emoji: String
)

val availableInstruments = listOf(
    InstrumentItem("Acoustic Guitar", "🎸"),
    InstrumentItem("Electric Guitar", "🎸"),
    InstrumentItem("Bass Guitar", "🎸"),
    InstrumentItem("Keyboard / Synthesizer", "🎹"),
    InstrumentItem("Piano", "🎹"),
    InstrumentItem("Drums / Percussion", "🥁"),
    InstrumentItem("Tabla", "🥁"),
    InstrumentItem("Sitar", "🪕"),
    InstrumentItem("Flute (Bansuri)", "💨"),
    InstrumentItem("Harmonium", "🎹"),
    InstrumentItem("Dholak", "🥁"),
    InstrumentItem("Violin", "🎻"),
    InstrumentItem("Saxophone", "🎷"),
    InstrumentItem("Trumpet", "🎺"),
    InstrumentItem("Sarod", "🪕"),
    InstrumentItem("Shehnai", "🎷"),
    InstrumentItem("Mridangam", "🥁")
)

// Enforce instrument applicability rule: Lyricists or Business roles do not typically play instruments
fun canRolePlayInstruments(role: String?): Boolean {
    if (role == null) return true
    val nonPerformingRoles = setOf(
        "Lyricist", "Artist Manager", "Music Label", "Music Publisher", "Event Organizer",
        "lyricist", "artist_manager", "music_label", "music_publisher", "event_organizer"
    )
    return !nonPerformingRoles.contains(role)
}

fun getRoleLabel(role: String?): String {
    if (role == null) return ""
    return availableRoles.find { it.id.equals(role, ignoreCase = true) || it.name.equals(role, ignoreCase = true) }?.name ?: role
}

fun getLanguageLabel(lang: String?): String {
    if (lang == null) return ""
    return availableLanguages.find { it.id.equals(lang, ignoreCase = true) || it.name.equals(lang, ignoreCase = true) }?.name ?: lang
}

fun getGenreLabel(genre: String?): String {
    if (genre == null) return ""
    return availableGenres.find { it.id.equals(genre, ignoreCase = true) || it.name.equals(genre, ignoreCase = true) }?.name ?: genre
}

fun getInstrumentLabel(instrument: String?): String {
    if (instrument == null) return ""
    return availableInstruments.find { it.id.equals(instrument, ignoreCase = true) || it.name.equals(instrument, ignoreCase = true) }?.name ?: instrument
}


// Simulated music avatars
data class AvatarItem(
    val id: Int,
    val name: String,
    val colorHex: Long,
    val letter: String,
    val imageUrl: String
)

val availableAvatars = listOf(
    AvatarItem(0, "Acoustic Gold", 0xFFFFB74D, "🎤", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80"),
    AvatarItem(1, "Neon Beats", 0xFF81C784, "🎧", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80"),
    AvatarItem(2, "Vibe Controller", 0xFF64B5F6, "🎛️", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&auto=format&fit=crop&q=80"),
    AvatarItem(3, "Bass Master", 0xFFE57373, "🎸", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop&q=80"),
    AvatarItem(4, "Studio Pro", 0xFFBA68C8, "🎹", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&auto=format&fit=crop&q=80"),
    AvatarItem(5, "Lyricist", 0xFF4DB6AC, "✍️", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=300&auto=format&fit=crop&q=80")
)

// Recommended Connections for local preview
data class ConnectionItem(
    val name: String,
    val role: String,
    val location: String,
    val matchScore: Int,
    val imageUrl: String,
    val mutuals: Int
)

val recommendedConnections = listOf(
    ConnectionItem("Kabir Sethi", "Indie Singer / Songwriter", "Mumbai, West", 98, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80", 14),
    ConnectionItem("Aanya Malhotra", "Alternative Vocalist", "Delhi, NCR", 95, "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80", 8),
    ConnectionItem("Rohan Deshmukh", "Acoustic Producer", "Pune, MH", 92, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400&auto=format&fit=crop&q=80", 21),
    ConnectionItem("Sanjana Sen", "Harpist & Composer", "Bangalore, KA", 89, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80", 4),
    ConnectionItem("Nikhil Verma", "Live Sound Engineer", "Mumbai, Suburban", 86, "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=400&auto=format&fit=crop&q=80", 11)
)

// Trending Opportunities
data class OpportunityItem(
    val title: String,
    val poster: String,
    val type: String,
    val pay: String,
    val matches: Boolean
)

val trendingOpportunities = listOf(
    OpportunityItem("Indie Rock Vocalist Wanted", "The Sound Attic", "Playback Project", "₹15,000 / Session", true),
    OpportunityItem("Lyricist for Hindi-Pop Single", "Sutra Records", "Independent Single", "₹8,000 / Song", true),
    OpportunityItem("Acoustic Guitarist for EP", "Cornerstone Studio", "EP Session", "Under Discussion", false),
    OpportunityItem("Need Sound Designer for Short Film", "Red Room Media", "Short Film", "₹30,000 / Project", true)
)

// Nearby Studios
data class StudioItem(
    val name: String,
    val location: String,
    val distance: String,
    val rate: String,
    val rating: String,
    val imageUrl: String
)

val nearbyStudios = listOf(
    StudioItem("The Hive Recording Studio", "Andheri West, Mumbai", "1.2 km", "₹1,200/hr", "4.8 ★", "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=300&q=80"),
    StudioItem("Octave Sound Rehearsals", "Bandra Link Road, Mumbai", "3.4 km", "₹800/hr", "4.6 ★", "https://images.unsplash.com/photo-1511192336575-5a79af67a629?auto=format&fit=crop&w=300&q=80"),
    StudioItem("Blue Room Mixing Suite", "Juhu, Mumbai", "5.1 km", "₹1,500/hr", "4.9 ★", "https://images.unsplash.com/photo-1516280440614-37939bbacd6a?auto=format&fit=crop&w=300&q=80")
)
