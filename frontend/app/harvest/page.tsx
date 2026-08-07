import { PageContainer } from '@/components/layout/page-container';
import { ComingSoon } from '@/components/shared/coming-soon';
import { ContentCard } from '@/components/shared/content-card';
import { PageHeader } from '@/components/shared/page-header';

export default function HarvestPage() {
  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader title="Harvest" description="Record and track harvests" />
        <ContentCard>
          <ComingSoon
            title="Harvest module coming soon"
            description="Harvest recording and tracking is planned for a future phase."
          />
        </ContentCard>
      </div>
    </PageContainer>
  );
}
