import Link from 'next/link';
import { PackageX } from 'lucide-react';

import { Button } from '@/components/ui/button';

interface NotFoundProps {
  title?: string;
  description?: string;
}

export function NotFound({
  title = 'Page not found',
  description = 'The page you are looking for does not exist or has not been built yet.',
}: NotFoundProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 py-10 text-center">
      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-muted">
        <PackageX className="h-6 w-6 text-muted-foreground" aria-hidden="true" />
      </div>
      <h1 className="mt-2 text-xl font-semibold">{title}</h1>
      <p className="max-w-sm text-sm text-muted-foreground">{description}</p>
      <Button asChild className="mt-4">
        <Link href="/">Back to dashboard</Link>
      </Button>
    </div>
  );
}
