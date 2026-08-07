'use client';

import { ConfirmDialog, type ConfirmDialogProps } from '@/components/dialogs/confirm-dialog';

export interface DeleteDialogProps
  extends Omit<ConfirmDialogProps, 'title' | 'confirmLabel' | 'variant'> {
  itemName?: string;
  title?: string;
  confirmLabel?: string;
}

export function DeleteDialog({
  itemName,
  title = itemName ? `Delete ${itemName}` : 'Delete item',
  confirmLabel = 'Delete',
  description,
  ...props
}: DeleteDialogProps) {
  return (
    <ConfirmDialog
      {...props}
      title={title}
      variant="destructive"
      confirmLabel={confirmLabel}
      description={
        description ?? (
          <>
            This action cannot be undone. This will permanently delete
            {itemName ? (
              <>
                {' '}
                <span className="font-medium text-foreground">{itemName}</span>
              </>
            ) : (
              ' this item'
            )}
            .
          </>
        )
      }
    />
  );
}
