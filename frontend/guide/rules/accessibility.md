# Accessibility

Meet WCAG AA contrast, support screen readers and keyboard/focus navigation,
preserve clear focus states, and never communicate critical meaning through
colour alone.

> **Reconciled 2026-08-31.** This file predates the v9 system and several of
> its values had gone stale. Corrections are marked below; the rules that
> survived are unchanged. It is now rendered into `/guide` by
> `scripts/build-guide.mjs`, so this file is the source and cannot drift
> from the catalogue again.

## Measured floors

| Rule | Value |
|---|---|
| Touch target | `--size-3` (48dp) web + Compose, 44pt iOS |
| Contrast, normal text | 4.5:1 |
| Contrast, large text | 3:1 |
| Focus ring | 2px `--color-pink` at 2px offset, `:focus-visible` only |

*Was "keep targets at least 44px" without qualification. 44 is the Apple floor;
web and Android are 48. Both now ship, per platform, from one token source.*

## Standing rules

- Controls smaller than the floor take an invisible 48dp target rather than
  growing — a 32dp chip is the right visual size.
- Text over artwork sits on `--media-scrim`, never on raw imagery.
- A disabled control announces why, not just that it is dimmed.
- Colour is never the only carrier of state — pair it with an icon or text.
