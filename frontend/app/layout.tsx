import './globals.css';
import { Metadata } from 'next';
import { Providers } from '@/components/providers';
import { Shell } from '@/components/layout/shell';

export const metadata: Metadata = {
  title: {
    default: 'Harvest Tracker',
    template: '%s | Harvest Tracker',
  },
  description: 'Harvest tracking platform for modern farm operations',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <Providers>
          <Shell>{children}</Shell>
        </Providers>
      </body>
    </html>
  );
}
