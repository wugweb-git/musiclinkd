# The spec chain — what exists, what is deferred, what is out of scope

**Recorded 2026-08-31** so it is not lost to session compaction, which is the
same reason `docs/04-Wireframes/FLOW-SPECS.md` exists. Decisions live here per
that file's own note: *"Where a decision superseded something here, the decision
is recorded in `docs/09-Roadmap/` and in the commit history."*

The chain the briefs describe:

> Flows → Modules → Personas → Use Cases → User Journeys → Screens →
> Components → Schema/Entities → Admin → Permissions → Notifications →
> Analytics → HTML/PWA screen JSON

Measured against the repo rather than asserted. Every number below was counted
from the filesystem on 2026-08-31.

**The screen count is a moving target.** It went 173 → 174 → 184 during the
hour this was written, because a parallel session is actively generating flows.
Treat every count here as "as of 2026-08-31" and re-run the commands in the last
section rather than trusting the figure — the RATIOS and the absences are the
durable part, not the totals.

## What exists

| Link | Where | Count |
|---|---|---|
| Flows | `webapp/flows/*/_flow.json` | 24 |
| Screens | `webapp/flows/*/*/screen.json` | 184 |
| Components | `/guide` — generated from the stylesheets | 133 banner sections, 173 `g-comp` blocks |
| Admin registry | `admin/app/(admin)/*` | 26 routes |
| Notifications | flow + `notifications` collection | 6 screens |
| States | `screen.json.states` | 184/184 |
| Entities | `screen.json.collectionsTouched` | 184/184, 26 collections |

Components additionally carry a derived **state map** — which of ten states each
one owns, inherits, composes, or lacks — parsed from the stylesheets rather than
listed by hand.

## What does not exist

**Personas.** `role_profiles` is a live Firestore collection with an admin route
at `/role-profiles`, and the superseded catalogue had a "Professional Roles &
Personas · 13 Roles" section. The design layer has none of it: `/guide`'s
`#taxonomy` renders the 18 categories from `Master_Data_Seed_Values.md` and
contains **zero** persona entries. A screen author cannot see which persona a
screen serves.

**Modules.** Same shape. `modules` is a live collection with an admin route; the
superseded catalogue had "Platform Modules · 10 Modules"; the design layer has
nothing.

> Both were briefly recorded as *migrated* in `scripts/audit-guide-migration.mjs`
> by aliasing them onto `#taxonomy`. That alias was never verified and was
> false — removed 2026-08-31. An unverified alias hides a real gap by counting
> it as done, which is the failure this audit exists to prevent. Removing the
> four bad aliases moved the honest gap count from 2 to 8.

**Use Cases.** One mention repo-wide. Effectively absent.

**Analytics.** `profile_analytics` collection and an `/analytics` admin route
exist. Nothing in the design layer.

**User Journeys** are prose only — 30 mentions across `docs/04-Wireframes/`,
no structured data.

## Deferred — one coherent pass, not started

`screen.json` is missing four fields that the briefs ask for, on **every**
screen:

| Field | Present |
|---|---|
| `module` | 0 / 184 |
| `route` | 0 / 184 |
| `actions` | 0 / 184 |
| `permissions` | 0 / 184 |

Plus the `<script type="application/json" data-screen>` block the briefs
specify. No generated screen emits it — the only two files mentioning
`data-screen` are `webapp/README.md` and `webapp/shared/app-shell.js`, i.e. the
documentation and the reader, with nothing producing it.

**One correction to how this was first stated.** `dataContract` is *not* empty
on all screens. It is present on every one and **populated on 95**; 89 are empty
arrays. The work is filling the remainder, not building the field.

`screenNumber` is also absent on 4.

This is one pass over `default-screens.ts` + `generate.ts` plus two generated
documents. **Not started, by explicit decision.**

### Two defects found in the Screens → Permissions link

Worth fixing whenever that pass happens:

- `businesses` and `opportunities` are named in `collectionsTouched` and have
  **no `firestore.rules` block at all**.
- `feedPosts` and `studioBookings` are camelCase in the screen contracts but
  `feed_posts` / `studio_bookings` in the rules — a naming mismatch, not a
  missing rule.

## Out of scope

Not deferred — **not this repo's job**:

- **Android implementation.** There is no Kotlin in this repository; `app/` was
  moved out in `1620e46`. `docs/04-Wireframes/PRODUCT-SPEC.md` §59 states it:
  *"Design against the PWA reference build and treat native-Kotlin translation
  as a separate, later step — I can't spec Compose screens I've never seen."*
- **Backend**, payment processing, recommendation algorithms, verification
  services.
- **CMS authoring.** Flow 22 is the *reader's* journey; authoring belongs to
  Admin, per the brief.

**The deliverable is the design layer plus the generated native export** —
`docs/handoff/MusicLinkdTokens.kt` (Compose) and `MusicLinkdTokens.swift`
(SwiftUI), both emitted from `tokens.json` by `scripts/build-handoff.mjs` and
gated on deploy by `--check`.

> One open question this does **not** settle: `PRODUCT-SPEC.md` §154 has listed
> *"Native Android — still open. Is there a Kotlin/Compose repo I should get
> access to, or is the near-term target actually the `webapp/` PWA?"* since
> 2026-08-16. Unanswered.

## Re-check these numbers

```bash
node scripts/audit-guide-migration.mjs   # migrated / dropped / MISSING
node scripts/audit-tokens.mjs            # dead tokens, literals, off-palette
node scripts/audit-screens.mjs           # unresolved vars, dead links, local <style>
```

Field coverage across every screen contract:

```bash
node -e 'const fs=require("fs"),cp=require("child_process");
const f=cp.execSync("find webapp/flows -name screen.json").toString().trim().split("\n");
const k={};let filled=0;
for(const p of f){const d=JSON.parse(fs.readFileSync(p,"utf8"));
  Object.keys(d).forEach(x=>k[x]=(k[x]||0)+1);
  if((d.dataContract||[]).length) filled++;}
console.log("screens",f.length,"dataContract filled",filled);
for(const w of ["module","route","actions","permissions"]) console.log(w,(k[w]||0)+"/"+f.length);'
```

## Verification note

When checking rendered output in a browser, **re-fetch the stylesheets and
`app-shell.js` past the cache**. Entries predating the cache-header fix
(`d9d02f4`, `eaf27c6`) are held as immutable and will serve a stale build that
looks current. Confirm served bytes equal on-disk bytes before trusting any
measurement — and confirm the viewport is not 0×0, which silently collapses the
layout and invalidates every geometry reading.
