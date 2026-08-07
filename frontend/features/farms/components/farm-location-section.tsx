'use client';

import { MapPin, Navigation } from 'lucide-react';

import { ContentCard } from '@/components/shared/content-card';
import { SectionHeader } from '@/components/shared/section-header';
import { formatArea } from '../utils/format';
import type { Farm } from '../types/farm.types';

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

interface FarmLocationSectionProps {
  farm: Farm;
}

export function FarmLocationSection({ farm }: FarmLocationSectionProps) {
  const hasCoordinates = farm.latitude !== null && farm.longitude !== null;

  return (
    <ContentCard>
      <div className="space-y-4">
        <SectionHeader title="Location" description="Address and GPS position" />

        <dl className="divide-y">
          <DetailRow label="Village" value={farm.village} />
          <DetailRow label="District" value={farm.district} />
          <DetailRow label="State" value={farm.state} />
          <DetailRow label="Area" value={formatArea(farm.area, farm.areaUnit)} />
        </dl>

        <div
          role="img"
          aria-label={`Map placeholder at latitude ${farm.latitude ?? 'unknown'} and longitude ${farm.longitude ?? 'unknown'}`}
          className="relative overflow-hidden rounded-lg border bg-muted/40"
        >
          <div
            className="pointer-events-none absolute inset-0 opacity-[0.15] dark:opacity-[0.08]"
            style={{
              backgroundImage:
                'linear-gradient(hsl(var(--foreground)) 1px, transparent 1px), linear-gradient(90deg, hsl(var(--foreground)) 1px, transparent 1px)',
              backgroundSize: '28px 28px',
            }}
            aria-hidden="true"
          />
          <div className="relative flex flex-col items-center justify-center gap-1.5 px-4 py-10 text-center">
            {hasCoordinates ? (
              <>
                <MapPin className="h-6 w-6 text-primary" aria-hidden="true" />
                <p className="text-sm font-medium">
                  {farm.latitude}°, {farm.longitude}°
                </p>
              </>
            ) : (
              <>
                <MapPin className="h-6 w-6 text-muted-foreground" aria-hidden="true" />
                <p className="text-sm font-medium">No coordinates recorded</p>
              </>
            )}
            <p className="flex items-center gap-1.5 text-xs text-muted-foreground">
              <Navigation className="h-3 w-3" aria-hidden="true" />
              Interactive map preview is not available. Coordinates are stored for future mapping.
            </p>
          </div>
        </div>
      </div>
    </ContentCard>
  );
}
