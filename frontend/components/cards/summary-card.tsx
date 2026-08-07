import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';

export interface SummaryItem {
  label: string;
  value: React.ReactNode;
  className?: string;
}

interface SummaryCardProps {
  title?: string;
  description?: string;
  items: SummaryItem[];
  columns?: 1 | 2;
  isLoading?: boolean;
  className?: string;
}

export function SummaryCard({
  title,
  description,
  items,
  columns = 1,
  isLoading,
  className,
}: SummaryCardProps) {
  return (
    <div className={cn('rounded-lg border bg-card p-5 text-card-foreground shadow-sm', className)}>
      {title || description ? (
        <div className="mb-4">
          {title ? <h3 className="text-sm font-semibold">{title}</h3> : null}
          {description ? (
            <p className="mt-0.5 text-sm text-muted-foreground">{description}</p>
          ) : null}
        </div>
      ) : null}

      <dl
        className={cn(
          'grid gap-x-6 gap-y-4',
          columns === 2 ? 'grid-cols-1 sm:grid-cols-2' : 'grid-cols-1',
        )}
      >
        {isLoading
          ? Array.from({ length: Math.max(items.length, 2) }).map((_, index) => (
              <div key={index}>
                <Skeleton className="h-3 w-24" />
                <Skeleton className="mt-2 h-5 w-32" />
              </div>
            ))
          : items.map((item) => (
              <div key={item.label} className={cn('min-w-0', item.className)}>
                <dt className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                  {item.label}
                </dt>
                <dd className="mt-1 truncate text-sm font-medium">{item.value}</dd>
              </div>
            ))}
      </dl>
    </div>
  );
}
