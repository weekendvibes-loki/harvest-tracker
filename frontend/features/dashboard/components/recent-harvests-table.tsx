'use client';

import type { LegacyColumnDef } from '@tanstack/react-table/legacy';
import { useMemo } from 'react';

import { DataTable } from '@/components/tables/data-table';
import { Badge } from '@/components/ui/badge';
import { ContentCard } from '@/components/shared/content-card';
import { cn } from '@/lib/utils';
import type { HarvestStatus, RecentHarvest } from '../types/dashboard.types';
import { formatWeight } from '../utils/format';

const harvestStatusStyles: Record<HarvestStatus, string> = {
  Confirmed:
    'border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-400',
  'Quality Check':
    'border-sky-200 bg-sky-50 text-sky-700 dark:border-sky-900 dark:bg-sky-950/40 dark:text-sky-300',
  Pending:
    'border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-300',
  Rejected:
    'border-red-200 bg-red-50 text-red-700 dark:border-red-900 dark:bg-red-950/40 dark:text-red-400',
};

function HarvestStatusBadge({ status }: { status: HarvestStatus }) {
  return (
    <Badge variant="outline" className={cn('font-medium', harvestStatusStyles[status])}>
      {status}
    </Badge>
  );
}

interface RecentHarvestsTableProps {
  harvests: RecentHarvest[];
}

export function RecentHarvestsTable({ harvests }: RecentHarvestsTableProps) {
  const columns = useMemo<LegacyColumnDef<RecentHarvest>[]>(
    () => [
      {
        accessorKey: 'batch',
        header: 'Batch',
        cell: ({ row }) => <span className="font-mono text-sm font-medium">{row.original.batch}</span>,
      },
      { accessorKey: 'farm', header: 'Farm' },
      { accessorKey: 'crop', header: 'Crop' },
      {
        accessorKey: 'quantityKg',
        header: 'Quantity',
        cell: ({ row }) => (
          <span className="font-mono text-sm font-semibold tabular-nums">
            {formatWeight(row.original.quantityKg)}
          </span>
        ),
      },
      { accessorKey: 'date', header: 'Date' },
      {
        accessorKey: 'status',
        header: 'Status',
        cell: ({ row }) => <HarvestStatusBadge status={row.original.status} />,
      },
    ],
    [],
  );

  return (
    <ContentCard title="Recent Harvests" description="Latest harvest batches across all farms">
      <DataTable
        columns={columns}
        data={harvests}
        searchable
        searchPlaceholder="Search harvests..."
        showPagination={false}
        emptyTitle="No harvest records found"
        emptyDescription="Harvests recorded this week will appear here."
      />
    </ContentCard>
  );
}
