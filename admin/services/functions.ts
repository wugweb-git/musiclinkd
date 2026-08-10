/**
 * Cloud Functions client wrapper. Only add callables here that genuinely
 * need server-side privilege the client SDK doesn't have — see
 * backend/functions/src/index.ts for why toggleModule specifically needs
 * this (Remote Config template writes require the Admin SDK).
 */
import { type Functions, getFunctions, httpsCallable } from "firebase/functions";
import { getFirebaseApp } from "./firebase";

let _functions: Functions | undefined;
function getFirebaseFunctions(): Functions {
  if (!_functions) _functions = getFunctions(getFirebaseApp());
  return _functions;
}

interface ToggleModuleRequest {
  moduleKey: string;
  enabled: boolean;
}

interface ToggleModuleResponse {
  success: true;
  remoteConfigUpdated: boolean;
}

export async function toggleModule(moduleKey: string, enabled: boolean): Promise<ToggleModuleResponse> {
  const fn = httpsCallable<ToggleModuleRequest, ToggleModuleResponse>(getFirebaseFunctions(), "toggleModule");
  const result = await fn({ moduleKey, enabled });
  return result.data;
}
