import { PageContainer } from '@/components/layout/page-container';
import { ComingSoon } from '@/components/shared/coming-soon';
import { ContentCard } from '@/components/shared/content-card';
import { PageHeader } from '@/components/shared/page-header';

export default function ExpensesPage() {
  return (
    <PageContainer>
      <div className="space-y-6">
        <PageHeader title="Expenses" description="Operational expenses" />
        <ContentCard>
          <ComingSoon
            title="Expenses module coming soon"
            description="Expense tracking is planned for a future phase."
          />
        </ContentCard>
      </div>
    </PageContainer>
  );
}
