'use client';

import { ChevronDown, Columns3 } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils';

export interface ColumnVisibilityOption {
  id: string;
  label: string;
  isVisible: boolean;
}

interface ColumnVisibilityProps {
  columns: ColumnVisibilityOption[];
  onToggle: (id: string) => void;
  triggerLabel?: string;
  className?: string;
}

export function ColumnVisibility({
  columns,
  onToggle,
  triggerLabel = 'Columns',
  className,
}: ColumnVisibilityProps) {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="sm" className={cn('gap-1.5', className)}>
          <Columns3 className="h-4 w-4" aria-hidden="true" />
          <span>{triggerLabel}</span>
          <ChevronDown className="h-3.5 w-3.5 text-muted-foreground" aria-hidden="true" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-48">
        <DropdownMenuLabel>Toggle columns</DropdownMenuLabel>
        <DropdownMenuSeparator />
        {columns.map((column) => (
          <DropdownMenuCheckboxItem
            key={column.id}
            checked={column.isVisible}
            onCheckedChange={() => onToggle(column.id)}
          >
            {column.label}
          </DropdownMenuCheckboxItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
