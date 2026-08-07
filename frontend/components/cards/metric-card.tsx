import { ArrowDownRight, ArrowUpRight, Minus, type LucideIcon } from 'lucide-react';

import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';

export type MetricChangeDirection = 'up' | 'down' | 'neutral';

export interface MetricChange {
  value: number;
  direction?: MetricChangeDirection;
  label?: string;
}

interface MetricCardProps {
  title: string;
  value: string | number;
  unit?: string;
  icon?: LucideIcon;
  change?: MetricChange;
  footer?: React.ReactNode;
  isLoading?: boolean;
  className?: string;
}

const changeIcons: Record<MetricChangeDirection, LucideIcon> = {
  up: ArrowUpRight,
  down: ArrowDownRight,
  neutral: Minus,
};

const changeStyles: Record<MetricChangeDirection, string> = {
  up: 'border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-400',
  down: 'border-red-200 bg-red-50 text-red-700 dark:border-red-900 dark:bg-red-950/40 dark:text-red-400',
  neutral:
    'border-border bg-muted text-muted-foreground',
};

export function MetricCard({
  title,
  value,
  unit,
  icon: Icon,
  change,
  footer,
  isLoading,
  className,
}: MetricCardProps) {
  const direction = change?.direction ?? 'neutral';
  const ChangeIcon = changeIcons[direction];

  return (
    <div className={cn('rounded-lg border bg-card p-5 text-card-foreground shadow-sm', className)}>
      <div className="flex items-center justify-between gap-2">
        <p className="truncate text-sm font-medium text-muted-foreground">{title}</p>
        {Icon ? (
          <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-muted">
            <Icon className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
          </span>
        ) : null}
      </div>

      <div className="mt-2 flex items-baseline gap-1.5">
        {isLoading ? (
          <Skeleton className="h-9 w-24" />
        ) : (
          <span className="text-3xl font-bold tracking-tight">{value}</span>
        )}
        {unit && !isLoading ? (
          <span className="text-sm font-medium text-muted-foreground">{unit}</span>
        ) : null}
      </div>

      {isLoading ? (
        <Skeleton className="mt-3 h-5 w-28" />
      ) : change ? (
        <Badge
          variant="outline"
          className={cn('mt-3 gap-1 font-medium', changeStyles[direction])}
        >
          <ChangeIcon className="h-3.5 w-3.5" aria-hidden="true" />
          <span>{change.value}</span>
          {change.label ? <span className="font-normal">{change.label}</span> : null}
        </Badge>
      ) : null}

      {footer ? (
        <div className="mt-4 border-t pt-3 text-xs text-muted-foreground">{footer}</div>
      ) : null}
    </div>
  );
}
