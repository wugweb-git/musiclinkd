---
doc: FINAL-PLAN-2026-09-14
version: 1.0
status: CURRENT
updated: 2026-09-14
verified_against: musiclinkd-9e6ca875@1937767 · Firestore musiclinkd-7ae30 live read 14 Sep
folder: plans
originally: new
---

> **CURRENT** · 14 Sep 2026. The consolidated plan. Supersedes the ordering in
> `PLAN-registration-flow-2026-09-14.md` §5 and `TO-CLAUDE-CODE-2026-09-13.md`; those stay for their
> detail. Every number below was measured at HEAD `1937767`, not carried forward.
> Ground truth lives in `state/CURRENT.md`.

# MusicLinkd — final plan

## Measurements at HEAD, 14 Sep

```
"svc-1" hard-coded into /booking/$id        3      ← live break
"Preview failure state" in production       2
text-sm  (14px, no token exists)          300
text-primary used as a text colour         56      ← fails AA on both grounds
text-[Npx] arbitrary sizes              1,006      → routes 1,002 · ml 0 · ui 4
inline style={{ }}                        111      → routes 56 · ml 53 · ui 2
Firestore users / real users            92 / 0
Supabase tables                             2      profiles · portfolio_items
```

**Correction on record:** I reported `text-primary` at 224. It is **56**. The 224 counted
`text-primary-foreground` as well, and the `fix/f3-onboarding-tokens` batch converted many of the
rest. Use 56.

**One number worth pausing on:** `components/ml` now has **zero** arbitrary type sizes. The kit is
clean. All 1,002 remaining are in route bodies. Phase 8 is a route sweep, not a component sweep, and
the estimate should be re-derived on that basis.

---

## Track A — ship-blocking. Do these first, in this order.

Each is one branch, one PR, exit gate on each.

### A1 · `fix/booking-link-targets` — **the only live functional break**

```
business.$id.index.tsx:91                  params={{ id: "svc-1" }}
opportunities.$id.applicants.$aid.tsx:168  params={{ id: "svc-1" }}
business.$id.enquiry.tsx:120               params={{ id: "svc-1" }}
```

`/booking/$id` accepts only the four space ids in `data/booking.ts` — `octave-sound-labs`,
`headroom-mastering`, `basement-jam`, `waveform-podcast`. Every Book button outside a completed
booking lands on "This space is unavailable".

`services.$id.index.tsx` now routes to `/services/$id/enquiry` instead, so the service page no longer
reaches booking at all — decide whether that is intended before fixing the other three.

**This is the revenue path, it is live, and it is a link-target fix.** It has been open since the
route audit and two batches have shipped around it. Size: tiny.

### A2 · `chore/remove-demo-controls`

"Preview failure state" at `booking.$id.pay.tsx:132` and `ml/portfolio.tsx:319`, plus the `/collab`
States cycler and the schedule toggles. Simulator controls in a production build. Remove, or gate
behind `import.meta.env.DEV` — implementation choice, not a product decision. Size: tiny.

### A3 · `fix/pink-text-contrast` — an accessibility defect, not a style preference

```
                 #EA4C89 (primary)   #C7295F (pink-strong)
page ground            3.03                 4.56
white card             3.56                 5.37
```

AA body text needs 4.5:1. **56 sites fail.** `pink-strong` exists for exactly this and is used 3
times.

Not a blind replace: icons and large text need only 3:1, which `#EA4C89` clears. Split text from
icons first. **Do the whole app in one pass** — converting screen by screen is what creates the
drift. Size: small.

### A4 · `investigate/auth-claims` — verify before anyone writes a fix

Two claims are in circulation that were inferred from greps, not runs:

- *"Google sign-in and the email-confirmation link skip onboarding."*
- *"Phone sign-in can never succeed."* — `auth.otp.tsx:59` renders that notice **conditionally on
  `error`**.

Run both. Report what happens. No code changes in this branch.

---

## Track B — design system. Two decisions, then mechanical work.

### B1 · DECISION: the 14px step

300 `text-sm` call sites, including `ui.tsx:198` — the `Btn` primitive, so every button label in the
app. 13px shrinks every button; 16px widens every button and your labels are already long in
Indian-language contexts.

**Recommendation: add the step.** The four-steps-per-scale cap is dead, and **14sp is a standard
Material step** (bodyMedium), so it satisfies the "web minimum equals Android sp" rule rather than
breaking it. Cheapest implementation: define `text-sm` as a real token in `@theme inline` pointing at
a new 14px ramp — 300 sites become compliant with zero call-site edits, and no renumbering cascade
into the 1,002 arbitrary sizes.

**Blocked on Vedanshu.**

### B2 · DECISION: the logo on Welcome and Home

Both render "MusicLinkd" as text with no mark. Welcome is the first screen a new user sees. Design
call, not a defect. **Blocked on Vedanshu.**

### B3 · `fix/auth-status-component`

`auth.locked` / `auth.maintenance` / `auth.offline` are one object with three tones — and they are
where three of the registration flow's five token breaks live (`text-[13px]` at `locked:43`, inline
backgrounds at `locked:33` and `maintenance:28`).

One `AuthStatus` in `ml/auth.tsx` taking `{ tone, icon, title, body, actions }`. Add
`--destructive-container` beside the existing `--pink-container` / `--success-container` /
`--warn-container`. Three files collapse to three call sites and the inline styles die by
construction.

**Do not merge** `index` / `phone` / `otp` / `verify-email` / `forgot`. Different inputs, different
validation, different failure modes. They already share what is worth sharing.

### B4 · `fix/emoji-in-shared-components`

Emoji and text glyphs used as icons in the **primitives**, so they render across many screens:

```
ml/ui.tsx:120            🎙
ml/cards.tsx:26,61,93    📍  ♡  ★
ml/profile.tsx:281       📍
ml/portfolio.tsx:360,503 🎧  ★ ☆
ml/opportunity.tsx:88    ★
```

Higher leverage than the route-level ones. Lucide equivalents exist for all of them.

### B5 · `fix/inline-styles-in-kit`

53 inline `style={{ }}` in `components/ml` — more than in all 209 route files combined (56). Same
reasoning as B4: the kit multiplies.

Exclusions that are correct as-is and must not be "fixed": computed progress-bar widths
(`complete.tsx:84`, `resume.tsx:74`), safe-area padding, and the four Google-brand hex fills in
`welcome.tsx:52-64`. Comment each so no future sweep touches them.

---

## Track C — UX mechanics. Measured properties, not taste.

### C1 · `feat/onboarding-motion`

`grep animate-|transition-` on `step.$id.tsx` returns **nothing**. Nineteen steps, every one a hard
cut. On Android that reads as a failed tap — nothing acknowledges the input.

This is Phase 2b, and the registration flow is where to land it first: every user sees it, 19 times.
Honour `prefers-reduced-motion`, use the existing `--duration-*` and `--ease-*` tokens, exit at
60-70% of enter.

### C2 · `feat/onboarding-optional-steps`

Only **4 of 19** steps are required — identity, role, location, visibility. `StepShell` shows
"Step 9 of 19" and a percentage, which is honest and discouraging. Mark the optional steps in the
step data and let `StepShell` render it.

### C3 · `chore/android-ime`

`__root.tsx:81` sets `viewport-fit=cover`. There is no `interactive-widget`, so the browser default
decides what happens to the sticky footer CTA when the Android keyboard opens — in a 19-step form
where that is the most-tapped control in the product. Specify it, then verify on a device at 412px
with the keyboard open. Capacitor prerequisite.

### C4 · Help screens — wiring only

Vedanshu designs the screens and writes the copy.

- Route: `/help/getting-started/$step` — one file, one set of states, step count can change without
  a new route.
- Attachment points that add no step to the 19: `/auth/success` (already offers Set up profile /
  Skip to Home) and `/onboarding/complete`. A third entry from `/help`, which currently shows
  "Nothing here yet" for two topics.
- **Dismissal must persist in the profile row, not `localStorage`** — `localStorage` is per-browser
  and the Capacitor wrap will re-show the help after every reinstall. No column exists yet.
- Not an interstitial. The flow already has 28 screens before Home.

---

## Track D — backend. Sequenced, gated on one decision.

Full reasoning in `state/DECISION-backend-direction-2026-09-14.md`.

### D1 · Taxonomy into Supabase — **the real unblocker**

29 categories, 95 options needing slugs (22 already have them), `languages` on ISO 639-1 so identity
is never the display string.

20+ columns across the entire intended schema are typed plain `string` purely because their values
belong in taxonomy tables. Nothing downstream can be modelled correctly until this exists, and with
**zero real users this is the cheapest it will ever be.** Spec ready:
`specs/TAXONOMY-MIGRATION-SPEC.md`.

### D2 · Design the tables. Do not copy Firestore's shape.

`profileType: BAND | BUSINESS | TALENT | VENUE` on one 22-field document **is** the god-document.
In Postgres: a `profiles` core plus per-type tables; `role_profiles` as one table with
`(owner_uid, role_key)` and `jsonb attrs`; denormalised fan-out fields (`collaboratorUids`,
`authorName`, `posterName`) dropped in favour of joins.

Fix in the port, not later: money to integer minor units, dates to `date`/`timestamptz`,
`instrumentProficiency: string[]` holding `"Guitar (Expert)"` to a join table.

Build tables **as features need them**, not 34 at once. Eight Firestore collections have zero
documents and no writer even in the old app.

### D3 · DECISION: what gets seeded, and is it marked

`profiles` SELECT is public. **Every imported row becomes a live public page the moment it lands.**

On 13 Sep the call was to keep 11 seeded profiles unmarked, pre-launch. Importing 92 makes it 103
pages for people who do not exist. Reasonable at 11; different at 103.

Two paths:

- **All 92 plus an `is_seed` flag** — one boolean column now, keeps the marking decision reversible
  without a second migration.
- **The 19 BUSINESS + 1 VENUE first** — studios, labels and venues are what booking and marketplace
  actually need, and they are not invented humans. The 62 TALENT rows follow once the flag question
  is settled.

**Blocked on Vedanshu.**

### D4 · The prize

The app's content lives in **22 TypeScript modules** under `src/data/` — the single largest line in
the Phase 8 estimate at **40.5 hours**. Move it into tables and the UI-copy / seed / runtime
separation `AGENTS.md` requires holds **by construction** rather than by discipline. That is the
architectural reason to do D1-D3 at all; the data movement is incidental.

---

## Deferred — do not touch

**Dark mode.** Vedanshu's decision, 13 Sep. `@custom-variant dark` stays. No `.dark` block, nothing
adds the class, no `prefers-color-scheme`, and exactly **one** `dark:` utility in the app —
`dark:border-destructive` in vendored `components/ui/alert.tsx`. Inert. Leave it.

---

## Still open, owner not engineering

1. **14px step** (B1) · 2. **Logo on Welcome and Home** (B2) · 3. **Seed marking** (D3)
4. **Account type** — registration or first onboarding step. Prerequisite that is *not* a decision:
   the answers land in `discovery_prefs` jsonb, nothing reads them, and no account-type column
   exists. A migration and a read path are needed either way.
5. **Signed-out visitor access** — which public pages open without an account. Partly already
   answered by the database: `profiles` SELECT is public.
6. **Capacitor path** — thin wrapper or static bundle. Gates `viewport-fit`,
   `@capacitor/preferences` and the manifest splash.
7. **The admin panel's fate** — stays on Firestore, or follows to Postgres. Nothing forces it today.

## Order, if you only read one line

**A1 → A2 → A3 → D1 → B3/B4/B5 → C1/C2/C3 → D2 → D3.**
A4 runs alongside anything. B1, B2, D3 block on you.
