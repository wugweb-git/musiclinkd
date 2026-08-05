# MusicLinkd — GEMINI AI Agent Instructions & Memory

This file contains key directives, architectural patterns, and guidelines for Gemini models working on the MusicLinkd codebase.

---

## 🏛 Core Architectural Rules

1. **Keep Applet Build Passing:** Always verify changes using `compile_applet`.
2. **Respect Design System:**
   - 24.dp card corner radius (`RoundedCornerShape(24.dp)`).
   - 1.dp border stroke (`BorderStroke(1.dp, Color(0xFFECECF1))`).
   - Clean light canvas (`Color(0xFFFCFCFD)`).
   - Pill primary action buttons (`RoundedCornerShape(999.dp)`).
3. **Product Topology:**
   - Website: `musiclinkd.com`
   - App: `app.musiclinkd.com`
   - Admin: `admin.musiclinkd.com`
   - Email: `hello@musiclinkd.com`
4. **Documentation Sync:**
   - Update `update.md`, `CHANGELOG.md`, and relevant files in `docs/` or `styleguide/` whenever making changes.
