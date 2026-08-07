'use client';

import { CalendarRange } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils';

export interface DateRange {
  from?: string;
  to?: string;
}

interface DateRangePickerProps {
  value?: DateRange;
  onChange: (range: DateRange) => void;
  placeholder?: string;
  className?: string;
}

const MONTHS = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec',
];

function formatDate(iso?: string): string {
  if (!iso) return '';
  const [year, month, day] = iso.split('-').map(Number);
  if (!year || !month || !day) return iso;
  return `${MONTHS[month - 1]} ${day}, ${year}`;
}

function dateInputClass(className?: string): string {
  return cn(
    'h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm text-foreground shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50',
    className,
  );
}

export function DateRangePicker({
  value,
  onChange,
  placeholder = 'Select date range',
  className,
}: DateRangePickerProps) {
  const { from, to } = value ?? {};

  const display = from
    ? to
      ? `${formatDate(from)} – ${formatDate(to)}`
      : `From ${formatDate(from)}`
    : to
      ? `Until ${formatDate(to)}`
      : placeholder;

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="outline"
          size="sm"
          className={cn('justify-start gap-1.5 font-normal', from || to ? 'pr-8' : 'pr-3', className)}
          aria-label="Select date range"
        >
          <CalendarRange className="h-4 w-4 shrink-0 text-muted-foreground" aria-hidden="true" />
          <span className="truncate">{display}</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="start" className="w-64">
        <DropdownMenuLabel>Date range</DropdownMenuLabel>
        <DropdownMenuSeparator />
        <div className="space-y-3 p-1">
          <div className="space-y-1">
            <label htmlFor="date-from" className="text-xs font-medium text-muted-foreground">
              From
            </label>
            <input
              id="date-from"
              type="date"
              value={from ?? ''}
              max={to}
              onChange={(event) => onChange({ from: event.target.value, to })}
              className={dateInputClass()}
            />
          </div>
          <div className="space-y-1">
            <label htmlFor="date-to" className="text-xs font-medium text-muted-foreground">
              To
            </label>
            <input
              id="date-to"
              type="date"
              value={to ?? ''}
              min={from}
              onChange={(event) => onChange({ from, to: event.target.value })}
              className={dateInputClass()}
            />
          </div>
          {from || to ? (
            <Button
              type="button"
              variant="ghost"
              size="sm"
              className="w-full"
              onClick={() => onChange({})}
            >
              Clear
            </Button>
          ) : null}
        </div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
