'use client';

import { useState } from 'react';
import { FileText, Info, UploadCloud } from 'lucide-react';

import { ContentCard } from '@/components/shared/content-card';
import { EmptyState } from '@/components/shared/empty-state';
import { SectionHeader } from '@/components/shared/section-header';
import { Button } from '@/components/ui/button';
import { formatDateTime, formatFileSize } from '../utils/format';
import type { Farm } from '../types/farm.types';

interface FarmDocumentsSectionProps {
  farm: Farm;
}

export function FarmDocumentsSection({ farm }: FarmDocumentsSectionProps) {
  const [showNotice, setShowNotice] = useState(false);

  return (
    <ContentCard>
      <div className="space-y-4">
        <SectionHeader
          title="Documents"
          description="Land deeds, reports and registrations"
        />

        <div
          role="group"
          aria-label="Document upload area"
          className="flex flex-col items-center justify-center gap-2 rounded-lg border border-dashed px-4 py-8 text-center"
        >
          <span className="flex h-10 w-10 items-center justify-center rounded-full bg-muted">
            <UploadCloud className="h-5 w-5 text-muted-foreground" aria-hidden="true" />
          </span>
          <p className="text-sm font-medium">Upload farm documents</p>
          <p className="text-xs text-muted-foreground">
            Drag &amp; drop or browse files. Upload is simulated in this preview.
          </p>
          <Button
            size="sm"
            variant="outline"
            className="mt-1"
            onClick={() => setShowNotice((current) => !current)}
            aria-pressed={showNotice}
          >
            Choose files
          </Button>
          {showNotice ? (
            <p className="flex items-center gap-1.5 text-xs text-muted-foreground" role="status">
              <Info className="h-3.5 w-3.5" aria-hidden="true" />
              Real document upload will be enabled when the backend is connected.
            </p>
          ) : null}
        </div>

        {farm.documents.length === 0 ? (
          <EmptyState
            title="No documents yet"
            description="Documents uploaded for this farm will appear here."
            icon={FileText}
          />
        ) : (
          <ul className="space-y-2" aria-label="Farm documents">
            {farm.documents.map((document) => (
              <li
                key={document.id}
                className="flex items-start gap-3 rounded-md border px-4 py-3"
              >
                <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-muted">
                  <FileText className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
                </span>
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-medium">{document.name}</p>
                  <p className="truncate text-xs text-muted-foreground">
                    {document.fileName} · {formatFileSize(document.fileSize)}
                  </p>
                </div>
                <span className="shrink-0 text-xs text-muted-foreground">
                  {formatDateTime(document.uploadedAt)}
                </span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </ContentCard>
  );
}
