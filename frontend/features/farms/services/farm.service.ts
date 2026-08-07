import { fetchMasterDataRecords } from '@/features/master-data/services/master-data.service';
import { farmMock } from '../mock/farm.mock';
import type {
  Farm,
  FarmCreateInput,
  FarmFruitType,
  FarmSeasonInput,
  FarmStatus,
  FarmUpdateInput,
} from '../types/farm.types';

/**
 * Mock service layer for the Farm API.
 *
 * Each method mirrors a backend Farm endpoint:
 * - fetchFarms()                -> GET  /api/farms
 * - fetchFarm(id)               -> GET  /api/farms/{id}
 * - createFarm(input)           -> POST /api/farms
 * - updateFarm(id, input)       -> PUT  /api/farms/{id}
 * - deleteFarm(id)              -> DELETE /api/farms/{id}
 * - setFarmStatus(id, status)   -> PATCH /api/farms/{id}/status
 * - addFarmFruitType(...)       -> POST /api/farms/{id}/fruit-types
 * - removeFarmFruitType(...)    -> DELETE /api/farms/{id}/fruit-types/{fruitTypeId}
 * - createFarmSeason(...)       -> POST /api/farms/{id}/seasons
 * - updateFarmSeason(...)       -> PUT  /api/farms/{id}/seasons/{seasonId}
 * - removeFarmSeason(...)       -> DELETE /api/farms/{id}/seasons/{seasonId}
 *
 * Later these implementations are swapped for Axios calls without changing
 * any UI component.
 */

export interface FarmServiceConfig {
  latencyMs: number;
  failRequests: boolean;
}

export const farmServiceConfig: FarmServiceConfig = {
  latencyMs: 350,
  failRequests: false,
};

export class DuplicateFarmNameError extends Error {
  constructor(public readonly farmName: string) {
    super(`A farm named "${farmName}" already exists.`);
    this.name = 'DuplicateFarmNameError';
  }
}

export class FarmNotFoundError extends Error {
  constructor() {
    super('The requested farm was not found.');
    this.name = 'FarmNotFoundError';
  }
}

const delay = (ms: number) => new Promise<void>((resolve) => setTimeout(resolve, ms));

const clone = <T>(value: T): T => JSON.parse(JSON.stringify(value)) as T;

const nowIso = () => new Date().toISOString();

const findFarm = (id: string): Farm => {
  const farm = farmMock.find((item) => item.id === id);
  if (!farm) {
    throw new FarmNotFoundError();
  }
  return farm;
};

const nextFarmId = (): string => {
  const max = farmMock.reduce((highest, farm) => {
    const numeric = Number(farm.id.split('-')[1]);
    return Number.isNaN(numeric) ? highest : Math.max(highest, numeric);
  }, 0);
  return `farm-${String(max + 1).padStart(3, '0')}`;
};

const nextSeasonId = (farmId: string): string => {
  const farm = findFarm(farmId);
  const max = farm.seasons.reduce((highest, season) => {
    const match = /-season-(\d+)$/.exec(season.id);
    const numeric = match ? Number(match[1]) : 0;
    return Math.max(highest, numeric);
  }, 0);
  return `${farmId}-season-${max + 1}`;
};

const promoteSeason = (farm: Farm, seasonId: string): Farm => {
  const target = farm.seasons.find((season) => season.id === seasonId);
  if (target && target.status === 'ACTIVE') {
    farm.seasons = farm.seasons.map((season) =>
      season.id !== seasonId && season.status === 'ACTIVE'
        ? { ...season, status: 'COMPLETED' }
        : season,
    );
  }
  return farm;
};

export async function fetchFarms(): Promise<Farm[]> {
  await delay(farmServiceConfig.latencyMs);
  if (farmServiceConfig.failRequests) {
    throw new Error('Simulated network failure while loading farms.');
  }
  return clone(farmMock);
}

export async function fetchFarm(id: string): Promise<Farm> {
  await delay(farmServiceConfig.latencyMs);
  if (farmServiceConfig.failRequests) {
    throw new Error('Simulated network failure while loading the farm.');
  }
  return clone(findFarm(id));
}

export async function createFarm(input: FarmCreateInput): Promise<Farm> {
  await delay(farmServiceConfig.latencyMs);
  const normalizedName = input.name.trim().toLowerCase();
  if (farmMock.some((farm) => farm.name.trim().toLowerCase() === normalizedName)) {
    throw new DuplicateFarmNameError(input.name);
  }
  const timestamp = nowIso();
  const farm: Farm = {
    id: nextFarmId(),
    ...input,
    name: input.name.trim(),
    seasons: [],
    documents: [],
    createdAt: timestamp,
    updatedAt: timestamp,
  };
  farmMock.unshift(farm);
  return clone(farm);
}

export async function updateFarm(id: string, input: FarmUpdateInput): Promise<Farm> {
  await delay(farmServiceConfig.latencyMs);
  const normalizedName = input.name.trim().toLowerCase();
  if (farmMock.some((farm) => farm.id !== id && farm.name.trim().toLowerCase() === normalizedName)) {
    throw new DuplicateFarmNameError(input.name);
  }
  const index = farmMock.findIndex((farm) => farm.id === id);
  if (index === -1) {
    throw new FarmNotFoundError();
  }
  const updated: Farm = {
    ...farmMock[index],
    ...input,
    name: input.name.trim(),
    updatedAt: nowIso(),
  };
  farmMock[index] = updated;
  return clone(updated);
}

export async function deleteFarm(id: string): Promise<void> {
  await delay(farmServiceConfig.latencyMs);
  const index = farmMock.findIndex((farm) => farm.id === id);
  if (index === -1) {
    throw new FarmNotFoundError();
  }
  farmMock.splice(index, 1);
}

export async function setFarmStatus(id: string, status: FarmStatus): Promise<Farm> {
  await delay(farmServiceConfig.latencyMs);
  const farm = findFarm(id);
  farm.status = status;
  farm.updatedAt = nowIso();
  return clone(farm);
}

export async function addFarmFruitType(
  farmId: string,
  fruitType: FarmFruitType,
): Promise<Farm> {
  await delay(farmServiceConfig.latencyMs);
  const farm = findFarm(farmId);
  if (!farm.fruitTypes.some((item) => item.id === fruitType.id)) {
    farm.fruitTypes = [...farm.fruitTypes, fruitType];
    farm.updatedAt = nowIso();
  }
  return clone(farm);
}

export async function removeFarmFruitType(
  farmId: string,
  fruitTypeId: string,
): Promise<Farm> {
  await delay(farmServiceConfig.latencyMs);
  const farm = findFarm(farmId);
  farm.fruitTypes = farm.fruitTypes.filter((item) => item.id !== fruitTypeId);
  farm.updatedAt = nowIso();
  return clone(farm);
}

export async function createFarmSeason(
  farmId: string,
  input: FarmSeasonInput,
): Promise<Farm> {
  await delay(farmServiceConfig.latencyMs);
  const farm = findFarm(farmId);
  const season = {
    id: nextSeasonId(farmId),
    ...input,
  };
  farm.seasons = [...farm.seasons, season];
  promoteSeason(farm, season.id);
  farm.updatedAt = nowIso();
  return clone(farm);
}

export async function updateFarmSeason(
  farmId: string,
  seasonId: string,
  input: FarmSeasonInput,
): Promise<Farm> {
  await delay(farmServiceConfig.latencyMs);
  const farm = findFarm(farmId);
  const seasonIndex = farm.seasons.findIndex((season) => season.id === seasonId);
  if (seasonIndex === -1) {
    throw new FarmNotFoundError();
  }
  farm.seasons[seasonIndex] = { ...farm.seasons[seasonIndex], ...input };
  promoteSeason(farm, seasonId);
  farm.updatedAt = nowIso();
  return clone(farm);
}

export async function removeFarmSeason(farmId: string, seasonId: string): Promise<Farm> {
  await delay(farmServiceConfig.latencyMs);
  const farm = findFarm(farmId);
  farm.seasons = farm.seasons.filter((season) => season.id !== seasonId);
  farm.updatedAt = nowIso();
  return clone(farm);
}

export async function fetchAvailableFruitTypes(): Promise<FarmFruitType[]> {
  await delay(Math.min(farmServiceConfig.latencyMs, 150));
  const fruitTypes = await fetchMasterDataRecords('fruit-types');
  return fruitTypes
    .filter((item) => item.status === 'ACTIVE')
    .map((item) => ({ id: item.id, name: item.name, code: item.code }));
}
