'use client';

import { Activity, Receipt, ShoppingCart, Sprout, Users, type LucideIcon } from 'lucide-react';

import { ContentCard } from '@/components/shared/content-card';
import { cn } from '@/lib/utils';
import type { ActivityItem, ActivityType } from '../types/dashboard.types';

const activityMeta: Record<ActivityType, { icon: LucideIcon; iconClass: string }> = {
  harvest: {
    icon: Sprout,
    iconClass: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/60 dark:text-emerald-300',
  },
  sales: {
    icon: ShoppingCart,
    iconClass: 'bg-sky-100 text-sky-700 dark:bg-sky-900/60 dark:text-sky-300',
  },
  worker: {
    icon: Users,
    iconClass: 'bg-amber-100 text-amber-700 dark:bg-amber-900/60 dark:text-amber-300',
  },
  expense: {
    icon: Receipt,
    iconClass: 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300',
  },
  system: {
    icon: Activity,
    iconClass: 'bg-violet-100 text-violet-700 dark:bg-violet-900/60 dark:text-violet-300',
  },
};

interface RecentActivitiesProps {
  activities: ActivityItem[];
}

export function RecentActivities({ activities }: RecentActivitiesProps) {
  return (
    <ContentCard title="Recent Activity" description="Latest operations across the platform">
      <ol className="space-y-4">
        {activities.map((item, index) => {
          const meta = activityMeta[item.type];
          const Icon = meta.icon;
          const isLast = index === activities.length - 1;

          return (
            <li key={item.id} className="relative flex gap-3">
              <div className="flex flex-col items-center">
                <span
                  className={cn(
                    'flex h-8 w-8 shrink-0 items-center justify-center rounded-full',
                    meta.iconClass,
                  )}
                >
                  <Icon className="h-4 w-4" aria-hidden="true" />
                </span>
                {!isLast ? <span className="mt-1 w-px flex-1 bg-border" aria-hidden="true" /> : null}
              </div>
              <div className="min-w-0 pb-1">
                <div className="flex items-center justify-between gap-2">
                  <p className="text-sm font-medium">{item.title}</p>
                  <time className="shrink-0 text-xs text-muted-foreground">{item.timestamp}</time>
                </div>
                <p className="mt-0.5 text-sm text-muted-foreground">{item.description}</p>
              </div>
            </li>
          );
        })}
      </ol>
    </ContentCard>
  );
}
