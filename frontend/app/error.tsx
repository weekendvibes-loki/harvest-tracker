'use client';

import { ErrorState } from '@/components/shared/error-state';

export default function Error({ reset }: { reset: () => void }) {
  return (
    <ErrorState
      title="Something went wrong"
      message="An unexpected error occurred while rendering this page."
      onRetry={() => reset()}
    />
  );
}
