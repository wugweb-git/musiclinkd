# Component Catalogue — MusicLinkd Reusable UI Design System

## Reusable Components Specification (`UnifiedComponents.kt`)

### 1. `UnifiedBaseCard`
- **Purpose:** Primary container element adhering to the mandatory 24.dp corner radius and 1.dp border stroke design token.
- **Props:** `modifier: Modifier`, `onClick: (() -> Unit)?`, `content: @Composable ColumnScope.() -> Unit`
- **Design Tokens:** Background `#FFFFFF`, Border `#ECECF1`, Corner Radius `24.dp`.

### 2. `UnifiedPrimaryButton`
- **Purpose:** Main call-to-action pill button for primary screen actions.
- **Props:** `text: String`, `onClick: () -> Unit`, `isLoading: Boolean`, `enabled: Boolean`
- **Design Tokens:** Height `48.dp`, Background `#EA4C89`, Text `#FFFFFF`, Pill Radius `999.dp`.

### 3. `UnifiedConnectButton`
- **Purpose:** Stateful connection toggle button with Checkmark transition.
- **Props:** `status: ConnectionStatus`, `onConnectClick: () -> Unit`
- **States:** `IDLE` ("Connect"), `PENDING` ("Requested"), `CONNECTED` ("Connected").

### 4. `UnifiedErrorCard`
- **Purpose:** Standardized error container with error code and retry button.
- **Props:** `errorCode: String`, `errorMessage: String`, `onRetry: () -> Unit`

### 5. `SearchAndFilterHeader`
- **Purpose:** Sticky search field input with horizontal category filter chips.
- **Props:** `searchQuery: String`, `onQueryChange: (String) -> Unit`, `selectedCategory: String`, `onCategorySelected: (String) -> Unit`
