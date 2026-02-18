// Enums matching backend
export enum EnglishLevel {
  BEGINNER = 'BEGINNER',
  ELEMENTARY = 'ELEMENTARY',
  INTERMEDIATE = 'INTERMEDIATE',
  UPPER_INTERMEDIATE = 'UPPER_INTERMEDIATE',
  ADVANCED = 'ADVANCED'
}

export enum CourseStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ARCHIVED = 'ARCHIVED'
}

// Main Course interface matching backend DTO
export interface Course {
  id?: number;
  title: string;
  description: string;
  level: EnglishLevel;
  maxStudents?: number;
  schedule?: string; // ISO date string
  duration?: number; // in minutes
  tutorId: number;
  fileUrl?: string;
  status: CourseStatus;
  createdAt?: string;
  updatedAt?: string;
}

// DTOs for API requests
export interface CreateCourseRequest {
  title: string;
  description: string;
  level: EnglishLevel;
  maxStudents?: number;
  schedule?: string;
  duration?: number;
  tutorId: number;
  fileUrl?: string;
  status?: CourseStatus;
}

export interface UpdateCourseRequest {
  title: string;
  description: string;
  level: EnglishLevel;
  maxStudents?: number;
  schedule?: string;
  duration?: number;
  tutorId: number;
  fileUrl?: string;
  status?: CourseStatus;
}
