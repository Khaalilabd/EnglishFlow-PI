// Chapter interface matching backend DTO
export interface Chapter {
  id?: number;
  title: string;
  description: string;
  objectives: string[];
  orderIndex: number;
  estimatedDuration?: number; // in minutes
  isPublished: boolean;
  courseId: number;
  createdAt?: string;
  updatedAt?: string;
}

// DTOs for API requests
export interface CreateChapterRequest {
  title: string;
  description: string;
  objectives: string[];
  orderIndex: number;
  estimatedDuration?: number;
  isPublished: boolean;
  courseId: number;
}

export interface UpdateChapterRequest {
  title: string;
  description: string;
  objectives: string[];
  orderIndex: number;
  estimatedDuration?: number;
  isPublished: boolean;
}
