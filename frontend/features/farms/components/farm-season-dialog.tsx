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
import type { FarmSeason, FarmSeasonInput, SeasonStatus } from '../types/farm.types';

export const SEASON_NAME_MAX = 100;
export const SEASON_NOTES_MAX = 500;

const SEASON_STATUS_OPTIONS: { value: SeasonStatus; label: string }[] = [
  { value: 'UPCOMING', label: 'Upcoming' },
  { value: 'ACTIVE', label: 'Active' },
  { value: 'COMPLETED', label: 'Completed' },
];

interface FormErrors {
  name?: string;
  startDate?: string;
  endDate?: string;
  notes?: string;
  general?: string;
}

interface FarmSeasonDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  mode: 'create' | 'edit';
  season?: FarmSeason | null;
  onSubmit: (input: FarmSeasonInput) => Promise<unknown>;
}

export function FarmSeasonDialog({
  open,
  onOpenChange,
  mode,
  season,
  onSubmit,
}: FarmSeasonDialogProps) {
  const [name, setName] = useState(season?.name ?? '');
  const [startDate, setStartDate] = useState(season?.startDate ?? '');
  const [endDate, setEndDate] = useState(season?.endDate ?? '');
  const [status, setStatus] = useState<SeasonStatus>(season?.status ?? 'ACTIVE');
  const [notes, setNotes] = useState(season?.notes ?? '');
  const [errors, setErrors] = useState<FormErrors>({});
  const [isSaving, setIsSaving] = useState(false);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const normalizedName = name.trim();
    const normalizedNotes = notes.trim();

    const nextErrors: FormErrors = {};
    if (!normalizedName) {
      nextErrors.name = 'Season name is required.';
    } else if (normalizedName.length > SEASON_NAME_MAX) {
      nextErrors.name = `Season name must be ${SEASON_NAME_MAX} characters or fewer.`;
    }

    if (!startDate) {
      nextErrors.startDate = 'Start date is required.';
    }

    if (!endDate) {
      nextErrors.endDate = 'End date is required.';
    } else if (startDate && endDate < startDate) {
      nextErrors.endDate = 'End date must be on or after the start date.';
    }

    if (normalizedNotes.length > SEASON_NOTES_MAX) {
      nextErrors.notes = `Notes must be ${SEASON_NOTES_MAX} characters or fewer.`;
    }

    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) {
      return;
    }

    setIsSaving(true);
    try {
      await onSubmit({
        name: normalizedName,
        startDate,
        endDate,
        status,
        notes: normalizedNotes,
      });
      onOpenChange(false);
    } catch {
      setErrors({ general: 'Could not save the season. Please try again.' });
    } finally {
      setIsSaving(false);
    }
  };

  const baseInputId = 'farm-season';

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>{mode === 'create' ? 'Create Season' : 'Edit Season'}</DialogTitle>
          <DialogDescription>
            {mode === 'create'
              ? 'Add a growing or harvest season for this farm.'
              : 'Update the season details below.'}
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} noValidate>
          <div className="space-y-4">
            {errors.general ? (
              <ValidationMessage variant="error">{errors.general}</ValidationMessage>
            ) : null}

            <FormField
              label="Season Name"
              htmlFor={`${baseInputId}-name`}
              required
              error={errors.name}
            >
              <Input
                id={`${baseInputId}-name`}
                value={name}
                onChange={(event) => setName(event.target.value)}
                placeholder="e.g. 2026 Harvest Season"
                maxLength={SEASON_NAME_MAX}
                autoFocus
                disabled={isSaving}
                aria-invalid={Boolean(errors.name)}
              />
            </FormField>

            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="Start Date"
                htmlFor={`${baseInputId}-start`}
                required
                error={errors.startDate}
              >
                <Input
                  id={`${baseInputId}-start`}
                  type="date"
                  value={startDate}
                  onChange={(event) => setStartDate(event.target.value)}
                  disabled={isSaving}
                  aria-invalid={Boolean(errors.startDate)}
                />
              </FormField>

              <FormField
                label="End Date"
                htmlFor={`${baseInputId}-end`}
                required
                error={errors.endDate}
              >
                <Input
                  id={`${baseInputId}-end`}
                  type="date"
                  value={endDate}
                  onChange={(event) => setEndDate(event.target.value)}
                  disabled={isSaving}
                  aria-invalid={Boolean(errors.endDate)}
                />
              </FormField>
            </div>

            <FormField label="Status" required>
              <div role="radiogroup" aria-label="Season status" className="flex flex-wrap gap-2">
                {SEASON_STATUS_OPTIONS.map((option) => (
                  <label
                    key={option.value}
                    className={cn(
                      'inline-flex cursor-pointer items-center gap-2 rounded-md border px-3 py-2 text-sm font-medium transition-colors focus-within:ring-2 focus-within:ring-ring',
                      status === option.value
                        ? 'border-primary bg-primary/5 text-primary'
                        : 'border-input text-muted-foreground hover:bg-accent',
                    )}
                  >
                    <input
                      type="radio"
                      name="seasonStatus"
                      value={option.value}
                      checked={status === option.value}
                      onChange={() => setStatus(option.value)}
                      disabled={isSaving}
                      className="h-4 w-4 accent-emerald-600"
                    />
                    {option.label}
                  </label>
                ))}
              </div>
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
                placeholder="Optional notes about this season"
                maxLength={SEASON_NOTES_MAX}
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
              {mode === 'create' ? 'Create Season' : 'Save Changes'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
