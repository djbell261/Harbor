export interface Category {
  id: string;
  code: string;
  name: string;
  description: string | null;
  iconName: string | null;
  sortOrder: number;
}
