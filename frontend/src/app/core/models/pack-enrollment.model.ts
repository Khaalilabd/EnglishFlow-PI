export interface PackEnrollment {
  id?: number;
  studentId: number;
  studentName: string;
  packId: number;
  packName: string;
  packCategory: string;
  packLevel: string;
  tutorId: number;
  tutorName: string;
  totalCourses: number;
  completedCourses?: number;
  enrolledAt?: string;
  completedAt?: string;
  status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  progressPercentage: number;
  isActive: boolean;
}
