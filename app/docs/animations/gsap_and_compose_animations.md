# GSAP Web & Jetpack Compose Native Animations Specification — MusicLinkd

## 🌐 1. Web Target (`musiclinkd.com`): GSAP Animation Architecture

For the public website (`musiclinkd.com`), GSAP (GreenSock Animation Platform) and ScrollTrigger drive fluid editorial motion, hero section transitions, and interactive synergy preview graphics.

```javascript
// GSAP Hero Title & Synergy Graphic Entrance
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';

gsap.registerPlugin(ScrollTrigger);

export function animateLandingHero(heroElement, cardGridElement) {
  const tl = gsap.timeline({ defaults: { ease: 'power3.out', duration: 1.2 } });

  tl.fromTo(heroElement, 
    { opacity: 0, y: 40 }, 
    { opacity: 1, y: 0 }
  ).fromTo(cardGridElement, 
    { opacity: 0, scale: 0.95 }, 
    { opacity: 1, scale: 1, stagger: 0.15 }, 
    "-=0.8"
  );
}
```

### GSAP Interaction Matrix (`musiclinkd.com`)
- **ScrollTrigger Parallax:** Hero audio waveform background layers scroll smoothly at variable speeds.
- **Synergy Score Radial Counter:** GSAP `TextPlugin` animates synergy percentage numbers from 0% to match score (e.g., 94%).
- **Interactive Card Stagger:** Artist spotlight cards float up with staggering on page load.

---

## 📱 2. Android App Target (`app.musiclinkd.com`): Jetpack Compose Native Motion

For the Jetpack Compose Android application (`app.musiclinkd.com`), native Compose Motion APIs provide 60 FPS / 120 FPS hardware-accelerated animations.

### Native Compose Animation Specs
1. **Waveform Audio Equalizer:** `rememberInfiniteTransition()` animates frequency bar heights smoothly during ExoPlayer playback.
2. **State Transitions (`AnimatedContent` / `AnimatedVisibility`):** Smooth crossfades when toggling filter categories or expanding studio slot sheets.
3. **Pill Action Button Feedback (`animateFloatAsState`):** Tactile scale down on button press (`0.96f`) returning to normal scale (`1.0f`) on release.
4. **Connection Toggle Transition:** Icon transitions from `Add` to `Checkmark` with rotational spring physics (`spring(dampingRatio = Spring.DampingRatioMediumBouncy)`).
