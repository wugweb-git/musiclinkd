import { onCall, HttpsError } from "firebase-functions/v2/https";
import { FieldValue, getFirestore } from "firebase-admin/firestore";
import { getRemoteConfig } from "firebase-admin/remote-config";
import { getAdminApp } from "./admin";
import { requireAdmin } from "./require-admin";

interface ToggleModuleRequest {
  moduleKey: string;
  enabled: boolean;
}

/**
 * Admin-panel ModuleKeys that can never be disabled — kept in sync by hand
 * with admin/config/constants.ts's NON_TOGGLEABLE_MODULES, since this
 * function can't import from the Next.js app.
 */
const NON_TOGGLEABLE_NAV_MODULE_KEYS = ["dashboard", "settings", "masterData", "modules"];

/**
 * The only path that can ever flip a modules/{key}.enabled field — Firestore
 * rules block clients from writing that field directly (see firestore.rules'
 * /modules match block), so this Admin-SDK write is authoritative. When the
 * module has a remoteConfigKey, this also publishes the matching Remote
 * Config parameter, since the client Remote Config SDK can only read
 * templates, never write them — that's the actual reason this needs to be a
 * Cloud Function instead of a plain Firestore write from the admin UI.
 */
export const toggleModule = onCall<ToggleModuleRequest>(async (request) => {
  const uid = await requireAdmin(request.auth);
  const { moduleKey, enabled } = request.data ?? {};
  if (!moduleKey || typeof moduleKey !== "string" || typeof enabled !== "boolean") {
    throw new HttpsError("invalid-argument", "moduleKey (string) and enabled (boolean) are required.");
  }

  const db = getFirestore(getAdminApp());
  const ref = db.collection("modules").doc(moduleKey);
  const snap = await ref.get();
  if (!snap.exists) {
    throw new HttpsError("not-found", `No module registered as "${moduleKey}".`);
  }
  const mod = snap.data()!;
  if (mod.navModuleKey && NON_TOGGLEABLE_NAV_MODULE_KEYS.includes(mod.navModuleKey)) {
    throw new HttpsError("failed-precondition", "This module cannot be disabled.");
  }

  await ref.update({ enabled, updatedBy: uid, updatedAt: FieldValue.serverTimestamp() });

  let remoteConfigUpdated = false;
  const remoteConfigKey = mod.remoteConfigKey as string | null | undefined;
  if (remoteConfigKey) {
    const rc = getRemoteConfig(getAdminApp());
    const template = await rc.getTemplate();
    template.parameters[remoteConfigKey] = {
      defaultValue: { value: String(enabled) },
      valueType: "BOOLEAN",
      description: `Mirrors modules/${moduleKey}.enabled — written by toggleModule().`,
    };
    await rc.publishTemplate(template);
    remoteConfigUpdated = true;
  }

  await db.collection("audit_logs").add({
    actorUid: uid,
    action: "module_toggled",
    targetPath: `modules/${moduleKey}`,
    timestamp: FieldValue.serverTimestamp(),
    metadata: { enabled },
  });

  return { success: true, remoteConfigUpdated };
});
