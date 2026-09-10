# /guide Design-System Fix Plan

Status: plan only, no edits made. Written 2026-08-30 against a live audit of
https://musiclinkd.com/guide (the self-generating page built from
`frontend/guide/system/ml-tokens.css` + `ml-core.css` + `ml-widgets.css` via
`frontend/guide/index.html`) reconciled against the local repo. Every finding
below cites a file:line — nothing is assumed. Waiting for your go-ahead before
touching anything, per your instruction.

Scope note: `styleguide.html` and `musiclinkd-system/` are separate, older
artifacts (see `frontend/guide/INDEX.md`) and are **not** what's live at
`/guide` today — the live page is the `ml-tokens`/`ml-core`/`ml-widgets` one.
They're mentioned below only where they hold conflicting color data worth
flagging.

---

## Tier 1 — Real bugs, fix outright (no judgment calls)

1. **Inset Card is coded but invisible.** `ml-widgets.css:722-835` defines a
   full 15-selector "Inset Card" family, but its section header uses a `══`
   comment style the generator's `parseSections()` doesn't recognize as a
   boundary (`scripts/build-guide.mjs:145-166` expects `──`). Result: those
   15 selectors get silently folded into the unrelated "tab-bar beta marker"
   section (`index.html:1290-1301`), and there is no demo markup anywhere
   rendering `.ml-inset*`. Fix: correct the header syntax so the parser
   splits it into its own section, then add demo markup — this single fix
   also resolves the "half-baked component" complaint for Inset Card, since
   the component itself is actually complete; it's just orphaned.
2. **Duplicate `.ml-tabbar__item` rule** — declared at `ml-widgets.css:107`
   (real) and again at `ml-widgets.css:704-705` (stray, inside the same
   misfiled block as #1). Delete the stray declaration. This also fixes the
   "510 selectors" badge being 2 short of the true per-section sum (512).
3. **`.ml-help` fails the 48dp tap-target rule in practice**, not just
   visually. `ml-widgets.css:1529-1541` uses the same `::after`
   band-expansion technique as `.ml-chip` (which works, and even has a code
   comment explaining why), but `.ml-help` never sets `position: relative`
   on itself, so the absolutely-positioned `::after` resolves against a
   farther-up ancestor instead. One-line fix. This is the only interactive
   control in the whole file that actually fails the native touch-target
   floor — everything else you flagged under "native sizing" already passes.
4. **Doubled token names in the Brand palette markup.** `index.html:188-198`
   labels every brand swatch `--color-color-brand-*` (double prefix) — the
   real token names in `ml-tokens.css:30-34` are `--color-brand-*`. Typo in
   the hand-written HTML, not the CSS.
5. **Stale header badge: "64 tokens".** Real count is 96
   (`ml-tokens.css` header comment itself says "96 tokens, ceiling 104").
   Root cause: `scripts/build-guide.mjs:1143` computes the badge as
   `foundation.color keys + a hardcoded "+43"` fudge factor instead of
   actually counting every category. Fix the counting logic, not just the
   number, or it goes stale again next edit.
6. **False claim: "Both now redirect here."** `index.html:2686-2688` claims
   `/guide/styleguide` and `/guide/musiclinkd-system` "now redirect here."
   Verified false — both are still live, full pages, no redirect exists.
   Either implement the redirects (hosting-config change, outside what I can
   verify from static files alone) or rewrite the sentence to state the
   actual relationship. Flagging as a decision, not doing either without
   your call.

## Tier 2 — Token compliance (hardcoded values bypassing the scale)

Concrete offending lines, `ml-core.css`: icon/control sizes at 150, 196,
353, 725, 780, 833, 924, 935, 957, 1038, 1044, plus one-off paddings/widths
at 300, 1083. `ml-widgets.css`: 122, 192, 590, 634, 696, 892, 971-972,
1214, 1248, 1531, 1714, 1926, 1010-1018. Most of these are near-misses of
the real icon scale (16/20/24/32) sitting at 14/18/22 instead — i.e. the
"Icon sizes" demo (`index.html:375-389`) is currently showing 8 sizes when
only 4 are real tokens, and the other 4 are exactly these stray values.
Proposed fix: snap every one of these to the nearest real token
(`--icon-1..4`, `--space-*`, `--radius-*`) and delete the 4 non-token sizes
from the Icon sizes demo, leaving 4 — matches your "I think we just keep 4?"
instinct. One documented exception stays as-is: `.ml-appgrid__canvas`
96px / 28px radius, which intentionally mirrors Android's 108dp
adaptive-icon canvas (a platform constant, already commented as such at
`ml-widgets.css:2069-2073`) — not a violation.

## Tier 3 — Color book: the merge you actually asked for

Two color books currently exist as **separate physical sections**, not one:
UI palette under Foundations (`index.html:203-236`, 16 swatches — ink,
paper, surface, pink, success/warning/error/info, etc.) and a Brand
"marketing" palette under the Brand section (`index.html:179-198`, 5
swatches — brand-warm-pink `#FF005C`, brand-black, brand-white,
brand-dark-gray, brand-light-gray). The page's own prose already *claims*
"these live in the same colour book as of 2026-08-30" (line 181) — that
sentence is aspirational, not true yet. Plan: physically move the 5 Brand
swatches into the Foundations → Colour section (one `g-swatches` block, one
place), leave the Brand section's logo/voice/type content where it is, fix
the doubled-token-name bug (#4 above) as part of the same move so the merged
block is clean. UI palette values are untouched, per your instruction.

Separately flagging, not touching without your say: `styleguide.html` (JS
palette array, `styleguide.html:5381`) and `frontend/guide/brand/README.md`
both carry **drifted** numbers that actively disagree with the live
tokens (e.g. styleguide's green `#16A34A` vs. the real `--color-success`
`#22C55E`; README's "Primary dark `#0D0B26`" matches neither `--color-ink`
nor `--color-brand-black`). These are outside the live `/guide` page, so
fixing them is a separate decision — say the word and I'll scope it.

## Tier 4 — Trim the audit essays (values stay, prose goes)

Per your "plan only, wait for approval" and "trim prose, keep values"
answers: `index.html:269-276` currently carries the full FAILS/CANNOT-be-
fixed/DELIBERATE paragraphs for the size and type scales, plus a
"Verdict — 5 of 7 scales pass outright..." summary line. Plan: delete the
verdict-summary sentence entirely (matches "guide will not have extra
content"), and shrink each scale's justification to a single factual
clause — e.g. "size: 32/40/48/56 — constant set, not a ramp (Material
48dp / iOS 44pt floor makes an exponential scale impossible)" and "type:
12/16/22/32 — modular perfect-fourth (1.33x), not a strict 1.5x ramp."
The actual value tables (`index.html:240-253`, 261-306) already exist
separately with no prose attached — those stay as-is.

## Tier 5 — Semantic tokens

There is no CSS custom-property semantic layer today — `tokens.json`'s
`semantic` block is explicitly `$emitCss: false` (documentation/native-export
only); web components reference foundation tokens directly. One concrete,
fixable gap: three components (`.ml-status--error`, `.ml-banner--error`,
`.ml-tier__feat svg`) each independently derive an error-background vs.
error-text color via their own inline `color-mix()` call instead of a shared
named token. Plan: add 2 real semantic custom properties
(`--color-error-bg`, `--color-error-text`) to `ml-tokens.css` and point all
three components at them — small, contained, fixable in CSS only.

## Tier 6 — Known gaps: merge into one table, resolve what's resolvable

Two separate "gaps" tables exist today (`index.html:369-373` and
`index.html:2665-2681`) — merge into one. Per-item disposition:
- `aspect 4/3` hardcoded in `.ml-media-card` — **fixable**, tokenize as
  `--media-aspect-card`.
- `breakpoints 767/1023/1440 not adopted` — not actually a gap, it's a
  resolved decision (Material 600/840 was chosen instead); rewrite the row
  as a decision record, not an open gap.
- **Density modes** — genuinely not implementable from CSS alone (native
  Android engineering). Keep as one honest line, no essay.
- **Snackbar 4s/10s durations** — lives in a JS toast controller, not the
  stylesheet; same treatment, one line, flagged as JS work not CSS.
- **"Widgets named but not built · 14 of 27"** and **"Screens not yet on
  the system · 103"** — these are content-build items, not bug fixes. Real
  list of the 14: RecommendedCollaboratorCard, DiscoverySearchResultCard,
  NetworkingFeed, BandMembersWidget, CollaboratorMatchWidget,
  StudioVenueListItem, FeaturedStudioCard, StudioSpecWidget, ProjectCard,
  RecentProjectCard, TrackCard, AudioPodcastDemoWidget, HireContactModal,
  WhatsAppShareSheet. This is a separate, much larger phase of work (real
  component build-out), not something to fold into a token/CSS-bug pass —
  proposing it as Phase 2 below rather than doing it inline with the fixes.

## Tier 7 — Half-baked components

- **Calendar** — actually complete (today/blocked/selected/range states,
  legend, nav). No action needed; you may have been going on the same
  impression the Inset Card bug created elsewhere on the page.
- **Chat** — functional but thin: only 2 states (in/out bubble). Proposed
  addition: typing-indicator state, failed/unsent message state.
- **Timeline** — only `.is-done`/`.is-current` states exist; no
  failed/cancelled state, which is a realistic case for booking/transaction
  progress. Proposed addition: `.is-failed` state.
- **Inset Card** — see Tier 1 #1, this is the real half-baked one, and it's
  a parser bug, not a missing build.

## Tier 8 — Brand section reorganization

You asked "why is Brand again?" — it's currently the **first** section in
both nav and body (`index.html:90`, 120), ahead of Foundations. Proposed
reorg: move the color palette out of Brand into Foundations (Tier 3), keep
logo/voice/type specimens in Brand, and move the Brand section itself to
sit right after Foundations rather than before it — Foundations (tokens)
first is the more standard design-system convention, Brand (identity
application) second. This is a judgment call, not a bug — flagging for your
decision rather than assuming.

## Tier 9 — webapp/index.html

Correction to your ask: the "Widgets named but not built" and "Coverage"
callouts you want removed once things are built are **not** in
`webapp/index.html` — they're the same Tier 6 Gaps/Coverage sections in
`frontend/guide/index.html`. `webapp/index.html` does correctly consume the
same token system (`webapp/shared/tokens.css` is a thin `@import` wrapper
around the three real files, confirmed by diff — not a copy) and has its
own, separate, legitimate live-computed stub counter
(`webapp/index.html:372`, "`N of 155 screens · designed / stubs`") driven by
`data/designed-screens.json`. That counter doesn't need removing — it's
supposed to shrink as real screens get built, same idea as the Gaps table.

---

## Proposed sequencing

**Phase 1 (this pass, all CSS/HTML, no content build):** Tier 1 bugs, Tier 2
token snapping, Tier 3 color merge, Tier 4 prose trim, Tier 5 semantic
tokens, Tier 6 gap-table merge + resolvable items, Tier 7 Chat/Timeline
states. Small, mechanical, contained — I can execute this in one pass once
you approve.

**Phase 2 (separate, larger, needs its own scoping):** building the 14
named-but-missing widgets and however many of the 103 stub screens you want
prioritized. This is real component-design work, not a bug-fix pass, and
deserves its own plan and priority order rather than being bundled in here.

**Open decisions — answered 2026-08-30:**
1. Redirect claim: moot — `firebase.json` already has real 301 redirects for
   both old pages, confirmed by reading it directly. The guide's original
   "both now redirect here" text was correct; a mid-pass edit of mine
   wrongly changed it, and that was reverted. No further action.
2. Drifted colors in `styleguide.html` / `brand/README.md`: **left alone**,
   per your answer. Both still disagree with the live token values (see
   Tier 3 note above) — flagging again here so it doesn't get lost, but not
   touching either file.
3. Brand section position: **left as-is** (first in nav), per your answer.
   Only the color-book duplication itself was fixed (colors now live in
   Foundations); Brand's place in the page order is untouched.
4. Phase 2 scope: below.

---

## Phase 2 scope — missing widgets & stub screens

You said scope now, build later ("I will do it in the end") — this is the
backlog and priority order, not an implementation.

### 14 missing widgets

All are CSS/markup work, no backend needed (confirmed against
`admin/docs/architecture/Widget_Directory.md` — they're documented capabilities
with no matching `.ml-*` component yet, not missing data):
RecommendedCollaboratorCard, DiscoverySearchResultCard, NetworkingFeed,
BandMembersWidget, CollaboratorMatchWidget, StudioVenueListItem,
FeaturedStudioCard, StudioSpecWidget, ProjectCard, RecentProjectCard,
TrackCard, AudioPodcastDemoWidget, HireContactModal, WhatsAppShareSheet.

Suggested build order, grouped by which flows are already otherwise
complete and would go from partial to fully covered fastest: Discover-
adjacent first (DiscoverySearchResultCard, NetworkingFeed, StudioVenueListItem,
FeaturedStudioCard, StudioSpecWidget — Discover is the single biggest stub
flow below, so these overlap with that work); then Opportunities/Portfolio
finishing touches (CollaboratorMatchWidget, RecommendedCollaboratorCard,
ProjectCard, RecentProjectCard, TrackCard, BandMembersWidget); then the two
standalone ones (HireContactModal, WhatsAppShareSheet, AudioPodcastDemoWidget)
last since nothing else depends on them.

### Stub screens — real counts, corrected

Re-counted directly from `webapp/flows/**` against
`webapp/data/designed-screens.json` (this is a different, more current metric
than the guide's own "52 of 155" Gaps line, which checks a different signal
— `html.includes("ml-app")` — and is now stale against this count; worth
reconciling later, not blocking Phase 2 scoping):

**69 designed / 86 stub / 155 total.** Fully done, zero stubs: `auth` (7),
`booking` (11), `communication` (7), `marketplace` (8), `onboarding` (13),
`opportunities` (6), `portfolio` (11) — 63 screens, don't touch.

Stub flows, worst first:

| Flow | Total | Stub | % stub |
|---|---|---|---|
| discover | 21 | 16 | 76% |
| user_settings | 13 | 13 | 100% |
| reviews | 11 | 11 | 100% |
| profile_management | 10 | 10 | 100% |
| public_profile | 9 | 8 | 89% |
| business_profiles | 7 | 7 | 100% |
| cms_pages | 5 | 5 | 100% |
| services_availability | 5 | 5 | 100% |
| notifications | 4 | 4 | 100% |
| band | 3 | 3 | 100% |
| qr_share | 1 | 1 | 100% |
| saved_favorites | 1 | 1 | 100% |
| search | 1 | 1 | 100% |
| verifications | 1 | 1 | 100% |

Recommended order: **discover** first — largest single flow (21 screens),
already 5 designed so the pattern's established, and it's the flow the new
widgets above mostly plug into, so building it next reuses that work instead
of doing it twice. Then the five 100%-stub flows with the most screens
(user_settings, reviews, profile_management, business_profiles — 41 screens
combined) before the four 1-screen flows (qr_share, saved_favorites, search,
verifications — quick wins, but low-impact individually, fine to do
whenever there's a small gap to fill).

Not scoping `recommendation_engine` or `subscription` — both show 0 screens
in `webapp/flows/`, meaning no screen folders exist for them yet at all, not
that they're stubs. That's a "does this flow have a screen registry entry
yet" question, one level before "is it designed" — flag for you to confirm
whether they're in scope at all before anyone scopes screens for them.

---

## Phase 3 — content mined from an older guidebook (2026-08-30)

You pasted a large block of design-system content and asked what's worth
enhancing the current `/guide` with. Diagnosis first: this text is from an
older, different document — not the live page. Two tells. It's internally
self-duplicating (Design Principles and Vision are word-for-word identical;
Accessibility/Tokens repeats three times; Voice & Tone and Guidelines are the
same table twice) — exactly the "duplicate token systems... duplicated
sections" problem this repo's own `CLAUDE.md` already logged against
`styleguide.html`. And its numbers contradict the current, audited tokens:
spacing here is 4/8/16/24/32 (five steps) vs. the real `--space-*` scale's
four steps (4/8/16/32, no 24); type is 11/14/20/28/48 vs. the real `--type-*`
scale's 12/16/22/32; the focus ring is a hardcoded `#EA4C89` hex vs. the real
`var(--color-pink)` token. None of the old numbers get ported — the current
ones are the ones that survived the 2026-08-30 scale audit (Tier 4 above).

### Worth building — confirmed genuinely missing, not duplicating anything live

Checked directly against `frontend/guide/index.html` (grepped for each —
zero matches on all of these):

1. **Filterable Widget Registry table.** The old doc has a live "Filter 27
   widgets by name, category, or code mapping…" table, grouped into the same
   9 categories as `admin/docs/architecture/Widget_Directory.md`. The current
   guide has no equivalent — the 27-widget data already exists (it's what
   `readGaps()` in `build-guide.mjs` checks widgets against), it's just never
   rendered as its own browsable table. Real, additive, matches the existing
   "generated from the repo, not hand-typed" philosophy.
2. **A 4-category "Not Built" ledger**, replacing the flatter current one.
   The old doc splits 45 excluded items into: not available in Gestalt
   (checked against the real package export list), different platform —
   Android-native out of scope, no real backing found (checked against both
   the widget directory and the screen registry), and aesthetic-inspiration
   references only. The current Known Gaps + Coverage sections do similar
   work but as one undifferentiated list — this categorization is more
   honest about *why* each thing isn't built, which is exactly this
   codebase's own stated design principle ("Recorded rather than quietly
   skipped").
3. **Voice & Tone / Content guidelines table** (context → tone → example,
   for onboarding/discovery/booking/error/success/trust). Real content gap —
   nothing like it exists on the current page, and copy tone is normally
   part of a design system. Needs a light edit pass first: the old table's
   success example uses an emoji ("Booking confirmed! 🎉"), which conflicts
   with this system's own icon library and its "no emoji" discipline implied
   by having a 423-icon set — swap for a real icon reference before porting.
4. **Motion Principles table** (purposeful / fast feedback / smooth
   transitions / spring physics / reduced motion) — a plain-English
   companion to the existing Motion section's technical duration/easing
   values. Additive, no conflict.
5. **Accessibility tokens table** (contrast ratio, touch target, screen
   reader, focus ring, reduced motion, dynamic type, color-blind, keyboard,
   semantic labels) — genuinely missing from the current guide entirely.
   Needs the same fix as #3: replace the hardcoded `#EA4C89` focus-ring value
   with `var(--color-pink)` and the touch-target row with the real
   `--size-3`/`--size-4` tokens (48dp/56dp) rather than a bare "48dp minimum"
   string.

### Needs your decision, not a default

6. **Admin Panel patterns** (data table, sidebar nav, top bar) — real and
   accurate to what's built (`admin/app/(admin)/...`), but the current guide
   was scoped to the *product* system, not the admin console. Adding it
   would be a deliberate scope expansion, not a bug fix — your call.
7. **Product Overview / Modules / User Journeys & Flows / Language &
   Instrument catalog** — these already exist, just not on this page: PRD &
   Product Overview, Personas & User Types, the Canonical Flow Registry, and
   Master Data System all live in Notion (this session's earlier work).
   Porting them here would create a second copy of content that already has
   a canonical home — recommend linking out instead of duplicating, unless
   you specifically want the guide to be self-contained without a Notion
   dependency.

### Not recommended — flagging why, not silently dropping

8. **Illustration/Texture/GIF-Lottie "library" scaffolds** — the old doc is
   explicit that these hold zero real files, and the "Lottie-style checkmark"
   it cites is a CSS/SVG stand-in, not an actual Lottie file. Adding empty
   scaffolding tables to the live guide would reintroduce exactly the kind
   of unverified claim this whole audit has been removing (see Tier 4/6
   above) — recommend leaving these out until real assets exist, not porting
   the scaffold.
9. **Gen-Z gradient blocks, glassmorphism, glow/ring effects, Swipe Deck,
   Brief Wizard, Stem Mixer/Equalizer, and the whole "24 Inspired"
   Dribbble/Pinterest/Apple-Music moodboard section** — the old doc's own
   text already labels most of this "Uncategorized... will be audited and
   categorized later" or explicit "inspiration, not real screens." It stayed
   uncategorized because it never got resolved, not because it's ready.
   Recommend leaving all of it out of the current system-of-record guide;
   if there's a real product need for a gradient/glass aesthetic direction,
   that's a design decision to make deliberately, not to inherit from an
   old scratch file.

### Suggested order, if you want this done

Items 1, 2, 4 first (pure additions, zero conflicts, no content-editing
judgment calls). Then 3 and 5 (need the emoji/hex cleanup noted above).
Then 6 and 7 need your answer before anyone starts. 8 and 9 — no action.

### 10. Typography Scale & Editorial Specimen Showcase

Added to the "worth building" list, same treatment as 1–5 above. Overlaps
partially with what already exists (the live guide already has "Type
specimens" and "Typography spec" sections), so this isn't a pure gap — it's
a request to present the same real data (Inter Tight for Display/Headline
with negative letter-spacing, Inter for Body/Label, the real `--type-1..4`
clamp values) as a dedicated, named, more editorial showcase rather than a
plain spec table. Real content either way; the only decision is whether it's
a new section or a restyle of the existing one — recommend restyling the
existing "Type specimens" section rather than adding a second, competing
typography section, per the same "one book, not two" principle Tier 3 (color)
already established.

---

## Phase 4 — screen-level UX critique (external, partially verified)

You pasted three overlapping critique passes (a numbered P0/P1/P2 list, a
"10 flat-out factual findings" list, and a 15-item audit-framework list),
apparently produced by another tool looking at rendered screens in
`admin.musiclinkd.com/preview` (Designed/Default state) against this guide's
own documented rules. This is a different kind of claim than Phase 1–3 above:
those were about the design-system source files themselves; this is about
whether actual *screens* correctly implement that system. I don't have
visual access to `admin.musiclinkd.com/preview` from here, so I checked what
I could — the design-system rules being cited — directly against the real
CSS/tokens, rather than taking "the guide explicitly defines X" on faith.

### One claim is factually wrong, and it's load-bearing for two items

**P0 #3 ("Onboarding selected state contradicts the system") and its
duplicate in the "10 flat-out factual" list (#2) both claim the guide
defines selected chip/card state as a pink 2px border, and that a black
border is the bug.** Checked directly:

```
ml-core.css:354  .ml-chip[aria-pressed="true"], .ml-chip.is-selected {
                   background: var(--color-ink); border-color: var(--color-ink); color: var(--color-white);
                 }
ml-core.css:970  .ml-choice[aria-checked="true"], .ml-choice.is-selected {
                   border-color: var(--color-ink); border-width: var(--border-2);
                 }
```

Both use `--color-ink` (black), not pink. The live guide's own "Component
states" table confirms this is deliberate, not an oversight: *"Selected —
Tap / click — `var(--color-ink)` fill for commitment; `var(--color-pink-container)`
for intent."* Ink means a committed selection (exactly what picking your
role in onboarding is); pink-container is reserved for a lighter
"intent/hover" state. So the screenshot's black border is very likely
**correct behavior, not a bug** — the critique had the rule backwards. Not
porting this fix. If you've seen the actual screen and still think it reads
wrong, that's a legitimate design opinion to hold, but it isn't "the system
already says do this" — it would be a rule change, not a fix.

### Confirmed accurate against the real system

- **16:9 studio hero (P0 #2 / factual #1).** `--media-aspect-wide: 16/9` is
  real (`ml-tokens.css:143`), and both `.ml-hero` and `.ml-studio-card__media`
  use it (`ml-widgets.css:379, 246`). The *token and component* are correctly
  16:9. Whether the specific rendered screen in `/preview` actually applies
  `.ml-hero` correctly, or overrides it with an inline style/local CSS, is a
  screen-implementation question I can't check without opening that URL —
  worth a quick visual confirm before fixing, but the system-level claim is
  real.
- **Icon mixing policy (P2 #15).** Real and exact: `rules/components.md:24`
  — *"Icons: two sets, never mixed inside one control. Gestalt (423, filled,
  16/24 grid) is the product set... Lucide (2006, 2px stroked) is the
  breadth set."* The critique's framing (pick one family per surface,
  enforce it) matches this rule's intent.
- **Progress component exists (P1 #8 / factual #8).** `.ml-progress` is real
  (`ml-widgets.css:426`, demoed as "Profile 80% complete" with a fill bar) —
  a step-count-plus-bar treatment is a legitimate composition of it, not a
  new component.
- **Stat grid exists as a named widget (P0 #4 / factual #7).** `.ml-stat-grid`
  is real (`ml-widgets.css:365`). The critique isn't disputing that it
  exists — it's disputing whether it should be the *first* thing on Home,
  which is a content-ordering/IA call, not a token or component question.

### Verified against live rendered screens (`admin.musiclinkd.com/preview`), 2026-08-31/09-01

Got real browser access to the Screen Workbench after the section above was
written. Re-checked the remaining critique items against actual rendered
screens instead of leaving them all "unverified." Status per item:

- **Home stat-grid before the "Continue" actionable card (P0 #4 / factual
  #7) — CONFIRMED TRUE.** Screenshotted the Home dashboard: the stat grid
  renders above the actionable "Continue" card, matching the critique. This
  is the IA/content-ordering question flagged above as not a token issue —
  now confirmed as the actual rendered order, so it's a real candidate for a
  content-ordering fix, your call on priority.
- **Studio Profile hero not actually 16:9 (P0 #2 / factual #1) — CONFIRMED
  TRUE, and it's a screen bug, not a token bug.** Screenshotted and measured
  the rendered hero at roughly 1.35:1, not 16:9 (1.78:1). The token
  (`--media-aspect-wide: 16/9`) and the component definition
  (`.ml-hero`/`.ml-studio-card__media`) are correct, as already established —
  this screen's markup isn't applying them correctly. Needs a look at the
  actual screen file (`webapp/flows/.../studio_profile/index.html` — not yet
  located/read).
- **Studio room card density + truncated second card, no carousel
  affordance — CONFIRMED TRUE.** Screenshotted the Studio Profile room list:
  first card is dense (42m², live room, ₹1,800/hr, Neve 1073, Verified all
  on one card), second card ("Mix Suite") is visibly cut off at the screen
  edge with no swipe/carousel affordance shown.
- **Onboarding progress is text-only ("1/13"), no visual bar — CONFIRMED
  TRUE.** Screenshotted onboarding: no progress bar element, just text.
  `.ml-progress` (the real bar component, confirmed to exist above) isn't
  used here.
- **Emoji icons on role-selection screen — CONFIRMED TRUE.** Screenshotted:
  real emoji glyphs render as the icons for role options. This is a direct
  violation of the documented icon policy (Gestalt/Lucide only, "two sets,
  never mixed" — emoji is a third, undocumented set).
- **Onboarding selected state — RE-CONFIRMED CORRECT (critique still
  wrong).** Screenshotted "Primary Role Selection": selected option shows a
  black 2px border + black checkmark, exactly matching `--color-ink` per the
  CSS already cited above. This is the second, independent confirmation that
  the critique's P0 #3 / factual #2 claim is backwards — not porting it.
- **Booking · Payment screen CTA — DOES NOT REPRODUCE.** Screenshotted: CTA
  already reads "Pay ₹5,000" (amount included), not the bare "Pay deposit"
  the critique implies. This specific screen is already correct.
- **Booking · Booking Summary screen (a different, earlier screen in the
  same flow) — CONFIRMED TRUE, two bugs, NOW FIXED.** This is very likely
  what the critique actually meant (adjacent screen in the same flow as
  Payment). Screenshotted two real bugs:
  1. CTA read "Pay deposit" with no amount, unlike the (correct) Payment
     screen one step later.
  2. Header line "Tracking Room 1 · Wed 12 Aug, 10:00–14:00" was visually
     truncated with "…" — traced to `.ml-result__meta`
     (`ml-widgets.css`, `overflow:hidden; text-overflow:ellipsis;
     white-space:nowrap`), a generic single-line list-row style reused here
     for booking-critical info it wasn't designed to hold.

  Fixed directly in `webapp/flows/booking/booking_summary/index.html`
  (2026-09-01, uncommitted — see note below): CTA now reads "Pay ₹5,000
  deposit"; the truncating `.ml-result` row was replaced with a plain
  stacked header (studio name / room / date-time on three separate lines,
  no ellipsis) so nothing gets cut off. Not yet re-verified visually against
  the deployed `/preview` (this is a local source-file edit; the live
  preview serves a deployed build, and it's unconfirmed whether it reflects
  local edits directly) and not yet committed (same `.git/index.lock` issue
  flagged earlier — needs to run from your real Terminal).

### Still not independently verified

Bottom-nav weight/hierarchy and any remaining P1/P2 items not listed above
were not checked in this pass — flagging rather than assuming they're fine
or broken. The icon-mixing enforcement (P2 #15) and the typography restyle
(item 10 above) remain checkable against source files alone and can move
straight to "worth doing," same tier as Phase 3's list.
