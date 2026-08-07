import type { LucideIcon } from 'lucide-react';

export const RECORD_STATUSES = ['ACTIVE', 'INACTIVE'] as const;

export type RecordStatus = (typeof RECORD_STATUSES)[number];

export const MASTER_DATA_MODULE_KEYS = [
  'fruit-types',
  'crop-variants',
  'units',
  'worker-types',
  'payment-methods',
  'expense-categories',
] as const;

export type MasterDataModuleKey = (typeof MASTER_DATA_MODULE_KEYS)[number];

export interface MasterDataRecord {
  id: string;
  name: string;
  code: string;
  description: string;
  status: RecordStatus;
  createdAt: string;
  updatedAt: string;
}

export interface MasterDataCreateInput {
  name: string;
  code: string;
  description: string;
  status: RecordStatus;
}

export interface MasterDataUpdateInput extends MasterDataCreateInput {}

export interface MasterDataModuleConfig {
  key: MasterDataModuleKey;
  title: string;
  description: string;
  singular: string;
  icon: LucideIcon;
  searchPlaceholder: string;
  nameLabel: string;
  namePlaceholder: string;
  nameHint: string;
  codeLabel: string;
  codePlaceholder: string;
  codeHint: string;
  descriptionLabel: string;
  descriptionPlaceholder: string;
  addDialogTitle: string;
  addDialogDescription: string;
  editDialogTitle: string;
  emptyTitle: string;
  emptyDescription: string;
}
