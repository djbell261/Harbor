import { useEffect, useState } from 'react';
import { ApiError } from './api/client';
import { harborApi } from './api/harborApi';
import {
  getOfflineSnapshots,
  saveOfflineSnapshot,
  type OfflineResourceSnapshot
} from './features/resources/offlineSnapshots';
import { HomePage } from './pages/HomePage';
import type { Category } from './types/category';
import type { ResourceDetail, ResourceSummary } from './types/resource';

export default function App() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [resources, setResources] = useState<ResourceSummary[]>([]);
  const [selectedResource, setSelectedResource] = useState<ResourceDetail | null>(null);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [city, setCity] = useState('Wilmington');
  const [loading, setLoading] = useState(true);
  const [detailLoading, setDetailLoading] = useState(false);
  const [error, setError] = useState('');
  const [offlineSnapshots, setOfflineSnapshots] = useState<OfflineResourceSnapshot[]>([]);

  useEffect(() => {
    async function loadInitialData() {
      setLoading(true);
      setError('');

      try {
        const [categoryData, resourceData] = await Promise.all([
          harborApi.getCategories(),
          harborApi.getResources({ city: 'Wilmington' })
        ]);
        setCategories(categoryData);
        setResources(resourceData);
      } catch (error) {
        setError(toUserMessage(error, 'Harbor could not reach the backend. Check that resource-service is running.'));
      } finally {
        setLoading(false);
      }
    }

    void loadInitialData();
    setOfflineSnapshots(getOfflineSnapshots());
  }, []);

  useEffect(() => {
    if (loading) {
      return;
    }

    void loadResources();
  }, [selectedCategory]);

  async function loadResources() {
    setLoading(true);
    setError('');
    setSelectedResource(null);

    try {
      const data = await harborApi.getResources({
        category: selectedCategory || undefined,
        city: city.trim() || undefined
      });
      setResources(data);
    } catch (error) {
      setError(toUserMessage(error, 'Harbor could not load resources. Please try again.'));
    } finally {
      setLoading(false);
    }
  }

  async function clearFilters() {
    setSelectedCategory('');
    setCity('Wilmington');
    setLoading(true);
    setError('');
    setSelectedResource(null);

    try {
      const data = await harborApi.getResources({ city: 'Wilmington' });
      setResources(data);
    } catch (error) {
      setError(toUserMessage(error, 'Harbor could not load resources. Please try again.'));
    } finally {
      setLoading(false);
    }
  }

  async function openResource(id: string) {
    setDetailLoading(true);
    setError('');

    try {
      const resource = await harborApi.getResource(id);
      setSelectedResource(resource);
      setOfflineSnapshots(saveOfflineSnapshot(resource));
    } catch (error) {
      setError(toUserMessage(error, 'Harbor could not open that resource.'));
    } finally {
      setDetailLoading(false);
    }
  }

  function saveResourceOffline(resource: ResourceDetail) {
    setOfflineSnapshots(saveOfflineSnapshot(resource));
  }

  function openOfflineSnapshot(snapshot: OfflineResourceSnapshot) {
    setSelectedResource(snapshot.resource);
  }

  return (
    <HomePage
      categories={categories}
      resources={resources}
      selectedResource={selectedResource}
      selectedCategory={selectedCategory}
      city={city}
      loading={loading}
      detailLoading={detailLoading}
      error={error}
      offlineSnapshots={offlineSnapshots}
      onCategoryChange={setSelectedCategory}
      onCityChange={setCity}
      onSubmitSearch={loadResources}
      onOpenResource={openResource}
      onBackToResults={() => setSelectedResource(null)}
      onClearFilters={clearFilters}
      onSaveOffline={saveResourceOffline}
      onOpenOfflineSnapshot={openOfflineSnapshot}
    />
  );
}

function toUserMessage(error: unknown, fallback: string) {
  if (error instanceof ApiError) {
    const reference = error.correlationId ? ` Reference: ${error.correlationId}` : '';
    return `${error.message || fallback}${reference}`;
  }

  return fallback;
}
