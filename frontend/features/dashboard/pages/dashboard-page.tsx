'use client';

import dynamic from 'next/dynamic';

import { ChartCard } from '@/components/charts/chart-card';
import { PageContainer } from '@/components/layout/page-container';
import { AlertsPanel } from '../components/alerts-panel';
import type { BarChartDatum, BarChartSeries } from '../components/charts/bar-chart';
import type { LineChartPoint } from '../components/charts/line-chart';
import { KpiGrid } from '../components/kpi-grid';
import { QuickActions } from '../components/quick-actions';
import { RecentActivities } from '../components/recent-activities';
import { RecentHarvestsTable } from '../components/recent-harvests-table';
import { RecentSalesTable } from '../components/recent-sales-table';
import { WeatherWidget } from '../components/weather-widget';
import { WelcomeBanner } from '../components/welcome-banner';
import { dashboardData } from '../mock/dashboard.mock';

const HarvestOverviewChart = dynamic(
  () => import('../components/harvest-overview-chart').then((m) => m.HarvestOverviewChart),
  {
    ssr: false,
    loading: () => (
      <ChartCard
        title="Harvest Overview"
        description="Daily harvest quantity this week"
        isLoading
      >
        <span className="sr-only">Loading harvest overview chart</span>
      </ChartCard>
    ),
  },
);

const SalesOverviewChart = dynamic(
  () => import('../components/sales-overview-chart').then((m) => m.SalesOverviewChart),
  {
    ssr: false,
    loading: () => (
      <ChartCard
        title="Sales Overview"
        description="Monthly revenue against target"
        isLoading
      >
        <span className="sr-only">Loading sales overview chart</span>
      </ChartCard>
    ),
  },
);

const salesSeries: BarChartSeries[] = [
  { key: 'revenue', label: 'Revenue', color: 'hsl(var(--primary))' },
  { key: 'target', label: 'Target', color: 'hsl(38 92% 50%)' },
];

export function DashboardPage() {
  const {
    userName,
    farmCount,
    kpis,
    quickActions,
    harvestOverview,
    salesOverview,
    harvestFooter,
    salesFooter,
    recentHarvests,
    recentSales,
    activities,
    weather,
    currentWeather,
    alerts,
  } = dashboardData;

  const activeWorkers = Number(kpis.find((kpi) => kpi.id === 'workers')?.value ?? 0);
  const openOrders = Number(kpis.find((kpi) => kpi.id === 'orders')?.value ?? 0);

  const salesChartData: BarChartDatum[] = salesOverview.map((point) => ({
    label: point.month,
    values: { revenue: point.revenue, target: point.target },
  }));

  const harvestChartData: LineChartPoint[] = harvestOverview.map((point) => ({
    label: point.day,
    value: point.value,
  }));

  return (
    <PageContainer>
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
        <section className="xl:col-span-12">
          <WelcomeBanner
            userName={userName}
            farmCount={farmCount}
            activeWorkers={activeWorkers}
            openOrders={openOrders}
            recordHref="/harvest"
          />
        </section>

        <section className="xl:col-span-12">
          <KpiGrid kpis={kpis} />
        </section>

        <section className="xl:col-span-12">
          <QuickActions actions={quickActions} />
        </section>

        <section className="xl:col-span-6">
          <HarvestOverviewChart points={harvestChartData} footer={harvestFooter} />
        </section>

        <section className="xl:col-span-6">
          <SalesOverviewChart data={salesChartData} series={salesSeries} footer={salesFooter} />
        </section>

        <section className="xl:col-span-12">
          <RecentHarvestsTable harvests={recentHarvests} />
        </section>

        <section className="xl:col-span-12">
          <RecentSalesTable sales={recentSales} />
        </section>

        <section className="xl:col-span-6">
          <RecentActivities activities={activities} />
        </section>

        <section className="xl:col-span-6">
          <div className="space-y-6">
            <WeatherWidget current={currentWeather} forecast={weather} />
            <AlertsPanel alerts={alerts} />
          </div>
        </section>
      </div>
    </PageContainer>
  );
}
