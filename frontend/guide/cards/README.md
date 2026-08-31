# Cards

Cards drive discovery and conversion. Every card conforms to the unified base
container specification.

> **Reconciled 2026-08-31.** This file predates the v9 system and several of
> its values had gone stale. Corrections are marked below; the rules that
> survived are unchanged. It is now rendered into `/guide` by
> `scripts/build-guide.mjs`, so this file is the source and cannot drift
> from the catalogue again.

## Container specification

```
shape        --radius-3   (32px)
border       --border-1   solid --color-line
background   --color-paper
padding      --space-3    (16px)
touch target --size-3     (48dp minimum)
```

*Was "RoundedCornerShape(24dp) / 1dp #ECECF1 / #FFFFFF / 16–20dp". The colour
was right (#ECECF1 is `--color-line`); 24dp and the 16–20dp range are not steps
on any scale, so no card could have been built from tokens and matched them.*

## Implementation rules

- One anatomy specification per type — reuse, do not fork.
- **Use the local photography library** under `/assets/img`, bound to a
  component. Never stock.
  *Was "use real photography from Unsplash". The guide carried 94 stock
  references and now carries zero; they were removed deliberately.*
- Each card has one primary CTA (hire, book, inquire, apply).
- Approved tokens only — see `tokens/tokens.json`.
- Zero shadow noise. Rely on the hairline border for definition.

## Canonical card types

| Card | Purpose |
|---|---|
| Artist Card | Primary discovery unit for talent |
| Studio Card | Recording venue and rehearsal space |
| Marketplace Card | Gear, instruments, equipment |
| Project Card | Collaboration sessions and gigs |
| Opportunity Card | Open auditions and freelance calls |
| Review Card | Client testimonials |
| Booking Card | Studio reservations |
| Notification Card | Push notification items |
| Message Card | Chat list items |
