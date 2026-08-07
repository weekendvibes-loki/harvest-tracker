import { cn } from '@/lib/utils';

interface FormCardProps {
  title?: string;
  description?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
  className?: string;
  bodyClassName?: string;
}

export function FormCard({
  title,
  description,
  children,
  footer,
  className,
  bodyClassName,
}: FormCardProps) {
  return (
    <div className={cn('rounded-lg border bg-card text-card-foreground shadow-sm', className)}>
      {title || description ? (
        <div className="border-b px-5 py-4">
          {title ? <h2 className="text-sm font-semibold">{title}</h2> : null}
          {description ? (
            <p className="mt-0.5 text-sm text-muted-foreground">{description}</p>
          ) : null}
        </div>
      ) : null}
      <div className={cn('p-5', bodyClassName)}>{children}</div>
      {footer ? (
        <div className="flex flex-col-reverse gap-2 border-t px-5 py-3 sm:flex-row sm:justify-end">
          {footer}
        </div>
      ) : null}
    </div>
  );
}
