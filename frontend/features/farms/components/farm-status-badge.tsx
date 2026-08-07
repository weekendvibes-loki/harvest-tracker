import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';
import type { FarmStatus } from '../types/farm.types';

const statusStyles: Record<FarmStatus, string> = {
  ACTIVE:
    'border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-400',
  INACTIVE:
    'border-slate-200 bg-slate-100 text-slate-600 dark:border-slate-800 dark:bg-slate-800/60 dark:text-slate-300',
};

interface FarmStatusBadgeProps {
  status: FarmStatus;
  className?: string;
}

export function FarmStatusBadge({ status, className }: FarmStatusBadgeProps) {
  return (
    <Badge variant="outline" className={cn('font-medium', statusStyles[status], className)}>
      {status.charAt(0) + status.slice(1).toLowerCase()}
    </Badge>
  );
}
