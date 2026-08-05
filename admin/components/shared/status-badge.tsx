import { Badge } from "@/components/ui/badge";

const TONE_BY_STATUS: Record<string, "default" | "secondary" | "destructive" | "outline"> = {
  APPROVED: "default",
  ACTIVE: "default",
  SENT: "default",
  RESOLVED: "default",
  ACCEPTED: "default",
  OPEN: "secondary",
  PENDING: "secondary",
  QUEUED: "secondary",
  IN_PROGRESS: "secondary",
  REJECTED: "destructive",
  DISMISSED: "destructive",
  FAILED: "destructive",
  DECLINED: "destructive",
  ACTIONED: "outline",
};

export function StatusBadge({ status }: { status: string }) {
  const tone = TONE_BY_STATUS[status] ?? "outline";
  return (
    <Badge variant={tone} className="font-medium">
      {status.replace(/_/g, " ")}
    </Badge>
  );
}
