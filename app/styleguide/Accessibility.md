# MusicLinkd Styleguide — Accessibility & Android Guidelines

## Accessibility Mandates
1. **Minimum Touch Targets:** Every interactive button, chip, card, and icon trigger meets the **48.dp x 48.dp** Android accessibility touch target standard.
2. **Contrast Ratios:** Text on `BrandBackground` (`#FCFCFD`) and `Color.White` cards maintains a minimum contrast ratio of 4.5:1.
3. **Screen Reader Semantics:** Every `Image` and `IconButton` provides meaningful `contentDescription` tags.
4. **Dynamic Text Scaling:** UI elements wrap and scale fluidly using `sp` typography units to accommodate system font size scaling.

## Android Native Guidelines
- **Edge-to-Edge:** Native `enableEdgeToEdge()` execution with proper `WindowInsets.systemBars` inset handling.
- **Adaptive Screen Layouts:** Responsive multi-column grid layout for tablets and ChromeOS foldables.
- **Deep Links:** Android App Links configured for `app.musiclinkd.com`.
