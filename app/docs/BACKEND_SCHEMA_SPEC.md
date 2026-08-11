# MusicLinkd — Authoritative Live Backend Schema & Sync Specification (musiclinkd-7ae30)

This document provides a detailed, comprehensive architectural mapping of the **MusicLinkd** application ecosystem on project `musiclinkd-7ae30`.

---

## 🌐 Product Topology & Target Hosts

1. **Public Website (`musiclinkd.com`):** Editorial marketing landing pages, SEO catalog, pricing tiers, and public artist/studio discovery previews.
2. **Application Target (`app.musiclinkd.com`):** High-contrast light Jetpack Compose Android app for musicians, vocalists, producers, audio engineers, and studio bookings.
3. **Admin Portal (`admin.musiclinkd.com`):** Enterprise control center for moderation, verification queue, flagged content, user management, and system analytics.
4. **Primary Backend & Firebase Project:** `musiclinkd-7ae30` (Single authoritative Firebase project for MVP powering Auth, Firestore, Storage, Cloud Messaging, Analytics, Crashlytics, Remote Config, and Hosting across all 3 hosts).
5. **Official Contact Email:** `hello@musiclinkd.com`

---

## 🎨 Design System Principles & Constraints

- **Base Container Radius:** `24.dp` on all cards, modals, sheets, and dialogs.
- **Card Styling:** White canvas (`#FFFFFF`) with a `1.dp` border stroke in `BrandBorder` (`#ECECF1`). Drop shadows are strictly avoided.
- **Brand Primary:** `#EA4C89` (Dribbble Pink / Active Accents).
- **Brand Dark:** `#0D0B26` (Midnight Navy / Primary Typography).
- **Brand Background:** `#FCFCFD` (Soft Off-White Canvas).
- **Touch Targets:** Minimum dimensions of `48.dp x 48.dp`.

---

## 📜 Authoritative Firestore Collections & Schema Blueprints

### 1. Modules & Feature Toggles (`modules/{moduleKey}`)
- **Firestore Collection:** `modules/{moduleKey}` (Public Read, Admin Write)
- **Fields:**
  | Field Name | Type | Description |
  | :--- | :--- | :--- |
  | `moduleKey` | String (PK) | e.g., `"marketplace"`, `"studioBooking"` |
  | `label` | String | Module Title |
  | `description` | String | Module Description |
  | `enabled` | Boolean | True if active |
  | `navModuleKey` | String? | Nav route key |
  | `remoteConfigKey` | String? | Optional remote config key |
  | `updatedBy` | String | Admin UID |
  | `updatedAt` | Timestamp | Timestamp |
  | `createdAt` | Timestamp | Timestamp |

### 2. Studio Bookings (`studio_bookings/{bookingId}`)
- **Firestore Collection:** `studio_bookings/{bookingId}`
- **Security Rules:** `create` allowed if `request.auth.uid == request.resource.data.userId`. `read`/`update` allowed if user is booker or studio owner or moderator.
- **Fields:**
  | Field Name | Type | Description |
  | :--- | :--- | :--- |
  | `studioId` | String | Studio document reference |
  | `userId` | String | Booker UID (NOT "artistUserId") |
  | `date` | String | ISO Date e.g. `"2026-08-11"` |
  | `slotText` | String | Summary string e.g. `"2:00 PM – 4:00 PM"` |
  | `bookedHourSlots` | Array<String> | Granular slots e.g. `["14:00", "15:00"]` |
  | `totalAmount` | Number | Total cost |
  | `bookingStatus` | String | `"requested"`, `"confirmed"`, `"payment_pending"`, `"cancelled"`, `"completed"` |
  | `paymentStatus` | String | `"PENDING"`, `"PAID"`, `"REFUNDED"`, `"FAILED"` |
  | `paymentTransactionId` | String | Transaction identifier |
  | `createdAt` | Timestamp | Creation timestamp |

### 3. Reviews (`reviews/{authorUid_targetId}`)
- **Firestore Collection:** `reviews/{authorUid_targetId}`
- **Document ID Rule:** `${authorUid}_${targetId}`
- **Fields:**
  | Field Name | Type | Description |
  | :--- | :--- | :--- |
  | `authorUid` | String | Author user UID |
  | `targetType` | String | `"STUDIO"` or `"USER"` |
  | `targetId` | String | Target studio or user UID |
  | `rating` | Number | Star rating (1-5) |
  | `text` | String | Review content |
  | `bookingId` | String | Optional link to `studio_bookings/{bookingId}` |
  | `createdAt` | Timestamp | Creation timestamp |

### 4. Verification Queue (`verification_requests/{requestId}`)
- **Firestore Collection:** `verification_requests`
- **Moderation Filter:** `status == "PENDING"`, ordered by `submittedAt`
- **Fields:**
  | Field Name | Type | Description |
  | :--- | :--- | :--- |
  | `requestId` | String | Unique request ID |
  | `userId` | String | Submitting user UID |
  | `status` | String | `"PENDING"`, `"APPROVED"`, `"REJECTED"` |
  | `identityDocumentUrl` | String | Document storage link |
  | `portfolioReferenceUrl` | String | Portfolio URL |
  | `submittedAt` | Timestamp | Submission time |

### 5. Connections (`connections/{senderUid_targetUserId}`)
- **Firestore Collection:** `connections/{senderUid_targetUserId}`
- **Document ID Rule:** `${senderUid}_${targetUserId}` (sender-first)
- **Fields:**
  | Field Name | Type | Description |
  | :--- | :--- | :--- |
  | `senderUid` | String | Sender UID |
  | `targetUserId` | String | Recipient UID |
  | `status` | String | `"PENDING"`, `"ACCEPTED"`, `"DECLINED"` |

### 6. Projects & Gig Opportunities (`projects/{projectId}`)
- **Firestore Collection:** `projects/{projectId}`
- **Fields:**
  | Field Name | Type | Description |
  | :--- | :--- | :--- |
  | `projectId` | String | Project ID |
  | `creatorUid` | String | Creator UID |
  | `title` | String | Title |
  | `description` | String | Description |
  | `roleRequired` | String | Role required |
  | `budget` | String | Budget range string |
  | `status` | String | `"OPEN"`, `"IN_PROGRESS"`, `"COMPLETED"`, `"CLOSED"` |
  | `genres` | Array<String> | Genres |
  | `location` | String | Location limits/city |
  | `applicantCount` | Number | Count of applicants |
  | `collaborationType` | String | Value from `"collaboration_types"` taxonomy (`work_for_hire`, `royalties_split`, `co_write`, `session_payment`) |

### 7. User Trust & Persona Badges (`users/{userId}`)
- **Fields:**
  | Field Name | Type | Description |
  | :--- | :--- | :--- |
  | `personaBadges` | Array<String> | Self-selected via onboarding (`persona_badges` taxonomy) |
  | `trustBadges` | Array<String> | ADMIN-GRANTED ONLY (`trust_badges` taxonomy, e.g. "Verified Artist", "Top Host") |

### 8. User Bookmarks (`user_bookmarks/{ownerUid_savedUserId}`)
- **Firestore Collection:** `user_bookmarks/{ownerUid_savedUserId}`
- **Document ID Rule:** `${ownerUid}_${savedUserId}`
- **Fields:**
  | Field Name | Type | Description |
  | :--- | :--- | :--- |
  | `ownerUid` | String | Booker/Saver UID |
  | `savedUserId` | String | Bookmarked target UID |
  | `createdAt` | Timestamp | Bookmark timestamp |

### 9. Master Taxonomy Categories (`taxonomy_categories/{categoryId}/options/{optionId}`)
Public Read, Admin Write via `admin.musiclinkd.com`.
Categories:
1. `person_types` — User roles
2. `languages` — Languages
3. `genres` — Genres
4. `instruments` — Instruments
5. `persona_badges` — Self-selected persona badges
6. `project_types` — Project format types
7. `project_roles` — Collaborator credit roles
8. `booking_statuses` — Studio booking states (`requested`, `confirmed`, `payment_pending`, `cancelled`, `completed`)
9. `studio_amenities` — Studio features & gear
10. `collaboration_types` — Project payment/split types (`work_for_hire`, `royalties_split`, `co_write`, `session_payment`)
11. `trust_badges` — Admin-granted badges (`Verified Artist`, `Top Host`)

---

## 🚫 Deferred & Dropped Items
- `metadata_auth_providers` — Dropped (handled by Firebase Console auth config).
- `user_sessions` — Deferred (Spark plan, no serverless token routing).
- `metadata_cities` / `sub_areas` — Deferred (using plain text location fields).
- `onboarding_progress` — Kept local-only in Room database.
- `user_settings` — Deferred until dedicated settings preferences screen is introduced.
