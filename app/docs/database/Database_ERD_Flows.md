# Database ERD & Hybrid Data Flow Architecture — MusicLinkd

## Entity Relationship Diagram (ERD)

```
                       ┌──────────────────────┐
                       │     users (Doc)      │
                       ├──────────────────────┤
                       │ id: String (PK)      │
                       │ displayName: String  │
                       │ role: String         │
                       │ isVerified: Boolean  │
                       └──────────┬───────────┘
                                  │
         ┌────────────────────────┼────────────────────────┐
         │ 1                      │ 1                      │ 1
         ▼ N                      ▼ N                      ▼ N
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│ connections(Doc) │    │ feed_posts(Doc)  │    │studio_bookings(Doc)│
├──────────────────┤    ├──────────────────┤    ├──────────────────┤
│ id: String (PK)  │    │ id: String (PK)  │    │ id: String (PK)  │
│ senderUid: String│    │ authorUid: String│    │ studioId: String │
│ targetUid: String│    │ content: String  │    │ userId: String   │
│ status: String   │    │ audioSnippetUrl  │    │ slotText: String │
└──────────────────┘    └──────────────────┘    └──────────────────┘
```

---

## Room SQLite Caching Strategy

```
[UI Composable] ──► [ViewModel] ──► [Repository]
                                        │
                         ┌──────────────┴──────────────┐
                         ▼                             ▼
                [Room Database (SQLite)]    [Firebase Firestore Remote]
                   (Fast Offline Read)         (Async Background Sync)
```
