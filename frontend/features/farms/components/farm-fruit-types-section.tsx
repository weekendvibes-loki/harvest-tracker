'use client';

import { useState } from 'react';
import { Apple, Plus, X } from 'lucide-react';

import { ContentCard } from '@/components/shared/content-card';
import { EmptyState } from '@/components/shared/empty-state';
import { SectionHeader } from '@/components/shared/section-header';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import type { Farm, FarmFruitType } from '../types/farm.types';

interface FarmFruitTypesSectionProps {
  farm: Farm;
  availableFruitTypes: FarmFruitType[];
  onAdd: (fruitType: FarmFruitType) => void;
  onRemove: (fruitTypeId: string) => void;
}

export function FarmFruitTypesSection({
  farm,
  availableFruitTypes,
  onAdd,
  onRemove,
}: FarmFruitTypesSectionProps) {
  const [selectedId, setSelectedId] = useState<string>('');

  const farmTypeIds = farm.fruitTypes.map((item) => item.id);
  const addable = availableFruitTypes.filter((item) => !farmTypeIds.includes(item.id));
  const addableCount = addable.length;
  const effectiveSelectedId =
    selectedId && addable.some((item) => item.id === selectedId) ? selectedId : addable[0]?.id ?? '';

  const handleAdd = () => {
    const fruitType = addable.find((item) => item.id === effectiveSelectedId);
    if (fruitType) {
      onAdd(fruitType);
      setSelectedId('');
    }
  };

  return (
    <ContentCard>
      <div className="space-y-4">
        <SectionHeader
          title="Fruit Types"
          description="Fruit types grown on this farm"
        />

        {farm.fruitTypes.length === 0 ? (
          <EmptyState
            title="No fruit types assigned"
            description="Add fruit types to track what is grown on this farm."
            icon={Apple}
          />
        ) : (
          <ul className="flex flex-wrap gap-2" aria-label="Assigned fruit types">
            {farm.fruitTypes.map((fruitType) => (
              <li key={fruitType.id}>
                <Badge variant="outline" className="gap-1.5 py-1 pr-1 pl-2.5 text-xs font-medium">
                  <span className="font-semibold">{fruitType.name}</span>
                  <span className="font-mono text-muted-foreground">{fruitType.code}</span>
                  <button
                    type="button"
                    onClick={() => onRemove(fruitType.id)}
                    aria-label={`Remove ${fruitType.name}`}
                    className="rounded-full p-0.5 text-muted-foreground transition-colors hover:bg-background hover:text-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                  >
                    <X className="h-3 w-3" aria-hidden="true" />
                  </button>
                </Badge>
              </li>
            ))}
          </ul>
        )}

        {addableCount > 0 ? (
          <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
            <select
              value={effectiveSelectedId}
              onChange={(event) => setSelectedId(event.target.value)}
              aria-label="Select fruit type to add"
              className="h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring sm:max-w-xs"
            >
              {addable.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.name} ({item.code})
                </option>
              ))}
            </select>
            <Button size="sm" onClick={handleAdd} disabled={!effectiveSelectedId}>
              <Plus className="h-4 w-4" aria-hidden="true" />
              Add
            </Button>
          </div>
        ) : (
          <p className="text-sm text-muted-foreground">
            All available fruit types are already assigned.
          </p>
        )}
      </div>
    </ContentCard>
  );
}
