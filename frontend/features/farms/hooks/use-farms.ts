'use client';

import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  createFarm,
  deleteFarm,
  fetchAvailableFruitTypes,
  fetchFarms,
  setFarmStatus,
  updateFarm,
} from '../services/farm.service';
import type {
  Farm,
  FarmCreateInput,
  FarmFruitType,
  FarmStatus,
  FarmUpdateInput,
} from '../types/farm.types';

export type FarmStatusFilter = 'ALL' | 'ACTIVE' | 'INACTIVE';
export type FarmOwnershipFilter = 'ALL' | 'OWNED' | 'LEASED';
export type FarmFruitTypeFilter = 'ALL' | string;

const waitForNextTick = () => new Promise<void>((resolve) => setTimeout(resolve, 0));

export function useFarms() {
  const [farms, setFarms] = useState<Farm[]>([]);
  const [availableFruitTypes, setAvailableFruitTypes] = useState<FarmFruitType[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<FarmStatusFilter>('ALL');
  const [ownershipFilter, setOwnershipFilter] = useState<FarmOwnershipFilter>('ALL');
  const [fruitTypeFilter, setFruitTypeFilter] = useState<FarmFruitTypeFilter>('ALL');
  const [pendingIds, setPendingIds] = useState<string[]>([]);

  const load = useCallback(async (asRefresh = false) => {
    if (asRefresh) {
      setIsRefreshing(true);
    }
    setError(null);
    try {
      const [records, fruitTypes] = await Promise.all([
        fetchFarms(),
        fetchAvailableFruitTypes(),
      ]);
      setFarms(records);
      setAvailableFruitTypes(fruitTypes);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load farms.');
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  }, []);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      await waitForNextTick();
      if (cancelled) return;
      try {
        const [records, fruitTypes] = await Promise.all([
          fetchFarms(),
          fetchAvailableFruitTypes(),
        ]);
        if (cancelled) return;
        setFarms(records);
        setAvailableFruitTypes(fruitTypes);
        setError(null);
      } catch (err) {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : 'Failed to load farms.');
      } finally {
        if (!cancelled) {
          setIsLoading(false);
          setIsRefreshing(false);
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  const filteredFarms = useMemo(() => {
    const query = search.trim().toLowerCase();
    return farms.filter((farm) => {
      const matchesSearch =
        !query ||
        farm.name.toLowerCase().includes(query) ||
        farm.ownerName.toLowerCase().includes(query) ||
        farm.village.toLowerCase().includes(query) ||
        farm.district.toLowerCase().includes(query) ||
        farm.state.toLowerCase().includes(query);
      const matchesStatus = statusFilter === 'ALL' || farm.status === statusFilter;
      const matchesOwnership =
        ownershipFilter === 'ALL' || farm.ownershipType === ownershipFilter;
      const matchesFruitType =
        fruitTypeFilter === 'ALL' ||
        farm.fruitTypes.some((fruitType) => fruitType.id === fruitTypeFilter);
      return matchesSearch && matchesStatus && matchesOwnership && matchesFruitType;
    });
  }, [farms, search, statusFilter, ownershipFilter, fruitTypeFilter]);

  const create = useCallback(async (input: FarmCreateInput): Promise<Farm> => {
    const farm = await createFarm(input);
    setFarms((current) => [farm, ...current]);
    return farm;
  }, []);

  const update = useCallback(
    async (id: string, input: FarmUpdateInput): Promise<Farm> => {
      const farm = await updateFarm(id, input);
      setFarms((current) => current.map((item) => (item.id === id ? farm : item)));
      return farm;
    },
    [],
  );

  const remove = useCallback(async (ids: string[]): Promise<void> => {
    for (const id of ids) {
      await deleteFarm(id);
    }
    const idSet = new Set(ids);
    setFarms((current) => current.filter((farm) => !idSet.has(farm.id)));
  }, []);

  const toggleStatus = useCallback(
    async (farm: Farm): Promise<void> => {
      const nextStatus: FarmStatus = farm.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
      setPendingIds((current) => [...current, farm.id]);
      try {
        const updated = await setFarmStatus(farm.id, nextStatus);
        setFarms((current) => current.map((item) => (item.id === farm.id ? updated : item)));
      } finally {
        setPendingIds((current) => current.filter((id) => id !== farm.id));
      }
    },
    [],
  );

  const resetFilters = useCallback(() => {
    setSearch('');
    setStatusFilter('ALL');
    setOwnershipFilter('ALL');
    setFruitTypeFilter('ALL');
  }, []);

  const activeFilterCount =
    (statusFilter !== 'ALL' ? 1 : 0) +
    (ownershipFilter !== 'ALL' ? 1 : 0) +
    (fruitTypeFilter !== 'ALL' ? 1 : 0) +
    (search.trim().length > 0 ? 1 : 0);

  return {
    farms: filteredFarms,
    allFarms: farms,
    availableFruitTypes,
    isLoading,
    isRefreshing,
    error,
    search,
    setSearch,
    statusFilter,
    setStatusFilter,
    ownershipFilter,
    setOwnershipFilter,
    fruitTypeFilter,
    setFruitTypeFilter,
    activeFilterCount,
    resetFilters,
    load,
    create,
    update,
    remove,
    toggleStatus,
    pendingIds,
  };
}
