/**
 * Mirrors app/docs/firestore/Firestore_Schema_Index_Rules.md exactly.
 * Keep both in sync by hand when the schema changes.
 */
import type { Timestamp } from "firebase/firestore";

export type AdminRole = "ROLE_ADMIN" | "ROLE_MODERATOR" | "ROLE_SUPPORT";

/**
 * Replaced the old hardcoded 5-value UserRole enum (ARTIST/PRODUCER/
 * VOCALIST/STUDIO/LABEL) — the app's real onboarding collects one of 17
 * granular roles (see the "person_types" master-data category), as
 * primaryRole (required) + optional secondaryRole, matching what
 * OnboardingViewModel.kt actually writes today. Values are dynamic
 * (admin-managed master data), not a fixed union — see Users_And_Masters
 * notes in admin/docs/architecture/ for the seed list.
 */
export interface UserDoc {
  displayName: string;
  email: string;
  primaryRole: string;
  secondaryRole: string;
  isVerified: boolean;
  isBanned: boolean;
  isAvailable: boolean;
  hourlyRate: string;
  location: string;
  bio: string;
  genres: string[];
  languages: string[];
  instruments: string[];
  personaBadges: string[];
  avatarUrl: string;
  createdAt: Timestamp;
}

export interface AdminRoleDoc {
  role: AdminRole;
  grantedBy: string;
  grantedAt: Timestamp;
}

export interface ConnectionDoc {
  senderUid: string;
  targetUserId: string;
  status: "PENDING" | "ACCEPTED" | "DECLINED";
}

export interface FeedPostDoc {
  authorUid: string;
  postType: string;
  audioUrl?: string;
  text?: string;
  likesCount: number;
  timestamp: Timestamp;
}

export interface StudioBookingDoc {
  studioId: string;
  userId: string;
  date: string;
  slotText: string;
  totalAmount: number;
}

export interface ChatThreadDoc {
  memberUids: string[];
}

export interface ChatMessageDoc {
  senderUid: string;
  text: string;
  sentAt: Timestamp;
}

export interface StudioDoc {
  ownerUid: string;
  name: string;
  location: string;
  rating: number;
  reviewCount: number;
  hourlyRate: string;
  verifiedGold: boolean;
  amenities: string[];
  consoleType: string;
  imageUrl: string;
}

export type ProjectStatus = "OPEN" | "IN_PROGRESS" | "COMPLETED";

export interface ProjectDoc {
  creatorUid: string;
  title: string;
  roleRequired: string;
  budget: string;
  status: ProjectStatus;
  genres: string[];
  createdAt: Timestamp;
}

export type MarketplaceCategory = "GEAR" | "STEMS" | "SERVICES";
export type MarketplaceCondition = "NEW" | "LIKE_NEW" | "USED";

export interface MarketplaceItemDoc {
  sellerUid: string;
  title: string;
  price: string;
  category: MarketplaceCategory;
  condition: MarketplaceCondition;
  imageUrl: string;
  createdAt: Timestamp;
}

export interface ReviewDoc {
  targetType: "STUDIO" | "USER";
  targetId: string;
  authorUid: string;
  rating: number;
  text: string;
  createdAt: Timestamp;
}

export type VerificationStatus = "PENDING" | "APPROVED" | "REJECTED";

export interface VerificationRequestDoc {
  subjectType: "USER" | "STUDIO";
  subjectId: string;
  submittedBy: string;
  status: VerificationStatus;
  documentPaths: string[];
  submittedAt: Timestamp;
  reviewedBy?: string;
  reviewedAt?: Timestamp;
  notes?: string;
}

export type ModerationTargetType = "POST" | "AUDIO" | "REVIEW" | "MARKETPLACE_ITEM" | "USER";
export type ModerationStatus = "PENDING" | "ACTIONED" | "DISMISSED";

export interface ModerationFlagDoc {
  targetType: ModerationTargetType;
  targetId: string;
  reportedBy: string;
  reason: string;
  status: ModerationStatus;
  createdAt: Timestamp;
  reviewedBy?: string;
  reviewedAt?: Timestamp;
}

export type SupportTicketStatus = "OPEN" | "IN_PROGRESS" | "RESOLVED";

export interface SupportTicketDoc {
  requesterUid: string;
  requesterEmail: string;
  subject: string;
  message: string;
  status: SupportTicketStatus;
  assignedTo?: string;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

export type BroadcastStatus = "QUEUED" | "SENT" | "FAILED";

export interface BroadcastMessageDoc {
  title: string;
  body: string;
  audience: string;
  sentBy: string;
  sentAt: Timestamp;
  status: BroadcastStatus;
}

export interface AuditLogDoc {
  actorUid: string;
  action: string;
  targetPath: string;
  timestamp: Timestamp;
  metadata?: Record<string, unknown>;
}

export type PageStatus = "DRAFT" | "PUBLISHED";

/**
 * Known gap, not silently dropped: frontend/website/ is static HTML and
 * never reads this collection at runtime, so a save here doesn't change
 * what's live. The real fix is a build-time SSG pull of PUBLISHED docs into
 * scripts/build-hosting.sh's frontend/website/ step — a separate, larger
 * workstream, not attempted yet. See admin/docs/architecture/CMS_Content_Mapping.md.
 */
export interface PageContentDoc {
  /** URL path on musiclinkd.com, e.g. "/", "/about", "/legal/privacy". */
  route: string;
  title: string;
  status: PageStatus;
  /** Plain text/markdown body for v1 — no block-based page builder. */
  content: string;
  seoDescription: string;
  updatedBy: string;
  updatedAt: Timestamp;
}

export interface TaxonomyCategoryDoc {
  key: string;
  label: string;
  description: string;
  isActive: boolean;
  createdAt: Timestamp;
  updatedAt: Timestamp;
  updatedBy: string;
}

export interface TaxonomyOptionDoc {
  label: string;
  value: string;
  sortOrder: number;
  isActive: boolean;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

export interface ModuleRegistryDoc {
  label: string;
  description: string;
  enabled: boolean;
  /** Links to a real admin nav item/route (see config/constants.ts ModuleKey), or null if this module has no admin-panel page. */
  navModuleKey: string | null;
  /** Remote Config parameter this toggle mirrors, or null if it's admin-only. */
  remoteConfigKey: string | null;
  updatedBy: string;
  updatedAt: Timestamp;
  createdAt: Timestamp;
}

export interface WithId<T> {
  id: string;
  data: T;
}
