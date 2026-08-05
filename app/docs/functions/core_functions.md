# Core Functions Reference — MusicLinkd

## Key API Functions (`FirebaseManager.kt`)

### `createUserSnapshot`
- **Signature:** `createUserSnapshot(userId: String, email: String, displayName: String?, onSuccess: () -> Unit, onFailure: (String) -> Unit)`
- **Purpose:** Creates user document in Firestore `/users/{uid}` with automatic offline emulation fallback.

### `sendConnectionRequest`
- **Signature:** `sendConnectionRequest(targetUserId: String, targetUserName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit)`
- **Purpose:** Sends collaboration connection request and triggers FCM push alert via `AppNotificationManager`.

### `uploadMediaAttachment`
- **Signature:** `uploadMediaAttachment(fileName: String, mediaType: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit)`
- **Purpose:** Uploads audio STEMs or artwork images to Firebase Storage.

### `calculateSynergyScore` (`GeminiMatchmaker.kt`)
- **Signature:** `calculateSynergyScore(artistA: UserProfile, artistB: UserProfile): SynergyScoreResult`
- **Purpose:** Uses Gemini 1.5 Flash model to evaluate creative compatibility score.
