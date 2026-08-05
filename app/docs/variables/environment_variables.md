# Environment Variables & Configuration — MusicLinkd

## Backend: single Firebase project

MusicLinkd runs on **one** Firebase project — `musiclinkd-7ae30` — for Android, the
marketing website, and the admin portal: one Firestore, one Auth tenant, one Storage
bucket, one set of security rules, one Hosting project. This is a deliberate MVP
simplification with one active developer. See
[`Build_And_Environment_Guide.md`](../environment/Build_And_Environment_Guide.md) for
the rationale and the future migration path (re-splitting into
`musiclinkd-dev`/`musiclinkd-stage`/`musiclinkd-prod` **projects**).

**This is independent of Android build variants below** — dev/staging/production
build targets differ in deep-link host and log verbosity, not in which Firebase
backend they talk to. All three `.env.*` files point at the same
`FIREBASE_PROJECT_ID`.

## Android (`app/app/`) — active `.env.*` files

The Secrets Gradle Plugin (`app/app/build.gradle.kts`, `secrets { propertiesFileName
= ".env"; defaultPropertiesFileName = ".env.example" }`) reads a single active file
named exactly `.env`. The three files below are per-build-target **reference
values** — copy the one matching your target into `.env` before building that
variant. `.env`, `.env.development`, `.env.staging`, and `.env.production` are all
gitignored (`app/app/.gitignore`); only [`.env.example`](../../../app/app/.env.example)
(placeholders only) is committed.

### Development
```env
GEMINI_API_KEY=your_dev_gemini_api_key_here
FIREBASE_PROJECT_ID=musiclinkd-7ae30
DEEP_LINK_HOST=dev.app.musiclinkd.com
ENVIRONMENT=development
ENABLE_ANALYTICS=false
LOG_LEVEL=DEBUG
```

### Staging
```env
GEMINI_API_KEY=your_staging_gemini_api_key_here
FIREBASE_PROJECT_ID=musiclinkd-7ae30
DEEP_LINK_HOST=staging.app.musiclinkd.com
ENVIRONMENT=staging
ENABLE_ANALYTICS=true
LOG_LEVEL=INFO
```

### Production
```env
GEMINI_API_KEY=your_production_gemini_api_key_here
FIREBASE_PROJECT_ID=musiclinkd-7ae30
DEEP_LINK_HOST=app.musiclinkd.com
ENVIRONMENT=production
ENABLE_ANALYTICS=true
LOG_LEVEL=WARN
```

## Admin (`admin/`) and website — no equivalent split

`admin/.env.local` (see [`admin/.env.example`](../../../admin/.env.example)) has no
dev/staging/production variants — it's one config for the one Firebase project's web
app. `website/` has no environment variables at all; it's static HTML.

## Full variable reference

| Variable | Purpose | Value (MVP) | Mechanism |
| --- | --- | --- | --- |
| `GEMINI_API_KEY` | Gemini AI matchmaker API (Android only — see [`backend_integrations.md`](../integration/backend_integrations.md)) | Injected placeholder | AI Studio Secrets panel → `.env` → `BuildConfig` at runtime |
| `FIREBASE_PROJECT_ID` | Backend project ID | `musiclinkd-7ae30` | `.env`, referenced by `FirebaseManager`/`AnalyticsHelper` |
| `DEEP_LINK_HOST` | App Links / Universal Link domain | Varies by target (see above) | `.env` + Android Manifest intent filters |
| `ENVIRONMENT` | Runtime target name | `development`/`staging`/`production` | `.env` |
| `ENABLE_ANALYTICS` | Firebase Analytics collection toggle | `false` (dev) / `true` (staging, prod) | `.env` + `AnalyticsHelper` |
| `LOG_LEVEL` | Logger verbosity | `DEBUG`/`INFO`/`WARN` | `.env` |
| `STORE_PASSWORD` | Release APK keystore password | — (never in a file) | System environment variable, `System.getenv("STORE_PASSWORD")` |
| `KEY_PASSWORD` | Release APK signing key password | — (never in a file) | System environment variable, `System.getenv("KEY_PASSWORD")` |

`STORE_PASSWORD`/`KEY_PASSWORD` are deliberately never written to any `.env` file —
set them as real OS/CI environment variables only, at build/sign time.

## Security rule

API keys and credentials are provided through AI Studio Secrets or injected via
`BuildConfig`. Never commit hardcoded production secrets to version control. Only
`.env.example` files (placeholders only) are committed — `admin/.env.example` and
`app/app/.env.example`.
