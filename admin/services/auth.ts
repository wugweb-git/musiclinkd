/**
 * Auth service — Google Sign-In only (decided: admin panel trusts real
 * Google identities; /admin_roles membership is the actual authorization
 * gate, not the login method). No email/password here — that's the Android
 * app's end-user auth surface (see backend_integrations.md), not this panel.
 */
import {
  type User,
  GoogleAuthProvider,
  onAuthStateChanged as fbOnAuthStateChanged,
  signInWithPopup,
  signOut as fbSignOut,
} from "firebase/auth";
import { getFirebaseAuth } from "./firebase";

const googleProvider = new GoogleAuthProvider();

export async function signInWithGoogle(): Promise<User> {
  const result = await signInWithPopup(getFirebaseAuth(), googleProvider);
  return result.user;
}

export async function signOut(): Promise<void> {
  await fbSignOut(getFirebaseAuth());
}

export function onAuthStateChanged(callback: (user: User | null) => void): () => void {
  return fbOnAuthStateChanged(getFirebaseAuth(), callback);
}

export function getCurrentUser(): User | null {
  return getFirebaseAuth().currentUser;
}
