import { AppShell } from '@/components/layout/app-shell';
import { TooltipProvider } from '@/components/ui/tooltip';

export function Shell({ children }: { children: React.ReactNode }) {
  return (
    <TooltipProvider>
      <AppShell>{children}</AppShell>
    </TooltipProvider>
  );
}
