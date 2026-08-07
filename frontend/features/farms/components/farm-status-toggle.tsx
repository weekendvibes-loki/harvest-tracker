'use client';

import { cn } from '@/lib/utils';

interface FarmStatusToggleProps {
  checked: boolean;
  onChange: (checked: boolean) => void;
  label: string;
  disabled?: boolean;
  compact?: boolean;
  className?: string;
}

export function FarmStatusToggle({
  checked,
  onChange,
  label,
  disabled,
  compact,
  className,
}: FarmStatusToggleProps) {
  return (
    <button
      type="button"
      role="switch"
      aria-checked={checked}
      aria-label={label}
      disabled={disabled}
      onClick={() => onChange(!checked)}
      className={cn(
        'inline-flex shrink-0 items-center rounded-full border p-0.5 transition-colors duration-150 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50',
        compact ? 'h-5 w-9' : 'h-6 w-11',
        checked
          ? 'justify-end border-transparent bg-emerald-600'
          : 'justify-start border-input bg-muted',
        className,
      )}
    >
      <span
        aria-hidden="true"
        className={cn(
          'rounded-full bg-white shadow-sm transition-transform motion-reduce:transition-none',
          compact ? 'h-4 w-4' : 'h-5 w-5',
        )}
      />
      <span className="sr-only">
        {label}: {checked ? 'Active' : 'Inactive'}
      </span>
    </button>
  );
}
