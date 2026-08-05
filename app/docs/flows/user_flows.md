# Application Flows Documentation — MusicLinkd

## Products Topology
- **Website:** `musiclinkd.com`
- **Application:** `app.musiclinkd.com`
- **Admin Portal:** `admin.musiclinkd.com`
- **Repository:** `https://github.com/wugweb-git/musiclinkd`

## End-to-End User Journeys

### 1. Onboarding & Registration Flow
`Splash -> Welcome -> Auth Choice -> Role Selection -> Genre Preferences -> Home Feed`
- **States:** Loading -> Empty -> Success -> Error Fallback
- **Persistence:** Local Room DB & Firebase Auth

### 2. Connection Request & Messaging Flow
`Discover Directory -> Select Artist -> Profile View -> Connect Request -> FCM Notification -> Messaging Thread`

### 3. Studio Session Booking Flow
`Studio Search -> Select Studio -> Select Slot & Equipment -> Confirm Booking -> Local & Remote Sync`

### 4. Portfolio Media Upload Flow
`Artist Profile -> Upload Media Button -> Firebase Storage Upload -> STEM Waveform Preview Update`
