# Clean Architecture & MVVM Pattern — MusicLinkd

## Layer Architecture

```
┌──────────────────────────────────────────────────────────┐
│                   UI Layer (Compose)                      │
├──────────────────────────────────────────────────────────┤
│                ViewModel (StateFlow)                     │
├──────────────────────────────────────────────────────────┤
│             Repository Layer (Singletons)               │
├──────────────────────────────────────────────────────────┤
│   Data Sources: Room DB | FirebaseManager | Gemini API   │
└──────────────────────────────────────────────────────────┘
```

- **Unidirectional Data Flow (UDF):** User actions emit events to ViewModels, which emit immutable `StateFlow` UI states to Composables.
- **Resiliency Pattern:** Network calls degrade gracefully to `DemoDataProvider` fallback data when offline.
