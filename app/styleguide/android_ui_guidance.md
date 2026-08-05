# Styleguide — Android UI Guidance

## Android Standards
1. **Edge-to-Edge Design:** Use `enableEdgeToEdge()` in `MainActivity` with `Scaffold` or `Modifier.windowInsetsPadding(WindowInsets.systemBars)`.
2. **Material Design 3 (M3):** Standardized M3 components (`Card`, `Button`, `TextField`, `NavigationBar`).
3. **Accessibility:** Touch target padding >= 48.dp, explicit `contentDescription` on vector icons and avatars.
4. **Adaptive Support:** Flexible grid and list layouts for foldables and tablet displays.
