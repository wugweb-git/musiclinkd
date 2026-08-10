import { HttpsError, type CallableRequest } from "firebase-functions/v2/https";
import { getFirestore } from "firebase-admin/firestore";
import { getAdminApp } from "./admin";

/**
 * Verifies the caller of an onCall function is signed in and has ROLE_ADMIN
 * in /admin_roles/{uid} — the same authority check firestore.rules'
 * isAdmin() performs client-side, re-implemented here because Cloud
 * Functions using the Admin SDK bypass firestore.rules entirely.
 */
export async function requireAdmin(auth: CallableRequest["auth"]): Promise<string> {
  if (!auth) {
    throw new HttpsError("unauthenticated", "Sign in required.");
  }
  const uid = auth.uid;
  const db = getFirestore(getAdminApp());
  const roleDoc = await db.collection("admin_roles").doc(uid).get();
  const role = roleDoc.exists ? (roleDoc.data()?.role as string | undefined) : undefined;
  if (role !== "ROLE_ADMIN") {
    throw new HttpsError("permission-denied", "ROLE_ADMIN required for this action.");
  }
  return uid;
}
