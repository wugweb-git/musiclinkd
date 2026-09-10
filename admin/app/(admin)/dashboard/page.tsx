"use client";

import { CalendarCheck, Flag, ShieldAlert, ShoppingBag, Users, Warehouse } from "lucide-react";
import { useEffect, useState } from "react";
import { Card, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { PageShell } from "@/components/layout/page-shell";
import { useRequireModule } from "@/hooks/use-require-module";
import {
  marketplaceItemsRepo,
  moderationFlagsRepo,
  studioBookingsRepo,
  studiosRepo,
  usersRepo,
  verificationRequestsRepo,
} from "@/services/repositories";

interface StatDef {
  label: string;
  fetch: () => Promise<number>;
  icon: React.ComponentType<{ className?: string }>;
  /** Highlights the number when non-zero — for stats that represent work waiting on someone. */
  attention?: boolean;
}

const STATS: StatDef[] = [
  { label: "Total users", fetch: () => usersRepo.count(), icon: Users },
  { label: "Studios listed", fetch: () => studiosRepo.count(), icon: Warehouse },
  { label: "Marketplace items", fetch: () => marketplaceItemsRepo.count(), icon: ShoppingBag },
  { label: "Studio bookings", fetch: () => studioBookingsRepo.count(), icon: CalendarCheck },
  {
    label: "Pending verifications",
    fetch: () => verificationRequestsRepo.count([["status", "==", "PENDING"]]),
    icon: ShieldAlert,
    attention: true,
  },
  {
    label: "Open moderation flags",
    fetch: () => moderationFlagsRepo.count([["status", "==", "PENDING"]]),
    icon: Flag,
    attention: true,
  },
];

export default function DashboardPage() {
  const { hasAccess } = useRequireModule("dashboard");
  const [values, setValues] = useState<Record<string, number | null>>({});

  useEffect(() => {
    if (!hasAccess) return;
    STATS.forEach((stat) => {
      stat
        .fetch()
        .then((n) => setValues((prev) => ({ ...prev, [stat.label]: n })))
        .catch(() => setValues((prev) => ({ ...prev, [stat.label]: null })));
    });
  }, [hasAccess]);

  if (!hasAccess) return null;

  return (
    <PageShell title="Dashboard" description="Platform activity at a glance.">
      <div className="grid grid-cols-2 gap-4 md:grid-cols-3">
        {STATS.map((stat) => {
          const value = values[stat.label];
          const hasAttention = stat.attention && typeof value === "number" && value > 0;
          return (
            <Card
              key={stat.label}
              className="transition-shadow hover:shadow-[var(--elevation-raised-top)]"
            >
              <CardHeader className="pb-2">
                <div className="flex items-start justify-between gap-3">
                  <div className="flex flex-col gap-1">
                    <CardDescription>{stat.label}</CardDescription>
                    <CardTitle className="font-heading text-3xl tabular-nums">
                      {value === undefined ? (
                        <Skeleton className="mt-1 h-8 w-16" />
                      ) : value === null ? (
                        <span className="text-sm font-normal text-destructive">Failed to load</span>
                      ) : (
                        <span className={hasAttention ? "text-accent" : undefined}>{value}</span>
                      )}
                    </CardTitle>
                  </div>
                  <span
                    className={
                      hasAttention
                        ? "flex size-9 shrink-0 items-center justify-center rounded-full bg-accent/10 text-accent"
                        : "flex size-9 shrink-0 items-center justify-center rounded-full bg-muted text-muted-foreground"
                    }
                  >
                    <stat.icon className="size-4" />
                  </span>
                </div>
              </CardHeader>
            </Card>
          );
        })}
      </div>
    </PageShell>
  );
}
