import { AlertCircle, CheckCircle2, Info, type LucideIcon } from 'lucide-react';

import { cn } from '@/lib/utils';

export type ValidationVariant = 'error' | 'success' | 'hint';

interface ValidationMessageProps {
  children: React.ReactNode;
  variant?: ValidationVariant;
  id?: string;
  icon?: LucideIcon;
  className?: string;
}

const variantConfig: Record<
  ValidationVariant,
  { className: string; icon: LucideIcon; role?: 'alert' | 'status' }
> = {
  error: { className: 'text-destructive', icon: AlertCircle, role: 'alert' },
  success: {
    className: 'text-emerald-600 dark:text-emerald-400',
    icon: CheckCircle2,
    role: 'status',
  },
  hint: { className: 'text-muted-foreground', icon: Info },
};

export function ValidationMessage({
  children,
  variant = 'hint',
  id,
  icon,
  className,
}: ValidationMessageProps) {
  const config = variantConfig[variant];
  const Icon = icon ?? config.icon;

  return (
    <p
      id={id}
      role={config.role}
      className={cn('flex items-start gap-1.5 text-xs leading-relaxed', config.className, className)}
    >
      <Icon className="mt-px h-3.5 w-3.5 shrink-0" aria-hidden="true" />
      <span>{children}</span>
    </p>
  );
}
