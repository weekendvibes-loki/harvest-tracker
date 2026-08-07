'use client';

import { useCallback, useEffect, useMemo, useState } from 'react';

import {
  createMasterDataRecord,
  deleteMasterDataRecords,
  fetchMasterDataRecords,
  setMasterDataRecordStatus,
  updateMasterDataRecord,
} from '../services/master-data.service';
import type {
  MasterDataCreateInput,
  MasterDataModuleKey,
  MasterDataRecord,
  MasterDataUpdateInput,
} from '../types/master-data.types';

export type MasterDataStatusFilter = 'ALL' | 'ACTIVE' | 'INACTIVE';

const waitForNextTick = () => new Promise<void>((resolve) => setTimeout(resolve, 0));

export function useMasterData(module: MasterDataModuleKey) {
  const [allItems, setAllItems] = useState<MasterDataRecord[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<MasterDataStatusFilter>('ALL');
  const [pendingIds, setPendingIds] = useState<string[]>([]);

  const load = useCallback(async (asRefresh = false) => {
    if (asRefresh) {
      setIsRefreshing(true);
    }
    setError(null);
    try {
      const records = await fetchMasterDataRecords(module);
      setAllItems(records);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load records.');
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  }, [module]);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      await waitForNextTick();
      if (cancelled) return;
      try {
        const records = await fetchMasterDataRecords(module);
        if (cancelled) return;
        setAllItems(records);
        setError(null);
      } catch (err) {
        if (cancelled) return;
        setError(err instanceof Error ? err.message : 'Failed to load records.');
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
  }, [module]);

  const filteredItems = useMemo(() => {
    const query = search.trim().toLowerCase();
    return allItems.filter((item) => {
      const matchesSearch =
        !query ||
        item.name.toLowerCase().includes(query) ||
        item.code.toLowerCase().includes(query) ||
        item.description.toLowerCase().includes(query);
      const matchesStatus = statusFilter === 'ALL' || item.status === statusFilter;
      return matchesSearch && matchesStatus;
    });
  }, [allItems, search, statusFilter]);

  const create = useCallback(
    async (input: MasterDataCreateInput): Promise<MasterDataRecord> => {
      const record = await createMasterDataRecord(module, input);
      setAllItems((current) => [record, ...current]);
      return record;
    },
    [module],
  );

  const update = useCallback(
    async (id: string, input: MasterDataUpdateInput): Promise<MasterDataRecord> => {
      const record = await updateMasterDataRecord(module, id, input);
      setAllItems((current) => current.map((item) => (item.id === id ? record : item)));
      return record;
    },
    [module],
  );

  const remove = useCallback(
    async (ids: string[]): Promise<void> => {
      await deleteMasterDataRecords(module, ids);
      setAllItems((current) => current.filter((item) => !ids.includes(item.id)));
    },
    [module],
  );

  const toggleStatus = useCallback(
    async (record: MasterDataRecord): Promise<void> => {
      const nextStatus = record.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
      setPendingIds((current) => [...current, record.id]);
      try {
        const updated = await setMasterDataRecordStatus(module, record.id, nextStatus);
        setAllItems((current) => current.map((item) => (item.id === record.id ? updated : item)));
      } finally {
        setPendingIds((current) => current.filter((id) => id !== record.id));
      }
    },
    [module],
  );

  const resetFilters = useCallback(() => {
    setSearch('');
    setStatusFilter('ALL');
  }, []);

  const activeFilterCount =
    (statusFilter !== 'ALL' ? 1 : 0) + (search.trim().length > 0 ? 1 : 0);

  return {
    items: filteredItems,
    allItems,
    isLoading,
    isRefreshing,
    error,
    search,
    setSearch,
    statusFilter,
    setStatusFilter,
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
