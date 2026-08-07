import { Receipt } from 'lucide-react';

import type { MasterDataModuleConfig } from '../types/master-data.types';

export const expenseCategoriesModuleConfig: MasterDataModuleConfig = {
  key: 'expense-categories',
  title: 'Expense Categories',
  description: 'Manage the categories used to classify operational expenses.',
  singular: 'Expense Category',
  icon: Receipt,
  searchPlaceholder: 'Search expense categories...',
  nameLabel: 'Category name',
  namePlaceholder: 'e.g. Fertilizer',
  nameHint: 'Display name of the expense category.',
  codeLabel: 'Category code',
  codePlaceholder: 'e.g. FERTILIZER',
  codeHint: 'Unique code. Uppercase letters, numbers and underscores.',
  descriptionLabel: 'Description',
  descriptionPlaceholder: 'Short description of the category.',
  addDialogTitle: 'Add Expense Category',
  addDialogDescription: 'Create a new expense category for cost tracking.',
  editDialogTitle: 'Edit Expense Category',
  emptyTitle: 'No expense categories found',
  emptyDescription: 'Add your first expense category to get started.',
};
