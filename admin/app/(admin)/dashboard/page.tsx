"use client";

import { useEffect, useState } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
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
}

const STATS: StatDef[] = [
  { label: "Total users", fetch: () => usersRepo.count() },
  { label: "Studios listed", fetch: () => studiosRepo.count() },
  { label: "Marketplace items", fetch: () => marketplaceItemsRepo.count() },
  { label: "Studio bookings", fetch: () => studioBookingsRepo.count() },
  {
    label: "Pending verifications",
    fetch: () => verificationRequestsRepo.count([["status", "==", "PENDING"]]),
  },
  {
    label: "Open moderation flags",
    fetch: () => moderationFlagsRepo.count([["status", "==", "PENDING"]]),
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
        {STATS.map((stat) => (
          <Card key={stat.label}>
            <CardHeader className="pb-2">
              <CardDescription>{stat.label}</CardDescription>
              <CardTitle className="font-heading text-3xl">
                {values[stat.label] === undefined ? (
                  <span className="text-muted-foreground">…</span>
                ) : values[stat.label] === null ? (
                  <span className="text-sm text-destructive">error</span>
                ) : (
                  values[stat.label]
                )}
              </CardTitle>
            </CardHeader>
            <CardContent />
          </Card>
        ))}
      </div>
    </PageShell>
  );
}
