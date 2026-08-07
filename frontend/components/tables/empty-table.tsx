import { PackageSearch, type LucideIcon } from 'lucide-react';

import { cn } from '@/lib/utils';

interface EmptyTableProps {
  title?: string;
  description?: string;
  icon?: LucideIcon;
  className?: string;
}

export function EmptyTable({
  title = 'No results found',
  description = 'Adjust your search or filters to find what you are looking for.',
  icon: Icon = PackageSearch,
  className,
}: EmptyTableProps) {
  return (
    <div
      className={cn(
        'flex flex-col items-center justify-center gap-2 px-6 py-12 text-center',
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
