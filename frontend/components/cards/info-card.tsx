import type { LucideIcon } from 'lucide-react';

import { cn } from '@/lib/utils';

export type InfoCardVariant = 'default' | 'info' | 'success' | 'warning' | 'danger';

interface InfoCardProps {
  title: string;
  description?: React.ReactNode;
  icon?: LucideIcon;
  variant?: InfoCardVariant;
  action?: React.ReactNode;
  children?: React.ReactNode;
  className?: string;
}

const cardStyles: Record<InfoCardVariant, string> = {
  default: 'border-border bg-card',
  info: 'border-sky-200 bg-sky-50/60 dark:border-sky-900/60 dark:bg-sky-950/30',
  success: 'border-emerald-200 bg-emerald-50/60 dark:border-emerald-900/60 dark:bg-emerald-950/30',
  warning: 'border-amber-200 bg-amber-50/60 dark:border-amber-900/60 dark:bg-amber-950/30',
  danger: 'border-red-200 bg-red-50/60 dark:border-red-900/60 dark:bg-red-950/30',
};

const iconStyles: Record<InfoCardVariant, string> = {
  default: 'bg-muted text-muted-foreground',
  info: 'bg-sky-100 text-sky-700 dark:bg-sky-900/60 dark:text-sky-300',
  success: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/60 dark:text-emerald-300',
  warning: 'bg-amber-100 text-amber-700 dark:bg-amber-900/60 dark:text-amber-300',
  danger: 'bg-red-100 text-red-700 dark:bg-red-900/60 dark:text-red-300',
};

export function InfoCard({
  title,
  description,
  icon: Icon,
  variant = 'default',
  action,
  children,
  className,
}: InfoCardProps) {
  return (
    <div
      className={cn(
        'flex items-start gap-3 rounded-lg border p-4 text-card-foreground',
        cardStyles[variant],
        className,
      )}
    >
      {Icon ? (
        <span
          className={cn(
            'flex h-9 w-9 shrink-0 items-center justify-center rounded-md',
            iconStyles[variant],
          )}
        >
          <Icon className="h-4 w-4" aria-hidden="true" />
        </span>
      ) : null}

      <div className="min-w-0 flex-1">
        <div className="flex items-start justify-between gap-2">
          <h4 className="text-sm font-semibold">{title}</h4>
          {action ? <div className="shrink-0">{action}</div> : null}
        </div>
        {description ? (
          <div className="mt-1 text-sm text-muted-foreground">{description}</div>
        ) : null}
        {children ? <div className="mt-2 text-sm">{children}</div> : null}
      </div>
    </div>
  );
}
