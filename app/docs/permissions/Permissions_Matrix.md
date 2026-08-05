# Role & System Permissions Matrix — MusicLinkd

## Platform Role Permissions (`app.musiclinkd.com` & `admin.musiclinkd.com`)

| Action / Capability | Guest | User | Verified Artist | Business / Studio | Moderator | Admin |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **View Profiles & Feeds** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Post Feed Updates** | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Send Connection Request** | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Upload Audio STEMs** | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **List Studio Suite** | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
| **Manage Verified Badges** | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| **Ban / Delete Users** | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| **Access Admin Dashboard (`admin.musiclinkd.com`)** | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |

---

## Android System Runtime Permissions (`AndroidManifest.xml`)

| Permission Name | Category | Android API | Purpose in App |
| :--- | :--- | :--- | :--- |
| `android.permission.INTERNET` | Normal | All | Cloud Firestore, Firebase Storage, Gemini API |
| `android.permission.ACCESS_NETWORK_STATE` | Normal | All | Network connectivity monitor & offline toggle |
| `android.permission.POST_NOTIFICATIONS` | Dangerous | API 33+ | FCM push notification alerts for connection requests |
| `android.permission.RECORD_AUDIO` | Dangerous | All | In-app vocal snippet recording & audio preview generation |
| `android.permission.READ_MEDIA_AUDIO` | Dangerous | API 33+ | Selecting audio STEM files for profile upload |
