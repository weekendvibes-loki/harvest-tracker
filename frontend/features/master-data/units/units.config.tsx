import { Scale } from 'lucide-react';

import type { MasterDataModuleConfig } from '../types/master-data.types';

export const unitsModuleConfig: MasterDataModuleConfig = {
  key: 'units',
  title: 'Units of Measure',
  description: 'Manage measurement units used for produce and materials.',
  singular: 'Unit of Measure',
  icon: Scale,
  searchPlaceholder: 'Search units...',
  nameLabel: 'Unit name',
  namePlaceholder: 'e.g. Kilogram',
  nameHint: 'Full display name of the unit.',
  codeLabel: 'Unit code',
  codePlaceholder: 'e.g. KG',
  codeHint: 'Short unique code. Uppercase letters, numbers and underscores.',
  descriptionLabel: 'Description',
  descriptionPlaceholder: 'Short description of the unit.',
  addDialogTitle: 'Add Unit of Measure',
  addDialogDescription: 'Create a new unit of measure for pricing and quantities.',
  editDialogTitle: 'Edit Unit of Measure',
  emptyTitle: 'No units found',
  emptyDescription: 'Add your first unit of measure to get started.',
};
