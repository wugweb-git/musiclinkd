# Pending Issues & Roadmap — MusicLinkd Ecosystem

Documented as of 2026-11-22.

---

## 1. Pending App Codebase Issues (`musiclinkd-9e6ca875`)

| Priority | Issue | Location | Status / Resolution Plan |
| :--- | :--- | :--- | :--- |
| 🔴 **High** | **RPC Permission Block on `rental_booking_counts()`** | `src/lib/rentals.functions.ts:460` & `supabase/migrations/20260922330000_security_lint_and_policies.sql` | ✅ **Resolved**: Migration `20261122180000_fix_rental_booking_counts_rpc.sql` created and server function updated to use `supabaseAdmin` with `anonClient` fallback. |
| 🟡 **Medium** | **TanStack Start Deprecated API (`inputValidator`)** | 38 files across `src/lib/*.functions.ts` | ✅ **Resolved**: Modernized all 38 server function modules from `.inputValidator()` to `.validator(schema.parse)`. |
| 🟡 **Medium** | **Supabase Type Drift (`types.ts`)** | `src/integrations/supabase/types.ts` | ✅ **Resolved**: Pruned zombie type `person_type_instruments` to match live PostgreSQL taxonomy schema. |
| 🟡 **Medium** | **Component Deduplication (`Field`)** | `listing-form.tsx`, `booking.$id.details.tsx`, `opportunities.new.tsx`, `bands.new.tsx`, `profile.edit.tsx` | ✅ **Resolved**: Canonical `Field` re-exported from `ui.tsx` with auto-slug fallback; local boilerplate wrappers removed. `EditRow`, `ActionRow`, and `WorkspaceLink` intentionally left decoupled. |
| 🟢 **Low** | **Fast Refresh ESLint Warnings (27 occurrences)** | `listing-form.tsx`, `market.tsx`, `public-profile.tsx`, `bookable-setup.tsx`, `portfolio.tsx`, `drawer-list.tsx`, `qr.tsx`, `ui.tsx`, `identity.scan.tsx` | ✅ **Resolved**: Extracted hooks and helpers into dedicated `src/hooks/` and `src/lib/` modules. ESLint reports **0 errors, 0 warnings** across the codebase. |
| 🟢 **Low** | **Raw `<button>` Migration** | 62 route files | 🟡 **In Progress (Batch 1 Complete)**: Migrated bare buttons across `saved.tsx`, `booking.$id.tsx`, `booking.$id.review.tsx`, `opportunities.$id.index.tsx`, `reviews.write.$id.tsx`, `notifications.index.tsx`, and `marketplace/l.$id.tsx` to `<Btn>` / `<IconBtn>`. Full 5-point quality gate passing (0 ESLint errors/warnings, 0 TS errors, 0 token drift, 168/168 Storybook tests passed). |

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
