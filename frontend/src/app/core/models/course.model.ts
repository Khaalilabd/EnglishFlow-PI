// Course Status enum
export enum CourseStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ARCHIVED = 'ARCHIVED'
}

// CEFR Levels constant
export const CEFR_LEVELS = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'] as const;
export type CEFRLevel = typeof CEFR_LEVELS[number];

// Main Course interface matching backend DTO
export interface Course {
  id?: number;
  title: string;
  description: string;
  category: string; // Dynamic category from database
  level: string; // A1, A2, B1, B2, C1, C2
  maxStudents?: number;
  schedule?: string; // ISO date string
  duration?: number; // in hours
  tutorId: number;
  tutorName?: string;
  price?: number;
  fileUrl?: string;
  thumbnailUrl?: string;
  objectives?: string;
  prerequisites?: string;
  isFeatured?: boolean;
  status: CourseStatus;
  chapterCount?: number;
  lessonCount?: number;
  createdAt?: string;
  updatedAt?: string;
}

// DTOs for API requests
export interface CreateCourseRequest {
  title: string;
  description: string;
  category: string;
  level: string;
  maxStudents?: number;
  schedule?: string;
  duration?: number;
  tutorId: number;
  price?: number;
  fileUrl?: string;
  thumbnailUrl?: string;
  objectives?: string;
  prerequisites?: string;
  isFeatured?: boolean;
  status?: CourseStatus;
}

export interface UpdateCourseRequest {
  title: string;
  description: string;
  category: string;
  level: string;
  maxStudents?: number;
  schedule?: string;
  duration?: number;
  tutorId: number;
  price?: number;
  fileUrl?: string;
  thumbnailUrl?: string;
  objectives?: string;
  prerequisites?: string;
  isFeatured?: boolean;
  status?: CourseStatus;
}
