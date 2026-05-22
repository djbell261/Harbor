import type { ResourceDetail } from '../../types/resource';

const STORAGE_KEY = 'harbor.offlineResources.v1';
const MAX_SNAPSHOTS = 8;

export interface OfflineResourceSnapshot {
  id: string;
  name: string;
  categoryName: string;
  city: string | null;
  region: string | null;
  phone: string | null;
  savedAt: string;
  resource: ResourceDetail;
}

export function getOfflineSnapshots(): OfflineResourceSnapshot[] {
  try {
    const rawValue = localStorage.getItem(STORAGE_KEY);
    return rawValue ? (JSON.parse(rawValue) as OfflineResourceSnapshot[]) : [];
  } catch {
    return [];
  }
}

export function saveOfflineSnapshot(resource: ResourceDetail) {
  try {
    const snapshots = getOfflineSnapshots().filter((snapshot) => snapshot.id !== resource.id);
    const nextSnapshot: OfflineResourceSnapshot = {
      id: resource.id,
      name: resource.name,
      categoryName: resource.categoryName,
      city: resource.city,
      region: resource.region,
      phone: resource.phone,
      savedAt: new Date().toISOString(),
      resource
    };

    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify([nextSnapshot, ...snapshots].slice(0, MAX_SNAPSHOTS))
    );

    return getOfflineSnapshots();
  } catch {
    return getOfflineSnapshots();
  }
}
