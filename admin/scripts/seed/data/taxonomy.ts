/**
 * Shared value pools for seed data. Taxonomy-backed fields (genres,
 * languages, instruments, person_types, studio_amenities, etc.) use the
 * *exact* label/value strings from
 * admin/docs/architecture/Master_Data_Seed_Values.md — that doc's values
 * are already seeded into taxonomy_categories/options, so anything typed
 * here that doesn't match those strings would silently fail to match admin
 * filter chips later.
 */

export const PERSON_TYPE_SLUGS = {
  "Singer / Vocalist": "singer",
  "Rapper / MC": "rapper",
  "Folk Vocalist": "folk_vocalist",
  Guitarist: "guitarist",
  "Keyboardist / Pianist": "keyboardist",
  "Drummer / Percussionist": "drummer",
  "Sitarist / Flutist": "sitarist",
  Composer: "composer",
  Lyricist: "lyricist",
  "Vocal Arranger": "arranger",
  "Music Producer": "producer",
  "Sound Engineer": "sound_engineer",
  "Sound Designer": "sound_designer",
  "Artist Manager": "artist_manager",
  "Music Label": "music_label",
  "Music Publisher": "music_publisher",
  "Recording Studio": "studio",
  "Event Organizer": "event_organizer",
} as const;

export const GENRES = [
  "Bollywood / Filmi",
  "Indie Pop",
  "Hindustani Classical",
  "Carnatic Classical",
  "Punjabi Folk / Bhangra",
  "Rajasthani Folk",
  "Sufi Devotional",
  "Ghazal",
  "Qawwali",
  "Rock / Metal",
  "Hip Hop / Trap",
  "Lofi / Chill Beats",
  "EDM / Synthwave",
  "Acoustic / Unplugged",
  "R&B / Soul",
  "Jazz / Blues",
  "Reggae / Dub",
] as const;

export const LANGUAGES = [
  "English",
  "Hindi (हिंदी)",
  "Punjabi (ਪੰਜਾਬੀ)",
  "Tamil (தமிழ்)",
  "Telugu (తెలుగు)",
  "Marathi (मराठी)",
  "Gujarati (ગુજરાતી)",
  "Kannada (ಕನ್ನಡ)",
  "Malayalam (മലയാളം)",
  "Bengali (বাংলা)",
] as const;

export const INSTRUMENTS = [
  "Acoustic Guitar",
  "Electric Guitar",
  "Bass Guitar",
  "Keyboard / Synthesizer",
  "Piano",
  "Drums / Percussion",
  "Tabla",
  "Sitar",
  "Flute (Bansuri)",
  "Harmonium",
  "Violin",
  "Saxophone",
] as const;

export const PERSONA_BADGES = [
  "Available for Collab",
  "Session Ready",
  "Touring",
  "Home Studio Owner",
  "Full-Time",
  "Weekend Warrior",
  "Open to Remote Work",
] as const;

export const TRUST_BADGES = ["Verified Artist", "Top Host", "Rising Talent", "Studio Partner"] as const;

export const VOCAL_RANGES = ["soprano", "mezzo_soprano", "alto", "tenor", "baritone", "bass"] as const;

export const DAW_TOOLS = ["logic_pro", "pro_tools", "ableton_live", "fl_studio", "cubase", "studio_one", "reaper"] as const;

export const AVAILABILITY_STATUSES = ["available_now", "open_for_gigs", "booked_soon", "on_tour", "unavailable"] as const;

export const BUDGET_TIERS = ["budget", "mid_range", "premium"] as const;

export const STUDIO_AMENITIES = [
  "Dolby Atmos",
  "Vocal Booth",
  "Live Room",
  "Valet Parking",
  "Resident Engineers",
  "Private Lounge",
  "24x7 Access",
  "Rehearsal Room",
  "Parking Space",
  "Acoustics Optimized",
  "Fiber Internet",
  "Stage Monitors",
] as const;

export const STUDIO_SERVICES = ["recording", "mixing", "mastering"] as const;

export const BOOKING_STATUSES = ["requested", "confirmed", "payment_pending", "cancelled", "completed"] as const;

export const SESSION_TYPES = ["recording", "mixing", "mastering", "podcast", "jam_session"] as const;

export const SESSION_DURATION_TIERS = ["hourly", "half_day", "full_day"] as const;

export const COLLABORATION_TYPES = ["work_for_hire", "royalties_split", "co_write", "session_payment"] as const;

export const PROJECT_TYPES = [
  "song",
  "album",
  "ep",
  "podcast",
  "dj_mix",
  "film_score",
  "commercial_jingle",
  "live_performance",
  "music_video",
] as const;

export const PROJECT_ROLES = [
  "Singer",
  "Composer",
  "Music Director",
  "Producer",
  "Lyricist",
  "Session Musician",
  "DJ",
  "Mix Engineer",
  "Mastering Engineer",
  "Sound Designer",
  "Band Member",
] as const;

export const MARKETPLACE_CATEGORIES = [
  "gear_sale",
  "gear_rental",
  "beats_licensing",
  "stems",
  "mixing_mastering",
  "creative_services",
] as const;

export const EQUIPMENT_CATEGORIES = [
  "microphones",
  "audio_interfaces",
  "studio_monitors",
  "mixers",
  "preamps",
  "headphones",
  "controllers",
] as const;

export const VERIFICATION_ID_TYPES = ["govt_id_aadhaar", "passport", "driving_license", "company_pan_gstin"] as const;

export const SINGING_STYLES = ["playback", "classical", "ghazal", "sufi", "indie", "pop", "rock", "rap", "bhajan"] as const;
export const DJ_TYPES = ["club", "wedding", "radio", "festival", "bollywood"] as const;
export const ENGINEER_SPECIALTIES = ["recording", "mixing", "mastering", "live_sound"] as const;
export const LYRICIST_SPECIALIZATIONS = ["romantic", "commercial", "devotional", "rap", "film_songs", "independent_music"] as const;

/** Indian cities/areas the dump repeatedly uses — kept for filler generation flavor. */
export const CITIES = [
  "Mumbai, Andheri West",
  "Mumbai, Bandra",
  "Mumbai, Juhu",
  "Bengaluru, Indiranagar",
  "Bengaluru, Koramangala",
  "New Delhi, Hauz Khas",
  "New Delhi, GK I",
  "Pune, Baner",
  "Pune, Koregaon Park",
  "Goa, Anjuna",
  "Chennai, Nungambakkam",
  "Kolkata, Park Street",
  "Hyderabad, Jubilee Hills",
  "Ahmedabad, Navrangpura",
  "Chandigarh, Sector 17",
] as const;

/** Fictional first/last name pools for procedurally generated filler profiles — Indian-name style matching the dump's own named entries. */
export const FIRST_NAMES = [
  "Aarav",
  "Ananya",
  "Ishaan",
  "Meera",
  "Vivaan",
  "Diya",
  "Reyansh",
  "Kavya",
  "Aditya",
  "Sara",
  "Arnav",
  "Myra",
  "Kabir",
  "Anika",
  "Vihaan",
  "Riya",
  "Advait",
  "Zara",
  "Yuvraj",
  "Nitya",
  "Dev",
  "Ira",
  "Rudra",
  "Tara",
  "Shaurya",
  "Naina",
  "Arjun",
  "Alia",
  "Kunal",
  "Sanya",
] as const;

export const LAST_NAMES = [
  "Mehta",
  "Kapoor",
  "Iyer",
  "Nair",
  "Reddy",
  "Bhatt",
  "Chopra",
  "Menon",
  "Rao",
  "Bose",
  "Khanna",
  "Joshi",
  "Sharma",
  "Verma",
  "Gupta",
  "Pillai",
  "Chatterjee",
  "Desai",
  "Kulkarni",
  "Banerjee",
] as const;

export const BAND_NAME_WORDS_A = ["Midnight", "Copper", "Velvet", "Crimson", "Analog", "Paper", "Neon", "Silver", "Monsoon", "Echo"] as const;
export const BAND_NAME_WORDS_B = ["Owls", "Wolves", "Static", "Collective", "Circuit", "Parade", "Society", "Machine", "Riot", "Ensemble"] as const;

export const STUDIO_CONSOLE_TYPES = [
  "SSL Duality Delta 48-Channel",
  "Neve 8816 Summing Mixer",
  "Avid S6 Digital Console",
  "API 1608",
  "Solid State Logic Origin",
] as const;
