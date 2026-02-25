export interface LessonProgress {
  id?: number;
  studentId: number;
  lessonId: number;
  courseId: number;
  isCompleted: boolean;
  completedAt?: string;
  timeSpent?: number; // in minutes
  lastAccessedAt?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateLessonProgressRequest {
  studentId: number;
  lessonId: number;
  courseId: number;
  isCompleted: boolean;
  timeSpent?: number;
}

export interface UpdateLessonProgressRequest {
  isCompleted: boolean;
  timeSpent?: number;
}

export interface CourseProgressSummary {
  courseId: number;
  studentId: number;
  totalLessons: number;
  completedLessons: number;
  progressPercentage: number;
  lastAccessedAt?: string;
}
