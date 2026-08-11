package com.example.onboarding.models

import com.google.firebase.Timestamp

/**
 * ============================================================================
 * MUSICLINKD AUTHORITATIVE BACKEND SCHEMA DEFINITIONS (musiclinkd-7ae30)
 * ============================================================================
 */

/**
 * MODULE 1: MODULE REGISTRY & FEATURE FLAGS
 * Firestore collection: `modules/{moduleKey}`
 */
data class ModuleRegistrySchema(
    val moduleKey: String,          // e.g. "marketplace", "studioBooking"
    val label: String,              // Display title
    val description: String,
    val enabled: Boolean = true,
    val navModuleKey: String? = null,
    val remoteConfigKey: String? = null,
    val updatedBy: String = "admin",
    val updatedAt: Any? = null,     // Timestamp
    val createdAt: Any? = null      // Timestamp
)

/**
 * MODULE 2: USER PROFILE, TRUST BADGES & BOOKMARKS
 * Firestore collection: `users/{userId}`
 */
data class UserTrustAndPersonaFields(
    val personaBadges: List<String> = emptyList(), // Self-selected via onboarding/profile edit
    val trustBadges: List<String> = emptyList()     // ADMIN-GRANTED ONLY (e.g. "Verified Artist", "Top Host")
)

/**
 * User Profile Bookmarks Mapping.
 * Top-level Firestore collection: `user_bookmarks/{ownerUid_savedUserId}`
 * Doc ID = "${ownerUid}_${savedUserId}"
 */
data class UserBookmarkSchema(
    val ownerUid: String,          // The user who saved the bookmark
    val savedUserId: String,       // The user being bookmarked
    val createdAt: Any? = null     // Timestamp
)

/**
 * MODULE 3: STUDIO BOOKINGS
 * Firestore collection: `studio_bookings/{bookingId}`
 */
data class StudioBookingSchema(
    val bookingId: String,
    val studioId: String,          // References the `studios` document
    val userId: String,            // Booker's UID (NOT "artistUserId")
    val date: String,              // e.g., "2026-08-11"
    val slotText: String,          // e.g., "2:00 PM – 4:00 PM"
    val bookedHourSlots: List<String> = emptyList(), // Granular slot list e.g. ["14:00", "15:00"]
    val totalAmount: Double = 0.0,  // Cost in base currency
    val bookingStatus: String = "requested", // "requested", "confirmed", "payment_pending", "cancelled", "completed"
    val paymentStatus: String = "PENDING",    // "PENDING", "PAID", "REFUNDED", "FAILED"
    val paymentTransactionId: String? = null,
    val createdAt: Any? = null     // Timestamp
)

/**
 * MODULE 4: REVIEWS & TRUST
 * Firestore collection: `reviews/{authorUid_targetId}`
 * Doc ID = "${authorUid}_${targetId}"
 */
data class ReviewSchema(
    val authorUid: String,         // Author user ID
    val targetType: String,        // "STUDIO" | "USER"
    val targetId: String,          // Target studio or user ID
    val rating: Double,            // e.g. 5.0
    val text: String,
    val bookingId: String = "",    // Optional link to studio_bookings/{bookingId} for verified purchase
    val createdAt: Any? = null     // Timestamp
)

/**
 * MODULE 5: VERIFICATION QUEUE
 * Firestore collection: `verification_requests/{requestId}`
 * Filter by status == "PENDING" for moderation queue
 */
data class VerificationRequestSchema(
    val requestId: String,
    val userId: String,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val identityDocumentUrl: String = "",
    val portfolioReferenceUrl: String = "",
    val submittedAt: Any? = null,
    val reviewedBy: String? = null,
    val feedbackNotes: String? = null
)

/**
 * MODULE 6: CONNECTIONS
 * Firestore collection: `connections/{senderUid_targetUserId}`
 * Doc ID = "${senderUid}_${targetUserId}"
 */
data class ConnectionSchema(
    val senderUid: String,
    val targetUserId: String,
    val status: String = "PENDING", // "PENDING", "ACCEPTED", "DECLINED"
    val createdAt: Any? = null
)

/**
 * MODULE 7: PROJECTS & GIG OPPORTUNITIES
 * Firestore collection: `projects/{projectId}`
 */
data class ProjectSchema(
    val projectId: String,
    val creatorUid: String,
    val title: String,
    val description: String = "",
    val roleRequired: String = "",
    val budget: String = "",
    val status: String = "OPEN",    // "OPEN", "IN_PROGRESS", "COMPLETED", "CLOSED"
    val genres: List<String> = emptyList(),
    val location: String = "",      // Location limits/city
    val applicantCount: Int = 0,    // Applicant count
    val collaborationType: String = "work_for_hire", // From "collaboration_types" taxonomy: "work_for_hire", "royalties_split", "co_write", "session_payment"
    val createdAt: Any? = null
)
