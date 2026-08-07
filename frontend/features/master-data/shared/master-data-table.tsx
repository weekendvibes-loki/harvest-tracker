'use client';

import type { LegacyColumnDef } from '@tanstack/react-table/legacy';
import { useEffect, useRef, useState } from 'react';
import { Pencil, Trash2 } from 'lucide-react';

import { DeleteDialog } from '@/components/dialogs/delete-dialog';
import { DataTable } from '@/components/tables/data-table';
import { Button } from '@/components/ui/button';
import type { MasterDataModuleConfig, MasterDataRecord } from '../types/master-data.types';
import { formatDate } from './format';
import { StatusBadge } from './status-badge';
import { StatusToggle } from './status-toggle';

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

interface MasterDataTableProps {
  config: MasterDataModuleConfig;
  items: MasterDataRecord[];
  isLoading: boolean;
  pendingIds: string[];
  emptyTitle: string;
  emptyDescription: string;
  onEdit: (record: MasterDataRecord) => void;
  onDelete: (ids: string[]) => Promise<void>;
  onToggleStatus: (record: MasterDataRecord) => void;
}

export function MasterDataTable({
  config,
  items,
  isLoading,
  pendingIds,
  emptyTitle,
  emptyDescription,
  onEdit,
  onDelete,
  onToggleStatus,
}: MasterDataTableProps) {
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [deleteTarget, setDeleteTarget] = useState<DeleteTarget | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const currentIds = items.map((item) => item.id);
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

  const columns: LegacyColumnDef<MasterDataRecord>[] = [
    {
      id: 'select',
      header: () => (
        <SelectAllCheckbox
          checked={allSelected}
          indeterminate={someSelected}
          onChange={toggleAll}
          disabled={isLoading}
          label="Select all rows"
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
      header: 'Name',
      cell: ({ row }) => <span className="font-medium">{row.original.name}</span>,
    },
    {
      accessorKey: 'code',
      header: 'Code',
      cell: ({ row }) => <span className="font-mono text-sm">{row.original.code}</span>,
    },
    {
      accessorKey: 'description',
      header: 'Description',
      enableSorting: false,
      cell: ({ row }) => (
        <span
          className="block max-w-[24rem] truncate text-muted-foreground"
          title={row.original.description}
        >
          {row.original.description || '—'}
        </span>
      ),
    },
    {
      accessorKey: 'status',
      header: 'Status',
      cell: ({ row }) => <StatusBadge status={row.original.status} />,
    },
    {
      accessorKey: 'createdAt',
      header: 'Created',
      cell: ({ row }) => (
        <span className="whitespace-nowrap text-muted-foreground">
          {formatDate(row.original.createdAt)}
        </span>
      ),
    },
    {
      accessorKey: 'updatedAt',
      header: 'Updated',
      cell: ({ row }) => (
        <span className="whitespace-nowrap text-muted-foreground">
          {formatDate(row.original.updatedAt)}
        </span>
      ),
    },
    {
      id: 'actions',
      header: 'Actions',
      enableSorting: false,
      enableHiding: false,
      cell: ({ row }) => {
        const record = row.original;
        return (
          <div className="flex items-center gap-1">
            <StatusToggle
              checked={record.status === 'ACTIVE'}
              onChange={() => onToggleStatus(record)}
              label={`Set ${record.name} as ${record.status === 'ACTIVE' ? 'inactive' : 'active'}`}
              disabled={pendingIds.includes(record.id)}
              compact
            />
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8"
              onClick={() => onEdit(record)}
              aria-label={`Edit ${record.name}`}
            >
              <Pencil className="h-4 w-4" aria-hidden="true" />
            </Button>
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8 text-destructive hover:text-destructive"
              onClick={() =>
                requestDelete({
                  ids: [record.id],
                  title: `Delete ${config.singular}`,
                  itemName: record.name,
                })
              }
              aria-label={`Delete ${record.name}`}
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
                  title: `Delete ${selectedCount} selected records`,
                  itemName: `${selectedCount} records`,
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
        data={items}
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
