# Platform Roles & Permissions — MusicLinkd

## Products Topology
- **Website:** `musiclinkd.com`
- **Application Target:** `app.musiclinkd.com`
- **Admin Portal:** `admin.musiclinkd.com`
- **Contact Email:** `hello@musiclinkd.com`

---

## Role Definitions & Permission Matrix

### 1. Guest (`ROLE_GUEST`)
- **Target Environment:** `musiclinkd.com` & public preview screens on `app.musiclinkd.com`.
- **Permissions:** Public read-only access.
- **Capabilities:**
  - Browse public artist profiles and studio directories.
  - Play audio STEM sample previews.
  - View public marketplace listings.
- **Restrictions:** Cannot initiate connection requests, send messages, post updates, or submit studio bookings.

### 2. User / Free Member (`ROLE_USER`)
- **Target Environment:** `app.musiclinkd.com`
- **Permissions:** Standard authenticated access.
- **Capabilities:**
  - Create and edit personal profile (role, bio, genres, location).
  - Send up to 5 collaboration connection requests per month.
  - Direct message connected artists.
  - Search and book studio recording sessions.
  - Upload up to 3 audio STEM snippets.
- **Restrictions:** Basic profile analytics only; standard listing priority in discovery searches.

### 3. Verified Artist / Pro Member (`ROLE_VERIFIED`)
- **Target Environment:** `app.musiclinkd.com`
- **Permissions:** Pro subscriber & verified identity.
- **Capabilities:**
  - Display blue "Verified Pro" badge on profile and search cards.
  - Send unlimited collaboration connection requests.
  - Upload up to 25 high-resolution audio STEM tracks and media attachments.
  - Priority placement in AI Synergy Matchmaker recommendations.
  - Access detailed portfolio view analytics and profile engagement insights.
- **Restrictions:** Cannot moderate platform content or manage system-wide settings.

### 4. Admin (`ROLE_ADMIN`)
- **Target Environment:** `admin.musiclinkd.com`
- **Permissions:** Complete administrative and enterprise control.
- **Capabilities:**
  - Full user management (verification queue approval, badge issuance, user suspension).
  - Moderation queue management for flagged posts, STEM tracks, and reviews.
  - Studio partner verification and suite listing approval.
  - System configuration, FCM notification broadcast dispatch, and audit logging.
