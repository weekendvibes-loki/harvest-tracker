import { cn } from '@/lib/utils';

interface TableToolbarProps {
  children?: React.ReactNode;
  actions?: React.ReactNode;
  className?: string;
}

export function TableToolbar({ children, actions, className }: TableToolbarProps) {
  return (
    <div
      className={cn(
        'flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between',
        className,
      )}
    >
      <div className="flex flex-1 flex-wrap items-center gap-2">{children}</div>
      {actions ? <div className="flex shrink-0 items-center gap-2">{actions}</div> : null}
    </div>
  );
}
