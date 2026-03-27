// Pack Enrollment Status enum
export enum PackEnrollmentStatus {
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

// Pack Enrollment interface matching backend DTO
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
  completedCourses: number;
  enrolledAt: string;
  completedAt?: string;
  status: PackEnrollmentStatus;
  progressPercentage: number;
  isActive: boolean;
}

// DTO for creating enrollment
export interface CreateEnrollmentRequest {
  studentId: number;
  packId: number;
}
