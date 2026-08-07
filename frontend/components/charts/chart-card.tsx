import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';

interface ChartCardProps {
  title?: string;
  description?: string;
  action?: React.ReactNode;
  children: React.ReactNode;
  footer?: React.ReactNode;
  isLoading?: boolean;
  className?: string;
  bodyClassName?: string;
}

export function ChartCard({
  title,
  description,
  action,
  children,
  footer,
  isLoading,
  className,
  bodyClassName,
}: ChartCardProps) {
  return (
    <div className={cn('rounded-lg border bg-card text-card-foreground shadow-sm', className)}>
      {title || description || action ? (
        <div className="flex items-start justify-between gap-4 border-b px-5 py-4">
          <div className="min-w-0">
            {title ? <h3 className="text-sm font-semibold">{title}</h3> : null}
            {description ? (
              <p className="mt-0.5 text-sm text-muted-foreground">{description}</p>
            ) : null}
          </div>
          {action ? <div className="shrink-0">{action}</div> : null}
        </div>
      ) : null}

      <div className={cn('p-5', bodyClassName)}>
        {isLoading ? (
          <div className="flex h-72 w-full flex-col justify-end gap-2">
            <Skeleton className="h-8 w-1/4" />
            <Skeleton className="h-3 w-1/3" />
            <Skeleton className="h-40 w-full rounded-md" />
          </div>
        ) : (
          children
        )}
      </div>

      {footer ? (
        <div className="border-t px-5 py-3 text-xs text-muted-foreground">{footer}</div>
      ) : null}
    </div>
  );
}
