export interface Chapter {
  id?: number;
  title: string;
  description: string;
  objectives?: string[];
  orderIndex: number;
  estimatedDuration?: number;
  isPublished: boolean;
  courseId: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateChapterRequest {
  title: string;
  description: string;
  objectives?: string[];
  orderIndex: number;
  estimatedDuration?: number;
  isPublished: boolean;
  courseId: number;
}

export interface UpdateChapterRequest {
  title: string;
  description: string;
  objectives?: string[];
  orderIndex: number;
  estimatedDuration?: number;
  isPublished: boolean;
  courseId: number;
}
