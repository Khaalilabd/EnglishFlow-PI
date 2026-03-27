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
  status: string;
  currentStep: number;
  createdAt: string;
  submittedAt?: string;
  interviewScheduledAt?: string;
  reviewedAt?: string;
  reviewedBy?: number;
  documents?: DocumentResponse[];
  notes?: NoteResponse[];
  qualificationScore?: number;
  presentationScore?: number;
  overallScore?: number;
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
  meetingLink?: string;
  notes?: string;
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
}
