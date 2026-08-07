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
import type {
  MasterDataCreateInput,
  MasterDataModuleConfig,
  MasterDataRecord,
} from '../types/master-data.types';
import { StatusToggle } from './status-toggle';

export const MASTER_DATA_NAME_MAX = 100;
export const MASTER_DATA_CODE_MAX = 20;
export const MASTER_DATA_DESCRIPTION_MAX = 500;

const CODE_PATTERN = /^[A-Z][A-Z0-9_]*$/;

export interface MasterDataFormValues {
  name: string;
  code: string;
  description: string;
  status: 'ACTIVE' | 'INACTIVE';
}

interface FormErrors {
  name?: string;
  code?: string;
  description?: string;
  general?: string;
}

interface MasterDataFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  mode: 'create' | 'edit';
  record?: MasterDataRecord | null;
  config: MasterDataModuleConfig;
  existing: MasterDataRecord[];
  onSubmit: (input: MasterDataCreateInput) => Promise<unknown>;
}

export function MasterDataFormDialog({
  open,
  onOpenChange,
  mode,
  record,
  config,
  existing,
  onSubmit,
}: MasterDataFormDialogProps) {
  const [name, setName] = useState(record?.name ?? '');
  const [code, setCode] = useState(record?.code ?? '');
  const [description, setDescription] = useState(record?.description ?? '');
  const [status, setStatus] = useState<'ACTIVE' | 'INACTIVE'>(record?.status ?? 'ACTIVE');
  const [errors, setErrors] = useState<FormErrors>({});
  const [isSaving, setIsSaving] = useState(false);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const normalizedName = name.trim();
    const normalizedCode = code.trim().toUpperCase();
    const normalizedDescription = description.trim();

    const nextErrors: FormErrors = {};
    if (!normalizedName) {
      nextErrors.name = 'Name is required.';
    } else if (normalizedName.length < 2) {
      nextErrors.name = 'Name must be at least 2 characters.';
    } else if (normalizedName.length > MASTER_DATA_NAME_MAX) {
      nextErrors.name = `Name must be ${MASTER_DATA_NAME_MAX} characters or fewer.`;
    }

    if (!normalizedCode) {
      nextErrors.code = 'Code is required.';
    } else if (normalizedCode.length < 2) {
      nextErrors.code = 'Code must be at least 2 characters.';
    } else if (normalizedCode.length > MASTER_DATA_CODE_MAX) {
      nextErrors.code = `Code must be ${MASTER_DATA_CODE_MAX} characters or fewer.`;
    } else if (!CODE_PATTERN.test(normalizedCode)) {
      nextErrors.code = 'Code must start with a letter and use uppercase letters, numbers or underscores.';
    } else if (
      existing.some(
        (item) => item.code === normalizedCode && item.id !== (record?.id ?? ''),
      )
    ) {
      nextErrors.code = `A record with code "${normalizedCode}" already exists.`;
    }

    if (normalizedDescription.length > MASTER_DATA_DESCRIPTION_MAX) {
      nextErrors.description = `Description must be ${MASTER_DATA_DESCRIPTION_MAX} characters or fewer.`;
    }

    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) {
      return;
    }

    setIsSaving(true);
    try {
      await onSubmit({
        name: normalizedName,
        code: normalizedCode,
        description: normalizedDescription,
        status,
      });
      onOpenChange(false);
    } catch {
      setErrors({ general: 'Could not save the record. Please try again.' });
    } finally {
      setIsSaving(false);
    }
  };

  const baseInputId = `md-${config.key}`;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>
            {mode === 'create' ? config.addDialogTitle : config.editDialogTitle}
          </DialogTitle>
          <DialogDescription>
            {mode === 'create' ? config.addDialogDescription : `Update the ${config.singular.toLowerCase()} details below.`}
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} noValidate>
          <div className="space-y-4">
            {errors.general ? (
              <ValidationMessage variant="error">{errors.general}</ValidationMessage>
            ) : null}

            <FormField
              label={config.nameLabel}
              htmlFor={`${baseInputId}-name`}
              required
              error={errors.name}
              hint={config.nameHint}
            >
              <Input
                id={`${baseInputId}-name`}
                value={name}
                onChange={(event) => setName(event.target.value)}
                placeholder={config.namePlaceholder}
                maxLength={MASTER_DATA_NAME_MAX}
                autoFocus
                disabled={isSaving}
                aria-invalid={Boolean(errors.name)}
              />
            </FormField>

            <FormField
              label={config.codeLabel}
              htmlFor={`${baseInputId}-code`}
              required
              error={errors.code}
              hint={config.codeHint}
            >
              <Input
                id={`${baseInputId}-code`}
                value={code}
                onChange={(event) => setCode(event.target.value)}
                placeholder={config.codePlaceholder}
                maxLength={MASTER_DATA_CODE_MAX}
                className="font-mono uppercase"
                disabled={isSaving}
                aria-invalid={Boolean(errors.code)}
              />
            </FormField>

            <FormField
              label={config.descriptionLabel}
              htmlFor={`${baseInputId}-description`}
              error={errors.description}
            >
              <textarea
                id={`${baseInputId}-description`}
                value={description}
                onChange={(event) => setDescription(event.target.value)}
                placeholder={config.descriptionPlaceholder}
                maxLength={MASTER_DATA_DESCRIPTION_MAX}
                rows={3}
                disabled={isSaving}
                aria-invalid={Boolean(errors.description)}
                className="flex w-full resize-none rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm transition-colors placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
              />
            </FormField>

            <FormField label="Status">
              <div className="flex items-center gap-2">
                <StatusToggle
                  checked={status === 'ACTIVE'}
                  onChange={(checked) => setStatus(checked ? 'ACTIVE' : 'INACTIVE')}
                  label="Record status"
                  disabled={isSaving}
                />
                <span className="text-sm text-muted-foreground">
                  {status === 'ACTIVE' ? 'Active' : 'Inactive'}
                </span>
              </div>
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
              {mode === 'create' ? 'Save' : 'Save Changes'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
