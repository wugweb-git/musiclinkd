# Firebase Storage Integration — MusicLinkd

## Storage Paths & Policies

- **Audio STEM Snippets:** `gs://musiclinkd.appspot.com/users/{userId}/stems/{fileName}`
- **Portfolio Images:** `gs://musiclinkd.appspot.com/users/{userId}/images/{fileName}`
- **Studio Imagery:** `gs://musiclinkd.appspot.com/studios/{studioId}/photos/{fileName}`

## Upload Workflow
Uploads are managed by `FirebaseManager.uploadMediaAttachment()` returning public HTTPS download URLs.
