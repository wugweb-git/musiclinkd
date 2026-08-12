import { Award, Building2, ShieldCheck, TrendingUp } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

/**
 * Visual tier for admin-granted trust badges (taxonomy_categories/trust_badges,
 * UserDoc.trustBadges — see Master_Data_Seed_Values.md). Distinct from
 * StatusBadge (workflow status like PENDING/APPROVED) — these represent an
 * earned/granted reputation tier, so they get their own icon + color per tier
 * rather than a generic outline chip.
 */
const TIER_STYLE: Record<string, { icon: typeof ShieldCheck; className: string }> = {
  "Verified Artist": { icon: ShieldCheck, className: "border-primary/30 bg-primary/10 text-primary" },
  "Top Host": { icon: Award, className: "border-amber-500/30 bg-amber-500/10 text-amber-600 dark:text-amber-400" },
  "Rising Talent": { icon: TrendingUp, className: "border-emerald-500/30 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400" },
  "Studio Partner": { icon: Building2, className: "border-sky-500/30 bg-sky-500/10 text-sky-600 dark:text-sky-400" },
};

export function TrustBadge({ label }: { label: string }) {
  const style = TIER_STYLE[label];
  const Icon = style?.icon ?? ShieldCheck;
  return (
    <Badge variant="outline" className={cn("gap-1 font-medium", style?.className)}>
      <Icon className="size-3" />
      {label}
    </Badge>
  );
}

/** Renders every badge in the array, or "—" when empty — the common list/cell case. */
export function TrustBadgeList({ badges }: { badges?: string[] }) {
  if (!badges || badges.length === 0) return <span className="text-muted-foreground">—</span>;
  return (
    <div className="flex flex-wrap gap-1">
      {badges.map((b) => (
        <TrustBadge key={b} label={b} />
      ))}
    </div>
  );
}
