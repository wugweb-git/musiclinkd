import Link from "next/link";
import { Music2 } from "lucide-react";

export default function NotFound() {
  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-4 bg-background px-6 text-center">
      <Music2 className="size-10 text-primary" />
      <div className="space-y-1">
        <h1 className="font-heading text-2xl font-bold tracking-tight">Page not found</h1>
        <p className="text-sm text-muted-foreground">This route doesn&apos;t exist in the operations panel.</p>
      </div>
      <Link href="/" className="text-sm font-medium text-primary underline">
        Back to start
      </Link>
    </div>
  );
}
