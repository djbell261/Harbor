import { Component, lazy, Suspense } from 'react';
import type { ReactNode } from 'react';
import { Card } from '../../components/Card';
import { LoadingState } from '../../components/LoadingState';
import type { ResourceSummary } from '../../types/resource';

const LazyResourceMap = lazy(() =>
  import('./ResourceMapInner').then((module) => ({ default: module.ResourceMapInner }))
);

interface ResourceMapProps {
  resources: ResourceSummary[];
  onOpenResource: (id: string) => void;
}

interface MapErrorBoundaryState {
  hasError: boolean;
}

class MapErrorBoundary extends Component<{ children: ReactNode }, MapErrorBoundaryState> {
  state: MapErrorBoundaryState = { hasError: false };

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  render() {
    if (this.state.hasError) {
      return (
        <Card>
          <p className="text-sm text-harbor-muted">
            Map view is unavailable. The resource list is still available.
          </p>
        </Card>
      );
    }

    return this.props.children;
  }
}

export function ResourceMap({ resources, onOpenResource }: ResourceMapProps) {
  return (
    <MapErrorBoundary>
      <Suspense fallback={<LoadingState label="Loading map view" />}>
        <LazyResourceMap resources={resources} onOpenResource={onOpenResource} />
      </Suspense>
    </MapErrorBoundary>
  );
}
