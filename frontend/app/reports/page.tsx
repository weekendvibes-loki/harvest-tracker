import { PageContainer } from '@/components/layout/page-container';
import { ComingSoon } from '@/components/shared/coming-soon';
import { ContentCard } from '@/components/shared/content-card';
import { PageHeader } from '@/components/shared/page-header';

export default function ReportsPage() {
  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader title="Reports" description="Insights and reporting" />
        <ContentCard>
          <ComingSoon
            title="Reports module coming soon"
            description="Reporting and insights are planned for a future phase."
          />
        </ContentCard>
      </div>
    </PageContainer>
  );
}
