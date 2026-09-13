// Static route map: every route, its file, what it does on load, and every code reference to it.
// Usage: node routemap.mjs > routes.json   (run from the app repo root)
import { readFileSync, readdirSync, statSync, existsSync } from "node:fs";
import { join, dirname, resolve, relative } from "node:path";

const root = process.cwd();
const tree = readFileSync("src/routeTree.gen.ts", "utf8");

// ---- 1. Routes from the generated tree (exact) ----
const importOf = {}; // XRouteImport -> file
for (const m of tree.matchAll(/import \{\s*Route as (\w+),?\s*\} from '\.\/routes\/([^']+)'/g)) importOf[m[1]] = `src/routes/${m[2]}.tsx`;
const constToImport = {};
for (const m of tree.matchAll(/const (\w+) =\s*(\w+)\.update\(/g)) constToImport[m[1]] = m[2];
const byFull = tree.slice(tree.indexOf("export interface FileRoutesByFullPath"));
const block = byFull.slice(0, byFull.indexOf("}"));
const routes = [];
for (const m of block.matchAll(/'([^']+)': typeof (\w+)/g)) {
  const file = importOf[constToImport[m[2].replace(/WithChildren$/, "")]];
  let path = m[1];
  if (path.length > 1 && path.endsWith("/")) path = path.slice(0, -1);
  if (routes.some((x) => x.path === path)) path = m[1];
  routes.push({ path, file });
}

// ---- 2. Per-file facts ----
const walk = (d) => readdirSync(d).flatMap((f) => { const p = join(d, f); return statSync(p).isDirectory() ? walk(p) : [p]; });
const srcFiles = walk("src").filter((f) => /\.(tsx?|mts)$/.test(f) && !f.endsWith("routeTree.gen.ts"));
const text = Object.fromEntries(srcFiles.map((f) => [f, readFileSync(f, "utf8")]));
const lineOf = (s, idx) => s.slice(0, idx).split("\n").length;

for (const r of routes) {
  const s = text[r.file] || "";
  const redirectTo = /redirect\(\{\s*to:\s*["'`]([^"'`]+)/.exec(s)?.[1];
  r.facts = {
    beforeLoadRedirect: /beforeLoad/.test(s) && redirectTo ? redirectTo : undefined,
    loaderRedirect: !/beforeLoad/.test(s) && redirectTo ? redirectTo : undefined,
    loader: /\bloader\s*:/.test(s),
    notFound: /notFound\(/.test(s),
    validateSearch: /validateSearch/.test(s),
    usesAuth: /useAuth\(/.test(s),
    authBounce: /!signedIn|signedIn\s*===\s*false/.test(s) && /["'`]\/(auth|welcome)/.test(s),
    title: /title:\s*["'`]([^"'`]+)/.exec(s)?.[1],
    head: /\bhead\s*:/.test(s),
    bottomNav: /<Screen(?![^>]*bottomNav=\{false\})/.test(s) ? "yes" : /bottomNav=\{false\}/.test(s) ? "no" : "n/a",
    back: /\bback=\{?\s*["'`]([^"'`]+)/.exec(s)?.[1],
    lines: s.split("\n").length,
  };
}

// ---- 3. Pattern matching (static segments beat params, like the router) ----
const compiled = routes.map((r) => {
  const segs = r.path === "/" ? [] : r.path.slice(1).split("/");
  return { r, segs, re: new RegExp("^" + (r.path === "/" ? "/" : "/" + segs.map((g) => (g.startsWith("$") ? "[^/]+" : g.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"))).join("/")) + "/?$"), score: segs.map((g) => (g.startsWith("$") ? 1 : 2)).join("") };
});
function match(target) {
  let t = target.split(/[?#]/)[0];
  if (t.length > 1 && t.endsWith("/")) t = t.slice(0, -1);
  const exact = compiled.find((c) => c.r.path === t);
  if (exact) return exact.r;
  const hits = compiled.filter((c) => c.re.test(t.replace(/\$\{[^}]*\}|\*/g, "x")));
  hits.sort((a, b) => (a.score < b.score ? 1 : -1));
  return hits[0]?.r;
}

// ---- 4. References ----
const refPatterns = [
  { kind: "link", re: /\bto\s*[:=]\s*\{?\s*(["'`])(\/[^"'`]*)\1/g },
  { kind: "back", re: /\bback\s*[:=]\s*\{?\s*(["'`])(\/[^"'`]*)\1/g },
  { kind: "data", re: /\b(?:href|route|path|url|link|publicRoute|cta|next|returnTo|redirect)\s*[:=]\s*\{?\s*(["'`])(\/[^"'`\s]*)\1/g },
];
const refs = [];
const unmatched = [];
for (const f of srcFiles) {
  const s = text[f];
  for (const { kind, re } of refPatterns) {
    for (const m of s.matchAll(re)) {
      const target = m[2];
      if (/\.(png|jpe?g|svg|webp|gif|ico|css|js|json|mp3|wav|pdf|woff2?)$/i.test(target.split("?")[0])) continue;
      if (/^\/(api|assets|img|images|fonts|__)\b/.test(target)) continue;
      const route = match(target);
      const ref = { file: f, line: lineOf(s, m.index), kind, target, route: route?.path };
      if (route) refs.push(ref); else unmatched.push(ref);
    }
  }
}

// ---- 5. Import graph + reachability from entry points ----
function resolveImport(from, spec) {
  let base;
  if (spec.startsWith("@/")) base = join("src", spec.slice(2));
  else if (spec.startsWith(".")) base = join(dirname(from), spec);
  else return null;
  for (const ext of ["", ".tsx", ".ts", "/index.tsx", "/index.ts"]) if (text[base + ext] !== undefined) return base + ext;
  return null;
}
const imports = {};
for (const f of srcFiles) imports[f] = [...text[f].matchAll(/from\s+["']([^"']+)["']/g)].map((m) => resolveImport(f, m[1])).filter(Boolean);
const linksFromFile = {};
for (const x of refs) (linksFromFile[x.file] ??= new Set()).add(x.route);
const routeByPath = Object.fromEntries(routes.map((r) => [r.path, r]));

function reach(entries, followImports) {
  const seenR = new Map(), seenF = new Set();
  const q = entries.map((p) => [p, 0, "entry"]);
  entries.forEach((p) => seenR.set(p, { depth: 0, via: "entry" }));
  while (q.length) {
    const [p, d] = q.shift();
    const files = [], stack = [routeByPath[p].file];
    const local = new Set();
    while (stack.length) {
      const f = stack.pop();
      if (!f || local.has(f)) continue;
      local.add(f); files.push(f);
      if (followImports) imports[f].filter((i) => !i.startsWith("src/routes/")).forEach((i) => stack.push(i));
    }
    // the root layout's links (BottomNav lives in ui.tsx via Screen) apply when followImports
    for (const f of files) for (const t of linksFromFile[f] || []) {
      if (!seenR.has(t)) { seenR.set(t, { depth: d + 1, via: p, file: f.replace(/^src\//, '') }); q.push([t, d + 1]); }
    }
  }
  return seenR;
}
const entries = ["/"];
const reachable = reach(entries, true);
const chainOf = (m, p) => { const out = []; let cur = p; while (cur && m.get(cur) && m.get(cur).via !== "entry") { out.unshift(cur); cur = m.get(cur).via; } if (cur) out.unshift(cur); return out; };
const newUser = reach(["/welcome"], true);
const returning = reach(["/feed"], true);
// Direct only: links written in the route file itself (no components/data) — a lower bound on reachability.
const reachableDirect = reach(entries, false);

for (const r of routes) {
  const inbound = refs.filter((x) => x.route === r.path && x.file !== r.file);
  r.inbound = inbound.length;
  r.inboundFiles = [...new Set(inbound.map((x) => x.file.replace(/^src\//, "")))];
  r.reachable = reachable.get(r.path) || null;
  r.newUser = newUser.get(r.path) ? chainOf(newUser, r.path) : null;
  r.returning = returning.get(r.path) ? chainOf(returning, r.path) : null;
  r.reachableDirect = Boolean(reachableDirect.get(r.path));
  r.outbound = [...(linksFromFile[r.file] || [])].filter((t) => t !== r.path).sort();
}

console.log(JSON.stringify({ generated: new Date().toISOString(), count: routes.length, routes, unmatched }, null, 1));
