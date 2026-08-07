import { PageContainer } from '@/components/layout/page-container';
import { ComingSoon } from '@/components/shared/coming-soon';
import { ContentCard } from '@/components/shared/content-card';
import { PageHeader } from '@/components/shared/page-header';

export default function SettingsPage() {
  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader title="Settings" description="Application configuration" />
        <ContentCard>
          <ComingSoon
            title="Settings module coming soon"
            description="Application configuration is planned for a future phase."
          />
        </ContentCard>
      </div>
    </PageContainer>
  );
}
