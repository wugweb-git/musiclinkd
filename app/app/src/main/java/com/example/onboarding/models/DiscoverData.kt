package com.example.onboarding.models

import androidx.compose.ui.graphics.Color

enum class DiscoverScreen {
    HOME,
    CATEGORY_DIRECTORY,
    SEARCH_RESULTS,
    SINGER_DIRECTORY,
    FULL_PROFILE,
    BAND_PROFILE,
    BUSINESS_PROFILE,
    VENUE_PROFILE,
    SAVED_PROFILES,
    RECOMMENDED_MATCHES
}

enum class ProfileType {
    TALENT,
    BAND,
    BUSINESS,
    VENUE_EVENT
}

data class MediaProject(
    val title: String,
    val duration: String,
    val platform: String, // "Spotify" | "YouTube" | "Apple Music"
    val playsCount: String,
    val date: String,
    val audioUrl: String? = null
)

data class Credit(
    val title: String,
    val role: String,
    val artist: String,
    val year: String
)

data class Review(
    val author: String,
    val rating: Double,
    val commentary: String,
    val date: String,
    val avatarColor: Long
)

data class BandMember(
    val name: String,
    val instrument: String,
    val isLeader: Boolean = false,
    val avatarColor: Long
)

data class DiscoverProfile(
    val id: String,
    val type: ProfileType,
    val name: String,
    val category: String, // "Singers", "Musicians", "DJs", "Engineers", "Composers", "Bands", "Studios", "Labels", "Event Organizers", "Music Schools", "Venues"
    val subRole: String, // e.g. "Playback Singer", "Session Guitarist", "Recording Studio"
    val avatarColorHex: Long = 0xFFEA4C89,
    val avatarEmoji: String = "🎤",
    val imageUrl: String? = null,
    val location: String = "Mumbai, MH",
    val area: String = "Andheri West",
    val rating: Double = 4.8,
    val pricing: String = "", // e.g. "₹2,500/hr"
    val followers: Int = 12400,
    val isVerified: Boolean = true,
    val availabilityStatus: String = "Available", // "Available", "Booking Open", "Unavailable"
    val languages: List<String> = listOf("Hindi", "English"),
    val genres: List<String> = listOf("Bollywood", "Indie", "Pop"),
    val bio: String = "",
    val projects: List<MediaProject> = emptyList(),
    val credits: List<Credit> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val equipment: List<String> = emptyList(),
    val hasAtmos: Boolean = false,
    val members: List<BandMember> = emptyList(),
    val vocalRange: String = "Tenor", // "Soprano", "Mezzo-Soprano", "Alto", "Tenor", "Baritone", "Bass" (for singers)
    val gender: String = "Male", // "Male", "Female" (for singers)
    val experienceLevel: String = "Pro (5+ years)", // "Rising Star (1-2 yrs)", "Established (3-5 yrs)", "Pro (5+ years)", "Legend (10+ yrs)"
    val remoteWork: Boolean = true,
    val sessionMusician: Boolean = true,
    
    // Quick tags / Custom saved notes
    var savedNotes: String = "",
    var savedTags: List<String> = emptyList()
)

// List of all Categories with Icons & Count
data class CategoryItem(
    val name: String,
    val icon: String,
    val profileCount: Int,
    val screenCode: String
)

val defaultCategories = listOf(
    CategoryItem("Singers", "🎤", 188, "SINGERS"),
    CategoryItem("Musicians", "🎸", 245, "MUSICIANS"),
    CategoryItem("DJs", "🎧", 95, "DJS"),
    CategoryItem("Engineers", "🎚", 112, "ENGINEERS"),
    CategoryItem("Composers", "🎼", 84, "COMPOSERS"),
    CategoryItem("Bands", "🥁", 62, "BANDS"),
    CategoryItem("Studios", "🏢", 48, "STUDIOS"),
    CategoryItem("Labels", "🏷", 34, "LABELS"),
    CategoryItem("Event Organizers", "🎪", 72, "EVENT_ORGANIZERS"),
    CategoryItem("Music Schools", "🎓", 29, "MUSIC_SCHOOLS"),
    CategoryItem("Venues", "🏟", 56, "VENUES")
)

// Pre-populated professional entities across India
val sampleDiscoverProfiles = listOf(
    DiscoverProfile(
        id = "p1",
        type = ProfileType.TALENT,
        name = "Arijit Sen",
        category = "Singers",
        subRole = "Playback & Classical Singer",
        avatarColorHex = 0xFFFF5A5F,
        avatarEmoji = "🎤",
        imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80",
        location = "Mumbai, MH",
        area = "Andheri",
        rating = 4.9,
        followers = 85200,
        isVerified = true,
        availabilityStatus = "Booking Open",
        languages = listOf("Hindi", "Bengali", "English"),
        genres = listOf("Bollywood", "Classical", "Sufi"),
        vocalRange = "Tenor",
        gender = "Male",
        experienceLevel = "Legend (10+ yrs)",
        remoteWork = true,
        sessionMusician = true,
        bio = "Lead playback vocalist for modern cinematic albums. Trained in Hindustani Classical under legendary gurus. Specializes in soulful ballads, intense Sufi tracks, and fusion sets.",
        projects = listOf(
            MediaProject("Tum Hi Toh (Unplugged)", "4:12", "Spotify", "4.2M plays", "Feb 2026"),
            MediaProject("O Re Piya (Acoustic Cover)", "5:01", "YouTube", "12M views", "Jan 2026"),
            MediaProject("Sufi Nights Volume 1", "24:30", "Apple Music", "850K streams", "Dec 2025")
        ),
        credits = listOf(
            Credit("Dharam Veer (OST)", "Lead Playback Vocalist", "Music by Pritam", "2025"),
            Credit("Salamat (Single)", "Vocalist & Composer", "Produced by T-Series", "2025"),
            Credit("Indie Roots Fest", "Headline Performer", "Organized by OML", "2024")
        ),
        reviews = listOf(
            Review("T-Series A&R Team", 5.0, "Absolute professional. Delivered standard-setting pitch and exceptional emotion in just two takes.", "March 2026", 0xFFEA4C89),
            Review("Pritam's Studio", 4.9, "Highly recommended for playback scoring. Incredible vocal stamina and depth.", "Jan 2026", 0xFF10B981)
        )
    ),
    DiscoverProfile(
        id = "p2",
        type = ProfileType.TALENT,
        name = "Vedanshu Srivastava",
        category = "Singers",
        subRole = "Indie Folk & Pop Vocalist",
        avatarColorHex = 0xFF8B5CF6,
        avatarEmoji = "👨‍🎤",
        imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80",
        location = "Bengaluru, KA",
        area = "Indiranagar",
        rating = 4.8,
        followers = 43100,
        isVerified = true,
        availabilityStatus = "Available",
        languages = listOf("English", "Tamil", "Hindi"),
        genres = listOf("Indie", "Pop", "Folk"),
        vocalRange = "Tenor",
        gender = "Male",
        experienceLevel = "Pro (5+ years)",
        remoteWork = true,
        sessionMusician = true,
        bio = "Independent singer-songwriter blending South Indian Carnatic hooks with dream-pop synths. Featured in Spotify's RADAR India playlist and NH7 Weekender.",
        projects = listOf(
            MediaProject("Coffee Shop Chords", "3:45", "Spotify", "2.1M plays", "May 2026"),
            MediaProject("Nizhalgal (Shadows)", "3:10", "YouTube", "3.2M views", "Mar 2026")
        ),
        credits = listOf(
            Credit("Modern Love Chennai (OST)", "Vocalist on Lead Track", "Produced by Amazon", "2024"),
            Credit("The Bengaluru Collective", "Featured Artist", "Produced by Sandeep", "2025")
        ),
        reviews = listOf(
            Review("Sanjay K. (Producer)", 4.8, "Her dynamic control and delicate vocal texture added a magic layer to our indie-folk single.", "April 2026", 0xFFF59E0B)
        )
    ),
    DiscoverProfile(
        id = "p3",
        type = ProfileType.TALENT,
        name = "Karan Saxena",
        category = "Musicians",
        subRole = "Session Acoustic & Electric Guitarist",
        avatarColorHex = 0xFF10B981,
        avatarEmoji = "🎸",
        imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=300&q=80",
        location = "New Delhi, DL",
        area = "GK I",
        rating = 4.9,
        followers = 18900,
        isVerified = true,
        availabilityStatus = "Available",
        languages = listOf("Hindi", "English"),
        genres = listOf("Rock", "Jazz", "Fusion"),
        experienceLevel = "Pro (5+ years)",
        remoteWork = true,
        sessionMusician = true,
        bio = "Fretboard enthusiast. 10 years of session experience recording lead guitars, solos, and nylon arrangements for commercial films and indie labels. Equipped with top-tier analog rigs.",
        projects = listOf(
            MediaProject("Midnight Blue Session (Live)", "8:15", "YouTube", "350K views", "Apr 2026"),
            MediaProject("Fretboard Whispers", "4:50", "Spotify", "500K plays", "Jan 2026")
        ),
        credits = listOf(
            Credit("Zoya Akhtar's Next", "Lead Guitar Sessions", "Music by Shankar-Ehsaan-Loy", "2026"),
            Credit("The Fusion Project", "Co-composer & Guitarist", "Self-published", "2025")
        ),
        reviews = listOf(
            Review("Ehsaan Q.", 5.0, "Karan's improvisational timing is world-class. Quick turnaround and beautiful guitar tone.", "May 2026", 0xFF3B82F6)
        )
    ),
    DiscoverProfile(
        id = "p4",
        type = ProfileType.TALENT,
        name = "DJ Rish",
        category = "DJs",
        subRole = "EDM & Club Remix DJ",
        avatarColorHex = 0xFFEF4444,
        avatarEmoji = "🎧",
        imageUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=300&q=80",
        location = "Goa, GA",
        area = "Anjuna",
        rating = 4.7,
        followers = 29500,
        isVerified = false,
        availabilityStatus = "Booking Open",
        languages = listOf("English", "Hindi"),
        genres = listOf("Electronic", "Pop", "Fusion"),
        experienceLevel = "Established (3-5 yrs)",
        remoteWork = false,
        sessionMusician = false,
        bio = "Resident DJ at premium clubs in Anjuna and Vagator. Curates electric audio journeys mixing traditional Indian classical motifs with modern psytrance and deep house.",
        projects = listOf(
            MediaProject("Anjuna Sunsets Vol 4", "1:15:00", "YouTube", "1.2M views", "Mar 2026"),
            MediaProject("Udaan (Psy Remix)", "5:40", "Spotify", "980K plays", "Jan 2026")
        ),
        credits = listOf(
            Credit("Hilltop Festival 2026", "Supporting Headliner", "Hilltop Records", "2026"),
            Credit("Club Odyssey", "Resident DJ", "Anjuna beach", "2025")
        )
    ),
    DiscoverProfile(
        id = "p5",
        type = ProfileType.BAND,
        name = "The Bangalore Blues Project",
        category = "Bands",
        subRole = "5-piece Blues & Jazz Ensemble",
        avatarColorHex = 0xFF3B82F6,
        avatarEmoji = "🎷",
        imageUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=300&q=80",
        location = "Bengaluru, KA",
        area = "Koramangala",
        rating = 4.9,
        followers = 32000,
        isVerified = true,
        availabilityStatus = "Booking Open",
        languages = listOf("English", "Kannada"),
        genres = listOf("Jazz", "Rock", "Fusion"),
        experienceLevel = "Legend (10+ yrs)",
        bio = "Bringing dusty Mississippi delta slide-guitar vibes and vintage 40s brass hooks straight to the Indian metropolis. Available for corporate gigs, music festivals, and custom live club circuits.",
        members = listOf(
            BandMember("Vikram Kumar", "Lead Vocalist & Slide Guitar", true, 0xFF3B82F6),
            BandMember("Sharon D'Souza", "Keyboards & Hammond B3", false, 0xFF8B5CF6),
            BandMember("Abhinav Das", "Alto Saxophone", false, 0xFFE57373),
            BandMember("Rohit Raj", "Double Bass", false, 0xFF81C784),
            BandMember("Siddharth M.", "Drums & Percussion", false, 0xFFFFB74D)
        ),
        projects = listOf(
            MediaProject("Koramangala Jam Session (Live)", "12:12", "YouTube", "450K views", "Apr 2026"),
            MediaProject("Coffee Shops & Rainy Afternoons", "4:05", "Spotify", "1.1M plays", "Dec 2025")
        ),
        credits = listOf(
            Credit("Hard Rock Cafe Tour", "Headline Act", "Sponsored by HRC", "2025"),
            Credit("Indian Indie Awards", "Best Live Band Nominee", "Music India Forum", "2025")
        )
    ),
    DiscoverProfile(
        id = "p6",
        type = ProfileType.BUSINESS,
        name = "Yash Raj Sound Studios",
        category = "Studios",
        subRole = "State-of-the-Art Mixing & Recording Facility",
        avatarColorHex = 0xFF1F2937,
        avatarEmoji = "🏢",
        imageUrl = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=300&q=80",
        location = "Mumbai, MH",
        area = "Andheri West",
        rating = 5.0,
        pricing = "₹4,500/hr",
        followers = 115000,
        isVerified = true,
        availabilityStatus = "Booking Open",
        hasAtmos = true,
        bio = "India's landmark soundstage. Offers two full live double-height tracking rooms, a dedicated Solid State Logic (SSL) 9000J 72-channel board room, and a certified Dolby Atmos HE Mixing Suite.",
        equipment = listOf(
            "SSL 9000J 72-Channel Console",
            "Certified Dolby Atmos 7.1.4 PMC Studio Monitors",
            "Vintage Neumann U87, U47, and Telefunken ELAM 251 Microphones",
            "Pro Tools HDX with 128 Analog Inputs",
            "Custom acoustic woodwork designed by Munro Acoustics UK"
        ),
        projects = listOf(
            MediaProject("Pathaan (Full Sound Mix)", "2:30:15", "Apple Music", "Cinematic Mastering", "2024"),
            MediaProject("Coke Studio India - Episode 1 - 4", "45:30", "YouTube", "55M views collectively", "2025")
        )
    ),
    DiscoverProfile(
        id = "p7",
        type = ProfileType.VENUE_EVENT,
        name = "Ironwood Live Hall & Venue",
        category = "Venues",
        subRole = "400-Capacity Live Concert Venue & Amphitheater",
        avatarColorHex = 0xFFF59E0B,
        avatarEmoji = "🏟",
        imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=300&q=80",
        location = "Pune, MH",
        area = "Baner",
        rating = 4.8,
        pricing = "₹75,000/day",
        followers = 14200,
        isVerified = true,
        availabilityStatus = "Booking Open",
        bio = "Premium live showcase space tailored specifically for indie band tours and electronic showcases. Custom acoustic wall treatment, high-density line-arrays, and full stage lighting plot included.",
        equipment = listOf(
            "L-Acoustics K3 Line Array Sound System",
            "GrandMA3 Lighting Console",
            "Full Backline (Pearl Masters Drums, Ampeg Bass Rigs, Marshall Guitar Cabs)",
            "Dual high-definition LED Screen Banners (16:9 ratio)"
        )
    ),
    DiscoverProfile(
        id = "p8",
        type = ProfileType.TALENT,
        name = "Rohit Malhotra",
        category = "Composers",
        subRole = "Cinematic Composer & Arranger",
        avatarColorHex = 0xFFEC4899,
        avatarEmoji = "🎼",
        imageUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=300&q=80",
        location = "Mumbai, MH",
        area = "Andheri West",
        rating = 4.9,
        followers = 34100,
        isVerified = true,
        availabilityStatus = "Booking Open",
        languages = listOf("Hindi", "English"),
        genres = listOf("Bollywood", "Fusion", "Classical"),
        experienceLevel = "Pro (5+ years)",
        remoteWork = true,
        sessionMusician = false,
        bio = "Pioneering soundtrack composer merging Indian classical instrumentation (sarod, sitar, bansuri) with modern cinematic strings and digital synthesis.",
        projects = listOf(
            MediaProject("Himalayan Rhapsody", "6:15", "Spotify", "1.4M streams", "May 2026"),
            MediaProject("Flute & Frequencies", "4:32", "YouTube", "2.1M views", "Mar 2026")
        ),
        credits = listOf(
            Credit("National Film Awards India", "Best Background Score Nominee", "Govt of India", "2025"),
            Credit("Echoes of Varanasi", "Arranger & Director", "Indie Music Trust", "2024")
        )
    ),
    DiscoverProfile(
        id = "p9",
        type = ProfileType.TALENT,
        name = "Siddharth Swar",
        category = "Engineers",
        subRole = "Mixing & Mastering Specialist",
        avatarColorHex = 0xFF10B981,
        avatarEmoji = "🎛️",
        imageUrl = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=300&q=80",
        location = "Chennai, TN",
        area = "Nungambakkam",
        rating = 4.9,
        followers = 15400,
        isVerified = true,
        availabilityStatus = "Available",
        languages = listOf("English", "Tamil"),
        genres = listOf("Pop", "Rock", "Indie"),
        experienceLevel = "Pro (5+ years)",
        remoteWork = true,
        sessionMusician = true,
        bio = "Mixing and mastering engineer focused on pristine high-fidelity playback. Specialized in spatial dynamic separation and certified Dolby Atmos configurations.",
        equipment = listOf(
            "Neve 8816 Summing Mixer",
            "Certified Dolby Atmos 7.1.4 array",
            "Manley Variable Mu Limiter Compressor",
            "ATC SCM45A Pro Reference Monitors"
        ),
        projects = listOf(
            MediaProject("Golden Hour (Atmos Mix)", "3:54", "Apple Music", "Spatial Native", "Jan 2026")
        )
    ),
    DiscoverProfile(
        id = "p10",
        type = ProfileType.BUSINESS,
        name = "Ironwood Records",
        category = "Labels",
        subRole = "Independent Rock & Folk Label",
        avatarColorHex = 0xFFEC4899,
        avatarEmoji = "🏷️",
        imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=300&q=80",
        location = "New Delhi, DL",
        area = "Hauz Khas",
        rating = 4.7,
        followers = 52000,
        isVerified = true,
        availabilityStatus = "Booking Open",
        bio = "A premier independent home supporting experimental folk, acoustic, and alternative rock sounds in North India. Specializing in global distribution, tour booking, and dynamic sync placement.",
        projects = listOf(
            MediaProject("Delhi Indie Vol. 3 (Compilation)", "42:15", "Spotify", "3.4M streams", "Apr 2026")
        )
    ),
    DiscoverProfile(
        id = "p11",
        type = ProfileType.VENUE_EVENT,
        name = "Nisha Rao Events",
        category = "Event Organizers",
        subRole = "Festival Booking Agency & Promoter",
        avatarColorHex = 0xFF8B5CF6,
        avatarEmoji = "🎪",
        imageUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=300&q=80",
        location = "Bengaluru, KA",
        area = "Koramangala",
        rating = 4.8,
        followers = 28300,
        isVerified = true,
        availabilityStatus = "Booking Open",
        bio = "Concert representation agency and live promoter. Organizing regional club packages and boutique music festivals across top-tier metro regions.",
        projects = listOf(
            MediaProject("Swar Fest 2025 Bangalore", "4:30:00", "YouTube", "Live stream archive", "Nov 2025")
        )
    ),
    DiscoverProfile(
        id = "p12",
        type = ProfileType.BUSINESS,
        name = "Swar Academy of Music",
        category = "Music Schools",
        subRole = "Hindustani & Contemporary School",
        avatarColorHex = 0xFFEF4444,
        avatarEmoji = "🎓",
        imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=300&q=80",
        location = "Mumbai, MH",
        area = "Juhu",
        rating = 4.8,
        followers = 19800,
        isVerified = true,
        availabilityStatus = "Booking Open",
        bio = "Premier education academy teaching classical Hindustani Hindustani Khayal vocal disciplines alongside modern music tech, digital audio sequencing, and acoustic synthesis.",
        projects = listOf(
            MediaProject("Swar Academy Grad Showcase 2026", "24:12", "YouTube", "Student performances", "May 2026")
        )
    )
)
