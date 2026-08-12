import type { BroadcastMessageDoc, ConnectionDoc, NotificationDoc, ReviewDoc } from "@/types/firestore";
import { mockId, onDate } from "../lib";
import { NAMED_STUDIOS } from "./studios";
import { UIDS } from "./users";

type SeedReview = { id: string; data: ReviewDoc };
type SeedNotification = { id: string; data: NotificationDoc };
type SeedConnection = { id: string; data: ConnectionDoc };
type SeedBroadcast = { id: string; data: BroadcastMessageDoc };

const octaveStudioId = NAMED_STUDIOS.find((s) => s.data.ownerUid === UIDS.octaveSoundLabs)!.id;

function reviewer(name: string) {
  return mockId("reviewer", name);
}

export const REVIEWS: SeedReview[] = [
  {
    id: mockId("review", "t-series-ar-arijit"),
    data: {
      targetType: "USER",
      targetId: UIDS.arijitSen,
      authorUid: reviewer("T-Series A&R Team"),
      rating: 5,
      text: "\"Exceptional range and control across genres.\" — T-Series A&R Team",
      createdAt: onDate("2026-06-10"),
    },
  },
  {
    id: mockId("review", "pritam-studio-arijit"),
    data: {
      targetType: "USER",
      targetId: UIDS.arijitSen,
      authorUid: reviewer("Pritam's Studio"),
      rating: 4.9,
      text: "\"One take, every time.\" — Pritam's Studio",
      createdAt: onDate("2026-05-22"),
    },
  },
  {
    id: mockId("review", "sanjay-k-vedanshu"),
    data: {
      targetType: "USER",
      targetId: UIDS.vedanshuBeats,
      authorUid: reviewer("Sanjay K."),
      rating: 4.8,
      text: "\"Mix quality is film-ready straight out of the session.\" — Sanjay K., Producer",
      createdAt: onDate("2026-06-01"),
    },
  },
  {
    id: mockId("review", "ehsaan-q-karan-saxena"),
    data: {
      targetType: "USER",
      targetId: UIDS.karanSaxena,
      authorUid: reviewer("Ehsaan Q."),
      rating: 5,
      text: "\"Excellent phrasing and control. Subscribed!\" — Ehsaan Q.",
      createdAt: onDate("2026-06-18"),
    },
  },
  {
    id: mockId("review", "arjun-mehta-octave"),
    data: {
      targetType: "STUDIO",
      targetId: octaveStudioId,
      authorUid: reviewer("Arjun Mehta"),
      rating: 5,
      text: "Mixed Monsoon Raga here — the Atmos room is genuinely world-class.",
      createdAt: onDate("2026-06-12"),
    },
  },
  {
    id: mockId("review", "riya-sen-octave"),
    data: {
      targetType: "STUDIO",
      targetId: octaveStudioId,
      authorUid: reviewer("Riya Sen"),
      rating: 5,
      text: "Vocal stems session — the booth is dead quiet and the preamps are gorgeous.",
      createdAt: onDate("2026-05-28"),
    },
  },
  {
    id: mockId("review", "kabir-trio-octave"),
    data: {
      targetType: "STUDIO",
      targetId: octaveStudioId,
      authorUid: reviewer("The Kabir Trio"),
      rating: 4,
      text: "Great live room for an acoustic EP, though load-in access could be easier.",
      createdAt: onDate("2026-04-18"),
    },
  },
  {
    id: mockId("review", "james-miller-vedanshu"),
    data: {
      targetType: "USER",
      targetId: UIDS.vedanshuBeats,
      authorUid: reviewer("James Miller"),
      rating: 5,
      text: "\"The raw vocals recorded are exceptionally clean. The Kemper profile work was masterfully done.\" — James Miller, A&R Universal Records",
      createdAt: onDate("2026-06-25"),
    },
  },
  {
    id: mockId("review", "sarah-connor-vedanshu"),
    data: {
      targetType: "USER",
      targetId: UIDS.vedanshuBeats,
      authorUid: reviewer("Sarah Connor"),
      rating: 5,
      text: "\"Delivered a soundtrack that elevated the entire game.\" — Sarah Connor, Creative Director, Synergy Games",
      createdAt: onDate("2026-06-05"),
    },
  },
  {
    id: mockId("review", "rohan-das-vedanshu"),
    data: {
      targetType: "USER",
      targetId: UIDS.vedanshuBeats,
      authorUid: reviewer("Rohan Das"),
      rating: 4,
      text: "\"Great ear for vintage synth tone.\" — Rohan Das, Vintage Synth Collector",
      createdAt: onDate("2026-05-14"),
    },
  },
  {
    id: mockId("review", "kabir-singh-vedanshu"),
    data: {
      targetType: "USER",
      targetId: UIDS.vedanshuBeats,
      authorUid: reviewer("Kabir Singh"),
      rating: 5,
      text: "\"Dolby Atmos stems came back flawless.\" — Kabir Singh, Producer",
      createdAt: onDate("2026-07-30"),
    },
  },
];

export const NOTIFICATIONS: SeedNotification[] = [
  {
    id: mockId("notification", "booking-approved-blue-room"),
    data: {
      userId: UIDS.vedanshuBeats,
      title: "Studio Booking Approved",
      body: "Your booking at Blue Room Mixing Suite, Mumbai on July 12, 14:00–18:00 has been confirmed.",
      type: "BOOKING_UPDATE",
      isRead: false,
      targetUrl: "/bookings",
      createdAt: onDate("2026-07-10"),
    },
  },
  {
    id: mockId("notification", "collab-request-aria-vox"),
    data: {
      userId: UIDS.vedanshuBeats,
      title: "New Collaboration Request",
      body: "Aria Vox wants to collaborate on Swar Sandhya.",
      type: "CONNECTION_REQUEST",
      isRead: false,
      createdAt: onDate("2026-07-18"),
    },
  },
  {
    id: mockId("notification", "five-star-kabir-singh"),
    data: {
      userId: UIDS.vedanshuBeats,
      title: "New 5-Star Review",
      body: "Kabir Singh (Producer) left a 5-star review on your Dolby Atmos stems.",
      type: "REVIEW_RECEIVED",
      isRead: true,
      createdAt: onDate("2026-07-30"),
    },
  },
  {
    id: mockId("notification", "verified-industry-professional"),
    data: {
      userId: UIDS.vedanshuBeats,
      title: "Verification Updated",
      body: "Your account is now marked as a Verified Industry Professional.",
      type: "SYSTEM_ALERT",
      isRead: true,
      createdAt: onDate("2026-06-15"),
    },
  },
  {
    id: mockId("notification", "trending-opportunity-sound-designer"),
    data: {
      userId: UIDS.vedanshuBeats,
      title: "Trending Opportunity Alert",
      body: "3 new audition posts for Sound Designer roles in Delhi NCR match your profile.",
      type: "GIG_MATCH",
      isRead: false,
      targetUrl: "/projects",
      createdAt: onDate("2026-08-05"),
    },
  },
];

export const CONNECTIONS: SeedConnection[] = [
  { id: mockId("connection", "kabir-sethi-to-vedanshu"), data: { senderUid: UIDS.kabirSethi, targetUserId: UIDS.vedanshuBeats, status: "PENDING" } },
  { id: mockId("connection", "aanya-malhotra-to-vedanshu"), data: { senderUid: UIDS.aanyaMalhotra, targetUserId: UIDS.vedanshuBeats, status: "PENDING" } },
  { id: mockId("connection", "rohan-deshmukh-to-vedanshu"), data: { senderUid: UIDS.rohanDeshmukh, targetUserId: UIDS.vedanshuBeats, status: "PENDING" } },
  { id: mockId("connection", "sanjana-sen-to-vedanshu"), data: { senderUid: UIDS.sanjanaSen, targetUserId: UIDS.vedanshuBeats, status: "PENDING" } },
  { id: mockId("connection", "nikhil-verma-to-vedanshu"), data: { senderUid: UIDS.nikhilVerma, targetUserId: UIDS.vedanshuBeats, status: "PENDING" } },
  { id: mockId("connection", "karan-saxena-to-vedanshu"), data: { senderUid: UIDS.karanSaxena, targetUserId: UIDS.vedanshuBeats, status: "PENDING" } },
  { id: mockId("connection", "rhea-kapoor-to-vedanshu"), data: { senderUid: UIDS.rheaKapoor, targetUserId: UIDS.vedanshuBeats, status: "PENDING" } },
  { id: mockId("connection", "vedanshu-to-aarav-mehta"), data: { senderUid: UIDS.vedanshuBeats, targetUserId: UIDS.aaravMehta, status: "ACCEPTED" } },
  { id: mockId("connection", "vedanshu-to-aditi-sen"), data: { senderUid: UIDS.vedanshuBeats, targetUserId: UIDS.aditiSen, status: "ACCEPTED" } },
  { id: mockId("connection", "vedanshu-to-vikramaditya-roy"), data: { senderUid: UIDS.vedanshuBeats, targetUserId: UIDS.vikramadityaRoy, status: "ACCEPTED" } },
];

export const BROADCAST_MESSAGES: SeedBroadcast[] = [
  {
    id: mockId("broadcast", "trending-sound-designer-delhi"),
    data: {
      title: "3 new Sound Designer gigs in Delhi NCR",
      body: "Fresh audition posts matching your Sound Designer profile just went live — check Opportunities before they close.",
      audience: "ROLE:sound_designer",
      sentBy: "mock-admin-system",
      sentAt: onDate("2026-08-05"),
      status: "SENT",
    },
  },
  {
    id: mockId("broadcast", "verified-badge-rollout"),
    data: {
      title: "Verified Industry Professional badges are live",
      body: "We've rolled out the Verified Industry Professional badge — submit your documents from Settings > Verification to apply.",
      audience: "ALL",
      sentBy: "mock-admin-system",
      sentAt: onDate("2026-06-15"),
      status: "SENT",
    },
  },
  {
    id: mockId("broadcast", "dolby-atmos-studios-week"),
    data: {
      title: "Dolby Atmos Studios Week",
      body: "Octave Sound Labs and Yash Raj Sound Studios are offering 15% off Atmos mixing sessions this week only.",
      audience: "CITY:Mumbai",
      sentBy: "mock-admin-system",
      sentAt: onDate("2026-07-01"),
      status: "SENT",
    },
  },
  {
    id: mockId("broadcast", "marketplace-launch"),
    data: {
      title: "Marketplace is open",
      body: "Buy, sell, and rent studio gear directly on MusicLinkd — first listing is free this month.",
      audience: "ALL",
      sentBy: "mock-admin-system",
      sentAt: onDate("2026-05-01"),
      status: "SENT",
    },
  },
  {
    id: mockId("broadcast", "swar-fest-2026-lineup"),
    data: {
      title: "Swar Fest 2026 lineup applications open",
      body: "Nisha Rao Events is now accepting act submissions for Swar Fest 2026, Bengaluru.",
      audience: "ROLE:singer",
      sentBy: "mock-admin-system",
      sentAt: onDate("2026-07-20"),
      status: "QUEUED",
    },
  },
  {
    id: mockId("broadcast", "app-maintenance-window"),
    data: {
      title: "Scheduled maintenance — Aug 20, 02:00–04:00 IST",
      body: "Booking and payment features will be briefly unavailable during this window.",
      audience: "ALL",
      sentBy: "mock-admin-system",
      sentAt: onDate("2026-08-10"),
      status: "QUEUED",
    },
  },
];
