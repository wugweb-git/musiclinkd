# User Journeys & Flow Specifications

## 1. Authentication & Onboarding Journey
```
Splash -> Welcome -> Auth Choice (Google / Email) -> Role Selection -> Genres & Gear -> Onboarding Success -> Dashboard
```
- **Loading State:** Circular animated progress indicator.
- **Empty / Failure State:** Redesigned error banner with retry option.
- **Completion:** Navigates directly to main Home hub.

## 2. Artist Discovery & Connection Request Flow
```
Home / Discover -> Universal Search -> Select Artist Card -> View Artist Profile -> Click "Connect" -> Firestore Write -> FCM Notification Triggered -> UI State Updated to "Pending"
```

## 3. Media Upload & Profile Portfolio Flow
```
Artist Profile -> Click "Upload Media" -> Select File -> Firebase Storage Upload -> Firestore Profile Document Merge -> Real-time UI Refresh
```

## 4. Studio Session Booking Flow
```
Discover Studios -> Select Studio Card -> Choose Time Slot & Equipment -> Review Total -> Confirm Reservation -> Booking Saved to Room & Firestore
```
