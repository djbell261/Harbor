import { Button } from './Button';

export function ErrorBanner({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <div
      className="flex flex-col gap-3 border border-red-200 bg-red-50 p-4 text-sm text-harbor-red shadow-soft sm:flex-row sm:items-center sm:justify-between"
      role="alert"
    >
      <span>{message}</span>
      {onRetry && (
        <Button variant="danger" onClick={onRetry}>
          Retry
        </Button>
      )}
    </div>
  );
}
