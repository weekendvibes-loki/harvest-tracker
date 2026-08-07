import * as React from 'react';

import { cn } from '@/lib/utils';

interface RequiredLabelProps extends React.LabelHTMLAttributes<HTMLLabelElement> {
  required?: boolean;
}

const RequiredLabel = React.forwardRef<HTMLLabelElement, RequiredLabelProps>(
  ({ children, required, className, ...props }, ref) => {
    return (
      <label
        ref={ref}
        className={cn(
          'text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70',
          className,
        )}
        {...props}
      >
        {children}
        {required ? (
          <span className="ml-0.5 text-destructive" aria-hidden="true">
            *
          </span>
        ) : null}
        {required ? <span className="sr-only">(required)</span> : null}
      </label>
    );
  },
);
RequiredLabel.displayName = 'RequiredLabel';

export { RequiredLabel };
