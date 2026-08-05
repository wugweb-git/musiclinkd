# MusicLinkd Operations

Internal admin panel for MusicLinkd — user management, moderation, verification,
studio/marketplace oversight, and page content. Deployed at
`admin.musiclinkd.com`, separate from the customer-facing app.

**Stack:** Next.js 15 (App Router, TypeScript), static export (`output: 'export'`).
Tailwind CSS + shadcn/ui, themed from the brand tokens in
[`app/brand.md`](../app/brand.md). No server component of its own — all data
access goes through the Firebase Web SDK, client-side.

**Hosting:** deployed as part of a single merged Hosting site shared with
`website/` — see [`infrastructure/firebase-hosting.md`](../infrastructure/firebase-hosting.md)
for the full model (one site, two domains, `scripts/build-hosting.sh` does the
merge). `admin.musiclinkd.com/` itself shows the marketing homepage (same
content as `musiclinkd.com` — no per-domain routing in Firebase Hosting); go
straight to `/login` or `/dashboard`.

## One-time setup

```bash
cd admin
npm install
cp .env.example .env.local
```

Fill in `.env.local` with the real Firebase Web SDK config for `musiclinkd-7ae30`:
Firebase console → Project settings → your web app (create one first if it
doesn't exist — **Add app → Web**). See `.env.example` for the exact variable
names and where each value comes from.

**Bootstrap the first admin** before anyone can sign in — rules only let an
existing `ROLE_ADMIN` grant new admins, so the very first one has to be created
manually. See "Bootstrapping the first admin" in
[`Firestore_Schema_Index_Rules.md`](../app/docs/firestore/Firestore_Schema_Index_Rules.md).

## Local development

```bash
npm run dev
```

Without a valid `.env.local`, the app shows a "Firebase isn't configured" screen
instead of crashing — that's intentional (see `config/environment.ts`), not a bug.

## Build & deploy

Deploys happen from the repo root, not from here — `admin/` and `website/` get
merged into one `dist/` before Firebase ever sees it:

```bash
cd ..
./scripts/build-hosting.sh
firebase deploy --only hosting,firestore,storage,remoteconfig
```

`npm run build` (and `npm run dev`) also regenerates
`public/firebase-messaging-sw.js` from your env vars — see
`scripts/generate-fcm-sw.mjs` for why that file can't just import the same
config module the rest of the app uses.

## Architecture

- `config/` — environment variables, Firebase config, app constants, feature-flag
  keys, navigation. Nothing outside this folder should hardcode a Firestore
  collection name or a role string.
- `services/` — typed wrappers around the Firebase SDK (auth, firestore, storage,
  messaging, analytics, performance, remote-config, app-check). No component
  calls the Firebase SDK directly.
- `services/repositories/` — one typed CRUD repository per Firestore collection,
  built off a single generic factory (`generic-repository.ts`) rather than 15
  hand-written near-duplicates.
- `contexts/`, `hooks/` — auth state (`useAuth`), per-module role gating
  (`useRequireModule`), and data fetching (`useCollection`).
- `components/shared/` — `DataTable` (search/sort/paginate/bulk-select, built on
  TanStack Table v8) and `CollectionListPage` (wires a repository + column defs
  into a full module page) are what almost every module is built from.
- `components/layout/` — sidebar, header, and the page shell every module page
  wraps its content in.
- `app/(admin)/<module>/` — one route per module. Each is a thin file: column
  defs + a `CollectionListPage` or `MockListPage` call, not bespoke fetch/table
  logic.

## Module status

Real Firestore data: Dashboard, Users, Studios, Marketplace, Bookings, Projects,
Reviews, Verification, Moderation, Audit Logs, Settings, Reports & Exports,
Notifications, **Pages** (Page Management).

Mock data only (no backing collection — see the flagged decisions below):
Analytics, Tracks.

**Messages** has real chat data in Firestore, but `firestore.rules` restricts
`/chat_threads` to thread members only — admins currently cannot read private
user DMs. That's a privacy decision, not an oversight; the module ships with
mock data until someone decides whether/how admins should see conversations.

**Page Management** has a real `page_content` collection and full CRUD, but
`website/` is still plain static HTML with no code that reads Firestore —
editing a page here does not change what's live at musiclinkd.com yet. See the
limitation note in the schema doc for what wiring that up for real would take.

**Analytics** can't have a real dashboard without either a BigQuery export +
aggregation backend, or just linking out to the Firebase console — Firebase
Analytics data isn't queryable ad hoc from client code the way Firestore is.

**Tracks** isn't a modeled concept in the schema at all — not obviously distinct
from `marketplace_items` (category `STEMS`) or `feed_posts` audio. Needs a
product decision before it gets real data.

## What's explicitly out of scope here

- **Crashlytics** has no Firebase Web SDK — Android/iOS only. Lives entirely in
  `/app` (Kotlin), not here.
- **Gemini API** (AI matchmaking/recommendations) is an Android app feature per
  `app/docs/integration/backend_integrations.md`, not an admin panel feature.
- **App Check** initializes but stays inert (no enforcement) until
  `NEXT_PUBLIC_APP_CHECK_SITE_KEY` is set — matches its own "Enable after initial
  testing" status.
