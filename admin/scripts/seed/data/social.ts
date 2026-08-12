/**
 * The 5 collections confirmed to have zero seed data as of 2026-08-13 (see
 * scripts/seed/seed.ts's groups array vs config/constants.ts's COLLECTIONS —
 * admin_roles is deliberately left out of that comparison and out of this
 * file, real admin accounts shouldn't get mock data).
 */
import type { BrandAssetsDoc, ChatMessageDoc, ChatThreadDoc, FeedPostDoc, SupportTicketDoc, UserSettingsDoc } from "@/types/firestore";
import { mockId, onDate } from "../lib";
import { UIDS } from "./users";

// --- feed_posts ---
type SeedFeedPost = { id: string; data: FeedPostDoc };

export const FEED_POSTS: SeedFeedPost[] = [
  {
    id: mockId("feed", "arijit-new-single"),
    data: {
      authorUid: UIDS.arijitSen,
      postType: "audio",
      audioUrl: "https://example.com/mock-audio/arijit-teaser.mp3",
      text: "New single teaser — mixed at Octave Sound Labs this week. Full release Friday.",
      likesCount: 142,
      commentsCount: 18,
      timestamp: onDate("2026-08-08"),
      authorName: "Arijit Sen",
      authorRole: "Singer / Vocalist",
    },
  },
  {
    id: mockId("feed", "vedanshu-beat-pack"),
    data: {
      authorUid: UIDS.vedanshuBeats,
      postType: "text",
      text: "Dropped a 10-beat pack for lo-fi/chill producers this morning — link in bio. First 3 downloads free.",
      likesCount: 76,
      commentsCount: 9,
      timestamp: onDate("2026-08-06"),
      authorName: "Vedanshu Beats",
      authorRole: "Music Producer",
    },
  },
  {
    id: mockId("feed", "octave-atmos-room"),
    data: {
      authorUid: UIDS.octaveSoundLabs,
      postType: "image",
      mediaUrls: ["https://example.com/mock-img/octave-atmos-room.jpg"],
      text: "Our Atmos 7.1.4 room is booked solid through September — grab a slot before festival season fills it up.",
      likesCount: 54,
      commentsCount: 4,
      timestamp: onDate("2026-08-05"),
      authorName: "Octave Sound Labs",
      authorRole: "Recording Studio",
    },
  },
  {
    id: mockId("feed", "vikramaditya-sitar-session"),
    data: {
      authorUid: UIDS.vikramadityaRoy,
      postType: "audio",
      audioUrl: "https://example.com/mock-audio/vikramaditya-raga.mp3",
      text: "Late-night raga improvisation, unedited. Looking for a tabla player to jam on a follow-up session.",
      likesCount: 203,
      commentsCount: 27,
      timestamp: onDate("2026-08-03"),
      authorName: "Vikramaditya Roy",
      authorRole: "Sitarist / Flutist",
    },
  },
  {
    id: mockId("feed", "kabeer-varma-stems-sent"),
    data: {
      authorUid: UIDS.kabeerVarma,
      postType: "text",
      text: "Sent the sitar stems over to Aditi Sen for the co-write — sounding great already.",
      likesCount: 31,
      commentsCount: 3,
      timestamp: onDate("2026-08-07"),
      authorName: "Kabeer Varma",
      authorRole: "Sitarist / Flutist",
    },
  },
];

// --- chat_threads + chat_threads/{threadId}/messages ---
type SeedChatThread = { id: string; data: ChatThreadDoc };
type SeedChatMessage = { threadId: string; id: string; data: ChatMessageDoc };

const threadAaravOctave = mockId("thread", "aarav-mehta-octave");
const threadPriyaBeatmaker = mockId("thread", "priya-iyer-beatmaker");
const threadKabirEcho = mockId("thread", "kabir-rao-echo");

export const CHAT_THREADS: SeedChatThread[] = [
  { id: threadAaravOctave, data: { memberUids: [UIDS.aaravMehta, UIDS.octaveSoundLabs] } },
  { id: threadPriyaBeatmaker, data: { memberUids: [UIDS.aditiSen, UIDS.vedanshuBeats] } },
  { id: threadKabirEcho, data: { memberUids: [UIDS.kabirSethi, UIDS.octaveSoundLabs] } },
];

export const CHAT_MESSAGES: SeedChatMessage[] = [
  { threadId: threadAaravOctave, id: mockId("msg", "aarav-1"), data: { senderUid: UIDS.aaravMehta, text: "Hey, is the North Room free Thursday 6pm?", sentAt: onDate("2026-08-01") } },
  { threadId: threadAaravOctave, id: mockId("msg", "octave-1"), data: { senderUid: UIDS.octaveSoundLabs, text: "Yes, confirmed for Thursday 6pm — see you then!", sentAt: onDate("2026-08-02") } },
  { threadId: threadPriyaBeatmaker, id: mockId("msg", "priya-1"), data: { senderUid: UIDS.aditiSen, text: "Just sent the stems over for the co-write.", sentAt: onDate("2026-08-03") } },
  { threadId: threadKabirEcho, id: mockId("msg", "kabir-1"), data: { senderUid: UIDS.kabirSethi, text: "Can we push the session to next week? Something came up.", sentAt: onDate("2026-08-04") } },
];

// --- support_tickets ---
type SeedSupportTicket = { id: string; data: SupportTicketDoc };

export const SUPPORT_TICKETS: SeedSupportTicket[] = [
  {
    id: mockId("ticket", "verification-stuck"),
    data: {
      requesterUid: UIDS.kabirSethi,
      requesterEmail: "kabir.sethi@example.com",
      subject: "Verification request stuck in pending for a week",
      message: "Submitted my Aadhaar and GST docs last Monday, status still shows pending. Can someone check?",
      status: "OPEN",
      createdAt: onDate("2026-08-05"),
      updatedAt: onDate("2026-08-05"),
    },
  },
  {
    id: mockId("ticket", "booking-payment-failed"),
    data: {
      requesterUid: UIDS.aaravMehta,
      requesterEmail: "aarav.mehta@example.com",
      subject: "Payment failed but amount was deducted",
      message: "Tried booking Octave Sound Labs' North Room, payment failed on the app but ₹7,200 was deducted from my card.",
      status: "IN_PROGRESS",
      assignedTo: "mock-admin-system",
      createdAt: onDate("2026-08-02"),
      updatedAt: onDate("2026-08-06"),
    },
  },
  {
    id: mockId("ticket", "profile-photo-not-updating"),
    data: {
      requesterUid: UIDS.mayaSharma,
      requesterEmail: "maya.sharma@example.com",
      subject: "Profile photo won't update",
      message: "Uploaded a new profile photo three times now, still shows the old one on my public profile.",
      status: "RESOLVED",
      assignedTo: "mock-admin-system",
      createdAt: onDate("2026-07-20"),
      updatedAt: onDate("2026-07-22"),
    },
  },
];

// --- user_settings ---
type SeedUserSettings = { id: string; data: UserSettingsDoc };

function settingsFor(uid: string, overrides: Partial<UserSettingsDoc> = {}): SeedUserSettings {
  return {
    id: uid,
    data: {
      pushNotificationsEnabled: true,
      emailNotificationsEnabled: true,
      profileVisibility: "PUBLIC",
      darkModeEnabled: false,
      currency: "INR",
      language: "English",
      updatedAt: onDate("2026-08-01"),
      ...overrides,
    },
  };
}

export const USER_SETTINGS: SeedUserSettings[] = [
  settingsFor(UIDS.arijitSen),
  settingsFor(UIDS.vedanshuBeats, { darkModeEnabled: true }),
  settingsFor(UIDS.karanSaxena, { emailNotificationsEnabled: false }),
  settingsFor(UIDS.aaravMehta),
  settingsFor(UIDS.octaveSoundLabs, { profileVisibility: "PUBLIC", currency: "INR" }),
];

// --- brand_assets ---
type SeedBrandAssets = { id: string; data: BrandAssetsDoc };

export const BRAND_ASSETS: SeedBrandAssets[] = [
  {
    id: mockId("brand", "octave-sound-labs"),
    data: {
      ownerUid: UIDS.octaveSoundLabs,
      logoUrl: "https://example.com/mock-brand/octave-logo.png",
      iconUrl: "https://example.com/mock-brand/octave-icon.png",
      thumbnailUrl: "https://example.com/mock-brand/octave-thumb.jpg",
      heroBannerUrl: "https://example.com/mock-brand/octave-hero.jpg",
      updatedAt: onDate("2026-07-15"),
    },
  },
  {
    id: mockId("brand", "yash-raj-sound-studios"),
    data: {
      ownerUid: UIDS.yashRajSoundStudios,
      logoUrl: "https://example.com/mock-brand/yashraj-logo.png",
      iconUrl: "https://example.com/mock-brand/yashraj-icon.png",
      updatedAt: onDate("2026-07-10"),
    },
  },
];
