'use client';

import { Drawer } from '@/components/dialogs/drawer';

interface SidePanelProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  title: string;
  description?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
  className?: string;
}

export function SidePanel({
  open,
  onOpenChange,
  title,
  description,
  children,
  footer,
  className,
}: SidePanelProps) {
  return (
    <Drawer
      open={open}
      onOpenChange={onOpenChange}
      title={title}
      description={description}
      side="right"
      size="md"
      footer={footer}
      className={className}
    >
      {children}
    </Drawer>
  );
}
