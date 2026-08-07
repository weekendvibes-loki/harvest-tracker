import { Sprout } from 'lucide-react';

import type { MasterDataModuleConfig } from '../types/master-data.types';

export const cropVariantsModuleConfig: MasterDataModuleConfig = {
  key: 'crop-variants',
  title: 'Crop Variants',
  description: 'Manage varieties that belong to each fruit type.',
  singular: 'Crop Variant',
  icon: Sprout,
  searchPlaceholder: 'Search crop variants...',
  nameLabel: 'Crop variant name',
  namePlaceholder: 'e.g. Alphonso',
  nameHint: 'Common name of the variety.',
  codeLabel: 'Crop variant code',
  codePlaceholder: 'e.g. ALPHONSO',
  codeHint: 'Unique code. Uppercase letters, numbers and underscores.',
  descriptionLabel: 'Description',
  descriptionPlaceholder: 'Short description of the variety.',
  addDialogTitle: 'Add Crop Variant',
  addDialogDescription: 'Create a new crop variant for use in harvest records.',
  editDialogTitle: 'Edit Crop Variant',
  emptyTitle: 'No crop variants found',
  emptyDescription: 'Add your first crop variant to get started.',
};
