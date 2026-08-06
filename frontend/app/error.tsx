'use client';

export default function Error({ reset }: { reset: () => void }) {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="text-center">
        <h2 className="text-xl font-semibold">Something went wrong</h2>
        <p className="mt-2 text-sm text-muted-foreground">A placeholder error boundary is active.</p>
        <button
          className="mt-4 rounded-md border border-slate-300 px-4 py-2 text-sm"
          onClick={() => reset()}
        >
          Try again
        </button>
      </div>
    </div>
  );
}
