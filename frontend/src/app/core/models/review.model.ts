export interface CourseReview {
  id: number;
  courseId: number;
  userId: number;
  userName: string;
  userAvatar: string;
  rating: number;
  comment: string;
  createdAt: string;
  helpful: number;
}

export interface CourseEnrollment {
  id: number;
  courseId: number;
  userId: number;
  enrolledAt: string;
  progress: number;
  completedLessons: number[];
  lastAccessedAt: string;
  certificateIssued: boolean;
}
