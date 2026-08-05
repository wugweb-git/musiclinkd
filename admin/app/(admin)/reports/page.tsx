"use client";

import { Download } from "lucide-react";
import { toast } from "sonner";
import { PageShell } from "@/components/layout/page-shell";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { useRequireModule } from "@/hooks/use-require-module";
import { downloadCsv } from "@/lib/csv";
import {
  marketplaceItemsRepo,
  moderationFlagsRepo,
  reviewsRepo,
  studioBookingsRepo,
  studiosRepo,
  usersRepo,
  verificationRequestsRepo,
} from "@/services/repositories";

const EXPORTS = [
  { label: "Users", repo: usersRepo, filename: "musiclinkd-users.csv" },
  { label: "Studios", repo: studiosRepo, filename: "musiclinkd-studios.csv" },
  { label: "Marketplace items", repo: marketplaceItemsRepo, filename: "musiclinkd-marketplace.csv" },
  { label: "Studio bookings", repo: studioBookingsRepo, filename: "musiclinkd-bookings.csv" },
  { label: "Reviews", repo: reviewsRepo, filename: "musiclinkd-reviews.csv" },
  { label: "Verification requests", repo: verificationRequestsRepo, filename: "musiclinkd-verification.csv" },
  { label: "Moderation flags", repo: moderationFlagsRepo, filename: "musiclinkd-moderation.csv" },
];

export default function ReportsPage() {
  const { hasAccess } = useRequireModule("reports");
  if (!hasAccess) return null;

  return (
    <PageShell
      title="Reports & exports"
      description="Export current collection snapshots as CSV. Merged with Reports since both read the same underlying data — kept as one module instead of two near-duplicates."
    >
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {EXPORTS.map((exp) => (
          <Card key={exp.label}>
            <CardHeader>
              <CardTitle className="text-base">{exp.label}</CardTitle>
              <CardDescription>Download the current snapshot (up to 200 most recent records).</CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                variant="outline"
                size="sm"
                onClick={async () => {
                  const rows = await exp.repo.list();
                  if (rows.length === 0) {
                    toast.info("Nothing to export yet");
                    return;
                  }
                  downloadCsv(exp.filename, rows);
                }}
              >
                <Download className="size-3.5" /> Export CSV
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>
    </PageShell>
  );
}
