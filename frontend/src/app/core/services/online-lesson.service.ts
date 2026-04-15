import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface AvailableTimeSlot {
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  booked: boolean;
  bookedByLessonId?: number;
}

export interface TutorAvailableSlots {
  tutorId: number;
  tutorName: string;
  hasAvailability: boolean;
  availableSlots: AvailableTimeSlot[];
}

export interface AssignTimeSlotRequest {
  dayOfWeek: string;
  startTime: string;
  endTime: string;
}

export interface LessonTimeAssignment {
  id: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  tutorId: number;
}

@Injectable({
  providedIn: 'root'
})
export class OnlineLessonService {
  private apiUrl = `${environment.apiUrl}/online-lessons`;
  private availabilityUrl = `${environment.apiUrl}/tutor-availability`;

  constructor(private http: HttpClient) {}

  getAvailableSlots(tutorId: number): Observable<TutorAvailableSlots> {
    return this.http.get<TutorAvailableSlots>(`${this.availabilityUrl}/tutor/${tutorId}/available-slots`);
  }

  assignTimeSlot(lessonId: number, tutorId: number, request: AssignTimeSlotRequest): Observable<LessonTimeAssignment> {
    return this.http.post<LessonTimeAssignment>(
      `${this.apiUrl}/${lessonId}/assign-time-slot?tutorId=${tutorId}`,
      request
    );
  }

  getTimeAssignment(lessonId: number): Observable<LessonTimeAssignment> {
    return this.http.get<LessonTimeAssignment>(`${this.apiUrl}/${lessonId}/time-assignment`);
  }

  removeTimeAssignment(lessonId: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${lessonId}/time-assignment`);
  }

  // Meeting session management
  createMeetingSession(lessonId: number, roomId: string, inviteLink: string, tutorId: number): Observable<any> {
    return this.http.post(`${environment.apiUrl}/meeting-sessions`, {
      lessonId,
      roomId,
      inviteLink,
      tutorId
    });
  }

  getActiveMeetingSession(lessonId: number): Observable<any> {
    return this.http.get(`${environment.apiUrl}/meeting-sessions/lesson/${lessonId}`);
  }

  endMeetingSession(lessonId: number): Observable<any> {
    return this.http.delete(`${environment.apiUrl}/meeting-sessions/lesson/${lessonId}`);
  }

  // Legacy method - kept for backward compatibility
  checkActiveMeeting(lessonId: number): Observable<{ active: boolean; roomId: string | null; startedAt: string | null }> {
    return this.getActiveMeetingSession(lessonId).pipe(
      map((response: any) => {
        if (response.active === false) {
          return { active: false, roomId: null, startedAt: null };
        }
        return {
          active: response.isActive || false,
          roomId: response.roomId || null,
          startedAt: response.startedAt || null
        };
      })
    );
  }
}
