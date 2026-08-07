'use client';

import { Cloud, CloudRain, CloudSun, Droplets, Sun, Wind, type LucideIcon } from 'lucide-react';

import { ContentCard } from '@/components/shared/content-card';
import { cn } from '@/lib/utils';
import type { CurrentWeather, WeatherCondition, WeatherForecast } from '../types/dashboard.types';

const weatherMeta: Record<WeatherCondition, { icon: LucideIcon; label: string; className: string }> = {
  sunny: { icon: Sun, label: 'Sunny', className: 'text-amber-500' },
  'partly-cloudy': { icon: CloudSun, label: 'Partly Cloudy', className: 'text-amber-500' },
  cloudy: { icon: Cloud, label: 'Cloudy', className: 'text-muted-foreground' },
  rainy: { icon: CloudRain, label: 'Rainy', className: 'text-sky-500' },
};

interface WeatherWidgetProps {
  current: CurrentWeather;
  forecast: WeatherForecast[];
  location?: string;
}

export function WeatherWidget({ current, forecast, location = 'Devgad, Maharashtra' }: WeatherWidgetProps) {
  const CurrentIcon = weatherMeta[current.condition].icon;

  return (
    <ContentCard title="Weather" description={location}>
      <div className="flex items-center justify-between gap-4">
        <div>
          <p className="text-3xl font-bold tracking-tight">{current.temperatureC}°C</p>
          <p className="mt-1 text-sm text-muted-foreground">{weatherMeta[current.condition].label}</p>
        </div>
        <div className="flex flex-col items-end gap-1.5 text-sm text-muted-foreground">
          <span className="inline-flex items-center gap-1.5">
            <Droplets className="h-4 w-4 text-sky-500" aria-hidden="true" />
            {current.humidity}% humidity
          </span>
          <span className="inline-flex items-center gap-1.5">
            <Wind className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
            {current.windKph} km/h wind
          </span>
        </div>
      </div>

      <div className="mt-5 divide-y">
        {forecast.map((day) => {
          const meta = weatherMeta[day.condition];
          const DayIcon = meta.icon;
          return (
            <div key={day.id} className="flex items-center gap-3 py-2.5 text-sm">
              <span className="w-16 shrink-0 font-medium">{day.day}</span>
              <span className="flex flex-1 items-center gap-2">
                <DayIcon className={cn('h-4 w-4', meta.className)} aria-hidden="true" />
                <span className="text-muted-foreground">{meta.label}</span>
              </span>
              <span className="inline-flex w-12 shrink-0 items-center justify-end gap-1 text-muted-foreground">
                <Droplets className="h-3.5 w-3.5 text-sky-500" aria-hidden="true" />
                {day.precipitation}%
              </span>
              <span className="w-14 shrink-0 text-right font-medium tabular-nums">
                {day.lowC}°–{day.highC}°
              </span>
            </div>
          );
        })}
      </div>
    </ContentCard>
  );
}
