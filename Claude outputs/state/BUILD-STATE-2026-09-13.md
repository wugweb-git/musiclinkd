---
doc: BUILD-STATE-2026-09-13
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: state
originally: BUILD-STATE-2026-09-13.md
---

> **CURRENT** · v1.0 · 2026-09-13. Dated snapshot of pushed / merged / live. Point-in-time; does not age into CURRENT.md.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# MusicLinkd — Handoff / Build State, 13 Sep 2026

Verified against `origin/main` = `0697a42` and the Lovable project record
(`latest_commit_sha 0697a422`, `is_published: true`, build ready, `error: null`). CI green.

**Hosting:** Nothing ships to Firebase. **Lovable is the host** — `musiclinkd.lovable.app`.
Firebase stays as the admin/Firestore backend only.

---

## 1. Corrections issued this session

| Claimed earlier | Actual |
|---|---|
| The services / service-builder / availability screens don't exist in any branch. | **They exist and are on `main`.** Eleven route files, committed 1–9 Sep by Lovable's bot, never in a PR. |
| `origin/sync` CI is unmerged; `main` has no `.github` directory. | **Merged, then rebuilt** — workflow rewritten to bun, branch deleted. |
| `scripts/build-handoff.mjs:95` will reject `--type-5`. | **Wrong.** That check validates a type step's *weight value* against the weight scale, and builds its map dynamically from `steps(f.weight)`. It never capped the number of type steps. The word "four" in its comment is stale prose, not a validator. |

The first two came from reading a local clone that had gone stale. The sandbox has `git` but no
credentials, so `git ls-remote` fails and staleness is silent. See §8.

---

## 2. Two writers on `main` — accommodated, not resolved

Claude Code branches, opens a PR, writes a descriptive commit message. Lovable's agent
(`gpt-engineer-app[bot]`) pushes straight to `main` with the message `Changes` or
`Work in progress`. CI runs on `push: [main]` as well as PRs, so Lovable's pushes *are* checked —
but they are still unreviewed and unattributable.

The CI design records this as a deliberate trade-off, in the workflow file itself:

- **Blocking:** typecheck, lint (correctness rules only), build.
- **Non-blocking:** `Format check` runs with `continue-on-error: true`. Lovable's agent commits
  without running Prettier, so gating on formatting would fail every Lovable push — and a
  permanently red check stops being read.

Lovable's first push after CI landed did fail lint; cleared in `ebbd4a4`.

**Attribution.** Rule: every commit's `Co-Authored-By` names Wugweb only, `admin@wugweb.com`,
nothing else.

- All five Claude Code commits comply — `ebbd4a4`, `1f1627f`, `cc6536f`, `968dfd0`, `0697a42`.
- Lovable's bot commits do not: `08ca2b3`, `f7ceab6`, `08ed071` carry
  `Co-authored-by: wugweb-git <132406863+wugweb-git@users.noreply.github.com>`; `e2861bc` carries
  no trailer. Lovable writes these itself and they are published, so they cannot be corrected
  without rewriting history — forbidden, it corrupts Lovable's sync.

**Still open: give Lovable's agent its own branch and a PR, or accept that roughly half of `main`
arrives unreviewed and mis-attributed.**

---

## 3. Landed and live

| Commit | What |
|---|---|
| `ebbd4a4` | Clear Lovable's lint failures; gate CI on correctness, not formatting |
| `cc6536f` | Quote the lint command so the workflow file parses |
| `968dfd0` | 26 child routes rendered their parent's screen |
| `0697a42` | Merge |

### 3.1 The 26-route bug

`/bands/new` and every band profile rendered the bands *list*. The same fault hit `/reviews/*`,
`/plans/*`, `/help/*` and Lovable's new `/search/*` pages.

**Cause:** 12 parent routes had no `<Outlet />`, so children had no slot to render into. The fix
renames each parent to an `.index.tsx` leaf, making the path a true parent with no component of
its own:

```
bands.tsx                → bands.index.tsx
bands.$id.tsx            → bands.$id.index.tsx
collab.new.tsx           → collab.new.index.tsx
collab.proposals.$id.tsx → collab.proposals.$id.index.tsx
help.tsx                 → help.index.tsx
identity.contact.tsx     → identity.contact.index.tsx
plans.tsx                → plans.index.tsx
portfolio.$id.tsx        → portfolio.$id.index.tsx
recommendations.tsx      → recommendations.index.tsx
reviews.tsx              → reviews.index.tsx
search.tsx               → search.index.tsx
services.$id.tsx         → services.$id.index.tsx
```

Verification recorded in the commit: tsc, lint (0 errors) and build pass; the regenerated tree has
**207 routes and 0 children under an Outlet-less parent**; 14 parent and child URLs fetched from
the dev server each render their own screen; `/bands/the-kite-notes` screenshotted at 412px with
no console errors. All affected pages re-checked on the live site after publishing.

---

## 4. Design guide repo — 7-step type scale merged

`wugweb-git/musiclinkd`, `main` in sync with `origin/main`.

| Commit | What |
|---|---|
| `f9f76fe` | Grow the type scale from 4 steps to 7 |
| `69a8db5` | Merge PR #3 |
| `18e2305` | Regenerate changelog page and screen registry |

`ml-tokens.css` now emits `--type-1` through `--type-7`, and carries **111 token declarations** —
the 108 ceiling is lifted. **No page looks different**, because every existing reference was
renumbered to keep its rendered size.

---

## 5. Pending-screens audit

| Verdict | Count |
|---|---|
| Done | 24 |
| Partial | 9 |
| Not done | 4 |

**The dominant problem is not unfinished screens — it is unreachable ones.** Roughly **20 finished
screens have no link pointing to them.** Wiring those into navigation is the cheapest next step by
a wide margin: the work is already built and paid for, and today no user can get to it.

---

## 6. Open code items, unchanged

Verified at `main`; the route and CI work did not touch them.

| Item | Detail |
|---|---|
| Inter weight 800 | Never added. Link is `Inter:wght@400;500;600;700`, while `h1..h4` are set to 700 and display needs 800 |
| `letter-spacing: -0.02em` | Still on `h1..h4`. It was Inter Tight compensation; Inter is not condensed |
| `styles.css` header | Still says *"Type is four fluid ramps"* while the block below documents five |
| `--radius-xl / 2xl / 3xl` | Still aliases of 32px across ~450 call sites. Making them real steps costs zero call-site edits |
| Dark mode | `@custom-variant dark` declared, no `.dark { }` block anywhere. Every `dark:` utility compiles and never matches |
| Two spacing systems | `--spacing-xs/sm/md/lg` coexist with numeric `p-1 / p-2 / p-4 / p-8`. Identical values, no rule |
| Retired navy | `--navy`, `--color-navy`, `--gradient-navy` live; `index.tsx` uses `bg-navy` and `from-navy/85` |
| `--text-secondary` | Byte-identical to `--muted-foreground` (`#66666f`). Kept only because `text-text2` is on ~100 call sites |
| Route-level tokens | Phase 2 swept `src/components/ml/` but not `src/routes/`. `index.tsx` alone has `text-[30px] [15px] [14px] [13px] [11px] [10px]`, two inline `style` objects, and 🔔 ✨ → ★ as icons |

---

## 7. Open decisions — owner, not engineering

1. **Capacitor path** — thin wrapper around the hosted site, or a static bundle shipped in the APK.
   Determines whether `viewport-fit=cover`, `@capacitor/preferences` and the manifest splash are
   needed at all.
2. **Phase 8 prerequisites** — the 204-route token sweep, priced at roughly 97 hours /
   16 working days / 8 PRs.
3. **Route naming** — `/recommendations` vs `/recommended`, and `/bands` vs `/band`. Both pairs
   currently exist.
4. **Lovable's agent: branch and PR, or direct-to-`main`?** See §2.
5. **`profiles.visibility` RLS** — PR #7 merged the migration; still **not applied**. Live SELECT
   policy is `USING (true)`. All 3 profiles are `public`, so nothing is exposed yet.
6. **Firestore `/users` PII** — `allow read: if true` publishes whole documents; `email` is on all
   83 world-readable user docs. Firestore has no field-level read control, so the first real OTP
   signup publishes that person's phone number.
7. **The Lovable project `description` field still specifies the superseded system** — `#5B4BFF`
   primary, `#8B5CF6` secondary, `#FAFAFA` ground, Google Sans. Project knowledge records that
   `#5B4BFF` is superseded and always a bug. The description is read alongside knowledge and should
   be rewritten.

---

## 8. Tooling note — GitHub access

There is no GitHub connector in the Claude connector directory. GitHub's **official remote MCP
server** is the route instead:

```
https://api.githubcopilot.com/mcp/            full
https://api.githubcopilot.com/mcp/readonly    read-only
```

Add via claude.ai → Settings → Connectors → Add custom connector. `/readonly` is the right scope
given the division of labour: Claude (Cowork) audits and specifies, Claude Code writes.

Until then, repo state is read from a local clone plus the read-only Lovable connector. The clone's
sandbox has `git` but no credentials, so `git ls-remote` fails and the clone can go stale with no
signal — which produced the first two corrections in §1.

---

Also filed in Notion as **🧾 Build State — 13 Sep 2026**, under
Wugweb HQ / MusicLinkd / MusicLinkd — Master Registry.
