# Styleguide — Material Icons System & IconLibrary Mapping

## 📦 Centralized Single Entry Point (`com.example.components.IconLibrary`)

All domain icons across `app.musiclinkd.com` are imported and accessed via `IconLibrary`.

```kotlin
import com.example.components.IconLibrary

// Example usage
Icon(
    imageVector = IconLibrary.getIcon("discover"),
    contentDescription = "Discover Feed"
)
```

---

## 🏷️ Labeled Icon Asset Mapping

### 1. Navigation Category
- **`back`**: Back Arrow (`Icons.AutoMirrored.Filled.ArrowBack`) — Primary screen back navigation.
- **`search`**: Search (`Icons.Default.Search`) — Catalog and profile query input.
- **`close`**: Close (`Icons.Default.Close`) — Sheet and dialog dismissal.
- **`more`**: More Options (`Icons.Default.MoreVert`) — Contextual dropdown menus.
- **`check`**: Checkmark (`Icons.Default.Check`) — Selection and confirmation state.

### 2. Main Tabs Category
- **`discover`**: Discover Feed (`Icons.Default.Home`) — Synergy matchmaker and primary feed.
- **`marketplace`**: Gear & STEM Marketplace (`Icons.Default.ShoppingBag`) — Equipment and sample packs.
- **`connections`**: Network & Collaborators (`Icons.Default.People`) — Social connections and bands.
- **`messages`**: Direct Messaging (`Icons.Default.Email`) — Collaboration chat threads.
- **`profile`**: Artist Profile (`Icons.Default.Person`) — Musician passport and portfolio.

### 3. Audio & Media Category
- **`play`**: Play Audio (`Icons.Default.PlayArrow`) — STEM preview playback.
- **`pause`**: Pause Audio (`Icons.Default.Pause`) — Active track pause.
- **`music`**: Music Track (`Icons.Default.MusicNote`) — General audio asset indicator.
- **`mic`**: Vocal Microphone (`Icons.Default.Mic`) — Vocalist role and voice recording.
- **`headset`**: Studio Monitor Headphones (`Icons.Default.Headset`) — Producer and mixing role.
- **`send_audio`**: Send Audio Track (`Icons.AutoMirrored.Filled.Send`) — Messaging track attachment.

### 4. Business & Booking Category
- **`studio`**: Recording Studio (`Icons.Default.Storefront`) — Studio suite directory.
- **`calendar`**: Booking Calendar (`Icons.Default.DateRange`) — Hourly slot reservation.
- **`star`**: Reputation Rating (`Icons.Default.Star`) — Verified review scores.
- **`verified`**: Verified Passport (`Icons.Default.Verified`) — Industry badge verification.
