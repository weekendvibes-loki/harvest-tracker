import type { LucideIcon } from 'lucide-react';

import type { TrendDirection } from '@/components/cards/stat-card';

export interface DashboardKpi {
  id: string;
  label: string;
  value: string;
  unit?: string;
  icon: LucideIcon;
  delta: {
    value: string;
    direction: TrendDirection;
    label: string;
  };
}

export interface DashboardQuickAction {
  id: string;
  label: string;
  description: string;
  href: string;
  icon: LucideIcon;
}

export interface HarvestDayPoint {
  day: string;
  value: number;
}

export interface SalesMonthPoint {
  month: string;
  revenue: number;
  target: number;
}

export type HarvestStatus = 'Confirmed' | 'Pending' | 'Quality Check' | 'Rejected';

export interface RecentHarvest {
  id: string;
  batch: string;
  farm: string;
  crop: string;
  quantityKg: number;
  date: string;
  status: HarvestStatus;
}

export type SaleStatus = 'Paid' | 'Partially Paid' | 'Pending' | 'Overdue';

export interface RecentSale {
  id: string;
  order: string;
  customer: string;
  amount: number;
  date: string;
  status: SaleStatus;
}

export type ActivityType = 'harvest' | 'sales' | 'worker' | 'expense' | 'system';

export interface ActivityItem {
  id: string;
  type: ActivityType;
  title: string;
  description: string;
  timestamp: string;
}

export type WeatherCondition = 'sunny' | 'partly-cloudy' | 'cloudy' | 'rainy';

export interface WeatherForecast {
  id: string;
  day: string;
  condition: WeatherCondition;
  highC: number;
  lowC: number;
  precipitation: number;
}

export interface CurrentWeather {
  temperatureC: number;
  humidity: number;
  windKph: number;
  condition: WeatherCondition;
}

export type AlertSeverity = 'info' | 'warning' | 'danger' | 'success';

export interface DashboardAlert {
  id: string;
  severity: AlertSeverity;
  title: string;
  description: string;
}

export interface DashboardData {
  userName: string;
  farmCount: number;
  kpis: DashboardKpi[];
  quickActions: DashboardQuickAction[];
  harvestOverview: HarvestDayPoint[];
  salesOverview: SalesMonthPoint[];
  harvestFooter: string;
  salesFooter: string;
  recentHarvests: RecentHarvest[];
  recentSales: RecentSale[];
  activities: ActivityItem[];
  weather: WeatherForecast[];
  currentWeather: CurrentWeather;
  alerts: DashboardAlert[];
}
