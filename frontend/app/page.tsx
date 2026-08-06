import { PageContainer } from '@/components/layout/page-container';

export default function HomePage() {
  return (
    <PageContainer>
      <div className="space-y-4">
        <h1 className="text-2xl font-semibold">Harvest Tracker</h1>
        <p className="text-sm text-muted-foreground">
          Project scaffold is ready for future phase implementation.
        </p>
      </div>
    </PageContainer>
  );
}
