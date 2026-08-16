# /guide — Index

Single entry point for everything living under `frontend/guide/`. This file is the "library catalog" — everything downloaded or vendored into this folder is listed here with what it is and where it came from.

| What | Path | Status |
|---|---|---|
| Original style guide | `styleguide.html` (+ its sibling `tokens/`, `icons/`, `components/`, `brand/`, etc.) | **Untouched** — nothing here was modified or replaced. |
| Gestalt design system (vendored source) | `gestalt/` | Added — see below |
| MusicLinkd System (live UI library) | `musiclinkd-system/` | Added — see below |
| Brand Kit | `brandkit/` | Added — see below |
| Claude skills (reference copies) | `skills/` | Added — see below |
| This file | `INDEX.md` | Added |
| Full Gestalt download manifest | `gestalt-library.md` | Added (superseded by this file as the top-level entry point, but kept — it has the detailed package-by-package breakdown) |

## `gestalt/` — Pinterest's Gestalt design system

Full fresh clone of [pinterest/gestalt](https://github.com/pinterest/gestalt) (v177.0.13, commit `22874a7`) — components, design tokens, icons, docs source, RFCs. 3,975 files, 34MB. Open `gestalt/index.html` for a static, linkable catalog of everything in it.

Full detail: [`gestalt-library.md`](./gestalt-library.md).

## `musiclinkd-system/` — live, dynamic UI library

A separate, standalone React app (own `package.json`/`node_modules`, bundled with esbuild) that renders the *real* Gestalt components, the *real* shipped design tokens, and the *real* icon set — genuinely interactive, not a mockup. Branded "MusicLinkd System." Five tabs:

- **Components (60 cards)** — every practically demoable Gestalt component, real and interactive, across 8 categories (Actions, Forms, Feedback, Data Display, **Navigation**, Overlays, Typography & Layout, Media & Gallery). The handful deliberately left out (Video, Collage/MasonryV2, search-index-specific pieces, infra-only providers/hooks, Layer, Container) are listed by name at the bottom of that tab, with why.
- **Blocks (38 composed patterns + admin widget directory)** — two sub-tabs. "Composed patterns" now spans four provenance tiers, all honestly distinguished: (1) originally designed patterns not tied to a specific data source (navigation header, artist profile card, track row, upload form, stats row, empty state, notification stack, cover art gallery, bottom navigation bar, trust badges — sourced from `admin/components/shared/trust-badge.tsx` — hero, metrics scorecard, activity feed, search & filters, quick actions, avatar stack/social proof, testimonial, pricing plans); (2) patterns validated against the real 124-screen registry (`dist/preview/flows/**/screen.json`) — booking summary, availability calendar, chat, rating breakdown, write review, success state, QR share, credit attribution, section tabs, public profile — several using the screens' own real seed data verbatim; (3) admin-panel-perspective patterns (data table, sidebar, top bar); (4) widget-directory-backed visual cards, added seventh pass — track card, studio card, featured studio card, marketplace card, opportunity card, review card, notification card, public reputation widget. "Admin widget directory": a read-only mirror of the real 27-widget catalog from `admin/app/(admin)/widgets/default-widgets.ts` (backed by `admin/docs/architecture/Widget_Directory.md`), transcribed verbatim, grouped by the same 9 categories, filterable by the same search box — a reference mirror, not the source of truth; see `musiclinkd-system/src/widgets-mirror.ts` for the re-transcription note.

### Old styleguide.html nav audit (2026-08-16, two passes)

Cross-checked the old styleguide's full component/block nav list (04 Primitives, 05 Native Components, 06 MusicLinkd Components, 07 Blocks, 24 Inspired, "More") against real data — not assumed. Second pass validated everything against the real per-screen registry at `dist/preview/flows/**/screen.json` (124 real screens, each with `flowStatus`, `notes`, and — where applicable — a real `dataContract` seed sample), not just the 27-widget directory used in the first pass.

**Definitive list — not available in Gestalt** (checked directly against the real package's export list; these components don't exist in it, period, and nothing built in this app pretends otherwise):

Upload / file-dropzone, Timeline, Skeleton Loading, Progress Bar, Time Picker, Date Picker, Slider, a distinct Snackbar (Gestalt uses `Toast` for both), Breadcrumb (checked `PageHeader.d.ts` directly — no breadcrumb prop anywhere in Gestalt).

**Entirely out of scope — different platform** (old "05 Native Components" section, all 19 items): Top App Bar, Floating Action Button, Navigation Drawer, Navigation Rail, Segmented Buttons, Progress Indicators, OTP Input, Search Suggestions, Filter Sheet, Permission Dialog, Camera Picker, Audio Recorder, Audio Scrubber, Android App Grids, M3 Navigation Bar. These are Android Material 3 native widgets, belonging to the real native app (`app/`, Kotlin) — Gestalt is web-only and can never cover them here.

**Old "More" utility list — checked against all 124 screens' titles and notes, not found anywhere:** Skeleton Loaders, Error Boundary, Offline Banner, Shimmer, Adaptive Banner, Navbar, Navbar (Alt), Sticky Nav, Segmented Switch, Metrics Row, Uncategorized, Swipe Deck, Brief Wizard. No real screen, note, or code reference backs any of these — not built.

**From that same list, confirmed real and built:** QR Share → real screen `qr_share_flow` (flowStatus **"missing"**: the UI exists per the app's own `SupportingScreens.kt:655`, but there's no backend — no schema, a simulated PDF, no real QR payload; the new "QR share" Block is labeled with that real status, not built as if it were fully wired). Empty States, Persistent Audio Player, and Bottom Navigation were already built in earlier passes.

**"06 MusicLinkd Components" / "07 Blocks" / "24 Inspired" — validated against the real screen registry, not just the widget directory this time.** Newly confirmed real and built this round: Booking Summary (real screen, real dataContract sample used verbatim — studio "Yash Raj Sound Studios," ₹32,000, Aug 12), Availability Calendar (real screen, real `monthly/weekly/timeline` states), Chat / Message Bubble (real screens `chat_thread_list` + `chat_conversation`), Rating Breakdown (real screen), Write Review (real screen, real states used for the star/comment flow), Success State (real screen), Credit Attribution (real screen), Section Tabs (real tab names taken directly from real business-profile screens: Services Tab, Team & Works Tab, Reviews & Gallery Tab, Equipment Tab).

Still **no confirmed backing anywhere** (neither the widget directory nor the 124-screen registry) — not built: Ambient Album Card, Artist Grid Card, Artist Row Card, Typing Indicator, Attachments, Notification Toggle (Notification *Settings* is real and already covered by the widget directory's `NotificationCard`/settings screens, but a distinct "toggle" widget isn't), Multi-Track Stem Mixer, Equalizer, User Header, Talent/Band/Business/Venue as separate card types, Direct Request, Request Status Tracker, Band Card, Application Card, Rental Card, plus effectively all of "24 Inspired" (Gradient Hero Cards, Content Design Patterns, Full-Bleed Bold Cards, etc. — these were explicitly aesthetic-inspiration references in the old file, not real MusicLinkd screens).

Also built this round, admin-panel perspective (not from the old styleguide list, per the direct request): Admin data table (real `Table.SortableHeaderCell` + `Checkbox` selection + pagination), Admin sidebar (real `SideNavigation`, admin-themed), Admin top bar.

### Brand Kit tab added inside musiclinkd-system (2026-08-16)

The standalone `brandkit/index.html` page's color and logo content is now also a **Brand Kit** tab inside `musiclinkd-system` itself (6th tab) — real colors, real logo files (both the 6-file Logo set on both backgrounds, and the 2-file App icon set inlined with a working live color picker), sourced from the same `brand/README.md` / `brand/assets/` files, the two app-icon SVGs imported directly via esbuild's text loader rather than copied by hand.

**Deliberately excluded: typography.** This app's own chrome renders in Gestalt's real system-font stack (`-apple-system, Segoe UI, Roboto...`), not MusicLinkd's brand typefaces (Inter Tight/Inter). Showing Inter Tight/Inter type specimens inside an app that isn't actually rendered in those fonts would misrepresent what's really running — so the Brand Kit tab shows color and logos only, with a note pointing to the standalone `brandkit/index.html` page for type specimens.
- **Foundations** — typography scale, rounding scale, elevation/shadow scale, spacing scale, a **Styles** section (real font stack pulled live from the shipped `--font-family-default-latin` token, real observed border widths, and this app's own recurring card-style recipes pulled from its own source), and icon sizes — all rendered live from the real components and real tokens, not a static description of them.
- **Design Tokens** — grouped into the 8-category taxonomy below (not a flat dump anymore), parsed at runtime/build-time from the shipped CSS plus real non-CSS sources (breakpoints, grid, z-index) documented per category.
- **Icons** — all 423, searchable.

To run: open `index.html` directly (double-click, or `open index.html`) — it's a plain static page now, no server required. (Older notes here said it needed HTTP for a runtime `fetch()`; that was replaced with build-time CSS inlining, see "What was NOT done" history below.)

**Verification note:** no GUI browser in this sandbox, so this is verified functionally (headless DOM render via jsdom: every tab, every one of the 8 token categories, light and dark mode, zero real console errors) rather than visually. One caveat found and worked around during this pass: jsdom itself doesn't implement `requestAnimationFrame`, which the real `Masonry` component (used in the Blocks tab) calls — this throws in the headless test harness only, not in any real browser (all real browsers implement `requestAnimationFrame`). Polyfilling it in the test script confirmed the Blocks tab is clean. Still worth a human glance in a real browser for the parts jsdom can't judge (actual visual layout, animation).

### Design Tokens — 8-category taxonomy (2026-08-16)

The Tokens tab is now organized under a specific taxonomy provided by the repo owner (01 Foundation → 08 Platform) instead of a flat list of CSS-var groups. Handled honestly per category — some map directly to real shipped tokens, some don't exist as tokens in Gestalt at all and say so instead of inventing data:

| # | Category | What's actually shown |
|---|---|---|
| 01 | Foundation | Real: raw color palette, font size/weight, spacing, rounding, opacity, elevation (all parsed from shipped CSS). Border: real observed pixel literals (1/2/3/4px) — flagged as not a token scale (Gestalt hardcodes these, doesn't tokenize width). Motion: the real custom-property *names* Gestalt's own compiled CSS references (`--base-motion-easing-bounce`, etc) — flagged as a genuine gap, since none of them are actually defined anywhere in the shipped classic theme. |
| 02 | Semantic | Real: `--color-background-*`, `--color-text-*`, `--color-border-*`, `--color-icon-*`, `--color-shadow-*` — split out from the same raw `--color-*` set by name pattern, since that's the real distinction Gestalt's own naming already encodes. |
| 03 | Layout | Real: breakpoints (576/768/1312px, from `useResponsiveMinWidth.ts`) and the 12-column `Column` grid system (`span`/`smSpan`/`mdSpan`/`lgSpan`, from `Column.d.ts`) — plus the Spacing scale, reused for gutters/padding since Gestalt has no separate gutter token. |
| 04 | Component | No token list — points to the Components (60 cards) and Blocks (9 patterns) tabs, since component anatomy is the components themselves, not a token file. |
| 05 | State | No token list — Gestalt encodes state via props/pseudo-classes, not standalone tokens. Points to the real state-bearing color roles that DO exist (e.g. `--color-border-checkbox-checked-error`) and to live examples (Checkbox, Switch, Button). |
| 06 | Interaction | No token list — hit area/gesture/dismissal are enforced by component behavior (IconButton tap target, TapArea), not exposed as tokens. Points to those real components. |
| 07 | Responsive | Real: same breakpoint + `Column` span data as Layout, from the responsive-behavior angle. Notes that Gestalt has no density token (no compact/comfortable variant). |
| 08 | Platform | Real: `FixedZIndex`/`CompositeZIndex` — a compositional stacking pattern (not a numeric `--z-index-*` scale, which doesn't exist), from `zIndex.ts`. States plainly that Gestalt has no App-vs-Web platform token layer since it's web-only — MusicLinkd's actual native app is a separate codebase this installation doesn't cover. |

Source: `musiclinkd-system/src/tokens.ts` (parsing + the new real non-CSS constants) and the `TokenSwatches`/`TokenNote` components in `src/App.tsx`.

### Color: Pinterest red → MusicLinkd pink (2026-08-16)

MusicLinkd's own custom UI chrome used Pinterest's brand red (`#e60023`) in two places — the active-tab underline in `gestalt/index.html`'s catalog page, and the same underline plus the new token-category nav in `musiclinkd-system`. Both now use MusicLinkd's real, already-documented primary color `#EA4C89` (from `brand/README.md`), via a dedicated `--ml-accent` CSS variable in `musiclinkd-system` rather than piggybacking on Gestalt's own `--color-icon-brand-primary` token (which would never have shown the fallback, since Gestalt's real token always resolves first). **Not changed:** any real Gestalt component demo — e.g. `Button color="red"` in the Components tab still shows Gestalt's actual red, because that's a real, accurate value from the library, not this app's own chrome. Gestalt's `Button` color prop has no pink option in its real type union, so no component was forced into a color it doesn't support.

## Responsive fixes + in-app "Not built" registry + honest audit (2026-08-16, third pass)

Direct challenge from the repo owner, three parts, each handled separately:

**1. "Not built" list moved into the app itself, not just this doc.** Blocks tab now has a third
sub-tab, **"Not built"** (alongside "Composed patterns" and "Admin widget directory"), rendering
the same four checked categories documented below — Not available in Gestalt, Different platform
(Android native), No real backing found, Aesthetic-inspiration-only — each with its real reason,
not just a name list. Source: `NOT_BUILT` constant + `NotBuiltRegistry()` in `src/App.tsx`, kept
in sync by hand with the "Old styleguide.html nav audit" section below (re-copy if that section
changes). Component-level exclusions (Video, Layer, Container, etc.) stay in the Components tab's
existing bottom note — not duplicated here, since that's a different list for a different reason
(real Gestalt exports deliberately not demoed, vs. things with no real backing at all).

**2. `index.html` audit — checked honestly, not rubber-stamped.** Read the file in full: correct
viewport meta tag, correct `<link>` order (`variables.css` → `gestalt.css` → `styles.css`),
correct `<script src="bundle.js">`. No bugs in `index.html` itself. The real problems were one
level down, in `src/styles.css` and in fixed-pixel-width `Box` components in `src/App.tsx` — see
below. Also confirmed (again) that `prepare-assets.mjs` still copies `src/styles.css` to the root
`styles.css` on every build (`diff` — in sync).

**3. Responsive/overlap bugs — root-caused and fixed, not just described.** Two real causes:

- `src/styles.css` had zero media queries and three grid classes with bare `minmax(Npx, 1fr)`
  values wider than common phone content-width. Concretely: `.ml-main`'s 64px total horizontal
  padding leaves ~311px of content on a 375px viewport, which is *less* than `.ml-block-grid`'s
  old `minmax(340px, 1fr)` — forcing horizontal overflow on the Blocks tab specifically on any
  phone at or below iPhone-12-mini width. `.ml-search`'s fixed `width: 260px` also overflowed
  `.ml-nav` outright below ~324px viewports (260px + 64px nav padding > 320px). Fixed: every grid
  (`comp`, `block`, `token`, `icon`) now uses `minmax(min(Npx, 100%), 1fr)` so a column can shrink
  to fit instead of forcing scroll; `.ml-search` uses `width: min(260px, 100%)`; a new
  `@media (max-width: 600px)` block tightens header/nav/main/footer padding to 16px and drops the
  search box to a full-width row below the tabs instead of squeezing into it.
- `src/App.tsx` had a handful of `Box` cards using a literal `width={220}` (Gestalt has no
  built-in responsive `width`, only fixed-pixel or `maxWidth`) as a top-level grid-cell child,
  which — unlike `maxWidth` — can't shrink below its set value and will spill into the next grid
  cell. Fixed three real instances: `SideNavigationDemo`, `StickyDemo`, `AdminSidebarBlock` — all
  switched `width={220}` → `maxWidth={220}`. `LetterboxDemo` is a genuine exception: Gestalt's
  `Letterbox` component requires a literal pixel `width` prop for its aspect-ratio math (confirmed
  in `Letterbox.d.ts` — not optional, can't be `maxWidth`), so instead of leaving it fixed at 220,
  it was shrunk to 160 to comfortably fit the smallest grid cell instead. The rest of the
  `width={N}` occurrences in `App.tsx` were checked individually and are safe as-is — either small
  decorative elements (avatars, icon swatches) or already inside a `Flex wrap` container that lets
  them shrink/reflow rather than force page-level overflow.

Re-verified after the fix: `npm run typecheck` clean, `npm run build` clean, and
`dev-verify-headless.mjs` (extended this pass to also click through the Brand Kit tab and all
three Blocks sub-tabs, including asserting the Not-built list actually renders real item names)
— all green. Two pre-existing console warnings remain (`Invalid DOM property fetchpriority`) —
traced to Gestalt's own compiled `Image` component (`node_modules/gestalt/dist/gestalt.js`), not
anything in this repo's own code; not fixable from here without patching a vendored dependency.
**Still unverified in an actual browser at real device widths** — jsdom confirms the DOM renders
and contains the right content, not that it visually looks correct at 375px; that needs a human
looking at a real phone or resized browser window.

## musiclinkd-system: real deployed screen ported in as a Block (2026-08-16, sixth pass)

While the `webapp/` preview build was being debugged this session (see the real-bug fixes below —
stray "screen.json not found" badge, clipped dev-badge text, a completely non-functional device
selector, a stale `?screen=` fallback), one screen — `public_profile_view_venue` — went from an
unfilled template stub to a fully designed screen using the real `screen.json` sample data, and
was deployed live to `admin.musiclinkd.com/preview/flows/public_profile/public_profile_view_venue/`
(confirmed live via a fresh fetch after deploy, not just trusted from the deploy log).

That real, now-production screen is ported into `musiclinkd-system`'s Blocks tab as **"Public
profile"** — a new screen-validated Block (`PublicProfileVenueBlock` in `src/App.tsx`), using the
exact same real `screen.json` sample verbatim (Arijit Sen, the "Tum Hi Toh (Unplugged)" portfolio
project, the T-Series A&R Team review) so the React/Gestalt version and the live deployed webapp
screen stay the same real screen rather than two independent guesses at it. Same honest gap
carried over from the webapp build: the screen's own `notes` field claims it renders Venue
extension fields, but the actual sample is Talent-shaped — no Venue-specific fields exist anywhere
in this build to render instead, so this Block shows the real sample as given, not backfilled with
invented Venue fields. Verified: `npm run typecheck` clean, `npm run build` clean, and a headless
render confirmed the new Block actually shows "Arijit Sen" and "Tum Hi Toh" in the rendered DOM,
not just that the tab opens.

## Pink-primary token override, 8 new widget Blocks, token grouping (2026-08-16, seventh pass)

**Primary color: red → pink.** Gestalt's `Button`/`IconButton`/`Icon` `color`/`iconColor`/`bgColor`
prop literal is still `"red"` — verified in `Button.d.ts`/`IconButton.d.ts`, there is no `"pink"`
option in Gestalt's real API, so that prop value is unchanged. What changed is which color that
`"red"` slot actually renders: `frontend/guide/musiclinkd-system/src/styles.css` now overrides the
CSS custom properties Gestalt's compiled CSS reads for the primary/brand semantic slot only
(`--color-background-primary-base/-strong/-weak`, the hardcoded `--color-background-button-primary-active`,
`--color-icon-brand-primary`, `--color-background-brand`) to MusicLinkd's real brand pink (`#EA4C89`,
the same `--ml-accent` already used for this app's own chrome) — verified against Gestalt's compiled
CSS in `node_modules/gestalt/dist/*.css`. Deliberately scoped to exclude the raw
`--color-red-pushpin-*` palette and `--color-background-error-*`/`--color-icon-error` (those also
derive from red-pushpin) so error/danger states stay a real, distinct red rather than also turning
pink. The weak/active shades (`#F28FB4`, `#B23A67`) have no source-of-truth swatch — they're derived
by mixing the base with white/black, documented as derived in the CSS comment, not presented as an
official value. This reverses a prior explicit decision in the same file's comment ("Gestalt demos
are deliberately left untouched") — reversed on direct instruction this pass.

**8 new visual widget Blocks**, added to "Composed patterns" (not the reference-only "Admin widget
directory" tab, which already existed and already mirrors all 27+ widgets as spec text): Track card,
Studio card, Featured studio card, Marketplace card, Opportunity card, Review card, Notification
card, Public reputation widget. Each sourced from a real entry in
`admin/app/(admin)/widgets/default-widgets.ts` (backed by `admin/docs/architecture/Widget_Directory.md`)
— purpose/content-composition fields are real and checked; sample copy (names, prices, counts) is
illustrative, same convention as the rest of this file's Blocks. Deliberately did NOT rebuild
BookingCard (already real as "Booking summary") or duplicate the plain "Track row" — the new Track
card adds artwork + platform badges that row format doesn't have, so it's additive, not a re-skin.
Not built: PersistentAudioPlayer (confirmed dead — `GlobalPersistentAudioPlayerBar.kt` deleted per
git history, already noted in the admin widget directory's own data). Verified: `npm run typecheck`
clean, `npm run build` clean, headless smoke test shows 0 new console errors (2 pre-existing
`fetchpriority` warnings, unrelated), and the compiled bundle was grepped to confirm the new
components' real sample text actually made it into the output.

**Token grouping.** `frontend/guide/styleguide.html`'s `:root` token block already implements almost
all of the requested 8-category architecture (Foundation/Semantic/Layout/Component/State/
Interaction/Responsive/Platform) — Layout/Component/State/Interaction/Responsive/Platform already
had section header comments; only Foundation and Semantic didn't, so two header comments were added
purely as labels (zero value changes). Breakpoint (`--bp-*`), sizing (`--sz-*`), radius (`--radius-*`
/`--r-*`/`--g-r-*`), elevation (`--el-*`/`--g-el-*`/`--gestalt-elevation-*`), border (`--bd-*`),
opacity (`--op-*`/`--gestalt-opacity-*`), z-index (`--z-*`), padding (`--sp-*`/`--space-*`), and
motion easing (`--ease-std`/`--ease-spring`/`--gestalt-ease-*`) all already existed — not
re-added, to avoid adding a 4th parallel token system on top of the 3 (core/`--gestalt-`/`--g-`)
CLAUDE.md already flags as a known, unremediated duplication problem. The one genuine gap: no
column/grid-count tokens existed (the real `@media` rules hardcode 1/2/3/4 columns inline) — added
`--grid-cols-mobile/-tablet/-desktop/-wide` (values 1/2/3/4, matching what those real rules already
use). `webapp/shared/tokens.css` was NOT re-synced this pass — it only ever mirrored the older
space/radius/opacity/duration/type/color/shadow subset, not the newer `--gestalt-`/`--g-`/`--layout-`
/`--comp-`/`--state-`/`--int-`/`--bp-`/`--plat-`/`--grid-cols-` additions; that gap predates this
pass and is flagged here rather than silently left unmentioned.

No Tailwind or GSAP dependency was added — this repo has zero existing Tailwind config and zero
GSAP usage; introducing either would itself be the kind of duplicate/parallel system CLAUDE.md
already flags. Confirmed with the user before proceeding.

## styleguide.html: pre-existing diff investigated, real components ported in, fifth pass (2026-08-16)

Direct request to start bringing musiclinkd-system's real components/blocks into `styleguide.html`
itself, plus to investigate the 555-line uncommitted diff already sitting in that file before
touching it further. Both done — findings below, not glossed over.

**The uncommitted diff was real, dated, legitimate work — not corruption.** Read the entire diff
(all 690 lines, `git diff -- frontend/guide/styleguide.html`). It's a careful CSS bug-fix + dedup
pass, self-documented with dated inline comments (2026-08-12/13, predating this session): several
`.sidebar`/`.top-nav`/`.main`/`.mobile-bottom-nav`/`.floating-pill-nav` rules were unconditional
(no media query) and silently competing with the correctly-scoped versions elsewhere in the file —
e.g. an unconditional `.sidebar{width:var(--sw)}` was picking up the tablet tier's `--sw:0px` and
collapsing the sidebar to zero width; an unconditional second `.top-nav` (floating pill variant)
was permanently hiding the real banner nav at every viewport, not just mobile. Each removal has a
comment explaining exactly what it competed with and why it lost. Also deduped a genuinely
duplicated content pair (`product-philosophy`, `lay-grid` existed twice; the `25 Effects`/`26
Gen-Z`/`27 More` sections existed twice and were consolidated to one location), and updated the
`brandAssets` JS array to the real logo filenames. **Verdict: safe to build on top of, not discarded.**

**Found a genuinely new, much bigger bug while investigating: `.visual-demo` / `.demo-label` /
`.demo-row` / `.demo-grid` — the containers used in ~131 places across nearly every "Live Preview"
block in this entire file — had no real CSS definition anywhere.** `.visual-demo` had only two
stray `min-height`/`max-height` rules left over from an unrelated responsive patch; the other three
classes had zero rules at all. Every demo block in this file has been rendering as unstyled,
unlaid-out plain `<div>`s — no card background/border, no grid/row layout grouping the items
inside — this affects far more of the file than anything I set out to look for. Fixed by adding
real definitions matching this file's own established `.card-s`/`.spec-card`/`.spec-grid` visual
language (`src`-adjacent insertion at the same spot as `.card-s`/`.spec-card`, `styleguide.html`
~line 583). This is the highest-value single fix made to this file this session — it benefits
every existing demo block, not just new ones.

**Two new sections added** (additive only — no existing section touched, no fabricated data used):

- **Admin Panel** (`#admin-panel`, under "15 Developer Documentation", next to Widget Registry) —
  data table, sidebar nav, and top bar, hand-ported from the real Gestalt-backed versions in
  `musiclinkd-system/src/App.tsx` (`AdminDataTableBlock`/`AdminSidebarBlock`/`AdminTopBarBlock`)
  into this file's own static `.card-s`/`.token-table`/`.btn` idiom — same seed rows (Ava Chen,
  Bloom Studio A, Ravi Nair), same real nav items. This category was genuinely absent from
  `styleguide.html` before today.
- **Not Built** (`#not-built`, new "18 Not Built" nav group) — the same real, checked exclusion
  list now in musiclinkd-system's Blocks → "Not built" sub-tab, ported here too so the honesty
  registry lives in both places, not just one.

**Verified, not assumed:** wrote a script checking tag balance (`<section>` 256/256 balanced,
my 2 new sections individually balanced at div-level), both new `href="#admin-panel"` /
`href="#not-built"` nav links resolve to real ids, and all 5 inline `<script>` blocks still pass
`node --check` after the edits. One honest caveat carried over from the pre-existing diff, not
introduced by this round: the whole-file `<div>` count is off by 9 (2787 open / 2796 close) and 39
nav `href="#x"` targets across the file don't resolve to any `id="x"` (e.g. `#lay-grid`, several
`#anatomy-*`/`#c2-*`/`#fm-*`/`#p-*` links) — both pre-exist this session's edits (confirmed neither
figure moves when my 3 insertions are excluded from the count) and are follow-up cleanup work, not
something silently introduced here.

**What's still not done, scoped honestly:** the user's original ask was "recreate it" — hand-port
the *rest* of musiclinkd-system's real components/blocks (not just the admin ones) into this file.
That's a much bigger job than this pass covered: e.g. the placeholder booking data ("North Room,"
₹7,200, Oct 15) that musiclinkd-system replaced with the real `booking_summary/screen.json` seed
(Yash Raj Sound Studios, ₹32,000, Aug 12) is duplicated across at least 5 separate demo instances
in `styleguide.html`, each in different surrounding context (nav preview mockup, typography
specimen, dialog demo, notification stack, MusicLinkd Components section) — not one canonical
section to update. Doing that properly means going through each of the ~90 combined
components/blocks individually, verifying real backing, and updating in place rather than adding
more duplicate sections (which would make this file's existing duplication problem worse, not
better). Flagged as a large, multi-session follow-up, not attempted wholesale in this pass.

## Real overlap bugs from screenshots, fourth pass (2026-08-16)

The repo owner sent real screenshots of the app running in their own browser showing three
concrete visual bugs — a genuine step up from the earlier jsdom-only verification, which can
confirm the DOM renders but not that it looks right. Diagnosed and fixed two of the three by
reading the actual layout, not by guessing:

- **Section tabs bleeding into Admin data table.** `SectionTabsBlock`'s card has a deliberate
  `maxWidth={340}` (for consistent card sizing in its row), but Gestalt's `Tabs` doesn't wrap its
  row, and this block's 4 real section names (`Overview`, `Services`, `Team & Works`, `Reviews &
  Gallery` — taken from real business-profile screens) need more than the ~300px this card has
  inside its padding. With plain CSS `overflow: visible` (the default), that extra width didn't
  grow the card — it rendered past the card's own right edge, on top of whatever card sat next to
  it in the grid. Fixed by wrapping the `Tabs` in `<Box overflow="scrollX">` so it scrolls inside
  its own card instead of spilling into the neighbor, plus a general `min-width: 0` added to every
  grid-item child of `.ml-comp-grid` / `.ml-block-grid` / `.ml-token-grid` in `styles.css` as a
  defensive rule against the same class of bug elsewhere.
- **Availability calendar's SegmentedControl wrapping mid-word** ("timelin" / "e", "monthl" /
  "y"). Its header row squeezed a text label and a live 3-segment `monthly`/`weekly`/`timeline`
  control side by side (`justifyContent="between"`) inside the same 340px-capped card, leaving the
  control only ~150-180px — not enough for three real state names next to each other. Fixed by
  stacking the label above the control instead of beside it, so the control gets the card's full
  inner width on its own row.
- **ButtonSocial / TapArea text overlap in the Components tab, row 2** — looked directly at both
  demos' source (`ButtonSocialDemo`, the `TapArea` entry in `DEMOS`): neither has a fixed width,
  an explicit `position`, or anything else that would explain content jumping two columns over.
  Couldn't reproduce a concrete cause from source alone, and this session has no working Chrome
  connection to inspect the live DOM directly (extension not connected). Said plainly: **not
  confirmed fixed.** If it's still there after this rebuild, the next step is a fresh, zoomed-in
  screenshot of just that one card pair, or a live browser connection so the actual computed
  layout can be inspected instead of guessed at.

Re-verified: `npm run typecheck` clean, `npm run build` clean, `dev-verify-headless.mjs` all
green (still can't visually confirm the specific pixel-level fix without a real browser — this
round's honesty gap is explicitly the third bug above, not glossed over).

## Coverage audit (2026-08-16, second pass)

Prompted by a direct challenge to double-check rather than reuse earlier claims: cross-checked every real named export in `gestalt/packages/gestalt/src/index.ts` (the actual source of truth) against what's demoed in `musiclinkd-system`, programmatically, not from memory. Found 4 real components that existed in the installed package but were silently absent from **both** the demo set and the "not demoed, on purpose" disclosure — meaning the disclosure itself was inaccurate, not just incomplete:

- **BannerOverlay** — now demoed (Feedback category). Real component, real `offset`/`primaryAction`/`onDismiss` props, matches Gestalt's own doc example. Renders `position: fixed` in its real source, so — same as Modal/Popover/Toast elsewhere in this app — it's shown behind a "Show BannerOverlay" trigger rather than mounted open, or it would cover the whole page.
- **SideNavigation** — now demoed (new **Navigation** category). Real `SideNavigation.Section`/`.TopItem` subcomponents, usage pattern taken directly from Gestalt's own `docs/examples/sidenavigation/activeItemExample.tsx`.
- **Sticky** — now demoed (Navigation category). Adapted from Gestalt's own `docs/examples/sticky/main.tsx` — a scrollable box with two children that pin on scroll.
- **Layer** — checked and genuinely has no standalone visual output (it's a `React.createPortal` wrapper with zero rendered styling of its own — Modal/Popover/Toast use it internally). Left undemoed, but now correctly named in the "not demoed" disclosure, which is where it should have been from the start.

Component count: 57 → 60. Category count: 7 → 8 (added Navigation).

## Bottom navigation bar (2026-08-16)

Added a real "Bottom navigation bar" block to the Blocks tab — a 5-item mobile tab bar (Home, Discover, Upload, Inbox, Profile) built from real `TapArea` + `Icon` + `Text`, with live active-tab state. Gestalt has no dedicated bottom-nav component (same as it has no dedicated top-nav-bar component); this is the real composition pattern, using real icon names verified against the installed icon set (`home`/`home-fill`, `compass`/`compass-fill`, `add-circle`, `speech-ellipsis`/`speech-ellipsis-fill`, `person`/`person-fill`). The active-tab indicator dot uses this app's own `--ml-accent` (MusicLinkd's real brand pink), not Gestalt's `brandPrimary` icon color (which resolves to Gestalt's own red) — consistent with the red/pink split described above.

## On Gestalt's typeface, for the record

Checked directly, since it's easy to assume otherwise: Gestalt does **not** ship a custom webfont. Its real, compiled CSS (`--font-family-default-latin` in `variables.css`) resolves to a native system-font stack — `-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, ...` — the same approach used for `musiclinkd-system`'s own chrome (`styles.css`). Inter Tight / Inter only appear in `brandkit/index.html`, where they're correct: those are MusicLinkd's own documented brand typefaces (`brand/README.md`), not Gestalt's, and the two are intentionally kept separate.

## `brandkit/` — Brand Kit reference page

A static HTML page (`brandkit/index.html`) assembling MusicLinkd's already-approved brand assets and documented values — logo SVGs from `brand/assets/`, the two real brand colors (`#EA4C89` primary, `#0D0B26` dark) and two real typefaces (Inter Tight / Inter) from `brand/README.md`, and the voice/positioning copy quoted directly from that file.

The Logos section is **generated, not hand-typed** — run `node generate.mjs` inside `brandkit/` to regenerate it from a real `fs.readdirSync` of `brand/assets/`. Two sets, confirmed with the repo owner directly (not guessed): **Logo** (the 6 wordmark/lockup/badge files) and **App icon** (the 2 mark-only files, `favicon-mark.svg` + `mark-grey.svg`).

Corrected on a second pass (2026-08-16) after the first version made a call it shouldn't have: it auto-guessed which background each logo file "needs" from counting `fill="white"` vs `fill="#hex"` occurrences. Checked more carefully — `logo-lockup-black.svg` has 11 white paths and 1 dark path, contradicting its own filename; multi-path lockups mix fill colors across letterforms in ways a script can't resolve to "this file is X color" without actually rendering it. The Logo set is now shown on **both** light and dark backgrounds side by side instead of asserting one. The App icon set is different — each file is a single path with a single fill, so there's no ambiguity — and those two are inlined live with a working color picker (`currentColor` + a `color` CSS override), so you can actually recolor them, not just read a claim that "SVGs can be recolored."

**Said plainly:** there's no AI image-generation tool available in this environment, so this is a coded reference page, not an AI-generated brand board — it doesn't invent a color palette beyond the 2 colors actually documented, doesn't invent new logo variants, and doesn't include any new photography or illustration. Everything shown is a file or value that already existed in this repo.

## `skills/` — reference copies of 4 Claude skills

Vendored (not modified) copies of four Claude skill packages, for reference/documentation purposes alongside the design system:

| Skill | What it's for |
|---|---|
| `skills/design-taste-frontend/` | Anti-slop frontend design guidance — reads a brief, picks a real design direction, avoids templated-looking output. Used to build `musiclinkd-system/`. |
| `skills/web-design-guidelines/` | Checklist/reference for reviewing UI code against web interface best practices (accessibility, UX conventions). |
| `skills/skill-creator/` | Full toolkit for creating and iterating on Claude skills — includes eval scripts, grading agents, and a review-viewer (`eval-viewer/`). |
| `skills/skill-development/` | Guidance on skill structure, progressive disclosure, and best practices for organizing skill content. |
| `skills/tailwind-design-system/` | Guidance for building design systems and component libraries with Tailwind CSS v4. |

These are straight copies of what's installed in the authoring environment as of 2026-08-14 — "update" here means re-copying if the source skill changes; there's no live sync.

## Completeness check (2026-08-14)

Verified against the live site, not just assumed:

- Every URL requested — `about_us`, `designers`, `developers/contributing/development_process`, `developers/contributing/experimentation`, `developers/tooling/web`, `developers/eslint_plugin` — has its exact source file already sitting in `gestalt/docs/markdown/` and `gestalt/docs/pages/` (e.g. `docs/markdown/get_started/developers/contributing/development_process.md`). These pages are generated from that source; nothing was missing.
- "Gestalt 2.0": the live `gestalt.pinterest.systems` root is a new marketing homepage, and its own "Get Started / Foundations / Components" links all point to `/v1/...` — the same documentation and component library already vendored here under `gestalt/`. There's no separate "2.0" component source to fetch; it's the same open-source library (Apache-2.0, `pinterest/gestalt`) behind a new front page.
- Icons, illustration graphics, and iconography assets that are part of the actual open-source repo (`docs/graphics/`, 4MB) were already included in the original clone.

**Deliberately not downloaded:** the new homepage's lifestyle/stock photography (hosted on Pinterest's Sanity CMS, e.g. `cdn.sanity.io/.../gestalt_staging_replica.../...jpg`). That's proprietary marketing photography for Pinterest's own site, not part of the Apache-2.0-licensed design system — different license status, different purpose. Happy to reconsider if you specifically need it for something, but it's not "the design system" in the sense everything else in this folder is.

## Bug audit (2026-08-15)

Ran a real audit — TypeScript type-checking (esbuild normally strips types without checking them, so this was previously never verified), an independent fresh-eyes code review pass, and a headless stress test (every button, every tab, both themes). Found and fixed five real bugs:

1. `Accordion` demo used the base (static, non-collapsible) component while claiming to be collapsible — it did nothing when clicked. Rebuilt using the real `Accordion.Expandable` with controlled state; verified by simulating a click and confirming the content actually swaps.
2. An invalid icon name (`"dollar-sign"`, which doesn't exist in Gestalt's icon set) on that same Accordion.
3. An invalid `Box` color value (`"brandPrimary"` isn't in `Box`'s color union — the real value is `"brand"`) in the Foundations spacing-scale bars.
4. A dead token category (`--sema-color-`) in the token parser that doesn't exist anywhere in the actual shipped CSS — silently produced zero results every time. Removed.
5. `IconCompact` was imported but never used anywhere — given a real demo card instead of being dead code.

Also caught during the same pass: the "Cover art gallery" block was using a plain `Flex`-wrap grid while the UI's own text claimed it demoed `Masonry` (Pinterest's real layout algorithm) — it didn't. Rebuilt with the actual `Masonry` component. Component count updated 56 → 57 to reflect the `IconCompact` addition.

Verification method, stated plainly: added a `tsconfig.audit.json` and a `typecheck` script (`npm run typecheck` inside `musiclinkd-system/`) — run it anytime, since the esbuild build itself does not type-check. Also ran a headless DOM stress test that clicks every button on every tab in both light and dark mode. Zero errors after fixes. Still not a substitute for a human looking at it in a real browser.

## What was NOT done

- `styleguide.html` and its existing sibling folders (`tokens/`, `icons/`, `components/`, `brand/`, etc.) — untouched, verified by checksum before and after every change in this session.
- Nothing outside `frontend/guide/` and the one `admin/` dependency install (see `gestalt-library.md`) was touched.
- **Not pushed to GitHub.** See the note in the chat response for exactly why — short version: this sandbox can create files but is blocked from deleting/overwriting locks, and has no GitHub credentials configured, so both `git commit` and `git push` are currently impossible from here.
