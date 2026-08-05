import { AlertTriangle } from "lucide-react";

export function ErrorState({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 rounded-lg border border-destructive/30 bg-destructive/5 py-16 text-center">
      <AlertTriangle className="size-8 text-destructive" />
      <p className="font-medium text-destructive">Something went wrong</p>
      <p className="max-w-sm text-sm text-muted-foreground">{message}</p>
      {onRetry ? (
        <button type="button" onClick={onRetry} className="text-sm font-medium text-primary underline">
          Try again
        </button>
      ) : null}
    </div>
  );
}
