import { CheckCircle2, type LucideIcon } from 'lucide-react';

import { cn } from '@/lib/utils';

interface SuccessStateProps {
  title: string;
  description?: string;
  icon?: LucideIcon;
  action?: React.ReactNode;
  className?: string;
}

export function SuccessState({
  title,
  description,
  icon: Icon = CheckCircle2,
  action,
  className,
}: SuccessStateProps) {
  return (
    <div
      className={cn(
        'flex flex-col items-center justify-center gap-2 rounded-lg border border-emerald-200 bg-emerald-50/50 px-6 py-12 text-center dark:border-emerald-900/60 dark:bg-emerald-950/30',
        className,
      )}
    >
      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-900/60 dark:text-emerald-300">
        <Icon className="h-6 w-6" aria-hidden="true" />
      </div>
      <h3 className="text-sm font-semibold">{title}</h3>
      {description ? (
        <p className="max-w-sm text-sm text-muted-foreground">{description}</p>
      ) : null}
      {action ? <div className="mt-2">{action}</div> : null}
    </div>
  );
}
