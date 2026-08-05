# Build Variants & Environment Guide — MusicLinkd

## Product Environments

**Current phase (MVP, one active developer): a single Firebase project.** Android, the
marketing website, and the admin portal all point at the same project — no separate
dev/staging/prod Firebase backends. Splitting into isolated environments now would add
multiple config files, OAuth client IDs, Storage buckets, security rule sets, and
Analytics properties for no benefit at this stage; that split is deferred until it's
actually needed.

Everything — Hosting (`musiclinkd.com`, `admin.musiclinkd.com`), Firebase Auth,
Firestore, Storage, Cloud Messaging, Analytics, Crashlytics, Remote Config, and
Performance Monitoring — lives in **`musiclinkd-7ae30`**, for every build variant
below. No per-variant project IDs.

### Future migration (backend only)

Reintroduce separate Firebase **projects** (`musiclinkd-dev` → `musiclinkd-stage` →
`musiclinkd-prod`) once there's a team, a release process, or a reason to isolate
staging data from production. Until that migration happens, any doc or config in
this repo referencing those three project IDs is describing a future state — do not
point live code at them. This is independent of the build variants below, which are
real today and don't require multiple Firebase projects.

## Android build variants

Three variants, one backend — differ only in deep-link host, log verbosity, and
whether Analytics is enabled. Full `.env` values for each in
[`environment_variables.md`](../variables/environment_variables.md#android-appapp--active-env-files).

| Variant | Host URL | Firebase Project | Log Level |
| :--- | :--- | :--- | :--- |
| Development | `https://dev.app.musiclinkd.com` | `musiclinkd-7ae30` | `DEBUG` |
| Staging | `https://staging.app.musiclinkd.com` | `musiclinkd-7ae30` | `INFO` |
| Production | `https://app.musiclinkd.com` | `musiclinkd-7ae30` | `WARN` |

---

## Build Tasks

```bash
# Debug APK Build
gradle :app:assembleDebug

# Release AAB Bundle Build
gradle :app:bundleRelease

# Run Local JVM Tests
gradle :app:testDebugUnitTest
```
