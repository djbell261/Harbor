import { apiClient } from './client';
import type { Category } from '../types/category';
import type { Organization } from '../types/organization';
import type { ResourceDetail, ResourceSummary } from '../types/resource';
import type {
  CreateVerificationReportRequest,
  VerificationReportResponse
} from '../types/verification';

export interface ResourceFilters {
  category?: string;
  city?: string;
  page?: number;
  size?: number;
}

function buildQuery(filters: ResourceFilters): string {
  const params = new URLSearchParams();

  if (filters.category) {
    params.set('category', filters.category);
  }

  if (filters.city) {
    params.set('city', filters.city);
  }

  if (filters.page !== undefined) {
    params.set('page', String(filters.page));
  }

  if (filters.size !== undefined) {
    params.set('size', String(filters.size));
  }

  const query = params.toString();
  return query ? `?${query}` : '';
}

export const harborApi = {
  getCategories: () => apiClient.get<Category[]>('/api/categories'),
  getOrganizations: () => apiClient.get<Organization[]>('/api/organizations'),
  getResources: (filters: ResourceFilters) =>
    apiClient.get<ResourceSummary[]>(`/api/resources${buildQuery(filters)}`),
  getResource: (id: string) => apiClient.get<ResourceDetail>(`/api/resources/${id}`),
  createVerificationReport: (resourceId: string, request: CreateVerificationReportRequest) =>
    apiClient.post<VerificationReportResponse>(
      `/api/resources/${resourceId}/verification-reports`,
      request
    )
};
