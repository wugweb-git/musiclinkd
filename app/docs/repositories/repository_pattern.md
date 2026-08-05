# Repository Pattern & Repositories — MusicLinkd

## Repository Architecture

- `OnboardingRepository`: Handles user registration, role selection, and user profile state.
- `ArtistRepository`: Manages queries for artist listings, genre filters, and profile metadata from Room & Firestore.
- `StudioRepository`: Manages studio availability slots, booking persistence, and equipment specifications.
- `MessagingRepository`: Handles chat threads, peer messages, and offline message queue.
- `RepositoryProvider`: Centralized singleton providing repository access across ViewModels.
