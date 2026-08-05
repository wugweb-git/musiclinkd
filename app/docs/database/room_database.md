# Room Database & Local Caching — MusicLinkd

## Local Room Database Configuration

- **Database Class:** `AppDatabase.kt`
- **DAOs:** `UserDao`, `ArtistDao`, `StudioDao`, `MessageDao`
- **Entity Storage:** Caches user profile details, offline direct messages, and saved favorite artists.
- **KSP Processing:** Annotated with Room `@Entity`, `@Dao`, and `@Database`.
