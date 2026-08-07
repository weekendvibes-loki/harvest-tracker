import {
  BarChart3,
  PlusCircle,
  Receipt,
  ShoppingCart,
  Sprout,
  TrendingUp,
  Users,
  Wallet,
  Wheat,
} from 'lucide-react';

import type { DashboardData } from '../types/dashboard.types';

export const dashboardData: DashboardData = {
  userName: 'Farm Manager',
  farmCount: 6,

  kpis: [
    {
      id: 'revenue',
      label: 'Total Revenue',
      value: '₹8,42,500',
      unit: 'this season',
      icon: Wallet,
      delta: {
        value: '+12.4%',
        direction: 'up',
        label: 'vs last season',
      },
    },
    {
      id: 'harvest',
      label: 'Total Harvest',
      value: '12,450 KG',
      unit: 'this season',
      icon: Sprout,
      delta: {
        value: '+8.2%',
        direction: 'up',
        label: 'vs last season',
      },
    },
    {
      id: 'profit',
      label: 'Net Profit',
      value: '₹6,56,100',
      unit: 'this season',
      icon: TrendingUp,
      delta: {
        value: '+14.6%',
        direction: 'up',
        label: 'vs last season',
      },
    },
    {
      id: 'workers',
      label: 'Active Workers',
      value: '34',
      unit: 'today',
      icon: Users,
      delta: {
        value: '-2',
        direction: 'down',
        label: 'vs yesterday',
      },
    },
    {
      id: 'orders',
      label: 'Open Orders',
      value: '18',
      unit: 'awaiting dispatch',
      icon: ShoppingCart,
      delta: {
        value: 'Unchanged',
        direction: 'neutral',
        label: 'vs yesterday',
      },
    },
    {
      id: 'expenses',
      label: 'Expenses',
      value: '₹1,86,400',
      unit: 'this month',
      icon: Receipt,
      delta: {
        value: 'On budget',
        direction: 'neutral',
        label: 'this month',
      },
    },
  ],

  quickActions: [
    {
      id: 'record-harvest',
      label: 'Record Harvest',
      description: 'Log a new harvest batch',
      href: '/harvest',
      icon: PlusCircle,
    },
    {
      id: 'manage-farms',
      label: 'Manage Farms',
      description: 'Update farm locations',
      href: '/farms',
      icon: Wheat,
    },
    {
      id: 'sales-orders',
      label: 'Sales Orders',
      description: 'Create a sales order',
      href: '/sales',
      icon: ShoppingCart,
    },
    {
      id: 'track-workers',
      label: 'Track Workers',
      description: 'Review worker attendance',
      href: '/workers',
      icon: Users,
    },
    {
      id: 'log-expense',
      label: 'Log Expense',
      description: 'Record an operational expense',
      href: '/expenses',
      icon: Receipt,
    },
    {
      id: 'view-reports',
      label: 'View Reports',
      description: 'Open reporting and insights',
      href: '/reports',
      icon: BarChart3,
    },
  ],

  harvestOverview: [
    { day: 'Mon', value: 480 },
    { day: 'Tue', value: 620 },
    { day: 'Wed', value: 540 },
    { day: 'Thu', value: 710 },
    { day: 'Fri', value: 660 },
    { day: 'Sat', value: 780 },
    { day: 'Sun', value: 720 },
  ],

  harvestFooter: 'Total this week: 4,510 KG · +8.2% vs last week',

  salesOverview: [
    { month: 'Jul', revenue: 95000, target: 100000 },
    { month: 'Aug', revenue: 120000, target: 110000 },
    { month: 'Sep', revenue: 145000, target: 130000 },
    { month: 'Oct', revenue: 132000, target: 135000 },
    { month: 'Nov', revenue: 165000, target: 140000 },
    { month: 'Dec', revenue: 185000, target: 150000 },
  ],

  salesFooter: 'Nov 2026 exceeded target by 18% · 6-month revenue ₹8,42,000',

  recentHarvests: [
    {
      id: 'hv-18',
      batch: 'HV-2026-018',
      farm: 'Devgad Mango Orchard',
      crop: 'Alphonso Mango',
      quantityKg: 520.0,
      date: '2026-08-06',
      status: 'Confirmed',
    },
    {
      id: 'hv-17',
      batch: 'HV-2026-017',
      farm: 'Kesar Farm',
      crop: 'Kesar Mango',
      quantityKg: 340.5,
      date: '2026-08-05',
      status: 'Quality Check',
    },
    {
      id: 'hv-16',
      batch: 'HV-2026-016',
      farm: 'Ratnagiri Orchard',
      crop: 'Alphonso Mango',
      quantityKg: 610.0,
      date: '2026-08-04',
      status: 'Confirmed',
    },
    {
      id: 'hv-15',
      batch: 'HV-2026-015',
      farm: 'Green Valley Farm',
      crop: 'Guava',
      quantityKg: 275.25,
      date: '2026-08-03',
      status: 'Pending',
    },
    {
      id: 'hv-14',
      batch: 'HV-2026-014',
      farm: 'Hilltop Orchard',
      crop: 'Coconut',
      quantityKg: 180.0,
      date: '2026-08-02',
      status: 'Rejected',
    },
  ],

  recentSales: [
    {
      id: 'so-81',
      order: 'SO-2026-081',
      customer: 'FreshMart Retail',
      amount: 185000,
      date: '2026-08-06',
      status: 'Paid',
    },
    {
      id: 'so-80',
      order: 'SO-2026-080',
      customer: 'Mumbai Fruit Hub',
      amount: 96500,
      date: '2026-08-05',
      status: 'Partially Paid',
    },
    {
      id: 'so-79',
      order: 'SO-2026-079',
      customer: 'GreenLeaf Exports',
      amount: 240000,
      date: '2026-08-05',
      status: 'Pending',
    },
    {
      id: 'so-78',
      order: 'SO-2026-078',
      customer: 'City Superstore',
      amount: 120750,
      date: '2026-08-04',
      status: 'Paid',
    },
    {
      id: 'so-77',
      order: 'SO-2026-077',
      customer: 'Sunrise Distributors',
      amount: 75000,
      date: '2026-08-02',
      status: 'Overdue',
    },
  ],

  activities: [
    {
      id: 'act-6',
      type: 'harvest',
      title: 'Harvest HV-2026-018 recorded',
      description: '520 kg of Alphonso mango harvested from Devgad Mango Orchard.',
      timestamp: '2h ago',
    },
    {
      id: 'act-5',
      type: 'sales',
      title: 'Order SO-2026-081 marked as paid',
      description: 'Settlement of ₹1,85,000 received from FreshMart Retail.',
      timestamp: '3h ago',
    },
    {
      id: 'act-4',
      type: 'worker',
      title: 'Attendance updated',
      description: '4 workers added to the Ratnagiri Orchard morning shift.',
      timestamp: '5h ago',
    },
    {
      id: 'act-3',
      type: 'expense',
      title: 'Expense logged',
      description: 'Fertilizer purchase of ₹8,400 recorded under orchard supplies.',
      timestamp: '8h ago',
    },
    {
      id: 'act-2',
      type: 'system',
      title: 'Quality check pending',
      description: 'Harvest HV-2026-017 is awaiting inspection approval.',
      timestamp: '10h ago',
    },
    {
      id: 'act-1',
      type: 'harvest',
      title: 'Harvest HV-2026-016 confirmed',
      description: '610 kg of Alphonso mango confirmed from Ratnagiri Orchard.',
      timestamp: 'Yesterday',
    },
  ],

  currentWeather: {
    temperatureC: 31,
    humidity: 62,
    windKph: 8,
    condition: 'sunny',
  },

  weather: [
    { id: 'w-1', day: 'Today', condition: 'sunny', highC: 31, lowC: 24, precipitation: 10 },
    { id: 'w-2', day: 'Tomorrow', condition: 'partly-cloudy', highC: 30, lowC: 23, precipitation: 20 },
    { id: 'w-3', day: 'Sat', condition: 'rainy', highC: 28, lowC: 22, precipitation: 70 },
    { id: 'w-4', day: 'Sun', condition: 'cloudy', highC: 29, lowC: 23, precipitation: 40 },
    { id: 'w-5', day: 'Mon', condition: 'partly-cloudy', highC: 31, lowC: 24, precipitation: 15 },
  ],

  alerts: [
    {
      id: 'alert-3',
      severity: 'warning',
      title: 'Low stock: Fertilizer',
      description: 'Fertilizer A blend is below 15% of the reorder level.',
    },
    {
      id: 'alert-2',
      severity: 'danger',
      title: 'Overdue payment',
      description: 'Order SO-2026-077 from Sunrise Distributors is 5 days overdue.',
    },
    {
      id: 'alert-1',
      severity: 'info',
      title: 'Quality check pending',
      description: 'Harvest HV-2026-017 is awaiting inspection approval.',
    },
    {
      id: 'alert-0',
      severity: 'success',
      title: 'Target achieved',
      description: 'November sales exceeded the monthly target by 18%.',
    },
  ],
};
