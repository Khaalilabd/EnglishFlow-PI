// Enum matching backend
export enum LessonType {
  VIDEO = 'VIDEO',
  TEXT = 'TEXT',
  FILE = 'FILE',
  IMAGE = 'IMAGE',
  MIXED = 'MIXED'
}

// Lesson interface matching backend DTO
export interface Lesson {
  id?: number;
  title: string;
  description: string;
  content?: string;
  contentUrl?: string;
  videoUrl?: string; // Alias for contentUrl when type is VIDEO
  fileUrl?: string;  // Alias for contentUrl when type is FILE
  lessonType: LessonType;
  type?: LessonType; // Alias for lessonType
  orderIndex: number;
  duration?: number; // in minutes
  isPreview: boolean;
  isPublished: boolean;
  chapterId: number;
  createdAt?: string;
  updatedAt?: string;
}

// DTOs for API requests
export interface CreateLessonRequest {
  title: string;
  description: string;
  content?: string;
  contentUrl?: string;
  lessonType: LessonType;
  orderIndex: number;
  duration?: number;
  isPreview: boolean;
  isPublished: boolean;
  chapterId: number;
}

export interface UpdateLessonRequest {
  title: string;
  description: string;
  content?: string;
  contentUrl?: string;
  lessonType: LessonType;
  orderIndex: number;
  duration?: number;
  isPreview: boolean;
  isPublished: boolean;
}

// Helper for lesson type icons
export const LessonTypeIcons: { [key in LessonType]: string } = {
  [LessonType.VIDEO]: '🎥',
  [LessonType.TEXT]: '📄',
  [LessonType.FILE]: '📁',
  [LessonType.IMAGE]: '🖼️',
  [LessonType.MIXED]: '📚'
};
