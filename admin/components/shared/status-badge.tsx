import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

const TONE_BY_STATUS: Record<string, "default" | "secondary" | "destructive" | "outline"> = {
  APPROVED: "default",
  ACTIVE: "default",
  SENT: "default",
  RESOLVED: "default",
  ACCEPTED: "default",
  documented: "default",
  OPEN: "secondary",
  PENDING: "secondary",
  QUEUED: "secondary",
  IN_PROGRESS: "secondary",
  partial: "secondary",
  REJECTED: "destructive",
  DISMISSED: "destructive",
  FAILED: "destructive",
  DECLINED: "destructive",
  missing: "destructive",
  ACTIONED: "outline",
  "app-mocked": "outline",
};

// Dot color mirrors the badge variant — a quick-scan signal that reads even
// before the label text does, without changing the Badge component itself.
const DOT_BY_VARIANT: Record<"default" | "secondary" | "destructive" | "outline", string> = {
  default: "bg-primary-foreground/70",
  secondary: "bg-amber-500",
  destructive: "bg-destructive-foreground/80",
  outline: "bg-muted-foreground",
};

export function StatusBadge({ status }: { status: string }) {
  const variant = TONE_BY_STATUS[status] ?? "outline";
  return (
    <Badge variant={variant} className="gap-1.5 font-medium">
      <span className={cn("size-1.5 shrink-0 rounded-full", DOT_BY_VARIANT[variant])} />
      {status.replace(/[-_]/g, " ")}
    </Badge>
  );
}
