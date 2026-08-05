# Platform Roles, Access Rights & Restrictions — MusicLinkd

## Platform Role Hierarchy

| Role ID | Role Name | Primary Target | Permissions | Accessible Modules | Key Actions & Restrictions |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `ROLE_GUEST` | Guest / Visitor | `musiclinkd.com` | Public Read-Only | Discover, Public Profiles, Studio Directory | **Actions:** Browse artists, preview STEMs.<br>**Restrictions:** Cannot message, book, or connect. |
| `ROLE_USER` | Free Member | `app.musiclinkd.com` | Authenticated Standard | Discover, Feed, Messaging, Bookings | **Actions:** 5 connections/month, upload 3 STEMs.<br>**Restrictions:** Basic analytics only. |
| `ROLE_VERIFIED` | Verified Pro Artist | `app.musiclinkd.com` | Pro Subscriber | Full App Suite, Pro Badge, Advanced Analytics | **Actions:** Unlimited connections, 25 STEMs, priority chat.<br>**Restrictions:** Cannot list studios. |
| `ROLE_BAND` | Band / Ensemble | `app.musiclinkd.com` | Group Entity | Group Management, Booking, STEM Packs | **Actions:** Multi-member roster management. |
| `ROLE_STUDIO` | Studio Owner / Host | `app.musiclinkd.com` | Studio Partner | Studio Booking Engine, Slot Management | **Actions:** Manage studio suites, equipment specs, hourly rates. |
| `ROLE_BUSINESS` | Label / Agency | `app.musiclinkd.com` | Enterprise | Talent Scouting, Bulk Messaging | **Actions:** Contract management, talent analytics. |
| `ROLE_MODERATOR` | Content Moderator | `admin.musiclinkd.com` | Moderation Suite | Moderation Queue, Flagged Content, Reports | **Actions:** Review flagged posts, suspend abusive users. |
| `ROLE_ADMIN` | System Administrator | `admin.musiclinkd.com` | Full Administrative | Complete Platform Architecture | **Actions:** System config, billing audits, verification queue. |
