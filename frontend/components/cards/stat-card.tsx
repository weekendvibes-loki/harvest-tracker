import { ArrowDownRight, ArrowUpRight, Minus, type LucideIcon } from 'lucide-react';

import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';

export type TrendDirection = 'up' | 'down' | 'neutral';

export interface StatCardDelta {
  value: string;
  direction?: TrendDirection;
  label?: string;
}

interface StatCardProps {
  label: string;
  value: string | number;
  icon?: LucideIcon;
  delta?: StatCardDelta;
  isLoading?: boolean;
  className?: string;
}

const trendIcons: Record<TrendDirection, LucideIcon> = {
  up: ArrowUpRight,
  down: ArrowDownRight,
  neutral: Minus,
};

const trendColors: Record<TrendDirection, string> = {
  up: 'text-emerald-600 dark:text-emerald-400',
  down: 'text-red-600 dark:text-red-400',
  neutral: 'text-muted-foreground',
};

export function StatCard({ label, value, icon: Icon, delta, isLoading, className }: StatCardProps) {
  const direction = delta?.direction ?? 'neutral';
  const TrendIcon = trendIcons[direction];

  return (
    <div className={cn('rounded-lg border bg-card p-5 text-card-foreground shadow-sm', className)}>
      <div className="flex items-center justify-between gap-2">
        <p className="truncate text-sm text-muted-foreground">{label}</p>
        {Icon ? (
          <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-muted">
            <Icon className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
          </span>
        ) : null}
      </div>

      {isLoading ? (
        <Skeleton className="mt-3 h-8 w-24" />
      ) : (
        <p className="mt-3 text-2xl font-semibold tracking-tight">{value}</p>
      )}

      {isLoading ? (
        <Skeleton className="mt-2 h-4 w-28" />
      ) : delta ? (
        <p
          className={cn('mt-2 flex items-center gap-1 text-xs font-medium', trendColors[direction])}
        >
          <TrendIcon className="h-3.5 w-3.5" aria-hidden="true" />
          <span>{delta.value}</span>
          {delta.label ? <span className="text-muted-foreground">{delta.label}</span> : null}
        </p>
      ) : (
        <span className="mt-2 block h-4" aria-hidden="true" />
      )}
    </div>
  );
}
