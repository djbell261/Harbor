import { FormEvent, useState } from 'react';
import { Button } from '../components/Button';
import { Card } from '../components/Card';
import { ErrorBanner } from '../components/ErrorBanner';
import { LoadingState } from '../components/LoadingState';
import { Section } from '../components/Section';
import { CategoryGrid } from '../features/categories/CategoryGrid';
import { OfflineSnapshots } from '../features/resources/OfflineResourceSnapshots';
import type { OfflineResourceSnapshot } from '../features/resources/offlineSnapshots';
import { ResourceDetail } from '../features/resources/ResourceDetail';
import { ResourceList } from '../features/resources/ResourceList';
import { ResourceMap } from '../features/resources/ResourceMap';
import type { Category } from '../types/category';
import type { ResourceDetail as ResourceDetailType, ResourceSummary } from '../types/resource';

interface HomePageProps {
  categories: Category[];
  resources: ResourceSummary[];
  selectedResource: ResourceDetailType | null;
  selectedCategory: string;
  city: string;
  loading: boolean;
  detailLoading: boolean;
  error: string;
  offlineSnapshots: OfflineResourceSnapshot[];
  onCategoryChange: (category: string) => void;
  onCityChange: (city: string) => void;
  onSubmitSearch: () => void;
  onOpenResource: (id: string) => void;
  onBackToResults: () => void;
  onClearFilters: () => void;
  onSaveOffline: (resource: ResourceDetailType) => void;
  onOpenOfflineSnapshot: (snapshot: OfflineResourceSnapshot) => void;
}

const quickFilters = [
  { label: 'Food', value: 'food' },
  { label: 'Shelter', value: 'shelter' },
  { label: 'Clinic', value: 'clinic' },
  { label: 'Wi-Fi', value: 'charging_wifi' }
];

const emergencyFilters = [
  { label: 'Food', value: 'food' },
  { label: 'Shelter', value: 'shelter' },
  { label: 'Restrooms', value: 'restroom' },
  { label: 'Warming/Cooling', value: 'warming_cooling' },
  { label: 'Transportation', value: 'transportation' }
];

export function HomePage({
  categories,
  resources,
  selectedResource,
  selectedCategory,
  city,
  loading,
  detailLoading,
  error,
  offlineSnapshots,
  onCategoryChange,
  onCityChange,
  onSubmitSearch,
  onOpenResource,
  onBackToResults,
  onClearFilters,
  onSaveOffline,
  onOpenOfflineSnapshot
}: HomePageProps) {
  const [showMap, setShowMap] = useState(false);

  function handleSearchSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    onSubmitSearch();
  }

  return (
    <main className="min-h-screen bg-harbor-wash">
      <div className="mx-auto max-w-6xl px-3 py-4 sm:px-6 sm:py-6 lg:px-8">
        <header className="border-b border-harbor-line pb-4 sm:pb-5">
          <p className="text-sm font-semibold uppercase tracking-wide text-harbor-blue">
            Privacy-first survival assistance
          </p>
          <h1 className="mt-2 text-3xl font-semibold leading-tight text-harbor-ink sm:text-4xl">
            Harbor
          </h1>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-harbor-muted sm:text-base sm:leading-7">
            Find nearby food, shelter, clinics, restrooms, Wi-Fi, transportation help, and safe
            indoor spaces without creating an account.
          </p>
        </header>

        <section className="py-4 sm:py-5" aria-labelledby="immediate-help-heading">
          <Card className="border-harbor-blue bg-[#f4fbfd]">
            <h2 id="immediate-help-heading" className="text-lg font-semibold text-harbor-ink">
              Need immediate help?
            </h2>
            <p className="mt-1 text-sm leading-6 text-harbor-muted">
              Jump to common emergency resource types.
            </p>
            <div className="mt-4 grid grid-cols-1 gap-2 min-[380px]:grid-cols-2 sm:grid-cols-5">
              {emergencyFilters.map((filter) => (
                <Button
                  key={filter.value}
                  className="justify-center px-3"
                  fullWidth
                  variant={selectedCategory === filter.value ? 'primary' : 'secondary'}
                  onClick={() => onCategoryChange(filter.value)}
                >
                  {filter.label}
                </Button>
              ))}
            </div>
          </Card>
        </section>

        <div className="grid gap-5 pb-6 lg:grid-cols-[18rem_1fr] lg:gap-6">
          <aside className="space-y-4 lg:sticky lg:top-4 lg:self-start">
            <Card as="section">
              <form onSubmit={handleSearchSubmit}>
                <label className="text-sm font-semibold text-harbor-ink" htmlFor="city">
                  City
                </label>
                <div className="mt-2 flex flex-col gap-2 min-[420px]:flex-row">
                  <input
                    id="city"
                    className="min-h-11 min-w-0 flex-1 border border-harbor-line px-3 py-2 text-base text-harbor-ink focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-harbor-blue sm:text-sm"
                    value={city}
                    onChange={(event) => onCityChange(event.target.value)}
                    placeholder="Wilmington"
                  />
                  <Button type="submit" variant="primary">
                    Search
                  </Button>
                </div>
              </form>
            </Card>

            <Card as="section">
              <h2 className="text-sm font-semibold text-harbor-ink">Quick filters</h2>
              <div className="mt-3 flex flex-wrap gap-2">
                {quickFilters.map((filter) => (
                  <Button
                    key={filter.value}
                    variant={selectedCategory === filter.value ? 'primary' : 'secondary'}
                    onClick={() => onCategoryChange(selectedCategory === filter.value ? '' : filter.value)}
                  >
                    {filter.label}
                  </Button>
                ))}
              </div>
            </Card>

            <OfflineSnapshots
              snapshots={offlineSnapshots}
              onOpenSnapshot={onOpenOfflineSnapshot}
            />
          </aside>

          <div className="space-y-5 sm:space-y-6">
            {selectedResource ? (
              <ResourceDetail
                resource={selectedResource}
                onBack={onBackToResults}
                onSaveOffline={onSaveOffline}
                savedOffline={offlineSnapshots.some((snapshot) => snapshot.id === selectedResource.id)}
              />
            ) : (
              <>
                <CategoryGrid
                  categories={categories}
                  selectedCategory={selectedCategory}
                  onSelectCategory={onCategoryChange}
                />

                <Section
                  title="Resources"
                  description={`Showing ${resources.length} result${resources.length === 1 ? '' : 's'}${
                    city ? ` for ${city}` : ''
                  }.`}
                  actions={
                    <Button
                      variant="secondary"
                      aria-pressed={showMap}
                      onClick={() => setShowMap(!showMap)}
                    >
                      {showMap ? 'Show list' : 'Show map'}
                    </Button>
                  }
                >
                  {error && <ErrorBanner message={error} onRetry={onSubmitSearch} />}
                  {loading ? (
                    <LoadingState />
                  ) : detailLoading ? (
                    <LoadingState label="Opening resource details" />
                  ) : showMap ? (
                    <ResourceMap resources={resources} onOpenResource={onOpenResource} />
                  ) : (
                    <ResourceList
                      resources={resources}
                      onOpenResource={onOpenResource}
                      onClearFilters={onClearFilters}
                    />
                  )}
                </Section>
              </>
            )}
          </div>
        </div>
      </div>
    </main>
  );
}
