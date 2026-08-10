# CMS Content Mapping

**Audited:** August 2026. Dated snapshot — re-verify before relying on
specifics, though this particular finding (a static site not reading its
own CMS collection) is unlikely to self-resolve without a deliberate fix.

## The finding

`admin/app/(admin)/pages/` (the "Pages" admin module) edits Firestore
documents in the `page_content` collection. `PageContentDoc`
(`admin/types/firestore.ts:174`) has a `route` field shaped like a real site
URL — `/`, `/about`, `/legal/privacy` — clearly intended to map onto actual
pages of the live marketing site.

`frontend/website/` — the site actually deployed to `musiclinkd.com` via
`scripts/build-hosting.sh` — is **100% static HTML**. Confirmed by grepping
the entire `frontend/website/` and `website/` trees for `page_content`,
`firestore`, `firebase`, `firebaseapp`, `firebasejs`: the only hits are
inside `website/admin/_next/static/chunks/*.js` — i.e. the *admin panel's
own* built JavaScript bundle, which happens to live in the merged output
directory. The marketing site itself never calls Firestore at runtime.

**Net effect:** editing a page in the admin panel's Pages module writes a
real Firestore document, but nothing on the live site ever reads it. The
CMS is currently a write-only system with no consumer.

## Why this wasn't fixed as part of the master-data/modules work

Two real fixes exist:
- **(a) Build-time SSG pull** — `scripts/build-hosting.sh` templates
  `page_content` into the static HTML during the build. Requires giving the
  build environment Firestore Admin credentials and restructuring
  `frontend/website/`'s build step from "copy static files" to "generate
  from data" — a `frontend/website/` architecture change, not an
  admin-panel or Cloud-Functions change.
- **(b) Client-side hydration** — the marketing site fetches `page_content`
  in the browser after load. Actively harms SEO (content absent from the
  initial HTML response the crawler sees) — a real regression for a site
  that presumably cares about search visibility, not just a scope
  trade-off.

Both were out of scope for the master-data/modules pass that produced this
doc (see the plan file's Part D). Instead, the Pages admin page
(`admin/app/(admin)/pages/page.tsx`) now shows a persistent notice stating
edits are Firestore-only and not yet live on the site, so editors aren't
misled into thinking a save is visible to the public.

## The real fix, if/when prioritized

Option (a) — build-time SSG — is the one that doesn't compromise SEO.
Concretely: `scripts/build-hosting.sh`'s `frontend/website/` step would need
to, for each `page_content` doc with `status: "PUBLISHED"`, either template
its `content` into the corresponding static HTML file at build time, or
replace the relevant static pages with a small static-site generator step
that reads Firestore via the Admin SDK before `cp -r frontend/website/.
dist/`. This is a genuinely separate, larger workstream — not attempted
here.
