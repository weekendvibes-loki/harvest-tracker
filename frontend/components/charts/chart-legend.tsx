import { cn } from '@/lib/utils';

export interface ChartLegendItem {
  label: string;
  color?: string;
  value?: React.ReactNode;
  className?: string;
}

interface ChartLegendProps {
  items: ChartLegendItem[];
  className?: string;
}

export function ChartLegend({ items, className }: ChartLegendProps) {
  return (
    <ul className={cn('flex flex-wrap items-center gap-x-4 gap-y-1.5', className)}>
      {items.map((item) => (
        <li key={item.label} className={cn('flex items-center gap-1.5 text-xs', item.className)}>
          <span
            className="h-2.5 w-2.5 shrink-0 rounded-sm"
            style={{ backgroundColor: item.color ?? 'currentColor' }}
            aria-hidden="true"
          />
          <span className="text-muted-foreground">{item.label}</span>
          {item.value ? (
            <span className="font-medium text-foreground">{item.value}</span>
          ) : null}
        </li>
      ))}
    </ul>
  );
}
