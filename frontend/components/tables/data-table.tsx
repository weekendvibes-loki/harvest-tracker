'use client';

import { flexRender } from '@tanstack/react-table';
import {
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useLegacyTable,
  type LegacyColumnDef,
} from '@tanstack/react-table/legacy';
import type { ColumnVisibilityState, PaginationState, RowData, SortingState } from '@tanstack/table-core';
import { ArrowDown, ArrowUp, ChevronsUpDown } from 'lucide-react';
import { useState } from 'react';

import { ColumnVisibility } from '@/components/tables/column-visibility';
import { EmptyTable } from '@/components/tables/empty-table';
import { LoadingTable } from '@/components/tables/loading-table';
import { Pagination } from '@/components/tables/pagination';
import { TableSearch } from '@/components/tables/table-search';
import { TableToolbar } from '@/components/tables/table-toolbar';
import { Button } from '@/components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { cn } from '@/lib/utils';

export interface DataTableProps<TData extends RowData, TValue> {
  columns: LegacyColumnDef<TData, TValue>[];
  data: TData[];
  isLoading?: boolean;
  searchable?: boolean;
  searchPlaceholder?: string;
  showColumnVisibility?: boolean;
  showPagination?: boolean;
  pageSize?: number;
  pageSizeOptions?: number[];
  toolbar?: React.ReactNode;
  toolbarActions?: React.ReactNode;
  emptyTitle?: string;
  emptyDescription?: string;
  className?: string;
}

export function DataTable<TData extends RowData, TValue>({
  columns,
  data,
  isLoading = false,
  searchable = false,
  searchPlaceholder = 'Search...',
  showColumnVisibility = true,
  showPagination = true,
  pageSize = 10,
  pageSizeOptions = [10, 25, 50],
  toolbar,
  toolbarActions,
  emptyTitle = 'No results found',
  emptyDescription = 'Adjust your search or filters to find what you are looking for.',
  className,
}: DataTableProps<TData, TValue>) {
  const [sorting, setSorting] = useState<SortingState>([]);
  const [columnVisibility, setColumnVisibility] = useState<ColumnVisibilityState>({});
  const [globalFilter, setGlobalFilter] = useState('');
  const [pagination, setPagination] = useState<PaginationState>({ pageIndex: 0, pageSize });

  const table = useLegacyTable<TData>({
    data,
    columns: columns as LegacyColumnDef<TData>[],
    state: {
      sorting,
      columnVisibility,
      globalFilter,
      pagination,
    },
    onSortingChange: setSorting,
    onColumnVisibilityChange: setColumnVisibility,
    onGlobalFilterChange: setGlobalFilter,
    onPaginationChange: setPagination,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
  });

  const rows = table.getRowModel().rows;
  const headerGroups = table.getHeaderGroups();
  const totalCount = table.getFilteredRowModel().rows.length;

  const visibilityColumns = table.getAllLeafColumns().map((column) => ({
    id: column.id,
    label: typeof column.columnDef.header === 'string' ? column.columnDef.header : column.id,
    isVisible: column.getIsVisible(),
  }));

  const showToolbar = Boolean(searchable || toolbar || toolbarActions || showColumnVisibility);

  return (
    <div className={cn('w-full', className)}>
      {showToolbar ? (
        <div className="mb-4">
          <TableToolbar
            actions={
              <div className="flex items-center gap-2">
                {toolbarActions}
                {showColumnVisibility ? (
                  <ColumnVisibility
                    columns={visibilityColumns}
                    onToggle={(id) => table.getColumn(id)?.toggleVisibility()}
                  />
                ) : null}
              </div>
            }
          >
            {searchable ? (
              <TableSearch
                value={globalFilter}
                onChange={setGlobalFilter}
                placeholder={searchPlaceholder}
              />
            ) : null}
            {toolbar}
          </TableToolbar>
        </div>
      ) : null}

      <div className="rounded-lg border">
        <Table>
          <TableHeader>
            {headerGroups.map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  const canSort = header.column.getCanSort();
                  const isSorted = header.column.getIsSorted();

                  return (
                    <TableHead
                      key={header.id}
                      className={canSort ? 'select-none' : undefined}
                    >
                      {header.isPlaceholder ? null : canSort ? (
                        <Button
                          variant="ghost"
                          size="sm"
                          className="-ml-3 h-8 gap-1 px-2 font-medium uppercase tracking-wide"
                          onClick={header.column.getToggleSortingHandler()}
                          aria-label={`Sort by ${String(header.column.columnDef.header ?? header.column.id)}`}
                        >
                          {flexRender(header.column.columnDef.header, header.getContext())}
                          {isSorted === 'asc' ? (
                            <ArrowUp className="h-3.5 w-3.5" aria-hidden="true" />
                          ) : isSorted === 'desc' ? (
                            <ArrowDown className="h-3.5 w-3.5" aria-hidden="true" />
                          ) : (
                            <ChevronsUpDown
                              className="h-3.5 w-3.5 text-muted-foreground/60"
                              aria-hidden="true"
                            />
                          )}
                        </Button>
                      ) : (
                        flexRender(header.column.columnDef.header, header.getContext())
                      )}
                    </TableHead>
                  );
                })}
              </TableRow>
            ))}
          </TableHeader>

          <TableBody>
            {isLoading ? (
              <LoadingTable colSpan={table.getAllLeafColumns().length} />
            ) : rows.length === 0 ? (
              <TableRow>
                <TableCell colSpan={table.getAllLeafColumns().length} className="p-0">
                  <EmptyTable title={emptyTitle} description={emptyDescription} />
                </TableCell>
              </TableRow>
            ) : (
              rows.map((row) => (
                <TableRow key={row.id} data-state={row.getIsSelected() ? 'selected' : undefined}>
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {showPagination && !isLoading && rows.length > 0 ? (
        <Pagination
          pageIndex={table.getState().pagination.pageIndex}
          pageCount={table.getPageCount()}
          pageSize={table.getState().pagination.pageSize}
          totalCount={totalCount}
          onPageChange={(index) => table.setPageIndex(index)}
          onPageSizeChange={(size) => {
            table.setPageSize(size);
            table.setPageIndex(0);
          }}
          pageSizeOptions={pageSizeOptions}
          className="mt-4"
        />
      ) : null}
    </div>
  );
}
