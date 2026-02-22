export enum AcademicRiskLevel {
  NORMAL = 'NORMAL',
  ATTENTION = 'ATTENTION',
  HIGH_RISK = 'HIGH_RISK'
}

export enum ComplaintStatus {
  SUBMITTED = 'SUBMITTED',
  ANALYZED = 'ANALYZED',
  ACTION_ASSIGNED = 'ACTION_ASSIGNED',
  IN_PROGRESS = 'IN_PROGRESS',
  PENDING_STUDENT_CONFIRMATION = 'PENDING_STUDENT_CONFIRMATION',
  RESOLVED = 'RESOLVED',
  REOPENED = 'REOPENED',
  REJECTED = 'REJECTED'
}

export enum AcademicActionType {
  ASSIGN_RECOVERY_SESSION = 'ASSIGN_RECOVERY_SESSION',
  CHANGE_TUTOR = 'CHANGE_TUTOR',
  SEND_LEARNING_PLAN = 'SEND_LEARNING_PLAN',
  SCHEDULE_MEETING = 'SCHEDULE_MEETING',
  OBSERVE_NEXT_CLASS = 'OBSERVE_NEXT_CLASS'
}

export enum ResolutionFeedback {
  IMPROVED = 'IMPROVED',
  PARTIALLY_IMPROVED = 'PARTIALLY_IMPROVED',
  NOT_RESOLVED = 'NOT_RESOLVED'
}

export interface StudentCaseCard {
  complaintId: number;
  studentId: number;
  studentName: string;
  studentEmail: string;
  avatarUrl?: string;
  
  cefrLevel: string;
  currentCourse: string;
  currentTutorName: string;
  progressPercentage: number;
  totalAbsences: number;
  
  category: string;
  subject: string;
  status: ComplaintStatus;
  complaintDate: string;
  
  riskLevel: AcademicRiskLevel;
  riskScore: number;
  requiresIntervention: boolean;
  
  isRecurringIssue: boolean;
  similarCasesCount: number;
}

export interface StudentCaseDetail {
  complaintId: number;
  studentId: number;
  studentName: string;
  studentEmail: string;
  avatarUrl?: string;
  
  cefrLevel: string;
  currentCourse: string;
  currentTutorName: string;
  attendanceRate: number;
  averageQuizScore: number;
  previousComplaintsCount: number;
  progressPercentage: number;
  totalAbsences: number;
  
  category: string;
  subject: string;
  description: string;
  status: ComplaintStatus;
  complaintDate: string;
  
  systemInsight: string;
  detectedCauses: string[];
  
  riskLevel: AcademicRiskLevel;
  riskScore: number;
  
  timeline: AcademicTimelineEvent[];
  actions: AcademicAction[];
}

export interface AcademicTimelineEvent {
  id: number;
  eventDescription: string;
  actorRole: string;
  eventDate: string;
}

export interface AcademicAction {
  id: number;
  actionType: AcademicActionType;
  actionDetails: string;
  completed: boolean;
  createdAt: string;
  completedAt?: string;
}

export interface TeachingQualityDashboard {
  topComplaintCategories: { [key: string]: number };
  tutorsWithMostComplaints: TutorComplaintStats[];
  resolutionSuccessRate: number;
  studentsAtRiskCount: number;
  tutorSkillHeatmap: { [tutor: string]: { [skill: string]: string } };
}

export interface TutorComplaintStats {
  tutorId: number;
  tutorName: string;
  complaintCount: number;
  resolutionRate: number;
}
