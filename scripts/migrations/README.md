# One-shot migrations

Everything here has already been applied to `webapp/flows`. They are kept
because they are the only record of exactly what changed across 205 screens,
and because each one is idempotent — re-running is a no-op, which makes them
usable as a check that nobody has hand-reverted a fix.

Run them from `webapp/`:

    node ../scripts/migrations/04-codemod.mjs flows --dry

| script | what it did |
|---|---|
| `04-codemod.mjs` | viewport 205 · appbar three-slot 152 · tab role 76 · tablist 10 · verbs 10 · fixes-css link 205 |
| `06-codemod-a11y.mjs` | link-name 12 · toggle-name 24 · qr-role 3 · scroll-focus 11 |
| `13-replace-emoji.mjs` | 262 star characters → 47 `.ml-rating` components |
| `14-fix-states.mjs` | 7 screens reconciled against their own markup, 60 states added |
| `15-triage-states.mjs` | reports which single-state screens actually need loading/empty/error |
| `19-fix-severed-links.mjs` | the 5 anchors that made 5 of 6 journeys unwalkable |
| `21-state-default-hidden.mjs` | `hidden` stamped on 428 non-default state elements across 132 screens |
| `23-fix-imagery.mjs` | 7 watermarked photos + 3 listings whose photo contradicted their title |
| `18-counters.mjs` | the six honest counts (templates / product screens / routes / states / …) |
| `24-type-renumber.mjs` | 416 `--type-N`/`ml-type-N` references renumbered (2→3, 3→4, 4→6) across 194 screens, for the 2026-09-11 type scale (4 steps → 7) |

The generators that must RE-RUN when content changes live one level up, in
`scripts/`: `audit-system.mjs`, `build-registry.mjs`, `build-icons.mjs`,
`build-journeys.mjs`, `build-template-families.mjs`.

## Not applied, and why

- **`05-codemod-css.mjs`** — planned to land alongside the type renumber
  above; superseded by `24-type-renumber.mjs`, which folds both concerns
  (renumbering plus the new-step CSS) into one migration. If `05-codemod-css.mjs`
  is ever found on disk, treat it as dead: do not run it after `24-type-renumber.mjs`
  has already applied.
- **`17-patch-tokens.mjs`** — half of what it wanted is now done: the ceiling
  was raised (108 → 111, 2026-09-11) and `foundation.type` grew 4 → 7 steps
  (12/14/16/22/28/32/40), closing the 44%-of-text-sits-at-12px gap this note
  describes. `foundation.space.5 = 64` is **still not applied** — the top
  space step is still 32, so the gap between sections can still only be twice
  the padding inside a card. That remains a real, open need and its own
  architecture decision, separate from type.
- **`09-build-guide.mjs`** — the multi-page guide emitter. It produces 161 good
  component and widget pages, but its index links to `/guide/foundations/` and
  `/guide/patterns/`, which it never emits, and it drops the Brand, Imagery,
  Motion, Taxonomy and Coverage prose entirely. Swapping it in today would
  regress /guide, so it is held.
