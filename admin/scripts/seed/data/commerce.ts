import type { MarketplaceItemDoc, StudioBookingDoc } from "@/types/firestore";
import { mockId, onDate } from "../lib";
import { NAMED_STUDIOS } from "./studios";
import { UIDS } from "./users";

type SeedMarketplaceItem = { id: string; data: MarketplaceItemDoc };
type SeedBooking = { id: string; data: StudioBookingDoc };

function studioIdFor(ownerUid: string) {
  return NAMED_STUDIOS.find((s) => s.data.ownerUid === ownerUid)!.id;
}

const yrfStudioId = studioIdFor(UIDS.yashRajSoundStudios);
const octaveStudioId = studioIdFor(UIDS.octaveSoundLabs);
const blueRoomStudioId = studioIdFor(UIDS.blueRoomMixingSuite);

export const MARKETPLACE_ITEMS: SeedMarketplaceItem[] = [
  {
    id: mockId("marketplace", "yamaha-hs8-studio-monitors"),
    data: {
      sellerUid: mockId("seller", "Arjun Mehta"),
      title: "Yamaha HS8 Active Studio Monitors (Pair)",
      price: "₹42,000",
      category: "gear_sale",
      condition: "LIKE_NEW",
      imageUrl: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600",
      itemCategory: "studio_monitors",
      createdAt: onDate("2026-07-15"),
    },
  },
  {
    id: mockId("marketplace", "neumann-u87-ai-mic"),
    data: {
      sellerUid: UIDS.yashRajSoundStudios,
      title: "Neumann U87 Ai Condenser Microphone",
      price: "₹1,85,000",
      category: "gear_sale",
      condition: "NEW",
      imageUrl: "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600",
      itemCategory: "microphones",
      createdAt: onDate("2026-07-10"),
    },
  },
  {
    id: mockId("marketplace", "focusrite-scarlett-2i2"),
    data: {
      sellerUid: mockId("seller", "Rahul Sharma"),
      title: "Focusrite Scarlett 2i2 USB Interface",
      price: "₹8,500",
      category: "gear_sale",
      condition: "LIKE_NEW",
      imageUrl: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600",
      itemCategory: "audio_interfaces",
      createdAt: onDate("2026-07-22"),
    },
  },
  {
    id: mockId("marketplace", "pearl-export-drum-kit"),
    data: {
      sellerUid: mockId("seller", "Blue Sitar Venues"),
      title: "Pearl Export 5-Piece Drum Kit (Rental)",
      price: "₹2,500/day + ₹10,000 deposit",
      category: "gear_rental",
      condition: "USED",
      imageUrl: "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600",
      itemCategory: "Drums / Percussion",
      createdAt: onDate("2026-07-05"),
    },
  },
  {
    id: mockId("marketplace", "ampeg-svt-bass-head"),
    data: {
      sellerUid: mockId("seller", "Sound Solutions"),
      title: "Ampeg SVT Classic Bass Amplifier Head (Rental)",
      price: "₹1,500/day + ₹5,000 deposit",
      category: "gear_rental",
      condition: "USED",
      imageUrl: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600",
      createdAt: onDate("2026-07-08"),
    },
  },
  {
    id: mockId("marketplace", "dolby-atmos-mixing-service"),
    data: {
      sellerUid: UIDS.octaveSoundLabs,
      title: "Dolby Atmos 7.1.4 Mixing & Stem Mastering",
      price: "₹12,000/track",
      category: "mixing_mastering",
      condition: "NEW",
      imageUrl: "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600",
      createdAt: onDate("2026-06-30"),
    },
  },
  {
    id: mockId("marketplace", "trap-lofi-beat-license"),
    data: {
      sellerUid: mockId("seller", "BeatCraft Studios"),
      title: "Exclusive Trap / Lofi Beat Instrumental License",
      price: "₹4,500",
      category: "beats_licensing",
      condition: "NEW",
      imageUrl: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600",
      createdAt: onDate("2026-07-28"),
    },
  },
  {
    id: mockId("marketplace", "hindustani-sufi-vocal-recording"),
    data: {
      sellerUid: mockId("seller", "Sunidhi Raga"),
      title: "Hindustani Classical & Sufi Vocal Recording",
      price: "₹6,000/session",
      category: "creative_services",
      condition: "NEW",
      imageUrl: "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600",
      createdAt: onDate("2026-07-19"),
    },
  },
];

export const STUDIO_BOOKINGS: SeedBooking[] = [
  {
    id: mockId("booking", "yrf-vocal-tracking-aditi"),
    data: {
      studioId: yrfStudioId,
      userId: UIDS.aditiSen,
      date: "2026-08-12",
      slotText: "10:00 AM – 2:00 PM",
      totalAmount: 32000,
      bookedHourSlots: ["10:00", "11:00", "12:00", "13:00"],
      bookingStatus: "confirmed",
      paymentStatus: "PAID",
      createdAt: onDate("2026-08-05"),
      sessionType: "recording",
      durationTier: "half_day",
      engineerRequested: true,
      paymentMethod: "UPI",
    },
  },
  {
    id: mockId("booking", "yrf-podcast-foley-karan"),
    data: {
      studioId: yrfStudioId,
      userId: UIDS.karanSaxena,
      date: "2026-08-12",
      slotText: "2:00 PM – 6:00 PM",
      totalAmount: 32000,
      bookedHourSlots: ["14:00", "15:00", "16:00", "17:00"],
      bookingStatus: "payment_pending",
      paymentStatus: "PENDING",
      createdAt: onDate("2026-08-06"),
      sessionType: "podcast",
      durationTier: "half_day",
    },
  },
  {
    id: mockId("booking", "yrf-atmos-mixing-rohit"),
    data: {
      studioId: yrfStudioId,
      userId: UIDS.rohitMalhotra,
      date: "2026-08-12",
      slotText: "6:00 PM – 10:00 PM",
      totalAmount: 32000,
      bookedHourSlots: ["18:00", "19:00", "20:00", "21:00"],
      bookingStatus: "confirmed",
      paymentStatus: "PAID",
      createdAt: onDate("2026-08-04"),
      sessionType: "mixing",
      durationTier: "half_day",
      engineerRequested: true,
      paymentMethod: "Card",
    },
  },
  {
    id: mockId("booking", "blue-room-jul12-vedanshu"),
    data: {
      studioId: blueRoomStudioId,
      userId: UIDS.vedanshuBeats,
      date: "2026-07-12",
      slotText: "14:00 – 18:00",
      totalAmount: 6000,
      bookedHourSlots: ["14:00", "15:00", "16:00", "17:00"],
      bookingStatus: "confirmed",
      paymentStatus: "PAID",
      createdAt: onDate("2026-07-10"),
      sessionType: "mixing",
      durationTier: "half_day",
      paymentMethod: "UPI",
    },
  },
  {
    id: mockId("booking", "octave-full-band-tracking-band"),
    data: {
      studioId: octaveStudioId,
      userId: UIDS.bangaloreBluesProject,
      date: "2026-06-28",
      slotText: "10:00 AM – 6:00 PM",
      totalAmount: 36000,
      bookedHourSlots: ["10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"],
      bookingStatus: "completed",
      paymentStatus: "PAID",
      createdAt: onDate("2026-06-20"),
      sessionType: "recording",
      durationTier: "full_day",
      engineerRequested: true,
      addons: ["Parking"],
      paymentMethod: "Net Banking",
    },
  },
  {
    id: mockId("booking", "octave-piano-session-kabeer"),
    data: {
      studioId: octaveStudioId,
      userId: UIDS.kabeerVarma,
      date: "2026-08-20",
      slotText: "3:00 PM – 5:00 PM",
      totalAmount: 7000,
      bookedHourSlots: ["15:00", "16:00"],
      bookingStatus: "requested",
      paymentStatus: "PENDING",
      createdAt: onDate("2026-08-11"),
      sessionType: "recording",
      durationTier: "hourly",
    },
  },
];
