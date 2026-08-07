import { Separator } from '@/components/ui/separator';

export function Footer() {
  return (
    <footer className="mt-auto">
      <Separator />
      <div className="px-4 py-3 sm:px-6 lg:px-8">
        <div className="flex flex-col items-start justify-between gap-1 text-xs text-muted-foreground sm:flex-row sm:items-center">
          <p>&copy; Harvest Tracker. All rights reserved.</p>
          <p>Enterprise Application Shell</p>
        </div>
      </div>
    </footer>
  );
}
