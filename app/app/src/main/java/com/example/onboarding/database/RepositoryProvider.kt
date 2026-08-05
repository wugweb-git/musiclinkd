package com.example.onboarding.database

import android.content.Context
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

    fun initialize(context: Context) {
        appContext = context.applicationContext
        if (database == null) {
            database = MusicLinkdDatabase.getDatabase(context)
        }
        FirebaseManager.initialize(context)

        CoroutineScope(Dispatchers.IO).launch {
            cleanExpiredCacheAndSyncPending()
            seedDatabaseIfEmpty()
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
    }
}

