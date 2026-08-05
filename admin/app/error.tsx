"use client";

import { AlertTriangle } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function GlobalError({ error, reset }: { error: Error & { digest?: string }; reset: () => void }) {
  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-4 bg-background px-6 text-center">
      <AlertTriangle className="size-10 text-destructive" />
      <div className="space-y-1">
        <h1 className="font-heading text-2xl font-bold tracking-tight">Something went wrong</h1>
        <p className="max-w-md text-sm text-muted-foreground">{error.message || "An unexpected error occurred."}</p>
      </div>
      <Button onClick={reset}>Try again</Button>
    </div>
  );
}
