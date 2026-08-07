'use client';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

export interface QuickFilterOption {
  id: string;
  label: string;
}

interface QuickFiltersProps {
  options: QuickFilterOption[];
  selected: string[];
  onChange: (selected: string[]) => void;
  multiple?: boolean;
  className?: string;
}

export function QuickFilters({
  options,
  selected,
  onChange,
  multiple = true,
  className,
}: QuickFiltersProps) {
  return (
    <div
      role="group"
      aria-label="Quick filters"
      className={cn('flex flex-wrap items-center gap-2', className)}
    >
      {options.map((option) => {
        const active = selected.includes(option.id);
        return (
          <Button
            key={option.id}
            type="button"
            variant={active ? 'default' : 'outline'}
            size="sm"
            aria-pressed={active}
            onClick={() => {
              if (multiple) {
                onChange(
                  active
                    ? selected.filter((id) => id !== option.id)
                    : [...selected, option.id],
                );
              } else {
                onChange(active ? [] : [option.id]);
              }
            }}
          >
            {option.label}
          </Button>
        );
      })}
    </div>
  );
}
