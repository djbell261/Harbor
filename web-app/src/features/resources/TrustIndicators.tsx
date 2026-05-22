import type { VerificationMetadata } from '../../types/resource';
import { formatFreshness } from './formatters';

export function TrustIndicators({ verification }: { verification?: VerificationMetadata }) {
  if (!verification) {
    return null;
  }

  const items = [
    verification.communityConfirmed ? 'Community confirmed' : null,
    verification.recentlyUpdated ? 'Recently updated' : null,
    verification.reportCount > 0 ? `${verification.reportCount} community report${verification.reportCount === 1 ? '' : 's'}` : null,
    verification.lastCommunityReportAt ? formatFreshness(verification.lastCommunityReportAt) : null
  ].filter(Boolean);

  if (items.length === 0) {
    return null;
  }

  return (
    <ul className="mt-3 flex flex-wrap gap-2 text-xs text-harbor-muted" aria-label="Trust and freshness indicators">
      {items.map((item) => (
        <li key={item} className="border border-harbor-line bg-harbor-wash px-2 py-1">
          {item}
        </li>
      ))}
    </ul>
  );
}
