# API Contracts & Integration Abstractions

## 1. Firebase Firestore API Contracts

### User Document (`users/{userId}`)
```json
{
  "userId": "string",
  "email": "string",
  "displayName": "string",
  "role": "ARTIST | PRODUCER | STUDIO | LABEL",
  "bio": "string",
  "location": "string",
  "genres": ["string"],
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### Connection Request (`connections/{senderUid_targetUid}`)
```json
{
  "senderUid": "string",
  "senderEmail": "string",
  "targetUserId": "string",
  "targetUserName": "string",
  "status": "PENDING | ACCEPTED | REJECTED",
  "message": "string",
  "timestamp": "timestamp"
}
```

### Networking Feed Post (`feed_posts/{postId}`)
```json
{
  "postId": "string",
  "authorUid": "string",
  "authorName": "string",
  "authorRole": "string",
  "content": "string",
  "postType": "COLLAB_CALL | RELEASE | STUDIO_UPDATE",
  "mediaUrl": "string?",
  "likesCount": "number",
  "timestamp": "timestamp"
}
```

## 2. Gemini 1.5 Flash Matchmaker Service Interface
```kotlin
interface GeminiMatchmakerService {
    suspend fun calculateSynergyScore(
        artistA: UserProfile,
        artistB: UserProfile
    ): SynergyResult
}
```
