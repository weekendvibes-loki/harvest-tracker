import { cn } from '@/lib/utils';

interface ContentCardProps {
  title?: string;
  description?: string;
  children?: React.ReactNode;
  className?: string;
  bodyClassName?: string;
}

export function ContentCard({
  title,
  description,
  children,
  className,
  bodyClassName,
}: ContentCardProps) {
  return (
    <div
      className={cn(
        'rounded-lg border bg-card text-card-foreground shadow-sm',
        className,
      )}
    >
      {title || description ? (
        <div className="border-b px-5 py-3">
          {title ? <h3 className="text-sm font-semibold">{title}</h3> : null}
          {description ? (
            <p className="mt-0.5 text-xs text-muted-foreground">{description}</p>
          ) : null}
        </div>
      ) : null}
      {children ? <div className={cn('p-5', bodyClassName)}>{children}</div> : null}
    </div>
  );
}
