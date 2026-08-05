# Standard UI States — MusicLinkd

## Sealed Resource State Pattern (`Resource<T>`)
Every asynchronous data fetch across `app.musiclinkd.com` implements four required visual states:

1. **Loading State:** `CircularProgressIndicator` centered inside card or screen.
2. **Success State:** Rendered UI with data binding.
3. **Empty State:** `EmptyStateView` component with descriptive subtitle and call to action.
4. **Error State:** `UnifiedErrorCard` or `ErrorBoundary` with warning icon and "Retry Request" action trigger.
