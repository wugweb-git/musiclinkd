import { readFileSync, writeFileSync } from "node:fs";
const g = JSON.parse(readFileSync(process.argv[2], "utf8"));
const R = JSON.parse(readFileSync(process.argv[3], "utf8")).routes;
const types = {
  "Talent (individual)": ["/me", "/profile/edit", "/portfolio", "/portfolio/new", "/opportunities", "/opportunities/$id/apply", "/opportunities/applications", "/opportunities/applications/$aid", "/collab", "/collab/invites/$id", "/provider/bookings", "/provider/bookings/requests", "/services", "/services/new", "/services/$id/manage", "/verification", "/identity/qr", "/plans/earnings", "/reviews", "/reviews/$id/reply", "/profile/insights"],
  "Hirer / client": ["/discover", "/search", "/search/why/$id", "/recommendations", "/profile/$username", "/opportunities/new", "/opportunities/mine", "/opportunities/$id/applicants", "/opportunities/$id/applicants/$aid", "/booking/$id", "/booking/$id/pay", "/bookings", "/bookings/$ref", "/reviews/write/$id", "/services/$id/enquiry", "/business/$id/enquiry", "/saved", "/messages"],
  "Band member": ["/bands", "/bands/new", "/bands/$id", "/bands/$id/manage", "/bands/$id/invite", "/bands/$id/events", "/bands/$id/portfolio", "/bands/$id/contact"],
  "Business owner": ["/business", "/business/new", "/business/$id", "/business/$id/manage", "/business/$id/dashboard", "/business/$id/analytics", "/business/$id/leads", "/business/$id/services", "/business/$id/availability", "/business/$id/portfolio", "/business/$id/reviews", "/business/$id/team", "/business/$id/verification", "/services/conflicts", "/provider/availability", "/provider/pricing"],
  "Seller / rental provider": ["/marketplace", "/marketplace/sell", "/marketplace/mine", "/marketplace/rent", "/marketplace/rent/list", "/marketplace/rent/listings", "/marketplace/rent/mine", "/marketplace/rent/request/$id", "/marketplace/rent/active/$id"],
  "Everyone": ["/notifications", "/notifications/settings", "/settings", "/settings/account", "/settings/privacy", "/settings/security", "/settings/delete-account", "/help", "/plans", "/auth/signout", "/recently-viewed", "/search/history", "/search/saved", "/discover/music", "/discover/projects", "/discover/community", "/discover/nearby"],
};
const out = {};
for (const [t, list] of Object.entries(types)) out[t] = list.map((p) => {
  if (!R.find((r) => r.path === p)) return { path: p, missing: true };
  const c = g.returning[p];
  const inb = (g.inbound[p] || []).map((x) => x.from);
  return { path: p, hops: c ? c.hops.length : null, chain: c ? [c.start, ...c.hops.map((h) => h.to)] : null, at: c ? c.hops.map((h) => h.at) : null, inboundFrom: [...new Set(inb)] };
});
writeFileSync(process.argv[4], JSON.stringify(out, null, 1));
for (const [t, rows] of Object.entries(out)) {
  console.log(`\n## ${t}`);
  for (const r of rows) console.log(r.missing ? `  MISSING ROUTE ${r.path}` : `  ${String(r.hops ?? "✗").padStart(2)}  ${r.path.padEnd(34)} ${r.chain ? r.chain.join(" → ") : "unreachable; linked from: " + (r.inboundFrom.join(", ") || "nothing")}`);
}
