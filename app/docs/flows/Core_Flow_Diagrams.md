# Core Flow Diagrams — MusicLinkd User & System Flows

## 1. Authentication Flow (`Auth_Flow_Diagram.md`)
```mermaid
sequenceDiagram
    autonumber
    User->>App: Launch app / Click "Sign In with Google"
    App->>FirebaseAuth: Prompt Google Identity Credentials
    FirebaseAuth-->>App: Return User ID Token & User Credentials
    App->>FirebaseManager: createUserSnapshot(uid, email, displayName)
    alt Firestore Success
        FirebaseManager-->>App: Snapshot created successfully
    else Cloud Timeout / Offline
        FirebaseManager-->>App: Local Snapshot Fallback (DemoDataProvider)
    end
    App->>MainTabScreen: Navigate to Main Tab Host Screen
```

---

## 2. Direct Messaging Flow (`Messaging_Flow_Diagram.md`)
```mermaid
sequenceDiagram
    autonumber
    ArtistA->>UI: Type message in ChatThreadView & click Send
    UI->>MessagingViewModel: sendMessage(receiverId, text)
    MessagingViewModel->>RoomDB: Insert DirectMessage (Local Instant Render)
    MessagingViewModel->>Firestore: Add document to /chat_threads/{id}/messages
    MessagingViewModel->>AppNotificationManager: Dispatch FCM Push Notification
    AppNotificationManager-->>ArtistB: Display Push Banner ("New message from Artist A")
```

---

## 3. Studio Session Booking Flow (`Booking_Flow_Diagram.md`)
```mermaid
sequenceDiagram
    autonumber
    User->>StudioBookingScreen: Browse Studio Suites (e.g. Octave Sound Labs)
    User->>SlotPickerSheet: Select Date & Time Slot (e.g. 4:00 PM - 8:00 PM)
    User->>UI: Confirm Reservation & Equipment requested
    UI->>StudioViewModel: submitBookingRequest(studioId, slotText, totalCost)
    StudioViewModel->>Firestore: Insert into /studio_bookings collection
    StudioViewModel-->>UI: Display BookingConfirmationCard (Receipt ID)
```

---

## 4. Media Upload Flow (`Media_Upload_Flow_Diagram.md`)
```mermaid
sequenceDiagram
    autonumber
    User->>ArtistProfileScreen: Click "Upload Audio STEM"
    User->>FilePicker: Select Audio File (.mp3 / .wav)
    FilePicker-->>FirebaseManager: Pass File Bytes & File Name
    FirebaseManager->>FirebaseStorage: Upload to gs://musiclinkd/users/{uid}/stems/
    FirebaseStorage-->>FirebaseManager: Return HTTPS Public Download URL
    FirebaseManager->>Firestore: Update user document profile audio STEMs list
    Firestore-->>UI: Refresh AudioWaveformPlayer UI
```
