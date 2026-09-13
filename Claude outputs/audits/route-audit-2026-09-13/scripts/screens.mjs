// Hidden-screen scan: what each route file can show besides its default view.
// Usage: node screens.mjs routes.json > screens.json   (run from the app repo root)
import { readFileSync, readdirSync, statSync } from "node:fs";
import { join } from "node:path";

const routes = JSON.parse(readFileSync(process.argv[2], "utf8")).routes;
const walk = (d) => readdirSync(d).flatMap((f) => { const p = join(d, f); return statSync(p).isDirectory() ? walk(p) : [p]; });
const all = walk("src").filter((f) => /\.tsx?$/.test(f) && !f.endsWith("routeTree.gen.ts"));
const text = Object.fromEntries(all.map((f) => [f, readFileSync(f, "utf8")]));
const lineOf = (s, i) => s.slice(0, i).split("\n").length;
const lits = (s) => [...s.matchAll(/["']([\w-]+)["']/g)].map((m) => m[1]);

const OVERLAY = /<([A-Z]\w*(?:Sheet|Modal|Dialog|Drawer|Popover)|Sheet|Dialog|AlertDialog|Drawer)\b([^>]*)>/g;
const SHELL = /<(Screen|AuthScreen|StateCard|EmptyState|Loading|NotFoundState|ErrorState|Skeleton)\b/;

function scan(file) {
  const s = text[file];
  const out = { variants: [], states: [], overlays: [], tabs: [] };

  // Search-param variants: validateSearch plus literal option arrays near it.
  const vs = /validateSearch[\s\S]*?\}\)\s*,?/.exec(s);
  if (vs) {
    for (const m of s.matchAll(/const (\w+)\s*=\s*\[([^\]]+)\]\s*as const/g)) out.variants.push({ param: m[1], values: lits(m[2]), line: lineOf(s, m.index) });
    for (const m of vs[0].matchAll(/search\[["'](\w+)["']\]\s*===\s*["']([\w-]+)["']\s*\?\s*["']([\w-]+)["']\s*:\s*["']([\w-]+)["']/g)) out.variants.push({ param: m[1], values: [...new Set([m[3], m[4]])], line: lineOf(s, vs.index) });
    for (const m of vs[0].matchAll(/(\w+):\s*\(?\s*\[([^\]]+)\]/g)) if (lits(m[2]).length > 1) out.variants.push({ param: m[1], values: lits(m[2]), line: lineOf(s, vs.index) });
  }

  // State-machine views: useState over a union of string literals.
  for (const m of s.matchAll(/const \[(\w+), set\w+\]\s*=\s*useState<([^>]+)>\(\s*["']([\w-]+)["']\s*\)/g)) {
    const values = lits(m[2]);
    if (values.length > 1) out.states.push({ name: m[1], values, line: lineOf(s, m.index) });
  }
  for (const m of s.matchAll(/const \[(step|view|mode|stage|phase|screen|status|state|tab|flow)\w*, (set\w+)\]\s*=\s*useState\(\s*["']([\w-]+)["']\s*\)/gi)) {
    const setter = m[2];
    const values = new Set([m[3], ...[...s.matchAll(new RegExp(`${setter}\\(\\s*["']([\\w-]+)["']`, "g"))].map((x) => x[1])]);
    if (values.size > 1) out.states.push({ name: m[1], values: [...values], line: lineOf(s, m.index) });
  }

  // Early-return states: `if (cond) return <Shell…` or `if (cond) { return (… <Shell`.
  for (const m of s.matchAll(/^\s*if \((.{1,90}?)\)\s*(?:\{\s*)?return\s*\(?\s*([\s\S]{0,260})/gm)) {
    const shell = SHELL.exec(m[2]);
    if (!shell) continue;
    const title = /(?:title|heading)=["{]\s*["'`]?([^"'`}]{2,60})/.exec(m[2])?.[1];
    out.states.push({ name: "early return", when: m[1].trim(), shell: shell[1], title, line: lineOf(s, m.index) });
  }

  // Overlays rendered by the page.
  for (const m of s.matchAll(OVERLAY)) {
    if (/^(SheetContent|SheetHeader|SheetTitle|DialogContent|DialogHeader|DialogTitle|DialogFooter|DialogDescription|SheetDescription|SheetFooter)$/.test(m[1])) continue;
    const title = /title=\{?\s*["'`]([^"'`]+)/.exec(m[2])?.[1];
    out.overlays.push({ component: m[1], title, line: lineOf(s, m.index) });
  }

  // Tabs / segmented views.
  for (const m of s.matchAll(/<(Segmented|Tabs|TabBar|ProfileTabs)\b([\s\S]{0,300}?)\/?>/g)) {
    const opts = /(?:options|tabs|items)=\{([^}]*\]?)\}?/.exec(m[2]);
    out.tabs.push({ component: m[1], values: opts ? lits(opts[1]) : [], line: lineOf(s, m.index) });
  }
  return out;
}

const perRoute = routes.map((r) => ({ path: r.path, file: r.file, ...scan(r.file) }));

// Screen-like components defined outside routes and used by nothing.
const orphans = [];
for (const f of all.filter((x) => !x.startsWith("src/routes/") && !x.includes("/components/ui/"))) {
  for (const m of text[f].matchAll(/export function ([A-Z]\w*)/g)) {
    const name = m[1];
    const used = all.some((g) => g !== f && new RegExp(`\\b${name}\\b`).test(text[g])) || new RegExp(`<${name}\\b`).test(text[f].replace(m[0], ""));
    if (!used) orphans.push({ name, at: `${f.replace(/^src\//, "")}:${lineOf(text[f], m.index)}`, rendersScreen: SHELL.test(text[f].slice(m.index, m.index + 3000)) });
  }
}
// Modules nothing imports.
const imported = new Set();
for (const f of all) for (const m of text[f].matchAll(/from\s+["'](@\/[^"']+|\.{1,2}\/[^"']+)["']/g)) imported.add(m[1].replace(/^@\//, "").replace(/^\.\.?\//, "").split("/").pop());
const deadModules = all.filter((f) => !f.startsWith("src/routes/") && !/router|server|start|styles|routeTree|\.d\.ts/.test(f)).filter((f) => !imported.has(f.split("/").pop().replace(/\.tsx?$/, "")));

console.log(JSON.stringify({ perRoute, orphans, deadModules }, null, 1));
