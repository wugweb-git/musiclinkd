---
doc: PHASE-SPECS-2b-to-9
version: 1.1
status: PARTLY STALE
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: plans
originally: PHASE-SPECS-2b-to-7.md
---

> **PARTLY STALE** · v1.1 · 2026-09-13. Branch-by-branch specs for phases 2b, 3, 4, 5, 6, 7, 9. Phase 7 is DONE. Phase 8 is referenced as a dependency five times and specified nowhere.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# MusicLinkd — Phase specs 2b → 9

Companion to `PHASE-2-TOKEN-SPEC.md` (Phase 2) and `CLAUDE-CODE-WORK-ORDER.md`.
Each phase is one branch, one PR, exit gate on each. Do not batch.

**Phase 9 (first-run: splash, gate, onboarding order) is specified at the end of this file.
It is gated on Phase 8 — do not start it while route bodies are still being swept.**

---

# PHASE 2b — Motion and scroll

**Branch:** `feat/motion-choreography` · **Risk:** medium (visual, everywhere) · **Est:** 1 day
**Depends on:** Phase 2 (needs `--duration-*` and `--ease-*`)

## The problem, stated

The app has **feedback** motion and no **choreography**. The only movement in the entire codebase
is `active:scale-[0.98]` on `Btn`. Everything else is a hard cut: routes swap instantly, sheets
appear fully-formed, lists paint in one frame, sticky headers snap.

The design system names this as its own known gap — *"0 entry animations, 0 view transitions, 0
scroll-driven motion, hard-cut state switching across 518 states."* So this is not a new opinion;
it is a documented debt.

**Ground rule:** motion here is orientation, not decoration. Every animation below answers "where
did this come from / where did it go". Nothing pulses, bounces or draws attention to itself.

## 2b.1 Sheets and dialogs — the worst offender

`Sheet` and `Dialog` both do `if (!open) return null`. There is no enter, no exit, no backdrop
fade. A bottom sheet that teleports reads as a bug.

**Note the ordering conflict:** Phase 6 replaces both with Radix primitives, which bring their own
`data-[state=open]` animation hooks. **Do the Radix swap first if you can** — otherwise this work
gets written twice. If Phase 6 slips, implement here and port the values across.

Target, once on Radix:

```css
/* backdrop */
[data-state="open"]   → opacity 0 → 1,  --duration-2, --ease-enter
[data-state="closed"] → opacity 1 → 0,  --duration-1   /* ~60% of enter */

/* sheet panel — slide from bottom */
[data-state="open"]   → translateY(100%) → 0, --duration-3, --ease-enter
[data-state="closed"] → translateY(0) → 100%, --duration-2, --ease-standard

/* dialog — fade + scale, no slide */
[data-state="open"]   → opacity 0→1, scale .96→1, --duration-3, --ease-enter
[data-state="closed"] → opacity 1→0, scale 1→.98, --duration-2
```

Exit at **60–70% of enter** — that ratio is in `motion.md` and it is what makes dismissal feel
responsive rather than sluggish.

## 2b.2 Route transitions

TanStack Router has no transition today; screens hard-cut. Add a shared shell transition in
`__root.tsx` — a short cross-fade with a few pixels of vertical travel. Keep it under
`--duration-2`; anything longer and navigation feels laggy on a phone.

Do **not** animate the bottom nav between routes. It is a fixed frame; moving it breaks the
illusion that it is the app chrome.

## 2b.3 List and card entry

Discover rails, search results, collab lists, marketplace grids all paint in one frame.

Stagger the first screenful only:

```
per-item delay: 20–30ms, capped at ~8 items
transform:      translateY(8px) → 0
opacity:        0 → 1
duration:       --duration-2
easing:         --ease-enter
```

**Only on first mount.** Not on filter changes, not on re-render, never on scroll-back. A list
that re-animates every time you return to it is the single most irritating pattern in mobile UI.

## 2b.4 Scroll behaviour

Currently: no scroll-linked motion at all, and horizontal rails are free-scrolling.

**Sticky header.** `TopBar` is `sticky top-0` with a permanent border. Bind the border and a
subtle `--elevation-1` to scroll position — flat at rest, hairline plus shadow once content moves
under it. That is the only scroll-driven effect worth having; it tells you the page has moved.

**Horizontal rails.** The carousels (`Featured talent`, `Trending`) scroll freely and stop
anywhere, which is why cards get clipped mid-item.

```css
scroll-snap-type: x mandatory;   /* on the rail */
scroll-snap-align: start;        /* on each card */
scroll-padding-left: var(--space-3);
```

This also fixes the clipped-card-at-the-edge problem visible in the live app.

**Smooth scrolling** for in-page anchors only — `scroll-behavior: smooth` on the container, not
globally, and gated behind `prefers-reduced-motion`.

**Do not** add parallax, scroll-triggered reveals below the fold, or scroll-jacking. On a 430px
shell they cost more than they give.

## 2b.5 State transitions

`AvailabilityBadge` / `StatusPill` colour changes, `Meter` fill, `Chip` and `Segmented` active
state, skeleton → content — all hard cuts today.

- Colour and background: `transition: colors --duration-2 --ease-standard`
- `Meter` fill width: `--duration-3 --ease-standard`
- Skeleton → content: cross-fade at `--duration-2`, never a pop
- Skeleton shimmer: **1.4s** loop (`motion.md` — currently 1.5s in Tailwind's default pulse)

## 2b.6 Reduced motion

The block in the Phase 2 token spec kills durations globally under
`prefers-reduced-motion: reduce`. Verify it actually takes: staggered lists and scroll-snap must
still *work*, just without the animation. Snap is not motion — keep it.

## Acceptance

Record a screen capture at 412px of: open a sheet, dismiss it, navigate between two tabs, scroll a
horizontal rail to its end, pull a list into view. Compare against the same five before the branch.
**Green build tells you nothing here.**

Then: enable "reduce motion" in the OS and repeat. Nothing should animate; everything should still
function.

---

# PHASE 3 — Optional-prop convention

**Branch:** `fix/optional-prop-types` · **Risk:** low · **Est:** 30 min

Full detail in the work order §Phase 3. Summary: `exactOptionalPropertyTypes: true` rejects an
explicitly-passed `undefined`. Declare optional props on shared primitives as
`prop?: T | undefined` **at the component definition**.

**Acceptance is unusual and non-negotiable: zero `TS2375` errors, with no call site modified.**
If a call site still errors, the primitive is still wrong. Four previous attempts patched callers
and the error count went 3 → 10.

Also fix `Btn`'s `Record<string, string>` variant map, which discards the union and makes
`styles[variant]` possibly-undefined at type level.

---

# PHASE 4 — Icons

**Branch:** `fix/icons-no-emoji` · **Risk:** low · **Est:** 1 hour

Six text glyphs render as empty boxes on real devices. `lucide-react` is installed and already
used for `BottomNav`, `Row`, `Stars`.

| Glyph | File | Replace |
|---|---|---|
| `⌕` | `ml/ui.tsx` `SearchBar` | `<Search />` |
| `🎙` | `ml/ui.tsx` `SearchBar` | `<Mic />` |
| `←` | `ml/ui.tsx` `TopBar` | `<ArrowLeft />` |
| `✓` | `ml/ui.tsx` `VerifiedBadge` | `<BadgeCheck />` |
| `📍` | `ml/cards.tsx` `TalentRow` | `<MapPin />` |
| `♡` | `ml/cards.tsx` `TalentRow` | `<Heart />` |

Then sweep `src/data/*` for emoji-as-data — **`src/data/portfolio.ts` exposes `workType().emoji`,
consumed by `src/routes/discover.music.tsx`.** Replace with an icon key.

Keep `aria-label="Back"` on `TopBar` when swapping the glyph. `SearchBar`'s `<input>` has no
label — add one while you are in the file.

**Acceptance:** no emoji in `src/` outside comments.

---

# PHASE 5 — Navigation and dead controls

**Branch:** `fix/nav-and-dead-controls` · **Risk:** low · **Est:** 1 hour

**5.1** `const active = path === item.to` in `BottomNav` — exact match, broken on ~200 of 207
routes. Longest-prefix match, `/` special-cased (it is Discover and would otherwise match
everything).

**5.2** Dead controls — `recently-viewed.tsx` "Clear history", `cards.tsx` `TalentRow` "Message".
Sweep for more. Where the action is not built: `disabled` plus a reason.

**5.3** `AvailabilityBadge` keys styling off display copy (`map[status] ?? map["Booked"]`). Key
off a union; label separately.

**5.4** Touch targets — `TalentRow`'s link (`min-h-11`) and save button (`h-11 w-11`) are 44px
against a 48dp floor. **Do not enlarge chips**: v9 says a 32px chip is correct and keeps an
invisible 48dp hit area instead.

**5.5** `/talent/$id` — new 10 Sep screens link a route scheduled for redirect since 2 Sep. Land
the redirect or stop wiring to it. Flag which.

---

# PHASE 6 — De-duplication

**Branch:** `refactor/dedupe-components` · **Risk:** highest · **Est:** 1 day

You asked for this three times: 12, 14 and 17 August.

| Duplicate | Action |
|---|---|
| `AvailabilityBadge` / `StatusPill` | Keep `StatusPill` — `kit.tsx` already claims "One status system across every flow" above it |
| `Chip` / `Segmented` | Byte-identical class strings; keep one |
| `Sheet` / `Dialog` | **Replace with Radix** `ui/sheet.tsx`, `ui/dialog.tsx` |
| `Loading` | Replace with `ui/skeleton.tsx` |

Hand-rolled modals have no focus trap, no Escape, no scroll lock, no `aria-labelledby`. 45 Radix
primitives ship unused.

**6.1** Type `Row` properly and delete the `as any` + eslint-disable. **This will surface real
broken links across 207 routes.** That is the point — they exist now and are hidden. Enumerate
them in the PR; do not cast them away.

**6.2** Move inline `style={{ background: "var(--…)" }}` (in `BottomNav`, `AvailabilityBadge`,
`StatusPill`, `Meter`, `TalentRow`) to Tailwind classes on the Phase 2 tokens.

**Do this phase when you can watch it.** Swapping four modals changes focus, Escape and scroll
behaviour on every sheet in the app.

---

# PHASE 7 — Inventory (no commit)

**Est:** 2 hours · **Output:** a table, not a branch

**207 route bodies and 21 of 22 `src/data` modules have never been opened by anyone.** Count per
file:

| Signal | Pattern |
|---|---|
| Arbitrary radius | `rounded-[` |
| Arbitrary type | `text-[` |
| Stock imagery | `unsplash`, `images.` |
| Emoji | non-ASCII glyph ranges |
| Dead controls | `<Btn` / `<Link` with no `onClick`/`to` |
| Deprecated route | `/talent/$id` |
| Inline design values | `style={{` |
| Relative-time strings | `"… ago"`, `"in … days"` |
| Fabricated state | hardcoded ratings, counts, availability |

**Output columns:** file · route · counts per signal · est. effort · notes.

**This number decides whether Phase 8 is one PR or three weeks.** The last plan written without it
collapsed at Wave 2. Do not skip it, and do not let it become a commit — it is a document.

---

## Sequencing note

Phase 2 → **2b** → 3 → 4 → 5 → 6 → 7 is the written order, but **6 before 2b is better if the
schedule allows**: Radix brings its own animation hooks, so doing motion first means writing sheet
choreography twice.

Phases 3, 4, 5 are independent of everything and can run in any order, or in parallel by a second
pair of hands. Phase 7 can run at any time — it touches nothing.

---

# PHASE 9 — First run: splash, entry gate, onboarding order

**Branch:** `feat/first-run-flow` · **Risk:** high (changes what every new user sees first)
**Est:** 2–3 days · **Depends on:** Phase 8 (route bodies swept) — see gate below

> Your instruction, 10 Sep: *"once all the screens are made, flows are done, and routes/
> function/ are done ..we will need to change the other of the screen and work on spalsh
> screen, use onboaeidng screen etc flow"*

## 9.0 The gate — why this is last, not first

This phase decides the **order** screens appear in. Reordering is only cheap once the
screens themselves are stable. If Phase 8 is still rewriting route bodies, every reorder
decision gets relitigated against a moving target. Do not open this branch until Phase 8
is merged and Phase 7's inventory table shows zero outstanding route work.

The reverse is also true: **do not let Phase 8 invent a splash screen in passing.** If a
route sweep produces one, it belongs here.

## 9.1 What exists today (verified against HEAD `384af26f`, 10 Sep)

**There is no splash route.** No `splash.tsx`, no `welcome.tsx`, no `intro.tsx`, nothing
equivalent under any name.

**`/` is `src/routes/index.tsx`, and it is Discover.** It renders the Discover screen
directly: header, search bar, category rail, Featured talent, Trending, Recommended,
Nearby. It performs **no auth check and no onboarding check**. A person who has never
seen the app lands mid-product, on seeded mock content, with a bell icon and someone
else's avatar in the corner presented as theirs (`talents[1]!.photo`, `alt="Your profile"`).

**Auth exists — 10 routes:**

```
auth.index · auth.phone · auth.otp · auth.forgot · auth.verify-email
auth.success · auth.signout · auth.locked · auth.maintenance · auth.offline
```

**Onboarding exists — 7 routes:**

```
onboarding/index · onboarding/step.$id · onboarding/resume · onboarding/preview
onboarding/complete · onboarding/done · onboarding/skip
```

**The state to drive a gate already persists.** `profiles` carries `onboarding_step`,
`onboarding_draft` (jsonb) and `onboarding_complete`, and `src/lib/profile-map.ts` already
maps draft ↔ profile record. Nothing new is needed in the database for this phase.

**So the pieces are all built. What is missing is the decision that routes between them.**
That is the actual content of this phase — not new screens.

## 9.2 The entry decision

```
no session                                  → /auth
session, onboarding_complete = false        → /onboarding/resume
session, onboarding_complete = true         → Discover
```

`/onboarding/resume` already exists and already reads `onboarding_step`, so the
resume-mid-flow case needs routing, not building.

### 9.2.1 PREREQUISITE — the session does not reach the server

**Corrected 10 Sep. An earlier draft of this section said the resolver could run
server-side in `beforeLoad` because `auth-middleware.ts` "already runs there." That was
wrong, and the phase is not implementable until this is fixed.**

Verified against HEAD:

- `package.json` has `@supabase/supabase-js` only. **No `@supabase/ssr`**, and
  `createServerClient` appears nowhere in `src/`.
- `src/integrations/supabase/client.ts` sets `storage: brokeredPreviewStorage()`, which
  resolves to `localStorage` (`previewAuthStorage.ts`).
- `src/hooks/use-auth.ts` is a client-only `useEffect` calling `supabase.auth.getSession()`.
- `requireSupabaseAuth` is `createMiddleware({ type: "function" })` — **function**
  middleware, not request middleware. It reads an `Authorization: Bearer` header that the
  client attaches to `createServerFn` RPC calls. It never runs on a document request.

So the token lives in `localStorage` and travels only as an explicit header on server-function
calls. On the initial SSR document render there is no cookie and no header — `beforeLoad` has
no way to know who the user is.

**Phase 9 is gated on a cookie-based auth migration (`@supabase/ssr`, `createServerClient`)
that is not currently in any phase.** Schedule it before this one, or accept a client-side
gate and its flash-of-wrong-screen.

### 9.2.2 The gate belongs in `__root.tsx`, not `index.tsx`

A gate on `index.tsx` guards `/` only. A first-time visitor deep-linking to `/feed` or any
of the other 203 routes walks straight past it. There is no `auth.tsx` or `onboarding.tsx`
layout route — routing is flat — so the gate goes in `__root.tsx`, which has no `beforeLoad`
today (the only one in the tree is the redirect in `talent.$id.tsx`).

**Exempt `/auth/*` and `/onboarding/*` from the redirect**, or the
`onboarding_complete = false` branch redirects into itself forever.

### 9.2.3 Open question for Vedanshu

Whether a logged-out visitor gets `/auth` or a public Discover. Public is the better
acquisition surface — it is the whole argument for SSR and per-route `head()` — but it means
classifying all 207 routes as public or gated. Product decision with an SEO tail.
**Decide before the branch opens.**

## 9.3 Splash

**Vedanshu designs the splash. This section records only the mechanical constraints it has
to satisfy — not what it looks like.**

Facts that bound the design:

- **SSR means first paint is already real content.** On the web there is no loading gap to
  cover unless the entry decision in §9.2 needs a session round-trip first. Whether that
  gap exists is measurable — measure it before designing around it.
- **If MusicLinkd ships wrapped** (PWA / TWA / Capacitor / Android shell), the platform draws
  its splash from the manifest/theme **before any JS runs**. It is never a route. A React
  `/splash` route would be a *second* screen after the native one. So the Android argument
  does not justify a splash route either — **a splash route is wrong on web and on Android.**
  If a splash exists at all it is manifest configuration, not a page.
- **Any hold must be dismissible the instant the decision resolves** — driven by the
  resolver, never a fixed timer.
- `prefers-reduced-motion` must be honoured.
- `Brand` already exists in `src/components/ml/auth.tsx`. Reuse or replace it deliberately;
  do not end up with two marks.

**Open for Vedanshu: whether a splash exists at all, and what it is.**

## 9.4 Onboarding order

**Vedanshu decides the order. Claude Code's job here is to write down what the order
currently IS, and to flag the mechanical faults below — not to propose a better sequence.**

**First, before anything changes:** read `src/data/onboarding.ts` and
`src/components/ml/onboarding.tsx` together and write the actual step sequence into
`roadmap.md`. It has never been written down, which is why nobody can say whether it is
right. That document is the input to Vedanshu's decision.

Mechanical faults found so far, independent of any ordering opinion:

**`complete.tsx` and `done.tsx` are chained, not duplicated.** *(Corrected 10 Sep — an
earlier draft called them rivals and suggested one should go. Verified wrong.)* `complete`
does the real work (`saveMyProfile`, `draftToProfile`, readiness and handoff cards) and links
forward to `done`; `done` is the lighter welcome screen and carries `back="/onboarding/complete"`,
registered as contract `ONB-22` in `src/data/onboarding.ts`. **Deleting either breaks the flow.**
The names are genuinely confusing — that is a rename, if Vedanshu wants one, not a dedupe.

**`skip.tsx` re-entry is undefined.** Verify whether `skip` leaves `onboarding_complete =
false` and whether any surface offers a way back in. Report the answer; do not invent the
re-entry surface.

**`preview.tsx` is a peer route, not a step.** State that as a fact for the ordering
decision. Whether it becomes a step is Vedanshu's call.

## 9.5 What this phase must NOT do

**No screen or flow design.** Standing rule: Vedanshu designs screens and flows. This phase
implements a routing decision and reports faults. It does not propose layouts, step
sequences, splash treatments or "better" journeys.

**No copy changes.** Standing rule: wording does not change unless Vedanshu says so. This
phase reorders screens and adds a gate. If a step's text is wrong, record it, do not rewrite it.

**No new onboarding steps.** Seven routes is already enough surface. Adding an eighth
because it seems useful is how this flow got un-reviewable in the first place.

**No fabricated first-run state.** A gated Discover must show the real signed-in user or a
neutral fallback — never a seed record dressed as the viewer. *(The current instance of this
is not a Phase 9 item — see below.)*

### Pulled out of this phase: the avatar bug ships wrong today

`src/routes/index.tsx` renders `src={talents[1]!.photo}` under `alt="Your profile"`, pulling
from the seeded `talents` array in `src/data/discover.ts`. **Every user of the live app is
being shown a stranger's face as their own, right now.** It has no dependency on the entry
gate, on Phase 8 ordering, or on the auth migration. Fix it as a standalone, ahead of
everything else in this phase.

Minimum correct behaviour: stop rendering a seeded person under "Your profile". What replaces
it — real avatar, initials, neutral mark, link target — is Vedanshu's call.

## 9.6 Acceptance

- Cold, never-signed-in, cleared storage → lands where §9.2 says, with no flash of Discover.
- Signed in, `onboarding_complete = false`, mid-flow → resumes at the recorded step.
- Signed in, complete → Discover, first paint, no redirect chain.
- Skip, then return → recoverable, and the profile still reads as incomplete.
- Back button from every first-run screen does something sane. Test this specifically:
  redirect chains and browser back are where entry gates break.
- Screen capture at 412px of the full cold path, before and after.
- Reduced motion on: splash, if built, does not animate.

**Green build tells you nothing here.** This phase is judged on the sequence a person
actually sees.

## 9.7 Carried debt this phase will collide with

`src/routes/index.tsx` has not been touched by Phase 2 — it still carries `text-[30px]`,
`text-[15px]`, `text-[14px]`, `text-[13px]`, `text-[11px]`, `text-[10px]`, `rounded-3xl`,
`rounded-2xl`, inline `style={{ background: ... }}`, and the emoji `🔔` `✨` `→` `★`.

**Correction, 10 Sep — navy is NOT a live defect.** An earlier draft of this section said the
home screen "paints with a colour the design system removed." Verified wrong. `src/styles.css`
still defines `--navy: oklch(0.1713 0.0539 282.44)` in `:root` and still maps `--color-navy:
var(--navy)` through `@theme inline`, so `bg-navy` and `from-navy/85` render exactly as
intended. The file's own header comment records the reason: navy is retired **in
`tokens.json`**, and migrating it *"is a colour-identity change across every screen and is not
done here."* It is a deliberate, documented back-compat shim across 17 route files. **Tracked
debt, not a bug.** There is no `tailwind.config` — this is Tailwind v4 css-first, so
`styles.css` is the whole mapping surface.

While reading it: the header comment's line **"hairline #ECECF1" is now stale.** `#ECECF1` is
`--background`; the hairline is `--border: #D5D5DE`. Worth correcting so the next reader is not
misled by the file that is supposed to be authoritative.

Phase 2 migrated `src/components/ml/` only. All 207 route bodies still carry the arbitrary
sizes and radii. That is Phase 8's job, which is exactly why Phase 9 waits for it.
