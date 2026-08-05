# Data Models Catalog — MusicLinkd

## Data Entities Catalog (`com.example.onboarding.models` & `DemoDataProvider`)

Every domain model across `app.musiclinkd.com` is documented below with its field schema, data types, and nullability rules.

---

## 1. `Artist` Entity (`DemoArtist`)
```kotlin
data class DemoArtist(
    val id: String,
    val name: String,
    val role: String,
    val location: String,
    val isVerified: Boolean = true,
    val isAvailable: Boolean = true,
    val rating: Double = 4.9,
    val reviewsCount: Int = 18,
    val hourlyRate: String = "₹2,500/hr",
    val genres: List<String>,
    val avatarUrl: String,
    val bio: String,
    val primaryInstrument: String
)
```

## 2. `Project` Entity
```kotlin
data class DemoProject(
    val id: String,
    val title: String,
    val artistName: String,
    val roleRequired: String,
    val budget: String,
    val status: String, // "OPEN", "IN_PROGRESS", "COMPLETED"
    val genres: List<String>,
    val description: String
)
```

## 3. `Track` Entity (`DemoTrack`)
```kotlin
data class DemoTrack(
    val id: String,
    val title: String,
    val artistName: String,
    val durationText: String,
    val genre: String,
    val coverUrl: String,
    val audioSampleUrl: String
)
```

## 4. `Review` Entity (`DemoReview`)
```kotlin
data class DemoReview(
    val id: String,
    val reviewerName: String,
    val reviewerRole: String,
    val rating: Double,
    val dateText: String,
    val comment: String
)
```

## 5. `Studio` Entity (`DemoStudio`)
```kotlin
data class DemoStudio(
    val id: String,
    val name: String,
    val location: String,
    val rating: Double,
    val reviewCount: Int,
    val hourlyRate: String,
    val verifiedGold: Boolean,
    val amenities: List<String>,
    val imageUrl: String,
    val consoleType: String
)
```

## 6. `Booking` Entity
```kotlin
data class DemoBooking(
    val id: String,
    val studioId: String,
    val studioName: String,
    val date: String,
    val timeSlot: String,
    val equipmentRequested: List<String>,
    val totalCost: String,
    val status: String // "CONFIRMED", "PENDING", "CANCELLED"
)
```

## 7. `Conversation` Entity & `Message`
```kotlin
data class DirectMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val messageText: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
```

## 8. `MarketplaceItem` Entity
```kotlin
data class MarketplaceItem(
    val id: String,
    val title: String,
    val sellerName: String,
    val price: String,
    val category: String, // "GEAR", "STEMS", "SERVICES"
    val imageUrl: String,
    val condition: String
)
```

## 9. `Notification` Entity
```kotlin
data class AppNotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val type: String, // "CONNECTION", "BOOKING", "MESSAGE", "SYSTEM"
    val timestamp: Long,
    val isRead: Boolean = false
)
```
