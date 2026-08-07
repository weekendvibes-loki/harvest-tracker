'use client';

import Link from 'next/link';
import type { Route } from 'next';
import { useEffect, useState } from 'react';
import {
  ArrowLeft,
  CalendarDays,
  FileText,
  Layers,
  Pencil,
  RefreshCw,
  Ruler,
} from 'lucide-react';

import { StatCard } from '@/components/cards/stat-card';
import { PageContainer } from '@/components/layout/page-container';
import { ContentCard } from '@/components/shared/content-card';
import { ErrorState } from '@/components/shared/error-state';
import { LoadingSkeleton } from '@/components/shared/loading-skeleton';
import { NotFound } from '@/components/shared/not-found';
import { PageHeader } from '@/components/shared/page-header';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { FarmDocumentsSection } from '../components/farm-documents-section';
import { FarmFormDialog } from '../components/farm-form-dialog';
import { FarmFruitTypesSection } from '../components/farm-fruit-types-section';
import { FarmLocationSection } from '../components/farm-location-section';
import { FarmSeasonsSection } from '../components/farm-seasons-section';
import { FarmStatusBadge } from '../components/farm-status-badge';
import { FarmTimelineSection } from '../components/farm-timeline-section';
import { useFarmDetail } from '../hooks/use-farm-detail';
import { fetchAvailableFruitTypes } from '../services/farm.service';
import type { Farm, FarmCreateInput, FarmFruitType, FarmSeasonInput } from '../types/farm.types';
import { formatArea, formatDate } from '../utils/format';

const waitForNextTick = () => new Promise<void>((resolve) => setTimeout(resolve, 0));

interface DetailRowProps {
  label: string;
  value: React.ReactNode;
}

function DetailRow({ label, value }: DetailRowProps) {
  return (
    <div className="flex items-start justify-between gap-4 py-2.5">
      <dt className="text-sm text-muted-foreground">{label}</dt>
      <dd className="text-right text-sm font-medium">{value}</dd>
    </div>
  );
}

interface FarmDetailPageProps {
  farmId: string;
}

export function FarmDetailPage({ farmId }: FarmDetailPageProps) {
  const {
    farm,
    isLoading,
    isRefreshing,
    error,
    load,
    update,
    addFruitType,
    removeFruitType,
    createSeason,
    updateSeason,
    removeSeason,
  } = useFarmDetail(farmId);

  const [availableFruitTypes, setAvailableFruitTypes] = useState<FarmFruitType[]>([]);
  const [formOpen, setFormOpen] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      await waitForNextTick();
      if (cancelled) return;
      try {
        const fruitTypes = await fetchAvailableFruitTypes();
        if (!cancelled) {
          setAvailableFruitTypes(fruitTypes);
        }
      } catch {
        // Non-critical lookup; the sections still render from farm data.
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  const handleUpdate = async (input: FarmCreateInput) => {
    await update(input);
  };

  const handleAddFruitType = async (fruitType: FarmFruitType) => {
    await addFruitType(fruitType);
  };

  const handleRemoveFruitType = async (fruitTypeId: string) => {
    await removeFruitType(fruitTypeId);
  };

  const handleCreateSeason = async (input: FarmSeasonInput) => {
    await createSeason(input);
  };

  const handleUpdateSeason = async (seasonId: string, input: FarmSeasonInput) => {
    await updateSeason(seasonId, input);
  };

  const handleRemoveSeason = async (seasonId: string) => {
    await removeSeason(seasonId);
  };

  if (isLoading) {
    return (
      <PageContainer>
        <LoadingSkeleton rows={4} />
      </PageContainer>
    );
  }

  if (error && !farm) {
    return (
      <PageContainer>
        <ErrorState message={error} onRetry={() => load()} />
      </PageContainer>
    );
  }

  if (!farm) {
    return (
      <PageContainer>
        <NotFound
          title="Farm not found"
          description="The farm you are looking for does not exist or may have been deleted."
        />
      </PageContainer>
    );
  }

  const currentSeason = farm.seasons.find((season) => season.status === 'ACTIVE') ?? null;

  return (
    <PageContainer>
      <div className="space-y-6">
        <nav aria-label="Breadcrumb" className="flex items-center gap-1.5 text-sm text-muted-foreground">
          <Link
            href="/farms"
            className="transition-colors hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring rounded-sm"
          >
            Farms
          </Link>
          <span aria-hidden="true">/</span>
          <span className="truncate font-medium text-foreground">{farm.name}</span>
        </nav>

        <PageHeader title={farm.name} description={`${farm.village}, ${farm.district}, ${farm.state}`}>
          <div className="flex items-center gap-2">
            <FarmStatusBadge status={farm.status} className="hidden sm:inline-flex" />
            <Button asChild variant="outline" size="sm">
              <Link href="/farms">
                <ArrowLeft className="h-4 w-4" aria-hidden="true" />
                Back to Farms
              </Link>
            </Button>
            <Button variant="outline" size="sm" onClick={() => load(true)} disabled={isRefreshing}>
              <RefreshCw
                className={cn('h-4 w-4', isRefreshing && 'animate-spin')}
                aria-hidden="true"
              />
              Refresh
            </Button>
            <Button size="sm" onClick={() => setFormOpen(true)}>
              <Pencil className="h-4 w-4" aria-hidden="true" />
              Edit Farm
            </Button>
          </div>
        </PageHeader>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <StatCard label="Fruit types" value={farm.fruitTypes.length} icon={Layers} />
          <StatCard label="Land area" value={formatArea(farm.area, farm.areaUnit)} icon={Ruler} />
          <StatCard label="Current season" value={currentSeason?.name ?? '—'} icon={CalendarDays} />
          <StatCard label="Documents" value={farm.documents.length} icon={FileText} />
        </div>

        <div className="grid gap-6 lg:grid-cols-2 xl:grid-cols-3">
          <div className="space-y-6 lg:col-span-1">
            <ContentCard>
              <div className="space-y-1">
                <h2 className="text-sm font-semibold">General Information</h2>
                <dl className="divide-y">
                  <DetailRow label="Owner" value={farm.ownerName} />
                  <DetailRow
                    label="Ownership"
                    value={
                      farm.ownershipType.charAt(0) + farm.ownershipType.slice(1).toLowerCase()
                    }
                  />
                  <DetailRow label="Status" value={<FarmStatusBadge status={farm.status} />} />
                  <DetailRow label="Created" value={formatDate(farm.createdAt)} />
                  <DetailRow label="Last updated" value={formatDate(farm.updatedAt)} />
                </dl>
              </div>
            </ContentCard>

            <FarmLocationSection farm={farm} />

            <FarmTimelineSection farm={farm} />
          </div>

          <div className="space-y-6 lg:col-span-1 xl:col-span-2">
            <FarmSeasonsSection
              farm={farm}
              onCreate={handleCreateSeason}
              onUpdate={handleUpdateSeason}
              onRemove={handleRemoveSeason}
            />

            <FarmFruitTypesSection
              farm={farm}
              availableFruitTypes={availableFruitTypes}
              onAdd={handleAddFruitType}
              onRemove={handleRemoveFruitType}
            />

            <FarmDocumentsSection farm={farm} />
          </div>
        </div>

        <FarmFormDialog
          open={formOpen}
          onOpenChange={setFormOpen}
          mode="edit"
          farm={farm}
          existing={[farm]}
          availableFruitTypes={availableFruitTypes}
          onSubmit={handleUpdate}
        />
      </div>
    </PageContainer>
  );
}
