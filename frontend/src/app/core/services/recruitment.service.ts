import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ApplicationStep1 {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  cin?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  nationality?: string;
}

export interface ApplicationStep2 {
  applicationId: number;
  education: string;
  certifications?: string;
  workExperience?: string;
  yearsOfExperience: number;
  englishLevel: string;
  specializations?: string;
}

export interface ApplicationStep3 {
  applicationId: number;
  motivationLetter: string;
  teachingPhilosophy: string;
  availability: string;
}

export interface ApplicationResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  cin?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  nationality?: string;
  education?: string;
  certifications?: string;
  workExperience?: string;
  yearsOfExperience?: number;
  englishLevel?: string;
  specializations?: string;
  motivationLetter?: string;
  teachingPhilosophy?: string;
  availability?: string;
  termsAccepted?: boolean;
  termsAcceptedAt?: string;
  status: string;
  currentStep: number;
  createdAt: string;
  submittedAt?: string;
  interviewScheduledAt?: string;
  interviewMeetingLink?: string;
  reviewedAt?: string;
  reviewedBy?: number;
  rejectionReason?: string;
  documents?: DocumentResponse[];
  notes?: NoteResponse[];
  qualificationScore?: number;
  presentationScore?: number;
  overallScore?: number;
  meetingLink?: string;
}

export interface DocumentResponse {
  id: number;
  type: string;
  fileName: string;
  filePath: string;
  fileType: string;
  fileSize: number;
  uploadedAt: string;
}

export interface NoteResponse {
  id: number;
  content: string;
  createdBy: number;
  createdAt: string;
}

export interface UpdateStatusRequest {
  status: string;
  comment?: string;
}

export interface ScoreApplicationRequest {
  qualificationScore?: number;
  presentationScore?: number;
  overallScore?: number;
}

export interface ScheduleInterviewRequest {
  interviewScheduledAt: string;

  platform?: MeetingPlatform;
  meetingLink?: string;
  meetingTitle?: string;
  durationMinutes?: number;
  notes?: string;
}

export enum MeetingPlatform {
  GOOGLE_MEET = 'GOOGLE_MEET',
  ZOOM = 'ZOOM',
  MICROSOFT_TEAMS = 'MICROSOFT_TEAMS',
  MANUAL = 'MANUAL'
}

export interface GenerateMeetingLinkRequest {
  platform: MeetingPlatform;
  interviewScheduledAt: string;
  title?: string;
  description?: string;
  durationMinutes?: number;
}

export interface MeetingLinkResponse {
  meetingLink: string;
  platform: MeetingPlatform;
  meetingId: string;
  password?: string;
  scheduledAt: string;
  durationMinutes: number;
  additionalInfo?: string;
}


export interface AddNoteRequest {
  content: string;
}

export interface RejectApplicationRequest {
  reason: string;
}

export interface ApplicationStatistics {
  total: number;
  draft: number;
  submitted: number;
  underReview: number;
  interviewScheduled: number;
  accepted: number;
  rejected: number;
}

export interface CalendarAvailabilityRequest {
  startDate: string;
  endDate: string;
  interviewerId?: number;
}

export interface CalendarAvailabilityResponse {
  startDate: string;
  endDate: string;
  interviewerId: number;
  interviewerName: string;
  scheduledEvents: CalendarEventResponse[];
  busySlots: TimeSlot[];
  availableSlots?: TimeSlot[];
  hasConflicts: boolean;
  message: string;
}

export interface CalendarEventResponse {
  scheduleId?: number;
  googleEventId?: string;
  title: string;
  description?: string;
  start: string;
  end: string;
  durationMinutes?: number;
  meetingLink?: string;
  platform?: string;
  status?: string;
  applicationId?: number;
  candidateName?: string;
  candidateEmail?: string;
  source?: 'LOCAL_DB' | 'GOOGLE_CALENDAR' | 'BOTH';
}

export interface TimeSlot {
  date: string;
  startTime: string;
  endTime: string;
  isAvailable: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class RecruitmentService {
  private apiUrl = `${environment.apiUrl}/auth/recruitment`;

  constructor(private http: HttpClient) {}

  // Public endpoints - Application submission
  createApplication(data: ApplicationStep1): Observable<ApplicationResponse> {
    return this.http.post<ApplicationResponse>(`${this.apiUrl}/apply/step1`, data);
  }

  updateQualifications(data: ApplicationStep2): Observable<ApplicationResponse> {
    return this.http.put<ApplicationResponse>(`${this.apiUrl}/apply/step2`, data);
  }

  updatePresentation(data: ApplicationStep3): Observable<ApplicationResponse> {
    return this.http.put<ApplicationResponse>(`${this.apiUrl}/apply/step3`, data);
  }

  uploadDocument(applicationId: number, file: File, documentType: string): Observable<DocumentResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', documentType);
    
    return this.http.post<DocumentResponse>(
      `${this.apiUrl}/apply/${applicationId}/upload`,
      formData
    );
  }

  submitApplication(applicationId: number): Observable<ApplicationResponse> {
    return this.http.post<ApplicationResponse>(`${this.apiUrl}/apply/${applicationId}/submit`, {});
  }

  acceptTerms(applicationId: number): Observable<ApplicationResponse> {
    return this.http.post<ApplicationResponse>(`${this.apiUrl}/apply/${applicationId}/accept-terms`, {});
  }

  getApplication(applicationId: number): Observable<ApplicationResponse> {
    return this.http.get<ApplicationResponse>(`${this.apiUrl}/apply/${applicationId}`);
  }

  // Admin endpoints
  getAllApplications(): Observable<ApplicationResponse[]> {
    return this.http.get<ApplicationResponse[]>(this.apiUrl);
  }

  getApplicationsByStatus(status: string): Observable<ApplicationResponse[]> {
    return this.http.get<ApplicationResponse[]>(`${this.apiUrl}/status/${status}`);
  }

  updateStatus(applicationId: number, data: UpdateStatusRequest): Observable<ApplicationResponse> {
    return this.http.put<ApplicationResponse>(`${this.apiUrl}/${applicationId}/status`, data);
  }

  scoreApplication(applicationId: number, data: ScoreApplicationRequest): Observable<ApplicationResponse> {
    return this.http.put<ApplicationResponse>(`${this.apiUrl}/${applicationId}/score`, data);
  }

  scheduleInterview(applicationId: number, data: ScheduleInterviewRequest): Observable<ApplicationResponse> {
    return this.http.post<ApplicationResponse>(`${this.apiUrl}/${applicationId}/interview`, data);
  }

  addNote(applicationId: number, data: AddNoteRequest): Observable<NoteResponse> {
    return this.http.post<NoteResponse>(`${this.apiUrl}/${applicationId}/notes`, data);
  }

  acceptApplication(applicationId: number): Observable<ApplicationResponse> {
    return this.http.post<ApplicationResponse>(`${this.apiUrl}/${applicationId}/accept`, {});
  }

  rejectApplication(applicationId: number, data: RejectApplicationRequest): Observable<ApplicationResponse> {
    return this.http.post<ApplicationResponse>(`${this.apiUrl}/${applicationId}/reject`, data);
  }

  getStatistics(): Observable<ApplicationStatistics> {
    return this.http.get<ApplicationStatistics>(`${this.apiUrl}/statistics`);
  }

  // Get application details by user ID
  getApplicationByUserId(userId: number): Observable<ApplicationResponse> {
    return this.http.get<ApplicationResponse>(`${this.apiUrl}/user/${userId}`);
  }


  // Meeting link generation
  generateMeetingLink(data: GenerateMeetingLinkRequest): Observable<MeetingLinkResponse> {
    return this.http.post<MeetingLinkResponse>(`${this.apiUrl}/generate-meeting-link`, data);
  }

  getAvailablePlatforms(): Observable<{ [key: string]: boolean }> {
    return this.http.get<{ [key: string]: boolean }>(`${this.apiUrl}/available-platforms`);
  }

  // Calendar methods
  getCalendarAvailability(data: CalendarAvailabilityRequest): Observable<CalendarAvailabilityResponse> {
    return this.http.post<CalendarAvailabilityResponse>(`${this.apiUrl}/calendar/availability`, data);
  }

  getUpcomingInterviews(): Observable<CalendarEventResponse[]> {
    return this.http.get<CalendarEventResponse[]>(`${this.apiUrl}/calendar/upcoming`);
  }

  cancelInterview(scheduleId: number, reason?: string): Observable<any> {
    const params: any = {};
    if (reason) {
      params.reason = reason;
    }
    return this.http.delete(`${this.apiUrl}/calendar/${scheduleId}`, { params });
  }

  cancelInterviewByApplicationId(applicationId: number, reason?: string): Observable<any> {
    const params: any = {};
    if (reason) {
      params.reason = reason;
    }
    return this.http.delete(`${this.apiUrl}/${applicationId}/interview`, { params });
  }

}
