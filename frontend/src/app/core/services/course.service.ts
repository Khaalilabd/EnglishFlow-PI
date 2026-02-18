import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  Course, 
  CreateCourseRequest, 
  UpdateCourseRequest,
  EnglishLevel,
  CourseStatus 
} from '../models/course.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private apiUrl = `${environment.apiUrl}/courses`;

  constructor(private http: HttpClient) {}

  // Create a new course
  createCourse(course: CreateCourseRequest): Observable<Course> {
    return this.http.post<Course>(this.apiUrl, course);
  }

  // Get course by ID
  getCourseById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/${id}`);
  }

  // Get all courses
  getAllCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl);
  }

  // Get courses by status
  getCoursesByStatus(status: CourseStatus): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/status/${status}`);
  }

  // Get courses by level
  getCoursesByLevel(level: EnglishLevel): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/level/${level}`);
  }

  // Get courses by tutor
  getCoursesByTutor(tutorId: number): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/tutor/${tutorId}`);
  }

  // Update course
  updateCourse(id: number, course: UpdateCourseRequest): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}`, course);
  }

  // Delete course
  deleteCourse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Helper methods
  getPublishedCourses(): Observable<Course[]> {
    return this.getCoursesByStatus(CourseStatus.PUBLISHED);
  }

  getDraftCourses(): Observable<Course[]> {
    return this.getCoursesByStatus(CourseStatus.DRAFT);
  }
}
