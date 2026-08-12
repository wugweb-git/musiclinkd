# Cards

Cards drive discovery and conversion. Every card conforms to the unified base container specification.

**Interactive reference:** `guide/styleguide.html` § Cards — live previews of all 9 card types with anatomy, tokens, and state specs.

## Canonical card types

| Card | Purpose | Key elements |
|---|---|---|
| Artist Card | Primary discovery unit for talent | Real photo, waveform, genre chips, Hire/Message CTAs |
| Studio Card | Recording venue and rehearsal space | Studio image, gold verified, star rating, equipment, hourly rate, Book CTA |
| Marketplace Card | Gear, instruments, equipment | Product image, price, condition, seller, Inquire CTA |
| Project Card | Collaboration sessions and gigs | Title, creator, seeking roles, budget, deadline, Apply CTA |
| Opportunity Card | Open auditions and freelance calls | Title, organization, compensation, tags, Submit CTA |
| Review Card | Client testimonials | Reviewer avatar, star rating, review text, date |
| Booking Card | Studio reservations | Time slot, status (Confirmed/Pending), price summary |
| Notification Card | Push notification items | Unread dot, title, message, timestamp |
| Message Card | Chat list items | Avatar, name, preview, time, unread count badge |

## Container specification

```
Card
  shape: RoundedCornerShape(24dp)
  border: 1dp solid #ECECF1
  background: #FFFFFF
  padding: 16dp–20dp
  min touch target: 48dp
```

## Implementation rules

- One anatomy specification per type — reuse, do not fork.
- Use real photography from Unsplash, not illustrations or emoji.
- Each card has one primary CTA (hire, book, inquire, apply).
- Use approved tokens only — see `tokens/tokens.json`.
- Zero shadow noise. Rely on the 1px border for definition.