'use client';

import { useState } from 'react';
import { ChevronDown, SlidersHorizontal } from 'lucide-react';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface FilterPanelProps {
  title?: string;
  open?: boolean;
  defaultOpen?: boolean;
  onOpenChange?: (open: boolean) => void;
  badge?: number;
  children: React.ReactNode;
  footer?: React.ReactNode;
  className?: string;
}

export function FilterPanel({
  title = 'Filters',
  open,
  defaultOpen = false,
  onOpenChange,
  badge,
  children,
  footer,
  className,
}: FilterPanelProps) {
  const [internalOpen, setInternalOpen] = useState(defaultOpen);
  const isControlled = open !== undefined;
  const isOpen = isControlled ? open : internalOpen;

  const toggle = () => {
    if (isControlled) {
      onOpenChange?.(!isOpen);
    } else {
      setInternalOpen((current) => !current);
    }
  };

  return (
    <div className={cn('rounded-lg border bg-card text-card-foreground shadow-sm', className)}>
      <button
        type="button"
        onClick={toggle}
        aria-expanded={isOpen}
        className="flex w-full items-center justify-between gap-2 px-4 py-3 text-left transition-colors hover:bg-muted/50"
      >
        <span className="flex items-center gap-2 text-sm font-medium">
          <SlidersHorizontal className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
          {title}
          {badge ? (
            <Badge variant="secondary" className="rounded-full px-1.5 py-0 text-xs tabular-nums">
              {badge}
            </Badge>
          ) : null}
        </span>
        <ChevronDown
          className={cn(
            'h-4 w-4 text-muted-foreground transition-transform duration-200',
            isOpen && 'rotate-180',
          )}
          aria-hidden="true"
        />
      </button>

      {isOpen ? (
        <div className="border-t px-4 py-4">
          <div className="space-y-3">{children}</div>
          {footer ? (
            <div className="mt-4 flex items-center justify-end gap-2">{footer}</div>
          ) : null}
        </div>
      ) : null}
    </div>
  );
}
