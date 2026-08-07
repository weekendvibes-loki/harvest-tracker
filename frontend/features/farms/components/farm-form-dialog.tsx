'use client';

import { useState } from 'react';

import { LoadingSpinner } from '@/components/feedback/loading-spinner';
import { FormField } from '@/components/forms/form-field';
import { ValidationMessage } from '@/components/forms/validation-message';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { cn } from '@/lib/utils';
import type {
  Farm,
  FarmCreateInput,
  FarmFruitType,
  FarmLandUnit,
  FarmOwnershipType,
  FarmStatus,
} from '../types/farm.types';
import { FarmStatusToggle } from './farm-status-toggle';

export const FARM_NAME_MAX = 100;
export const FARM_OWNER_MAX = 100;
export const FARM_TEXT_MAX = 100;
export const FARM_NOTES_MAX = 500;
export const FARM_AREA_MAX = 100000;

const LAND_UNITS: FarmLandUnit[] = ['ACRE', 'HECTARE', 'SQFT'];
const OWNERSHIP_TYPES: { value: FarmOwnershipType; label: string }[] = [
  { value: 'OWNED', label: 'Owned' },
  { value: 'LEASED', label: 'Leased' },
];

interface FormErrors {
  name?: string;
  ownerName?: string;
  village?: string;
  district?: string;
  state?: string;
  area?: string;
  latitude?: string;
  longitude?: string;
  notes?: string;
  general?: string;
}

interface FarmFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  mode: 'create' | 'edit';
  farm?: Farm | null;
  existing: Farm[];
  availableFruitTypes: FarmFruitType[];
  onSubmit: (input: FarmCreateInput) => Promise<unknown>;
}

export function FarmFormDialog({
  open,
  onOpenChange,
  mode,
  farm,
  existing,
  availableFruitTypes,
  onSubmit,
}: FarmFormDialogProps) {
  const [name, setName] = useState(farm?.name ?? '');
  const [ownerName, setOwnerName] = useState(farm?.ownerName ?? '');
  const [ownershipType, setOwnershipType] = useState<FarmOwnershipType>(
    farm?.ownershipType ?? 'OWNED',
  );
  const [village, setVillage] = useState(farm?.village ?? '');
  const [district, setDistrict] = useState(farm?.district ?? '');
  const [state, setState] = useState(farm?.state ?? '');
  const [area, setArea] = useState(farm ? String(farm.area) : '');
  const [areaUnit, setAreaUnit] = useState<FarmLandUnit>(farm?.areaUnit ?? 'ACRE');
  const [latitude, setLatitude] = useState(farm?.latitude != null ? String(farm.latitude) : '');
  const [longitude, setLongitude] = useState(farm?.longitude != null ? String(farm.longitude) : '');
  const [notes, setNotes] = useState(farm?.notes ?? '');
  const [status, setStatus] = useState<FarmStatus>(farm?.status ?? 'ACTIVE');
  const [fruitTypeIds, setFruitTypeIds] = useState<string[]>(
    farm?.fruitTypes.map((item) => item.id) ?? [],
  );
  const [errors, setErrors] = useState<FormErrors>({});
  const [isSaving, setIsSaving] = useState(false);

  const toggleFruitType = (id: string) => {
    setFruitTypeIds((current) =>
      current.includes(id) ? current.filter((item) => item !== id) : [...current, id],
    );
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const normalizedName = name.trim();
    const normalizedOwner = ownerName.trim();
    const normalizedVillage = village.trim();
    const normalizedDistrict = district.trim();
    const normalizedState = state.trim();
    const normalizedNotes = notes.trim();
    const numericArea = Number(area);
    const numericLatitude = latitude.trim() === '' ? null : Number(latitude);
    const numericLongitude = longitude.trim() === '' ? null : Number(longitude);

    const nextErrors: FormErrors = {};
    if (!normalizedName) {
      nextErrors.name = 'Farm name is required.';
    } else if (normalizedName.length < 2) {
      nextErrors.name = 'Farm name must be at least 2 characters.';
    } else if (normalizedName.length > FARM_NAME_MAX) {
      nextErrors.name = `Farm name must be ${FARM_NAME_MAX} characters or fewer.`;
    } else if (
      existing.some(
        (item) => item.name.trim().toLowerCase() === normalizedName.toLowerCase() && item.id !== (farm?.id ?? ''),
      )
    ) {
      nextErrors.name = `A farm named "${normalizedName}" already exists.`;
    }

    if (!normalizedOwner) {
      nextErrors.ownerName = 'Owner name is required.';
    } else if (normalizedOwner.length > FARM_OWNER_MAX) {
      nextErrors.ownerName = `Owner name must be ${FARM_OWNER_MAX} characters or fewer.`;
    }

    if (!normalizedVillage) {
      nextErrors.village = 'Village is required.';
    } else if (normalizedVillage.length > FARM_TEXT_MAX) {
      nextErrors.village = `Village must be ${FARM_TEXT_MAX} characters or fewer.`;
    }

    if (!normalizedDistrict) {
      nextErrors.district = 'District is required.';
    } else if (normalizedDistrict.length > FARM_TEXT_MAX) {
      nextErrors.district = `District must be ${FARM_TEXT_MAX} characters or fewer.`;
    }

    if (!normalizedState) {
      nextErrors.state = 'State is required.';
    } else if (normalizedState.length > FARM_TEXT_MAX) {
      nextErrors.state = `State must be ${FARM_TEXT_MAX} characters or fewer.`;
    }

    if (area.trim() === '' || Number.isNaN(numericArea)) {
      nextErrors.area = 'Area is required and must be a number.';
    } else if (numericArea <= 0) {
      nextErrors.area = 'Area must be greater than zero.';
    } else if (numericArea > FARM_AREA_MAX) {
      nextErrors.area = `Area must be ${FARM_AREA_MAX.toLocaleString('en-IN')} or fewer.`;
    }

    if (numericLatitude !== null && (Number.isNaN(numericLatitude) || numericLatitude < -90 || numericLatitude > 90)) {
      nextErrors.latitude = 'Latitude must be between -90 and 90.';
    }

    if (numericLongitude !== null && (Number.isNaN(numericLongitude) || numericLongitude < -180 || numericLongitude > 180)) {
      nextErrors.longitude = 'Longitude must be between -180 and 180.';
    }

    if (normalizedNotes.length > FARM_NOTES_MAX) {
      nextErrors.notes = `Notes must be ${FARM_NOTES_MAX} characters or fewer.`;
    }

    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) {
      return;
    }

    const fruitTypes = fruitTypeIds
      .map((id) => availableFruitTypes.find((item) => item.id === id))
      .filter((item): item is FarmFruitType => Boolean(item));

    setIsSaving(true);
    try {
      await onSubmit({
        name: normalizedName,
        ownerName: normalizedOwner,
        ownershipType,
        village: normalizedVillage,
        district: normalizedDistrict,
        state: normalizedState,
        area: numericArea,
        areaUnit,
        latitude: numericLatitude,
        longitude: numericLongitude,
        notes: normalizedNotes,
        status,
        fruitTypes,
      });
      onOpenChange(false);
    } catch {
      setErrors({ general: 'Could not save the farm. Please try again.' });
    } finally {
      setIsSaving(false);
    }
  };

  const baseInputId = 'farm-form';

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>{mode === 'create' ? 'Create Farm' : 'Edit Farm'}</DialogTitle>
          <DialogDescription>
            {mode === 'create'
              ? 'Add a new farm to your business. Fields marked with an asterisk are required.'
              : 'Update the farm details below. Fields marked with an asterisk are required.'}
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} noValidate>
          <div className="space-y-4">
            {errors.general ? (
              <ValidationMessage variant="error">{errors.general}</ValidationMessage>
            ) : null}

            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="Farm Name"
                htmlFor={`${baseInputId}-name`}
                required
                error={errors.name}
              >
                <Input
                  id={`${baseInputId}-name`}
                  value={name}
                  onChange={(event) => setName(event.target.value)}
                  placeholder="e.g. Ratnagiri Mango Estate"
                  maxLength={FARM_NAME_MAX}
                  autoFocus
                  disabled={isSaving}
                  aria-invalid={Boolean(errors.name)}
                />
              </FormField>

              <FormField
                label="Owner Name"
                htmlFor={`${baseInputId}-owner`}
                required
                error={errors.ownerName}
              >
                <Input
                  id={`${baseInputId}-owner`}
                  value={ownerName}
                  onChange={(event) => setOwnerName(event.target.value)}
                  placeholder="e.g. Ramesh Patil"
                  maxLength={FARM_OWNER_MAX}
                  disabled={isSaving}
                  aria-invalid={Boolean(errors.ownerName)}
                />
              </FormField>
            </div>

            <FormField label="Ownership Type" required>
              <div role="radiogroup" aria-label="Ownership type" className="flex flex-wrap gap-2">
                {OWNERSHIP_TYPES.map((option) => (
                  <label
                    key={option.value}
                    className={cn(
                      'inline-flex cursor-pointer items-center gap-2 rounded-md border px-3 py-2 text-sm font-medium transition-colors focus-within:ring-2 focus-within:ring-ring',
                      ownershipType === option.value
                        ? 'border-primary bg-primary/5 text-primary'
                        : 'border-input text-muted-foreground hover:bg-accent',
                    )}
                  >
                    <input
                      type="radio"
                      name="ownershipType"
                      value={option.value}
                      checked={ownershipType === option.value}
                      onChange={() => setOwnershipType(option.value)}
                      disabled={isSaving}
                      className="h-4 w-4 accent-emerald-600"
                    />
                    {option.label}
                  </label>
                ))}
              </div>
            </FormField>

            <div className="grid gap-4 sm:grid-cols-3">
              <FormField
                label="Village"
                htmlFor={`${baseInputId}-village`}
                required
                error={errors.village}
              >
                <Input
                  id={`${baseInputId}-village`}
                  value={village}
                  onChange={(event) => setVillage(event.target.value)}
                  placeholder="e.g. Palshet"
                  maxLength={FARM_TEXT_MAX}
                  disabled={isSaving}
                  aria-invalid={Boolean(errors.village)}
                />
              </FormField>

              <FormField
                label="District"
                htmlFor={`${baseInputId}-district`}
                required
                error={errors.district}
              >
                <Input
                  id={`${baseInputId}-district`}
                  value={district}
                  onChange={(event) => setDistrict(event.target.value)}
                  placeholder="e.g. Ratnagiri"
                  maxLength={FARM_TEXT_MAX}
                  disabled={isSaving}
                  aria-invalid={Boolean(errors.district)}
                />
              </FormField>

              <FormField
                label="State"
                htmlFor={`${baseInputId}-state`}
                required
                error={errors.state}
              >
                <Input
                  id={`${baseInputId}-state`}
                  value={state}
                  onChange={(event) => setState(event.target.value)}
                  placeholder="e.g. Maharashtra"
                  maxLength={FARM_TEXT_MAX}
                  disabled={isSaving}
                  aria-invalid={Boolean(errors.state)}
                />
              </FormField>
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="Area"
                htmlFor={`${baseInputId}-area`}
                required
                error={errors.area}
              >
                <div className="flex gap-2">
                  <Input
                    id={`${baseInputId}-area`}
                    type="number"
                    inputMode="decimal"
                    step="any"
                    min="0"
                    max={FARM_AREA_MAX}
                    value={area}
                    onChange={(event) => setArea(event.target.value)}
                    placeholder="e.g. 25"
                    disabled={isSaving}
                    aria-invalid={Boolean(errors.area)}
                    className="w-full"
                  />
                  <select
                    value={areaUnit}
                    onChange={(event) => setAreaUnit(event.target.value as FarmLandUnit)}
                    disabled={isSaving}
                    aria-label="Area unit"
                    className="h-9 w-28 shrink-0 rounded-md border border-input bg-transparent px-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    {LAND_UNITS.map((unit) => (
                      <option key={unit} value={unit}>
                        {unit === 'ACRE' ? 'acre' : unit === 'HECTARE' ? 'hectare' : 'sq ft'}
                      </option>
                    ))}
                  </select>
                </div>
              </FormField>

              <FormField label="Status">
                <div className="flex h-9 items-center gap-2">
                  <FarmStatusToggle
                    checked={status === 'ACTIVE'}
                    onChange={(checked) => setStatus(checked ? 'ACTIVE' : 'INACTIVE')}
                    label="Farm status"
                    disabled={isSaving}
                  />
                  <span className="text-sm text-muted-foreground">
                    {status === 'ACTIVE' ? 'Active' : 'Inactive'}
                  </span>
                </div>
              </FormField>
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="Latitude"
                htmlFor={`${baseInputId}-latitude`}
                hint="Optional GPS latitude (-90 to 90)"
                error={errors.latitude}
              >
                <Input
                  id={`${baseInputId}-latitude`}
                  type="number"
                  inputMode="decimal"
                  step="any"
                  min="-90"
                  max="90"
                  value={latitude}
                  onChange={(event) => setLatitude(event.target.value)}
                  placeholder="e.g. 16.9928"
                  disabled={isSaving}
                  aria-invalid={Boolean(errors.latitude)}
                />
              </FormField>

              <FormField
                label="Longitude"
                htmlFor={`${baseInputId}-longitude`}
                hint="Optional GPS longitude (-180 to 180)"
                error={errors.longitude}
              >
                <Input
                  id={`${baseInputId}-longitude`}
                  type="number"
                  inputMode="decimal"
                  step="any"
                  min="-180"
                  max="180"
                  value={longitude}
                  onChange={(event) => setLongitude(event.target.value)}
                  placeholder="e.g. 73.3053"
                  disabled={isSaving}
                  aria-invalid={Boolean(errors.longitude)}
                />
              </FormField>
            </div>

            <FormField label="Fruit Types" hint="Select the fruit types grown on this farm">
              {availableFruitTypes.length === 0 ? (
                <p className="text-sm text-muted-foreground">
                  No fruit types available. Add fruit types in Master Data first.
                </p>
              ) : (
                <div
                  role="group"
                  aria-label="Fruit types"
                  className="max-h-40 space-y-1 overflow-y-auto rounded-md border p-2"
                >
                  {availableFruitTypes.map((fruitType) => {
                    const checked = fruitTypeIds.includes(fruitType.id);
                    return (
                      <label
                        key={fruitType.id}
                        className="flex cursor-pointer items-center gap-2 rounded-md px-2 py-1.5 text-sm transition-colors hover:bg-accent"
                      >
                        <input
                          type="checkbox"
                          checked={checked}
                          onChange={() => toggleFruitType(fruitType.id)}
                          disabled={isSaving}
                          className="h-4 w-4 cursor-pointer rounded accent-emerald-600"
                        />
                        <span className="font-medium">{fruitType.name}</span>
                        <span className="ml-auto font-mono text-xs text-muted-foreground">
                          {fruitType.code}
                        </span>
                      </label>
                    );
                  })}
                </div>
              )}
            </FormField>

            <FormField
              label="Notes"
              htmlFor={`${baseInputId}-notes`}
              error={errors.notes}
            >
              <textarea
                id={`${baseInputId}-notes`}
                value={notes}
                onChange={(event) => setNotes(event.target.value)}
                placeholder="Additional notes about the farm"
                maxLength={FARM_NOTES_MAX}
                rows={3}
                disabled={isSaving}
                aria-invalid={Boolean(errors.notes)}
                className="flex w-full resize-none rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm transition-colors placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
              />
            </FormField>
          </div>

          <DialogFooter className="mt-6">
            <Button
              type="button"
              variant="outline"
              disabled={isSaving}
              onClick={() => onOpenChange(false)}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isSaving}>
              {isSaving ? <LoadingSpinner size="sm" label="" /> : null}
              {mode === 'create' ? 'Create Farm' : 'Save Changes'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
