import { Button } from '../../components/Button';
import { Card } from '../../components/Card';
import { FormEvent, useState } from 'react';
import { ApiError } from '../../api/client';
import { harborApi } from '../../api/harborApi';
import type { VerificationReportType } from '../../types/verification';

const reportTypes: Array<{ value: VerificationReportType; label: string }> = [
  { value: 'food_unavailable', label: 'Food unavailable' },
  { value: 'shelter_full', label: 'Shelter full' },
  { value: 'restroom_closed', label: 'Restroom closed' },
  { value: 'wifi_offline', label: 'Wi-Fi offline' },
  { value: 'unsafe_location', label: 'Unsafe location' },
  { value: 'incorrect_hours', label: 'Incorrect hours' },
  { value: 'inaccessible', label: 'Inaccessible' },
  { value: 'other', label: 'Other' }
];

export function VerificationReportForm({ resourceId }: { resourceId: string }) {
  const [reportType, setReportType] = useState<VerificationReportType>('incorrect_hours');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setSuccess('');
    setError('');

    try {
      await harborApi.createVerificationReport(resourceId, {
        reportType,
        description: description.trim(),
        suggestedValue: {
          reporterKind: 'anonymous'
        }
      });
      setDescription('');
      setSuccess('Thanks. Harbor received your report for review.');
    } catch (error) {
      if (error instanceof ApiError) {
        const reference = error.correlationId ? ` Reference: ${error.correlationId}` : '';
        setError(`${error.message || 'Harbor could not submit the report.'}${reference}`);
      } else {
        setError('Harbor could not submit the report. Please try again.');
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Card as="section" aria-labelledby="report-heading">
      <h3 id="report-heading" className="text-base font-semibold text-harbor-ink">
        Report incorrect information
      </h3>
      <form className="mt-4 space-y-4" onSubmit={handleSubmit}>
        <div>
          <label className="text-sm font-semibold text-harbor-ink" htmlFor="reportType">
            What needs attention?
          </label>
          <select
            id="reportType"
            className="mt-2 min-h-11 w-full border border-harbor-line bg-white px-3 py-2 text-base text-harbor-ink focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-harbor-blue sm:text-sm"
            value={reportType}
            onChange={(event) => setReportType(event.target.value as VerificationReportType)}
          >
            {reportTypes.map((type) => (
              <option key={type.value} value={type.value}>
                {type.label}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="text-sm font-semibold text-harbor-ink" htmlFor="description">
            Details
          </label>
          <textarea
            id="description"
            className="mt-2 min-h-32 w-full resize-y border border-harbor-line bg-white px-3 py-2 text-base text-harbor-ink focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-harbor-blue sm:text-sm"
            maxLength={2000}
            placeholder="Share what changed, what you saw, or what someone should confirm."
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            required
          />
          <p className="mt-1 text-xs text-harbor-muted">Submitted anonymously.</p>
        </div>

        {success && (
          <p className="border border-green-200 bg-green-50 p-3 text-sm text-harbor-green" role="status">
            {success}
          </p>
        )}
        {error && <p className="border border-red-200 bg-red-50 p-3 text-sm text-harbor-red">{error}</p>}

        <Button type="submit" variant="primary" fullWidth disabled={submitting} className="sm:w-auto">
          {submitting ? 'Submitting...' : 'Submit report'}
        </Button>
      </form>
    </Card>
  );
}
