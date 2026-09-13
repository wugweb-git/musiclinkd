---
doc: handoff-gap-audit-2026-09-13
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@47f7c7b
folder: audits
originally: handoff-gap-audit.md
source: Claude Code session scratchpad (/private/tmp, not version-controlled)
---

> **CURRENT** · imported 13 Sep 2026. The sweep of every plan/audit/spec doc against the handoff, used to find what the handoff was missing. Rescued from a scratch directory that gets wiped.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# Handoff gap audit — 13 Sep 2026

## (a) Items open in the sources but missing from HANDOFF-2026-09-13.md §3

| Item | Source doc:line | Still open? (evidence) | Class |
|---|---|---|---|
| **No messaging/network screens exist at all** — chat conversation, networking feed, connection requests, collaborators (network) tab. `/messages` is a stub list with no thread view; `messages.tsx` rows are plain divs. This is separate from the "20 unlinked screens" (§3.B), which already have routes — these have none. | `docs/inventory-2026-09-11.md:1063-1066,1082-1083` (registry #78,79,82,83) | Yes — `ls src/routes` has no conversation/thread file; `messages.tsx` still has no composer | Lovable/UI work (decision-for-owner on priority first) |
| **~16 more registry screens have zero implementing route**, grouped: settings/support (Appearance, Raise ticket, Report user w/ reasons, Feedback), CMS (About/Pricing/Careers, admin pages editor), Saved & Favorites (Collection detail, Saved-item detail), onboarding/portfolio singles (Instrument proficiency, Credit attribution, portfolio Success screen), venue-type profile (editor + public template), notifications (detail route, onboarding notification-preferences step), Community Detail. (Splash is the only "no-route" registry item and it *is* covered, in §3.B item 13.) | `docs/inventory-2026-09-11.md:1054-1127` (Part 3A, full table) | Yes — verified `src/routes/settings*` has no appearance route; grep for "Appearance" in `src/data/settings.ts` = 0 hits | Lovable/UI work (decision-for-owner on which to build) |
| **Phase 9 (first-run/splash/onboarding gate) is blocked on a cookie-based Supabase auth migration that is in no phase**, not just "gated on 8." Session lives in `localStorage` only (`previewAuthStorage.ts`); `requireSupabaseAuth` is function middleware reading a Bearer header, so `beforeLoad` on SSR sees no user. `@supabase/ssr`/`createServerClient` are absent from `package.json`. | `Claude outputs/plans/PHASE-SPECS-2b-to-9.md:341-364` | Yes — confirmed: no `@supabase/ssr` in repo; `client.ts` storage resolves to `localStorage` | Claude-Code engineering |
| **Attached decision:** whether a logged-out visitor gets `/auth` or public Discover — requires classifying all 207 routes as public or gated, "product decision with an SEO tail," and must be decided before the Phase 9 branch opens. | same file, `:376-381` | Yes, unresolved | decision-for-owner |
| **DB parity gap to reach feature parity is ~30 Postgres tables** (20 ported + 10 redesigned) beyond the live 2 (`profiles`, `portfolio_items`) — covers booking, marketplace, opportunities, reviews, bands, business, messaging, notifications, verification, all currently seeded mock data. Includes named data-shape defects to fix in the port (money stored as strings in places, dates as strings, structured data flattened to display strings, polymorphic FKs with no constraint). Not the same as the Taxonomy spec (already in §3.20) or the Firestore `/users` PII bug (already in §3.4) — this is the wider schema-buildout decision. | `Claude outputs/audits/DB-GAP-TO-LOVABLE-PARITY.md` (whole doc) | Yes — explicitly "not yet decided… whether it's sequenced as one migration or phased alongside Phase 8" | decision-for-owner |
| **Guide repo still ships `Inter Tight`/weight-800 tokens the app dropped.** `frontend/guide/system/ml-tokens.css:96` sets `--font-display: 'Inter Tight'…`; `:84` keeps `--weight-4: 800`, used 17× in `ml-widgets.css`, while the app loads Inter only to 700. A third instance of the "guide vs. app" divergence framed generically in §3 item 0 (which names only radius and photography as instances). | `docs/inventory-2026-09-11.md:635-639`; verified live at `frontend/guide/system/ml-tokens.css:84,96` | Yes, confirmed in guide repo today | decision-for-owner (guide-lead-or-follow, item 0's family) |
| **`ui/skeleton` still defaults to a pink tint** (`bg-primary/10`) and has never rendered in the app — needs fixing before any of the ten skeleton call sites can route through the real Radix primitive (a concrete Phase 6 prerequisite, not just "de-duplication"). | `docs/inventory-2026-09-11.md:544-545` | Yes — confirmed at `src/components/ui/skeleton.tsx:4` | Claude-Code engineering |

## (b) Handoff claims the sources show as already done or overstated

Nothing found that is factually wrong. The handoff is itself the product of a same-day audit and is internally self-correcting (e.g., its own §2 table already flags where earlier Cowork claims were wrong). Two nearby notes, not contradictions:

- Handoff item 7 ("Weight 800: not a bug, nothing uses it") is accurate for **the app**, but the **guide repo** does use weight 800 seventeen times in `ml-widgets.css` — this is the missing item above, not an error in what's stated.
- The pre-supplied "already done" list's "Inter-only fonts" refers to the app repo (accurate, confirmed in `CHANGELOG.md`/`roadmap.md`); it does not cover the guide repo's residual `Inter Tight` reference, which is a separate, still-open file.

## (c) Count

**7 missing items found, all verified still open** (2 are decisions bundled with their engineering follow-on) vs. roughly 25 open items in §3 that the handoff already covers correctly.
