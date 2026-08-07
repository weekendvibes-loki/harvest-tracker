import { masterDataMock } from '../mock/master-data.mock';
import type {
  MasterDataCreateInput,
  MasterDataModuleKey,
  MasterDataRecord,
  MasterDataUpdateInput,
} from '../types/master-data.types';

/**
 * Mock service layer.
 *
 * Each method mirrors the backend API contract for master data endpoints.
 * Later these implementations will be swapped for Axios calls without
 * changing any UI component.
 *
 * Simulated behaviour:
 * - Artificial latency via `mockServiceConfig.latencyMs`.
 * - Deterministic failure simulation via `mockServiceConfig.failRequests`
 *   (set to `true` to exercise the list error state).
 * - Duplicate `code` rejection via `DuplicateCodeError`.
 */

export interface MockServiceConfig {
  latencyMs: number;
  failRequests: boolean;
}

export const mockServiceConfig: MockServiceConfig = {
  latencyMs: 350,
  failRequests: false,
};

export class DuplicateCodeError extends Error {
  constructor(public readonly code: string) {
    super(`A record with code "${code}" already exists.`);
    this.name = 'DuplicateCodeError';
  }
}

export class MasterDataNotFoundError extends Error {
  constructor() {
    super('The requested record was not found.');
    this.name = 'MasterDataNotFoundError';
  }
}

const delay = (ms: number) => new Promise<void>((resolve) => setTimeout(resolve, ms));

const clone = (record: MasterDataRecord): MasterDataRecord => ({ ...record });

const nowIso = () => new Date().toISOString();

const nextId = (module: MasterDataModuleKey): string => {
  const max = masterDataMock[module].reduce((highest, record) => {
    const numeric = Number(record.id.split('-')[1]);
    return Number.isNaN(numeric) ? highest : Math.max(highest, numeric);
  }, 0);
  return `${module === 'payment-methods' ? 'pm' : module === 'units' ? 'un' : module === 'expense-categories' ? 'ec' : module === 'worker-types' ? 'wt' : module === 'crop-variants' ? 'cv' : 'ft'}-${String(max + 1).padStart(3, '0')}`;
};

export async function fetchMasterDataRecords(
  module: MasterDataModuleKey,
): Promise<MasterDataRecord[]> {
  await delay(mockServiceConfig.latencyMs);
  if (mockServiceConfig.failRequests) {
    throw new Error('Simulated network failure while loading records.');
  }
  return masterDataMock[module].map(clone);
}

export async function isMasterDataCodeAvailable(
  module: MasterDataModuleKey,
  code: string,
  excludeId?: string,
): Promise<boolean> {
  await delay(Math.min(mockServiceConfig.latencyMs, 120));
  return !masterDataMock[module].some(
    (record) => record.code === code && record.id !== excludeId,
  );
}

export async function createMasterDataRecord(
  module: MasterDataModuleKey,
  input: MasterDataCreateInput,
): Promise<MasterDataRecord> {
  await delay(mockServiceConfig.latencyMs);
  if (masterDataMock[module].some((record) => record.code === input.code)) {
    throw new DuplicateCodeError(input.code);
  }
  const timestamp = nowIso();
  const record: MasterDataRecord = {
    id: nextId(module),
    ...input,
    createdAt: timestamp,
    updatedAt: timestamp,
  };
  masterDataMock[module].unshift(record);
  return clone(record);
}

export async function updateMasterDataRecord(
  module: MasterDataModuleKey,
  id: string,
  input: MasterDataUpdateInput,
): Promise<MasterDataRecord> {
  await delay(mockServiceConfig.latencyMs);
  if (masterDataMock[module].some((record) => record.code === input.code && record.id !== id)) {
    throw new DuplicateCodeError(input.code);
  }
  const index = masterDataMock[module].findIndex((record) => record.id === id);
  if (index === -1) {
    throw new MasterDataNotFoundError();
  }
  const updated: MasterDataRecord = {
    ...masterDataMock[module][index],
    ...input,
    updatedAt: nowIso(),
  };
  masterDataMock[module][index] = updated;
  return clone(updated);
}

export async function deleteMasterDataRecords(
  module: MasterDataModuleKey,
  ids: string[],
): Promise<void> {
  await delay(mockServiceConfig.latencyMs);
  masterDataMock[module] = masterDataMock[module].filter((record) => !ids.includes(record.id));
}

export async function setMasterDataRecordStatus(
  module: MasterDataModuleKey,
  id: string,
  status: 'ACTIVE' | 'INACTIVE',
): Promise<MasterDataRecord> {
  await delay(mockServiceConfig.latencyMs);
  const index = masterDataMock[module].findIndex((record) => record.id === id);
  if (index === -1) {
    throw new MasterDataNotFoundError();
  }
  const updated: MasterDataRecord = {
    ...masterDataMock[module][index],
    status,
    updatedAt: nowIso(),
  };
  masterDataMock[module][index] = updated;
  return clone(updated);
}
