import { readFileSync, writeFileSync } from "node:fs";
const R = JSON.parse(readFileSync(process.argv[2], "utf8")).routes;
const g = JSON.parse(readFileSync(process.argv[3], "utf8"));
const crawl = JSON.parse(readFileSync(process.argv[4], "utf8")).routes;
const samples = Object.fromEntries(readFileSync(process.argv[5], "utf8").trim().split("\n").map((l) => l.split("\t")).map((c) => [c[2], { status: c[0], url: c[3], title: c[4] }]));
const bad = Object.fromEntries(readFileSync(process.argv[6], "utf8").trim().split("\n").map((l) => l.split("\t")).map((c) => [c[3], { status: c[0], body: c[5] }]));
const area = (p) => (p === "/" ? "/" : "/" + p.split("/")[1]);
const groups = {};
for (const r of R) (groups[area(r.path)] ??= []).push(r);
let md = "";
const esc = (s) => String(s ?? "").replace(/\|/g, "\\|");
for (const [a, rows] of Object.entries(groups).sort()) {
  md += `\n### \`${a}\` (${rows.length})\n\n| Route | Hops from Home | Reached from | Live | Notes |\n|---|---|---|---|---|\n`;
  for (const r of rows.sort((x, y) => x.path.localeCompare(y.path))) {
    const c = g.returning[r.path];
    const n = g.newUser[r.path];
    const hops = r.path === "/" ? "app start" : r.path === "/welcome" ? "new user start" : c ? String(c.hops.length) : n ? "new-user flow" : "**unreachable**";
    const from = [...new Set((g.inbound[r.path] || []).map((x) => x.from))];
    const reached = from.length ? from.slice(0, 3).map((f) => `\`${f}\``).join(", ") + (from.length > 3 ? ` +${from.length - 3}` : "") : "nothing links here";
    const live = crawl[r.path]?.status ?? samples[r.path]?.status ?? "—";
    const notes = [];
    if (r.facts.beforeLoadRedirect) notes.push(`redirects → \`${r.facts.beforeLoadRedirect}\``);
    if (r.facts.loaderRedirect) notes.push(`loader redirect → \`${r.facts.loaderRedirect}\``);
    if (r.facts.notFound) notes.push("real 404 for unknown id");
    if (r.facts.authBounce) notes.push("signed-out → sign in");
    if (!r.facts.head) notes.push("no page title");
    md += `| \`${esc(r.path)}\` | ${hops} | ${reached} | ${live} | ${notes.join("; ")} |\n`;
  }
}
writeFileSync(process.argv[7], md);
console.log("rows", R.length, "chars", md.length);
