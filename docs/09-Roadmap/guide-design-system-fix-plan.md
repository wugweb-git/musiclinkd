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

**Open decisions needing your call before I touch anything:**
1. Redirect `/guide/styleguide` and `/guide/musiclinkd-system` to `/guide`
   for real, or just fix the false claim to describe reality?
2. Fix the drifted color numbers in `styleguide.html` and `brand/README.md`
   too, or leave those files alone since they're outside the live page?
3. Move Brand section after Foundations, or leave its position as-is and
   just fix the color-book duplication in place?
4. Phase 2 (missing widgets/screens) — want it scoped now or later?
