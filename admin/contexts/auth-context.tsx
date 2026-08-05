"use client";

import type { User } from "firebase/auth";
import { createContext, useEffect, useState } from "react";
import type { AdminRole } from "@/types/firestore";
import { fetchAdminRole } from "@/services/admin-role";
import { onAuthStateChanged } from "@/services/auth";
import { isFirebaseConfigured } from "@/config/environment";

export interface AuthContextValue {
  user: User | null;
  role: AdminRole | null;
  /** True while the initial Auth + role check is in flight. */
  loading: boolean;
  /** True once loading is false and the user is signed in but has no /admin_roles doc. */
  isUnauthorized: boolean;
}

export const AuthContext = createContext<AuthContextValue>({
  user: null,
  role: null,
  loading: true,
  isUnauthorized: false,
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [role, setRole] = useState<AdminRole | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isFirebaseConfigured) {
      setLoading(false);
      return;
    }
    const unsubscribe = onAuthStateChanged(async (nextUser) => {
      setUser(nextUser);
      if (!nextUser) {
        setRole(null);
        setLoading(false);
        return;
      }
      const nextRole = await fetchAdminRole(nextUser.uid);
      setRole(nextRole);
      setLoading(false);
    });
    return unsubscribe;
  }, []);

  const isUnauthorized = !loading && user !== null && role === null;

  return (
    <AuthContext.Provider value={{ user, role, loading, isUnauthorized }}>
      {children}
    </AuthContext.Provider>
  );
}
