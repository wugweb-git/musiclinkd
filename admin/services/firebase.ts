/**
 * Firebase singleton bootstrap. Every getter is lazy — nothing calls
 * `initializeApp`/`getAuth`/etc. at module-load time, so this file is safe to
 * import from a client component that Next prerenders once during
 * `next build` even without real credentials present (see environment.ts).
 * Call sites should check `isFirebaseConfigured` before relying on live data.
 */
import { type FirebaseApp, getApps, initializeApp } from "firebase/app";
import { type Auth, getAuth } from "firebase/auth";
import { type Firestore, getFirestore } from "firebase/firestore";
import { type FirebaseStorage, getStorage } from "firebase/storage";
import { firebaseConfig } from "@/config/firebase.config";

let _app: FirebaseApp | undefined;

export function getFirebaseApp(): FirebaseApp {
  if (!_app) {
    _app = getApps().length > 0 ? getApps()[0]! : initializeApp(firebaseConfig);
  }
  return _app;
}

let _auth: Auth | undefined;
export function getFirebaseAuth(): Auth {
  if (!_auth) _auth = getAuth(getFirebaseApp());
  return _auth;
}

let _db: Firestore | undefined;
export function getFirebaseDb(): Firestore {
  if (!_db) _db = getFirestore(getFirebaseApp());
  return _db;
}

let _storage: FirebaseStorage | undefined;
export function getFirebaseStorage(): FirebaseStorage {
  if (!_storage) _storage = getStorage(getFirebaseApp());
  return _storage;
}
