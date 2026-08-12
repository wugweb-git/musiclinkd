import { COLLECTIONS } from "@/config/constants";
import type {
  AdminRoleDoc,
  AuditLogDoc,
  BrandAssetsDoc,
  BroadcastMessageDoc,
  CapabilityMatrixDoc,
  ConnectionDoc,
  FeedPostDoc,
  FlowDoc,
  MarketplaceItemDoc,
  ModerationFlagDoc,
  ModuleRegistryDoc,
  NotificationDoc,
  PageContentDoc,
  PortfolioProjectDoc,
  ProjectDoc,
  ReviewDoc,
  RoleProfileDoc,
  StudioBookingDoc,
  StudioDoc,
  SupportTicketDoc,
  TaxonomyCategoryDoc,
  UserDoc,
  UserSettingsDoc,
  VerificationRequestDoc,
  WidgetDoc,
} from "@/types/firestore";
import { createRepository } from "./generic-repository";

export const usersRepo = createRepository<UserDoc>(COLLECTIONS.users);
export const adminRolesRepo = createRepository<AdminRoleDoc>(COLLECTIONS.adminRoles);
export const connectionsRepo = createRepository<ConnectionDoc>(COLLECTIONS.connections);
export const feedPostsRepo = createRepository<FeedPostDoc>(COLLECTIONS.feedPosts);
export const studioBookingsRepo = createRepository<StudioBookingDoc>(COLLECTIONS.studioBookings);
export const studiosRepo = createRepository<StudioDoc>(COLLECTIONS.studios);
export const projectsRepo = createRepository<ProjectDoc>(COLLECTIONS.projects);
export const marketplaceItemsRepo = createRepository<MarketplaceItemDoc>(COLLECTIONS.marketplaceItems);
export const reviewsRepo = createRepository<ReviewDoc>(COLLECTIONS.reviews);
export const verificationRequestsRepo = createRepository<VerificationRequestDoc>(COLLECTIONS.verificationRequests);
export const moderationFlagsRepo = createRepository<ModerationFlagDoc>(COLLECTIONS.moderationFlags);
export const supportTicketsRepo = createRepository<SupportTicketDoc>(COLLECTIONS.supportTickets);
export const broadcastMessagesRepo = createRepository<BroadcastMessageDoc>(COLLECTIONS.broadcastMessages);
export const auditLogsRepo = createRepository<AuditLogDoc>(COLLECTIONS.auditLogs);
export const pageContentRepo = createRepository<PageContentDoc>(COLLECTIONS.pageContent);
export const taxonomyCategoriesRepo = createRepository<TaxonomyCategoryDoc>(COLLECTIONS.taxonomyCategories);
export const modulesRepo = createRepository<ModuleRegistryDoc>(COLLECTIONS.modules);
export const portfolioProjectsRepo = createRepository<PortfolioProjectDoc>(COLLECTIONS.portfolioProjects);
export const notificationsRepo = createRepository<NotificationDoc>(COLLECTIONS.notifications);
export const userSettingsRepo = createRepository<UserSettingsDoc>(COLLECTIONS.userSettings);
export const brandAssetsRepo = createRepository<BrandAssetsDoc>(COLLECTIONS.brandAssets);
export const roleProfilesRepo = createRepository<RoleProfileDoc>(COLLECTIONS.roleProfiles);
export const flowsRepo = createRepository<FlowDoc>(COLLECTIONS.flows);
export const capabilitiesRepo = createRepository<CapabilityMatrixDoc>(COLLECTIONS.capabilities);
export const widgetsRepo = createRepository<WidgetDoc>(COLLECTIONS.widgets);
