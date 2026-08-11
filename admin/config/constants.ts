/**
 * Single source of truth for every string identifier used across services and
 * modules — collection names, storage path prefixes, roles, feature-flag keys.
 * Nothing outside this file should hardcode a Firestore collection name.
 */
import type { AdminRole } from "@/types/firestore";

export const COLLECTIONS = {
  users: "users",
  adminRoles: "admin_roles",
  connections: "connections",
  feedPosts: "feed_posts",
  studioBookings: "studio_bookings",
  chatThreads: "chat_threads",
  studios: "studios",
  projects: "projects",
  marketplaceItems: "marketplace_items",
  reviews: "reviews",
  verificationRequests: "verification_requests",
  moderationFlags: "moderation_flags",
  supportTickets: "support_tickets",
  broadcastMessages: "broadcast_messages",
  auditLogs: "audit_logs",
  pageContent: "page_content",
  taxonomyCategories: "taxonomy_categories",
  modules: "modules",
  portfolioProjects: "portfolio_projects",
  notifications: "notifications",
  userSettings: "user_settings",
  brandAssets: "brand_assets",
} as const;

export const STORAGE_PATHS = {
  avatar: (uid: string) => `users/${uid}/avatar`,
  cover: (uid: string) => `users/${uid}/cover`,
  portfolio: (uid: string) => `users/${uid}/portfolio`,
  audio: (uid: string) => `audio/${uid}`,
  stems: (uid: string, projectId: string) => `stems/${uid}/${projectId}`,
  studioGallery: (studioId: string) => `studios/${studioId}`,
  verificationDocuments: (uid: string) => `verification_documents/${uid}`,
  chatAttachments: (threadId: string) => `chat_attachments/${threadId}`,
} as const;

export const ADMIN_ROLES = ["ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_SUPPORT"] as const;

/** Which admin roles may access which module. Checked client-side by the route guard. */
function perm(...roles: AdminRole[]): AdminRole[] {
  return roles;
}

export const MODULE_PERMISSIONS = {
  dashboard: perm("ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_SUPPORT"),
  users: perm("ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_SUPPORT"),
  studios: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  marketplace: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  bookings: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  projects: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  portfolio: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  reviews: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  verification: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  moderation: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  messages: perm("ROLE_ADMIN"),
  notifications: perm("ROLE_ADMIN"),
  reports: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  pages: perm("ROLE_ADMIN"),
  analytics: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  tracks: perm("ROLE_ADMIN", "ROLE_MODERATOR"),
  "audit-logs": perm("ROLE_ADMIN"),
  settings: perm("ROLE_ADMIN"),
  "master-data": perm("ROLE_ADMIN"),
  modules: perm("ROLE_ADMIN"),
} satisfies Record<string, AdminRole[]>;

export type ModuleKey = keyof typeof MODULE_PERMISSIONS;

/**
 * Modules the registry (see the `modules` collection / admin Modules page)
 * is never allowed to disable, regardless of what an admin sets — core
 * admin infrastructure can't be allowed to lock itself out.
 */
export const NON_TOGGLEABLE_MODULES: ModuleKey[] = ["dashboard", "settings", "master-data", "modules"];

/** Remote Config parameter keys — must match remoteconfig.template.json at repo root. */
export const FEATURE_FLAGS = {
  maintenanceMode: "maintenance_mode",
  minimumAppVersion: "minimum_app_version",
  supportContactEmail: "support_contact_email",
} as const;

export const FEATURE_FLAG_DEFAULTS = {
  [FEATURE_FLAGS.maintenanceMode]: false,
  [FEATURE_FLAGS.minimumAppVersion]: "1.0.0",
  [FEATURE_FLAGS.supportContactEmail]: "hello@musiclinkd.com",
};

/**
 * Modules with no backing Firestore collection yet. "pages" was mock (as
 * "cms") until Page Management got a real collection (page_content);
 * "analytics" and "tracks" were mock at launch but are now both fully wired
 * to live Firestore data (analytics: real aggregate counts across
 * collections; tracks: a real filtered view over marketplace_items) —
 * removed from this list so the sidebar stops showing a stale "mock" badge
 * on modules that actually work.
 */
export const MOCK_ONLY_MODULES: ModuleKey[] = [];

export const ANALYTICS_EVENTS = {
  adminLogin: "admin_login",
  adminLogout: "admin_logout",
  moderationActioned: "moderation_actioned",
  verificationReviewed: "verification_reviewed",
  broadcastSent: "broadcast_sent",
  recordDeleted: "record_deleted",
} as const;
