# MusicLinkd

## 1. Purpose and users

MusicLinkd is India's professional network and operating system for the music industry. It is not
a social feed — it is task-oriented infrastructure for finding work, hiring people, booking rooms,
renting gear and running collaborations.

**This app is THE MusicLinkd frontend.** It replaces a Kotlin/Compose Android app that is being
retired. Everything built here ships to real users; nothing is throwaway.

**Two sides of the market.**

*Talent and creators* — singers, instrumentalists, session artists, producers, composers,
lyricists, DJs, RJs, podcasters, sound engineers, bands. They come to be discovered, show credited
work, get booked, and find collaborators and opportunities.

*Businesses and hirers* — labels, recording/mixing/mastering studios, rehearsal and jam rooms,
venues, event organisers, music schools, equipment dealers, rental providers, agencies. They come
to find and verify talent, publish opportunities, take bookings and list gear.

**Jobs the product does:** discover → evaluate (portfolio, credits, verification, reviews,
availability, location) → connect → transact (hire, book, buy, rent) → collaborate → review.

**India-first, not localised-later.** ₹ pricing, Indian cities and states, Indian languages, and
Indian instruments as first-class citizens — tabla, sitar, harmonium, sarod, santoor, bansuri,
veena, tanpura, dholak — alongside Western ones. Never a Western-only music store. Copy is English
for now, but the information architecture must support localisation.

**Form factor.** Mobile-first, 430px shell, verified at 412px Android. Bottom navigation is
**exactly five** destinations — Home, Discover, Connections, Market, Profile. Never add a sixth;
new sections are reached from Profile, Discover or the dashboard.

## 2. Architecture, schema and APIs

**Stack.** TanStack Start (SSR) · React 19 · Tailwind CSS v4 · Radix/shadcn · Supabase JS · bun.

**Routing.** File-based, flat dot-named files under `src/routes/` (`booking.$id.pay.tsx` →
`/booking/:id/pay`). `__root.tsx` is the only shell. `routeTree.gen.ts` is generated — never edit
it. Dynamic segments use bare `$`. No `src/pages/`, no `app/layout.tsx` — those are Next.js and
Remix conventions and do not apply. Every route defines its own `head()` metadata. Wizards keep
state in a `?step=` search param so URLs are shareable. Every route must be reachable from a real
entry point — no orphans.

**Directory contract.**
`src/components/ml/` — the MusicLinkd component kit (`ui.tsx` shell/primitives, `kit.tsx` shared
patterns, plus per-feature modules).
`src/components/ui/` — 45 installed Radix/shadcn primitives. Use them.
`src/data/` — typed content modules.
`src/lib/` — helpers and server functions.
`src/integrations/supabase/` — `client.ts` (browser), `client.server.ts`, `auth-attacher.ts`,
`auth-middleware.ts`, `types.ts` (generated DB types).
`src/hooks/use-auth.ts` — auth state.

**Data access.** Server-side via `createServerFn` handlers in `*.functions.ts` guarded by
`requireSupabaseAuth` (see `src/lib/portfolio.functions.ts`). Public reads go through narrow anon
SELECT policies. Do not add client-side Supabase writes.

**Live database — two tables only.**

`profiles` — PK `id` → `auth.users(id)` ON DELETE CASCADE. Columns: `username` (unique),
`display_name`, `headline`, `bio`, `city`, `state`, `country`, `roles[]`, `instruments[]`,
`genres[]`, `languages[]`, `specialisations[]`, `work_modes[]`, `intents[]`, `contact_channels[]`,
`experience_level`, `avatar_url`, `availability`, `visibility` (default `public`), `links` jsonb,
`discovery_prefs` jsonb, `onboarding_step`, `onboarding_draft` jsonb, `onboarding_complete`,
`created_at`, `updated_at`. RLS: public SELECT; owner INSERT/UPDATE on `auth.uid() = id`.

`portfolio_items` — PK `id` uuid, `user_id` → `auth.users(id)` CASCADE. Columns: `title`, `kind`
(default `track`), `description`, `role`, `collaborators[]`, `cover_url`, `media_url`,
`release_year`, `published`, `sort_order`, `created_at`, `updated_at`. Index `(user_id,
sort_order)`. RLS: public SELECT where `published = true`; owner full CRUD.

Triggers: `update_updated_at_column()` on both; `handle_new_user()` seeds a profile row on signup.

`src/lib/profile-map.ts` maps the onboarding draft to and from the profile record
(`draftToProfile`, `profileToDraft`, `profileGaps`). Extend that, don't bypass it.

**Everything else is seeded mock data, by decision.** Auth, onboarding and portfolio persist. All
other flows — booking, services, business, opportunities, reviews, bands, collab, marketplace,
rentals, notifications, plans, verification, QR — are design-only for now.

**Schema changes go through a migration file** in `supabase/migrations/`. Never apply DDL directly
to the live database; it desyncs the schema from migration history. **Do not design new tables
from scratch** — a live Firebase backend already models this domain (16 collections, 34 document
types, working admin panel). Ask before adding tables so the model does not fork.

**Payments are undecided.** Stripe and Paddle are unavailable (seller registered in India);
Razorpay is likely but unapproved. Media is link-based — there is no upload or storage
infrastructure; the uploader is a designed state machine over local previews.

## 3. Project rules

### Design tokens — the Gestalt v9 system

`README.md` in this repo is stale. Ignore its palette and type values; these are canonical.

**Colour.** Brand pink `#EA4C89` · navy `#0D0B26` · paper `#FCFCFD` · line `#ECECF1` · muted
`#7D7D8E` · success `#22C55E` · warning `#F59E0B` · error `#DC2626`. **`#5B4BFF` is wrong** — it
comes from a superseded brief; if you see it, it is a bug. **Purple `#8B5CF6` is permitted in
exactly one place**: the category badge. It never paints a button, link, border or surface. Pink =
brand, navy = primary, purple = badges only.

**Radius: 8 / 16 / 32 / pill.** Those four and nothing else. `24px` and `20px` are not on the
scale — never `rounded-[24px]`. Cards use 32.

**Type: four fluid `clamp()` tokens** — 10–12px, 14–18px, 20–28px, 28–44px. Never arbitrary sizes
like `text-[13px]`. Inter Tight for display, Inter for body, JetBrains Mono for metadata.

**Spacing 4/8/16/32 · opacity 0.08/0.16/0.32/0.64 · motion 125/200/320/500ms**, three easings,
pressed `scale(0.97)`, shimmer 1.4s, exit at 60–70% of enter, honour `prefers-reduced-motion`.

**Elevation.** Cards lift with elevation plus radius. The hairline border is a fallback for cards
on white, not the default.

**Icons: never emoji.** Not `⌕ 🎙 ← ✓ 📍 ♡ ★` — not in JSX, not as data fields. They render as
empty boxes on real devices. Two sets exist: Gestalt (filled, product) and Lucide (stroked,
breadth); never mix them inside one control. `lucide-react` is installed.

**Photography: never stock, never Unsplash.** Stock imagery was deliberately removed from the
design system. No initials, cartoon avatars, or AI-looking images for people-facing content.

**Accessibility.** `:focus-visible` ring, 2px pink at 2px offset, on every interactive control.
48dp touch floor — a control that should look smaller (a 32px chip) keeps an invisible 48dp hit
area rather than growing. Contrast 4.5:1 body, 3:1 large. Accessible label on every icon-only
button. Colour is never the only signal of state.

### Components

**Do not create a component that already exists.** Check `ml/ui.tsx`, `ml/kit.tsx` and
`components/ui/` first. Known duplicate pairs to consolidate, not extend:
`AvailabilityBadge`/`StatusPill`, `Chip`/`Segmented`, `Sheet`/`Dialog`.

**Use the installed Radix primitives** — `dialog`, `sheet`, `drawer`, `alert-dialog`, `skeleton`.
Hand-rolled modals miss focus trap, Escape and scroll lock. Don't write another one, or a custom
loader.

**Never ship a control that does nothing.** A button with no handler is a bug; disable it and say
why. **Never key styling off display copy** — use a union type, keep the label separate. **One
styling mechanism**: Tailwind + semantic tokens, not inline `style` objects.

### TypeScript

**`exactOptionalPropertyTypes` is on.** Declare optional props as `prop?: T | undefined` at the
component definition. Patching call sites makes the same build failure return with the next batch
of routes. **No `as any` on router links** — if `to` won't type-check, the route is wrong.
**Navigation active state uses prefix matching**, longest-wins, `/` special-cased; exact equality
leaves most routes unhighlighted.

### Content

Keep three kinds separate, never in one module: **UI copy** · **seed/demo content** · **runtime
data**. Runtime data must be shaped so a real query can replace it without editing the screen.
**Never store relative time as a string** (`"3 weeks ago"`) — store a timestamp, format at render.
Never fabricate availability, counts, ratings or transaction state. **Never imply a payment
succeeded, a booking is confirmed, or a deposit was refunded** unless the system supports it.

### Working rules

Verify before claiming done — typecheck, build, 412px render pass; if you didn't run them, say so.
Commit messages must describe the commit. **If a link, connector or attachment is unreadable, say
so in the reply** — never produce a silent no-op turn. **Surface conflicts instead of resolving
them silently**: sources disagree on flow numbering (FLOW 03 is "Market" in one document and "Add
Portfolio" in another). Every screen ships loading, empty, error and offline states. Update
`roadmap.md` as work lands. Never force-push or rewrite published history.
