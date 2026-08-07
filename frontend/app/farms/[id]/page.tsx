import { FarmDetailPage } from '@/features/farms/pages/farm-detail-page';

interface FarmDetailRouteProps {
  params: Promise<{ id: string }>;
}

export default async function FarmDetailRoute({ params }: FarmDetailRouteProps) {
  const { id } = await params;
  return <FarmDetailPage farmId={id} />;
}
