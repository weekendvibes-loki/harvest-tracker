import type { MasterDataModuleConfig, MasterDataModuleKey } from '../types/master-data.types';
import { cropVariantsModuleConfig } from '../crop-variants/crop-variants.config';
import { expenseCategoriesModuleConfig } from '../expense-categories/expense-categories.config';
import { fruitTypesModuleConfig } from '../fruit-types/fruit-types.config';
import { paymentMethodsModuleConfig } from '../payment-methods/payment-methods.config';
import { unitsModuleConfig } from '../units/units.config';
import { workerTypesModuleConfig } from '../worker-types/worker-types.config';

export const masterDataModuleConfigs: Record<MasterDataModuleKey, MasterDataModuleConfig> = {
  'fruit-types': fruitTypesModuleConfig,
  'crop-variants': cropVariantsModuleConfig,
  units: unitsModuleConfig,
  'worker-types': workerTypesModuleConfig,
  'payment-methods': paymentMethodsModuleConfig,
  'expense-categories': expenseCategoriesModuleConfig,
};
