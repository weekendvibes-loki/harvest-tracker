import { PageContainer } from '@/components/layout/page-container';
import { ComingSoon } from '@/components/shared/coming-soon';
import { ContentCard } from '@/components/shared/content-card';
import { PageHeader } from '@/components/shared/page-header';

export default function FarmsPage() {
  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader title="Farms" description="Manage farm locations" />
        <ContentCard>
          <ComingSoon
            title="Farms module coming soon"
            description="Farm location management is planned for a future phase."
          />
        </ContentCard>
      </div>
    </PageContainer>
  );
}
