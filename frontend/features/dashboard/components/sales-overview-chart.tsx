'use client';

import { ChartCard } from '@/components/charts/chart-card';
import { ChartLegend } from '@/components/charts/chart-legend';
import { BarChart, type BarChartDatum, type BarChartSeries } from './charts/bar-chart';
import { formatCurrency } from '../utils/format';

interface SalesOverviewChartProps {
  data: BarChartDatum[];
  series: BarChartSeries[];
  footer?: string;
}

export function SalesOverviewChart({ data, series, footer }: SalesOverviewChartProps) {
  const totals = series.map((s) => ({
    key: s.key,
    label: s.label,
    total: data.reduce((sum, datum) => sum + (datum.values[s.key] ?? 0), 0),
  }));

  return (
    <ChartCard
      title="Sales Overview"
      description="Monthly revenue against target"
      action={
        <ChartLegend
          items={series.map((s) => ({
            label: s.label,
            color: s.color,
            value: formatCurrency(totals.find((t) => t.key === s.key)?.total ?? 0),
          }))}
        />
      }
      footer={footer}
    >
      <BarChart data={data} series={series} formatValue={(value) => formatCurrency(value)} />
    </ChartCard>
  );
}
