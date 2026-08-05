# Styleguide — Primitives

## Low-Level Design Primitives

### 1. Border Radius
- `RadiusBase`: `24.dp` — Mandatory container corner radius for all cards, modals, and sheets.
- `RadiusPill`: `999.dp` — Used for action buttons and status chips.
- `RadiusSmall`: `12.dp` — Used for internal search inputs and media thumbnail previews.

### 2. Spacing Primitives
- `Space4`: `4.dp`
- `Space8`: `8.dp`
- `Space12`: `12.dp`
- `Space16`: `16.dp`
- `Space24`: `24.dp`
- `Space32`: `32.dp`

### 3. Touch Target Minimum
- All clickable elements enforce a minimum dimensions of `48.dp x 48.dp` to conform to Material 3 accessibility rules.
