export interface Organization {
  id: string;
  name: string;
  description: string | null;
  websiteUrl: string | null;
  phone: string | null;
  email: string | null;
  trustedStatus: string;
}
