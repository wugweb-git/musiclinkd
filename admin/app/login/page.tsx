"use client";

import { Music2 } from "lucide-react";
import { useRouter, useSearchParams } from "next/navigation";
import { Suspense, useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { useAuth } from "@/hooks/use-auth";
import { signInWithGoogle } from "@/services/auth";
import { trackEvent } from "@/services/analytics";
import { ANALYTICS_EVENTS } from "@/config/constants";

function LoginForm() {
  const [pending, setPending] = useState(false);
  const router = useRouter();
  const searchParams = useSearchParams();
  const { user, role, loading } = useAuth();

  const redirectTo = searchParams.get("redirect") ?? "/dashboard";
  const authError = searchParams.get("error");

  useEffect(() => {
    if (authError === "unauthorized") {
      toast.error("Your account isn't set up as a MusicLinkd operator yet.");
    }
  }, [authError]);

  useEffect(() => {
    if (!loading && user && role) router.replace(redirectTo);
  }, [loading, user, role, redirectTo, router]);

  async function handleSignIn() {
    setPending(true);
    try {
      await signInWithGoogle();
      await trackEvent(ANALYTICS_EVENTS.adminLogin);
    } catch {
      toast.error("Sign-in failed. Try again.");
    } finally {
      setPending(false);
    }
  }

  return (
    <div className="flex min-h-svh items-center justify-center bg-background px-4">
      <Card className="w-full max-w-sm">
        <CardHeader className="items-center text-center">
          <Music2 className="mb-2 size-8 text-primary" />
          <CardTitle className="font-heading text-2xl">MusicLinkd Operations</CardTitle>
          <CardDescription>Sign in with your MusicLinkd Google account to continue.</CardDescription>
        </CardHeader>
        <CardContent>
          <Button className="w-full" size="lg" onClick={handleSignIn} disabled={pending}>
            {pending ? "Signing in…" : "Continue with Google"}
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}

export default function LoginPage() {
  return (
    <Suspense fallback={null}>
      <LoginForm />
    </Suspense>
  );
}
