import { Button } from '../../components/Button';
import { Card } from '../../components/Card';
import type { OfflineResourceSnapshot } from './offlineSnapshots';
import { formatRelativeTime } from './formatters';

interface OfflineSnapshotsProps {
  snapshots: OfflineResourceSnapshot[];
  onOpenSnapshot: (snapshot: OfflineResourceSnapshot) => void;
}

export function OfflineSnapshots({ snapshots, onOpenSnapshot }: OfflineSnapshotsProps) {
  if (snapshots.length === 0) {
    return null;
  }

  return (
    <Card as="section" aria-labelledby="offline-heading">
      <h2 id="offline-heading" className="text-sm font-semibold text-harbor-ink">
        Available offline
      </h2>
      <p className="mt-1 text-sm text-harbor-muted">Recently viewed resources saved on this device.</p>
      <div className="mt-3 space-y-2">
        {snapshots.slice(0, 4).map((snapshot) => (
          <div key={snapshot.id} className="border border-harbor-line p-3">
            <p className="text-sm font-semibold text-harbor-ink">{snapshot.name}</p>
            <p className="mt-1 text-xs text-harbor-muted">
              {snapshot.categoryName} · saved {formatRelativeTime(snapshot.savedAt)}
            </p>
            <Button className="mt-3" fullWidth onClick={() => onOpenSnapshot(snapshot)}>
              Open saved copy
            </Button>
          </div>
        ))}
      </div>
    </Card>
  );
}
