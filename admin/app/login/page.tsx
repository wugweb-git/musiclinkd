"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { Suspense, useEffect, useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/use-auth";
import { signInWithGoogle } from "@/services/auth";
import { trackEvent } from "@/services/analytics";
import { ANALYTICS_EVENTS } from "@/config/constants";

// Same brand mark used on the marketing site's nav (website/index.html) —
// keep these two in sync if the mark ever changes.
function BrandMark({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <rect width="64" height="64" rx="16" fill="#141414" />
      <rect x="16" y="26" width="5" height="20" rx="2.5" fill="#FFFFFF" />
      <rect x="25.5" y="16" width="5" height="32" rx="2.5" fill="#EA4C89" />
      <rect x="35" y="22" width="5" height="24" rx="2.5" fill="#FFFFFF" />
      <rect x="44.5" y="30" width="5" height="16" rx="2.5" fill="#EA4C89" />
    </svg>
  );
}

function GoogleIcon() {
  return (
    <svg viewBox="0 0 24 24" className="size-4" aria-hidden="true">
      <path
        fill="#4285F4"
        d="M23.52 12.27c0-.85-.08-1.67-.22-2.45H12v4.64h6.47a5.54 5.54 0 0 1-2.4 3.64v2.99h3.86c2.27-2.08 3.58-5.16 3.58-8.82Z"
      />
      <path
        fill="#34A853"
        d="M12 24c3.24 0 5.96-1.07 7.94-2.91l-3.86-2.99c-1.07.72-2.45 1.14-4.08 1.14-3.13 0-5.79-2.11-6.74-4.96H1.28v3.09A12 12 0 0 0 12 24Z"
      />
      <path
        fill="#FBBC05"
        d="M5.26 14.28a7.2 7.2 0 0 1 0-4.56V6.63H1.28a12 12 0 0 0 0 10.74l3.98-3.09Z"
      />
      <path
        fill="#EA4335"
        d="M12 4.76c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.23 0 12 0A12 12 0 0 0 1.28 6.63l3.98 3.09C6.21 6.87 8.87 4.76 12 4.76Z"
      />
    </svg>
  );
}

function LoginForm() {
  const [pending, setPending] = useState(false);
  const router = useRouter();
  const searchParams = useSearchParams();
  const { user, role, loading, isUnauthorized } = useAuth();

  const redirectTo = searchParams.get("redirect") ?? "/dashboard";
  const authError = searchParams.get("error");

  useEffect(() => {
    if (authError === "unauthorized") {
      toast.error("Your account isn't set up as a MusicLinkd operator yet.");
    }
  }, [authError]);

  useEffect(() => {
    if (loading) return;
    if (user && role) {
      router.replace(redirectTo);
    } else if (isUnauthorized) {
      // Signed in with Google successfully, but no /admin_roles doc exists
      // for this UID yet — without this branch the page just silently sits
      // here with the sign-in button re-shown and no explanation.
      router.replace("/unauthorized");
    }
  }, [loading, user, role, isUnauthorized, redirectTo, router]);

  async function handleSignIn() {
    setPending(true);
    try {
      await signInWithGoogle();
      await trackEvent(ANALYTICS_EVENTS.adminLogin);
    } catch (err) {
      const code = err instanceof Error && "code" in err ? String((err as { code: unknown }).code) : "unknown";
      console.error("Sign-in failed:", err);
      toast.error(`Sign-in failed (${code}). See console for details.`);
    } finally {
      setPending(false);
    }
  }

  return (
    <div className="grid min-h-svh grid-cols-1 md:grid-cols-2">
      <div className="relative hidden flex-col justify-between overflow-hidden bg-[#141414] p-12 text-white md:flex">
        <div
          className="pointer-events-none absolute -right-24 -top-24 size-96 rounded-full opacity-20 blur-3xl"
          style={{ background: "radial-gradient(circle, #EA4C89, transparent 70%)" }}
        />
        <div
          className="pointer-events-none absolute -bottom-32 -left-16 size-96 rounded-full opacity-10 blur-3xl"
          style={{ background: "radial-gradient(circle, #8B5CF6, transparent 70%)" }}
        />
        <div className="relative flex items-center gap-3">
          <BrandMark className="size-9" />
          <span className="font-heading text-xl font-extrabold tracking-tight">
            MusicLink<em className="not-italic text-[#EA4C89]">d</em>
          </span>
        </div>
        <div className="relative max-w-sm space-y-3">
          <p className="font-heading text-[13px] font-bold uppercase tracking-[0.14em] text-[#EA4C89]">Operations</p>
          <h1 className="font-heading text-4xl font-bold leading-[1.05] tracking-tight">
            The trust layer behind the network.
          </h1>
          <p className="text-sm leading-relaxed text-white/60">
            Verification, moderation, and platform operations for every artist, studio, and
            professional on MusicLinkd.
          </p>
        </div>
      </div>

      <div className="flex flex-col items-center justify-center bg-background px-6 py-16">
        <div className="w-full max-w-sm space-y-8">
          <div className="flex flex-col items-center gap-4 text-center md:items-start md:text-left">
            <BrandMark className="size-10 md:hidden" />
            <div>
              <h2 className="font-heading text-2xl font-bold tracking-tight">Welcome back</h2>
              <p className="mt-1 text-sm text-muted-foreground">
                Sign in with your MusicLinkd Google account to continue.
              </p>
            </div>
          </div>
          <Button
            className="w-full gap-2.5 border-border bg-white text-foreground shadow-sm hover:bg-muted"
            variant="outline"
            size="lg"
            onClick={handleSignIn}
            disabled={pending}
          >
            <GoogleIcon />
            {pending ? "Signing in…" : "Continue with Google"}
          </Button>
          <p className="text-center text-xs text-muted-foreground md:text-left">
            Access is granted by an existing admin — reach out if you need an account.
          </p>
        </div>
      </div>
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
