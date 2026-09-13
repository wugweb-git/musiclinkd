---
doc: lovable-knowledge-2026-09-13
version: 1.1
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@d66392f
folder: reference
originally: lovable-knowledge-draft.md
source: Claude Code session scratchpad
---

> **CURRENT** · imported 13 Sep 2026. The project knowledge text applied to Lovable on 13 Sep. Lovable's agent reads this on every prompt. NOTE: the photography rule was removed from this text; see state/CURRENT.md before treating that as settled.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.
> **v1.1** mirrors the text live in Lovable after the 13 Sep chip/eyebrow fix: chips are `full`, not `sm`;
> there is no `--font-eyebrow`; `src/styles.css` is the only token source. v1.0 is in git history.

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
keep state in `?step=`. No orphan routes — every new screen gets a link from an existing one.

**A route with children must render `<Outlet />` or be an index leaf.** `bands.new.tsx` is a child of
`bands.tsx`; if `bands.tsx` has no `<Outlet />`, `/bands/new` silently shows the bands list (26
routes shipped broken this way). A screen with sub-pages is `bands.index.tsx` +
`createFileRoute("/bands/")`. Before adding `x.y.tsx`, check whether `x.tsx` exists.

**Directories.** `src/components/ml/` the kit (`ui.tsx` primitives, `kit.tsx` shared patterns) ·
`src/components/ui/` 46 shadcn primitives, use them · `src/data/` content · `src/lib/` helpers and
server fns · `src/integrations/supabase/` · `src/hooks/use-auth.ts`.

**Data access.** Server-side `createServerFn` in `*.functions.ts` guarded by `requireSupabaseAuth`
(see `src/lib/portfolio.functions.ts`). Public reads via narrow anon SELECT policies. No
client-side writes.

**Live database — two tables.**

`profiles` — PK `id` → `auth.users(id)` CASCADE. `username` (unique), `display_name`, `headline`,
`bio`, `city`, `state`, `country`, `roles[]`, `instruments[]`, `genres[]`, `languages[]`,
`specialisations[]`, `work_modes[]`, `intents[]`, `contact_channels[]`, `experience_level`,
`avatar_url`, `availability`, `visibility`, `links` jsonb, `discovery_prefs` jsonb,
`onboarding_step`, `onboarding_draft` jsonb, `onboarding_complete`, timestamps. RLS: SELECT where
`visibility = 'public'` or own row; owner INSERT/UPDATE on `auth.uid() = id`.

`portfolio_items` — PK `id` uuid, `user_id` → `auth.users(id)` CASCADE. `title`, `kind`,
`description`, `role`, `collaborators[]`, `cover_url`, `media_url`, `release_year`, `published`,
`sort_order`, timestamps. Index `(user_id, sort_order)`. RLS: public SELECT where `published`;
owner full CRUD.

`src/lib/profile-map.ts` maps onboarding draft ↔ profile row. Extend it, don't bypass it.

**Everything else is seeded mock data, by decision.** Auth, onboarding and portfolio persist;
booking, services, business, opportunities, reviews, bands, collab, marketplace, rentals,
notifications, plans, verification, QR are design-only.

**Schema changes go through a migration file** in `supabase/migrations/` — never apply DDL
directly. **Don't design new tables from scratch**: a live Firebase backend already models this
domain (16 collections, 34 doc types). Ask first.

**Payments undecided** — Stripe/Paddle unavailable (Indian seller), Razorpay unapproved. Media is
link-based; no upload or storage.

## 3. Design tokens

**Source of truth: `src/styles.css`.**

**Type — five fluid steps in this app:**

```css
--type-1: clamp(12px, calc(12px + 1  * (100vw - 360px) / 1080), 13px);
--type-2: clamp(16px, calc(16px + 2  * (100vw - 360px) / 1080), 18px);
--type-3: clamp(22px, calc(22px + 6  * (100vw - 360px) / 1080), 28px);
--type-4: clamp(32px, calc(32px + 12 * (100vw - 360px) / 1080), 44px);
--type-5: clamp(40px, calc(40px + 16 * (100vw - 360px) / 1080), 56px);
```

Usage: type-1 captions/badges · type-2 body, list titles · type-3 section/card/screen headings ·
type-4 display, the one big number · type-5 hero only (pre-auth landing) — question a second use.

**The "exactly four steps per scale" cap is LIFTED.** What stays banned is the **arbitrary value**:
`text-[17px]`, `rounded-[24px]`, inline `style`. Need a new step? Add a token. Every step's web
minimum equals its Android sp value (12/16/22/32/40). **10/14/20/28 is retired — never reintroduce.**

**Fonts — ONE family: Inter**, weights 400/500/600/700 (nothing uses 800). `--font-sans` and
`--font-display` both resolve to Inter; there is no `--font-eyebrow` token. **Inter Tight, Unica One and
`--font-mono` are retired** — never reintroduce. **No letter-spacing tuning on headings** —
Inter's normal tracking everywhere (no `-0.02em`, no `tracking-tight`). Uppercase eyebrow labels may
use positive tracking (`tracking-[0.14em]`).

**Inter has no Indic, Arabic or CJK glyphs** — native-script labels (`Hindi (हिंदी)`) fall back to
the OS. `→ ★ ✓` are absent from Inter's subsets too: use Lucide, never a glyph.

**Colour (UI).** Page ground `#ECECF1` (`--background`) · `ink #0D0D0D` · `white #FFFFFF` (cards) ·
`surface #F8F8FB` · `line #D5D5DE` · `muted #66666F` · `pink #EA4C89` · `pinkPressed #C7295F` ·
`pinkContainer #FCE7F3` · `inkPressed #2B2B2B` · `success #22C55E` · `warning #F59E0B` ·
`error #DC2626` · `info #2563EB` · `disabled #9CA3AF` · `scrim rgba(13,13,13,.32)` ·
`purple #8B5CF6`.

**Navy is removed** — near-black is `ink` (`bg-ink`, `Btn variant="ink"`). **`#5B4BFF` is superseded and always a bug.** **Purple is
category-badge only.** Brand palette `#FF005C` · `#141621` · `#4F5059` · `#C4C5C8` is **marketing
only**. Pink FILLS `#EA4C89`; pink TEXT `#C7295F` (`#EA4C89` fails AA as text).

**Radius — Material 3 shape scale:** `sm` 8 small controls · `md` 12 small tiles, thumbnails,
inputs · `lg` 16 **cards** and list containers · `xl` 20 large tiles · `2xl` 28 sheets and dialogs ·
`3xl` 32 hero only · `full` buttons, chips, pills and badges. Pick by role. **Space:** Tailwind numeric only (`p-1` 4px …
`p-8` 32px) — no named spacing tokens. **Secondary text:** `text-muted-foreground`. **Opacity**
.08/.16/.32/.64 · **Size** 32/40/48/56 · **Icon** 16/20/24/32 · **Border** 1/2.

**Elevation** `1: 0 1px 3px rgba(13,11,38,.08)` · `2: 0 4px 12px rgba(13,11,38,.05)` ·
`3: 0 8px 24px rgba(13,11,38,.08)` · `4: 0 12px 32px rgba(13,11,38,.12)`.

**Motion** `--duration-1..4` = 125/200/320/500ms. `--ease-standard cubic-bezier(.4,0,.2,1)` ·
`--ease-enter cubic-bezier(.05,.7,.1,1)` · `--ease-spring cubic-bezier(0,.35,0,1.25)`. Exit at
60–70% of enter. Honour `prefers-reduced-motion`.

**Icons: never emoji or text glyphs** — not `⌕ 🎙 ← ✓ 📍 ♡ ★ →` in JSX or data. `lucide-react` is
installed.

**Photography:** Unsplash is fine; the team library (guide repo `frontend/website/assets/img/`) is
also available. No initials, cartoon avatars or AI images for people.

**Accessibility.** `:focus-visible` ring, 2px pink at 2px offset, every interactive control. 48dp
touch floor — a smaller-looking control keeps an invisible 48dp hit area. Contrast 4.5:1 body, 3:1
large. Label icon-only buttons. Colour alone is never a state signal. Safe-area insets work
(`viewport-fit=cover` is set) — use `env(safe-area-inset-*)` for fixed bars.

## 4. Code rules

**Components.** Check `ml/ui.tsx`, `ml/kit.tsx`, `components/ui/` first. Consolidate, don't extend:
`AvailabilityBadge`/`StatusPill`, `Chip`/`Segmented`, `Sheet`/`Dialog`. Use the Radix primitives —
hand-rolled modals here have no focus trap, Escape or scroll lock. **Never ship a control that does
nothing** — disable it with a reason. **Never key styling off display copy.** One styling
mechanism: Tailwind + semantic tokens, never inline `style`.

**TypeScript.** `exactOptionalPropertyTypes` is on — declare optional props as
`prop?: T | undefined` **at the component definition**. No `as any` on router links. Nav active
state uses longest-prefix matching, `/` special-cased.

**Content.** Keep UI copy, seed content and runtime data in separate modules. **Never store relative
time as a string** — store a timestamp. Never fabricate availability, counts, ratings or
transaction state, or imply a payment/booking succeeded unless the system supports it.

**Working rules.** CI (typecheck, lint, build) runs on every push to `main` — keep it green; run
`bunx tsc --noEmit` and `bunx eslint .` before finishing. Verify at 412px; if you didn't run checks,
say so. Commit messages must describe the diff. **If a link or attachment is unreadable, say so.**
**Surface conflicts, don't resolve them silently.** Every screen ships loading, empty, error and
offline states. Update `roadmap.md` as work lands. Never force-push or rewrite published history.
