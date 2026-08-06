"use client";

import { useEffect, useState } from "react";
import { ExternalLink, TrendingUp, Users, ShoppingBag, Calendar, FolderGit2, ShieldAlert, BarChart3 } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { PageShell } from "@/components/layout/page-shell";
import { useRequireModule } from "@/hooks/use-require-module";
import {
  feedPostsRepo,
  marketplaceItemsRepo,
  moderationFlagsRepo,
  projectsRepo,
  reviewsRepo,
  studioBookingsRepo,
  supportTicketsRepo,
  usersRepo,
} from "@/services/repositories";

interface MetricCard {
  label: string;
  category: string;
  icon: React.ElementType;
  fetch: () => Promise<string | number>;
}

const METRICS: MetricCard[] = [
  {
    label: "Total Registered Users",
    category: "User Base",
    icon: Users,
    fetch: async () => {
      const count = await usersRepo.count();
      return count.toLocaleString();
    },
  },
  {
    label: "Verified Artists & Pros",
    category: "User Base",
    icon: Users,
    fetch: async () => {
      const count = await usersRepo.count([["isVerified", "==", true]]);
      return count.toLocaleString();
    },
  },
  {
    label: "Marketplace Listings & STEMs",
    category: "Marketplace",
    icon: ShoppingBag,
    fetch: async () => {
      const count = await marketplaceItemsRepo.count();
      return count.toLocaleString();
    },
  },
  {
    label: "Total Studio Bookings",
    category: "Bookings",
    icon: Calendar,
    fetch: async () => {
      const count = await studioBookingsRepo.count();
      return count.toLocaleString();
    },
  },
  {
    label: "Active Open Collaborations",
    category: "Projects",
    icon: FolderGit2,
    fetch: async () => {
      const count = await projectsRepo.count([["status", "==", "OPEN"]]);
      return count.toLocaleString();
    },
  },
  {
    label: "Community Feed Posts",
    category: "Engagement",
    icon: TrendingUp,
    fetch: async () => {
      const count = await feedPostsRepo.count();
      return count.toLocaleString();
    },
  },
  {
    label: "Platform Reviews & Ratings",
    category: "Engagement",
    icon: ReviewsIcon,
    fetch: async () => {
      const count = await reviewsRepo.count();
      return count.toLocaleString();
    },
  },
  {
    label: "Pending Moderation & Tickets",
    category: "Governance",
    icon: ShieldAlert,
    fetch: async () => {
      const flags = await moderationFlagsRepo.count([["status", "==", "PENDING"]]);
      const tickets = await supportTicketsRepo.count([["status", "==", "OPEN"]]);
      return flags + tickets;
    },
  },
];

function ReviewsIcon(props: React.ComponentProps<"svg">) {
  return <BarChart3 {...props} />;
}

export default function AnalyticsPage() {
  const { hasAccess } = useRequireModule("analytics");
  const [metrics, setMetrics] = useState<Record<string, string | number | null>>({});

  useEffect(() => {
    if (!hasAccess) return;
    METRICS.forEach((m) => {
      m.fetch()
        .then((val) => setMetrics((prev) => ({ ...prev, [m.label]: val })))
        .catch(() => setMetrics((prev) => ({ ...prev, [m.label]: "—" })));
    });
  }, [hasAccess]);

  if (!hasAccess) return null;

  return (
    <PageShell
      title="Analytics & Platform Metrics"
      description="Real-time collection aggregations and zero-cost Firebase Analytics event overview."
    >
      {/* Firebase Console Deep Link Card */}
      <Card className="border-primary/20 bg-primary/5">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <div>
            <CardTitle className="text-lg font-semibold">Google / Firebase Analytics Console</CardTitle>
            <CardDescription className="text-sm">
              Live event stream, user acquisition, retention, and custom CUJ event metrics for app.musiclinkd.com (ID: G-VQWH600D3Z).
            </CardDescription>
          </div>
          <Button
            variant="default"
            size="sm"
            onClick={() =>
              window.open(
                "https://console.firebase.google.com/project/musiclinkd-7ae30/analytics",
                "_blank",
                "noopener,noreferrer"
              )
            }
            className="gap-2 shrink-0"
          >
            <span>Open Firebase Analytics</span>
            <ExternalLink className="size-4" />
          </Button>
        </CardHeader>
      </Card>

      {/* Real-time Firestore Aggregate Cards Grid */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-4">
        {METRICS.map((m) => {
          const Icon = m.icon;
          const val = metrics[m.label];

          return (
            <Card key={m.label}>
              <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
                <CardDescription className="text-xs uppercase font-semibold tracking-wider text-muted-foreground">
                  {m.category}
                </CardDescription>
                <Icon className="size-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold font-heading">
                  {val === undefined ? (
                    <span className="text-muted-foreground animate-pulse">…</span>
                  ) : (
                    val
                  )}
                </div>
                <p className="text-xs text-muted-foreground mt-1">{m.label}</p>
              </CardContent>
            </Card>
          );
        })}
      </div>
    </PageShell>
  );
}

