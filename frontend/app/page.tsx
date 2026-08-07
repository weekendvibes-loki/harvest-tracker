import { PageContainer } from '@/components/layout/page-container';
import { ContentCard } from '@/components/shared/content-card';
import { EmptyState } from '@/components/shared/empty-state';
import { PageHeader } from '@/components/shared/page-header';

export default function HomePage() {
  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader
          title="Dashboard"
          description="Operational overview and key metrics"
        />
        <ContentCard>
          <EmptyState
            title="Welcome to Harvest Tracker"
            description="Key performance indicators and farm activity will appear here in a future phase."
          />
        </ContentCard>
      </div>
    </PageContainer>
  );
}
