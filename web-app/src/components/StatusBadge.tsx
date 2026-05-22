import type { ResourceStatus } from '../types/resource';

const statusStyles: Record<ResourceStatus, string> = {
  open: 'border-emerald-300 bg-emerald-50 text-emerald-800',
  limited: 'border-amber-300 bg-amber-50 text-amber-800',
  unknown: 'border-slate-300 bg-slate-50 text-slate-700',
  closed: 'border-rose-300 bg-rose-50 text-rose-800',
  temporarily_closed: 'border-rose-300 bg-rose-50 text-rose-800'
};

const statusLabels: Record<ResourceStatus, string> = {
  open: 'OPEN',
  limited: 'LIMITED',
  unknown: 'UNKNOWN',
  closed: 'CLOSED',
  temporarily_closed: 'CLOSED'
};

export function StatusBadge({ status }: { status: ResourceStatus }) {
  return (
    <span
      className={`inline-flex min-h-8 items-center rounded-full border px-3 py-1 text-xs font-bold tracking-wide ${statusStyles[status]}`}
    >
      {statusLabels[status]}
    </span>
  );
}
