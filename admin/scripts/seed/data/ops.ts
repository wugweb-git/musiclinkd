import { MODULE_PERMISSIONS } from "@/config/constants";
import type {
  AuditLogDoc,
  CapabilityMatrixDoc,
  ModerationFlagDoc,
  ModuleRegistryDoc,
  PageContentDoc,
  RoleProfileDoc,
  VerificationRequestDoc,
} from "@/types/firestore";
import { DEFAULT_FLOWS } from "@/app/(admin)/flows/default-flows";
import { DEFAULT_SCREENS } from "@/app/(admin)/flows/screens/default-screens";
import { DEFAULT_WIDGETS } from "@/app/(admin)/widgets/default-widgets";
import { mockId, onDate } from "../lib";
import { NAMED_STUDIOS } from "./studios";
import { UIDS } from "./users";

type SeedVerification = { id: string; data: VerificationRequestDoc };
type SeedModeration = { id: string; data: ModerationFlagDoc };
type SeedAuditLog = { id: string; data: AuditLogDoc };
type SeedRoleProfile = { id: string; data: RoleProfileDoc };
type SeedPage = { id: string; data: PageContentDoc };
type SeedCapability = { id: string; data: CapabilityMatrixDoc };
type SeedModule = { id: string; data: ModuleRegistryDoc };
type SeedFlow = { id: string; data: (typeof DEFAULT_FLOWS)[number] & { createdAt: ReturnType<typeof onDate>; updatedAt: ReturnType<typeof onDate> } };
type SeedFlowScreen = { flowId: string; id: string; data: Record<string, unknown> };
type SeedWidget = { id: string; data: (typeof DEFAULT_WIDGETS)[number] & { createdAt: ReturnType<typeof onDate>; updatedAt: ReturnType<typeof onDate> } };

const octaveStudioId = NAMED_STUDIOS.find((s) => s.data.ownerUid === UIDS.octaveSoundLabs)!.id;
const ADMIN_ACTOR = "mock-admin-system";

export const VERIFICATION_REQUESTS: SeedVerification[] = [
  {
    id: mockId("verification", "vedanshu-aadhaar"),
    data: {
      subjectType: "USER",
      subjectId: UIDS.vedanshuBeats,
      submittedBy: UIDS.vedanshuBeats,
      status: "APPROVED",
      documentPaths: ["verification_documents/mock/Aadhaar_Front_SwarSandhya.pdf"],
      verificationIdType: "govt_id_aadhaar",
      submittedAt: onDate("2026-06-01"),
      reviewedBy: ADMIN_ACTOR,
      reviewedAt: onDate("2026-06-15"),
      notes: "ID matches profile name; approved.",
    },
  },
  {
    id: mockId("verification", "octave-gstin"),
    data: {
      subjectType: "STUDIO",
      subjectId: octaveStudioId,
      submittedBy: UIDS.octaveSoundLabs,
      status: "APPROVED",
      documentPaths: ["verification_documents/mock/GST_Verification_VedanshuMusic.pdf"],
      verificationIdType: "company_pan_gstin",
      submittedAt: onDate("2026-05-20"),
      reviewedBy: ADMIN_ACTOR,
      reviewedAt: onDate("2026-05-29"),
    },
  },
  {
    id: mockId("verification", "kabeer-varma-aadhaar"),
    data: {
      subjectType: "USER",
      subjectId: UIDS.kabeerVarma,
      submittedBy: UIDS.kabeerVarma,
      status: "PENDING",
      documentPaths: ["verification_documents/mock/Aadhaar_KabeerVarma.pdf"],
      verificationIdType: "govt_id_aadhaar",
      submittedAt: onDate("2026-08-08"),
    },
  },
  {
    id: mockId("verification", "dj-rish-passport"),
    data: {
      subjectType: "USER",
      subjectId: UIDS.djRish,
      submittedBy: UIDS.djRish,
      status: "PENDING",
      documentPaths: ["verification_documents/mock/Passport_DJRish.pdf"],
      verificationIdType: "passport",
      submittedAt: onDate("2026-08-09"),
    },
  },
  {
    id: mockId("verification", "aanya-malhotra-license"),
    data: {
      subjectType: "USER",
      subjectId: UIDS.aanyaMalhotra,
      submittedBy: UIDS.aanyaMalhotra,
      status: "REJECTED",
      documentPaths: ["verification_documents/mock/DL_AanyaMalhotra.pdf"],
      verificationIdType: "driving_license",
      submittedAt: onDate("2026-07-02"),
      reviewedBy: ADMIN_ACTOR,
      reviewedAt: onDate("2026-07-06"),
      notes: "Document image too blurry to verify — resubmission requested.",
    },
  },
];

export const MODERATION_FLAGS: SeedModeration[] = [
  {
    id: mockId("moderation", "review-spam-flag"),
    data: {
      targetType: "REVIEW",
      targetId: mockId("review", "sanjay-k-vedanshu"),
      reportedBy: mockId("reporter", "anon-user-1"),
      reason: "Reported as suspected fake/incentivized review.",
      status: "DISMISSED",
      createdAt: onDate("2026-06-03"),
      reviewedBy: ADMIN_ACTOR,
      reviewedAt: onDate("2026-06-04"),
    },
  },
  {
    id: mockId("moderation", "beat-license-copyright"),
    data: {
      targetType: "MARKETPLACE_ITEM",
      targetId: mockId("marketplace", "trap-lofi-beat-license"),
      reportedBy: mockId("reporter", "anon-user-2"),
      reason: "Possible unauthorized sample in the beat preview.",
      status: "ACTIONED",
      createdAt: onDate("2026-07-29"),
      reviewedBy: ADMIN_ACTOR,
      reviewedAt: onDate("2026-07-31"),
    },
  },
  {
    id: mockId("moderation", "filler-user-spam-flag"),
    data: {
      targetType: "USER",
      targetId: UIDS.djRish,
      reportedBy: mockId("reporter", "anon-user-3"),
      reason: "Reported for sending unsolicited promo DMs.",
      status: "PENDING",
      createdAt: onDate("2026-08-09"),
    },
  },
  {
    id: mockId("moderation", "portfolio-audio-flag"),
    data: {
      targetType: "AUDIO",
      targetId: mockId("portfolio", `${UIDS.karanMalhotra}-Midnight Drive in Colaba`),
      reportedBy: mockId("reporter", "anon-user-4"),
      reason: "Reported audio may contain an uncleared sample.",
      status: "PENDING",
      createdAt: onDate("2026-08-07"),
    },
  },
];

export const AUDIT_LOGS: SeedAuditLog[] = [
  { id: mockId("audit", "1"), data: { actorUid: ADMIN_ACTOR, action: "user.verification.approved", targetPath: `users/${UIDS.vedanshuBeats}`, timestamp: onDate("2026-06-15"), metadata: { verificationId: mockId("verification", "vedanshu-aadhaar") } } },
  { id: mockId("audit", "2"), data: { actorUid: ADMIN_ACTOR, action: "studio.verification.approved", targetPath: `studios/${octaveStudioId}`, timestamp: onDate("2026-05-29") } },
  { id: mockId("audit", "3"), data: { actorUid: ADMIN_ACTOR, action: "moderation.actioned", targetPath: `marketplace_items/${mockId("marketplace", "trap-lofi-beat-license")}`, timestamp: onDate("2026-07-31") } },
  { id: mockId("audit", "4"), data: { actorUid: ADMIN_ACTOR, action: "moderation.dismissed", targetPath: `reviews/${mockId("review", "sanjay-k-vedanshu")}`, timestamp: onDate("2026-06-04") } },
  { id: mockId("audit", "5"), data: { actorUid: ADMIN_ACTOR, action: "verification.rejected", targetPath: `users/${UIDS.aanyaMalhotra}`, timestamp: onDate("2026-07-06") } },
  { id: mockId("audit", "6"), data: { actorUid: ADMIN_ACTOR, action: "broadcast.sent", targetPath: `broadcast_messages/${mockId("broadcast", "verified-badge-rollout")}`, timestamp: onDate("2026-06-15") } },
  { id: mockId("audit", "7"), data: { actorUid: ADMIN_ACTOR, action: "record.created", targetPath: `studios/${octaveStudioId}`, timestamp: onDate("2026-05-01"), metadata: { source: "seed-script" } } },
  { id: mockId("audit", "8"), data: { actorUid: UIDS.vedanshuBeats, action: "booking.created", targetPath: `studio_bookings/${mockId("booking", "blue-room-jul12-vedanshu")}`, timestamp: onDate("2026-07-10") } },
];

export const ROLE_PROFILES: SeedRoleProfile[] = [
  {
    id: `${UIDS.arijitSen}_singer`,
    data: {
      ownerUid: UIDS.arijitSen,
      roleKey: "singer",
      vocalRange: "tenor",
      singingStyles: ["playback", "classical", "sufi"],
      originalSongsCount: 6,
      coversCount: 14,
      createdAt: onDate("2026-05-01"),
      updatedAt: onDate("2026-07-01"),
    },
  },
  {
    id: `${UIDS.vikramadityaRoy}_musician`,
    data: {
      ownerUid: UIDS.vikramadityaRoy,
      roleKey: "musician",
      primaryInstrument: "Sitar",
      secondaryInstruments: ["Flute (Bansuri)"],
      yearsOfExperience: 15,
      createdAt: onDate("2026-05-05"),
      updatedAt: onDate("2026-07-01"),
    },
  },
  {
    id: `${UIDS.siddharthSwar}_sound_engineer`,
    data: {
      ownerUid: UIDS.siddharthSwar,
      roleKey: "sound_engineer",
      specialty: "mastering",
      dawStack: ["pro_tools"],
      createdAt: onDate("2026-05-10"),
      updatedAt: onDate("2026-07-01"),
    },
  },
  {
    id: `${UIDS.djRish}_dj`,
    data: {
      ownerUid: UIDS.djRish,
      roleKey: "dj",
      djType: "festival",
      eventExperienceYears: 6,
      createdAt: onDate("2026-05-12"),
      updatedAt: onDate("2026-07-01"),
    },
  },
  {
    id: `${UIDS.kabirSethi}_lyricist`,
    data: {
      ownerUid: UIDS.kabirSethi,
      roleKey: "lyricist",
      specializations: ["romantic", "independent_music"],
      publishedWorksCount: 9,
      createdAt: onDate("2026-05-14"),
      updatedAt: onDate("2026-07-01"),
    },
  },
  {
    id: `${UIDS.rohitMalhotra}_composer`,
    data: {
      ownerUid: UIDS.rohitMalhotra,
      roleKey: "composer",
      compositionStyles: ["orchestral", "film_score"],
      createdAt: onDate("2026-05-16"),
      updatedAt: onDate("2026-07-01"),
    },
  },
];

/**
 * 2026-08-13: about/blog/careers removed per the user — Page Management is
 * scoped to core site pages + compliance + contact only, not content
 * marketing pages. home/pricing kept (core site pages, not what "remove
 * blog/about/careers" was aimed at); contact added new.
 */
export const PAGES: SeedPage[] = [
  { id: "home", data: { route: "/", title: "MusicLinkd — Find your next collaborator", status: "PUBLISHED", content: "MusicLinkd connects singers, musicians, producers, studios, and venues across India for collaboration, booking, and hire.", seoDescription: "MusicLinkd is India's marketplace for music talent, studios, and collaboration.", updatedBy: ADMIN_ACTOR, updatedAt: onDate("2026-08-01") } },
  { id: "pricing", data: { route: "/pricing", title: "Pricing", status: "PUBLISHED", content: "Free to join. Studios and businesses can upgrade to a Pro plan for advanced booking and analytics tools.", seoDescription: "MusicLinkd pricing plans for talent, studios, and businesses.", updatedBy: ADMIN_ACTOR, updatedAt: onDate("2026-08-01") } },
  { id: "contact", data: { route: "/contact", title: "Contact MusicLinkd", status: "PUBLISHED", content: "Reach the MusicLinkd team for support, partnerships, or press inquiries.", seoDescription: "Get in touch with the MusicLinkd team.", updatedBy: ADMIN_ACTOR, updatedAt: onDate("2026-08-13") } },
  { id: "legal-privacy", data: { route: "/legal/privacy", title: "Privacy Policy", status: "PUBLISHED", content: "This Privacy Policy explains how MusicLinkd collects, uses, and protects your information.", seoDescription: "MusicLinkd privacy policy.", updatedBy: ADMIN_ACTOR, updatedAt: onDate("2026-06-01") } },
  { id: "legal-terms", data: { route: "/legal/terms", title: "Terms of Service", status: "PUBLISHED", content: "These Terms of Service govern your use of MusicLinkd.", seoDescription: "MusicLinkd terms of service.", updatedBy: ADMIN_ACTOR, updatedAt: onDate("2026-06-01") } },
  { id: "legal-cookies", data: { route: "/legal/cookies", title: "Cookie Policy", status: "PUBLISHED", content: "This Cookie Policy explains how MusicLinkd uses cookies and similar technologies.", seoDescription: "MusicLinkd cookie policy.", updatedBy: ADMIN_ACTOR, updatedAt: onDate("2026-06-01") } },
];

/** Mirrors capabilities/page.tsx's inline DEFAULT_CAPABILITIES (not importable — that file is a "use client" component). Keep in sync by hand if that list changes. */
export const CAPABILITIES: SeedCapability[] = [
  { id: "talent", data: { roleKey: "talent", label: "Talent", canCreateProfile: true, canPostOpportunities: false, canSellEquipment: true, canCreateEvents: true, crmAccess: "none", updatedBy: ADMIN_ACTOR, createdAt: onDate("2026-08-01"), updatedAt: onDate("2026-08-01") } },
  { id: "studio", data: { roleKey: "studio", label: "Studio", canCreateProfile: true, canPostOpportunities: true, canSellEquipment: true, canCreateEvents: true, crmAccess: "full", updatedBy: ADMIN_ACTOR, createdAt: onDate("2026-08-01"), updatedAt: onDate("2026-08-01") } },
  { id: "label", data: { roleKey: "label", label: "Label", canCreateProfile: true, canPostOpportunities: true, canSellEquipment: false, canCreateEvents: true, crmAccess: "full", updatedBy: ADMIN_ACTOR, createdAt: onDate("2026-08-01"), updatedAt: onDate("2026-08-01") } },
  { id: "agency", data: { roleKey: "agency", label: "Agency", canCreateProfile: true, canPostOpportunities: true, canSellEquipment: false, canCreateEvents: true, crmAccess: "full", updatedBy: ADMIN_ACTOR, createdAt: onDate("2026-08-01"), updatedAt: onDate("2026-08-01") } },
  { id: "admin", data: { roleKey: "admin", label: "Admin", canCreateProfile: true, canPostOpportunities: true, canSellEquipment: true, canCreateEvents: true, crmAccess: "full", updatedBy: ADMIN_ACTOR, createdAt: onDate("2026-08-01"), updatedAt: onDate("2026-08-01") } },
];

const MODULE_LABELS: Record<string, string> = {
  dashboard: "Dashboard",
  users: "Users",
  studios: "Studios",
  marketplace: "Marketplace",
  bookings: "Bookings",
  projects: "Projects",
  portfolio: "Portfolio",
  "role-profiles": "Role Profiles",
  reviews: "Reviews",
  verification: "Verification",
  moderation: "Moderation",
  messages: "Messages",
  notifications: "Notifications",
  reports: "Reports & Exports",
  pages: "Page Management",
  analytics: "Analytics",
  tracks: "Tracks",
  "audit-logs": "Audit Logs",
  settings: "Settings",
  "master-data": "Master Data",
  modules: "Modules",
  flows: "Flows",
  capabilities: "Capabilities",
};

export const MODULES: SeedModule[] = Object.keys(MODULE_PERMISSIONS).map((key) => ({
  id: key,
  data: {
    label: MODULE_LABELS[key] ?? key,
    description: `Admin module: ${MODULE_LABELS[key] ?? key}.`,
    enabled: true,
    navModuleKey: key,
    remoteConfigKey: null,
    updatedBy: ADMIN_ACTOR,
    updatedAt: onDate("2026-08-01"),
    createdAt: onDate("2026-08-01"),
  },
}));

export const FLOWS: SeedFlow[] = DEFAULT_FLOWS.map((f) => ({
  id: f.key,
  data: { ...f, createdAt: onDate("2026-08-01"), updatedAt: onDate("2026-08-01") },
}));

export const FLOW_SCREENS: SeedFlowScreen[] = Object.entries(DEFAULT_SCREENS).flatMap(([flowId, screens]) =>
  screens.map((s) => ({
    flowId,
    id: s.key,
    data: { ...s, createdAt: onDate("2026-08-01"), updatedAt: onDate("2026-08-01") },
  })),
);

export const WIDGETS: SeedWidget[] = DEFAULT_WIDGETS.map((w) => ({
  id: w.key,
  data: { ...w, createdAt: onDate("2026-08-01"), updatedAt: onDate("2026-08-01") },
}));
