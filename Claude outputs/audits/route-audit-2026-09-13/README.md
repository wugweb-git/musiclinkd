# Route audit data — 13 Sep 2026

Companion data for `../route-audit-2026-09-13.md`. Run the scripts from the **app repo root**
(`musiclinkd-9e6ca875`), with the dev server on :8080 for the live checks.

| Script | Produces | What it does |
|---|---|---|
| `scripts/routemap.mjs` | `data/routes.json` | Every route from `routeTree.gen.ts`, its file, load behaviour, and every link in the code resolved to a route |
| `scripts/declgraph.mjs routes.json` | `data/graph.json` | Declaration-level link graph (a route gets only the links inside code it actually uses), inbound links, and shortest journeys from `/welcome` (new user) and `/feed` (signed in) |
| `scripts/types.mjs graph.json routes.json out.json` | `data/types.json` | Journey tables per user type |
| `scripts/crawl.mjs routes.json` | `data/crawl.json` | Live HTTP status and title per route from the dev server |
| `scripts/check.mjs samples.txt` | `data/samples-result.txt`, `data/bad-result.txt` | Live check of parameter routes with real ids and with unknown ids |
| `scripts/appendix.mjs …` | appendix table | The per-route table in §9 |
| `scripts/screens.mjs routes.json` | — | Regex pre-scan for hidden screens; the full inventory in `screen-inventory.md` came from reading every route file |

Limits: links built at runtime from data (e.g. `navigate({ href })`) are not resolved; tabs and sheets are
counted from code, not clicked.
