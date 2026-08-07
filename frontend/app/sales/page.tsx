import { PageContainer } from '@/components/layout/page-container';
import { ComingSoon } from '@/components/shared/coming-soon';
import { ContentCard } from '@/components/shared/content-card';
import { PageHeader } from '@/components/shared/page-header';

export default function SalesPage() {
  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader title="Sales" description="Sales orders and deliveries" />
        <ContentCard>
          <ComingSoon
            title="Sales module coming soon"
            description="Sales order management is planned for a future phase."
          />
        </ContentCard>
      </div>
    </PageContainer>
  );
}
