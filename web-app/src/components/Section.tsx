import type { ReactNode } from 'react';

interface SectionProps {
  title: string;
  description?: string;
  children: ReactNode;
  actions?: ReactNode;
}

export function Section({ title, description, children, actions }: SectionProps) {
  return (
    <section aria-labelledby={sectionId(title)}>
      <div className="mb-3 flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h2 id={sectionId(title)} className="text-base font-semibold text-harbor-ink">
            {title}
          </h2>
          {description && <p className="mt-1 text-sm text-harbor-muted">{description}</p>}
        </div>
        {actions}
      </div>
      {children}
    </section>
  );
}

function sectionId(title: string) {
  return title.toLowerCase().replace(/[^a-z0-9]+/g, '-');
}
