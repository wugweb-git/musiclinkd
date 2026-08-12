/**
 * Seeds Firestore (musiclinkd-7ae30) with mock directory + content data for
 * the admin portal. Run with `npm run seed` from admin/. Idempotent — every
 * doc uses a deterministic `mock-*` id, so reruns overwrite instead of
 * duplicating. See admin/docs/architecture/ for schema background and the
 * approved plan this implements.
 */
import { COLLECTIONS } from "@/config/constants";
import { getAdminApp } from "./admin-init";
import { stripUndefined } from "./lib";

import { ALL_STUDIOS } from "./data/studios";
import { ALL_USERS } from "./data/users";

import { OPEN_PROJECTS, PORTFOLIO_PROJECTS } from "./data/content";

import { BROADCAST_MESSAGES, CONNECTIONS, NOTIFICATIONS, REVIEWS } from "./data/engagement";

import { MARKETPLACE_ITEMS, STUDIO_BOOKINGS } from "./data/commerce";

import { AUDIT_LOGS, CAPABILITIES, FLOW_SCREENS, FLOWS, MODERATION_FLAGS, MODULES, PAGES, ROLE_PROFILES, VERIFICATION_REQUESTS, WIDGETS } from "./data/ops";

import { TAXONOMY_CATEGORIES, TAXONOMY_OPTIONS } from "./data/taxonomy-seed";

import { BRAND_ASSETS, CHAT_MESSAGES, CHAT_THREADS, FEED_POSTS, SUPPORT_TICKETS, USER_SETTINGS } from "./data/social";

import { getFirestore } from "firebase-admin/firestore";

type Op = { path: string; data: Record<string, unknown> };

function opsFor(collectionName: string, items: Array<{ id: string; data: object }>): Op[] {
  return items.map((item) => ({ path: `${collectionName}/${item.id}`, data: stripUndefined(item.data as Record<string, unknown>) }));
}

async function commit(db: FirebaseFirestore.Firestore, ops: Op[]) {
  const CHUNK = 400;
  for (let i = 0; i < ops.length; i += CHUNK) {
    const batch = db.batch();
    for (const op of ops.slice(i, i + CHUNK)) {
      batch.set(db.doc(op.path), op.data);
    }
    await batch.commit();
  }
}

async function main() {
  const app = getAdminApp();
  const db = getFirestore(app);

  const groups: Array<{ label: string; collection: string; ops: Op[] }> = [
    { label: "users", collection: COLLECTIONS.users, ops: opsFor(COLLECTIONS.users, ALL_USERS) },
    { label: "studios", collection: COLLECTIONS.studios, ops: opsFor(COLLECTIONS.studios, ALL_STUDIOS) },
    { label: "portfolio_projects", collection: COLLECTIONS.portfolioProjects, ops: opsFor(COLLECTIONS.portfolioProjects, PORTFOLIO_PROJECTS) },
    { label: "projects", collection: COLLECTIONS.projects, ops: opsFor(COLLECTIONS.projects, OPEN_PROJECTS) },
    { label: "reviews", collection: COLLECTIONS.reviews, ops: opsFor(COLLECTIONS.reviews, REVIEWS) },
    { label: "notifications", collection: COLLECTIONS.notifications, ops: opsFor(COLLECTIONS.notifications, NOTIFICATIONS) },
    { label: "connections", collection: COLLECTIONS.connections, ops: opsFor(COLLECTIONS.connections, CONNECTIONS) },
    { label: "broadcast_messages", collection: COLLECTIONS.broadcastMessages, ops: opsFor(COLLECTIONS.broadcastMessages, BROADCAST_MESSAGES) },
    { label: "marketplace_items", collection: COLLECTIONS.marketplaceItems, ops: opsFor(COLLECTIONS.marketplaceItems, MARKETPLACE_ITEMS) },
    { label: "studio_bookings", collection: COLLECTIONS.studioBookings, ops: opsFor(COLLECTIONS.studioBookings, STUDIO_BOOKINGS) },
    { label: "verification_requests", collection: COLLECTIONS.verificationRequests, ops: opsFor(COLLECTIONS.verificationRequests, VERIFICATION_REQUESTS) },
    { label: "moderation_flags", collection: COLLECTIONS.moderationFlags, ops: opsFor(COLLECTIONS.moderationFlags, MODERATION_FLAGS) },
    { label: "audit_logs", collection: COLLECTIONS.auditLogs, ops: opsFor(COLLECTIONS.auditLogs, AUDIT_LOGS) },
    { label: "role_profiles", collection: COLLECTIONS.roleProfiles, ops: opsFor(COLLECTIONS.roleProfiles, ROLE_PROFILES) },
    { label: "page_content", collection: COLLECTIONS.pageContent, ops: opsFor(COLLECTIONS.pageContent, PAGES) },
    { label: "capabilities", collection: COLLECTIONS.capabilities, ops: opsFor(COLLECTIONS.capabilities, CAPABILITIES) },
    { label: "modules", collection: COLLECTIONS.modules, ops: opsFor(COLLECTIONS.modules, MODULES) },
    { label: "flows", collection: COLLECTIONS.flows, ops: opsFor(COLLECTIONS.flows, FLOWS) },
    { label: "widgets", collection: COLLECTIONS.widgets, ops: opsFor(COLLECTIONS.widgets, WIDGETS) },
    {
      label: "flows/*/screens",
      collection: "flows/*/screens",
      ops: FLOW_SCREENS.map((s) => ({ path: `flows/${s.flowId}/screens/${s.id}`, data: stripUndefined(s.data) })),
    },
    { label: "taxonomy_categories", collection: COLLECTIONS.taxonomyCategories, ops: opsFor(COLLECTIONS.taxonomyCategories, TAXONOMY_CATEGORIES) },
    {
      label: "taxonomy_categories/*/options",
      collection: "taxonomy_categories/*/options",
      ops: TAXONOMY_OPTIONS.map((o) => ({ path: `${COLLECTIONS.taxonomyCategories}/${o.categoryId}/options/${o.id}`, data: stripUndefined(o.data) })),
    },
    { label: "feed_posts", collection: COLLECTIONS.feedPosts, ops: opsFor(COLLECTIONS.feedPosts, FEED_POSTS) },
    { label: "chat_threads", collection: COLLECTIONS.chatThreads, ops: opsFor(COLLECTIONS.chatThreads, CHAT_THREADS) },
    {
      label: "chat_threads/*/messages",
      collection: "chat_threads/*/messages",
      ops: CHAT_MESSAGES.map((m) => ({ path: `${COLLECTIONS.chatThreads}/${m.threadId}/messages/${m.id}`, data: stripUndefined(m.data as unknown as Record<string, unknown>) })),
    },
    { label: "support_tickets", collection: COLLECTIONS.supportTickets, ops: opsFor(COLLECTIONS.supportTickets, SUPPORT_TICKETS) },
    { label: "user_settings", collection: COLLECTIONS.userSettings, ops: opsFor(COLLECTIONS.userSettings, USER_SETTINGS) },
    { label: "brand_assets", collection: COLLECTIONS.brandAssets, ops: opsFor(COLLECTIONS.brandAssets, BRAND_ASSETS) },
  ];

  console.log(`Seeding project musiclinkd-7ae30 — ${groups.reduce((n, g) => n + g.ops.length, 0)} documents across ${groups.length} collections\n`);

  for (const group of groups) {
    await commit(db, group.ops);
    console.log(`  ${group.label.padEnd(24)} ${group.ops.length}`);
  }

  console.log("\nDone.");
}

main().catch((err) => {
  console.error("Seed failed:", err);
  process.exit(1);
});
