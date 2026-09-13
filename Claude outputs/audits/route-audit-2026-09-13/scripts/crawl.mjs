// Live pass over the dev server: crawl rendered <a href> from entry points, then request every route.
// Usage: node crawl.mjs routes.json > crawl.json
import { readFileSync } from "node:fs";

const BASE = "http://localhost:8080";
const data = JSON.parse(readFileSync(process.argv[2], "utf8"));
const routes = data.routes;
const compiled = routes.map((r) => {
  const segs = r.path === "/" ? [] : r.path.replace(/\/$/, "").slice(1).split("/");
  return { r, re: new RegExp("^/" + segs.map((g) => (g.startsWith("$") ? "[^/]+" : g.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"))).join("/") + "/?$"), score: segs.map((g) => (g.startsWith("$") ? 1 : 2)).join("") };
});
const match = (u) => {
  const t = u.split(/[?#]/)[0];
  const hits = compiled.filter((c) => c.re.test(t)).sort((a, b) => (a.score < b.score ? 1 : -1));
  return hits[0]?.r.path;
};

async function get(url) {
  const t0 = Date.now();
  try {
    const res = await fetch(BASE + url, { redirect: "manual" });
    const html = res.status >= 300 && res.status < 400 ? "" : await res.text();
    const title = /<title>([^<]*)<\/title>/.exec(html)?.[1];
    const hrefs = [...html.matchAll(/<a\b[^>]*\bhref="(\/[^"#]*)"/g)].map((m) => m[1].replace(/&amp;/g, "&"));
    const nestedButtons = (html.match(/<a\b[^>]*>(?:(?!<\/a>)[\s\S])*?<button/g) || []).length;
    return { status: res.status, location: res.headers.get("location"), title, hrefs, nestedButtons, ms: Date.now() - t0 };
  } catch (e) {
    return { status: 0, error: String(e) };
  }
}

// 1. Crawl from the entry points, following rendered links (SSR only: sheets/tabs opened by JS are not seen).
const reached = {}; // route -> {url, depth, from}
const samples = {}; // route -> [concrete urls]
const queue = [["/", 0, "entry"], ["/welcome", 0, "entry"], ["/feed", 0, "entry"]];
const seen = new Set(queue.map((q) => q[0]));
let fetched = 0;
while (queue.length && fetched < 700) {
  const batch = queue.splice(0, 8);
  const results = await Promise.all(batch.map(([u]) => get(u)));
  fetched += batch.length;
  batch.forEach(([u, d, from], i) => {
    const route = match(u);
    if (route && !reached[route]) reached[route] = { url: u, depth: d, from };
    for (const h of results[i].hrefs || []) {
      const rp = match(h);
      if (!rp) continue;
      (samples[rp] ??= new Set()).add(h.split("#")[0]);
      const key = h.split("?")[0];
      // Crawl at most 2 concrete URLs per route pattern.
      if (!seen.has(key) && [...seen].filter((s) => match(s) === rp).length < 2) {
        seen.add(key);
        queue.push([key, d + 1, u]);
      }
    }
  });
}

// 2. Request every route once, using a crawled or code-referenced concrete URL for param routes.
const codeSamples = {};
for (const r of routes) {
  if (!r.path.includes("$")) continue;
}
const out = {};
const todo = routes.map((r) => {
  let url = r.path.includes("$") ? [...(samples[r.path] || [])][0] : r.path.replace(/\/$/, "") || "/";
  return [r.path, url];
});
for (let i = 0; i < todo.length; i += 8) {
  const batch = todo.slice(i, i + 8);
  const res = await Promise.all(batch.map(([, u]) => (u ? get(u.split("?")[0]) : null)));
  batch.forEach(([p, u], k) => {
    const x = res[k];
    out[p] = x
      ? { url: u, status: x.status, location: x.location, title: x.title, nestedButtons: x.nestedButtons, ms: x.ms, linksOut: new Set((x.hrefs || []).map(match).filter(Boolean)).size }
      : { url: null, status: null };
    out[p].crawled = reached[p] || null;
  });
}
console.log(JSON.stringify({ fetched, routes: out }, null, 1));
