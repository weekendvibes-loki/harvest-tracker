import { RequiredLabel } from '@/components/forms/required-label';
import { ValidationMessage } from '@/components/forms/validation-message';
import { cn } from '@/lib/utils';

interface FormFieldProps {
  label: string;
  htmlFor?: string;
  required?: boolean;
  hint?: string;
  error?: string;
  children: React.ReactNode;
  className?: string;
}

export function FormField({
  label,
  htmlFor,
  required,
  hint,
  error,
  children,
  className,
}: FormFieldProps) {
  const messageId = error
    ? `${htmlFor ?? label}-error`
    : hint
      ? `${htmlFor ?? label}-hint`
      : undefined;

  return (
    <div className={cn('space-y-1.5', className)}>
      <RequiredLabel htmlFor={htmlFor} required={required}>
        {label}
      </RequiredLabel>
      {children}
      {error ? (
        <ValidationMessage id={messageId} variant="error">
          {error}
        </ValidationMessage>
      ) : hint ? (
        <ValidationMessage id={messageId} variant="hint">
          {hint}
        </ValidationMessage>
      ) : null}
    </div>
  );
}
