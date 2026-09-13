---
doc: PR-SPEC-dead-spacing-and-band-route
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: specs
originally: new
---

> **CURRENT** · v1.0 · 2026-09-13. One small PR for Claude Code. Two findings that were previously
> mis-filed as open decisions; measurement showed neither needs a decision.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# PR: remove the dead spacing tokens and the orphaned `/band` route

One branch, one PR: `fix/dead-spacing-and-band-route`. Both changes are removals. Neither alters a
single rendered pixel — verify that claim, don't take it.

Every count below was measured against `origin/main` = `0697a42` with `grep -r` over `src/`.

---

## Change 1 — delete four unused spacing tokens

### The measurement

```
p-xs   p-sm   p-md   p-lg          0 uses
gap-xs gap-sm gap-md gap-lg        0 uses
mx/my/px/py/mt/mb/-xs|sm|md|lg     0 uses
numeric p- px- py- gap- m- mt- mb- 3,513 uses
```

Tailwind v4's `--spacing-*` entries in `@theme inline` **add** named utilities alongside the
numeric scale rather than replacing it. These four were declared and never used once. The codebase
has been on the numeric scale from the start.

This was written up in `audits/TAILWIND-DS-AUDIT.md` §A2 as *"Pick one. Right now you have both and
no rule."* That framing was wrong: there is one system in use and four dead declarations.

### The edit

`src/styles.css`, inside `@theme inline` — delete four lines:

```css
  /* Spacing steps. Numeric utilities (p-1, gap-4 …) are untouched. */
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 16px;
  --spacing-lg: 32px;
```

### Do NOT delete the fifth line

```css
  --spacing-nav-clear: 96px;
```

**This one is live.** `src/components/ml/ui.tsx:14` uses it:

```tsx
<div className={bottomNav ? "flex-1 pb-nav-clear" : "flex-1 pb-8"}>{children}</div>
```

Removing it silently drops 96px of bottom padding under the bottom nav on every screen that has
one. Keep it, and keep a comment saying it is the only named spacing token and why.

### Acceptance

- `grep -rE '\b(p|px|py|pt|pb|m|mx|my|mt|mb|gap|space-x|space-y)-(xs|sm|md|lg)\b' src/` returns
  nothing.
- `grep -rn 'nav-clear' src/` still returns `styles.css` and `ml/ui.tsx`.
- Build passes. A 412px render pass on any screen with a bottom nav shows unchanged spacing.

---

## Change 2 — repoint a dead link and delete an orphaned route

### What I found, and the correction to my own first proposal

I originally said `/band` duplicates `/bands/$id` and should redirect to
`/bands/the-local-train`. **That is wrong and would have shipped a broken screen.**

`src/routes/band.tsx` (212 lines) is a hardcoded profile of a band called **"The Local Train"**.
`src/data/band.ts` contains exactly one band:

```ts
export const bands: Band[] = [ { id: "the-kite-notes", name: "The Kite Notes", … } ];
export const getBand = (id: string) => bands.find((b) => b.id === id);
export const myBandId = "the-kite-notes";
```

There is no `the-local-train` record. `/bands/the-local-train` would hit the `if (!band)` branch in
`bands.$id.index.tsx` and render *"This band is no longer available."* Redirecting there converts a
working demo screen into an error state.

### The actual defect is upstream of the route

`/band` has exactly one inbound link, `src/routes/onboarding/complete.tsx:141`:

```tsx
<HandoffCard
  flow="FLOW 15"
  title="Create your band profile"
  body="A shared profile with members, shows and a joint calendar."
  action={
    <Link to="/band" className="block">
      <Btn className="w-full">Create band</Btn>
    </Link>
  }
/>
```

A button labelled **"Create band"**, under a card titled **"Create your band profile"**, navigates
to a read-only profile of somebody else's band. `src/routes/bands.new.tsx` — the actual creation
screen — exists and is reachable from `bands.index.tsx:46` and `:65`.

This is not a naming-pair question. It is a wrong destination on a CTA in the onboarding
completion screen, which is the last thing a new user sees.

### The edits

**a.** `src/routes/onboarding/complete.tsx:141` — point the CTA at the screen its label describes:

```tsx
<Link to="/bands/new" className="block">
```

**b.** Delete `src/routes/band.tsx`. After (a) it has zero inbound links, and it renders a band
that does not exist in the data layer.

**c.** Let `routeTree.gen.ts` regenerate. Do not hand-edit it.

### If you would rather keep the screen

Only one alternative is coherent: add a `the-local-train` record to `src/data/band.ts` and make
`/band` a redirect to `/bands/the-local-train`. That is a **content** change — inventing a second
band — and content is Vedanshu's call, not Claude Code's. Do not take this path without him saying
so. Change (a) is correct either way.

### Acceptance

- `grep -rn 'to="/band"' src/` returns nothing.
- `src/routes/band.tsx` does not exist; `routeTree.gen.ts` regenerated, route count 207 → 206.
- Onboarding → complete, with `bandIntent` set, lands on `/bands/new`.
- `/bands` and `/bands/the-kite-notes` both render their own screens.

---

## Exit gate

```sh
bun install
bunx tsc --noEmit
bun run lint
bun run build
```

Then a 412px render pass on `/onboarding/complete`, `/bands`, `/bands/new` and
`/bands/the-kite-notes`.

Commit ends with, and only with:

```
Co-Authored-By: Wugweb <admin@wugweb.com>
```

---

## Found while measuring — NOT in this PR

**15 files reference Unsplash**, against a standing rule that photography comes from the local
library and never from stock:

```
components/ml/onboarding.tsx   routes/band.tsx   routes/studio.tsx
data/band.ts   data/booking.ts   data/business.ts   data/collab.ts
data/discover.ts   data/market.ts   data/notifications.ts
data/opportunities.ts   data/profile.ts   data/recommendations.ts
data/rentals.ts   data/reviews.ts
```

Change 2 removes one of them incidentally. The remaining 14 are blocked on the local photo library
existing, which is why this is named here rather than fixed. It belongs in Phase 8 or its own
branch once there are real images to swap in.
