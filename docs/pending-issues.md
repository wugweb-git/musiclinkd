# Pending Issues & Roadmap — MusicLinkd Ecosystem

Documented as of 2026-11-22.

---

## 1. Pending App Codebase Issues (`musiclinkd-app` / `musiclinkd-9e6ca875`)

| Priority | Issue | Location | Status / Resolution Plan |
| :--- | :--- | :--- | :--- |
| 🔴 **High** | **RPC Permission Block on `rental_booking_counts()`** | `src/lib/rentals.functions.ts:460` & `supabase/migrations/20260922330000_security_lint_and_policies.sql` | ✅ **Resolved**: Migration `20261122180000_fix_rental_booking_counts_rpc.sql` created and server function updated to use `supabaseAdmin` with `anonClient` fallback. |
| 🟡 **Medium** | **TanStack Start Deprecated API (`inputValidator`)** | 38 files across `src/lib/*.functions.ts` | ✅ **Resolved**: Modernized all 38 server function modules from `.inputValidator()` to `.validator(schema.parse)`. |
| 🟡 **Medium** | **Supabase Type Drift (`types.ts`)** | `src/integrations/supabase/types.ts` | ✅ **Resolved**: Pruned zombie type `person_type_instruments` to match live PostgreSQL taxonomy schema. |
| 🟡 **Medium** | **Component Deduplication (`Field`)** | `listing-form.tsx`, `booking.$id.details.tsx`, `opportunities.new.tsx`, `bands.new.tsx`, `profile.edit.tsx` | ✅ **Resolved**: Canonical `Field` re-exported from `ui.tsx` with auto-slug fallback; local boilerplate wrappers removed. `EditRow`, `ActionRow`, and `WorkspaceLink` intentionally left decoupled. |
| 🟢 **Low** | **Fast Refresh ESLint Warnings (27 occurrences)** | `listing-form.tsx`, `market.tsx`, `public-profile.tsx`, `bookable-setup.tsx`, `portfolio.tsx`, `drawer-list.tsx`, `qr.tsx`, `ui.tsx`, `identity.scan.tsx` | ✅ **Resolved**: Extracted hooks and helpers into dedicated `src/hooks/` and `src/lib/` modules. ESLint reports **0 errors, 0 warnings** across the codebase. |
| 🟢 **Low** | **Raw `<button>` Migration** | 62 route files | 🟡 **In Progress (Batch 1 & 2 Complete)**: Migrated bare buttons across `saved.tsx`, `booking.$id.tsx`, `booking.$id.review.tsx`, `opportunities.$id.index.tsx`, `reviews.write.$id.tsx`, `notifications.index.tsx`, `marketplace/l.$id.tsx`, `services.$id.index.tsx`, `marketplace/seller.$id.tsx`, `booking.$id.details.tsx`, `conversation.$id.tsx`, and `notifications.settings.tsx` to `<Btn>` / `<IconBtn>`. Full 5-point quality gate passing (0 ESLint errors/warnings, 0 TS errors, 0 token drift, clean build). |
| 🟢 **Low** | **Dynamic Identity QR Wireframe (`/identity/q/$code`)** | `src/routes/identity.q.$code.tsx` & `src/lib/qr.functions.ts` | ✅ **Resolved**: Replaced static wireframe with live database resolver `resolveDestination()`. Queries `profiles` and `bookables` with active/private/deactivated handling, offline resilience, and demo fallbacks. |
| 🟢 **Low** | **PWA Ground Token Alignment** | `public/manifest.webmanifest` & `src/routes/__root.tsx` | ✅ **Resolved**: Synchronized manifest and root layout `<meta name="theme-color">` to canonical `#F3F1EC` warm ground token. |
| 🟡 **Medium** | **Collab Owner Guard Migration (`collab_owner_guard`)** | `supabase/migrations/20261015120000_collab_owner_guard.sql` | ⏸️ **Postponed**: Parked in `docs/logs/postponed-issues.json` (Issue `SEC-COL-001`). Non-blocking until Lovable Cloud runs migration to avoid missing RPC exceptions. |

---

## 2. WordPress & Supabase Database Mapping Strategy

### Architectural Principle: "Supabase as Operational Buffer, WordPress as CMS/CRM"

To ensure zero downtime, zero data corruption, and robust performance, we do **not** couple WordPress MySQL and Supabase PostgreSQL with brittle database-level replication or direct SQL bridges. Instead, we maintain a clean asynchronous, API-driven mapping layer:

```
┌─────────────────────────────────┐                 ┌─────────────────────────────────┐
│     WordPress CMS & Hub         │                 │      MusicLinkd App Buffer      │
│  (Hostinger MySQL / musiclinkd) │                 │ (Supabase PostgreSQL / App Rep) │
├─────────────────────────────────┤                 ├─────────────────────────────────┤
│ • Canonical Landing & Legal     │  REST / Webhook │ • Operational OLTP Data         │
│ • FluentCRM (contact@)          │ ──────────────> │ • User Auth (auth.users)        │
│ • Fluent Forms (Leads/Waitlist) │ <────────────── │ • Bookings, Escrow & Collabs    │
│ • Headless CMS (/wp-json/wp/v2) │                 │ • Realtime Messages & Storage   │
└─────────────────────────────────┘                 └─────────────────────────────────┘
```

### Architectural Law: Supabase as Buffer, WordPress as Canonical Home
- **Supabase (`gsqeymhtyzepwcnorjun.supabase.co`):** Operational buffer for the customer app (`app.musiclinkd.com`). Handles high-concurrency client reads, auth JWTs, real-time messaging, and mobile caching.
- **WordPress (`musiclinkd.com`):** The canonical home where everything lands in the end. Centralizes all customer Talent & Business profiles (`profile` CPT + SCF), CMS editorial content, SEO indexing, and marketing automation (`FluentCRM`).

### Mapping Matrix

1. **Content & Editorial (Gutenberg → App):**
   - **Source:** WordPress `wp_posts` (`/wp-json/wp/v2/pages`).
   - **Buffer:** In-memory TanStack SSR cache + Supabase `cms_pages` table as permanent offline fallback.
   - **Sync:** Handled by scheduled GitHub Actions worker (`scripts/worker_content_sync.py`) running every 6 hours.

2. **Lead Capture & Enquiries (Fluent Forms → App):**
   - **Source:** WordPress Fluent Forms (`Early Access`, `Creator Application`, `Contact Desk`).
   - **Buffer:** Direct webhook to Supabase Edge Function or ingestion into Supabase `enquiries` and `support_requests`.
   - **Benefit:** Marketing captures leads without requiring users to register full auth accounts first.

3. **Customer Relationship & Marketing (FluentCRM → Supabase):**
   - **Source:** FluentCRM (`wp_fluentcrm_subscribers`).
   - **Buffer:** Supabase `profiles.discovery_prefs` & `notification_preferences`.
   - **Sync:** Unsubscribe events and contact status synced bidirectionally to prevent spamming users across channels.

4. **Identity & Auth Federation (Future Phase):**
   - **Primary Source:** Supabase `auth.users` (JWT).
   - **WordPress Mapping:** MiniOrange / OpenID Connect plugin on WordPress allowing creator login via Supabase credentials without storing separate passwords on MySQL.

---

## 3. App Ecosystem Audit & Verification Status (2026-11-24)

| Area / System | Current Status | Details & Engineering Context |
| :--- | :--- | :--- |
| **Production Deployments** | ✅ **Live & Verified** | • `https://app.musiclinkd.com`: Cloudflare Edge / Nitro runtime (`musiclinkd-app` / `musiclinkd-9e6ca875`). `HTTP/2 200 OK`.<br>• `https://musiclinkd.com`: Hostinger LiteSpeed + hCDN (WordPress FSE theme). `HTTP/2 200 OK`.<br>• Headless CMS Sync: All 14 canonical slugs returning HTTP 200 via `scripts/worker_content_sync.py` (`docs/logs/sync-health.json`). |
| **Server Functions (`src/lib/*.functions.ts`)** | ✅ **100% Real DB Backed** | All 44 server function modules queried against live Supabase PostgreSQL tables (`profiles`, `bookables`, `bookings`, `messages`, `reviews`, `channels`, `escrow_holds`, `rentals`, `bands`). Zero mock stubs, dummy data, or fake "not implemented" throws. |
| **Dynamic QR Resolver (`/identity/q/$code`)** | ✅ **Resolved & Live** | `resolveDestination` in `src/lib/qr.functions.ts` checks `profiles` by username and `bookables` by slug. Handles active/private/deactivated states, offline network resilience, and demo fallbacks. |
| **PWA Manifest & Ground Color** | ✅ **Aligned** | `public/manifest.webmanifest` and `src/routes/__root.tsx` aligned to canonical `#F3F1EC` warm ground. |
| **Raw `<button>` Standardisation** | 🟡 **Batch 1 & 2 Complete** | 12 routes migrated to `<Btn>` / `<IconBtn>`. Remaining routes can be updated incrementally without blocking release. All 5 quality gates pass. |
| **Spotify Integration** | 🟡 **Ready for Credentials** | OAuth callback (`/auth/callback/spotify`), schema (`spotify_connections`), and `SpotifyRow` UI built. Awaiting production Spotify Developer App Client ID & Secret. |
| **Calendar Feed** | ✅ **Integrated** | Device-level calendar via `@capacitor-community/calendar` + universal `webcal://` `.ics` subscriptions via `/api/calendar/subscribe` SSR feed. |
| **Payments / Escrow** | ⏸️ **Intentionally Deferred** | Peer-to-peer agreement and connection model. In-app payment gateway processing deferred per product decision. |
| **Collab Owner Guard (`SEC-COL-001`)** | ⏸️ **Postponed in JSON** | Non-blocking RPC migration parked in `docs/logs/postponed-issues.json` until Lovable Cloud runs `20261015120000_collab_owner_guard.sql`. |
