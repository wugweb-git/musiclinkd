# Icons — Gestalt (Pinterest)

423 SVG icons (398 standard + 25 `compact/`), extracted verbatim from
[github.com/pinterest/gestalt](https://github.com/pinterest/gestalt)
(`packages/gestalt/src/icons`), Apache License 2.0. Full license text:
[github.com/pinterest/gestalt/blob/master/LICENSE](https://github.com/pinterest/gestalt/blob/master/LICENSE).

This is now the canonical icon family for new MusicLinkd work (see
`../README.md`). Existing Lucide icon usage already shipped in
`styleguide.html`/`merge3.html` has **not** been swapped in this pass — that's
a separate, larger migration (every icon reference across ~11,000 lines of
markup) tracked as a backlog item, not attempted here.

## Format

- 24×24 viewBox, single `<path>`, no `fill`/`stroke` attributes set — inherits
  `currentColor` via CSS (`color: var(--pink)` etc.) same as the existing
  Lucide usage pattern.
- `compact/` holds Gestalt's smaller/simplified variants (e.g.
  `compact-chevron-down.svg`) for dense UI (table cells, inline badges).
- `manifest.txt` / `compact/manifest.txt` list every icon name (filename
  without extension) — grep these before hand-writing an icon reference.

## Usage

```html
<span class="icon" aria-hidden="true">
  <!-- inline the SVG, or reference via <img src="icons/heart.svg"> for non-tinted contexts -->
</span>
```

For tinted/interactive icons (buttons, active nav, etc.), inline the SVG
markup directly so `currentColor` picks up the surrounding text color —
matches how `--pink` focus/active states already work elsewhere in the guide.

## Naming

Gestalt's naming is kebab-case and descriptive (`arrow-up-right.svg`,
`check-circle-fill.svg`, `add-pin.svg` — a few are Pinterest-specific like
`pin` mapping to a generic "pin" concept elsewhere). Search `manifest.txt` for
the closest match before assuming an icon doesn't exist.
