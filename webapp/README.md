# MusicLinkd — PWA product reference build

Not the production app. A screen-by-screen reference: real flow/screen
mapping, real schema-shaped sample data, running as an installable PWA so
it opens full-screen like a native app when added to a home screen —
intended to hand to AI Studio (or anyone designing screens) as a spec that
can't drift out of sync with the actual Firestore schema, because it's
generated from the same source files the schema and admin panel use.

## What's here vs. what's yours to design

**Environment (this, generated/scaffolded):**
- `manifest.json`, `service-worker.js` — installable, standalone display, offline shell
- `shared/app-shell.css` + `shared/app-shell.js` — safe-area handling, 100dvh fixed viewport, no page-level scroll unless a screen opts in, keyboard-aware layout (bottom bar hides when the OS keyboard opens), a bottom-sheet primitive (open/close/transition), service worker registration. **Deliberately zero color/type/spacing/brand tokens** — this project has its own design system (`frontend/guide/tokens/`); layer that on top of this shell per screen.
- `template/index.html` — the structural pattern every screen follows (topbar/content/bottombar regions, one example bottom sheet). Copy this pattern, don't fight it. As of 2026-08-16 this is a real on-brand placeholder shell (back button, screen title read live from `screen.json`, a centered "not designed yet" state using the actual design tokens) instead of a blank page with a stray dev button — it was rendering as an effectively invisible screen in the Flow/Screen Index preview, which is what prompted the fix. All 103 stub screens that existed at the time were regenerated in place to pick up the new shell (see git history) — the scaffolder in `generate.ts` only writes `template/index.html`'s content into screens that don't have an `index.html` yet, so this one-time regeneration was a separate manual pass, not something the normal regenerate step does for you retroactively.
- `webapp/flows/<flow>/<screen>/screen.json` — for every real screen in every flow: flow name, screen name, screen number, states, which Firestore collections it touches, and a **real sample document** for each (pulled straight from the same seed data already loaded into `musiclinkd-7ae30`, not fabricated separately).
- `webapp/flows/<flow>/<screen>/index.html` — auto-scaffolded from `template/index.html` **only if it doesn't already exist** — re-running the generator never overwrites design work.
- `data/flows-map.json` — the full index, consumed by `index.html`'s flow picker.

**Design (yours):** everything visual — color, type, layout inside each `.app-content`, components, animation choices beyond the shell's own transitions.

## Regenerating

```bash
cd admin
npx tsx scripts/webapp-map/generate.ts
```

Pulls live from `admin/app/(admin)/flows/default-flows.ts`,
`admin/app/(admin)/flows/screens/default-screens.ts`, and the seed data
under `admin/scripts/seed/data/`. Safe to re-run any time those change —
screen folders that already have a hand-designed `index.html` are left
alone; only `screen.json` and `_flow.json` are always refreshed.

## Reading a screen's contract

Every screen loads its own `./screen.json` at runtime (see
`shared/app-shell.js`'s `loadScreenContract()`) and exposes it as
`window.MusicLinkd.screen`. The current flow/screen/state are published on
`<html>` as `data-flow` / `data-screen` / `data-state`, which is where the
workbench and any automated check read them from. (A floating dev badge used
to paint this into the corner of every screen; it was removed on 2026-08-29
because it sat on top of real UI and made screenshots useless for review.)
Preview a specific state with `?state=<name>` in the URL, e.g.
`otp_verification/index.html?state=error`.

### Rendering a state — `data-state`, not per-screen JS

`applyState()` drives the whole thing off markup, so a screen needs no
JavaScript of its own:

- **`data-state="a b c"`** on any element — shown only in those states
  (space-separated list), hidden otherwise. Elements without the attribute
  are always shown, so a screen only marks up what actually *varies*.
- **`data-chip="a b"`** on a control that *selects* a state — a category
  chip, a tab. The shell sets its `aria-pressed` from the current state, and
  `.ml-chip` already styles that. Make it an `<a href="?state=x">` and the
  filter works with JavaScript disabled and is deep-linkable.

This replaces the older habit of each screen shipping a click handler that
rewrote inline `background`/`border`/`color`. Those rot silently:
`marketplace_browse`'s queried `.chip` while its markup wrote `.ml-chip`, so
it matched nothing and all six categories rendered identically while the
screen still *declared* eight states. `audit-flow.mjs` now reports declared
states that no `data-state` implements, so that gap is visible rather than
assumed.

Note both attributes take a **list**. Anything comparing the whole attribute
value against one state name will silently mismatch every multi-state
element — a bug `audit-flow.mjs` itself had.

## Known gaps

- **PWA icons are SVG only** (`shared/icon.svg`). Modern Android/Chrome
  accept SVG manifest icons; iOS's "Add to Home Screen" wants a real PNG
  `apple-touch-icon` for full fidelity pre-iOS 16. Swap in real exported
  PNGs (180×180 at minimum) before this goes to an actual client.
- **No live Firestore connection, on purpose** — every `sample` in every
  `screen.json` is realistic and schema-accurate but static, generated at
  build time from the seed data. Wiring live reads is a deliberate later
  step, not an oversight — see the flow/screen `notes` fields for which
  screens are real-data-backed in the actual app vs. still mocked there
  too.
- **Flows with zero screens** (`opportunities`, `equipment_rental`,
  `subscription`, `recommendation_engine`) have a `_flow.json` but no
  screen folders — confirmed to have no real UI as of the 2026-08-12
  audit (see each flow's `description` in `default-flows.ts`). Don't
  invent placeholder screens for these; if that changes, re-run a real
  audit first.


## Regenerating `data/designed-screens.json`

The Flow/Screen Index badges each row **Designed** or **Stub**. That comes from
`data/designed-screens.json`, which is a static list, so it goes stale the
moment you design a screen without regenerating it. From `webapp/`:

```bash
python3 - <<'EOF'
import os, json
designed = []
for flow in sorted(os.listdir('flows')):
    fp = os.path.join('flows', flow)
    if not os.path.isdir(fp): continue
    for scr in sorted(os.listdir(fp)):
        idx = os.path.join(fp, scr, 'index.html')
        if not os.path.isfile(idx): continue
        if 'THIS FILE IS A TEMPLATE, NOT A DESIGNED SCREEN' not in open(idx, encoding='utf-8').read():
            designed.append(f"{flow}/{scr}")
json.dump({"_note": "Regenerate with the snippet in webapp/README.md.",
           "designed": designed}, open('data/designed-screens.json', 'w'), indent=2)
print(len(designed), "designed")
EOF
```

The marker string is the one the scaffolder writes into every fresh template,
so a screen counts as designed the moment that line is removed.
