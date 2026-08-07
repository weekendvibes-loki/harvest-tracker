export const FARM_STATUSES = ['ACTIVE', 'INACTIVE'] as const;

export type FarmStatus = (typeof FARM_STATUSES)[number];

export const FARM_OWNERSHIP_TYPES = ['OWNED', 'LEASED'] as const;

export type FarmOwnershipType = (typeof FARM_OWNERSHIP_TYPES)[number];

export const FARM_LAND_UNITS = ['ACRE', 'HECTARE', 'SQFT'] as const;

export type FarmLandUnit = (typeof FARM_LAND_UNITS)[number];

export const SEASON_STATUSES = ['UPCOMING', 'ACTIVE', 'COMPLETED'] as const;

export type SeasonStatus = (typeof SEASON_STATUSES)[number];

export interface FarmFruitType {
  id: string;
  name: string;
  code: string;
}

export interface FarmSeason {
  id: string;
  name: string;
  startDate: string;
  endDate: string;
  status: SeasonStatus;
  notes: string;
}

export interface FarmDocument {
  id: string;
  name: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  uploadedAt: string;
}

/**
 * Farm DTO.
 *
 * Field names map to the backend `farms` table columns where they exist:
 * - name -> farms.name
 * - ownerName -> farms.owner_id (denormalised to the owner's display name for now)
 * - ownershipType -> farms.ownership_type
 * - area + areaUnit -> farms.land_size (normalised with the land unit)
 * - latitude/longitude -> farms.gps_location (normalised into two fields)
 * - status -> farms.status
 *
 * `fruitTypes`, `seasons` and `documents` are related sub-resources returned
 * with the farm payload by GET /api/farms/{id}.
 */
export interface Farm {
  id: string;
  name: string;
  ownerName: string;
  ownershipType: FarmOwnershipType;
  village: string;
  district: string;
  state: string;
  area: number;
  areaUnit: FarmLandUnit;
  latitude: number | null;
  longitude: number | null;
  notes: string;
  status: FarmStatus;
  fruitTypes: FarmFruitType[];
  seasons: FarmSeason[];
  documents: FarmDocument[];
  createdAt: string;
  updatedAt: string;
}

export interface FarmCreateInput {
  name: string;
  ownerName: string;
  ownershipType: FarmOwnershipType;
  village: string;
  district: string;
  state: string;
  area: number;
  areaUnit: FarmLandUnit;
  latitude: number | null;
  longitude: number | null;
  notes: string;
  status: FarmStatus;
  fruitTypes: FarmFruitType[];
}

export interface FarmUpdateInput extends FarmCreateInput {}

export interface FarmSeasonInput {
  name: string;
  startDate: string;
  endDate: string;
  status: SeasonStatus;
  notes: string;
}
