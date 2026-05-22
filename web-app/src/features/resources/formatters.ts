import type { ResourceHour } from '../../types/resource';

export function formatDate(value: string | null) {
  if (!value) {
    return 'Not listed';
  }

  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  }).format(new Date(value));
}

export function formatFreshness(value: string | null) {
  if (!value) {
    return 'Verification date not listed';
  }

  const verified = startOfDay(new Date(value));
  const today = startOfDay(new Date());
  const diffDays = Math.max(
    0,
    Math.floor((today.getTime() - verified.getTime()) / (1000 * 60 * 60 * 24))
  );

  if (diffDays === 0) {
    return 'Verified today';
  }

  if (diffDays === 1) {
    return 'Verified yesterday';
  }

  return `Verified ${diffDays} days ago`;
}

export function formatRelativeTime(value: string | null) {
  if (!value) {
    return 'recently';
  }

  const then = new Date(value).getTime();
  const now = Date.now();
  const diffMinutes = Math.max(0, Math.floor((now - then) / (1000 * 60)));

  if (diffMinutes < 1) {
    return 'just now';
  }

  if (diffMinutes < 60) {
    return `${diffMinutes} minute${diffMinutes === 1 ? '' : 's'} ago`;
  }

  const diffHours = Math.floor(diffMinutes / 60);
  if (diffHours < 24) {
    return `${diffHours} hour${diffHours === 1 ? '' : 's'} ago`;
  }

  const diffDays = Math.floor(diffHours / 24);
  if (diffDays === 1) {
    return 'yesterday';
  }

  return `${diffDays} days ago`;
}

export function formatPercent(value: number | null | undefined) {
  if (value === null || value === undefined) {
    return 'Not listed';
  }

  return `${Math.round(Number(value) * 100)}%`;
}

export function formatHourRange(hour: ResourceHour) {
  if (hour.closed) {
    return 'Closed';
  }

  if (!hour.opensAt || !hour.closesAt) {
    return 'Hours vary';
  }

  return `${formatTime(hour.opensAt)} to ${formatTime(hour.closesAt)}`;
}

export function getOpenNowLabel(hours: ResourceHour[]) {
  const now = new Date();
  const today = now.getDay();
  const nowMinutes = now.getHours() * 60 + now.getMinutes();
  const todaysHours = hours
    .filter((hour) => hour.dayOfWeek === today && !hour.closed && hour.opensAt && hour.closesAt)
    .sort((a, b) => minutesFromTime(a.opensAt) - minutesFromTime(b.opensAt));

  const active = todaysHours.find((hour) => {
    const opens = minutesFromTime(hour.opensAt);
    const closes = minutesFromTime(hour.closesAt);
    return nowMinutes >= opens && nowMinutes < closes;
  });

  if (active) {
    return 'OPEN NOW';
  }

  const laterToday = todaysHours.find((hour) => minutesFromTime(hour.opensAt) > nowMinutes);
  if (laterToday?.opensAt) {
    return `Opens at ${formatTime(laterToday.opensAt)} today`;
  }

  for (let offset = 1; offset <= 7; offset += 1) {
    const day = (today + offset) % 7;
    const next = hours
      .filter((hour) => hour.dayOfWeek === day && !hour.closed && hour.opensAt)
      .sort((a, b) => minutesFromTime(a.opensAt) - minutesFromTime(b.opensAt))[0];

    if (next?.opensAt) {
      return offset === 1
        ? `Opens tomorrow at ${formatTime(next.opensAt)}`
        : `Opens ${dayLabel(day)} at ${formatTime(next.opensAt)}`;
    }
  }

  return 'CLOSED';
}

function formatTime(value: string) {
  const [hours, minutes] = value.split(':').map(Number);
  const date = new Date();
  date.setHours(hours, minutes, 0, 0);

  return new Intl.DateTimeFormat(undefined, {
    hour: 'numeric',
    minute: '2-digit'
  }).format(date);
}

function minutesFromTime(value: string | null) {
  if (!value) {
    return Number.POSITIVE_INFINITY;
  }

  const [hours, minutes] = value.split(':').map(Number);
  return hours * 60 + minutes;
}

function startOfDay(value: Date) {
  return new Date(value.getFullYear(), value.getMonth(), value.getDate());
}

function dayLabel(day: number) {
  return ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][day] ?? 'soon';
}
