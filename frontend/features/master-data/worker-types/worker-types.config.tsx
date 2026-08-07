import { HardHat } from 'lucide-react';

import type { MasterDataModuleConfig } from '../types/master-data.types';

export const workerTypesModuleConfig: MasterDataModuleConfig = {
  key: 'worker-types',
  title: 'Worker Types',
  description: 'Manage the employment types used for field workers.',
  singular: 'Worker Type',
  icon: HardHat,
  searchPlaceholder: 'Search worker types...',
  nameLabel: 'Worker type name',
  namePlaceholder: 'e.g. Full Time',
  nameHint: 'Display name of the worker type.',
  codeLabel: 'Worker type code',
  codePlaceholder: 'e.g. FULL_TIME',
  codeHint: 'Unique code. Uppercase letters, numbers and underscores.',
  descriptionLabel: 'Description',
  descriptionPlaceholder: 'Short description of the worker type.',
  addDialogTitle: 'Add Worker Type',
  addDialogDescription: 'Create a new worker type for attendance and payroll.',
  editDialogTitle: 'Edit Worker Type',
  emptyTitle: 'No worker types found',
  emptyDescription: 'Add your first worker type to get started.',
};
