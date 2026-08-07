import { BarChart3, type LucideIcon } from 'lucide-react';

import { cn } from '@/lib/utils';

interface ChartEmptyStateProps {
  title?: string;
  description?: string;
  icon?: LucideIcon;
  className?: string;
}

export function ChartEmptyState({
  title = 'No data to display',
  description = 'Add data to see your chart here.',
  icon: Icon = BarChart3,
  className,
}: ChartEmptyStateProps) {
  return (
    <div
      className={cn(
        'flex h-full min-h-[16rem] flex-col items-center justify-center gap-2 px-6 text-center',
        className,
      )}
    >
      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-muted">
        <Icon className="h-6 w-6 text-muted-foreground" aria-hidden="true" />
      </div>
      <p className="text-sm font-semibold">{title}</p>
      {description ? (
        <p className="max-w-sm text-sm text-muted-foreground">{description}</p>
      ) : null}
    </div>
  );
}
