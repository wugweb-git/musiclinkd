# MUSICLINKD CARD SPECIFICATIONS & RECYCLING CATALOG

Every card component in MusicLinkd strictly conforms to the **Unified 24.dp Base Container Component Architecture**.

---

## 📐 1. CARD CONTAINER SPECIFICATION

```kotlin
Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BrandBorder) // #ECECF1
) {
    // Parameterized Content
}
```

### Key Principles:
1. **Zero Shadow Noise**: Clean flat surface relying on the `1.dp` `BrandBorder` stroke.
2. **Generous Padding**: Minimum `16.dp` to `20.dp` internal content padding.
3. **Responsive Spacing**: Using `Arrangement.spacedBy(12.dp)` for structured hierarchy.
4. **Interactive Target**: Entire card surface or inner CTA meets the `48.dp` minimum touch target requirement.

---

## 🎴 2. RECYCLED CARD CATALOG

### 1. Artist Card (`UnifiedArtistCard`)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `artistName`, `role`, `location`, `avatarUrl`, `isVerified`, `isAvailable`, `genres`, `initialConnectStatus`.
- **Primary CTA**: Recycles `UnifiedConnectButton`.

### 2. Studio Card (`UnifiedStudioCard`)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `studioName`, `location`, `rating`, `reviewCount`, `hourlyRate`, `verifiedGold`, `amenities`, `imageUrl`.
- **Primary CTA**: `"Book Slot"` button (`UnifiedPrimaryButton`).

### 3. Track Card (`UnifiedTrackCard`)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `title`, `artistName`, `duration`, `coverUrl`, `isPlaying`, `onPlayToggle`.
- **Special Layout**: Embeds `WaveformAudioPlayer`.

### 4. Project / Collaboration Card (`UnifiedProjectCard`)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `projectTitle`, `creatorName`, `category`, `seekingRoles`, `budget`, `deadline`.
- **Primary CTA**: `"Apply for Session"`.

### 5. Marketplace Gear Card (`UnifiedMarketplaceCard`)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `gearName`, `category`, `priceText`, `condition`, `sellerName`, `imageUrl`.
- **Primary CTA**: `"Inquire Gear"`.

### 6. Opportunity Card (`UnifiedOpportunityCard`)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `title`, `organization`, `location`, `compensation`, `tags`, `deadline`.
- **Primary CTA**: `"Submit Audition"`.

### 7. Booking Reservation Card (`UnifiedBookingCard`)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `studioName`, `timeSlotText`, `status` (`CONFIRMED`, `PENDING`, `COMPLETED`), `totalAmount`.

### 8. Feed Post Card (`NetworkingFeed` Item)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `authorName`, `authorRole`, `postType`, `content`, `timestampText`, `audioTitle`, `likesCount`.
- **Primary CTA**: Integrated `UnifiedConnectButton` + audio waveform player.

### 9. Notification Card (`UnifiedNotificationCard`)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `title`, `message`, `timestamp`, `isUnread`.

### 10. Client Review Card (`UnifiedReviewCard`)
- **Container**: Base 24dp white card with 1dp border.
- **State Parameters**: `reviewerName`, `reviewerRole`, `rating`, `reviewText`, `dateText`.
