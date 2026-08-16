# /guide — Index

Single entry point for everything living under `frontend/guide/`. This file is the "library catalog" — everything downloaded or vendored into this folder is listed here with what it is and where it came from.

| What | Path | Status |
|---|---|---|
| Original style guide | `styleguide.html` (+ its sibling `tokens/`, `icons/`, `components/`, `brand/`, etc.) | **Untouched** — nothing here was modified or replaced. |
| Gestalt design system (vendored source) | `gestalt/` | Added — see below |
| MusicLinkd System (live UI library) | `musiclinkd-system/` | Added — see below |
| Claude skills (reference copies) | `skills/` | Added — see below |
| This file | `INDEX.md` | Added |
| Full Gestalt download manifest | `gestalt-library.md` | Added (superseded by this file as the top-level entry point, but kept — it has the detailed package-by-package breakdown) |

## `gestalt/` — Pinterest's Gestalt design system

Full fresh clone of [pinterest/gestalt](https://github.com/pinterest/gestalt) (v177.0.13, commit `22874a7`) — components, design tokens, icons, docs source, RFCs. 3,975 files, 34MB. Open `gestalt/index.html` for a static, linkable catalog of everything in it.

Full detail: [`gestalt-library.md`](./gestalt-library.md).

## `musiclinkd-system/` — live, dynamic UI library

A separate, standalone React app (own `package.json`/`node_modules`, bundled with esbuild) that renders the *real* Gestalt components, the *real* shipped design tokens, and the *real* icon set — genuinely interactive, not a mockup. Branded "MusicLinkd System." Five tabs:

- **Components (57 cards)** — every practically demoable Gestalt component, real and interactive. The handful deliberately left out (Video, Collage/MasonryV2, search-index-specific pieces, infra-only providers/hooks, Container) are listed by name at the bottom of that tab, with why.
- **Blocks (8 patterns)** — composed, realistic screens built from those same components: navigation header, artist profile card, track row, upload form, stats row, empty state, notification stack, and a cover art gallery using the real `Masonry` component (Pinterest's own packing algorithm, not a plain grid standing in for it).
- **Foundations** — typography scale, rounding scale, elevation/shadow scale, spacing scale, and icon sizes, all rendered live from the real components and real tokens (not a static description of them).
- **Design Tokens** — the raw token dump (colors, spacing, fonts, elevation, opacity), parsed at runtime from the shipped CSS.
- **Icons** — all 423, searchable.

To run: `cd frontend/guide/musiclinkd-system && npm run build`, then serve the folder over HTTP (`python3 -m http.server`) and open `index.html`. It needs HTTP, not `file://`, because it fetches the token CSS at runtime.

**Verification note:** same as before, no GUI browser in this sandbox, so this was verified functionally (headless DOM render, every button on all 5 tabs clicked, zero console errors) rather than visually. Worth a human glance in a real browser.

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
