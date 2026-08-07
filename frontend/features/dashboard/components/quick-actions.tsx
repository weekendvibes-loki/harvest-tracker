'use client';

import { motion } from 'framer-motion';
import { ArrowUpRight } from 'lucide-react';
import Link from 'next/link';
import type { Route } from 'next';

import { ContentCard } from '@/components/shared/content-card';
import type { DashboardQuickAction } from '../types/dashboard.types';

interface QuickActionsProps {
  actions: DashboardQuickAction[];
}

export function QuickActions({ actions }: QuickActionsProps) {
  return (
    <ContentCard title="Quick Actions" description="Common tasks across the platform">
      <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        {actions.map((action, index) => (
          <motion.div
            key={action.id}
            initial={{ opacity: 0, y: 6 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.2, delay: index * 0.05, ease: 'easeOut' }}
            whileHover={{ y: -2 }}
          >
            <Link
              href={action.href as Route}
              className="group flex h-full items-start gap-3 rounded-lg border p-4 transition-colors duration-150 hover:border-primary/40 hover:bg-accent/40"
            >
              <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-muted text-muted-foreground transition-colors duration-150 group-hover:bg-primary group-hover:text-primary-foreground">
                <action.icon className="h-4 w-4" aria-hidden="true" />
              </span>
              <span className="min-w-0 flex-1">
                <span className="flex items-center justify-between gap-2">
                  <span className="text-sm font-medium">{action.label}</span>
                  <ArrowUpRight
                    className="h-3.5 w-3.5 shrink-0 text-muted-foreground transition-colors duration-150 group-hover:text-primary"
                    aria-hidden="true"
                  />
                </span>
                <span className="mt-0.5 block text-xs text-muted-foreground">
                  {action.description}
                </span>
              </span>
            </Link>
          </motion.div>
        ))}
      </div>
    </ContentCard>
  );
}
