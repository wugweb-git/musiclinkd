# Admin Portal Architecture & Readiness Specification (`admin.musiclinkd.com`)

## Target URL & Domain Isolation
- **Admin Portal Target:** `admin.musiclinkd.com`
- **Primary Support Email:** `hello@musiclinkd.com`

---

## 🏛 Information Architecture

**As built** (`admin/`, Next.js — see [admin/README.md](../../../admin/README.md)).
The original nested tree below this section is what this doc specified before
the app existed; the build flattened it into 17 top-level routes because that
maps directly onto Firestore collections and role checks, one module = one
route = one repository:

```
admin.musiclinkd.com
├── Dashboard              — aggregate counts across collections
├── Users                  — /users (includes Artists via a role filter)
├── Studios                — /studios
├── Marketplace            — /marketplace_items
├── Bookings               — /studio_bookings
├── Projects               — /projects
├── Reviews                — /reviews
├── Verification           — /verification_requests (was "Verification Queue")
├── Moderation              — /moderation_flags (was "Content Moderation")
├── Audit Logs             — /audit_logs
├── Settings               — Remote Config feature flags + /admin_roles management
├── Reports & Exports      — CSV export over the collections above (merged; kept as one module instead of two near-duplicates)
├── Notifications           — /broadcast_messages (FCM push broadcasts — queues only, actual send needs a Cloud Function not built yet)
├── Page Management        — /page_content: homepage, footer pages, legal pages (added after this doc was first written — see the schema doc's limitation note: website/ doesn't read this collection yet)
├── Messages               — real /chat_threads data exists, but admins have no read access by rules (privacy decision, not built) — mock data only
├── Analytics               — mock data only; Firebase Analytics isn't queryable client-side the way Firestore is
└── Tracks                  — mock data only; not a modeled concept in the schema
```

"Studio Partner Management" and "Support & Tickets" from the original tree are
not separate top-level modules in the build: studio approvals route through
Verification (`subjectType: STUDIO`), and support tickets have a schema
(`/support_tickets`) and rules but no dedicated admin route yet — the
Notifications/Settings modules cover the rest of System Settings' original
scope (Feature Flags → Settings, FCM Push Broadcasts → Notifications, Audit
Logs → its own module).

---

## 🔑 Administrative Roles & Permissions

- `ROLE_ADMIN`: Full access across all modules, configuration flags, and audit logs.
- `ROLE_MODERATOR`: Access restricted to Moderation Queue, Flagged Content, and Review Reports.
- `ROLE_SUPPORT`: Read-only user lookup and support ticket response handling.

The exact per-module role matrix is defined in code, not prose — see
`MODULE_PERMISSIONS` in [admin/config/constants.ts](../../../admin/config/constants.ts),
enforced client-side by `useRequireModule`. Role membership itself lives in a
separate `/admin_roles/{uid}` Firestore collection, never on the public-readable
`/users/{uid}` profile — see the canonical schema doc for why.
