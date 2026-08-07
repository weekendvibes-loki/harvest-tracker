import { Skeleton } from '@/components/ui/skeleton';

interface LoadingTableProps {
  colSpan?: number;
  rows?: number;
  className?: string;
}

export function LoadingTable({ colSpan = 1, rows = 5, className }: LoadingTableProps) {
  return (
    <>
      {Array.from({ length: rows }).map((_, index) => (
        <tr key={index} className={className}>
          <td colSpan={colSpan} className="p-3">
            <Skeleton className="h-6 w-full" />
          </td>
        </tr>
      ))}
    </>
  );
}
