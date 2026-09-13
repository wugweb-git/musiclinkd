# Changelog

All notable repository-level changes are recorded here.

## Unreleased

### Added

- Gestalt (Pinterest) design-system reference layer, `frontend/guide/tokens/tokens.json`'s `foundation.gestalt` — full-fidelity spacing/rounding/opacity/elevation/motion scales and 423 icons (`frontend/guide/icons/`), extracted verbatim from `github.com/pinterest/gestalt` (Apache-2.0). Additive alongside the existing 4-token core, not a replacement — brand colors untouched. 29 Gestalt-Inspired components (Segmented Control through Wash Animated) built as vanilla HTML/CSS in `frontend/guide/merge3.html`, most with explicit multi-state rows (default/hover/disabled/error/etc.), in MusicLinkd's own brand colors, not Pinterest's.
- Android: Compose theme files (`app/.../ui/theme/Spacing.kt`, `Shape.kt`, `Elevation.kt`, `Motion.kt`) implementing the same Gestalt scale natively, wired into `Theme.kt`; starter composables (`GestaltButton`, `GestaltTag`, `GestaltStatus`, `GestaltCard`) in `app/.../ui/components/`.
- Admin: numbered screens (`FlowScreenDoc.screenNumber`, globally unique across ~120 screens) with a `variantOf`/`variantLabel` fork scheme; flat/stacked view toggle on the Flows page (`flows-stacked-view.tsx`); seed data for the 6 previously-empty collections (`taxonomy_categories`+`options` — 28 categories/221 values sourced from `Master_Data_Seed_Values.md`, `feed_posts`, `chat_threads`+`messages`, `support_tickets`, `user_settings`, `brand_assets`); a real Brand Assets CRUD module (`admin/app/(admin)/brand/`); visual trust-badge tiers (`components/shared/trust-badge.tsx`) on the Users list; many-to-many widget↔screen linking (`WidgetDoc.relatedScreens`) with a searchable multi-select picker; Gestalt-derived elevation CSS tokens in `admin/app/globals.css` (admin previously had none).
- Admin flows/screens registry: 2 flows previously missing entirely (`qr_share`, `saved_favorites`), `search` corrected from "no dedicated screen" to a real standalone `UniversalSearchFlow`, and `reviews` filled in from 6 to its full 11 real screens — all grounded in direct reads of the actual Android source, not the prior audit's assumptions.
- `MarketplaceItemDoc.quantity` field (units-available), matching the `StudioEquipmentItem.quantity` field that already existed.
- `admin/scripts/seed/` — a Firestore seed script (`npm run seed`, admin SDK, reuses the developer's existing `firebase login` session so no service-account key is needed) that populates every collection above with realistic, schema-accurate mock data consolidated from the product's original mock-data corpus, currently 672 documents across 28 collections.
- `StudioDoc.rooms?: StudioRoom[]` — individually bookable rooms within a studio (name/capacity/hourlyRate/amenities), seeded on Octave Sound Labs and Yash Raj Sound Studios. Studio "Engineers-as-bookable-staff" stays explicitly deferred (not this round).
- `UserDoc.hourlyRate`/`StudioDoc.hourlyRate` migrated `string` → `number` (per-hour INR equivalent; 0 = unset/not applicable) across schema, both editor sheets (now numeric inputs), the Studios list column, and all seed data — was a free-text display string, blocking any real price sort/filter.
- `webapp/` — a PWA product-reference build, generated (not hand-authored) from `default-flows.ts`/`default-screens.ts` and the seed data: one `screen.json` data contract (flow/screen metadata + a real schema-accurate sample document) plus a scaffolded `index.html` per real screen (124 screens across 24 flows), a shared native-app shell (safe-area, 100dvh, keyboard-aware layout, bottom-sheet mechanics, offline-capable manifest/service worker) with zero visual/brand styling by design, and a flow/screen picker at `webapp/index.html`. Regenerate via `admin/scripts/webapp-map/generate.ts`; never overwrites a screen's `index.html` once designed. See `webapp/README.md`.
- `admin/docs/architecture/Documentation_Backlog_2026-08-12.md` — a 20-category backlog of diagram/journey/lifecycle documentation (error journeys, state diagrams, Firestore/notification/analytics lifecycles, admin flows, website flows, etc.), explicitly saved for later, not actioned.

### Changed

- `foundation.type` grown 4 → 7 steps (`frontend/guide/tokens/tokens.json`): 12/14/16/22/28/32/40px, up
  from 12/16/22/32. Old steps 2/3/4 (16-18 / 22-28 / 32-44px) became 3/4/6 to make room; new steps 2
  (14px), 5 (28-36px) and 7 (40-56px, hero) fill the two gaps the old ramp left and give the Lovable
  app's ad hoc `--type-5` a real home. `meta.tokenBudget.ceiling` raised 108 → 111 for the three new
  steps, when the brand owner lifted the 4-step-per-scale cap `scripts/build-tokens.mjs` had enforced
  since v9 (`scale()` now checks the steps are numbered 1…n with no gaps, not that there are ≤4 of
  them). Regenerated everywhere the scale is emitted: `ml-tokens.css` (guide + `webapp/shared/`),
  `docs/handoff/MusicLinkdTokens.{kt,swift}` (now 7 `Type*`/`type*` entries each), `frontend/guide/index.html`.
- `scripts/build-guide.mjs`'s type-scale section rewritten to read the step count from `tokens.json`
  rather than assume 4: `scaleRow()` takes an explicit column list (type now gets its own 7-column
  table, split out of the shared 4-column space/radius/size/icon/avatar/elevation/opacity/motion
  table it no longer fits), the type specimens block and the typography-spec table both iterate
  `foundation.type`'s actual keys instead of a hardcoded `["1","2","3","4"]`.
- `scripts/migrations/24-type-renumber.mjs` — the type renumber `05-codemod-css.mjs` was held for
  (see `scripts/migrations/README.md`). Renumbers `--type-N`/`ml-type-N` 2→3, 3→4, 4→6 across
  `webapp/flows/` (416 references, 194 of 205 screens) in one pass, so no value is renumbered twice;
  idempotent via a per-file marker comment. The same remap applied by hand to every other file that
  references a type step by number: `ml-core.css`, `ml-widgets.css`, `scripts/screens/*.mjs` (26
  generator sources), `webapp/index.html`, `webapp/template/index.html` — 361 references, 31 files.
- `scripts/audit-system.mjs`'s token-ceiling check read the ceiling from a hardcoded `108` instead of
  `tokens.json`'s own `meta.tokenBudget.ceiling` — the same duplicate-value sprawl the ceiling exists
  to prevent, and it would have failed the build the moment the ceiling above moved to 111. Now reads
  `tokens.json` (falls back to 108 only if the file can't be found from the audit's working directory).
- `styleguide.html`/`merge3.html` moved from `frontend/website/` to `frontend/guide/` — now the source-of-truth home for design-system material (tokens, icons, docs, and these two interactive references), deploying to `/guide/*.html` via `scripts/build-hosting.sh`. Live-site links and `merge3.html`'s asset paths updated accordingly.
- Admin Page Management: removed `about`/`blog`/`careers` content pages, added `contact`; kept `home`/`pricing`/legal pages.
- Admin Capabilities module: removed the one-click "Seed defaults" button (CLI `npm run seed` remains the seeding path).

### Fixed

- `firestore.rules`: `/brand_assets/{assetId}` create/update had no admin bypass (only the literal owner could write, unlike every other owner-scoped collection such as `/users/{userId}`) — would have silently broken the new admin Brand Assets module. Added the same `isModerator()` bypass already used elsewhere.

### Added

- Governance rule §8 "Changelog Discipline" — every commit must include a `CHANGELOG.md` entry following Keep a Changelog format. Applies to all repos: app, admin, website, guide.
- Phone-frame visual mockups for all 11 screen sections in `guide/merge3.html` (Authentication, Onboarding, Discovery, Artist, Business, Studio, Marketplace, Messaging, Projects, Reputation, Settings).
- Color swatches (`<span class="ts">`) added to all bare hex codes in documentation tables across `guide/merge3.html` — Semantic Colors, Iconography Extended, Border Tokens, Accessibility, Design Philosophy sections. Complies with Governance §2 Color Swatch Rule.

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
