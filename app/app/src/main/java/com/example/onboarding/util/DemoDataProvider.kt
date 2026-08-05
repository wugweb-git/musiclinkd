package com.example.onboarding.util

import com.example.onboarding.ui.components.MediaAttachmentItem

/**
 * Centralized Demo Data Provider for MusicLinkd application.
 * Contains mock data models, high-res avatar/cover image URLs, artist details,
 * studio specs, audio track samples, and feed posts.
 */
object DemoDataProvider {

    // High-Resolution Image URLs (Unsplash Artists, Studios, & Instruments)
    object ImageUrls {
        const val AVATAR_MALE_GUITARIST = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80"
        const val AVATAR_FEMALE_VOCALIST = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=600&q=80"
        const val AVATAR_PRODUCER = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=600&q=80"
        const val AVATAR_DRUMMER = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=600&q=80"
        const val AVATAR_SITARIST = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=600&q=80"
        const val AVATAR_DJ = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=600&q=80"

        const val STUDIO_ATMOS = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=800&q=80"
        const val STUDIO_ANALOG = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80"
        const val STUDIO_VOCAL_BOOTH = "https://images.unsplash.com/photo-1519671482749-fd09be7ccebf?auto=format&fit=crop&w=800&q=80"

        const val ALBUM_COVER_1 = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=600&q=80"
        const val ALBUM_COVER_2 = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?auto=format&fit=crop&w=600&q=80"
        const val ALBUM_COVER_3 = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=600&q=80"
    }

    // Audio URLs
    object AudioUrls {
        const val SAMPLE_STEM_1 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        const val SAMPLE_STEM_2 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        const val SAMPLE_STEM_3 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    }

    // Demo Artist Data Model
    data class DemoArtist(
        val id: String,
        val name: String,
        val role: String,
        val location: String,
        val isVerified: Boolean,
        val isAvailable: Boolean,
        val rating: Double,
        val reviewsCount: Int,
        val hourlyRate: String,
        val genres: List<String>,
        val avatarUrl: String,
        val bio: String,
        val primaryInstrument: String
    )

    // Demo Studio Data Model
    data class DemoStudio(
        val id: String,
        val name: String,
        val location: String,
        val rating: Double,
        val reviewCount: Int,
        val hourlyRate: String,
        val verifiedGold: Boolean,
        val amenities: List<String>,
        val imageUrl: String,
        val consoleType: String
    )

    // Demo Track Data Model
    data class DemoTrack(
        val id: String,
        val title: String,
        val artistName: String,
        val durationText: String,
        val coverUrl: String,
        val audioUrl: String,
        val genre: String
    )

    // Demo Post Model for Feed
    data class DemoFeedPost(
        val id: String,
        val authorName: String,
        val authorRole: String,
        val authorAvatar: String,
        val postType: String, // "COLLAB_CALL", "RELEASE", "STUDIO_UPDATE"
        val content: String,
        val timestampText: String,
        val audioTitle: String? = null,
        val likesCount: Int = 0
    )

    // Sample Artists List
    val sampleArtists = listOf(
        DemoArtist(
            id = "artist_1",
            name = "Rohan Sharma",
            role = "Lead Guitarist & Producer",
            location = "Bandra West, Mumbai",
            isVerified = true,
            isAvailable = true,
            rating = 4.9,
            reviewsCount = 42,
            hourlyRate = "₹2,500/hr",
            genres = listOf("Indie Rock", "Gibson Custom", "Ableton Live"),
            avatarUrl = ImageUrls.AVATAR_MALE_GUITARIST,
            bio = "Session guitarist with 8+ years experience in Bollywood film scores and indie album productions.",
            primaryInstrument = "Electric Guitar & Production"
        ),
        DemoArtist(
            id = "artist_2",
            name = "Aditi Sen",
            role = "Jazz Vocalist & Topliner",
            location = "Indiranagar, Bengaluru",
            isVerified = true,
            isAvailable = true,
            rating = 5.0,
            reviewsCount = 38,
            hourlyRate = "₹3,000/hr",
            genres = listOf("Neo-Soul", "Vocal Tuning", "Jazz"),
            avatarUrl = ImageUrls.AVATAR_FEMALE_VOCALIST,
            bio = "Berklee alumna, classical & jazz fusion lead singer for major brand jingles and acoustic EPs.",
            primaryInstrument = "Lead Vocals & Harmonies"
        ),
        DemoArtist(
            id = "artist_3",
            name = "Vikramaditya Roy",
            role = "Sitar Virtuoso & Session Soloist",
            location = "Hauz Khas, New Delhi",
            isVerified = true,
            isAvailable = false,
            rating = 4.8,
            reviewsCount = 29,
            hourlyRate = "₹3,500/hr",
            genres = listOf("Classical Fusion", "Raga", "Live Concert"),
            avatarUrl = ImageUrls.AVATAR_SITARIST,
            bio = "Improvisational Hindustani classical performer blending electronic ambient backings with traditional ragas.",
            primaryInstrument = "Sitar & Acoustic Plucked Strings"
        ),
        DemoArtist(
            id = "artist_4",
            name = "Karan Malhotra",
            role = "Electronic Music Producer & Engineer",
            location = "Koregaon Park, Pune",
            isVerified = true,
            isAvailable = true,
            rating = 4.9,
            reviewsCount = 64,
            hourlyRate = "₹4,000/hr",
            genres = listOf("Synthwave", "Logic Pro X", "Mixing"),
            avatarUrl = ImageUrls.AVATAR_PRODUCER,
            bio = "Mixing & mastering engineer specializing in punchy drums, spatial synth layers, and streaming-ready masters.",
            primaryInstrument = "Synthesizers & Analog Consoles"
        )
    )

    // Sample Studios List
    val sampleStudios = listOf(
        DemoStudio(
            id = "studio_1",
            name = "Octave Sound Labs",
            location = "Bandra West, Mumbai",
            rating = 4.95,
            reviewCount = 88,
            hourlyRate = "₹3,500/hr",
            verifiedGold = true,
            amenities = listOf("Dolby Atmos 7.1.4", "SSL Duality Console", "Neumann U87"),
            imageUrl = ImageUrls.STUDIO_ATMOS,
            consoleType = "SSL Duality Delta 48"
        ),
        DemoStudio(
            id = "studio_2",
            name = "Resonance Acoustic Suites",
            location = "Indiranagar, Bengaluru",
            rating = 4.88,
            reviewCount = 54,
            hourlyRate = "₹2,800/hr",
            verifiedGold = true,
            amenities = listOf("Genelec Monitoring", "Acoustic Treated Booth", "Pro Tools HDX"),
            imageUrl = ImageUrls.STUDIO_ANALOG,
            consoleType = "API 1608 Analog Console"
        ),
        DemoStudio(
            id = "studio_3",
            name = "Velvet Vocal & Foley Studio",
            location = "Juhu, Mumbai",
            rating = 4.9,
            reviewCount = 31,
            hourlyRate = "₹2,200/hr",
            verifiedGold = false,
            amenities = listOf("Isolation Vocal Booth", "Avalon VT-737", "Universal Audio Apollo"),
            imageUrl = ImageUrls.STUDIO_VOCAL_BOOTH,
            consoleType = "Universal Audio Apollo x16"
        )
    )

    // Sample Tracks List
    val sampleTracks = listOf(
        DemoTrack(
            id = "track_1",
            title = "Monsoon Echoes (Acoustic STEM)",
            artistName = "Rohan Sharma ft. Aditi Sen",
            durationText = "3:42",
            coverUrl = ImageUrls.ALBUM_COVER_1,
            audioUrl = AudioUrls.SAMPLE_STEM_1,
            genre = "Indie Acoustic"
        ),
        DemoTrack(
            id = "track_2",
            title = "Shadows of Banaras (Raga Fusion)",
            artistName = "Vikramaditya Roy",
            durationText = "4:15",
            coverUrl = ImageUrls.ALBUM_COVER_2,
            audioUrl = AudioUrls.SAMPLE_STEM_2,
            genre = "Classical Fusion"
        ),
        DemoTrack(
            id = "track_3",
            title = "Midnight Drive in Colaba",
            artistName = "Karan Malhotra",
            durationText = "3:10",
            coverUrl = ImageUrls.ALBUM_COVER_3,
            audioUrl = AudioUrls.SAMPLE_STEM_3,
            genre = "Synthwave / Electronic"
        )
    )

    // Sample Feed Posts List
    val sampleFeedPosts = listOf(
        DemoFeedPost(
            id = "feed_1",
            authorName = "Rohan Sharma",
            authorRole = "Lead Guitarist",
            authorAvatar = ImageUrls.AVATAR_MALE_GUITARIST,
            postType = "COLLAB_CALL",
            content = "Looking for a session flutist or sitarist for a 3-track acoustic fusion EP recording at Octave Sound Labs next Thursday! Drop a connection request if available.",
            timestampText = "2 hrs ago",
            audioTitle = "Sufi_Echoes_Acoustic_Take.wav",
            likesCount = 14
        ),
        DemoFeedPost(
            id = "feed_2",
            authorName = "Octave Sound Labs",
            authorRole = "Recording Studio",
            authorAvatar = ImageUrls.STUDIO_ATMOS,
            postType = "STUDIO_UPDATE",
            content = "We just upgraded our main control room to full Dolby Atmos 7.1.4 immersive audio mixing! Special weekday slots available for indie album mastering.",
            timestampText = "5 hrs ago",
            likesCount = 28
        ),
        DemoFeedPost(
            id = "feed_3",
            authorName = "Aditi Sen",
            authorRole = "Jazz Vocalist",
            authorAvatar = ImageUrls.AVATAR_FEMALE_VOCALIST,
            postType = "RELEASE",
            content = "New vocal sample library stem packs uploaded to my portfolio! Check out the dry vocal multi-tracks for lo-fi producers.",
            timestampText = "Yesterday",
            audioTitle = "Neo_Soul_Vocal_Runs_95BPM.mp3",
            likesCount = 42
        )
    )

    // Sample Media Attachments for Artist Profile
    val defaultMediaAttachments = listOf(
        MediaAttachmentItem("1", "Sufi_Echoes_Acoustic_STEM.wav", "AUDIO", AudioUrls.SAMPLE_STEM_1, "3:15"),
        MediaAttachmentItem("2", "Studio_Session_Bandra_01.jpg", "IMAGE", ImageUrls.STUDIO_ATMOS, "1.2 MB"),
        MediaAttachmentItem("3", "Live_Vocal_Rehearsal.jpg", "IMAGE", ImageUrls.AVATAR_FEMALE_VOCALIST, "2.4 MB")
    )

    // Categories List
    val searchFilterCategories = listOf("All", "Musicians", "Producers", "Studios", "Bands", "Projects")

    // Stats
    const val STAT_CONNECTIONS_COUNT = "1,420+"
    const val STAT_STUDIOS_COUNT = "180+"
    const val STAT_COLLABORATIONS_COUNT = "3,850+"
}
