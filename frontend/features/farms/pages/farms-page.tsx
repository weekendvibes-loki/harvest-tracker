'use client';

import { useRouter } from 'next/navigation';
import type { Route } from 'next';
import { useState } from 'react';
import { CheckCircle2, LandPlot, MapPin, Plus, RefreshCw, Tractor } from 'lucide-react';

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
import { FarmFormDialog } from '../components/farm-form-dialog';
import { FarmTable } from '../components/farm-table';
import { useFarms } from '../hooks/use-farms';
import type { Farm, FarmCreateInput } from '../types/farm.types';

const STATUS_OPTIONS = [
  { id: 'ACTIVE', label: 'Active' },
  { id: 'INACTIVE', label: 'Inactive' },
];

const OWNERSHIP_OPTIONS = [
  { id: 'OWNED', label: 'Owned' },
  { id: 'LEASED', label: 'Leased' },
];

export function FarmsPage() {
  const router = useRouter();
  const {
    farms,
    allFarms,
    availableFruitTypes,
    isLoading,
    isRefreshing,
    error,
    search,
    setSearch,
    statusFilter,
    setStatusFilter,
    ownershipFilter,
    setOwnershipFilter,
    fruitTypeFilter,
    setFruitTypeFilter,
    activeFilterCount,
    resetFilters,
    load,
    create,
    update,
    remove,
    toggleStatus,
    pendingIds,
  } = useFarms();

  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Farm | null>(null);

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const openEdit = (farm: Farm) => {
    setEditing(farm);
    setFormOpen(true);
  };

  const handleSubmit = async (input: FarmCreateInput) => {
    if (editing) {
      await update(editing.id, input);
    } else {
      await create(input);
    }
  };

  const openView = (farm: Farm) => {
    router.push(`/farms/${farm.id}` as Route);
  };

  const activeCount = allFarms.filter((farm) => farm.status === 'ACTIVE').length;
  const leasedCount = allFarms.filter((farm) => farm.ownershipType === 'LEASED').length;
  const districtCount = new Set(allFarms.map((farm) => farm.district)).size;

  const hasActiveFilters = activeFilterCount > 0;

  const fruitTypeName =
    availableFruitTypes.find((item) => item.id === fruitTypeFilter)?.name ?? fruitTypeFilter;

  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader title="Farms" description="Manage farm locations">
          <Button variant="outline" size="sm" onClick={() => load(true)} disabled={isRefreshing}>
            <RefreshCw
              className={cn('h-4 w-4', isRefreshing && 'animate-spin')}
              aria-hidden="true"
            />
            Refresh
          </Button>
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-4 w-4" aria-hidden="true" />
            Add Farm
          </Button>
        </PageHeader>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <StatCard label="Total farms" value={allFarms.length} icon={Tractor} isLoading={isLoading} />
          <StatCard label="Active" value={activeCount} icon={CheckCircle2} isLoading={isLoading} />
          <StatCard label="Leased" value={leasedCount} icon={LandPlot} isLoading={isLoading} />
          <StatCard label="Districts" value={districtCount} icon={MapPin} isLoading={isLoading} />
        </div>

        <div className="flex flex-col gap-4 lg:flex-row lg:items-start">
          <SearchBar
            value={search}
            onChange={setSearch}
            placeholder="Search by farm name, owner, village, district or state"
            aria-label="Search farms"
            className="w-full lg:max-w-md"
          />
          <FilterPanel badge={hasActiveFilters ? activeFilterCount : undefined} className="w-full lg:max-w-md">
            <div className="space-y-3">
              <div>
                <p className="mb-1.5 text-xs font-medium uppercase tracking-wide text-muted-foreground">
                  Status
                </p>
                <QuickFilters
                  options={STATUS_OPTIONS}
                  selected={statusFilter === 'ALL' ? [] : [statusFilter]}
                  onChange={(selected) =>
                    setStatusFilter(selected.length === 0 ? 'ALL' : (selected[0] as 'ACTIVE' | 'INACTIVE'))
                  }
                  multiple={false}
                />
              </div>
              <div>
                <p className="mb-1.5 text-xs font-medium uppercase tracking-wide text-muted-foreground">
                  Farm Type
                </p>
                <QuickFilters
                  options={OWNERSHIP_OPTIONS}
                  selected={ownershipFilter === 'ALL' ? [] : [ownershipFilter]}
                  onChange={(selected) =>
                    setOwnershipFilter(selected.length === 0 ? 'ALL' : (selected[0] as 'OWNED' | 'LEASED'))
                  }
                  multiple={false}
                />
              </div>
              <div>
                <p className="mb-1.5 text-xs font-medium uppercase tracking-wide text-muted-foreground">
                  Fruit Type
                </p>
                <QuickFilters
                  options={availableFruitTypes.map((item) => ({ id: item.id, label: item.name }))}
                  selected={fruitTypeFilter === 'ALL' ? [] : [fruitTypeFilter]}
                  onChange={(selected) =>
                    setFruitTypeFilter(selected.length === 0 ? 'ALL' : selected[0])
                  }
                  multiple={false}
                />
              </div>
            </div>
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
            {ownershipFilter !== 'ALL' ? (
              <FilterChip
                label="Farm Type"
                value={ownershipFilter.charAt(0) + ownershipFilter.slice(1).toLowerCase()}
                onRemove={() => setOwnershipFilter('ALL')}
              />
            ) : null}
            {fruitTypeFilter !== 'ALL' ? (
              <FilterChip label="Fruit Type" value={fruitTypeName} onRemove={() => setFruitTypeFilter('ALL')} />
            ) : null}
            <Button variant="link" size="sm" className="px-1" onClick={resetFilters}>
              Clear all
            </Button>
          </div>
        ) : null}

        {error ? (
          <ErrorState message={error} onRetry={() => load()} />
        ) : (
          <FarmTable
            farms={farms}
            isLoading={isLoading}
            pendingIds={pendingIds}
            emptyTitle={hasActiveFilters ? 'No matching farms' : 'No farms yet'}
            emptyDescription={
              hasActiveFilters
                ? 'Try adjusting your search or filters.'
                : 'Add your first farm to start managing locations.'
            }
            onView={openView}
            onEdit={openEdit}
            onDelete={remove}
            onToggleStatus={toggleStatus}
          />
        )}

        <FarmFormDialog
          open={formOpen}
          onOpenChange={setFormOpen}
          mode={editing ? 'edit' : 'create'}
          farm={editing}
          existing={allFarms}
          availableFruitTypes={availableFruitTypes}
          onSubmit={handleSubmit}
        />
      </div>
    </PageContainer>
  );
}
