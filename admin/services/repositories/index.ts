import { COLLECTIONS } from "@/config/constants";
import type {
  AdminRoleDoc,
  AuditLogDoc,
  BroadcastMessageDoc,
  ConnectionDoc,
  FeedPostDoc,
  MarketplaceItemDoc,
  ModerationFlagDoc,
  PageContentDoc,
  ProjectDoc,
  ReviewDoc,
  StudioBookingDoc,
  StudioDoc,
  SupportTicketDoc,
  UserDoc,
  VerificationRequestDoc,
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
