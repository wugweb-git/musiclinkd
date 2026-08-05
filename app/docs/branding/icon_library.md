# Icon Library Catalog — MusicLinkd

## 📦 Single Source of Truth (`com.example.components.IconLibrary`)

All vector icon assets across the `app.musiclinkd.com` Android target and design system are mapped and cataloged below. Icons are loaded programmatically via `IconLibrary.getIcon(key)` or directly referenced through vector drawables in `app/src/main/res/drawable/`.

---

## 🏷️ Cataloged Icon Assets

### 1. Navigation & App Controls (`res/drawable` & `IconLibrary`)

| Icon Key | Icon Name | Category | Usage Context | Source / Drawable Path |
| :--- | :--- | :--- | :--- | :--- |
| `back` | Back Arrow | Navigation | Header back navigation button | `Icons.AutoMirrored.Filled.ArrowBack` |
| `search` | Search Glass | Navigation | Artist, track, and studio search bar | `Icons.Default.Search` |
| `close` | Close / Dismiss | Navigation | Modal sheet and dialog dismissal | `Icons.Default.Close` |
| `more` | More Options | Navigation | Overflow menu dropdown | `Icons.Default.MoreVert` |
| `check` | Checkmark | Navigation | Form completion and selection state | `Icons.Default.Check` |

---

### 2. Primary Navigation Tabs (`IconLibrary`)

| Icon Key | Icon Name | Category | Usage Context | Source / Drawable Path |
| :--- | :--- | :--- | :--- | :--- |
| `discover` | Discover / Home | MainTabs | Synergy matchmaker & feed tab | `Icons.Default.Home` |
| `marketplace` | Marketplace | MainTabs | Audio STEM & gear marketplace tab | `Icons.Default.ShoppingBag` |
| `connections` | Network | MainTabs | Connections & band roster tab | `Icons.Default.People` |
| `messages` | Direct Chat | MainTabs | Chat threads & audio notes tab | `Icons.Default.Email` |
| `profile` | Artist Profile | MainTabs | User passport & portfolio tab | `Icons.Default.Person` |

---

### 3. Audio Player & Media Controls (`IconLibrary` & `res/drawable`)

| Icon Key | Icon Name | Category | Usage Context | Source / Drawable Path |
| :--- | :--- | :--- | :--- | :--- |
| `play` | Play | Audio | STEM track preview playback | `Icons.Default.PlayArrow` |
| `pause` | Pause | Audio | Active track playback pause | `Icons.Default.Pause` |
| `music` | Music Note | Audio | Audio asset & STEM indicator | `Icons.Default.MusicNote` |
| `mic` | Vocal Mic | Audio | Vocalist role & audio note recording | `Icons.Default.Mic` |
| `headset` | Headphones | Audio | Producer & audio engineer role | `Icons.Default.Headset` |
| `send_audio` | Send Track | Audio | Send track attachment in chat | `Icons.AutoMirrored.Filled.Send` |

---

### 4. Business, Studio & Badges (`IconLibrary`)

| Icon Key | Icon Name | Category | Usage Context | Source / Drawable Path |
| :--- | :--- | :--- | :--- | :--- |
| `studio` | Storefront / Studio | Business | Studio suite directory & listings | `Icons.Default.Storefront` |
| `calendar` | Date / Slot Picker | Business | Hourly studio slot booking | `Icons.Default.DateRange` |
| `star` | Star Rating | Business | Verified review scores & badges | `Icons.Default.Star` |
| `verified` | Verified Badge | Business | Verified Industry Passport badge | `Icons.Default.Verified` |

---

## 🎨 Design System Code Usage Example

```kotlin
import androidx.compose.material3.Icon
import com.example.components.IconLibrary

// Rendering a cataloged icon
Icon(
    imageVector = IconLibrary.getIcon("discover"),
    contentDescription = "Discover Feed",
    tint = MaterialTheme.colorScheme.primary
)
```
