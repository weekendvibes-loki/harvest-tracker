'use client';

import { Plus, ShoppingCart, Sprout, Users, type LucideIcon } from 'lucide-react';
import Link from 'next/link';
import type { Route } from 'next';

import { Button } from '@/components/ui/button';
import { getGreeting } from '../utils/greeting';

interface WelcomeBannerProps {
  userName?: string;
  farmCount: number;
  activeWorkers: number;
  openOrders: number;
  recordHref: string;
}

function StatLine({ icon: Icon, label }: { icon: LucideIcon; label: string }) {
  return (
    <span className="inline-flex items-center gap-1.5">
      <Icon className="h-3.5 w-3.5" aria-hidden="true" />
      <span>{label}</span>
    </span>
  );
}

export function WelcomeBanner({
  userName = 'Farm Manager',
  farmCount,
  activeWorkers,
  openOrders,
  recordHref,
}: WelcomeBannerProps) {
  const today = new Intl.DateTimeFormat('en-IN', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  }).format(new Date());

  return (
    <section
      aria-labelledby="welcome-title"
      className="relative overflow-hidden rounded-lg border bg-gradient-to-br from-primary to-primary/90 p-6 text-primary-foreground shadow-sm sm:p-8"
    >
      <div
        aria-hidden="true"
        className="pointer-events-none absolute -right-16 -top-24 h-64 w-64 rounded-full bg-primary-foreground/10"
      />
      <div
        aria-hidden="true"
        className="pointer-events-none absolute -bottom-24 right-24 h-48 w-48 rounded-full bg-amber-400/20"
      />

      <div className="relative space-y-4">
        <div>
          <p className="text-sm font-medium opacity-80">{today}</p>
          <h1 id="welcome-title" className="mt-1 text-2xl font-bold tracking-tight sm:text-3xl">
            {getGreeting()}, {userName}
          </h1>
          <p className="mt-2 max-w-xl text-sm text-primary-foreground/85">
            Here is what is happening across your orchards today.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-x-6 gap-y-2 text-sm">
          <StatLine icon={Sprout} label={`${farmCount} farms active`} />
          <StatLine icon={Users} label={`${activeWorkers} workers today`} />
          <StatLine icon={ShoppingCart} label={`${openOrders} open orders`} />
        </div>

        <div className="pt-1">
          <Button asChild className="bg-white text-primary hover:bg-white/90 hover:text-primary">
            <Link href={recordHref as Route}>
              <Plus className="h-4 w-4" aria-hidden="true" />
              Record Harvest
            </Link>
          </Button>
        </div>
      </div>
    </section>
  );
}
