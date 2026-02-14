export type EbookLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1';
export type EbookCategory = 'GRAMMAR' | 'VOCABULARY' | 'BUSINESS' | 'EXAM_PREP' | 'GENERAL';

export interface Ebook {
  id?: number;
  title: string;
  description: string;
  fileUrl: string;
  fileSize?: number;
  mimeType?: string;
  coverImageUrl?: string;
  level?: EbookLevel;
  category?: EbookCategory;
  free: boolean;
  downloadCount?: number;
  createdAt?: string;
}
