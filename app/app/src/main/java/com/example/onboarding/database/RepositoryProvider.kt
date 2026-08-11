package com.example.onboarding.database

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import com.example.onboarding.ui.screens.ChatContext
import com.example.onboarding.ui.screens.ConvoItem
import com.example.onboarding.ui.screens.MessageBubbleItem
import com.example.onboarding.ui.screens.ReputationReviewItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object RepositoryProvider {
    private var database: MusicLinkdDatabase? = null
    var appContext: Context? = null
        private set

    val onboardingRepository: OnboardingRepository by lazy {
        OnboardingRepository(database!!.onboardingDao())
    }

    val chatRepository: ChatRepository by lazy {
        ChatRepository(database!!.chatDao())
    }

    val reviewRepository: ReviewRepository by lazy {
        ReviewRepository(database!!.reviewDao())
    }

    val userProfileDao: UserProfileDao by lazy {
        database!!.userProfileDao()
    }

    val userProfileRepository: UserProfileRepository by lazy {
        RoomUserProfileRepository(database!!.userProfileDao())
    }

    val notificationDao: NotificationDao by lazy {
        database!!.notificationDao()
    }

    val discoverFeedDao: DiscoverFeedDao by lazy {
        database!!.discoverFeedDao()
    }

    val searchHistoryDao: SearchHistoryDao by lazy {
        database!!.searchHistoryDao()
    }

    val customMasterDao: CustomMasterDao by lazy {
        database!!.customMasterDao()
    }

    fun initialize(context: Context) {
        appContext = context.applicationContext
        if (database == null) {
            database = MusicLinkdDatabase.getDatabase(context)
        }
        FirebaseManager.initialize(context)

        CoroutineScope(Dispatchers.IO).launch {
            cleanExpiredCacheAndSyncPending()
            seedDatabaseIfEmpty()
            loadCachedTaxonomy()
            syncTaxonomyFromFirestore()
        }
    }

    /**
     * Enforce Room Database expiration policy and perform partial sync for Discover feed and Notifications.
     */
    private suspend fun cleanExpiredCacheAndSyncPending() {
        val db = database ?: return
        try {
            val now = System.currentTimeMillis()
            val deletedDiscover = db.discoverFeedDao().deleteExpiredItems(now)
            val deletedNotifs = db.notificationDao().deleteExpiredNotifications(now)

            // Partial sync processing for unsynced items
            val unsyncedFeed = db.discoverFeedDao().getUnsyncedFeedItems()
            if (unsyncedFeed.isNotEmpty()) {
                db.discoverFeedDao().markItemsSynced(unsyncedFeed.map { it.id })
            }

            val unsyncedNotifs = db.notificationDao().getUnsyncedNotifications()
            if (unsyncedNotifs.isNotEmpty()) {
                db.notificationDao().markNotificationsSynced(unsyncedNotifs.map { it.id })
            }
        } catch (e: Throwable) {
            com.example.onboarding.util.ErrorReportingService.logException(
                e,
                "RepositoryProvider",
                "Error running database expiration and sync policy cleanup"
            )
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        val db = database ?: return

        // 1. Seed Onboarding Profile
        if (db.onboardingDao().getOnboardingProfile() == null) {
            db.onboardingDao().saveOnboardingProfile(
                OnboardingEntity(
                    id = "current_user",
                    mobileNumber = "+91 98765 43210",
                    isOtpVerified = true,
                    primaryRole = "Music Producer",
                    secondaryRole = "Sound Engineer",
                    selectedLanguages = "English,Hindi,Punjabi",
                    selectedGenres = "Bollywood,Indie,Pop,Electronic,Sufi",
                    selectedInstruments = "Synthesizer,Piano,Guitar",
                    state = "Maharashtra",
                    city = "Mumbai",
                    area = "Andheri West",
                    fullName = "Vedanshu Srivastava",
                    displayName = "Vedanshu",
                    email = "vedanshu.srivastava@gmail.com",
                    bio = "Music Producer & Sound Designer with 5+ years experience crafting Bollywood & Indie hit records.",
                    selectedAvatarIndex = 0,
                    currentScreenName = "DASHBOARD"
                )
            )
        }

        // Seed Offline User Profile for Room persistence
        if (db.userProfileDao().getUserProfile() == null) {
            db.userProfileDao().saveUserProfile(
                UserProfileEntity(
                    id = "current_user",
                    displayName = "Vedanshu Srivastava",
                    professionalName = "Vedanshu",
                    headline = "Music Producer & Composer",
                    bio = "Passionate sound architect based in Mumbai. Specializing in cinematic fusion, synth layering, and spatial audio production.",
                    location = "Mumbai, Maharashtra",
                    languages = "English, Hindi, Punjabi",
                    genres = "Indie Rock, Bollywood Fusion, Synthwave, Electronic",
                    skills = "Music Producer, Sound Engineer, Synthesizer Player"
                )
            )
        }

        // Seed Offline Notifications for Room persistence
        val initialNotifs = listOf(
            NotificationEntity(
                id = "notif_room_1",
                title = "Studio Booking Approved",
                body = "Sound Studio Mumbai approved your 4-hour slot for tomorrow at 11:00 AM.",
                timestamp = "10 mins ago",
                category = "bookings",
                isRead = false,
                sourceName = "Room Offline Storage",
                iconEmoji = "🎙️"
            ),
            NotificationEntity(
                id = "notif_room_2",
                title = "New Collaboration Request",
                body = "Rohan Sharma requested to collaborate on 'Banaras Film Score'.",
                timestamp = "2 hours ago",
                category = "network",
                isRead = false,
                sourceName = "Room Offline Storage",
                iconEmoji = "🤝"
            )
        )
        db.notificationDao().insertAll(initialNotifs)

        // 2. Seed Reviews
        val reviewCount = db.reviewDao().getReviewCount()
        if (reviewCount == 0) {
            reviewRepository.insertReviews(
                listOf(
                    ReputationReviewItem(
                        id = "rev-1",
                        reviewerName = "James Miller",
                        reviewerRole = "A&R Universal Records",
                        isVerified = true,
                        ratingStars = 5,
                        reviewText = "The raw vocal tracks Vedanshu recorded are exceptionally clean. The Kemper profile work was masterfully done. Saved us days of EQ in our final studio mix.",
                        projectContext = "Project: Dark Ambient Synth Mastery EP",
                        date = "2 days ago",
                        initialHelpfulCount = 14,
                        hasAudioSample = true,
                        audioTitle = "Dry_Vocal_Stem_Take3.wav",
                        hasImages = true,
                        businessReplyText = "Thanks James! It was a pleasure engineering these session stems."
                    ),
                    ReputationReviewItem(
                        id = "rev-2",
                        reviewerName = "Rhea Kapoor",
                        reviewerRole = "Playback Artist & Songwriter",
                        isVerified = true,
                        ratingStars = 5,
                        reviewText = "Amazing session at SoundOne Studios! The acoustic treatment in Tracking Room B made my vocal takes sound super intimate.",
                        projectContext = "Studio Session: SoundOne Studios Suite A",
                        date = "1 week ago",
                        initialHelpfulCount = 8,
                        hasAudioSample = true,
                        audioTitle = "Acoustic_Take_A_Master.wav",
                        hasImages = false
                    ),
                    ReputationReviewItem(
                        id = "rev-3",
                        reviewerName = "Rohan Mehta",
                        reviewerRole = "Film Score Composer",
                        isVerified = true,
                        ratingStars = 4,
                        reviewText = "Delivered the requested orchestral synth stems ahead of deadline. Very responsive and easy to communicate with.",
                        projectContext = "Commercial Score Licensing",
                        date = "2 weeks ago",
                        initialHelpfulCount = 5,
                        hasAudioSample = false
                    )
                )
            )
        }

        // 3. Seed Conversations & Messages
        val convos = db.chatDao().getAllConversationsSync()
        if (convos.isEmpty()) {
            val defaultConvos = listOf(
                ConvoItem(
                    id = "studio_1",
                    name = "SoundOne Studios",
                    role = "Premium Mixing Suite",
                    lastMessage = "Hey Kabir, your booking request for July 12 is secure-booking locked. See you then!",
                    timestamp = "10:24 AM",
                    unreadCount = 2,
                    context = ChatContext.STUDIO,
                    contextStatus = "Booking Confirmed",
                    isPinned = true
                ),
                ConvoItem(
                    id = "market_1",
                    name = "Aria Vox",
                    role = "Vintage Gear Seller",
                    lastMessage = "I can do ₹1,15,000 if you buy the vintage Gibson today. Secured transaction is ready.",
                    timestamp = "Yesterday",
                    unreadCount = 0,
                    context = ChatContext.MARKETPLACE,
                    contextStatus = "Offer Pending"
                ),
                ConvoItem(
                    id = "opp_1",
                    name = "Netflix India Music",
                    role = "Music Licensing Guild",
                    lastMessage = "We reviewed your electronic backing track demo. Please attach your complete FLAC portfolio.",
                    timestamp = "Jun 26",
                    unreadCount = 1,
                    context = ChatContext.OPPORTUNITY,
                    contextStatus = "Under Review",
                    isPinned = true
                ),
                ConvoItem(
                    id = "project_1",
                    name = "Kabir & The Band",
                    role = "EP Collaboration Project",
                    lastMessage = "Track 4 stem mixing is almost done. I've uploaded the drum transient wav file.",
                    timestamp = "Jun 24",
                    unreadCount = 0,
                    context = ChatContext.PROJECT,
                    contextStatus = "60% Completed"
                )
            )
            chatRepository.insertConversations(defaultConvos)

            val messagesMap = mapOf(
                "studio_1" to listOf(
                    MessageBubbleItem("s1_1", "SoundOne Studios", "Welcome to SoundOne. We offer dual SSL 9000J recording suites.", "Yesterday", false),
                    MessageBubbleItem("s1_2", "Me", "Hey, I need to book the primary tracking suite for vocal session stems.", "Yesterday", true),
                    MessageBubbleItem("s1_3", "SoundOne Studios", "Perfect. Let's create a secure booking block for July 12.", "Yesterday", false),
                    MessageBubbleItem("s1_4", "System", "Booking Contract Proposed", "10:00 AM", false, type = "booking", metaTitle = "Vocal Suite A Block", metaDesc = "July 12, 2:00 PM (4 Hours)", metaPrice = "₹12,000", metaStatus = "PENDING"),
                    MessageBubbleItem("s1_5", "SoundOne Studios", "I've locked the calendar hold. You can accept and pay the secure deposit directly in the header action.", "10:22 AM", false),
                    MessageBubbleItem("s1_6", "System", "Invoice Generated", "10:23 AM", false, type = "document", metaTitle = "INV-2026-894.pdf", metaDesc = "Booking Invoice (4 Hours Room Hire)", metaPrice = "₹12,000", metaStatus = "UNPAID")
                ),
                "market_1" to listOf(
                    MessageBubbleItem("m1_1", "Aria Vox", "Hello! Here is the custom listing for the Gibson guitar.", "Yesterday", false),
                    MessageBubbleItem("m1_2", "System", "Gibson Les Paul Standard 1978", "Yesterday", false, type = "marketplace", metaTitle = "Vintage Gibson Les Paul 1978", metaDesc = "Excellent condition, original fretboard, low action, hard-shell case included.", metaPrice = "₹1,20,000", metaStatus = "AVAILABLE"),
                    MessageBubbleItem("m1_3", "Me", "Can we do ₹1,10,000 with secure shipping?", "Yesterday", true),
                    MessageBubbleItem("m1_4", "Aria Vox", "I can do ₹1,15,000 if you buy the vintage Gibson today. Secured transaction is ready.", "Yesterday", false)
                )
            )

            messagesMap.forEach { (convoId, msgs) ->
                msgs.forEach { msg ->
                    chatRepository.insertMessage(convoId, msg)
                }
            }

            val defaultSearchQueries = listOf(
                SearchHistoryEntity(query = "Aria Vox", category = "Artist", resultCount = 12),
                SearchHistoryEntity(query = "SoundOne Studios", category = "Studio", resultCount = 5),
                SearchHistoryEntity(query = "Electronic Synth Producer", category = "Role", resultCount = 28),
                SearchHistoryEntity(query = "Bollywood Acoustic Project", category = "Project", resultCount = 15),
                SearchHistoryEntity(query = "Kabir & The Band", category = "Artist", resultCount = 8)
            )
            defaultSearchQueries.forEach { db.searchHistoryDao().insertSearchQuery(it) }
        }

        // 4. Seed Custom Master Metadata categories
        if (db.customMasterDao().getAllCustomMasters().isEmpty()) {
            val masterSeeds = listOf(
                // Genres (genres)
                CustomMasterEntity(category = "genres", itemKey = "Bollywood / Filmi", displayLabel = "Bollywood / Filmi", sortOrder = 1),
                CustomMasterEntity(category = "genres", itemKey = "Indie Pop", displayLabel = "Indie Pop", sortOrder = 2),
                CustomMasterEntity(category = "genres", itemKey = "Hindustani Classical", displayLabel = "Hindustani Classical", sortOrder = 3),
                CustomMasterEntity(category = "genres", itemKey = "Carnatic Classical", displayLabel = "Carnatic Classical", sortOrder = 4),
                CustomMasterEntity(category = "genres", itemKey = "Punjabi Folk / Bhangra", displayLabel = "Punjabi Folk / Bhangra", sortOrder = 5),
                CustomMasterEntity(category = "genres", itemKey = "Rajasthani Folk", displayLabel = "Rajasthani Folk", sortOrder = 6),
                CustomMasterEntity(category = "genres", itemKey = "Sufi Devotional", displayLabel = "Sufi Devotional", sortOrder = 7),
                CustomMasterEntity(category = "genres", itemKey = "Ghazal", displayLabel = "Ghazal", sortOrder = 8),
                CustomMasterEntity(category = "genres", itemKey = "Qawwali", displayLabel = "Qawwali", sortOrder = 9),
                CustomMasterEntity(category = "genres", itemKey = "Rock / Metal", displayLabel = "Rock / Metal", sortOrder = 10),
                CustomMasterEntity(category = "genres", itemKey = "Hip Hop / Trap", displayLabel = "Hip Hop / Trap", sortOrder = 11),
                CustomMasterEntity(category = "genres", itemKey = "Lofi / Chill Beats", displayLabel = "Lofi / Chill Beats", sortOrder = 12),
                CustomMasterEntity(category = "genres", itemKey = "EDM / Synthwave", displayLabel = "EDM / Synthwave", sortOrder = 13),
                CustomMasterEntity(category = "genres", itemKey = "Acoustic / Unplugged", displayLabel = "Acoustic / Unplugged", sortOrder = 14),
                CustomMasterEntity(category = "genres", itemKey = "R&B / Soul", displayLabel = "R&B / Soul", sortOrder = 15),
                CustomMasterEntity(category = "genres", itemKey = "Jazz / Blues", displayLabel = "Jazz / Blues", sortOrder = 16),
                CustomMasterEntity(category = "genres", itemKey = "Reggae / Dub", displayLabel = "Reggae / Dub", sortOrder = 17),

                // Person Types (person_types)
                CustomMasterEntity(category = "person_types", itemKey = "singer", displayLabel = "Singer / Vocalist", sortOrder = 1),
                CustomMasterEntity(category = "person_types", itemKey = "rapper", displayLabel = "Rapper / MC", sortOrder = 2),
                CustomMasterEntity(category = "person_types", itemKey = "folk_vocalist", displayLabel = "Folk Vocalist", sortOrder = 3),
                CustomMasterEntity(category = "person_types", itemKey = "guitarist", displayLabel = "Guitarist", sortOrder = 4),
                CustomMasterEntity(category = "person_types", itemKey = "keyboardist", displayLabel = "Keyboardist / Pianist", sortOrder = 5),
                CustomMasterEntity(category = "person_types", itemKey = "drummer", displayLabel = "Drummer / Percussionist", sortOrder = 6),
                CustomMasterEntity(category = "person_types", itemKey = "sitarist", displayLabel = "Sitarist / Flutist", sortOrder = 7),
                CustomMasterEntity(category = "person_types", itemKey = "composer", displayLabel = "Composer", sortOrder = 8),
                CustomMasterEntity(category = "person_types", itemKey = "lyricist", displayLabel = "Lyricist", sortOrder = 9),
                CustomMasterEntity(category = "person_types", itemKey = "arranger", displayLabel = "Vocal Arranger", sortOrder = 10),
                CustomMasterEntity(category = "person_types", itemKey = "producer", displayLabel = "Music Producer", sortOrder = 11),
                CustomMasterEntity(category = "person_types", itemKey = "sound_engineer", displayLabel = "Sound Engineer", sortOrder = 12),
                CustomMasterEntity(category = "person_types", itemKey = "sound_designer", displayLabel = "Sound Designer", sortOrder = 13),
                CustomMasterEntity(category = "person_types", itemKey = "artist_manager", displayLabel = "Artist Manager", sortOrder = 14),
                CustomMasterEntity(category = "person_types", itemKey = "music_label", displayLabel = "Music Label", sortOrder = 15),
                CustomMasterEntity(category = "person_types", itemKey = "music_publisher", displayLabel = "Music Publisher", sortOrder = 16),
                CustomMasterEntity(category = "person_types", itemKey = "studio", displayLabel = "Recording Studio", sortOrder = 17),
                CustomMasterEntity(category = "person_types", itemKey = "event_organizer", displayLabel = "Event Organizer", sortOrder = 18),

                // Languages (languages)
                CustomMasterEntity(category = "languages", itemKey = "English", displayLabel = "English", sortOrder = 1),
                CustomMasterEntity(category = "languages", itemKey = "Hindi (हिंदी)", displayLabel = "Hindi (हिंदी)", sortOrder = 2),
                CustomMasterEntity(category = "languages", itemKey = "Punjabi (ਪੰਜਾਬੀ)", displayLabel = "Punjabi (ਪੰਜਾਬੀ)", sortOrder = 3),
                CustomMasterEntity(category = "languages", itemKey = "Tamil (தமிழ்)", displayLabel = "Tamil (தமிழ்)", sortOrder = 4),
                CustomMasterEntity(category = "languages", itemKey = "Telugu (తెలుగు)", displayLabel = "Telugu (తెలుగు)", sortOrder = 5),
                CustomMasterEntity(category = "languages", itemKey = "Marathi (मराठी)", displayLabel = "Marathi (मराठी)", sortOrder = 6),
                CustomMasterEntity(category = "languages", itemKey = "Gujarati (ગુજરાતી)", displayLabel = "Gujarati (ગુજરાતી)", sortOrder = 7),
                CustomMasterEntity(category = "languages", itemKey = "Kannada (ಕನ್ನಡ)", displayLabel = "Kannada (ಕನ್ನಡ)", sortOrder = 8),
                CustomMasterEntity(category = "languages", itemKey = "Malayalam (മലയാളം)", displayLabel = "Malayalam (മലയാളം)", sortOrder = 9),
                CustomMasterEntity(category = "languages", itemKey = "Bengali (বাংলা)", displayLabel = "Bengali (বাংলা)", sortOrder = 10),
                CustomMasterEntity(category = "languages", itemKey = "Assamese (অसमীয়া)", displayLabel = "Assamese (অसमীয়া)", sortOrder = 11),
                CustomMasterEntity(category = "languages", itemKey = "Odia (ଓଡ଼ିଆ)", displayLabel = "Odia (ଓଡ଼ିଆ)", sortOrder = 12),
                CustomMasterEntity(category = "languages", itemKey = "Urdu (اردو)", displayLabel = "Urdu (اردو)", sortOrder = 13),
                CustomMasterEntity(category = "languages", itemKey = "Sanskrit (संस्कृत)", displayLabel = "Sanskrit (संस्कृत)", sortOrder = 14),
                CustomMasterEntity(category = "languages", itemKey = "Spanish (Español)", displayLabel = "Spanish (Español)", sortOrder = 15),
                CustomMasterEntity(category = "languages", itemKey = "French (Français)", displayLabel = "French (Français)", sortOrder = 16),
                CustomMasterEntity(category = "languages", itemKey = "German (Deutsch)", displayLabel = "German (Deutsch)", sortOrder = 17),
                CustomMasterEntity(category = "languages", itemKey = "Japanese (日本語)", displayLabel = "Japanese (日本語)", sortOrder = 18),
                CustomMasterEntity(category = "languages", itemKey = "Korean (한국어)", displayLabel = "Korean (한국어)", sortOrder = 19),

                // Instruments (instruments)
                CustomMasterEntity(category = "instruments", itemKey = "Acoustic Guitar", displayLabel = "Acoustic Guitar", sortOrder = 1),
                CustomMasterEntity(category = "instruments", itemKey = "Electric Guitar", displayLabel = "Electric Guitar", sortOrder = 2),
                CustomMasterEntity(category = "instruments", itemKey = "Bass Guitar", displayLabel = "Bass Guitar", sortOrder = 3),
                CustomMasterEntity(category = "instruments", itemKey = "Keyboard / Synthesizer", displayLabel = "Keyboard / Synthesizer", sortOrder = 4),
                CustomMasterEntity(category = "instruments", itemKey = "Piano", displayLabel = "Piano", sortOrder = 5),
                CustomMasterEntity(category = "instruments", itemKey = "Drums / Percussion", displayLabel = "Drums / Percussion", sortOrder = 6),
                CustomMasterEntity(category = "instruments", itemKey = "Tabla", displayLabel = "Tabla", sortOrder = 7),
                CustomMasterEntity(category = "instruments", itemKey = "Sitar", displayLabel = "Sitar", sortOrder = 8),
                CustomMasterEntity(category = "instruments", itemKey = "Flute (Bansuri)", displayLabel = "Flute (Bansuri)", sortOrder = 9),
                CustomMasterEntity(category = "instruments", itemKey = "Harmonium", displayLabel = "Harmonium", sortOrder = 10),
                CustomMasterEntity(category = "instruments", itemKey = "Dholak", displayLabel = "Dholak", sortOrder = 11),
                CustomMasterEntity(category = "instruments", itemKey = "Violin", displayLabel = "Violin", sortOrder = 12),
                CustomMasterEntity(category = "instruments", itemKey = "Saxophone", displayLabel = "Saxophone", sortOrder = 13),
                CustomMasterEntity(category = "instruments", itemKey = "Trumpet", displayLabel = "Trumpet", sortOrder = 14),
                CustomMasterEntity(category = "instruments", itemKey = "Sarod", displayLabel = "Sarod", sortOrder = 15),
                CustomMasterEntity(category = "instruments", itemKey = "Shehnai", displayLabel = "Shehnai", sortOrder = 16),
                CustomMasterEntity(category = "instruments", itemKey = "Mridangam", displayLabel = "Mridangam", sortOrder = 17),

                // Persona Badges (persona_badges)
                CustomMasterEntity(category = "persona_badges", itemKey = "Available for Collab", displayLabel = "Available for Collab", sortOrder = 1),
                CustomMasterEntity(category = "persona_badges", itemKey = "Session Ready", displayLabel = "Session Ready", sortOrder = 2),
                CustomMasterEntity(category = "persona_badges", itemKey = "Touring", displayLabel = "Touring", sortOrder = 3),
                CustomMasterEntity(category = "persona_badges", itemKey = "Home Studio Owner", displayLabel = "Home Studio Owner", sortOrder = 4),
                CustomMasterEntity(category = "persona_badges", itemKey = "Full-Time", displayLabel = "Full-Time", sortOrder = 5),
                CustomMasterEntity(category = "persona_badges", itemKey = "Weekend Warrior", displayLabel = "Weekend Warrior", sortOrder = 6),
                CustomMasterEntity(category = "persona_badges", itemKey = "Open to Remote Work", displayLabel = "Open to Remote Work", sortOrder = 7),

                // Project Types (project_types)
                CustomMasterEntity(category = "project_types", itemKey = "song", displayLabel = "Song", sortOrder = 1),
                CustomMasterEntity(category = "project_types", itemKey = "album", displayLabel = "Album", sortOrder = 2),
                CustomMasterEntity(category = "project_types", itemKey = "ep", displayLabel = "EP", sortOrder = 3),
                CustomMasterEntity(category = "project_types", itemKey = "podcast", displayLabel = "Podcast", sortOrder = 4),
                CustomMasterEntity(category = "project_types", itemKey = "dj_mix", displayLabel = "DJ Mix", sortOrder = 5),
                CustomMasterEntity(category = "project_types", itemKey = "film_score", displayLabel = "Film Score", sortOrder = 6),
                CustomMasterEntity(category = "project_types", itemKey = "commercial_jingle", displayLabel = "Commercial Jingle", sortOrder = 7),
                CustomMasterEntity(category = "project_types", itemKey = "live_performance", displayLabel = "Live Performance", sortOrder = 8),
                CustomMasterEntity(category = "project_types", itemKey = "music_video", displayLabel = "Music Video", sortOrder = 9),
                CustomMasterEntity(category = "project_types", itemKey = "custom_project", displayLabel = "Custom Project", sortOrder = 10),

                // Project Roles (project_roles)
                CustomMasterEntity(category = "project_roles", itemKey = "Singer", displayLabel = "Singer", sortOrder = 1),
                CustomMasterEntity(category = "project_roles", itemKey = "Composer", displayLabel = "Composer", sortOrder = 2),
                CustomMasterEntity(category = "project_roles", itemKey = "Music Director", displayLabel = "Music Director", sortOrder = 3),
                CustomMasterEntity(category = "project_roles", itemKey = "Producer", displayLabel = "Producer", sortOrder = 4),
                CustomMasterEntity(category = "project_roles", itemKey = "Lyricist", displayLabel = "Lyricist", sortOrder = 5),
                CustomMasterEntity(category = "project_roles", itemKey = "Session Musician", displayLabel = "Session Musician", sortOrder = 6),
                CustomMasterEntity(category = "project_roles", itemKey = "DJ", displayLabel = "DJ", sortOrder = 7),
                CustomMasterEntity(category = "project_roles", itemKey = "Mix Engineer", displayLabel = "Mix Engineer", sortOrder = 8),
                CustomMasterEntity(category = "project_roles", itemKey = "Mastering Engineer", displayLabel = "Mastering Engineer", sortOrder = 9),
                CustomMasterEntity(category = "project_roles", itemKey = "Sound Designer", displayLabel = "Sound Designer", sortOrder = 10),
                CustomMasterEntity(category = "project_roles", itemKey = "Band Member", displayLabel = "Band Member", sortOrder = 11),

                // Booking Statuses (booking_statuses)
                CustomMasterEntity(category = "booking_statuses", itemKey = "requested", displayLabel = "Requested", sortOrder = 1),
                CustomMasterEntity(category = "booking_statuses", itemKey = "confirmed", displayLabel = "Confirmed", sortOrder = 2),
                CustomMasterEntity(category = "booking_statuses", itemKey = "payment_pending", displayLabel = "Payment Pending", sortOrder = 3),
                CustomMasterEntity(category = "booking_statuses", itemKey = "cancelled", displayLabel = "Cancelled", sortOrder = 4),
                CustomMasterEntity(category = "booking_statuses", itemKey = "completed", displayLabel = "Completed", sortOrder = 5),

                // Studio Amenities (studio_amenities)
                CustomMasterEntity(category = "studio_amenities", itemKey = "Dolby Atmos", displayLabel = "Dolby Atmos", sortOrder = 1),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Vocal Booth", displayLabel = "Vocal Booth", sortOrder = 2),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Live Room", displayLabel = "Live Room", sortOrder = 3),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Valet Parking", displayLabel = "Valet Parking", sortOrder = 4),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Resident Engineers", displayLabel = "Resident Engineers", sortOrder = 5),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Private Lounge", displayLabel = "Private Lounge", sortOrder = 6),
                CustomMasterEntity(category = "studio_amenities", itemKey = "24x7 Access", displayLabel = "24x7 Access", sortOrder = 7),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Rehearsal Room", displayLabel = "Rehearsal Room", sortOrder = 8),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Chill Lounge", displayLabel = "Chill Lounge", sortOrder = 9),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Drums Included", displayLabel = "Drums Included", sortOrder = 10),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Parking Space", displayLabel = "Parking Space", sortOrder = 11),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Instruments Support", displayLabel = "Instruments Support", sortOrder = 12),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Acoustics Optimized", displayLabel = "Acoustics Optimized", sortOrder = 13),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Fiber Internet", displayLabel = "Fiber Internet", sortOrder = 14),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Stage Monitors", displayLabel = "Stage Monitors", sortOrder = 15),
                CustomMasterEntity(category = "studio_amenities", itemKey = "Bar & Diner Access", displayLabel = "Bar & Diner Access", sortOrder = 16),

                // Collaboration Types (collaboration_types)
                CustomMasterEntity(category = "collaboration_types", itemKey = "work_for_hire", displayLabel = "Work for Hire", sortOrder = 1),
                CustomMasterEntity(category = "collaboration_types", itemKey = "royalties_split", displayLabel = "Royalties Split", sortOrder = 2),
                CustomMasterEntity(category = "collaboration_types", itemKey = "co_write", displayLabel = "Co-Write", sortOrder = 3),
                CustomMasterEntity(category = "collaboration_types", itemKey = "session_payment", displayLabel = "Session Payment", sortOrder = 4),

                // Trust Badges (trust_badges)
                CustomMasterEntity(category = "trust_badges", itemKey = "Verified Artist", displayLabel = "Verified Artist", sortOrder = 1),
                CustomMasterEntity(category = "trust_badges", itemKey = "Top Host", displayLabel = "Top Host", sortOrder = 2),
                CustomMasterEntity(category = "trust_badges", itemKey = "Rising Talent", displayLabel = "Rising Talent", sortOrder = 3),
                CustomMasterEntity(category = "trust_badges", itemKey = "Studio Partner", displayLabel = "Studio Partner", sortOrder = 4)
            )

            for (seed in masterSeeds) {
                db.customMasterDao().insertCustomMaster(seed)
            }
        }
    }

    suspend fun loadCachedTaxonomy() {
        val db = database ?: return
        try {
            val dao = db.customMasterDao()
            val cached = dao.getAllCustomMasters()
            val grouped = cached.groupBy { it.category }
            
            grouped.forEach { (categoryId, list) ->
                updateActiveMemoryLists(categoryId, list)
            }
            Log.d("RepositoryProvider", "Loaded cached taxonomy for ${grouped.keys.size} categories.")
        } catch (e: Throwable) {
            Log.e("RepositoryProvider", "Error loading cached taxonomy: ${e.message}")
        }
    }

    fun syncTaxonomyFromFirestore() {
        val firestore = FirebaseManager.getFirestore()
        if (firestore == null) {
            Log.w("RepositoryProvider", "Firestore is not initialized. Using cached Room taxonomy or local fallbacks.")
            return
        }
        
        val categories = listOf("person_types", "languages", "genres", "instruments", "persona_badges", "project_types", "project_roles")
        
        categories.forEach { categoryId ->
            try {
                firestore.collection("taxonomy_categories").document(categoryId).collection("options")
                    .whereEqualTo("isActive", true)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            val optionsList = querySnapshot.documents.mapNotNull { doc ->
                                val label = doc.getString("label") ?: return@mapNotNull null
                                val value = doc.getString("value") ?: return@mapNotNull null
                                val sortOrder = doc.getLong("sortOrder")?.toInt() ?: 0
                                val emoji = doc.getString("emoji") ?: doc.getString("extraData") ?: ""
                                
                                CustomMasterEntity(
                                    category = categoryId,
                                    itemKey = value,
                                    displayLabel = label,
                                    extraData = emoji,
                                    sortOrder = sortOrder
                                )
                            }.sortedBy { it.sortOrder }
                            
                            if (optionsList.isNotEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val dao = database!!.customMasterDao()
                                        dao.deleteByCategory(categoryId)
                                        optionsList.forEach { option ->
                                            dao.insertCustomMaster(option)
                                        }
                                        updateActiveMemoryLists(categoryId, optionsList)
                                        Log.d("RepositoryProvider", "Synced category $categoryId successfully: ${optionsList.size} options cached.")
                                    } catch (e: Throwable) {
                                        Log.e("RepositoryProvider", "Error saving options for category $categoryId to Room: ${e.message}")
                                    }
                                }
                            }
                        } else {
                            Log.d("RepositoryProvider", "No options fetched or category $categoryId not seeded yet in Firestore. Falling back to default/cache.")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("RepositoryProvider", "Firestore fetch failed for category $categoryId: ${exception.message}. Onboarding will work offline using cache or hardcoded fallbacks.")
                    }
            } catch (e: Throwable) {
                Log.e("RepositoryProvider", "Exception in syncing category $categoryId: ${e.message}")
            }
        }
    }

    private fun updateActiveMemoryLists(categoryId: String, options: List<CustomMasterEntity>) {
        when (categoryId) {
            "person_types" -> {
                val mapped = options.map { option ->
                    val existing = com.example.onboarding.models.defaultRoles.find { it.id == option.itemKey || it.name.equals(option.itemKey, ignoreCase = true) }
                    com.example.onboarding.models.RoleItem(
                        id = option.itemKey,
                        name = option.displayLabel,
                        description = existing?.description ?: "independent music professional",
                        icon = existing?.icon ?: Icons.Default.MusicNote,
                        section = existing?.section ?: "Other Professionals"
                    )
                }
                if (mapped.isNotEmpty()) {
                    com.example.onboarding.models.availableRoles = mapped
                }
            }
            "languages" -> {
                val mapped = options.map { option ->
                    com.example.onboarding.models.LanguageItem(id = option.itemKey, name = option.displayLabel)
                }
                if (mapped.isNotEmpty()) {
                    com.example.onboarding.models.availableLanguages = mapped
                }
            }
            "genres" -> {
                val mapped = options.map { option ->
                    com.example.onboarding.models.GenreItem(id = option.itemKey, name = option.displayLabel)
                }
                if (mapped.isNotEmpty()) {
                    com.example.onboarding.models.availableGenres = mapped
                }
            }
            "instruments" -> {
                val mapped = options.map { option ->
                    val emoji = if (option.extraData.isNotEmpty()) option.extraData else {
                        com.example.onboarding.models.defaultInstruments.find { it.id == option.itemKey || it.name.equals(option.itemKey, ignoreCase = true) }?.emoji ?: "🎸"
                    }
                    com.example.onboarding.models.InstrumentItem(id = option.itemKey, name = option.displayLabel, emoji = emoji)
                }
                if (mapped.isNotEmpty()) {
                    com.example.onboarding.models.availableInstruments = mapped
                }
            }
        }
    }
}

