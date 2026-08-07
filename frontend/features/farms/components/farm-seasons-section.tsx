'use client';

import { useState } from 'react';
import { CalendarDays, Pencil, Plus, Trash2 } from 'lucide-react';

import { ConfirmDialog } from '@/components/dialogs/confirm-dialog';
import { ContentCard } from '@/components/shared/content-card';
import { EmptyState } from '@/components/shared/empty-state';
import { SectionHeader } from '@/components/shared/section-header';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { formatDate } from '../utils/format';
import type { Farm, FarmSeason, FarmSeasonInput, SeasonStatus } from '../types/farm.types';
import { FarmSeasonDialog } from './farm-season-dialog';

const seasonBadgeStyles: Record<SeasonStatus, string> = {
  ACTIVE:
    'border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-900 dark:bg-emerald-950/40 dark:text-emerald-400',
  UPCOMING:
    'border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-400',
  COMPLETED:
    'border-slate-200 bg-slate-100 text-slate-600 dark:border-slate-800 dark:bg-slate-800/60 dark:text-slate-300',
};

function SeasonStatusBadge({ status }: { status: SeasonStatus }) {
  return (
    <Badge variant="outline" className={cn('font-medium', seasonBadgeStyles[status])}>
      {status.charAt(0) + status.slice(1).toLowerCase()}
    </Badge>
  );
}

const daysUntil = (isoDate: string): number =>
  Math.ceil((new Date(isoDate).getTime() - Date.now()) / 86_400_000);

function SeasonDates({ season }: { season: FarmSeason }) {
  const daysLeft = daysUntil(season.endDate);
  let label = '';
  if (season.status === 'ACTIVE') {
    label = daysLeft >= 0 ? `${daysLeft} day${daysLeft === 1 ? '' : 's'} remaining` : 'Season ended';
  } else if (season.status === 'UPCOMING') {
    const daysToStart = daysUntil(season.startDate);
    label = daysToStart >= 0 ? `Starts in ${daysToStart} day${daysToStart === 1 ? '' : 's'}` : 'Starting soon';
  } else {
    label = 'Completed';
  }

  return (
    <p className="flex items-center gap-1.5 text-sm text-muted-foreground">
      <CalendarDays className="h-3.5 w-3.5" aria-hidden="true" />
      <span>
        {formatDate(season.startDate)} – {formatDate(season.endDate)}
      </span>
      <span className="text-foreground/70">·</span>
      <span>{label}</span>
    </p>
  );
}

interface FarmSeasonsSectionProps {
  farm: Farm;
  onCreate: (input: FarmSeasonInput) => Promise<unknown>;
  onUpdate: (seasonId: string, input: FarmSeasonInput) => Promise<unknown>;
  onRemove: (seasonId: string) => Promise<unknown>;
}

export function FarmSeasonsSection({ farm, onCreate, onUpdate, onRemove }: FarmSeasonsSectionProps) {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingSeason, setEditingSeason] = useState<FarmSeason | null>(null);
  const [deletingSeason, setDeletingSeason] = useState<FarmSeason | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const currentSeason = farm.seasons.find((season) => season.status === 'ACTIVE') ?? null;
  const previousSeasons = farm.seasons
    .filter((season) => season.status !== 'ACTIVE')
    .sort((a, b) => (a.startDate < b.startDate ? 1 : -1));

  const openCreate = () => {
    setEditingSeason(null);
    setDialogOpen(true);
  };

  const openEdit = (season: FarmSeason) => {
    setEditingSeason(season);
    setDialogOpen(true);
  };

  const confirmDelete = async () => {
    if (!deletingSeason) return;
    setIsDeleting(true);
    try {
      await onRemove(deletingSeason.id);
      setDeletingSeason(null);
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <ContentCard>
      <div className="space-y-4">
        <SectionHeader
          title="Current Season"
          description="Active growing or harvest season"
        >
          <Button size="sm" onClick={openCreate}>
            <Plus className="h-4 w-4" aria-hidden="true" />
            New Season
          </Button>
        </SectionHeader>

        {currentSeason ? (
          <div className="rounded-md border border-emerald-200 bg-emerald-50/50 p-4 dark:border-emerald-900/60 dark:bg-emerald-950/20">
            <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
              <div className="min-w-0 space-y-1">
                <div className="flex flex-wrap items-center gap-2">
                  <p className="text-sm font-semibold">{currentSeason.name}</p>
                  <SeasonStatusBadge status={currentSeason.status} />
                </div>
                <SeasonDates season={currentSeason} />
                {currentSeason.notes ? (
                  <p className="text-sm text-muted-foreground">{currentSeason.notes}</p>
                ) : null}
              </div>
              <div className="flex shrink-0 items-center gap-1">
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8"
                  onClick={() => openEdit(currentSeason)}
                  aria-label={`Edit ${currentSeason.name}`}
                >
                  <Pencil className="h-4 w-4" aria-hidden="true" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8 text-destructive hover:text-destructive"
                  onClick={() => setDeletingSeason(currentSeason)}
                  aria-label={`Delete ${currentSeason.name}`}
                >
                  <Trash2 className="h-4 w-4" aria-hidden="true" />
                </Button>
              </div>
            </div>
          </div>
        ) : (
          <EmptyState
            title="No active season"
            description="Create a season to start tracking growing and harvest cycles."
            icon={CalendarDays}
            action={
              <Button size="sm" onClick={openCreate}>
                <Plus className="h-4 w-4" aria-hidden="true" />
                Create Season
              </Button>
            }
          />
        )}

        <div className="border-t pt-4">
          <SectionHeader
            title="Previous Seasons"
            description="Upcoming and completed seasons"
          />
          {previousSeasons.length === 0 ? (
            <p className="text-sm text-muted-foreground">No previous seasons recorded.</p>
          ) : (
            <ul className="space-y-2">
              {previousSeasons.map((season) => (
                <li
                  key={season.id}
                  className="flex flex-col gap-2 rounded-md border px-4 py-3 sm:flex-row sm:items-center sm:justify-between"
                >
                  <div className="min-w-0 space-y-1">
                    <div className="flex flex-wrap items-center gap-2">
                      <p className="text-sm font-medium">{season.name}</p>
                      <SeasonStatusBadge status={season.status} />
                    </div>
                    <SeasonDates season={season} />
                  </div>
                  <div className="flex shrink-0 items-center gap-1">
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-8 w-8"
                      onClick={() => openEdit(season)}
                      aria-label={`Edit ${season.name}`}
                    >
                      <Pencil className="h-4 w-4" aria-hidden="true" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-8 w-8 text-destructive hover:text-destructive"
                      onClick={() => setDeletingSeason(season)}
                      aria-label={`Delete ${season.name}`}
                    >
                      <Trash2 className="h-4 w-4" aria-hidden="true" />
                    </Button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <FarmSeasonDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        mode={editingSeason ? 'edit' : 'create'}
        season={editingSeason}
        onSubmit={(input) =>
          editingSeason ? onUpdate(editingSeason.id, input) : onCreate(input)
        }
      />

      <ConfirmDialog
        open={deletingSeason !== null}
        onOpenChange={(open) => {
          if (!open) setDeletingSeason(null);
        }}
        title="Delete Season"
        description={
          <>
            This will permanently delete{' '}
            <span className="font-medium text-foreground">{deletingSeason?.name}</span> from this
            farm.
          </>
        }
        confirmLabel="Delete"
        variant="destructive"
        isLoading={isDeleting}
        onConfirm={confirmDelete}
      />
    </ContentCard>
  );
}
