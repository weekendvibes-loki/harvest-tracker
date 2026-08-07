import { Construction, type LucideIcon } from 'lucide-react';

interface ComingSoonProps {
  title?: string;
  description?: string;
  icon?: LucideIcon;
}

export function ComingSoon({
  title = 'Coming soon',
  description = 'This module is planned for a future phase.',
  icon: Icon = Construction,
}: ComingSoonProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 py-10 text-center">
      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-muted">
        <Icon className="h-6 w-6 text-muted-foreground" aria-hidden="true" />
      </div>
      <h2 className="text-base font-semibold">{title}</h2>
      <p className="max-w-sm text-sm text-muted-foreground">{description}</p>
    </div>
  );
}
