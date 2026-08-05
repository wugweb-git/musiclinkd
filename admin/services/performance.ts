/** Browser-only — Performance Monitoring has a real Web SDK, unlike Crashlytics. */
import { type FirebasePerformance, getPerformance } from "firebase/performance";
import { getFirebaseApp } from "./firebase";

let _performance: FirebasePerformance | undefined;

export function getPerformanceInstance(): FirebasePerformance | null {
  if (typeof window === "undefined") return null;
  if (!_performance) _performance = getPerformance(getFirebaseApp());
  return _performance;
}
