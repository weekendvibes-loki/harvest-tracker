'use client';

import { AlertCircle, AlertTriangle, CheckCircle2, Info, type LucideIcon } from 'lucide-react';

import { InfoCard, type InfoCardVariant } from '@/components/cards/info-card';
import { ContentCard } from '@/components/shared/content-card';
import type { AlertSeverity, DashboardAlert } from '../types/dashboard.types';

const severityMeta: Record<AlertSeverity, { variant: InfoCardVariant; icon: LucideIcon }> = {
  warning: { variant: 'warning', icon: AlertTriangle },
  danger: { variant: 'danger', icon: AlertCircle },
  info: { variant: 'info', icon: Info },
  success: { variant: 'success', icon: CheckCircle2 },
};

interface AlertsPanelProps {
  alerts: DashboardAlert[];
}

export function AlertsPanel({ alerts }: AlertsPanelProps) {
  return (
    <ContentCard title="Alerts" description="Items that need your attention">
      <div className="space-y-3">
        {alerts.map((alert) => {
          const meta = severityMeta[alert.severity];
          return (
            <InfoCard
              key={alert.id}
              title={alert.title}
              description={alert.description}
              icon={meta.icon}
              variant={meta.variant}
            />
          );
        })}
      </div>
    </ContentCard>
  );
}
