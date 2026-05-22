export interface ResourceSummary {
  id: string;
  name: string;
  categoryCode: string;
  categoryName: string;
  city: string | null;
  region: string | null;
  postalCode: string | null;
  latitude: number | null;
  longitude: number | null;
  phone: string | null;
  status: ResourceStatus;
  lastVerifiedAt: string | null;
  confidenceScore: number;
  verification: VerificationMetadata;
}

export interface ResourceHour {
  id: string;
  dayOfWeek: number;
  opensAt: string | null;
  closesAt: string | null;
  closed: boolean;
  notes: string | null;
}

export interface CurrentResourceStatus {
  id: string;
  status: ResourceStatus;
  reason: string | null;
  effectiveFrom: string;
  effectiveUntil: string | null;
  reportedByType: string;
}

export interface ResourceOrganization {
  id: string;
  name: string;
  websiteUrl?: string | null;
  phone?: string | null;
  trustedStatus?: string | null;
}

export interface ResourceDetail {
  id: string;
  name: string;
  description: string | null;
  categoryCode: string;
  categoryName: string;
  addressLine1: string | null;
  addressLine2: string | null;
  city: string | null;
  region: string | null;
  postalCode: string | null;
  countryCode: string | null;
  latitude: number | null;
  longitude: number | null;
  phone: string | null;
  websiteUrl: string | null;
  eligibilityNotes: string | null;
  intakeNotes: string | null;
  accessibilityNotes: string | null;
  dataSource: string | null;
  sourceUrl: string | null;
  lastVerifiedAt: string | null;
  confidenceScore: number;
  currentStatus: CurrentResourceStatus | null;
  hours: ResourceHour[];
  organization?: ResourceOrganization | null;
  verification: VerificationMetadata;
  communityUpdates: CommunityUpdate[];
}

export interface VerificationMetadata {
  reportCount: number;
  lastCommunityReportAt: string | null;
  communityConfirmed: boolean;
  recentlyUpdated: boolean;
}

export interface CommunityUpdate {
  id: string;
  reportType: string;
  message: string;
  createdAt: string;
}

export type ResourceStatus =
  | 'open'
  | 'closed'
  | 'limited'
  | 'unknown'
  | 'temporarily_closed';
