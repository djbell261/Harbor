import { Button } from '../../components/Button';
import { Card } from '../../components/Card';
import { StatusBadge } from '../../components/StatusBadge';
import type { ReactNode } from 'react';
import type { ResourceDetail as ResourceDetailType } from '../../types/resource';
import { VerificationReportForm } from '../verification/VerificationReportForm';
import { CommunityTimeline } from './CommunityTimeline';
import {
  formatFreshness,
  formatHourRange,
  formatPercent,
  getOpenNowLabel
} from './formatters';
import { TrustIndicators } from './TrustIndicators';

interface ResourceDetailProps {
  resource: ResourceDetailType;
  onBack: () => void;
  onSaveOffline: (resource: ResourceDetailType) => void;
  savedOffline: boolean;
}

export function ResourceDetail({
  resource,
  onBack,
  onSaveOffline,
  savedOffline
}: ResourceDetailProps) {
  const status = resource.currentStatus?.status ?? 'unknown';
  const openNowLabel = getOpenNowLabel(resource.hours);
  const address = [
    resource.addressLine1,
    resource.addressLine2,
    [resource.city, resource.region, resource.postalCode].filter(Boolean).join(', ')
  ].filter(Boolean);

  return (
    <div className="space-y-4">
      <Button variant="quiet" onClick={onBack}>
        Back to results
      </Button>

      <Card as="article">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <p className="text-sm font-semibold text-harbor-blue">{resource.categoryName}</p>
            <h2 className="mt-1 text-2xl font-semibold leading-8 text-harbor-ink">
              {resource.name}
            </h2>
          </div>
          <StatusBadge status={status} />
        </div>
        <TrustIndicators verification={resource.verification} />

        {resource.description && (
          <p className="mt-4 text-sm leading-6 text-harbor-muted">{resource.description}</p>
        )}

        <div
          className="mt-4 border border-emerald-200 bg-emerald-50 p-3 text-sm font-semibold text-emerald-900"
          aria-live="polite"
        >
          {openNowLabel}
        </div>

        <dl className="mt-5 grid gap-4 text-sm sm:grid-cols-2">
          <InfoItem label="Address" value={address.length ? address.join('\n') : 'Not listed'} />
          <InfoItem
            label="Phone"
            value={
              resource.phone ? (
                <a className="text-harbor-blue underline-offset-4 hover:underline" href={`tel:${resource.phone}`}>
                  {resource.phone}
                </a>
              ) : (
                'Not listed'
              )
            }
          />
          <InfoItem
            label="Website"
            value={
              resource.websiteUrl ? (
                <a
                  className="break-words text-harbor-blue underline-offset-4 hover:underline"
                  href={resource.websiteUrl}
                  target="_blank"
                  rel="noreferrer"
                >
                  {resource.websiteUrl}
                </a>
              ) : (
                'Not listed'
              )
            }
          />
          <InfoItem label="Last verified" value={formatFreshness(resource.lastVerifiedAt)} />
          <InfoItem label="Confidence" value={formatPercent(resource.confidenceScore)} />
          <InfoItem
            label="Organization"
            value={resource.organization?.name ?? 'Not listed by the current API'}
          />
        </dl>
      </Card>

      <Card as="section" aria-labelledby="hours-heading">
        <h3 id="hours-heading" className="text-base font-semibold text-harbor-ink">
          Hours
        </h3>
        <div className="mt-3 divide-y divide-harbor-line border border-harbor-line">
          {resource.hours.length > 0 ? (
            resource.hours.map((hour) => (
              <div key={hour.id} className="grid grid-cols-[4rem_1fr] gap-3 p-3 text-sm sm:grid-cols-[5rem_1fr]">
                <span className="font-semibold text-harbor-ink">{dayLabel(hour.dayOfWeek)}</span>
                <span className="text-harbor-muted">
                  {formatHourRange(hour)}
                  {hour.notes ? ` - ${hour.notes}` : ''}
                </span>
              </div>
            ))
          ) : (
            <p className="p-3 text-sm text-harbor-muted">Hours are not listed yet.</p>
          )}
        </div>
      </Card>

      <section className="grid gap-4 md:grid-cols-3">
        <NoteBlock title="Eligibility" value={resource.eligibilityNotes} />
        <NoteBlock title="Intake" value={resource.intakeNotes} />
        <NoteBlock title="Accessibility" value={resource.accessibilityNotes} />
      </section>

      <Card as="section" aria-labelledby="community-updates-heading">
        <h3 id="community-updates-heading" className="text-base font-semibold text-harbor-ink">
          Recent community updates
        </h3>
        <CommunityTimeline updates={resource.communityUpdates ?? []} />
      </Card>

      <Card as="section" aria-labelledby="offline-save-heading">
        <h3 id="offline-save-heading" className="text-base font-semibold text-harbor-ink">
          Offline access
        </h3>
        <p className="mt-2 text-sm leading-6 text-harbor-muted">
          Save this resource on this device for quick access if your connection becomes unstable.
        </p>
        <Button className="mt-3" fullWidth onClick={() => onSaveOffline(resource)}>
          {savedOffline ? 'Saved for offline use' : 'Save for offline use'}
        </Button>
      </Card>

      <VerificationReportForm resourceId={resource.id} />
    </div>
  );
}

function InfoItem({
  label,
  value
}: {
  label: string;
  value: string | ReactNode;
}) {
  return (
    <div>
      <dt className="text-xs font-semibold uppercase text-harbor-muted">{label}</dt>
      <dd className="mt-1 whitespace-pre-line text-harbor-ink">{value}</dd>
    </div>
  );
}

function NoteBlock({ title, value }: { title: string; value: string | null }) {
  return (
    <Card>
      <h3 className="text-sm font-semibold text-harbor-ink">{title}</h3>
      <p className="mt-2 text-sm leading-6 text-harbor-muted">{value ?? 'Not listed yet.'}</p>
    </Card>
  );
}

function dayLabel(day: number) {
  return ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][day] ?? 'Day';
}
