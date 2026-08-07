import { PageContainer } from '@/components/layout/page-container';
import { ComingSoon } from '@/components/shared/coming-soon';
import { ContentCard } from '@/components/shared/content-card';
import { PageHeader } from '@/components/shared/page-header';

export default function WorkersPage() {
  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader title="Workers" description="Manage field workers" />
        <ContentCard>
          <ComingSoon
            title="Workers module coming soon"
            description="Worker management is planned for a future phase."
          />
        </ContentCard>
      </div>
    </PageContainer>
  );
}
