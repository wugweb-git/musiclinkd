---
doc: NOTION-VS-LOVABLE-CROSSCHECK
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@90a4676 (Claude outputs/state/CURRENT.md) + live Notion fetch
folder: audits
originally: new
---

# Notion vs. Lovable/Supabase reality — cross-check

Requested: check whether the Notion workspace ("MusicLinkd" hub and its child pages) reflects the actual
live product at `musiclinkd.lovable.app`, or has fallen out of sync with it. Short answer: **it has never
mentioned it.** Every Notion page describing product architecture, hosting, and implementation status was
written against a single-stack assumption (one Firebase project, one repo, Firestore as the data authority)
that stopped being true no later than 1 September 2026, when the Lovable/Supabase frontend became the
actual live product.

## What was checked

Fetched and read in full: **PRD & Product Overview**, **Deployment Plan**, **Modules Map**, **MusicLinkd —
Product Spec** (Implementation Log, already updated separately this session). Searched (workspace search)
and confirmed absent from **MusicLinkd — Master Registry** (75K+ characters, grepped for "lovable",
"supabase", "tanstack" — zero matches).

Compared against `Claude outputs/state/CURRENT.md` — the actively-maintained ground-truth file for the
Lovable app's repo (`wugweb-git/musiclinkd-9e6ca875`), verified 13 Sep 2026 against commit `90a4676`.

## The core finding

Notion's documentation model treats **one** system as canonical: Firebase project `musiclinkd-7ae30`,
repo `wugweb-git/musiclinkd`, Firestore as "the data authority" (PRD's own words), Gestalt as "the visual
authority." That system is real — it's the `admin/` Next.js panel this session spent most of its time
redesigning, backed by Firestore.

But the actual product a visitor sees at `musiclinkd.lovable.app` is a **second, separate system** that no
Notion page acknowledges exists:

| | Notion's model | Actual live product |
|---|---|---|
| Repo | `wugweb-git/musiclinkd` | `wugweb-git/musiclinkd-9e6ca875` |
| Stack | Next.js (admin), static HTML (site) | TanStack Start, React 19, Tailwind v4, bun |
| Database | Firestore (16+ collections documented in Master Registry) | Supabase project `gsqeymhtyzepwcnorjun` — **2 tables**: `profiles`, `portfolio_items` |
| Hosting | "One Firebase Hosting site... serves `musiclinkd.com`, `admin.musiclinkd.com`" (Deployment Plan) | Lovable, direct-to-`main`, own CI. Firebase Hosting still serves the admin panel only. |
| Design system | "Gestalt is the visual authority" (PRD) | Material 3 radius scale, Inter-only type — unrelated to Gestalt, and itself diverging from this repo's own `frontend/guide` tokens |
| Implementation status | Modules Map's Real/UI-only/Mock table, sourced from the retired Kotlin/Android app | Its own tracked state: 207 routes, 24 screens done / 9 partial / 4 not done, a known ~20-screen unlinked-navigation gap, an unresolved Firestore-world-readable-email security note |

None of the right-hand column exists anywhere in the Notion workspace prior to this session.

## What was corrected in Notion

Rather than just report this in chat and let it go stale again, added dated correction callouts — in the
same voice/format this workspace already uses for its "Integrity correction" and "Reconciliation Overlay"
callouts — to the two pages where the wrong assumption is most load-bearing:

- **PRD & Product Overview** — new callout immediately before the existing "Integrity correction — Flow
  inventory" section, laying out the two-system reality, the Supabase table count, the design-system split,
  and explicitly leaving "does `musiclinkd.com` itself point at Lovable now?" as an open, unconfirmed
  question rather than asserting an answer.
- **Deployment Plan** — new callout directly under the existing "planning document" disclaimer, stating
  that the Firebase Hosting pipeline described in that page covers the **admin panel only**, and that the
  live product frontend deploys entirely separately through Lovable with its own CI.

**Deliberately not touched:** Master Registry (75K+ characters — a targeted correction risks being lost in
or contradicting content I didn't fully re-verify line by line) and Modules Map (its Real/UI-only/Mock
table is about the retired Android app specifically, which is a separate — if related — staleness issue
from the one this cross-check was scoped to). Both would benefit from the same treatment; flagging rather
than doing it silently.

## Open question this cross-check does not answer

Whether `musiclinkd.com` (the public marketing domain, distinct from the `.lovable.app` preview URL) is
currently pointed at the Lovable app, the old Firebase-hosted static site, or something else. `CURRENT.md`
doesn't say, and no Notion page has current DNS/hosting evidence either. Worth checking directly (e.g. via
a live fetch of `musiclinkd.com`) before anyone treats either domain claim as settled.
