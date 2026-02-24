export enum DayOfWeek {
  MONDAY = 'MONDAY',
  TUESDAY = 'TUESDAY',
  WEDNESDAY = 'WEDNESDAY',
  THURSDAY = 'THURSDAY',
  FRIDAY = 'FRIDAY',
  SATURDAY = 'SATURDAY',
  SUNDAY = 'SUNDAY'
}

export enum TutorStatus {
  AVAILABLE = 'AVAILABLE',
  BUSY = 'BUSY',
  UNAVAILABLE = 'UNAVAILABLE'
}

export interface TimeSlot {
  id?: number;
  startTime: string;
  endTime: string;
}

export interface TutorAvailability {
  id?: number;
  tutorId: number;
  tutorName: string;
  availableDays: DayOfWeek[];
  timeSlots: TimeSlot[];
  maxStudentsCapacity: number;
  currentStudentsCount?: number;
  availableCapacity?: number;
  capacityPercentage?: number;
  categories: string[]; // Dynamic category names
  levels: string[];
  status: TutorStatus;
  lastUpdated?: string;
  createdAt?: string;
}
