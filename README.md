# MusicLinkd

MusicLinkd is a professional network and marketplace for the music industry.

## Repository map

- `app/` — customer-facing mobile application source and shared client assets.
- `website/` — public marketing website for `musiclinkd.com`.
- `admin/` — internal operations portal (Next.js, static export) for `admin.musiclinkd.com`; separate from the customer app. See [admin/README.md](admin/README.md).
- `docs/` — product, business, and engineering documentation.
- `brand/` — approved brand voice, marks, and reusable brand assets.
- `styleguide/` — the canonical UI and brand source of truth.
- `backend/` — future API, authentication, database, and webhook services.
- `infrastructure/` — future deployment, CI/CD, and observability configuration.
- `archive/` — deprecated artifacts retained for reference only.

## UI governance

Before adding or changing a UI surface, read [the styleguide](styleguide/README.md). Do not create a new component, token, card, or page pattern when an approved equivalent already exists.

## Public addresses

| Address | Purpose | Source directory |
| --- | --- | --- |
| `www.musiclinkd.com` | Public marketing website | `website/` |
| `app.musiclinkd.com` | Customer app and Android App Links | `app/` |
| `admin.musiclinkd.com` | Internal operations portal | `admin/` |
| `styleguide.musiclinkd.com` | Rendered design-system reference | `styleguide/` |

See [domain mapping](infrastructure/domains.md) for the deployment ownership and DNS requirements.

## Backend

Single Firebase project for everything right now: `musiclinkd-7ae30` (Android,
website, and admin all point at it — see
[Build_And_Environment_Guide.md](app/docs/environment/Build_And_Environment_Guide.md)
for why). Firestore schema, security rules, and indexes are defined at the repo
root (`firestore.rules`, `firestore.indexes.json`, `storage.rules`,
`remoteconfig.template.json`, `firebase.json`) — the canonical schema doc is
[Firestore_Schema_Index_Rules.md](app/docs/firestore/Firestore_Schema_Index_Rules.md).
Hosting setup for `musiclinkd.com` and `admin.musiclinkd.com` is documented in
[infrastructure/firebase-hosting.md](infrastructure/firebase-hosting.md).
