# Repository Architecture & Data Flow Diagram — MusicLinkd

## Repository Flow Architecture

```
                       ┌──────────────────────┐
                       │  RepositoryProvider  │
                       └──────────┬───────────┘
                                  │
         ┌────────────────────────┼────────────────────────┐
         ▼                        ▼                        ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│ ArtistRepository │    │ StudioRepository │    │MessagingRepository│
├──────────────────┤    ├──────────────────┤    ├──────────────────┤
│ - getArtists()   │    │ - getStudios()   │    │ - getMessages()  │
│ - getArtistById()│    │ - bookSlot()     │    │ - sendMessage()  │
└────────┬─────────┘    └────────┬─────────┘    └────────┬─────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 ▼
                     ┌───────────────────────┐
                     │   DemoDataProvider    │
                     │  (Centralized Seed)   │
                     └───────────────────────┘
```
