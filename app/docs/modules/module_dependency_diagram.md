# Module Dependency & Architecture Diagram — MusicLinkd

## Module Architecture Diagram

```
                 ┌────────────────────────────────┐
                 │       com.example.onboarding   │
                 └───────────────┬────────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         ▼                       ▼                       ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│     ui.auth      │    │    ui.discover   │    │   ui.profile     │
│ (Login/Onboard)  │    │ (Search & Feed)  │    │(Artist Portfolio)│
└────────┬─────────┘    └────────┬─────────┘    └────────┬─────────┘
         │                       │                       │
         ├───────────────────────┼───────────────────────┤
         ▼                       ▼                       ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│   ui.messaging   │    │    ui.studios    │    │  ui.marketplace  │
│ (Direct Messages)│    │(Slot Reservation)│    │(Gear & STEM Packs│
└────────┬─────────┘    └────────┬─────────┘    └────────┬─────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 ▼
                    ┌──────────────────────────┐
                    │  UnifiedComponents (M3)  │
                    │   & DemoDataProvider     │
                    └──────────────────────────┘
```
