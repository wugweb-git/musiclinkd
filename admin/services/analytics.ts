/**
 * Browser-only and feature-detected — Analytics isn't supported in every
 * context (ad blockers, unsupported browsers, and definitely not during
 * Next's build-time prerender). Logging is pageview/event only; this is not
 * a source for a custom Analytics dashboard — see the approved plan's
 * "Analytics module has nothing to connect to" flag.
 */
import { type Analytics, getAnalytics, isSupported, logEvent } from "firebase/analytics";
import { getFirebaseApp } from "./firebase";

let _analytics: Analytics | null | undefined;

async function getAnalyticsInstance(): Promise<Analytics | null> {
  if (_analytics !== undefined) return _analytics;
  if (typeof window === "undefined" || !(await isSupported())) {
    _analytics = null;
    return _analytics;
  }
  _analytics = getAnalytics(getFirebaseApp());
  return _analytics;
}

export async function trackEvent(name: string, params?: Record<string, unknown>): Promise<void> {
  const analytics = await getAnalyticsInstance();
  if (analytics) logEvent(analytics, name, params);
}
