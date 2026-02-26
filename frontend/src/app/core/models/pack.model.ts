export interface Pack {
  id?: number;
  name: string;
  category: string;
  level: string;
  tutorId: number;
  tutorName: string;
  tutorRating?: number;
  courseIds: number[];
  coursesCount?: number;
  price: number;
  estimatedDuration: number;
  maxStudents: number;
  currentEnrolledStudents?: number;
  availableSlots?: number;
  enrollmentPercentage?: number;
  enrollmentStartDate?: string;
  enrollmentEndDate?: string;
  description?: string;
  status: PackStatus;
  createdBy?: number;
  createdAt?: string;
  updatedAt?: string;
  isEnrollmentOpen?: boolean;
}

export enum PackStatus {
  DRAFT = 'DRAFT',
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  ARCHIVED = 'ARCHIVED'
}

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
  status: string;
  progressPercentage?: number;
  isActive: boolean;
}
