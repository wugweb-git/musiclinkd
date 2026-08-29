# MusicLinkd Design System — Rebuild Plan

Status: draft, for review. Written 2026-08-29 against the repo and both live
guide pages directly — every fact below is checked, not assumed.

## The problem, stated precisely

Two things live under `/guide/` right now and they are not the same thing,
despite both claiming to represent "the" MusicLinkd design system:

**`musiclinkd-system`** (`frontend/guide/musiclinkd-system/`) — a React 19
app with `gestalt` (`^177.0.12`) as a real npm dependency. Its own
`package.json` description: *"Live, dynamic UI library — renders real
Gestalt components, tokens, and icons."* Confirmed live: 60 components, 94
blocks, 423 icons, all Pinterest's actual Gestalt library, generically
skinned. No product voice, no MusicLinkd content, no brand identity beyond
a title. This is a Gestalt component browser with a MusicLinkd label on it,
not a MusicLinkd design system.

**`styleguide.html`** (`frontend/guide/styleguide.html`, 8,206 lines,
"296 consolidated sections & screens") — genuinely branded: real wordmark,
pink-accent FAB, product-specific taxonomy (13 music roles, 10 languages,
real instrument/vocal-register catalog for the actual Indian music market),
organized as Product → Brand → Foundations → Primitives → Components →
Patterns. This has the actual product's soul in it.

**The irony:** `frontend/guide/README.md` — the project's own governing
doc — already says this exact thing should not exist:

> "Gestalt is a React library with no native mobile SDK, so it is **never
> installed as a dependency here** — it's a design-language reference,
> reimplemented natively per platform... Do not adopt... the `gestalt` npm
> package as a runtime dependency — it is a reference only."

`musiclinkd-system`'s `package.json` violates that boundary directly. It
isn't a case of "we chose Gestalt and it's wrong for mobile" — it's a case
of the repo's own rules being written correctly and then not followed when
this specific app was built. That's good news: fixing this isn't a
philosophical fight, it's bringing one artifact back in line with a
decision the project already made.

## Why it's genuinely not mobile-capable

- `gestalt` has no native mobile SDK — full stop. It's React-for-web. There
  is no path from "install gestalt" to "ship this on iOS/Android."
- Its component set (Button, TextField, IconButton, ButtonToggle, Pog, etc.
  — see the live screenshot) is web-interaction-shaped: hover states,
  pointer-first, no touch-target sizing discipline, no gesture/haptic
  model. `tokens.json` already forward-declares `foundation.touch`,
  `foundation.safeArea`, `semantic.haptic`, and a whole `platform.app`
  section for exactly this — annotated `"consumesTokensFrom": "Nothing
  yet — no native app exists in this repo."` The token layer is ready;
  nothing built on it is native-shaped yet.
- Visually, it reads as a component catalog, not a phone screen. Compare
  the two screenshots taken for this plan: `musiclinkd-system` is gray
  cards on white in a desktop-width grid; `styleguide.html` has an actual
  sidebar-app shell with brand color, real content hierarchy, and a
  floating action button — much closer to what a *product* looks like,
  even though it's still a web reference, not a native shell either.

## What already exists and is worth keeping (not a rebuild from zero)

- `tokens/tokens.json` (v8.0.0) — this is genuinely good and shouldn't be
  thrown out. 8-category framework (Foundation/Semantic/Layout/Component/
  State/Interaction/Responsive/Platform), documented provenance for every
  value (which real CSS file it was extracted from, when, why), explicit
  `platform.app` reservation for native work. This is the actual source of
  truth and the rebuild should keep extending it, not replace it.
- `admin/docs/architecture/Widget_Directory.md` — 27 audited widgets across
  6+ categories (Discovery & Collaboration, Studios & Venues, Marketplace &
  Opportunities, Portfolio & Projects, Media, ...), each with a real
  code-mapping column. This is the actual component inventory the new
  system needs to implement against — it's already scoped, don't
  re-derive it.
- `webapp/shared/tokens.css` — the simplified CSS subset this session's
  screen-design work has been using (black-primary, pink-accent, the same
  4-value spacing/radius/opacity/motion/type scale as tokens.json). Already
  consistent with tokens.json; the visual-polish primitives added this
  session (`.stat-card`, `.cat-tile`, `.status-pill`, `.hero-photo`,
  `.pill-cta-row`, `.progress-card`) are a small head start on the
  "enhance for native app level" ask and should feed into the real system
  rather than staying webapp-only.
- `styleguide.html`'s actual product content (roles, personas, journeys,
  language/instrument catalog, platform modules) — this is real product
  knowledge, not decoration. Worth extracting even if the HTML file itself
  isn't kept verbatim.

## Direction: what "totally different than Gestalt" means concretely

Not "pick a different component library to skin." The README's own answer
is the right one and this plan just proposes actually doing it:

1. **Gestalt becomes reference-only again.** Remove it as a runtime
   dependency from `musiclinkd-system`. Keep consulting Gestalt's spec for
   spacing/interaction-pattern *ideas* (as `tokens.json`'s
   `foundation.gestalt.*` already does), but ship zero Gestalt code.
2. **One real component layer, hand-built, native-shaped.** Every
   component gets: a minimum 48dp touch target (already in
   `foundation.touch.targetMin`), a pressed/selected state (not just
   hover), no reliance on mouse-only affordances (tooltips-on-hover,
   right-click), and is authored against the Widget Directory's 27 real
   widgets first, generic form controls second.
3. **Platform tokens get consumed, not just declared.** `platform.app`
   (touch/safeArea/haptic) currently has zero consumers. The new system's
   web reference should visibly demonstrate what each token maps to
   (e.g. a real safe-area-inset demo, a real touch-target overlay mode)
   so the native (Compose/SwiftUI) implementation has something concrete
   to match, not just a JSON value.
4. **`styleguide.html`'s aesthetic and product content become the visual
   baseline**, not `musiclinkd-system`'s current gray-card catalog look —
   see migration section below.

## Migration: bringing `styleguide.html`'s values into the new system

Rather than two competing living docs, converge on one:

1. **Audit `styleguide.html` first, don't port blind.** `CLAUDE.md`
   already flags known issues in it (duplicate token systems, an
   unenforced emoji-vs-icon rule, duplicated sections) that predate this
   plan — those need fixing during extraction, not carried over. At 8,206
   lines this needs a dedicated read-through pass before anything is
   copied; don't assume the whole file is clean just because the visual
   direction is right.
2. **Extract, don't relocate.** Pull out: the brand shell (sidebar nav,
   wordmark treatment, section taxonomy), the pink-accent-on-black-primary
   color application (matches `tokens.json`'s already-corrected
   `semantic.action`), and the real product content (roles/personas/
   languages/instruments/journeys) into structured data (JSON/MD) that
   both the web reference *and* a future native app can read — not
   trapped in one HTML file's inline markup.
3. **Rebuild the component catalog view on top of that shell**, populated
   from the Widget Directory + `tokens.json`, replacing
   `musiclinkd-system`'s current Gestalt-rendered grid with hand-built
   MusicLinkd components inside `styleguide.html`'s visual language.
4. **One URL, one canonical reference** at the end of this:
   `/guide/styleguide` (or a renamed successor) stops being "the old one"
   and becomes the only one; `musiclinkd-system` either gets replaced in
   place or retired once parity is reached.

## Native-app-level enhancement — what "way more" concretely covers

Beyond de-Gestalt-ifying: the token layer already anticipates a lot of this
(safe area, haptics, touch targets) but nothing demonstrates it yet.

- Touch target + hit-area audit against `foundation.touch` (48dp min,
  56dp comfortable) for every existing component — Gestalt's web controls
  were never built to this discipline.
- Gesture and dismissal patterns (swipe-to-dismiss, pull-to-refresh,
  bottom-sheet drag) — currently zero MusicLinkd tokens exist for these
  (`interaction.gesture` in tokens.json is explicitly marked "no token,"
  hardcoded in `App.tsx`'s `SwipeDeckBlock`). Needs real motion specs.
- Haptic feedback mapping (`semantic.haptic` already has selection/
  success/waveform entries pointing at Android APIs) — needs a real
  demonstration layer and equivalents documented for iOS.
- Safe-area and notch handling as a visible, testable pattern, not just a
  reserved token.
- Elevation/shadow system for native (Gestalt's `foundation.gestalt.
  elevation` is reference-only and explicitly notes Gestalt drops shadows
  in dark mode in favor of borders — decide MusicLinkd's own rule here).
- Dark mode: `musiclinkd-system` currently has none (removed on purpose
  per `CLAUDE.md`); tokens.json already has a `semantic.action.dark`
  block ready to consume — native apps need this far more than web does
  (OS-level dark mode is a user expectation, not a nice-to-have, on both
  iOS and Android).

## Phased roadmap

1. **Audit** — full read-through of `styleguide.html` (known issues from
   `CLAUDE.md`), confirm current `musiclinkd-system`/Gestalt dependency
   surface (what actually breaks if `gestalt` is removed), reconcile
   against Widget Directory's 27 widgets (which exist today vs. proposed).
2. **Extraction** — pull `styleguide.html`'s brand shell + product content
   into structured, reusable data; keep `tokens.json` as the single token
   source (extend, don't fork).
3. **Rebuild the component layer** — hand-built, Gestalt-free, against the
   Widget Directory, native-touch-target-first, inside the extracted brand
   shell.
4. **Native-pattern layer** — gesture/haptic/safe-area/dark-mode
   demonstrations wired to real tokens, not just declared.
5. **Converge to one canonical reference URL**, retire the duplicate.
6. **Feed back into `webapp/`** — the screen-design work already in
   progress this session should consume the new component layer once it
   exists, replacing today's hand-rolled `tokens.css` primitives.

## Open decisions — need your call before implementation starts

- Full rebuild of `musiclinkd-system` in place, or a fresh app that
  replaces it once parity is reached?
- Keep the `musiclinkd-system` name, or rename now that it's no longer
  Gestalt-based (naming it after what it *is* — a MusicLinkd-native
  component system — rather than what it used to run on)?
- Priority order: is native-pattern work (gesture/haptic/safe-area) more
  urgent than finishing the de-Gestalt-ification, or should they land
  together?
- Given the Codex session active on this repo — should this rebuild be
  this session's work, Codex's, or split by area (e.g. Codex on
  `musiclinkd-system`/tokens, this session on `webapp/` screen consumption)
  to avoid the same collision risk already hit in git today?
