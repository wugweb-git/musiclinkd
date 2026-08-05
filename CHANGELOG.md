# Changelog

All notable repository-level changes are recorded here.

## Unreleased

### Changed

- Hosting collapsed from two Firebase Hosting sites to one — `musiclinkd.com` and
  `admin.musiclinkd.com` are now two custom domains on the same site, serving a
  merged build (`website/` + `admin/`'s Next.js export) produced by
  `scripts/build-hosting.sh`. Both domains serve identical content; visit
  `admin.musiclinkd.com/login` or `/dashboard` directly rather than its root.

### Added

- Simple product-area structure for app, website, admin, documentation, brand, styleguide, backend, infrastructure, and archive.
- Central styleguide guidance, base design tokens, and a rendered styleguide reference page.
- Static launch page for the marketing website (`website/`).
- Domain ownership map for the planned MusicLinkd URLs.
- Firebase Hosting configured for `musiclinkd.com` and `admin.musiclinkd.com` (`firebase.json`, `.firebaserc`).
- Single-project Firebase decision (`musiclinkd-7ae30` for Android, website, and admin) replacing the previously-documented, never-built dev/stage/prod split.
- Canonical Firestore schema (`app/docs/firestore/Firestore_Schema_Index_Rules.md`) consolidating three previously-contradictory schema docs, plus 7 new collections needed for admin (`admin_roles`, `reviews`, `verification_requests`, `moderation_flags`, `support_tickets`, `broadcast_messages`, `audit_logs`) and one for website content (`page_content`).
- Deployable Firebase config at the repo root: `firestore.rules`, `firestore.indexes.json`, `storage.rules`, `remoteconfig.template.json`.
- `admin/` is now a real Next.js 15 admin application (static export), replacing the static placeholder — 17 modules (Dashboard, Users, Studios, Marketplace, Bookings, Projects, Reviews, Verification, Moderation, Audit Logs, Settings, Reports & Exports, Notifications, Page Management, Messages, Analytics, Tracks), Google Sign-In auth, role-gated routing (`ROLE_ADMIN`/`ROLE_MODERATOR`/`ROLE_SUPPORT`), and a Tailwind theme matching the marketing site's brand tokens. See `admin/README.md` for module-by-module status (which are wired to real data vs. mock, and why).

### Changed

- The top-level styleguide is the canonical location for UI rules and reusable web-facing design references.
- The old static `admin/index.html` and `admin/404.html` placeholders moved to `archive/admin-placeholder/`.

### Fixed

- The previous Firestore rules draft let any signed-in user self-write their own `isVerified`/`role` fields on `/users` — closed before anything depended on it; those fields are now update-locked to admins/moderators.
