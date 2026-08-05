# Security Policy — MusicLinkd

## Supported Environments
- **Website:** `musiclinkd.com`
- **Application Target:** `app.musiclinkd.com`
- **Admin Target:** `admin.musiclinkd.com`

---

## Reporting Vulnerabilities

If you discover a security vulnerability or potential credential leak within MusicLinkd, please report it immediately to our security team.

- **Email:** `hello@musiclinkd.com` / `security@musiclinkd.com`
- **Response SLA:** Within 24 hours.

Please **do not** publicly disclose vulnerabilities via GitHub issues until our team has triaged and remediated the reported issue.

---

## Secret Management

- API keys (e.g., Gemini API key, Firebase configuration) must NEVER be hardcoded in `.kt` source files.
- Secrets are managed via AI Studio Secrets Panel or injected via `BuildConfig` from `.env.production`.
