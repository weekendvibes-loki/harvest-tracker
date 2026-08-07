'use client';

import { X } from 'lucide-react';

import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';

interface FilterChipProps {
  label: string;
  value?: React.ReactNode;
  onRemove: () => void;
  disabled?: boolean;
  className?: string;
}

export function FilterChip({
  label,
  value,
  onRemove,
  disabled,
  className,
}: FilterChipProps) {
  return (
    <Badge
      variant="secondary"
      className={cn('gap-1.5 py-1 pr-1 pl-2.5 text-xs font-medium', className)}
    >
      <span className="text-muted-foreground">{label}</span>
      {value ? <span className="font-semibold">{value}</span> : null}
      <button
        type="button"
        onClick={onRemove}
        disabled={disabled}
        aria-label={`Remove ${label} filter`}
        className="rounded-full p-0.5 text-muted-foreground transition-colors hover:bg-background hover:text-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50"
      >
        <X className="h-3 w-3" aria-hidden="true" />
      </button>
    </Badge>
  );
}
