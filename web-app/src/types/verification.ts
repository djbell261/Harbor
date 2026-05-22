export type VerificationReportType =
  | 'food_unavailable'
  | 'shelter_full'
  | 'restroom_closed'
  | 'wifi_offline'
  | 'unsafe_location'
  | 'incorrect_hours'
  | 'inaccessible'
  | 'closed'
  | 'wrong_hours'
  | 'wrong_address'
  | 'wrong_phone'
  | 'unsafe'
  | 'duplicate'
  | 'other';

export interface CreateVerificationReportRequest {
  reportType: VerificationReportType;
  description: string;
  suggestedValue?: Record<string, unknown>;
}

export interface VerificationReportResponse {
  id: string;
  resourceId: string;
  reportType: VerificationReportType;
  status: string;
  description: string | null;
  createdAt: string;
}
