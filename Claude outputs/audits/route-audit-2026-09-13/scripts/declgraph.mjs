// Declaration-level link graph: a route reaches the links inside the top-level declarations it
// actually uses, followed by name across files. Much tighter than whole-file imports.
// Usage: node declgraph.mjs routes.json > graph.json   (run from the app repo root)
import { readFileSync, readdirSync, statSync } from "node:fs";
import { join, dirname } from "node:path";

const routesData = JSON.parse(readFileSync(process.argv[2], "utf8"));
const routes = routesData.routes;
const walk = (d) => readdirSync(d).flatMap((f) => { const p = join(d, f); return statSync(p).isDirectory() ? walk(p) : [p]; });
const files = walk("src").filter((f) => /\.tsx?$/.test(f) && !f.endsWith("routeTree.gen.ts") && !f.includes("/components/ui/"));
const text = Object.fromEntries(files.map((f) => [f, readFileSync(f, "utf8")]));
const lineOf = (s, i) => s.slice(0, i).split("\n").length;

const compiled = routes.map((r) => {
  const segs = r.path === "/" ? [] : r.path.replace(/\/$/, "").slice(1).split("/");
  return { p: r.path, re: new RegExp("^/" + segs.map((g) => (g.startsWith("$") ? "[^/]+" : g.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"))).join("/") + "/?$"), score: segs.map((g) => (g.startsWith("$") ? 1 : 2)).join("") };
});
const match = (t) => {
  t = t.split(/[?#]/)[0].replace(/\$\{[^}]*\}/g, "x");
  const exact = routes.find((r) => r.path === t || r.path === t + "/");
  if (exact) return exact.path;
  return compiled.filter((c) => c.re.test(t)).sort((a, b) => (a.score < b.score ? 1 : -1))[0]?.p;
};
const linkRe = /\b(to|back|href|route|publicRoute|path|url|link)\s*[:=]\s*\{?\s*(["'`])(\/[^"'`\s]*)\2/g;

function resolve(from, spec) {
  let base;
  if (spec.startsWith("@/")) base = join("src", spec.slice(2));
  else if (spec.startsWith(".")) base = join(dirname(from), spec);
  else return null;
  for (const ext of ["", ".tsx", ".ts", "/index.tsx", "/index.ts"]) if (text[base + ext] !== undefined) return base + ext;
  return null;
}

// Per file: top-level declarations, their spans, links, and identifier uses.
const decls = {}; // file -> [{name, start, end}]
const importsOf = {}; // file -> {localName: {file, name}}
for (const f of files) {
  const s = text[f];
  const list = [];
  for (const m of s.matchAll(/^(?:export\s+)?(?:default\s+)?(?:async\s+)?(?:function\*?|const|let|var|class|type|interface|enum)\s+([A-Za-z_$][\w$]*)/gm)) list.push({ name: m[1], start: m.index });
  list.sort((a, b) => a.start - b.start);
  list.forEach((d, i) => (d.end = i + 1 < list.length ? list[i + 1].start : s.length));
  decls[f] = list;
  const imp = {};
  for (const m of s.matchAll(/import\s+(type\s+)?([\s\S]*?)\s+from\s+["']([^"']+)["']/g)) {
    const target = resolve(f, m[3]);
    if (!target) continue;
    const clause = m[2];
    const named = /\{([\s\S]*)\}/.exec(clause)?.[1];
    if (named) for (const part of named.split(",")) {
      const p = part.replace(/\btype\s+/, "").trim();
      if (!p) continue;
      const [orig, local] = p.split(/\s+as\s+/);
      imp[(local || orig).trim()] = { file: target, name: orig.trim() };
    }
    const ns = /\*\s+as\s+(\w+)/.exec(clause)?.[1];
    if (ns) imp[ns] = { file: target, name: "*" };
    const def = /^([A-Za-z_$][\w$]*)/.exec(clause.trim())?.[1];
    if (def && def !== "type") imp[def] = { file: target, name: "default" };
  }
  importsOf[f] = imp;
}

const COUNT_ONLY = new Set(["unreadCount"]);
const nodeInfo = new Map(); // "file#name" -> {links:[{target,route,line}], uses:[keys]}
function info(file, name) {
  const key = `${file}#${name}`;
  if (nodeInfo.has(key)) return nodeInfo.get(key);
  const s = text[file];
  const spans = name === "*" ? decls[file] : decls[file].filter((d) => d.name === name);
  const body = spans.map((d) => [d.start, d.end]);
  // Code before the first declaration (e.g. module-level JSX config) counts too for "*".
  const res = { links: [], uses: [] };
  nodeInfo.set(key, res);
  const localNames = new Set(decls[file].map((d) => d.name));
  for (const [a, b] of body) {
    const chunk = s.slice(a, b);
    for (const m of chunk.matchAll(linkRe)) {
      const route = match(m[3]);
      if (route) res.links.push({ route, target: m[3], at: `${file.replace(/^src\//, "")}:${lineOf(s, a + m.index)}` });
    }
    // Conditional targets: every path literal inside to={…} or navigate({ to: cond ? "…" : "…" }).
    for (const m of chunk.matchAll(/\bto\s*[:=]\s*\{?([^{}\n]*\?[^{}\n]*:[^{}\n]*)/g)) {
      for (const lit of m[1].matchAll(/["'`](\/[^"'`\s]*)["'`]/g)) {
        const route = match(lit[1]);
        if (route) res.links.push({ route, target: lit[1], at: `${file.replace(/^src\//, "")}:${lineOf(s, a + m.index)}` });
      }
    }
    const ids = new Set(chunk.match(/[A-Za-z_$][\w$]*/g) || []);
    for (const id of ids) {
      if (localNames.has(id) && id !== name) res.uses.push(`${file}#${id}`);
      const im = importsOf[file][id];
      // Count-only helpers read a list without rendering its links.
      if (im && !COUNT_ONLY.has(im.name)) res.uses.push(`${im.file}#${im.name}`);
    }
  }
  return res;
}

// Links a route renders or navigates to = closure over used declarations from its Route decl.
function routeLinks(route) {
  const file = route.file;
  const seen = new Set();
  const stack = [`${file}#Route`];
  const out = [];
  while (stack.length) {
    const k = stack.pop();
    if (seen.has(k)) continue;
    seen.add(k);
    const [f, n] = [k.slice(0, k.lastIndexOf("#")), k.slice(k.lastIndexOf("#") + 1)];
    if (!text[f]) continue;
    const i = info(f, n === "default" ? "*" : n);
    out.push(...i.links);
    stack.push(...i.uses);
  }
  return out;
}

const edges = {};
for (const r of routes) {
  const ls = routeLinks(r).filter((l) => l.route !== r.path);
  const by = {};
  for (const l of ls) (by[l.route] ??= l.at);
  edges[r.path] = by;
}

function bfs(entry, skip = () => false) {
  const parent = new Map([[entry, null]]);
  const q = [entry];
  while (q.length) {
    const p = q.shift();
    for (const [t, at] of Object.entries(edges[p] || {})) if (!parent.has(t) && !skip(t)) { parent.set(t, { from: p, at }); q.push(t); }
  }
  const chain = (p) => { const hops = []; let cur = p; while (parent.get(cur)) { const x = parent.get(cur); hops.unshift({ to: cur, at: x.at }); cur = x.from; } return { start: cur, hops }; };
  return Object.fromEntries([...parent.keys()].map((p) => [p, chain(p)]));
}

const inbound = {};
for (const [from, by] of Object.entries(edges)) for (const [t, at] of Object.entries(by)) (inbound[t] ??= []).push({ from, at });

console.log(JSON.stringify({ edges, inbound, newUser: bfs("/welcome", (t) => t === "/feed" || t === "/discover"), returning: bfs("/feed", (t) => t === "/welcome" || (t.startsWith("/auth") && t !== "/auth/signout") || t === "/") }, null, 1));
