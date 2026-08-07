'use client';

import { Clock, FileText, History, Sprout } from 'lucide-react';

import { ContentCard } from '@/components/shared/content-card';
import { EmptyState } from '@/components/shared/empty-state';
import { SectionHeader } from '@/components/shared/section-header';
import { cn } from '@/lib/utils';
import { formatDateTime } from '../utils/format';
import type { Farm } from '../types/farm.types';

interface TimelineEvent {
  id: string;
  date: string;
  title: string;
  description: string;
  tone: 'created' | 'updated' | 'season' | 'document';
}

const toneStyles: Record<TimelineEvent['tone'], string> = {
  created: 'bg-emerald-500',
  updated: 'bg-primary',
  season: 'bg-amber-500',
  document: 'bg-slate-400',
};

function buildEvents(farm: Farm): TimelineEvent[] {
  const events: TimelineEvent[] = [
    {
      id: 'event-created',
      date: farm.createdAt,
      title: 'Farm created',
      description: `${farm.name} was added to the business.`,
      tone: 'created',
    },
    {
      id: 'event-updated',
      date: farm.updatedAt,
      title: 'Farm updated',
      description: 'Record details were last modified.',
      tone: 'updated',
    },
  ];

  farm.seasons.forEach((season) => {
    events.push({
      id: `event-season-${season.id}`,
      date: season.startDate,
      title: `${season.name} began`,
      description: `Season scheduled from ${season.startDate} to ${season.endDate}.`,
      tone: 'season',
    });
  });

  farm.documents.forEach((document) => {
    events.push({
      id: `event-document-${document.id}`,
      date: document.uploadedAt,
      title: `Document uploaded: ${document.name}`,
      description: `${document.fileName} attached to this farm.`,
      tone: 'document',
    });
  });

  return events.sort((a, b) => (a.date < b.date ? 1 : -1));
}

interface FarmTimelineSectionProps {
  farm: Farm;
}

export function FarmTimelineSection({ farm }: FarmTimelineSectionProps) {
  const events = buildEvents(farm);

  return (
    <ContentCard>
      <div className="space-y-4">
        <SectionHeader
          title="Timeline"
          description="Key events for this farm"
        />

        {events.length === 0 ? (
          <EmptyState
            title="No activity yet"
            description="Farm activity will appear here over time."
            icon={History}
          />
        ) : (
          <ol className="relative space-y-4 before:absolute before:inset-y-1 before:left-[5px] before:w-px before:bg-border" aria-label="Farm timeline">
            {events.map((event) => {
              const EventIcon =
                event.tone === 'created'
                  ? Sprout
                  : event.tone === 'season'
                    ? Clock
                    : event.tone === 'document'
                      ? FileText
                      : History;
              return (
                <li key={event.id} className="relative pl-6">
                  <span
                    aria-hidden="true"
                    className={cn(
                      'absolute left-0 top-1 flex h-[11px] w-[11px] items-center justify-center rounded-full ring-4 ring-background',
                      toneStyles[event.tone],
                    )}
                  />
                  <div className="flex flex-col gap-0.5 sm:flex-row sm:items-baseline sm:justify-between">
                    <div className="min-w-0">
                      <p className="flex items-center gap-1.5 text-sm font-medium">
                        <EventIcon className="h-3.5 w-3.5 text-muted-foreground" aria-hidden="true" />
                        {event.title}
                      </p>
                      <p className="text-sm text-muted-foreground">{event.description}</p>
                    </div>
                    <time className="shrink-0 text-xs text-muted-foreground" dateTime={event.date}>
                      {formatDateTime(event.date)}
                    </time>
                  </div>
                </li>
              );
            })}
          </ol>
        )}
      </div>
    </ContentCard>
  );
}
