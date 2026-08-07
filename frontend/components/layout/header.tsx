'use client';

import Link from 'next/link';
import type { Route } from 'next';
import { usePathname } from 'next/navigation';
import { useTheme } from 'next-themes';
import { useSyncExternalStore } from 'react';
import { Bell, ChevronDown, ChevronRight, Menu, Monitor, Moon, PanelLeftClose, PanelLeftOpen, Search, Sun, type LucideIcon } from 'lucide-react';

import { useShell } from '@/components/layout/app-shell';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Input } from '@/components/ui/input';
import { getNavTitle } from '@/lib/navigation';
import { cn } from '@/lib/utils';

interface Crumb {
  href: string;
  label: string;
}

function useBreadcrumbs(): Crumb[] {
  const pathname = usePathname();
  const segments = pathname.split('/').filter(Boolean);

  if (segments.length === 0) {
    return [{ href: '/', label: 'Dashboard' }];
  }

  const crumbs: Crumb[] = [{ href: '/', label: 'Dashboard' }];
  let acc = '';
  for (const segment of segments) {
    acc += `/${segment}`;
    crumbs.push({ href: acc, label: getNavTitle(acc) });
  }
  return crumbs;
}

function SearchBox() {
  return (
    <div className="relative hidden md:block">
      <Search
        className="pointer-events-none absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
        aria-hidden="true"
      />
      <Input
        type="search"
        placeholder="Search..."
        aria-label="Search"
        className="h-9 w-48 pl-8 lg:w-64"
      />
    </div>
  );
}

interface ThemeOptionProps {
  icon: LucideIcon;
  label: string;
  active: boolean;
  onSelect: () => void;
}

function ThemeOption({ icon: Icon, label, active, onSelect }: ThemeOptionProps) {
  return (
    <DropdownMenuItem onSelect={onSelect} className={cn(active && 'bg-accent text-accent-foreground')}>
      <Icon className="h-4 w-4" aria-hidden="true" />
      <span>{label}</span>
    </DropdownMenuItem>
  );
}

const emptySubscribe = () => () => {};

function useHydrated(): boolean {
  return useSyncExternalStore(
    emptySubscribe,
    () => true,
    () => false,
  );
}

function ThemeToggle() {
  const { theme, resolvedTheme, setTheme } = useTheme();
  const hydrated = useHydrated();

  const isDark = resolvedTheme === 'dark';
  const CurrentIcon = !hydrated ? Monitor : isDark ? Moon : Sun;

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" aria-label="Change theme">
          <CurrentIcon className="h-4 w-4" aria-hidden="true" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuLabel>Theme</DropdownMenuLabel>
        <DropdownMenuSeparator />
        <ThemeOption
          icon={Sun}
          label="Light"
          active={theme === 'light'}
          onSelect={() => setTheme('light')}
        />
        <ThemeOption
          icon={Moon}
          label="Dark"
          active={theme === 'dark'}
          onSelect={() => setTheme('dark')}
        />
        <ThemeOption
          icon={Monitor}
          label="System"
          active={theme === 'system'}
          onSelect={() => setTheme('system')}
        />
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

function Notifications() {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" aria-label="Notifications" className="relative">
          <Bell className="h-4 w-4" aria-hidden="true" />
          <span
            className="absolute right-1.5 top-1.5 h-2 w-2 rounded-full bg-primary"
            aria-hidden="true"
          />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-80">
        <DropdownMenuLabel>Notifications</DropdownMenuLabel>
        <DropdownMenuSeparator />
        <div className="flex flex-col items-center gap-2 py-8 text-center">
          <Bell className="h-8 w-8 text-muted-foreground" aria-hidden="true" />
          <p className="text-sm text-muted-foreground">No new notifications</p>
        </div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

function ProfileMenu() {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="h-9 gap-2 px-1.5" aria-label="Account menu">
          <Avatar className="h-8 w-8">
            <AvatarFallback className="bg-primary/10 text-xs font-semibold text-primary">
              HT
            </AvatarFallback>
          </Avatar>
          <span className="hidden text-sm font-medium md:inline">Farm Admin</span>
          <ChevronDown className="hidden h-4 w-4 text-muted-foreground md:inline" aria-hidden="true" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-56">
        <DropdownMenuLabel>
          <p className="text-sm font-semibold">Farm Admin</p>
          <p className="text-xs font-normal text-muted-foreground">admin@harvesttracker.app</p>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuItem disabled>Profile</DropdownMenuItem>
        <DropdownMenuItem asChild>
          <Link href="/settings">Settings</Link>
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem disabled className="text-destructive focus:text-destructive">
          Sign out
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

export function Header() {
  const { collapsed, toggleCollapsed, setMobileOpen } = useShell();
  const breadcrumbs = useBreadcrumbs();
  const pageTitle = breadcrumbs[breadcrumbs.length - 1].label;

  return (
    <header className="sticky top-0 z-30 border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/80">
      <div className="flex h-14 items-center justify-between gap-4 px-4 sm:px-6 lg:px-8">
        <div className="flex min-w-0 items-center gap-2">
          <Button
            variant="ghost"
            size="icon"
            className="lg:hidden"
            onClick={() => setMobileOpen(true)}
            aria-label="Open navigation"
          >
            <Menu className="h-4 w-4" aria-hidden="true" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            className="hidden lg:inline-flex"
            onClick={toggleCollapsed}
            aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          >
            {collapsed ? (
              <PanelLeftOpen className="h-4 w-4" aria-hidden="true" />
            ) : (
              <PanelLeftClose className="h-4 w-4" aria-hidden="true" />
            )}
          </Button>

          <div className="min-w-0">
            <nav aria-label="Breadcrumb" className="hidden sm:block">
              <ol className="flex items-center gap-1 text-xs text-muted-foreground">
                {breadcrumbs.map((crumb, index) => {
                  const isLast = index === breadcrumbs.length - 1;
                  return (
                    <li key={crumb.href} className="flex items-center gap-1">
                      {index > 0 && (
                        <ChevronRight className="h-3 w-3" aria-hidden="true" />
                      )}
                      {isLast ? (
                        <span aria-current="page" className="font-medium text-foreground">
                          {crumb.label}
                        </span>
                      ) : (
                        <Link href={crumb.href as Route} className="transition-colors hover:text-foreground">
                          {crumb.label}
                        </Link>
                      )}
                    </li>
                  );
                })}
              </ol>
            </nav>
            <h1 className="truncate text-base font-semibold leading-tight">{pageTitle}</h1>
          </div>
        </div>

        <div className="flex items-center gap-1 sm:gap-2">
          <SearchBox />
          <ThemeToggle />
          <Notifications />
          <ProfileMenu />
        </div>
      </div>
    </header>
  );
}
