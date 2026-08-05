# Notification Matrix & Delivery Architecture — MusicLinkd

## Products Topology
- **Website:** `musiclinkd.com`
- **Application Target:** `app.musiclinkd.com`
- **Admin Portal:** `admin.musiclinkd.com`
- **Official Email:** `hello@musiclinkd.com`

---

## 🔔 Cross-Channel Notification Matrix

| Event / Trigger | Primary Recipient | Push (FCM) | In-App Alert | Email Alert | Navigation Route Target | Priority |
| :--- | :--- | :---: | :---: | :---: | :--- | :---: |
| **Connection Request Received** | Target Artist | ✅ | ✅ | ✅ | `app.musiclinkd.com/artist/{sender_id}` | High |
| **Connection Accepted** | Requesting Artist | ✅ | ✅ | ✅ | `app.musiclinkd.com/messages/{thread_id}` | High |
| **Direct Message Received** | Thread Recipient | ✅ | ✅ | ❌ | `app.musiclinkd.com/messages/{thread_id}` | Immediate |
| **Studio Booking Confirmed** | Artist / Client | ✅ | ✅ | ✅ | `app.musiclinkd.com/booking/receipt/{id}` | High |
| **Studio Booking Cancelled** | Artist / Studio Host | ✅ | ✅ | ✅ | `app.musiclinkd.com/studios/{studio_id}` | High |
| **New Review Received** | Reviewed Artist / Studio | ✅ | ✅ | ❌ | `app.musiclinkd.com/artist/{id}#reviews` | Normal |
| **Marketplace Inquiry** | Listing Seller | ✅ | ✅ | ✅ | `app.musiclinkd.com/marketplace/item/{id}` | High |
| **Collab Opportunity Match** | Matched Artist | ✅ | ✅ | ❌ | `app.musiclinkd.com/discover?filter=matches` | Normal |
| **Verified Badge Approved** | Verified Artist | ✅ | ✅ | ✅ | `app.musiclinkd.com/profile` | High |
| **Profile Viewed** | Pro Artist | ❌ | ✅ | ❌ | `app.musiclinkd.com/analytics` | Low |
