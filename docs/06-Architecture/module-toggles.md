# Module Feature Toggles Schema & Architecture
Archived from `backend/functions/src/index.ts` (Firebase Cloud Functions v2)

## Purpose
Authoritative server-side handler for flipping module feature flags (`modules/{moduleKey}.enabled`).

## Schema & Interfaces

```typescript
interface ToggleModuleRequest {
  moduleKey: string;
  enabled: boolean;
}

/**
 * Non-toggleable system modules that can never be disabled.
 */
const NON_TOGGLEABLE_NAV_MODULE_KEYS = [
  "dashboard",
  "settings",
  "masterData",
  "modules"
];
```

## Function Specification
* **Trigger:** HTTPS callable (`onCall`)
* **Auth Requirement:** Must have authenticated admin role (`requireAdmin(request.auth)`)
* **Execution Steps:**
  1. Validates input (`moduleKey` as string, `enabled` as boolean).
  2. Reads module document from database `modules/{moduleKey}`.
  3. Verifies that module is not in `NON_TOGGLEABLE_NAV_MODULE_KEYS`.
  4. Updates module record with `{ enabled, updatedBy: uid, updatedAt: timestamp }`.
  5. If `remoteConfigKey` is present, updates the matching Remote Config parameter (`defaultValue: String(enabled)`).
  6. Writes immutable record to `audit_logs` collection:
     ```typescript
     {
       actorUid: uid,
       action: "module_toggled",
       targetPath: `modules/${moduleKey}`,
       timestamp: serverTimestamp(),
       metadata: { enabled }
     }
     ```
  7. Returns `{ success: true, remoteConfigUpdated: boolean }`.
