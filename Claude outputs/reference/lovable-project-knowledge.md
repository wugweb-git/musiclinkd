---
doc: lovable-project-knowledge
version: 2.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: reference
originally: lovable-project-knowledge.md
---

> **CURRENT** · v2.0 · 2026-09-13. Mirror of the Lovable project knowledge actually pushed via set_project_knowledge.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# MusicLinkd

## 1. Purpose and users

India's professional network and operating system for the music industry. Not a social feed —
task-oriented infrastructure for finding work, hiring, booking rooms, renting gear, collaborating.

**This app is THE MusicLinkd frontend**, replacing a Kotlin/Compose Android app being retired.
Everything here ships to real users.

*Talent* — singers, instrumentalists, session artists, producers, composers, lyricists, DJs, RJs,
podcasters, engineers, bands: discovered, credited, booked, collaborating. *Businesses and hirers*
— labels, studios, jam rooms, venues, organisers, schools, dealers, rental providers, agencies:
find and verify talent, publish opportunities, take bookings, list gear.

**The job chain:** discover → evaluate (portfolio, credits, verification, reviews, availability) →
connect → transact (hire, book, buy, rent) → collaborate → review.

**India-first, not localised-later.** ₹ pricing, Indian cities/states/languages, and Indian
instruments first-class — tabla, sitar, harmonium, sarod, santoor, bansuri, veena, tanpura, dholak.
Copy is English for now; the IA must support localisation.

**Form factor.** Mobile-first, 430px shell, verified at 412px. Bottom nav is **exactly five** —
Home, Discover, Connections, Market, Profile. Never a sixth.

## 2. Architecture, schema and APIs

**Stack.** TanStack Start (SSR) · React 19 · Tailwind v4 · Radix/shadcn · Supabase · bun.

**Routing.** Flat dot-named files under `src/routes/` (`booking.$id.pay.tsx` → `/booking/:id/pay`).
Bare `$` for dynamic segments. `__root.tsx` is the only shell. `routeTree.gen.ts` is generated —
never edit. No `src/pages/`, no `app/layout.tsx`. Every route defines its own `head()`. Wizards
keep state in `?step=`. No orphan routes.

**Directories.** `src/components/ml/` the kit (`ui.tsx` primitives, `kit.tsx` shared patterns) ·
`src/components/ui/` 45 Radix primitives, use them · `src/data/` content · `src/lib/` helpers and
server fns · `src/integrations/supabase/` · `src/hooks/use-auth.ts`.

**Data access.** Server-side `createServerFn` in `*.functions.ts` guarded by `requireSupabaseAuth`
(see `src/lib/portfolio.functions.ts`). Public reads via narrow anon SELECT policies. No
client-side writes.

**Live database — two tables.**

`profiles` — PK `id` → `auth.users(id)` CASCADE. `username` (unique), `display_name`, `headline`,
`bio`, `city`, `state`, `country`, `roles[]`, `instruments[]`, `genres[]`, `languages[]`,
`specialisations[]`, `work_modes[]`, `intents[]`, `contact_channels[]`, `experience_level`,
`avatar_url`, `availability`, `visibility`, `links` jsonb, `discovery_prefs` jsonb,
`onboarding_step`, `onboarding_draft` jsonb, `onboarding_complete`, timestamps. RLS: public SELECT;
owner INSERT/UPDATE on `auth.uid() = id`.

`portfolio_items` — PK `id` uuid, `user_id` → `auth.users(id)` CASCADE. `title`, `kind`,
`description`, `role`, `collaborators[]`, `cover_url`, `media_url`, `release_year`, `published`,
`sort_order`, timestamps. Index `(user_id, sort_order)`. RLS: public SELECT where `published`;
owner full CRUD.

`src/lib/profile-map.ts` maps onboarding draft ↔ profile row. Extend it, don't bypass it.

**Everything else is seeded mock data, by decision.** Auth, onboarding and portfolio persist;
booking, services, business, opportunities, reviews, bands, collab, marketplace, rentals,
notifications, plans, verification, QR are design-only.

**Schema changes go through a migration file** in `supabase/migrations/` — never apply DDL
directly, it desyncs schema from history. **Don't design new tables from scratch**: a live Firebase
backend already models this domain (16 collections, 34 doc types). Ask first.

**Payments undecided** — Stripe/Paddle unavailable (Indian seller), Razorpay unapproved. Media is
link-based; no upload or storage.

## 3. Design tokens

**Source of truth: `tokens/tokens.json` and the generated `ml-tokens.css`.** Prose files —
`README.md`, `governance.md`, `frontend/guide/README.md` — carry pre-v9 values and are stale. When
they disagree with the generated tokens, the tokens win.

**Type — fluid steps. Current ramps:**

```css
--type-1: clamp(12px, calc(12px + 1  * (100vw - 360px) / 1080), 13px);
--type-2: clamp(16px, calc(16px + 2  * (100vw - 360px) / 1080), 18px);
--type-3: clamp(22px, calc(22px + 6  * (100vw - 360px) / 1080), 28px);
--type-4: clamp(32px, calc(32px + 12 * (100vw - 360px) / 1080), 44px);
```

**The "exactly four steps per scale" cap is LIFTED.** Type, weight, radius, spacing, size, opacity
and elevation may carry as many steps as the design needs. What stays banned is the **arbitrary
value**: `text-[17px]`, `rounded-[24px]`, inline `style`. Need a new step? Add a token. Never a
one-off.

**Every step's web minimum equals its Android sp value — 12 / 16 / 22 / 32, and any new step
follows the same rule.** A web minimum below the sp value makes the preview render smaller text
than the app ever can. That bug existed before 2026-08-29 (web 10/14/20/28 vs native 12/16/22/32;
the caption measured 10.03px). **10/14/20/28 is retired — never reintroduce it.**

Usage: type-1 micro labels/captions/badges · type-2 body, default UI text, list titles · type-3
section/card/screen headings · type-4 display, hero, the one big number.

**Fonts — ONE family: Inter.** `--font-sans`, `--font-display` and `--font-eyebrow` all resolve to
Inter. Keep the three token names (a future change stays one line); they just point at the same
family. Loaded weights 400/500/600/700/800. **Inter Tight is retired** (was `--font-display`),
**Unica One is retired** (was `--font-eyebrow` — never actually loaded here), **`--font-mono` is
retired** (named JetBrains Mono, never loaded, silently fell back to generic monospace). Never
reintroduce any of the three. **No letter-spacing tuning** — headings use Inter's normal tracking,
the wordmark is set at 0, and `-0.02em` on `h1..h4` was removed with Inter Tight.

**Inter has no Indic, Arabic or CJK glyphs**, and language labels carry native script
(`Hindi (हिंदी)`, `Tamil (தமிழ்)`) — those already fall back to the OS. Declare an explicit
per-script fallback stack. `→ ★ ✓` are absent from Inter's subsets too: use Lucide, never a glyph.

**Colour (UI).** `ink #0D0D0D` · `paper #FCFCFD` · `white #FFFFFF` · `surface #F8F8FB` · `line
#D5D5DE` · `muted #66666F` · `pink #EA4C89` · `pinkPressed #C7295F` · `pinkContainer #FCE7F3` ·
`inkPressed #2B2B2B` · `success #22C55E` · `warning #F59E0B` · `error #DC2626` · `info #2563EB` ·
`disabled #9CA3AF` · `scrim rgba(13,13,13,.32)` · `purple #8B5CF6`

**Navy `#0D0B26` is retired**; use `ink`. **`#5B4BFF` is superseded and always a bug.** **Purple is
category-badge only** — never a button, link, border or surface.

**Brand/expression palette — MARKETING ONLY, never on a product screen:** `#FF005C` warm pink ·
`#141621` brand black · `#4F5059` · `#C4C5C8`. In-product accent stays `#EA4C89`. Pink FILLS use
`#EA4C89`; pink TEXT uses `#C7295F` (`#EA4C89` is 3.48:1 on paper, fails AA as text).

**Radius** 8/16/32/pill · **Space** 4/8/16/32 · **Opacity** .08/.16/.32/.64 · **Size** 32/40/48/56
· **Icon** 16/20/24/32 · **Border** 1/2. Extend these as needed; don't go off-token.

**Elevation** `1: 0 1px 3px rgba(13,11,38,.08)` · `2: 0 4px 12px rgba(13,11,38,.05)` ·
`3: 0 8px 24px rgba(13,11,38,.08)` · `4: 0 12px 32px rgba(13,11,38,.12)`. Cards lift; the hairline
is a fallback on white, not the default.

**Motion** `--duration-1..4` = 125 / 200 / 320 / 500ms. `--ease-standard cubic-bezier(.4,0,.2,1)` ·
`--ease-enter cubic-bezier(.05,.7,.1,1)` · `--ease-spring cubic-bezier(0,.35,0,1.25)`. Exit runs at
60–70% of enter. Honour `prefers-reduced-motion`.

**Icons: never emoji or text glyphs** — not `⌕ 🎙 ← ✓ 📍 ♡ ★ →` in JSX, not as data fields; they
render as empty boxes. Two sets: Gestalt (filled) and Lucide (stroked); never mix in one control.
`lucide-react` is installed.

**Photography: never stock or Unsplash.** Local library only. No initials, cartoon avatars or
AI images for people.

**Accessibility.** `:focus-visible` ring, 2px pink at 2px offset, every interactive control. 48dp
touch floor — a control that should look smaller (a 32px chip) keeps an invisible 48dp hit area
rather than growing. Contrast 4.5:1 body, 3:1 large. Label icon-only buttons. Colour alone is never
a state signal.

## 4. Code rules

**Components.** Check `ml/ui.tsx`, `ml/kit.tsx`, `components/ui/` first. Consolidate, don't extend:
`AvailabilityBadge`/`StatusPill`, `Chip`/`Segmented`, `Sheet`/`Dialog`. Use the Radix primitives —
hand-rolled modals here have no focus trap, Escape or scroll lock. **Never ship a control that
does nothing** — disable it with a reason. **Never key styling off
display copy** — use a union, keep the label separate. One styling mechanism: Tailwind + semantic
tokens, never inline `style`.

**TypeScript.** `exactOptionalPropertyTypes` is on — declare optional props as
`prop?: T | undefined` **at the component definition**, never at the call site. No `as any` on
router links. Nav active state uses longest-prefix matching, `/` special-cased.

**Content.** Keep UI copy, seed/demo content and runtime data in separate modules. Runtime data
must be shaped so a real query can replace it without editing the screen. **Never store relative
time as a string** (`"3 weeks ago"`) — store a timestamp, format at render. Never fabricate
availability, counts, ratings or transaction state, and never imply a payment succeeded or a
booking is confirmed unless the system supports it.

**Working rules.** Verify before claiming done — typecheck, build, 412px render pass; if you didn't
run them, say so. Commit messages must describe the diff. **If a link or attachment is unreadable,
say so** — never a silent no-op turn. **Surface conflicts, don't resolve them silently** (sources
disagree on flow numbering). Every screen ships loading, empty, error and offline states. Update
`roadmap.md` as work lands. Never force-push or rewrite published history.
