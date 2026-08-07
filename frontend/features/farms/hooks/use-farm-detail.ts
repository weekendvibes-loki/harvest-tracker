'use client';

import { useCallback, useEffect, useState } from 'react';
import {
  addFarmFruitType,
  createFarmSeason,
  fetchFarm,
  removeFarmFruitType,
  removeFarmSeason,
  setFarmStatus,
  updateFarm,
  updateFarmSeason,
} from '../services/farm.service';
import type {
  Farm,
  FarmFruitType,
  FarmSeasonInput,
  FarmStatus,
  FarmUpdateInput,
} from '../types/farm.types';

const waitForNextTick = () => new Promise<void>((resolve) => setTimeout(resolve, 0));

export function useFarmDetail(farmId: string) {
  const [farm, setFarm] = useState<Farm | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [pendingIds, setPendingIds] = useState<string[]>([]);

  const load = useCallback(
    async (asRefresh = false) => {
      if (asRefresh) {
        setIsRefreshing(true);
      }
      setError(null);
      try {
        const record = await fetchFarm(farmId);
        setFarm(record);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load the farm.');
      } finally {
        setIsLoading(false);
        setIsRefreshing(false);
      }
    },
    [farmId],
  );

  useEffect(() => {
    let cancelled = false;
    (async () => {
      await waitForNextTick();
      if (cancelled) return;
      try {
        const record = await fetchFarm(farmId);
        if (cancelled) return;
        setFarm(record);
        setError(null);
      } catch (err) {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : 'Failed to load the farm.');
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
  }, [farmId]);

  const toggleStatus = useCallback(
    async (): Promise<void> => {
      if (!farm) return;
      const nextStatus: FarmStatus = farm.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
      setPendingIds((current) => [...current, farm.id]);
      try {
        const updated = await setFarmStatus(farm.id, nextStatus);
        setFarm(updated);
      } finally {
        setPendingIds((current) => current.filter((id) => id !== farm.id));
      }
    },
    [farm],
  );

  const update = useCallback(
    async (input: FarmUpdateInput): Promise<void> => {
      if (!farm) return;
      const updated = await updateFarm(farm.id, input);
      setFarm(updated);
    },
    [farm],
  );

  const addFruitType = useCallback(
    async (fruitType: FarmFruitType): Promise<void> => {
      if (!farm) return;
      const updated = await addFarmFruitType(farm.id, fruitType);
      setFarm(updated);
    },
    [farm],
  );

  const removeFruitType = useCallback(
    async (fruitTypeId: string): Promise<void> => {
      if (!farm) return;
      const updated = await removeFarmFruitType(farm.id, fruitTypeId);
      setFarm(updated);
    },
    [farm],
  );

  const createSeason = useCallback(
    async (input: FarmSeasonInput): Promise<void> => {
      if (!farm) return;
      const updated = await createFarmSeason(farm.id, input);
      setFarm(updated);
    },
    [farm],
  );

  const updateSeason = useCallback(
    async (seasonId: string, input: FarmSeasonInput): Promise<void> => {
      if (!farm) return;
      const updated = await updateFarmSeason(farm.id, seasonId, input);
      setFarm(updated);
    },
    [farm],
  );

  const removeSeason = useCallback(
    async (seasonId: string): Promise<void> => {
      if (!farm) return;
      const updated = await removeFarmSeason(farm.id, seasonId);
      setFarm(updated);
    },
    [farm],
  );

  return {
    farm,
    isLoading,
    isRefreshing,
    error,
    pendingIds,
    load,
    update,
    toggleStatus,
    addFruitType,
    removeFruitType,
    createSeason,
    updateSeason,
    removeSeason,
  };
}
