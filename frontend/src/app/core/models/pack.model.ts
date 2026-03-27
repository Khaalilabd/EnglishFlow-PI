export enum PackStatus {
  DRAFT = 'DRAFT',
  ACTIVE = 'ACTIVE',
  FULL = 'FULL',
  CLOSED = 'CLOSED',
  ARCHIVED = 'ARCHIVED'
}

export interface Pack {
  id?: number;
  name: string;
  category: string; // Dynamic category name
  level: string; // A1, A2, B1, B2, C1, C2
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
  createdBy: number;
  createdAt?: string;
  updatedAt?: string;
  isEnrollmentOpen?: boolean;
}
