"use client";

import { ShieldAlert } from "lucide-react";
import { Button } from "@/components/ui/button";
import { appConfig } from "@/config/app.config";
import { useAuth } from "@/hooks/use-auth";
import { signOut } from "@/services/auth";

export default function UnauthorizedPage() {
  const { user, role } = useAuth();

  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-4 bg-background px-6 text-center">
      <ShieldAlert className="size-10 text-destructive" />
      <div className="space-y-1">
        <h1 className="font-heading text-2xl font-bold tracking-tight">Not authorized</h1>
        <p className="max-w-md text-sm text-muted-foreground">
          {role
            ? "Your role doesn't have access to that module."
            : `${user?.email ?? "This account"} isn't set up as a MusicLinkd operator. Ask an existing admin to grant access, or contact ${appConfig.supportEmail}.`}
        </p>
      </div>
      <Button variant="outline" onClick={() => signOut()}>
        Sign out
      </Button>
    </div>
  );
}
