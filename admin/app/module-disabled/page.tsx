"use client";

import { PowerOff } from "lucide-react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Suspense } from "react";
import { Button } from "@/components/ui/button";

function ModuleDisabledContent() {
  const searchParams = useSearchParams();
  const moduleKey = searchParams.get("module");

  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-4 bg-background px-6 text-center">
      <PowerOff className="size-10 text-muted-foreground" />
      <div className="space-y-1">
        <h1 className="font-heading text-2xl font-bold tracking-tight">Module disabled</h1>
        <p className="max-w-md text-sm text-muted-foreground">
          {moduleKey ? (
            <>
              The <span className="font-medium text-foreground">{moduleKey}</span> module has been
              disabled by an administrator.
            </>
          ) : (
            "This module has been disabled by an administrator."
          )}{" "}
          An admin can re-enable it from Modules.
        </p>
      </div>
      <Button variant="outline" render={<Link href="/modules" />}>
        Go to Modules
      </Button>
    </div>
  );
}

export default function ModuleDisabledPage() {
  return (
    <Suspense fallback={null}>
      <ModuleDisabledContent />
    </Suspense>
  );
}
