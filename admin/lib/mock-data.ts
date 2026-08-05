/**
 * Seed data for modules with no backing Firestore collection yet — see the
 * approved plan's flagged open decisions (Messages needs a privacy decision,
 * Analytics/Tracks need a product decision before any schema work). Pages
 * used to be here too (as "CMS") but now has a real page_content collection
 * — see app/(admin)/pages/page.tsx.
 */

export const MOCK_MESSAGES = [
  { id: "thread_1", participants: "Aarav Mehta, Nova Sound Studio", lastMessage: "Confirmed for Thursday 6pm", updatedAt: "2026-08-02" },
  { id: "thread_2", participants: "Priya Iyer, Beatmaker Collective", lastMessage: "Sent the STEMs over", updatedAt: "2026-08-03" },
  { id: "thread_3", participants: "Kabir Rao, Echo Chamber Studio", lastMessage: "Can we push to next week?", updatedAt: "2026-08-04" },
];

export const MOCK_ANALYTICS_EVENTS = [
  { id: "evt_1", event: "sign_up_completed", count: 1240, trend: "+12%" },
  { id: "evt_2", event: "booking_created", count: 386, trend: "+4%" },
  { id: "evt_3", event: "marketplace_purchase", count: 97, trend: "-2%" },
];

export const MOCK_TRACKS = [
  { id: "track_1", title: "Midnight Reverb (preview)", owner: "Aarav Mehta", plays: 412 },
  { id: "track_2", title: "Analog Drift", owner: "Nova Sound Studio", plays: 198 },
  { id: "track_3", title: "Glass Room Session 3", owner: "Priya Iyer", plays: 76 },
];
