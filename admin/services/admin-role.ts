import { doc, getDoc } from "firebase/firestore";
import { COLLECTIONS } from "@/config/constants";
import type { AdminRole, AdminRoleDoc } from "@/types/firestore";
import { getFirebaseDb } from "./firebase";

/** Returns null if the user has no /admin_roles doc — i.e. is not an admin at all. */
export async function fetchAdminRole(uid: string): Promise<AdminRole | null> {
  const snap = await getDoc(doc(getFirebaseDb(), COLLECTIONS.adminRoles, uid));
  if (!snap.exists()) return null;
  return (snap.data() as AdminRoleDoc).role ?? null;
}
