import type { ReactNode } from 'react';

interface EmptyStateProps {
  title: string;
  message: string;
  action?: ReactNode;
}

export function EmptyState({ title, message, action }: EmptyStateProps) {
  return (
    <div className="border border-dashed border-harbor-line bg-white p-5 text-center shadow-soft sm:p-6">
      <p className="text-sm font-semibold text-harbor-ink">{title}</p>
      <p className="mt-1 text-sm text-harbor-muted">{message}</p>
      {action && <div className="mt-4 flex justify-center">{action}</div>}
    </div>
  );
}
