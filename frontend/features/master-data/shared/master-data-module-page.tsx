'use client';

import { useState } from 'react';
import { CheckCircle2, Circle, Plus, RefreshCw } from 'lucide-react';

import { StatCard } from '@/components/cards/stat-card';
import { PageContainer } from '@/components/layout/page-container';
import { FilterChip } from '@/components/search/filter-chip';
import { FilterPanel } from '@/components/search/filter-panel';
import { QuickFilters } from '@/components/search/quick-filters';
import { SearchBar } from '@/components/search/search-bar';
import { ErrorState } from '@/components/shared/error-state';
import { PageHeader } from '@/components/shared/page-header';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { useMasterData } from '../hooks/use-master-data';
import type {
  MasterDataCreateInput,
  MasterDataModuleKey,
  MasterDataRecord,
} from '../types/master-data.types';
import { MasterDataFormDialog } from './master-data-form-dialog';
import { MasterDataTable } from './master-data-table';
import { masterDataModuleConfigs } from './module-configs';

const STATUS_OPTIONS = [
  { id: 'ACTIVE', label: 'Active' },
  { id: 'INACTIVE', label: 'Inactive' },
];

const daysSince = (isoDate: string) =>
  Math.floor((Date.now() - new Date(isoDate).getTime()) / 86_400_000);

interface MasterDataModulePageProps {
  moduleKey: MasterDataModuleKey;
}

export function MasterDataModulePage({ moduleKey }: MasterDataModulePageProps) {
  const config = masterDataModuleConfigs[moduleKey];

  const {
    items,
    allItems,
    isLoading,
    isRefreshing,
    error,
    search,
    setSearch,
    statusFilter,
    setStatusFilter,
    activeFilterCount,
    resetFilters,
    load,
    create,
    update,
    remove,
    toggleStatus,
    pendingIds,
  } = useMasterData(config.key);

  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<MasterDataRecord | null>(null);

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const openEdit = (record: MasterDataRecord) => {
    setEditing(record);
    setFormOpen(true);
  };

  const handleSubmit = async (input: MasterDataCreateInput) => {
    if (editing) {
      await update(editing.id, input);
    } else {
      await create(input);
    }
  };

  const activeCount = allItems.filter((item) => item.status === 'ACTIVE').length;
  const inactiveCount = allItems.length - activeCount;
  const recentlyUpdated = allItems.filter((item) => daysSince(item.updatedAt) <= 7).length;

  const hasActiveFilters = activeFilterCount > 0;

  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader title={config.title} description={config.description}>
          <Button
            variant="outline"
            size="sm"
            onClick={() => load(true)}
            disabled={isRefreshing}
          >
            <RefreshCw
              className={cn('h-4 w-4', isRefreshing && 'animate-spin')}
              aria-hidden="true"
            />
            Refresh
          </Button>
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-4 w-4" aria-hidden="true" />
            Add {config.singular}
          </Button>
        </PageHeader>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <StatCard
            label="Total records"
            value={allItems.length}
            icon={config.icon}
            isLoading={isLoading}
          />
          <StatCard label="Active" value={activeCount} icon={CheckCircle2} isLoading={isLoading} />
          <StatCard label="Inactive" value={inactiveCount} icon={Circle} isLoading={isLoading} />
          <StatCard
            label="Updated this week"
            value={recentlyUpdated}
            icon={RefreshCw}
            isLoading={isLoading}
          />
        </div>

        <div className="flex flex-col gap-4 lg:flex-row lg:items-start">
          <SearchBar
            value={search}
            onChange={setSearch}
            placeholder={config.searchPlaceholder}
            aria-label={`Search ${config.title}`}
            className="w-full lg:max-w-sm"
          />
          <FilterPanel badge={hasActiveFilters ? activeFilterCount : undefined} className="w-full lg:max-w-sm">
            <QuickFilters
              options={STATUS_OPTIONS}
              selected={statusFilter === 'ALL' ? [] : [statusFilter]}
              onChange={(selected) =>
                setStatusFilter(
                  selected.length === 0 ? 'ALL' : (selected[0] as 'ACTIVE' | 'INACTIVE'),
                )
              }
              multiple={false}
            />
          </FilterPanel>
        </div>

        {hasActiveFilters ? (
          <div className="flex flex-wrap gap-2">
            {search.trim() ? (
              <FilterChip label="Search" value={search} onRemove={() => setSearch('')} />
            ) : null}
            {statusFilter !== 'ALL' ? (
              <FilterChip
                label="Status"
                value={statusFilter.charAt(0) + statusFilter.slice(1).toLowerCase()}
                onRemove={() => setStatusFilter('ALL')}
              />
            ) : null}
            <Button variant="link" size="sm" className="px-1" onClick={resetFilters}>
              Clear all
            </Button>
          </div>
        ) : null}

        {error ? (
          <ErrorState message={error} onRetry={() => load()} />
        ) : (
          <MasterDataTable
            config={config}
            items={items}
            isLoading={isLoading}
            pendingIds={pendingIds}
            emptyTitle={hasActiveFilters ? 'No matching records' : config.emptyTitle}
            emptyDescription={
              hasActiveFilters
                ? 'Try adjusting your search or filters.'
                : config.emptyDescription
            }
            onEdit={openEdit}
            onDelete={remove}
            onToggleStatus={toggleStatus}
          />
        )}

        <MasterDataFormDialog
          open={formOpen}
          onOpenChange={setFormOpen}
          mode={editing ? 'edit' : 'create'}
          record={editing}
          config={config}
          existing={allItems}
          onSubmit={handleSubmit}
        />
      </div>
    </PageContainer>
  );
}
