'use client';

import { usePathname } from 'next/navigation';
import { motion } from 'framer-motion';

export function ContentWrapper({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();

  return (
    <motion.main
      key={pathname}
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.2, ease: 'easeOut' }}
      className="flex-1"
    >
      <div className="px-4 py-6 sm:px-6 lg:px-8">{children}</div>
    </motion.main>
  );
}
