import { CreditCard } from 'lucide-react';

import type { MasterDataModuleConfig } from '../types/master-data.types';

export const paymentMethodsModuleConfig: MasterDataModuleConfig = {
  key: 'payment-methods',
  title: 'Payment Methods',
  description: 'Manage the payment methods accepted for sales and expenses.',
  singular: 'Payment Method',
  icon: CreditCard,
  searchPlaceholder: 'Search payment methods...',
  nameLabel: 'Payment method name',
  namePlaceholder: 'e.g. UPI',
  nameHint: 'Display name of the payment method.',
  codeLabel: 'Payment method code',
  codePlaceholder: 'e.g. UPI',
  codeHint: 'Unique code. Uppercase letters, numbers and underscores.',
  descriptionLabel: 'Description',
  descriptionPlaceholder: 'Short description of the payment method.',
  addDialogTitle: 'Add Payment Method',
  addDialogDescription: 'Create a new payment method for order settlements.',
  editDialogTitle: 'Edit Payment Method',
  emptyTitle: 'No payment methods found',
  emptyDescription: 'Add your first payment method to get started.',
};
