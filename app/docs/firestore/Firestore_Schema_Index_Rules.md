# Firestore Schema, Indexes & Security Rules — MusicLinkd

**This is the canonical schema doc.** [`firestore_schema.md`](firestore_schema.md) and
[`../database/schema_definition.md`](../database/schema_definition.md) previously
duplicated parts of this collection list and had drifted out of sync with each other
(different collection sets, no shared source of truth). Both are now short pointers
back here — edit this file only.

Deployable copies of the rules and indexes below live at the repo root:
[`firestore.rules`](../../../firestore.rules) and
[`firestore.indexes.json`](../../../firestore.indexes.json). Keep this doc and those
files in sync by hand until schema changes are driven from one source.

---

## Collections Matrix

| Collection Path | Primary Key | Purpose | Public Read | Write |
| :--- | :--- | :--- | :--- | :--- |
| `/users/{userId}` | `userId` (= Auth UID) | Profiles: musicians, producers, studios, labels | Yes | Owner (restricted fields — see below), admin/moderator (all fields) |
| `/admin_roles/{userId}` | `userId` (= Auth UID) | Grants `ROLE_ADMIN` / `ROLE_MODERATOR` / `ROLE_SUPPORT` | No — self + admins only | Existing `ROLE_ADMIN` only |
| `/connections/{connectionId}` | `{senderUid}_{targetUid}` | Connection proposals & statuses | No — involved users only | Involved users |
| `/feed_posts/{postId}` | `postId` | Networking social feed updates | Yes | Author; admin/moderator can remove |
| `/studio_bookings/{bookingId}` | `bookingId` | Studio slot reservations | No — booker/studio owner/admin | Booker create; booker/studio owner/admin update |
| `/chat_threads/{threadId}` | `threadId` | Thread metadata (`memberUids: List<String>`) | No — thread members only | Thread members |
| `/chat_threads/{threadId}/messages/{messageId}` | `messageId` | Peer-to-peer chat messages | No — thread members only | Thread members |
| `/studios/{studioId}` | `studioId` | Recording studio / rehearsal space listings | Yes | Owner (`ownerUid`), admin |
| `/projects/{projectId}` | `projectId` | Collaboration calls / open project opportunities | Yes | Creator (`creatorUid`), admin |
| `/marketplace_items/{itemId}` | `itemId` | Gear, STEM packs, services for sale | Yes | Seller (`sellerUid`), admin (moderation removal) |
| `/reviews/{reviewId}` | `{authorUid}_{targetId}` | Reviews of a user or studio | Yes | Author create/update own; admin/moderator remove |
| `/verification_requests/{requestId}` | `requestId` | User/studio verification queue | No — submitter + admin/moderator | Submitter create; admin/moderator update status |
| `/moderation_flags/{flagId}` | `flagId` | Reports on posts, audio, reviews, listings, users | No — reporter (own) + admin/moderator | Any auth user create; admin/moderator update |
| `/support_tickets/{ticketId}` | `ticketId` | In-app support requests (mirrors `hello@musiclinkd.com` inbox) | No — requester + admin/support | Requester create; admin/support update |
| `/broadcast_messages/{broadcastId}` | `broadcastId` | FCM push broadcast log | No — admin only | `ROLE_ADMIN` only |
| `/audit_logs/{logId}` | `logId` | Admin action trail | No — admin only | `ROLE_ADMIN` / `ROLE_MODERATOR` only |
| `/page_content/{pageId}` | route slug, e.g. `home`, `about`, `legal-privacy` | Website page copy — homepage, footer pages, legal pages, managed from admin's Page Management module | Yes | `ROLE_ADMIN` only |

### Field reference

**`/users/{userId}`**
`displayName: String`, `email: String`, `role: String` (`ARTIST`|`PRODUCER`|`VOCALIST`|`STUDIO`|`LABEL` — business role, distinct from admin permissions in `/admin_roles`), `isVerified: Boolean`, `isBanned: Boolean`, `isAvailable: Boolean`, `hourlyRate: String`, `location: String`, `bio: String`, `genres: List<String>`, `avatarUrl: String`, `createdAt: Timestamp`.

`isVerified`, `isBanned`, and `role` are **admin-controlled fields** — see the rules
below. A plain user can create and edit their own profile but cannot self-verify,
self-unban, or grant themselves an admin-adjacent role.

**`/admin_roles/{userId}`**
`role: String` (`ROLE_ADMIN`|`ROLE_MODERATOR`|`ROLE_SUPPORT`), `grantedBy: String`, `grantedAt: Timestamp`. Deliberately kept out of `/users` so admin status is never exposed by the public profile read.

**`/studios/{studioId}`**
`ownerUid: String`, `name: String`, `location: String`, `rating: Number`, `reviewCount: Number`, `hourlyRate: String`, `verifiedGold: Boolean`, `amenities: List<String>`, `consoleType: String`, `imageUrl: String`.

**`/projects/{projectId}`**
`creatorUid: String`, `title: String`, `roleRequired: String`, `budget: String`, `status: String` (`OPEN`|`IN_PROGRESS`|`COMPLETED`), `genres: List<String>`, `createdAt: Timestamp`.

**`/marketplace_items/{itemId}`**
`sellerUid: String`, `title: String`, `price: String`, `category: String` (`GEAR`|`STEMS`|`SERVICES`), `condition: String` (`NEW`|`LIKE_NEW`|`USED`), `imageUrl: String`, `createdAt: Timestamp`.

**`/reviews/{reviewId}`**
`targetType: String` (`STUDIO`|`USER`), `targetId: String`, `authorUid: String`, `rating: Number` (1–5), `text: String`, `createdAt: Timestamp`. Document ID is `${authorUid}_${targetId}` to enforce one review per author per target, same pattern as `connections`.

**`/verification_requests/{requestId}`**
`subjectType: String` (`USER`|`STUDIO`), `subjectId: String`, `submittedBy: String`, `status: String` (`PENDING`|`APPROVED`|`REJECTED`), `documentPaths: List<String>` (Storage paths under `verification_documents/`), `submittedAt: Timestamp`, `reviewedBy: String`, `reviewedAt: Timestamp`, `notes: String`.

**`/moderation_flags/{flagId}`**
`targetType: String` (`POST`|`AUDIO`|`REVIEW`|`MARKETPLACE_ITEM`|`USER`), `targetId: String`, `reportedBy: String`, `reason: String`, `status: String` (`PENDING`|`ACTIONED`|`DISMISSED`), `createdAt: Timestamp`, `reviewedBy: String`, `reviewedAt: Timestamp`.

**`/support_tickets/{ticketId}`**
`requesterUid: String`, `requesterEmail: String`, `subject: String`, `message: String`, `status: String` (`OPEN`|`IN_PROGRESS`|`RESOLVED`), `assignedTo: String`, `createdAt: Timestamp`, `updatedAt: Timestamp`.

**`/broadcast_messages/{broadcastId}`**
`title: String`, `body: String`, `audience: String` (`ALL` or an FCM topic name), `sentBy: String`, `sentAt: Timestamp`, `status: String` (`QUEUED`|`SENT`|`FAILED`).

**`/audit_logs/{logId}`**
`actorUid: String`, `action: String`, `targetPath: String`, `timestamp: Timestamp`, `metadata: Map`. Client writes are allowed for now so the admin panel works before any Cloud Functions exist; once a Functions backend lands, lock writes to server-only (`allow write: if false` client-side) so the trail can't be edited by an admin covering their own tracks.

**`/page_content/{pageId}`**
`route: String` (URL path on musiclinkd.com, e.g. `/`, `/about`, `/legal/privacy`), `title: String`, `status: String` (`DRAFT`|`PUBLISHED`), `content: String` (plain text/markdown body — no block-based page builder in v1), `seoDescription: String`, `updatedBy: String`, `updatedAt: Timestamp`. Document ID is a route slug (`home`, `about`, `pricing`, `careers`, `blog`, `legal-privacy`, `legal-terms`, `legal-cookies`) matching the empty page folders already scaffolded under `website/`.

**Important limitation, not yet resolved:** `website/` is still plain static HTML
with content hardcoded directly in `index.html` and friends — it has no
JavaScript, no build step, and no code that reads Firestore. Editing a page here
updates this collection; it does **not** yet change what's live at
musiclinkd.com. Wiring the marketing site to actually render this content is a
separate follow-on task (most likely converting `website/` to a static-generated
site that fetches this collection at build time, mirroring how `admin/` now
works) — this schema and the admin CRUD UI are the data layer for that, not a
complete pipeline on their own.

---

## Required Composite Indexes (`firestore.indexes.json`)

See the deployable file at [`firestore.indexes.json`](../../../firestore.indexes.json)
for the full, current list — `studios` (location+rating, verifiedGold+hourlyRate),
`projects` (status+createdAt), `marketplace_items` (category+createdAt), plus the
admin-queue indexes (`moderation_flags` status+createdAt,
`verification_requests` status+submittedAt, `reviews` targetId+createdAt).

---

## Security Rules Specification (`firestore.rules`)

See the deployable file at [`firestore.rules`](../../../firestore.rules) for the full,
current rules — reproduced here for reference:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    function signedIn() {
      return request.auth != null;
    }

    function adminRole() {
      return signedIn()
        ? get(/databases/$(database)/documents/admin_roles/$(request.auth.uid)).data.role
        : null;
    }

    function isAdmin() {
      return adminRole() == 'ROLE_ADMIN';
    }

    function isModerator() {
      return isAdmin() || adminRole() == 'ROLE_MODERATOR';
    }

    function isSupport() {
      return isAdmin() || adminRole() == 'ROLE_SUPPORT';
    }

    function isOwner(uid) {
      return signedIn() && request.auth.uid == uid;
    }

    match /users/{userId} {
      allow read: if true;
      allow create: if isOwner(userId)
        && request.resource.data.role in ['ARTIST', 'PRODUCER', 'VOCALIST', 'STUDIO', 'LABEL']
        && request.resource.data.isVerified == false
        && request.resource.data.isBanned == false;
      allow update: if isModerator()
        || (isOwner(userId)
            && !request.resource.data.diff(resource.data)
                 .affectedKeys()
                 .hasAny(['isVerified', 'isBanned', 'role']));
    }

    match /admin_roles/{userId} {
      allow read: if isOwner(userId) || isAdmin();
      allow write: if isAdmin();
    }

    match /connections/{connectionId} {
      allow read, write: if signedIn()
        && (request.auth.uid == resource.data.senderUid
            || request.auth.uid == resource.data.targetUserId);
    }

    match /feed_posts/{postId} {
      allow read: if true;
      allow create, update: if signedIn()
        && (request.auth.uid == request.resource.data.authorUid || isModerator());
      allow delete: if isModerator()
        || (signedIn() && request.auth.uid == resource.data.authorUid);
    }

    match /studio_bookings/{bookingId} {
      allow create: if signedIn() && request.auth.uid == request.resource.data.userId;
      allow read, update: if signedIn()
        && (request.auth.uid == resource.data.userId
            || request.auth.uid == get(/databases/$(database)/documents/studios/$(resource.data.studioId)).data.ownerUid
            || isModerator());
    }

    match /chat_threads/{threadId} {
      allow read, update: if signedIn() && request.auth.uid in resource.data.memberUids;
      allow create: if signedIn() && request.auth.uid in request.resource.data.memberUids;

      match /messages/{messageId} {
        allow read, create: if signedIn()
          && request.auth.uid in get(/databases/$(database)/documents/chat_threads/$(threadId)).data.memberUids;
      }
    }

    match /studios/{studioId} {
      allow read: if true;
      allow create: if signedIn() && request.auth.uid == request.resource.data.ownerUid;
      allow update, delete: if isModerator()
        || (signedIn() && request.auth.uid == resource.data.ownerUid);
    }

    match /projects/{projectId} {
      allow read: if true;
      allow create: if signedIn() && request.auth.uid == request.resource.data.creatorUid;
      allow update, delete: if isModerator()
        || (signedIn() && request.auth.uid == resource.data.creatorUid);
    }

    match /marketplace_items/{itemId} {
      allow read: if true;
      allow create: if signedIn() && request.auth.uid == request.resource.data.sellerUid;
      allow update, delete: if isModerator()
        || (signedIn() && request.auth.uid == resource.data.sellerUid);
    }

    match /reviews/{reviewId} {
      allow read: if true;
      allow create, update: if signedIn()
        && request.auth.uid == request.resource.data.authorUid
        && reviewId == request.auth.uid + '_' + request.resource.data.targetId;
      allow delete: if isModerator()
        || (signedIn() && request.auth.uid == resource.data.authorUid);
    }

    match /verification_requests/{requestId} {
      allow create: if signedIn() && request.auth.uid == request.resource.data.submittedBy;
      allow read: if isModerator()
        || (signedIn() && request.auth.uid == resource.data.submittedBy);
      allow update: if isModerator();
    }

    match /moderation_flags/{flagId} {
      allow create: if signedIn() && request.auth.uid == request.resource.data.reportedBy;
      allow read: if isModerator()
        || (signedIn() && request.auth.uid == resource.data.reportedBy);
      allow update: if isModerator();
    }

    match /support_tickets/{ticketId} {
      allow create: if signedIn() && request.auth.uid == request.resource.data.requesterUid;
      allow read, update: if isSupport()
        || (signedIn() && request.auth.uid == resource.data.requesterUid);
    }

    match /broadcast_messages/{broadcastId} {
      allow read: if isAdmin();
      allow create: if isAdmin();
    }

    match /audit_logs/{logId} {
      allow read: if isAdmin();
      allow create: if isModerator();
    }

    match /page_content/{pageId} {
      allow read: if true;
      allow write: if isAdmin();
    }
  }
}
```

### Bootstrapping the first admin

Rules only let an existing `ROLE_ADMIN` write to `/admin_roles`, so the very first
admin document can't be created by the app itself, and the `firebase` CLI has no
generic "write a document" command. Create it once, manually, after deploying rules:

Firebase console → Firestore Database → Start collection → collection ID
`admin_roles` → document ID: your Auth UID (find it under Authentication → Users) →
add fields `role` (string) = `ROLE_ADMIN`, `grantedBy` (string) = `bootstrap`,
`grantedAt` (timestamp) = now.

Every admin after that can be granted through the admin panel itself once that UI
exists — client writes to `/admin_roles` are already restricted to existing
`ROLE_ADMIN` users, so this bootstrap step only has to happen once.
