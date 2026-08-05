import { AlertOctagon } from "lucide-react";

export function SetupRequiredScreen() {
  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-4 bg-background px-6 text-center">
      <AlertOctagon className="size-10 text-primary" />
      <div className="space-y-1">
        <h1 className="font-heading text-2xl font-bold tracking-tight">Firebase isn&apos;t configured</h1>
        <p className="max-w-md text-sm text-muted-foreground">
          Copy <code className="rounded bg-muted px-1 py-0.5">.env.example</code> to{" "}
          <code className="rounded bg-muted px-1 py-0.5">.env.local</code> and fill in your Firebase Web
          SDK config (Firebase console → Project settings → your web app), then restart the dev server or
          rebuild.
        </p>
      </div>
    </div>
  );
}
