# Motion

Use purposeful motion only: pressed state, gentle card lift, sheet/modal fade and scale, swipeable discovery, audio waveform feedback, and skeleton loading. Respect reduced-motion preferences.

**Interactive reference:** `guide/styleguide.html` § Motion — interactive hover and press demos with live easing.

## Timing

| Duration | Easing | Usage |
|---|---|---|
| 150ms | cubic-bezier(0.4, 0, 0.2, 1) | Button presses, chip toggles |
| 200ms | cubic-bezier(0.4, 0, 0.2, 1) | Hover states, icon changes |
| 300ms | cubic-bezier(0.4, 0, 0.2, 1) | Card lifts, page transitions, sheet reveal |
| 500ms | cubic-bezier(0.4, 0, 0.2, 1) | Modal/dialog fade+scale, splash fade-in |

## Rules

- Use one easing curve for all motion: `cubic-bezier(0.4, 0, 0.2, 1)`.
- Pressed state: scale to `0.97`.
- Hover: lift 2–4px with minimal shadow.
- Skeleton loading: shimmer animation at 1.5s loop.
- Audio waveform: CSS keyframe bounce, staggered delays.
- Always check `prefers-reduced-motion` and disable animations when set.

## Canonical animations

| Animation | Spec |
|---|---|
| Card hover lift | 200ms, translateY(-2px), shadow 0 8px 24px rgba(0,0,0,0.06) |
| Button press | 150ms, scale(0.97) |
| Bottom sheet reveal | 300ms, translateY + fade |
| Modal entrance | 500ms, scale(0.95→1) + fade in |
| Skeleton shimmer | 1.5s infinite linear, background-position slide |
| Waveform bounce | 800ms infinite alternate, scaleY, staggered by 80ms |