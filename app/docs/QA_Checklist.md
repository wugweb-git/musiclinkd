# Quality Assurance (QA) Test Suite

## End-to-End Test Matrix

### 1. Onboarding & Registration
- [x] Launch app -> Verify Splash screen renders within 1 second.
- [x] Click Google Sign-In -> Verify snapshot creation and redirect to Role Selection.
- [x] Complete multi-step survey -> Verify user preferences saved to Room & Firestore.

### 2. Networking & Discovery
- [x] Open Universal Search -> Type query "Guitarist" -> Verify filtered results appear.
- [x] Select Artist Card -> Verify navigation to Artist Profile with waveform audio player.
- [x] Click "Connect" -> Verify button transitions to "Pending" and FCM notification fires.

### 3. Media Upload & Storage
- [x] Open Artist Profile -> Click "Upload Media" -> Verify progress spinner and new STEM preview card addition.

### 4. Error Handling & Offline
- [x] Simulate network disconnection -> Verify `UnifiedErrorCard` displays gracefully with "Retry Request" trigger.
