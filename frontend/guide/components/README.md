# Components

Approved components are shared primitives, controls, and composable blocks. Create an implementation and a usage specification together.

**Interactive reference:** `guide/styleguide.html` § Components — live demos of buttons, inputs, chips, search, navigation, audio player, bottom sheets, and empty states.

## Canonical component list

| Component | Purpose |
|---|---|
| Button (primary, secondary, ghost) | CTAs, actions, passive triggers |
| Input Field | Form fields, onboarding, credentials, messaging |
| Chip | Genre/role/filter selectors (pill, toggle) |
| Search Bar | Directory search with category filters |
| Bottom Navigation | Master app tab switching (Home, Discover, Market, Messages, Profile) |
| Audio Player / Waveform | Playable audio moments with animated waveform |
| Bottom Sheet | Modal overlay for confirmations, filters, forms |
| Empty State | Universal template for empty lists and no results |
| Verification Badge | Trust signal for verified artists and studios |
| Status Badge | Availability, confirmation, urgency indicators |

## Implementation rules

- Base container: 24dp radius, 1px `#ECECF1` border, `#FFFFFF` background.
- All buttons: pill shape (9999px), min 44px touch target.
- Use Lucide icons only, 1.75px stroke.
- Maintain one canonical implementation per component.
- Do not create a visual variant when an existing component satisfies the need.