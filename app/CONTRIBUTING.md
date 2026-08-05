# Contributing to MusicLinkd

Thank you for your interest in contributing to **MusicLinkd**! We welcome pull requests, bug reports, and documentation improvements.

---

## 📜 Governance & Definition of Done

All contributions to MusicLinkd must strictly follow our engineering guidelines defined in [`governance.md`](./governance.md).

### Mandatory Engineering Rule:
> "No implementation, bug fix, refactor, feature, UI update or documentation update is complete until `update.md`, `CHANGELOG.md` and all relevant documentation in `docs/` and `styleguide/` have been updated."

---

## 🛠 Local Development Workflow

1. **Branch Naming:** Use feature-focused branch names (e.g., `feature/studio-booking`, `fix/snapshot-fallback`).
2. **Code Style:** Follow Kotlin and Jetpack Compose modern architecture guidelines (M3, 24.dp container radius, state hoisting).
3. **Verification:** Ensure `compile_applet` / Gradle build succeeds clean before submitting a pull request.
4. **Documentation:** Update the corresponding markdown file under `docs/` and `styleguide/`.
