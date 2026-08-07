'use client';

import { motion } from 'framer-motion';

import { StatCard } from '@/components/cards/stat-card';
import type { DashboardKpi } from '../types/dashboard.types';

interface KpiGridProps {
  kpis: DashboardKpi[];
}

export function KpiGrid({ kpis }: KpiGridProps) {
  return (
    <div className="grid gap-4 sm:grid-cols-2">
      {kpis.map((kpi, index) => (
        <motion.div
          key={kpi.id}
          initial={{ opacity: 0, y: 6 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.2, delay: index * 0.05, ease: 'easeOut' }}
          whileHover={{ y: -2 }}
        >
          <StatCard
            label={kpi.label}
            value={kpi.value}
            icon={kpi.icon}
            delta={{ value: kpi.delta.value, direction: kpi.delta.direction, label: kpi.delta.label }}
            className="h-full"
          />
        </motion.div>
      ))}
    </div>
  );
}
