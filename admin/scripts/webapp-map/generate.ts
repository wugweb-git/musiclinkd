/**
 * Generates webapp/'s flow → screen → schema map, mechanically, from the
 * same source-of-truth files the admin panel and Firestore seed already
 * use: DEFAULT_FLOWS, DEFAULT_SCREENS, and the seed data's own realistic
 * sample documents. Nothing here is hand-typed, so it can't drift out of
 * sync with the real schema the way a hand-maintained JSON file would.
 *
 * Run with: cd admin && npx tsx scripts/webapp-map/generate.ts
 * Safe to re-run any time default-flows.ts/default-screens.ts change —
 * it fully regenerates webapp/data/ and webapp/flows/*\/*\/screen.json,
 * but never touches a screen's index.html once one exists (so design work
 * already done on a screen is never clobbered by a re-run).
 */
import { existsSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";
import { DEFAULT_FLOWS } from "@/app/(admin)/flows/default-flows";
import { DEFAULT_SCREENS } from "@/app/(admin)/flows/screens/default-screens";

import { ALL_STUDIOS } from "../seed/data/studios";
import { ALL_USERS } from "../seed/data/users";
import { OPEN_PROJECTS, PORTFOLIO_PROJECTS } from "../seed/data/content";
import { BROADCAST_MESSAGES, CONNECTIONS, NOTIFICATIONS, REVIEWS } from "../seed/data/engagement";
import { MARKETPLACE_ITEMS, STUDIO_BOOKINGS } from "../seed/data/commerce";
import { AUDIT_LOGS, CAPABILITIES, FLOWS as SEED_FLOWS, MODERATION_FLAGS, MODULES, PAGES, ROLE_PROFILES, VERIFICATION_REQUESTS, WIDGETS } from "../seed/data/ops";
import { GENRES, INSTRUMENTS, LANGUAGES, PERSON_TYPE_SLUGS } from "../seed/data/taxonomy";

const WEBAPP_ROOT = join(__dirname, "..", "..", "..", "webapp");

/** Firestore Timestamp-shaped values (from firebase-admin, in these seed modules) → ISO string, for a readable JSON contract. */
function serializeSample(value: unknown): unknown {
  if (Array.isArray(value)) return value.map(serializeSample);
  if (value !== null && typeof value === "object") {
    const v = value as Record<string, unknown>;
    if (typeof v.toDate === "function") return (v.toDate as () => Date)().toISOString();
    if ("_seconds" in v && "_nanoseconds" in v) return new Date((v._seconds as number) * 1000).toISOString();
    const out: Record<string, unknown> = {};
    for (const [k, val] of Object.entries(v)) out[k] = serializeSample(val);
    return out;
  }
  return value;
}

/** One representative real sample per collection, pulled straight from the seed data already used to populate Firestore — not fabricated separately. */
const COLLECTION_SAMPLES: Record<string, unknown> = {
  users: serializeSample(ALL_USERS[0]?.data),
  studios: serializeSample(ALL_STUDIOS[0]?.data),
  reviews: serializeSample(REVIEWS[0]?.data),
  portfolio_projects: serializeSample(PORTFOLIO_PROJECTS[0]?.data),
  projects: serializeSample(OPEN_PROJECTS[0]?.data),
  marketplace_items: serializeSample(MARKETPLACE_ITEMS[0]?.data),
  studio_bookings: serializeSample(STUDIO_BOOKINGS[0]?.data),
  notifications: serializeSample(NOTIFICATIONS[0]?.data),
  connections: serializeSample(CONNECTIONS[0]?.data),
  broadcast_messages: serializeSample(BROADCAST_MESSAGES[0]?.data),
  verification_requests: serializeSample(VERIFICATION_REQUESTS[0]?.data),
  moderation_flags: serializeSample(MODERATION_FLAGS[0]?.data),
  audit_logs: serializeSample(AUDIT_LOGS[0]?.data),
  role_profiles: serializeSample(ROLE_PROFILES[0]?.data),
  page_content: serializeSample(PAGES[0]?.data),
  capabilities: serializeSample(CAPABILITIES[0]?.data),
  modules: serializeSample(MODULES[0]?.data),
  widgets: serializeSample(WIDGETS[0]?.data),
  flows: serializeSample(SEED_FLOWS[0]?.data),
  taxonomy_categories: {
    _note: "Not a single-doc shape — taxonomy_categories/{key}/options/{id}. Three representative categories shown.",
    genres: { key: "genres", options: GENRES.slice(0, 5) },
    person_types: { key: "person_types", options: Object.entries(PERSON_TYPE_SLUGS).slice(0, 5).map(([label, value]) => ({ label, value })) },
    languages: { key: "languages", options: LANGUAGES.slice(0, 5) },
    instruments: { key: "instruments", options: INSTRUMENTS.slice(0, 5) },
  },
};

function sampleFor(collection: string): { collection: string; sample: unknown; note?: string } {
  if (collection in COLLECTION_SAMPLES) return { collection, sample: COLLECTION_SAMPLES[collection] };
  return { collection, sample: null, note: "No seed sample available for this collection in this build." };
}

function ensureDir(path: string) {
  if (!existsSync(path)) mkdirSync(path, { recursive: true });
}

type ScreenRecord = {
  flowKey: string;
  flowTitle: string;
  flowStatus: string;
  flowOrder: number;
  screenKey: string;
  screenNumber?: number;
  screenTitle: string;
  screenOrder: number;
  states: string[];
  collectionsTouched: string[];
  notes?: string;
  dataContract: Array<{ collection: string; sample: unknown; note?: string }>;
};

const allScreens: ScreenRecord[] = [];
const flowSummaries: Array<{
  key: string;
  title: string;
  order: number;
  status: string;
  description: string;
  screenCount: number;
}> = [];

for (const flow of DEFAULT_FLOWS) {
  const screens = DEFAULT_SCREENS[flow.key] ?? [];
  flowSummaries.push({
    key: flow.key,
    title: flow.title,
    order: flow.order,
    status: flow.status,
    description: flow.description,
    screenCount: screens.length,
  });

  const flowDir = join(WEBAPP_ROOT, "flows", flow.key);
  ensureDir(flowDir);
  writeFileSync(
    join(flowDir, "_flow.json"),
    JSON.stringify({ key: flow.key, title: flow.title, order: flow.order, status: flow.status, description: flow.description, screenCount: screens.length }, null, 2),
  );

  for (const screen of screens) {
    const collectionsTouched = screen.collectionsTouched ?? [];
    const record: ScreenRecord = {
      flowKey: flow.key,
      flowTitle: flow.title,
      flowStatus: flow.status,
      flowOrder: flow.order,
      screenKey: screen.key,
      screenNumber: (screen as { screenNumber?: number }).screenNumber,
      screenTitle: screen.title,
      screenOrder: screen.order,
      states: screen.states ?? ["default"],
      collectionsTouched,
      notes: screen.notes,
      dataContract: collectionsTouched.map(sampleFor),
    };
    allScreens.push(record);

    const screenDir = join(flowDir, screen.key);
    ensureDir(screenDir);
    writeFileSync(join(screenDir, "screen.json"), JSON.stringify(record, null, 2));

    // Only scaffold index.html if one doesn't exist yet — never clobber design work.
    const indexPath = join(screenDir, "index.html");
    if (!existsSync(indexPath)) {
      const templatePath = join(WEBAPP_ROOT, "template", "index.html");
      // template/index.html already uses absolute /preview/... paths (see
      // its own header comment for why relative paths break under
      // cleanUrls+trailingSlash:false), so no path rewriting is needed here
      // regardless of this screen's folder depth — just inject the one
      // thing that's genuinely per-screen: where its own screen.json lives.
      const screenJsonMeta = `<meta name="screen-json-path" content="/preview/flows/${flow.key}/${screen.key}/screen.json" />`;
      const template = readFileSync(templatePath, "utf8")
        .replace("<title>MusicLinkd — Template Screen</title>", `${screenJsonMeta}\n<title>MusicLinkd — ${flow.title} / ${screen.title}</title>`);
      writeFileSync(indexPath, template);
    }
  }
}

ensureDir(join(WEBAPP_ROOT, "data"));
writeFileSync(join(WEBAPP_ROOT, "data", "flows-map.json"), JSON.stringify({ generatedAt: new Date().toISOString(), flows: flowSummaries, screens: allScreens }, null, 2));

console.log(`Generated ${flowSummaries.length} flows, ${allScreens.length} screens.`);
console.log(`  flows with 0 screens (confirmed no real UI — see their description): ${flowSummaries.filter((f) => f.screenCount === 0).map((f) => f.key).join(", ")}`);
