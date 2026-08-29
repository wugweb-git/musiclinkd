# MusicLinkd

MusicLinkd is a professional network and marketplace for the music industry, with a customer-facing mobile app, public website, internal operations portal, and a shared design-system reference.

## Repository map

| Directory | Purpose |
| --- | --- |
| `app/` | Customer-facing Android/mobile application and app-specific documentation/assets. |
| `website/` | Public marketing website for `www.musiclinkd.com`. |
| `admin/` | Internal operations portal for `admin.musiclinkd.com`; separate from the customer app. See [admin/README.md](admin/README.md). |
| `docs/` | Product, business, architecture, and engineering documentation. |
| `backend/` | Backend/API foundation and future service layer. |
| `styleguide/` | Public design-system reference and generated UI catalogue. The catalogue is built from the canonical system sources; see `scripts/build-guide.mjs`. |
| `archive/` | Deprecated or superseded artifacts retained for reference only. |
| `infrastructure/` | Hosting, domain, deployment, and infrastructure documentation/configuration. |

## Design system and UI governance

The design system is the UI source of truth. Before adding or changing a UI surface:

1. Check the existing system components, tokens, patterns, and documented flows.
2. Reuse an approved equivalent whenever one exists.
3. Do not introduce a duplicate component, token, card, or page pattern without documenting why.
4. Keep product UI, brand-expression assets, and implementation details aligned with the canonical system.

The design-system catalogue is generated from the repository rather than maintained as an independent hand-written component inventory. The build entry point is [`scripts/build-guide.mjs`](scripts/build-guide.mjs).

## Product surfaces

| Address | Purpose | Source |
| --- | --- | --- |
| `www.musiclinkd.com` | Public marketing website | `website/` |
| `app.musiclinkd.com` | Customer app / Android App Links | `app/` |
| `admin.musiclinkd.com` | Internal operations portal | `admin/` |
| `styleguide.musiclinkd.com` | Public design-system reference | `styleguide/` |
| `api.musiclinkd.com` | Reserved future API | `backend/` |

See [infrastructure/domains.md](infrastructure/domains.md) for deployment ownership and DNS requirements. These hostnames are documented product surfaces; source configuration alone does not mean a hostname is currently deployed.

## Backend and Firebase

MusicLinkd currently uses the Firebase project `musiclinkd-7ae30` across the Android app, website, and admin surfaces.

Repository-level Firebase configuration includes:

- `firebase.json`
- `firestore.rules`
- `firestore.indexes.json`
- `storage.rules`
- `remoteconfig.template.json`
- `.firebaserc`

The canonical Firestore schema documentation is [`app/docs/firestore/Firestore_Schema_Index_Rules.md`](app/docs/firestore/Firestore_Schema_Index_Rules.md). Environment and build configuration is documented in [`app/docs/environment/Build_And_Environment_Guide.md`](app/docs/environment/Build_And_Environment_Guide.md).

## Documentation

Start with:

- [`docs/`](docs/) — product, business, and engineering documentation.
- [`app/docs/`](app/docs/) — mobile application architecture, flows, environment, and Firestore documentation.
- [`admin/docs/`](admin/docs/) — admin architecture, seed data, widgets, and operational documentation.
- [`CHANGELOG.md`](CHANGELOG.md) — recent repository changes.
- [`AGENTS.md`](AGENTS.md) — repository-level engineering guidance.

## Hardcoded content rule

User-visible UI copy belongs in Android resources rather than Compose literals. Demo/seed entities belong in seed providers. Real runtime values belong to application state, repositories, or the database.

The recovered hardcoded-content audit is maintained at [`guide/docs/hardcoded-content-audit.md`](guide/docs/hardcoded-content-audit.md). Treat it as an audit and migration backlog, not as proof that every listed hotspot still exists unchanged in the current source tree.

## Current development state

The repository contains active product surfaces alongside documented scaffolding and future-service areas. Do not infer deployment readiness solely from the existence of a directory or hostname; use the relevant infrastructure and environment documentation for the current state.
