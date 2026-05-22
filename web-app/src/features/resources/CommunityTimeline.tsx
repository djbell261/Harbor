import type { CommunityUpdate } from '../../types/resource';
import { formatRelativeTime } from './formatters';

export function CommunityTimeline({ updates }: { updates: CommunityUpdate[] }) {
  if (updates.length === 0) {
    return (
      <p className="mt-3 text-sm text-harbor-muted">
        No recent anonymous community updates yet.
      </p>
    );
  }

  return (
    <ol className="mt-3 space-y-3">
      {updates.map((update) => (
        <li key={update.id} className="border-l-2 border-harbor-line pl-3">
          <p className="text-sm font-medium text-harbor-ink">{update.message}</p>
          <p className="mt-1 text-xs text-harbor-muted">{formatRelativeTime(update.createdAt)}</p>
        </li>
      ))}
    </ol>
  );
}
