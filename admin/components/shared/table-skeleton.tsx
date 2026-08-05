import { Skeleton } from "@/components/ui/skeleton";

export function TableSkeleton({ columns, rows = 6 }: { columns: number; rows?: number }) {
  return (
    <div className="space-y-2 rounded-lg border p-4">
      {Array.from({ length: rows }).map((_, rowIndex) => (
        // biome-ignore lint: static skeleton rows, no stable id available
        <div key={rowIndex} className="flex gap-4">
          {Array.from({ length: columns }).map((_, colIndex) => (
            // biome-ignore lint: static skeleton cells, no stable id available
            <Skeleton key={colIndex} className="h-6 flex-1" />
          ))}
        </div>
      ))}
    </div>
  );
}
