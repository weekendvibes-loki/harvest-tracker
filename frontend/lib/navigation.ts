import type { LucideIcon } from 'lucide-react';
import {
  BarChart3,
  LayoutDashboard,
  Receipt,
  Settings,
  ShoppingCart,
  Tractor,
  Users,
  Wheat,
} from 'lucide-react';

export interface NavItem {
  title: string;
  href: string;
  icon: LucideIcon;
  description: string;
}

export interface NavGroup {
  label: string;
  items: NavItem[];
}

export const navGroups: NavGroup[] = [
  {
    label: 'Overview',
    items: [
      {
        title: 'Dashboard',
        href: '/',
        icon: LayoutDashboard,
        description: 'Operational overview and key metrics',
      },
    ],
  },
  {
    label: 'Operations',
    items: [
      {
        title: 'Farms',
        href: '/farms',
        icon: Tractor,
        description: 'Manage farm locations',
      },
      {
        title: 'Harvest',
        href: '/harvest',
        icon: Wheat,
        description: 'Record and track harvests',
      },
      {
        title: 'Workers',
        href: '/workers',
        icon: Users,
        description: 'Manage field workers',
      },
    ],
  },
  {
    label: 'Business',
    items: [
      {
        title: 'Sales',
        href: '/sales',
        icon: ShoppingCart,
        description: 'Sales orders and deliveries',
      },
      {
        title: 'Expenses',
        href: '/expenses',
        icon: Receipt,
        description: 'Operational expenses',
      },
      {
        title: 'Reports',
        href: '/reports',
        icon: BarChart3,
        description: 'Insights and reporting',
      },
    ],
  },
  {
    label: 'Administration',
    items: [
      {
        title: 'Settings',
        href: '/settings',
        icon: Settings,
        description: 'Application configuration',
      },
    ],
  },
];

export const navItems: NavItem[] = navGroups.flatMap((group) => group.items);

const DEFAULT_TITLE = 'Harvest Tracker';

export function getNavTitle(href: string): string {
  const exact = navItems.find((item) => item.href === href);
  if (exact) return exact.title;

  const parts = href.split('/').filter(Boolean);
  if (parts.length === 0) return 'Dashboard';

  const last = parts[parts.length - 1];
  const partial = navItems.find((item) => item.href === `/${last}`);
  if (partial) return partial.title;

  return last.charAt(0).toUpperCase() + last.slice(1);
}

export { DEFAULT_TITLE };
