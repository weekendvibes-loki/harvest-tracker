'use client';

import type { Route } from 'next';
import Link from 'next/link';
import type { LegacyColumnDef } from '@tanstack/react-table/legacy';
import { Eye, Pencil, Trash2 } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';

import { DeleteDialog } from '@/components/dialogs/delete-dialog';
import { DataTable } from '@/components/tables/data-table';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { formatArea } from '../utils/format';
import type { Farm } from '../types/farm.types';
import { FarmStatusBadge } from './farm-status-badge';
import { FarmStatusToggle } from './farm-status-toggle';

const ownershipStyles: Record<'OWNED' | 'LEASED', string> = {
  OWNED:
    'border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-400',
  LEASED:
    'border-sky-200 bg-sky-50 text-sky-700 dark:border-sky-900 dark:bg-sky-950/40 dark:text-sky-400',
};

interface SelectAllCheckboxProps {
  checked: boolean;
  indeterminate: boolean;
  onChange: () => void;
  disabled?: boolean;
  label: string;
}

function SelectAllCheckbox({
  checked,
  indeterminate,
  onChange,
  disabled,
  label,
}: SelectAllCheckboxProps) {
  const ref = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (ref.current) {
      ref.current.indeterminate = indeterminate;
    }
  }, [indeterminate]);

  return (
    <input
      ref={ref}
      type="checkbox"
      checked={checked}
      onChange={onChange}
      disabled={disabled}
      aria-label={label}
      className="h-4 w-4 cursor-pointer rounded accent-emerald-600 disabled:cursor-not-allowed disabled:opacity-50"
    />
  );
}

interface DeleteTarget {
  ids: string[];
  title: string;
  itemName: string;
}

interface FarmTableProps {
  farms: Farm[];
  isLoading: boolean;
  pendingIds: string[];
  emptyTitle: string;
  emptyDescription: string;
  onView: (farm: Farm) => void;
  onEdit: (farm: Farm) => void;
  onDelete: (ids: string[]) => Promise<void>;
  onToggleStatus: (farm: Farm) => void;
}

export function FarmTable({
  farms,
  isLoading,
  pendingIds,
  emptyTitle,
  emptyDescription,
  onView,
  onEdit,
  onDelete,
  onToggleStatus,
}: FarmTableProps) {
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [deleteTarget, setDeleteTarget] = useState<DeleteTarget | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const currentIds = farms.map((farm) => farm.id);
  const selectedSet = new Set(selectedIds.filter((id) => currentIds.includes(id)));
  const selectedCount = selectedSet.size;
  const allSelected = currentIds.length > 0 && currentIds.every((id) => selectedSet.has(id));
  const someSelected = selectedCount > 0 && !allSelected;

  const toggleAll = () => {
    if (allSelected) {
      setSelectedIds((current) => current.filter((id) => !currentIds.includes(id)));
    } else {
      const merged = Array.from(new Set([...selectedIds, ...currentIds]));
      setSelectedIds(merged);
    }
  };

  const toggleOne = (id: string) => {
    setSelectedIds((current) =>
      current.includes(id) ? current.filter((item) => item !== id) : [...current, id],
    );
  };

  const requestDelete = (target: DeleteTarget) => setDeleteTarget(target);

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    setIsDeleting(true);
    try {
      await onDelete(deleteTarget.ids);
      setSelectedIds((current) => current.filter((id) => !deleteTarget.ids.includes(id)));
      setDeleteTarget(null);
    } finally {
      setIsDeleting(false);
    }
  };

  const columns: LegacyColumnDef<Farm>[] = [
    {
      id: 'select',
      header: () => (
        <SelectAllCheckbox
          checked={allSelected}
          indeterminate={someSelected}
          onChange={toggleAll}
          disabled={isLoading}
          label="Select all farms"
        />
      ),
      cell: ({ row }) => (
        <input
          type="checkbox"
          checked={selectedSet.has(row.original.id)}
          onChange={() => toggleOne(row.original.id)}
          aria-label={`Select ${row.original.name}`}
          className="h-4 w-4 cursor-pointer rounded accent-emerald-600"
        />
      ),
      enableSorting: false,
      enableHiding: false,
    },
    {
      accessorKey: 'name',
      header: 'Farm Name',
      cell: ({ row }) => (
        <span
          className="block max-w-[16rem] truncate"
          title={row.original.name}
        >
          <Link
            href={`/farms/${row.original.id}` as Route}
            className="font-medium hover:text-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring rounded-sm"
          >
            {row.original.name}
          </Link>
        </span>
      ),
    },
    {
      accessorKey: 'ownerName',
      header: 'Owner',
      cell: ({ row }) => <span className="text-muted-foreground">{row.original.ownerName}</span>,
    },
    {
      accessorKey: 'ownershipType',
      header: 'Farm Type',
      cell: ({ row }) => (
        <Badge variant="outline" className={`font-medium ${ownershipStyles[row.original.ownershipType]}`}>
          {row.original.ownershipType.charAt(0) + row.original.ownershipType.slice(1).toLowerCase()}
        </Badge>
      ),
    },
    {
      accessorKey: 'village',
      header: 'Village',
      cell: ({ row }) => <span className="text-muted-foreground">{row.original.village}</span>,
    },
    {
      accessorKey: 'district',
      header: 'District',
      cell: ({ row }) => <span className="text-muted-foreground">{row.original.district}</span>,
    },
    {
      accessorKey: 'fruitTypes',
      header: 'Fruit Types',
      enableSorting: false,
      cell: ({ row }) => {
        const names = row.original.fruitTypes.map((item) => item.name);
        if (names.length === 0) {
          return <span className="text-muted-foreground">—</span>;
        }
        const shown = names.slice(0, 2).join(', ');
        const extra = names.length > 2 ? ` +${names.length - 2}` : '';
        return (
          <span className="block max-w-[14rem] truncate text-sm" title={names.join(', ')}>
            {shown}
            {extra}
          </span>
        );
      },
    },
    {
      accessorKey: 'area',
      header: 'Area',
      cell: ({ row }) => (
        <span className="whitespace-nowrap text-muted-foreground">
          {formatArea(row.original.area, row.original.areaUnit)}
        </span>
      ),
    },
    {
      accessorKey: 'status',
      header: 'Status',
      cell: ({ row }) => <FarmStatusBadge status={row.original.status} />,
    },
    {
      id: 'actions',
      header: 'Actions',
      enableSorting: false,
      enableHiding: false,
      cell: ({ row }) => {
        const farm = row.original;
        return (
          <div className="flex items-center gap-1">
            <FarmStatusToggle
              checked={farm.status === 'ACTIVE'}
              onChange={() => onToggleStatus(farm)}
              label={`Set ${farm.name} as ${farm.status === 'ACTIVE' ? 'inactive' : 'active'}`}
              disabled={pendingIds.includes(farm.id)}
              compact
            />
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8"
              onClick={() => onView(farm)}
              aria-label={`View ${farm.name}`}
            >
              <Eye className="h-4 w-4" aria-hidden="true" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8"
              onClick={() => onEdit(farm)}
              aria-label={`Edit ${farm.name}`}
            >
              <Pencil className="h-4 w-4" aria-hidden="true" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8 text-destructive hover:text-destructive"
              onClick={() =>
                requestDelete({
                  ids: [farm.id],
                  title: 'Delete Farm',
                  itemName: farm.name,
                })
              }
              aria-label={`Delete ${farm.name}`}
            >
              <Trash2 className="h-4 w-4" aria-hidden="true" />
            </Button>
          </div>
        );
      },
    },
  ];

  return (
    <div className="space-y-4">
      {selectedCount > 0 ? (
        <div className="flex flex-col gap-3 rounded-md border bg-muted/40 px-4 py-2.5 sm:flex-row sm:items-center sm:justify-between">
          <p className="text-sm font-medium" aria-live="polite">
            {selectedCount} selected
          </p>
          <div className="flex items-center gap-2">
            <Button variant="ghost" size="sm" onClick={() => setSelectedIds([])}>
              Clear selection
            </Button>
            <Button
              variant="destructive"
              size="sm"
              onClick={() =>
                requestDelete({
                  ids: Array.from(selectedSet),
                  title: `Delete ${selectedCount} selected farms`,
                  itemName: `${selectedCount} farms`,
                })
              }
            >
              <Trash2 className="h-4 w-4" aria-hidden="true" />
              Delete
            </Button>
          </div>
        </div>
      ) : null}

      <DataTable
        columns={columns}
        data={farms}
        isLoading={isLoading}
        pageSize={10}
        pageSizeOptions={[10, 25, 50]}
        searchable={false}
        emptyTitle={emptyTitle}
        emptyDescription={emptyDescription}
      />

      <DeleteDialog
        open={deleteTarget !== null}
        onOpenChange={(open) => {
          if (!open) setDeleteTarget(null);
        }}
        title={deleteTarget?.title}
        itemName={deleteTarget?.itemName}
        isLoading={isDeleting}
        onConfirm={confirmDelete}
      />
    </div>
  );
}
