import { Button } from '../../components/Button';
import { Card } from '../../components/Card';
import { StatusBadge } from '../../components/StatusBadge';
import type { ResourceSummary } from '../../types/resource';
import { formatFreshness, formatPercent } from './formatters';
import { TrustIndicators } from './TrustIndicators';

interface ResourceCardProps {
  resource: ResourceSummary;
  onOpen: (id: string) => void;
}

export function ResourceCard({ resource, onOpen }: ResourceCardProps) {
  return (
    <Card as="article" className="flex h-full flex-col">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <h3 className="text-base font-semibold leading-6 text-harbor-ink">{resource.name}</h3>
          <p className="mt-1 text-sm text-harbor-muted">{resource.categoryName}</p>
        </div>
        <StatusBadge status={resource.status} />
      </div>
      <TrustIndicators verification={resource.verification} />

      <dl className="mt-4 grid grid-cols-1 gap-3 text-sm min-[380px]:grid-cols-2">
        <div>
          <dt className="text-xs font-semibold uppercase text-harbor-muted">Location</dt>
          <dd className="mt-1 text-harbor-ink">
            {[resource.city, resource.region].filter(Boolean).join(', ') || 'Not listed'}
          </dd>
        </div>
        <div>
          <dt className="text-xs font-semibold uppercase text-harbor-muted">Phone</dt>
          <dd className="mt-1 text-harbor-ink">
            {resource.phone ? (
              <a className="text-harbor-blue underline-offset-4 hover:underline" href={`tel:${resource.phone}`}>
                {resource.phone}
              </a>
            ) : (
              'Not listed'
            )}
          </dd>
        </div>
        <div>
          <dt className="text-xs font-semibold uppercase text-harbor-muted">Verified</dt>
          <dd className="mt-1 text-harbor-ink">{formatFreshness(resource.lastVerifiedAt)}</dd>
        </div>
        <div>
          <dt className="text-xs font-semibold uppercase text-harbor-muted">Confidence</dt>
          <dd className="mt-1 text-harbor-ink">{formatPercent(resource.confidenceScore)}</dd>
        </div>
      </dl>

      <Button className="mt-4" fullWidth onClick={() => onOpen(resource.id)}>
        View details
      </Button>
    </Card>
  );
}
