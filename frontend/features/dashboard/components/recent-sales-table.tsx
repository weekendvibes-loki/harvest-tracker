'use client';

import type { LegacyColumnDef } from '@tanstack/react-table/legacy';
import { useMemo } from 'react';

import { DataTable } from '@/components/tables/data-table';
import { Badge } from '@/components/ui/badge';
import { ContentCard } from '@/components/shared/content-card';
import { cn } from '@/lib/utils';
import type { RecentSale, SaleStatus } from '../types/dashboard.types';
import { formatCurrency } from '../utils/format';

const saleStatusStyles: Record<SaleStatus, string> = {
  Paid: 'border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-400',
  'Partially Paid':
    'border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-300',
  Pending:
    'border-sky-200 bg-sky-50 text-sky-700 dark:border-sky-900 dark:bg-sky-950/40 dark:text-sky-300',
  Overdue:
    'border-red-200 bg-red-50 text-red-700 dark:border-red-900 dark:bg-red-950/40 dark:text-red-400',
};

function SaleStatusBadge({ status }: { status: SaleStatus }) {
  return (
    <Badge variant="outline" className={cn('font-medium', saleStatusStyles[status])}>
      {status}
    </Badge>
  );
}

interface RecentSalesTableProps {
  sales: RecentSale[];
}

export function RecentSalesTable({ sales }: RecentSalesTableProps) {
  const columns = useMemo<LegacyColumnDef<RecentSale>[]>(
    () => [
      {
        accessorKey: 'order',
        header: 'Order',
        cell: ({ row }) => <span className="font-mono text-sm font-medium">{row.original.order}</span>,
      },
      { accessorKey: 'customer', header: 'Customer' },
      {
        accessorKey: 'amount',
        header: 'Amount',
        cell: ({ row }) => (
          <span className="font-mono text-sm font-semibold tabular-nums">
            {formatCurrency(row.original.amount)}
          </span>
        ),
      },
      { accessorKey: 'date', header: 'Date' },
      {
        accessorKey: 'status',
        header: 'Status',
        cell: ({ row }) => <SaleStatusBadge status={row.original.status} />,
      },
    ],
    [],
  );

  return (
    <ContentCard title="Recent Sales" description="Latest sales orders and payment status">
      <DataTable
        columns={columns}
        data={sales}
        searchable
        searchPlaceholder="Search orders..."
        showPagination={false}
        emptyTitle="No sales orders found"
        emptyDescription="Sales orders recorded this week will appear here."
      />
    </ContentCard>
  );
}
