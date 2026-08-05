# ADR 002: Offline Room Database & Firebase Cloud Firestore Hybrid Persistence

## Status
Accepted

## Context
Musicians often work in studio environments with intermittent or zero cellular connectivity (e.g., soundproof recording booths). The platform must guarantee offline access to direct messaging threads, saved portfolios, and studio booking receipts.

## Decision
We implement a hybrid cache-first persistence layer:
- **Local Source of Truth:** SQLite via Room Database (`AppDatabase.kt`) for direct messages, user profile settings, and saved artists.
- **Remote Cloud Sync:** Firebase Cloud Firestore (`FirebaseManager.kt`) for real-time collaboration connection requests, networking feed posts, and push notification triggers.

## Rationale
- Immediate UI rendering from local Room cache upon app launch.
- Automatic background synchronization with Firestore when network connectivity is restored.
- Fault-tolerant local snapshot creation fallback if cloud auth operations experience timeout.
