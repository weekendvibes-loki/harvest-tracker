import { Footer } from '@/components/layout/footer';
import { Header } from '@/components/layout/header';
import { Sidebar } from '@/components/layout/sidebar';

export function Shell({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-900">
      <div className="flex min-h-screen flex-col">
        <Header />
        <div className="flex flex-1">
          <Sidebar />
          <main className="flex-1 p-6">{children}</main>
        </div>
        <Footer />
      </div>
    </div>
  );
}
