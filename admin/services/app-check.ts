/**
 * Scaffolded but inert — App Check's own status in the Connect table is
 * "Enable after initial testing." This initializes the SDK only when a real
 * reCAPTCHA v3 site key is present, and even then does not turn on
 * enforcement (that's a separate per-service console toggle). Call
 * `initAppCheckIfConfigured()` once from the root layout; it's a no-op
 * otherwise.
 */
import { type AppCheck, ReCaptchaV3Provider, initializeAppCheck } from "firebase/app-check";
import { env } from "@/config/environment";
import { getFirebaseApp } from "./firebase";

let _appCheck: AppCheck | undefined;

export function initAppCheckIfConfigured(): void {
  if (typeof window === "undefined") return;
  if (!env.appCheckSiteKey || _appCheck) return;
  _appCheck = initializeAppCheck(getFirebaseApp(), {
    provider: new ReCaptchaV3Provider(env.appCheckSiteKey),
    isTokenAutoRefreshEnabled: false,
  });
}
