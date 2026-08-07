import { Progress, type ProgressVariant } from '@/components/ui/progress';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';

interface ProgressCardProps {
  title?: string;
  description?: string;
  value: number;
  max: number;
  variant?: ProgressVariant;
  showValue?: boolean;
  formatValue?: (value: number, max: number) => string;
  footer?: React.ReactNode;
  isLoading?: boolean;
  className?: string;
}

export function ProgressCard({
  title,
  description,
  value,
  max,
  variant = 'default',
  showValue = true,
  formatValue,
  footer,
  isLoading,
  className,
}: ProgressCardProps) {
  const formatted =
    formatValue ?? ((current: number, maximum: number) => `${current} / ${maximum}`);

  return (
    <div className={cn('rounded-lg border bg-card p-5 text-card-foreground shadow-sm', className)}>
      {title || showValue ? (
        <div className="mb-3 flex items-start justify-between gap-2">
          {title ? <p className="text-sm font-medium">{title}</p> : <span />}
          {isLoading ? (
            <Skeleton className="h-4 w-16" />
          ) : showValue ? (
            <span className="text-sm font-semibold tabular-nums text-muted-foreground">
              {formatted(value, max)}
            </span>
          ) : null}
        </div>
      ) : null}

      {isLoading ? (
        <Skeleton className="h-2 w-full rounded-full" />
      ) : (
        <Progress value={value} max={max} variant={variant} aria-label={title ?? 'Progress'} />
      )}

      {description ? (
        <p className="mt-3 text-xs text-muted-foreground">{description}</p>
      ) : null}

      {footer ? <div className="mt-4 border-t pt-3 text-xs text-muted-foreground">{footer}</div> : null}
    </div>
  );
}
