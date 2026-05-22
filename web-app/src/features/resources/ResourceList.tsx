import { EmptyState } from '../../components/EmptyState';
import { Button } from '../../components/Button';
import type { ResourceSummary } from '../../types/resource';
import { ResourceCard } from './ResourceCard';

interface ResourceListProps {
  resources: ResourceSummary[];
  onOpenResource: (id: string) => void;
  onClearFilters: () => void;
}

export function ResourceList({ resources, onOpenResource, onClearFilters }: ResourceListProps) {
  if (resources.length === 0) {
    return (
      <EmptyState
        title="No resources found"
        message="Try clearing the category filter or searching another city."
        action={<Button onClick={onClearFilters}>Clear filters</Button>}
      />
    );
  }

  return (
    <div className="grid gap-3 sm:gap-4 md:grid-cols-2">
      {resources.map((resource) => (
        <ResourceCard key={resource.id} resource={resource} onOpen={onOpenResource} />
      ))}
    </div>
  );
}
