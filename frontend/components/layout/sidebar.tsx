'use client';

import Link from 'next/link';
import type { Route } from 'next';
import { usePathname } from 'next/navigation';
import { AnimatePresence, motion } from 'framer-motion';
import { Sprout, X } from 'lucide-react';

import { useShell } from '@/components/layout/app-shell';
import { Button } from '@/components/ui/button';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { navGroups, type NavItem } from '@/lib/navigation';
import { cn } from '@/lib/utils';

const COLLAPSED_WIDTH = 68;
const EXPANDED_WIDTH = 256;

function isActive(pathname: string, href: string): boolean {
  if (href === '/') {
    return pathname === '/';
  }
  return pathname === href || pathname.startsWith(`${href}/`);
}

interface NavLinkProps {
  item: NavItem;
  collapsed: boolean;
  onNavigate?: () => void;
}

function NavLink({ item, collapsed, onNavigate }: NavLinkProps) {
  const pathname = usePathname();
  const active = isActive(pathname, item.href);
  const { icon: Icon } = item;

  const link = (
    <Link
      href={item.href as Route}
      onClick={onNavigate}
      aria-label={item.title}
      aria-current={active ? 'page' : undefined}
      title={collapsed ? item.title : undefined}
      className={cn(
        'group flex items-center rounded-md text-sm font-medium transition-colors',
        collapsed ? 'h-10 w-10 justify-center' : 'h-9 px-3',
        active
          ? 'bg-accent text-accent-foreground'
          : 'text-muted-foreground hover:bg-accent/60 hover:text-accent-foreground',
      )}
    >
      <Icon
        className={cn('h-4 w-4 shrink-0', active ? 'text-primary' : 'text-muted-foreground')}
        aria-hidden="true"
      />
      <span
        className={cn(
          'truncate transition-all duration-200',
          collapsed ? 'w-0 opacity-0' : 'ml-3 opacity-100',
        )}
      >
        {item.title}
      </span>
    </Link>
  );

  if (!collapsed) {
    return link;
  }

  return (
    <Tooltip delayDuration={100}>
      <TooltipTrigger asChild>{link}</TooltipTrigger>
      <TooltipContent side="right">{item.title}</TooltipContent>
    </Tooltip>
  );
}

interface SidebarContentProps {
  collapsed: boolean;
  onNavigate?: () => void;
}

function SidebarContent({ collapsed, onNavigate }: SidebarContentProps) {
  return (
    <div className="flex h-full flex-col">
      <div
        className={cn(
          'flex h-14 shrink-0 items-center border-b',
          collapsed ? 'justify-center px-2' : 'px-4',
        )}
      >
        <Link
          href="/"
          onClick={onNavigate}
          aria-label="Harvest Tracker home"
          className="flex items-center overflow-hidden"
        >
          <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-md bg-primary text-primary-foreground">
            <Sprout className="h-4 w-4" aria-hidden="true" />
          </span>
          <span
            className={cn(
              'truncate text-sm font-semibold transition-all duration-200',
              collapsed ? 'w-0 opacity-0' : 'ml-2 opacity-100',
            )}
          >
            Harvest Tracker
          </span>
        </Link>
      </div>

      <nav aria-label="Main navigation" className="flex-1 overflow-y-auto px-3 py-4">
        <ul className="flex flex-col gap-6">
          {navGroups.map((group) => (
            <li key={group.label}>
              {!collapsed && (
                <p className="mb-2 px-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  {group.label}
                </p>
              )}
              <ul className={cn('flex flex-col gap-1', collapsed && 'items-center')}>
                {group.items.map((item) => (
                  <li key={item.href}>
                    <NavLink item={item} collapsed={collapsed} onNavigate={onNavigate} />
                  </li>
                ))}
              </ul>
            </li>
          ))}
        </ul>
      </nav>

      <div className="shrink-0 border-t p-3">
        <div
          className={cn(
            'flex items-center rounded-md px-2 py-2 text-xs text-muted-foreground',
            collapsed ? 'justify-center' : 'gap-2',
          )}
          title={collapsed ? 'System operational' : undefined}
        >
          <span className="h-2 w-2 shrink-0 rounded-full bg-emerald-500" aria-hidden="true" />
          {!collapsed && <span className="truncate">System operational</span>}
        </div>
      </div>
    </div>
  );
}

function DesktopSidebar() {
  const { collapsed } = useShell();

  return (
    <motion.aside
      initial={false}
      animate={{ width: collapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH }}
      transition={{ duration: 0.2, ease: 'easeInOut' }}
      className="sticky top-0 hidden h-screen shrink-0 overflow-hidden border-r bg-card lg:block"
    >
      <SidebarContent collapsed={collapsed} />
    </motion.aside>
  );
}

function MobileSidebar() {
  const { mobileOpen, setMobileOpen } = useShell();

  return (
    <AnimatePresence>
      {mobileOpen && (
        <div className="lg:hidden">
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.2 }}
            className="fixed inset-0 z-40 bg-background/70 backdrop-blur-sm"
            onClick={() => setMobileOpen(false)}
            aria-hidden="true"
          />
          <motion.aside
            initial={{ x: '-100%' }}
            animate={{ x: 0 }}
            exit={{ x: '-100%' }}
            transition={{ duration: 0.2, ease: 'easeInOut' }}
            className="fixed inset-y-0 left-0 z-50 flex w-72 flex-col border-r bg-card shadow-xl"
            role="dialog"
            aria-modal="true"
            aria-label="Mobile navigation"
          >
            <Button
              variant="ghost"
              size="icon"
              className="absolute right-3 top-3 z-10"
              onClick={() => setMobileOpen(false)}
              aria-label="Close navigation"
              autoFocus
            >
              <X className="h-4 w-4" aria-hidden="true" />
            </Button>
            <SidebarContent collapsed={false} onNavigate={() => setMobileOpen(false)} />
          </motion.aside>
        </div>
      )}
    </AnimatePresence>
  );
}

export function Sidebar() {
  return (
    <>
      <DesktopSidebar />
      <MobileSidebar />
    </>
  );
}
