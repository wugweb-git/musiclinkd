/**
 * Foreground FCM only (background messages are handled by
 * public/firebase-messaging-sw.js, a separate classic-script file — see
 * scripts/generate-fcm-sw.mjs). Feature-detected: unsupported in some
 * browsers and always unsupported during build-time prerender.
 */
import { type Messaging, getMessaging, getToken, isSupported, onMessage } from "firebase/messaging";
import { getFirebaseApp } from "./firebase";

let _messaging: Messaging | null | undefined;

async function getMessagingInstance(): Promise<Messaging | null> {
  if (_messaging !== undefined) return _messaging;
  if (typeof window === "undefined" || !(await isSupported())) {
    _messaging = null;
    return _messaging;
  }
  _messaging = getMessaging(getFirebaseApp());
  return _messaging;
}

export async function requestFcmToken(vapidKey: string): Promise<string | null> {
  const messaging = await getMessagingInstance();
  if (!messaging) return null;
  const permission = await Notification.requestPermission();
  if (permission !== "granted") return null;
  return getToken(messaging, { vapidKey });
}

export async function onForegroundMessage(callback: (payload: unknown) => void): Promise<() => void> {
  const messaging = await getMessagingInstance();
  if (!messaging) return () => {};
  return onMessage(messaging, callback);
}
