'use client';

import { ChartCard } from '@/components/charts/chart-card';
import { ChartLegend } from '@/components/charts/chart-legend';
import { LineChart, type LineChartPoint } from './charts/line-chart';
import { formatQuantity } from '../utils/format';

interface HarvestOverviewChartProps {
  points: LineChartPoint[];
  footer?: string;
}

export function HarvestOverviewChart({ points, footer }: HarvestOverviewChartProps) {
  const total = points.reduce((sum, point) => sum + point.value, 0);

  return (
    <ChartCard
      title="Harvest Overview"
      description="Daily harvest quantity this week"
      action={
        <ChartLegend
          items={[{ label: 'Harvested', color: 'hsl(var(--primary))', value: formatQuantity(total) }]}
        />
      }
      footer={footer}
    >
      <LineChart data={points} formatValue={(value) => `${value} KG`} />
    </ChartCard>
  );
}
