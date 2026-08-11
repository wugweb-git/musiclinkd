# Gemini AI Design Guidelines & Component Specifications

Guidelines for integrating Gemini 2.5 Flash (`gemini-2.5-flash`) into the MusicLinkd user experience:

---

## AI UI Components

1. **AI Recommendation Card**: Surfaced in Discovery with a gradient border (`#EA4C89` to `#0D0B26`) and explicit synergy match score (`94% Match`).
2. **AI Confidence Indicator**: Badge displaying confidence tier:
   - `High Synergy (90-100%)` -> Green Badge
   - `Good Match (75-89%)` -> Pink Badge
   - `Explore Option (60-74%)` -> Neutral Badge
3. **Explainability Box**: Concise statement explaining why a collaborator or studio was suggested (e.g. *"Matched based on Hindustani Vocals genre, Mumbai location, and 4.9★ rating"*).
4. **Thinking & Generation States**: Shimmer placeholder bars (`@keyframes pulseGlow`) with text *"Gemini evaluating collaboration synergy..."*.
5. **Action Controls**: Every AI recommendation MUST feature `Accept`, `Reject`, `Edit Parameters`, and `Rate Suggestion (👍/👎)` controls.
