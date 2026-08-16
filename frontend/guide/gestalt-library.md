# Gestalt Library — Download Manifest

> See [`INDEX.md`](./INDEX.md) for the full catalog of everything in this folder (this file, `musiclinkd-system/`, and `skills/`).

New, separate design-system library. Does not touch or replace the existing `frontend/guide/styleguide.html` or its supporting folders (`tokens/`, `icons/`, `components/`, etc. at the `frontend/guide/` root) — those are untouched.

- Source: https://github.com/pinterest/gestalt
- Commit cloned: `22874a7522d1803df992fae2bcb31ef42be29519` (2026-08-06)
- Package version: `177.0.13`
- License: Apache-2.0 (see `gestalt/LICENSE`)
- Clone method: `git clone --depth 1` (full current source tree, no git history — kept out of this repo's `.git`)
- Local path: `frontend/guide/gestalt/`
- Total size on disk: 34M, 3,975 files

## What's inside `frontend/guide/gestalt/`

| Path | What it is | Size |
|---|---|---|
| `packages/gestalt/` | Core React component library — 108 top-level components (Box, Button, Modal, SideNavigation, Masonry, Dropdown, etc.), 1,056 files total | main package |
| `packages/gestalt-design-tokens/` | Design tokens: color, spacing, typography, elevation, motion — 67 token files across `classic/`, `vr-theme/`, `vr-theme-web-mapping/` | 92 files |
| `packages/gestalt-charts/` | Chart components (ChartGraph) built on Gestalt | 28 files |
| `packages/gestalt-datepicker/` | DateField, DatePicker, DateRange components | 28 files |
| `packages/gestalt-codemods/` | Automated codemods for upgrading between Gestalt versions | 56 files |
| `packages/eslint-plugin-gestalt/` | ESLint rules enforcing Gestalt usage conventions | 266 files |
| `packages/stylelint-plugin-gestalt/` | Stylelint rules for Gestalt CSS conventions | 13 files |
| `packages/gestalt/src/icons/` | 423 SVG icons | 423 files |
| `docs/` | Full documentation site source (Next.js) — 324 files, 87 component doc pages under `docs/pages/web/` | 324 files |
| `rfcs/` | Design/engineering RFCs behind Gestalt's component decisions | — |
| `CHANGELOG.md` | Full version history | — |
| `README.md`, `LICENSE`, `CODE_OF_CONDUCT.md` | Project docs | — |

## Component inventory (108 top-level components)

Accordion, AccordionExpandable, ActivationCard, Avatar, AvatarGroup, AvatarGroupCluster, Backdrop, Badge, BannerCallout, BannerOverlay, BannerSlim, BannerUpsell, BannerUpsellForm, Box, Button, ButtonGroup, ButtonLink, ButtonSocial, ButtonToggle, Caret, Checkbox, Collage, Column, ComboBox, Container, Datapoint, Divider, Dropdown, DropdownItem, DropdownLink, DropdownSection, Fieldset, Flex, FlexItem, Heading, HelpButton, Icon, IconButton, IconButtonFloating, IconButtonLink, IconCompact, Image, Indicator, Label, Layer, Letterbox, Link, List, ListItem, Mask, Masonry, MasonryV2, Modal, ModalAlert, NumberField, OverlayPanel, PageHeader, Pog, Popover, PopoverMessage, Pulsar, RadioGroup, RadioGroupButton, ScrollBoundaryContainer, ScrollFetch, SearchField, SearchGuide, SearchGuideLink, SegmentedControl, SelectList, SheetMobile, SideNavigation (+ Group/NestedGroup/NestedItem/Section/TopItem), Spinner, Status, Sticky, Table (+ subcomponents), Tabs, Tag, TapArea, Text, TextArea, TextField, Toast, Tooltip, Video, and more — full list in `packages/gestalt/src/`.

Plus from the satellite packages: ChartGraph (`gestalt-charts`), DateField / DatePicker / DateRange (`gestalt-datepicker`).

## Stack compatibility — read before wiring into an app

- `gestalt` requires **React ^19.0.0 / React-DOM ^19.0.0** as peer deps.
- `admin/` runs React 19.1.0 + Next.js 15.5.22 → **compatible**, wired in (see below).
- `webapp/` and `frontend/website/` are plain HTML/JS (no React runtime) → Gestalt's React components **cannot run there as-is**. This library is stored here for reference/tokens (colors, spacing, icons can be read and ported to CSS manually) but is not "installed" into those two.
- `admin/` already uses shadcn/ui + Tailwind v4 + Base UI for its own component layer. Gestalt ships its own CSS (`gestalt/dist/gestalt.css`) and its own visual language — running both systems in the same app risks class/style collisions and a mixed visual identity. This install makes Gestalt *available* in admin; it does not migrate admin's existing UI to it.

## What was wired into `admin/`

- `gestalt` added to `admin/package.json` (`^177.0.12`) and installed — 18 packages added, 0 new vulnerabilities (the 9 flagged by `npm audit` all pre-exist in `next`/`firebase-admin` transitive deps, unrelated to this install).
- `admin/components/providers.tsx`: wrapped the existing provider tree in `ColorSchemeProvider` and `DefaultLabelProvider` from `gestalt`, so any Gestalt component can now be imported and used anywhere in the app. Nothing else in that file changed. `tsc --noEmit` passes clean.
- **Deliberately not done:** `gestalt/dist/gestalt.css` was not imported globally in `app/layout.tsx`. Gestalt's stylesheet resets and box-model conventions would apply admin-wide immediately and could visually collide with the existing shadcn/Tailwind UI. When you're ready to actually build a screen with Gestalt components, import `gestalt/dist/gestalt.css` in that route (or scope it) rather than globally — that's a call worth making deliberately, not one I wanted to make silently on your behalf.

## Dynamic UI library — `frontend/guide/musiclinkd-system/`

A second, separate deliverable: a genuinely live, interactive component/token/icon browser branded "MusicLinkd System" — not a static mockup. It's a standalone React app (own `package.json`, own `node_modules`, bundled with esbuild) that imports the *real* `gestalt` package and the *real* shipped design-token CSS, so what you see is exactly what the library ships, not hand-copied values or screenshots.

- **Components tab** — ~28 real Gestalt components (Button, TextField, Checkbox, Switch, Tabs, Modal, Popover, ComboBox, RadioGroup, and more), grouped by category, each genuinely interactive (click a button, type in a field, toggle a switch — real React state, not decoration).
- **Tokens tab** — color/spacing/font/elevation/opacity tokens parsed live at runtime from the actual `gestalt-design-tokens` CSS shipped in the npm package (`variables.css`), rendered as real color swatches.
- **Icons tab** — same 423 icons, searchable.
- Light/dark toggle switches the real token CSS file and Gestalt's `ColorSchemeProvider`.
- Search bar filters whichever tab is active.

**To open it:** `frontend/guide/musiclinkd-system/index.html` after running `npm run build` in that folder (already built once; rerun `npm run build` if you edit the source). It needs to be served over HTTP, not opened as a bare `file://` — the token viewer fetches `variables.css` at runtime, which browsers block over `file://`. A one-liner: `cd frontend/guide/musiclinkd-system && python3 -m http.server 8080`, then visit `http://localhost:8080`.

**How this was verified:** No GUI browser was available in the build sandbox, so I could not take a screenshot. I verified it the next best way — rendered the real bundle in a headless DOM (jsdom), simulated clicks across every button on all three tabs (including opening the Modal and Popover), and confirmed zero console errors/warnings and no broken component output. One real bug was caught and fixed this way (an invalid `Heading` `size` prop that silently produced a broken tag). Worth having a human open it once in an actual browser to confirm the visual layout looks right — jsdom doesn't render CSS.

## What was NOT done

- Not pushed to GitHub (local commit only, per instruction).
- Old `frontend/guide/styleguide.html` and its sibling folders left untouched.
- `webapp/` and `frontend/website/` not modified.
