import { cn } from "@/lib/utils";

/**
 * Single-line truncate + native title tooltip for long free-text table
 * cells (descriptions, reasons, review bodies). Since TableCell now wraps
 * by default (see components/ui/table.tsx), columns holding genuinely long
 * text need this explicitly — otherwise a 500-character value makes its row
 * absurdly tall instead of clipping cleanly with a hover affordance.
 */
export function TruncatedCell({ text, className, maxWidth = 420 }: { text: string; className?: string; maxWidth?: number }) {
  return (
    <span
      className={cn("block truncate text-muted-foreground", className)}
      style={{ maxWidth }}
      title={text}
    >
      {text}
    </span>
  );
}
