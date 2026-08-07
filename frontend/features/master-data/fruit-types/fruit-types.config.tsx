import { Apple } from 'lucide-react';

import type { MasterDataModuleConfig } from '../types/master-data.types';

export const fruitTypesModuleConfig: MasterDataModuleConfig = {
  key: 'fruit-types',
  title: 'Fruit Types',
  description: 'Manage the catalogue of fruit types grown on your farms.',
  singular: 'Fruit Type',
  icon: Apple,
  searchPlaceholder: 'Search fruit types...',
  nameLabel: 'Fruit type name',
  namePlaceholder: 'e.g. Mango',
  nameHint: 'Display name of the fruit.',
  codeLabel: 'Fruit type code',
  codePlaceholder: 'e.g. MANGO',
  codeHint: 'Unique code. Uppercase letters, numbers and underscores.',
  descriptionLabel: 'Description',
  descriptionPlaceholder: 'Short description of the fruit type.',
  addDialogTitle: 'Add Fruit Type',
  addDialogDescription: 'Create a new fruit type for use across the platform.',
  editDialogTitle: 'Edit Fruit Type',
  emptyTitle: 'No fruit types found',
  emptyDescription: 'Add your first fruit type to get started.',
};
