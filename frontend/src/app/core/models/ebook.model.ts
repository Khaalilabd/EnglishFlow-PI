export type EbookLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1';
export type EbookCategory = 'GRAMMAR' | 'VOCABULARY' | 'BUSINESS' | 'EXAM_PREP' | 'GENERAL';
export type PricingModel = 'FREE' | 'FREEMIUM' | 'PREMIUM';
export type PublishStatus = 'DRAFT' | 'SCHEDULED' | 'PUBLISHED' | 'ARCHIVED';

export interface Ebook {
  id?: number;
  title: string;
  description: string;
  fileUrl: string;
  fileSize?: number;
  mimeType?: string;
  coverImageUrl?: string;
  thumbnailUrl?: string;
  level?: EbookLevel;
  category?: EbookCategory;
  
  // Metadata
  metadata?: EbookMetadata;
  
  // Pricing
  free: boolean;
  price?: number;
  pricingModel?: PricingModel;
  
  // Stats
  downloadCount?: number;
  viewCount?: number;
  averageRating?: number;
  reviewCount?: number;
  
  // Publishing
  status?: PublishStatus;
  publishedAt?: string;
  scheduledFor?: string;
  
  // Relations
  chapters?: EbookChapter[];
  tags?: Tag[];
  
  // Timestamps
  createdAt?: string;
  updatedAt?: string;
}

export interface EbookMetadata {
  id?: number;
  ebookId?: number;
  author?: string;
  publisher?: string;
  isbn?: string;
  totalPages?: number;
  estimatedReadTimeMinutes?: number;
  language?: string;
  edition?: string;
  publicationDate?: string;
  keywords?: string[];
  tableOfContents?: string;
}

export interface EbookChapter {
  id?: number;
  ebookId?: number;
  title: string;
  description?: string;
  orderIndex: number;
  startPage?: number;
  endPage?: number;
  fileUrl?: string;
  isFree?: boolean;
}

export interface Review {
  id?: number;
  ebookId: number;
  userId: number;
  userName?: string;
  userAvatar?: string;
  rating: number; // 1-5
  comment?: string;
  isVerified?: boolean;
  helpfulCount?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface ReadingProgress {
  id?: number;
  ebookId: number;
  ebookTitle?: string;
  ebookCoverUrl?: string;
  userId: number;
  currentPage: number;
  totalPages?: number;
  progressPercentage: number;
  lastReadAt?: string;
  readingTimeMinutes?: number;
  isCompleted?: boolean;
  bookmarksCount?: number;
  notesCount?: number;
}

export interface Bookmark {
  id?: number;
  progressId: number;
  pageNumber: number;
  note?: string;
  createdAt?: string;
}

export interface Note {
  id?: number;
  progressId: number;
  pageNumber: number;
  content: string;
  highlightedText?: string;
  color?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Tag {
  id?: number;
  name: string;
  slug: string;
  color?: string;
}

export interface Collection {
  id?: number;
  name: string;
  description?: string;
  isPublic: boolean;
  ownerId: number;
  ownerName?: string;
  ebookIds?: number[];
  ebooksCount?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateReviewRequest {
  ebookId: number;
  rating: number;
  comment?: string;
}

export interface UpdateProgressRequest {
  ebookId: number;
  currentPage: number;
  totalPages?: number;
  readingTimeMinutes?: number;
}
